package com.vector.wear.run.domain

import kotlinx.coroutines.flow.Flow

interface ExerciseTracker {
    val heartRate: Flow<Int>
    suspend fun isHeartRateTrackingSupported(): Boolean

}