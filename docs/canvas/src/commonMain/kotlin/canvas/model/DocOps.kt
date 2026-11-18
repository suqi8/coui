// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.model

import kotlinx.serialization.json.Json
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** A dp rectangle in canvas coordinates. */
data class Box(val x: Float, val y: Float, val w: Float, val h: Float) {
    val right: Float get() = x + w
    val bottom: Float get() = y + h
    val cx: Float get() = x + w / 2f
    val cy: Float get() = y + h / 2f

    fun contains(px: Float, py: Float): Boolean = px >= x && px < right && py >= y && py < bottom

    fun intersects(o: Box): Boolean = x < o.right && o.x < right && y < o.bottom && o.y < bottom

    /** Whether [o] lies inside this box, allowing [slack] dp. */
    fun encloses(o: Box, slack: Float = 1f): Boolean = o.x >= x - slack && o.y >= y - slack && o.right <= right + slack && o.bottom <= bottom + slack
}

/** The JSON settings shared by files, links and the browser store. */
val CanvasJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = false
    isLenient = true
    coerceInputValues = true
}

/** Pretty JSON for saved files. */
val CanvasJsonPretty: Json = Json(CanvasJson) { prettyPrint = true }

/** The side margin of screen content: COUI cards sit 16dp from the screen edges. */
const val MARGIN = 16f

/** The status bar every phone frame draws above its content. */
const val STATUS_BAR_H = 32f

/** The width of the content between the side margins of [frame]. */
fun contentWidth(frame: Frame): Float = frame.w - 2 * MARGIN

/** The screen a group sits on: the frame that contains its top-left corner, or null for loose parts. */
fun CanvasDoc.frameOf(group: Group): Frame? = frames.firstOrNull { f -> Box(f.x, f.y, f.w, f.drawnHeight).contains(group.x + 1f, group.y + 1f) }

fun CanvasDoc.frame(id: String?): Frame? = id?.let { frames.firstOrNull { f -> f.id == it } }

fun CanvasDoc.group(id: String?): Group? = id?.let { groups.firstOrNull { g -> g.id == it } }

fun CanvasDoc.groupOfItem(itemId: String?): Group? = itemId?.let { groups.firstOrNull { g -> g.items.any { it.id == itemId } } }

fun CanvasDoc.item(id: String?): Item? = id?.let { groups.firstNotNullOfOrNull { g -> g.items.firstOrNull { it.id == id } } }

/** Groups on [frame], bottom layer first. */
fun CanvasDoc.groupsOn(frame: Frame): List<Group> = groups.filter { frameOf(it)?.id == frame.id }

/** The width a single item takes on [frame] before it is measured. */
fun itemWidth(item: Item, frame: Frame?): Float {
    val kind = Kind.of(item.kind)
    item.size?.let { return it }
    val screenW = frame?.w ?: Frame.PHONE_W
    return when {
        kind == null -> screenW - 2 * MARGIN
        kind.defaultW == Kind.FULL -> screenW
        kind.defaultW != null -> kind.defaultW
        kind == Kind.Button -> 96f
        else -> screenW - 2 * MARGIN
    }
}

/** The height a single item takes before it is measured. */
fun itemHeight(item: Item): Float = item.size2?.takeIf { Kind.of(item.kind)?.let { k -> Field.Size2 in k.fields } == true } ?: (Kind.of(item.kind)?.defaultH ?: 48f)

/** A group's box before measurement: the canvas replaces it with the measured box once drawn. */
fun CanvasDoc.estimatedBox(group: Group): Box {
    val frame = frameOf(group)
    return if (group.axis == Group.AXIS_X) {
        val w = group.items.sumOf { itemWidth(it, frame).toDouble() }.toFloat() + BUTTON_GAP * (group.items.size - 1)
        Box(group.x, group.y, w, group.items.maxOf { itemHeight(it) })
    } else {
        Box(group.x, group.y, group.items.maxOf { itemWidth(it, frame) }, group.items.sumOf { itemHeight(it).toDouble() }.toFloat())
    }
}

/** The gap between buttons or chips of one connected run. */
const val BUTTON_GAP = 12f

/** A fresh id that no frame, group or item of the document uses yet. */
fun CanvasDoc.newId(prefix: String): String {
    val used = HashSet<String>()
    frames.forEach { used += it.id }
    groups.forEach { g ->
        used += g.id
        g.items.forEach { used += it.id }
    }
    var n = used.size + 1
    while ("$prefix$n" in used) n++
    return "$prefix$n"
}

/** Several fresh ids at once. */
fun CanvasDoc.newIds(prefix: String, count: Int): List<String> {
    val out = ArrayList<String>(count)
    var doc = this
    repeat(count) {
        val id = doc.newId(prefix)
        out += id
        doc = doc.copy(frames = doc.frames + Frame(id = id, name = ""))
    }
    return out
}

fun CanvasDoc.updateGroup(id: String, transform: (Group) -> Group): CanvasDoc = copy(groups = groups.map { if (it.id == id) transform(it) else it })

fun CanvasDoc.updateItem(id: String, transform: (Item) -> Item): CanvasDoc = copy(
    groups = groups.map { g -> if (g.items.any { it.id == id }) g.copy(items = g.items.map { if (it.id == id) transform(it) else it }) else g },
)

fun CanvasDoc.updateFrame(id: String, transform: (Frame) -> Frame): CanvasDoc = copy(frames = frames.map { if (it.id == id) transform(it) else it })

/** Removes an item; its group goes when it was the last one. Actions that pointed nowhere stay (the problems list reports them). */
fun CanvasDoc.removeItem(id: String): CanvasDoc = copy(
    groups = groups.mapNotNull { g ->
        if (g.items.none { it.id == id }) {
            g
        } else {
            val rest = g.items.filterNot { it.id == id }
            if (rest.isEmpty()) null else g.copy(items = rest)
        }
    },
)

/** Removes a screen with everything on it, and turns actions that opened it into nothing. */
fun CanvasDoc.removeFrame(id: String): CanvasDoc {
    val frame = frame(id) ?: return this
    val gone = groupsOn(frame).map { it.id }.toSet()
    return copy(frames = frames.filterNot { it.id == id }, groups = groups.filterNot { it.id in gone }).dropActionsTo(id)
}

private fun CanvasDoc.dropActionsTo(frameId: String): CanvasDoc = copy(
    frames = frames.map { f -> f.copy(swipe = f.swipe?.filterValues { it != frameId }?.takeIf { it.isNotEmpty() }) },
    groups = groups.map { g ->
        g.copy(
            items = g.items.map { item ->
                item.copy(
                    action = item.action?.takeIf { it.to != frameId },
                    actions = item.actions?.filterValues { it.to != frameId }?.takeIf { it.isNotEmpty() },
                )
            },
        )
    },
)

/** Moves a screen and everything on it. */
fun CanvasDoc.moveFrame(id: String, dx: Float, dy: Float): CanvasDoc {
    val frame = frame(id) ?: return this
    val on = groupsOn(frame).map { it.id }.toSet()
    return copy(
        frames = frames.map { if (it.id == id) it.copy(x = it.x + dx, y = it.y + dy) else it },
        groups = groups.map { if (it.id in on) it.copy(x = it.x + dx, y = it.y + dy) else it },
    )
}

/** Where the next screen goes: right of the rightmost one. */
fun CanvasDoc.nextFramePosition(): Pair<Float, Float> {
    if (frames.isEmpty()) return 0f to 0f
    val right = frames.maxOf { it.x + it.w }
    return right + Frame.GAP to frames.minOf { it.y }
}

/** Takes an item out of its connected run into a group of its own, placed just beside the run. */
fun CanvasDoc.detachItem(itemId: String, newGroupId: String, box: Box): CanvasDoc {
    val group = groupOfItem(itemId) ?: return this
    if (group.items.size < 2) return this
    val item = group.items.first { it.id == itemId }
    val (nx, ny) = if (group.axis == Group.AXIS_Y) group.x to box.bottom + MARGIN else box.right + MARGIN to group.y
    val rest = group.copy(items = group.items.filterNot { it.id == itemId })
    val index = groups.indexOfFirst { it.id == group.id }
    val list = groups.toMutableList()
    list[index] = rest
    list.add(index + 1, Group(id = newGroupId, x = nx, y = ny, axis = group.axis, items = listOf(item)))
    return copy(groups = list)
}

/**
 * Fuses the group [movedId] into a neighbour of the same family when their edges touch: a row run
 * dropped right under (or above) another row run joins it, a button or chip dropped right after
 * (or before) another joins its run. Returns the document unchanged when nothing is close enough.
 */
fun CanvasDoc.fuse(movedId: String, boxOf: (Group) -> Box, threshold: Float = 12f): CanvasDoc {
    val moved = group(movedId) ?: return this
    val family = moved.kind?.family ?: return this
    if (family == Family.None) return this
    val frame = frameOf(moved)
    val mb = boxOf(moved)
    for (other in groups) {
        if (other.id == moved.id || other.kind?.family != family || frameOf(other)?.id != frame?.id) continue
        val ob = boxOf(other)
        if (family == Family.Row) {
            val aligned = abs(mb.x - ob.x) <= 24f
            if (aligned && abs(mb.y - ob.bottom) <= threshold) return merge(other, moved, append = true)
            if (aligned && abs(mb.bottom - ob.y) <= threshold) return merge(other, moved, append = false)
        } else {
            val aligned = abs(mb.cy - ob.cy) <= 16f
            if (aligned && abs(mb.x - ob.right) <= threshold + BUTTON_GAP) return merge(other, moved, append = true)
            if (aligned && abs(mb.right - ob.x) <= threshold + BUTTON_GAP) return merge(other, moved, append = false)
        }
    }
    return this
}

private fun CanvasDoc.merge(target: Group, moved: Group, append: Boolean): CanvasDoc {
    val items = if (append) target.items + moved.items else moved.items + target.items
    val x = if (append) target.x else min(target.x, moved.x)
    val y = if (append) target.y else moved.y
    val merged = target.copy(items = items, x = if (target.axis == Group.AXIS_Y) target.x else x, y = if (target.axis == Group.AXIS_Y) y else target.y)
    return copy(
        groups = groups.mapNotNull {
            if (it.id == moved.id) {
                null
            } else if (it.id == target.id) {
                merged
            } else {
                it
            }
        },
    )
}

/**
 * Tidy: bars snap to the edges of [frame], the FAB to its corner, dialogs to the centre and sheets
 * to the bottom; the rest keeps its reading order and stacks the COUI way — content 16dp from the
 * screen edges, a 16dp gap under every card, category titles right above the card they head. Parts
 * side by side (overlapping rows) stay side by side.
 */
fun CanvasDoc.tidy(frameId: String, boxOf: (Group) -> Box): CanvasDoc {
    val frame = frame(frameId) ?: return this
    val on = groupsOn(frame)
    if (on.isEmpty()) return this
    val placed = HashMap<String, Pair<Float, Float>>()
    val screenBottom = frame.y + frame.h

    var top = frame.y
    on.filter { it.kind?.pin == Pin.Top }.forEach { g ->
        placed[g.id] = frame.x to top
        top += boxOf(g).h
    }
    var bottom = screenBottom
    on.filter { it.kind == Kind.BottomNav }.forEach { g ->
        bottom -= boxOf(g).h
        placed[g.id] = frame.x to bottom
    }
    on.filter { it.kind == Kind.Fab }.forEach { g ->
        val b = boxOf(g)
        placed[g.id] = frame.x + frame.w - MARGIN - 8f - b.w to bottom - MARGIN - b.h
    }
    on.filter { it.kind == Kind.Snackbar }.forEach { g ->
        val b = boxOf(g)
        placed[g.id] = frame.x + (frame.w - b.w) / 2f to bottom - MARGIN - b.h
    }
    on.filter { it.kind == Kind.Dialog }.forEach { g ->
        val b = boxOf(g)
        // COUI 17 dialogs sit near the bottom on phones (32dp margin), centred on wide screens.
        val y = if (frame.isWide) frame.y + (frame.h - b.h) / 2f else screenBottom - 32f - b.h
        placed[g.id] = frame.x + (frame.w - b.w) / 2f to y
    }
    on.filter { it.kind == Kind.BottomSheet }.forEach { g ->
        val b = boxOf(g)
        placed[g.id] = frame.x to screenBottom - b.h
    }

    // The body: everything else, in reading order, grouped into lines of parts side by side.
    val sheetBoxes = on.filter { it.kind == Kind.BottomSheet || it.kind == Kind.Dialog }.map { boxOf(it) }
    val body = on.filter { it.id !in placed && sheetBoxes.none { s -> s.encloses(boxOf(it), 4f) } }.sortedWith(compareBy({ boxOf(it).y }, { boxOf(it).x }))
    var y = top + (if (on.none { it.kind?.pin == Pin.Top }) STATUS_BAR_H + MARGIN else 0f)
    var i = 0
    var previous: Group? = null
    while (i < body.size) {
        val first = body[i]
        val fb = boxOf(first)
        val line = mutableListOf(first)
        var j = i + 1

        // Parts side by side: they overlap vertically and not horizontally. Bars, category titles
        // and other full-width parts always take a line of their own.
        fun wide(g: Group) = g.kind?.fullWidth == true || g.kind == Kind.Category
        while (j < body.size && !wide(first)) {
            val g = body[j]
            val b = boxOf(g)
            val beside = line.none { boxOf(it).let { o -> b.x < o.right - 4f && o.x < b.right - 4f } }
            if (!wide(g) && beside && b.y < fb.bottom - 8f && b.bottom > fb.y + 8f) line += g else break
            j++
        }
        if (previous != null && previous.kind != Kind.Category) {
            // Cards and runs leave 16dp to what follows; plain text 8dp.
            y += if (previous.kind == Kind.Text || previous.kind == Kind.Divider) 8f else MARGIN
        }
        val lineH = line.maxOf { boxOf(it).h }
        if (line.size == 1) {
            val g = line.single()
            val b = boxOf(g)
            val x = when {
                g.kind?.fullWidth == true || g.kind == Kind.Category -> frame.x
                g.kind == Kind.Fab || g.kind == Kind.IconButton || g.kind == Kind.Chip || g.kind == Kind.Button -> b.x.coerceIn(frame.x + MARGIN, frame.x + frame.w - MARGIN - b.w)
                else -> frame.x + MARGIN
            }
            placed[g.id] = x to y
        } else {
            val sorted = line.sortedBy { boxOf(it).x }
            var x = frame.x + MARGIN
            sorted.forEach { g ->
                placed[g.id] = x to y
                x += boxOf(g).w + BUTTON_GAP
            }
        }
        y += lineH
        previous = line.last()
        i = j
    }
    // Parts inside a sheet or dialog move with it.
    val containers = on.filter { it.kind == Kind.BottomSheet || it.kind == Kind.Dialog }
    val moves = HashMap<String, Pair<Float, Float>>()
    containers.forEach { c ->
        val to = placed[c.id] ?: return@forEach
        val dx = to.first - c.x
        val dy = to.second - c.y
        val cb = boxOf(c)
        on.filter { it.id != c.id && cb.encloses(boxOf(it), 4f) }.forEach { inner -> moves[inner.id] = inner.x + dx to inner.y + dy }
    }
    val snapped = groups.map { g ->
        val p = moves[g.id] ?: placed[g.id] ?: return@map g
        g.copy(x = snap(p.first), y = snap(p.second))
    }
    return copy(groups = snapped)
}

/** Rounds to the 4dp grid. */
fun snap(v: Float, grid: Float = 4f): Float = (v / grid).roundToInt() * grid

/** The bounding box of everything on the canvas. */
fun CanvasDoc.worldBox(boxOf: (Group) -> Box): Box {
    if (frames.isEmpty() && groups.isEmpty()) return Box(0f, 0f, Frame.PHONE_W, Frame.PHONE_H)
    var l = Float.MAX_VALUE
    var t = Float.MAX_VALUE
    var r = -Float.MAX_VALUE
    var b = -Float.MAX_VALUE
    frames.forEach {
        l = min(l, it.x)
        t = min(t, it.y)
        r = max(r, it.x + it.w)
        b = max(b, it.y + it.drawnHeight)
    }
    groups.forEach {
        val box = boxOf(it)
        l = min(l, box.x)
        t = min(t, box.y)
        r = max(r, box.right)
        b = max(b, box.bottom)
    }
    return Box(l, t, r - l, b - t)
}
