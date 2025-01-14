package com.vector.analytics.data.di

import com.plcoding.core.database.RunDatabase
import com.vector.analytics.data.RoomAnalyticsRepository
import com.vector.analytics.domain.AnalyticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
    single {
        get<RunDatabase>().analyticsDao
    }
}