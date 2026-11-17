// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.model

/** What is wrong with a document; the editor lists these when a file or link opens. */
data class Problem(val type: Type, val subject: String) {
    enum class Type {
        /** The text is not a canvas document at all. */
        NotADocument,

        /** An item's `kind` is not one the canvas knows; the item is dropped. */
        UnknownKind,

        /** Two frames, groups or items share an id; the later ones get new ids. */
        DuplicateId,

        /** An action opens a frame that does not exist; the action is dropped. */
        MissingTarget,

        /** A group without items; it is dropped. */
        EmptyGroup,
    }
}

/** A parsed document with what had to be repaired on the way in. */
data class ParsedDoc(val doc: CanvasDoc?, val problems: List<Problem>)

/** Reads a document from JSON and repairs what can be repaired. */
fun parseDoc(text: String): ParsedDoc {
    val raw = runCatching { CanvasJson.decodeFromString(CanvasDoc.serializer(), text) }.getOrElse {
        return ParsedDoc(null, listOf(Problem(Problem.Type.NotADocument, it.message?.take(200) ?: "")))
    }
    return repair(raw)
}

/** Drops unknown kinds and empty groups, renames duplicate ids and drops actions to missing frames. */
fun repair(doc: CanvasDoc): ParsedDoc {
    val problems = mutableListOf<Problem>()
    val used = HashSet<String>()
    var counter = 0
    fun unique(id: String): String {
        if (used.add(id)) return id
        problems += Problem(Problem.Type.DuplicateId, id)
        var next: String
        do {
            next = "${id}_${++counter}"
        } while (!used.add(next))
        return next
    }
    val frames = doc.frames.map { it.copy(id = unique(it.id), w = it.w.takeIf { w -> w > 0 } ?: Frame.PHONE_W, h = it.h.takeIf { h -> h > 0 } ?: Frame.PHONE_H) }
    val frameIds = frames.map { it.id }.toSet()
    fun Action.valid(): Action? = if (to == Action.BACK || to in frameIds) {
        this
    } else {
        problems += Problem(Problem.Type.MissingTarget, to)
        null
    }
    val groups = doc.groups.mapNotNull { g ->
        val items = g.items.mapNotNull { item ->
            if (Kind.of(item.kind) == null) {
                problems += Problem(Problem.Type.UnknownKind, item.kind)
                null
            } else {
                item.copy(
                    id = unique(item.id),
                    action = item.action?.valid(),
                    actions = item.actions?.mapNotNull { (k, v) -> v.valid()?.let { k to it } }?.toMap()?.takeIf { it.isNotEmpty() },
                    value = item.value?.coerceIn(0f, 100f),
                )
            }
        }
        if (items.isEmpty()) {
            problems += Problem(Problem.Type.EmptyGroup, g.id)
            null
        } else {
            val axis = if (g.axis == Group.AXIS_X || g.axis == Group.AXIS_Y) g.axis else Group.AXIS_Y
            g.copy(id = unique(g.id), items = items, axis = axis)
        }
    }
    val fixedFrames = frames.map { f -> f.copy(swipe = f.swipe?.filterValues { it in frameIds }?.takeIf { it.isNotEmpty() }) }
    return ParsedDoc(doc.copy(frames = fixedFrames, groups = groups), problems)
}

/** The document as JSON for a file (pretty) or a link (compact). */
fun CanvasDoc.toJson(pretty: Boolean = false): String = (if (pretty) CanvasJsonPretty else CanvasJson).encodeToString(CanvasDoc.serializer(), this)
