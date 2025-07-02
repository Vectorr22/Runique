package com.vector.wear.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.vector.core.notification.ActiveRunService
import com.vector.core.presentation.designsystem_wear.RuniqueTheme
import com.vector.wear.run.presentation.TrackerScreenRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            RuniqueTheme {
                TrackerScreenRoot(
                    onServiceToggle = { shouldStartRuning ->
                        if (shouldStartRuning) {
                            startService(
                                ActiveRunService.createStartIntent(
                                    applicationContext,
                                    this::class.java
                                )
                            )
                        } else {
                            startService(
                                ActiveRunService.createStopIntent(
                                    applicationContext
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}
