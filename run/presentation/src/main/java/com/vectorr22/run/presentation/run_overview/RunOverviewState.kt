package com.vectorr22.run.presentation.run_overview

import com.vectorr22.run.presentation.run_overview.module.RunUi

data class RunOverviewState(
    val runs: List<RunUi> = emptyList()
)
