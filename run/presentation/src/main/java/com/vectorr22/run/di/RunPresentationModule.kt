package com.vectorr22.run.di

import com.plcoding.run.domain.RunningTracker
import com.vectorr22.run.presentation.active_run.ActiveRunViewModel
import com.vectorr22.run.presentation.run_overview.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val runPresentationModule = module {
    singleOf(::RunningTracker)
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}