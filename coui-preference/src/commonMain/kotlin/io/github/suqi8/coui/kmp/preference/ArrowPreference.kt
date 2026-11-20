// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.ArrowRight
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * An arrow with a title and a summary (COUI `COUIJumpPreference`).
 *
 * @param title The title of the [ArrowPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [ArrowPreference].
 * @param summaryColor The color of the summary.
 * @param startAction The [Composable] content on the start side of the [ArrowPreference].
 * @param endActions The [Composable] content on the end side of the [ArrowPreference].
 * @param bottomAction The [Composable] content at the bottom of the [ArrowPreference].
 * @param modifier The modifier to be applied to the [ArrowPreference].
 * @param insideMargin The margin inside the [ArrowPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [ArrowPreference] is clickable.
 * @param selected Whether the row is selected (`COUIPreference.setSelected`): the press layer stays on
 *   and the card-group dividers next to the row are hidden.
 * @param startBadge Whether a red dot sits on the top end corner of [startAction] (COUI `iconRedDotMode`).
 * @param endBadgeCount The red dot before the end content (COUI `endRedDotMode` / `endRedDotNum`):
 *   `null` hides it, `0` shows a plain dot and a positive value shows the number.
 * @param endIcon An optional 16dp icon right before the end content (COUI `coui_assign_icon`).
 */
@Composable
@NonRestartableComposable
fun ArrowPreference(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable RowScope.() -> Unit = {},
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    onClick: (() -> Unit)? = null,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
    selected: Boolean = false,
    startBadge: Boolean = false,
    endBadgeCount: Int? = null,
    endIcon: (@Composable () -> Unit)? = null,
) {
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            Row(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .align(Alignment.CenterVertically)
                    .weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                endActions()
            }
            JumpArrowEndAction(
                enabled = enabled,
            )
        },
        bottomAction = bottomAction,
        onClick = onClick,
        holdDownState = holdDownState,
        enabled = enabled,
        selected = selected,
        startBadge = startBadge,
        endBadgeCount = endBadgeCount,
        endIcon = endIcon,
    )
}

/**
 * The COUI jump arrow (`coui_preference_widget_jump` with `coui_btn_next`): a 12 x 24dp chevron in
 * [ArrowPreferenceDefaults.endActionColors], shared by every preference that uses the jump widget.
 */
@Composable
internal fun RowScope.JumpArrowEndAction(
    enabled: Boolean,
) {
    val actionColors = ArrowPreferenceDefaults.endActionColors()
    val tintFilter = remember(enabled, actionColors) {
        ColorFilter.tint(actionColors.color(enabled = enabled))
    }
    Image(
        modifier = Modifier
            // RTL mirroring is handled by the icon itself (autoMirror).
            .size(width = 12.dp, height = 24.dp)
            .align(Alignment.CenterVertically),
        imageVector = COUIIcons.Basic.ArrowRight,
        contentDescription = null,
        colorFilter = tintFilter,
    )
}

object ArrowPreferenceDefaults {
    /**
     * The default color of the arrow: `coui_btn_next` draws `coui_btn_next_normal` in
     * couiColorLabelTertiary and `coui_btn_next_disabled` in couiColorLabelQuaternary.
     */
    @Composable
    fun endActionColors(): EndActionColors {
        val color = COUITheme.colorScheme.labelTertiary
        val disabledColor = COUITheme.colorScheme.labelQuaternary
        return remember(color, disabledColor) {
            EndActionColors(
                color = color,
                disabledColor = disabledColor,
            )
        }
    }
}

@Immutable
data class EndActionColors(
    private val color: Color,
    private val disabledColor: Color,
) {
    @Stable
    internal fun color(enabled: Boolean): Color = if (enabled) color else disabledColor
}
