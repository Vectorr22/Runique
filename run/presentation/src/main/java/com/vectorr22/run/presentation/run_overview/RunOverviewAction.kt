package com.vectorr22.run.presentation.run_overview

import com.vectorr22.run.presentation.run_overview.module.RunUi

sealed interface RunOverviewAction {
    data object onStartClick: RunOverviewAction

    data object onLogoutClick: RunOverviewAction

    data object onAnalyticsClick: RunOverviewAction

    data class DeleteRun(val run: RunUi): RunOverviewAction
}