package com.vectorr22.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class ActiveRunViewModel: ViewModel(){
    var state by mutableStateOf(ActiveRunState())
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _hasLocationPermission = MutableStateFlow(false)

    fun onAction(action: ActiveRunAction){
        when(action){
            ActiveRunAction.OnFinishRunClick -> {}
            ActiveRunAction.OnResumeRun -> {}
            ActiveRunAction.OnToggleRunClick -> {}
            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                _hasLocationPermission.value = action.locationPermissionAccepted
                state = state.copy(
                    showLocationRationale = action.showLocationRationale
                )
            }
            is ActiveRunAction.SubmitNotificationPermissionInfo -> {}
            is ActiveRunAction.DismissRationaleDialog -> {
                state = state.copy(
                    showLocationRationale = false,
                    showNotificationRationale = false
                )
            }
            else -> Unit
        }
    }


}
