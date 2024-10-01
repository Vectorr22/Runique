package com.vectorr22.run.di

import com.vectorr22.run.presentation.active_run.ActiveRunViewModel
import com.vectorr22.run.presentation.run_overview.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val runViewModelModule = module {
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}