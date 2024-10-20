package com.plcoding.core.domain.run

import com.plcoding.core.domain.location.Location
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class Run(
    val id: String?, //null if new run
    val duration: Duration,
    val dateTimeUtc: ZonedDateTime,
    val distanceInMeters: Int,
    val location: Location,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?
){
    val avgSpeedInKmh: Double
        get() = (distanceInMeters/1000.0) / duration.toDouble(DurationUnit.HOURS)
}
