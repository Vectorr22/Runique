package com.vectorr22.run.location.di

import com.plcoding.run.domain.LocationObserver
import com.vectorr22.run.location.AndroidLocationObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule = module {
    singleOf(::AndroidLocationObserver).bind<LocationObserver>()
}