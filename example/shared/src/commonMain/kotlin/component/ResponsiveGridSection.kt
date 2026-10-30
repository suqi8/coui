// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.ResponsiveGrid
import io.github.suqi8.coui.kmp.basic.ResponsiveGridSpec
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.PercentWidthType
import io.github.suqi8.coui.kmp.utils.percentWidth

fun LazyListScope.responsiveGridSection() {
    item(key = "responsiveGrid") {
        SmallTitle(text = "ResponsiveGrid")
        // Grid mode: each tile spans two layout-grid columns, so 2 / 4 / 6 tiles per row on
        // compact / medium / expanded widths.
        ResponsiveGrid(
            spec = ResponsiveGridSpec.Grid(childGridNumber = 2),
            modifier = Modifier.padding(bottom = 12.dp),
            verticalGap = 12.dp,
            childHeight = 72.dp,
        ) {
            repeat(8) { GridDemoTile("Grid ${it + 1}") }
        }
        // Specific-gap mode: tiles at least 120dp wide, 8dp apart, stretched to fill the row.
        ResponsiveGrid(
            spec = ResponsiveGridSpec.SpecificGap(childMinWidth = 120.dp, horizontalGap = 8.dp, childMinHeight = 60.dp),
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
            verticalGap = 8.dp,
        ) {
            repeat(7) { GridDemoTile("Gap ${it + 1}") }
        }
        // Specific-size mode: fixed 96dp tiles, the leftover width spread between them.
        ResponsiveGrid(
            spec = ResponsiveGridSpec.SpecificSize(childWidth = 96.dp, minHorizontalGap = 8.dp),
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
            verticalGap = 8.dp,
            childHeight = 56.dp,
        ) {
            repeat(6) { GridDemoTile("Size ${it + 1}") }
        }
    }
    item(key = "percentWidth") {
        SmallTitle(text = "percentWidth")
        // A card list on the percent-width grid: 4 / 6 / 8 columns wide by the window size,
        // widened by the 16dp card-list special padding.
        Box(Modifier.fillMaxWidth().percentWidth(type = PercentWidthType.CardList)) {
            Card(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)) {
                BasicComponent(title = "Percent-width card list", summary = "Centred on the COUI layout grid")
                BasicComponent(title = "Wider windows", summary = "6 of 8 columns, or 8 of 12 columns")
            }
        }
    }
}

@Composable
private fun GridDemoTile(label: String) {
    Box(
        modifier = Modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(COUITheme.colorScheme.surfaceContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = COUITheme.textStyles.body2)
    }
}
