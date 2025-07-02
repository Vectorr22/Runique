package com.vector.wear.run.presentation.di

import com.vector.wear.run.domain.RunningTracker
import com.vector.wear.run.presentation.TrackerViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.time.Duration

val wearRunPresentationModule = module {
    viewModelOf(::TrackerViewModel)
    single<StateFlow<Duration>> {
        get<RunningTracker>().elapsedTime
    }
}