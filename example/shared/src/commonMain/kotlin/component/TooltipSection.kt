// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.IconTooltip
import io.github.suqi8.coui.kmp.basic.ImageTooltip
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.Tooltip
import io.github.suqi8.coui.kmp.basic.TooltipAnchorPosition
import io.github.suqi8.coui.kmp.basic.TooltipBox
import io.github.suqi8.coui.kmp.basic.TooltipMode
import io.github.suqi8.coui.kmp.basic.TooltipScope
import io.github.suqi8.coui.kmp.basic.rememberTooltipState
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.ic_launcher
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

fun LazyListScope.tooltipSection() {
    item(key = "tooltip") {
        SmallTitle(text = "Tooltip")
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TooltipDemoButton(label = "Tooltips") {
                        Tooltip(text = "Tap the close icon to dismiss this tip.")
                    }
                    TooltipDemoButton(label = "Info", dismissOnTouchOutside = true) {
                        Tooltip(text = "Info tooltips have no close icon; tap outside.", mode = TooltipMode.Info)
                    }
                    TooltipDemoButton(label = "Icon", placement = TooltipAnchorPosition.Below) {
                        IconTooltip(
                            title = "New feature",
                            text = "Icon tooltips pair a 40dp icon with a title and a body.",
                            icon = painterResource(Res.drawable.ic_launcher),
                            dismissText = "Got it",
                        )
                    }
                    TooltipDemoButton(label = "Image", placement = TooltipAnchorPosition.Below) {
                        ImageTooltip(
                            title = "Image tooltip",
                            text = "Image tooltips are 260dp wide with a cropped image on top.",
                            image = painterResource(Res.drawable.blur_test_bg),
                            dismissText = "Got it",
                        )
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TooltipDemoButton(label = "Start", placement = TooltipAnchorPosition.Start, dismissOnTouchOutside = true) {
                        Tooltip(text = "Start", mode = TooltipMode.Info)
                    }
                    TooltipDemoButton(label = "No arrow", showArrow = false, dismissOnTouchOutside = true) {
                        Tooltip(text = "A tooltip without its arrow.", mode = TooltipMode.Info)
                    }
                    TooltipDemoButton(label = "End", placement = TooltipAnchorPosition.End, dismissOnTouchOutside = true) {
                        Tooltip(text = "End", mode = TooltipMode.Info)
                    }
                }
            }
        }
    }
}

@Composable
private fun TooltipDemoButton(
    label: String,
    placement: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    showArrow: Boolean = true,
    dismissOnTouchOutside: Boolean = false,
    tooltip: @Composable TooltipScope.() -> Unit,
) {
    val state = rememberTooltipState()
    val scope = rememberCoroutineScope()
    TooltipBox(
        state = state,
        tooltip = tooltip,
        placement = placement,
        showArrow = showArrow,
        dismissOnTouchOutside = dismissOnTouchOutside,
    ) {
        TextButton(
            text = label,
            onClick = {
                if (state.isVisible) state.dismiss() else scope.launch { state.show() }
            },
        )
    }
}
