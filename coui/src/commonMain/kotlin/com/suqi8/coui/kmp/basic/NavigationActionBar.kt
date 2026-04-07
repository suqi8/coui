// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mocharealm.gaze.capsule.ContinuousRoundedRectangle
import com.suqi8.coui.kmp.theme.COUITheme

@Immutable
data class NavigationActionItem(
    val label: String,
    val icon: ImageVector,
    val enabled: Boolean = true,
    val onClick: () -> Unit,
)

@Composable
fun NavigationActionBar(
    actions: List<NavigationActionItem>,
    modifier: Modifier = Modifier,
    containerColor: Color = COUITheme.colorScheme.surfaceContainer,
    contentColor: Color = COUITheme.colorScheme.onSurface,
    dividerColor: Color = COUITheme.colorScheme.dividerLine,
) {
    if (actions.isEmpty()) return

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
        contentColor = contentColor
    ) {
        ColumnWithTopDivider(dividerColor = dividerColor) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = NavigationActionBarTokens.MinHeight)
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                    .padding(NavigationActionBarTokens.ContentPadding),
                horizontalArrangement = Arrangement.spacedBy(NavigationActionBarTokens.ItemSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions.forEach { action ->
                    Surface(
                        onClick = action.onClick,
                        enabled = action.enabled,
                        modifier = Modifier.weight(1f),
                        color = Color.Transparent,
                        shape = ContinuousRoundedRectangle(NavigationActionBarTokens.ItemCornerRadius)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.label,
                                tint = contentColor
                            )
                            Text(
                                text = action.label,
                                modifier = Modifier.padding(start = 8.dp),
                                color = contentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnWithTopDivider(
    dividerColor: Color,
    content: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.Column {
        HorizontalDivider(color = dividerColor, thickness = 0.33.dp)
        content()
    }
}

private object NavigationActionBarTokens {
    val MinHeight = 56.dp
    val ItemSpacing = 8.dp
    val ItemCornerRadius = 18.dp
    val ContentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
}
