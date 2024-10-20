package com.vectorr22.run.presentation.run_overview.mappers

import com.plcoding.core.domain.run.Run
import com.plcoding.core.presentation.ui.formatted
import com.plcoding.core.presentation.ui.formattedToKm
import com.plcoding.core.presentation.ui.toFormattedKmh
import com.plcoding.core.presentation.ui.toFormattedMeters
import com.plcoding.core.presentation.ui.toFormattedPace
import com.vectorr22.run.presentation.run_overview.module.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUi(): RunUi{
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())

    val formattedDateTime = DateTimeFormatter
        .ofPattern("MMM dd, yyyy - hh:mma")
        .format(dateTimeInLocalTime)

    val distanceInKm = distanceInMeters / 1000.0

    return RunUi(
        id = id!!,
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceInKm.formattedToKm(),
        avgSpeed = avgSpeedInKmh.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        pace = duration.toFormattedPace(distanceInKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl
    )
}