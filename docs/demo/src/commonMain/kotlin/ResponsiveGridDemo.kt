// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.ResponsiveGrid
import io.github.suqi8.coui.kmp.basic.ResponsiveGridSpec
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.PercentWidthType
import io.github.suqi8.coui.kmp.utils.percentWidth

@Composable
fun ResponsiveGridDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground())
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ResponsiveGrid(
            spec = ResponsiveGridSpec.Grid(childGridNumber = 2),
            verticalGap = 12.dp,
            childHeight = 64.dp,
        ) {
            repeat(8) { GridTile("Grid ${it + 1}") }
        }
        ResponsiveGrid(
            spec = ResponsiveGridSpec.SpecificGap(childMinWidth = 120.dp, horizontalGap = 8.dp, childMinHeight = 60.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalGap = 8.dp,
        ) {
            repeat(7) { GridTile("Gap ${it + 1}") }
        }
        Box(Modifier.fillMaxWidth().percentWidth(type = PercentWidthType.CardList)) {
            Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                BasicComponent(title = "Percent-width card list", summary = "Centred on the COUI layout grid")
            }
        }
    }
}

@Composable
private fun GridTile(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(COUITheme.colorScheme.surfaceContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = COUITheme.textStyles.body2)
    }
}
