package com.vector.wear.run.presentation

sealed interface TrackerAction {
    data object OnToggleRunClick: TrackerAction
    data object OnFinishRunClick: TrackerAction

}