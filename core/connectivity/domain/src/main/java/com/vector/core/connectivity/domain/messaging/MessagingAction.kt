package com.vector.core.connectivity.domain.messaging

import kotlin.time.Duration

sealed interface MessagingAction {
    data object StartOrResume : MessagingAction
    data object PauseRun : MessagingAction
    data object FinishRun : MessagingAction
    data object Trackable : MessagingAction
    data object Untrackable : MessagingAction
    data object ConnectionRequest : MessagingAction
    data class HeartRateUpdate(val newHeartRate: Int) : MessagingAction
    data class DistanceUpdate(val distanceMeters: Int) : MessagingAction
    data class TimeUpdate(val elapsedDuration: Duration) : MessagingAction
}