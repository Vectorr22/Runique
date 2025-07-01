package com.vector.core.connectivity.data.messaging

import com.vector.core.connectivity.domain.messaging.MessagingAction

fun MessagingAction.toMessagingActionDto(): MessagingActionDto {
    return when(this) {
        MessagingAction.ConnectionRequest -> MessagingActionDto.ConnectionRequest
        is MessagingAction.DistanceUpdate -> MessagingActionDto.DistanceUpdate(this.distanceMeters)
        MessagingAction.FinishRun -> MessagingActionDto.FinishRun
        is MessagingAction.HeartRateUpdate -> MessagingActionDto.HeartRateUpdate(this.newHeartRate)
        MessagingAction.PauseRun -> MessagingActionDto.PauseRun
        MessagingAction.StartOrResume -> MessagingActionDto.StartOrResume
        is MessagingAction.TimeUpdate -> MessagingActionDto.TimeUpdate(this.elapsedDuration)
        MessagingAction.Trackable -> MessagingActionDto.Trackable
        MessagingAction.Untrackable -> MessagingActionDto.Untrackable
    }
}

fun MessagingActionDto.toMessagingAction(): MessagingAction {
    return when(this) {
        MessagingActionDto.ConnectionRequest -> MessagingAction.ConnectionRequest
        is MessagingActionDto.DistanceUpdate -> MessagingAction.DistanceUpdate(distanceMeters)
        MessagingActionDto.FinishRun -> MessagingAction.FinishRun
        is MessagingActionDto.HeartRateUpdate -> MessagingAction.HeartRateUpdate(newHeartRate)
        MessagingActionDto.PauseRun -> MessagingAction.PauseRun
        MessagingActionDto.StartOrResume -> MessagingAction.StartOrResume
        is MessagingActionDto.TimeUpdate -> MessagingAction.TimeUpdate(elapsedDuration)
        MessagingActionDto.Trackable -> MessagingAction.Trackable
        MessagingActionDto.Untrackable -> MessagingAction.Untrackable
    }
}