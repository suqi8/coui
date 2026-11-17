// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BasicTooltipDefaults
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.toPath
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.addSquircleRect
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.jvm.JvmInline
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

/**
 * The preferred position of a tooltip relative to its anchor. [Start] / [End] resolve to [Left] /
 * [Right] following the layout direction (COUI `ALIGN_START` / `ALIGN_END`).
 *
 * A COUI tooltip above / below its anchor moves to the other side when it does not fit and, when
 * neither side fits, takes the side with more room and scrolls its content. A tooltip to the left /
 * right never flips sideways: it narrows to the room on that side, and moves above the anchor when it
 * would reach the bottom of the window.
 */
@JvmInline
value class TooltipAnchorPosition private constructor(private val value: Int) {
    override fun toString(): String = when (this) {
        Above -> "Above"
        Below -> "Below"
        Left -> "Left"
        Right -> "Right"
        Start -> "Start"
        End -> "End"
        else -> "Invalid"
    }

    companion object {
        val Above = TooltipAnchorPosition(0)
        val Below = TooltipAnchorPosition(1)
        val Left = TooltipAnchorPosition(2)
        val Right = TooltipAnchorPosition(3)
        val Start = TooltipAnchorPosition(4)
        val End = TooltipAnchorPosition(5)
    }
}

/**
 * The three COUI 17 tooltip bubble styles, the three `COUIIBubbleStyle` implementations.
 */
enum class TooltipStyle {
    /**
     * `COUIDefaultBubbleStyleImpl`: a text bubble. Its look depends on the [TooltipMode]: an accent
     * bubble with white text and a close icon, or a white "detail floating" info bubble.
     */
    Default,

    /** `COUIIconBubbleStyleImpl`: a white card with a 40dp icon, a title, a body and a dismiss button. */
    Icon,

    /** `COUIImageBubbleStyleImpl`: a 260dp white card with an image, a title, a body and a dismiss button. */
    Image,
}

/** The two modes of the [TooltipStyle.Default] bubble (COUI `MODE_TOOLTIPS` / `MODE_INFO`). */
enum class TooltipMode {
    /** `MODE_TOOLTIPS`: the accent (`couiColorTips`) bubble with white text and a close icon. */
    Tooltips,

    /** `MODE_INFO`: the "detail floating" bubble, secondary text on `couiColorBackgroundTop`. */
    Info,
}

/**
 * Receiver scope for the [TooltipBox] tooltip content. It exposes the requested anchor position and
 * the anchor bounds.
 */
@Stable
sealed interface TooltipScope {
    /** The preferred [TooltipAnchorPosition] of the tooltip relative to its anchor. */
    val positioning: TooltipAnchorPosition

    /** The anchor layout coordinates, or null before the anchor is placed. */
    fun obtainAnchorBounds(): LayoutCoordinates?
}

/**
 * The shared placement state of one COUI tooltip. The position provider writes the anchor bounds,
 * the viewport and the resolved [placement]; the bubble reads them to size itself and to draw its
 * arrow, and writes its metrics and the effective side back for the provider.
 */
@Stable
internal class TooltipLayoutState(density: Float) {
    var anchorBounds by mutableStateOf<IntRect?>(null)
    var viewport by mutableStateOf(IntSize.Zero)
    var placement by mutableStateOf<TooltipPlacement?>(null)

    // Written during composition / measure and read by the position provider in the same frame.
    var position: TooltipAnchorPosition = TooltipAnchorPosition.Above
    var showArrow: Boolean = true
    var offset: IntOffset = IntOffset.Zero
    var arrowFollowsOffset: Boolean = false
    var metrics: TooltipMetrics = TooltipMetrics.of(density, 13f, 13f, 13f, 13f)
    var fallbackSide: TooltipSide? = null
    var anchorContainer: TooltipAnchorContainer? = null
    private val density = density

    /** `WindowSpacingControlHelper.getAnchorViewTypeEnum` + `getAnchorViewSpacing` for [anchor]. */
    fun anchorSpacing(anchor: IntRect): TooltipAnchorSpacing {
        val container = anchorContainer ?: return TooltipAnchorSpacing.Normal
        return when (container.type) {
            TooltipAnchorType.Toolbar -> TooltipAnchorSpacing.toolbar(density)

            TooltipAnchorType.Navigation -> {
                val coordinates = container.coordinates?.takeIf { it.isAttached }
                val containerTop = coordinates?.positionInWindow()?.y?.roundToInt() ?: anchor.top
                TooltipAnchorSpacing.navigation(density, containerTop, anchor.top)
            }

            TooltipAnchorType.Normal -> TooltipAnchorSpacing.Normal
        }
    }
}

/**
 * The kind of container a tooltip anchor sits in, which changes the anchor gap the way
 * `WindowSpacingControlHelper.AnchorViewTypeEnum` does: an anchor inside a toolbar ([Toolbar]) or a
 * bottom navigation bar ([Navigation]) pulls its bubble window into the bar instead of keeping the
 * plain 5.4dp gap.
 */
enum class TooltipAnchorType {
    /** A plain anchor (`NORMAL`). */
    Normal,

    /** An anchor inside a toolbar (`TOOLBAR`, `coui_tooltips_toolbar_margin` -10.1dp). */
    Toolbar,

    /** An anchor inside a bottom navigation bar (`NAVIGATION`, `coui_tooltips_navigation_margin` -3.3dp). */
    Navigation,
}

/**
 * A toolbar / bottom navigation container that [TooltipBox] anchors inside it detect, the Compose
 * stand-in for COUI walking up the View tree to a `Toolbar` / `BottomNavigationView`. [TopAppBar],
 * [SmallTopAppBar] and [NavigationBar] provide one; wrap a custom bar with
 * [ProvideTooltipAnchorContainer] to get the same spacing.
 */
@Stable
class TooltipAnchorContainer internal constructor(val type: TooltipAnchorType) {
    internal var coordinates: LayoutCoordinates? = null
}

/** The [TooltipAnchorContainer] tooltip anchors are placed in, or null for a plain anchor. */
val LocalTooltipAnchorContainer = staticCompositionLocalOf<TooltipAnchorContainer?> { null }

/**
 * Marks [content] as a toolbar / navigation container for the tooltips anchored inside it (see
 * [TooltipAnchorType]).
 *
 * @param type the container type.
 * @param modifier the modifier applied to the container wrapper.
 * @param content the container content.
 */
@Composable
fun ProvideTooltipAnchorContainer(
    type: TooltipAnchorType,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val container = rememberTooltipAnchorContainer(type)
    Box(modifier = modifier.tooltipAnchorContainer(container), propagateMinConstraints = true) {
        CompositionLocalProvider(LocalTooltipAnchorContainer provides container, content = content)
    }
}

/** Remembers a [TooltipAnchorContainer] of [type]; track its bounds with [tooltipAnchorContainer]. */
@Composable
internal fun rememberTooltipAnchorContainer(type: TooltipAnchorType): TooltipAnchorContainer = remember(type) { TooltipAnchorContainer(type) }

/** Records the bounds of [container] (needed by [TooltipAnchorType.Navigation]). */
internal fun Modifier.tooltipAnchorContainer(container: TooltipAnchorContainer): Modifier = onGloballyPositioned { container.coordinates = it }

private class CouiTooltipScopeImpl(
    override val positioning: TooltipAnchorPosition,
    val layoutState: TooltipLayoutState,
    val state: TooltipState,
    private val anchorBounds: () -> LayoutCoordinates?,
) : TooltipScope {
    override fun obtainAnchorBounds(): LayoutCoordinates? = anchorBounds()
}

private class CouiTooltipPositionProvider(private val layoutState: TooltipLayoutState) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        if (layoutState.anchorBounds != anchorBounds) layoutState.anchorBounds = anchorBounds
        if (layoutState.viewport != windowSize) layoutState.viewport = windowSize
        val requested = layoutState.fallbackSide ?: layoutState.position.toTooltipSide(layoutDirection)
        val placement = calculateTooltipPlacement(
            spacing = layoutState.anchorSpacing(anchorBounds),
            anchor = anchorBounds,
            viewport = windowSize,
            windowSize = popupContentSize,
            requested = requested,
            metrics = layoutState.metrics,
            showArrow = layoutState.showArrow,
            offset = layoutState.offset,
            arrowFollowsOffset = layoutState.arrowFollowsOffset,
            layoutDirection = layoutDirection,
        )
        if (layoutState.placement != placement) layoutState.placement = placement
        return placement.position
    }
}

/**
 * The state that controls the visibility of a [TooltipBox].
 *
 * Created via [rememberTooltipState]. When [isPersistent] is true (the default, as COUI tooltips have
 * no timeout) the tooltip stays visible until [dismiss] is called, its close / dismiss button is
 * tapped or, when enabled, the user taps outside. When false it is dismissed after a short timeout.
 * A global [MutatorMutex] keeps only one tooltip visible at a time.
 */
@Stable
interface TooltipState {
    /** The transition driving the tooltip's enter / exit animation. */
    val transition: MutableTransitionState<Boolean>

    /** Whether the tooltip is currently visible (including while animating out). */
    val isVisible: Boolean

    /** Whether the tooltip stays visible until explicitly dismissed. */
    val isPersistent: Boolean

    /** Shows the tooltip, suspending until it is dismissed or superseded by another tooltip. */
    suspend fun show(mutatePriority: MutatePriority = MutatePriority.Default)

    /** Dismisses the tooltip. */
    fun dismiss()

    /** Cleans up the coroutine backing [show] when the tooltip leaves the composition. */
    fun onDispose()
}

@OptIn(ExperimentalFoundationApi::class)
@Stable
private class TooltipStateImpl(
    initialIsVisible: Boolean,
    override val isPersistent: Boolean,
    private val mutatorMutex: MutatorMutex,
) : TooltipState {
    private var transitionState by mutableStateOf(MutableTransitionState(initialIsVisible))

    override val transition: MutableTransitionState<Boolean>
        get() = transitionState

    /**
     * Set by the COUI [TooltipBox] while its bubble is on screen, so the popup stays composed through
     * the 320ms exit, which no Transition consumes.
     */
    var exitHold by mutableStateOf(false)

    /** Counts [dismiss] calls; a second dismiss during the exit removes the tooltip at once. */
    var dismissRequests by mutableIntStateOf(0)
        private set

    override val isVisible: Boolean
        // Keep the popup composed while shown or still animating. Gating on currentState would
        // hard-cut the exit when a dismiss interrupts an unfinished enter, since currentState only
        // flips to true once the enter has settled; !isIdle keeps it through the reversal so the
        // exit can animate out.
        get() = transition.targetState || !transition.isIdle || exitHold

    /** Ends the COUI exit: drops the hold and snaps the transition to hidden. */
    fun finishExit() {
        exitHold = false
        val current = transitionState
        if (!current.targetState && (current.currentState || !current.isIdle)) {
            transitionState = MutableTransitionState(false)
        }
    }

    /** `COUIToolTips.dismissImmediately`: hides the tooltip without the exit animation. */
    fun dismissImmediately() {
        transition.targetState = false
        if (isPersistent) job?.cancel()
        finishExit()
    }

    /** continuation used to clean up */
    private var job: (CancellableContinuation<Unit>)? = null

    override suspend fun show(mutatePriority: MutatePriority) {
        val cancellableShow: suspend () -> Unit = {
            suspendCancellableCoroutine { continuation ->
                transition.targetState = true
                job = continuation
            }
        }

        mutatorMutex.mutate(mutatePriority) {
            try {
                if (isPersistent || mutatePriority == MutatePriority.UserInput) {
                    cancellableShow()
                } else {
                    withTimeout(BasicTooltipDefaults.TooltipDuration.milliseconds) { cancellableShow() }
                }
            } finally {
                if (mutatePriority != MutatePriority.PreventUserInput) {
                    dismiss()
                }
            }
        }
    }

    override fun dismiss() {
        transition.targetState = false
        dismissRequests++
        if (isPersistent) {
            job?.cancel()
        }
    }

    override fun onDispose() {
        job?.cancel()
    }
}

/**
 * Creates and remembers a [TooltipState].
 *
 * @param initialIsVisible whether the tooltip is initially visible.
 * @param isPersistent whether the tooltip stays visible until explicitly dismissed. True by default:
 *   a COUI tooltip has no timeout (`COUIToolTips.setDelay` is a no-op).
 * @param mutatorMutex the [MutatorMutex] used to ensure only one tooltip is shown at a time.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberTooltipState(
    initialIsVisible: Boolean = false,
    isPersistent: Boolean = true,
    mutatorMutex: MutatorMutex = BasicTooltipDefaults.GlobalMutatorMutex,
): TooltipState = remember(isPersistent, mutatorMutex) {
    TooltipStateImpl(initialIsVisible, isPersistent, mutatorMutex)
}

/** `COUIToolTips.animateSpring` scale spring: COUISpringForce bounce 0.2 / response 0.4. */
private val TooltipScaleSpring = spring(dampingRatio = 0.8f, stiffness = 246.74f, visibilityThreshold = 0.0001f)

/** `COUIToolTips.animateSpring` alpha spring: COUISpringForce bounce 0.2 / response 0.3. */
private val TooltipAlphaSpring = spring(dampingRatio = 0.8f, stiffness = 438.65f, visibilityThreshold = 0.0001f)

/** `COUIToolTips.animateExit`: the window is torn down 320ms after the exit starts (`mCheckAnimateExit`). */
private const val TOOLTIP_EXIT_CUT_MILLIS = 320L

/** Hides [this] at once (COUI `dismissImmediately`); falls back to [TooltipState.dismiss] for custom states. */
private fun TooltipState.dismissImmediately() {
    if (this is TooltipStateImpl) dismissImmediately() else dismiss()
}

/**
 * A COUI 17 tooltip (`COUIToolTips`) anchored to [content]: a [Tooltip], [IconTooltip] or
 * [ImageTooltip] bubble with an arrow pointing at the anchor.
 *
 * The bubble sits [placement] of the anchor (above by default, COUI `DEFAULT_ALIGN_DIRECTION`), keeps
 * a 16dp margin from the window edges and moves the arrow along its edge to keep pointing at the
 * anchor centre. It grows from the arrow tip on a spring (scale and alpha from 0) and shrinks back
 * into it on dismiss. Like COUI it is shown programmatically ([TooltipState.show]) and has no
 * timeout; it is dismissed by its close / dismiss button, [TooltipState.dismiss] or, when
 * [dismissOnTouchOutside], an outside tap or a back press.
 *
 * @param state the [TooltipState] controlling visibility.
 * @param tooltip the tooltip bubble: [Tooltip], [IconTooltip], [ImageTooltip] or a [TooltipBubble].
 * @param modifier the modifier applied to the anchor wrapper.
 * @param placement the preferred [TooltipAnchorPosition] (COUI `showWithDirection`).
 * @param showArrow whether the arrow is drawn (COUI `hasIndicator`).
 * @param dismissOnTouchOutside whether an outside tap or a back press dismisses the tooltip (COUI
 *   `setDismissOnTouchOutside`, off by default). It also makes the tooltip window focusable.
 * @param enableUserInput whether hover / long press on the anchor shows the tooltip. COUI tooltips
 *   are shown programmatically, so this is off by default.
 * @param offset an extra offset of the tooltip from its computed position (COUI `offsetX` /
 *   `offsetY`); x is mirrored in RTL.
 * @param arrowFollowsOffset whether the arrow moves with [offset] (COUI `indicatorFollowContent`);
 *   by default it stays pointing at the anchor.
 * @param content the anchor content.
 */
@Composable
fun TooltipBox(
    state: TooltipState,
    tooltip: @Composable TooltipScope.() -> Unit,
    modifier: Modifier = Modifier,
    placement: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    showArrow: Boolean = true,
    dismissOnTouchOutside: Boolean = false,
    enableUserInput: Boolean = false,
    offset: DpOffset = DpOffset.Zero,
    arrowFollowsOffset: Boolean = false,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val layoutState = remember { TooltipLayoutState(density.density) }
    layoutState.anchorContainer = LocalTooltipAnchorContainer.current
    layoutState.position = placement
    layoutState.showArrow = showArrow
    layoutState.arrowFollowsOffset = arrowFollowsOffset
    layoutState.offset = with(density) { IntOffset(offset.x.roundToPx(), offset.y.roundToPx()) }

    var anchorCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val scope = remember(placement, layoutState, state) {
        CouiTooltipScopeImpl(placement, layoutState, state) { anchorCoordinates }
    }
    val positionProvider = remember(layoutState) { CouiTooltipPositionProvider(layoutState) }

    TooltipBoxImpl(
        positionProvider = positionProvider,
        tooltip = { TooltipAnimatedContent(state = state, layoutState = layoutState) { scope.tooltip() } },
        state = state,
        modifier = modifier.onGloballyPositioned { coordinates ->
            anchorCoordinates = coordinates
            // COUIToolTips.refreshWhileLayoutChange: an anchor that is no longer in its visible rect
            // (scrolled off, clipped away or collapsed to zero size) dismisses the tooltip at once.
            if (state.isVisible && !coordinates.isVisibleInWindow()) state.dismissImmediately()
        },
        properties = PopupProperties(
            focusable = dismissOnTouchOutside,
            dismissOnBackPress = dismissOnTouchOutside,
            dismissOnClickOutside = dismissOnTouchOutside,
        ),
        enableUserInput = enableUserInput,
        content = content,
    )

    // COUI dismisses a tooltip whose anchor is gone; an anchor leaving the composition takes the
    // popup with it, so reset the state without an exit animation.
    DisposableEffect(state) { onDispose { state.dismissImmediately() } }
}

/** Whether the anchor still has a visible, non-empty rect in its window (`UIUtil.isInVisibleRect`). */
private fun LayoutCoordinates.isVisibleInWindow(): Boolean {
    if (!isAttached || size.width <= 0 || size.height <= 0) return false
    val bounds = boundsInWindow()
    return bounds.width > 0f && bounds.height > 0f
}

/**
 * `COUIToolTips.animateSpring`: every enter and every exit restarts a 0 -> 1 progress spring for the
 * scale and one for the alpha (`setStartValue(0)`), mapped to 0 -> 1 on enter and 1 -> 0 on exit, so
 * an exit that interrupts an enter jumps back to full size first. The window is pivoted at the arrow
 * tip, drops its shadow and ignores touches while exiting, and is removed 320ms after the exit starts
 * (`mCheckAnimateExit`), or at once on a second dismiss.
 */
@Composable
private fun TooltipAnimatedContent(
    state: TooltipState,
    layoutState: TooltipLayoutState,
    content: @Composable () -> Unit,
) {
    val impl = state as? TooltipStateImpl
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val showing = state.transition.targetState
    var exitStartRequests by remember { mutableIntStateOf(-1) }
    LaunchedEffect(showing) {
        if (showing) {
            impl?.exitHold = true
            exitStartRequests = -1
            launch {
                alpha.snapTo(0f)
                alpha.animateTo(1f, TooltipAlphaSpring)
            }
            scale.snapTo(0f)
            scale.animateTo(1f, TooltipScaleSpring)
        } else {
            exitStartRequests = impl?.dismissRequests ?: 0
            launch {
                alpha.snapTo(1f)
                alpha.animateTo(0f, TooltipAlphaSpring)
            }
            launch {
                scale.snapTo(1f)
                scale.animateTo(0f, TooltipScaleSpring)
            }
            delay(TOOLTIP_EXIT_CUT_MILLIS)
            impl?.finishExit()
        }
    }
    // COUIToolTips.dismiss: a dismiss while the exit runs tears the window down immediately.
    val requests = impl?.dismissRequests ?: 0
    LaunchedEffect(requests) {
        if (!state.transition.targetState && exitStartRequests >= 0 && requests > exitStartRequests) {
            impl?.finishExit()
        }
    }
    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value.coerceIn(0f, 1f)
                transformOrigin = layoutState.placement?.pivot ?: TransformOrigin.Center
            }
            .then(if (!showing) Modifier.blockPointerInput() else Modifier),
    ) {
        content()
    }
}

/** Consumes every pointer event on the initial pass, so nothing inside reacts (COUI `setTouchable(false)`). */
private fun Modifier.blockPointerInput(): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            event.changes.forEach { it.consume() }
        }
    }
}

private const val TOOLTIP_LONG_PRESS_LABEL = "Show tooltip"
private const val TOOLTIP_PANE_TITLE = "Tooltip"

/**
 * The tooltip host. Forked from the experimental foundation `BasicTooltipBox` so the mouse hover
 * gesture keeps a persistent tooltip open: when the cursor leaves the anchor the tooltip is dismissed
 * only if it is not persistent.
 */
@Composable
internal fun TooltipBoxImpl(
    positionProvider: PopupPositionProvider,
    tooltip: @Composable () -> Unit,
    state: TooltipState,
    modifier: Modifier = Modifier,
    properties: PopupProperties = PopupProperties(),
    enableUserInput: Boolean = true,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .tooltipGestures(enableUserInput, state)
            .tooltipAnchorSemantics(enableUserInput, state, scope),
    ) {
        if (state.isVisible) {
            Popup(
                popupPositionProvider = positionProvider,
                onDismissRequest = { if (state.isVisible) state.dismiss() },
                properties = properties,
            ) {
                Box(
                    modifier = Modifier.semantics {
                        liveRegion = LiveRegionMode.Assertive
                        paneTitle = TOOLTIP_PANE_TITLE
                    },
                ) {
                    tooltip()
                }
            }
        }
        content()
    }

    DisposableEffect(state) { onDispose { state.onDispose() } }
}

/**
 * Shows the tooltip on long press (touch / stylus) and on mouse hover. The mouse
 * [PointerEventType.Exit] branch dismisses only non-persistent tooltips, so a persistent tooltip
 * stays open while the cursor moves from the anchor onto the tooltip.
 */
private fun Modifier.tooltipGestures(enabled: Boolean, state: TooltipState): Modifier = if (enabled) {
    this
        .pointerInput(state) {
            coroutineScope {
                awaitEachGesture {
                    val pass = PointerEventPass.Initial
                    val inputType = awaitFirstDown(pass = pass).type
                    if (inputType == PointerType.Touch || inputType == PointerType.Stylus) {
                        try {
                            withTimeout(viewConfiguration.longPressTimeoutMillis) {
                                waitForUpOrCancellation(pass = pass)
                            }
                        } catch (_: PointerEventTimeoutCancellationException) {
                            // Long press detected: show the tooltip and consume the following up
                            // event so the anchor's own click handling does not also fire.
                            launch { state.show(MutatePriority.UserInput) }
                            val changes = awaitPointerEvent(pass = pass).changes
                            for (i in changes.indices) {
                                changes[i].consume()
                            }
                        }
                    }
                }
            }
        }
        .pointerInput(state) {
            coroutineScope {
                awaitPointerEventScope {
                    val pass = PointerEventPass.Main
                    while (true) {
                        val event = awaitPointerEvent(pass)
                        if (event.changes[0].type == PointerType.Mouse) {
                            when (event.type) {
                                PointerEventType.Enter ->
                                    launch { state.show(MutatePriority.UserInput) }

                                PointerEventType.Exit ->
                                    if (!state.isPersistent) state.dismiss()
                            }
                        }
                    }
                }
            }
        }
} else {
    this
}

private fun Modifier.tooltipAnchorSemantics(
    enabled: Boolean,
    state: TooltipState,
    scope: CoroutineScope,
): Modifier = if (enabled) {
    this.semantics(mergeDescendants = true) {
        onLongClick(
            label = TOOLTIP_LONG_PRESS_LABEL,
            action = {
                scope.launch { state.show() }
                true
            },
        )
    }
} else {
    this
}

/**
 * The COUI 17 tooltip bubble the three styles are built on: the `COUIToolTips` panel, a smooth round
 * rect (17dp at weight 3.0) inside a transparent window inset that holds the arrow and the LV4
 * shadow, with the arrow joined to it as one outline. Use it for fully custom tooltip content; the
 * content is clipped to the bubble and receives the max width / max height the placement allows.
 *
 * Inside a [TooltipBox] the bubble sizes itself the COUI way (narrowing next to the anchor, shrinking
 * and scrolling when neither side fits) and draws the arrow.
 *
 * @param modifier the modifier applied to the bubble window.
 * @param style the [TooltipStyle] whose window inset, width and colours apply.
 * @param mode the [TooltipMode] of a [TooltipStyle.Default] bubble.
 * @param containerColor the colour of the bubble and the arrow.
 * @param maxWidth the maximum width of the visible bubble.
 * @param cornerRadius the corner radius of the bubble, drawn at [TooltipDefaults.CornerWeight].
 * @param content the bubble content.
 */
@Composable
fun TooltipScope.TooltipBubble(
    modifier: Modifier = Modifier,
    style: TooltipStyle = TooltipStyle.Default,
    mode: TooltipMode = TooltipMode.Tooltips,
    containerColor: Color = TooltipDefaults.tooltipColors(style = style, mode = mode).containerColor,
    maxWidth: Dp = TooltipDefaults.maxWidth(style),
    cornerRadius: Dp = TooltipDefaults.CornerRadius,
    content: @Composable () -> Unit,
) {
    val couiScope = this as? CouiTooltipScopeImpl
    val layoutState = couiScope?.layoutState
    val density = LocalDensity.current
    val insets = TooltipDefaults.windowInset(style, mode)
    val insetLeft = insets.width
    val insetTop = insets.height
    val metrics = remember(density, insetLeft, insetTop) {
        TooltipMetrics.of(density.density, insetLeft.value, insetTop.value, insetLeft.value, insetTop.value)
    }
    layoutState?.metrics = metrics
    val maxWidthPx = with(density) { maxWidth.roundToPx() }
    val minWidthPx = with(density) { TooltipDefaults.minWidth(style, mode).roundToPx() }
    val fixedWidth = style == TooltipStyle.Image

    val placement = layoutState?.placement
    val squircleEnabled = isSquircleEnabled()
    val shape = remember(insetLeft, insetTop, cornerRadius, placement?.arrow, placement?.arrowTopLeft, squircleEnabled) {
        TooltipBubbleShape(
            insetHorizontal = insetLeft,
            insetVertical = insetTop,
            cornerRadius = cornerRadius,
            arrow = placement?.arrow,
            arrowTopLeft = placement?.arrowTopLeft ?: Offset.Zero,
            squircleEnabled = squircleEnabled,
        )
    }
    val showing = couiScope?.state?.transition?.targetState ?: true
    val isDark = COUITheme.colorScheme.isDark

    val shadow = if (showing) {
        Modifier.dropShadow(shape = shape, shadow = if (isDark) TooltipShadowDark else TooltipShadowLight)
    } else {
        Modifier
    }
    val bubble: @Composable () -> Unit = {
        Box(
            modifier = shadow
                .background(color = containerColor, shape = shape)
                .padding(horizontal = insetLeft, vertical = insetTop),
            propagateMinConstraints = true,
        ) {
            Box(
                modifier = Modifier
                    .clip(SquircleShape(cornerRadius, TooltipDefaults.CornerWeight))
                    .semantics(mergeDescendants = false) { isTraversalGroup = true },
            ) {
                content()
            }
        }
    }

    // Only the placed subcomposition carries semantics; the measure-only ones are cleared.
    var placedSlot by remember { mutableStateOf(TooltipBubbleSlot.Main) }
    val placedContent: @Composable () -> Unit = { TooltipBubbleSlotContent(probe = false, bubble = bubble) }
    val probeContent: @Composable () -> Unit = { TooltipBubbleSlotContent(probe = true, bubble = bubble) }

    // COUIToolTips.sizePopupWindow + the height half of refreshCoordinated, measured for real: the
    // window width is capped at the style max (and the room beside the anchor for a left / right
    // tooltip); a left / right tooltip that would reach the window bottom is re-measured above the
    // anchor at full width; a tooltip that fits neither above nor below is re-measured at the height of
    // the larger side, where its content scrolls.
    SubcomposeLayout(modifier = modifier) { constraints ->
        var finalSlot = TooltipBubbleSlot.Main

        fun measure(slot: TooltipBubbleSlot, maxWidth: Int, maxHeight: Int): Placeable {
            val width = max(min(maxWidth, constraints.maxWidth), 0)
            val minWidth = if (fixedWidth) width else min(minWidthPx, width)
            finalSlot = slot
            val slotContent = if (slot == placedSlot) placedContent else probeContent
            return subcompose(slot, slotContent).first().measure(
                Constraints(minWidth = max(minWidth, 0), maxWidth = width, minHeight = 0, maxHeight = maxHeight),
            )
        }

        val maxHeight = constraints.maxHeight
        val anchor = layoutState?.anchorBounds
        val viewport = layoutState?.viewport ?: IntSize.Zero
        val placeable = if (layoutState == null || anchor == null || viewport == IntSize.Zero) {
            measure(TooltipBubbleSlot.Main, maxWidthPx + metrics.insetLeft + metrics.insetRight, maxHeight)
        } else {
            val spacing = layoutState.anchorSpacing(anchor)
            var side = layoutState.position.toTooltipSide(layoutDirection)
            var fallback: TooltipSide? = null
            var sidePlaceable: Placeable? = null
            if (side == TooltipSide.Left || side == TooltipSide.Right) {
                val sideWidth = tooltipMaxWindowWidth(side, anchor, viewport, metrics, maxWidthPx, spacing)
                val measured = measure(TooltipBubbleSlot.Main, sideWidth, maxHeight)
                if (tooltipSideFallsBackToVertical(anchor, viewport, metrics, measured.height)) {
                    side = TooltipSide.Above
                    fallback = TooltipSide.Above
                } else {
                    sidePlaceable = measured
                }
            }
            layoutState.fallbackSide = fallback
            sidePlaceable ?: run {
                val width = tooltipMaxWindowWidth(side, anchor, viewport, metrics, maxWidthPx, spacing)
                val slot = if (fallback != null) TooltipBubbleSlot.Fallback else TooltipBubbleSlot.Main
                val measured = measure(slot, width, maxHeight)
                val cap = tooltipVerticalSide(side, anchor, viewport, measured.height, spacing).second
                if (cap != null && cap < measured.height) {
                    measure(TooltipBubbleSlot.Shrunk, width, max(cap, 0))
                } else {
                    measured
                }
            }
        }
        if (placedSlot != finalSlot) placedSlot = finalSlot
        layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }
}

/** The [TooltipBubble] subcompositions: the first measure, the side fallback and the shrunk window. */
private enum class TooltipBubbleSlot { Main, Fallback, Shrunk }

/** One [TooltipBubble] subcomposition; a measure-only [probe] keeps out of the semantics tree. */
@Composable
private fun TooltipBubbleSlotContent(probe: Boolean, bubble: @Composable () -> Unit) {
    Box(modifier = if (probe) Modifier.clearAndSetSemantics {} else Modifier, propagateMinConstraints = true) {
        bubble()
    }
}

/**
 * The bubble outline: the smooth round rect inside the window inset, joined with the arrow into one
 * path, so the fill and the shadow have no seam.
 */
@Immutable
private class TooltipBubbleShape(
    private val insetHorizontal: Dp,
    private val insetVertical: Dp,
    private val cornerRadius: Dp,
    private val arrow: TooltipArrowDirection?,
    private val arrowTopLeft: Offset,
    private val squircleEnabled: Boolean,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val h = with(density) { insetHorizontal.toPx() }
        val v = with(density) { insetVertical.toPx() }
        val body = Path()
        val rect = Rect(h, v, size.width - h, size.height - v)
        if (rect.width > 0f && rect.height > 0f) {
            body.addSquircleRect(rect, with(density) { cornerRadius.toPx() }, TooltipDefaults.CornerWeight, squircleEnabled)
        }
        if (arrow == null) return Outline.Generic(body)
        val arrowPath = arrow.path(density.density, arrowTopLeft)
        return Outline.Generic(Path.combine(PathOperation.Union, body, arrowPath))
    }

    override fun equals(other: Any?): Boolean = other is TooltipBubbleShape &&
        other.insetHorizontal == insetHorizontal &&
        other.insetVertical == insetVertical &&
        other.cornerRadius == cornerRadius &&
        other.arrow == arrow &&
        other.arrowTopLeft == arrowTopLeft &&
        other.squircleEnabled == squircleEnabled

    override fun hashCode(): Int {
        var result = insetHorizontal.hashCode()
        result = 31 * result + insetVertical.hashCode()
        result = 31 * result + cornerRadius.hashCode()
        result = 31 * result + (arrow?.hashCode() ?: 0)
        result = 31 * result + arrowTopLeft.hashCode()
        result = 31 * result + squircleEnabled.hashCode()
        return result
    }
}

/**
 * COUI SHADOW_LV4 (`ShadowUtils.setElevationToView(view, 3, …)` on the panel and the arrow), the
 * same calibration as the popup menu: 26.66dp blur at 13.7% black (`coui_shadow_color_lv4` 48/255),
 * 3.33dp down.
 */
private val TooltipShadowLight = Shadow(radius = 26.66.dp, color = Color.Black, alpha = 0.137f, offset = DpOffset(0.dp, 3.33.dp))

/** The dark SHADOW_LV4: `coui_shadow_color_lv4` rises to 150/255 at a 36dp elevation. */
private val TooltipShadowDark = Shadow(radius = 26.66.dp, color = Color.Black, alpha = 0.43f, offset = DpOffset(0.dp, 3.33.dp))

/**
 * A [TooltipStyle.Default] tooltip with a text body (`COUIDefaultBubbleStyleImpl`).
 *
 * In [TooltipMode.Tooltips] it is the accent bubble with white 13sp text and a close icon at its end;
 * the icon dismisses the tooltip and then calls [onCloseClick]. In [TooltipMode.Info] it is the
 * white "detail floating" bubble with secondary text and no close icon.
 *
 * @param text the tooltip text.
 * @param modifier the modifier applied to the bubble window.
 * @param mode the [TooltipMode].
 * @param colors the [TooltipColors]; `containerColor` and `contentColor` apply.
 * @param maxWidth the maximum width of the visible bubble.
 * @param onCloseClick called after the close icon dismissed the tooltip (COUI
 *   `OnCloseIconClickListener`).
 */
@Composable
fun TooltipScope.Tooltip(
    text: String,
    modifier: Modifier = Modifier,
    mode: TooltipMode = TooltipMode.Tooltips,
    colors: TooltipColors = TooltipDefaults.tooltipColors(mode = mode),
    maxWidth: Dp = TooltipDefaults.MaxWidth,
    onCloseClick: (() -> Unit)? = null,
) {
    Tooltip(
        modifier = modifier,
        mode = mode,
        colors = colors,
        maxWidth = maxWidth,
        onCloseClick = onCloseClick,
    ) {
        Text(
            text = text,
            color = colors.contentColor,
            style = COUITheme.textStyles.bodyS,
        )
    }
}

/**
 * A [TooltipStyle.Default] tooltip with custom content (COUI `setContent(View)`), framed by the
 * Default bubble's margins (12dp, with 42dp at the end for the close icon in [TooltipMode.Tooltips]).
 * The content scrolls when the tooltip has to shrink.
 *
 * @param modifier the modifier applied to the bubble window.
 * @param mode the [TooltipMode].
 * @param colors the [TooltipColors]; `containerColor` and `contentColor` apply.
 * @param maxWidth the maximum width of the visible bubble.
 * @param onCloseClick called after the close icon dismissed the tooltip.
 * @param content the tooltip content; [LocalContentColor] is `colors.contentColor`.
 */
@Composable
fun TooltipScope.Tooltip(
    modifier: Modifier = Modifier,
    mode: TooltipMode = TooltipMode.Tooltips,
    colors: TooltipColors = TooltipDefaults.tooltipColors(mode = mode),
    maxWidth: Dp = TooltipDefaults.MaxWidth,
    onCloseClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val tooltips = mode == TooltipMode.Tooltips
    val state = (this as? CouiTooltipScopeImpl)?.state
    TooltipBubble(
        modifier = modifier,
        style = TooltipStyle.Default,
        mode = mode,
        containerColor = colors.containerColor,
        maxWidth = maxWidth,
    ) {
        Box(contentAlignment = if (tooltips) Alignment.CenterStart else Alignment.Center) {
            Box(
                modifier = Modifier
                    .padding(
                        start = TooltipDefaults.ContentMargin,
                        top = TooltipDefaults.ContentMargin,
                        end = if (tooltips) TooltipDefaults.CloseIconContentMarginEnd else TooltipDefaults.ContentMargin,
                        bottom = TooltipDefaults.ContentMargin,
                    )
                    .verticalScroll(rememberScrollState()),
            ) {
                CompositionLocalProvider(LocalContentColor provides colors.contentColor) {
                    content()
                }
            }
            if (tooltips) {
                TooltipCloseIcon(
                    color = colors.contentColor,
                    onClick = {
                        state?.dismiss()
                        onCloseClick?.invoke()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = TooltipDefaults.ContentMargin),
                )
            }
        }
    }
}

/**
 * A [TooltipStyle.Icon] tooltip (`COUIIconBubbleStyleImpl`): a white card with a 40dp icon (8dp
 * corners), an optional title, the body and an optional dismiss text button.
 *
 * @param text the body text.
 * @param icon the icon, drawn 40dp with `ContentScale.Fit`.
 * @param modifier the modifier applied to the bubble window.
 * @param title the optional title.
 * @param dismissText the optional dismiss button label; it dismisses the tooltip and then calls
 *   [onDismissClick].
 * @param colors the [TooltipColors] of the tooltip.
 * @param maxWidth the maximum width of the visible bubble.
 * @param onDismissClick called after the dismiss button dismissed the tooltip.
 */
@Composable
fun TooltipScope.IconTooltip(
    text: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    title: String? = null,
    dismissText: String? = null,
    colors: TooltipColors = TooltipDefaults.tooltipColors(style = TooltipStyle.Icon),
    maxWidth: Dp = TooltipDefaults.MaxWidth,
    onDismissClick: (() -> Unit)? = null,
) {
    val state = (this as? CouiTooltipScopeImpl)?.state
    TooltipBubble(
        modifier = modifier,
        style = TooltipStyle.Icon,
        containerColor = colors.containerColor,
        maxWidth = maxWidth,
    ) {
        Column {
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(top = 12.dp)
                    .tooltipFadingEdges(scrollState)
                    .verticalScroll(scrollState),
            ) {
                Box {
                    Image(
                        painter = icon,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(start = 12.dp, top = 2.dp)
                            .size(TooltipDefaults.IconSize)
                            .clip(RoundedCornerShape(TooltipDefaults.MediaCornerRadius)),
                    )
                    Column(modifier = Modifier.padding(start = 62.dp, end = 16.dp)) {
                        TooltipTitleAndBody(title = title, text = text, colors = colors)
                    }
                }
            }
            TooltipDismissButton(
                text = dismissText,
                color = colors.actionColor,
                startMargin = 50.dp,
                onClick = {
                    state?.dismiss()
                    onDismissClick?.invoke()
                },
            )
        }
    }
}

/**
 * A [TooltipStyle.Image] tooltip (`COUIImageBubbleStyleImpl`) showing [image] cropped to the media
 * area, with an optional title, the body and an optional dismiss text button.
 *
 * @param text the body text.
 * @param image the image, drawn with `ContentScale.Crop`.
 * @param modifier the modifier applied to the bubble window.
 * @param title the optional title.
 * @param dismissText the optional dismiss button label.
 * @param imageWithEdges true (the COUI default) for a 228 x 116dp image with 8dp corners inset 16dp
 *   from the bubble; false for a full-bleed 132dp image clipped by the bubble corners.
 * @param colors the [TooltipColors] of the tooltip.
 * @param maxWidth the width of the visible bubble (an image tooltip always takes its max width).
 * @param onDismissClick called after the dismiss button dismissed the tooltip.
 */
@Composable
fun TooltipScope.ImageTooltip(
    text: String,
    image: Painter,
    modifier: Modifier = Modifier,
    title: String? = null,
    dismissText: String? = null,
    imageWithEdges: Boolean = true,
    colors: TooltipColors = TooltipDefaults.tooltipColors(style = TooltipStyle.Image),
    maxWidth: Dp = TooltipDefaults.ImageMaxWidth,
    onDismissClick: (() -> Unit)? = null,
) {
    ImageTooltip(
        text = text,
        modifier = modifier,
        title = title,
        dismissText = dismissText,
        imageWithEdges = imageWithEdges,
        colors = colors,
        maxWidth = maxWidth,
        onDismissClick = onDismissClick,
    ) {
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * A [TooltipStyle.Image] tooltip with custom media, e.g. a looping animation (COUI `loadAnim`).
 *
 * @param text the body text.
 * @param modifier the modifier applied to the bubble window.
 * @param title the optional title.
 * @param dismissText the optional dismiss button label.
 * @param imageWithEdges true for a 228 x 116dp media area with 8dp corners; false for a full-bleed
 *   132dp area.
 * @param colors the [TooltipColors] of the tooltip.
 * @param maxWidth the width of the visible bubble.
 * @param onDismissClick called after the dismiss button dismissed the tooltip.
 * @param media the media content, laid out in the media area.
 */
@Composable
fun TooltipScope.ImageTooltip(
    text: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    dismissText: String? = null,
    imageWithEdges: Boolean = true,
    colors: TooltipColors = TooltipDefaults.tooltipColors(style = TooltipStyle.Image),
    maxWidth: Dp = TooltipDefaults.ImageMaxWidth,
    onDismissClick: (() -> Unit)? = null,
    media: @Composable BoxScope.() -> Unit,
) {
    val state = (this as? CouiTooltipScopeImpl)?.state
    TooltipBubble(
        modifier = modifier,
        style = TooltipStyle.Image,
        containerColor = colors.containerColor,
        maxWidth = maxWidth,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(top = if (imageWithEdges) 16.dp else 0.dp)
                    .tooltipFadingEdges(scrollState)
                    .verticalScroll(scrollState),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (imageWithEdges) TooltipDefaults.ImageWithEdgesSize.height else TooltipDefaults.ImageNoEdgesHeight),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Box(
                            modifier = if (imageWithEdges) {
                                Modifier
                                    .size(TooltipDefaults.ImageWithEdgesSize)
                                    .clip(RoundedCornerShape(TooltipDefaults.MediaCornerRadius))
                            } else {
                                Modifier.fillMaxSize()
                            },
                            content = media,
                        )
                    }
                    Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 12.dp)) {
                        TooltipTitleAndBody(title = title, text = text, colors = colors)
                    }
                }
            }
            TooltipDismissButton(
                text = dismissText,
                color = colors.actionColor,
                startMargin = 4.dp,
                onClick = {
                    state?.dismiss()
                    onDismissClick?.invoke()
                },
            )
        }
    }
}

@Composable
private fun ColumnScope.TooltipTitleAndBody(title: String?, text: String, colors: TooltipColors) {
    if (title != null) {
        Text(text = title, color = colors.titleColor, style = COUITheme.textStyles.headlineXS)
    }
    Text(text = text, color = colors.contentColor, style = COUITheme.textStyles.bodyS)
}

/**
 * The Icon / Image dismiss text button: `couiTextButtonM` in `couiColorLabelTheme`, at most two lines,
 * end aligned with 2dp above and 10dp below, padded 12 x 4dp by `setPressRippleDrawable` with a
 * capsule press mask.
 */
@Composable
private fun ColumnScope.TooltipDismissButton(
    text: String?,
    color: Color,
    startMargin: Dp,
    onClick: () -> Unit,
) {
    if (text == null) {
        Spacer(modifier = Modifier.height(TooltipDefaults.ContentMargin))
        return
    }
    Text(
        text = text,
        color = color,
        style = COUITheme.textStyles.buttonM,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .align(Alignment.End)
            .padding(start = startMargin, top = 2.dp, end = 4.dp, bottom = 10.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(TooltipDefaults.DismissButtonPadding),
    )
}

/**
 * The 18dp close icon (`coui_tool_tips_delete_icon`): a 30% circle with a rounded cross, both in the
 * content colour. Its touch target is grown by 20dp on each side (`couiToolTipsCancelButtonInsects`).
 */
@Composable
private fun TooltipCloseIcon(
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val base = LocalViewConfiguration.current
    val description = tooltipCloseDescription(Locale.current)
    val configuration = remember(base) { ExpandedTouchTargetConfiguration(base, TooltipDefaults.CloseIconTouchTarget) }
    CompositionLocalProvider(LocalViewConfiguration provides configuration) {
        Canvas(
            modifier = modifier
                .size(TooltipDefaults.CloseIconSize)
                .semantics { contentDescription = description }
                .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onClick),
        ) {
            val unit = size.width / 18f
            drawCircle(color = color, alpha = 0.3f)
            val cross = addPathNodes(TOOLTIP_CLOSE_CROSS_PATH).toPath()
            val matrix = Matrix()
            matrix.values[Matrix.ScaleX] = unit
            matrix.values[Matrix.ScaleY] = unit
            cross.transform(matrix)
            drawPath(cross, color = color)
        }
    }
}

/**
 * `@string/coui_tool_tips_delete_icon_description` in [locale], taken from the COUI `values-*`
 * resources and resolved the way Android picks a `values-<lang>[-r<REGION>]` folder (English
 * otherwise).
 */
internal fun tooltipCloseDescription(locale: Locale): String {
    val region = locale.region.uppercase()
    return when (locale.language.lowercase()) {
        "zh" -> if (locale.script.equals("Hant", ignoreCase = true) || region == "HK" || region == "TW" || region == "MO") {
            "關閉提示"
        } else {
            "关闭提示"
        }

        "es" -> if (region == "MX") "Cerrar esta sugerencia" else "Cerrar este consejo"

        "pt" -> if (region == "BR") "Fechar dica" else TOOLTIP_CLOSE_DESCRIPTION_DEFAULT

        "ar" -> "إغلاق هذا التلميح"

        "bn" -> "এই টিপটি বন্ধ করুন"

        "bo" -> "གསལ་འདེབས་ཁ་རྒྱག"

        "de" -> "Diesen Tipp schließen"

        "fr" -> "Fermer cette info-bulle"

        "hi" -> "इस सुझाव को बंद करें"

        "in", "id" -> "Tutup kiat ini"

        "it" -> "Chiudi questo suggerimento"

        "ja" -> "このヒントを閉じる"

        "km" -> "បិទគន្លឹះនេះ"

        "ko" -> "이 팁을 닫습니다"

        "lo" -> "​ປິດ​ເຄັດ​ລັບ​ນີ້"

        "ms" -> "Tutup petua ini"

        "my" -> "ဤအကြံပြုချက်ကို ပိတ်ပါ"

        "ne" -> "यो सुझाव बन्द गर्नुहोस्"

        "nl" -> "Deze tip sluiten"

        "ru" -> "Закрыть совет"

        "th" -> "ปิดเคล็ดลับนี้"

        "tl", "fil" -> "Isara ang tip na ito"

        "ug" -> "ئەسكەرتمىنى ئېتىش"

        "vi" -> "Đóng mẹo này"

        else -> TOOLTIP_CLOSE_DESCRIPTION_DEFAULT
    }
}

/** `@string/coui_tool_tips_delete_icon_description` in `values/`. */
private const val TOOLTIP_CLOSE_DESCRIPTION_DEFAULT = "Close this tip"

private const val TOOLTIP_CLOSE_CROSS_PATH =
    "M9,7.87 L6.6,5.46a0.81,0.81 0,0 0,-1.14 0h0a0.81,0.81 0,0 0,0 1.14L7.87,9 5.46,11.4a0.81,0.81 0,0 0,0 1.14h0" +
        "a0.81,0.81 0,0 0,1.14 0L9,10.13l2.4,2.41a0.81,0.81 0,0 0,1.14 0h0a0.81,0.81 0,0 0,0 -1.14L10.13,9l2.41,-2.4" +
        "a0.81,0.81 0,0 0,0 -1.14h0a0.81,0.81 0,0 0,-1.14 0Z"

private class ExpandedTouchTargetConfiguration(
    private val base: ViewConfiguration,
    private val touchTarget: DpSize,
) : ViewConfiguration by base {
    override val minimumTouchTargetSize: DpSize
        get() = touchTarget
}

/**
 * The vertical fading edges of the Icon / Image scroll area (`requiresFadingEdge="vertical"`, 24dp
 * `coui_tooltips_scroll_fading_length`), each one as strong as the remaining scroll allows, like
 * `View.getTopFadingEdgeStrength`.
 */
private fun Modifier.tooltipFadingEdges(scrollState: ScrollState): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val length = TooltipDefaults.FadingEdgeLength.toPx()
        if (length <= 0f) return@drawWithContent
        val top = min(scrollState.value / length, 1f) * length
        val bottom = min((scrollState.maxValue - scrollState.value) / length, 1f) * length
        if (top > 0f) {
            drawRect(
                brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black), startY = 0f, endY = top),
                size = Size(size.width, top),
                blendMode = BlendMode.DstIn,
            )
        }
        if (bottom > 0f) {
            drawRect(
                brush = Brush.verticalGradient(listOf(Color.Black, Color.Transparent), startY = size.height - bottom, endY = size.height),
                topLeft = Offset(0f, size.height - bottom),
                size = Size(size.width, bottom),
                blendMode = BlendMode.DstIn,
            )
        }
    }

/**
 * Colours of a COUI tooltip.
 *
 * @param containerColor the bubble and arrow colour.
 * @param contentColor the body text (and Default close icon) colour.
 * @param titleColor the Icon / Image title colour.
 * @param actionColor the Icon / Image dismiss button colour.
 */
@Immutable
data class TooltipColors(
    val containerColor: Color,
    val contentColor: Color,
    val titleColor: Color,
    val actionColor: Color,
)

/**
 * Defaults for [TooltipBox] and the COUI tooltip bubbles.
 */
object TooltipDefaults {
    /**
     * The bubble corner radius: COUI 17 outlines the `COUIToolTips` panel with
     * `coui_tooltips_os_16_1_radius_17_dp` at weight [CornerWeight].
     */
    val CornerRadius = 17.dp

    /** The bubble corner weight (superellipse exponent): `COUIToolTips.OS_16_1_WEIGHT` 3.0. */
    val CornerWeight: Float = 3.0f

    /** The max width of a Default / Icon bubble (`tool_tips_max_width`). */
    val MaxWidth = 328.dp

    /** The width of an Image bubble (`tool_tips_image_style_max_width`). */
    val ImageMaxWidth = 260.dp

    /** The min width of the Info window, inset included (`detail_floating_min_width`). */
    val InfoMinWidth = 80.dp

    /** The content margin of the Default bubble and the close icon end margin. */
    internal val ContentMargin = 12.dp

    /** The content end margin of the Default tooltips bubble, which leaves room for the close icon. */
    internal val CloseIconContentMarginEnd = 42.dp

    /** The close icon size (`coui_tool_tips_delete_background_size`). */
    internal val CloseIconSize = 18.dp

    /** The close icon touch target: 18dp plus 20dp on each side (`couiToolTipsCancelButtonInsects`). */
    internal val CloseIconTouchTarget = DpSize(58.dp, 58.dp)

    /** The Icon bubble icon size. */
    internal val IconSize = 40.dp

    /** The icon / image corner radius (`coui_tooltips_imageview_radius`). */
    internal val MediaCornerRadius = 8.dp

    /** The media area of an Image bubble with edges. */
    internal val ImageWithEdgesSize = DpSize(228.dp, 116.dp)

    /** The media height of an Image bubble without edges. */
    internal val ImageNoEdgesHeight = 132.dp

    /** The fading edge length of the Icon / Image scroll area (`coui_tooltips_scroll_fading_length`). */
    internal val FadingEdgeLength = 24.dp

    /** The dismiss button press padding (`text_ripple_bg_padding_horizontal` / `_vertical`). */
    internal val DismissButtonPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)

    /**
     * The transparent window inset around the bubble (`couiToolTipsPadding*`), which holds the arrow
     * and the shadow: 13dp all round, 16dp horizontally for the Info bubble.
     */
    fun windowInset(style: TooltipStyle, mode: TooltipMode = TooltipMode.Tooltips): DpSize = if (style == TooltipStyle.Default && mode == TooltipMode.Info) DpSize(16.dp, 13.dp) else DpSize(13.dp, 13.dp)

    /** The max width of a bubble of [style]. */
    fun maxWidth(style: TooltipStyle): Dp = if (style == TooltipStyle.Image) ImageMaxWidth else MaxWidth

    /** The min width of the window (inset included) of a bubble of [style] and [mode]. */
    fun minWidth(style: TooltipStyle, mode: TooltipMode = TooltipMode.Tooltips): Dp = if (style == TooltipStyle.Default && mode == TooltipMode.Info) InfoMinWidth else 0.dp

    /**
     * The colours of a tooltip of [style] and [mode]: the accent (`couiColorTips` = primary) bubble
     * with white text for the Default tooltips mode, `couiColorBackgroundTop` with secondary text
     * otherwise; titles in `couiColorLabelPrimary`, dismiss buttons in `couiColorLabelTheme`.
     */
    @Composable
    fun tooltipColors(
        style: TooltipStyle = TooltipStyle.Default,
        mode: TooltipMode = TooltipMode.Tooltips,
        containerColor: Color = if (style == TooltipStyle.Default && mode == TooltipMode.Tooltips) {
            COUITheme.colorScheme.primary
        } else {
            COUITheme.colorScheme.backgroundTop
        },
        contentColor: Color = if (style == TooltipStyle.Default && mode == TooltipMode.Tooltips) {
            COUITheme.colorScheme.onPrimary
        } else {
            COUITheme.colorScheme.labelSecondary
        },
        titleColor: Color = COUITheme.colorScheme.labelPrimary,
        actionColor: Color = COUITheme.colorScheme.primary,
    ): TooltipColors = remember(containerColor, contentColor, titleColor, actionColor) {
        TooltipColors(
            containerColor = containerColor,
            contentColor = contentColor,
            titleColor = titleColor,
            actionColor = actionColor,
        )
    }
}
