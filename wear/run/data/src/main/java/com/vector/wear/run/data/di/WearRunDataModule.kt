package com.vector.wear.run.data.di

import com.vector.wear.run.data.HealthServiceExerciseTracker
import com.vector.wear.run.data.WatchToPhoneConnector
import com.vector.wear.run.domain.ExerciseTracker
import com.vector.wear.run.domain.PhoneConnector
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val wearRunDataModule = module {
    singleOf(::HealthServiceExerciseTracker).bind<ExerciseTracker>()
    singleOf(::WatchToPhoneConnector).bind<PhoneConnector>()
}