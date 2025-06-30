package com.vector.wear.run.data.di

import com.vector.wear.run.data.HealthServiceExerciseTracker
import com.vector.wear.run.domain.ExerciseTracker
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val wearRunDataModule = module {
    singleOf(::HealthServiceExerciseTracker).bind<ExerciseTracker>()
}