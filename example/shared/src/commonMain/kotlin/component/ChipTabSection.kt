// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.ChipTab
import io.github.suqi8.coui.kmp.basic.ChipTabMaterialType
import io.github.suqi8.coui.kmp.basic.ChipTabRow
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Check
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.icon.basic.Sidebar
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import org.jetbrains.compose.resources.painterResource
import utils.rememberMaterialBackdropProvider

private val ChipTabDemoTabs = listOf("All", "Photos", "Videos", "Documents", "Music", "Downloads", "Archives", "Others")

fun LazyListScope.chipTabSection() {
    item(key = "chipTab") {
        SmallTitle(text = "ChipTab")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            ScrollingChipTabRowDemo()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            LeadingIconChipTabRowDemo()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SingleChipTabsDemo()
        }
        BackdropChipTabRowsDemo()
    }
}

@Composable
private fun ScrollingChipTabRowDemo() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    ChipTabRow(
        tabs = ChipTabDemoTabs,
        selectedTabIndex = selectedIndex,
        onTabSelected = { selectedIndex = it },
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun LeadingIconChipTabRowDemo() {
    var selectedIndex by remember { mutableIntStateOf(1) }
    ChipTabRow(
        tabs = ChipTabDemoTabs,
        selectedTabIndex = selectedIndex,
        onTabSelected = { selectedIndex = it },
        modifier = Modifier.padding(vertical = 8.dp),
        onLeadingIconClick = { selectedIndex = 0 },
        leadingIcon = {
            Icon(
                imageVector = COUIIcons.Basic.Sidebar,
                contentDescription = "Categories",
                modifier = Modifier.size(20.dp),
            )
        },
    )
}

@Composable
private fun SingleChipTabsDemo() {
    var withIcon by remember { mutableStateOf(true) }
    var plain by remember { mutableStateOf(false) }
    var iconOnly by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChipTab(
                selected = withIcon,
                onClick = { withIcon = !withIcon },
                label = "Starred",
                icon = {
                    Icon(
                        imageVector = COUIIcons.Basic.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
            ChipTab(
                selected = plain,
                onClick = { plain = !plain },
                label = "No material",
                materialEffect = false,
            )
            ChipTab(
                selected = iconOnly,
                onClick = { iconOnly = !iconOnly },
                icon = {
                    Icon(
                        imageVector = COUIIcons.Basic.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChipTab(
                selected = false,
                onClick = {},
                label = "Disabled",
                enabled = false,
            )
            ChipTab(
                selected = true,
                onClick = {},
                label = "Disabled selected",
                enabled = false,
            )
        }
    }
}

/**
 * The COUI 17 chip tab material over a recorded image: the backdrop replaces the fill with the
 * blurred (or, for the no-blur type, filtered) and tinted image.
 */
@Composable
private fun BackdropChipTabRowsDemo() {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberMaterialBackdropProvider(backdrop)
    var selectedBlur by remember { mutableIntStateOf(0) }
    var selectedNoBlur by remember { mutableIntStateOf(2) }
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
    ) {
        Image(
            painter = painterResource(Res.drawable.blur_test_bg),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .layerBackdrop(backdrop),
            contentScale = ContentScale.Crop,
        )
        CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                ChipTabRow(
                    tabs = ChipTabDemoTabs,
                    selectedTabIndex = selectedBlur,
                    onTabSelected = { selectedBlur = it },
                )
                ChipTabRow(
                    tabs = ChipTabDemoTabs,
                    selectedTabIndex = selectedNoBlur,
                    onTabSelected = { selectedNoBlur = it },
                    materialType = ChipTabMaterialType.NoBlur,
                )
            }
        }
    }
}
