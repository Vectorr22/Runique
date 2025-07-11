package com.vectorr22.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.core.domain.location.Location
import com.plcoding.core.domain.run.Run
import com.plcoding.core.domain.run.RunRepository
import com.plcoding.core.domain.util.Result
import com.plcoding.core.presentation.ui.asUiText
import com.plcoding.run.domain.LocationDataCalculator
import com.plcoding.run.domain.RunningTracker
import com.plcoding.run.domain.WatchConnector
import com.vector.core.connectivity.domain.messaging.MessagingAction
import com.vector.core.notification.ActiveRunService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val watchConnector: WatchConnector,
    private val applicationScope: CoroutineScope
) : ViewModel() {
    var state by mutableStateOf(
        ActiveRunState(
            shouldTrack = ActiveRunService.isServiceActive.value && runningTracker.isTracking.value,
            hasStartedRunning = ActiveRunService.isServiceActive.value
        )
    )
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val shouldTrack = snapshotFlow { state.shouldTrack }
        .stateIn(viewModelScope, SharingStarted.Lazily, state.shouldTrack)

    private val hasLocationPermission = MutableStateFlow(false)

    private val isTracking = combine(
        shouldTrack,
        hasLocationPermission
    ) { shouldTrack, hasPermission ->
        shouldTrack && hasPermission
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        watchConnector.connectedDevice
            .filterNotNull()
            .onEach {
                Timber.d("New device detected: ${it.displayName}")
            }.launchIn(viewModelScope)
        hasLocationPermission.onEach { hasPermission ->
            if (hasPermission)
                runningTracker.startObservingLocation()
            else
                runningTracker.stopObservingLocation()
        }.launchIn(viewModelScope)

        isTracking.onEach { isTracking ->
            runningTracker.setIsTracking(isTracking)
        }.launchIn(viewModelScope)

//        runningTracker
//            .currentLocation
//            .onEach {
//                state = state.copy(
//                    currentLocation = it?.location
//                )
//            }.launchIn(viewModelScope)
//
//        runningTracker
//            .runDataFlow
//            .onEach {
//                state = state.copy(
//                    runData = it
//                )
//            }.launchIn(viewModelScope)
//
//        runningTracker
//            .elapsedTime
//            .onEach {
//                state = state.copy(
//                    elapsedTime = it
//                )
//            }.launchIn(viewModelScope)

        combine(
            runningTracker.currentLocation,
            runningTracker.runData,
            runningTracker.elapsedTime
        ) { currentLocation, runData, elapsedTime ->
            state.copy(
                currentLocation = currentLocation?.location,
                runData = runData,
                elapsedTime = elapsedTime
            )
        }
            .onEach { newState ->
                state = newState
            }
            .launchIn(viewModelScope)

        listenToWatchActions()
    }

    fun onAction(action: ActiveRunAction, triggeredOnWatch: Boolean = false) {
        if(!triggeredOnWatch) {
            val messagingAction = when(action) {
                ActiveRunAction.OnFinishRunClick -> MessagingAction.FinishRun
                ActiveRunAction.OnResumeRun -> MessagingAction.StartOrResume
                ActiveRunAction.OnToggleRunClick -> {
                    if(state.hasStartedRunning) {
                        MessagingAction.PauseRun
                    } else {
                        MessagingAction.StartOrResume
                    }
                }
                else -> null
            }
            messagingAction?.let {
                viewModelScope.launch {
                    watchConnector.sendActionToWatch(it)
                }
            }
        }
        when (action) {
            ActiveRunAction.OnFinishRunClick -> {
                state = state.copy(
                    isRunFinished = true,
                    isSavingRun = true
                )
            }
            ActiveRunAction.OnResumeRun -> {
                state = state.copy(
                    shouldTrack = true
                )
            }

            ActiveRunAction.OnBackClick -> {
                state = state.copy(
                    shouldTrack = false
                )
            }

            ActiveRunAction.OnToggleRunClick -> {
                state = state.copy(
                    hasStartedRunning = true,
                    shouldTrack = !state.shouldTrack
                )
            }

            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.value = action.locationPermissionAccepted
                state = state.copy(
                    showLocationRationale = action.showLocationRationale
                )
            }

            is ActiveRunAction.SubmitNotificationPermissionInfo -> {
                state = state.copy(
                    showNotificationRationale = action.showNotificationRationale
                )
            }
            is ActiveRunAction.DismissRationaleDialog -> {
                state = state.copy(
                    showLocationRationale = false,
                    showNotificationRationale = false
                )
            }
            is ActiveRunAction.OnRunProcessed -> {
                finishRun(action.mapPictureBytes)
            }
        }
    }

    private fun finishRun(mapPictureBytes: ByteArray) {
        val locations = state.runData.locations
        if(locations.isEmpty() || locations.first().size <= 1) {
            state = state.copy(isSavingRun = false)
            return
        }

        viewModelScope.launch {
            val run = Run(
                id = null,
                duration = state.elapsedTime,
                dateTimeUtc = ZonedDateTime.now()
                    .withZoneSameInstant(ZoneId.of("UTC")),
                distanceInMeters = state.runData.distanceMeters,
                location = state.currentLocation ?: Location(0.0, 0.0),
                maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(locations),
                totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(locations),
                mapPictureUrl = null,
                avgHeartRate = if(state.runData.heartRates.isEmpty()){
                    null
                } else {
                    state.runData.heartRates.average().roundToInt()
                },
                maxHeartRate = if(state.runData.heartRates.isEmpty()){
                    null
                } else {
                    state.runData.heartRates.max()
                }
            )
            runningTracker.finishRun()
            // Save run in repository
            when(val result = runRepository.upsertRun(run, mapPictureBytes)){
                is Result.Error -> {
                    eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
                }
                is Result.Success -> {
                    eventChannel.send(ActiveRunEvent.RunSaved)
                }
            }
            state = state.copy(isSavingRun = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if(!ActiveRunService.isServiceActive.value){
            applicationScope.launch {
                watchConnector.sendActionToWatch(MessagingAction.Untrackable)
            }
            runningTracker.stopObservingLocation()
        }
    }

    private fun listenToWatchActions() {
        watchConnector
            .messagingActions
            .onEach { action ->
                when(action) {
                    MessagingAction.ConnectionRequest -> {
                        if(isTracking.value) {
                            watchConnector.sendActionToWatch(MessagingAction.StartOrResume)
                        }
                    }
                    MessagingAction.FinishRun -> {
                        onAction(
                            action = ActiveRunAction.OnFinishRunClick,
                            triggeredOnWatch = true
                        )
                    }
                    MessagingAction.PauseRun -> {
                        if(isTracking.value) {
                            onAction(
                                action = ActiveRunAction.OnToggleRunClick,
                                triggeredOnWatch = true
                            )
                        }
                    }
                    MessagingAction.StartOrResume -> {
                        if(!isTracking.value) {
                            if(state.hasStartedRunning) {
                                onAction(
                                    action = ActiveRunAction.OnResumeRun,
                                    triggeredOnWatch = true
                                )
                            }
                            else {
                                onAction(
                                    action = ActiveRunAction.OnToggleRunClick,
                                    triggeredOnWatch = true
                                )
                            }
                        }
                    }
                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

}
