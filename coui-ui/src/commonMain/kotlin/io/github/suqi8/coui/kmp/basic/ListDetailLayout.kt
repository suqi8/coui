// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid

/**
 * A list-detail container ported from `COUIListDetailView`.
 *
 * On a compact layout (narrower than 600dp, `WindowTotalSizeClass.Compact`) it is a single pane:
 * the [detail] when there is one, otherwise the [list]. Wider, the list is
 * `clamp(width * listWidthFraction, minListWidth, maxListWidth)` wide on the start side, a
 * hairline divider follows, and the rest shows the [detail], or [emptyDetail] while [detail] is
 * null (`COUIListDetailView`'s empty-page container).
 *
 * The size class is taken from the layout's own size, as `COUIListDetailView.onMeasure` does.
 *
 * @param list The list (main) pane.
 * @param detail The detail (sub) pane, or null when nothing is selected.
 * @param modifier The modifier to be applied to the layout.
 * @param listWidthFraction `setMainFragmentPercent`: the list's share of the width.
 * @param minListWidth `coui_main_fragment_min_width`.
 * @param maxListWidth `coui_main_fragment_max_width`.
 * @param dividerColor The divider color (`couiColorDivider`).
 * @param dividerThickness `coui_fragment_gap_width`.
 * @param emptyDetail What the detail pane shows while [detail] is null in split mode.
 */
@Composable
fun ListDetailLayout(
    list: @Composable () -> Unit,
    detail: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    listWidthFraction: Float = ListDetailLayoutDefaults.ListWidthFraction,
    minListWidth: Dp = ListDetailLayoutDefaults.MinListWidth,
    maxListWidth: Dp = ListDetailLayoutDefaults.MaxListWidth,
    dividerColor: Color = DividerDefaults.DividerColor,
    dividerThickness: Dp = ListDetailLayoutDefaults.DividerThickness,
    emptyDetail: @Composable () -> Unit = {},
) {
    val windowHeight = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
    // Both panes move between the single-pane and the split arrangement without losing state.
    val currentList by rememberUpdatedState(list)
    val currentDetail by rememberUpdatedState(detail)
    val listPane = remember { movableContentOf { currentList() } }
    val detailPane = remember { movableContentOf { currentDetail?.invoke() } }
    BoxWithConstraints(modifier = modifier.clipToBounds()) {
        val height = if (constraints.hasBoundedHeight) maxHeight else windowHeight
        if (!ListDetailLayoutDefaults.isSplitMode(maxWidth, height)) {
            Box(Modifier.fillMaxSize()) {
                if (detail != null) detailPane() else listPane()
            }
        } else {
            val listWidth = (maxWidth * listWidthFraction)
                .coerceAtMost(maxListWidth)
                .coerceAtLeast(minListWidth)
                .coerceAtMost(maxWidth)
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.width(listWidth).fillMaxHeight()) { listPane() }
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    thickness = dividerThickness,
                    color = dividerColor,
                )
                Box(Modifier.weight(1f).fillMaxHeight()) {
                    if (detail != null) detailPane() else emptyDetail()
                }
            }
        }
    }
}

/** Default values of [ListDetailLayout]. */
object ListDetailLayoutDefaults {

    /** `DEFAULT_MAIN_WIDTH_PERCENT`. */
    val ListWidthFraction: Float = 0.4f

    /** `coui_main_fragment_min_width`. */
    val MinListWidth: Dp = 280.dp

    /** `coui_main_fragment_max_width`. */
    val MaxListWidth: Dp = 412.dp

    /** `coui_fragment_gap_width`. */
    val DividerThickness: Dp = 0.33.dp

    /**
     * Whether a layout [width] x [height] shows both panes: any `WindowTotalSizeClass` other
     * than `Compact`, i.e. at least 600dp wide.
     */
    fun isSplitMode(width: Dp, height: Dp): Boolean = COUILayoutGrid.screenTier(width, height) != COUILayoutGrid.ScreenTier.Small
}
