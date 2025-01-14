@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.vectorr22.run.presentation.run_overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.vectorr22.run.presentation.run_overview.components.RunListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick: () -> Unit,
    onLogoutClick:() -> Unit,
    onAnalyticsClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    RunOverviewScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                RunOverviewAction.onStartClick -> onStartRunClick()
                RunOverviewAction.onLogoutClick -> onLogoutClick()
                RunOverviewAction.onAnalyticsClick -> onAnalyticsClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RunOverviewScreen(
    state: RunOverViewState,
    onAction: (RunOverviewAction) -> Unit
) {
    LaunchedEffect(Unit) {
        onAction(RunOverviewAction.OnScreenAppeared)
    }
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
                title = stringResource(id = R.string.runique),
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
                onMenuItemClick = { index ->
                    when (index) {
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 16.dp),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.runs, key = { it.id }) {
                RunListItem(
                    runUi = it, onDeleteClick = {
                        onAction(RunOverviewAction.DeleteRun(it))
                    },
                    modifier = Modifier.animateItemPlacement()
                )
            }

        }

    }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RuniqueTheme {
        RunOverviewScreen(
            onAction = {},
            state = RunOverViewState()
        )
    }
}