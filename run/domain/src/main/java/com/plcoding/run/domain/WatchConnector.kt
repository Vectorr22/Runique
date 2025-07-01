package com.plcoding.run.domain

import com.plcoding.core.domain.util.EmptyResult
import com.vector.core.connectivity.domain.DeviceNode
import com.vector.core.connectivity.domain.messaging.MessagingAction
import com.vector.core.connectivity.domain.messaging.MessagingError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WatchConnector {
    val connectedDevice: StateFlow<DeviceNode?>

    val messagingActions: Flow<MessagingAction>

    suspend fun sendActionToWatch(action: MessagingAction): EmptyResult<MessagingError>

    fun setIsTrackable(isTrackable: Boolean)
}