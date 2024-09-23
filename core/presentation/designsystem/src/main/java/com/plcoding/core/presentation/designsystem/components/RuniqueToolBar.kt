@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.plcoding.core.presentation.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.core.presentation.designsystem.AnalyticsIcon
import com.plcoding.core.presentation.designsystem.ArrowLeftIcon
import com.plcoding.core.presentation.designsystem.LogoIcon
import com.plcoding.core.presentation.designsystem.Poppins
import com.plcoding.core.presentation.designsystem.R
import com.plcoding.core.presentation.designsystem.RuniqueGreen
import com.plcoding.core.presentation.designsystem.RuniqueTheme
import com.plcoding.core.presentation.designsystem.utils.DropDownItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuniqueToolBar(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    title: String,
    onNavigateBack: () -> Unit = {},
    menuItems: List<DropDownItem> = emptyList(),
    onMenuItemClick: (Int) -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    startContent: (@Composable () -> Unit)? = null
) {
    var isMenuClicked by rememberSaveable {
        mutableStateOf(false)
    }
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                startContent?.invoke()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = ArrowLeftIcon,
                        contentDescription = stringResource(id = R.string.go_back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        actions = {
            if (menuItems.isNotEmpty()) {
                Box {
                    DropdownMenu(
                        expanded = isMenuClicked,
                        onDismissRequest = { isMenuClicked = false }
                    ) {
                        menuItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .clickable { onMenuItemClick(index) }
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = item.icon, contentDescription = item.title)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item.title)
                            }
                        }
                    }
                    IconButton(onClick = {
                        isMenuClicked = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(
                                id = R.string.show_more_options
                            ),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}


@Preview
@Composable
private fun RuniqueToolBarPreview() {
    RuniqueTheme {
        RuniqueToolBar(
            canNavigateBack = false,
            title = "Runique",
            startContent = {
                Icon(
                    imageVector = LogoIcon,
                    contentDescription = null,
                    tint = RuniqueGreen,
                    modifier = Modifier
                        .size(35.dp)
                )
            },
            menuItems = listOf(
                DropDownItem(
                    AnalyticsIcon,
                    "Analytics"
                )
            )
        )
    }
}