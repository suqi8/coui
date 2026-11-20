// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.couiPressScale
import io.github.suqi8.coui.kmp.interfaces.HoldDownObserver
import io.github.suqi8.coui.kmp.interfaces.collectIsHeldDownAsState
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import io.github.suqi8.coui.kmp.utils.COUITouchInteraction
import io.github.suqi8.coui.kmp.utils.PressFeedbackType
import io.github.suqi8.coui.kmp.utils.TiltFeedback
import io.github.suqi8.coui.kmp.utils.couiTouchFeedback
import io.github.suqi8.coui.kmp.utils.pressable
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A [Card] component with COUI style.
 * Card contain content and actions that relate information about a subject.
 *
 * This [Card] does not handle input events
 *
 * @param modifier The modifier to be applied to the [Card].
 * @param cornerRadius The corner radius of the [Card].
 * @param insideMargin The margin inside the [Card].
 * @param colors [CardColors] that will be used to resolve the color(s) used for the [Card].
 * @param shadow The [CardShadow] drawn around the [Card] (COUI `COUIShadowCardView`), or `null`
 *   (the default) for a flat card. See [CardDefaults.shadow].
 * @param content The [Composable] content of the [Card].
 */
@Composable
@NonRestartableComposable
fun Card(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CardDefaults.CornerRadius,
    insideMargin: PaddingValues = CardDefaults.InsideMargin,
    colors: CardColors = CardDefaults.defaultColors(),
    shadow: CardShadow? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    BasicCard(
        color = colors.color,
        contentColor = colors.contentColor,
        modifier = modifier,
        cornerRadius = cornerRadius,
        shadow = shadow,
    ) {
        Column(
            modifier = Modifier.padding(insideMargin),
            content = content,
        )
    }
}

/**
 * A [Card] component with COUI style.
 * Card contain contain content and actions that relate information about a subject.
 *
 * This [Card] handles input events
 *
 * @param modifier The modifier to be applied to the [Card].
 * @param cornerRadius The corner radius of the [Card].
 * @param insideMargin The margin inside the [Card].
 * @param colors [CardColors] that will be used to resolve the color(s) used for the [Card].
 * @param shadow The [CardShadow] drawn around the [Card] (COUI `COUIShadowCardView`), or `null`
 *   (the default) for a flat card. The shadow follows the card's press scale.
 * @param pressFeedbackType The press feedback type of the [Card]. Defaults to
 *   [PressFeedbackType.Tint], which animates the fill towards [CardColors.pressedColor].
 *   [PressFeedbackType.Sink] scales the card by the COUI 17 area-based press scale.
 * @param showIndication Whether to show indication of the [Card].
 * @param holdDownState Whether the [Card] is in a hold-down state.
 * @param onClick The callback to be invoked when the [Card] is clicked.
 * @param onLongPress The callback to be invoked when the [Card] is long pressed.
 * @param content The [Composable] content of the [Card].
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CardDefaults.CornerRadius,
    insideMargin: PaddingValues = CardDefaults.InsideMargin,
    colors: CardColors = CardDefaults.defaultColors(),
    shadow: CardShadow? = null,
    pressFeedbackType: PressFeedbackType = PressFeedbackType.Tint,
    showIndication: Boolean = false,
    holdDownState: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongPress by rememberUpdatedState(onLongPress)

    HoldDownObserver(holdDownState, interactionSource)

    val pressFeedback = remember(pressFeedbackType) {
        when (pressFeedbackType) {
            PressFeedbackType.None, PressFeedbackType.Tint, PressFeedbackType.Sink -> null
            PressFeedbackType.Tilt -> TiltFeedback()
        }
    }

    // COUI card press feedback (COUICardListSelectedItemLayout): fill animates towards pressedColor.
    val tintEnabled = pressFeedbackType == PressFeedbackType.Tint
    val isPressed by interactionSource.collectIsPressedAsState()
    val isTouched by interactionSource.collectCardTouchedAsState()
    val isHeldDown by interactionSource.collectIsHeldDownAsState()
    // A single card (COUICardListSelectedItemLayout with consumeDispatchingEventForState) and the
    // COUIPressFeedbackHelper card scale both start on ACTION_DOWN, so the touch drives the feedback;
    // the clickable's press (delayed inside scrolling containers) adds keyboard presses.
    val pressActive = isPressed || isTouched || isHeldDown
    val tintProgress = remember { Animatable(0f) }
    LaunchedEffect(tintEnabled, pressActive) {
        if (!tintEnabled) {
            tintProgress.snapTo(0f)
        } else if (pressActive) {
            tintProgress.animateTo(1f, CardPressFeedbackSpring)
        } else if (tintProgress.value > 0f) {
            if (tintProgress.value < PressedTintMinVisibleProgress) {
                // Keep the press-in animation running until the tint is clearly visible.
                try {
                    tintProgress.animateTo(1f, CardPressFeedbackSpring) {
                        if (value >= PressedTintMinVisibleProgress) throw CardPressTintThresholdReached()
                    }
                } catch (_: CardPressTintThresholdReached) {
                    // Minimum visible progress reached; fall through to the fade-out.
                }
            }
            tintProgress.animateTo(0f, CardPressFeedbackSpring)
        }
    }

    // COUI 17 touch motion press scale: the target scale follows the card's own dp size.
    val sinkEnabled = pressFeedbackType == PressFeedbackType.Sink
    val pressScaleModifier = if (sinkEnabled) {
        Modifier.couiPressScale(pressed = pressActive)
    } else {
        Modifier
    }

    val usedInteractionSource = if (pressFeedback != null || sinkEnabled) interactionSource else null
    val indicationToUse = if (showIndication) LocalIndication.current else null

    val hasOnClick = onClick != null
    val hasLongPress = onLongPress != null
    val isClickable = hasOnClick || hasLongPress
    val clickableModifier = remember(isClickable, hasLongPress, interactionSource, indicationToUse) {
        if (isClickable) {
            Modifier
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = indicationToUse,
                    onClick = { currentOnClick?.invoke() },
                    onLongClick = if (hasLongPress) {
                        { currentOnLongPress?.invoke() }
                    } else {
                        null
                    },
                )
                // Touch down / up / a scroll taking over, as COUITouchInteraction (no second press).
                .couiTouchFeedback(interactionSource)
        } else {
            Modifier
        }
    }

    BasicCard(
        color = colors.color,
        contentColor = colors.contentColor,
        modifier = modifier
            .then(pressScaleModifier)
            .pressable(
                interactionSource = usedInteractionSource,
                indication = pressFeedback,
                delay = null,
            ),
        cornerRadius = cornerRadius,
        shadow = shadow,
        pressedColor = if (tintEnabled) colors.pressedColor else colors.color,
        pressProgress = { tintProgress.value },
    ) {
        Column(
            modifier = Modifier
                .then(clickableModifier)
                .padding(insideMargin),
            content = content,
        )
    }
}

/**
 * A [BasicCard] component.
 *
 * @param color The background color of the [BasicCard].
 * @param contentColor The content color of the [BasicCard].
 * @param modifier The modifier to be applied to the [BasicCard].
 * @param cornerRadius The corner radius of the [BasicCard].
 * @param shadow The optional [CardShadow] drawn around the [BasicCard].
 * @param pressedColor The fill the card moves towards as [pressProgress] goes to 1; equal to
 *   [color] for a card without press tint.
 * @param pressProgress The press tint progress, read only while drawing so its animation does not
 *   recompose the card.
 * @param content The [Composable] content of the [BasicCard].
 */
@Composable
private fun BasicCard(
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CardDefaults.CornerRadius,
    shadow: CardShadow? = null,
    pressedColor: Color = color,
    pressProgress: () -> Float = { 0f },
    content: @Composable () -> Unit,
) {
    val shadowModifier = if (shadow != null && shadow.size > 0.dp) {
        val squircleEnabled = isSquircleEnabled()
        remember(shadow, cornerRadius, squircleEnabled) {
            val shape = if (squircleEnabled) {
                SquircleShape(cornerRadius, COUIRoundCorner.Normal.Weight)
            } else {
                RoundedCornerShape(cornerRadius)
            }
            Modifier.cardShadow(shadow, shape)
        }
    } else {
        Modifier
    }
    // A tinted card draws its own fill inside the squircle mask, so the press animation only
    // redraws it; translucent fills are interpolated, not stacked.
    val tinted = pressedColor != color
    val fillModifier = if (tinted) {
        Modifier.drawBehind {
            val progress = pressProgress().coerceIn(0f, 1f)
            drawRect(if (progress > 0f) lerp(color, pressedColor, progress) else color)
        }
    } else {
        Modifier
    }
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
    ) {
        Box(
            modifier = modifier
                .semantics(mergeDescendants = false) {
                    isTraversalGroup = true
                }
                .then(shadowModifier)
                .squircleSurface(
                    color = if (tinted) Color.Transparent else color,
                    cornerRadius = cornerRadius,
                    weight = COUIRoundCorner.Normal.Weight,
                )
                .then(fillModifier),
            propagateMinConstraints = true,
        ) {
            content()
        }
    }
}

/**
 * The position of a row inside a card group, mirroring COUI's `COUICardListHelper` constants
 * (`NONE = 0`, `HEAD = 1`, `MIDDLE = 2`, `TAIL = 3`, `FULL = 4`).
 *
 * COUI's `COUICardListSelectedItemLayout.setPadding` uses the position to decide which of the card's
 * outer edges get an extra [CardListPosition.HeadOrTailPadding] of vertical padding, and grows the
 * row's minimum height by the same amount. Only the edges that are visually rounded receive it, so a
 * standalone row (both edges rounded) gains the padding twice while a middle row gains none.
 *
 * @see cardListPositionOf
 */
enum class CardListPosition {
    /** Not part of a card group; no rounded edges and no extra padding. */
    None,

    /** First row of a multi-row group; the top edge is rounded. */
    Head,

    /** A row with a sibling on both sides; neither edge is rounded. */
    Middle,

    /** Last row of a multi-row group; the bottom edge is rounded. */
    Tail,

    /** The only row of the group; both edges are rounded. */
    Full,
    ;

    /** The extra top padding contributed by this position. */
    internal val extraPaddingTop: Dp
        get() = if (this == Head || this == Full) HeadOrTailPadding else 0.dp

    /** The extra bottom padding contributed by this position. */
    internal val extraPaddingBottom: Dp
        get() = if (this == Tail || this == Full) HeadOrTailPadding else 0.dp

    companion object {
        /**
         * The padding COUI adds to each rounded outer edge of a card group
         * (COUI `coui_list_card_head_or_tail_padding`).
         */
        val HeadOrTailPadding = 2.dp
    }
}

/**
 * Resolves the [CardListPosition] of the row at [index] within a card group of [count] rows,
 * mirroring COUI's `COUICardListHelper.getPositionInGroup(int, int)`.
 *
 * @param index The zero-based index of the row within the group.
 * @param count The total number of rows in the group.
 */
@Stable
fun cardListPositionOf(index: Int, count: Int): CardListPosition = when {
    count <= 0 || index !in 0 until count -> CardListPosition.None
    count == 1 -> CardListPosition.Full
    index == 0 -> CardListPosition.Head
    index == count - 1 -> CardListPosition.Tail
    else -> CardListPosition.Middle
}

object CardDefaults {

    /**
     * The default corner radius of the [Card]: the ColorOS 17 card list radius
     * ([COUIRoundCorner.Normal.XL], COUI coui_round_corner_xl_radius_normal_16_1), which
     * `COUICardListSelectedItemLayout.getInnerRadius` uses whenever smooth corners are on.
     */
    val CornerRadius = COUIRoundCorner.Normal.XL

    /**
     * The default margin inside the [Card].
     */
    val InsideMargin = PaddingValues(0.dp)

    /**
     * The default shadow size of a shadowed [Card] (COUI `support_shadow_size_level_for_shadow_card`,
     * the `couiShadowSize` the ColorOS clock gives its `COUIShadowCardView`).
     */
    val ShadowSize = 12.dp

    /**
     * The default shadow color. `COUIShadowCardView` hands it to the compat shadow renderer, which
     * keeps only its RGB (ColorOS clock `alarm_snooze_fluid_shadow_bg` #1F000000, i.e. black).
     */
    val ShadowColor = Color.Black

    /**
     * The default shadow offset along [CardShadow.angle] (COUIShadowCardView `couiShadowOffset`
     * falls back to 7px, 2dp at the 3.5x reference density).
     */
    val ShadowOffset = 2.dp

    /**
     * Creates a [CardShadow], after COUI `COUIShadowCardView`.
     *
     * @param size The shadow size (`couiShadowSize`, the compat elevation).
     * @param color The shadow color; only its RGB is used (`couiShadowColor`).
     * @param offset How far the shadow is shifted along [angle] (`couiShadowOffset`).
     * @param angle The direction of [offset] in degrees, 0 pointing down and 90 to the right
     *   (`couiShadowAngle`, MaterialShapeDrawable `shadowCompatRotation`).
     * @param hideLeft Whether no shadow is drawn past the left edge (`couiHideLeftShadow`).
     * @param hideTop Whether no shadow is drawn past the top edge (`couiHideTopShadow`).
     * @param hideRight Whether no shadow is drawn past the right edge (`couiHideRightShadow`).
     * @param hideBottom Whether no shadow is drawn past the bottom edge (`couiHideBottomShadow`).
     */
    fun shadow(
        size: Dp = ShadowSize,
        color: Color = ShadowColor,
        offset: Dp = ShadowOffset,
        angle: Int = 0,
        hideLeft: Boolean = false,
        hideTop: Boolean = false,
        hideRight: Boolean = false,
        hideBottom: Boolean = false,
    ): CardShadow = CardShadow(
        size = size,
        color = color,
        offset = offset,
        angle = angle,
        hideLeft = hideLeft,
        hideTop = hideTop,
        hideRight = hideRight,
        hideBottom = hideBottom,
    )

    /**
     * The default colors width of the [Card].
     */
    @Composable
    fun defaultColors(
        color: Color = COUITheme.colorScheme.cardBackground,
        contentColor: Color = COUITheme.colorScheme.labelPrimary,
        pressedColor: Color = COUITheme.colorScheme.cardPressed,
    ): CardColors = remember(color, contentColor, pressedColor) {
        CardColors(
            color = color,
            contentColor = contentColor,
            pressedColor = pressedColor,
        )
    }
}

/**
 * Colors used by a [Card].
 *
 * @param color The background color of the card.
 * @param contentColor The content color of the card.
 * @param pressedColor The fill color the card animates towards while pressed (couiColorCardPressed).
 */
@Immutable
data class CardColors(
    val color: Color,
    val contentColor: Color,
    val pressedColor: Color,
)

/**
 * The shadow of a [Card], after COUI `COUIShadowCardView`: a `MaterialShapeDrawable` background in
 * `SHADOW_COMPAT_MODE_ALWAYS` whose compat shadow is drawn by `ShadowRenderer`.
 *
 * The compat shadow spreads `ceil(0.75 x size)` past the outline as a 68/255 -> 20/255 -> 0 alpha
 * ramp of the shadow RGB and is shifted by [offset] along [angle]. It is drawn here as a Gaussian
 * drop shadow fitted to that ramp: blur radius 0.62 x [size] at alpha 136/255, which meets 68/255 at
 * the outline and 20/255 half way out.
 *
 * @param size The shadow size (`couiShadowSize`); 0 draws no shadow, as in COUI.
 * @param color The shadow color; its alpha is ignored, as `ShadowRenderer.setShadowColor` replaces it.
 * @param offset How far the shadow is shifted along [angle] (`couiShadowOffset`).
 * @param angle The direction of [offset] in degrees, 0 pointing down (`couiShadowAngle`).
 * @param hideLeft Whether no shadow is drawn past the left edge (`couiHideLeftShadow`).
 * @param hideTop Whether no shadow is drawn past the top edge (`couiHideTopShadow`).
 * @param hideRight Whether no shadow is drawn past the right edge (`couiHideRightShadow`).
 * @param hideBottom Whether no shadow is drawn past the bottom edge (`couiHideBottomShadow`).
 */
@Immutable
data class CardShadow(
    val size: Dp,
    val color: Color,
    val offset: Dp,
    val angle: Int,
    val hideLeft: Boolean,
    val hideTop: Boolean,
    val hideRight: Boolean,
    val hideBottom: Boolean,
)

/**
 * Draws [shadow] around [shape]. A hidden side clips the shadow at the card edge, the counterpart of
 * the zero-length edge and corner treatments `COUIShadowCardView` installs on that side.
 */
private fun Modifier.cardShadow(shadow: CardShadow, shape: Shape): Modifier {
    val radians = shadow.angle * PI / 180.0
    val dropShadow = Shadow(
        radius = shadow.size * CARD_SHADOW_RADIUS_PER_SIZE,
        color = shadow.color.copy(alpha = 1f),
        alpha = CARD_SHADOW_ALPHA,
        offset = DpOffset(x = shadow.offset * sin(radians).toFloat(), y = shadow.offset * cos(radians).toFloat()),
    )
    val clipped = shadow.hideLeft || shadow.hideTop || shadow.hideRight || shadow.hideBottom
    val clip = if (clipped) {
        Modifier.drawWithContent {
            clipRect(
                left = if (shadow.hideLeft) 0f else -CARD_SHADOW_CLIP_REACH,
                top = if (shadow.hideTop) 0f else -CARD_SHADOW_CLIP_REACH,
                right = if (shadow.hideRight) size.width else size.width + CARD_SHADOW_CLIP_REACH,
                bottom = if (shadow.hideBottom) size.height else size.height + CARD_SHADOW_CLIP_REACH,
            ) {
                this@drawWithContent.drawContent()
            }
        }
    } else {
        Modifier
    }
    return this
        .then(clip)
        .dropShadow(shape = shape, shadow = dropShadow)
}

/**
 * The Gaussian blur radius per unit of [CardShadow.size]: `ShadowRenderer` ramps over
 * `0.75 x size`, and a sigma of 0.476 times that ramp reproduces its 20/255 midpoint.
 */
private const val CARD_SHADOW_RADIUS_PER_SIZE = 0.62f

/** Twice `ShadowRenderer.COLOR_ALPHA_START` (68/255): a Gaussian edge sits at half its alpha. */
private const val CARD_SHADOW_ALPHA = 136f / 255f

/** How far past an unhidden side the shadow clip reaches, in pixels. */
private const val CARD_SHADOW_CLIP_REACH = 100_000f

/**
 * Whether a finger is on the card, from the [COUITouchInteraction]s that [couiTouchFeedback] reports
 * from the touch down to the lift or to a scroll taking the gesture over.
 */
@Composable
private fun InteractionSource.collectCardTouchedAsState(): State<Boolean> {
    val touched = remember { mutableStateOf(false) }
    LaunchedEffect(this) {
        val touches = mutableListOf<COUITouchInteraction.Enter>()
        interactions.collect { interaction ->
            when (interaction) {
                is COUITouchInteraction.Enter -> touches.add(interaction)
                is COUITouchInteraction.Exit -> touches.remove(interaction.enter)
            }
            touched.value = touches.isNotEmpty()
        }
    }
    return touched
}

/** The COUI card press feedback spring (COUISpringForce response 0.3f / bounce 0f). */
private val CardPressFeedbackSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/** The minimum press tint progress before a release may fade out (COUIMaskEffectDrawable). */
private val PressedTintMinVisibleProgress = 0.7f

/** Thrown to stop the deferred press-in tint animation once [PressedTintMinVisibleProgress] is reached. */
private class CardPressTintThresholdReached : CancellationException("Press tint reached the minimum visible progress")
