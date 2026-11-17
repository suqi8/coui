// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.ProgressIndicatorDefaults
import io.github.suqi8.coui.kmp.basic.RotatingProgressIndicator
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A preference row whose end widget switches between a status text, a status icon and a loading
 * animation, mirroring COUIStatusSwitchingPreference.
 *
 * COUI keeps one of three widgets visible in coui_preference_widget_status_switching.xml, chosen by
 * the last `showText` / `showIcon` / `showAnim` call. Here the widget is derived from the
 * parameters, in this order:
 * - [switching] `true` (showAnim): the COUI rotating spinner, which stands in for the Lottie raw
 *   resource the original plays in a loop;
 * - [statusIcon] not `null` (showIcon): the icon;
 * - [status] not `null` (showText): the status text (couiTextAppearanceBody 14sp,
 *   couiColorLabelSecondary);
 * - otherwise (STATUS_TYPE_DEFAULT): no widget.
 *
 * The widget keeps 16dp to the title (coui_status_switching_preference_widget_margin_start).
 *
 * @param title The title of the [StatusSwitchingPreference].
 * @param modifier The modifier to be applied to the [StatusSwitchingPreference].
 * @param status The status text, for example "On" / "Off".
 * @param switching Whether the status is being switched; shows the loading spinner.
 * @param statusIcon The status icon, shown instead of [status] when not `null`.
 * @param titleColor The color of the title.
 * @param summary The summary of the [StatusSwitchingPreference].
 * @param summaryColor The color of the summary.
 * @param statusColor The color of the status text.
 * @param indicatorColor The color of the loading spinner.
 * @param indicatorSize The size of the loading spinner. COUI sizes the animation per call
 *   (showAnim(rawRes, width, height)); the default is the COUILottieLoadingView default tier.
 * @param startAction The [Composable] content on the start side of the [StatusSwitchingPreference].
 * @param bottomAction The [Composable] content at the bottom of the [StatusSwitchingPreference].
 * @param insideMargin The margin inside the [StatusSwitchingPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param onClick The callback when the row is clicked.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [StatusSwitchingPreference] is clickable.
 */
@Composable
fun StatusSwitchingPreference(
    title: String,
    modifier: Modifier = Modifier,
    status: String? = null,
    switching: Boolean = false,
    statusIcon: (@Composable () -> Unit)? = null,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    statusColor: Color = StatusSwitchingPreferenceDefaults.statusColor(),
    indicatorColor: Color = ProgressIndicatorDefaults.rotatingProgressIndicatorColor(),
    indicatorSize: Dp = ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorSize,
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    onClick: (() -> Unit)? = null,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
) {
    val hasWidget = switching || statusIcon != null || status != null
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = if (hasWidget) {
            {
                StatusSwitchingWidget(
                    status = status,
                    switching = switching,
                    statusIcon = statusIcon,
                    statusColor = statusColor,
                    indicatorColor = indicatorColor,
                    indicatorSize = indicatorSize,
                )
            }
        } else {
            null
        },
        bottomAction = bottomAction,
        onClick = onClick,
        holdDownState = holdDownState,
        enabled = enabled,
    )
}

@Composable
private fun RowScope.StatusSwitchingWidget(
    status: String?,
    switching: Boolean,
    statusIcon: (@Composable () -> Unit)?,
    statusColor: Color,
    indicatorColor: Color,
    indicatorSize: Dp,
) {
    Row(
        // BasicComponent already keeps BasicComponentDefaults.EndActionsSpacing to the title.
        modifier = Modifier
            .padding(
                start = (StatusSwitchingPreferenceDefaults.WidgetMarginStart - BasicComponentDefaults.EndActionsSpacing)
                    .coerceAtLeast(0.dp),
            )
            .weight(1f, fill = false),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            switching -> {
                val ringRatio = ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorRingDiameter /
                    ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorSize
                val strokeRatio = ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorStrokeWidth /
                    ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorSize
                RotatingProgressIndicator(
                    color = indicatorColor,
                    size = indicatorSize,
                    ringDiameter = indicatorSize * ringRatio,
                    strokeWidth = indicatorSize * strokeRatio,
                )
            }

            statusIcon != null -> Box(contentAlignment = Alignment.Center) { statusIcon() }

            status != null -> Text(
                text = status,
                style = COUITheme.textStyles.bodyM,
                color = statusColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Contains default values used by [StatusSwitchingPreference]. */
object StatusSwitchingPreferenceDefaults {

    /**
     * The gap between the title and the status widget
     * (COUI coui_status_switching_preference_widget_margin_start).
     */
    val WidgetMarginStart = 16.dp

    /** The default status text color (COUI ?couiColorLabelSecondary). */
    @Composable
    fun statusColor(): Color = COUITheme.colorScheme.labelSecondary
}
