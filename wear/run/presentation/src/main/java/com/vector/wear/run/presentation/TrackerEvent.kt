package com.vector.wear.run.presentation

sealed interface TrackerEvent {
    data object RunFinished: TrackerEvent
}