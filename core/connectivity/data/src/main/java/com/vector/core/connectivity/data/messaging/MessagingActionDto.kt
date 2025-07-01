package com.vector.core.connectivity.data.messaging

import com.vector.core.connectivity.domain.messaging.MessagingAction
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
sealed interface MessagingActionDto {
    @Serializable
    data object StartOrResume : MessagingActionDto
    @Serializable
    data object PauseRun : MessagingActionDto
    @Serializable
    data object FinishRun : MessagingActionDto
    @Serializable
    data object Trackable : MessagingActionDto
    @Serializable
    data object Untrackable : MessagingActionDto
    @Serializable
    data object ConnectionRequest : MessagingActionDto
    @Serializable
    data class HeartRateUpdate(val newHeartRate: Int) : MessagingActionDto
    @Serializable
    data class DistanceUpdate(val distanceMeters: Int) : MessagingActionDto
    @Serializable
    data class TimeUpdate(val elapsedDuration: Duration) : MessagingActionDto
}