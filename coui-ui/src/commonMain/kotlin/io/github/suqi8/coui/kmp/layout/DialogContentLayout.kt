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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.isSpecified
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur
import io.github.suqi8.coui.kmp.effect.MaterialStrokePresets
import io.github.suqi8.coui.kmp.effect.materialStroke
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
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
 * @param maxWidth The maximum width of the dialog.
 * @param largeScreen Optional override for the large-screen presentation (centered scale/fade
 *   instead of bottom slide-in). If null, detected from the window size.
 * @param cornerRadius Optional corner radius override. If null, [DialogDefaults.CornerRadius].
 * @param topInset Optional top inset override. If null, calculated from window insets.
 * @param backgroundModifier Applied inside the panel's corner clip, below the content.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred panel.
 * @param customContent The COUI `customPanel` / `listPanel` slot between the message and
 *   [content]. Its presence drives the COUI content spacing (`COUIAlertDialogBuilder.initContentSpacing`).
 * @param customContentPaddingBottom The bottom padding of [customContent]; the center layouts pad
 *   their `customPanel` by `coui_alert_dialog_customer_layout_padding_bottom`, the assignment
 *   layouts do not.
 * @param summaryMaxHeight The max height of the message scroll view, see [DialogContent].
 * @param dismissOnClickOutside Whether a tap outside the panel invokes [onDismissRequest].
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
    backgroundModifier: Modifier = Modifier,
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
    materialStroke: Boolean = false,
    customContent: (@Composable () -> Unit)? = null,
    customContentPaddingBottom: Dp = DialogDefaults.CustomContentPaddingBottom,
    summaryMaxHeight: Dp = Dp.Unspecified,
    dismissOnClickOutside: Boolean = true,
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
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(EnterDurationMillis, easing = DialogEnterEasing),
            )
        } else {
            if (!internalVisible.value) return@LaunchedEffect
            if (imeInsets.getBottom(density) > 0) {
                keyboardController?.hide()
            }
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
            val baseColor = COUITheme.colorScheme.mask
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawRect(baseColor.copy(alpha = baseColor.alpha * dimAlpha.floatValue * dimProgress.value))
                    },
            )
        }

        // Off-screen distance below the dialog so the enter slide starts at the bottom screen edge.
        val slideBottomPadding = if (isCentered) {
            0.dp
        } else {
            dialogBottomGap(
                navigationBar = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                margin = outsideMargin.height,
            ) + WindowInsets.captionBar.asPaddingValues().calculateBottomPadding()
        }
        val contentModifier = modifier.graphicsLayer {
            val progress = animationProgress.value
            if (isCentered) {
                // COUI: enter scales 0.8 -> 1 while fading in; exit only fades out.
                val scale = if (show) 0.8f + 0.2f * progress else 1f
                scaleX = scale
                scaleY = scale
                alpha = progress
            } else {
                // COUI: enter slides by the dialog's own height; exit slides to the parent bottom.
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

        val wrappedCustomContent: (@Composable () -> Unit)? = if (customContent != null) {
            {
                DialogSlotLocals(requestDismiss = requestDismiss, materialStroke = materialStroke, content = customContent)
            }
        } else {
            null
        }
        DialogContent(
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
            maxWidth = maxWidth,
            largeScreen = largeScreen,
            cornerRadius = cornerRadius,
            topInset = topInset,
            backgroundModifier = backgroundModifier,
            materialStroke = materialStroke,
            customContent = wrappedCustomContent,
            customContentPaddingBottom = customContentPaddingBottom,
            summaryMaxHeight = summaryMaxHeight,
            dismissOnClickOutside = dismissOnClickOutside,
            content = {
                DialogSlotLocals(requestDismiss = requestDismiss, materialStroke = materialStroke, content = content)
            },
        )
    }
}

/** The composition locals every dialog slot sees. */
@Composable
private fun DialogSlotLocals(
    requestDismiss: () -> Unit,
    materialStroke: Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalDismissState provides requestDismiss,
        // COUIButtonBarLayout.setBlurBackgroundWindow: the buttons follow the panel blur.
        LocalDialogMaterial provides materialStroke,
        // The page backdrop lies behind the panel; buttons on it keep their fill.
        LocalMaterialBackdrop provides null,
        // A menu opened on the panel would blur the page beneath it, not the panel.
        LocalPopupMaterialBlur provides null,
        LocalCardListPosition provides CardListPosition.None,
        content = content,
    )
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
    backgroundModifier: Modifier = Modifier,
    forceCentered: Boolean = false,
    maxWidth: Dp = DialogDefaults.MaxWidth,
    largeScreen: Boolean? = null,
    cornerRadius: Dp? = null,
    topInset: Dp? = null,
    materialStroke: Boolean = false,
    customContent: (@Composable () -> Unit)? = null,
    customContentPaddingBottom: Dp = DialogDefaults.CustomContentPaddingBottom,
    summaryMaxHeight: Dp = Dp.Unspecified,
    dismissOnClickOutside: Boolean = true,
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

    val navigationBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // The gap between the panel and the bottom screen edge, see [dialogBottomGap]; the centered
    // tier keeps the plain navigation inset + margin.
    val bottomGap = if (isCentered) {
        navigationBarBottom + outsideMargin.height
    } else {
        dialogBottomGap(navigationBar = navigationBarBottom, margin = outsideMargin.height)
    }
    // Predictive-back translation pad (small-screen only). Pre-converted to px so the per-frame
    // graphicsLayer block does not call asPaddingValues() / toPx() each invalidation.
    val captionBarBottom = WindowInsets.captionBar.asPaddingValues().calculateBottomPadding()
    val extraBottomPaddingPx = remember(density, bottomGap, captionBarBottom, isCentered) {
        if (isCentered) 0f else with(density) { (bottomGap + captionBarBottom).toPx() }
    }

    val contentModifier = modifier
        .widthIn(max = maxWidth)
        .heightIn(
            max = if (isCentered) {
                minOf(windowHeight * (2f / 3f), DialogDefaults.MaxHeight)
            } else {
                DialogDefaults.MaxHeight
            },
        )
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
        // COUI panels carry no blanket inside padding; each slot brings its own margins.
        .squircleSurface(color = backgroundColor, cornerRadius = resolvedCornerRadius, weight = COUIRoundCorner.Large.Weight)
        // Inside the corner clip: a backdrop blur provider paints here, under the content.
        .then(backgroundModifier)
        // COUIAlertDialogRootLayout draws its shadow edge in onDraw, above the background and
        // below the children.
        .materialStroke(
            light = MaterialStrokePresets.DialogLight,
            dark = MaterialStrokePresets.DialogDark,
            topCornerRadius = resolvedCornerRadius,
            bottomCornerRadius = resolvedCornerRadius,
            enabled = materialStroke,
        )

    Box(
        modifier = Modifier
            .then(
                if (defaultWindowInsetsPadding) {
                    Modifier
                        .imePadding()
                        // The bottom navigation inset is folded into the bottom gap below.
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                        .captionBarPadding()
                } else {
                    Modifier
                },
            )
            .fillMaxSize()
            .pointerInput(dismissOnClickOutside) {
                detectTapGestures(
                    onTap = { if (dismissOnClickOutside) currentOnDismiss?.invoke() },
                )
            }
            .then(
                if (dismissOnClickOutside) {
                    Modifier.semantics {
                        onClick(label = "Dismiss") {
                            currentOnDismiss?.invoke()
                            true
                        }
                    }
                } else {
                    Modifier
                },
            )
            .padding(horizontal = outsideMargin.width)
            .padding(
                top = calculatedTopInset,
                bottom = if (defaultWindowInsetsPadding) bottomGap else outsideMargin.height,
            ),
    ) {
        Column(
            modifier = contentModifier.align(contentAlignment),
        ) {
            val summaryMultiline = remember { mutableStateOf(false) }
            title?.let {
                DialogTitle(
                    text = it,
                    color = titleColor,
                    modifier = Modifier
                        .padding(horizontal = insideMargin.width)
                        .padding(
                            top = insideMargin.height,
                            // COUIAlertDialogBuilder.initContentSpacing: a dialog with no message,
                            // custom view or list adds coui_alert_dialog_scroll_padding_bottom_message
                            // below the title.
                            bottom = if (summary == null && customContent == null) {
                                TitleMarginBottom + MessagePaddingBottom
                            } else {
                                TitleMarginBottom
                            },
                        ),
                )
            }
            summary?.let {
                // COUI keeps the message in a COUIMaxHeightNestedScrollView inside the weighted
                // contentPanel, so a long message scrolls while the button bar stays in view.
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .then(
                            if (summaryMaxHeight.isSpecified) Modifier.heightIn(max = summaryMaxHeight) else Modifier,
                        )
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = insideMargin.width)
                            // COUIAlertDialogMessageScrollViewStyle: 0dp above, 16dp below; 4dp below
                            // once a custom view or a list follows (initContentSpacing). With no
                            // title the message takes the title's top margin instead.
                            .padding(
                                top = if (title == null) insideMargin.height else MessagePaddingTop,
                                bottom = if (customContent != null) {
                                    MessagePaddingBottomWithCustomContent
                                } else {
                                    MessagePaddingBottom
                                },
                            ),
                        text = it,
                        // COUIAlertDialogMessageStyle: couiTextBodyS (13sp).
                        style = COUITheme.textStyles.bodyS,
                        // COUIAlertDialogMessageView centers a single-line message and start-aligns
                        // once it wraps.
                        textAlign = if (summaryMultiline.value) TextAlign.Start else TextAlign.Center,
                        color = summaryColor,
                        onTextLayout = { summaryMultiline.value = it.lineCount > 1 },
                    )
                }
            }
            customContent?.let {
                // COUI customPanel / listPanel: weighted like the contentPanel, so a tall custom
                // view shrinks before the button bar does.
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth()
                        .padding(bottom = customContentPaddingBottom),
                ) {
                    it()
                }
            }
            content()
        }
    }
}

/**
 * The COUI dialog title (`COUIDialogTitle` inside the `alert_title_scroll_view`
 * `COUIMaxHeightScrollView`): centered `couiTextHeadlineS` text that scrolls once it outgrows
 * [DialogDefaults.titleMaxHeight].
 */
@Composable
internal fun DialogTitle(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = DialogDefaults.titleMaxHeight())
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            // COUIDialogTextAppearance.Title: couiTextHeadlineS (18sp). Its
            // sans-serif-semibold family is overridden by the inherited
            // android:textFontWeight 550.
            style = COUITheme.textStyles.headlineS,
            fontWeight = TitleFontWeight,
            textAlign = TextAlign.Center,
            color = color,
        )
    }
}

object DialogDefaults {
    /**
     * Whether the window takes the large-screen presentation, in which COUI centers the dialog with
     * the center-dialog transitions. `COUIAlertDialogBuilder.isForceCenterStyleInLargeScreen` checks
     * `COUIResponsiveUtils.isLargePadWindow`: the window is `WindowTotalSizeClass.Expanded` in
     * either orientation, that is at least 840dp wide and 480dp tall, and not a portrait window
     * narrower than 960dp once it is 900dp or taller (`ExpandedPortrait`). COUI also excludes
     * foldable devices, which cannot be detected here.
     */
    @Composable
    internal fun isLargeScreen(): Boolean {
        val windowInfo = LocalWindowInfo.current
        val windowWidth = windowInfo.containerDpSize.width
        val windowHeight = windowInfo.containerDpSize.height
        return isExpandedWindow(windowWidth, windowHeight) || isExpandedWindow(windowHeight, windowWidth)
    }

    /**
     * `WindowTotalSizeClass.fromWidthAndHeight(width, height) == Expanded`, after the COUI
     * responsive breakpoints (`Breakpoints`: 840dp expanded width, 480 / 900dp medium / expanded
     * height, 960dp expanded maximum width).
     */
    private fun isExpandedWindow(width: Dp, height: Dp): Boolean {
        if (width < 840.dp || height < 480.dp) return false
        return height < 900.dp || !(height > width && width < 960.dp)
    }

    /**
     * The max height of the dialog title before it scrolls. COUI
     * `coui_alert_dialog_builder_title_scroll_max_height`: 80dp, 120dp from `values-h300dp`, 160dp
     * from `values-h560dp`.
     */
    @Composable
    internal fun titleMaxHeight(): Dp {
        val windowHeight = LocalWindowInfo.current.containerDpSize.height
        return when {
            windowHeight >= 560.dp -> 160.dp
            windowHeight >= 300.dp -> 120.dp
            else -> 80.dp
        }
    }

    /**
     * The bottom padding of a custom view in the center layouts. COUI
     * `coui_alert_dialog_customer_layout_padding_bottom` on the `customPanel` of
     * `coui_center_alert_dialog_layout`.
     */
    val CustomContentPaddingBottom = 8.dp

    /**
     * The default color of the title.
     */
    @Composable
    fun titleColor() = COUITheme.colorScheme.labelPrimary

    /**
     * The default color of the summary. COUI 17 `COUIAlertDialogMessageStyle` uses
     * `?couiColorLabelSecondary`.
     */
    @Composable
    fun summaryColor() = COUITheme.colorScheme.labelSecondary

    /**
     * The default background color of the dialog (`?couiColorSurface`, the opaque fill
     * `coui_alert_dialog_background` draws and the overlay COUI falls back to when the blur is
     * unavailable). Pass [androidx.compose.ui.graphics.Color.Transparent] when a backdrop blur
     * paints the panel through `backgroundModifier`.
     */
    @Composable
    fun backgroundColor() = COUITheme.colorScheme.surface

    /**
     * The default corner radius of the dialog panel. COUI 17 `COUIAlertDialogMaxLinearLayout`
     * takes `coui_round_corner_xxxxl_radius_large_16_1` (45dp) once the Oplus OS version is above 37
     * with smooth corners on, drawn as a weight-3.0 smooth round rect, which the squircle surface draws
     * with [COUIRoundCorner.Large.Weight].
     */
    val CornerRadius = 45.dp

    /**
     * The default upper bound on the visible panel width. COUI `COUIAlertDialogLayoutStyle` caps
     * the `parentPanel` at `coui_dialog_max_width` (392dp), a width that includes the
     * `coui_dialog_layout_margin_horizontal` (16dp) side insets of its background, leaving 360dp for
     * the panel itself.
     */
    val MaxWidth = 360.dp

    /**
     * The upper bound on the panel height. COUI `COUIAlertDialogLayoutStyle` caps the panel at
     * `coui_dialog_max_height` (632dp), a height that includes the
     * `coui_dialog_layout_margin_vertical` (32dp) bottom inset of its background, leaving 600dp
     * for the panel itself.
     */
    val MaxHeight = 600.dp

    /**
     * The default margin outside the dialog. COUI `coui_dialog_layout_margin_horizontal` (16dp) /
     * `coui_dialog_layout_margin_vertical` (32dp); the height is the bottom gap on a screen
     * without a navigation bar, see [MarginBottomWithNavigationBar].
     */
    val outsideMargin = DpSize(16.dp, 32.dp)

    /**
     * The distance between a bottom-aligned dialog and the bottom screen edge under three-button
     * navigation. COUI `COUIAlertDialogMaxLinearLayout.updateDialogBottomMargin` pads the panel by
     * `coui_dialog_layout_margin_bottom_with_navigation_bar - navigationBarHeight`, so the panel
     * always ends 60dp above the screen edge; under gesture navigation it pads by
     * `max(0, coui_dialog_layout_margin_vertical - navigationBarHeight)` instead.
     */
    val MarginBottomWithNavigationBar = 60.dp

    /**
     * The default margin for the built-in title and summary texts: width is their horizontal
     * padding (COUI `coui_alert_dialog_message_padding_left`), height is the top margin of the
     * title (`coui_alert_dialog_title_margin_top`). The content slot is left unpadded so the button
     * bar can lay out its own paddings.
     */
    val insideMargin = DpSize(24.dp, 24.dp)

    /**
     * The min height of a horizontal dialog button bar and of each of its buttons. COUI
     * `coui_alert_dialog_button_height`.
     */
    val ButtonBarMinHeight = 44.dp

    /**
     * The paddings of a button in a horizontal dialog button bar. COUI
     * `COUIButtonBarLayout.setButHorizontal`: `coui_alert_dialog_button_horizontal_padding` (12dp)
     * on both sides and `coui_bottom_alert_dialog_horizontal_button_padding_top/bottom_extra_new`
     * (4dp) above and below the label.
     */
    val ButtonBarInsideMargin = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
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

/** COUI couiTextHeadlineS android:textFontWeight. */
private val TitleFontWeight = FontWeight(550)

/** COUI coui_alert_dialog_title_margin_bottom. */
private val TitleMarginBottom = 6.dp

/** COUI coui_alert_dialog_scroll_padding_top_message_tallDialog (the message scroll view style). */
private val MessagePaddingTop = 0.dp

/** COUI coui_alert_dialog_scroll_padding_bottom_message_tallDialog (the message scroll view style). */
private val MessagePaddingBottom = 16.dp

/**
 * COUI coui_alert_dialog_scroll_padding_bottom_message_has_title_in_assignment, the message bottom
 * padding `COUIAlertDialogBuilder.initContentSpacing` sets once a custom view or a list follows.
 */
private val MessagePaddingBottomWithCustomContent = 4.dp

/**
 * The gap between a bottom-aligned dialog and the bottom screen edge, after
 * `COUIAlertDialogMaxLinearLayout.updateDialogBottomMargin`. Gesture navigation pads the panel by
 * `max(0, margin - navigationBar)`, which lands it [margin] above the screen edge; three-button
 * navigation pads it by `60dp - navigationBar`, which lands it
 * [DialogDefaults.MarginBottomWithNavigationBar] above the edge. Compose cannot tell the two
 * navigation modes apart, so a navigation bar taller than [margin] is taken as the three-button
 * one (the gesture handle bar is well below the 32dp default).
 */
private fun dialogBottomGap(navigationBar: Dp, margin: Dp): Dp = if (navigationBar <= margin) {
    margin
} else {
    maxOf(DialogDefaults.MarginBottomWithNavigationBar, navigationBar)
}
