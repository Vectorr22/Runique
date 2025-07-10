package com.plcoding.core.data.di

import com.plcoding.core.data.auth.EncryptedSessionStorage
import com.plcoding.core.data.networking.HttpClientFactory
import com.plcoding.core.data.run.OfflineFirstRunRepository
import com.plcoding.core.domain.SessionStorage
import com.plcoding.core.domain.run.RunRepository
import io.ktor.client.engine.cio.CIO
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(get()).build(CIO.create())
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
}