// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NavigationRail(
    items: List<NavigationItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxVisibleItems: Int = 4,
    colors: NavigationColors = CouiNavigationDefaults.colors()
) {
    if (items.isEmpty()) return

    val visibleItems = items.take(maxVisibleItems.coerceAtLeast(1))

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(NavigationRailTokens.ContainerWidth)
            .background(colors.containerColor),
        color = colors.containerColor,
        contentColor = colors.itemColors.contentColor(enabled = true, selected = false).value
    ) {
        Row(modifier = Modifier.fillMaxHeight()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(top = NavigationRailTokens.TopPadding)
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Vertical))
            ) {
                visibleItems.forEachIndexed { index, item ->
                    NavigationRailItem(
                        item = item,
                        isSelected = index == selectedIndex,
                        onClick = { onItemSelected(index) },
                        colors = colors.itemColors
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                thickness = NavigationRailTokens.DividerWidth,
                color = colors.dividerColor
            )
        }
    }
}
