// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangedIgnoreConsumed
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect
import io.github.suqi8.coui.kmp.effect.FrameworkEdgeBlendMode
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropProvider
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.ShadowEdgePresets
import io.github.suqi8.coui.kmp.effect.ShadowEdgeShader
import io.github.suqi8.coui.kmp.effect.SpotLightShader
import io.github.suqi8.coui.kmp.effect.SpotLightState
import io.github.suqi8.coui.kmp.effect.SpotLightType
import io.github.suqi8.coui.kmp.effect.StrokeEffectParams
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.effect.splitFrameworkStroke
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.squircleBackground
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.graphicsLayerWithOverflow
import io.github.suqi8.coui.kmp.utils.layerOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * The COUI 17 floating navigation bar (`COUIBottomFloatingNavigationBar`): a 56dp capsule of 2 to 5
 * [FloatingNavigationBarItem]s, each 70 x 48dp with a 24dp icon over a 10dp label, floating 16dp
 * above the bottom inset, with an optional [expansion] view beside it.
 *
 * - **Selection**: tapping an item selects it, and sliding a finger across the capsule selects the
 *   item under it as it goes (the selection follows the finger); the item the finger ends on is
 *   committed with its `onClick` when the finger lifts, wherever it lifts, and also when a scrolling
 *   parent takes the gesture over (`COUIBottomFloatingNavigationBarMenuView.onMenuTouchMove` /
 *   `dispatchTouchEvent`). There is no indicator and no haptic feedback: the selected item simply
 *   switches to the selected colors.
 * - **Touch motion**: the whole capsule takes the `COUITouchMotionAnimator` press scale, rubber-band
 *   drag-follow and drag deformation (`BaseBottomFloatingMenuView.initTouchMotionAnim`), except while
 *   it slides sideways (`doHorizontalMoveAnim`); the items have no press mask of their own, only
 *   COUI's hover / focus mask for a pointer or the keyboard.
 * - **Material**: COUI 17 draws the capsule with the framework bottom-bar material
 *   (`TYPE_FRAMEWORK_BOTTOM_BAR`, a 25px backdrop blur, see [FloatingNavigationBarDefaults.backdropSpec]),
 *   the `TYPE_FRAMEWORK_CAPSULE_3` edge lighting and inner shadow, and a `TYPE_OPAQUE_MEDIUM_2` touch
 *   spotlight. coui-ui has no backdrop capture of its own: the blur is drawn when a
 *   [LocalMaterialBackdrop] provider is present, and then it replaces [color]. Without one the
 *   capsule keeps COUI's opaque `?couiColorBar` fallback. The edge lighting and spotlight need runtime shaders (Android 13+ and every Skia
 *   target) and are skipped elsewhere.
 * - **Item changes**: when the items no longer fit or their count changes, the capsule width and
 *   position follow on springs (response 0.55, bounce 0.35) while the items spread with it, and each
 *   label cross-fades between its old and new width (`doWidthChangeAnim`, `startWidthAnimation`).
 * - **Expansion view**: [expansion] (for example a 56dp search button) sits 12dp beside the capsule
 *   while [expansionVisible]. Showing it slides it out from behind the capsule's end on a spring
 *   (response 0.55, bounce 0.35) while it scales 0.5 -> 1 and the capsule makes room; its alpha
 *   follows how much of it the capsule still covers, and the two shapes merge like metaballs while
 *   they touch. Hiding reverses it and stops the slide as soon as the capsule covers the view
 *   (`showExpansionView` / `hideExpansionView`, `finishCollapseTranslationIfFullyOverlapped`). A
 *   [FloatingNavigationBarAccessory] used as the expansion view only takes its touch motion once it
 *   has slid out (`setExpansionViewTouchMotionAnimEnabled`).
 * - **Show / hide**: [visible] runs the COUI 17 springs: alpha (response 0.2), scale 0.8 <-> 1
 *   (response 0.35, bounce 0.35) and, with [materialEffect], a 30px foreground blur (response 0.3)
 *   (`doMenuViewShowOrHideAnim`). A hidden capsule keeps its space but takes no touches.
 *
 * @param modifier The modifier applied to the bar's container (the full-width area holding the capsule).
 * @param backgroundModifier Applied inside the capsule clip, above [color] and below the items: the
 *   place for a custom background. The COUI 17 backdrop blur comes from [LocalMaterialBackdrop].
 * @param visible Whether the capsule is shown. Changes animate with the COUI 17 show / hide springs.
 * @param expansionVisible Whether [expansion] is shown beside the capsule.
 * @param materialEffect Whether the COUI 17 material is drawn on the capsule: the edge lighting and
 *   inner shadow, the touch spotlight, the hide blur, the metaball merge with [expansion] and, with
 *   a [LocalMaterialBackdrop] provider, the backdrop blur. Defaults to
 *   [LocalCOUIMaterialEffectEnabled].
 * @param showDivider Whether to draw a thin divider ring around the capsule (not part of COUI 17).
 * @param defaultWindowInsetsPadding Whether to keep the capsule above the navigation and caption bars.
 * @param color The capsule fill. Defaults to COUI's opaque `?couiColorBar`; replaced by the backdrop
 *   blur when a [LocalMaterialBackdrop] provider is present and [materialEffect] is on.
 * @param cornerRadius The corner radius. The default [FloatingNavigationBarDefaults.CornerRadius]
 *   rounds the bar fully into the COUI 17 soft capsule; a smaller radius is drawn at the ColorOS
 *   default smoothing weight.
 * @param horizontalAlignment The alignment of the capsule within the bar's width while no
 *   [expansion] is shown. With the expansion view, COUI's own placement applies: on compact screens
 *   (narrower than 600dp) the capsule hugs the start edge and the expansion view the end edge, on
 *   wider ones the pair is centred.
 * @param horizontalOutSidePadding The minimum distance between the capsule and the bar's sides. When
 *   the items do not fit, they shrink equally.
 * @param shadowElevation The blur radius of the capsule's drop shadow; 0 disables it.
 * @param expansion An optional view shown beside the capsule while [expansionVisible], laid out at
 *   its own size and centred on a 56dp slot.
 * @param content The content of the [FloatingNavigationBar], [FloatingNavigationBarItem]s placed
 *   directly inside.
 */
@Composable
fun FloatingNavigationBar(
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    visible: Boolean = true,
    expansionVisible: Boolean = true,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
    showDivider: Boolean = false,
    defaultWindowInsetsPadding: Boolean = true,
    color: Color = FloatingNavigationBarDefaults.color(),
    cornerRadius: Dp = FloatingNavigationBarDefaults.CornerRadius,
    horizontalAlignment: Alignment.Horizontal = CenterHorizontally,
    horizontalOutSidePadding: Dp = FloatingNavigationBarDefaults.HorizontalOutSidePadding,
    shadowElevation: Dp = FloatingNavigationBarDefaults.ShadowElevation,
    expansion: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val state = remember { FloatingNavigationBarState() }
    val isDark = COUITheme.colorScheme.isDark
    val expansionActive = expansion != null && expansionVisible
    // BaseBottomFloatingMenuView: with the material the backdrop blur is the fill and the
    // background turns transparent; without a backdrop the opaque fill stays.
    val backdropProvider = if (materialEffect) LocalMaterialBackdrop.current else null
    val fill = if (backdropProvider != null) Color.Transparent else color

    // initNaviMenuAnim: the capsule bounds and the expansion slot follow their layout targets on
    // the melt-ball springs (doHorizontalMoveAnim / doWidthChangeAnim /
    // handleExpansionViewTranslationXAnim); a new container width snaps (mSkipNextLayoutAnimation).
    LaunchedEffect(state) {
        var last: FloatingNavigationBarTargets? = null
        snapshotFlow { state.targets }.filterNotNull().collect { target ->
            val previous = last
            last = target
            if (previous == null || previous.availableWidth != target.availableWidth) {
                state.menuLeft.snapTo(target.menuLeft)
                state.menuWidth.snapTo(target.menuWidth)
                state.slotLeft.snapTo(target.slotLeft)
                state.initialized = true
            } else {
                if (previous.menuLeft != target.menuLeft) launch { state.menuLeft.animateTo(target.menuLeft, BoundsSpring) }
                if (previous.menuWidth != target.menuWidth) launch { state.menuWidth.animateTo(target.menuWidth, BoundsSpring) }
                if (previous.slotLeft != target.slotLeft) {
                    launch {
                        // finishCollapseTranslationIfFullyOverlapped: once the capsule covers the
                        // hiding view completely, the slide ends at its target, so the bounce
                        // cannot carry the view back out.
                        state.slotLeft.animateToUntilCovered(target.slotLeft, BoundsSpring) {
                            !target.expansionActive && state.expansionOverlapRatio() >= 1f
                        }
                    }
                }
            }
        }
    }
    // doExpansionViewAlphaScaleAnim: the scale springs 0.5 <-> 1; the alpha follows the overlap.
    // A bar composed with the expansion view already shown starts at full scale, as an
    // unanimated showExpansionView(false).
    LaunchedEffect(expansionActive) {
        val target = if (expansionActive) 1f else FloatingNavigationBarDefaults.ExpansionHiddenScale
        if (!state.expansionStarted) {
            state.expansionStarted = true
            state.expansionScale.snapTo(target)
        } else {
            state.expansionScale.animateTo(
                targetValue = target,
                animationSpec = TouchMotion.spring(response = 0.55f, bounce = 0.35f, visibilityThreshold = 0.001f),
            )
        }
    }

    val neckShaders = remember(materialEffect) {
        if (materialEffect && isRuntimeShaderSupported()) List(2) { RuntimeShader(SearchBarMetaballShader.Source) } else null
    }
    val neckFilters = if (fill.alpha < 1f) {
        val spec = FloatingNavigationBarDefaults.backdropSpec(isDark)
        listOf(spec.layer1 to spec.mode1, spec.layer2 to spec.mode2)
    } else {
        null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (defaultWindowInsetsPadding) {
                    Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                        .windowInsetsPadding(WindowInsets.captionBar.only(WindowInsetsSides.Bottom))
                } else {
                    Modifier
                },
            )
            // COUIBottomFloatingNavigationBar: 16dp side padding on compact screens.
            .padding(horizontal = horizontalOutSidePadding),
    ) {
        val outsidePaddingPx = with(LocalDensity.current) { horizontalOutSidePadding.roundToPx() }
        Layout(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (neckShaders != null && expansion != null) {
                        Modifier.drawWithContent {
                            drawContent()
                            val capsule = state.capsuleRect
                            val slot = state.expansionRect(state.expansionScale.value)
                            with(SearchBarMetaballShader) {
                                drawNeck(
                                    shaders = neckShaders,
                                    capsule = capsule,
                                    action = slot,
                                    alpha = state.expansionAlpha(),
                                    filters = neckFilters,
                                    fallback = color,
                                )
                            }
                        }
                    } else {
                        Modifier
                    },
                ),
            content = {
                Box(Modifier.layoutId(CAPSULE_LAYOUT_ID)) {
                    FloatingNavigationBarCapsule(
                        state = state,
                        visible = visible,
                        materialEffect = materialEffect,
                        showDivider = showDivider,
                        color = fill,
                        cornerRadius = cornerRadius,
                        shadowElevation = shadowElevation,
                        backdropProvider = backdropProvider,
                        backgroundModifier = backgroundModifier,
                        content = content,
                    )
                }
                // COUI draws the large icon as a floating view in the bar's overlay that clicks its
                // item: this target catches taps on the part above the capsule.
                Box(
                    modifier = Modifier
                        .layoutId(LARGE_ICON_LAYOUT_ID)
                        .then(
                            if (visible) {
                                Modifier.pointerInput(state) {
                                    detectTapGestures {
                                        state.largeIconItem()?.takeIf { it.enabled }?.onClick?.invoke()
                                    }
                                }
                            } else {
                                Modifier
                            },
                        ),
                )
                if (expansion != null) {
                    Box(
                        modifier = Modifier
                            .layoutId(EXPANSION_LAYOUT_ID)
                            // Room for the shadow of a material button (IconButton) while the
                            // fade renders the view offscreen.
                            .graphicsLayerWithOverflow(COUIMaterialEffect.ShadowOverflow) {
                                val scale = state.expansionScale.value
                                scaleX = scale
                                scaleY = scale
                                alpha = state.expansionAlpha()
                            }
                            .then(if (expansionActive) Modifier else Modifier.clearAndSetSemantics { }),
                    ) {
                        // setExpansionViewTouchMotionAnimEnabled: a FloatingNavigationBarAccessory
                        // shown here only takes its touch motion once it has slid out.
                        CompositionLocalProvider(
                            LocalFloatingNavigationAccessoryTouchMotion provides (expansionActive && !state.slotLeft.isRunning),
                        ) {
                            expansion()
                        }
                    }
                }
            },
        ) { measurables, constraints ->
            val available = constraints.maxWidth
            val top = FloatingNavigationBarDefaults.VerticalOutSidePadding.roundToPx()
            val barHeight = FloatingNavigationBarDefaults.Height.roundToPx()
            val slot = FloatingNavigationBarDefaults.ExpansionSlotSize.roundToPx()
            val spacing = FloatingNavigationBarDefaults.ExpansionSpacing.roundToPx()
            val capsuleMeasurable = measurables.first { it.layoutId == CAPSULE_LAYOUT_ID }
            val expansionMeasurable = measurables.firstOrNull { it.layoutId == EXPANSION_LAYOUT_ID }
            val largeIconMeasurable = measurables.first { it.layoutId == LARGE_ICON_LAYOUT_ID }

            val menuAvailable = if (expansionActive) max(0, available - slot - spacing) else available
            val capsule = capsuleMeasurable.measure(Constraints(maxWidth = menuAvailable, maxHeight = barHeight))
            val menuWidth = state.capsuleTargetWidth
            val compact = available + outsidePaddingPx * 2 < FloatingNavigationBarDefaults.CompactWidthBreakpoint.roundToPx()
            val (menuLeft, slotLeft) = floatingNavigationBarPositions(
                availableWidth = available,
                menuWidth = menuWidth,
                slotSize = slot,
                spacing = spacing,
                expansionActive = expansionActive,
                compact = compact,
                alignment = when (horizontalAlignment) {
                    Alignment.Start -> -1
                    Alignment.End -> 1
                    else -> 0
                },
            )
            val target = FloatingNavigationBarTargets(
                availableWidth = available,
                menuLeft = menuLeft.toFloat(),
                menuWidth = menuWidth.toFloat(),
                slotLeft = slotLeft.toFloat(),
                expansionActive = expansionActive,
            )
            if (state.targets != target) state.targets = target

            val initialized = state.initialized
            val drawnLeft = if (initialized) state.menuLeft.value.roundToInt() else menuLeft
            val drawnSlotLeft = if (initialized) state.slotLeft.value else slotLeft.toFloat()
            // A hidden expansion view that has settled is INVISIBLE: not placed, no touches.
            val expansionPlaceable = expansionMeasurable?.measure(Constraints())
            val expansionSettledHidden = !expansionActive && !state.expansionScale.isRunning &&
                state.expansionScale.value <= FloatingNavigationBarDefaults.ExpansionHiddenScale + 0.001f
            val height = top * 2 + barHeight
            val rtl = layoutDirection == LayoutDirection.Rtl
            val capsuleX = if (rtl) available - drawnLeft - capsule.width else drawnLeft
            // The large icon's bounds above the capsule, in the row.
            val largeIcon = state.largeIconBounds.firstOrNull { it != null }
            val largeIconPlaceable = largeIconMeasurable.measure(
                if (largeIcon != null) {
                    Constraints.fixed(largeIcon.width.roundToInt(), (-largeIcon.top).coerceIn(0f, largeIcon.height).roundToInt())
                } else {
                    Constraints.fixed(0, 0)
                },
            )
            layout(available, height) {
                capsule.placeRelative(drawnLeft, top)
                if (largeIcon != null) {
                    largeIconPlaceable.place((capsuleX + largeIcon.left).roundToInt(), (top + largeIcon.top).roundToInt())
                }
                state.capsuleRect = Rect(
                    capsuleX.toFloat(),
                    top.toFloat(),
                    (capsuleX + capsule.width).toFloat(),
                    (top + capsule.height).toFloat(),
                )
                state.availableWidth = available
                state.rtl = rtl
                state.slotTop = top + (barHeight - slot) / 2f
                state.slotSize = slot.toFloat()
                if (expansionPlaceable != null) {
                    state.expansionSize = Size(
                        expansionPlaceable.width.toFloat(),
                        expansionPlaceable.height.toFloat(),
                    )
                    if (!expansionSettledHidden) {
                        val centerX = drawnSlotLeft + slot / 2f
                        expansionPlaceable.placeRelative(
                            (centerX - expansionPlaceable.width / 2f).roundToInt(),
                            top + (barHeight - expansionPlaceable.height) / 2,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun FloatingNavigationBarCapsule(
    state: FloatingNavigationBarState,
    visible: Boolean,
    materialEffect: Boolean,
    showDivider: Boolean,
    color: Color,
    cornerRadius: Dp,
    shadowElevation: Dp,
    backdropProvider: MaterialBackdropProvider?,
    backgroundModifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val reorderToken = state.reorderToken
    LaunchedEffect(reorderToken) {
        if (!state.reorderPending) return@LaunchedEffect
        state.reorderProgress.snapTo(0f)
        state.reorderPending = false
        state.reorderProgress.animateTo(1f, ReorderSpring)
    }
    val isDark = COUITheme.colorScheme.isDark
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val dividerColor = COUITheme.colorScheme.divider
    val shadowOverflow = if (shadowElevation > 0.dp) floatingNavigationBarShadow(shadowElevation).layerOverflow() else 0.dp

    // doMenuViewShowOrHideAnim, melt-ball (COUI 17) springs.
    val alpha = remember { Animatable(if (visible) 1f else 0f) }
    val scale = remember { Animatable(if (visible) 1f else FloatingNavigationBarDefaults.HiddenScale) }
    val blur = remember { Animatable(0f) }
    LaunchedEffect(visible, materialEffect) {
        launch {
            alpha.animateTo(
                targetValue = if (visible) 1f else 0f,
                animationSpec = TouchMotion.spring(response = 0.2f, bounce = 0f, visibilityThreshold = 0.001f),
            )
        }
        launch {
            scale.animateTo(
                targetValue = if (visible) 1f else FloatingNavigationBarDefaults.HiddenScale,
                animationSpec = TouchMotion.spring(response = 0.35f, bounce = 0.35f, visibilityThreshold = 0.001f),
            )
        }
        // The foreground blur only runs with the material (isBlurEffectEnable).
        if (materialEffect) {
            launch {
                blur.animateTo(
                    targetValue = if (visible) 0f else FloatingNavigationBarDefaults.HiddenBlurRadiusPx,
                    animationSpec = TouchMotion.spring(response = 0.3f, bounce = 0f, visibilityThreshold = 0.01f),
                )
            }
        } else {
            blur.snapTo(0f)
        }
    }

    val shaders = remember(materialEffect) {
        if (materialEffect && isRuntimeShaderSupported()) {
            RuntimeShader(ShadowEdgeShader.Source) to RuntimeShader(SpotLightShader.Source)
        } else {
            null
        }
    }
    // The edge-light pass of the framework stroke (see FrameworkEdgeBlendMode).
    val edgePass = remember(shaders) { shaders?.let { RuntimeShader(ShadowEdgeShader.Source) } }
    val spotlight = remember { SpotLightState() }
    val stroke = if (isDark) FloatingNavigationBarStrokeDark else FloatingNavigationBarStrokeLight
    val spotType = SpotLightType.OpaqueMedium2
    val backdropModifier = if (backdropProvider != null) {
        val spec = FloatingNavigationBarDefaults.backdropSpec(isDark)
        val backdropShape = remember(cornerRadius) { SquircleShape(cornerRadius, COUIRoundCorner.UnspecifiedWeight) }
        remember(backdropProvider, spec, backdropShape) { backdropProvider.materialBackdrop(spec, backdropShape) }
    } else {
        Modifier
    }

    Layout(
        content = {
            // The capsule fill, material and spotlight. The items sit above it unclipped, so a large
            // icon can rise out of the capsule like COUI's overlay floating icon.
            Box(
                modifier = Modifier
                    .then(
                        if (showDivider) {
                            Modifier
                                .squircleBackground(color = dividerColor, cornerRadius = cornerRadius, weight = COUIRoundCorner.UnspecifiedWeight)
                                .padding(0.75.dp)
                        } else {
                            Modifier
                        },
                    )
                    .squircleSurface(color = color, cornerRadius = cornerRadius, weight = COUIRoundCorner.UnspecifiedWeight)
                    // Inside the surface's offscreen layer: the edge light below is overlaid on what
                    // lies beneath it in this layer, so the blurred backdrop has to be drawn here.
                    .then(backdropModifier)
                    .then(backgroundModifier)
                    .then(
                        if (shaders != null && edgePass != null) {
                            Modifier.drawWithCache {
                                val radius = min(cornerRadius.toPx(), min(size.width, size.height) / 2f)
                                configureFloatingNavigationBarEdge(shaders.first, size.width, size.height, radius, stroke)
                                configureFloatingNavigationBarEdge(edgePass, size.width, size.height, radius, stroke)
                                splitFrameworkStroke(shadowPass = shaders.first, edgePass = edgePass)
                                val shadowBrush = shaders.first.asBrush()
                                val edgeBrush = edgePass.asBrush()
                                onDrawWithContent {
                                    // BaseBottomFloatingMenuView.onDraw: the spotlight, clipped to the
                                    // capsule, and the stroke are drawn above the background and below
                                    // the items.
                                    val glow = spotlight.progress.value
                                    if (glow > 0f) {
                                        val light = spotlight.position.value
                                        SpotLightShader.setUniforms(
                                            shader = shaders.second,
                                            lightX = light.x,
                                            lightY = light.y,
                                            radius = SpotLightShader.radius(glow, spotlight.extinguishing),
                                            intensity = spotType.endIntensity(isDark) * glow,
                                        )
                                        drawRect(brush = shaders.second.asBrush(), blendMode = spotType.blendMode(isDark))
                                    }
                                    // The edge shader carries its own SDF mask.
                                    drawRect(shadowBrush)
                                    drawRect(edgeBrush, blendMode = FrameworkEdgeBlendMode)
                                    drawContent()
                                }
                            }
                        } else {
                            Modifier
                        },
                    ),
            )
            CompositionLocalProvider(LocalFloatingNavigationBarState provides state) {
                content()
            }
        },
        modifier = Modifier
            // The show / hide fade and blur render the capsule offscreen: the layer reaches past
            // it so the drop shadow is not cut off.
            .graphicsLayerWithOverflow(shadowOverflow) {
                this.alpha = alpha.value
                scaleX = scale.value
                scaleY = scale.value
                val radius = blur.value
                renderEffect = if (radius > 0.01f) BlurEffect(radius, radius, TileMode.Clamp) else null
            }
            // A hidden capsule is INVISIBLE: no touches, nothing for accessibility. doHorizontalMoveAnim
            // turns the touch motion off while the capsule slides sideways.
            .then(
                if (visible) {
                    Modifier.couiTouchMotion(interactionSource = null, enabled = !state.menuLeft.isRunning)
                } else {
                    Modifier.clearAndSetSemantics { }
                },
            )
            .then(if (shadowElevation > 0.dp) Modifier.dropShadow(shape = shape, shadow = floatingNavigationBarShadow(shadowElevation)) else Modifier)
            .then(
                if (visible) {
                    Modifier.pointerInput(state, shaders != null) {
                        val spot = shaders != null
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            if (spot) scope.launch { spotlight.glow(down.position) }
                            val downItem = state.itemAt(down.position)
                            var slideItem: FloatingNavigationItemHandle? = null
                            var upPosition: Offset? = null
                            var cancelled = false
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                // A scrolling parent took the gesture (ACTION_CANCEL).
                                if (change.isConsumed && change.positionChangedIgnoreConsumed()) {
                                    cancelled = true
                                    break
                                }
                                if (!change.pressed) {
                                    upPosition = change.position
                                    break
                                }
                                if (change.position != change.previousPosition) {
                                    val position = change.position
                                    if (spot) scope.launch { spotlight.follow(position) }
                                    val inBounds = position.x in 0f..size.width.toFloat() &&
                                        position.y in 0f..size.height.toFloat()
                                    if (inBounds) {
                                        val item = state.itemAt(position)
                                        if (item != null && item.enabled && item !== state.currentSelection()) {
                                            state.slideTarget = item
                                            slideItem = item
                                        }
                                    }
                                }
                            }
                            if (spot) scope.launch { spotlight.extinguish() }
                            // A slide commits the item it ended on, wherever the finger lifts; a plain
                            // tap clicks the item under the finger. When a scrolling parent takes the
                            // gesture over (ACTION_CANCEL) the tap is dropped, but an item the slide has
                            // already selected is still committed (dispatchPendingSlideItemClickIfNeeded).
                            val target = if (cancelled) {
                                slideItem?.takeIf { it.enabled }
                            } else {
                                slideItem ?: upPosition?.let { position ->
                                    state.itemAt(position)?.takeIf { it === downItem && it.enabled }
                                }
                            }
                            target?.onClick?.invoke()
                            state.slideTarget = null
                        }
                    }
                } else {
                    Modifier
                },
            )
            .selectableGroup(),
    ) { measurables, constraints ->
        val itemMeasurables = measurables.drop(1)
        val geometry = floatingNavigationBarLayout(
            itemCount = itemMeasurables.size,
            maxWidth = constraints.maxWidth,
            itemMaxWidth = FloatingNavigationBarDefaults.ItemMaxWidth.roundToPx(),
            innerPadding = FloatingNavigationBarDefaults.HorizontalPadding.roundToPx(),
            itemSpacing = FloatingNavigationBarDefaults.ItemSpacing.roundToPx(),
        )
        if (state.capsuleTargetWidth != geometry.width) state.capsuleTargetWidth = geometry.width
        val width = if (state.initialized) state.menuWidth.value.roundToInt() else geometry.width
        val height = FloatingNavigationBarDefaults.Height.roundToPx()
        val itemHeight = min(FloatingNavigationBarDefaults.ItemHeight.roundToPx(), height)
        val background = measurables[0].measure(Constraints.fixed(max(width, 0), height))
        val handles = itemMeasurables.map { it.layoutId as? FloatingNavigationItemHandle }
        handles.forEach { handle -> if (handle != null && handle.itemWidth != geometry.itemWidth) handle.itemWidth = geometry.itemWidth }
        val placeables = itemMeasurables.map { it.measure(Constraints.fixed(geometry.itemWidth, itemHeight)) }
        state.items = handles
        // An item count change: COUI rebuilds every item view and lets them jump to the spread of
        // the new count inside the old width. Items here keep their identity, so each one starts
        // from where it was drawn and eases onto that spread with the bounds spring instead.
        if (state.lastItemCount >= 0 && handles.size != state.lastItemCount) {
            handles.forEachIndexed { index, handle ->
                if (handle != null) {
                    val newCenter = geometry.itemCenter(index, width.toFloat())
                    handle.reorderOffset = if (handle.lastCenter.isNaN()) 0f else handle.lastCenter - newCenter
                }
            }
            state.reorderPending = true
            state.reorderToken++
        }
        state.lastItemCount = handles.size
        val reorderRemaining = if (state.reorderPending) 1f else 1f - state.reorderProgress.value
        val rtl = layoutDirection == LayoutDirection.Rtl
        val largeIconSize = FloatingNavigationBarDefaults.LargeIconSize.roundToPx()
        val largeIconOverflow = FloatingNavigationBarDefaults.LargeIconOverflow.roundToPx()
        layout(width, height) {
            background.place(0, 0)
            val top = (height - itemHeight) / 2
            val bounds = ArrayList<Rect>(placeables.size)
            val largeBounds = ArrayList<Rect?>(placeables.size)
            placeables.forEachIndexed { index, placeable ->
                // doWidthChangeAnim: items keep their final width, their centres follow the drawn width.
                val handle = handles[index]
                val center = geometry.itemCenter(index, width.toFloat()) + (handle?.reorderOffset ?: 0f) * reorderRemaining
                handle?.lastCenter = center
                val x = (center - geometry.itemWidth / 2f).roundToInt()
                placeable.placeRelative(x, top)
                val left = if (rtl) width - x - geometry.itemWidth else x
                bounds += Rect(left.toFloat(), top.toFloat(), (left + geometry.itemWidth).toFloat(), (top + itemHeight).toFloat())
                largeBounds += if (handles[index]?.largeIcon == true) {
                    val iconLeft = left + (geometry.itemWidth - largeIconSize) / 2f
                    val iconTop = (top - largeIconOverflow).toFloat()
                    Rect(iconLeft, iconTop, iconLeft + largeIconSize, iconTop + largeIconSize)
                } else {
                    null
                }
            }
            state.itemBounds = bounds
            state.largeIconBounds = largeBounds
        }
    }
}

/**
 * A [FloatingNavigationBarItem] that is suitable for [FloatingNavigationBar]: a 24dp [icon] over a
 * 10dp medium [label] (COUI `COUIBottomFloatingToolbarItemView`, `STYLE_NAVI_VERTICAL`), or with
 * [largeIcon] a 40dp icon rising 14dp above the item (`STYLE_NAVI_LARGE_ICON`). The icon and label
 * switch instantly between [FloatingNavigationBarColors.selectedColor] and
 * [FloatingNavigationBarColors.unselectedColor]; there is no press mask (COUI strips the pressed state
 * of navigation items, the capsule's touch motion is the press feedback). A pointer hovering a normal
 * item or keyboard focus on it fades in COUI's hover / focus mask behind the icon and the label, a
 * pill reaching 8dp past the item's sides (`COUIMaskRippleDrawable`). The selected item offers no
 * click action to accessibility services (`setupItemAccessibility`).
 *
 * Put the items directly in the [FloatingNavigationBar] content: the bar lays them out and drives
 * their taps and slide-to-select gestures.
 *
 * @param selected Whether the item is selected.
 * @param onClick The callback when the item is tapped, or when a slide across the bar ends on it.
 * @param icon The icon of the item, tinted with the item's content color (an unspecified color
 *   leaves it untinted).
 * @param label The label of the item.
 * @param modifier The modifier to be applied to the [FloatingNavigationBarItem].
 * @param enabled Whether the item is enabled. A disabled item ignores taps and slides and hides its badge.
 * @param largeIcon Whether the icon is the 40dp large icon that rises above the capsule
 *   (coui_bottom_floating_navigation_bar_large_icon_size / _overflow).
 * @param badge The red dot on the icon (`setRedDot`): `null` for none, 0 for a plain 6dp dot at the
 *   icon's top end corner, a positive number for a number badge 4dp above the icon whose end edge
 *   lies 6 / 9 / 10dp past the icon's end for 1 / 2 / 3 digits.
 * @param colors The content colors of the item.
 */
@Composable
fun FloatingNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    largeIcon: Boolean = false,
    badge: Int? = null,
    colors: FloatingNavigationBarColors = FloatingNavigationBarDefaults.itemColors(),
) {
    val barState = LocalFloatingNavigationBarState.current
    val handle = remember { FloatingNavigationItemHandle() }
    val currentOnClick by rememberUpdatedState(onClick)
    SideEffect {
        handle.selected = selected
        handle.enabled = enabled
        handle.largeIcon = largeIcon
        handle.onClick = onClick
    }
    // While a slide is in progress the bar's slide target is the selected item.
    val slideTarget = barState?.slideTarget
    val shownSelected = if (slideTarget != null) slideTarget === handle else selected
    val contentColor = colors.contentColor(enabled = enabled, selected = shownSelected)

    val density = LocalDensity.current
    val labelStyle = remember(density) {
        TextStyle(
            fontSize = with(density) { FloatingNavigationBarDefaults.LabelTextSize.toSp() },
            fontWeight = FontWeight.Medium,
            // An unspaced TextView line: the font metrics height, 1.1719em for Roboto.
            lineHeight = 1.1719f.em,
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Proportional,
                trim = LineHeightStyle.Trim.None,
            ),
            textAlign = TextAlign.Center,
        )
    }

    // startWidthAnimation: when the item width changes, the label laid out at the wider width and
    // the one laid out at the narrower width cross-fade (shrinking: wide out at response 0.2,
    // narrow in at 0.3; growing the other way round).
    val itemWidth = handle.itemWidth
    val titleAlpha = remember { Animatable(1f) }
    val shortTitleAlpha = remember { Animatable(0f) }
    val widthChange = remember { FloatingNavigationItemWidthChange() }
    LaunchedEffect(itemWidth) {
        val previous = widthChange.lastWidth
        widthChange.lastWidth = itemWidth
        if (previous <= 0 || previous == itemWidth || itemWidth <= 0) return@LaunchedEffect
        val shrinking = itemWidth < previous
        widthChange.maxWidth = max(previous, itemWidth)
        widthChange.minWidth = min(previous, itemWidth)
        widthChange.animating = true
        coroutineScope {
            launch {
                titleAlpha.animateTo(
                    targetValue = if (shrinking) 0f else 1f,
                    animationSpec = TouchMotion.spring(response = if (shrinking) 0.2f else 0.3f, bounce = 0f, visibilityThreshold = 0.001f),
                )
            }
            launch {
                shortTitleAlpha.animateTo(
                    targetValue = if (shrinking) 1f else 0f,
                    animationSpec = TouchMotion.spring(response = if (shrinking) 0.3f else 0.2f, bounce = 0f, visibilityThreshold = 0.001f),
                )
            }
        }
        widthChange.animating = false
    }

    val interactionSource = remember { MutableInteractionSource() }
    val showBadge = badge != null && enabled
    Layout(
        modifier = Modifier
            .layoutId(handle)
            .then(modifier)
            // STYLE_NAVI_VERTICAL: the COUIMaskRippleDrawable hover / focus mask, reaching
            // coui_bottom_floating_navigation_bar_item_bg_horizontal_padding past the item's sides
            // with fully round ends. The large icon style turns the drawable off.
            .navigationItemStateMask(
                interactionSource = interactionSource,
                enabled = enabled && !largeIcon,
                cornerRadius = FloatingNavigationBarDefaults.ItemHeight / 2,
                horizontalInset = -FloatingNavigationBarDefaults.ItemMaskHorizontalOutset,
            )
            .hoverable(interactionSource = interactionSource, enabled = enabled)
            .focusable(enabled = enabled, interactionSource = interactionSource)
            .onKeyEvent { event ->
                val activate = event.key == Key.Enter || event.key == Key.NumPadEnter || event.key == Key.Spacebar
                if (enabled && activate && event.type == KeyEventType.KeyUp) {
                    currentOnClick()
                    true
                } else {
                    false
                }
            }
            .semantics(mergeDescendants = true) {
                role = Role.Tab
                this.selected = shownSelected
                // setupItemAccessibility: the selected item offers no click action.
                if (!shownSelected) {
                    onClick {
                        if (enabled) currentOnClick()
                        enabled
                    }
                }
                if (!enabled) disabled()
            },
        content = {
            Image(
                imageVector = icon,
                contentDescription = null,
                colorFilter = if (contentColor.isSpecified) ColorFilter.tint(contentColor) else null,
            )
            // The calibration below moves the label down by the font padding Android's TextView
            // adds above the ascent (includeFontPadding: Roboto's yMax 2163 vs ascent 1900 units).
            Text(
                text = label,
                modifier = Modifier
                    .padding(top = with(density) { (FloatingNavigationBarDefaults.LabelTextSize.toPx() * FONT_PADDING_TOP_EM).toDp() })
                    .graphicsLayer { alpha = titleAlpha.value },
                color = contentColor,
                style = labelStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = label,
                modifier = Modifier
                    .padding(top = with(density) { (FloatingNavigationBarDefaults.LabelTextSize.toPx() * FONT_PADDING_TOP_EM).toDp() })
                    .graphicsLayer { alpha = shortTitleAlpha.value },
                color = contentColor,
                style = labelStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (showBadge) Badge(count = badge ?: 0)
        },
    ) { measurables, constraints ->
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else FloatingNavigationBarDefaults.ItemMaxWidth.roundToPx()
        val height = if (constraints.hasBoundedHeight) constraints.maxHeight else FloatingNavigationBarDefaults.ItemHeight.roundToPx()
        val iconSize = (if (largeIcon) FloatingNavigationBarDefaults.LargeIconSize else FloatingNavigationBarDefaults.IconSize).roundToPx()
        val iconPlaceable = measurables[0].measure(Constraints.fixed(iconSize, iconSize))
        val animating = widthChange.animating
        val titleWidth = if (animating) widthChange.maxWidth else width
        val shortWidth = if (animating) widthChange.minWidth else width
        val titlePlaceable = measurables[1].measure(Constraints(minWidth = titleWidth, maxWidth = titleWidth))
        val shortPlaceable = measurables[2].measure(Constraints(minWidth = shortWidth, maxWidth = shortWidth))
        val badgePlaceable = measurables.getOrNull(3)?.measure(Constraints())
        val iconTopMargin = FloatingNavigationBarDefaults.IconTopPadding.roundToPx()
        // layoutNaviVertical / layoutNaviLargeIcon: the label always starts below the 24dp icon slot.
        val titleTop = iconTopMargin + FloatingNavigationBarDefaults.IconSize.roundToPx() + FloatingNavigationBarDefaults.LabelTopSpacing.roundToPx()
        layout(width, height) {
            val iconLeft = (width - iconSize) / 2
            val iconTop = if (largeIcon) -FloatingNavigationBarDefaults.LargeIconOverflow.roundToPx() else iconTopMargin
            iconPlaceable.placeRelative(iconLeft, iconTop)
            titlePlaceable.placeRelative((width - titlePlaceable.width) / 2, titleTop)
            shortPlaceable.placeRelative((width - shortPlaceable.width) / 2, titleTop)
            if (badgePlaceable != null) {
                val count = badge ?: 0
                val iconRight = iconLeft + iconSize
                when {
                    // layoutNaviLargeIcon: the dot hugs the large icon's end, at the item top.
                    largeIcon -> badgePlaceable.placeRelative(iconRight - badgePlaceable.width, max(0, iconTop))

                    // layoutRedDotRelativeToIcon, point mode: the icon's top end corner.
                    count <= 0 -> badgePlaceable.placeRelative(iconRight - badgePlaceable.width, iconTop)

                    // Number mode: 4dp above the icon, the end edge past the icon's end.
                    else -> {
                        val overflow = when {
                            count < 10 -> FloatingNavigationBarDefaults.BadgeEndOverflowOneDigit
                            count < 100 -> FloatingNavigationBarDefaults.BadgeEndOverflowTwoDigits
                            else -> FloatingNavigationBarDefaults.BadgeEndOverflowThreeDigits
                        }.roundToPx()
                        badgePlaceable.placeRelative(
                            iconRight + overflow - badgePlaceable.width,
                            iconTop - FloatingNavigationBarDefaults.BadgeTopOverflow.roundToPx(),
                        )
                    }
                }
            }
        }
    }
}

/**
 * The content colors of a [FloatingNavigationBarItem].
 *
 * @param selectedColor The icon and label color of the selected item.
 * @param unselectedColor The icon and label color of an unselected item.
 * @param disabledColor The icon and label color of a disabled item.
 */
@Immutable
data class FloatingNavigationBarColors(
    val selectedColor: Color,
    val unselectedColor: Color,
    val disabledColor: Color,
) {
    @Stable
    internal fun contentColor(enabled: Boolean, selected: Boolean): Color = when {
        !enabled -> disabledColor
        selected -> selectedColor
        else -> unselectedColor
    }
}

/** Contains default values used by [FloatingNavigationBar] and [FloatingNavigationBarItem]. */
object FloatingNavigationBarDefaults {
    /**
     * The default corner radius: [COUIRoundCorner.Full], which rounds the bar fully at any height.
     * COUI 17 `BaseBottomFloatingMenuView.updateSmoothRoundPath` takes half the content height and
     * calls `addSmoothRoundRect` without a weight, which draws the soft capsule.
     */
    val CornerRadius = COUIRoundCorner.Full

    /**
     * The minimum horizontal distance between the capsule and the bar's sides
     * (coui_bottom_floating_navigation_bar_padding_horizontal).
     */
    val HorizontalOutSidePadding = 16.dp

    /**
     * The space above and below the capsule: the 56dp capsule centred in the 88dp
     * coui_bottom_floating_navigation_bar_min_height_without_padding_vertical container.
     */
    val VerticalOutSidePadding = 16.dp

    /**
     * The blur radius of the drop shadow: the COUI 17 caustic shadow
     * (`ShadowUtils.SHADOW_CAUSTIC_DOWN`, elevation 24dp, alpha 45, light 320dp above at 1600dp).
     * The caustic light layer (`BaseBottomFloatingMenuView.setCausticShadowEffect`:
     * `coui_caustic_color_2` #42FFFFFF, `_level4` layout offsets 0/0/0/4.9dp, sdf width 10.4dp) is
     * not reproduced: COUI only hands these values to the Oplus `RenderNode.setOutlineCausticShadow*`
     * natives (`OplusMaterialUtil`), and the light itself is drawn inside the vendor `libhwui`; no
     * shader for it ships in the app or the framework jar.
     */
    val ShadowElevation = 24.dp

    /** The capsule height (coui_bottom_floating_navigation_bar_height). */
    val Height = 56.dp

    /** The horizontal padding inside the capsule (coui_bottom_floating_navigation_bar_item_padding). */
    val HorizontalPadding = 12.dp

    /** The spacing between items (coui_bottom_floating_navigation_bar_item_space). */
    val ItemSpacing = 8.dp

    /**
     * The item width (coui_bottom_floating_navigation_bar_item_max_width). When the items do not fit,
     * they shrink equally (`BaseBottomFloatingMenuView.onMeasure`).
     */
    val ItemMaxWidth = 70.dp

    /** The item height (coui_bottom_floating_navigation_bar_item_height), centred in the capsule. */
    val ItemHeight = 48.dp

    /**
     * How far an item's hover / focus mask reaches past its sides
     * (coui_bottom_floating_navigation_bar_item_bg_horizontal_padding); its ends are fully round.
     */
    val ItemMaskHorizontalOutset = 8.dp

    /** The icon size (coui_floating_toolbar_item_icon_width / _height). */
    val IconSize = 24.dp

    /** The icon's distance from the item top (coui_bottom_floating_navigation_bar_item_icon_top_margin). */
    val IconTopPadding = 4.dp

    /** The gap between icon and label (coui_bottom_floating_navigation_bar_item_icon_title_gap_vertical). */
    val LabelTopSpacing = 2.dp

    /**
     * The label text size: `dp2px(10)` (`COUIBottomFloatingToolbarItemView.applyNaviVerticalStyle`),
     * so the label does not follow the font scale.
     */
    val LabelTextSize = 10.dp

    /** The large icon size (coui_bottom_floating_navigation_bar_large_icon_size). */
    val LargeIconSize = 40.dp

    /**
     * How far the large icon rises above its item (coui_bottom_floating_navigation_bar_large_icon_overflow):
     * 14dp, so 10dp above the capsule.
     */
    val LargeIconOverflow = 14.dp

    /** How far a number badge rises above the icon (coui_bottom_floating_navigation_bar_red_dot_top_overflow). */
    val BadgeTopOverflow = 4.dp

    /** How far a 1-digit badge's end lies past the icon's end (..._red_dot_right_overflow_1_digit). */
    val BadgeEndOverflowOneDigit = 6.dp

    /** How far a 2-digit badge's end lies past the icon's end (..._red_dot_right_overflow_2_digit). */
    val BadgeEndOverflowTwoDigits = 9.dp

    /** How far a 3-digit badge's end lies past the icon's end (..._red_dot_right_overflow_3_digit). */
    val BadgeEndOverflowThreeDigits = 10.dp

    /** The slot the expansion view is centred on: a square of the capsule height (`mFakeViewSize`). */
    val ExpansionSlotSize = 56.dp

    /**
     * The gap between the capsule and the expansion view
     * (coui_bottom_floating_navigation_bar_Minimum_spacing_with_slide_view).
     */
    val ExpansionSpacing = 12.dp

    /** The expansion view scale when hidden (`EXPANSION_VIEW_MIN_SCALE`). */
    val ExpansionHiddenScale = 0.5f

    /** Below this container width the bar uses COUI's compact placement (`WindowWidthSizeClass.Compact`). */
    val CompactWidthBreakpoint = 600.dp

    /** The capsule scale when hidden (`MENU_VIEW_MIN_SCALE`). */
    val HiddenScale = 0.8f

    /** The capsule's own blur radius in raw pixels when hidden (`MENU_VIEW_MAX_BLUR_RADIUS`). */
    val HiddenBlurRadiusPx = 30f

    /**
     * The opaque capsule fill: COUI's non-blur fallback `coui_tab_navigation_view_bg` = `?couiColorBar`,
     * `#FAFAFA` in the light scheme and `#1E1E1E` in the dark one.
     */
    @Composable
    fun color(): Color = COUITheme.colorScheme.bar

    /**
     * The COUI 17 bottom-bar material, `COUIMaterialBlurEffect.TYPE_FRAMEWORK_BOTTOM_BAR`: a 25px
     * backdrop blur, then `#B3666666` COLOR_DODGE + `#99F5F5F5` LUMINOSITY in the light scheme and
     * `#80666666` OVERLAY + `#B3262626` LUMINOSITY in the dark one. [FloatingNavigationBar] draws it
     * through the [io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop] provider.
     *
     * @param isDark Whether the dark scheme is active.
     */
    fun backdropSpec(isDark: Boolean): MaterialBackdropSpec = if (isDark) BackdropDark else BackdropLight

    /**
     * The item content colors (`COUIBottomFloatingToolbarItemView.resolveBottomNavDefaultTitleColorStateList`):
     * selected `?couiColorLabelPrimary` (`#E6000000` / `#E6FFFFFF`), unselected and pressed
     * `?couiColorLabelPrimaryVariant` (`#B8000000` / `#B8FFFFFF`), disabled `?couiColorLabelTertiary`
     * (`#42000000` / `#4DFFFFFF`).
     *
     * @param selectedColor The icon and label color of the selected item.
     * @param unselectedColor The icon and label color of an unselected item.
     * @param disabledColor The icon and label color of a disabled item.
     */
    @Composable
    fun itemColors(
        selectedColor: Color = COUITheme.colorScheme.labelPrimary,
        unselectedColor: Color = COUITheme.colorScheme.labelPrimary.copy(alpha = LABEL_PRIMARY_VARIANT_ALPHA),
        disabledColor: Color = COUITheme.colorScheme.disabledNeutral,
    ): FloatingNavigationBarColors = remember(selectedColor, unselectedColor, disabledColor) {
        FloatingNavigationBarColors(
            selectedColor = selectedColor,
            unselectedColor = unselectedColor,
            disabledColor = disabledColor,
        )
    }

    /** `?couiColorLabelPrimaryVariant` alpha, `0xB8`. */
    private const val LABEL_PRIMARY_VARIANT_ALPHA = 0xB8 / 255f

    private val BackdropLight = MaterialBackdropSpec(
        blurRadiusPx = 25f,
        layer1 = Color(0xB3666666),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0x99F5F5F5),
        mode2 = BlendMode.Luminosity,
    )

    private val BackdropDark = MaterialBackdropSpec(
        blurRadiusPx = 25f,
        layer1 = Color(0x80666666),
        mode1 = BlendMode.Overlay,
        layer2 = Color(0xB3262626),
        mode2 = BlendMode.Luminosity,
    )
}

// COUIMaterialStrokeEffect FRAMEWORK_CAPSULE_PARAMS_3_LIGHT / _DARK (TYPE_FRAMEWORK_CAPSULE_3).
internal val FloatingNavigationBarStrokeLight = StrokeEffectParams(0.25f, 2.5f, 0f, 0.1f, 1f, 1f)
internal val FloatingNavigationBarStrokeDark = StrokeEffectParams(0.8f, 2.5f, 0f, 0.03f, 1f, 1f)

/**
 * The Roboto font padding Android's `includeFontPadding` adds above the ascent, in em:
 * `(yMax 2163 - ascender 1900) / 2048`.
 */
private const val FONT_PADDING_TOP_EM = (2163f - 1900f) / 2048f

/** The melt-ball bounds springs (`HIGH_END_HORIZONTAL_MOVE` / `WIDTH_CHANGE` / `EXPANSION_VIEW_TRANSLATION_X`). */
private val BoundsSpring = TouchMotion.spring(response = 0.55f, bounce = 0.35f, visibilityThreshold = 0.5f)

/** [BoundsSpring] on a 0..1 progress, carrying the items from their old places on a count change. */
private val ReorderSpring = TouchMotion.spring(response = 0.55f, bounce = 0.35f, visibilityThreshold = 0.001f)

private const val CAPSULE_LAYOUT_ID = "floatingNavigationBarCapsule"
private const val EXPANSION_LAYOUT_ID = "floatingNavigationBarExpansion"
private const val LARGE_ICON_LAYOUT_ID = "floatingNavigationBarLargeIcon"

/**
 * The caustic shadow approximated as a drop shadow of [radius] at alpha 45/255, offset down by
 * `radius x 320 / 1600` (the light sits 320dp above the capsule at a 1600dp radius).
 */
internal fun floatingNavigationBarShadow(radius: Dp): Shadow = Shadow(
    radius = radius,
    color = Color.Black,
    offset = DpOffset(0.dp, radius * 0.2f),
    alpha = 45f / 255f,
)

/**
 * Feeds [ShadowEdgeShader] the `TYPE_FRAMEWORK_CAPSULE_3` stroke of the capsule: edge and inner
 * shadow on, capsule style, weight 1.0 (`setCornerType(2).setWeight(1.0f)`).
 */
internal fun Density.configureFloatingNavigationBarEdge(
    shader: RuntimeShader,
    width: Float,
    height: Float,
    cornerRadius: Float,
    params: StrokeEffectParams,
) {
    shader.setFloatUniform("u_commonArray", ShadowEdgeShader.commonArray(width, height, cornerRadius, weight = 1f))
    shader.setFloatUniform("u_shadow", 1f)
    shader.setFloatUniform(
        "u_shadowArray",
        ShadowEdgeShader.shadowArray(
            spec = ShadowEdgePresets.CapsuleShadow,
            width = width,
            height = height,
            fadeWidthScale = params.shadowFadeScale,
            fadeAlphaInScale = params.shadowFadeIn,
            fadeAlphaOutScale = params.shadowFadeOut,
            density = density,
        ),
    )
    shader.setFloatUniform("u_edge", 1f)
    shader.setFloatUniform(
        "u_edgeArray",
        ShadowEdgeShader.edgeArray(
            spec = ShadowEdgePresets.CapsuleEdge,
            width = width,
            height = height,
            lineWidth = params.edgeWidth.dp.toPx(),
            lineAlpha = params.edgeAlpha,
            lineAngle = params.edgeAngle,
            density = density,
        ),
    )
}

/**
 * The horizontal geometry of a [FloatingNavigationBar] capsule (`BaseBottomFloatingMenuView.onMeasure`
 * / `onLayout`), in pixels.
 *
 * @property itemWidth The width every item gets.
 * @property width The capsule width.
 */
@Immutable
internal class FloatingNavigationBarLayout(
    val itemWidth: Int,
    val width: Int,
    private val itemCount: Int,
    private val innerPadding: Int,
    private val itemSpacing: Int,
) {
    /** The start offset of item [index], before any right-to-left mirroring. */
    fun itemStart(index: Int): Int = innerPadding + index * (itemWidth + itemSpacing)

    /**
     * `calculateItemCenterPointX`: the centre of item [index] when the capsule is drawn [drawnWidth]
     * wide, the items spreading with the capsule while it springs to [width].
     */
    fun itemCenter(index: Int, drawnWidth: Float): Float {
        if (itemCount <= 0) return 0f
        val gaps = (itemCount - 1) * itemSpacing
        val slot = (drawnWidth - innerPadding * 2 - gaps) / itemCount
        return innerPadding + slot * (index + 0.5f) + itemSpacing * index
    }
}

/**
 * `BaseBottomFloatingMenuView.onMeasure`: each item is [itemMaxWidth] wide when all of them fit into
 * [maxWidth] with the [innerPadding] on both ends and [itemSpacing] between them; otherwise the
 * items shrink equally to fill [maxWidth].
 */
internal fun floatingNavigationBarLayout(
    itemCount: Int,
    maxWidth: Int,
    itemMaxWidth: Int,
    innerPadding: Int,
    itemSpacing: Int,
): FloatingNavigationBarLayout {
    if (itemCount <= 0) return FloatingNavigationBarLayout(0, 0, 0, innerPadding, itemSpacing)
    val gaps = (itemCount - 1) * itemSpacing
    val full = itemMaxWidth * itemCount + innerPadding * 2 + gaps
    return if (maxWidth == Constraints.Infinity || full <= maxWidth) {
        FloatingNavigationBarLayout(itemMaxWidth, full, itemCount, innerPadding, itemSpacing)
    } else {
        val itemWidth = ((maxWidth - innerPadding * 2 - gaps) / itemCount).coerceAtLeast(0)
        FloatingNavigationBarLayout(itemWidth, itemWidth * itemCount + innerPadding * 2 + gaps, itemCount, innerPadding, itemSpacing)
    }
}

/**
 * The capsule and expansion-slot start offsets, before right-to-left mirroring
 * (`COUIBottomFloatingNavigationBar.layoutWhenExpansionViewInactive` /
 * `computeExpansionActiveHorizontalPositions`):
 * - inactive: the capsule by [alignment] (-1 start, 0 centre, 1 end), the slot hidden behind its end;
 * - active, [compact]: the capsule at the start edge, the slot at the end edge;
 * - active, wide: capsule, [spacing] and slot centred as a group.
 *
 * @return the capsule start and the slot start.
 */
internal fun floatingNavigationBarPositions(
    availableWidth: Int,
    menuWidth: Int,
    slotSize: Int,
    spacing: Int,
    expansionActive: Boolean,
    compact: Boolean,
    alignment: Int,
): Pair<Int, Int> = when {
    !expansionActive -> {
        val menuLeft = when {
            alignment < 0 -> 0
            alignment > 0 -> availableWidth - menuWidth
            else -> (availableWidth - menuWidth) / 2
        }
        menuLeft to menuLeft + menuWidth - slotSize
    }

    compact -> 0 to availableWidth - slotSize

    else -> {
        val groupLeft = (availableWidth - (menuWidth + spacing + slotSize)) / 2
        groupLeft to groupLeft + menuWidth + spacing
    }
}

/**
 * `BottomFloatingOverlapHelper.alphaFromOverlapRatio` with `ALPHA_FADE_START_OVERLAP_PERCENT`: fully
 * opaque until half the view is covered, then fading out linearly to fully covered.
 */
internal fun floatingNavigationExpansionAlpha(overlapRatio: Float): Float {
    val start = 0.5f
    return if (overlapRatio <= start) 1f else (1f - (overlapRatio - start) / (1f - start)).coerceIn(0f, 1f)
}

/** The layout targets the bar's bounds springs chase, in pixels, and whether the expansion view is shown. */
@Immutable
internal data class FloatingNavigationBarTargets(
    val availableWidth: Int,
    val menuLeft: Float,
    val menuWidth: Float,
    val slotLeft: Float,
    val expansionActive: Boolean,
)

/** What the [FloatingNavigationBar] knows about one of its [FloatingNavigationBarItem]s. */
@Stable
internal class FloatingNavigationItemHandle {
    var selected: Boolean = false
    var enabled: Boolean = true
    var largeIcon: Boolean = false
    var onClick: () -> Unit = {}

    /** The item width the capsule measures it at, written by the measure pass. */
    var itemWidth by mutableIntStateOf(0)

    /** The centre the item was last drawn at, from the capsule start; NaN before its first placement. */
    var lastCenter = Float.NaN

    /** How far the item was drawn from its new spread when the item count changed. */
    var reorderOffset = 0f
}

/** The label cross-fade widths of a [FloatingNavigationBarItem] (`mMaxTitleWidth` / `mMinTitleWidth`). */
@Stable
private class FloatingNavigationItemWidthChange {
    var lastWidth = 0
    var maxWidth by mutableIntStateOf(0)
    var minWidth by mutableIntStateOf(0)
    var animating by mutableStateOf(false)
}

/** The item registry, slide-to-select state and bounds animations of one [FloatingNavigationBar]. */
@Stable
internal class FloatingNavigationBarState {
    /** The items in layout order, written by the measure pass. */
    var items: List<FloatingNavigationItemHandle?> = emptyList()

    /** The item bounds in the capsule's coordinates, written by the placement pass. */
    var itemBounds: List<Rect> = emptyList()

    /**
     * The large-icon bounds of each item (null for a normal item), in the capsule's coordinates.
     * Read by the bar row's measure pass, which places the tap target above the capsule.
     */
    var largeIconBounds by mutableStateOf<List<Rect?>>(emptyList())

    /**
     * The item a slide has moved the selection to, shown as selected instead of the caller's
     * selection until the finger lifts (`COUIBottomFloatingNavigationBarMenuView.setSelectedItem`).
     */
    var slideTarget by mutableStateOf<FloatingNavigationItemHandle?>(null)

    /**
     * The capsule width the items need, written by the capsule's measure pass and read by the bar
     * row's, so the row re-lays out when it changes.
     */
    var capsuleTargetWidth by mutableIntStateOf(0)

    /** The latest layout targets; the bounds springs chase them. */
    var targets by mutableStateOf<FloatingNavigationBarTargets?>(null)

    /** Whether the bounds springs hold a value yet. */
    var initialized by mutableStateOf(false)

    /** The item count of the previous measure pass; -1 before the first. */
    var lastItemCount = -1

    /** Bumped on every item count change; the capsule restarts [reorderProgress] on it. */
    var reorderToken by mutableIntStateOf(0)

    /** Whether a count change waits for [reorderProgress] to restart (the items hold their offsets). */
    var reorderPending = false

    /** How far the items have eased from their old places onto the new spread, 0 to 1. */
    val reorderProgress = Animatable(1f, visibilityThreshold = 0.001f)

    val menuLeft = Animatable(0f, visibilityThreshold = 0.5f)
    val menuWidth = Animatable(0f, visibilityThreshold = 0.5f)
    val slotLeft = Animatable(0f, visibilityThreshold = 0.5f)
    val expansionScale = Animatable(FloatingNavigationBarDefaults.ExpansionHiddenScale, visibilityThreshold = 0.001f)

    /** Whether the expansion scale has taken its first value. */
    var expansionStarted = false

    // Placement results in the bar row's coordinates.
    var capsuleRect = Rect.Zero
    var availableWidth = 0
    var rtl = false
    var slotTop = 0f
    var slotSize = 0f
    var expansionSize = Size.Zero

    /** `findChildViewUnder`: the item whose bounds (or large icon) contain [position], if any. */
    fun itemAt(position: Offset): FloatingNavigationItemHandle? {
        val bounds = itemBounds
        for (index in bounds.indices) {
            val rect = bounds[index]
            val large = largeIconBounds.getOrNull(index)
            if (rect.containsInclusive(position) || large?.containsInclusive(position) == true) {
                return items.getOrNull(index)
            }
        }
        return null
    }

    /** The first large-icon item (`getLargeIconItemView`), if any. */
    fun largeIconItem(): FloatingNavigationItemHandle? = largeIconBounds.indexOfFirst { it != null }.let { items.getOrNull(it) }

    /** The item currently shown as selected. */
    fun currentSelection(): FloatingNavigationItemHandle? = slideTarget ?: items.firstOrNull { it?.selected == true }

    /**
     * The expansion view's bounds in the bar row at [scale], centred on its slot. Reads the bounds
     * springs, so a draw or layer block calling it follows them.
     */
    fun expansionRect(scale: Float): Rect {
        val size = expansionSize
        if (size.width <= 0f || size.height <= 0f) return Rect.Zero
        val slotStart = if (initialized) slotLeft.value else (targets?.slotLeft ?: 0f)
        val centerStart = slotStart + slotSize / 2f
        val centerX = if (rtl) availableWidth - centerStart else centerStart
        val centerY = slotTop + slotSize / 2f
        val halfWidth = size.width * scale / 2f
        val halfHeight = size.height * scale / 2f
        return Rect(centerX - halfWidth, centerY - halfHeight, centerX + halfWidth, centerY + halfHeight)
    }

    /**
     * `computeExpansionOverlapRatioWithMenu`: how much of the expansion view's scaled bounds the
     * drawn capsule covers, from 0 to 1; 0 while the view has no size.
     */
    fun expansionOverlapRatio(): Float {
        val view = expansionRect(expansionScale.value)
        val area = view.width * view.height
        if (area <= 0f) return 0f
        val menuStart = if (initialized) menuLeft.value else (targets?.menuLeft ?: 0f)
        val menuWidthValue = if (initialized) menuWidth.value else (targets?.menuWidth ?: 0f)
        val left = if (rtl) availableWidth - menuStart - menuWidthValue else menuStart
        val menu = Rect(left, capsuleRect.top, left + menuWidthValue, capsuleRect.bottom)
        val overlapWidth = max(0f, min(view.right, menu.right) - max(view.left, menu.left))
        val overlapHeight = max(0f, min(view.bottom, menu.bottom) - max(view.top, menu.top))
        return min(1f, overlapWidth * overlapHeight / area)
    }

    /**
     * `updateExpansionViewAlphaByOverlap`: the expansion view's alpha from how much of its scaled
     * bounds the capsule covers.
     */
    fun expansionAlpha(): Float {
        val view = expansionRect(expansionScale.value)
        if (view.width * view.height <= 0f) return 0f
        return floatingNavigationExpansionAlpha(expansionOverlapRatio())
    }
}

/**
 * Springs to [target] like `animateTo`, but jumps straight to it once [covered] holds: the hiding
 * expansion or accessory view is then entirely behind the capsule, and COUI ends its slide there
 * (`finishCollapseTranslationIfFullyOverlapped`) so the spring's bounce cannot show it again.
 */
internal suspend fun Animatable<Float, AnimationVector1D>.animateToUntilCovered(
    target: Float,
    animationSpec: AnimationSpec<Float>,
    covered: () -> Boolean,
) {
    try {
        animateTo(target, animationSpec) {
            if (covered()) throw FloatingNavigationCoveredException()
        }
    } catch (_: FloatingNavigationCoveredException) {
        snapTo(target)
    }
}

/** Stops [animateToUntilCovered]'s spring once the capsule covers the moving view. */
private class FloatingNavigationCoveredException : CancellationException("The capsule covers the hiding view")

private fun Rect.containsInclusive(position: Offset): Boolean = position.x >= left && position.x <= right && position.y >= top && position.y <= bottom

private val LocalFloatingNavigationBarState = staticCompositionLocalOf<FloatingNavigationBarState?> { null }
