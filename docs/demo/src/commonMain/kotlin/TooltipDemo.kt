// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.IconTooltip
import io.github.suqi8.coui.kmp.basic.ImageTooltip
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.Tooltip
import io.github.suqi8.coui.kmp.basic.TooltipAnchorPosition
import io.github.suqi8.coui.kmp.basic.TooltipBox
import io.github.suqi8.coui.kmp.basic.TooltipMode
import io.github.suqi8.coui.kmp.basic.TooltipScope
import io.github.suqi8.coui.kmp.basic.rememberTooltipState
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Info
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch

@Composable
fun TooltipDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DemoTooltipButton(label = "Tooltips") {
                        Tooltip(text = "Tap the close icon to dismiss this tip.")
                    }
                    DemoTooltipButton(label = "Info", dismissOnTouchOutside = true) {
                        Tooltip(text = "Info tooltips have no close icon.", mode = TooltipMode.Info)
                    }
                    DemoTooltipButton(label = "Icon", placement = TooltipAnchorPosition.Below) {
                        IconTooltip(
                            title = "New feature",
                            text = "Icon tooltips pair an icon with a title and a body.",
                            icon = rememberVectorPainter(COUIIcons.Info),
                            dismissText = "Got it",
                        )
                    }
                    DemoTooltipButton(label = "Image", placement = TooltipAnchorPosition.Below) {
                        ImageTooltip(
                            title = "Image tooltip",
                            text = "Image tooltips show a cropped image above the text.",
                            image = ColorPainter(COUITheme.colorScheme.primaryContainer),
                            dismissText = "Got it",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoTooltipButton(
    label: String,
    placement: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    dismissOnTouchOutside: Boolean = false,
    tooltip: @Composable TooltipScope.() -> Unit,
) {
    val state = rememberTooltipState()
    val scope = rememberCoroutineScope()
    TooltipBox(
        state = state,
        tooltip = tooltip,
        placement = placement,
        dismissOnTouchOutside = dismissOnTouchOutside,
    ) {
        TextButton(
            text = label,
            onClick = { if (state.isVisible) state.dismiss() else scope.launch { state.show() } },
        )
    }
}
