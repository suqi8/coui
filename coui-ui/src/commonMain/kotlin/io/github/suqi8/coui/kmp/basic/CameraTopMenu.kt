// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.effect.MaterialBlurBlendMode
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * An entry of a [CameraTopMenu]: an icon in the bar and, when [options] is not empty, the option
 * panel it expands into.
 *
 * @param key The unique key of the entry, reported by the menu callbacks.
 * @param icon The icon, drawn as is (the camera's menu drawables carry their own colors), 20dp.
 * @param contentDescription The accessibility description of the entry.
 * @param options The option rows of the panel. One row expands into the single-row strip of the
 *   camera's `CameraMenuContainerExpandTopSingle` (flash, ratio); more rows expand into the grouped
 *   panel of `CameraMenuContainerExpandTopGroup` (video resolution and frame rate). Empty for a
 *   plain toggle.
 */
@Immutable
data class CameraTopMenuItem(
    val key: String,
    val icon: ImageVector,
    val contentDescription: String?,
    val options: List<CameraTopMenuOptionRow> = emptyList(),
)

/**
 * One option row of a [CameraTopMenuItem] panel (a camera `CheckListBar`).
 *
 * @param key The unique key of the row within its entry, reported by
 *   [CameraTopMenu]'s `onOptionSelected`.
 * @param options The option labels, from start to end.
 * @param selectedIndex The index of the selected option, drawn in the accent color.
 * @param title An optional title at the start of the row. In a single-row panel the title replaces
 *   the entry icon at the start of the strip.
 */
@Immutable
data class CameraTopMenuOptionRow(
    val key: String,
    val options: List<String>,
    val selectedIndex: Int,
    val title: String? = null,
)

/**
 * The settings bar at the top of the camera viewfinder, the port of the camera's
 * `CameraMenuContainerTopLeft` / `TopMiddle` / `TopRight` and their expand panels.
 *
 * Up to three 32dp capsules hold the entries as 32dp cells with 20dp icons: [startItems] 14dp from
 * the start, [endItems] 14dp from the end and [middleItems] 10dp before the end capsule. Each
 * capsule is the camera material blur when [materialBlur] is given (80dp, dark parameters) and a
 * 50% black capsule otherwise.
 *
 * Tapping an entry calls [onItemClick]; set [expandedItemKey] to an entry with options to open its
 * panel, and back to null to close it. The panel morphs out of the entry's capsule over the full
 * width of the bar (minus the 14dp margins), drawn on top of the bar and below it, outside the
 * bar's bounds, so give it room and do not clip the bar:
 * - one option row: a 32dp strip (`CameraMenuContainerExpandTopSingle`) grows from the capsule in
 *   380ms on the COUI critically damped spring (response 0.8); the entry icon slides to the start
 *   of the strip in 300ms (0.3, 0, 0.1, 1) and the options fade in 200ms later over 300ms
 *   (0.33, 0, 0.67, 1). Closing runs the morph back while the options fade out over 300ms.
 * - several rows: a panel (`CameraMenuContainerExpandTopGroup`, 24dp start / 12dp end / 12dp
 *   vertical padding, 38dp rows, a 48dp title column) springs out of the capsule (COUI spring,
 *   bounce 0.2, response 0.5) while its corner grows from 16dp to 24dp, the content scales from 10%
 *   to full size, the capsule shrinks to 10%, and the content fades in 150ms later over 300ms.
 *   Closing springs back while the whole panel fades out over 150ms. It carries the camera edge
 *   light; its non-blur fill is the opaque #141414 panel with a hairline border.
 *
 * Options are 12dp text, medium, turning semibold and fading to the camera accent in 300ms when
 * selected; tapping one calls [onOptionSelected]. The menu always uses its fixed light-on-dark
 * palette, since it sits over the camera preview.
 *
 * @param onItemClick Called with the key of a tapped entry (also the icon at the start of an open
 *   single-row strip).
 * @param onOptionSelected Called with the entry key, the row key and the index of a tapped option.
 * @param modifier The modifier to be applied to the bar. It fills the width it is given and is
 *   [CameraTopMenuDefaults.ItemSize] high.
 * @param startItems The entries of the start capsule.
 * @param middleItems The entries of the capsule before the end capsule.
 * @param endItems The entries of the end capsule.
 * @param expandedItemKey The key of the entry whose panel is open, or null.
 * @param materialBlur Paints the camera material blur behind the capsules and panels. Pass a
 *   provider recording the content under the bar (for example the camera preview); null keeps the
 *   opaque fills. The recorded content must not contain the bar itself.
 * @param colors The colors of the menu, see [CameraTopMenuDefaults.cameraTopMenuColors].
 */
@Composable
fun CameraTopMenu(
    onItemClick: (itemKey: String) -> Unit,
    onOptionSelected: (itemKey: String, rowKey: String, index: Int) -> Unit,
    modifier: Modifier = Modifier,
    startItems: List<CameraTopMenuItem> = emptyList(),
    middleItems: List<CameraTopMenuItem> = emptyList(),
    endItems: List<CameraTopMenuItem> = emptyList(),
    expandedItemKey: String? = null,
    materialBlur: MaterialBlurProvider? = null,
    colors: CameraTopMenuColors = CameraTopMenuDefaults.cameraTopMenuColors(),
) {
    val density = LocalDensity.current
    val blurSpec = remember(density, colors) {
        MaterialBlurSpec(
            blurRadiusPx = with(density) { CameraTopMenuDefaults.BlurRadius.toPx() },
            mixColor = colors.blurMixColor,
            blendColor = colors.blurBlendColor,
            blendMode = MaterialBlurBlendMode.GlowOverlay,
        )
    }
    val groups = listOf(startItems, middleItems, endItems)
    val geometry = remember { TopMenuGeometry() }
    val progress = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    val iconProgress = remember { Animatable(0f) }
    var displayedKey by remember { mutableStateOf<String?>(null) }
    var collapsing by remember { mutableStateOf(false) }

    LaunchedEffect(expandedItemKey) {
        val target = locateTopMenuItem(expandedItemKey, groups)?.takeIf { it.item.options.isNotEmpty() }
        if (target != null) {
            if (displayedKey != target.item.key) {
                progress.snapTo(0f)
                contentAlpha.snapTo(0f)
                iconProgress.snapTo(0f)
            }
            displayedKey = target.item.key
            collapsing = false
            val single = target.item.options.size == 1
            coroutineScope {
                launch { progress.animateTo(1f, topMenuMorphSpec(single)) }
                launch {
                    contentAlpha.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(300, delayMillis = if (single) 200 else 150, easing = TopMenuStandardEasing),
                    )
                }
                if (single) launch { iconProgress.animateTo(1f, tween(300, easing = TopMenuMoveEaseEasing)) }
            }
        } else if (displayedKey != null) {
            val single = locateTopMenuItem(displayedKey, groups)?.item?.options?.size == 1
            collapsing = true
            coroutineScope {
                launch { progress.animateTo(0f, topMenuMorphSpec(single)) }
                launch { contentAlpha.animateTo(0f, tween(if (single) 300 else 150, easing = TopMenuStandardEasing)) }
                if (single) launch { iconProgress.animateTo(0f, tween(300, easing = TopMenuMoveEaseEasing)) }
            }
            displayedKey = null
            collapsing = false
        }
    }

    val displayed = locateTopMenuItem(displayedKey, groups)?.takeIf { it.item.options.isNotEmpty() }
    val displayedSingle = displayed?.item?.options?.size == 1
    val capsuleShape = remember { RoundedCornerShape(CameraTopMenuDefaults.CornerRadius) }

    Layout(
        modifier = modifier
            .fillMaxWidth()
            .height(CameraTopMenuDefaults.ItemSize),
        content = {
            groups.forEachIndexed { groupIndex, items ->
                if (items.isNotEmpty()) {
                    val sourceMode = when {
                        displayed?.group != groupIndex -> SOURCE_NONE
                        displayedSingle -> SOURCE_SINGLE
                        else -> SOURCE_GROUP
                    }
                    TopMenuCapsule(
                        items = items,
                        onItemClick = onItemClick,
                        backgroundModifier = materialBlur?.materialBlur(blurSpec, capsuleShape)
                            ?: Modifier.background(colors.containerColor, capsuleShape),
                        sourceMode = sourceMode,
                        progress = { progress.value },
                        modifier = Modifier.layoutId(groupIndex),
                    )
                }
            }
            if (displayed != null) {
                TopMenuPanel(
                    location = displayed,
                    geometry = geometry,
                    progress = { progress.value },
                    contentAlpha = { contentAlpha.value },
                    iconProgress = { iconProgress.value },
                    panelAlpha = { if (collapsing && !displayedSingle) contentAlpha.value else 1f },
                    onItemClick = onItemClick,
                    onOptionSelected = onOptionSelected,
                    materialBlur = materialBlur,
                    blurSpec = blurSpec,
                    colors = colors,
                    modifier = Modifier.layoutId(PANEL_ID),
                )
            }
        },
    ) { measurables, constraints ->
        val margin = CameraTopMenuDefaults.HorizontalMargin.roundToPx()
        val gap = CameraTopMenuDefaults.MiddleSpacing.roundToPx()
        val itemSize = CameraTopMenuDefaults.ItemSize.roundToPx()
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val capsules = arrayOfNulls<Placeable>(3)
        var panel: Placeable? = null
        val capsuleMeasurables = measurables.filter { it.layoutId != PANEL_ID }
        capsuleMeasurables.forEach { measurable ->
            val id = measurable.layoutId as Int
            capsules[id] = measurable.measure(loose)
        }
        val width = if (constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            capsules.sumOf { it?.width ?: 0 } + margin * 2 + gap
        }
        val endX = width - margin - (capsules[2]?.width ?: 0)
        geometry.capsuleLeft[0] = margin
        geometry.capsuleLeft[2] = endX
        geometry.capsuleLeft[1] = if (capsules[2] != null) {
            endX - gap - (capsules[1]?.width ?: 0)
        } else {
            width - margin - (capsules[1]?.width ?: 0)
        }
        for (i in 0 until 3) geometry.capsuleWidth[i] = capsules[i]?.width ?: 0
        geometry.margin = margin
        geometry.itemSize = itemSize
        geometry.panelWidth = (width - margin * 2).coerceAtLeast(0)
        measurables.firstOrNull { it.layoutId == PANEL_ID }?.let {
            panel = it.measure(Constraints(minWidth = geometry.panelWidth, maxWidth = geometry.panelWidth))
        }
        val height = itemSize.coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(width, height) {
            for (i in 0 until 3) capsules[i]?.placeRelative(geometry.capsuleLeft[i], 0)
            panel?.placeRelative(margin, 0)
        }
    }
}

/** A bar capsule (`CameraMenuContainerTop*`): a row of 32dp entry cells on the capsule fill. */
@Composable
private fun TopMenuCapsule(
    items: List<CameraTopMenuItem>,
    onItemClick: (String) -> Unit,
    sourceMode: Int,
    progress: () -> Float,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(CameraTopMenuDefaults.ItemSize)
            .graphicsLayer {
                when (sourceMode) {
                    // The strip takes over the capsule: its background is suppressed while expanded.
                    SOURCE_SINGLE -> alpha = 0f

                    // The capsule shrinks to 10% under the growing panel (`ExpandTopGroup.l`).
                    SOURCE_GROUP -> {
                        val scale = 1f - progress() * 0.9f
                        scaleX = scale
                        scaleY = scale
                    }
                }
            }
            .then(backgroundModifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            Box(
                modifier = Modifier
                    .size(CameraTopMenuDefaults.ItemSize)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = { onItemClick(item.key) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription,
                    modifier = Modifier.size(CameraTopMenuDefaults.IconSize),
                    tint = Color.Unspecified,
                )
            }
        }
    }
}

/**
 * The expanded panel of an entry. The outer layout places the morphing window (the panel outline)
 * between the source capsule and the full panel; the inner layout keeps the content at its final
 * position inside the window.
 */
@Composable
private fun TopMenuPanel(
    location: TopMenuLocation,
    geometry: TopMenuGeometry,
    progress: () -> Float,
    contentAlpha: () -> Float,
    iconProgress: () -> Float,
    panelAlpha: () -> Float,
    onItemClick: (String) -> Unit,
    onOptionSelected: (String, String, Int) -> Unit,
    materialBlur: MaterialBlurProvider?,
    blurSpec: MaterialBlurSpec,
    colors: CameraTopMenuColors,
    modifier: Modifier = Modifier,
) {
    val item = location.item
    val single = item.options.size == 1
    val shape = remember(single) {
        TopMenuMorphShape { density ->
            with(density) {
                if (single) {
                    CameraTopMenuDefaults.CornerRadius.toPx()
                } else {
                    val from = CameraTopMenuDefaults.ItemSize.toPx() / 2f
                    from + (CameraTopMenuDefaults.PanelCornerRadius.toPx() - from) * progress()
                }
            }
        }
    }
    val background = materialBlur?.materialBlur(blurSpec, shape) ?: if (single) {
        Modifier.background(colors.containerColor, shape)
    } else {
        Modifier
            .background(colors.panelContainerColor, shape)
            .border(CameraTopMenuDefaults.PanelBorderWidth, colors.panelBorderColor, shape)
    }
    val finalHeight: Density.() -> Int = {
        if (single) {
            CameraTopMenuDefaults.ItemSize.roundToPx()
        } else {
            (CameraTopMenuDefaults.PanelPaddingVertical * 2 + CameraTopMenuDefaults.PanelRowHeight * item.options.size).roundToPx()
        }
    }
    Box(
        modifier = modifier
            .layout { measurable, _ ->
                val window = geometry.window(location.group, progress(), finalHeight())
                val placeable = measurable.measure(Constraints.fixed(window.width, window.height))
                layout(geometry.panelWidth, finalHeight()) { placeable.placeRelative(window.x, 0) }
            }
            .graphicsLayer { alpha = panelAlpha() }
            .then(background)
            .then(if (single) Modifier else Modifier.cameraInnerShadow(CameraInnerShadows.Panel, CameraTopMenuDefaults.PanelCornerRadius))
            .clip(shape)
            // Taps between the options stay on the panel.
            .pointerInput(Unit) { detectTapGestures { } }
            .layout { measurable, constraints ->
                val window = geometry.window(location.group, progress(), finalHeight())
                val placeable = measurable.measure(Constraints.fixed(geometry.panelWidth, finalHeight()))
                layout(constraints.maxWidth, constraints.maxHeight) { placeable.placeRelative(-window.x, 0) }
            },
    ) {
        if (single) {
            TopMenuSingleContent(
                item = item,
                iconStartX = { (geometry.capsuleLeft[location.group] - geometry.margin + location.index * geometry.itemSize).toFloat() },
                contentAlpha = contentAlpha,
                iconProgress = iconProgress,
                onItemClick = onItemClick,
                onOptionSelected = onOptionSelected,
                colors = colors,
            )
        } else {
            TopMenuGroupContent(
                item = item,
                progress = progress,
                contentAlpha = contentAlpha,
                onOptionSelected = onOptionSelected,
                colors = colors,
            )
        }
    }
}

/**
 * The single-row strip (`ExpandTopSingle`): the entry icon (or the row title, 48dp) at the start
 * and the options sharing the rest of the width, as `setDataWithIconType` / `setDataWithTitleType`.
 */
@Composable
private fun TopMenuSingleContent(
    item: CameraTopMenuItem,
    iconStartX: () -> Float,
    contentAlpha: () -> Float,
    iconProgress: () -> Float,
    onItemClick: (String) -> Unit,
    onOptionSelected: (String, String, Int) -> Unit,
    colors: CameraTopMenuColors,
) {
    val row = item.options.first()
    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        if (row.title != null) {
            TopMenuTitle(
                text = row.title,
                textAlign = TextAlign.Center,
                colors = colors,
                modifier = Modifier
                    .width(CameraTopMenuDefaults.TitleWidth)
                    .graphicsLayer { alpha = contentAlpha() },
            )
        } else {
            Box(
                modifier = Modifier
                    // The shared icon glides from its cell in the capsule to the start of the strip.
                    .offset { IntOffset((iconStartX() * (1f - iconProgress())).roundToInt(), 0) }
                    .size(CameraTopMenuDefaults.ItemSize)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = { onItemClick(item.key) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription,
                    modifier = Modifier.size(CameraTopMenuDefaults.IconSize),
                    tint = Color.Unspecified,
                )
            }
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = if (row.title != null) CameraTopMenuDefaults.PanelPaddingEnd else 0.dp)
                .graphicsLayer { alpha = contentAlpha() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            row.options.forEachIndexed { index, label ->
                TopMenuOption(
                    text = label,
                    selected = index == row.selectedIndex,
                    onClick = { onOptionSelected(item.key, row.key, index) },
                    colors = colors,
                    modifier = Modifier
                        .weight(1f)
                        .height(CameraTopMenuDefaults.ItemSize),
                )
            }
        }
    }
}

/**
 * The grouped panel (`ExpandTopGroup`): one 38dp row per option row, the options of every row
 * sharing the width left after the 48dp title column, divided by the longest row.
 */
@Composable
private fun TopMenuGroupContent(
    item: CameraTopMenuItem,
    progress: () -> Float,
    contentAlpha: () -> Float,
    onOptionSelected: (String, String, Int) -> Unit,
    colors: CameraTopMenuColors,
) {
    val maxCount = item.options.maxOf { it.options.size }.coerceAtLeast(1)
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = contentAlpha()
                val scale = 0.1f + 0.9f * progress()
                scaleX = scale
                scaleY = scale
            }
            .padding(
                start = CameraTopMenuDefaults.PanelPaddingStart,
                end = CameraTopMenuDefaults.PanelPaddingEnd,
                top = CameraTopMenuDefaults.PanelPaddingVertical,
                bottom = CameraTopMenuDefaults.PanelPaddingVertical,
            ),
    ) {
        val optionWidth = ((maxWidth - CameraTopMenuDefaults.TitleWidth) / maxCount).coerceAtLeast(0.dp)
        Column {
            item.options.forEach { row ->
                Row(
                    modifier = Modifier.height(CameraTopMenuDefaults.PanelRowHeight),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (row.title != null) {
                        TopMenuTitle(
                            text = row.title,
                            textAlign = TextAlign.Start,
                            colors = colors,
                            modifier = Modifier.width(CameraTopMenuDefaults.TitleWidth),
                        )
                    }
                    row.options.forEachIndexed { index, label ->
                        TopMenuOption(
                            text = label,
                            selected = index == row.selectedIndex,
                            onClick = { onOptionSelected(item.key, row.key, index) },
                            colors = colors,
                            modifier = Modifier
                                .width(optionWidth)
                                .height(CameraTopMenuDefaults.ItemSize),
                        )
                    }
                }
            }
        }
    }
}

/** A row title (`DefaultCheckListAdapter.a`): 12dp medium text in `text_color_top_expand_menu_title`. */
@Composable
private fun TopMenuTitle(
    text: String,
    textAlign: TextAlign,
    colors: CameraTopMenuColors,
    modifier: Modifier = Modifier,
) {
    val style = rememberTopMenuTextStyle(FontWeight.Medium, textAlign)
    Text(
        text = text,
        modifier = modifier,
        color = colors.titleColor,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

/**
 * An option (`SelectableTextView`): 12dp text with 4dp side padding, medium, semibold when
 * selected, its color fading between white and the accent in 300ms (0.33, 0, 0.67, 1).
 */
@Composable
private fun TopMenuOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    colors: CameraTopMenuColors,
    modifier: Modifier = Modifier,
) {
    val color = animateColorAsState(
        targetValue = if (selected) colors.selectedOptionColor else colors.optionColor,
        animationSpec = tween(300, easing = TopMenuStandardEasing),
    )
    val style = rememberTopMenuTextStyle(if (selected) FontWeight.SemiBold else FontWeight.Medium, TextAlign.Center)
    Box(
        modifier = modifier.selectable(
            selected = selected,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            role = Role.RadioButton,
            onClick = onClick,
        ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = { color.value },
            modifier = Modifier.padding(horizontal = CameraTopMenuDefaults.OptionPaddingHorizontal),
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** The menu text style, in dp like the camera (`camera_top_expand_menu_text_size`). */
@Composable
private fun rememberTopMenuTextStyle(weight: FontWeight, textAlign: TextAlign): TextStyle {
    val density = LocalDensity.current
    return remember(density, weight, textAlign) {
        TextStyle(
            fontSize = (CameraTopMenuDefaults.TextSize.value / density.fontScale).sp,
            fontWeight = weight,
            textAlign = textAlign,
        )
    }
}

private const val SOURCE_NONE = 0
private const val SOURCE_SINGLE = 1
private const val SOURCE_GROUP = 2
private const val PANEL_ID = 3

/** Where an entry sits: its capsule (0 start, 1 middle, 2 end) and its index in the capsule. */
private class TopMenuLocation(val group: Int, val index: Int, val item: CameraTopMenuItem)

private fun locateTopMenuItem(key: String?, groups: List<List<CameraTopMenuItem>>): TopMenuLocation? {
    if (key == null) return null
    groups.forEachIndexed { group, items ->
        val index = items.indexOfFirst { it.key == key }
        if (index >= 0) return TopMenuLocation(group, index, items[index])
    }
    return null
}

/** The window of the panel at a morph progress, relative to the panel's start. */
private class TopMenuWindow(val x: Int, val width: Int, val height: Int)

/**
 * The capsule positions of the last layout, written while the bar measures and read by the panel
 * layout (not snapshot state: the panel is measured right after the bar writes it).
 */
private class TopMenuGeometry {
    val capsuleLeft = IntArray(3)
    val capsuleWidth = IntArray(3)
    var margin = 0
    var itemSize = 0
    var panelWidth = 0

    /** `ExpandTopSingle.H0` / `ExpandTopGroup.l`: the bounds between the capsule and the panel. */
    fun window(group: Int, progress: Float, finalHeight: Int): TopMenuWindow {
        val fromX = (capsuleLeft[group] - margin).toFloat()
        val fromWidth = capsuleWidth[group].toFloat()
        val fromHeight = itemSize.toFloat()
        return TopMenuWindow(
            x = (fromX * (1f - progress)).roundToInt(),
            width = (fromWidth + (panelWidth - fromWidth) * progress).roundToInt().coerceAtLeast(0),
            height = (fromHeight + (finalHeight - fromHeight) * progress).roundToInt().coerceAtLeast(0),
        )
    }
}

/** A rounded rect whose radius is read when the outline is built, coerced to a capsule. */
private class TopMenuMorphShape(private val radius: (Density) -> Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val r = min(radius(density), min(size.width, size.height) / 2f).coerceAtLeast(0f)
        return Outline.Rounded(RoundRect(0f, 0f, size.width, size.height, CornerRadius(r)))
    }
}

/** The morph animation: the 380ms spring-eased tween of a strip, the COUI spring of a panel. */
private fun topMenuMorphSpec(single: Boolean): AnimationSpec<Float> = if (single) {
    tween(durationMillis = 380, easing = TopMenuExpandEasing)
} else {
    // COUISpringForce b(bounce 0.2, response 0.5): damping 0.8, stiffness (2 pi / 0.5)^2.
    spring(dampingRatio = 0.8f, stiffness = 157.91367f, visibilityThreshold = 0.00025f)
}

/**
 * `COUISpringInterpolator(0.8, 0)`: a critically damped spring of response 0.8 sampled over the
 * animation fraction, `1 - e^(-wt)(1 + wt)` with w = 2 pi / 0.8, normalized to reach 1.
 */
private val TopMenuExpandEasing: Easing = object : Easing {
    private val omega = (2.0 * PI / 0.8).toFloat()
    private val end = 1f - exp(-omega) * (1f + omega)

    override fun transform(fraction: Float): Float = (1f - exp(-omega * fraction) * (1f + omega * fraction)) / end
}

/** `com.coui.appcompat.animation.e`, the COUI move-ease curve (0.3, 0, 0.1, 1). */
private val TopMenuMoveEaseEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `com.coui.appcompat.animation.b`, the COUI standard curve (0.33, 0, 0.67, 1). */
private val TopMenuStandardEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** Default values of [CameraTopMenu], from the camera's `camera_menu_*` / `camera_top_*` dimensions. */
object CameraTopMenuDefaults {
    /** The bar height and entry cell size (`camera_menu_item_height`, `camera_menu_option_size`). */
    val ItemSize = 32.dp

    /** The entry icon size (`camera_menu_option_icon_size`). */
    val IconSize = 20.dp

    /** The bar inset at both sides (`camera_top_menu_item_margin`, `camera_top_expand_menu_margin_*`). */
    val HorizontalMargin = 14.dp

    /** The gap between the middle and the end capsule (`camera_top_middle_menu_margin_end`). */
    val MiddleSpacing = 10.dp

    /** The capsule and strip corner radius (`setting_corner_radius`), coerced to a capsule. */
    val CornerRadius = 32.dp

    /** The material blur radius (`setting_blur_radius`). */
    val BlurRadius = 80.dp

    /** The corner radius of an open grouped panel (`camera_top_expand_menu_corner`). */
    val PanelCornerRadius = 24.dp

    /** The grouped panel start padding (`camera_top_expand_menu_padding_start`). */
    val PanelPaddingStart = 24.dp

    /** The panel end padding (`camera_top_expand_menu_padding_end`). */
    val PanelPaddingEnd = 12.dp

    /** The grouped panel top and bottom padding (`camera_top_expand_menu_padding_vertical`). */
    val PanelPaddingVertical = 12.dp

    /** The grouped panel row height (`camera_top_expand_menu_item_height`). */
    val PanelRowHeight = 38.dp

    /** The row title width (`camera_top_expand_menu_item_title_width`). */
    val TitleWidth = 48.dp

    /** The border of the non-blur grouped panel (`bottom_menu_container_border_width`). */
    val PanelBorderWidth = 0.67.dp

    /** The title and option text size (`camera_top_expand_menu_text_size`), in dp like the camera. */
    val TextSize = 12.dp

    /** The option text side padding (`dd_toolbar_selection_text_padding_horizontal`). */
    val OptionPaddingHorizontal = 4.dp

    /**
     * The fixed camera palette: the `color_black_with_50_percent_transparency` capsules and strip,
     * the `color_bottom_menu_bg_color_no_blur` #141414 grouped panel with a
     * `color_white_with_10_percent_transparency` border, the material blur colors of
     * `BlurBackgroundImpl` (dark parameters, progress 1: a 15% grey mixed at 60%, a 65% grey glow
     * overlay at 40%), `text_color_top_expand_menu_title` #88FFFFFF titles, white options and the
     * accent `color_primary_light_hasselblad` #FB822A for the selected option.
     */
    @Composable
    fun cameraTopMenuColors(
        containerColor: Color = Color(0x7F000000),
        panelContainerColor: Color = Color(0xFF141414),
        panelBorderColor: Color = Color(0x1AFFFFFF),
        blurMixColor: Color = Color(red = 0.15f, green = 0.15f, blue = 0.15f, alpha = 0.6f),
        blurBlendColor: Color = Color(red = 0.65f, green = 0.65f, blue = 0.65f, alpha = 0.4f),
        titleColor: Color = Color(0x88FFFFFF),
        optionColor: Color = Color.White,
        selectedOptionColor: Color = Color(0xFFFB822A),
    ): CameraTopMenuColors = remember(
        containerColor,
        panelContainerColor,
        panelBorderColor,
        blurMixColor,
        blurBlendColor,
        titleColor,
        optionColor,
        selectedOptionColor,
    ) {
        CameraTopMenuColors(
            containerColor = containerColor,
            panelContainerColor = panelContainerColor,
            panelBorderColor = panelBorderColor,
            blurMixColor = blurMixColor,
            blurBlendColor = blurBlendColor,
            titleColor = titleColor,
            optionColor = optionColor,
            selectedOptionColor = selectedOptionColor,
        )
    }
}

/**
 * The colors of a [CameraTopMenu].
 *
 * @param containerColor The capsules and the single-row strip without material blur.
 * @param panelContainerColor The grouped panel without material blur.
 * @param panelBorderColor The border of the grouped panel without material blur.
 * @param blurMixColor The material blur mix color.
 * @param blurBlendColor The material blur glow-overlay color.
 * @param titleColor The row titles.
 * @param optionColor The options.
 * @param selectedOptionColor The selected option.
 */
@Immutable
data class CameraTopMenuColors(
    val containerColor: Color,
    val panelContainerColor: Color,
    val panelBorderColor: Color,
    val blurMixColor: Color,
    val blurBlendColor: Color,
    val titleColor: Color,
    val optionColor: Color,
    val selectedOptionColor: Color,
)
