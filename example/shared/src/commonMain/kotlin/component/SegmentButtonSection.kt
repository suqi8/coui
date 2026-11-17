// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.SegmentButton
import io.github.suqi8.coui.kmp.basic.SegmentButtonDefaults
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.painterResource
import utils.rememberMaterialBackdropProvider

fun LazyListScope.segmentButtonSection() {
    item(key = "segmentButton") {
        SmallTitle(text = "SegmentButton")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var wrapIndex by remember { mutableIntStateOf(0) }
                SegmentButton(
                    items = listOf("Day", "Week", "Month"),
                    selectedIndex = wrapIndex,
                    onSelectedIndexChange = { wrapIndex = it },
                )

                var fillIndex by remember { mutableIntStateOf(1) }
                SegmentButton(
                    items = listOf("All", "Allowed", "Not allowed"),
                    selectedIndex = fillIndex,
                    onSelectedIndexChange = { fillIndex = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                var smallIndex by remember { mutableIntStateOf(0) }
                SegmentButton(
                    items = listOf("List", "Grid"),
                    selectedIndex = smallIndex,
                    onSelectedIndexChange = { smallIndex = it },
                    height = SegmentButtonDefaults.HeightSmall,
                    padding = SegmentButtonDefaults.PaddingSmall,
                )

                var materialIndex by remember { mutableIntStateOf(0) }
                SegmentButton(
                    items = listOf("Photos", "Albums", "Search"),
                    selectedIndex = materialIndex,
                    onSelectedIndexChange = { materialIndex = it },
                    modifier = Modifier.fillMaxWidth(),
                    materialEffect = true,
                )

                SegmentButton(
                    items = listOf("On", "Off"),
                    selectedIndex = 0,
                    onSelectedIndexChange = {},
                    enabled = false,
                )
            }
        }
        MaterialBackdropSegmentButton()
    }
}

/**
 * A material [SegmentButton] over an image, blurring it through a [LocalMaterialBackdrop]
 * provider recorded from the image.
 */
@Composable
private fun MaterialBackdropSegmentButton() {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberMaterialBackdropProvider(backdrop)
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
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Material backdrop",
                    color = COUITheme.colorScheme.labelPrimary,
                    style = COUITheme.textStyles.bodyS,
                )
                var index by remember { mutableIntStateOf(0) }
                SegmentButton(
                    items = listOf("Recent", "Favorites", "Shared"),
                    selectedIndex = index,
                    onSelectedIndexChange = { index = it },
                    modifier = Modifier.fillMaxWidth(),
                    materialEffect = true,
                )
            }
        }
    }
}
