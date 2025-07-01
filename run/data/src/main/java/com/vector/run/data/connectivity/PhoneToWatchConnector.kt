package com.vector.run.data.connectivity

import com.plcoding.core.domain.util.EmptyResult
import com.plcoding.run.domain.WatchConnector
import com.vector.core.connectivity.domain.DeviceNode
import com.vector.core.connectivity.domain.DeviceType
import com.vector.core.connectivity.domain.NodeDiscovery
import com.vector.core.connectivity.domain.messaging.MessagingAction
import com.vector.core.connectivity.domain.messaging.MessagingClient
import com.vector.core.connectivity.domain.messaging.MessagingError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)
class PhoneToWatchConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope,
    private val messagingClient: MessagingClient
) : WatchConnector {

    private val _connectedNode = MutableStateFlow<DeviceNode?>(null)

    override val connectedDevice: StateFlow<DeviceNode?> = _connectedNode.asStateFlow()

    private val isTrackable = MutableStateFlow(false)

    init {
        _connectedNode
            .filterNotNull()
            .flatMapLatest { isTrackable }
            .onEach { isTrackable ->
                sendActionToWatch(MessagingAction.ConnectionRequest)
                val action = if (isTrackable) {
                    MessagingAction.Trackable
                } else {
                    MessagingAction.Untrackable
                }
                sendActionToWatch(action)
            }
            .launchIn(applicationScope)
    }

    override val messagingActions: Flow<MessagingAction> =
        nodeDiscovery.observeConnectedDevices(DeviceType.PHONE)
            .flatMapLatest { connectedDevices ->
                val node = connectedDevices.firstOrNull()
                if (node != null && node.isNearby) {
                    _connectedNode.value = node
                    messagingClient.connectToNode(node.id)
                } else flowOf()
            }
            .onEach { action ->
                if (action == MessagingAction.ConnectionRequest) {
                    if (isTrackable.value) {
                        sendActionToWatch(MessagingAction.Trackable)
                    } else {
                        sendActionToWatch(MessagingAction.Untrackable)
                    }
                }
            }
            .shareIn(
                applicationScope,
                SharingStarted.Eagerly
            )

    override suspend fun sendActionToWatch(action: MessagingAction): EmptyResult<MessagingError> {
        return messagingClient.sendOrQueueAction(action)
    }

    override fun setIsTrackable(isTrackable: Boolean) {
        this.isTrackable.value = isTrackable
    }

}