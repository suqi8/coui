// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.CouiButton
import io.github.suqi8.coui.kmp.basic.PageIndicator
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.effect.ContentButtonMaterialType
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * One page of a guide dialog (`COUIGuidePageItem`): an illustration over a title and a
 * description. A missing [image] and a blank [title] or [description] are left out
 * (`COUIGuidePageAdapter.bindTextOrGoneIfBlank`).
 *
 * @param title The page title.
 * @param description The page description.
 * @param image The illustration, cropped to fill the full width at the dialog's image height.
 * @param imageContentDescription The accessibility label of [image].
 */
@Stable
data class GuideDialogPage(
    val title: String? = null,
    val description: String? = null,
    val image: Painter? = null,
    val imageContentDescription: String? = null,
)

/**
 * Internal shared layout for the guide dialog variants (`COUIGuideDialogBuilder` with
 * `COUIGuideLayoutContentView`): a bottom (center on large screens) alert dialog holding a pager of
 * [GuideDialogPage]s, a page indicator and a skip / next button pair that merges into a single
 * "got it" button on the last page.
 *
 * @param show Whether the dialog is shown.
 * @param pages The guide pages.
 * @param onStart Invoked by the start button (the merged button on the last page, or the only button
 *   of a single-page guide).
 * @param popupHost A composable that provides the dialog container (e.g., DialogLayout or Dialog).
 * @param modifier The modifier applied to the dialog panel.
 * @param backgroundModifier Applied inside the panel's corner clip, below the content.
 * @param onSkip Invoked by the skip button; `null` falls back to [onStart].
 * @param onNext Invoked with the target page when the next button turns the page.
 * @param onDismissRequest Invoked on the back gesture. Taps outside the panel are ignored
 *   (`setCanceledOnTouchOutside(false)`).
 * @param skipText The skip button label.
 * @param nextText The next button label.
 * @param startText The start button label.
 * @param imageHeight The height of every page illustration.
 * @param imagePadding The margins around every page illustration.
 * @param colors The title and description colors.
 * @param backgroundColor The panel fill.
 * @param enableWindowDim Whether to dim the window behind the dialog.
 * @param materialStroke Whether to draw the material stroke of a blurred panel; the buttons then take
 *   their drawable material too (`COUIGuideDialogButtonLayout.setBlurState`).
 * @param onDismissFinished Invoked when the hide animation completes.
 * @param topInset Optional top inset override forwarded to [DialogContentLayout].
 */
@Composable
internal fun GuideDialogContentLayout(
    show: Boolean,
    pages: List<GuideDialogPage>,
    onStart: () -> Unit,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    onSkip: (() -> Unit)? = null,
    onNext: ((page: Int) -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = null,
    skipText: String = GuideDialogDefaults.SkipText,
    nextText: String = GuideDialogDefaults.NextText,
    startText: String = GuideDialogDefaults.StartText,
    imageHeight: Dp = GuideDialogDefaults.ImageHeight,
    imagePadding: PaddingValues = GuideDialogDefaults.ImagePadding,
    colors: GuideDialogColors = GuideDialogDefaults.guideDialogColors(),
    backgroundColor: Color = DialogDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    materialStroke: Boolean = false,
    onDismissFinished: (() -> Unit)? = null,
    topInset: Dp? = null,
) {
    // The panel bounds in root coordinates, for ignoring taps outside it.
    val panelBounds = remember { mutableStateOf(Rect.Zero) }
    DialogContentLayout(
        show = show,
        titleColor = colors.titleColor,
        summaryColor = colors.descriptionColor,
        backgroundColor = backgroundColor,
        outsideMargin = DialogDefaults.outsideMargin,
        insideMargin = DialogDefaults.insideMargin,
        popupHost = { visible, hostContent ->
            popupHost(visible) {
                OutsideTapBlocker(panelBounds = { panelBounds.value }) {
                    hostContent()
                }
            }
        },
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        topInset = topInset,
        materialStroke = materialStroke,
    ) {
        GuideDialogBody(
            pages = pages,
            onStart = onStart,
            onSkip = onSkip,
            onNext = onNext,
            skipText = skipText,
            nextText = nextText,
            startText = startText,
            imageHeight = imageHeight,
            imagePadding = imagePadding,
            colors = colors,
            onPanelBoundsChange = { panelBounds.value = it },
        )
    }
}

@Composable
private fun GuideDialogBody(
    pages: List<GuideDialogPage>,
    onStart: () -> Unit,
    onSkip: (() -> Unit)?,
    onNext: ((Int) -> Unit)?,
    skipText: String,
    nextText: String,
    startText: String,
    imageHeight: Dp,
    imagePadding: PaddingValues,
    colors: GuideDialogColors,
    onPanelBoundsChange: (Rect) -> Unit,
) {
    val pageCount = pages.size
    val multiple = pageCount > 1
    // COUIGuideDialogBuilder.show builds a new dialog each time: the pager starts on page 0.
    val pagerState = rememberPagerState { pageCount }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { onPanelBoundsChange(it.boundsInRoot()) },
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            // initAfterAdapterSet: a single page cannot be swiped.
            userScrollEnabled = multiple,
            verticalAlignment = Alignment.Top,
        ) { page ->
            GuidePage(
                page = pages[page],
                imageHeight = imageHeight,
                imagePadding = imagePadding,
                colors = colors,
            )
        }
        // coui_dialog_guide_viewpager2_margin_bottom / _margin_single_bottom.
        Spacer(Modifier.height(if (multiple) PagerMarginBottom else PagerMarginBottomSingle))
        if (multiple) {
            // The indicator follows the pager; its dots are not clickable (setIsClickable(false)).
            PageIndicator(
                pagerState = pagerState,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                dotClickable = false,
            )
            Spacer(Modifier.height(IndicatorMarginBottom))
        }
        GuideButtonBar(
            pagerState = pagerState,
            pageCount = pageCount,
            skipText = skipText,
            nextText = nextText,
            startText = startText,
            onStart = onStart,
            onSkip = onSkip ?: onStart,
            onNext = onNext,
        )
        // coui_dialog_guide_button_layout_margin_bottom plus the custom panel's
        // coui_alert_dialog_customer_layout_padding_bottom.
        Spacer(Modifier.height(ButtonBarMarginBottom + CustomPanelPaddingBottom))
    }
}

/** `coui_guide_page_item.xml`: the cropped illustration, a centred title and a centred description. */
@Composable
private fun GuidePage(
    page: GuideDialogPage,
    imageHeight: Dp,
    imagePadding: PaddingValues,
    colors: GuideDialogColors,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        page.image?.let { image ->
            Image(
                painter = image,
                contentDescription = page.imageContentDescription,
                modifier = Modifier
                    .padding(imagePadding)
                    .fillMaxWidth()
                    .height(imageHeight),
                contentScale = ContentScale.Crop,
            )
        }
        page.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                modifier = Modifier
                    .padding(top = TitleMarginTop, bottom = TitleMarginBottom)
                    .padding(horizontal = TextPaddingHorizontal),
                // ?couiTextHeadlineS: 18sp at textFontWeight 550.
                style = COUITheme.textStyles.headlineS,
                fontWeight = TitleFontWeight,
                textAlign = TextAlign.Center,
                color = colors.titleColor,
            )
        }
        page.description?.takeIf { it.isNotBlank() }?.let { description ->
            Text(
                text = description,
                modifier = Modifier.padding(horizontal = TextPaddingHorizontal),
                // ?couiTextBodyS: 13sp.
                style = COUITheme.textStyles.bodyS,
                textAlign = TextAlign.Center,
                color = colors.descriptionColor,
            )
        }
    }
}

/**
 * `COUIGuideDialogButtonLayout`: a skip button and a next button side by side. Scrolling from the
 * second-to-last page to the last one widens the next button over the skip button, which fades
 * out, while the next label cross-fades into the start label.
 */
@Composable
private fun GuideButtonBar(
    pagerState: PagerState,
    pageCount: Int,
    skipText: String,
    nextText: String,
    startText: String,
    onStart: () -> Unit,
    onSkip: () -> Unit,
    onNext: ((Int) -> Unit)?,
) {
    val multiple = pageCount > 1
    val scope = rememberCoroutineScope()
    val material = LocalDialogMaterial.current
    // mRawOffset: the spring-followed merge progress, 0 with two buttons, 1 with the merged one.
    val merge = remember { Animatable(0f, visibilityThreshold = 0.001f) }
    // COUITwoTextButton: the next label is shown first, the start label hidden.
    val nextAlpha = remember { mutableFloatStateOf(1f) }
    val startAlpha = remember { mutableFloatStateOf(0f) }
    val dragged by pagerState.interactionSource.collectIsDraggedAsState()
    val currentOnStart by rememberUpdatedState(onStart)
    val currentOnSkip by rememberUpdatedState(onSkip)
    val currentOnNext by rememberUpdatedState(onNext)

    LaunchedEffect(pagerState, pageCount) {
        if (pageCount <= 1) return@LaunchedEffect
        // onPageScrolled: the position past the second-to-last page, clamped and eased, which the
        // width spring then follows (response 0.15, bounce 0).
        snapshotFlow {
            val position = pagerState.currentPage + pagerState.currentPageOffsetFraction
            PageScrollEasing.transform((position - (pageCount - 2)).coerceIn(0f, 1f))
        }.collectLatest { offset ->
            // mStartDraggingPage: the page the scroll started from.
            val startPage = pagerState.settledPage
            when {
                startPage == pageCount - 1 && offset < 1f -> guideTextAlphas(1f - offset, fromLast = true, nextAlpha, startAlpha)
                startPage == pageCount - 2 && offset > 0f -> guideTextAlphas(offset, fromLast = false, nextAlpha, startAlpha)
            }
            merge.animateTo(offset, MergeSpring)
        }
    }

    val nextClick: () -> Unit = remember(pagerState, multiple) {
        {
            when {
                !multiple -> currentOnStart()

                // A drag in progress (SCROLL_STATE_DRAGGING) ignores the button.
                dragged -> Unit

                startAlpha.floatValue > nextAlpha.floatValue -> currentOnStart()

                else -> {
                    val target = (pagerState.currentPage + 1).coerceAtMost(pagerState.pageCount - 1)
                    currentOnNext?.invoke(target)
                    scope.launch { pagerState.animateScrollToPage(target, animationSpec = ProgramScrollSpec) }
                }
            }
        }
    }

    Layout(
        content = {
            if (multiple) {
                GuideButton(
                    onClick = {
                        // Only a fully shown skip button skips.
                        if (skipAlpha(merge.value) == 1f) currentOnSkip()
                    },
                    primary = false,
                    material = material,
                    modifier = Modifier.graphicsLayer { alpha = skipAlpha(merge.value) },
                ) {
                    Text(
                        text = skipText,
                        style = COUITheme.textStyles.buttonL,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            GuideButton(
                onClick = nextClick,
                primary = true,
                material = material,
            ) {
                if (multiple) {
                    val label = if (startAlpha.floatValue > nextAlpha.floatValue) startText else nextText
                    Box(
                        modifier = Modifier.clearAndSetSemantics { contentDescription = label },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = nextText,
                            modifier = Modifier.graphicsLayer { alpha = nextAlpha.floatValue },
                            style = COUITheme.textStyles.buttonL,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = startText,
                            modifier = Modifier.graphicsLayer { alpha = startAlpha.floatValue },
                            style = COUITheme.textStyles.buttonL,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                } else {
                    Text(
                        text = startText,
                        style = COUITheme.textStyles.buttonL,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ButtonBarPaddingHorizontal),
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val spacing = ButtonSpacing.roundToPx()
        if (!multiple) {
            // SINGLE_BUTTON: the start button spans the bar.
            val start = measurables[0].measure(Constraints(minWidth = width, maxWidth = width))
            return@Layout layout(width, start.height) { start.placeRelative(0, 0) }
        }
        val raw = merge.value
        val buttonWidth = max(0, (width - spacing) / 2)
        // widthChange: the next button widens over the skip button once the merge passes 0.2.
        val grow = ((raw - WIDTH_CHANGE_START_PROGRESS) / (1f - WIDTH_CHANGE_START_PROGRESS)).coerceIn(0f, 1f)
        val nextWidth = (buttonWidth + (buttonWidth + spacing) * grow).roundToInt()
        val skip = measurables[0].measure(Constraints(minWidth = buttonWidth, maxWidth = buttonWidth))
        val next = measurables[1].measure(Constraints(minWidth = nextWidth, maxWidth = nextWidth))
        val height = max(skip.height, next.height)
        layout(width, height) {
            if (skipAlpha(raw) > 0f) skip.placeRelative(0, (height - skip.height) / 2)
            // layoutButtonTransition: the next button's centre travels from its own slot to the
            // middle of the bar.
            val restCenter = buttonWidth + spacing + buttonWidth / 2f
            val center = restCenter + raw * (width / 2f - restCenter)
            next.placeRelative((center - next.width / 2f).roundToInt(), (height - next.height) / 2)
        }
    }
}

@Composable
private fun GuideButton(
    onClick: () -> Unit,
    primary: Boolean,
    material: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // The skip button is Widget.COUI.Button.Large.ButtonNew.Secondary.NoMargin, the next button
    // Widget.COUI.Button.Large as PRIMARY; both take the drawable material only while the window
    // background is blurred (setIsDrawableMaterialEffect(true) + setBlurState).
    CouiButton(
        onClick = onClick,
        colors = if (primary) ButtonDefaults.buttonColorsPrimary() else ButtonDefaults.buttonColors(),
        materialType = when {
            !material -> null
            primary -> ContentButtonMaterialType.DrawablePrimary
            else -> ContentButtonMaterialType.DrawableSecondary
        },
        modifier = modifier,
        minWidth = 0.dp,
        minHeight = ButtonDefaults.MinHeight,
    ) {
        content()
    }
}

/**
 * `textAlphaAnimate`: the next and start labels cross-fade over the middle of the scroll. Scrolling
 * away from the second-to-last page ([fromLast] false) fades the next label out over 0.5..0.85
 * and the start label in over 0.5..1; scrolling back from the last page does the reverse. Once one
 * label is fully shown the other is cleared.
 */
private fun guideTextAlphas(
    fraction: Float,
    fromLast: Boolean,
    nextAlpha: MutableFloatState,
    startAlpha: MutableFloatState,
) {
    val fadeOut = if (fraction > TEXT_FADE_START_PROGRESS) {
        ((TEXT_FADE_END_PROGRESS - fraction) / TEXT_FADE_RANGE).coerceIn(0f, 1f)
    } else {
        1f
    }
    val fadeIn = ((fraction - TEXT_FADE_START_PROGRESS) / TEXT_FADE_START_PROGRESS).coerceIn(0f, 1f)
    if (fromLast) {
        nextAlpha.floatValue = fadeIn
        startAlpha.floatValue = fadeOut
    } else {
        nextAlpha.floatValue = fadeOut
        startAlpha.floatValue = fadeIn
    }
    if (startAlpha.floatValue == 1f) {
        nextAlpha.floatValue = 0f
    } else if (nextAlpha.floatValue == 1f) {
        startAlpha.floatValue = 0f
    }
}

/** `widthChange`: the skip button fades out over the first 60% of the merge. */
private fun skipAlpha(merge: Float): Float = ((SECONDARY_BUTTON_ALPHA_END_PROGRESS - merge) / SECONDARY_BUTTON_ALPHA_END_PROGRESS).coerceIn(0f, 1f)

/**
 * Ignores the taps outside the dialog panel ([panelBounds], in root coordinates), so the dialog
 * does not dismiss on them (`COUIGuideDialogBuilder.show`: `setCanceledOnTouchOutside(false)`).
 */
@Composable
private fun OutsideTapBlocker(
    panelBounds: () -> Rect,
    content: @Composable () -> Unit,
) {
    val origin = remember { OriginHolder() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { origin.value = it.positionInRoot() }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    val bounds = panelBounds()
                    if (bounds.isEmpty || bounds.contains(down.position + origin.value)) return@awaitEachGesture
                    down.consume()
                    do {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        event.changes.forEach { it.consume() }
                    } while (event.changes.any { it.pressed })
                }
            },
    ) {
        content()
    }
}

private class OriginHolder {
    var value: Offset = Offset.Zero
}

/**
 * The title and description colors of a guide dialog page.
 *
 * @param titleColor The page title color.
 * @param descriptionColor The page description color.
 */
@Immutable
data class GuideDialogColors(
    val titleColor: Color,
    val descriptionColor: Color,
)

/** Contains default values used by the guide dialogs. */
object GuideDialogDefaults {
    /** The skip button label (`coui_guide_dialog_skip_text`). */
    val SkipText = "Skip"

    /** The next button label (`coui_guide_dialog_next_text`). */
    val NextText = "Next"

    /** The start button label (`coui_guide_dialog_known_text`). */
    val StartText = "Got it"

    /** The illustration height (`ImageHeightStyle.DEFAULT`, 328dp). */
    val ImageHeight = 328.dp

    /** The shorter illustration height (`ImageHeightStyle.HEIGHT_240DP`). */
    val ImageHeightSmall = 240.dp

    /**
     * The margins around the illustration: none (`COUIGuideLayoutContentView` starts them at 0).
     * COUI's `setImagePadding` takes dp but its adapter applies the raw numbers as pixels; here the
     * padding is in dp.
     */
    val ImagePadding = PaddingValues(0.dp)

    /**
     * The default [GuideDialogColors]: the title in `?couiColorLabelPrimary` and the description in
     * `?couiColorLabelSecondary` (`coui_guide_page_item.xml`).
     *
     * @param titleColor The page title color.
     * @param descriptionColor The page description color.
     */
    @Composable
    fun guideDialogColors(
        titleColor: Color = DialogDefaults.titleColor(),
        descriptionColor: Color = DialogDefaults.summaryColor(),
    ): GuideDialogColors = remember(titleColor, descriptionColor) {
        GuideDialogColors(titleColor = titleColor, descriptionColor = descriptionColor)
    }
}

/** `coui_guide_page_item.xml` title android:layout_marginTop. */
private val TitleMarginTop = 24.dp

/** `coui_guide_page_item.xml` title android:layout_marginBottom. */
private val TitleMarginBottom = 6.dp

/** `coui_guide_page_item.xml` text android:paddingHorizontal. */
private val TextPaddingHorizontal = 24.dp

/** ?couiTextHeadlineS android:textFontWeight. */
private val TitleFontWeight = FontWeight(550)

/** coui_dialog_guide_viewpager2_margin_bottom. */
private val PagerMarginBottom = 16.dp

/** coui_dialog_guide_viewpager2_margin_single_bottom. */
private val PagerMarginBottomSingle = 20.dp

/** coui_dialog_guide_indicator_margin_bottom. */
private val IndicatorMarginBottom = 20.dp

/** coui_dialog_guide_button_layout_padding_horizontal. */
private val ButtonBarPaddingHorizontal = 16.dp

/** coui_dialog_guide_button_layout_space. */
private val ButtonSpacing = 8.dp

/** coui_dialog_guide_button_layout_margin_bottom. */
private val ButtonBarMarginBottom = 8.dp

/** coui_alert_dialog_customer_layout_padding_bottom, the custom panel's bottom padding. */
private val CustomPanelPaddingBottom = 8.dp

private const val WIDTH_CHANGE_START_PROGRESS = 0.2f
private const val SECONDARY_BUTTON_ALPHA_END_PROGRESS = 0.6f
private const val TEXT_FADE_START_PROGRESS = 0.5f
private const val TEXT_FADE_END_PROGRESS = 0.85f
private const val TEXT_FADE_RANGE = 0.35f

/** `PAGE_SCROLL_INTEPOLATOR`, COUIEaseInterpolator (0.33, 0, 0.67, 1). */
private val PageScrollEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** `mWidthSpringAnimation`: response 0.15, bounce 0. */
private val MergeSpring = TouchMotion.spring(response = 0.15f, bounce = 0f, visibilityThreshold = 0.001f)

/**
 * `COUISpringInterpolator(0.6, 0)`: a critically damped spring over the normalised time, with an
 * angular frequency of `2π / 0.6`, scaled to end at exactly 1.
 */
private val GuideSpringEasing = Easing { fraction ->
    val omega = 2.0 * PI / 0.6
    fun raw(t: Double): Double = 1.0 - (1.0 + omega * t) * exp(-omega * t)
    (raw(fraction.toDouble()) / raw(1.0)).toFloat()
}

/** `createGuideDialogConfig`: the program scroll runs 650ms on [GuideSpringEasing]. */
private val ProgramScrollSpec = tween<Float>(durationMillis = 650, easing = GuideSpringEasing)
