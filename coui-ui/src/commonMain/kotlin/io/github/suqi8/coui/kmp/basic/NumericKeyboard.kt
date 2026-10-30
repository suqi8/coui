// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect
import io.github.suqi8.coui.kmp.effect.pressSpring
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

/**
 * The press feedback of a [NumericKeyboard] (`couiPressEffect`).
 */
enum class NumericKeyboardPressEffect {
    /**
     * `pressRipple` (the default): a disc grows under the key and fades out while a soft halo
     * spreads around it.
     */
    Ripple,

    /**
     * `pressLight`, the lock-screen glass keys: no fill, an inner shadow and a hairline border on
     * every digit, the pressed key springs down and glows from its rim while the neighbouring keys
     * catch its light.
     */
    Light,
}

/**
 * A side key of a [NumericKeyboard], the bottom-left or bottom-right cell
 * (`COUINumericKeyboard.SideStyle`). It shows [icon] when set, otherwise [text].
 *
 * @param icon The icon, tinted with [NumericKeyboardColors.numberColor].
 * @param text The label, drawn bold.
 * @param contentDescription The accessibility description.
 * @param textColor The color of [text]; unspecified uses [NumericKeyboardColors.wordTextColor].
 * @param textSize The size of [text] (`coui_number_keyboard_finish_text_size`, a dp size).
 */
@Immutable
data class NumericKeyboardSideKey(
    val icon: ImageVector? = null,
    val text: String? = null,
    val contentDescription: String? = text,
    val textColor: Color = Color.Unspecified,
    val textSize: Dp = NumericKeyboardDefaults.SideTextSize,
)

/**
 * The per-key animations of a [NumericKeyboard]. Create it with [rememberNumericKeyboardState].
 */
@Stable
class NumericKeyboardState internal constructor() {
    internal val keys = Array(12) { NumericKeyDotState() }
    internal var scope: CoroutineScope? = null

    /**
     * Plays the COUI entrance (`getEnterAnim`): each key rises 258dp
     * (`coui_numeric_keyboard_max_translate_y`) over 500ms (`COUIInEaseInterpolator`) and fades in
     * over 167ms (`COUIEaseInterpolator`) after 166ms, key `i` delayed by `16ms * i` (the right key
     * takes the left key's slot when there is no left key).
     */
    suspend fun animateEnter(hasLeftKey: Boolean = false) {
        val scope = scope ?: return
        val jobs = mutableListOf<Job>()
        keys.forEachIndexed { index, key ->
            val slot = if ((index == 10 || index == 11) && !hasLeftKey) index - 1 else index
            key.enterAlpha.snapTo(0f)
            key.enterTranslate.snapTo(1f)
            val stagger = slot * KEY_ENTER_STAGGER_MS
            jobs += scope.launch { key.enterAlpha.animateTo(1f, tween(KEY_ENTER_ALPHA_MS, KEY_ENTER_ALPHA_DELAY_MS + stagger, KeyCouiEase)) }
            jobs += scope.launch { key.enterTranslate.animateTo(0f, tween(KEY_ENTER_TRANSLATE_MS, stagger, KeyCouiInEase)) }
        }
        jobs.forEach { it.join() }
    }
}

/** The animation values of one key (`COUINumericKeyboard.Cell`). */
internal class NumericKeyDotState {
    // Ripple (pressRipple): the normal disc and the blurred halo.
    val normalScale = Animatable(1f)
    val normalAlpha = Animatable(0f)
    val blurProgress = Animatable(1f)
    var showJob: Job? = null

    // Light (pressLight): the press scale and the light springs.
    val press = Animatable(0f)
    val light = LockLightEffectState()

    // Entrance.
    val enterAlpha = Animatable(1f)
    val enterTranslate = Animatable(0f)

    var pointer: PointerId? by mutableStateOf(null)
}

/** Creates and remembers a [NumericKeyboardState]. */
@Composable
fun rememberNumericKeyboardState(): NumericKeyboardState = remember { NumericKeyboardState() }

/**
 * The COUI numeric keyboard (`COUINumericKeyboard`): digits 1-9 and 0 on round 68dp keys in a
 * 3 x 4 grid, with an optional side key in each bottom corner (by default a delete key on the
 * right).
 *
 * A key fires on release when the finger is still on it; sliding off cancels it. Each touch down
 * plays a keyboard haptic. Several fingers can press keys at once. The press effect is either
 * the [NumericKeyboardPressEffect.Ripple] of the Settings keyboard or the
 * [NumericKeyboardPressEffect.Light] glass keys of the lock screen (use
 * [NumericKeyboardDefaults.lockScreenColors] with it, over a wallpaper or a dark surface).
 *
 * The keyboard measures 248 x 326dp by default (`coui_numeric_keyboard_view_width / _height`);
 * a [modifier] size overrides it and the spacing between keys follows.
 *
 * @param onNumberClick Called with the digit of a released key.
 * @param modifier The modifier applied to the keyboard.
 * @param state The [NumericKeyboardState] holding the key animations.
 * @param enabled Whether keys accept touches.
 * @param hapticFeedbackEnabled Whether a touch down vibrates (`setTactileFeedbackEnabled`).
 * @param leftKey The bottom-left side key, or `null` for an empty cell.
 * @param rightKey The bottom-right side key, or `null` for an empty cell. Setting or clearing it
 *   after the first composition fades it in or out on COUI's response 0.2 spring
 *   (`setRightStyle(style, true)`).
 * @param pressEffect The [NumericKeyboardPressEffect].
 * @param keySize The diameter of a key (`coui_numeric_keyboard_view_size`).
 * @param colors The [NumericKeyboardColors].
 * @param onLeftKeyClick Called when [leftKey] is released (`onClickLeft`).
 * @param onRightKeyClick Called when [rightKey] is released (`onClickRight`).
 */
@Composable
fun NumericKeyboard(
    onNumberClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    state: NumericKeyboardState = rememberNumericKeyboardState(),
    enabled: Boolean = true,
    hapticFeedbackEnabled: Boolean = true,
    leftKey: NumericKeyboardSideKey? = null,
    rightKey: NumericKeyboardSideKey? = NumericKeyboardDefaults.deleteKey(),
    pressEffect: NumericKeyboardPressEffect = NumericKeyboardPressEffect.Ripple,
    keySize: Dp = NumericKeyboardDefaults.KeySize,
    colors: NumericKeyboardColors = NumericKeyboardDefaults.colors(),
    onLeftKeyClick: () -> Unit = {},
    onRightKeyClick: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    state.scope = scope
    val haptic = LocalHapticFeedback.current
    val currentHaptic by rememberUpdatedState(haptic)
    val currentHapticEnabled by rememberUpdatedState(hapticFeedbackEnabled)
    val currentOnNumber by rememberUpdatedState(onNumberClick)
    val currentOnLeft by rememberUpdatedState(onLeftKeyClick)
    val currentOnRight by rememberUpdatedState(onRightKeyClick)
    val currentEffect by rememberUpdatedState(pressEffect)
    val currentCircleMaxAlpha by rememberUpdatedState(colors.circleMaxAlpha)

    val left = rememberSideKey(leftKey)
    val right = rememberSideKey(rightKey)

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer(cacheSize = 16)
    val numberStyle = remember(density, colors.numberColor) {
        TextStyle(
            color = colors.numberColor,
            fontSize = with(density) { NumericKeyboardDefaults.NumberTextSize.toSp() },
            fontWeight = FontWeight.Medium,
        )
    }
    val leftPainter = left.key?.icon?.let { rememberVectorPainter(it) }
    val rightPainter = right.key?.icon?.let { rememberVectorPainter(it) }
    val sidePainters = remember(leftPainter, rightPainter) { arrayOf(leftPainter, rightPainter) }

    val geometry = remember { KeyboardGeometry() }

    Layout(
        content = {
            // Accessibility only: one node per key (PatternExploreByTouchHelper).
            for (index in 0 until 12) {
                val side = when (index) {
                    SIDE_LEFT_INDEX -> left
                    SIDE_RIGHT_INDEX -> right
                    else -> null
                }
                if (side != null && side.key == null) {
                    Spacer(Modifier)
                } else {
                    val description = side?.key?.contentDescription ?: KeyboardNumbers[index].toString()
                    Spacer(
                        Modifier.semantics {
                            role = Role.Button
                            contentDescription = description
                            onClick {
                                if (!enabled) return@onClick false
                                dispatchKey(index, currentOnNumber, currentOnLeft, currentOnRight)
                                true
                            }
                        },
                    )
                }
            }
        },
        modifier = modifier
            .size(NumericKeyboardDefaults.Width, NumericKeyboardDefaults.Height)
            .pointerInput(state, enabled) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    var event = awaitPointerEvent()
                    while (true) {
                        event.changes.forEach { change ->
                            val id = change.id
                            when {
                                change.pressed && !change.previousPressed -> {
                                    val index = geometry.hit(change.position, left.visible, right.visible)
                                    if (index >= 0) {
                                        if (currentHapticEnabled) currentHaptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                        val key = state.keys[index]
                                        key.pointer = id
                                        pressKey(key, scope, currentEffect, currentCircleMaxAlpha)
                                    }
                                    change.consume()
                                }

                                !change.pressed && change.previousPressed -> {
                                    val index = geometry.hit(change.position, left.visible, right.visible)
                                    if (index >= 0 && state.keys[index].pointer == id) {
                                        dispatchKey(index, currentOnNumber, currentOnLeft, currentOnRight)
                                    }
                                    state.keys.firstOrNull { it.pointer == id }?.let {
                                        it.pointer = null
                                        releaseKey(it, scope, currentEffect, currentCircleMaxAlpha)
                                    }
                                    change.consume()
                                }

                                change.pressed -> {
                                    val owned = state.keys.indexOfFirst { it.pointer == id }
                                    if (owned >= 0) {
                                        val index = geometry.hit(change.position, left.visible, right.visible)
                                        if (index != owned || change.isConsumed) {
                                            val key = state.keys[owned]
                                            key.pointer = null
                                            releaseKey(key, scope, currentEffect, currentCircleMaxAlpha)
                                        }
                                    }
                                    change.consume()
                                }
                            }
                        }
                        if (event.changes.none { it.pressed }) break
                        event = awaitPointerEvent()
                    }
                    state.keys.forEach { key ->
                        if (key.pointer != null) {
                            key.pointer = null
                            releaseKey(key, scope, currentEffect, currentCircleMaxAlpha)
                        }
                    }
                }
            }
            .drawWithCache {
                val keyPx = keySize.toPx()
                geometry.update(size, keyPx, NumericKeyboardDefaults.AdditionalPressableArea.toPx())
                val shadows = if (pressEffect == NumericKeyboardPressEffect.Light) {
                    recordLockInnerShadow(Size(keyPx, keyPx)) { Path().apply { addOval(Rect(Offset.Zero, it)) } }
                } else {
                    emptyList()
                }
                val numberLayouts = KeyboardNumbers.map { number ->
                    if (number < 0) null else textMeasurer.measure(number.toString(), numberStyle)
                }
                val numberOffsetY = NumericKeyboardDefaults.NumberOffsetY.toPx()
                val maxTranslate = NumericKeyboardDefaults.EnterTranslateY.toPx()
                val lightRadius = NumericKeyboardDefaults.LightShaderRadius.toPx()
                onDrawBehind {
                    val radius = keyPx / 2f
                    for (index in 0 until 12) {
                        val key = state.keys[index]
                        val center = geometry.center(index).let { Offset(it.x, it.y + key.enterTranslate.value * maxTranslate) }
                        val scale = if (pressEffect == NumericKeyboardPressEffect.Light) keyScale(key, keyPx) else 1f
                        val side = when (index) {
                            SIDE_LEFT_INDEX -> left
                            SIDE_RIGHT_INDEX -> right
                            else -> null
                        }
                        if (side != null) {
                            if (!side.visible) continue
                            if (pressEffect == NumericKeyboardPressEffect.Ripple) drawPressCircle(key, center, radius, colors)
                            drawSideKey(
                                side = side,
                                painter = sidePainters[if (index == SIDE_LEFT_INDEX) 0 else 1],
                                center = center,
                                radius = radius,
                                scale = scale,
                                alpha = key.enterAlpha.value,
                                colors = colors,
                                textMeasurer = textMeasurer,
                            )
                            continue
                        }
                        val alpha = key.enterAlpha.value.coerceIn(0f, 1f)
                        if (pressEffect == NumericKeyboardPressEffect.Ripple) {
                            drawPressCircle(key, center, radius, colors)
                            if (colors.numberBackgroundColor.alpha > 0f) {
                                drawCircle(colors.numberBackgroundColor, radius, center, alpha = alpha)
                            }
                        } else {
                            val circle = Path().apply { addOval(Rect(center, radius * scale)) }
                            drawLockInnerShadow(shadows, circle, Offset(center.x - radius * scale, center.y - radius * scale), scale, alpha)
                            val highlight = key.light.inner.value
                            if (highlight > 0f) {
                                // COUI sets the color to the highlight *alpha* (0xCC, an opaque-less blue
                                // channel) before blending with LUMINOSITY; kept as it renders on device.
                                drawLockInnerBorder(circle, HighlightBorderColor, alpha = highlight, blendMode = BlendMode.Luminosity)
                            }
                            drawLockInnerBorder(circle, alpha = alpha)
                        }
                        val layout = numberLayouts[index] ?: continue
                        scale(scale, scale, pivot = center) {
                            drawText(
                                textLayoutResult = layout,
                                topLeft = Offset(
                                    center.x - layout.size.width / 2f,
                                    center.y - layout.size.height / 2f - numberOffsetY,
                                ),
                                alpha = alpha,
                            )
                        }
                    }
                    if (pressEffect == NumericKeyboardPressEffect.Light) {
                        for (index in 0 until 12) {
                            val key = state.keys[index]
                            val inner = key.light.inner.value
                            val outer = key.light.outer.value
                            // drawLightEffect only runs while the inner light is up.
                            if (inner <= 0f) continue
                            if (index == SIDE_LEFT_INDEX && !left.visible) continue
                            if (index == SIDE_RIGHT_INDEX && !right.visible) continue
                            val center = geometry.center(index)
                            val neighbours = Path()
                            for (other in 0 until 12) {
                                if (other == index || other == SIDE_LEFT_INDEX || other == SIDE_RIGHT_INDEX) continue
                                val otherScale = keyScale(state.keys[other], keyPx)
                                neighbours.addOval(Rect(geometry.center(other), radius * otherScale))
                            }
                            val reach = Path().apply { addOval(Rect(center, lightRadius)) }
                            val lightPath = Path().apply { op(reach, neighbours, PathOperation.Intersect) }
                            drawLockLightEffect(
                                center = center,
                                lightPath = lightPath,
                                lightRadius = lightRadius,
                                innerRadius = radius,
                                scale = keyScale(key, keyPx),
                                innerAlpha = inner,
                                outerAlpha = outer,
                            )
                        }
                    }
                }
            },
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val keyPx = keySize.roundToPx()
        geometry.update(Size(width.toFloat(), height.toFloat()), keyPx.toFloat(), NumericKeyboardDefaults.AdditionalPressableArea.toPx())
        val placeables = measurables.map { it.measure(Constraints.fixed(keyPx, keyPx)) }
        layout(width, height) {
            placeables.forEachIndexed { index, placeable ->
                val center = geometry.center(index)
                placeable.place((center.x - keyPx / 2f).toInt(), (center.y - keyPx / 2f).toInt())
            }
        }
    }
}

/** A side key with its visibility spring (`SideStyle.mAlpha`, `mSideStyleAlphaAnimator`). */
private class SideKeyHolder(initial: NumericKeyboardSideKey?) {
    var key: NumericKeyboardSideKey? by mutableStateOf(initial)
    val alpha = Animatable(if (initial != null) 1f else 0f)
    var initialized = false
    val visible: Boolean get() = key != null && alpha.value > 0f
}

@Composable
private fun rememberSideKey(target: NumericKeyboardSideKey?): SideKeyHolder {
    val holder = remember { SideKeyHolder(target) }
    LaunchedEffect(target) {
        if (!holder.initialized) {
            // setLeftStyle / setRightStyle without animation: shown at full alpha at once.
            holder.initialized = true
            return@LaunchedEffect
        }
        if (target != null) {
            holder.key = target
            holder.alpha.animateTo(1f, SideKeySpring)
        } else {
            holder.alpha.animateTo(0f, SideKeySpring)
            holder.key = null
        }
    }
    return holder
}

/** The key grid (`onSizeChanged`, `getCenterXForColumn`, `getRowHit` / `getColumnHit`). */
private class KeyboardGeometry {
    var key = 0f
    var hSpacing = 0f
    var vSpacing = 0f
    var extra = 0f
    var extraV = 0f

    fun update(size: Size, keyPx: Float, additional: Float) {
        key = keyPx
        hSpacing = (size.width - keyPx * 3f) / 2f
        vSpacing = (size.height - keyPx * 4f) / 3f
        extra = max(0f, min(hSpacing / 2f, additional))
        extraV = max(0f, min(vSpacing / 2f, additional))
    }

    fun center(index: Int): Offset {
        val row = index / 3
        val column = index % 3
        return Offset(key / 2f + column * (key + hSpacing), key / 2f + row * (key + vSpacing))
    }

    fun hit(position: Offset, leftVisible: Boolean, rightVisible: Boolean): Int {
        var row = -1
        for (i in 0..3) {
            val c = key / 2f + i * (key + vSpacing)
            if (position.y >= max(c - key / 2f - extraV, 0f) && position.y <= c + key / 2f + extraV) {
                row = i
                break
            }
        }
        if (row < 0) return -1
        var column = -1
        for (i in 0..2) {
            val c = key / 2f + i * (key + hSpacing)
            if (position.x >= max(c - key / 2f - extra, 0f) && position.x <= c + key / 2f + extra) {
                column = i
                break
            }
        }
        if (column < 0) return -1
        val index = row * 3 + column
        if (index == SIDE_LEFT_INDEX && !leftVisible) return -1
        if (index == SIDE_RIGHT_INDEX && !rightVisible) return -1
        return index
    }
}

private fun dispatchKey(index: Int, onNumber: (Int) -> Unit, onLeft: () -> Unit, onRight: () -> Unit) {
    when (index) {
        SIDE_LEFT_INDEX -> onLeft()
        SIDE_RIGHT_INDEX -> onRight()
        else -> onNumber(KeyboardNumbers[index])
    }
}

/** `initShowAnimator` / `executeLightEffectAnimator(true)`. */
private fun pressKey(key: NumericKeyDotState, scope: CoroutineScope, effect: NumericKeyboardPressEffect, maxAlpha: Float) {
    if (effect == NumericKeyboardPressEffect.Ripple) {
        key.showJob?.cancel()
        key.showJob = scope.launch {
            launch {
                key.normalScale.snapTo(1f)
                key.normalScale.animateTo(SHOW_END_SCALE, tween(SHOW_DURATION_MS, easing = KeyCouiOutEase))
            }
            launch {
                key.normalAlpha.snapTo(0f)
                key.normalAlpha.animateTo(maxAlpha, tween(SHOW_DURATION_MS, easing = KeyCouiOutEase))
            }
        }
    } else {
        scope.launch { key.press.animateTo(1f, pressSpring(KEY_DP, KEY_DP, pressed = true)) }
        scope.launch { key.light.animate(true) }
    }
}

/** `initFadeAnimator` / `executeLightEffectAnimator(false)`. */
private fun releaseKey(key: NumericKeyDotState, scope: CoroutineScope, effect: NumericKeyboardPressEffect, maxAlpha: Float) {
    if (effect == NumericKeyboardPressEffect.Ripple) {
        // showAnimator.end(): jump to the end of the show before fading.
        key.showJob?.cancel()
        key.showJob = scope.launch {
            launch {
                key.normalScale.snapTo(SHOW_END_SCALE)
                key.normalScale.animateTo(FADE_END_SCALE, tween(FADE_DURATION_MS, easing = FadeEase))
            }
            launch {
                key.normalAlpha.snapTo(maxAlpha)
                key.normalAlpha.animateTo(0f, tween(FADE_DURATION_MS, easing = FadeEase))
            }
            launch {
                key.blurProgress.snapTo(0f)
                key.blurProgress.animateTo(1f, tween(BLUR_DURATION_MS, easing = FadeEase))
            }
        }
    } else {
        scope.launch { key.press.animateTo(0f, pressSpring(KEY_DP, KEY_DP, pressed = false)) }
        scope.launch { key.light.animate(false) }
    }
}

/** The `COUIPressFeedbackHelper` scale of a key (the `COUIPressAnimatorUnit` target for its size). */
private fun DrawScope.keyScale(key: NumericKeyDotState, keyPx: Float): Float {
    val keyDp = keyPx / density
    return 1f + (COUIMaterialEffect.pressTargetScale(keyDp, keyDp) - 1f) * key.press.value
}

/**
 * `drawPressCircle`: `coui_number_keyboard_normal_circle` (a disc of 46.7% of the key radius,
 * scaled 1 -> 2.15 on press and 2.15 -> 2.5 on release) and `coui_number_keyboard_blur_circle`
 * (a soft disc filling the key bounds, scaled 1 -> 2 while its alpha peaks halfway), both tinted
 * with [NumericKeyboardColors.pressedColor].
 */
private fun DrawScope.drawPressCircle(key: NumericKeyDotState, center: Offset, radius: Float, colors: NumericKeyboardColors) {
    val normalAlpha = key.normalAlpha.value
    if (normalAlpha > 0f) {
        drawCircle(
            color = colors.pressedColor,
            radius = radius * NORMAL_CIRCLE_FRACTION * key.normalScale.value,
            center = center,
            alpha = normalAlpha.coerceIn(0f, 1f),
        )
    }
    val progress = key.blurProgress.value
    if (progress < 1f) {
        val blurAlpha = colors.circleMaxAlpha * (if (progress < 0.5f) progress * 2f else (1f - progress) * 2f)
        if (blurAlpha > 0f) {
            val blurRadius = radius * (1f + progress)
            drawCircle(
                brush = Brush.radialGradient(colorStops = BlurCircleStops, center = center, radius = blurRadius),
                radius = blurRadius,
                center = center,
                alpha = blurAlpha.coerceIn(0f, 1f),
                colorFilter = ColorFilter.tint(colors.pressedColor),
            )
        }
    }
}

/** `drawSide`: the side background circle, then the icon or the bold label. */
private fun DrawScope.drawSideKey(
    side: SideKeyHolder,
    painter: Painter?,
    center: Offset,
    radius: Float,
    scale: Float,
    alpha: Float,
    colors: NumericKeyboardColors,
    textMeasurer: TextMeasurer,
) {
    val key = side.key ?: return
    val sideAlpha = (side.alpha.value * alpha).coerceIn(0f, 1f)
    if (sideAlpha <= 0f) return
    if (colors.sideBackgroundColor.alpha > 0f) {
        drawCircle(colors.sideBackgroundColor, radius * scale, center, alpha = sideAlpha)
    }
    if (painter != null) {
        val intrinsic = painter.intrinsicSize
        val w = intrinsic.width * scale
        val h = intrinsic.height * scale
        translate(center.x - w / 2f, center.y - h / 2f) {
            with(painter) { draw(Size(w, h), alpha = sideAlpha, colorFilter = ColorFilter.tint(colors.numberColor)) }
        }
    } else if (!key.text.isNullOrEmpty()) {
        val color = if (key.textColor != Color.Unspecified) key.textColor else colors.wordTextColor
        val layout = textMeasurer.measure(
            key.text,
            TextStyle(color = color, fontSize = (key.textSize.toPx() * scale).toSp(), fontWeight = FontWeight.Bold),
        )
        drawText(layout, topLeft = Offset(center.x - layout.size.width / 2f, center.y - layout.size.height / 2f), alpha = sideAlpha)
    }
}

/** Contains the default values used by [NumericKeyboard]. */
object NumericKeyboardDefaults {
    /** The default width (`coui_numeric_keyboard_view_width`). */
    val Width: Dp = 248.dp

    /** The default height (`coui_numeric_keyboard_view_height`). */
    val Height: Dp = 326.dp

    /** The key diameter (`coui_numeric_keyboard_view_size`). */
    val KeySize: Dp = 68.dp

    /** The digit size (`number_keyboard_number_size`, a dp size not scaled by the font scale). */
    val NumberTextSize: Dp = 32.dp

    /** How far the digits sit above the key center (`coui_numeric_keyboard_number_offset_y`). */
    val NumberOffsetY: Dp = 1.dp

    /** The side key label size (`coui_number_keyboard_finish_text_size`). */
    val SideTextSize: Dp = 16.dp

    /** The touch slop around each key, capped at half the spacing (`coui_additional_pressable_area`). */
    val AdditionalPressableArea: Dp = 32.dp

    /** The reach of a pressed key's light on its neighbours (`coui_numeric_light_shader_radius`). */
    val LightShaderRadius: Dp = 124.dp

    /** The rise of the entrance animation (`coui_numeric_keyboard_max_translate_y`). */
    val EnterTranslateY: Dp = 258.dp

    /** The delete glyph (`ic_coui_number_keyboard_setting_delete`, 25 x 14dp). */
    val DeleteIcon: ImageVector by lazy {
        ImageVector.Builder("NumericKeyboardDelete", 25.dp, 14.dp, 25f, 14f).apply {
            addPath(
                pathData = addPathNodes(
                    "M6.777,0C6.196,0 5.644,0.253 5.265,0.692L0.324,6.413C-0.124,6.931 -0.105,7.704 0.366,8.201L5.271,13.364" +
                        "C5.649,13.761 6.172,13.986 6.72,13.986H22.512C23.616,13.986 24.51,13.092 24.51,11.988V1.998" +
                        "C24.51,0.895 23.616,0 22.512,0H6.777ZM12.149,3.996L11.207,4.938L13.245,6.976L11.28,8.941L12.222,9.883" +
                        "L14.187,7.918L16.152,9.883L17.094,8.941L15.129,6.976L17.167,4.938L16.225,3.996L14.187,6.034L12.149,3.996Z",
                ),
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            )
        }.build()
    }

    /** The delete side key (`mDeleteStyle`, `coui_number_keyboard_delete`). */
    fun deleteKey(contentDescription: String = "Delete"): NumericKeyboardSideKey = NumericKeyboardSideKey(icon = DeleteIcon, contentDescription = contentDescription)

    /** The finish side key (`mFinishStyle`, `coui_numeric_keyboard_sure`). */
    fun finishKey(text: String = "Done"): NumericKeyboardSideKey = NumericKeyboardSideKey(text = text)

    /**
     * The Settings keyboard colors (`SettingsNumericKeyboardStyle` on
     * `Widget.COUI.COUINumericKeyboard.Dark`, and `SettingsNumericKeyboardDarkStyle` on
     * `.Dark.Dark`): digits #000000 / `coui_color_label_primary_dark` #E6FFFFFF on
     * `coui_color_surface` keys, side keys without a fill / #4DD9D9D9, ripple #000000 / #FFFFFF at
     * 6% / 12% (`coui_numeric_keyboard_dark_circle_max_alpha`), finish label #2AD181.
     */
    @Composable
    fun colors(
        numberColor: Color = if (isDark()) Color(0xE6FFFFFF) else Color.Black,
        numberBackgroundColor: Color = COUITheme.colorScheme.surfaceContainer,
        sideBackgroundColor: Color = if (isDark()) Color(0x4DD9D9D9) else Color.Transparent,
        pressedColor: Color = if (isDark()) Color.White else Color.Black,
        circleMaxAlpha: Float = if (isDark()) 0.12f else 0.06f,
        wordTextColor: Color = Color(0xFF2AD181),
    ): NumericKeyboardColors = remember(numberColor, numberBackgroundColor, sideBackgroundColor, pressedColor, circleMaxAlpha, wordTextColor) {
        NumericKeyboardColors(numberColor, numberBackgroundColor, sideBackgroundColor, pressedColor, circleMaxAlpha, wordTextColor)
    }

    /**
     * The lock-screen keyboard colors (`Widget.COUI.COUINumericKeyboard`): white digits and labels
     * (`coui_numeric_keyboard_number_color`, `_word_text_normal_color`) with no key fill, white
     * ripple at 12%. Pair with [NumericKeyboardPressEffect.Light].
     */
    @Composable
    fun lockScreenColors(
        numberColor: Color = Color.White,
        numberBackgroundColor: Color = Color.Transparent,
        sideBackgroundColor: Color = Color.Transparent,
        pressedColor: Color = Color.White,
        circleMaxAlpha: Float = 0.12f,
        wordTextColor: Color = Color.White,
    ): NumericKeyboardColors = remember(numberColor, numberBackgroundColor, sideBackgroundColor, pressedColor, circleMaxAlpha, wordTextColor) {
        NumericKeyboardColors(numberColor, numberBackgroundColor, sideBackgroundColor, pressedColor, circleMaxAlpha, wordTextColor)
    }

    @Composable
    private fun isDark(): Boolean = COUITheme.colorScheme.background.luminance() < 0.5f
}

/**
 * Colors used by a [NumericKeyboard].
 *
 * @param numberColor The digits and the side key icons (`couiNumberColor`).
 * @param numberBackgroundColor The fill of the digit keys (`couiNumberBackgroundColor`; ripple
 *   effect only).
 * @param sideBackgroundColor The fill of the side keys (`couiSideBackgroundColor`).
 * @param pressedColor The ripple color (`couiNumPressColor`).
 * @param circleMaxAlpha The peak alpha of the ripple (`couiCircleMaxAlpha`).
 * @param wordTextColor The default side key label color (`couiWordTextNormalColor`).
 */
@Immutable
data class NumericKeyboardColors(
    val numberColor: Color,
    val numberBackgroundColor: Color,
    val sideBackgroundColor: Color,
    val pressedColor: Color,
    val circleMaxAlpha: Float,
    val wordTextColor: Color,
)

/** `mKeyboardNumbers`: the digit of each cell, -1 for the side cells. */
private val KeyboardNumbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, -1, 0, -1)

private const val SIDE_LEFT_INDEX = 9
private const val SIDE_RIGHT_INDEX = 11

/** The key size in dp the press springs are resolved for. */
private const val KEY_DP = 68f

/** The normal circle disc: 70px of the 150px half-size of `coui_number_keyboard_normal_circle.png`. */
private const val NORMAL_CIRCLE_FRACTION = 70f / 150f

private const val SHOW_END_SCALE = 2.15f
private const val FADE_END_SCALE = 2.5f
private const val SHOW_DURATION_MS = 100
private const val FADE_DURATION_MS = 160
private const val BLUR_DURATION_MS = 400
private const val KEY_ENTER_ALPHA_MS = 167
private const val KEY_ENTER_ALPHA_DELAY_MS = 166
private const val KEY_ENTER_TRANSLATE_MS = 500
private const val KEY_ENTER_STAGGER_MS = 16

/** `coui_numeric_keyboard_border_highlight_color` as COUI ends up painting it (see the draw). */
private val HighlightBorderColor = Color(0xFF0000CC)

/**
 * `coui_number_keyboard_blur_circle.png` (300px at xxhdpi), alpha against the fraction of its
 * radius: opaque to 17%, 95% at 33%, 84% at 47%, 52% at 67%, 22% at 83% and 5.5% at the edge.
 */
private val BlurCircleStops = arrayOf(
    0f to Color.White,
    0.167f to Color.White,
    0.333f to Color.White.copy(alpha = 0.953f),
    0.467f to Color.White.copy(alpha = 0.843f),
    0.667f to Color.White.copy(alpha = 0.518f),
    0.833f to Color.White.copy(alpha = 0.22f),
    1f to Color.White.copy(alpha = 0.055f),
)

/** `COUIOutEaseInterpolator`: cubic-bezier(0.3, 0, 1, 1), the show animation. */
private val KeyCouiOutEase = CubicBezierEasing(0.3f, 0f, 1f, 1f)

/** `PATH_INTERPOLATOR`: `PathInterpolator(0, 0, 0.6, 1)`, the fade animations. */
private val FadeEase = CubicBezierEasing(0f, 0f, 0.6f, 1f)

/** `COUIEaseInterpolator`: cubic-bezier(0.33, 0, 0.67, 1). */
private val KeyCouiEase = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** `COUIInEaseInterpolator`: cubic-bezier(0, 0, 0.1, 1). */
private val KeyCouiInEase = CubicBezierEasing(0f, 0f, 0.1f, 1f)

/** `SIDE_STYLE_SPRING_RESPONSE` 0.2, bounce 0. */
private val SideKeySpring = spring<Float>(dampingRatio = 1f, stiffness = 986.96f)
