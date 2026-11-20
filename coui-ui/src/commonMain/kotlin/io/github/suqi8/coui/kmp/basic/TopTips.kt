// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.max
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.roundToInt

/**
 * A [TopTips] component with COUI style.
 *
 * A tip banner shown at the top of a page, mirroring COUI 17 `COUIDefaultTopTips` and its content
 * view `COUIDefaultTopTipsView` (coui_default_toptips.xml): a rounded card (couiColorContainer4
 * fill) holding an optional start icon, an optional 16sp medium [title] of at most two lines, a
 * 13sp message, an optional solid primary action button (`Widget.COUI.Button.Small.ButtonNew`)
 * and an optional close button. The action and close buttons can be shown together.
 *
 * The layout follows `changeDefaultLayoutWithoutTitle` / `changeDefaultLayoutWithTitle`:
 * - Without a title the start icon is [TopTipsDefaults.IconSize] and the buttons sit after the
 *   message, centred on it. Only when a start icon is shown and the message needs more than two
 *   lines next to the buttons (`isNeedDefaultMultiTextWithoutTitle`) does the action drop below
 *   the message, aligned with its start edge, while the icon and the close button centre on the
 *   first two lines.
 * - With a title the start icon is [TopTipsDefaults.TitleIconSize] and the buttons are centred on
 *   the title + message block. When a start icon is shown and either text no longer fits on one
 *   line next to the buttons (`isNeedDefaultMultiTextWithTitle`), the action drops below the
 *   message and the close button aligns with the title.
 *
 * Toggling [visible] plays `COUIDefaultTopTips.animateExpand` / `animateCollapse`: the card is
 * revealed from its top edge by a 250ms height clip (COUIEaseInterpolator) while the content fades
 * with the same progress. Like the official view, the banner keeps its full height in the layout
 * during the animation and leaves the composition once collapsed.
 *
 * @param text The message of the [TopTips].
 * @param modifier The modifier to be applied to the [TopTips].
 * @param visible Whether the [TopTips] is visible. Changing this value animates the banner in/out.
 * @param title The optional title shown above the message (`setTitleText`).
 * @param startIcon The optional leading icon, laid out in a [TopTipsDefaults.IconSize] box, or in
 *   a [TopTipsDefaults.TitleIconSize] box when a [title] is shown. The box imposes its size on the
 *   icon, as the official `ImageView` scales its drawable to the box.
 * @param actionText The optional label of the action button (`setPositiveButton`).
 * @param onAction The callback when the action button is clicked.
 * @param onClose The callback when the close button is clicked. The close button is shown when
 *   this is not null (`setCloseDrawable`).
 * @param marquee Whether the message is shown as a single scrolling line (`COUIMarqueeTopTips`
 *   `startRoll` / `stopRoll`): it scrolls at [TopTipsDefaults.MarqueeVelocity] with both edges
 *   faded when it does not fit, and never moves the action below it.
 * @param cornerRadius The corner radius of the [TopTips]. [Dp.Unspecified] (the default) follows
 *   COUI 17 `COUIDefaultTopTips.updateRadius`: a banner without a title whose message fits on one
 *   line is a capsule ([TopTipsDefaults.SingleLineCornerRadius], soft capsule weight
 *   [TopTipsDefaults.SingleLineWeight]), anything else uses [TopTipsDefaults.CornerRadius] at
 *   [TopTipsDefaults.Weight], and switching between the two animates radius and weight over 300ms.
 *   An explicit radius is drawn at [TopTipsDefaults.Weight].
 * @param colors The [TopTipsColors] of the [TopTips].
 */
@Composable
fun TopTips(
    text: String,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    title: String? = null,
    startIcon: (@Composable () -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    marquee: Boolean = false,
    cornerRadius: Dp = Dp.Unspecified,
    colors: TopTipsColors = TopTipsDefaults.topTipsColors(),
) {
    // COUIDefaultTopTips.createClipAnimator: the outline height runs 0..height over 250ms with
    // COUIEaseInterpolator and the children alpha follows the animated fraction, in both directions.
    val reveal = remember { Animatable(if (visible) 1f else 0f) }
    LaunchedEffect(visible) {
        val target = if (visible) 1f else 0f
        if (reveal.value != target) {
            reveal.animateTo(target, tween(ExpandCollapseDurationMillis, easing = COUIEaseEasing))
        }
    }
    val collapsed by remember { derivedStateOf { reveal.value == 0f } }
    if (!visible && collapsed) return

    val titleText = title?.takeIf { it.isNotEmpty() }
    val action = actionText?.takeIf { it.isNotEmpty() }
    val titleStyle = TopTipsDefaults.titleTextStyle()
    val textStyle = TopTipsDefaults.textStyle()
    val actionStyle = TopTipsDefaults.actionTextStyle()

    BoxWithConstraints(
        modifier = modifier,
        propagateMinConstraints = true,
    ) {
        val density = LocalDensity.current
        val textMeasurer = rememberTextMeasurer()
        // COUIButton measured UNSPECIFIED: label + Widget.COUI.Button.Small padding, at least its min width.
        val actionWidth = action?.let {
            val labelWidth = with(density) {
                textMeasurer.measure(AnnotatedString(it), style = actionStyle, maxLines = 1).size.width.toDp()
            }
            max(labelWidth + ActionHorizontalPadding * 2, ActionMinWidth)
        }
        val arrangement = if (constraints.hasBoundedWidth) {
            arrangeTopTips(
                measurer = textMeasurer,
                density = density,
                maxWidth = maxWidth,
                text = text,
                textStyle = textStyle,
                title = titleText,
                titleStyle = titleStyle,
                hasIcon = startIcon != null,
                actionWidth = actionWidth,
                hasClose = onClose != null,
                marquee = marquee,
            )
        } else {
            TopTipsArrangement(multiLine = false, singleLineText = titleText == null)
        }
        val corner = rememberTopTipsCorner(cornerRadius, titleText == null && arrangement.singleLineText)
        val parts = TopTipsParts(
            text = text,
            title = titleText,
            startIcon = startIcon,
            action = action,
            onAction = onAction,
            onClose = onClose,
            marquee = marquee,
            colors = colors,
            titleStyle = titleStyle,
            textStyle = textStyle,
            actionStyle = actionStyle,
        )

        Box {
            // COUIDefaultTopTips clips its outline (and so the card fill) to the animated height.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .revealHeight { reveal.value }
                    .squircleSurface(
                        color = colors.containerColor,
                        cornerRadius = corner.radius,
                        weight = corner.weight,
                    ),
            )
            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = reveal.value }
                    .drawWithContent {
                        val progress = reveal.value
                        if (progress >= 1f) {
                            drawContent()
                        } else {
                            clipRect(bottom = size.height * progress) { this@drawWithContent.drawContent() }
                        }
                    },
            ) {
                when {
                    !arrangement.multiLine -> TopTipsInlineLayout(parts, arrangement)
                    titleText == null -> TopTipsStackedLayout(parts, arrangement)
                    else -> TopTipsTitledStackedLayout(parts, arrangement)
                }
            }
        }
    }
}

/** Everything a [TopTips] layout variant draws. */
private class TopTipsParts(
    val text: String,
    val title: String?,
    val startIcon: (@Composable () -> Unit)?,
    val action: String?,
    val onAction: (() -> Unit)?,
    val onClose: (() -> Unit)?,
    val marquee: Boolean,
    val colors: TopTipsColors,
    val titleStyle: TextStyle,
    val textStyle: TextStyle,
    val actionStyle: TextStyle,
)

/**
 * The layout decision of `COUIDefaultTopTipsView`.
 *
 * @param multiLine Whether the action sits below the message (the official "multi text" layout).
 * @param singleLineText Whether the message fits on one line next to the buttons (`mIsSingleLineText`).
 * @param iconTop The top offset of the start icon in the multi-line layouts.
 * @param closeTop The top offset of the close button in the multi-line layouts.
 */
private class TopTipsArrangement(
    val multiLine: Boolean,
    val singleLineText: Boolean,
    val iconTop: Dp = 0.dp,
    val closeTop: Dp = 0.dp,
)

/**
 * Replicates `COUIDefaultTopTipsView.isNeedDefaultMultiTextWithoutTitle` /
 * `isNeedDefaultMultiTextWithTitle` and the icon placement of the multi-line layouts
 * (`getIconMarginAlignedToFirstTwoLines`, `getIconTopForSingleTitleMultiContent`).
 */
private fun arrangeTopTips(
    measurer: TextMeasurer,
    density: Density,
    maxWidth: Dp,
    text: String,
    textStyle: TextStyle,
    title: String?,
    titleStyle: TextStyle,
    hasIcon: Boolean,
    actionWidth: Dp?,
    hasClose: Boolean,
    marquee: Boolean,
): TopTipsArrangement {
    with(density) {
        fun measure(value: String, style: TextStyle, width: Dp? = null, maxLines: Int = Int.MAX_VALUE): TextLayoutResult = measurer.measure(
            text = AnnotatedString(value),
            style = style,
            maxLines = maxLines,
            constraints = if (width == null) Constraints() else Constraints(maxWidth = width.roundToPx().coerceAtLeast(0)),
        )
        if (title == null) {
            // A marquee message is single-line (getMaxLines() == 1) and never moves the action.
            if (marquee) return TopTipsArrangement(multiLine = false, singleLineText = true)
            val leading = if (hasIcon) TopTipsDefaults.HorizontalPadding + TopTipsDefaults.IconSize + TopTipsDefaults.IconSpacing else TopTipsDefaults.HorizontalPadding
            val trailing = when {
                actionWidth != null ->
                    TextToButtonGap + actionWidth +
                        if (hasClose) ActionCloseGap + TopTipsDefaults.IconSize + CloseEndMargin else TopTipsDefaults.HorizontalPadding

                hasClose -> TextToButtonGap + TopTipsDefaults.IconSize + TopTipsDefaults.HorizontalPadding

                else -> TopTipsDefaults.HorizontalPadding
            }
            val available = maxWidth - leading - trailing
            if (available <= 0.dp || text.isEmpty()) return TopTipsArrangement(multiLine = false, singleLineText = false)
            val lineCount = measure(text, textStyle, available).lineCount
            // Without a start icon the message always stays beside the buttons.
            if (!hasIcon || lineCount <= 2) return TopTipsArrangement(multiLine = false, singleLineText = lineCount == 1)
            val contentEnd = if (hasClose) TextToButtonGap + TopTipsDefaults.IconSize + TopTipsDefaults.HorizontalPadding else TopTipsDefaults.HorizontalPadding
            val content = measure(text, textStyle, maxWidth - leading - contentEnd)
            val alignToFirstTwoLines = if (content.lineCount >= 2) {
                ((content.getLineTop(0) + content.getLineBottom(1)) / 2f).toDp() - TopTipsDefaults.IconSize / 2
            } else {
                0.dp
            }
            val top = TopTipsDefaults.VerticalPadding + alignToFirstTwoLines.coerceAtLeast(0.dp)
            return TopTipsArrangement(multiLine = true, singleLineText = false, iconTop = top, closeTop = top)
        }

        val leading = if (hasIcon) TopTipsDefaults.HorizontalPadding + TopTipsDefaults.TitleIconSize + TopTipsDefaults.TitleIconSpacing else TopTipsDefaults.HorizontalPadding
        val trailing = when {
            actionWidth != null && hasClose -> TextToButtonGap + actionWidth + ActionCloseGap + TopTipsDefaults.IconSize + CloseEndMargin
            actionWidth != null -> TextToButtonGap + actionWidth + TopTipsDefaults.HorizontalPadding
            hasClose -> TextToButtonGap + TopTipsDefaults.IconSize + CloseEndMargin
            else -> TopTipsDefaults.HorizontalPadding
        }
        val available = maxWidth - leading - trailing
        val titleWidth = measure(title, titleStyle, maxLines = 1).size.width.toDp()
        val textWidth = measure(text, textStyle, maxLines = 1).size.width.toDp()
        val titleNeedsWrap = titleWidth > available
        if (!hasIcon || !(titleNeedsWrap || textWidth > available)) {
            return TopTipsArrangement(multiLine = false, singleLineText = false)
        }
        val titleEnd = if (hasClose) TextToButtonGap + TopTipsDefaults.IconSize + TopTipsDefaults.HorizontalPadding else TopTipsDefaults.HorizontalPadding
        val titleMaxWidth = maxWidth - leading - titleEnd
        val iconTop = if (titleNeedsWrap && titleWidth > titleMaxWidth) {
            // isTitleMultiLineInMultiLayout: the icon centres on the wrapped title.
            val titleHeight = max(TopTipsDefaults.TitleMinHeight, measure(title, titleStyle, titleMaxWidth, maxLines = 2).size.height.toDp())
            TopTipsDefaults.VerticalPadding + (titleHeight - TopTipsDefaults.TitleIconSize) / 2
        } else {
            // getIconTopForSingleTitleMultiContent: centred between the title and the first message line.
            val titleHeight = max(TopTipsDefaults.TitleMinHeight, measure(title, titleStyle, maxLines = 1).size.height.toDp())
            val content = measure(
                text,
                textStyle,
                maxWidth - leading - TopTipsDefaults.HorizontalPadding,
                maxLines = if (marquee) 1 else Int.MAX_VALUE,
            )
            val firstLineCenter = ((content.getLineTop(0) + content.getLineBottom(0)) / 2f).toDp()
            val titleCenter = TopTipsDefaults.VerticalPadding + titleHeight / 2
            val textCenter = TopTipsDefaults.VerticalPadding + titleHeight + firstLineCenter
            (titleCenter + textCenter) / 2 - TopTipsDefaults.TitleIconSize / 2
        }
        return TopTipsArrangement(
            multiLine = true,
            singleLineText = false,
            iconTop = iconTop.coerceAtLeast(0.dp),
            closeTop = TopTipsDefaults.VerticalPadding,
        )
    }
}

/**
 * The single-row layout: start icon, the text block and the buttons in one row, everything
 * centred on the text block (the non-multi branches of `changeDefaultLayoutWith(out)Title`).
 */
@Composable
private fun TopTipsInlineLayout(parts: TopTipsParts, arrangement: TopTipsArrangement) {
    val hasTitle = parts.title != null
    val hasTrailing = parts.action != null || parts.onClose != null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = TopTipsDefaults.HorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (parts.startIcon != null) {
            TopTipsIcon(size = if (hasTitle) TopTipsDefaults.TitleIconSize else TopTipsDefaults.IconSize, content = parts.startIcon)
            Spacer(Modifier.width(if (hasTitle) TopTipsDefaults.TitleIconSpacing else TopTipsDefaults.IconSpacing))
        }
        if (hasTitle) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = TopTipsDefaults.VerticalPadding),
            ) {
                TopTipsTitle(parts)
                TopTipsMessage(parts)
            }
        } else {
            TopTipsMessage(
                parts = parts,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = TopTipsDefaults.VerticalPadding),
            )
        }
        Spacer(Modifier.width(if (hasTrailing) TextToButtonGap else TopTipsDefaults.HorizontalPadding))
        if (parts.action != null) {
            TopTipsActionButton(parts)
            val actionEnd = when {
                parts.onClose != null -> ActionCloseGap

                // The single-line message keeps the action 8dp from the edge (mTextToBtnGap).
                !hasTitle && arrangement.singleLineText -> TextToButtonGap

                else -> TopTipsDefaults.HorizontalPadding
            }
            Spacer(Modifier.width(actionEnd))
        }
        if (parts.onClose != null) {
            TopTipsCloseButton(parts.colors, parts.onClose)
            Spacer(Modifier.width(if (hasTitle || parts.action != null) CloseEndMargin else TopTipsDefaults.HorizontalPadding))
        }
    }
}

/**
 * The multi-line layout without a title: the action below the message at its start edge, the
 * start icon and the close button centred on the first two message lines.
 */
@Composable
private fun TopTipsStackedLayout(parts: TopTipsParts, arrangement: TopTipsArrangement) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = TopTipsDefaults.HorizontalPadding),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            if (parts.startIcon != null) {
                TopTipsIcon(
                    size = TopTipsDefaults.IconSize,
                    modifier = Modifier.padding(top = arrangement.iconTop),
                    content = parts.startIcon,
                )
                Spacer(Modifier.width(TopTipsDefaults.IconSpacing))
            }
            TopTipsMessage(
                parts = parts,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        top = TopTipsDefaults.VerticalPadding,
                        bottom = if (parts.action != null) 0.dp else TopTipsDefaults.VerticalPadding,
                    ),
            )
            if (parts.onClose != null) {
                Spacer(Modifier.width(TextToButtonGap))
                TopTipsCloseButton(parts.colors, parts.onClose, Modifier.padding(top = arrangement.closeTop))
            }
        }
        if (parts.action != null) {
            TopTipsActionButton(
                parts = parts,
                modifier = Modifier.padding(
                    start = if (parts.startIcon != null) TopTipsDefaults.IconSize + TopTipsDefaults.IconSpacing else 0.dp,
                    top = ActionTopMargin,
                    bottom = MultiActionBottomMargin,
                ),
            )
        }
    }
}

/**
 * The multi-line layout with a title: the close button beside the title, the message below it at
 * full width, the action below the message at the title's start edge.
 */
@Composable
private fun TopTipsTitledStackedLayout(parts: TopTipsParts, arrangement: TopTipsArrangement) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = TopTipsDefaults.HorizontalPadding),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            if (parts.startIcon != null) {
                TopTipsIcon(
                    size = TopTipsDefaults.TitleIconSize,
                    modifier = Modifier.padding(top = arrangement.iconTop),
                    content = parts.startIcon,
                )
                Spacer(Modifier.width(TopTipsDefaults.TitleIconSpacing))
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top) {
                    TopTipsTitle(
                        parts = parts,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = TopTipsDefaults.VerticalPadding),
                    )
                    if (parts.onClose != null) {
                        Spacer(Modifier.width(TextToButtonGap))
                        TopTipsCloseButton(parts.colors, parts.onClose, Modifier.padding(top = arrangement.closeTop))
                    }
                }
                TopTipsMessage(
                    parts = parts,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (parts.action != null) 0.dp else TopTipsDefaults.VerticalPadding),
                )
            }
        }
        if (parts.action != null) {
            TopTipsActionButton(
                parts = parts,
                modifier = Modifier.padding(
                    start = if (parts.startIcon != null) TopTipsDefaults.TitleIconSize + TopTipsDefaults.TitleIconSpacing else 0.dp,
                    top = ActionTopMargin,
                    bottom = MultiActionBottomMargin,
                ),
            )
        }
    }
}

/** The start icon box; it imposes its size on the icon like the official `ImageView`. */
@Composable
private fun TopTipsIcon(
    size: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
        propagateMinConstraints = true,
    ) {
        content()
    }
}

/** The title: couiTextHeadlineXS 16sp medium, couiColorLabelPrimary, at most two lines. */
@Composable
private fun TopTipsTitle(parts: TopTipsParts, modifier: Modifier = Modifier) {
    Text(
        text = parts.title.orEmpty(),
        color = parts.colors.titleColor,
        style = parts.titleStyle,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.defaultMinSize(minHeight = TopTipsDefaults.TitleMinHeight),
    )
}

/** The message: couiTextBodyS 13sp, couiColorLabelSecondary, vertically centred in its min height. */
@Composable
private fun TopTipsMessage(parts: TopTipsParts, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.defaultMinSize(minHeight = TopTipsDefaults.TextMinHeight),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (parts.marquee) {
            TopTipsMarqueeText(text = parts.text, color = parts.colors.contentColor, style = parts.textStyle)
        } else {
            Text(text = parts.text, color = parts.colors.contentColor, style = parts.textStyle)
        }
    }
}

/**
 * The action: a `Widget.COUI.Button.Small.ButtonNew` primary capsule with the material effect
 * forced off (`useMaterialEffect = forceDisable`, `resetDefaultButtonStyle`).
 */
@Composable
private fun TopTipsActionButton(parts: TopTipsParts, modifier: Modifier = Modifier) {
    val onAction = parts.onAction
    Button(
        onClick = { onAction?.invoke() },
        modifier = modifier,
        minWidth = ActionMinWidth,
        minHeight = ActionMinHeight,
        colors = ButtonDefaults.buttonColorsPrimary(
            color = parts.colors.actionColor,
            contentColor = parts.colors.actionContentColor,
        ),
        insideMargin = ButtonDefaults.InsideMarginSmall,
        material = ButtonMaterial.None,
    ) {
        Text(
            text = parts.action.orEmpty(),
            color = parts.colors.actionContentColor,
            style = parts.actionStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * A single scrolling line after `COUIMarqueeTextView`: when the text overflows it starts
 * [MarqueeStartLocation] in, scrolls continuously at [TopTipsDefaults.MarqueeVelocity] with copies
 * [MarqueeTextInterval] apart and both edges faded over [MarqueeFadingEdgeLength].
 */
@Composable
private fun TopTipsMarqueeText(text: String, color: Color, style: TextStyle) {
    var containerWidth by remember { mutableIntStateOf(0) }
    var textWidth by remember { mutableIntStateOf(0) }
    val overflowing = containerWidth in 1 until textWidth
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = 1,
        softWrap = false,
        onTextLayout = { textWidth = it.size.width },
        modifier = Modifier
            .onSizeChanged { containerWidth = it.width }
            .then(if (overflowing) Modifier.topTipsFadingEdges() else Modifier)
            .basicMarquee(
                iterations = Int.MAX_VALUE,
                repeatDelayMillis = 0,
                initialDelayMillis = 0,
                spacing = MarqueeSpacing(MarqueeTextInterval - MarqueeStartLocation),
                velocity = TopTipsDefaults.MarqueeVelocity,
            )
            .then(if (overflowing) Modifier.padding(start = MarqueeStartLocation) else Modifier),
    )
}

/** Fades both horizontal edges over [MarqueeFadingEdgeLength] (the TextView fading edge at strength 1). */
private fun Modifier.topTipsFadingEdges(): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithCache {
        val fade = MarqueeFadingEdgeLength.toPx().coerceAtMost(size.width / 2f)
        val fadeSize = Size(fade, size.height)
        val startFade = Brush.horizontalGradient(0f to Color.Transparent, 1f to Color.Black, startX = 0f, endX = fade)
        val endFade = Brush.horizontalGradient(0f to Color.Black, 1f to Color.Transparent, startX = size.width - fade, endX = size.width)
        onDrawWithContent {
            drawContent()
            if (fade <= 0f) return@onDrawWithContent
            drawRect(brush = startFade, size = fadeSize, blendMode = BlendMode.DstIn)
            drawRect(brush = endFade, topLeft = Offset(size.width - fade, 0f), size = fadeSize, blendMode = BlendMode.DstIn)
        }
    }

/**
 * Lays the node out at its full size but measures and draws what follows at [progress] of the
 * height from the top, the `COUIDefaultTopTips` outline clip.
 */
private fun Modifier.revealHeight(progress: () -> Float): Modifier = layout { measurable, constraints ->
    val width = constraints.maxWidth
    val height = constraints.maxHeight
    val revealed = (height * progress().coerceIn(0f, 1f)).roundToInt()
    val placeable = measurable.measure(Constraints.fixed(width, revealed))
    layout(width, height) {
        if (revealed > 0) placeable.place(0, 0)
    }
}

/**
 * The close button, redrawn from COUI 17 ic_embedded_top_tips_close: a 24dp icon holding a
 * radius-10 couiColorLabelTertiary circle with a white rounded cross. Like the official close
 * `ImageView` (no background, a plain vector without a pressed state) it shows no press feedback.
 */
@Composable
private fun TopTipsCloseButton(
    colors: TopTipsColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = colors.closeButtonColor
    val iconColor = colors.closeIconColor
    Box(
        modifier = modifier
            .size(TopTipsDefaults.IconSize)
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = "Close" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(TopTipsDefaults.IconSize)) {
            drawCircle(color = backgroundColor, radius = CloseCircleRadius.toPx(), center = center)
            val arm = CloseCrossArm.toPx()
            val stroke = CloseCrossStrokeWidth.toPx()
            drawLine(
                color = iconColor,
                start = Offset(center.x - arm, center.y - arm),
                end = Offset(center.x + arm, center.y + arm),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = iconColor,
                start = Offset(center.x - arm, center.y + arm),
                end = Offset(center.x + arm, center.y - arm),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

/** The corner of a [TopTips] banner: the radius with its smoothing weight. */
private class TopTipsCorner(val radius: Dp, val weight: Float)

/**
 * The COUI 17 top tips corner (`COUIDefaultTopTips.updateRadius` / `createCornerAnimator`): the
 * single-line capsule or the multi-line [TopTipsDefaults.CornerRadius], radius and weight animated
 * together over 300ms with COUIEaseInterpolator when the message changes between the two. An
 * explicit [cornerRadius] is used as is.
 */
@Composable
private fun rememberTopTipsCorner(cornerRadius: Dp, singleLineText: Boolean): TopTipsCorner {
    val target = if (singleLineText) 0f else 1f
    val progress = remember { Animatable(target) }
    LaunchedEffect(target) {
        if (progress.value != target) progress.animateTo(target, tween(CornerAnimDurationMillis, easing = COUIEaseEasing))
    }
    if (cornerRadius != Dp.Unspecified) return TopTipsCorner(cornerRadius, TopTipsDefaults.Weight)
    val fraction = progress.value
    return TopTipsCorner(
        radius = lerp(TopTipsDefaults.SingleLineCornerRadius, TopTipsDefaults.CornerRadius, fraction),
        weight = TopTipsDefaults.SingleLineWeight + (TopTipsDefaults.Weight - TopTipsDefaults.SingleLineWeight) * fraction,
    )
}

/** Contains default values used by [TopTips]. */
object TopTipsDefaults {
    /**
     * The corner radius of a banner whose message wraps or that has its action on its own row (COUI
     * 17 coui_toptips_view_radius 28dp, `getMultiLineRadius`).
     */
    val CornerRadius = 28.dp

    /** The smoothing weight of [CornerRadius] (COUI 17 `getMultiLineWeight`: 3.0). */
    val Weight = COUIRoundCorner.Large.Weight

    /**
     * The corner radius of a single-line banner: couiRoundCornerFULL (72dp), a capsule
     * (`getSingleLineRadius`).
     */
    val SingleLineCornerRadius = COUIRoundCorner.Full

    /** The smoothing weight of [SingleLineCornerRadius]: the 1.0 soft capsule (`getSingleLineWeight`). */
    val SingleLineWeight = COUIRoundCorner.CapsuleWeight

    /**
     * The horizontal margin at both edges of the banner: the start margin of the icon
     * (coui_toptips_view_icon_start_margin) or of the text without an icon
     * (coui_toptips_view_content_end_margin), and the end margin of the text or of a lone button
     * (coui_toptips_view_btn_margin), all 16dp.
     */
    val HorizontalPadding = 16.dp

    /** The top and bottom margin of the text block (coui_toptips_view_content_top_margin). */
    val VerticalPadding = 12.dp

    /** The size of the start icon without a title and of the close button (coui_toptips_view_icon_btn_size). */
    val IconSize = 24.dp

    /** The size of the start icon when a title is shown (coui_toptips_view_title_icon_size). */
    val TitleIconSize = 36.dp

    /** The gap between the start icon and the message (coui_toptips_view_content_start_margin). */
    val IconSpacing = 8.dp

    /** The gap between the title-sized start icon and the text (coui_toptips_view_title_icon_text_gap). */
    val TitleIconSpacing = 12.dp

    /** The minimum height of the message (coui_toptips_view_content_min_height). */
    val TextMinHeight = 20.dp

    /** The minimum height of the title (coui_toptips_view_title_min_height). */
    val TitleMinHeight = 22.dp

    /** The marquee scroll speed per second (COUIMarqueeTextView, coui_top_tips_scroll_speed). */
    val MarqueeVelocity = 25.dp

    /** The text style of the title (couiTextHeadlineXS: 16sp sans-serif-medium). */
    @Composable
    fun titleTextStyle(): TextStyle = COUITheme.textStyles.headlineXS

    /** The text style of the message (couiTextBodyS, coui_toptips_view_default_text_size: 13sp). */
    @Composable
    fun textStyle(): TextStyle = COUITheme.textStyles.bodyS

    /** The text style of the action button label (Widget.COUI.Button.Small: 14sp sans-serif-medium). */
    @Composable
    fun actionTextStyle(): TextStyle = ButtonDefaults.textStyleSmall()

    /**
     * The default [TopTipsColors], mapping COUIDefaultTopTips: container = couiColorContainer4
     * (#0A000000 light / #1AFFFFFF dark, `BackgroundStyle.GRAY`), title = couiColorLabelPrimary,
     * message = couiColorLabelSecondary, action button = couiColorContainerTheme fill with the
     * couiColorLabelOnColor label (`resetDefaultButtonStyle`) and the close button =
     * couiColorLabelTertiary circle with a white cross (ic_embedded_top_tips_close).
     */
    @Composable
    fun topTipsColors(
        containerColor: Color = COUITheme.colorScheme.container4,
        titleColor: Color = COUITheme.colorScheme.labelPrimary,
        contentColor: Color = COUITheme.colorScheme.labelSecondary,
        actionColor: Color = COUITheme.colorScheme.primary,
        actionContentColor: Color = COUITheme.colorScheme.onPrimary,
        closeButtonColor: Color = COUITheme.colorScheme.labelTertiary,
        closeIconColor: Color = Color.White,
    ): TopTipsColors = remember(containerColor, titleColor, contentColor, actionColor, actionContentColor, closeButtonColor, closeIconColor) {
        TopTipsColors(
            containerColor = containerColor,
            titleColor = titleColor,
            contentColor = contentColor,
            actionColor = actionColor,
            actionContentColor = actionContentColor,
            closeButtonColor = closeButtonColor,
            closeIconColor = closeIconColor,
        )
    }
}

/**
 * Colors used by a [TopTips].
 *
 * @param containerColor The background color of the banner.
 * @param titleColor The color of the title.
 * @param contentColor The color of the message.
 * @param actionColor The fill color of the action button.
 * @param actionContentColor The label color of the action button.
 * @param closeButtonColor The fill color of the close button circle.
 * @param closeIconColor The color of the cross inside the close button.
 */
@Immutable
data class TopTipsColors(
    val containerColor: Color,
    val titleColor: Color,
    val contentColor: Color,
    val actionColor: Color,
    val actionContentColor: Color,
    val closeButtonColor: Color,
    val closeIconColor: Color,
)

/** The gap between the text and the button after it (coui_toptips_view_text_to_btn_gap). */
private val TextToButtonGap = 8.dp

/** The gap between the action and the close button (coui_toptips_view_action_close_gap). */
private val ActionCloseGap = 12.dp

/** The end margin of the close button next to an action or a title (coui_toptips_view_close_end_margin). */
private val CloseEndMargin = 12.dp

/** The gap between the message and the action below it (coui_toptips_view_btn_top_margin). */
private val ActionTopMargin = 8.dp

/** The bottom margin of the action below the message (coui_toptips_view_multi_btn_text_bottom_margin). */
private val MultiActionBottomMargin = 12.dp

/** The horizontal padding of the action button (Widget.COUI.Button.Small paddingLeft/Right). */
private val ActionHorizontalPadding = 12.dp

/** The min width of the action button (coui_btn_small_width_min). */
private val ActionMinWidth = ButtonDefaults.MinWidthSmall

/** The min height of the action button (coui_btn_small_height_min). */
private val ActionMinHeight = ButtonDefaults.MinHeightSmall

/** The radius of the close button circle (ic_embedded_top_tips_close: r=10 in a 24dp viewport). */
private val CloseCircleRadius = 10.dp

/**
 * The reach of each cross arm from the center in ic_embedded_top_tips_close: the rounded caps are
 * centred at 8.76 / 15.24 in the 24dp viewport.
 */
private val CloseCrossArm = 3.24.dp

/** The stroke width of the cross in ic_embedded_top_tips_close (the cap diameter, 1.745). */
private val CloseCrossStrokeWidth = 1.745.dp

/** The marquee text inset before scrolling (coui_top_tips_scroll_text_start_location). */
private val MarqueeStartLocation = 10.dp

/** The gap between marquee copies (coui_top_tips_scroll_text_interval). */
private val MarqueeTextInterval = 80.dp

/** The marquee fading edge length (coui_top_tips_fading_edge_size). */
private val MarqueeFadingEdgeLength = 26.dp

/** COUIDefaultTopTips CLIP_ANIMATION_DURATION, the expand / collapse duration. */
private val ExpandCollapseDurationMillis = 250

/** COUIDefaultTopTips.createCornerAnimator duration. */
private val CornerAnimDurationMillis = 300

/** COUIEaseInterpolator (0.33, 0, 0.67, 1), the clip and corner animators' interpolator. */
private val COUIEaseEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)
