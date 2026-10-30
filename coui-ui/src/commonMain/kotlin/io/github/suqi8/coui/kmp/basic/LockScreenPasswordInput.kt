// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.ShadowContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect
import io.github.suqi8.coui.kmp.effect.pressSpring
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch

/** Where a [LockScreenPasswordInput] is shown (`couiIsScenesMode`). */
enum class LockScreenPasswordInputMode {
    /** `SETTING`: a card-colored field and next button, for Settings pages. */
    Setting,

    /**
     * `DESKTOP`: the lock-screen glass look over the wallpaper. The field and the button are
     * transparent with an inner shadow and a hairline border; the button springs down when pressed
     * and lights up its rim and the end of the field.
     */
    Desktop,
}

/**
 * The COUI lock-screen password entry (`COUILockScreenPwdInputLayout` +
 * `COUILockScreenPwdInputView`): a 48dp capsule password field with an eye toggle and, 12dp to its
 * end, a round 48dp next button.
 *
 * With [enableInputCount] the input is limited to [maxCount] characters and the next button only
 * becomes active (`_allow` icon) once [minCount] are entered; otherwise it is always active.
 * Clicking an active button calls [onNextClick] with the password. A non-empty [errorMessage] is
 * shown below the field (`text_input_error`) and turns the field into its error state.
 *
 * @param value The password.
 * @param onValueChange Called when the password changes.
 * @param onNextClick Called with the password when the active next button is clicked
 *   (`NextIconCheckListener.checkedPwd`).
 * @param modifier The modifier applied to the whole component.
 * @param mode The [LockScreenPasswordInputMode].
 * @param enabled Whether the field accepts input.
 * @param label The hint shown while the field is empty.
 * @param errorMessage The error shown below the field; empty hides it.
 * @param showPasswordToggle Whether the eye toggle is shown (`couiEnablePassword`, true in
 *   `coui_input_lock_screen_pwd_layout`).
 * @param enableInputCount Whether [minCount] / [maxCount] apply (`couiEnableInputCount`).
 * @param minCount The minimum length that activates the next button (`couiInputMinCount`).
 * @param maxCount The maximum length (`couiInputMaxCount`).
 * @param colors The [LockScreenPasswordInputColors]; defaults follow [mode].
 * @param keyboardOptions The keyboard options (`couiInputType` numberPassword by default).
 * @param keyboardActions The keyboard actions.
 * @param nextContentDescription The accessibility description of the next button
 *   (`coui_lock_screen_next_button_description`).
 * @param nextButtonInteractionSource The [MutableInteractionSource] of the next button. Its press
 *   interactions drive the [LockScreenPasswordInputMode.Desktop] press feedback (light effect and
 *   scale), so emitting a press here plays it as a touch would.
 */
@Composable
fun LockScreenPasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    onNextClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    mode: LockScreenPasswordInputMode = LockScreenPasswordInputMode.Setting,
    enabled: Boolean = true,
    label: String = "",
    errorMessage: String = "",
    showPasswordToggle: Boolean = true,
    enableInputCount: Boolean = false,
    minCount: Int = LockScreenPasswordInputDefaults.MinCount,
    maxCount: Int = LockScreenPasswordInputDefaults.MaxCount,
    colors: LockScreenPasswordInputColors = LockScreenPasswordInputDefaults.colors(mode),
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    nextContentDescription: String = "Next",
    nextButtonInteractionSource: MutableInteractionSource? = null,
) {
    // initInputCountSetting: an invalid pair falls back to 16 / 6.
    val (resolvedMin, resolvedMax) = if (maxCount <= 0 || minCount <= 0 || maxCount <= minCount) {
        LockScreenPasswordInputDefaults.MinCount to LockScreenPasswordInputDefaults.MaxCount
    } else {
        minCount to maxCount
    }
    val allowNext = !enableInputCount || value.length >= resolvedMin
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val handleValueChange = remember(enableInputCount, resolvedMax) {
        { new: String -> currentOnValueChange(if (enableInputCount) new.take(resolvedMax) else new) }
    }
    val isError = errorMessage.isNotEmpty()
    var displayedError by remember { mutableStateOf(errorMessage) }
    if (isError && displayedError != errorMessage) displayedError = errorMessage

    val desktop = mode == LockScreenPasswordInputMode.Desktop
    val shadowContext = LocalGraphicsContext.current.shadowContext
    val scope = rememberCoroutineScope()
    val press = remember { Animatable(0f) }
    val light = remember { LockLightEffectState() }
    val interactionSource = nextButtonInteractionSource ?: remember { MutableInteractionSource() }
    val currentAllow by rememberUpdatedState(allowNext)
    LaunchedEffect(interactionSource, desktop) {
        if (!desktop) return@LaunchedEffect
        interactionSource.interactions.collect { interaction ->
            // The touch listener only runs while the button is active.
            val pressed = when (interaction) {
                is PressInteraction.Press -> true
                is PressInteraction.Release, is PressInteraction.Cancel -> false
                else -> return@collect
            }
            if (pressed && !currentAllow) return@collect
            val size = LockScreenPasswordInputDefaults.Height.value
            scope.launch { press.animateTo(if (pressed) 1f else 0f, pressSpring(size, size, pressed)) }
            scope.launch { light.animate(pressed) }
        }
    }

    Column(modifier = modifier) {
        Layout(
            content = {
                PasswordField(
                    value = value,
                    onValueChange = handleValueChange,
                    desktop = desktop,
                    enabled = enabled,
                    label = label,
                    isError = isError,
                    showPasswordToggle = showPasswordToggle,
                    colors = colors,
                    shadowContext = shadowContext,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                )
                NextButton(
                    allow = allowNext,
                    desktop = desktop,
                    colors = colors,
                    interactionSource = interactionSource,
                    contentDescription = nextContentDescription,
                    pressScale = { buttonScale(press.value) },
                    onClick = { if (allowNext) onNextClick(value) },
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (desktop) {
                        Modifier.drawWithCache {
                            val buttonPx = LockScreenPasswordInputDefaults.Height.toPx()
                            val shadows = recordLockInnerShadow(Size(buttonPx, buttonPx), shadowContext) { Path().apply { addOval(Rect(Offset.Zero, it)) } }
                            val fieldWidth = size.width - buttonPx - LockScreenPasswordInputDefaults.ButtonMargin.toPx()
                            // The button sits at the end: on the left in RTL, like the COUI LinearLayout.
                            val rtl = layoutDirection == LayoutDirection.Rtl
                            val buttonCenter = Offset(if (rtl) buttonPx / 2f else size.width - buttonPx / 2f, size.height / 2f)
                            val fieldLeft = if (rtl) size.width - fieldWidth else 0f
                            val lightRadius = size.width - fieldWidth / 2f - buttonPx / 2f
                            val field = Path().apply {
                                addRoundRect(RoundRect(Rect(fieldLeft, 0f, fieldLeft + fieldWidth, size.height), CornerRadius(size.height / 2f)))
                            }
                            val reach = Path().apply { addOval(Rect(buttonCenter, lightRadius)) }
                            val lightPath = Path().apply { op(reach, field, PathOperation.Intersect) }
                            onDrawWithContent {
                                drawContent()
                                // dispatchDraw: the button's inner shadow, border and light, over the children.
                                val scale = buttonScale(press.value)
                                val radius = buttonPx / 2f * scale
                                val circle = Path().apply { addOval(Rect(buttonCenter, radius)) }
                                drawLockInnerShadow(shadows, circle, Offset(buttonCenter.x - radius, buttonCenter.y - radius), scale)
                                drawLockInnerBorder(circle)
                                if (light.inner.value > 0f) {
                                    drawLockLightEffect(
                                        center = buttonCenter,
                                        lightPath = lightPath,
                                        lightRadius = lightRadius,
                                        innerRadius = buttonPx / 2f,
                                        scale = scale,
                                        innerAlpha = light.inner.value,
                                        outerAlpha = light.outer.value,
                                    )
                                }
                            }
                        }
                    } else {
                        Modifier
                    },
                ),
        ) { measurables, constraints ->
            val height = LockScreenPasswordInputDefaults.Height.roundToPx()
            val button = height
            val margin = LockScreenPasswordInputDefaults.ButtonMargin.roundToPx()
            val width = if (constraints.hasBoundedWidth) {
                constraints.maxWidth
            } else {
                (LockScreenPasswordInputDefaults.FieldWidth + LockScreenPasswordInputDefaults.ButtonMargin + LockScreenPasswordInputDefaults.Height).roundToPx()
            }
            val fieldWidth = (width - button - margin).coerceAtLeast(0)
            val field = measurables[0].measure(Constraints.fixed(fieldWidth, height))
            val next = measurables[1].measure(Constraints.fixed(button, button))
            layout(width, height) {
                field.placeRelative(0, 0)
                next.placeRelative(width - button, 0)
            }
        }
        AnimatedVisibility(
            visible = isError,
            enter = fadeIn(tween(durationMillis = ERROR_APPEAR_MS, easing = PwdEase)),
            exit = fadeOut(tween(durationMillis = ERROR_DISAPPEAR_MS, easing = PwdEase)),
        ) {
            Text(
                text = displayedError,
                fontSize = LockScreenPasswordInputDefaults.ErrorFontSize,
                color = colors.errorColor,
                modifier = Modifier.padding(
                    top = LockScreenPasswordInputDefaults.ErrorPaddingTop,
                    start = LockScreenPasswordInputDefaults.ErrorPaddingHorizontal,
                    end = LockScreenPasswordInputDefaults.ErrorPaddingHorizontal,
                ),
            )
        }
    }
}

/** `COUIPressFeedbackHelper` on the 48dp next button. */
private fun buttonScale(fraction: Float): Float {
    val size = LockScreenPasswordInputDefaults.Height.value
    return 1f + (COUIMaterialEffect.pressTargetScale(size, size) - 1f) * fraction
}

/** `lock_screen_pwd_card`: the capsule holding the bare edit text and the eye toggle. */
@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    desktop: Boolean,
    enabled: Boolean,
    label: String,
    isError: Boolean,
    showPasswordToggle: Boolean,
    colors: LockScreenPasswordInputColors,
    shadowContext: ShadowContext,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.fieldColor)
            .then(
                if (desktop) {
                    Modifier.drawWithCache {
                        val outline: (Size) -> Path = { s ->
                            Path().apply { addRoundRect(RoundRect(Rect(Offset.Zero, s), CornerRadius(s.height / 2f))) }
                        }
                        val shadows = recordLockInnerShadow(size, shadowContext, outline)
                        val path = outline(size)
                        onDrawWithContent {
                            // COUILockScreenPwdInputView.onDraw: border, then the inner shadow.
                            drawLockInnerBorder(path)
                            drawLockInnerShadow(shadows, path, Offset.Zero)
                            drawContent()
                        }
                    }
                } else {
                    Modifier
                },
            )
            .padding(horizontal = LockScreenPasswordInputDefaults.FieldPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            backgroundMode = TextFieldMode.None,
            insideMargin = DpSize(0.dp, LockScreenPasswordInputDefaults.FieldPaddingVertical),
            colors = TextFieldDefaults.textFieldColors(
                labelColor = colors.hintColor,
                iconColor = colors.iconColor,
            ),
            label = label,
            enabled = enabled,
            isError = isError,
            showPasswordToggle = showPasswordToggle,
            textStyle = TextFieldDefaults.textStyle(TextFieldMode.None).merge(TextStyle(color = colors.textColor)),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
        )
    }
}

/** `iv_intput_next`: the round next button with its arrow. */
@Composable
private fun NextButton(
    allow: Boolean,
    desktop: Boolean,
    colors: LockScreenPasswordInputColors,
    interactionSource: MutableInteractionSource,
    contentDescription: String,
    pressScale: () -> Float,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(LockScreenPasswordInputDefaults.Height)
            .graphicsLayer {
                val scale = if (desktop) pressScale() else 1f
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(colors.buttonColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = NextArrowIcon,
            contentDescription = null,
            tint = if (allow) colors.activeIconColor else colors.iconColor,
            modifier = Modifier.size(LockScreenPasswordInputDefaults.IconSize),
        )
    }
}

/** Contains the default values used by [LockScreenPasswordInput]. */
object LockScreenPasswordInputDefaults {
    /** The field and button height (`coui_input_lock_screen_pwd_height`). */
    val Height: Dp = 48.dp

    /** The gap between the field and the button (`coui_input_lock_screen_pwd_icon_margin`). */
    val ButtonMargin: Dp = 12.dp

    /**
     * The field width used when the width is unbounded
     * (`coui_input_lock_screen_pwd_setting_width`).
     */
    val FieldWidth: Dp = 268.dp

    /** The horizontal padding inside the field (`coui_input_lock_screen_pwd_card_padding`). */
    val FieldPadding: Dp = 16.dp

    /**
     * The vertical padding of the edit text
     * (`coui_input_lock_screen_pwd_title_padding_top / _bottom`).
     */
    val FieldPaddingVertical: Dp = 10.5.dp

    /** The arrow size (`coui_input_lock_screen_pwd_next_src`). */
    val IconSize: Dp = 24.dp

    /** The size of the error line (`couiTextAppearanceCaption`). */
    val ErrorFontSize: TextUnit = 10.sp

    /** The top padding of the error line (`text_input_error` paddingTop). */
    val ErrorPaddingTop: Dp = 8.dp

    /** The horizontal padding of the error line (`text_input_error` paddingStart / End). */
    val ErrorPaddingHorizontal: Dp = 32.dp

    /** `COUILockScreenPwdInputView.DEFAULT_MIN_INPUT_COUNT`. */
    val MinCount: Int = 6

    /** `COUILockScreenPwdInputView.DEFAULT_MAX_INPUT_COUNT`. */
    val MaxCount: Int = 16

    /**
     * The fill of the desktop field and button when no blur is available
     * (`coui_input_lock_screen_pwd_view_bg_color_desktop`, #33FFFFFF).
     */
    val DesktopFallbackColor: Color = Color(0x33FFFFFF)

    /**
     * The default colors for [mode].
     *
     * - [LockScreenPasswordInputMode.Setting]: field `?couiColorCard` and button
     *   `?couiColorCardBackground` (`surfaceContainer`), arrow `?couiColorLabelTertiary`
     *   (`onSurfaceVariantActions`), active arrow `?couiColorPrimaryNeutral` (`onSurface`). The
     *   field and the button are card-colored: place them on the page surface
     *   (`COUITheme.colorScheme.surface`), not on a card.
     * - [LockScreenPasswordInputMode.Desktop]: transparent field and button, white text and eye
     *   (`coui_input_lock_screen_pwd_view_edittext_text_color_desktop`, `coui_btn_eye_*_desktop`),
     *   arrow `coui_color_label_tertiary_dark` #4DFFFFFF, active arrow #FFFFFF.
     *
     * The error line is `?couiColorError` in both.
     */
    @Composable
    fun colors(mode: LockScreenPasswordInputMode = LockScreenPasswordInputMode.Setting): LockScreenPasswordInputColors {
        val scheme = COUITheme.colorScheme
        return remember(mode, scheme.surfaceContainer, scheme.onSurface, scheme.onSurfaceVariantActions, scheme.onSurfaceSecondary, scheme.error) {
            if (mode == LockScreenPasswordInputMode.Desktop) {
                LockScreenPasswordInputColors(
                    fieldColor = Color.Transparent,
                    buttonColor = Color.Transparent,
                    textColor = Color.White,
                    hintColor = DesktopTertiary,
                    iconColor = DesktopTertiary,
                    activeIconColor = Color.White,
                    errorColor = scheme.error,
                )
            } else {
                LockScreenPasswordInputColors(
                    fieldColor = scheme.surfaceContainer,
                    buttonColor = scheme.surfaceContainer,
                    textColor = scheme.onSurface,
                    hintColor = scheme.onSurfaceSecondary,
                    iconColor = scheme.onSurfaceVariantActions,
                    activeIconColor = scheme.onSurface,
                    errorColor = scheme.error,
                )
            }
        }
    }
}

/**
 * Colors used by a [LockScreenPasswordInput].
 *
 * @param fieldColor The fill of the password field.
 * @param buttonColor The fill of the next button.
 * @param textColor The password text.
 * @param hintColor The hint.
 * @param iconColor The inactive next arrow, also used for the eye toggle.
 * @param activeIconColor The active next arrow (`*_next_src_allow`).
 * @param errorColor The error line.
 */
@Immutable
data class LockScreenPasswordInputColors(
    val fieldColor: Color,
    val buttonColor: Color,
    val textColor: Color,
    val hintColor: Color,
    val iconColor: Color,
    val activeIconColor: Color,
    val errorColor: Color,
)

/** `coui_input_lock_screen_pwd_next_src` arrow (24dp, auto-mirrored). */
private val NextArrowIcon: ImageVector by lazy {
    ImageVector.Builder("LockScreenNext", 24.dp, 24.dp, 24f, 24f, autoMirror = true).apply {
        addPath(
            pathData = addPathNodes(
                "M13.06,19.246C12.865,19.442 12.865,19.758 13.06,19.954L13.696,20.59C13.892,20.785 14.208,20.785 14.404,20.59" +
                    "L14.722,20.272L22.322,12.672C22.693,12.301 22.693,11.699 22.322,11.328L14.722,3.728L14.404,3.41" +
                    "C14.208,3.215 13.892,3.215 13.696,3.41L13.06,4.046C12.865,4.242 12.865,4.558 13.06,4.753L13.378,5.072" +
                    "L19.357,11.05L2.35,11.05L1.9,11.05C1.624,11.05 1.4,11.274 1.4,11.55L1.4,12.45C1.4,12.726 1.624,12.95 1.9,12.95" +
                    "L2.35,12.95L19.356,12.95L13.378,18.928L13.06,19.246Z",
            ),
            fill = SolidColor(Color.Black),
            pathFillType = PathFillType.EvenOdd,
        )
    }.build()
}

private val DesktopTertiary = Color(0x4DFFFFFF)

private const val ERROR_APPEAR_MS = 217
private const val ERROR_DISAPPEAR_MS = 283

/** COUIEaseInterpolator (0.33, 0, 0.67, 1), the COUIInputView error fade. */
private val PwdEase = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)
