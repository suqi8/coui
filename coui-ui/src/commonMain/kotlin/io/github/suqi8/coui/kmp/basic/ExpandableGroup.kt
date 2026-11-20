// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.roundToInt

/**
 * Adds a COUI expandable group to a lazy list: a clickable [ExpandableGroupHeader] item followed
 * by an [ExpandableGroupContent] item holding the children.
 *
 * This mirrors ColorOS's COUIExpandableRecyclerView: clicking a group header toggles the group
 * (COUIExpandableRecyclerView.handleClick), and the children grow in and out of a single
 * placeholder whose height runs between zero and the children's total height while its alpha
 * follows the same fraction (ExpandableRecyclerConnector.DummyView / ExpandAnimator).
 *
 * @param key A stable key of the group, unique within the list. The two items use string keys
 *   derived from it, so it must have a stable [toString].
 * @param title The title of the group header.
 * @param expanded Whether the group is expanded.
 * @param onExpandedChange Called with the requested expanded state when the header is clicked.
 * @param summary The optional summary of the group header.
 * @param enabled Whether the header can be clicked.
 * @param content The children of the group.
 */
fun LazyListScope.expandableGroup(
    key: Any,
    title: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    summary: String? = null,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    item(key = "coui_expandable_group_header_$key") {
        ExpandableGroupHeader(
            title = title,
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            summary = summary,
            enabled = enabled,
        )
    }
    item(key = "coui_expandable_group_content_$key") {
        ExpandableGroupContent(expanded = expanded, content = content)
    }
}

/**
 * The header row of a COUI expandable group: a [BasicComponent] with title and summary, and an
 * [ExpandableGroupArrow] at the end that turns over when the group expands.
 *
 * @param title The title of the header.
 * @param expanded Whether the group is expanded.
 * @param onExpandedChange Called with the requested expanded state when the header is clicked.
 * @param modifier The modifier to be applied to the header.
 * @param summary The optional summary of the header.
 * @param enabled Whether the header can be clicked.
 * @param arrowColor The color of the arrow.
 */
@Composable
fun ExpandableGroupHeader(
    title: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    arrowColor: Color = ExpandableGroupDefaults.arrowColor(),
) {
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    BasicComponent(
        modifier = modifier.semantics {
            if (enabled) {
                if (expanded) {
                    collapse {
                        currentOnExpandedChange(false)
                        true
                    }
                } else {
                    expand {
                        currentOnExpandedChange(true)
                        true
                    }
                }
            }
        },
        title = title,
        summary = summary,
        endActions = {
            ExpandableGroupArrow(expanded = expanded, color = arrowColor)
        },
        onClick = { currentOnExpandedChange(!expanded) },
        enabled = enabled,
    )
}

/**
 * The expand / collapse arrow of a COUI expandable group header, as ColorOS 17 Settings draws its
 * expandable rows (`settings_preference_widget_rotateview.xml`): a COUIRotateView of
 * `expandable_list_item_size` (24dp) showing the `coui_line_arrow` chevron (16 x 24dp,
 * couiColorLabelSecondary) with `supportRotateType` couiRotateAloneZ, which turns the view from 0 to
 * 180 degrees over 400ms on PathInterpolator(0.133, 0, 0.3, 1).
 *
 * @param expanded Whether the arrow points at the expanded state.
 * @param modifier The modifier to be applied to the arrow.
 * @param color The color of the arrow.
 * @param size The size of the box the arrow turns in; the chevron keeps its 2:3 aspect ratio and is
 *   centered in it.
 */
@Composable
fun ExpandableGroupArrow(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    color: Color = ExpandableGroupDefaults.arrowColor(),
    size: Dp = ExpandableGroupDefaults.ArrowSize,
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) ARROW_EXPANDED_ROTATION else 0f,
        animationSpec = tween(EXPAND_DURATION_MILLIS, easing = RotateEase),
    )
    val colorFilter = remember(color) { ColorFilter.tint(color) }
    Image(
        imageVector = ExpanderArrowIcon,
        contentDescription = null,
        colorFilter = colorFilter,
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation },
    )
}

/**
 * The children of a COUI expandable group, revealed and hidden with the group animation.
 *
 * ExpandableRecyclerConnector animates a placeholder view from 0 to the children's total height
 * (expanding) or back (collapsing) in 400ms on COUIMoveEaseInterpolator (0.3, 0, 0.1, 1), with
 * the placeholder alpha equal to `height / totalHeight`. An animation that interrupts a running
 * one starts from the current height: a collapse lasts `height / totalHeight * 400ms`, while an
 * expand always takes the full 400ms (`expandAnimationStart` is handed the total height). The
 * children are only composed while the group is expanded or animating.
 *
 * @param expanded Whether the group is expanded.
 * @param modifier The modifier to be applied to the content.
 * @param content The children of the group.
 */
@Composable
fun ExpandableGroupContent(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val fraction = remember { Animatable(if (expanded) 1f else 0f) }
    LaunchedEffect(expanded) {
        val target = if (expanded) 1f else 0f
        if (fraction.value != target) {
            // ExpandableRecyclerConnector.collapseAnimationStart scales the duration by
            // dummyHeight / totalHeight; expandAnimationStart passes totalHeight, so it keeps 400ms.
            val duration = if (expanded) {
                EXPAND_DURATION_MILLIS
            } else {
                (EXPAND_DURATION_MILLIS * fraction.value).roundToInt()
            }
            fraction.animateTo(
                targetValue = target,
                animationSpec = tween(duration, easing = MoveEase),
            )
        }
    }
    val hasHeight by remember { derivedStateOf { fraction.value > 0f } }
    val visible = expanded || hasHeight
    Layout(
        // One layer clips the growing placeholder and applies its alpha.
        modifier = modifier.graphicsLayer {
            clip = true
            alpha = fraction.value
        },
        content = {
            if (visible) {
                Column(content = content)
            }
        },
    ) { measurables, constraints ->
        val placeable = measurables.firstOrNull()?.measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
        val fullHeight = placeable?.height ?: 0
        val width = placeable?.width ?: constraints.minWidth
        val height = (fullHeight * fraction.value).roundToInt()
        layout(width, constraints.constrainHeight(height)) {
            placeable?.placeRelative(0, 0)
        }
    }
}

/** Contains default values used by the COUI expandable group. */
object ExpandableGroupDefaults {
    /** The size of the box the arrow turns in (COUI expandable_list_item_size, 24dp). */
    val ArrowSize = 24.dp

    /** The arrow color (COUI coui_line_arrow: couiColorLabelSecondary #8A000000 / #8AFFFFFF). */
    @Composable
    fun arrowColor(): Color = COUITheme.colorScheme.labelSecondary
}

/** The downward chevron of COUI coui_line_arrow_collapsed.xml (16 x 24dp, 16 x 24 viewport). */
private val ExpanderArrowIcon: ImageVector by lazy {
    ImageVector.Builder("ExpanderArrow", 16.dp, 24.dp, 16f, 24f).apply {
        addPath(
            pathData = addPathNodes(
                "M8.595,13.743C8.751,13.901 8.829,13.981 8.921,14.003C8.973,14.016 9.027,14.016 9.079,14.003" +
                    "C9.171,13.981 9.249,13.901 9.405,13.743L15.035,8L16,8.975L9.795,15.202" +
                    "C9.489,15.509 9.336,15.663 9.154,15.707C9.053,15.731 8.947,15.731 8.846,15.707" +
                    "C8.664,15.663 8.511,15.509 8.205,15.202L2,8.975L2.966,8L8.595,13.743Z",
            ),
            pathFillType = PathFillType.EvenOdd,
            fill = SolidColor(Color.Black),
        )
    }.build()
}

/** ExpandableRecyclerConnector ANIMATION_DURATION, also COUIRotateView's mDuration. */
private const val EXPAND_DURATION_MILLIS = 400

/** COUIRotateView ROTATION_ANGLE. */
private const val ARROW_EXPANDED_ROTATION = 180f

/** COUIMoveEaseInterpolator. */
private val MoveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** COUIRotateView PathInterpolator(0.133, 0, 0.3, 1). */
private val RotateEase = CubicBezierEasing(0.133f, 0f, 0.3f, 1f)
