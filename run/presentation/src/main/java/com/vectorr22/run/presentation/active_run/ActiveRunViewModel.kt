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
import com.vectorr22.run.presentation.active_run.services.ActiveRunService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository
) : ViewModel() {
    var state by mutableStateOf(
        ActiveRunState(
            shouldTrack = ActiveRunService.isServiceActive && runningTracker.isTracking.value,
            hasStartedRunning = ActiveRunService.isServiceActive
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
            runningTracker.runDataFlow,
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
    }

    fun onAction(action: ActiveRunAction) {
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
            else -> Unit
        }
    }

    private fun finishRun(mapPictureBytes: ByteArray) {
        val locations = state.runData.location
        if (locations.isEmpty() || locations.first().size <= 1) {
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
                location = state.currentLocation ?: Location(lat = 0.0, long = 0.0),
                maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(locations),
                totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(locations),
                mapPictureUrl = null
            )

            //Aqui se guardara la run en el repo
            runningTracker.finishRun()
            //uploadToS3(picture)
            when (val result = runRepository.upsertRun(run,mapPictureBytes)) {
                is Result.Error -> {
                    eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
                }

                is Result.Success -> {
                    eventChannel.send(ActiveRunEvent.RunSaved)
                }
            }
            state = state.copy(
                isSavingRun = false
            )
        }
    }


    override fun onCleared() {
        super.onCleared()
        if (!ActiveRunService.isServiceActive) {
            runningTracker.stopObservingLocation()
        }
    }

}
