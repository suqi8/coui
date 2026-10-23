// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import io.github.suqi8.coui.kmp.squircle.LegacySquircleShape
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.PI
import kotlin.math.cos

/**
 * A [Badge] component with COUI style.
 *
 * A hint red dot mirroring COUIHintRedDot with its three forms:
 * - [count] <= 0: a plain 6dp dot (POINT_ONLY_MODE).
 * - [count] in 1..999: a 16dp-high capsule showing the number, widening with the digit count
 *   (16dp under 10, 20dp under 100, 26dp under 1000).
 * - [count] >= 1000: a 20dp-wide capsule showing a three-dot ellipsis (COUI red_dot_more).
 *
 * Count changes animate like the original: the capsule width eases over 517ms
 * (COUIMoveEaseInterpolator) while the old and new numbers crossfade over 150ms on the framework
 * default accelerate-decelerate curve.
 *
 * Use [BadgeBox] to anchor a badge at the top end corner of an icon.
 *
 * @param modifier The modifier to be applied to the [Badge].
 * @param count The number shown in the [Badge]; values <= 0 show a plain dot.
 * @param stroke Whether to draw a white outline around the [Badge]
 *   (POINT_ONLY_MODE_STROKE / POINT_NUM_MODE_STROKE), for badges on colored surfaces.
 * @param colors The [BadgeColors] of the [Badge].
 * @param dotDiameter The diameter of the plain dot form.
 * @param height The height of the number capsule form.
 * @param navigation Whether the number capsule takes the bottom-navigation widths
 *   (`COUIHintRedDot` `POINT_NAVI_WITH_NUM`, `getNaviBgWidth`): 12dp under 10, 16dp under 100 and
 *   20dp otherwise, not widened to [height], instead of 16 / 20 / 26dp.
 */
@Composable
fun Badge(
    modifier: Modifier = Modifier,
    count: Int = 0,
    stroke: Boolean = false,
    colors: BadgeColors = BadgeDefaults.badgeColors(),
    dotDiameter: Dp = BadgeDefaults.DotDiameter,
    height: Dp = BadgeDefaults.Height,
    navigation: Boolean = false,
) {
    if (count <= 0) {
        DotBadge(stroke = stroke, colors = colors, dotDiameter = dotDiameter, modifier = modifier)
    } else {
        CountBadge(count = count, stroke = stroke, colors = colors, height = height, navigation = navigation, modifier = modifier)
    }
}

/**
 * A layout that anchors [badge] at the top end corner of [content], mirroring
 * COUIRedDotFrameLayout.
 *
 * A positive [overhang] lets the badge stick out of the content's top end corner by that amount
 * and grows the layout accordingly (COUI rectangular anchors); a negative [overhang] insets the
 * badge inside the corner instead (COUI circular anchors such as avatars).
 *
 * @param badge The badge to anchor, typically a [Badge].
 * @param modifier The modifier to be applied to the layout.
 * @param overhang How far the badge extends beyond the content's top end corner.
 * @param content The anchor content, typically an icon.
 */
@Composable
fun BadgeBox(
    badge: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overhang: Dp = BadgeDefaults.DotOverhang,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = {
            Box { content() }
            Box { badge() }
        },
    ) { measurables, constraints ->
        val overhangPx = overhang.roundToPx()
        val outsetPx = maxOf(overhangPx, 0)
        val insetPx = maxOf(-overhangPx, 0)
        val contentPlaceable = measurables[0].measure(constraints.offset(-outsetPx, -outsetPx))
        val badgePlaceable = measurables[1].measure(constraints.copy(minWidth = 0, minHeight = 0))
        val width = constraints.constrainWidth(contentPlaceable.width + outsetPx)
        val height = constraints.constrainHeight(contentPlaceable.height + outsetPx)
        layout(width, height) {
            contentPlaceable.placeRelative(0, outsetPx)
            badgePlaceable.placeRelative(width - badgePlaceable.width - insetPx, insetPx)
        }
    }
}

/** The plain dot form (COUIHintRedDotHelper.drawPointOnly / drawPointStroke). */
@Composable
private fun DotBadge(
    stroke: Boolean,
    colors: BadgeColors,
    dotDiameter: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(dotDiameter)
            .drawBehind {
                val radius = size.minDimension / 2f
                if (stroke) {
                    // The white ring eats into the dot: red radius = size / 2 - stroke width.
                    drawCircle(color = colors.strokeColor, radius = radius)
                    drawCircle(color = colors.containerColor, radius = radius - BadgeDefaults.StrokeWidth.toPx())
                } else {
                    drawCircle(color = colors.containerColor, radius = radius)
                }
            },
    )
}

/** The number capsule form (COUIHintRedDotHelper.drawPointWithNumber / drawPointWithStroke). */
@Composable
private fun CountBadge(
    count: Int,
    stroke: Boolean,
    colors: BadgeColors,
    height: Dp,
    navigation: Boolean,
    modifier: Modifier = Modifier,
) {
    val width by animateDpAsState(
        targetValue = if (navigation) naviBadgeWidth(count) else badgeWidth(count, height),
        animationSpec = tween(durationMillis = WidthAnimDurationMillis, easing = WidthAnimEasing),
        label = "badgeWidth",
    )
    // drawPointWithNumber clamps couiRoundCornerFULL (72dp) to min(width, height) / 2 and draws the
    // capsule with the COUIRoundRectUtil Java path (COUIShapePath), not the Oplus superellipse.
    // drawPointWithStroke passes the unclamped 72dp to the outer path, which folds over itself; the
    // outer ring uses the clamped radius here, the inner fill the same min / 2 rule as COUI.
    // A navigation capsule can be narrower than it is tall (12dp x 16dp): the radius clamps to
    // min(width, height) / 2 like drawPointWithNumber.
    val cornerRadius = minOf(width, height) / 2
    val backgroundModifier = if (stroke) {
        Modifier
            .background(color = colors.strokeColor, shape = LegacySquircleShape(cornerRadius))
            .padding(BadgeDefaults.StrokeWidth)
            .background(color = colors.containerColor, shape = LegacySquircleShape(cornerRadius - BadgeDefaults.StrokeWidth))
    } else {
        Modifier.background(color = colors.containerColor, shape = LegacySquircleShape(cornerRadius))
    }
    val density = LocalDensity.current
    val fontSize = with(density) { BadgeDefaults.TextSize.toSp() }
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .then(backgroundModifier),
        contentAlignment = Alignment.Center,
    ) {
        Crossfade(
            targetState = count,
            modifier = Modifier.matchParentSize(),
            animationSpec = tween(durationMillis = CountFadeDurationMillis, easing = CountFadeEasing),
            label = "badgeCount",
        ) { targetCount ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (targetCount < OverflowCount) {
                    Text(
                        text = targetCount.toString(),
                        color = colors.contentColor,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                    )
                } else {
                    BadgeEllipsis(colors.contentColor)
                }
            }
        }
    }
}

/** The three-dot ellipsis drawn for counts of 1000 and above (COUI red_dot_more). */
@Composable
private fun BadgeEllipsis(color: Color) {
    val diameter = BadgeDefaults.EllipsisDotDiameter
    val spacing = BadgeDefaults.EllipsisSpacing
    Canvas(modifier = Modifier.size(width = diameter * 3 + spacing * 2, height = diameter)) {
        val r = size.height / 2f
        val step = diameter.toPx() + spacing.toPx()
        val cy = size.height / 2f
        drawCircle(color = color, radius = r, center = Offset(r, cy))
        drawCircle(color = color, radius = r, center = Offset(r + step, cy))
        drawCircle(color = color, radius = r, center = Offset(r + 2 * step, cy))
    }
}

/**
 * The navigation capsule width for [count] (COUIHintRedDotHelper.getNaviBgWidth(int)):
 * coui_hint_red_dot_navi_small_width under 10, coui_small_width under 100, coui_medium_width otherwise.
 */
private fun naviBadgeWidth(count: Int): Dp = when {
    count < 10 -> BadgeDefaults.NaviSmallWidth
    count < 100 -> BadgeDefaults.SmallWidth
    else -> BadgeDefaults.MediumWidth
}

/**
 * The capsule width for [count] (COUIHintRedDotHelper.getBgWidth): small under 10, medium under
 * 100, large under 1000, and back to medium for the 1000+ ellipsis, never narrower than [height].
 */
private fun badgeWidth(count: Int, height: Dp): Dp {
    val base = when {
        count < 10 -> BadgeDefaults.SmallWidth
        count < 100 -> BadgeDefaults.MediumWidth
        count < OverflowCount -> BadgeDefaults.LargeWidth
        else -> BadgeDefaults.MediumWidth
    }
    return if (base > height) base else height
}

/** Contains default values used by [Badge] and [BadgeBox]. */
object BadgeDefaults {

    /** The diameter of the plain dot form (COUI coui_dot_diameter). */
    val DotDiameter = 6.dp

    /** The height of the number capsule form (COUI coui_height). */
    val Height = 16.dp

    /** The navigation capsule width for counts under 10 (COUI coui_hint_red_dot_navi_small_width). */
    val NaviSmallWidth = 12.dp

    /** The capsule width for counts under 10 (COUI coui_small_width). */
    val SmallWidth = 16.dp

    /** The capsule width for counts under 100 and the 1000+ ellipsis (COUI coui_medium_width). */
    val MediumWidth = 20.dp

    /** The capsule width for counts under 1000 (COUI coui_large_width). */
    val LargeWidth = 26.dp

    /**
     * The size of the count text (COUI coui_hint_text_size). Defined in dp like the original,
     * so the badge ignores the user font scale; converted with the current density.
     */
    val TextSize = 10.dp

    /** The width of the white outline in the stroke forms (COUI coui_dot_stroke_width). */
    val StrokeWidth = 1.dp

    /** The diameter of one ellipsis dot (COUI couiEllipsisDiameter). */
    val EllipsisDotDiameter = 2.dp

    /** The gap between two ellipsis dots (COUI coui_hint_red_dot_ellipsis_spacing). */
    val EllipsisSpacing = 2.dp

    /**
     * The default [BadgeBox] overhang for a plain dot on a rectangular icon
     * (COUI coui_hint_red_dot_medium_icon_topend_margin_rectangle).
     */
    val DotOverhang = 2.dp

    /**
     * The [BadgeBox] overhang for a count badge on a rectangular icon
     * (COUI coui_hint_red_dot_medium_number_topend_margin_rectangle).
     */
    val CountOverhang = 3.dp

    /**
     * The duration of the COUI badge show / hide scale animation
     * (COUIHintRedDot RED_POINT_ANIM_DURATION), for hosts animating badge visibility.
     */
    val ScaleAnimDurationMillis = 520

    /**
     * The default [BadgeColors]. COUIHintRedDot fills with couiColorAdditionalRed - a dedicated
     * badge red distinct from the error color - with white text and stroke.
     */
    @Composable
    fun badgeColors(
        containerColor: Color = if (COUITheme.colorScheme.background.luminance() < 0.5f) BadgeRedDark else BadgeRedLight,
        contentColor: Color = Color.White,
        strokeColor: Color = Color.White,
    ): BadgeColors = remember(containerColor, contentColor, strokeColor) {
        BadgeColors(
            containerColor = containerColor,
            contentColor = contentColor,
            strokeColor = strokeColor,
        )
    }
}

@Immutable
data class BadgeColors(
    val containerColor: Color,
    val contentColor: Color,
    val strokeColor: Color,
)

/** couiColorAdditionalRed in light themes (coui_color_additional_red #EB3B2F). */
private val BadgeRedLight = Color(0xFFEB3B2F)

/** couiColorAdditionalRed in dark themes (coui_color_additional_red_dark #EB493D). */
private val BadgeRedDark = Color(0xFFEB493D)

/** Counts at or above this render the ellipsis instead of digits (COUIHintRedDotHelper). */
private val OverflowCount = 1000

/** COUIHintRedDot NUM_CHANGE_WIDTH_ANIM_DURATION. */
private val WidthAnimDurationMillis = 517

/** COUIHintRedDot NUM_CHANGE_ALPHA_ANIM_DURATION. */
private val CountFadeDurationMillis = 150

/**
 * The count cross-fade easing. COUIHintRedDot.executeAlphaAnim sets no interpolator on its
 * `ValueAnimator`, so it runs on the framework default AccelerateDecelerateInterpolator:
 * `cos((t + 1) * PI) / 2 + 0.5`.
 */
internal val CountFadeEasing = Easing { fraction -> (cos((fraction + 1) * PI) / 2.0 + 0.5).toFloat() }

/** COUIMoveEaseInterpolator, shared by the width and show / hide animations. */
private val WidthAnimEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
