// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.roundToInt

/**
 * The size class of an [EmptyState] (COUIEmptyStateView.emptyViewSizeType), which scales the
 * illustration.
 */
enum class EmptyStateSizeType {
    /** Picked from the available size: [Small] below 200dp of height, [Medium] below 320dp of width or 480dp of height, else [Large]. */
    Auto,

    /** No illustration, the text group sits at half the free height. */
    Small,

    /** The illustration at full size (COUI 17 ANIM_SIZE_SCALE_VALUE_NORMAL = 1.0). */
    Medium,

    /** The illustration at full size. */
    Large,
}

/**
 * An [EmptyState] component with COUI style, mirroring ColorOS's COUIEmptyStateView
 * (coui_component_empty_state.xml): an illustration slot, a title, a subtitle and a borderless
 * action text button, stacked and centered.
 *
 * As in COUIEmptyStateView.onMeasure / onLayout, the group is centered horizontally and, when the
 * height is bounded, placed 45% of the way down the free height (50% for [EmptyStateSizeType.Small]).
 * The illustration is hidden for Small and shown at full size otherwise
 * (COUIEmptyStateView.getAnimTargetSize: `sizeType != SMALL ? 1.0 : 0.0` in COUI 17).
 * The title is capped at two lines and the subtitle at three; both are 280dp wide and scroll
 * once they exceed 92dp together (COUIMaxHeightScrollView). Text that fits neither scrolls nor
 * rubber-bands, so a drag on it moves the page behind instead.
 *
 * COUI fills the illustration with a Lottie animation or an image resource; here [illustration]
 * is a slot drawn inside a box of the resolved illustration size.
 *
 * @param title The title (COUI couiTextHeadlineXS 16sp medium, couiColorLabelPrimary). Hidden when empty.
 * @param modifier The modifier to be applied to the [EmptyState].
 * @param subtitle The subtitle (COUI couiTextBodyXS 12sp, couiColorLabelSecondary). Hidden when null or empty.
 * @param actionText The label of the action text button (COUI couiTextButtonM 14sp medium, primary color).
 *   Hidden when null or empty.
 * @param onActionClick The callback invoked when the action text button is clicked.
 * @param sizeType The [EmptyStateSizeType] that scales the illustration.
 * @param illustrationWidth The full width of the illustration (COUI coui_component_empty_anim_view_width_normal).
 * @param illustrationHeight The full height of the illustration (COUI coui_component_empty_anim_view_height_normal).
 * @param colors The [EmptyStateColors] of the [EmptyState].
 * @param titleStyle The [TextStyle] of the title.
 * @param subtitleStyle The [TextStyle] of the subtitle.
 * @param actionStyle The [TextStyle] of the action text button.
 * @param illustration The illustration, drawn inside a box of the resolved illustration size.
 *   Not composed for [EmptyStateSizeType.Small].
 */
@Composable
fun EmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    sizeType: EmptyStateSizeType = EmptyStateSizeType.Auto,
    illustrationWidth: Dp = EmptyStateDefaults.IllustrationWidth,
    illustrationHeight: Dp = EmptyStateDefaults.IllustrationHeight,
    colors: EmptyStateColors = EmptyStateDefaults.emptyStateColors(),
    titleStyle: TextStyle = COUITheme.textStyles.headlineXS,
    subtitleStyle: TextStyle = COUITheme.textStyles.bodyXS,
    actionStyle: TextStyle = COUITheme.textStyles.buttonM,
    illustration: (@Composable () -> Unit)? = null,
) {
    BoxWithConstraints(modifier = modifier) {
        val resolved = EmptyStateDefaults.resolveSizeType(
            sizeType = sizeType,
            width = if (constraints.hasBoundedWidth) maxWidth else Dp.Infinity,
            height = if (constraints.hasBoundedHeight) maxHeight else Dp.Infinity,
        )
        val scale = if (resolved == EmptyStateSizeType.Small) 0f else 1f
        val offsetRatio = if (resolved == EmptyStateSizeType.Small) 0.5f else 0.45f
        Layout(
            content = {
                EmptyStateGroup(
                    title = title,
                    subtitle = subtitle,
                    actionText = actionText,
                    onActionClick = onActionClick,
                    illustrationWidth = illustrationWidth * scale,
                    illustrationHeight = illustrationHeight * scale,
                    colors = colors,
                    titleStyle = titleStyle,
                    subtitleStyle = subtitleStyle,
                    actionStyle = actionStyle,
                    illustration = if (scale > 0f) illustration else null,
                )
            },
        ) { measurables, constraints ->
            val group = measurables.first().measure(constraints.copy(minWidth = 0, minHeight = 0))
            val width = if (constraints.hasBoundedWidth) constraints.maxWidth else constraints.constrainWidth(group.width)
            val height = if (constraints.hasBoundedHeight) constraints.maxHeight else constraints.constrainHeight(group.height)
            layout(width, height) {
                val top = ((height - group.height) * offsetRatio).roundToInt().coerceAtLeast(0)
                group.place((width - group.width) / 2, top)
            }
        }
    }
}

/** The centered column of coui_component_empty_state.xml. */
@Composable
private fun EmptyStateGroup(
    title: String,
    subtitle: String?,
    actionText: String?,
    onActionClick: (() -> Unit)?,
    illustrationWidth: Dp,
    illustrationHeight: Dp,
    colors: EmptyStateColors,
    titleStyle: TextStyle,
    subtitleStyle: TextStyle,
    actionStyle: TextStyle,
    illustration: (@Composable () -> Unit)?,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (illustration != null) {
            Box(
                modifier = Modifier.size(illustrationWidth, illustrationHeight),
                contentAlignment = Alignment.Center,
            ) {
                illustration()
            }
        }
        val hasSubtitle = !subtitle.isNullOrEmpty()
        if (title.isNotEmpty() || hasSubtitle) {
            val textScrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .heightIn(max = EmptyStateDefaults.TextMaxHeight)
                    // COUIMaxHeightScrollView (a COUIScrollView, overScrollMode ifContentScrolls)
                    // clamps the overscroll of text that fits: only an overflowing text block
                    // scrolls and rubber-bands; otherwise drags go to the page behind it.
                    .verticalScroll(textScrollState, enabled = textScrollState.maxValue > 0),
            ) {
                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        modifier = Modifier.width(EmptyStateDefaults.TextWidth),
                        color = colors.titleColor,
                        style = titleStyle,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (!subtitle.isNullOrEmpty()) {
                    Text(
                        text = subtitle,
                        modifier = Modifier
                            .padding(top = EmptyStateDefaults.SubtitleMarginTop)
                            .width(EmptyStateDefaults.TextWidth),
                        color = colors.subtitleColor,
                        style = subtitleStyle,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        if (!actionText.isNullOrEmpty()) {
            // COUITextViewCompatUtil.setPressRippleDrawable: wrap-content label with a capsule
            // press ripple padded 12dp / 4dp (text_ripple_bg_padding_horizontal / _vertical).
            Text(
                text = actionText,
                modifier = Modifier
                    .padding(top = EmptyStateDefaults.ActionMarginTop)
                    .clip(CircleShape)
                    .clickable(
                        enabled = onActionClick != null,
                        role = Role.Button,
                        onClick = { onActionClick?.invoke() },
                    )
                    .padding(EmptyStateDefaults.ActionPadding),
                color = colors.actionColor,
                style = actionStyle,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Contains default values used by [EmptyState]. */
object EmptyStateDefaults {

    /** Full illustration width (COUI 17 coui_component_empty_anim_view_width_normal). */
    val IllustrationWidth = 120.dp

    /** Full illustration height (COUI 17 coui_component_empty_anim_view_height_normal). */
    val IllustrationHeight = 100.dp

    /** Width of the title and subtitle (COUI coui_component_empty_group_width). */
    val TextWidth = 280.dp

    /** Maximum height of the scrolling text block (COUI coui_component_empty_text_scroll_max_height). */
    val TextMaxHeight = 92.dp

    /** Top margin of the subtitle (COUI coui_component_empty_view_subtitle_margin_top). */
    val SubtitleMarginTop = 2.dp

    /** Top margin of the action button (coui_component_empty_state.xml layout_marginTop). */
    val ActionMarginTop = 8.dp

    /** Press area padding of the action button (COUI text_ripple_bg_padding_horizontal / _vertical). */
    val ActionPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)

    /** Below this height the size type resolves to Small (COUI coui_component_height_threshold_small). */
    val HeightThresholdSmall = 200.dp

    /** Below this width the size type resolves to Medium (COUI coui_component_width_threshold_medium). */
    val WidthThresholdMedium = 320.dp

    /** Below this height the size type resolves to Medium (COUI coui_component_height_threshold_medium). */
    val HeightThresholdMedium = 480.dp

    /**
     * Resolves [EmptyStateSizeType.Auto] against the available size (COUIEmptyStateView.e):
     * other size types are returned unchanged.
     */
    fun resolveSizeType(sizeType: EmptyStateSizeType, width: Dp, height: Dp): EmptyStateSizeType = when {
        sizeType != EmptyStateSizeType.Auto -> sizeType
        height < HeightThresholdSmall -> EmptyStateSizeType.Small
        width < WidthThresholdMedium || height < HeightThresholdMedium -> EmptyStateSizeType.Medium
        else -> EmptyStateSizeType.Large
    }

    /**
     * The default [EmptyStateColors]: title in couiColorLabelPrimary, subtitle in
     * couiColorLabelSecondary and the action in the theme primary color.
     */
    @Composable
    fun emptyStateColors(
        titleColor: Color = COUITheme.colorScheme.labelPrimary,
        subtitleColor: Color = COUITheme.colorScheme.labelSecondary,
        actionColor: Color = COUITheme.colorScheme.primary,
    ): EmptyStateColors = remember(titleColor, subtitleColor, actionColor) {
        EmptyStateColors(
            titleColor = titleColor,
            subtitleColor = subtitleColor,
            actionColor = actionColor,
        )
    }
}

/**
 * Colors used by an [EmptyState].
 *
 * @param titleColor The color of the title.
 * @param subtitleColor The color of the subtitle.
 * @param actionColor The color of the action text button.
 */
@Immutable
data class EmptyStateColors(
    val titleColor: Color,
    val subtitleColor: Color,
    val actionColor: Color,
)
