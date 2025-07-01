package com.vector.core.connectivity.data

import com.google.android.gms.wearable.Node
import com.vector.core.connectivity.domain.DeviceNode

fun Node.toDeviceNode(): DeviceNode{
    return DeviceNode(
        id = this.id,
        displayName = this.displayName,
        isNearby = this.isNearby
    )
}