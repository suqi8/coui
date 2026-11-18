// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import canvas.model.Frame
import canvas.model.Group
import canvas.model.Kind
import canvas.model.STATUS_BAR_H
import canvas.model.frameOf
import canvas.model.fuse
import canvas.model.groupsOn
import canvas.model.moveFrame
import canvas.model.updateGroup
import canvas.model.worldBox
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.icon.extended.ZoomOut
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.exp
import kotlin.math.roundToInt
import canvas.model.Box as DpBox

/** The selection colour of the editor chrome (outlines, guides, handles). */
val SelectionBlue = Color(0xFF3482FF)

/** Snapping guide colour. */
private val GuidePink = Color(0xFFFF3B6B)

/** The infinite canvas: screens with their parts, pan and zoom, selection and dragging. */
@Composable
fun CanvasView(state: EditorState, modifier: Modifier = Modifier) {
    val density = LocalDensity.current.density
    state.density = density
    val chrome = COUITheme.colorScheme
    Box(
        modifier
            .fillMaxSize()
            .clipToBounds()
            .background(if (chrome.isDark) Color(0xFF161616) else Color(0xFFEDEEF2))
            .onGloballyPositioned {
                state.canvasOrigin = it.positionInWindow()
                state.viewport = it.size
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Scroll) {
                            val change = event.changes.first()
                            val delta = change.scrollDelta
                            if (event.keyboardModifiers.isCtrlPressed || event.keyboardModifiers.isMetaPressed) {
                                state.zoomAt(exp(-delta.y * 0.08f), change.position)
                            } else {
                                state.pan -= Offset(delta.x, delta.y) * 40f
                            }
                            event.changes.fastForEach { it.consume() }
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    state.selectItem(null)
                    state.selectFrame(null)
                    state.requestFocus()
                })
            }
            .pointerInput(Unit) {
                detectDragGestures { change, drag ->
                    change.consume()
                    state.pan += drag
                }
            },
    ) {
        World(state)
        ZoomControls(state, Modifier.align(Alignment.BottomEnd).padding(16.dp))
    }
}

@Composable
private fun World(state: EditorState) {
    val doc = state.doc
    val world = doc.worldBox(state::boxOf)
    // Everything is drawn relative to an origin left of and above the world, so no child sits at a
    // negative position (hit testing and sizing stay simple).
    val ox = world.x - 2000f
    val oy = world.y - 2000f
    Box(
        Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(Constraints())
                layout(constraints.maxWidth, constraints.maxHeight) {
                    placeable.placeWithLayer(0, 0) {
                        val s = state.density * state.zoom
                        translationX = state.pan.x + ox * s
                        translationY = state.pan.y + oy * s
                        scaleX = state.zoom
                        scaleY = state.zoom
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                }
            }
            .requiredSize((world.w + 4000f).dp, (world.h + 4000f).dp),
    ) {
        doc.frames.forEach { frame ->
            FrameLabel(state, frame, ox, oy)
            FrameBody(state, frame, ox, oy)
            if (state.selectedFrame == frame.id) FrameOutline(state, frame, ox, oy)
        }
        // Loose parts that sit on no screen.
        doc.groups.filter { doc.frameOf(it) == null }.forEach { g ->
            DesignTheme(doc.theme) { GroupHost(state, g, null, g.x - ox, g.y - oy) }
        }
        SelectionOverlay(state, ox, oy)
        GuidesOverlay(state, ox, oy, world)
    }
}

/** The screen name above a screen: drag it to move the screen, tap it to select the screen. */
@Composable
private fun FrameLabel(state: EditorState, frame: Frame, ox: Float, oy: Float) {
    val selected = state.selectedFrame == frame.id
    val labelColor = if (selected) SelectionBlue else COUITheme.colorScheme.labelSecondary
    Text(
        frame.name.ifEmpty { "—" } + if (frame.isWide) "  ·  ${frame.w.roundToInt()}×${frame.h.roundToInt()}" else "",
        modifier = Modifier
            .offset((frame.x - ox).dp, (frame.y - oy - 28f).dp)
            .pointerInput(frame.id) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    down.consume()
                    state.selectFrame(frame.id)
                    state.requestFocus()
                    state.beginGesture()
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (change.changedToUp()) break
                        val d = change.positionChange()
                        if (d != Offset.Zero) {
                            change.consume()
                            state.live(state.doc.moveFrame(frame.id, d.x / state.density, d.y / state.density))
                        }
                    }
                    state.endGesture()
                }
            },
        style = COUITheme.textStyles.bodyM,
        fontSize = 14.sp,
        color = labelColor,
    )
}

/** A screen with its parts, in the design's theme. */
@Composable
private fun FrameBody(state: EditorState, frame: Frame, ox: Float, oy: Float) {
    val doc = state.doc
    DesignTheme(doc.theme) {
        val colors = COUITheme.colorScheme
        val bg = if (frame.bg == "card") colors.card else colors.backgroundWithCard
        Box(
            Modifier
                .offset((frame.x - ox).dp, (frame.y - oy).dp)
                .requiredSize(frame.w.dp, frame.drawnHeight.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(bg)
                .pointerInput(frame.id) {
                    detectTapGestures {
                        state.selectFrame(frame.id)
                        state.requestFocus()
                    }
                },
        ) {
            val on = doc.groupsOn(frame)
            val firstOverlay = on.indexOfFirst { it.kind?.isOverlay == true }
            on.forEachIndexed { index, g ->
                if (index == firstOverlay) Scrim(frame)
                GroupHost(state, g, frame, g.x - frame.x, g.y - frame.y)
            }
            StatusBar(Modifier.fillMaxWidth())
            if ((frame.length ?: 0f) > frame.h) FoldLine(frame)
        }
    }
}

/** The selection outline around a screen. */
@Composable
private fun FrameOutline(state: EditorState, frame: Frame, ox: Float, oy: Float) {
    Box(
        Modifier
            .offset((frame.x - ox - 3f).dp, (frame.y - oy - 3f).dp)
            .requiredSize((frame.w + 6f).dp, (frame.drawnHeight + 6f).dp)
            .border((2f / state.zoom).coerceAtLeast(1f).dp, SelectionBlue, RoundedCornerShape(23.dp)),
    )
}

@Composable
private fun Scrim(frame: Frame) {
    Box(Modifier.requiredSize(frame.w.dp, frame.drawnHeight.dp).background(COUITheme.colorScheme.mask))
}

@Composable
private fun FoldLine(frame: Frame) {
    Canvas(Modifier.offset(0.dp, frame.h.dp).requiredSize(frame.w.dp, 1.dp)) {
        drawLine(SelectionBlue.copy(alpha = 0.7f), Offset.Zero, Offset(size.width, 0f), strokeWidth = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f)))
    }
}

/** The phone status bar drawn over every screen's top 32dp. */
@Composable
fun StatusBar(modifier: Modifier = Modifier) {
    val color = COUITheme.colorScheme.labelPrimary
    Row(
        modifier.height(STATUS_BAR_H.dp).padding(horizontal = 26.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("9:41", fontSize = 13.sp, color = color, style = COUITheme.textStyles.bodyS)
        Canvas(Modifier.size(width = 52.dp, height = 12.dp)) {
            // Signal bars, then a battery.
            val bar = 3.dp.toPx()
            for (i in 0 until 4) {
                val h = size.height * (0.4f + 0.2f * i)
                drawRoundRect(color, Offset(i * (bar + 2f), size.height - h), Size(bar, h), CornerRadius(1f))
            }
            val bx = size.width - 24.dp.toPx()
            drawRoundRect(color, Offset(bx, 0f), Size(21.dp.toPx(), size.height), CornerRadius(3.dp.toPx()), style = Stroke(1.2f.dp.toPx()))
            drawRoundRect(color, Offset(bx + 2.dp.toPx(), 2.dp.toPx()), Size(14.dp.toPx(), size.height - 4.dp.toPx()), CornerRadius(1.5f.dp.toPx()))
        }
    }
}

/** A group on the canvas: the real COUI part, measured, under an overlay that takes the pointer. */
@Composable
private fun GroupHost(state: EditorState, group: Group, frame: Frame?, x: Float, y: Float) {
    val ctx = remember { PartContext(live = false) }
    var groupCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    Box(
        Modifier
            .offset(x.dp, y.dp)
            .wrapContentSize(Alignment.TopStart, unbounded = true)
            .onSizeChanged {
                val size = it.width / state.density to it.height / state.density
                if (state.measured[group.id] != size) state.measured[group.id] = size
            },
    ) {
        Box(Modifier.onGloballyPositioned { groupCoords = it }) {
            GroupView(group, frame, ctx, onItemPlaced = { id, coords ->
                val gc = groupCoords
                if (gc != null && gc.isAttached && coords.isAttached) {
                    val p = gc.localPositionOf(coords, Offset.Zero)
                    val r = Rect(p.x / state.density, p.y / state.density, (p.x + coords.size.width) / state.density, (p.y + coords.size.height) / state.density)
                    if (state.itemRects[id] != r) state.itemRects[id] = r
                }
            })
        }
        Box(
            Modifier
                .matchParentSize()
                .pointerInput(group.id) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        down.consume()
                        val current = state.doc.groups.firstOrNull { it.id == group.id } ?: return@awaitEachGesture
                        val p = down.position / state.density
                        val hit = current.items.firstOrNull { state.itemRects[it.id]?.contains(p) == true } ?: current.items.first()
                        state.selectItem(hit.id)
                        state.requestFocus()
                        if (current.locked) return@awaitEachGesture
                        var rawX = current.x
                        var rawY = current.y
                        var travelled = 0f
                        var dragging = false
                        state.beginGesture()
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (change.changedToUp()) break
                            val d = change.positionChange()
                            travelled += d.getDistance()
                            rawX += d.x / state.density
                            rawY += d.y / state.density
                            if (!dragging && travelled > viewConfiguration.touchSlop) dragging = true
                            if (dragging) {
                                change.consume()
                                val g = state.doc.groups.firstOrNull { it.id == group.id } ?: break
                                val (sx, sy) = state.snapDrag(g, rawX, rawY, free = event.keyboardModifiers.isCtrlPressed || event.keyboardModifiers.isMetaPressed)
                                state.live(state.doc.updateGroup(group.id) { it.copy(x = sx, y = sy) })
                            }
                        }
                        if (dragging) {
                            state.guides = emptyList()
                            val fused = state.doc.fuse(group.id, state::boxOf)
                            if (fused != state.doc) {
                                state.live(fused)
                                state.measured.remove(group.id)
                            }
                        }
                        state.endGesture()
                    }
                },
        )
    }
}

@Composable
private fun SelectionOverlay(state: EditorState, ox: Float, oy: Float) {
    val group = state.selectedGroup ?: return
    val box = state.boxOf(group)
    val stroke = (2f / state.zoom).coerceAtLeast(1f)
    val itemRect = if (group.items.size > 1) state.itemRects[state.selectedItem] else null
    Box(
        Modifier
            .offset((box.x - ox - 2f).dp, (box.y - oy - 2f).dp)
            .requiredSize((box.w + 4f).dp, (box.h + 4f).dp)
            .border(stroke.dp, SelectionBlue, RoundedCornerShape(6.dp)),
    ) {
        // The selected item of a connected run.
        if (itemRect != null) {
            Box(
                Modifier
                    .offset((itemRect.left + 2f).dp, (itemRect.top + 2f).dp)
                    .requiredSize(itemRect.width.dp, itemRect.height.dp)
                    .background(SelectionBlue.copy(alpha = 0.08f))
                    .border((stroke / 2f).coerceAtLeast(1f).dp, SelectionBlue.copy(alpha = 0.7f)),
            )
        }
    }
}

@Composable
private fun GuidesOverlay(state: EditorState, ox: Float, oy: Float, world: DpBox) {
    if (state.guides.isEmpty()) return
    val stroke = (1.5f / state.zoom).coerceAtLeast(1f)
    Canvas(Modifier.requiredSize((world.w + 4000f).dp, (world.h + 4000f).dp)) {
        state.guides.forEach { g ->
            if (g.vertical) {
                val x = (g.at - ox).dp.toPx()
                drawLine(GuidePink, Offset(x, 0f), Offset(x, size.height), strokeWidth = stroke.dp.toPx())
            } else {
                val y = (g.at - oy).dp.toPx()
                drawLine(GuidePink, Offset(0f, y), Offset(size.width, y), strokeWidth = stroke.dp.toPx())
            }
        }
    }
}

@Composable
private fun ZoomControls(state: EditorState, modifier: Modifier = Modifier) {
    Card(modifier = modifier, cornerRadius = 20.dp) {
        Row(Modifier.padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { state.zoomBy(1f / 1.25f) }) {
                Text("−", fontSize = 22.sp, color = COUITheme.colorScheme.labelPrimary)
            }
            Text(
                "${(state.zoom * 100).roundToInt()}%",
                modifier = Modifier.width(52.dp),
                style = COUITheme.textStyles.bodyS,
                color = COUITheme.colorScheme.labelSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            IconButton(onClick = { state.zoomBy(1.25f) }) { Icon(COUIIcons.Add, contentDescription = tr("Zoom in", "放大")) }
            Spacer(Modifier.width(2.dp))
            IconButton(onClick = { state.fit() }) { Icon(COUIIcons.ZoomOut, contentDescription = tr("Fit", "适应窗口")) }
        }
    }
}
