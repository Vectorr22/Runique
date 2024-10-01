@file:OptIn(ExperimentalMaterial3Api::class)

package com.vectorr22.run.presentation.run_overview

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.core.presentation.designsystem.AnalyticsIcon
import com.plcoding.core.presentation.designsystem.LogoIcon
import com.plcoding.core.presentation.designsystem.LogoutIcon
import com.plcoding.core.presentation.designsystem.RunIcon
import com.plcoding.core.presentation.designsystem.RuniqueTheme
import com.plcoding.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.plcoding.core.presentation.designsystem.components.RuniqueScaffold
import com.plcoding.core.presentation.designsystem.components.RuniqueToolBar
import com.plcoding.core.presentation.designsystem.utils.DropDownItem
import com.plcoding.run.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick:() -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    RunOverviewScreen(
        onAction = { action ->
            when(action){
                RunOverviewAction.onStartClick -> onStartRunClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )

}

@Composable
private fun RunOverviewScreen(
    onAction: (RunOverviewAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )
    val menuItems = listOf(
        DropDownItem(
            icon = AnalyticsIcon,
            title = stringResource(id = R.string.analytics)
        ),
        DropDownItem(
            icon = LogoutIcon,
            title = stringResource(id = R.string.logout)
        )
    )
    RuniqueScaffold(
        topAppBar = {
            RuniqueToolBar(
                canNavigateBack = false,
                title = stringResource(id = R.string.runique_title),
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(35.dp)
                    )
                },
                scrollBehavior = scrollBehavior,
                menuItems = menuItems,
                onMenuItemClick = {index ->
                    when(index){
                        0 -> onAction(RunOverviewAction.onAnalyticsClick)
                        1 -> onAction(RunOverviewAction.onLogoutClick)
                    }
                }
            )
        },
        floatingActionButton = {
            RuniqueFloatingActionButton(
                icon = RunIcon,
                onButtonClicked = { onAction(RunOverviewAction.onStartClick) },
                contentDescription = stringResource(id = R.string.track_your_run),

            )
        }
    ) {padding ->
        
    }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RuniqueTheme {
        RunOverviewScreen(
            onAction = {}
        )
    }
}