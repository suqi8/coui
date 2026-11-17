// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/*
 * A design is one JSON document: screens ([Frame]) side by side on an infinite canvas, and parts
 * ([Group]s of [Item]s) placed on them in canvas coordinates. The same document is saved to a file,
 * kept in the browser, carried in a share link and written by AI agents (see docs/public/canvas/agent.md),
 * so every field is optional except the ids, and unknown fields are ignored on read.
 */

/** The whole design. */
@Immutable
@Serializable
data class CanvasDoc(
    val version: Int = 1,
    /** The app's name. */
    val title: String = "",
    /** One or two sentences on what the app is for. */
    val brief: String = "",
    val theme: DocTheme = DocTheme(),
    val frames: List<Frame> = emptyList(),
    /** Parts, bottom layer first. */
    val groups: List<Group> = emptyList(),
)

/** How the screens are themed; it goes into the prompt and the generated code too. */
@Immutable
@Serializable
data class DocTheme(
    /** Which mode the canvas shows. */
    val dark: Boolean = false,
    /** Whether the app supports both light and dark mode (follows the system). */
    val bothModes: Boolean = true,
    /** A key colour as `#RRGGBB` the scheme is generated from; null keeps the COUI blue. */
    val keyColor: String? = null,
    /** `android` (an Android app) or `multiplatform` (Compose Multiplatform, UI in commonMain). */
    val platform: String = PLATFORM_ANDROID,
) {
    companion object {
        const val PLATFORM_ANDROID = "android"
        const val PLATFORM_MULTIPLATFORM = "multiplatform"
    }
}

/** One screen. */
@Immutable
@Serializable
data class Frame(
    val id: String,
    /** What the screen is called in the prompt and in the generated code. */
    val name: String,
    val x: Float = 0f,
    val y: Float = 0f,
    val w: Float = PHONE_W,
    val h: Float = PHONE_H,
    /** What the screen is for, in a sentence. */
    val note: String? = null,
    /** The scrollable content length in dp when it is longer than [h]. */
    val length: Float? = null,
    /** `background` (the window background, default) or `card` (the card colour). */
    val bg: String? = null,
    /** Screens reached by swiping: keys `left`, `right`, `up`, `down`. */
    val swipe: Map<String, String>? = null,
) {
    /** The height the screen draws at: its scroll length when that is longer than the device. */
    val drawnHeight: Float get() = maxOf(h, length ?: h)

    val isWide: Boolean get() = w >= WIDE_MIN_W

    companion object {
        const val PHONE_W = 360f
        const val PHONE_H = 800f
        const val WIDE_W = 1024f
        const val WIDE_H = 720f
        const val WIDE_MIN_W = 600f

        /** The gap between screens placed side by side. */
        const val GAP = 80f
    }
}

/**
 * One part, or a connected run of parts of one family drawn as a unit: preference rows stacked in
 * one card (`axis = "y"`), buttons or chips side by side (`axis = "x"`).
 */
@Immutable
@Serializable
data class Group(
    val id: String,
    val x: Float,
    val y: Float,
    val axis: String = AXIS_Y,
    val items: List<Item>,
    /** Locked groups cannot be dragged on the canvas. */
    val locked: Boolean = false,
) {
    val first: Item get() = items.first()
    val kind: Kind? get() = Kind.of(first.kind)

    companion object {
        const val AXIS_X = "x"
        const val AXIS_Y = "y"
    }
}

/** One part. Which fields mean something depends on [kind]; see [Kind]. */
@Immutable
@Serializable
data class Item(
    val id: String,
    val kind: String,
    val label: String = "",
    /** The secondary text: a summary, a subtitle, a hint or an action label. */
    val supporting: String? = null,
    /** The value shown at the end of a row, or the confirm button of a dialog. */
    val end: String? = null,
    /** An icon name from [Icons]; null for none. */
    val icon: String? = null,
    /** A second (trailing) icon name. */
    val icon2: String? = null,
    /** A style variant, one of [Kind.variants]. */
    val variant: String? = null,
    val checked: Boolean? = null,
    /** A 0–100 value for sliders and progress. */
    val value: Float? = null,
    /** The width in dp; null keeps the default width of the kind. */
    val size: Float? = null,
    /** The height in dp where the kind has a free height. */
    val size2: Float? = null,
    /** Tabs, options or buttons. */
    val tabs: List<Tab>? = null,
    /** The selected tab or option. */
    val selected: Int? = null,
    /** What the part does, in the author's words; goes into the prompt verbatim. */
    val note: String? = null,
    /** The screen a tap opens. */
    val action: Action? = null,
    /** Per-target actions: `tab:0`… for tabs, navigation items and dialog buttons; `icon2` for a trailing icon. */
    val actions: Map<String, Action>? = null,
)

/** A tab, navigation item, option or dialog button. */
@Immutable
@Serializable
data class Tab(
    val label: String = "",
    val icon: String? = null,
)

/** Where a tap goes. */
@Immutable
@Serializable
data class Action(
    /** A frame id, or [BACK]. */
    val to: String,
    /** One of [TRANSITIONS]. */
    val transition: String = TRANSITION_PUSH,
) {
    companion object {
        const val BACK = "back"
        const val TRANSITION_PUSH = "push"
        const val TRANSITION_FADE = "fade"
        const val TRANSITION_UP = "up"
        const val TRANSITION_NONE = "none"
        val TRANSITIONS = listOf(TRANSITION_PUSH, TRANSITION_FADE, TRANSITION_UP, TRANSITION_NONE)
    }
}
