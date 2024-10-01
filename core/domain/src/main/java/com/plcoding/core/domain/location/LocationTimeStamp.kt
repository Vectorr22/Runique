package com.plcoding.core.domain.location

import kotlin.time.Duration

data class LocationTimeStamp(
    val location: LocationWithAltitude,
    val durationTimestamp: Duration
)
