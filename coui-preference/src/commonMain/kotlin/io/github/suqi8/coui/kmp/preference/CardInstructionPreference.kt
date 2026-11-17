// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.PageIndicator
import io.github.suqi8.coui.kmp.basic.PageIndicatorDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.min

/**
 * One illustration of a [CardInstructionPage], mirroring an `AnimViewGroup`
 * (coui_component_card_instruction_anim.xml).
 *
 * @param content The illustration itself (COUI EffectiveAnimationView: an image or a looping
 *   animation), centered horizontally in its column.
 * @param title The optional caption below the illustration (COUI animTitles).
 */
@Stable
data class CardInstructionIllustration(
    val content: @Composable () -> Unit,
    val title: String? = null,
)

/**
 * One page of a [CardInstructionPreference], mirroring a COUI `ImageDisplayInfo` /
 * `AnimDisplayInfo` bound by `CardInstructionDescriptionAdapter`.
 *
 * @param illustrations The illustrations shown side by side at the top of the page.
 * @param title The page title. Hidden when null.
 * @param summary The page description. Hidden when null.
 */
@Stable
data class CardInstructionPage(
    val illustrations: List<CardInstructionIllustration> = emptyList(),
    val title: String? = null,
    val summary: String? = null,
)

/**
 * A page-top description card with an illustration pager, mirroring COUICardInstructionPreference
 * in its default CARD_INSTRUCTION_TYPE_DESCRIPTION form.
 *
 * COUI inflates coui_component_card_instruction_preference.xml: a rounded card
 * (coui_component_card_instruction_bg: couiColorCardBackground, 16dp corners) padded 16dp / 24dp,
 * holding a `ViewPager2` of [pages] and, 24dp below it, a `COUIPageIndicator` that is only visible
 * with more than one page. Each page (coui_component_card_instruction_description_page.xml) lays
 * its illustrations out in equal-width columns 24dp apart
 * (coui_component_card_instruction_divider_width), each with an optional 14sp caption 24dp below,
 * then 24dp lower a vertically scrolling, edge-faded text block: the centered 16sp medium title
 * (at most 2 lines) and the summary (12sp, start-aligned inside a centered block). With a title the
 * block is capped at 102dp and the summary sits 12dp below the title; without one it is capped at
 * 64dp and the summary sits 24dp lower (CardInstructionDescriptionAdapter.DescriptionHolder.bind).
 *
 * @param pages The pages of the card. Build the list inside a `remember` block to keep it stable.
 * @param modifier The modifier to be applied to the [CardInstructionPreference].
 * @param pagerState The [PagerState] of the illustration pager.
 * @param cornerRadius The corner radius of the card.
 * @param colors The [CardInstructionPreferenceColors] of the card.
 * @param insideMargin The padding inside the card.
 */
@Composable
fun CardInstructionPreference(
    pages: List<CardInstructionPage>,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { pages.size },
    cornerRadius: Dp = CardInstructionPreferenceDefaults.CornerRadius,
    colors: CardInstructionPreferenceColors = CardInstructionPreferenceDefaults.cardInstructionPreferenceColors(),
    insideMargin: PaddingValues = CardInstructionPreferenceDefaults.InsideMargin,
) {
    if (pages.isEmpty()) return
    Column(
        modifier = modifier
            .fillMaxWidth()
            // coui_component_card_instruction_bg is a plain <shape> with circular corners.
            .background(colors.containerColor, RoundedCornerShape(cornerRadius))
            .padding(insideMargin),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            // ViewPager2.setOffscreenPageLimit(itemCount): every page stays composed.
            beyondViewportPageCount = (pages.size - 1).coerceAtLeast(0),
            verticalAlignment = Alignment.Top,
        ) { index ->
            CardInstructionPageContent(page = pages[index], colors = colors)
        }
        if (pages.size > 1) {
            // COUICardInstructionPreference drives a stock COUIPageIndicator from the ViewPager2 callback.
            PageIndicator(
                pagerState = pagerState,
                modifier = Modifier.padding(top = CardInstructionPreferenceDefaults.IndicatorTopMargin),
                pageCount = pages.size,
                colors = PageIndicatorDefaults.colors(
                    dotColor = colors.dotColor,
                    traceColor = colors.selectedDotColor,
                ),
            )
        }
    }
}

@Composable
private fun CardInstructionPageContent(
    page: CardInstructionPage,
    colors: CardInstructionPreferenceColors,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (page.illustrations.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    CardInstructionPreferenceDefaults.IllustrationSpacing,
                    Alignment.CenterHorizontally,
                ),
            ) {
                page.illustrations.forEach { illustration ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        illustration.content()
                        illustration.title?.let { title ->
                            Text(
                                text = title,
                                modifier = Modifier.padding(top = CardInstructionPreferenceDefaults.IllustrationTitleTopMargin),
                                style = COUITheme.textStyles.bodyM,
                                color = colors.summaryColor,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
        val hasTitle = page.title != null
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(top = CardInstructionPreferenceDefaults.ContentTopMargin)
                .fillMaxWidth()
                .heightIn(
                    max = if (hasTitle) {
                        CardInstructionPreferenceDefaults.ContentMaxHeightWithTitle
                    } else {
                        CardInstructionPreferenceDefaults.ContentMaxHeightWithoutTitle
                    },
                )
                .verticalFadingEdge(
                    scrollState = scrollState,
                    length = CardInstructionPreferenceDefaults.ContentFadingEdgeLength,
                )
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            page.title?.let { title ->
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    style = COUITheme.textStyles.headlineXS,
                    color = colors.titleColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            page.summary?.let { summary ->
                Text(
                    text = summary,
                    modifier = Modifier
                        .padding(
                            top = if (hasTitle) {
                                CardInstructionPreferenceDefaults.SummaryTopMarginWithTitle
                            } else {
                                CardInstructionPreferenceDefaults.SummaryTopMarginWithoutTitle
                            },
                        )
                        .wrapContentWidth(),
                    style = COUITheme.textStyles.bodyXS,
                    color = colors.summaryColor,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}

/**
 * Fades the top / bottom [length] of the content while [scrollState] can scroll that way, like
 * `android:requiresFadingEdge="vertical"` on the COUI COUIMutableSizeScrollView. The scroll state is
 * read in the draw phase, so scrolling only redraws, and the gradients are cached per size.
 */
private fun Modifier.verticalFadingEdge(
    scrollState: ScrollState,
    length: Dp,
): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithCache {
        val edge = min(length.toPx(), size.height / 2f)
        val edgeSize = Size(size.width, edge)
        val bottomEdgeOffset = Offset(0f, size.height - edge)
        val topFade = Brush.verticalGradient(0f to Color.Transparent, 1f to Color.Black, startY = 0f, endY = edge)
        val bottomFade = Brush.verticalGradient(
            0f to Color.Black,
            1f to Color.Transparent,
            startY = size.height - edge,
            endY = size.height,
        )
        onDrawWithContent {
            drawContent()
            if (edge <= 0f) return@onDrawWithContent
            if (scrollState.canScrollBackward) {
                drawRect(brush = topFade, size = edgeSize, blendMode = BlendMode.DstIn)
            }
            if (scrollState.canScrollForward) {
                drawRect(brush = bottomFade, topLeft = bottomEdgeOffset, size = edgeSize, blendMode = BlendMode.DstIn)
            }
        }
    }

object CardInstructionPreferenceDefaults {

    /** The corner radius of the card (COUI coui_component_card_instruction_bg: 16dp). */
    val CornerRadius = 16.dp

    /** The padding inside the card (COUI coui_component_card_instruction_preference.xml: 16dp / 24dp). */
    val InsideMargin = PaddingValues(horizontal = 16.dp, vertical = 24.dp)

    /** The gap between two illustrations (COUI coui_component_card_instruction_divider_width). */
    val IllustrationSpacing = 24.dp

    /** The gap between an illustration and its caption (COUI coui_component_card_instruction_anim.xml). */
    val IllustrationTitleTopMargin = 24.dp

    /** The gap between the illustrations and the text block (COUI content_container marginTop). */
    val ContentTopMargin = 24.dp

    /** The maximum height of the text block with a title (COUI coui_component_card_instruction_content_height_complete). */
    val ContentMaxHeightWithTitle = 102.dp

    /** The maximum height of the text block without a title (COUI coui_component_card_instruction_content_height_part). */
    val ContentMaxHeightWithoutTitle = 64.dp

    /** The fading edge of the text block (COUI coui_component_card_instruction_content_fading_edge_length). */
    val ContentFadingEdgeLength = 40.dp

    /** The gap between the title and the summary (COUI coui_component_card_instruction_summary_margin_top_small). */
    val SummaryTopMarginWithTitle = 12.dp

    /** The gap above the summary without a title (COUI coui_component_card_instruction_summary_margin_top_large). */
    val SummaryTopMarginWithoutTitle = 24.dp

    /** The gap between the pager and the page indicator (COUI indicator marginTop). */
    val IndicatorTopMargin = 24.dp

    /**
     * The default [CardInstructionPreferenceColors].
     *
     * @param containerColor The card fill (COUI couiColorCardBackground).
     * @param titleColor The page title color (COUI couiColorPrimaryNeutral).
     * @param summaryColor The summary and caption color (COUI couiColorSecondNeutral).
     * @param dotColor The indicator dot color (COUI dotColor = couiColorHintNeutral).
     * @param selectedDotColor The indicator trace color (COUI traceDotColor = coui_color_label_primary).
     */
    @Composable
    fun cardInstructionPreferenceColors(
        containerColor: Color = COUITheme.colorScheme.cardBackground,
        titleColor: Color = COUITheme.colorScheme.labelPrimary,
        summaryColor: Color = COUITheme.colorScheme.labelSecondary,
        dotColor: Color = COUITheme.colorScheme.labelTertiary,
        selectedDotColor: Color = COUITheme.colorScheme.labelPrimary,
    ): CardInstructionPreferenceColors = remember(containerColor, titleColor, summaryColor, dotColor, selectedDotColor) {
        CardInstructionPreferenceColors(
            containerColor = containerColor,
            titleColor = titleColor,
            summaryColor = summaryColor,
            dotColor = dotColor,
            selectedDotColor = selectedDotColor,
        )
    }
}

/**
 * Colors used by a [CardInstructionPreference].
 *
 * @param containerColor The fill of the card.
 * @param titleColor The color of the page titles.
 * @param summaryColor The color of the summaries and illustration captions.
 * @param dotColor The color of the page indicator dots.
 * @param selectedDotColor The color of the page indicator trace.
 */
@Immutable
data class CardInstructionPreferenceColors(
    val containerColor: Color,
    val titleColor: Color,
    val summaryColor: Color,
    val dotColor: Color,
    val selectedDotColor: Color,
)
