package com.plcoding.runique

import android.app.Application
import com.plcoding.auth.data.di.authDataModule
import com.plcoding.auth.presentation.di.authViewModelModule
import com.plcoding.core.data.di.coreDataModule
import com.plcoding.core.database.di.databaseModule
import com.plcoding.run.network.di.netWorkModule
import com.plcoding.runique.di.appModule
import com.vector.run.data.di.runDataModule
import com.vectorr22.run.di.runPresentationModule
import com.vectorr22.run.location.di.locationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            workManagerFactory()
            modules(
                authDataModule,
                authViewModelModule,
                appModule,
                coreDataModule,
                runPresentationModule,
                locationModule,
                databaseModule,
                netWorkModule,
                runDataModule
            )
        }
    }
}