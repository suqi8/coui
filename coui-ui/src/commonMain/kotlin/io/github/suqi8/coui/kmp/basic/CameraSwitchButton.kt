// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.MaterialBlurBlendMode
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * The round camera control button, the port of `SwitchCameraButton` (a `ShutterButton`, which is
 * a `RotatePressImageView`). The camera uses the same button with other icons for pausing and
 * resuming a recording (`VideoPauseResumeButton`) and for taking a photo while recording
 * (`RecordingCaptureButton`); [CameraSwitchButtonDefaults] has those icons.
 *
 * A [size] glass circle holds a 24dp white [icon]. Pressing fades the button to 50% in 200ms and
 * back in 340ms, both on (0.33, 0, 0.67, 1); a disabled button is drawn at 40%. Changing [icon]
 * shrinks the old icon to 80% while fading it out in 200ms, then grows and fades in the new one in
 * 200ms, both on (0.3, 0, 0.1, 1). Changing [iconRotation] (the device orientation) turns the icon
 * the short way round at 270 degrees per second.
 *
 * The circle is the camera material blur when [materialBlur] is given (80dp blur, glow overlay of
 * a 40% light grey over a 60% mix of 15% grey) and otherwise a 50% black circle. The button always
 * uses its fixed light-on-dark palette, since it sits over the camera preview.
 *
 * @param onClick Called when the button is tapped.
 * @param modifier The modifier to be applied to the button.
 * @param enabled Whether the button accepts input.
 * @param icon The icon, [CameraSwitchButtonDefaults.SwitchIcon] by default.
 * @param contentDescription The accessibility label of the button.
 * @param iconRotation The rotation of the icon in degrees, following the device orientation.
 * @param size The diameter of the button: [CameraSwitchButtonDefaults.Size] for the camera switch,
 *   [CameraSwitchButtonDefaults.ControlSize] for the pause / resume and capture buttons.
 * @param iconSize The size the icon is drawn at.
 * @param materialBlur Paints the camera material blur behind the circle. Pass a provider recording
 *   the content under the button; null keeps the 50% black fill.
 * @param colors The colors of the button, see [CameraSwitchButtonDefaults.cameraSwitchButtonColors].
 */
@Composable
fun CameraSwitchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector = CameraSwitchButtonDefaults.SwitchIcon,
    contentDescription: String? = null,
    iconRotation: Float = 0f,
    size: Dp = CameraSwitchButtonDefaults.Size,
    iconSize: Dp = CameraSwitchButtonDefaults.IconSize,
    materialBlur: MaterialBlurProvider? = null,
    colors: CameraSwitchButtonColors = CameraSwitchButtonDefaults.cameraSwitchButtonColors(),
) {
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // `COUIPressFeedbackHelper` animation type 0: alpha to 0.5 in 200ms, back in 340ms.
    val pressAlpha = remember { Animatable(1f) }
    LaunchedEffect(pressed) {
        if (pressed) {
            pressAlpha.animateTo(CameraSwitchButtonDefaults.PressedAlpha, tween(200, easing = SwitchButtonPressEasing))
        } else {
            pressAlpha.animateTo(1f, tween(340, easing = SwitchButtonPressEasing))
        }
    }

    // `RotateImageView.b`: the shortest turn to the new orientation at 270 degrees per second.
    val rotation = remember { Animatable(iconRotation) }
    LaunchedEffect(iconRotation) {
        val current = rotation.value
        var delta = (iconRotation - current) % 360f
        if (delta < 0f) delta += 360f
        if (delta > 180f) delta -= 360f
        if (delta == 0f) return@LaunchedEffect
        val duration = (abs(delta) * 1000f / ROTATION_DEGREES_PER_SECOND).roundToInt()
        rotation.animateTo(current + delta, tween(duration, easing = LinearEasing))
    }

    // `ShutterButton.setResIdWithAnimation`: out in 200ms, then the new icon in 200ms.
    var shownIcon by remember { mutableStateOf(icon) }
    val iconFraction = remember { Animatable(1f) }
    LaunchedEffect(icon) {
        if (icon != shownIcon) {
            iconFraction.animateTo(0f, tween(200, easing = SwitchButtonMoveEasing))
            shownIcon = icon
        }
        iconFraction.animateTo(1f, tween(200, easing = SwitchButtonMoveEasing))
    }

    val blurSpec = remember(density, colors) {
        MaterialBlurSpec(
            blurRadiusPx = with(density) { CameraSwitchButtonDefaults.BlurRadius.toPx() },
            mixColor = colors.blurMixColor,
            blendColor = colors.blurBlendColor,
            blendMode = MaterialBlurBlendMode.GlowOverlay,
        )
    }
    val background = materialBlur?.materialBlur(blurSpec, CircleShape)
        ?: Modifier.background(colors.containerColor, CircleShape)

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                alpha = pressAlpha.value * if (enabled) 1f else CameraSwitchButtonDefaults.DisabledAlpha
            }
            .then(background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = shownIcon,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    val v = iconFraction.value
                    alpha = v
                    val s = ICON_START_SCALE + (1f - ICON_START_SCALE) * v
                    scaleX = s
                    scaleY = s
                    rotationZ = rotation.value
                },
            tint = colors.iconColor,
        )
    }
}

/** `RotateImageView` turns 270 degrees per second. */
private const val ROTATION_DEGREES_PER_SECOND = 270f

/** The scale an icon shrinks to and grows from when it changes. */
private const val ICON_START_SCALE = 0.8f

/** `COUIEaseInterpolator` (0.33, 0, 0.67, 1). */
private val SwitchButtonPressEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** `COUIMoveEaseInterpolator` (0.3, 0, 0.1, 1). */
private val SwitchButtonMoveEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/**
 * Default values of [CameraSwitchButton], from the camera's control button dimensions and icons.
 */
object CameraSwitchButtonDefaults {
    /** The camera switch button size (`switch_camera_button_width`). */
    val Size = 40.dp

    /**
     * The size of the pause / resume and capture-while-recording buttons, the oval of their
     * `switch_camera_button_bg`.
     */
    val ControlSize = 48.dp

    /** The icon size (`btn_switch_camera`, `btn_video_pause`). */
    val IconSize = 24.dp

    /** The size the capture-while-recording glyph is drawn at (`btn_video_recording`). */
    val CaptureIconSize = 56.dp

    /** The material blur radius (`button_blur_radius`). */
    val BlurRadius = 80.dp

    /** The alpha while pressed (`COUIPressFeedbackHelper`, animation type 0). */
    val PressedAlpha = 0.5f

    /** The alpha of a disabled button (`TwoStateImageView.setEnabled`). */
    val DisabledAlpha = 0.4f

    /** The camera switch arrows (`btn_switch_camera`, 1.2dp strokes). */
    val SwitchIcon: ImageVector by lazy {
        strokeIcon(
            "CameraSwitch",
            "M22.943,11.668L20.29,14.321L17.638,11.668" to StrokeCap.Square,
            "M1.059,12.332L3.711,9.679L6.364,12.332" to StrokeCap.Square,
            "M6.033,6.031C9.329,2.735 14.674,2.735 17.97,6.031C19.954,8.015 20.744,10.741 20.34,13.316" to StrokeCap.Square,
            "M17.969,17.969C14.673,21.265 9.329,21.265 6.032,17.969C4.048,15.985 3.259,13.259 3.663,10.684" to StrokeCap.Square,
        )
    }

    /** The pause bars of a running recording (`btn_video_pause`). */
    val PauseIcon: ImageVector by lazy {
        strokeIcon(
            "CameraPause",
            "M8.5,5.875V18.125" to StrokeCap.Square,
            "M15.5,5.875V18.125" to StrokeCap.Square,
        )
    }

    /** The resume triangle of a paused recording (`btn_video_record_resume`). */
    val ResumeIcon: ImageVector by lazy {
        strokeIcon(
            "CameraResume",
            "M8.158,18.72L18.679,12L8.158,5.28V18.72Z" to StrokeCap.Round,
        )
    }

    /**
     * The small shutter of the capture-while-recording button (`btn_video_recording`, drawn at
     * [CaptureIconSize]): a ring and a disc.
     */
    val CaptureIcon: ImageVector by lazy {
        ImageVector.Builder("CameraCapture", CaptureIconSize, CaptureIconSize, 56f, 56f).apply {
            addPath(
                pathData = addPathNodes(
                    "M28,4C41.255,4 52,14.745 52,28C52,41.255 41.255,52 28,52C14.745,52 4,41.255 4,28" +
                        "C4,14.745 14.745,4 28,4ZM28,6.909C16.352,6.909 6.909,16.352 6.909,28" +
                        "C6.909,39.648 16.352,49.091 28,49.091C39.648,49.091 49.091,39.648 49.091,28" +
                        "C49.091,16.352 39.648,6.909 28,6.909ZM28,8.5C38.77,8.5 47.5,17.23 47.5,28" +
                        "C47.5,38.77 38.77,47.5 28,47.5C17.23,47.5 8.5,38.77 8.5,28C8.5,17.23 17.23,8.5 28,8.5Z",
                ),
                fill = SolidColor(Color.White),
            )
        }.build()
    }

    /**
     * The fixed camera palette: the non-blur `color_black_with_50_percent_transparency` circle,
     * the material blur colors of `BlurBackgroundImpl` (dark material, progress 1) and a white
     * icon.
     */
    @Composable
    fun cameraSwitchButtonColors(
        containerColor: Color = Color(0x7F000000),
        blurMixColor: Color = Color(red = 0.15f, green = 0.15f, blue = 0.15f, alpha = 0.6f),
        blurBlendColor: Color = Color(red = 0.65f, green = 0.65f, blue = 0.65f, alpha = 0.4f),
        iconColor: Color = Color.White,
    ): CameraSwitchButtonColors = remember(containerColor, blurMixColor, blurBlendColor, iconColor) {
        CameraSwitchButtonColors(
            containerColor = containerColor,
            blurMixColor = blurMixColor,
            blurBlendColor = blurBlendColor,
            iconColor = iconColor,
        )
    }
}

/** A 24dp icon of white 1.2dp strokes with round joins, like the camera control icons. */
private fun strokeIcon(name: String, vararg paths: Pair<String, StrokeCap>): ImageVector = ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
    paths.forEach { (data, cap) ->
        addPath(
            pathData = addPathNodes(data),
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.2f,
            strokeLineCap = cap,
            strokeLineJoin = StrokeJoin.Round,
        )
    }
}.build()

/**
 * The colors of a [CameraSwitchButton].
 *
 * @param containerColor The circle without material blur.
 * @param blurMixColor The material blur mix color.
 * @param blurBlendColor The material blur glow-overlay color.
 * @param iconColor The icon tint.
 */
@Immutable
data class CameraSwitchButtonColors(
    val containerColor: Color,
    val blurMixColor: Color,
    val blurBlendColor: Color,
    val iconColor: Color,
)
