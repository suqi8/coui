// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.gen

import canvas.model.Action
import canvas.model.Box
import canvas.model.CanvasDoc
import canvas.model.Family
import canvas.model.Frame
import canvas.model.Group
import canvas.model.Item
import canvas.model.Kind
import canvas.model.estimatedBox
import canvas.model.frameOf

/*
 * Layout analysis shared by the prompt and the code generator: it turns the free-form canvas into
 * what a COUI page is made of — a top bar, a body read top to bottom, bars and overlays — and works
 * out where taps and swipes go.
 */

/** One part on a screen: a [group] with its (measured or estimated) [box]. */
data class Part(val group: Group, val box: Box) {
    val kind: Kind get() = group.kind ?: Kind.Text
    val item: Item get() = group.first
    val items: List<Item> get() = group.items

    /** A run of preference rows: one card. */
    val isRowRun: Boolean get() = kind.family == Family.Row
}

/** A piece of a screen body, in reading order. */
sealed interface Block {
    /** Every part of the block, in reading order. */
    val parts: List<Part>

    /** A run of preference rows in one card, with the category title right above it when there is one. */
    data class CardRun(val category: Part?, val run: Part) : Block {
        override val parts: List<Part> get() = listOfNotNull(category, run)
    }

    /** A part on a line of its own. */
    data class Single(val part: Part) : Block {
        override val parts: List<Part> get() = listOf(part)
    }

    /** Parts side by side (their vertical ranges overlap), left to right. */
    data class Line(override val parts: List<Part>) : Block

    /** Lines of the same shape repeated one under another: a grid of [columns] columns. */
    data class Grid(val lines: List<List<Part>>) : Block {
        val columns: Int get() = lines.first().size
        override val parts: List<Part> get() = lines.flatten()
    }
}

/** A dialog or bottom sheet drawn open on its screen, with the parts drawn inside it. */
data class Overlay(
    val part: Part,
    val content: List<Block>,
    /** The part on the same screen whose label names this overlay, which most likely opens it. */
    val opener: Item?,
)

/** What a tap or swipe does. */
data class NavEdge(
    /** The screen the trigger is on. */
    val from: String,
    /** A frame id, or [Action.BACK]. */
    val to: String,
    val transition: String,
    /** The item tapped; null for a swipe. */
    val item: Item?,
    /** `tap`, `tab:N`, `icon` / `icon2` / `search` (top bar) or `swipe:<direction>`. */
    val trigger: String,
    /** Whether the edge was not drawn but inferred (a top bar navigation icon without an action goes back). */
    val implicit: Boolean = false,
) {
    val isBack: Boolean get() = to == Action.BACK
    val isSwipe: Boolean get() = trigger.startsWith(SWIPE)

    /** The label of what is tapped (a tab or button label, the item label), for prose. */
    val label: String
        get() {
            val item = item ?: return ""
            if (trigger.startsWith(TAB)) return item.tabs?.getOrNull(trigger.removePrefix(TAB).toIntOrNull() ?: -1)?.label.orEmpty()
            if (trigger == ICON2) return item.icon2.orEmpty()
            if (trigger == ICON) return item.icon.orEmpty()
            if (trigger == SEARCH) return item.supporting.orEmpty()
            return item.label
        }

    companion object {
        const val TAP = "tap"
        const val TAB = "tab:"
        const val ICON = "icon"
        const val ICON2 = "icon2"
        const val SEARCH = "search"
        const val SWIPE = "swipe:"
    }
}

/** One screen, laid out the COUI way. */
data class ScreenPlan(
    val frame: Frame,
    /** A unique PascalCase Kotlin identifier: the route is `Route.<ident>`, the page `<ident>Page`. */
    val ident: String,
    /** The snake_case prefix of the screen's string resources. */
    val snake: String,
    val topBar: Part?,
    val bottomNav: Part?,
    val fab: Part?,
    val snackbar: Part?,
    val overlays: List<Overlay>,
    val body: List<Block>,
) {
    /** Every part on the screen. */
    val parts: List<Part>
        get() = listOfNotNull(topBar) + body.flatMap { it.parts } + overlays.flatMap { o -> listOf(o.part) + o.content.flatMap { it.parts } } +
            listOfNotNull(bottomNav, fab, snackbar)
}

/** The whole app: screens in reading order, where taps go and which screen opens first. */
data class AppPlan(
    val screens: List<ScreenPlan>,
    val edges: List<NavEdge>,
    val start: ScreenPlan?,
) {
    fun screen(frameId: String?): ScreenPlan? = screens.firstOrNull { it.frame.id == frameId }

    fun edgesFrom(frameId: String): List<NavEdge> = edges.filter { it.from == frameId }

    /** Screens a screen's taps open (back and swipes excluded), in first-use order. */
    fun tapTargets(frameId: String): List<ScreenPlan> = edgesFrom(frameId).filter { !it.isBack && !it.isSwipe && it.to != frameId }.mapNotNull { screen(it.to) }.distinct()

    /** Whether something on the screen goes back. */
    fun goesBack(frameId: String): Boolean = edgesFrom(frameId).any { it.isBack && !it.isSwipe }

    /** The transition a screen is opened with: the one of the first edge that opens it. */
    fun transitionInto(frameId: String): String = edges.firstOrNull { it.to == frameId && it.from != frameId }?.transition ?: Action.TRANSITION_PUSH
}

/** Builds the [AppPlan] of a document. */
object Structure {
    /** Parts whose vertical ranges overlap by more than this share a line. */
    private const val LINE_OVERLAP = 8f

    /** The slack in dp for a part to count as inside a dialog or sheet. */
    private const val ENCLOSE_SLACK = 4f

    /** The largest gap in dp between a category title and the card it heads. */
    private const val CATEGORY_GAP = 40f

    fun analyze(doc: CanvasDoc, boxOf: (Group) -> Box = { doc.estimatedBox(it) }): AppPlan {
        val frames = readingOrder(doc.frames, { Box(it.x, it.y, it.w, it.drawnHeight) }, overlap = 0.5f)
        val idents = frameIdentifiers(frames)
        val screens = frames.map { frame ->
            val parts = doc.groups.filter { it.kind != null && doc.frameOf(it)?.id == frame.id }.map { Part(it, boxOf(it)) }
            plan(frame, idents.getValue(frame.id), parts)
        }
        val drawn = screens.flatMap { drawnEdges(it) }
        val targets = drawn.filter { !it.isBack && it.to != it.from }.map { it.to }.toSet()
        val start = screens.firstOrNull { it.frame.id !in targets } ?: screens.firstOrNull()
        // A top bar navigation icon without an action goes back, except on the start screen.
        val implicit = screens.mapNotNull { s ->
            val bar = s.topBar?.item ?: return@mapNotNull null
            if (bar.icon == null || drawn.any { it.item?.id == bar.id && it.trigger == NavEdge.ICON } || s == start) return@mapNotNull null
            NavEdge(s.frame.id, Action.BACK, Action.TRANSITION_PUSH, bar, NavEdge.ICON, implicit = true)
        }
        val edges = screens.flatMap { s -> drawn.filter { it.from == s.frame.id } + implicit.filter { it.from == s.frame.id } }
        return AppPlan(screens, edges, start)
    }

    /** Unique PascalCase identifiers for [frames] (in the given order), keyed by frame id. */
    fun frameIdentifiers(frames: List<Frame>): Map<String, String> {
        val used = HashSet<String>()
        val out = LinkedHashMap<String, String>()
        frames.forEachIndexed { index, frame ->
            var base = Names.pascal(frame.name)
            for (suffix in listOf("Page", "Screen")) {
                if (base.length > suffix.length && base.endsWith(suffix)) base = base.removeSuffix(suffix)
            }
            if (base.isEmpty()) base = "Screen${index + 1}"
            if (base in RESERVED) base += "Screen"
            var name = base
            var n = 2
            while (!used.add(name)) name = "$base${n++}"
            out[frame.id] = name
        }
        return out
    }

    /** Kinds whose `tab:N` actions are taps on their tabs, items or buttons. */
    private val TABBED = setOf(Kind.Tabs, Kind.BottomNav, Kind.Dialog)

    /** The tap targets of a top bar: navigation icon, trailing action, search capsule. */
    private val TOP_BAR_KEYS = setOf(NavEdge.ICON, NavEdge.ICON2, NavEdge.SEARCH)

    /** Names the generated code declares itself. */
    private val RESERVED = setOf("Route", "App", "Res", "AppNavigation", "Navigation")

    private fun plan(frame: Frame, ident: String, all: List<Part>): ScreenPlan {
        val claimed = HashSet<String>()
        fun firstOf(kind: Kind): Part? = all.firstOrNull { it.kind == kind }?.also { claimed += it.group.id }
        val topBar = firstOf(Kind.TopBar)
        val bottomNav = firstOf(Kind.BottomNav)
        val fab = firstOf(Kind.Fab)
        val snackbar = firstOf(Kind.Snackbar)

        // Overlays claim the parts drawn inside them; the topmost overlay wins.
        val overlayParts = all.filter { it.kind.isOverlay }
        claimed += overlayParts.map { it.group.id }
        val inside = LinkedHashMap<String, MutableList<Part>>()
        all.filter { it.group.id !in claimed }.forEach { p ->
            val owner = overlayParts.lastOrNull { it.box.encloses(p.box, ENCLOSE_SLACK) } ?: return@forEach
            inside.getOrPut(owner.group.id) { mutableListOf() } += p
            claimed += p.group.id
        }
        val body = blocks(all.filter { it.group.id !in claimed })
        val overlays = overlayParts.map { o ->
            Overlay(o, blocks(inside[o.group.id].orEmpty()), opener(o, body))
        }
        return ScreenPlan(frame, ident, Names.snake(ident), topBar, bottomNav, fab, snackbar, overlays, body)
    }

    /** The tappable part without an action whose label the overlay's title starts with or contains. */
    private fun opener(overlay: Part, body: List<Block>): Item? {
        val title = Names.normalize(overlay.item.label)
        if (title.isEmpty()) return null
        return body.flatMap { it.parts }.flatMap { it.items }.firstOrNull { item ->
            val kind = Kind.of(item.kind)
            val label = Names.normalize(item.label)
            kind != null && kind.tappable && item.action == null && label.length >= 2 && (title.contains(label) || label.contains(title))
        }
    }

    /** Groups parts into lines in reading order, then lines into blocks. */
    fun blocks(parts: List<Part>): List<Block> {
        val lines = readingOrder(parts, { it.box }, overlap = null).let { sorted -> lines(sorted) }
        val out = mutableListOf<Block>()
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            val next = lines.getOrNull(i + 1)
            if (line.size == 1) {
                val p = line.single()
                when {
                    p.kind == Kind.Category && next != null && next.size == 1 && next.single().isRowRun && next.single().box.y - p.box.bottom <= CATEGORY_GAP -> {
                        out += Block.CardRun(p, next.single())
                        i += 2
                        continue
                    }

                    p.isRowRun -> out += Block.CardRun(null, p)

                    else -> out += Block.Single(p)
                }
            } else {
                out += Block.Line(line)
            }
            i++
        }
        return grids(out)
    }

    /** Merges runs of two or more lines with the same kinds in the same columns into a grid. */
    private fun grids(blocks: List<Block>): List<Block> {
        val out = mutableListOf<Block>()
        var i = 0
        while (i < blocks.size) {
            val b = blocks[i]
            if (b is Block.Line) {
                val shape = b.parts.map { it.kind }
                var j = i + 1
                while (j < blocks.size) {
                    val n = blocks[j]
                    if (n is Block.Line && n.parts.map { it.kind } == shape) j++ else break
                }
                if (j - i >= 2) {
                    out += Block.Grid(blocks.subList(i, j).map { it.parts })
                    i = j
                    continue
                }
            }
            out += b
            i++
        }
        return out
    }

    /** Splits parts sorted in reading order into lines of parts side by side. */
    private fun lines(sorted: List<Part>): List<List<Part>> {
        val out = mutableListOf<List<Part>>()
        var i = 0
        while (i < sorted.size) {
            val first = sorted[i].box
            val line = mutableListOf(sorted[i])
            var j = i + 1
            while (j < sorted.size) {
                val b = sorted[j].box
                if (b.y < first.bottom - LINE_OVERLAP && b.bottom > first.y + LINE_OVERLAP) line += sorted[j] else break
                j++
            }
            out += line.sortedBy { it.box.x }
            i = j
        }
        return out
    }

    /**
     * Sorts by y then x. With [overlap] set, things whose vertical ranges overlap by more than that
     * fraction of the smaller height count as one row and are read left to right (used for frames,
     * which are rarely aligned to the dp).
     */
    private fun <T> readingOrder(list: List<T>, box: (T) -> Box, overlap: Float?): List<T> {
        val byY = list.sortedWith(compareBy({ box(it).y }, { box(it).x }))
        if (overlap == null) return byY
        val rows = mutableListOf<MutableList<T>>()
        byY.forEach { t ->
            val b = box(t)
            val row = rows.lastOrNull()
            val head = row?.first()?.let(box)
            if (head != null) {
                val shared = minOf(b.bottom, head.bottom) - maxOf(b.y, head.y)
                if (shared > overlap * minOf(b.h, head.h)) {
                    row += t
                    return@forEach
                }
            }
            rows += mutableListOf(t)
        }
        return rows.flatMap { r -> r.sortedBy { box(it).x } }
    }

    /** The taps and swipes drawn on a screen, in reading order. */
    private fun drawnEdges(screen: ScreenPlan): List<NavEdge> {
        val from = screen.frame.id
        val out = mutableListOf<NavEdge>()
        screen.parts.forEach { part ->
            part.items.forEach { item ->
                val kind = Kind.of(item.kind) ?: return@forEach
                // On a top bar the plain action and the `icon` key both mean the navigation icon.
                val tapKey = if (kind == Kind.TopBar) NavEdge.ICON else NavEdge.TAP
                if (kind.tappable && !(kind == Kind.TopBar && item.actions?.containsKey(NavEdge.ICON) == true)) {
                    item.action?.let { out += NavEdge(from, it.to, it.transition, item, tapKey) }
                }
                item.actions?.entries?.sortedBy { it.key }?.forEach { (key, a) ->
                    val fits = if (kind == Kind.TopBar) key in TOP_BAR_KEYS else key.startsWith(NavEdge.TAB) && kind in TABBED
                    if (fits) out += NavEdge(from, a.to, a.transition, item, key)
                }
            }
        }
        screen.frame.swipe?.entries?.sortedBy { it.key }?.forEach { (dir, to) ->
            out += NavEdge(from, to, Action.TRANSITION_PUSH, null, NavEdge.SWIPE + dir)
        }
        return out
    }
}

/** Identifier and text helpers. */
object Names {
    /** ASCII words of [text]: runs of letters and digits. */
    fun words(text: String): List<String> = Regex("[A-Za-z0-9]+").findAll(text).map { it.value }.toList()

    /** PascalCase from the ASCII words of [text]; empty when it has none. A leading digit gets an `S` prefix. */
    fun pascal(text: String): String {
        val s = words(text).joinToString("") { w -> w.replaceFirstChar { it.uppercaseChar() } }
        return if (s.firstOrNull()?.isDigit() == true) "S$s" else s
    }

    /** lowerCamelCase from at most [max] ASCII words of [text]; empty when it has none or starts with a digit. */
    fun camel(text: String, max: Int = 3): String {
        val w = words(text).take(max)
        if (w.isEmpty() || w.first().first().isDigit()) return ""
        return w.first().lowercase() + w.drop(1).joinToString("") { p -> p.lowercase().replaceFirstChar { it.uppercaseChar() } }
    }

    /** snake_case of a PascalCase identifier. */
    fun snake(ident: String): String = buildString {
        ident.forEachIndexed { i, c ->
            if (c.isUpperCase() && i > 0 && (ident[i - 1].isLowerCase() || ident[i - 1].isDigit())) append('_')
            append(c.lowercaseChar())
        }
    }

    /** A resource key slug from at most [max] ASCII words of [text]; empty when it has none. */
    fun slug(text: String, max: Int = 4): String = words(text).take(max).joinToString("_") { it.lowercase() }.take(40).trimEnd('_')

    /** Lower case letters and digits only (any script), for loose label comparison. */
    fun normalize(text: String): String = text.filter { it.isLetterOrDigit() }.lowercase()

    /** Whether [text] has CJK characters. */
    fun hasCjk(text: String): Boolean = text.any { it.code in 0x3400..0x9FFF || it.code in 0xF900..0xFAFF || it.code in 0x3000..0x303F || it.code in 0xFF00..0xFFEF }
}
