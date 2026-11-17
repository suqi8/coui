// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSupported
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidatePlacement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect.materialPanel
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
import io.github.suqi8.coui.kmp.utils.graphicsLayerWithOverflow
import io.github.suqi8.coui.kmp.utils.hasFocusReassignBug
import io.github.suqi8.coui.kmp.utils.layerOverflowInner
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A [SearchBar] component with COUI style.
 *
 * @param inputField the input field to input a query in the [SearchBar].
 * @param onExpandedChange the callback to be invoked when the [SearchBar]'s expanded state is
 *   changed.
 * @param modifier the [Modifier] to be applied to the [SearchBar].
 * @param insideMargin The margin inside the [SearchBar]. A [Dp.Unspecified] width (the default)
 *   takes COUI's responsive horizontal padding (`mUseResponsivePadding`): 16dp, 24dp or 40dp as the
 *   bar's width falls in the compact, medium or expanded window class
 *   ([SearchBarDefaults.responsiveHorizontalPadding]); a specified width is used as it is.
 * @param expanded whether the [SearchBar] is expanded and showing search results.
 * @param type how the bar behaves when it is expanded, COUI `setSearchAnimateType`; see
 *   [SearchBarType].
 * @param navigationButton a 40dp button 12dp before the capsule, typically an [IconButton] (COUI
 *   `setNavigationViewDrawable`). COUI lays it out for a [SearchBarType.NonInstant] bar only.
 * @param outerPrimaryButton a 40dp button 12dp after the capsule, typically an [IconButton] (COUI
 *   `setOuterPrimaryButton`). A [SearchBarType.Instant] bar shows an outer button only while it is
 *   the only one set; it fades out (alpha 1 to 0, scale 1 to 0.8, blurring) as the bar expands, on
 *   its own spring back (response 0.4, bounce 0.3, growing from 0.5).
 * @param outerSecondaryButton a second outer button after [outerPrimaryButton] (COUI
 *   `setOuterSecondaryButton`), see [outerPrimaryButton].
 * @param outsideEndAction the functional button shown while the bar is expanded, typically a
 *   [CancelButton]; `null` hides it (COUI `setHideFunctionalButton`). On a [SearchBarType.Instant]
 *   bar it is placed 12dp after the capsule and grows in with the COUI 17 edit-state spring (scale
 *   0.5 to 1, fading in) while the capsule narrows to make room. On a [SearchBarType.NonInstant]
 *   bar it moves inside the capsule, after a 1dp x 16dp divider.
 * @param content the content to be shown when the [SearchBar] is expanded.
 */
@Composable
fun SearchBar(
    inputField: @Composable () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    insideMargin: DpSize = SearchBarDefaults.InsideMargin,
    expanded: Boolean = false,
    type: SearchBarType = SearchBarType.Instant,
    navigationButton: @Composable (() -> Unit)? = null,
    outerPrimaryButton: @Composable (() -> Unit)? = null,
    outerSecondaryButton: @Composable (() -> Unit)? = null,
    outsideEndAction: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val navigationEventState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
    val iconExpand = LocalSearchBarIconExpand.current
    val capsuleInfo = remember { SearchBarCapsuleInfo() }
    val geometry = remember { SearchBarGeometry() }
    val barLayoutDirection = LocalLayoutDirection.current
    val windowHeight = LocalWindowInfo.current.containerDpSize.height
    val nonInstant = type == SearchBarType.NonInstant
    // showInToolBar runs the icon expansion only for a blurred bar; a flat one fades in as a whole.
    val expandMotion = if (capsuleInfo.material) iconExpand else null
    val fadeExpand = iconExpand != null && !capsuleInfo.material

    // COUI 17 COUISearchBar: the capsule width (mStateChangedAnimation) and the functional button
    // (mFunBtnSpringAnimation) run on response 0.5 / bounce 0.25 springs, both in
    // StateChangedToEditParamsHigh and StateChangedToNormalParamsHigh. They start together with
    // the same force, so one progress drives both. Expanded out of a toolbar icon
    // (showInToolBar), the bar is laid out in its edit state for the whole icon expansion instead,
    // and a non-instant bar changes state without animation (changeStateImmediately).
    val progress = remember { Animatable(if (expanded || iconExpand != null) 1f else 0f) }
    // mOuterBtnSpringAnimation: the outer buttons run on their own spring, whose force and scale
    // depend on the direction.
    val outerProgress = remember { Animatable(if (expanded || iconExpand != null) 1f else 0f) }
    var outerToEdit by remember { mutableStateOf(expanded) }
    LaunchedEffect(expanded, iconExpand != null, nonInstant) {
        val target = if (expanded || iconExpand != null) 1f else 0f
        if (iconExpand != null || nonInstant) {
            progress.snapTo(target)
            outerProgress.snapTo(target)
        } else {
            outerToEdit = expanded
            launch { outerProgress.animateTo(target, if (expanded) OuterToEditSpring else OuterToNormalSpring) }
            progress.animateTo(targetValue = target, animationSpec = StateChangeSpring)
        }
    }

    val shaders = remember {
        if (isRuntimeShaderSupported()) List(2) { RuntimeShader(SearchBarMetaballShader.Source) } else null
    }
    val isDark = COUITheme.colorScheme.isDark
    val blendSupported = remember { BlendMode.ColorDodge.isSupported() && BlendMode.Luminosity.isSupported() }
    val outsideAction = if (nonInstant) null else outsideEndAction
    // A non-instant bar hands its functional button to the capsule, shown while expanded.
    val innerAction = if (nonInstant && expanded) outsideEndAction else null
    val actionInMotion by remember { derivedStateOf { progress.value != 0f } }
    val actionShown = outsideAction != null && (expanded || actionInMotion)

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .searchBarHorizontalPadding(insideMargin.width, windowHeight)
                .onGloballyPositioned { geometry.rowRootX = it.positionInRoot().x }
                .then(
                    if (fadeExpand) {
                        // mFadeInFadeOutAnimation: setAlpha2Child(this, p).
                        Modifier.graphicsLayerWithOverflow(COUIMaterialEffect.ShadowOverflow) {
                            alpha = (iconExpand?.progress() ?: 1f).coerceIn(0f, 1f)
                        }
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (outsideAction != null && shaders != null) {
                        Modifier.drawWithContent {
                            drawSearchBarNeck(
                                geometry = geometry,
                                progress = progress.value,
                                iconExpand = expandMotion,
                                capsuleInfo = capsuleInfo,
                                capsuleInsetPx = insideMargin.height.toPx(),
                                shaders = shaders,
                                filters = if (blendSupported) materialFilters(isDark) else null,
                            )
                            drawContent()
                        }
                    } else {
                        Modifier
                    },
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (nonInstant && navigationButton != null) {
                // layoutNavigationButton: 40dp at the start padding, 12dp before the capsule.
                Box(
                    modifier = Modifier.size(SearchBarDefaults.OuterButtonSize),
                    contentAlignment = Alignment.Center,
                ) {
                    navigationButton()
                }
                Spacer(modifier = Modifier.width(SearchBarDefaults.NavigationButtonEndGap))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .onPlaced { geometry.inputRect = Rect(it.positionInParent(), it.size.toSize()) }
                    .then(
                        if (expandMotion == null) {
                            Modifier
                        } else {
                            // The blur renders the capsule offscreen: the layer reaches past it so
                            // the material shadow is not cut off.
                            Modifier.graphicsLayerWithOverflow(if (capsuleInfo.material) COUIMaterialEffect.ShadowOverflow else 0.dp) {
                                // showInToolBar: the background view starts centred on the toolbar
                                // icon (mIconExpandStartX, measured glyph centre to icon centre) and
                                // slides home, blurring most half way.
                                val p = expandMotion.progress()
                                val startX = iconExpandCapsuleStartX(geometry, expandMotion, barLayoutDirection)
                                if (startX.isNaN()) {
                                    alpha = 0f
                                } else {
                                    alpha = 1f
                                    translationX = startX * (1f - p)
                                }
                                val blur = if (capsuleInfo.material) iconExpandCapsuleBlurPx(p) else 0f
                                renderEffect = if (blur > 0f) BlurEffect(blur, blur, TileMode.Decal) else null
                            }
                        },
                    )
                    .padding(vertical = insideMargin.height),
            ) {
                CompositionLocalProvider(
                    LocalSearchBarCapsuleInfo provides capsuleInfo,
                    LocalSearchBarInnerAction provides innerAction,
                ) {
                    inputField()
                }
            }
            if (nonInstant) {
                // layoutOuterButton: each 40dp outer button 12dp after the previous view, still.
                listOfNotNull(outerPrimaryButton, outerSecondaryButton).forEach { button ->
                    Spacer(modifier = Modifier.width(SearchBarDefaults.OuterButtonGap))
                    Box(
                        modifier = Modifier.size(SearchBarDefaults.OuterButtonSize),
                        contentAlignment = Alignment.Center,
                    ) {
                        button()
                    }
                }
            }
            // getOuterButtonCount() == 1: an instant bar lays an outer button out only alone.
            val instantOuterButton = when {
                nonInstant -> null
                outerSecondaryButton == null -> outerPrimaryButton
                outerPrimaryButton == null -> outerSecondaryButton
                else -> null
            }
            if (instantOuterButton != null) {
                val outerShown by remember { derivedStateOf { outerProgress.value != 1f } }
                if (outerShown) {
                    val outerOverflow = COUIMaterialEffect.ShadowOverflow
                    Box(
                        modifier = Modifier
                            .layout { measurable, constraints ->
                                val overflow = outerOverflow.roundToPx()
                                val size = SearchBarDefaults.OuterButtonSize.roundToPx()
                                val placeable = measurable.measure(Constraints.fixed(size + 2 * overflow, size + 2 * overflow))
                                // measureBackground: the capsule spans from its normal width (beside
                                // gap + outer button) to its edit width (beside gap + functional
                                // button) on the state spring; this slot gives up the outer share.
                                val fraction = 1f - progress.value
                                val width = ((size + SearchBarDefaults.OuterButtonGap.roundToPx()) * fraction).roundToInt().coerceAtLeast(0)
                                layout(width, size) {
                                    // layoutOuterButtonBackToNormal: the button keeps its normal-state
                                    // place against the end edge, over the action slot growing beside it.
                                    val actionWidth = if (actionShown) geometry.actionSlotWidth else 0
                                    placeable.placeRelativeWithLayer(width + actionWidth - size - overflow, -overflow) {
                                        // mOuterBtnSpringAnimation listener: alpha 1 - q, scale from 1
                                        // toward the end scale (0.8 to edit) or from the init scale
                                        // (0.5 to normal), blurring 30px * q.
                                        val q = outerProgress.value
                                        val init = if (outerToEdit) OuterToEditEndScale else OuterToNormalInitScale
                                        val scale = init + (1f - init) * (1f - q)
                                        scaleX = scale
                                        scaleY = scale
                                        alpha = (1f - q).coerceIn(0f, 1f)
                                        val blur = if (capsuleInfo.material) max(q, 0f) * ActionBlurPx else 0f
                                        renderEffect = if (blur > 0f) BlurEffect(blur, blur, TileMode.Decal) else null
                                    }
                                }
                            }
                            .layerOverflowInner(outerOverflow),
                        contentAlignment = Alignment.Center,
                    ) {
                        instantOuterButton()
                    }
                }
            }
            if (outsideAction != null) {
                if (actionShown) {
                    // Room for the shadow of a material action (CancelButton) around its layer.
                    val actionOverflow = COUIMaterialEffect.ShadowOverflow
                    val actionTextAlpha = remember(expandMotion) {
                        if (expandMotion == null) {
                            { searchBarActionFrame(progress.value, blurred = capsuleInfo.material).textAlpha }
                        } else {
                            { 1f }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .onPlaced { geometry.actionSlotOrigin = it.positionInParent() }
                            .layout { measurable, constraints ->
                                // The scale, fade and blur below render the button offscreen: its
                                // layer reaches the overflow past it (layerOverflowInner), so the
                                // material shadow is not cut off.
                                val overflow = actionOverflow.roundToPx()
                                // The slot claims (gap + button) * progress, so the capsule beside
                                // it narrows with the spring, overshoot included. The button itself
                                // sits at its edit-state position against the end edge.
                                val placeable = measurable.measure(
                                    constraints.copy(minWidth = 0).offset(horizontal = 2 * overflow, vertical = 2 * overflow),
                                )
                                val buttonWidth = placeable.width - 2 * overflow
                                val buttonHeight = placeable.height - 2 * overflow
                                val fraction = max(progress.value, 0f)
                                val width = ((buttonWidth + ActionStartGap.roundToPx()) * fraction).roundToInt()
                                val rtl = layoutDirection == LayoutDirection.Rtl
                                val direction = if (rtl) -1f else 1f
                                // Slot-local bounds; the slot origin shifts them into the row.
                                val buttonX = if (rtl) 0 else width - buttonWidth
                                geometry.actionLocalRect = Rect(
                                    left = buttonX.toFloat(),
                                    top = 0f,
                                    right = (buttonX + buttonWidth).toFloat(),
                                    bottom = buttonHeight.toFloat(),
                                )
                                geometry.actionSlotWidth = width
                                layout(width, buttonHeight) {
                                    placeable.placeRelativeWithLayer(width - buttonWidth - overflow, -overflow) {
                                        if (expandMotion == null) {
                                            // COUISearchBar fun-button listener, the same frame
                                            // function both ways: scale, offset, alpha and blur.
                                            val frame = searchBarActionFrame(progress.value, blurred = capsuleInfo.material)
                                            scaleX = frame.scale
                                            scaleY = frame.scale
                                            translationX = direction * frame.translationPx
                                            alpha = frame.alpha
                                            val blur = frame.blurPx
                                            renderEffect = if (blur > 0f) BlurEffect(blur, blur, TileMode.Decal) else null
                                        } else {
                                            // funBtnAnimateIconExpand: the button flies from the
                                            // toolbar icon centre (mFunctionalButtonStartX).
                                            val p = expandMotion.progress()
                                            val scale = iconExpandActionScale(p)
                                            scaleX = scale
                                            scaleY = scale
                                            val startX = iconExpandActionStartX(geometry, expandMotion)
                                            translationX = if (startX.isNaN()) 0f else startX * (1f - p)
                                            alpha = iconExpandActionAlpha(p)
                                            val blur = if (capsuleInfo.material) iconExpandActionBlurPx(p) else 0f
                                            renderEffect = if (blur > 0f) BlurEffect(blur, blur, TileMode.Decal) else null
                                        }
                                    }
                                }
                            }
                            .layerOverflowInner(actionOverflow),
                    ) {
                        CompositionLocalProvider(LocalSearchBarActionTextAlpha provides actionTextAlpha) {
                            outsideAction()
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = expanded,
        ) {
            content()
        }
    }

    NavigationBackHandler(
        state = navigationEventState,
        isBackEnabled = expanded,
        onBackCompleted = {
            currentOnExpandedChange(false)
        },
    )
}

/**
 * `mIconExpandStartX`: the toolbar icon centre minus the search glyph centre, in root pixels; `NaN`
 * until both are measured.
 */
private fun Density.iconExpandCapsuleStartX(
    geometry: SearchBarGeometry,
    iconExpand: SearchBarIconExpand,
    layoutDirection: LayoutDirection,
): Float {
    val iconX = iconExpand.sourceCenterX()
    val rowX = geometry.rowRootX
    if (iconX.isNaN() || rowX.isNaN() || geometry.inputRect.isEmpty) return Float.NaN
    val glyphInset = SearchIconCenterInset.toPx()
    val glyphX = if (layoutDirection == LayoutDirection.Rtl) {
        rowX + geometry.inputRect.right - glyphInset
    } else {
        rowX + geometry.inputRect.left + glyphInset
    }
    return iconX - glyphX
}

/** `mFunctionalButtonStartX`: the toolbar icon centre minus the button centre, in root pixels. */
private fun iconExpandActionStartX(geometry: SearchBarGeometry, iconExpand: SearchBarIconExpand): Float {
    val iconX = iconExpand.sourceCenterX()
    val rowX = geometry.rowRootX
    if (iconX.isNaN() || rowX.isNaN() || geometry.actionRect.isEmpty) return Float.NaN
    return iconX - (rowX + geometry.actionRect.center.x)
}

/**
 * Draws the metaball neck between the capsule and the action while they move, from the same
 * transforms the two layers apply. Nothing is drawn at rest, where `COUISearchBar`'s 12dp gap keeps
 * the two shapes apart anyway.
 */
private fun DrawScope.drawSearchBarNeck(
    geometry: SearchBarGeometry,
    progress: Float,
    iconExpand: SearchBarIconExpand?,
    capsuleInfo: SearchBarCapsuleInfo,
    capsuleInsetPx: Float,
    shaders: List<RuntimeShader>,
    filters: List<Pair<Color, BlendMode>>?,
) {
    if (!capsuleInfo.material) return
    val input = geometry.inputRect
    val action = geometry.actionRect
    if (input.isEmpty || action.isEmpty) return
    val capsuleTop = input.top + capsuleInsetPx
    val capsuleBottom = input.bottom - capsuleInsetPx
    val capsule: Rect
    val button: Rect
    val alpha: Float
    if (iconExpand == null) {
        if (progress <= 0f || progress >= 1f) return
        capsule = Rect(input.left, capsuleTop, input.right, capsuleBottom)
        val frame = searchBarActionFrame(progress, blurred = true)
        val direction = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f
        button = action.scaledAround(frame.scale).translate(direction * frame.translationPx, 0f)
        alpha = frame.alpha
    } else {
        val p = iconExpand.progress()
        if (p <= 0f || p >= 1f) return
        val capsuleStartX = iconExpandCapsuleStartX(geometry, iconExpand, layoutDirection)
        val actionStartX = iconExpandActionStartX(geometry, iconExpand)
        if (capsuleStartX.isNaN() || actionStartX.isNaN()) return
        val width = iconExpandCapsuleWidth(capsuleBottom - capsuleTop, input.width, p)
        val shift = capsuleStartX * (1f - p)
        capsule = if (layoutDirection == LayoutDirection.Rtl) {
            Rect(input.right - width + shift, capsuleTop, input.right + shift, capsuleBottom)
        } else {
            Rect(input.left + shift, capsuleTop, input.left + width + shift, capsuleBottom)
        }
        button = action.scaledAround(iconExpandActionScale(p)).translate(actionStartX * (1f - p), 0f)
        alpha = iconExpandActionAlpha(p)
    }
    with(SearchBarMetaballShader) {
        drawNeck(
            shaders = shaders,
            capsule = capsule,
            action = button,
            alpha = alpha,
            filters = filters,
            fallback = capsuleInfo.fallbackColor,
        )
    }
}

private fun Rect.scaledAround(scale: Float): Rect {
    val c = center
    val halfWidth = width / 2f * scale
    val halfHeight = height / 2f * scale
    return Rect(c.x - halfWidth, c.y - halfHeight, c.x + halfWidth, c.y + halfHeight)
}

/**
 * The top-bar material colour filters [COUIMaterialEffect.materialPanel] draws (light `#66333333`
 * COLOR_DODGE + `#99F5F5F5` LUMINOSITY, dark `#CC262626` SRC_OVER + `#B37F7F7F` OVERLAY), so the neck
 * matches the shapes it joins.
 */
private fun materialFilters(isDark: Boolean): List<Pair<Color, BlendMode>> = if (isDark) {
    listOf(Color(0xCC262626) to BlendMode.SrcOver, Color(0xB37F7F7F) to BlendMode.Overlay)
} else {
    listOf(Color(0x66333333) to BlendMode.ColorDodge, Color(0x99F5F5F5) to BlendMode.Luminosity)
}

/**
 * The search glyph centre's inset from the capsule's start edge while the bar expands out of a
 * toolbar icon (`setAtBehindToolBar`, `mAddToToolbarAtBehind`):
 * `coui_search_bar_icon_expand_search_icon_start_gap` (2dp) + `coui_search_bar_inner_search_icon_size`
 * / 2 (18dp), the centre of the capsule's starting circle, which the default leading icon's
 * [SearchBarDefaults.IconExpandLeadingIconStartPadding] + 10dp also lands on.
 */
private val SearchIconCenterInset = 20.dp

/**
 * Applies a [SearchBar]'s horizontal inside margin: [padding] when it is specified, else COUI's
 * responsive padding for the bar's own width (`configResponsive`, from `getMeasuredWidth()`).
 */
private fun Modifier.searchBarHorizontalPadding(padding: Dp, windowHeight: Dp): Modifier = layout { measurable, constraints ->
    val paddingPx = if (padding.isSpecified) {
        padding.roundToPx()
    } else {
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth.toDp() else 0.dp
        SearchBarDefaults.responsiveHorizontalPadding(width, windowHeight).roundToPx()
    }
    val placeable = measurable.measure(constraints.offset(horizontal = -2 * paddingPx))
    layout(placeable.width + 2 * paddingPx, placeable.height) {
        placeable.placeRelative(paddingPx, 0)
    }
}

/**
 * A text field to input a query in a search bar with COUI style.
 *
 * @param query the query text to be shown in the input field.
 * @param onQueryChange the callback to be invoked when the input service updates the query. An
 *   updated text comes as a parameter of the callback.
 * @param onSearch the callback to be invoked when the input service triggers the
 *   [ImeAction.Search] action. The current [query] comes as a parameter of the callback.
 * @param expanded whether the search bar is expanded and showing search results.
 * @param onExpandedChange the callback to be invoked when the search bar's expanded state is
 *   changed.
 * @param modifier the [Modifier] to be applied to this input field.
 * @param label the hint shown while the query is empty, expanded or not (the edit text's hint,
 *   which COUI never clears on entering the edit state). It stays on one line, ellipsized at the end.
 * @param enabled the enabled state of this input field. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services: the hint and the default search icon take [SearchBarColors.disabledLabelColor] and
 *   [SearchBarColors.disabledIconColor] (`SearchBarBackgroundView.setEnabled`).
 * @param material whether the capsule takes the COUI 17 top-bar glass material (filtered
 *   backdrop, shadow, edge lighting, touch spotlight and the `COUIViewTouchAnimator` press scale,
 *   rubber band and deformation; see [COUIMaterialEffect.materialPanel]), as `SearchBarBackgroundView`
 *   does by default. When `false` the capsule is a flat [SearchBarColors.backgroundColor] fill.
 *   Defaults to [LocalCOUIMaterialEffectEnabled].
 * @param textStyle Style configuration that applies at character level such as color, font etc.
 * @param colors [SearchBarColors] that will be used to resolve the colors used for this input
 *   field in different states. See [SearchBarDefaults.searchBarColors].
 * @param collapsePercent how far through its scroll-driven collapse the bar is: 1 fully expanded,
 *   0 flattened to a divider line. See [searchBarCollapsePercent] for turning a scroll offset into
 *   this value, and [searchBarCollapseGeometry] for what it drives.
 * @param leadingIcon the leading icon to be displayed at the start of the input field, in place of
 *   the default search icon (COUI `couiSearchIcon`).
 * @param innerPrimaryButton a button inside the capsule after the text, in a 36dp slot, typically a
 *   36dp flat [IconButton] such as voice input (COUI `setInnerPrimaryButton`). The inner buttons show
 *   while the query is empty; typing swaps them for the clear button, and clearing brings them back.
 *   A gesture that starts on one leaves the capsule's touch motion and spotlight alone.
 * @param innerSecondaryButton a second inner button after [innerPrimaryButton] (COUI
 *   `setInnerSecondaryButton`), see [innerPrimaryButton].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this input field. You can use this to change the search bar's
 *   appearance or preview the search bar in different states. Note that if `null` is provided,
 *   interactions will still happen internally.
 * @param hints rotating hints shown in place of [label] while the query is empty, after COUI
 *   `COUIHintAnimationLayout.setHintsAnimation`: the first hint shows at once and every 3s the
 *   current one slides up and fades out over 250ms while the next one rises from below 150ms
 *   later (translation on `PathInterpolator(0.3, 0, 0.2, 1)`, alpha on COUIEaseInterpolator,
 *   travel half the free height plus 5dp, `coui_search_bar_animation_translate_extra`). Typing
 *   hides them and stops the rotation; clearing the query brings the current hint back, still
 *   (`COUIHintAnimationLayout` resumes only when it is attached again). An empty list keeps the
 *   static [label].
 * @param hintRepeatCount how many hint changes to play before the rotation stops on the current hint
 *   (COUI `setRepeatCount`), counted across the pauses while a query is typed and reset with new
 *   [hints]; [Int.MAX_VALUE] (the default) rotates forever.
 * @param onHintChange called with the index of each newly shown hint (COUI
 *   `COUIHintAnimationChangeListener`), e.g. to search for the shown hint on an empty submit. The
 *   first hint (index 0) shows without a call.
 */
@Composable
fun InputField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
    material: Boolean = LocalCOUIMaterialEffectEnabled.current,
    textStyle: TextStyle? = null,
    colors: SearchBarColors = SearchBarDefaults.searchBarColors(),
    collapsePercent: Float = 1f,
    leadingIcon: @Composable (() -> Unit)? = null,
    innerPrimaryButton: @Composable (() -> Unit)? = null,
    innerSecondaryButton: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    hints: List<String> = emptyList(),
    hintRepeatCount: Int = Int.MAX_VALUE,
    onHintChange: ((Int) -> Unit)? = null,
) {
    val currentOnQueryChange by rememberUpdatedState(onQueryChange)
    val currentOnSearch by rememberUpdatedState(onSearch)
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val internalInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    // SearchBarBackgroundView.updateSmoothRoundPath: radius = height / 2 at the explicit weight 1.0,
    // the COUI 17 soft capsule.
    val capsuleShape = if (isSquircleEnabled()) {
        remember { SquircleShape(COUIRoundCorner.Full, COUIRoundCorner.CapsuleWeight) }
    } else {
        CircleShape
    }
    val iconExpand = LocalSearchBarIconExpand.current
    val capsuleInfo = LocalSearchBarCapsuleInfo.current
    // SearchBarBackgroundView.dispatchTouchEvent decides on ACTION_DOWN whether a gesture drives the
    // capsule's touch animator and spotlight: not when it starts on an inner primary/secondary
    // button, but the quick delete (clear) button is not in that list and still does.
    var innerButtonPressed by remember { mutableStateOf(false) }
    val quickDeleteDown = remember { arrayOfNulls<PointerId>(1) }
    val iconExpandFullWidth = remember { IntArray(1) }

    // A non-instant SearchBar's functional button, shown inside the capsule while expanded.
    val innerAction = LocalSearchBarInnerAction.current

    val actualLeadingIcon = leadingIcon ?: {
        // coui_search_view_icon: a 20dp glyph in couiColorSecondNeutral, swapped for
        // coui_search_view_icon_disable (couiColorLabelTertiary) while disabled. The view is not
        // important for accessibility. Expanding out of a toolbar icon (mAddToToolbarAtBehind) it
        // sits 2dp from the capsule start instead of 4dp.
        val startPadding = if (iconExpand == null) SearchBarDefaults.LeadingIconStartPadding else SearchBarDefaults.IconExpandLeadingIconStartPadding
        Icon(
            modifier = Modifier.padding(start = startPadding, end = SearchBarDefaults.LeadingIconEndPadding),
            imageVector = COUIIcons.Basic.Search,
            tint = if (enabled) colors.iconColor else colors.disabledIconColor,
            contentDescription = null,
        )
    }

    // SearchBarBackgroundView inner buttons, after the edit frame's 4dp end gap: the quick delete
    // button while there is text, else the inner primary and secondary buttons (afterTextChanged
    // toggles their visibility at once); a 4dp gap closes the run when any of them shows, and a
    // non-instant bar then adds its divider and functional button.
    val actualTrailingIcons: @Composable () -> Unit = {
        val showClear = query.isNotEmpty()
        if (showClear) {
            // Clear button: a 36dp circular touch target holding an 18dp filled circle with a
            // white cross (ic_edit_text_delete_search_view).
            val clearCircleColor = colors.clearIconColor
            Box(
                modifier = Modifier
                    .size(SearchBarDefaults.InnerButtonSize)
                    .clip(CircleShape)
                    // Tags its downs in the Initial pass, before the clickable consumes them, so the
                    // capsule keeps treating them as its own (mQuickDeleteButton is not excluded).
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            quickDeleteDown[0] = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial).id
                        }
                    }
                    .clickable(enabled = enabled) { currentOnQueryChange("") }
                    // coui_search_clear_button_description.
                    .semantics { contentDescription = "Clear text" },
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(SearchBarDefaults.ClearButtonVisualSize)) {
                    drawCircle(color = clearCircleColor)
                    // ic_edit_text_delete_search_view cross: 8.2dp span, 1.6dp stroke, always white.
                    val half = 4.1.dp.toPx()
                    val stroke = 1.6.dp.toPx()
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x - half, center.y - half),
                        end = Offset(center.x + half, center.y + half),
                        strokeWidth = stroke,
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x - half, center.y + half),
                        end = Offset(center.x + half, center.y - half),
                        strokeWidth = stroke,
                    )
                }
            }
        } else {
            innerPrimaryButton?.let { SearchBarInnerButtonSlot(it) }
            innerSecondaryButton?.let { SearchBarInnerButtonSlot(it) }
        }
        val anyButton = showClear || innerPrimaryButton != null || innerSecondaryButton != null
        if (anyButton || innerAction != null) {
            Spacer(modifier = Modifier.width(SearchBarDefaults.InnerButtonGap))
        }
        if (innerAction != null) {
            // layoutDivider + layoutFunctionalButton: a 1dp x 16dp couiColorDivider line, 4dp, the
            // button, and 4dp to the capsule's end.
            Box(
                modifier = Modifier
                    .size(SearchBarDefaults.FunctionalDividerWidth, SearchBarDefaults.FunctionalDividerHeight)
                    .background(COUITheme.colorScheme.divider),
            )
            Spacer(modifier = Modifier.width(SearchBarDefaults.InnerButtonGap))
            CompositionLocalProvider(LocalSearchBarActionInCapsule provides true) {
                innerAction()
            }
            Spacer(modifier = Modifier.width(SearchBarDefaults.InnerButtonGap))
        }
    }

    val focused = internalInteractionSource.collectIsFocusedAsState().value
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val textColor = LocalContentColor.current
    // COUI input text: 15sp regular (Widget.COUI.COUISearchBar inputTextSize =
    // coui_search_view_input_text_size, applied by SearchBarBackgroundView.loadAttr).
    val inputTextStyle = COUITheme.textStyles.bodyXL
        .copy(fontSize = SearchBarDefaults.InputFieldFontSize)
        .merge(textStyle)
        .copy(color = textColor)

    val cursorBrush = SolidColor(COUITheme.colorScheme.primary)
    // Null while fully expanded, so an unused collapse costs nothing and the modifier chain stays
    // identical to what it was before this parameter existed.
    val dividerColor = COUITheme.colorScheme.divider
    val collapse = if (collapsePercent >= 1f) {
        null
    } else {
        remember(collapsePercent, colors.backgroundColor, dividerColor) {
            searchBarCollapseGeometry(
                percent = collapsePercent,
                normalColor = colors.backgroundColor,
                dividerColor = dividerColor,
            )
        }
    }
    val capsuleMaterial = material && collapse == null
    val fallbackColor = colors.backgroundColor
    SideEffect {
        capsuleInfo?.material = capsuleMaterial
        capsuleInfo?.fallbackColor = fallbackColor
        iconExpand?.onFadeChange?.invoke(!capsuleMaterial)
    }
    // showInToolBar plays the circle-to-capsule expansion only for a blurred capsule; a flat one is
    // faded in whole by the SearchBar.
    val expandMotion = if (capsuleMaterial) iconExpand else null
    // The edit text's hint: shown whenever the text is empty, edit state included.
    val labelText = if (query.isEmpty()) label else ""
    val hintColor = if (enabled) colors.labelColor else colors.disabledLabelColor

    // On API 26-27, focus is incorrectly reassigned after clearFocus(), preventing the
    // SearchBar from closing. Workaround: disable the TextField when collapsed and use
    // pointerInput to handle tap-to-expand. https://issuetracker.google.com/issues/433382598
    val workaroundEnabled = !hasFocusReassignBug || expanded
    val expandOnTapModifier = if (workaroundEnabled || !enabled) {
        Modifier
    } else {
        Modifier.pointerInput(Unit) { detectTapGestures { currentOnExpandedChange(true) } }
    }

    BasicTextField(
        value = query,
        onValueChange = currentOnQueryChange,
        modifier = modifier
            .then(expandOnTapModifier)
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) currentOnExpandedChange(true) }
            .semantics {
                onClick {
                    focusRequester.requestFocus()
                    true
                }
            },
        enabled = enabled && workaroundEnabled,
        singleLine = true,
        textStyle = inputTextStyle,
        cursorBrush = cursorBrush,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { currentOnSearch(query) }),
        interactionSource = internalInteractionSource,
        decorationBox = { innerTextField ->
            // COUI 17 SearchBarBackgroundView: the TOP_BAR material over a transparent background
            // (ToolbarMaterialEffectResolver), ?couiColorBar without it. A collapsing capsule tints
            // toward the divider line, so it drops the material for its flat colour.
            Box(
                modifier = Modifier
                    .then(
                        if (collapse == null) {
                            Modifier
                        } else {
                            // The capsule shrinks and simultaneously widens past its own edges,
                            // tinting to the divider color, so a fully collapsed bar reads as the
                            // list's divider line.
                            Modifier
                                .height(collapse.capsuleHeight)
                                .layout { measurable, constraints ->
                                    val extra = collapse.extraHorizontalWidth.roundToPx()
                                    val placeable = measurable.measure(
                                        constraints.offset(horizontal = extra * 2),
                                    )
                                    // Report the resting width so the row keeps its place; the
                                    // capsule simply draws wider than the space it occupies.
                                    layout(placeable.width - extra * 2, placeable.height) {
                                        placeable.place(-extra, 0)
                                    }
                                }
                        },
                    )
                    .then(
                        if (capsuleMaterial) {
                            // Observes the Main pass after every child, so a down an inner button
                            // (any clickable icon in the capsule other than the clear button) has
                            // consumed switches the touch motion and spotlight off for that gesture.
                            Modifier.pointerInput(Unit) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    val onQuickDelete = quickDeleteDown[0] == down.id
                                    quickDeleteDown[0] = null
                                    if (down.isConsumed && !onQuickDelete) {
                                        innerButtonPressed = true
                                        do {
                                            val event = awaitPointerEvent()
                                        } while (event.changes.any { it.pressed })
                                        innerButtonPressed = false
                                    }
                                }
                            }
                        } else {
                            Modifier
                        },
                    )
                    .then(
                        if (expandMotion == null) {
                            Modifier
                        } else {
                            // showInToolBar: the capsule is clipped to a circle of its height that
                            // widens into the full capsule (updateIconExpandClippingWidth), from the
                            // start edge, while its content keeps the full width.
                            Modifier.layout { measurable, constraints ->
                                val full = constraints.maxWidth
                                iconExpandFullWidth[0] = full
                                val width = if (constraints.hasBoundedWidth) {
                                    iconExpandCapsuleWidth(
                                        height = SearchBarDefaults.InputFieldMinHeight.toPx(),
                                        fullWidth = full.toFloat(),
                                        progress = expandMotion.progress(),
                                    ).roundToInt().coerceAtLeast(0)
                                } else {
                                    full
                                }
                                val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
                                layout(full, placeable.height) { placeable.placeRelative(0, 0) }
                            }
                        },
                    )
                    .then(
                        if (capsuleMaterial) {
                            Modifier.materialPanel(
                                interactionSource = internalInteractionSource,
                                cornerRadius = COUIRoundCorner.Full,
                                motionEnabled = enabled,
                                spotLightSuppressed = { innerButtonPressed || iconExpand?.interactive?.invoke() == false },
                                touchMotionSuppressed = { innerButtonPressed || iconExpand?.interactive?.invoke() == false },
                            )
                        } else {
                            Modifier.background(
                                color = collapse?.capsuleColor ?: colors.backgroundColor,
                                shape = capsuleShape,
                            )
                        },
                    )
                    .then(
                        if (expandMotion == null) {
                            Modifier
                        } else {
                            Modifier
                                .clipToBounds()
                                .layout { measurable, constraints ->
                                    val full = iconExpandFullWidth[0]
                                    val placeable = if (constraints.hasBoundedWidth && full > 0) {
                                        measurable.measure(constraints.copy(minWidth = full, maxWidth = full))
                                    } else {
                                        measurable.measure(constraints)
                                    }
                                    layout(constraints.maxWidth, placeable.height) { placeable.placeRelative(0, 0) }
                                }
                        },
                    ),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (collapse == null) {
                                Modifier
                            } else {
                                Modifier.graphicsLayer { alpha = collapse.contentAlpha }
                            },
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    actualLeadingIcon()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            // coui_search_bar_edittext_end_gap after the edit frame.
                            .padding(end = SearchBarDefaults.EditTextEndGap)
                            .heightIn(min = SearchBarDefaults.InputFieldMinHeight)
                            .then(
                                if (expandMotion == null) {
                                    Modifier
                                } else {
                                    // showInToolBar: the edit text fades in with the expansion.
                                    Modifier.graphicsLayer { alpha = expandMotion.progress().coerceIn(0f, 1f) }
                                },
                            ),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        val mergedLabelStyle = remember(textStyle) {
                            TextStyle(fontSize = SearchBarDefaults.InputFieldFontSize).merge(textStyle)
                        }
                        // Hint text: coui_search_view_hint_color_selector, couiColorLabelSecondary
                        // and couiColorLabelTertiary while disabled. The edit text is single line,
                        // ellipsized at the end (setMaxLines(1), TruncateAt.END).
                        if (hints.isEmpty()) {
                            Text(
                                text = labelText,
                                style = mergedLabelStyle,
                                color = hintColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        } else {
                            SearchBarHintCarousel(
                                hints = hints,
                                active = query.isEmpty(),
                                repeatCount = hintRepeatCount,
                                style = mergedLabelStyle,
                                color = hintColor,
                                onHintChange = onHintChange,
                                modifier = Modifier.matchParentSize(),
                            )
                        }
                        innerTextField()
                    }
                    actualTrailingIcons()
                }
            }
        },
    )

    // Whether the bar was expanded when the last expanded change ran, so a bar that starts out
    // collapsed keeps the query it was given.
    val wasExpanded = remember { booleanArrayOf(expanded) }
    LaunchedEffect(expanded) {
        if (expanded) {
            // Explicitly request focus when expanded. On API 26-27, the workaround disables
            // the TextField when collapsed, so the initial tap doesn't grant focus — this
            // ensures the keyboard appears after the TextField becomes enabled again.
            focusRequester.requestFocus()
        } else {
            // openSoftInputAndNotifyStateChanged(STATE_NORMAL): the text is cleared at once
            // (setText(null)) as the bar starts back to its normal state.
            if (wasExpanded[0] && query.isNotEmpty()) currentOnQueryChange("")
            if (focused) focusManager.clearFocus()
        }
        wasExpanded[0] = expanded
    }
}

/** A 36dp slot for an inner button of the [InputField] (`coui_search_bar_inner_search_icon_size`). */
@Composable
private fun SearchBarInnerButtonSlot(button: @Composable () -> Unit) {
    Box(
        modifier = Modifier.size(SearchBarDefaults.InnerButtonSize),
        contentAlignment = Alignment.Center,
    ) {
        button()
    }
}

/**
 * The rotating hints of an [InputField] (COUI `COUIHintAnimationLayout`): two text slots, the
 * current hint and the incoming one, swapped every [HINT_INTERVAL_MILLIS].
 */
@Composable
private fun SearchBarHintCarousel(
    hints: List<String>,
    active: Boolean,
    repeatCount: Int,
    style: TextStyle,
    color: Color,
    onHintChange: ((Int) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var index by remember(hints) { mutableIntStateOf(0) }
    var previous by remember(hints) { mutableIntStateOf(-1) }
    // COUI mCurrentPlayTime: counted across pauses (typing), reset only with new hints.
    val plays = remember(hints) { intArrayOf(0) }
    val outProgress = remember { Animatable(1f) }
    val inProgress = remember { Animatable(1f) }
    var containerHeight by remember { mutableIntStateOf(0) }
    val currentOnHintChange by rememberUpdatedState(onHintChange)
    val extraPx = with(LocalDensity.current) { HintTranslateExtra.toPx() }
    // Typing stops the rotation for good (mAnimationIsWorking = false): clearing the text puts the
    // current hint back without posting the next change; only a new attach resumes it.
    var stopped by remember(hints) { mutableStateOf(false) }
    LaunchedEffect(hints, active) {
        if (!active) stopped = true
    }

    LaunchedEffect(hints, active, stopped, repeatCount) {
        if (!active || stopped || hints.size < 2) {
            previous = -1
            inProgress.snapTo(1f)
            return@LaunchedEffect
        }
        while (true) {
            delay(HINT_INTERVAL_MILLIS)
            // applyHintChangeAnimation: past the repeat count the rotation pauses on the current hint.
            if (plays[0] >= repeatCount) break
            plays[0]++
            previous = index
            index = (index + 1) % hints.size
            currentOnHintChange?.invoke(index)
            outProgress.snapTo(0f)
            inProgress.snapTo(0f)
            coroutineScope {
                launch { outProgress.animateTo(1f, tween(HINT_ANIMATION_MILLIS, easing = LinearEasing)) }
                launch {
                    delay(HINT_IN_DELAY_MILLIS)
                    inProgress.animateTo(1f, tween(HINT_ANIMATION_MILLIS, easing = LinearEasing))
                }
            }
            previous = -1
        }
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { containerHeight = it.height },
        contentAlignment = Alignment.CenterStart,
    ) {
        if (!active) return@Box
        val outgoing = previous
        if (outgoing in hints.indices) {
            Text(
                text = hints[outgoing],
                style = style,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.graphicsLayer {
                    val travel = (containerHeight - size.height) / 2f + extraPx
                    val p = outProgress.value
                    translationY = -travel * HintTranslateEasing.transform(p)
                    alpha = 1f - HintFadeEasing.transform(p)
                },
            )
        }
        Text(
            text = hints[index.coerceIn(hints.indices)],
            style = style,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.graphicsLayer {
                val travel = (containerHeight - size.height) / 2f + extraPx
                val p = inProgress.value
                translationY = travel * (1f - HintTranslateEasing.transform(p))
                alpha = HintFadeEasing.transform(p)
            },
        )
    }
}

/** COUIHintAnimationLayout `INTERVAL_TIME`. */
private const val HINT_INTERVAL_MILLIS = 3000L

/** COUIHintAnimationLayout `DURATION_TIME`. */
private const val HINT_ANIMATION_MILLIS = 250

/** COUIHintAnimationLayout `IN_DELAY_TIME`. */
private const val HINT_IN_DELAY_MILLIS = 150L

/** COUI coui_search_bar_animation_translate_extra. */
private val HintTranslateExtra = 5.dp

/** COUIHintAnimationLayout `TRANSLATEINTERPOLATOR`. */
private val HintTranslateEasing = CubicBezierEasing(0.3f, 0f, 0.2f, 1f)

/** COUIEaseInterpolator, the hint fade. */
private val HintFadeEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/**
 * Lifts a search bar into the title bar area while [expanded], after the COUI
 * `COUISearchViewAnimate` standard-type enter / exit (`mSearchViewOffsetEnterAnimator` /
 * `ExitAnimator`): the bar's top moves from where it is laid out to [targetTop] (window
 * coordinates) over 450ms on COUIMoveEaseInterpolator, and back when [expanded] turns false.
 *
 * Apply it to the [SearchBar] (or its row) under a title it replaces, and hide or fade that title
 * yourself; the keyboard opens through the [InputField] focus request as before. Only the bar is
 * translated (drawn and hit-tested at the lifted position); the layout of the page below it does not
 * move, so content that should follow the bar can read the same progress with
 * [SearchBarDefaults.LiftAnimationSpec].
 *
 * @param expanded Whether the bar is lifted.
 * @param targetTop The window y the top of the bar moves to, for example the status bar height.
 */
fun Modifier.searchBarLift(expanded: Boolean, targetTop: Dp = 0.dp): Modifier = this then SearchBarLiftElement(expanded, targetTop)

private data class SearchBarLiftElement(
    val expanded: Boolean,
    val targetTop: Dp,
) : ModifierNodeElement<SearchBarLiftNode>() {
    override fun create(): SearchBarLiftNode = SearchBarLiftNode(expanded, targetTop)

    override fun update(node: SearchBarLiftNode) {
        node.update(expanded, targetTop)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "searchBarLift"
        properties["expanded"] = expanded
        properties["targetTop"] = targetTop
    }
}

private class SearchBarLiftNode(
    private var expanded: Boolean,
    private var targetTop: Dp,
) : Modifier.Node(),
    LayoutModifierNode,
    GlobalPositionAwareModifierNode {
    private val progress = Animatable(if (expanded) 1f else 0f)

    /** The window y of the bar's resting top the current placement translates from. */
    private var restingTop = Float.NaN

    fun update(expanded: Boolean, targetTop: Dp) {
        if (this.targetTop != targetTop) {
            this.targetTop = targetTop
            invalidatePlacement()
        }
        if (this.expanded != expanded) {
            this.expanded = expanded
            if (isAttached) {
                // The resting top may have gone stale while the bar rested (see onGloballyPositioned).
                invalidatePlacement()
                coroutineScope.launch {
                    progress.animateTo(if (expanded) 1f else 0f, SearchBarDefaults.LiftAnimationSpec)
                }
            }
        }
    }

    override fun onAttach() {
        coroutineScope.launch { progress.snapTo(if (expanded) 1f else 0f) }
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        val targetTopPx = targetTop.toPx()
        return layout(placeable.width, placeable.height) {
            // The resting position of the bar, before the lift translation of its own layer.
            val top = coordinates?.positionInWindow()?.y ?: targetTopPx
            restingTop = top
            placeable.placeWithLayer(0, 0) {
                translationY = (targetTopPx - top) * progress.value
            }
        }
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        // An ancestor that moves (a scrolling page, a collapsing header) does not run this placement
        // again: catch the new resting top here so the lifted bar stays at targetTop. A resting bar
        // has no translation to correct.
        if (progress.value != 0f && coordinates.positionInWindow().y != restingTop) invalidatePlacement()
    }
}

/** Contains default values used by [SearchBar] and [InputField]. */
object SearchBarDefaults {
    /**
     * The animation of [searchBarLift]: 450ms on COUIMoveEaseInterpolator (COUISearchViewAnimate
     * `DEFAULT_SEARCH_VIEW_OFFSET_CHANGE_DURATION` / `_INTERPOLATOR`).
     */
    val LiftAnimationSpec: AnimationSpec<Float> = tween(durationMillis = 450, easing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f))

    /**
     * The default inside margin of the [SearchBar]: the responsive horizontal padding (an unspecified
     * width, see [responsiveHorizontalPadding]) x (`coui_search_view_height` 52dp - 40dp) / 2.
     */
    val InsideMargin = DpSize(Dp.Unspecified, 6.dp)

    /** COUI `coui_search_bar_responsive_horizontal_padding_compat`: compact windows. */
    val HorizontalPaddingCompact = 16.dp

    /** COUI `coui_search_bar_responsive_horizontal_padding_medium`: medium windows. */
    val HorizontalPaddingMedium = 24.dp

    /** COUI `coui_search_bar_responsive_horizontal_padding_expanded`: expanded windows. */
    val HorizontalPaddingExpanded = 40.dp

    /**
     * The responsive horizontal padding of a [SearchBar] [width] wide in a window [windowHeight]
     * tall, COUI `COUISearchBar.configResponsive`: [HorizontalPaddingCompact],
     * [HorizontalPaddingMedium] or [HorizontalPaddingExpanded] as `COUIResponsiveUtils` classifies the
     * bar's measured width as a small, medium or large screen.
     */
    fun responsiveHorizontalPadding(width: Dp, windowHeight: Dp): Dp = when (COUILayoutGrid.screenTier(width, windowHeight)) {
        COUILayoutGrid.ScreenTier.Small -> HorizontalPaddingCompact
        COUILayoutGrid.ScreenTier.Medium -> HorizontalPaddingMedium
        COUILayoutGrid.ScreenTier.Large -> HorizontalPaddingExpanded
    }

    /** Size of the navigation and outer buttons. COUI `coui_search_bar_outer_button_width` (40dp). */
    val OuterButtonSize = 40.dp

    /**
     * Gap between the capsule and each outer button. COUI
     * `coui_search_bar_outer_button_gap_between_background` (12dp).
     */
    val OuterButtonGap = 12.dp

    /** Gap after the navigation button. COUI `coui_search_bar_navigation_button_end_gap` (12dp). */
    val NavigationButtonEndGap = 12.dp

    /**
     * The default minimum height of the [InputField] background capsule.
     * COUI `coui_search_bar_normal_background_height` (40dp).
     */
    val InputFieldMinHeight = 40.dp

    /**
     * The default font size for the [InputField] text and label.
     * COUI `coui_search_view_input_text_size` (15sp, via `inputTextSize` on the search bar style).
     */
    val InputFieldFontSize = 15.sp

    /**
     * The start padding for the default leading icon: the COUI 4dp icon container start gap plus
     * (36dp container - 20dp glyph) / 2.
     */
    val LeadingIconStartPadding = 12.dp

    /**
     * The start padding for the default leading icon while the bar expands out of a toolbar icon
     * ([ToolbarFloatingSearchBar]): COUI `coui_search_bar_icon_expand_search_icon_start_gap` (2dp)
     * plus (36dp container - 20dp glyph) / 2, so the glyph centres in the capsule's starting circle.
     */
    val IconExpandLeadingIconStartPadding = 10.dp

    /**
     * The end padding for the default leading icon: (36dp container - 20dp glyph) / 2, equal to
     * COUI `coui_search_view_hint_padding_start`.
     */
    val LeadingIconEndPadding = 8.dp

    /** The gap after the edit text. COUI `coui_search_bar_edittext_end_gap` (4dp). */
    val EditTextEndGap = 4.dp

    /**
     * The gap closing the inner buttons (clear, inner primary and secondary) and around a
     * non-instant functional button. COUI `coui_search_bar_non_instant_search_inner_gap` (4dp).
     */
    val InnerButtonGap = 4.dp

    /**
     * The slot size of the clear button and the inner buttons.
     * COUI `coui_search_bar_inner_search_icon_size` (36dp).
     */
    val InnerButtonSize = 36.dp

    /**
     * The divider before a non-instant functional button: COUI
     * `coui_search_bar_functional_button_divider_width` (1dp) x `_height` (16dp).
     */
    val FunctionalDividerWidth = 1.dp

    /** See [FunctionalDividerWidth]. */
    val FunctionalDividerHeight = 16.dp

    /**
     * The padding of a [CancelButton] inside a non-instant capsule, COUI
     * `SearchBarFunctionButton.applyNonInstantSearchButtonInsets`: `text_ripple_bg_padding_horizontal`
     * (12dp) and `_vertical` (4dp).
     */
    val NonInstantCancelPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)

    /**
     * The font size of the [CancelButton] label. COUI `couiTextAppearanceButton`, the style
     * `initFunctionButton` applies to the functional button.
     */
    val CancelFontSize = 14.sp

    /**
     * The horizontal padding of the [CancelButton] label inside its capsule. COUI 17
     * `coui_search_bar_functional_button_text_padding_horizontal`, which `SearchBarFunctionButton`
     * applies to the instant-search button.
     */
    val CancelPaddingHorizontal = 16.dp

    /**
     * The height of the [CancelButton] capsule. COUI 17 `SearchBarFunctionButton` matches the search
     * capsule: `coui_search_bar_normal_background_height`.
     */
    val CancelHeight = 40.dp

    /** The widest a [CancelButton] may grow. COUI `coui_search_bar_functional_button_max_width`. */
    val CancelMaxWidth = 152.dp

    /**
     * The visual circle size of the default clear button, matching the 9dp-radius circle of
     * ic_edit_text_delete_search_view.
     */
    val ClearButtonVisualSize = 18.dp

    /**
     * Creates a [SearchBarColors] with the default colors of the [InputField] and [CancelButton],
     * following COUI 17 COUISearchBar: `?couiColorBar` capsule fill (the colour the glass material
     * falls back to), couiColorLabelSecondary hint, couiColorSecondNeutral glyph (baked into
     * `coui_search_view_icon_normal`), couiColorHintNeutral clear circle and the
     * `coui_search_function_button_text_color` (couiColorLabelPrimary) cancel label. Disabled, the
     * hint (`coui_search_view_hint_color_selector`), the glyph (`coui_search_view_icon_disable`) and
     * the cancel label all turn couiColorLabelTertiary.
     */
    @Composable
    fun searchBarColors(
        backgroundColor: Color = barColor(),
        labelColor: Color = COUITheme.colorScheme.labelSecondary,
        disabledLabelColor: Color = COUITheme.colorScheme.disabledNeutral,
        iconColor: Color = COUITheme.colorScheme.labelSecondary,
        disabledIconColor: Color = COUITheme.colorScheme.disabledNeutral,
        clearIconColor: Color = COUITheme.colorScheme.labelTertiary,
        cancelTextColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledCancelTextColor: Color = COUITheme.colorScheme.disabledNeutral,
    ): SearchBarColors = remember(
        backgroundColor,
        labelColor,
        disabledLabelColor,
        iconColor,
        disabledIconColor,
        clearIconColor,
        cancelTextColor,
        disabledCancelTextColor,
    ) {
        SearchBarColors(
            backgroundColor = backgroundColor,
            labelColor = labelColor,
            disabledLabelColor = disabledLabelColor,
            iconColor = iconColor,
            disabledIconColor = disabledIconColor,
            clearIconColor = clearIconColor,
            cancelTextColor = cancelTextColor,
            disabledCancelTextColor = disabledCancelTextColor,
        )
    }

    /**
     * `?couiColorBar` (`coui_color_bar`: `#FAFAFA` light, `#1E1E1E` dark), the fill
     * `ToolbarMaterialEffectResolver.getToolbarBackgroundColor` gives the search capsule and the
     * cancel capsule without the material.
     */
    @Composable
    private fun barColor(): Color = COUITheme.colorScheme.bar
}

/**
 * Colors used by the [InputField] and [CancelButton] of a [SearchBar].
 *
 * @param backgroundColor the capsule fill when the material is off, and the colour a collapsing
 *   capsule starts from.
 * @param labelColor the color of the label (hint) text.
 * @param disabledLabelColor the color of the label (hint) text while the [InputField] is disabled.
 * @param iconColor the tint of the default leading search icon.
 * @param disabledIconColor the tint of the default leading search icon while disabled.
 * @param clearIconColor the fill color of the default clear button circle.
 * @param cancelTextColor the color of the [CancelButton] label.
 * @param disabledCancelTextColor the color of the [CancelButton] label while disabled.
 */
@Immutable
data class SearchBarColors(
    val backgroundColor: Color,
    val labelColor: Color,
    val disabledLabelColor: Color,
    val iconColor: Color,
    val disabledIconColor: Color,
    val clearIconColor: Color,
    val cancelTextColor: Color,
    val disabledCancelTextColor: Color,
)

/**
 * How a [SearchBar] behaves when it is expanded, COUI `COUISearchBar.setSearchAnimateType`.
 */
enum class SearchBarType {
    /**
     * `TYPE_INSTANT_SEARCH`, the default: expanding animates the capsule narrowing for the
     * functional button that grows in after it, while a lone outer button fades out.
     */
    Instant,

    /**
     * `TYPE_NON_INSTANT_SEARCH`: the state changes without animation, the functional button sits
     * inside the capsule behind a divider while expanded, and the navigation button and up to two
     * outer buttons stay beside the capsule.
     */
    NonInstant,
}

/**
 * The trailing button a COUI search bar shows while expanded.
 *
 * On COUI 17 `SearchBarFunctionButton` is its own 40dp capsule: the same top-bar glass material as
 * the search capsule (`?couiColorBar` without it), 16dp horizontal text padding and a 14sp medium
 * couiColorLabelPrimary label. It appears as soon as the bar expands, whether or not a query has
 * been typed, and the class deliberately leaves the click unwired — the host decides. Settings
 * collapses the bar, and collapsing clears the query, so the net effect there is clear + collapse +
 * hide the keyboard.
 *
 * Inside a [SearchBar] `outsideEndAction` the label fades in with the edit-state progress while the
 * capsule reaches full opacity within the first fifth of it, as `COUISearchBar` does for a blurred
 * button. In a [SearchBarType.NonInstant] bar it sits inside the search capsule instead and is a
 * plain text button (`applyNonInstantSearchButtonInsets`): no capsule or material, 12dp x 4dp
 * padding and the default press indication.
 *
 * @param onClick the callback invoked when the button is clicked.
 * @param modifier the [Modifier] to be applied to the button.
 * @param text the label of the button.
 * @param enabled whether the button responds to clicks.
 * @param material whether the capsule takes the COUI 17 top-bar glass material (see
 *   [COUIMaterialEffect.materialPanel]). When `false` it is a flat [SearchBarColors.backgroundColor]
 *   capsule. Defaults to [LocalCOUIMaterialEffectEnabled].
 * @param colors the [SearchBarColors] resolving the label and fallback fill colors.
 */
@Composable
fun CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Cancel",
    enabled: Boolean = true,
    material: Boolean = LocalCOUIMaterialEffectEnabled.current,
    colors: SearchBarColors = SearchBarDefaults.searchBarColors(),
) {
    val currentOnClick by rememberUpdatedState(onClick)
    val interactionSource = remember { MutableInteractionSource() }
    val textAlpha = LocalSearchBarActionTextAlpha.current
    val iconExpand = LocalSearchBarIconExpand.current
    val inCapsule = LocalSearchBarActionInCapsule.current
    // coui_search_function_button_text_color: couiColorLabelPrimary, disabled couiColorLabelTertiary.
    val labelColor = if (enabled) colors.cancelTextColor else colors.disabledCancelTextColor
    // SearchBarFunctionButton: a smooth round rect at half the height with weight 1.0.
    val capsuleShape = if (isSquircleEnabled()) {
        remember { SquircleShape(COUIRoundCorner.Full, COUIRoundCorner.CapsuleWeight) }
    } else {
        CircleShape
    }
    // The material draws its own press feedback.
    val indication = if (material && !inCapsule) null else LocalIndication.current
    Box(
        modifier = modifier
            .widthIn(max = SearchBarDefaults.CancelMaxWidth)
            .then(
                if (inCapsule) {
                    // Non-instant: a wrap-content text button with a mask ripple and no material.
                    Modifier
                } else if (material) {
                    Modifier.materialPanel(
                        interactionSource = interactionSource,
                        cornerRadius = COUIRoundCorner.Full,
                        motionEnabled = enabled,
                        spotLightSuppressed = { iconExpand?.interactive?.invoke() == false },
                        touchMotionSuppressed = { iconExpand?.interactive?.invoke() == false },
                    )
                } else {
                    Modifier.background(color = colors.backgroundColor, shape = capsuleShape)
                },
            )
            .then(
                // The instant-search button is measured at exactly the capsule height.
                if (inCapsule) Modifier else Modifier.height(SearchBarDefaults.CancelHeight),
            )
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = indication,
                onClick = { currentOnClick() },
            )
            .then(
                if (inCapsule) {
                    Modifier.padding(SearchBarDefaults.NonInstantCancelPadding)
                } else {
                    Modifier.padding(horizontal = SearchBarDefaults.CancelPaddingHorizontal)
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            modifier = if (textAlpha == null) {
                Modifier
            } else {
                Modifier.graphicsLayer { alpha = textAlpha() }
            },
            color = labelColor,
            fontSize = SearchBarDefaults.CancelFontSize,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * The extra alpha the [CancelButton] label applies inside a [SearchBar] action slot, on top of the
 * slot's own [SearchBarActionFrame.alpha]; `null` outside a [SearchBar].
 */
private val LocalSearchBarActionTextAlpha = staticCompositionLocalOf<(() -> Float)?> { null }

/**
 * The functional button a [SearchBarType.NonInstant] [SearchBar] hands to its [InputField] while
 * expanded, to show inside the capsule; `null` otherwise.
 */
private val LocalSearchBarInnerAction = staticCompositionLocalOf<(@Composable () -> Unit)?> { null }

/** Whether a [CancelButton] sits inside a non-instant capsule rather than beside the capsule. */
private val LocalSearchBarActionInCapsule = staticCompositionLocalOf { false }

/** COUI 17 `StateChangedToEditParamsHigh` / `StateChangedToNormalParamsHigh`: response 0.5, bounce 0.25. */
internal val StateChangeSpring = TouchMotion.spring(response = 0.5f, bounce = 0.25f, visibilityThreshold = 0.001f)

/** `StateChangedToEditParamsHigh` outer buttons: response 0.5, bounce 0.25. */
private val OuterToEditSpring = TouchMotion.spring(response = 0.5f, bounce = 0.25f, visibilityThreshold = 0.001f)

/** `StateChangedToNormalParamsHigh` outer buttons: response 0.4, bounce 0.3. */
private val OuterToNormalSpring = TouchMotion.spring(response = 0.4f, bounce = 0.3f, visibilityThreshold = 0.001f)

/** `StateChangedToEditParamsHigh.outerButtonEndScale`: the outer buttons shrink to 0.8 as they go. */
private val OuterToEditEndScale = 0.8f

/** `StateChangedToNormalParamsHigh.outerButtonInitScale`: the outer buttons grow back from 0.5. */
private val OuterToNormalInitScale = 0.5f

/**
 * `COUISearchBar.mFunctionalInitScale`: `functionalButtonInitScale` (to edit) and
 * `functionalButtonEndScale` (to normal) are both 0.5 in the COUI 17 high-end params.
 */
private val ActionInitScale = 0.5f

/** `COUISearchBar.updateFunctionalButtonAlpha`: a blurred button is fully opaque at this progress. */
private val ActionAlphaRemapProgress = 0.2f

/**
 * The COUI 17 starting translation of the action, `mFunctionalButtonOffsetX`: the search background
 * view's end edge minus the button's, read while the background view is laid out at its full width
 * plus 20px (`handleStateChangedAnimation`). The button therefore grows in almost in place, shifted
 * 20px outward at the start. The 60dp `functional_button_offset_distance` is ColorOS 16 only.
 */
private val ActionOffsetPx = 20f

/** Gap between the capsule and the action: COUI `coui_search_bar_functional_button_start_gap` (12dp). */
private val ActionStartGap = 12.dp

/**
 * One frame of the functional (cancel) button during the edit-state change, from the
 * `mFunBtnSpringAnimation` update listener in `COUISearchBar`. Expanding and collapsing run the
 * same function on the same spring, the progress rising from 0 to 1 or falling from 1 to 0.
 *
 * @property scale `mFunctionalInitScale + (1 - mFunctionalInitScale) * p`, 0.5 at rest both ways.
 * @property translationPx `mFunctionalButtonOffsetX * (1 - p)`, toward the end edge, raw pixels.
 * @property alpha the button alpha: `remapAlphaProgress(p, 0.2)` for a blurred (material) button,
 *   which is opaque from 20% on, else `p`.
 * @property textAlpha the extra label alpha inside the button: `setFunctionalButtonTextAlpha(p)`
 *   for a blurred button (so the label shows at `alpha * p`), else 1.
 * @property blurPx `updateBlurWithAnimationProgress(button, 1 - p)`: `30 * (1 - p)` raw pixels for a
 *   blurred button, so the opaque capsule dissolves into blur before it fades out.
 */
@Immutable
internal data class SearchBarActionFrame(
    val scale: Float,
    val translationPx: Float,
    val alpha: Float,
    val textAlpha: Float,
    val blurPx: Float,
)

/** The [SearchBarActionFrame] at edit-state [progress]; [blurred] is `isBlurEnable()`. */
internal fun searchBarActionFrame(progress: Float, blurred: Boolean): SearchBarActionFrame {
    val clamped = progress.coerceIn(0f, 1f)
    return SearchBarActionFrame(
        scale = ActionInitScale + (1f - ActionInitScale) * progress,
        translationPx = ActionOffsetPx * (1f - progress),
        alpha = if (blurred) remapAlphaProgress(clamped, ActionAlphaRemapProgress) else clamped,
        textAlpha = if (blurred) clamped else 1f,
        blurPx = if (blurred) max(1f - progress, 0f) * ActionBlurPx else 0f,
    )
}

/** `COUISearchBar.remapAlphaProgress`: `p / limit` below [limit], 1 from there on. */
private fun remapAlphaProgress(progress: Float, limit: Float): Float = if (progress < limit && limit > 0f) progress / limit else 1f

/** `updateBlurWithAnimationProgress`: the blur radius scale, in raw pixels. */
private val ActionBlurPx = 30f
