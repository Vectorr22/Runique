package com.vector.core.connectivity.domain

import kotlinx.coroutines.flow.Flow

interface NodeDiscovery {
    fun observeConnectedDevices(localDeviceType: DeviceType): Flow<Set<DeviceNode>>
}