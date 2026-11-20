// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.FrameworkEdgeBlendMode
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.ShadowEdgePresets
import io.github.suqi8.coui.kmp.effect.ShadowEdgeShader
import io.github.suqi8.coui.kmp.effect.SpotLightShader
import io.github.suqi8.coui.kmp.effect.SpotLightState
import io.github.suqi8.coui.kmp.effect.StrokeEffectParams
import io.github.suqi8.coui.kmp.effect.couiPressScale
import io.github.suqi8.coui.kmp.effect.splitFrameworkStroke
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.squircleClip
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import io.github.suqi8.coui.kmp.utils.graphicsLayerWithOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * A [ChipTab] component with COUI style: one chip of a ColorOS 17 chip tab bar
 * (`COUIChipTab`, a `COUIChip` drawn by `COUIChipTabDrawable`).
 *
 * Unlike [Chip], a chip tab never shows an accent color: both states keep the primary label, and
 * the selection is told by the capsule itself. The unselected capsule is a faint translucent
 * tint; the selected one rises into an elevated surface with a soft light-source shadow. The two
 * states cross-fade on the `CheckedLayerAlphaAnimation` / `UncheckedLayerAlphaAnimation` springs
 * (response 0.2, bounce 0), which also drive the material, the edge light and the shadow.
 *
 * With [materialEffect] on (ColorOS 17's `FOLLOW_SYSTEM` with every material switch enabled) the
 * chip draws:
 * - **Backdrop**: `COUIMaterialEffect.setViewBlurEffect` between the unselected and the selected
 *   `COUIMaterialBlurEffect` presets of [materialType] (see [ChipTabDefaults.backdropSpec]). The
 *   backdrop replaces the fill, so it is drawn only when a [LocalMaterialBackdrop] provider is
 *   present; coui-ui has no backdrop capture of its own. Without a provider the chip keeps the
 *   fill `COUIChipTabDrawable.drawDualLayerBackground` paints whenever the blur is off, which is
 *   COUI's own non-blur look.
 * - **Edge light + inner shadow**: the `TYPE_NO_EFFECT_BASE_1` -> `TYPE_FRAMEWORK_CAPSULE_2`
 *   stroke effect, interpolated by the selection (an unselected chip has none).
 * - **Touch spotlight**: `COUISpotLightEffect.TYPE_CHIP_TAB`, which replaces the press and hover
 *   masks.
 *
 * Without the material the selected chip gets the 0.6dp checked stroke and pressing or hovering
 * tints the capsule with `press` / `hover` (`COUIChipDrawable.drawStateEffects`). Either way
 * pressing scales the chip like `COUIChip` (the size-dependent COUI 17 press scale) and keyboard
 * focus draws the 3dp `focusOutline` ring.
 *
 * Clicking a chip tab only reports [onClick]; deselection is up to the caller (`COUIChipTab.toggle`
 * refuses to uncheck a selected chip). Use [ChipTabRow] for the single-selection scrolling row.
 *
 * @param selected Whether the [ChipTab] is selected.
 * @param onClick The callback when the [ChipTab] is clicked.
 * @param label The text label of the [ChipTab]. Single line, ellipsized beyond
 *   [ChipTabDefaults.TextMaxWidth].
 * @param modifier The modifier to be applied to the [ChipTab].
 * @param enabled Whether the [ChipTab] is enabled. A disabled chip tab keeps its capsule and greys
 *   its label and icon (`COUIChipTab` checked / unchecked disabled text and icon tints).
 * @param materialEffect Whether the COUI 17 material is drawn, see above. Defaults to
 *   [LocalCOUIMaterialEffectEnabled].
 * @param materialType Which `COUIChipTab.MaterialEffectType` presets the backdrop takes.
 * @param icon The optional leading icon, laid out in a [ChipTabDefaults.IconSize] box.
 *   [LocalContentColor] carries the label color, the `checkedChipIconTint` /
 *   `uncheckedChipIconTint` of the style.
 * @param cornerRadius The corner radius of the [ChipTab]. `COUIChipTab` fixes it at 16dp, a capsule
 *   at the default height, and clips to the Oplus smooth round rect of weight 1.0.
 * @param minWidth The minimum width of the [ChipTab].
 * @param minHeight The minimum height of the [ChipTab].
 * @param maxWidth The maximum width of the [ChipTab].
 * @param colors The [ChipTabColors] of the [ChipTab].
 * @param insideMargin The horizontal padding inside the [ChipTab].
 * @param interactionSource The [MutableInteractionSource] to be used for the [ChipTab].
 */
@Composable
fun ChipTab(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
    materialType: ChipTabMaterialType = ChipTabMaterialType.Default,
    icon: (@Composable () -> Unit)? = null,
    cornerRadius: Dp = ChipTabDefaults.CornerRadius,
    minWidth: Dp = ChipTabDefaults.MinWidth,
    minHeight: Dp = ChipTabDefaults.MinHeight,
    maxWidth: Dp = ChipTabDefaults.MaxWidth,
    colors: ChipTabColors = ChipTabDefaults.chipTabColors(),
    insideMargin: PaddingValues = ChipTabDefaults.InsideMargin,
    interactionSource: MutableInteractionSource? = null,
) {
    ChipTabSurface(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        materialEffect = materialEffect,
        materialType = materialType,
        cornerRadius = cornerRadius,
        colors = colors,
        interactionSource = interactionSource,
    ) { contentColor ->
        Row(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                .padding(insideMargin),
            horizontalArrangement = Arrangement.spacedBy(ChipTabDefaults.IconSpacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier.size(ChipTabDefaults.IconSize),
                    contentAlignment = Alignment.Center,
                ) {
                    icon()
                }
            }
            Text(
                text = label,
                color = contentColor,
                style = ChipTabDefaults.textStyle(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = ChipTabDefaults.TextMaxWidth),
            )
        }
    }
}

/**
 * An icon-only [ChipTab]: `COUIChipTab.setShowIconOnly(true)`, a [ChipTabDefaults.IconOnlySize]
 * circle (32dp, corner radius 16dp) around the icon. It draws, selects and animates exactly like
 * the labelled [ChipTab].
 *
 * @param selected Whether the [ChipTab] is selected.
 * @param onClick The callback when the [ChipTab] is clicked.
 * @param icon The icon, laid out in an [iconSize] box and tinted through [LocalContentColor].
 * @param modifier The modifier to be applied to the [ChipTab].
 * @param enabled Whether the [ChipTab] is enabled.
 * @param materialEffect Whether the COUI 17 material is drawn. Defaults to
 *   [LocalCOUIMaterialEffectEnabled].
 * @param materialType Which `COUIChipTab.MaterialEffectType` presets the backdrop takes.
 * @param size The diameter of the [ChipTab].
 * @param iconSize The size of the icon box. `COUIChipTab` keeps the 16dp `chipIconSize`;
 *   `COUIChipTabLayout` enlarges its leading chip to [ChipTabDefaults.LeadingIconSize].
 * @param colors The [ChipTabColors] of the [ChipTab].
 * @param interactionSource The [MutableInteractionSource] to be used for the [ChipTab].
 */
@Composable
fun ChipTab(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
    materialType: ChipTabMaterialType = ChipTabMaterialType.Default,
    size: Dp = ChipTabDefaults.IconOnlySize,
    iconSize: Dp = ChipTabDefaults.IconSize,
    colors: ChipTabColors = ChipTabDefaults.chipTabColors(),
    interactionSource: MutableInteractionSource? = null,
) {
    ChipTabSurface(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        materialEffect = materialEffect,
        materialType = materialType,
        cornerRadius = size / 2,
        colors = colors,
        interactionSource = interactionSource,
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.size(iconSize),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
        }
    }
}

/**
 * A single-selection, horizontally scrolling row of [ChipTab]s with COUI style: ColorOS 17's
 * `COUIChipTabLayout` (a `ChipMaskHorizontalScrollView` holding a `COUIChipTabGroup`, with an
 * optional leading icon-only chip).
 *
 * - The chips sit 8dp apart (`chipSpacingHorizontal`) with 8dp above and below them; the scroll
 *   view is inset 16dp on both sides, 8dp once the leading chip is shown
 *   (`COUIChipTabLayout.setIconOnlyChipVisible`).
 * - While the row can scroll towards an edge the chips are cut at the inset there and faded out
 *   over 12dp (`ChipMaskHorizontalScrollView`, a DST_OUT gradient in an offscreen layer that
 *   reaches past the row vertically so the selected chip's shadow survives).
 * - Clicking a chip selects it (clicking the selected chip changes nothing:
 *   `COUIChipTabGroup.setSelectedChip`), and a chip cut by the visible edge is scrolled into view
 *   on a response 0.4 / bounce 0.2 spring (`scrollToChipTabWithAnimation`).
 *
 * @param tabs The labels of the chips.
 * @param selectedTabIndex The index of the selected chip; any other value selects none.
 * @param onTabSelected The callback when an unselected chip is clicked.
 * @param modifier The modifier to be applied to the [ChipTabRow].
 * @param enabled Whether the chips are enabled.
 * @param materialEffect Whether the chips draw the COUI 17 material, see [ChipTab]. Defaults to
 *   [LocalCOUIMaterialEffectEnabled].
 * @param materialType Which `COUIChipTab.MaterialEffectType` presets the chips take.
 * @param colors The [ChipTabColors] of the chips.
 * @param horizontalPadding The inset of the scrolling chips. [Dp.Unspecified] uses the
 *   `COUIChipTabLayout` value: 16dp, or 8dp when [leadingIcon] is shown.
 * @param itemSpacing The spacing between two chips.
 * @param scrollState The [ScrollState] of the chips.
 * @param onLeadingIconClick The callback when the leading icon chip is clicked.
 * @param leadingIcon The optional icon of the leading icon-only chip, which `COUIChipTabLayout`
 *   keeps selected, 16dp from the start edge, with a [ChipTabDefaults.LeadingIconSize] icon.
 */
@Composable
fun ChipTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
    materialType: ChipTabMaterialType = ChipTabMaterialType.Default,
    colors: ChipTabColors = ChipTabDefaults.chipTabColors(),
    horizontalPadding: Dp = Dp.Unspecified,
    itemSpacing: Dp = ChipTabDefaults.ItemSpacing,
    scrollState: ScrollState? = null,
    onLeadingIconClick: () -> Unit = {},
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    @Suppress("NAME_SHADOWING")
    val scrollState = scrollState ?: rememberScrollState()
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)
    val currentSelectedTabIndex by rememberUpdatedState(selectedTabIndex)
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val inset = when {
        horizontalPadding != Dp.Unspecified -> horizontalPadding
        leadingIcon != null -> ChipTabDefaults.RowHorizontalPaddingWithLeadingIcon
        else -> ChipTabDefaults.RowHorizontalPadding
    }
    // Layout handles read only by the click handler, never by composition or drawing.
    val viewport = remember { arrayOfNulls<LayoutCoordinates>(1) }
    val chipCoordinates = remember { HashMap<Int, LayoutCoordinates>() }

    fun revealChip(index: Int) {
        val viewportCoordinates = viewport[0] ?: return
        val chip = chipCoordinates[index] ?: return
        if (!viewportCoordinates.isAttached || !chip.isAttached) return
        val bounds = viewportCoordinates.localBoundingBoxOf(chip, clipBounds = false)
        val width = viewportCoordinates.size.width.toFloat()
        // Edges measured from the start of the row, so RTL takes the same path.
        val chipStart = if (layoutDirection == LayoutDirection.Rtl) width - bounds.right else bounds.left
        val chipEnd = if (layoutDirection == LayoutDirection.Rtl) width - bounds.left else bounds.right
        val edge = with(density) { (inset + ChipTabDefaults.FadingEdgeLength).toPx() }
        val value = scrollState.value
        val maxValue = scrollState.maxValue
        // COUIChipTabGroup.updateContentVisibleRect: a scrolled edge loses the inset and the fade.
        val visibleStart = edge
        val visibleEnd = width - edge
        val delta = when {
            value > 0 && visibleStart > chipStart + 1f && visibleStart < chipEnd - 1f -> chipStart - visibleStart
            value < maxValue && visibleEnd > chipStart + 1f && visibleEnd < chipEnd - 1f -> chipEnd - visibleEnd
            else -> 0f
        }
        if (delta != 0f) {
            val target = (value + delta).roundToInt().coerceIn(0, maxValue)
            scope.launch { scrollState.animateScrollTo(target, ChipTabRevealSpring) }
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            ChipTab(
                selected = true,
                onClick = onLeadingIconClick,
                icon = leadingIcon,
                modifier = Modifier.padding(
                    start = ChipTabDefaults.RowHorizontalPadding,
                    top = ChipTabDefaults.RowVerticalPadding,
                    bottom = ChipTabDefaults.RowVerticalPadding,
                ),
                enabled = enabled,
                materialEffect = materialEffect,
                materialType = materialType,
                iconSize = ChipTabDefaults.LeadingIconSize,
                colors = colors,
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                // ChipMaskHorizontalScrollView draws into a layer that reaches past the row, so the
                // fades can erase the chips without cutting off the selected chip's shadow.
                .graphicsLayerWithOverflow(ChipTabRowLayerOverflow) {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .onPlaced { viewport[0] = it }
                .drawWithContent {
                    val rtl = layoutDirection == LayoutDirection.Rtl
                    val canScrollStart = scrollState.value > 0
                    val canScrollEnd = scrollState.value < scrollState.maxValue
                    val insetPx = inset.toPx()
                    val fade = ChipTabDefaults.FadingEdgeLength.toPx()
                    val reach = ChipTabRowLayerOverflow.toPx()
                    val startClip = if (canScrollStart) insetPx else -reach
                    val endClip = if (canScrollEnd) size.width - insetPx else size.width + reach
                    val left = if (rtl) size.width - endClip else startClip
                    val right = if (rtl) size.width - startClip else endClip
                    clipRect(left = left, top = -reach, right = right, bottom = size.height + reach) {
                        this@drawWithContent.drawContent()
                    }
                    if (canScrollStart) {
                        val from = if (rtl) size.width - insetPx else insetPx
                        val to = if (rtl) from - fade else from + fade
                        drawRect(
                            brush = Brush.horizontalGradient(listOf(Color.Black, Color.Transparent), startX = from, endX = to),
                            topLeft = Offset(min(from, to), -reach),
                            size = Size(fade, size.height + 2 * reach),
                            blendMode = BlendMode.DstOut,
                        )
                    }
                    if (canScrollEnd) {
                        val from = if (rtl) insetPx else size.width - insetPx
                        val to = if (rtl) from + fade else from - fade
                        drawRect(
                            brush = Brush.horizontalGradient(listOf(Color.Black, Color.Transparent), startX = from, endX = to),
                            topLeft = Offset(min(from, to), -reach),
                            size = Size(fade, size.height + 2 * reach),
                            blendMode = BlendMode.DstOut,
                        )
                    }
                },
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(horizontal = inset, vertical = ChipTabDefaults.RowVerticalPadding)
                    .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEachIndexed { index, label ->
                    ChipTab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            if (index != currentSelectedTabIndex) currentOnTabSelected(index)
                            revealChip(index)
                        },
                        label = label,
                        modifier = Modifier.onPlaced { chipCoordinates[index] = it },
                        enabled = enabled,
                        materialEffect = materialEffect,
                        materialType = materialType,
                        colors = colors,
                    )
                }
            }
        }
    }
}

/**
 * The page under a [ChipTabRow], switched with the ColorOS 17 chip tab page transition
 * (`COUIChipTabGroupFragmentMediator.switchToFragment`): the page that is left fades out on a
 * response 0.2 / bounce 0 spring and is then dropped, while the selected page fades in on a
 * response 0.3 / bounce 0 spring after a 150ms delay (`PAGE_APPEAR_DELAY`). The first page fades
 * in without the delay. A new selection before a switch settles drops the page still fading out at
 * once and fades the page that was appearing out from where it is.
 *
 * Every page keeps its saveable state while it is not shown, as the mediator keeps its fragments.
 *
 * @param selectedTabIndex The page to show, usually the selected chip of the [ChipTabRow].
 * @param modifier The modifier to be applied to the [ChipTabContent].
 * @param content The page for an index.
 */
@Composable
fun ChipTabContent(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int) -> Unit,
) {
    val pages = remember { mutableStateListOf<ChipTabContentPage>() }
    // The page being shown, read only by the switch.
    val current = remember { arrayOfNulls<ChipTabContentPage>(1) }
    val stateHolder = rememberSaveableStateHolder()
    LaunchedEffect(selectedTabIndex) {
        switchChipTabContentPage(pages, current, selectedTabIndex)
    }
    Box(modifier = modifier, propagateMinConstraints = true) {
        pages.forEach { page ->
            key(page.index) {
                Box(
                    modifier = Modifier.graphicsLayer { alpha = page.alpha.value },
                    propagateMinConstraints = true,
                ) {
                    stateHolder.SaveableStateProvider(page.index) {
                        content(page.index)
                    }
                }
            }
        }
    }
}

/** One page of a [ChipTabContent] and its alpha. */
private class ChipTabContentPage(val index: Int) {
    val alpha = Animatable(0f, visibilityThreshold = CHIP_TAB_PAGE_MIN_VISIBLE_ALPHA)
}

/**
 * `COUIChipTabGroupFragmentMediator.switchToFragment`: shows page [index] in [pages], fading the
 * page in [current] out and the new one in. Runs until both fades have finished; a newer switch
 * cancels it and ends the old fades itself.
 */
private suspend fun switchChipTabContentPage(
    pages: MutableList<ChipTabContentPage>,
    current: Array<ChipTabContentPage?>,
    index: Int,
) {
    val previous = current[0]
    if (previous?.index == index) return
    // The fades of an earlier switch end now (cancelling a COUISpringAnimation runs its end
    // listener): a page that was still fading out is hidden at once.
    pages.removeAll { it !== previous && it.index != index }
    val next = pages.firstOrNull { it.index == index } ?: ChipTabContentPage(index).also { pages.add(it) }
    current[0] = next
    next.alpha.snapTo(0f)
    if (previous == null) {
        next.alpha.animateTo(1f, ChipTabPageFadeInSpring)
        return
    }
    coroutineScope {
        launch {
            // A page that never became visible is hidden without a fade.
            if (previous.alpha.value > 0f) previous.alpha.animateTo(0f, ChipTabPageFadeOutSpring)
            pages.remove(previous)
        }
        delay(CHIP_TAB_PAGE_APPEAR_DELAY_MILLIS)
        next.alpha.animateTo(1f, ChipTabPageFadeInSpring)
    }
}

/**
 * The material presets of a [ChipTab]: `COUIChipTab.MaterialEffectType`, which picks the
 * `COUIMaterialBlurEffect` types the backdrop interpolates between.
 */
enum class ChipTabMaterialType {
    /** `TYPE_DEFAULT`: `TYPE_CONTENT_CHIP_TAB` / `TYPE_CONTENT_CHIP_TAB_SELECTED`, a 40px blur. */
    Default,

    /**
     * `TYPE_DEFAULT_NO_BLUR`: `TYPE_CONTENT_CHIP_TAB_NO_BLUR` /
     * `TYPE_CONTENT_CHIP_TAB_SELECTED_NO_BLUR`, the same colour filters over an unblurred backdrop.
     */
    NoBlur,
}

/** Contains default values used by [ChipTab] and [ChipTabRow]. */
object ChipTabDefaults {
    /** The minimum width of a chip tab (COUI coui_chip_default_min_width). */
    val MinWidth = 52.dp

    /** The minimum height of a chip tab (COUI coui_chip_tab_style_height). */
    val MinHeight = 32.dp

    /** The maximum width of a chip tab (COUI coui_chip_default_max_width). */
    val MaxWidth = 300.dp

    /** The maximum width of the label (COUI chipTextMaxWidth, coui_chip_default_max_text_width). */
    val TextMaxWidth = 200.dp

    /** The corner radius of a chip tab (`COUIChipTab` `setChipCornerRadius(16dp)`). */
    val CornerRadius = 16.dp

    /** The size of the leading icon (COUI coui_chip_tab_style_chip_icon_size). */
    val IconSize = 16.dp

    /** The spacing between the icon and the label (COUI coui_chip_suggest_style_chip_icon_end_padding). */
    val IconSpacing = 4.dp

    /** The horizontal padding inside a chip tab (COUI coui_chip_suggest_style_chip_horizontal_padding). */
    val InsideMargin = PaddingValues(horizontal = 12.dp)

    /** The diameter of an icon-only chip tab (`COUIChipTab.ICON_ONLY_DIAMETER_DP`). */
    val IconOnlySize = 32.dp

    /** The icon size of the leading chip of a [ChipTabRow] (`COUIChipTabLayout.ICON_SIZE_20`). */
    val LeadingIconSize = 20.dp

    /** The spacing between two chips of a [ChipTabRow] (layout_chip_tab `chipSpacingHorizontal`). */
    val ItemSpacing = 8.dp

    /** The space above and below the chips of a [ChipTabRow] (`COUIChipTabGroup` paddingVertical). */
    val RowVerticalPadding = 8.dp

    /** The inset of the chips of a [ChipTabRow] (`COUIChipTabLayout.PADDING_HORIZONTAL_16`). */
    val RowHorizontalPadding = 16.dp

    /**
     * The inset of the chips of a [ChipTabRow] next to its leading chip
     * (`COUIChipTabLayout.PADDING_HORIZONTAL_WITH_ICON_ONLY_CHIP`).
     */
    val RowHorizontalPaddingWithLeadingIcon = 8.dp

    /** The fade at a scrolled edge of a [ChipTabRow] (`ChipMaskHorizontalScrollView.FADING_EDGE_WIDTH`). */
    val FadingEdgeLength = 12.dp

    /** The label style (Widget.COUI.Chip.ChipTab textAppearance couiTextButtonM: 14sp medium). */
    @Composable
    fun textStyle(): TextStyle = COUITheme.textStyles.buttonM

    /**
     * The default [ChipTabColors], the colors `COUIChipTabDrawable.updateCachedThemeColors` and
     * `COUIChipTab.refresh` resolve on ColorOS 17 (API 37):
     * - unselected tint couiColorContainer4 #0A000000 / #1AFFFFFF;
     * - selected surface couiColorBackgroundElevated #FFFFFF / #1E1E1E (above API 36 the unchecked
     *   surface is the same colour at alpha 0, so the surface simply fades in);
     * - selected highlight coui_color_checked_background_gradient_start #40FFFFFF;
     * - selected stroke coui_color_checked_background_stroke #0A000000 / #0AFFFFFF;
     * - label and icon couiColorLabelPrimary #E6000000 / #E6FFFFFF, disabled couiColorLabelTertiary
     *   #42000000 / #4DFFFFFF.
     */
    @Composable
    fun chipTabColors(
        containerColor: Color = COUITheme.colorScheme.container4,
        selectedContainerColor: Color = COUITheme.colorScheme.surfaceContainerHighest,
        selectedHighlightColor: Color = Color(0x40FFFFFF),
        selectedStrokeColor: Color = if (isDarkScheme()) Color(0x0AFFFFFF) else Color(0x0A000000),
        contentColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledContentColor: Color = COUITheme.colorScheme.labelTertiary,
    ): ChipTabColors = remember(
        containerColor,
        selectedContainerColor,
        selectedHighlightColor,
        selectedStrokeColor,
        contentColor,
        disabledContentColor,
    ) {
        ChipTabColors(
            containerColor = containerColor,
            selectedContainerColor = selectedContainerColor,
            selectedHighlightColor = selectedHighlightColor,
            selectedStrokeColor = selectedStrokeColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }

    /**
     * The COUI 17 backdrop of a chip tab at [selectedFraction] of its selection:
     * `COUIMaterialBlurEffect.setViewBlurEffect(view, unselected, selected, selected, fraction)`
     * lerps the blur radius, mixes both colour layers with an `ArgbEvaluator` and keeps the
     * selected blend modes.
     *
     * - [ChipTabMaterialType.Default] light: unselected 40px `#144E4E4E` COLOR_DODGE + `#14606060`
     *   LUMINOSITY, selected 40px `#66A3A1A1` COLOR_DODGE + `#99F5F5F5` LUMINOSITY; dark:
     *   unselected 40px `#33B2B2B2` LUMINOSITY + `#33303030` OVERLAY, selected 40px `#A6505050`
     *   LUMINOSITY + `#4CA7A7A7` OVERLAY.
     * - [ChipTabMaterialType.NoBlur] light: unselected 0px `#9A9B9B9B` COLOR_DODGE + `#9ACECECE`
     *   LUMINOSITY, selected 0px `#66A3A3A3` COLOR_DODGE + `#CEF5F5F5` LUMINOSITY; dark: unselected
     *   0px `#EE474747` LUMINOSITY + `#CE8D8D8D` OVERLAY, selected 0px `#DA888888` LUMINOSITY +
     *   `#80A8A8A8` OVERLAY.
     *
     * @param isDark Whether the dark-scheme presets are used.
     * @param materialType The preset family.
     * @param selectedFraction How far the chip is selected, 0 (unselected) to 1 (selected).
     */
    fun backdropSpec(isDark: Boolean, materialType: ChipTabMaterialType, selectedFraction: Float): MaterialBackdropSpec {
        val (start, end) = when (materialType) {
            ChipTabMaterialType.Default -> if (isDark) ChipTabBlurDark to ChipTabSelectedBlurDark else ChipTabBlurLight to ChipTabSelectedBlurLight
            ChipTabMaterialType.NoBlur -> if (isDark) ChipTabNoBlurDark to ChipTabSelectedNoBlurDark else ChipTabNoBlurLight to ChipTabSelectedNoBlurLight
        }
        val fraction = selectedFraction.coerceIn(0f, 1f)
        if (fraction == 0f) return start
        if (fraction == 1f) return end
        return MaterialBackdropSpec(
            blurRadiusPx = start.blurRadiusPx + (end.blurRadiusPx - start.blurRadiusPx) * fraction,
            layer1 = argbEvaluate(fraction, start.layer1, end.layer1),
            mode1 = end.mode1,
            layer2 = argbEvaluate(fraction, start.layer2, end.layer2),
            mode2 = end.mode2,
        )
    }
}

/**
 * Colors used by a [ChipTab].
 *
 * @param containerColor The tint of an unselected chip (`mCachedColorContainer4`), faded out as the
 *   chip is selected.
 * @param selectedContainerColor The surface of a selected chip (`mCheckedSurface`), faded in as the
 *   chip is selected.
 * @param selectedHighlightColor The highlight laid over a selected chip (`mCachedCheckedBackground`).
 * @param selectedStrokeColor The 0.6dp outline of a selected chip without the material edge light
 *   (`mCachedCheckedStroke`).
 * @param contentColor The label and icon color.
 * @param disabledContentColor The label and icon color when disabled.
 */
@Immutable
data class ChipTabColors(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val selectedHighlightColor: Color,
    val selectedStrokeColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
) {
    @Stable
    internal fun contentColor(enabled: Boolean): Color = if (enabled) contentColor else disabledContentColor
}

/**
 * The chip tab capsule and its effects, shared by both [ChipTab] overloads. [content] receives the
 * label color, which is also provided as [LocalContentColor].
 */
@Composable
private fun ChipTabSurface(
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    materialEffect: Boolean,
    materialType: ChipTabMaterialType,
    cornerRadius: Dp,
    colors: ChipTabColors,
    interactionSource: MutableInteractionSource?,
    modifier: Modifier = Modifier,
    content: @Composable (contentColor: Color) -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isDark = isDarkScheme()
    val contentColor = colors.contentColor(enabled)

    // COUIChipTabDrawable.Checked/UncheckedLayerAlphaAnimation: two COUISpringForce(0.2, 0) springs
    // moving in opposite directions, so the unchecked layer is always 1 - checked. A chip that is
    // not laid out yet takes the value directly (LayerAlphaAnimation.animateToFinalPosition).
    val checkedLayer = remember { Animatable(if (selected) 1f else 0f) }
    LaunchedEffect(selected) {
        checkedLayer.animateTo(if (selected) 1f else 0f, ChipTabLayerSpring)
    }

    // The stroke and spotlight effects need runtime shaders, as COUIShadowEdgeDrawable and
    // COUISpotLightEffectDrawable do; without them the chip behaves as with the material off.
    val shaders = remember(materialEffect) {
        if (materialEffect && isRuntimeShaderSupported()) {
            ChipTabShaders(
                shadowPass = RuntimeShader(ShadowEdgeShader.Source),
                edgePass = RuntimeShader(ShadowEdgeShader.Source),
                spotLight = RuntimeShader(SpotLightShader.Source),
            )
        } else {
            null
        }
    }

    // COUIChipTab.applyMaterialEffect: the backdrop replaces the fill. It can only be drawn when the
    // app provides a backdrop; the selection fraction is read here, so a selection animation
    // recomposes the chip only while a provider is present.
    val backdropProvider = if (materialEffect) LocalMaterialBackdrop.current else null
    val backdropModifier = if (backdropProvider != null) {
        val spec = ChipTabDefaults.backdropSpec(isDark, materialType, checkedLayer.value)
        val backdropShape = remember(cornerRadius) { SquircleShape(cornerRadius, ChipTabCornerWeight) }
        remember(backdropProvider, spec, backdropShape) { backdropProvider.materialBackdrop(spec, backdropShape) }
    } else {
        null
    }
    val blurred = backdropModifier != null

    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    // COUIChipDrawable.drawStateEffects (the COUIMaskEffectDrawable press and hover masks) is skipped
    // while the spotlight is on.
    val masksOn = shaders == null
    val hoverProgress = remember { Animatable(0f) }
    LaunchedEffect(enabled, isHovered, masksOn) {
        if (!enabled || !masksOn) hoverProgress.snapTo(0f) else hoverProgress.animateTo(if (isHovered) 1f else 0f, ChipTabMaskSpring)
    }
    val pressProgress = remember { Animatable(0f) }
    LaunchedEffect(enabled, isPressed, masksOn) {
        if (enabled && masksOn && isPressed) {
            pressProgress.animateTo(1f, ChipTabMaskSpring)
        } else if (!enabled || !masksOn) {
            pressProgress.snapTo(0f)
        } else if (pressProgress.value > 0f) {
            if (pressProgress.value < CHIP_TAB_PRESSED_MASK_MIN_VISIBLE_PROGRESS) {
                // COUIMaskEffectDrawable.animateToProgressUntil: a quick tap still flashes the mask.
                try {
                    pressProgress.animateTo(1f, ChipTabMaskSpring) {
                        if (value >= CHIP_TAB_PRESSED_MASK_MIN_VISIBLE_PROGRESS) throw ChipTabMaskThresholdReached()
                    }
                } catch (_: ChipTabMaskThresholdReached) {
                    // Minimum visible progress reached; fall through to the fade-out.
                }
            }
            pressProgress.animateTo(0f, ChipTabMaskSpring)
        }
    }
    // COUIStrokeDrawable focus outline, cleared at once when disabled.
    val focusProgress = remember { Animatable(0f) }
    LaunchedEffect(enabled, isFocused) {
        if (!enabled) focusProgress.snapTo(0f) else focusProgress.animateTo(if (isFocused) 1f else 0f, ChipTabMaskSpring)
    }

    val spotlight = remember { SpotLightState() }
    val scope = rememberCoroutineScope()
    val spotlightOn = shaders != null && enabled

    val pressMask = COUITheme.colorScheme.press
    val hoverMask = COUITheme.colorScheme.hover
    val focusOutline = COUITheme.colorScheme.focusOutline
    val shadowShape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    // Where the chip sits below the light source, for the spot shadow offset.
    val centerY = remember { mutableFloatStateOf(0f) }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = modifier
                .couiPressScale(pressed = isPressed, enabled = enabled)
                .onGloballyPositioned { coordinates ->
                    centerY.floatValue = coordinates.positionInWindow().y + coordinates.size.height / 2f
                }
                .chipTabShadow(shadowShape, isDark, { checkedLayer.value }, { centerY.floatValue })
                .drawWithContent {
                    drawContent()
                    val focus = focusProgress.value
                    if (focus > 0f) {
                        // COUIStrokeDrawable TYPE_OUTER: a 3dp ring just outside the capsule.
                        val ring = ChipTabFocusStrokeWidth.toPx()
                        val radius = min(cornerRadius.toPx(), size.minDimension / 2f)
                        drawRoundRect(
                            color = focusOutline,
                            topLeft = Offset(-ring / 2f, -ring / 2f),
                            size = Size(size.width + ring, size.height + ring),
                            cornerRadius = CornerRadius(radius + ring / 2f),
                            style = Stroke(width = ring),
                            alpha = focus.coerceIn(0f, 1f),
                        )
                    }
                }
                // COUIChipTab clips to its outline, the Oplus smooth round rect of weight 1.0.
                .squircleClip(cornerRadius = cornerRadius, weight = ChipTabCornerWeight)
                // Inside the clip's offscreen layer: the edge light is overlaid on what lies
                // beneath it in this layer, so the blurred backdrop has to be drawn here.
                .then(backdropModifier ?: Modifier)
                .drawWithCache {
                    val radius = min(cornerRadius.toPx(), size.minDimension / 2f)
                    val strokeWidth = ChipTabCheckedStrokeWidth.toPx()
                    val commonArray = ShadowEdgeShader.commonArray(size.width, size.height, radius, weight = ChipTabCornerWeight)
                    val strokeParams = if (isDark) ChipTabStrokeDark else ChipTabStrokeLight
                    onDrawWithContent {
                        val checked = checkedLayer.value.coerceIn(0f, 1f)

                        // COUIChipTab.draw: the spotlight goes down before the chip drawable.
                        val glow = spotlight.progress.value
                        if (shaders != null && glow > 0f) {
                            val light = spotlight.position.value
                            shaders.spotLight.setFloatUniform(
                                "u_lightPosAndHeight",
                                light.x,
                                light.y,
                                SpotLightShader.radius(glow, spotlight.extinguishing, CHIP_TAB_SPOT_END_RADIUS),
                                (if (isDark) CHIP_TAB_SPOT_INTENSITY_DARK else CHIP_TAB_SPOT_INTENSITY_LIGHT) * glow,
                            )
                            shaders.spotLight.setFloatUniform("u_lightColorAndAmbient", 1f, 1f, 1f, CHIP_TAB_SPOT_AMBIENT)
                            shaders.spotLight.setFloatUniform("u_lightingParams", CHIP_TAB_SPOT_DIFFUSE, CHIP_TAB_SPOT_LIGHT_HEIGHT, 0f, 0f)
                            drawRect(
                                brush = shaders.spotLight.asBrush(),
                                blendMode = if (isDark) BlendMode.SrcOver else BlendMode.Plus,
                            )
                        }

                        // drawDualLayerBackground: without the blur the drawable paints the fill.
                        if (!blurred) {
                            if (checked > 0f) drawRect(colors.selectedContainerColor.scaleAlpha(checked))
                            if (checked < 1f) drawRect(colors.containerColor.scaleAlpha(1f - checked))
                            if (checked > 0f) drawRect(colors.selectedHighlightColor.scaleAlpha(checked))
                        }
                        // drawLightOSCheckedBackground: the 0.6dp checked stroke, unless the material
                        // edge light replaces it. A plain round rect inset by half the stroke.
                        if (shaders == null && checked > 0f) {
                            drawRoundRect(
                                color = colors.selectedStrokeColor.scaleAlpha(checked),
                                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                                cornerRadius = CornerRadius(max(0f, radius - strokeWidth / 2f)),
                                style = Stroke(width = strokeWidth),
                            )
                        }

                        val hover = hoverProgress.value
                        if (hover > 0f) drawRect(hoverMask, alpha = hover.coerceIn(0f, 1f))
                        val press = pressProgress.value
                        if (press > 0f) drawRect(pressMask, alpha = press.coerceIn(0f, 1f))

                        // COUIChipTab.applyMaterialEffect: TYPE_NO_EFFECT_BASE_1 -> TYPE_FRAMEWORK_CAPSULE_2
                        // by the checked layer alpha, every parameter lerped from zero.
                        if (shaders != null && checked > CHIP_TAB_MIN_VISIBLE_LAYER_ALPHA) {
                            for (pass in arrayOf(shaders.shadowPass, shaders.edgePass)) {
                                pass.setFloatUniform("u_commonArray", commonArray)
                                pass.setFloatUniform("u_shadow", 1f)
                                pass.setFloatUniform(
                                    "u_shadowArray",
                                    ShadowEdgeShader.shadowArray(
                                        spec = ShadowEdgePresets.CapsuleShadow,
                                        width = size.width,
                                        height = size.height,
                                        fadeWidthScale = strokeParams.shadowFadeScale * checked,
                                        fadeAlphaInScale = strokeParams.shadowFadeIn * checked,
                                        fadeAlphaOutScale = strokeParams.shadowFadeOut * checked,
                                        density = density,
                                    ),
                                )
                                pass.setFloatUniform("u_edge", 1f)
                                pass.setFloatUniform(
                                    "u_edgeArray",
                                    ShadowEdgeShader.edgeArray(
                                        spec = ShadowEdgePresets.CapsuleEdge,
                                        width = size.width,
                                        height = size.height,
                                        lineWidth = (strokeParams.edgeWidth * checked).dp.toPx(),
                                        lineAlpha = strokeParams.edgeAlpha * checked,
                                        lineAngle = strokeParams.edgeAngle * checked,
                                        density = density,
                                    ),
                                )
                            }
                            splitFrameworkStroke(shadowPass = shaders.shadowPass, edgePass = shaders.edgePass)
                            drawRect(shaders.shadowPass.asBrush())
                            drawRect(shaders.edgePass.asBrush(), blendMode = FrameworkEdgeBlendMode)
                        }
                        drawContent()
                    }
                }
                .then(
                    if (spotlightOn) {
                        // COUIChipTab.dispatchTouchEvent feeds the spotlight; touches are only observed.
                        Modifier.pointerInput(Unit) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                scope.launch { spotlight.glow(down.position) }
                                while (true) {
                                    val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                                    if (change.changedToUp() || !change.pressed) break
                                    scope.launch { spotlight.follow(change.position) }
                                }
                                scope.launch { spotlight.extinguish() }
                            }
                        }
                    } else {
                        Modifier
                    },
                )
                // COUIChip.handleActionDown starts the press scale and the press mask on ACTION_DOWN,
                // also inside the scrolling ChipTabRow, where a clickable would wait for the tap
                // timeout first.
                .tabTouchDownPress(interactionSource = interactionSource, enabled = enabled)
                .selectable(
                    selected = selected,
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Tab,
                    onClick = onClick,
                ),
            propagateMinConstraints = true,
        ) {
            content(contentColor)
        }
    }
}

/**
 * `ShadowUtils.setElevationToView(chip, 5, checkedLayerAlpha)` (`SHADOW_LV3_A`), the light-source
 * shadow a selected chip tab casts: elevation coui_shadow_elevation_level3a 100dp scaled by the
 * checked layer alpha, colour alpha coui_shadow_color_lv3a 34 / 68, light 10000dp above at
 * y -200dp (-240dp in values-night) with radius 2000dp. As for the popups, the renderer splits it
 * into an ambient layer (blur `e / 2`, alpha `c * 0.039 / (1 + e / 128)`) and a spot layer (offset
 * `e * (y - lightY) / (lightZ - e)`, penumbra `lightR * e / (lightZ - e)` drawn as a blur of a
 * third less, alpha `c * 0.19`). The caustic shadow COUI 17 swaps in on material devices is not
 * reproduced.
 */
private fun Modifier.chipTabShadow(
    shape: Shape,
    isDark: Boolean,
    checkedLayer: () -> Float,
    centerY: () -> Float,
): Modifier {
    val colorAlpha = if (isDark) CHIP_TAB_SHADOW_COLOR_ALPHA_DARK else CHIP_TAB_SHADOW_COLOR_ALPHA_LIGHT
    val lightY = if (isDark) ChipTabShadowLightYDark else ChipTabShadowLightYLight
    return this
        .dropShadow(shape) {
            val elevation = ChipTabShadowElevation.toPx() * checkedLayer().coerceIn(0f, 1f)
            if (elevation <= 0f) {
                radius = 0f
                color = Color.Transparent
                return@dropShadow
            }
            radius = elevation / 2f
            color = Color.Black.copy(alpha = colorAlpha * CHIP_TAB_AMBIENT_SHADOW_ALPHA / (1f + elevation / density / 128f))
        }
        .dropShadow(shape) {
            val elevation = ChipTabShadowElevation.toPx() * checkedLayer().coerceIn(0f, 1f)
            val lightZ = ChipTabShadowLightZ.toPx()
            if (elevation <= 0f || elevation >= lightZ) {
                radius = 0f
                offset = Offset.Zero
                color = Color.Transparent
                return@dropShadow
            }
            radius = ChipTabShadowLightRadius.toPx() * (elevation / (lightZ - elevation)) / CHIP_TAB_PENUMBRA_PER_BLUR_RADIUS
            offset = Offset(0f, elevation * (centerY() - lightY.toPx()) / (lightZ - elevation))
            color = Color.Black.copy(alpha = colorAlpha * CHIP_TAB_SPOT_SHADOW_ALPHA)
        }
}

/** The three runtime shaders of a material chip tab. */
private class ChipTabShaders(
    val shadowPass: RuntimeShader,
    val edgePass: RuntimeShader,
    val spotLight: RuntimeShader,
)

@Composable
private fun isDarkScheme(): Boolean = COUITheme.colorScheme.isDark

private fun Color.scaleAlpha(fraction: Float): Color = copy(alpha = alpha * fraction)

/**
 * `android.animation.ArgbEvaluator`: the alpha mixes linearly, the colour channels in linear
 * light (gamma 2.2), as `COUIMaterialBlurEffect` mixes its layer colours.
 */
private fun argbEvaluate(fraction: Float, start: Color, end: Color): Color {
    fun toLinear(c: Float) = c.toDouble().pow(2.2).toFloat()
    fun toGamma(c: Float) = c.coerceIn(0f, 1f).toDouble().pow(1.0 / 2.2).toFloat()
    return Color(
        red = toGamma(toLinear(start.red) + (toLinear(end.red) - toLinear(start.red)) * fraction),
        green = toGamma(toLinear(start.green) + (toLinear(end.green) - toLinear(start.green)) * fraction),
        blue = toGamma(toLinear(start.blue) + (toLinear(end.blue) - toLinear(start.blue)) * fraction),
        alpha = (start.alpha + (end.alpha - start.alpha) * fraction).coerceIn(0f, 1f),
    )
}

/** `COUIChipTab` outline and material corner weight (`OplusMaterialCornerParams(80, 1.0)`). */
private val ChipTabCornerWeight = 1f

/**
 * The checked / unchecked layer spring: COUISpringForce(response 0.2, bounce 0), i.e. critically
 * damped with stiffness (2 * PI / 0.2)^2 ~= 986.96, settling within 1/256
 * (`ALPHA_MIN_VISIBLE_CHANGE`).
 */
private val ChipTabLayerSpring = spring(dampingRatio = 1f, stiffness = 986.96f, visibilityThreshold = 1f / 256f)

/** The layer alpha below which the edge light is not drawn (`ALPHA_MIN_VISIBLE_CHANGE`). */
private const val CHIP_TAB_MIN_VISIBLE_LAYER_ALPHA = 1f / 256f

/** The COUIMaskEffectDrawable spring: COUISpringForce(response 0.3, bounce 0). */
private val ChipTabMaskSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/** COUIMaskEffectDrawable DEFAULT_MIN_PROGRESS_FOR_TOUCH_ENTER_ANIMATION. */
private const val CHIP_TAB_PRESSED_MASK_MIN_VISIBLE_PROGRESS = 0.7f

/** Thrown to stop the deferred press-in mask animation once the minimum visible progress is reached. */
private class ChipTabMaskThresholdReached : CancellationException("Press mask reached the minimum visible progress")

/** `COUIChipTabGroup.scrollToChipTabWithAnimation`: COUISpringForce(response 0.4, bounce 0.2). */
private val ChipTabRevealSpring = spring<Float>(dampingRatio = 0.8f, stiffness = 246.74f)

/**
 * `COUIChipTabGroupFragmentMediator.pageFadeInSpring`: COUISpringForce(response 0.3, bounce 0) on
 * the page alpha, settling within the 1/256 `MIN_VISIBLE_CHANGE_ALPHA`.
 */
private val ChipTabPageFadeInSpring = spring(dampingRatio = 1f, stiffness = 438.65f, visibilityThreshold = 1f / 256f)

/** `COUIChipTabGroupFragmentMediator.pageFadeOutSpring`: COUISpringForce(response 0.2, bounce 0). */
private val ChipTabPageFadeOutSpring = spring(dampingRatio = 1f, stiffness = 986.96f, visibilityThreshold = 1f / 256f)

/** `COUIChipTabGroupFragmentMediator.PAGE_APPEAR_DELAY`. */
private const val CHIP_TAB_PAGE_APPEAR_DELAY_MILLIS = 150L

/** `DynamicAnimation.MIN_VISIBLE_CHANGE_ALPHA`. */
private const val CHIP_TAB_PAGE_MIN_VISIBLE_ALPHA = 1f / 256f

/** The visible width of the focus outline: COUI default_focus_stroke_radius. */
private val ChipTabFocusStrokeWidth = 3.dp

/** `COUIChipTabDrawable.CHECKED_STROKE_WIDTH_DP`. */
private val ChipTabCheckedStrokeWidth = 0.6.dp

/**
 * How far the [ChipTabRow] layer reaches above and below the row so the selected chip's shadow is
 * not cut off; the scroll container clips its cross axis at the same 30dp
 * (`clipScrollableContainer`). COUI expands the layer by 100px.
 */
private val ChipTabRowLayerOverflow = 30.dp

/** `FRAMEWORK_CAPSULE_PARAMS_2_LIGHT`. */
private val ChipTabStrokeLight = StrokeEffectParams(0.2f, 2f, 0f, 0.1f, 1f, 1f)

/** `FRAMEWORK_CAPSULE_PARAMS_2_DARK`. */
private val ChipTabStrokeDark = StrokeEffectParams(0.6f, 2f, 0f, 0.02f, 1f, 1f)

// COUISpotLightEffect CHIP_TAB_PARAMS_LIGHT / _DARK: PLUS 0.2 light, SRC_OVER 0.3 dark, radius
// 100 -> 600px, light height 200px, ambient 0.1, diffuse 0.5, no dither or noise.
private const val CHIP_TAB_SPOT_INTENSITY_LIGHT = 0.2f
private const val CHIP_TAB_SPOT_INTENSITY_DARK = 0.3f
private const val CHIP_TAB_SPOT_END_RADIUS = 600f
private const val CHIP_TAB_SPOT_LIGHT_HEIGHT = 200f
private const val CHIP_TAB_SPOT_AMBIENT = 0.1f
private const val CHIP_TAB_SPOT_DIFFUSE = 0.5f

// ShadowUtils SHADOW_LV3_A (coui_shadow_*_level3a, coui_shadow_color_lv3a).
private val ChipTabShadowElevation = 100.dp
private val ChipTabShadowLightYLight = (-200).dp
private val ChipTabShadowLightYDark = (-240).dp
private val ChipTabShadowLightZ = 10000.dp
private val ChipTabShadowLightRadius = 2000.dp
private const val CHIP_TAB_SHADOW_COLOR_ALPHA_LIGHT = 34f / 255f
private const val CHIP_TAB_SHADOW_COLOR_ALPHA_DARK = 68f / 255f

// The framework spotShadowAlpha / ambientShadowAlpha defaults, and the penumbra a Compose blur
// radius covers.
private const val CHIP_TAB_SPOT_SHADOW_ALPHA = 0.19f
private const val CHIP_TAB_AMBIENT_SHADOW_ALPHA = 0.039f
private const val CHIP_TAB_PENUMBRA_PER_BLUR_RADIUS = 1.5f

// COUIMaterialBlurEffect TYPE_CONTENT_CHIP_TAB*.
private val ChipTabBlurLight = MaterialBackdropSpec(40f, Color(0x144E4E4E), BlendMode.ColorDodge, Color(0x14606060), BlendMode.Luminosity)
private val ChipTabSelectedBlurLight = MaterialBackdropSpec(40f, Color(0x66A3A1A1), BlendMode.ColorDodge, Color(0x99F5F5F5), BlendMode.Luminosity)
private val ChipTabBlurDark = MaterialBackdropSpec(40f, Color(0x33B2B2B2), BlendMode.Luminosity, Color(0x33303030), BlendMode.Overlay)
private val ChipTabSelectedBlurDark = MaterialBackdropSpec(40f, Color(0xA6505050), BlendMode.Luminosity, Color(0x4CA7A7A7), BlendMode.Overlay)
private val ChipTabNoBlurLight = MaterialBackdropSpec(0f, Color(0x9A9B9B9B), BlendMode.ColorDodge, Color(0x9ACECECE), BlendMode.Luminosity)
private val ChipTabSelectedNoBlurLight = MaterialBackdropSpec(0f, Color(0x66A3A3A3), BlendMode.ColorDodge, Color(0xCEF5F5F5), BlendMode.Luminosity)
private val ChipTabNoBlurDark = MaterialBackdropSpec(0f, Color(0xEE474747), BlendMode.Luminosity, Color(0xCE8D8D8D), BlendMode.Overlay)
private val ChipTabSelectedNoBlurDark = MaterialBackdropSpec(0f, Color(0xDA888888), BlendMode.Luminosity, Color(0x80A8A8A8), BlendMode.Overlay)
