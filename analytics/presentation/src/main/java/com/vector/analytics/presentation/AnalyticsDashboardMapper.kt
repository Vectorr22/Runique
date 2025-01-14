package com.vector.analytics.presentation

import com.plcoding.core.presentation.ui.formatted
import com.plcoding.core.presentation.ui.formattedToKm
import com.plcoding.core.presentation.ui.toFormattedKmh
import com.vector.analytics.domain.AnalyticsValues
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

fun Duration.toFormattedTotalTime(): String{
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24
    val minutes = toLong(DurationUnit.MINUTES) % 60

    return "${days}d ${hours}h ${minutes}m"
}

fun AnalyticsValues.toAnalyticsDashboardState(): AnalyticsDashboardState{
    return AnalyticsDashboardState(
        totalDistanceRun = (totalDistanceRun / 1000.0).toFormattedKmh(),
        totalTimeRun = totalTimeRun.toFormattedTotalTime(),
        fastestEverRun = fastestEverRun.toFormattedKmh(),
        avgDistance = (avgDistanceRun / 1000.0).toFormattedKmh(),
        avgPace = avgPacePerRun.seconds.formatted()
    )
}