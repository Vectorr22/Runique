package com.plcoding.run.domain

import com.plcoding.core.domain.location.LocationTimeStamp
import kotlin.time.Duration

data class RunData(
    val distanceMeters: Int = 0,
    val pace: Duration = Duration.ZERO,
    val location: List<List<LocationTimeStamp>> = emptyList()
)
