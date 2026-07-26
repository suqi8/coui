// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.window.WindowDialog
import kotlinx.coroutines.launch

/**
 * Internal shared layout logic for [OverlayDialog] and [WindowDialog].
 *
 * @param show Whether the dialog is currently shown.
 * @param titleColor The color of the title.
 * @param summaryColor The color of the summary.
 * @param backgroundColor The background color of the dialog.
 * @param outsideMargin The margin outside the dialog.
 * @param insideMargin The margin for the built-in texts: width is the horizontal padding of
 *   the title and summary, height is the top padding above the title. The [content] slot is
 *   left unpadded so COUI-style button bars can span the full panel width.
 * @param popupHost A composable that provides the dialog container (e.g., DialogLayout or Dialog).
 *   It receives the visibility state and the inner content composable.
 * @param modifier The modifier to be applied to the dialog content.
 * @param title The title of the dialog.
 * @param summary The summary of the dialog.
 * @param enableWindowDim Whether to enable window dimming.
 * @param onDismissRequest The callback when the dialog is dismissed.
 * @param onDismissFinished Invoked when the hide animation completes; not invoked if the hide
 *   is cancelled mid-flight (e.g., by [show] toggling back to true).
 * @param defaultWindowInsetsPadding Whether to apply default window insets padding.
 * @param forceCentered When true, the dialog is centered with the center-dialog transitions
 *   regardless of window size (COUI Center-style dialogs, e.g. the rotating loading dialog).
 * @param cornerRadius The corner radius of the dialog panel.
 * @param topInset Optional top inset override. If null, calculated from window insets.
 * @param content The content of the dialog.
 */
@Suppress("ktlint:compose:modifier-not-used-at-root")
@Composable
internal fun DialogContentLayout(
    show: Boolean,
    titleColor: Color,
    summaryColor: Color,
    backgroundColor: Color,
    outsideMargin: DpSize,
    insideMargin: DpSize,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    summary: String? = null,
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    defaultWindowInsetsPadding: Boolean = true,
    forceCentered: Boolean = false,
    maxWidth: Dp = DialogDefaults.MaxWidth,
    largeScreen: Boolean? = null,
    cornerRadius: Dp? = null,
    topInset: Dp? = null,
    content: @Composable () -> Unit,
) {
    val animationProgress = remember { Animatable(0f, visibilityThreshold = 0.0001f) }
    val dimProgress = remember { Animatable(0f) }
    val currentOnDismissFinished by rememberUpdatedState(onDismissFinished)
    val internalVisible = remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime
    val keyboardController = LocalSoftwareKeyboardController.current
    val isCentered = forceCentered || (largeScreen ?: DialogDefaults.isLargeScreen())

    LaunchedEffect(show) {
        // Snapshot at launch so a window-resize crossing the breakpoint mid-animation does not
        // swap the spec or relaunch the effect.
        val centeredAtStart = isCentered
        if (show) {
            internalVisible.value = true
            if (enableWindowDim) {
                launch { dimProgress.animateTo(1f, tween(EnterDurationMillis, easing = DialogEnterEasing)) }
            }
            // COUI coui_bottom_dialog_enter / coui_center_dialog_enter: 250ms,
            // pathInterpolator(0.3, 0, 0.1, 1).
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(EnterDurationMillis, easing = DialogEnterEasing),
            )
        } else {
            if (!internalVisible.value) return@LaunchedEffect
            if (imeInsets.getBottom(density) > 0) {
                keyboardController?.hide()
            }
            // COUI coui_bottom_dialog_exit: 250ms translate; coui_center_dialog_exit: 150ms alpha.
            // Both use pathInterpolator(0.3, 0, 1, 1).
            val exitDuration = if (centeredAtStart) CenterExitDurationMillis else BottomExitDurationMillis
            if (enableWindowDim) {
                launch { dimProgress.animateTo(0f, tween(exitDuration, easing = DialogExitEasing)) }
            }
            animationProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(exitDuration, easing = DialogExitEasing),
            )
            dimProgress.snapTo(0f)
            internalVisible.value = false
            currentOnDismissFinished?.invoke()
        }
    }

    if (!show && !internalVisible.value) return

    val coroutineScope = rememberCoroutineScope()
    val dimAlpha = remember { mutableFloatStateOf(1f) }
    val dialogHeightPx = remember { mutableIntStateOf(0) }
    val backProgress = remember { Animatable(0f) }
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    val windowInfo = LocalWindowInfo.current

    val requestDismiss: () -> Unit = remember {
        { currentOnDismissRequest?.invoke() }
    }

    val resetGesture: suspend () -> Unit = remember {
        {
            backProgress.animateTo(0f, animationSpec = tween(durationMillis = 150))
            animate(dimAlpha.floatValue, 1f, animationSpec = tween(durationMillis = 150)) { value, _ ->
                dimAlpha.floatValue = value
            }
        }
    }

    popupHost(internalVisible.value) {
        val navigationEventState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
        NavigationBackHandler(
            state = navigationEventState,
            isBackEnabled = show,
            onBackCancelled = {
                coroutineScope.launch { resetGesture() }
            },
            onBackCompleted = { requestDismiss() },
        )

        LaunchedEffect(Unit) {
            // Collect inside a single coroutine so the per-frame `transitionState` ticks during a
            // back gesture do not cancel/relaunch the LaunchedEffect on every progress update.
            snapshotFlow { navigationEventState.transitionState }
                .collect { transitionState ->
                    if (
                        transitionState is NavigationEventTransitionState.InProgress &&
                        transitionState.direction == NavigationEventTransitionState.TRANSITIONING_BACK
                    ) {
                        val progress = transitionState.latestEvent.progress
                        backProgress.snapTo(progress)
                        dimAlpha.floatValue = 1f - progress
                    }
                }
        }

        if (enableWindowDim) {
            val baseColor = COUITheme.colorScheme.windowDimming
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawRect(baseColor.copy(alpha = baseColor.alpha * dimAlpha.floatValue * dimProgress.value))
                    },
            )
        }

        // Off-screen distance below the dialog (nav bar + caption bar + outer margin), used so the
        // enter slide starts exactly at the bottom screen edge (COUI fromYDelta 100% of self).
        val slideBottomPadding = if (isCentered) {
            0.dp
        } else {
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
                WindowInsets.captionBar.asPaddingValues().calculateBottomPadding() +
                outsideMargin.height
        }
        val contentModifier = modifier.graphicsLayer {
            val progress = animationProgress.value
            if (isCentered) {
                // COUI coui_center_dialog_enter scales 0.8 -> 1 while fading in;
                // coui_center_dialog_exit only fades out (scale stays at rest).
                val scale = if (show) 0.8f + 0.2f * progress else 1f
                scaleX = scale
                scaleY = scale
                alpha = progress
            } else {
                // COUI coui_bottom_dialog_enter translates by the dialog's own height (100% self);
                // coui_bottom_dialog_exit translates to the parent bottom (100%p).
                val heightPx = dialogHeightPx.intValue
                val distance = if (show && heightPx > 0) {
                    heightPx + slideBottomPadding.toPx()
                } else {
                    windowInfo.containerDpSize.height.toPx()
                }
                translationY = (1f - progress) * distance
                alpha = 1f
            }
        }

        DialogContent(
            maxWidth = maxWidth,
            largeScreen = largeScreen,
            title = title,
            titleColor = titleColor,
            summary = summary,
            summaryColor = summaryColor,
            backgroundColor = backgroundColor,
            outsideMargin = outsideMargin,
            insideMargin = insideMargin,
            defaultWindowInsetsPadding = defaultWindowInsetsPadding,
            backProgress = backProgress,
            dialogHeightPx = dialogHeightPx,
            onDismissRequest = requestDismiss,
            modifier = contentModifier,
            forceCentered = forceCentered,
            cornerRadius = cornerRadius,
            topInset = topInset,
            content = {
                CompositionLocalProvider(LocalDismissState provides requestDismiss) {
                    content()
                }
            },
        )
    }
}

@Suppress("ktlint:compose:modifier-not-used-at-root")
@Composable
internal fun DialogContent(
    title: String?,
    titleColor: Color,
    summary: String?,
    summaryColor: Color,
    backgroundColor: Color,
    outsideMargin: DpSize,
    insideMargin: DpSize,
    defaultWindowInsetsPadding: Boolean,
    backProgress: Animatable<Float, *>,
    dialogHeightPx: MutableIntState,
    onDismissRequest: (() -> Unit)?,
    modifier: Modifier = Modifier,
    forceCentered: Boolean = false,
    maxWidth: Dp = DialogDefaults.MaxWidth,
    largeScreen: Boolean? = null,
    cornerRadius: Dp? = null,
    topInset: Dp? = null,
    content: @Composable () -> Unit,
) {
    val resolvedCornerRadius = cornerRadius ?: DialogDefaults.CornerRadius
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val windowHeight = windowInfo.containerDpSize.height
    val isCentered = forceCentered || (largeScreen ?: DialogDefaults.isLargeScreen())
    val contentAlignment = remember(isCentered) {
        if (isCentered) Alignment.Center else Alignment.BottomCenter
    }
    val currentOnDismiss by rememberUpdatedState(onDismissRequest)

    val calculatedTopInset = if (topInset != null) {
        topInset
    } else {
        val statusBars = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val captionBar = WindowInsets.captionBar.asPaddingValues().calculateTopPadding()
        val displayCutout = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
        maxOf(statusBars, captionBar, displayCutout)
    }

    // Predictive-back translation pad (small-screen only). Pre-converted to px so the per-frame
    // graphicsLayer block does not call asPaddingValues() / toPx() each invalidation.
    val bottomPadding = if (isCentered) {
        0.dp
    } else {
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
            WindowInsets.captionBar.asPaddingValues().calculateBottomPadding()
    }
    val extraBottomPaddingPx = remember(density, bottomPadding, outsideMargin.height, isCentered) {
        if (isCentered) 0f else with(density) { (bottomPadding + outsideMargin.height).toPx() }
    }

    val contentModifier = modifier
        .widthIn(max = maxWidth)
        .heightIn(max = if (isCentered) windowHeight * (2f / 3f) else Dp.Unspecified)
        .onGloballyPositioned { coordinates ->
            dialogHeightPx.intValue = coordinates.size.height
        }
        .graphicsLayer {
            // Apply predictive back animation; branch inside the block so the modifier chain
            // produces a single graphicsLayer node instead of swapping nodes per recomposition.
            if (isCentered) {
                val scale = 1f - (backProgress.value * 0.2f)
                scaleX = scale
                scaleY = scale
            } else {
                val maxOffset = if (dialogHeightPx.intValue > 0) {
                    dialogHeightPx.intValue.toFloat() + extraBottomPaddingPx
                } else {
                    500f
                }
                translationY = backProgress.value * maxOffset
            }
        }
        .pointerInput(Unit) {
            detectTapGestures { /* Consume click */ }
        }
        // COUI panels carry no blanket inside padding; the title/summary/buttons each bring
        // their own margins so button bars can span the full panel width.
        .squircleSurface(color = backgroundColor, cornerRadius = resolvedCornerRadius)

    Box(
        modifier = Modifier
            .then(
                if (defaultWindowInsetsPadding) {
                    Modifier
                        .imePadding()
                        .navigationBarsPadding()
                        .captionBarPadding()
                } else {
                    Modifier
                },
            )
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { currentOnDismiss?.invoke() },
                )
            }
            .semantics {
                onClick(label = "Dismiss") {
                    currentOnDismiss?.invoke()
                    true
                }
            }
            .padding(horizontal = outsideMargin.width)
            .padding(top = calculatedTopInset, bottom = outsideMargin.height),
    ) {
        Column(
            modifier = contentModifier.align(contentAlignment),
        ) {
            val titleMultiline = remember { mutableStateOf(false) }
            val summaryMultiline = remember { mutableStateOf(false) }
            // COUIAlertDialogMaxLinearLayout swaps the 18dp message scroll paddings for 8dp once
            // the title or the message wraps to more than one line (a "tall" dialog).
            val messagePadding = if (titleMultiline.value || summaryMultiline.value) {
                MessagePaddingVerticalTall
            } else {
                MessagePaddingVertical
            }
            title?.let {
                Text(
                    // COUI title_template: 24dp top margin (coui_no_message_alert_dialog_title_margin_top,
                    // from insideMargin.height), 24dp side margins (coui_alert_dialog_message_padding_left,
                    // from insideMargin.width) and 6dp bottom margin
                    // (coui_no_message_alert_dialog_title_margin_bottom, applied by the builder for
                    // every non-tiny dialog).
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = insideMargin.width)
                        .padding(top = insideMargin.height, bottom = TitleMarginBottom),
                    text = it,
                    // COUIDialogTextAppearance.Title: 18sp medium, lineSpacingMultiplier 1.1.
                    fontSize = COUITheme.textStyles.title4.fontSize,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = DialogLineHeight,
                    color = titleColor,
                    onTextLayout = { titleMultiline.value = it.lineCount > 1 },
                )
            }
            summary?.let {
                Text(
                    // COUI message scroll view pads 18dp above and below the message
                    // (coui_alert_dialog_scroll_padding_top/bottom_message_tallDialog; both drop
                    // to 8dp on tall dialogs) and the message itself pads 24dp horizontally
                    // (coui_alert_dialog_message_padding_left, from insideMargin.width).
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = insideMargin.width)
                        .padding(vertical = messagePadding),
                    text = it,
                    // COUI dialog message uses couiTextAppearanceArticleBody (14sp, spacing 1.1).
                    fontSize = COUITheme.textStyles.body2.fontSize,
                    // COUIAlertDialogMessageView centers a single line and start-aligns
                    // once the message wraps.
                    textAlign = if (summaryMultiline.value) TextAlign.Start else TextAlign.Center,
                    lineHeight = DialogLineHeight,
                    color = summaryColor,
                    onTextLayout = { summaryMultiline.value = it.lineCount > 1 },
                )
            }
            content()
        }
    }
}

object DialogDefaults {
    /**
     * Window-size threshold above which the dialog is centered (instead of bottom-aligned)
     * and uses scale-in transitions. Roughly aligns with the Window Size Class
     * compact -> expanded boundary (840 dp width / 480 dp height).
     */
    @Composable
    internal fun isLargeScreen(): Boolean {
        val windowInfo = LocalWindowInfo.current
        val windowWidth = windowInfo.containerDpSize.width
        val windowHeight = windowInfo.containerDpSize.height
        return windowHeight >= 480.dp && windowWidth >= 840.dp
    }

    /**
     * The default color of the title.
     */
    @Composable
    fun titleColor() = COUITheme.colorScheme.onBackground

    /**
     * The default color of the summary. COUI dialog messages use couiColorLabelPrimary
     * (#E6000000 light / #E6FFFFFF dark), the same label color as the title.
     */
    @Composable
    fun summaryColor() = COUITheme.colorScheme.onBackground

    /**
     * The default background color of the dialog.
     */
    @Composable
    fun backgroundColor() = COUITheme.colorScheme.background

    /**
     * The dialog panel corner radius. COUIAlertDialogMaxLinearLayout clips the panel with the
     * couiRoundCornerXLRadius/XXLRadius smooth-corner tokens; the standard alert dialog resolves
     * couiRoundCornerXXLRadius = 19dp (`coui_round_corner_xxl_radius`, plain equivalent 24dp),
     * which the squircle extension reproduces.
     */
    val CornerRadius = 19.dp

    /**
     * The default upper bound on dialog content width. Keeps dialogs from stretching across
     * tablet / desktop windows. Matches COUI `coui_dialog_max_width` (392dp).
     */
    val MaxWidth = 392.dp

    /**
     * The default margin outside the dialog. Matches COUI
     * `coui_dialog_layout_margin_horizontal` (16dp) / `coui_dialog_layout_margin_vertical` (24dp).
     */
    val outsideMargin = DpSize(16.dp, 24.dp)

    /**
     * The default margin for the built-in title and summary texts. Width matches COUI
     * `coui_alert_dialog_message_padding_left` (24dp title/message side margins); height
     * matches `coui_no_message_alert_dialog_title_margin_top` (24dp above the title).
     * The content slot is not padded by this value: COUI button bars span the full panel
     * width and carry their own paddings (24dp horizontal, 12dp top, 22dp bottom).
     */
    val insideMargin = DpSize(24.dp, 24.dp)
}

/** COUI android_alert_dialog_enter: pathInterpolator(0.3, 0, 0.1, 1). */
private val DialogEnterEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** COUI android_alert_dialog_exit: pathInterpolator(0.3, 0, 1, 1). */
private val DialogExitEasing = CubicBezierEasing(0.3f, 0f, 1f, 1f)

/** COUI dialog enter animations (bottom translate / center scale+fade) run for 250ms. */
private val EnterDurationMillis = 250

/** COUI coui_bottom_dialog_exit translate duration. */
private val BottomExitDurationMillis = 250

/** COUI coui_center_dialog_exit alpha duration. */
private val CenterExitDurationMillis = 150

/**
 * COUI dialog title/message lineSpacingMultiplier is 1.1; with Roboto/sans-serif font metrics
 * (ascent + descent = 1.172 x size) that resolves to ~1.29 x fontSize.
 */
private val DialogLineHeight = 1.29f.em

/**
 * COUI coui_no_message_alert_dialog_title_margin_bottom: COUIAlertDialogBuilder.initTitle
 * applies the 6dp bottom margin for every non-tiny dialog, with or without a message.
 */
private val TitleMarginBottom = 6.dp

/** COUI coui_alert_dialog_scroll_padding_top/bottom_message_tallDialog (the resting values). */
private val MessagePaddingVertical = 18.dp

/**
 * COUI coui_alert_dialog_scroll_padding_top/bottom_message: COUIAlertDialogMaxLinearLayout
 * switches the message scroll paddings to 8dp when the title or message is multiline.
 */
private val MessagePaddingVerticalTall = 8.dp
