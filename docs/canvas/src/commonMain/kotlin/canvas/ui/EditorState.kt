// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import canvas.model.Action
import canvas.model.Box
import canvas.model.CanvasDoc
import canvas.model.Family
import canvas.model.Frame
import canvas.model.Group
import canvas.model.Item
import canvas.model.Kind
import canvas.model.MARGIN
import canvas.model.Pin
import canvas.model.STATUS_BAR_H
import canvas.model.estimatedBox
import canvas.model.frame
import canvas.model.frameOf
import canvas.model.group
import canvas.model.groupOfItem
import canvas.model.groupsOn
import canvas.model.newId
import canvas.model.newIds
import canvas.model.nextFramePosition
import canvas.model.removeFrame
import canvas.model.removeItem
import canvas.model.snap
import canvas.model.tidy
import canvas.model.toJson
import canvas.model.worldBox
import kotlin.math.max
import kotlin.math.min

enum class Tool { Select, Hand }

enum class Panel { Prompt, Code, Share }

/** A snapping guide line in canvas dp: vertical when [vertical], at [at]. */
data class Guide(val vertical: Boolean, val at: Float)

/** Everything the editor knows: the document with its history, the selection and the view. */
@Stable
class EditorState(
    val platform: CanvasPlatform,
    initial: CanvasDoc,
    zh: Boolean,
) {
    var doc: CanvasDoc by mutableStateOf(initial)
        private set

    private val undoStack = ArrayDeque<CanvasDoc>()
    private val redoStack = ArrayDeque<CanvasDoc>()
    private var historyVersion by mutableIntStateOf(0)
    private var lastKey: String? = null
    private var gestureStart: CanvasDoc? = null

    val canUndo: Boolean get() = historyVersion >= 0 && undoStack.isNotEmpty()
    val canRedo: Boolean get() = historyVersion >= 0 && redoStack.isNotEmpty()

    var zh: Boolean by mutableStateOf(zh)

    /** The selected item (and with it its group), or null. */
    var selectedItem: String? by mutableStateOf(null)
        private set

    /** The selected frame when no item is selected. */
    var selectedFrame: String? by mutableStateOf(null)
        private set

    var tool: Tool by mutableStateOf(Tool.Select)
    var panel: Panel? by mutableStateOf(null)

    /** The frame the preview shows, or null when not previewing. */
    var previewFrame: String? by mutableStateOf(null)

    var message: String? by mutableStateOf(null)

    // View: canvas dp → screen px is `pan + world * density * zoom`.
    var zoom: Float by mutableFloatStateOf(1f)
    var pan: Offset by mutableStateOf(Offset(80f, 80f))
    var density: Float = 1f
    var viewport: IntSize by mutableStateOf(IntSize.Zero)

    /** The canvas' top-left in window px, for drops from the palette. */
    var canvasOrigin: Offset = Offset.Zero

    /** Measured group sizes (dp). */
    val measured = mutableStateMapOf<String, Pair<Float, Float>>()

    /** Item rectangles relative to their group (dp). */
    val itemRects = mutableStateMapOf<String, Rect>()

    var guides: List<Guide> by mutableStateOf(emptyList())

    /** A part being dragged in from the palette: its kind and the pointer in window px. */
    var paletteDrag: Pair<Kind, Offset>? by mutableStateOf(null)

    /** Gives the keyboard back to the editor (after a text field had it). */
    var requestFocus: () -> Unit = {}

    /** Set once the first layout has been measured, so the view can be fitted. */
    var fitted = false

    val selectedGroup: Group? get() = doc.groupOfItem(selectedItem)

    fun boxOf(group: Group): Box = measured[group.id]?.let { (w, h) -> Box(group.x, group.y, w, h) } ?: doc.estimatedBox(group)

    // ---------------------------------------------------------------------------------------------
    // History

    /** Replaces the document and records the old one for undo. Edits with the same [key] in a row merge into one step. */
    fun commit(next: CanvasDoc, key: String? = null) {
        if (next == doc) return
        if (key == null || key != lastKey) {
            undoStack.addLast(doc)
            if (undoStack.size > 200) undoStack.removeFirst()
        }
        redoStack.clear()
        lastKey = key
        doc = next
        historyVersion++
        persist()
    }

    /** Starts a gesture (a drag): live updates follow through [live] and land as one step on [endGesture]. */
    fun beginGesture() {
        gestureStart = doc
    }

    fun live(next: CanvasDoc) {
        doc = next
    }

    fun endGesture() {
        val start = gestureStart ?: return
        gestureStart = null
        if (start != doc) {
            undoStack.addLast(start)
            redoStack.clear()
            lastKey = null
            historyVersion++
            persist()
        }
    }

    fun undo() {
        val prev = undoStack.removeLastOrNull() ?: return
        redoStack.addLast(doc)
        doc = prev
        lastKey = null
        historyVersion++
        fixSelection()
        persist()
    }

    fun redo() {
        val next = redoStack.removeLastOrNull() ?: return
        undoStack.addLast(doc)
        doc = next
        lastKey = null
        historyVersion++
        fixSelection()
        persist()
    }

    /** Opens another document (a file, a link) as a fresh start that can still be undone. */
    fun replace(next: CanvasDoc) {
        commit(next)
        lastKey = null
        selectedItem = null
        selectedFrame = null
        measured.clear()
        itemRects.clear()
        fitted = false
    }

    private fun fixSelection() {
        if (selectedItem != null && doc.groupOfItem(selectedItem) == null) selectedItem = null
        if (selectedFrame != null && doc.frame(selectedFrame) == null) selectedFrame = null
    }

    fun persist() {
        platform.save(STORE_KEY, doc.toJson())
    }

    // ---------------------------------------------------------------------------------------------
    // Selection

    fun selectItem(id: String?) {
        selectedItem = id
        selectedFrame = null
    }

    fun selectFrame(id: String?) {
        selectedFrame = id
        selectedItem = null
    }

    /** The frame new parts go to: the selected one, the one under the selection, or the first. */
    val activeFrame: Frame?
        get() = doc.frame(selectedFrame) ?: selectedGroup?.let { doc.frameOf(it) } ?: doc.frames.firstOrNull()

    // ---------------------------------------------------------------------------------------------
    // View

    fun worldToScreen(x: Float, y: Float): Offset = Offset(pan.x + x * density * zoom, pan.y + y * density * zoom)

    fun screenToWorld(p: Offset): Offset = Offset((p.x - pan.x) / (density * zoom), (p.y - pan.y) / (density * zoom))

    fun zoomAt(factor: Float, anchor: Offset) {
        val next = (zoom * factor).coerceIn(0.1f, 4f)
        val world = screenToWorld(anchor)
        zoom = next
        pan = Offset(anchor.x - world.x * density * next, anchor.y - world.y * density * next)
    }

    fun zoomBy(factor: Float) = zoomAt(factor, Offset(viewport.width / 2f, viewport.height / 2f))

    /** Fits everything (or [box]) into the viewport. */
    fun fit(box: Box? = null) {
        if (viewport.width == 0 || viewport.height == 0) return
        val b = box ?: doc.worldBox(::boxOf)
        val pad = 48f * density
        val zx = (viewport.width - 2 * pad) / (b.w * density)
        val zy = (viewport.height - 2 * pad) / (b.h * density)
        zoom = min(zx, zy).coerceIn(0.1f, 1.5f)
        val cx = b.cx * density * zoom
        val cy = b.cy * density * zoom
        pan = Offset(viewport.width / 2f - cx, viewport.height / 2f - cy)
    }

    fun focusFrame(id: String) {
        val f = doc.frame(id) ?: return
        fit(Box(f.x, f.y, f.w, f.drawnHeight))
    }

    // ---------------------------------------------------------------------------------------------
    // Editing

    fun addFrame(wide: Boolean = false) {
        val (x, y) = doc.nextFramePosition()
        val id = doc.newId("screen")
        val n = doc.frames.size + 1
        val frame = Frame(
            id = id,
            name = tr(zh, "Screen $n", "屏幕 $n"),
            x = x,
            y = y,
            w = if (wide) Frame.WIDE_W else Frame.PHONE_W,
            h = if (wide) Frame.WIDE_H else Frame.PHONE_H,
        )
        commit(doc.copy(frames = doc.frames + frame))
        selectFrame(id)
    }

    /**
     * Adds a part of [kind]. With [at] (canvas dp) it lands there; otherwise on the active frame —
     * bars at their edge, rows appended to the selected or the last card run, the rest below
     * whatever is there.
     */
    fun addPart(kind: Kind, at: Offset? = null) {
        var d = doc
        val itemId = d.newId(kind.key)
        val item = defaultItem(kind, itemId, zh)
        val frame = if (at != null) d.frames.firstOrNull { Box(it.x, it.y, it.w, it.drawnHeight).contains(at.x, at.y) } else activeFrame
        // A row lands in the selected (or the last) card run of its screen.
        if (at == null && kind.family == Family.Row && frame != null) {
            val run = selectedGroup?.takeIf { it.kind?.family == Family.Row && d.frameOf(it)?.id == frame.id }
                ?: d.groupsOn(frame).lastOrNull { it.kind?.family == Family.Row }
            if (run != null) {
                val index = run.items.indexOfFirst { it.id == selectedItem }.let { if (it < 0) run.items.size else it + 1 }
                val items = run.items.toMutableList().apply { add(index, item) }
                commit(d.copy(groups = d.groups.map { if (it.id == run.id) it.copy(items = items) else it }))
                selectItem(itemId)
                return
            }
        }
        val groupId = d.newId("g")
        val estimate = d.copy(groups = d.groups + Group(groupId, 0f, 0f, if (kind.family == Family.Row) Group.AXIS_Y else Group.AXIS_X, listOf(item))).let { it.estimatedBox(it.groups.last()) }
        val (x, y) = when {
            at != null -> snap(at.x - (if (kind.fullWidth && frame != null) 0f else estimate.w / 2f)) to snap(at.y - estimate.h / 2f)
            frame == null -> 0f to 0f
            else -> placeOn(frame, kind, estimate)
        }
        val fx = if (at != null && frame != null && (kind.fullWidth || kind == Kind.Category)) frame.x else x
        d = d.copy(groups = d.groups + Group(groupId, fx, y, if (kind.family == Family.Row) Group.AXIS_Y else Group.AXIS_X, listOf(item)))
        commit(d)
        selectItem(itemId)
    }

    private fun placeOn(frame: Frame, kind: Kind, size: Box): Pair<Float, Float> {
        val on = doc.groupsOn(frame)
        val bottom = frame.y + frame.h
        val navTop = on.filter { it.kind == Kind.BottomNav }.minOfOrNull { boxOf(it).y } ?: bottom
        return when (kind.pin) {
            Pin.Top -> frame.x to frame.y

            Pin.Bottom -> if (kind == Kind.Snackbar) frame.x + (frame.w - size.w) / 2f to navTop - MARGIN - size.h else frame.x to bottom - size.h

            Pin.Fab -> frame.x + frame.w - MARGIN - 8f - size.w to navTop - MARGIN - size.h

            Pin.Center -> frame.x + (frame.w - size.w) / 2f to (if (frame.isWide) frame.y + (frame.h - size.h) / 2f else bottom - 32f - size.h)

            Pin.None -> {
                val body = on.filter { it.kind?.pin == Pin.None || it.kind?.pin == Pin.Top }
                val lowest = body.maxOfOrNull { boxOf(it).bottom } ?: (frame.y + STATUS_BAR_H)
                val gap = if (body.lastOrNull()?.kind == Kind.Category || body.isEmpty()) 0f else MARGIN
                val x = if (kind.fullWidth || kind == Kind.Category) frame.x else frame.x + MARGIN
                x to snap(lowest + gap)
            }
        }
    }

    fun deleteSelection() {
        val item = selectedItem
        val frame = selectedFrame
        when {
            item != null -> {
                commit(doc.removeItem(item))
                selectedItem = null
            }

            frame != null -> {
                commit(doc.removeFrame(frame))
                selectedFrame = null
            }
        }
    }

    /** Duplicates the selected group (or frame with its parts) next to it. */
    fun duplicateSelection() {
        val g = selectedGroup
        val f = doc.frame(selectedFrame)
        if (g != null) {
            val ids = doc.newIds("i", g.items.size + 1).iterator()
            val items = g.items.map { it.copy(id = ids.next()) }
            val copyGroup = g.copy(id = ids.next(), items = items, y = boxOf(g).bottom + MARGIN)
            commit(doc.copy(groups = doc.groups + copyGroup))
            selectItem(items.first().id)
        } else if (f != null) {
            val (x, y) = doc.nextFramePosition()
            val on = doc.groupsOn(f)
            val ids = doc.newIds("i", 1 + on.size + on.sumOf { it.items.size }).iterator()
            val newFrameId = ids.next()
            val copies = on.map { g0 -> g0.copy(id = ids.next(), items = g0.items.map { it.copy(id = ids.next()) }, x = g0.x - f.x + x, y = g0.y - f.y + y) }
            commit(doc.copy(frames = doc.frames + f.copy(id = newFrameId, name = f.name + tr(zh, " copy", " 副本"), x = x, y = y), groups = doc.groups + copies))
            selectFrame(newFrameId)
        }
    }

    fun nudge(dx: Float, dy: Float) {
        val g = selectedGroup
        if (g != null) {
            commit(doc.copy(groups = doc.groups.map { if (it.id == g.id) it.copy(x = it.x + dx, y = it.y + dy) else it }), key = "nudge:" + g.id)
        }
    }

    fun tidy() {
        val frames = doc.frame(selectedFrame)?.let { listOf(it) } ?: selectedGroup?.let { doc.frameOf(it) }?.let { listOf(it) } ?: doc.frames
        var d = doc
        frames.forEach { f -> d = d.tidy(f.id) { g -> measured[g.id]?.let { (w, h) -> Box(g.x, g.y, w, h) } ?: d.estimatedBox(g) } }
        commit(d)
    }

    /** Moves the selected group one layer up (+1) or down (-1), or to the very top / bottom. */
    fun reorder(delta: Int) {
        val g = selectedGroup ?: return
        val list = doc.groups.toMutableList()
        val i = list.indexOfFirst { it.id == g.id }
        val j = when (delta) {
            Int.MAX_VALUE -> list.lastIndex
            Int.MIN_VALUE -> 0
            else -> (i + delta).coerceIn(0, list.lastIndex)
        }
        if (i == j) return
        list.removeAt(i)
        list.add(j, g)
        commit(doc.copy(groups = list))
    }

    fun setAction(itemId: String, target: String, action: Action?) {
        commit(
            doc.copy(
                groups = doc.groups.map { g ->
                    g.copy(
                        items = g.items.map { item ->
                            if (item.id != itemId) {
                                item
                            } else if (target.isEmpty()) {
                                item.copy(action = action)
                            } else {
                                val map = item.actions.orEmpty().toMutableMap()
                                if (action == null) map.remove(target) else map[target] = action
                                item.copy(actions = map.takeIf { it.isNotEmpty() })
                            }
                        },
                    )
                },
            ),
        )
    }

    fun groupBounds(): Rect? {
        val g = selectedGroup ?: return null
        val b = boxOf(g)
        return Rect(b.x, b.y, b.right, b.bottom)
    }

    /** Where a group goes while dragged: snapped to the grid, the screen margins and its neighbours. */
    fun snapDrag(group: Group, rawX: Float, rawY: Float, free: Boolean): Pair<Float, Float> {
        if (free) {
            guides = emptyList()
            return rawX to rawY
        }
        val size = boxOf(group)
        val probe = doc.copy(groups = doc.groups.map { if (it.id == group.id) it.copy(x = rawX, y = rawY) else it })
        val frame = probe.frameOf(probe.group(group.id)!!) ?: doc.frameOf(group)
        val threshold = 6f / max(zoom, 0.25f)
        val xs = mutableListOf<Pair<Float, Float>>() // candidate left edge to guide position
        val ys = mutableListOf<Pair<Float, Float>>()
        if (frame != null) {
            xs += frame.x to frame.x
            xs += frame.x + MARGIN to frame.x + MARGIN
            xs += frame.x + frame.w - MARGIN - size.w to frame.x + frame.w - MARGIN
            xs += frame.x + frame.w - size.w to frame.x + frame.w
            xs += frame.x + (frame.w - size.w) / 2f to frame.x + frame.w / 2f
            ys += frame.y to frame.y
            ys += frame.y + frame.h - size.h to frame.y + frame.h
            doc.groupsOn(frame).filter { it.id != group.id }.forEach { o ->
                val b = boxOf(o)
                xs += b.x to b.x
                xs += b.right - size.w to b.right
                ys += b.bottom to b.bottom
                ys += b.bottom + MARGIN to b.bottom + MARGIN
                ys += b.y to b.y
                ys += b.y - size.h to b.y
                ys += b.y - MARGIN - size.h to b.y - MARGIN
            }
        }
        val bx = xs.minByOrNull { kotlin.math.abs(it.first - rawX) }?.takeIf { kotlin.math.abs(it.first - rawX) <= threshold }
        val by = ys.minByOrNull { kotlin.math.abs(it.first - rawY) }?.takeIf { kotlin.math.abs(it.first - rawY) <= threshold }
        guides = listOfNotNull(bx?.let { Guide(true, it.second) }, by?.let { Guide(false, it.second) })
        return (bx?.first ?: snap(rawX)) to (by?.first ?: snap(rawY))
    }

    companion object {
        const val STORE_KEY = "coui-canvas-doc"
        const val LANG_KEY = "coui-canvas-lang"
    }
}
