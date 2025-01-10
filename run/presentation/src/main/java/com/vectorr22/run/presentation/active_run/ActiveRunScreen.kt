@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package com.vectorr22.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.core.presentation.designsystem.RuniqueTheme
import com.plcoding.core.presentation.designsystem.StartIcon
import com.plcoding.core.presentation.designsystem.StopIcon
import com.plcoding.core.presentation.designsystem.components.RuniqueActionButton
import com.plcoding.core.presentation.designsystem.components.RuniqueDialog
import com.plcoding.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.plcoding.core.presentation.designsystem.components.RuniqueOutlinedActionButton
import com.plcoding.core.presentation.designsystem.components.RuniqueScaffold
import com.plcoding.core.presentation.designsystem.components.RuniqueToolBar
import com.plcoding.core.presentation.ui.ObserveAsEvents
import com.plcoding.run.presentation.R
import com.vectorr22.run.presentation.active_run.components.RunDataCard
import com.vectorr22.run.presentation.active_run.maps.TrackerMap
import com.vectorr22.run.presentation.active_run.services.ActiveRunService
import com.vectorr22.run.presentation.util.hasLocationPermission
import com.vectorr22.run.presentation.util.hasNotificationPermission
import com.vectorr22.run.presentation.util.shouldShowLocationPermissionRationale
import com.vectorr22.run.presentation.util.shouldShowNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@Composable
fun ActiveRunScreenRoot(
    onServiceToggle: (isServiceBoolean: Boolean) -> Unit,
    viewModel: ActiveRunViewModel = koinViewModel(),
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    ObserveAsEvents(flow = viewModel.events) { event ->
        when(event){
            is ActiveRunEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
            ActiveRunEvent.RunSaved -> onFinish()
        }
        
    }
    ActiveRunScreen(
        state = viewModel.state,
        onServiceToggle = onServiceToggle,
        onAction = { action ->
            when(action){
               ActiveRunAction.OnBackClick -> {
                   if(!viewModel.state.hasStartedRunning){
                       onBack()
                   }
               }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun ActiveRunScreen(
    state: ActiveRunState,
    onServiceToggle: (isServiceBoolean: Boolean) -> Unit,
    onAction: (ActiveRunAction) -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher =
        rememberLauncherForActivityResult( //Lanza una activity para solicitar permisos
            contract = ActivityResultContracts.RequestMultiplePermissions() //El contrato es que va a lanzar, por asi decirlo, en este caso, multiples permisos.
        ) { perms ->
            val hasCourseLocationPermission =
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true //Evalua si ya hay permisos y los almacena
            val hasFineLocationPermission = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val hasPostNotificationPermission = if (Build.VERSION.SDK_INT >= 33) {
                perms[Manifest.permission.POST_NOTIFICATIONS] == true
            } else true

            val activity =
                context as ComponentActivity //para mandar a llamar a metodos de permisos necesitamos el contexto de component activity
            val showLocationRationale = activity.shouldShowLocationPermissionRationale()
            val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

            onAction(   //mandamos a llamar o actualizar el viewModel
                ActiveRunAction.SubmitLocationPermissionInfo(
                    locationPermissionAccepted = hasCourseLocationPermission && hasFineLocationPermission,
                    showLocationRationale = showLocationRationale
                )
            )

            onAction(
                ActiveRunAction.SubmitNotificationPermissionInfo(
                    notificationPermissionAccepted = hasPostNotificationPermission,
                    showNotificationRationale = showNotificationRationale
                )
            )
        }
    LaunchedEffect(key1 = true) {
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                locationPermissionAccepted = activity.hasLocationPermission(),
                showLocationRationale = showLocationRationale
            )
        )

        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                notificationPermissionAccepted = activity.hasNotificationPermission(),
                showNotificationRationale = showNotificationRationale
            )
        )

        if (!showNotificationRationale && !showLocationRationale) {
            permissionLauncher.requestRuniquePermissions(context)
        }
    }

    LaunchedEffect(key1 = state.isRunFinished) {
        if(state.isRunFinished){
            onServiceToggle(false)
        }
        
    }
    LaunchedEffect(key1 = state.shouldTrack) {
        if (state.shouldTrack && context.hasNotificationPermission() && !ActiveRunService.isServiceActive) {
            onServiceToggle(true)
        }
    }
    RuniqueScaffold(
        topAppBar = {
            RuniqueToolBar(
                canNavigateBack = true,
                title = stringResource(R.string.active_run),
                onNavigateBack = { onAction(ActiveRunAction.OnBackClick) }
            )
        },
        floatingActionButton = {
            RuniqueFloatingActionButton(
                icon = if (state.shouldTrack) StopIcon else StartIcon,
                onButtonClicked = { onAction(ActiveRunAction.OnToggleRunClick) },
                iconSize = 20.dp,
                contentDescription =
                if (state.shouldTrack)
                    stringResource(id = R.string.start_your_run)
                else
                    stringResource(id = R.string.stop_run)
            )
        },
        withGradient = false
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(padding)
        ) {
            TrackerMap(
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                listOfLocations = state.runData.location,
                onSnapshot = { bmp ->
                    val stream = ByteArrayOutputStream()
                    stream.use {
                        bmp.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            it
                        )
                    }
                    onAction(ActiveRunAction.OnRunProcessed(mapPictureBytes = stream.toByteArray()))
                },
                modifier = Modifier.fillMaxSize()
            )
            RunDataCard(
                runData = state.runData,
                elapsedTime = state.elapsedTime,
                modifier = Modifier
                    .padding(12.dp)
            )
        }

    }

    if (!state.shouldTrack && state.hasStartedRunning) {
        RuniqueDialog(
            title = stringResource(id = R.string.running_is_paused),
            description = stringResource(id = R.string.resume_or_finish_run),
            onDismiss = {
                onAction(ActiveRunAction.OnResumeRun)
            },
            primaryButton = {
                RuniqueActionButton(
                    text = stringResource(id = R.string.resume),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.OnResumeRun)
                    },
                    modifier = Modifier
                        .weight(1f)
                )
            },
            secondaryButton = {
                RuniqueOutlinedActionButton(
                    text = stringResource(id = R.string.finish),
                    isLoading = state.isSavingRun,
                    onClick = {
                        onAction(ActiveRunAction.OnFinishRunClick)
                    },
                    modifier = Modifier
                        .weight(1f)
                )
            }
        )
    }
    if (state.showLocationRationale || state.showNotificationRationale) {
        RuniqueDialog(
            title = stringResource(id = R.string.permission_required),
            description = when {
                state.showNotificationRationale && state.showLocationRationale -> {
                    stringResource(id = R.string.location_notification_rationale)
                }

                state.showLocationRationale -> {
                    stringResource(id = R.string.location_rationale)
                }

                else -> {
                    stringResource(id = R.string.notification_rationale)
                }
            },
            onDismiss = { /* onDismiss will be empty because user should only accept or not the permissions, he cant evade the dialog buttons.*/ },
            primaryButton = {
                RuniqueOutlinedActionButton(
                    text = stringResource(id = R.string.okay),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.DismissRationaleDialog)
                        permissionLauncher.requestRuniquePermissions(context)
                    }
                )
            }
        )
    }

}

private fun ActivityResultLauncher<Array<String>>.requestRuniquePermissions(
    context: Context
) {
    val hasLocationPermissions = context.hasLocationPermission()
    val hasNotificationPermissions = context.hasNotificationPermission()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val notificationPermissions = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else emptyArray()

    when {
        !hasLocationPermissions && !hasNotificationPermissions -> {
            launch(locationPermissions + notificationPermissions)
        }

        !hasLocationPermissions -> launch(locationPermissions)
        !hasNotificationPermissions -> launch(notificationPermissions)
    }
}



@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RuniqueTheme {
        ActiveRunScreen(
            state = ActiveRunState(),
            onAction = {},
            onServiceToggle = {}
        )
    }
}