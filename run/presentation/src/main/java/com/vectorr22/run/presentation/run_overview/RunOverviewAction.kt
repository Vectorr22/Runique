package com.vectorr22.run.presentation.run_overview

sealed interface RunOverviewAction {
    data object onStartClick: RunOverviewAction

    data object onLogoutClick: RunOverviewAction

    data object onAnalyticsClick: RunOverviewAction
}