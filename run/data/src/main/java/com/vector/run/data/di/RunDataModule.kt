package com.vector.run.data.di

import com.plcoding.core.domain.run.SyncRunScheduler
import com.vector.run.data.CreateRunWorker
import com.vector.run.data.DeleteRunWorker
import com.vector.run.data.FetchRunsWorker
import com.vector.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)
    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}