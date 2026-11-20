// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A full page statement component with COUI style, mirroring ColorOS's COUIFullPageStatement
 * (coui_full_page_statement.xml): a centered title, a scrollable statement body with vertical
 * fading edges, and a bottom area holding a filled primary button plus an optional borderless
 * exit text button. Typically used as the first-launch privacy / user statement page.
 *
 * Like the COUI layout, the statement body grows with its text up to [statementMaxHeight] (and
 * shrinks when the available height is short) and the buttons follow right below it; any height
 * the [modifier] adds beyond that stays empty at the bottom. A title or body that fits is not
 * draggable, so it does not rubber-band (`disableTitleScrollBeforeMaxHeight`, and COUIScrollView's
 * overscroll clamp under `overScrollMode="ifContentScrolls"`). On small landscape windows (smallest
 * width < 480dp) the exit and confirm actions sit side by side as two buttons
 * (button_layout_land).
 *
 * @param title The title of the [FullPageStatement], centered at the top
 *   (COUI couiTextHeadlineXS: 16sp, medium). It scrolls past
 *   [FullPageStatementDefaults.titleMaxHeight].
 * @param content The scrollable statement text (COUI couiTextBodyS: 13sp, secondary color). Use
 *   [LinkAnnotation]s styled with [FullPageStatementDefaults.linkStyles] for agreement links
 *   (COUIStatementClickableSpan).
 * @param primaryButtonText The label of the filled primary button (COUI btn_confirm).
 * @param onPrimaryButtonClick The callback when the primary button is clicked.
 * @param modifier The modifier to be applied to the [FullPageStatement].
 * @param secondaryButtonText The label of the borderless exit text button below the primary
 *   button (COUI txt_exit). When null, the exit button is not shown.
 * @param onSecondaryButtonClick The callback when the exit text button is clicked.
 * @param primaryButtonWidth The width of the primary button. When null, the COUI adaptive
 *   width is used (174dp, 220dp from 300dp wide windows, 280dp from 600dp wide windows —
 *   coui_full_page_statement_button_width buckets).
 * @param statementMaxHeight The maximum height of the statement body (COUI scrollViewMaxHeight
 *   coui_full_page_statement_scroll_max_height, `setStatementMaxHeight`).
 * @param colors The [FullPageStatementColors] of the [FullPageStatement].
 * @param primaryButtonColors The [ButtonColors] of the filled primary button. COUI uses the
 *   colorful default style (theme primary fill).
 * @param contentPadding The padding around the statement text inside the scrollable area
 *   (COUI coui_full_page_statement_padding_horizontal/top/bottom).
 * @param scrollState The [ScrollState] of the statement text area.
 */
@Composable
fun FullPageStatement(
    title: String,
    content: AnnotatedString,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryButtonText: String? = null,
    onSecondaryButtonClick: (() -> Unit)? = null,
    primaryButtonWidth: Dp? = null,
    statementMaxHeight: Dp = FullPageStatementDefaults.StatementMaxHeight,
    colors: FullPageStatementColors = FullPageStatementDefaults.fullPageStatementColors(),
    primaryButtonColors: ButtonColors = ButtonDefaults.buttonColorsPrimary(),
    contentPadding: PaddingValues = FullPageStatementDefaults.ContentPadding,
    scrollState: ScrollState = rememberScrollState(),
) {
    val windowSize = LocalWindowInfo.current.containerDpSize
    // COUIFullPageStatement.isInSmallLand: smallestScreenWidthDp < 480 in landscape.
    val smallLand = min(windowSize.width, windowSize.height) < FullPageStatementDefaults.SmallLandSmallestWidth &&
        windowSize.width > windowSize.height
    // values-h200dp: windows 200-299dp tall tighten the title and button margins.
    val shortWindow = windowSize.height < FullPageStatementDefaults.ShortWindowHeight
    val titleScrollState = rememberScrollState()
    Column(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = if (shortWindow) FullPageStatementDefaults.TitleMarginShort else FullPageStatementDefaults.TitleMarginTop,
                    bottom = if (shortWindow) FullPageStatementDefaults.TitleMarginShort else FullPageStatementDefaults.TitleMarginBottom,
                )
                .padding(horizontal = FullPageStatementDefaults.TitleMarginHorizontal)
                .heightIn(max = FullPageStatementDefaults.titleMaxHeight(windowSize.height))
                .verticalFadingEdges(titleScrollState, FullPageStatementDefaults.ScrollFadeLength)
                // disableTitleScrollBeforeMaxHeight: a title below its max height swallows touches,
                // so it neither scrolls nor rubber-bands.
                .verticalScroll(titleScrollState, enabled = titleScrollState.maxValue > 0),
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                fontSize = COUITheme.textStyles.headlineXS.fontSize,
                fontWeight = FontWeight.Medium,
                color = colors.titleColor,
                textAlign = TextAlign.Center,
            )
        }
        // Statement body with vertical fading edges, like the COUIMaxHeightScrollView host.
        Box(
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth()
                .heightIn(max = statementMaxHeight)
                .verticalFadingEdges(scrollState, FullPageStatementDefaults.ScrollFadeLength)
                // COUIScrollView.onOverScrolled clamps the overscroll of content that fits
                // (overScrollMode ifContentScrolls), so short statements do not rubber-band.
                .verticalScroll(scrollState, enabled = scrollState.maxValue > 0),
        ) {
            Text(
                text = content,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                style = COUITheme.textStyles.bodyS,
                color = colors.contentColor,
            )
        }
        if (smallLand) {
            // coui_full_page_statement.xml button_layout_land: exit + confirm side by side.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = FullPageStatementDefaults.LandButtonMarginTop,
                        bottom = FullPageStatementDefaults.LandButtonMarginBottom,
                    )
                    .padding(horizontal = FullPageStatementDefaults.LandButtonMarginHorizontal),
                horizontalArrangement = Arrangement.spacedBy(FullPageStatementDefaults.LandButtonSpacing),
            ) {
                if (secondaryButtonText != null) {
                    StatementButton(
                        text = secondaryButtonText,
                        onClick = { onSecondaryButtonClick?.invoke() },
                        colors = ButtonDefaults.buttonColors(),
                        modifier = Modifier
                            .weight(1f)
                            .height(FullPageStatementDefaults.LandButtonHeight),
                    )
                }
                StatementButton(
                    text = primaryButtonText,
                    onClick = onPrimaryButtonClick,
                    colors = primaryButtonColors,
                    modifier = Modifier
                        .weight(1f)
                        .height(FullPageStatementDefaults.LandButtonHeight),
                )
            }
        } else {
            // Bottom button area (coui_full_page_statement.xml button_layout_normal).
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
            ) {
                val resolvedButtonWidth = primaryButtonWidth
                    ?: FullPageStatementDefaults.primaryButtonWidth(maxWidth)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    StatementButton(
                        text = primaryButtonText,
                        onClick = onPrimaryButtonClick,
                        colors = primaryButtonColors,
                        modifier = Modifier
                            .padding(
                                top = if (shortWindow) {
                                    FullPageStatementDefaults.PrimaryButtonMarginTopShort
                                } else {
                                    FullPageStatementDefaults.PrimaryButtonMarginTop
                                },
                                bottom = if (shortWindow) {
                                    FullPageStatementDefaults.PrimaryButtonMarginBottomShort
                                } else {
                                    FullPageStatementDefaults.PrimaryButtonMarginBottom
                                },
                            )
                            .width(resolvedButtonWidth),
                    )
                    if (secondaryButtonText != null) {
                        // COUI txt_exit is a bare accent-colored TextView with no fill and no press overlay.
                        val interactionSource = remember { MutableInteractionSource() }
                        Text(
                            text = secondaryButtonText,
                            modifier = Modifier
                                .padding(bottom = FullPageStatementDefaults.SecondaryButtonMarginBottom)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    role = Role.Button,
                                    onClick = { onSecondaryButtonClick?.invoke() },
                                ),
                            fontSize = COUITheme.textStyles.headlineXS.fontSize,
                            fontWeight = FontWeight.Medium,
                            color = colors.secondaryButtonColor,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

/** A statement button whose label wraps to two lines at most (COUIFullPageStatement `setMaxLines(2)`). */
@Composable
private fun StatementButton(
    text: String,
    onClick: () -> Unit,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
    ) {
        Text(
            text = text,
            style = COUITheme.textStyles.buttonL,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Android `requiresFadingEdge="vertical"` for a [verticalScroll] host: the content fades out over
 * [fadeLength] at an edge that has more content beyond it, ramping in over the first [fadeLength]
 * of scroll. Apply it before [verticalScroll] on the same node.
 */
internal fun Modifier.verticalFadingEdges(scrollState: ScrollState, fadeLength: Dp): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val fadePx = fadeLength.toPx().coerceAtMost(size.height / 2f)
        if (fadePx <= 0f || scrollState.maxValue <= 0) return@drawWithContent
        val topLength = fadePx * (scrollState.value / fadePx).coerceIn(0f, 1f)
        if (topLength > 0f) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = 0f,
                    endY = topLength,
                ),
                size = Size(size.width, topLength),
                blendMode = BlendMode.DstIn,
            )
        }
        val remaining = (scrollState.maxValue - scrollState.value).toFloat()
        val bottomLength = fadePx * (remaining / fadePx).coerceIn(0f, 1f)
        if (bottomLength > 0f) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startY = size.height - bottomLength,
                    endY = size.height,
                ),
                topLeft = Offset(0f, size.height - bottomLength),
                size = Size(size.width, bottomLength),
                blendMode = BlendMode.DstIn,
            )
        }
    }

/** Contains default values used by [FullPageStatement]. */
object FullPageStatementDefaults {

    /** Top margin of the title (COUI coui_full_page_statement_text_button_padding). */
    val TitleMarginTop = 12.dp

    /** Bottom margin of the title (COUI coui_full_page_statement_content_margin). */
    val TitleMarginBottom = 12.dp

    /**
     * Horizontal margin of the title
     * (COUI coui_full_page_statement_text_button_padding_horizontal).
     */
    val TitleMarginHorizontal = 24.dp

    /**
     * The default padding around the statement text
     * (COUI coui_full_page_statement_padding_horizontal 24dp / _top 12dp / _bottom 14dp).
     */
    val ContentPadding = PaddingValues(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 14.dp)

    /**
     * Length of the vertical fading edges (COUI coui_full_page_statement_scroll_fade_length; the
     * title scroller uses center_dialog_scroll_fading_edge_length, also 32dp).
     */
    val ScrollFadeLength = 32.dp

    /**
     * Default maximum height of the statement body
     * (COUI coui_full_page_statement_scroll_max_height).
     */
    val StatementMaxHeight = 248.dp

    /** Title margins in windows shorter than [ShortWindowHeight] (values-h200dp). */
    val TitleMarginShort = 3.dp

    /** Window height below which the values-h200dp margins apply. */
    val ShortWindowHeight = 300.dp

    /**
     * The maximum height of the title scroller for a window of the given height
     * (COUIAlertDialogTitleScrollViewStyle coui_alert_dialog_builder_title_scroll_max_height:
     * 80dp, 120dp from 300dp tall windows, 160dp from 560dp tall windows).
     */
    fun titleMaxHeight(windowHeight: Dp): Dp = when {
        windowHeight >= 560.dp -> 160.dp
        windowHeight >= 300.dp -> 120.dp
        else -> 80.dp
    }

    /** Top margin of the primary button (COUI coui_full_page_statement_button_margin_top). */
    val PrimaryButtonMarginTop = 20.dp

    /** Bottom margin of the primary button (COUI coui_full_page_statement_button_margin). */
    val PrimaryButtonMarginBottom = 16.dp

    /** Top margin of the primary button in windows shorter than [ShortWindowHeight] (values-h200dp). */
    val PrimaryButtonMarginTopShort = 10.dp

    /** Bottom margin of the primary button in windows shorter than [ShortWindowHeight] (values-h200dp). */
    val PrimaryButtonMarginBottomShort = 8.dp

    /** Smallest window width below which a landscape window uses the side-by-side buttons. */
    val SmallLandSmallestWidth = 480.dp

    /** Height of the side-by-side buttons (coui_full_page_statement_button_button_width_landlong). */
    val LandButtonHeight = 44.dp

    /** Outer margin of the side-by-side buttons (coui_full_page_statement_button_margin_horizontal_landlong). */
    val LandButtonMarginHorizontal = 37.dp

    /** Gap between the side-by-side buttons: 2 x coui_full_page_statement_button_margin_center_landlong. */
    val LandButtonSpacing = 8.dp

    /** Top padding of the side-by-side button row (coui_full_page_statement_button_margin_top_landlong). */
    val LandButtonMarginTop = 16.dp

    /** Bottom padding of the side-by-side button row (coui_full_page_statement_button_margin_bottom_landlong). */
    val LandButtonMarginBottom = 24.dp

    /**
     * Bottom margin of the exit text button
     * (COUI coui_full_page_statement_exit_button_margin_bottom).
     */
    val SecondaryButtonMarginBottom = 24.dp

    /** Primary button width in windows narrower than [MediumWidthThreshold] (base bucket). */
    val PrimaryButtonWidthCompact = 174.dp

    /** Primary button width in windows at least [MediumWidthThreshold] wide (values-w300dp). */
    val PrimaryButtonWidthMedium = 220.dp

    /** Primary button width in windows at least [ExpandedWidthThreshold] wide (values-w600dp). */
    val PrimaryButtonWidthExpanded = 280.dp

    /** Available width from which [PrimaryButtonWidthMedium] applies. */
    val MediumWidthThreshold = 300.dp

    /** Available width from which [PrimaryButtonWidthExpanded] applies. */
    val ExpandedWidthThreshold = 600.dp

    /**
     * The COUI adaptive primary button width for the given available width, following the
     * coui_full_page_statement_button_width resource buckets (base / w300dp / w600dp).
     */
    fun primaryButtonWidth(availableWidth: Dp): Dp = when {
        availableWidth >= ExpandedWidthThreshold -> PrimaryButtonWidthExpanded
        availableWidth >= MediumWidthThreshold -> PrimaryButtonWidthMedium
        else -> PrimaryButtonWidthCompact
    }

    /**
     * Link styles for agreement links inside the statement text, matching
     * COUIStatementClickableSpan: `couiColorLink` at rest and alpha 77/255 while pressed, no
     * underline. Pass them to a [LinkAnnotation.Clickable] or [LinkAnnotation.Url].
     */
    @Composable
    fun linkStyles(
        color: Color = COUITheme.colorScheme.link,
    ): TextLinkStyles = remember(color) {
        TextLinkStyles(
            style = SpanStyle(color = color),
            pressedStyle = SpanStyle(color = color.copy(alpha = LinkPressedAlpha)),
        )
    }

    /** COUIStatementClickableSpan.ALPHA_PRESSED (77 / 255). */
    private val LinkPressedAlpha = 77f / 255f

    /**
     * The default [FullPageStatementColors]: title on couiColorPrimaryNeutral, statement body
     * on couiColorSecondNeutral and exit text button on couiColorPrimaryTextOnPopup.
     */
    @Composable
    fun fullPageStatementColors(
        titleColor: Color = COUITheme.colorScheme.labelPrimary,
        contentColor: Color = COUITheme.colorScheme.labelSecondary,
        secondaryButtonColor: Color = COUITheme.colorScheme.primary,
    ): FullPageStatementColors = remember(titleColor, contentColor, secondaryButtonColor) {
        FullPageStatementColors(
            titleColor = titleColor,
            contentColor = contentColor,
            secondaryButtonColor = secondaryButtonColor,
        )
    }
}

/**
 * Colors used by a [FullPageStatement].
 *
 * @param titleColor The color of the title.
 * @param contentColor The color of the statement text.
 * @param secondaryButtonColor The color of the exit text button.
 */
@Immutable
data class FullPageStatementColors(
    val titleColor: Color,
    val contentColor: Color,
    val secondaryButtonColor: Color,
)
