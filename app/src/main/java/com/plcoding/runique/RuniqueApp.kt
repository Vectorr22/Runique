package com.plcoding.runique

import android.app.Application
import com.plcoding.auth.data.di.authDataModule
import com.plcoding.auth.presentation.di.authViewModelModule
import com.plcoding.core.data.di.coreDataModule
import com.plcoding.runique.di.appModule
import com.vectorr22.run.di.runViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp: Application() {
    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            modules(
                authDataModule,
                authViewModelModule,
                appModule,
                coreDataModule,
                runViewModelModule
            )
        }
    }
}