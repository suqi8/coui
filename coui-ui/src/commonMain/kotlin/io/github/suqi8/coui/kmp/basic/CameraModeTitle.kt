// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.MaterialBlurBlendMode
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec

/**
 * The title the camera shows in place of the [CameraModeBar] while a mode picked from "More" is
 * open (slow motion, time-lapse, ...), the port of `ModeTitleView` (`ModeTitleLayout` from
 * `mode_title.xml`) over the `HeadlineBackground` capsule.
 *
 * A [CameraModeTitleDefaults.CapsuleHeight] capsule holds the mode name in the camera accent,
 * [CameraModeTitleDefaults.TextStartPadding] from its start, and a close cross
 * [CameraModeTitleDefaults.IconSpacing] after it, [CameraModeTitleDefaults.IconEndPadding] from its
 * end. Names wider than [CameraModeTitleDefaults.TextMaxWidth] scroll as a marquee with faded edges.
 * Tapping anywhere on the title calls [onClick], which in the camera leaves the mode for the one
 * it came from. While pressed the whole title dims to half (200ms) and recovers in 340ms; when the
 * name changes the capsule resizes in 250ms.
 *
 * The capsule is the camera's material blur with its edge light when [materialBlur] is given, and
 * otherwise a 50% black fill.
 *
 * @param onClick Called when the title is tapped.
 * @param title The name of the open mode.
 * @param modifier The modifier to be applied to the title.
 * @param enabled Whether the title accepts taps.
 * @param materialBlur Paints the camera material blur behind the capsule. Pass a provider recording
 *   the content under the title; null keeps the plain fill.
 * @param colors The colors of the title, see [CameraModeTitleDefaults.cameraModeTitleColors].
 */
@Composable
fun CameraModeTitle(
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    materialBlur: MaterialBlurProvider? = null,
    colors: CameraModeTitleColors = CameraModeTitleDefaults.cameraModeTitleColors(),
) {
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    // ModeTitleView.o: 1 -> 0.5 in 200ms on (0.4, 0, 0.2, 1), back in 340ms on (0, 0, 0.2, 1).
    val pressAlpha by animateFloatAsState(
        targetValue = if (pressed) CameraModeTitleDefaults.PressedAlpha else 1f,
        animationSpec = if (pressed) {
            tween(durationMillis = 200, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f))
        } else {
            tween(durationMillis = 340, easing = CubicBezierEasing(0f, 0f, 0.2f, 1f))
        },
    )
    val textStyle = remember(density) {
        TextStyle(
            fontSize = cameraTextSize(CameraModeTitleDefaults.TextSize, density.fontScale),
            fontWeight = FontWeight.Medium,
        )
    }
    val blurSpec = remember(density, colors) {
        MaterialBlurSpec(
            blurRadiusPx = with(density) { CameraModeBarDefaults.BlurRadius.toPx() },
            mixColor = colors.blurMixColor,
            blendColor = colors.blurBlendColor,
            blendMode = MaterialBlurBlendMode.GlowOverlay,
        )
    }
    val blurModifier = materialBlur?.materialBlur(blurSpec, CircleShape)
    val capsuleBackground = if (blurModifier != null) {
        blurModifier.cameraInnerShadow(CameraInnerShadows.Panel, CameraModeTitleDefaults.CapsuleHeight / 2)
    } else {
        Modifier.background(colors.containerColor, CircleShape)
    }

    Box(
        modifier = modifier
            .height(CameraModeTitleDefaults.Height)
            .graphicsLayer { alpha = pressAlpha }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            // HeadlineBackground.B: the capsule follows a new name in 250ms on (0.3, 0, 0.1, 1),
            // the title clipped to it around the center.
            .animateContentSize(
                animationSpec = tween(durationMillis = 250, easing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(CameraModeTitleDefaults.CapsuleHeight)
                .then(capsuleBackground)
                .padding(
                    start = CameraModeTitleDefaults.TextStartPadding,
                    end = CameraModeTitleDefaults.IconEndPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MarqueeText(
                text = title,
                modifier = Modifier.widthIn(max = CameraModeTitleDefaults.TextMaxWidth),
                color = colors.textColor,
                style = textStyle,
                velocity = CameraModeTitleDefaults.MarqueeVelocity,
                initialDelayMillis = CameraModeTitleDefaults.MarqueeDelayMillis,
                spacing = CameraModeTitleDefaults.TextMaxWidth / 3,
                startOffset = 0.dp,
                fadingEdgeLength = CameraModeTitleDefaults.FadingEdgeLength,
            )
            Spacer(Modifier.width(CameraModeTitleDefaults.IconSpacing))
            Icon(
                imageVector = CameraModeCloseIcon,
                contentDescription = null,
                modifier = Modifier.size(CameraModeTitleDefaults.IconSize),
                tint = colors.iconColor,
            )
        }
    }
}

/**
 * The close cross of the mode title, ported 1:1 from the camera `close_mode_selector.xml` (16x16dp
 * viewport, filled and stroked at 1.33333 with the accent).
 */
private val CameraModeCloseIcon: ImageVector by lazy {
    ImageVector.Builder("CameraModeClose", 16.dp, 16.dp, 16f, 16f).apply {
        addPath(
            pathData = addPathNodes(
                "M7.4,8.457L7.871,7.986L7.4,7.515L4.58,4.695L4.818,4.456L7.638,7.277L8.109,7.748" +
                    "L8.58,7.277L11.29,4.567L11.528,4.805L8.819,7.515L8.347,7.986L8.819,8.457" +
                    "L11.417,11.056L11.179,11.294L8.58,8.695L8.109,8.224L7.638,8.695L4.928,11.405" +
                    "L4.69,11.167L7.4,8.457Z",
            ),
            fill = SolidColor(Color.Black),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.33333f,
        )
    }.build()
}

/**
 * Default values of [CameraModeTitle], from the camera's `mode_title_*`, `mode_name_tv_*` and
 * `mode_close_img_*` dimensions.
 */
object CameraModeTitleDefaults {
    /** The title height (`mode_title_height`). */
    val Height = 36.dp

    /** The capsule height (`head_line_selected_bg_height`, shared with the mode bar). */
    val CapsuleHeight = 34.dp

    /** The name size (`mode_title_scale_text_size` 14sp), following the font scale up to 1.35. */
    val TextSize = 14.dp

    /** The gap from the capsule start to the name (`mode_name_tv_margin_left`). */
    val TextStartPadding = 16.dp

    /** The widest name before it scrolls (`mode_name_tv_max_width`). */
    val TextMaxWidth = 198.dp

    /** The fade at both edges of a scrolling name (`mode_name_tv_fading_edge`). */
    val FadingEdgeLength = 16.dp

    /** The marquee speed per second (`TextView` marquee, 30dp/s). */
    val MarqueeVelocity = 30.dp

    /** The delay before the name starts scrolling (`TextView` marquee delay). */
    val MarqueeDelayMillis = 1200

    /** The gap between the name and the close cross (`mode_close_img_margin_left`). */
    val IconSpacing = 4.dp

    /** The gap between the close cross and the capsule end (`mode_close_img_margin_right`). */
    val IconEndPadding = 12.dp

    /** The close cross size (`close_mode_selector`). */
    val IconSize = 16.dp

    /** The alpha of the pressed title (`ModeTitleView.o`). */
    val PressedAlpha = 0.5f

    /**
     * The fixed camera palette: the name and the cross in the camera accent (`couiColorPrimary`,
     * `color_primary_light_hasselblad` #FB822A), the `HeadlineBackground` fill
     * `color_black_with_50_percent_transparency` and the material blur colors of
     * `BlurBackgroundImpl` (dark, progress 0).
     */
    @Composable
    fun cameraModeTitleColors(
        textColor: Color = Color(0xFFFB822A),
        iconColor: Color = Color(0xFFFB822A),
        containerColor: Color = Color(0x80000000),
        blurMixColor: Color = Color(red = 0.302f, green = 0.302f, blue = 0.302f, alpha = 0.6f),
        blurBlendColor: Color = Color(red = 0.15f, green = 0.15f, blue = 0.15f, alpha = 0.15f),
    ): CameraModeTitleColors = remember(textColor, iconColor, containerColor, blurMixColor, blurBlendColor) {
        CameraModeTitleColors(
            textColor = textColor,
            iconColor = iconColor,
            containerColor = containerColor,
            blurMixColor = blurMixColor,
            blurBlendColor = blurBlendColor,
        )
    }
}

/**
 * The colors of a [CameraModeTitle].
 *
 * @param textColor The mode name.
 * @param iconColor The close cross.
 * @param containerColor The capsule fill without material blur.
 * @param blurMixColor The material blur mix color.
 * @param blurBlendColor The material blur glow-overlay color.
 */
@Immutable
data class CameraModeTitleColors(
    val textColor: Color,
    val iconColor: Color,
    val containerColor: Color,
    val blurMixColor: Color,
    val blurBlendColor: Color,
)
