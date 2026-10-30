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
import kotlin.math.abs
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
 * The expand / collapse arrow of a COUI expandable group header: the filled triangle of COUI
 * coui_app_expander_close_default (24dp, couiColorControls) turned by COUIRotateView with
 * `supportRotateType` "z": 0 to 180 degrees over 400ms on PathInterpolator(0.133, 0, 0.3, 1).
 *
 * @param expanded Whether the arrow points at the expanded state.
 * @param modifier The modifier to be applied to the arrow.
 * @param color The color of the arrow.
 * @param size The size of the arrow.
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
 * one starts from the current height and is shortened in proportion to the distance left. The
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
        val distance = abs(target - fraction.value)
        if (distance > 0f) {
            fraction.animateTo(
                targetValue = target,
                animationSpec = tween((EXPAND_DURATION_MILLIS * distance).roundToInt(), easing = MoveEase),
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
    /** The size of the arrow (COUI coui_app_expander_close_default, 24dp). */
    val ArrowSize = 24.dp

    /** The arrow color (COUI coui_color_controls #29000000 / #40FFFFFF). */
    @Composable
    fun arrowColor(): Color = COUITheme.colorScheme.dragHandle
}

/** The downward triangle of COUI coui_app_expander_close_default.xml (72 x 72 viewport). */
private val ExpanderArrowIcon: ImageVector by lazy {
    ImageVector.Builder("ExpanderArrow", 24.dp, 24.dp, 72f, 72f).apply {
        addPath(
            pathData = addPathNodes(
                "M 25.6 28 L 46.4 28 C 47.3 28 48 28.7 48 29.6 C 48 30 47.9 30.3 47.6 30.6 L 37.6 43.2 " +
                    "C 36.9 44.1 35.7 44.2 34.8 43.5 L 34.5 43.2 L 24.4 30.6 C 23.8 29.9 24 28.9 24.6 28.3 " +
                    "C 24.9 28.1 25.2 28 25.6 28 Z",
            ),
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
