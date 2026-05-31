// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package top.yukonga.miuix.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.LocalColors
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.isDynamicColor
import top.yukonga.miuix.kmp.utils.CouiHapticEffect
import top.yukonga.miuix.kmp.utils.rememberCouiHaptic

/**
 * A [Switch] component with Miuix style.
 *
 * @param checked The checked state of the [Switch].
 * @param onCheckedChange The callback to be called when the state of the [Switch] changes.
 * @param modifier The modifier to be applied to the [Switch].
 * @param colors The [SwitchColors] of the [Switch].
 * @param enabled Whether the [Switch] is enabled.
 * @param isLoading Whether the [Switch] shows a loading spinner on the thumb (COUI loading style).
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.switchColors(),
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    // COUI fires the OPLUS linear-motor toggle waveform (302) on switch toggle; off-ColorOS this
    // falls back to the closest standard haptic.
    val couiHaptic = rememberCouiHaptic()

    val capsuleShape = CircleShape
    val thumbOffsetSpringSpec = remember { spring<Dp>(dampingRatio = 0.7f, stiffness = 987f) }
    // COUI toggle interpolator: PathInterpolatorCompat.create(0.3f, 0f, 0.1f, 1f).
    val couiToggleEasing = remember { CubicBezierEasing(0.3f, 0f, 0.1f, 1f) }

    // COUI switch is tap-driven: the thumb only animates between the off/on positions on toggle,
    // it never tracks the finger (COUISwitch.onTouchEvent has no ACTION_MOVE).
    val thumbOffsetState = animateDpAsState(
        targetValue = if (checked) SwitchDefaults.ThumbEndOffset else SwitchDefaults.ThumbStartOffset,
        animationSpec = thumbOffsetSpringSpec,
    )

    // COUI thumb squash: on toggle, circleScaleX briefly goes 1.0 -> 1.04 (133ms) then back to 1.0
    // (250ms after a 133ms delay). It is NOT a press effect (COUISwitch.b()).
    val thumbSquash = remember { Animatable(1f) }
    var isFirstComposition by remember { mutableStateOf(true) }
    LaunchedEffect(checked) {
        if (isFirstComposition) {
            isFirstComposition = false
            return@LaunchedEffect
        }
        thumbSquash.animateTo(1.04f, tween(durationMillis = 133, easing = couiToggleEasing))
        thumbSquash.animateTo(1f, tween(durationMillis = 250, easing = couiToggleEasing))
    }

    // COUI thumb has a two-layer structure: an outer circle plus an inner circle that is visible
    // when unchecked and fades out when checked (innerCircleAlpha 1->0, COUISwitch.b()).
    val innerCircleAlphaState = animateFloatAsState(
        targetValue = if (checked) 0f else 1f,
        animationSpec = tween(durationMillis = 100, easing = couiToggleEasing),
    )

    val thumbColorState = animateColorAsState(
        if (checked) colors.checkedThumbColor(enabled) else colors.uncheckedThumbColor(enabled),
    )

    val backgroundColorState = animateColorAsState(
        if (checked) colors.checkedTrackColor(enabled) else colors.uncheckedTrackColor(enabled),
        animationSpec = spring(dampingRatio = 0.99f, stiffness = 438.6f),
    )

    // COUI press/hover feedback is a translucent overlay on the track tint (coui_color_press 12% /
    // coui_color_hover 7.8%), composited over the bar color. The overlay tone follows the scheme
    // (black in light, white in dark), so derive it from onSurface (COUISwitch.f() + e()).
    val overlayTone = MiuixTheme.colorScheme.onSurface
    val pressOverlayState = animateColorAsState(
        when {
            !enabled -> Color.Transparent
            isPressed -> overlayTone.copy(alpha = 0.12f)
            isHovered -> overlayTone.copy(alpha = 0.078f)
            else -> Color.Transparent
        },
    )

    // The inner circle uses the unchecked track color so the unchecked thumb reads as a ring.
    val innerCircleColor = colors.uncheckedTrackColor(enabled)

    // COUI loading style: the thumb hosts a spinner rotating 0..360 over 800ms (COUISwitch q()).
    val loadingRotation = if (isLoading) {
        val transition = rememberInfiniteTransition(label = "switchLoading")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "switchLoadingRotation",
        )
    } else {
        null
    }

    val currentCouiHaptic by rememberUpdatedState(couiHaptic)
    // Loading switches cannot be toggled (COUISwitch loading style swallows touch).
    val interactive = enabled && !isLoading
    val hasCallback = onCheckedChange != null
    val toggleableModifier = if (hasCallback) {
        remember(checked, interactive, interactionSource) {
            Modifier.toggleable(
                value = checked,
                onValueChange = { v ->
                    currentOnCheckedChange?.invoke(v)
                    currentCouiHaptic(CouiHapticEffect.Switch)
                },
                enabled = interactive,
                role = Role.Switch,
                interactionSource = interactionSource,
            )
        }
    } else {
        Modifier.semantics {
            role = Role.Switch
            toggleableState = ToggleableState(checked)
            if (!enabled) disabled()
        }
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .size(SwitchDefaults.TrackWidth, SwitchDefaults.TrackHeight)
            .clip(capsuleShape)
            .drawBehind {
                drawRect(backgroundColorState.value)
                drawRect(pressOverlayState.value)
            }
            .hoverable(
                interactionSource = interactionSource,
                enabled = interactive,
            )
            .then(toggleableModifier),
    ) {
        Box(
            modifier = Modifier
                .size(SwitchDefaults.ThumbSize)
                .align(Alignment.CenterStart)
                .offset {
                    IntOffset(thumbOffsetState.value.roundToPx(), 0)
                }
                .graphicsLayer {
                    scaleX = thumbSquash.value
                }
                .drawBehind {
                    // Outer circle (the thumb body).
                    drawCircle(color = thumbColorState.value)
                    val rotation = loadingRotation?.value
                    if (rotation != null) {
                        // COUI loading style: a rotating arc on the thumb.
                        val stroke = size.minDimension * 0.1f
                        val inset = stroke * 1.5f
                        rotate(rotation) {
                            drawArc(
                                color = innerCircleColor,
                                startAngle = 0f,
                                sweepAngle = 270f,
                                useCenter = false,
                                topLeft = Offset(inset, inset),
                                size = Size(size.width - inset * 2f, size.height - inset * 2f),
                                style = Stroke(width = stroke, cap = StrokeCap.Round),
                            )
                        }
                    } else {
                        // Inner circle: visible when unchecked, fades out when checked
                        // (COUISwitch inner_circle_width 7.9dp vs outer 18dp).
                        val innerAlpha = innerCircleAlphaState.value
                        if (innerAlpha > 0f) {
                            drawCircle(
                                color = innerCircleColor,
                                radius = size.minDimension / 2f * (SwitchDefaults.InnerCircleSize / SwitchDefaults.ThumbSize),
                                alpha = innerAlpha,
                            )
                        }
                    }
                },
        )
    }
}

object SwitchDefaults {

    /** The track width of the [Switch] (COUISwitch bar_width). */
    val TrackWidth: Dp = 38.dp

    /** The track height of the [Switch] (COUISwitch bar_height). */
    val TrackHeight: Dp = 24.dp

    /** The diameter of the [Switch] thumb (COUISwitch outer_circle_width). */
    val ThumbSize: Dp = 18.dp

    /** The diameter of the inner circle shown on the unchecked thumb (COUISwitch inner_circle_width). */
    val InnerCircleSize: Dp = 7.9.dp

    /** The inset of the thumb from the track edge when fully on one side (COUISwitch circle_padding). */
    private val ThumbMargin: Dp = 3.dp

    /** The thumb offset in the unchecked (off) position. */
    val ThumbStartOffset: Dp = ThumbMargin

    /** The thumb offset in the checked (on) position. */
    val ThumbEndOffset: Dp = TrackWidth - ThumbSize - ThumbMargin

    /** The distance the thumb travels between off and on. */
    val Travel: Dp = ThumbEndOffset - ThumbStartOffset

    /**
     * The default colors for the [Switch].
     */
    @Composable
    fun switchColors(
        checkedThumbColor: Color = if (isDynamicColor) LocalColors.current.onPrimary else MiuixTheme.colorScheme.onPrimary,
        uncheckedThumbColor: Color = if (isDynamicColor) LocalColors.current.onSurface.copy(0.38f) else MiuixTheme.colorScheme.onSecondary,
        disabledCheckedThumbColor: Color = if (isDynamicColor) LocalColors.current.surface else MiuixTheme.colorScheme.disabledOnPrimary,
        disabledUncheckedThumbColor: Color = MiuixTheme.colorScheme.disabledOnSecondary,
        checkedTrackColor: Color = MiuixTheme.colorScheme.primary,
        uncheckedTrackColor: Color = MiuixTheme.colorScheme.secondary,
        disabledCheckedTrackColor: Color = MiuixTheme.colorScheme.disabledPrimary,
        disabledUncheckedTrackColor: Color = MiuixTheme.colorScheme.disabledSecondary,
    ): SwitchColors = remember(
        checkedThumbColor,
        uncheckedThumbColor,
        disabledCheckedThumbColor,
        disabledUncheckedThumbColor,
        checkedTrackColor,
        uncheckedTrackColor,
        disabledCheckedTrackColor,
        disabledUncheckedTrackColor,
    ) {
        SwitchColors(
            checkedThumbColor = checkedThumbColor,
            uncheckedThumbColor = uncheckedThumbColor,
            disabledCheckedThumbColor = disabledCheckedThumbColor,
            disabledUncheckedThumbColor = disabledUncheckedThumbColor,
            checkedTrackColor = checkedTrackColor,
            uncheckedTrackColor = uncheckedTrackColor,
            disabledCheckedTrackColor = disabledCheckedTrackColor,
            disabledUncheckedTrackColor = disabledUncheckedTrackColor,
        )
    }
}

@Immutable
data class SwitchColors(
    private val checkedThumbColor: Color,
    private val uncheckedThumbColor: Color,
    private val disabledCheckedThumbColor: Color,
    private val disabledUncheckedThumbColor: Color,
    private val checkedTrackColor: Color,
    private val uncheckedTrackColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledUncheckedTrackColor: Color,
) {
    @Stable
    internal fun checkedThumbColor(enabled: Boolean): Color = if (enabled) checkedThumbColor else disabledCheckedThumbColor

    @Stable
    internal fun uncheckedThumbColor(enabled: Boolean): Color = if (enabled) uncheckedThumbColor else disabledUncheckedThumbColor

    @Stable
    internal fun checkedTrackColor(enabled: Boolean): Color = if (enabled) checkedTrackColor else disabledCheckedTrackColor

    @Stable
    internal fun uncheckedTrackColor(enabled: Boolean): Color = if (enabled) uncheckedTrackColor else disabledUncheckedTrackColor
}
