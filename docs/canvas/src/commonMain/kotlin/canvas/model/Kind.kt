// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.model

/** The palette section a kind is listed under. */
enum class Section { Bars, Rows, Buttons, Content, Overlays }

/** Parts of one family placed edge to edge fuse into one connected [Group]. */
enum class Family { Row, Button, Chip, None }

/** Where Tidy pins a kind on its screen; [None] stacks with the body. */
enum class Pin { Top, Bottom, Fab, Center, None }

/** [Kind.defaultW] for parts as wide as the screen. */
const val FULL_WIDTH = -1f

/** The [Item] fields a kind uses (the inspector shows exactly these). */
enum class Field { Label, Supporting, End, Icon, Icon2, Variant, Checked, Value, Size, Size2, Tabs, Selected, Action }

/**
 * Every part the canvas knows, with the COUI component it stands for. [key] is the `kind` string
 * stored in documents; [defaultW] / [defaultH] are dp sizes used before the part has been measured
 * ([defaultW] null = the full content width of the screen, [FULL] = the full screen width).
 */
enum class Kind(
    val key: String,
    val section: Section,
    val family: Family,
    val pin: Pin,
    val component: String,
    val defaultW: Float?,
    val defaultH: Float,
    val fields: Set<Field>,
    val variants: List<String> = emptyList(),
) {
    TopBar(
        "topBar", Section.Bars, Family.None, Pin.Top, "TopAppBar", FULL_WIDTH, 152f,
        setOf(Field.Label, Field.Supporting, Field.Icon, Field.Icon2, Field.Variant, Field.Action),
        listOf("large", "home", "small"),
    ),
    SearchBar("searchBar", Section.Bars, Family.None, Pin.None, "SearchBar + InputField", null, 52f, setOf(Field.Label, Field.Action)),
    Tabs("tabs", Section.Bars, Family.None, Pin.None, "TabRow", null, 48f, setOf(Field.Tabs, Field.Selected, Field.Variant), listOf("underline", "contour")),
    BottomNav("bottomNav", Section.Bars, Family.None, Pin.Bottom, "FloatingNavigationBar", FULL_WIDTH, 96f, setOf(Field.Tabs, Field.Selected)),

    Category("category", Section.Rows, Family.None, Pin.None, "PreferenceCategory", FULL_WIDTH, 40f, setOf(Field.Label)),
    SwitchRow("switchRow", Section.Rows, Family.Row, Pin.None, "SwitchPreference", null, 64f, setOf(Field.Label, Field.Supporting, Field.Icon, Field.Checked)),
    ArrowRow("arrowRow", Section.Rows, Family.Row, Pin.None, "ArrowPreference", null, 64f, setOf(Field.Label, Field.Supporting, Field.End, Field.Icon, Field.Action)),
    BasicRow("basicRow", Section.Rows, Family.Row, Pin.None, "BasicComponent", null, 64f, setOf(Field.Label, Field.Supporting, Field.End, Field.Icon, Field.Action)),
    DropdownRow("dropdownRow", Section.Rows, Family.Row, Pin.None, "OverlayDropdownPreference", null, 64f, setOf(Field.Label, Field.Supporting, Field.Icon, Field.Tabs, Field.Selected)),
    CheckboxRow("checkboxRow", Section.Rows, Family.Row, Pin.None, "CheckboxPreference", null, 64f, setOf(Field.Label, Field.Supporting, Field.Checked)),
    RadioRow("radioRow", Section.Rows, Family.Row, Pin.None, "RadioButtonPreference", null, 64f, setOf(Field.Label, Field.Supporting, Field.Checked)),
    MarkRow("markRow", Section.Rows, Family.Row, Pin.None, "MarkPreference", null, 64f, setOf(Field.Label, Field.Supporting, Field.Checked)),
    SliderRow("sliderRow", Section.Rows, Family.Row, Pin.None, "SliderPreference", null, 96f, setOf(Field.Label, Field.Supporting, Field.End, Field.Value)),
    InputRow("inputRow", Section.Rows, Family.Row, Pin.None, "InputPreference", null, 64f, setOf(Field.Label, Field.Supporting)),

    Button("button", Section.Buttons, Family.Button, Pin.None, "TextButton", null, 44f, setOf(Field.Label, Field.Variant, Field.Size, Field.Action), listOf("primary", "secondary")),
    IconButton("iconButton", Section.Buttons, Family.Button, Pin.None, "IconButton", 40f, 40f, setOf(Field.Icon, Field.Action)),
    Fab("fab", Section.Buttons, Family.None, Pin.Fab, "FloatingActionButton", 56f, 56f, setOf(Field.Icon, Field.Action)),
    Chip("chip", Section.Buttons, Family.Chip, Pin.None, "Chip", 72f, 32f, setOf(Field.Label, Field.Icon, Field.Checked, Field.Action)),

    Card("card", Section.Content, Family.None, Pin.None, "Card", null, 120f, setOf(Field.Label, Field.Supporting, Field.Icon, Field.Size, Field.Size2, Field.Action)),
    Text("text", Section.Content, Family.None, Pin.None, "Text", null, 28f, setOf(Field.Label, Field.Variant, Field.Size), listOf("title", "body", "caption")),
    Image("image", Section.Content, Family.None, Pin.None, "Image", null, 180f, setOf(Field.Label, Field.Size, Field.Size2)),
    TopTips("topTips", Section.Content, Family.None, Pin.None, "TopTips", null, 64f, setOf(Field.Label, Field.Supporting, Field.Action)),
    EmptyState("emptyState", Section.Content, Family.None, Pin.None, "EmptyState", null, 260f, setOf(Field.Label, Field.Supporting, Field.End, Field.Action)),
    TextField("textField", Section.Content, Family.None, Pin.None, "TextField", null, 52f, setOf(Field.Label, Field.Supporting, Field.Variant), listOf("line", "filled")),
    Slider("slider", Section.Content, Family.None, Pin.None, "Slider", null, 36f, setOf(Field.Value)),
    Progress("progress", Section.Content, Family.None, Pin.None, "ProgressIndicator", null, 24f, setOf(Field.Value, Field.Variant), listOf("linear", "circular", "loading")),
    Divider("divider", Section.Content, Family.None, Pin.None, "HorizontalDivider", null, 16f, emptySet()),

    Dialog("dialog", Section.Overlays, Family.None, Pin.Center, "OverlayDialog", 328f, 200f, setOf(Field.Label, Field.Supporting, Field.Tabs)),
    BottomSheet("bottomSheet", Section.Overlays, Family.None, Pin.Bottom, "OverlayBottomSheet", FULL_WIDTH, 360f, setOf(Field.Label, Field.Size2)),
    Snackbar("snackbar", Section.Overlays, Family.None, Pin.Bottom, "Snackbar", null, 56f, setOf(Field.Label, Field.Supporting)),
    ;

    /** Whether this kind takes the whole screen width wherever it sits. */
    val fullWidth: Boolean get() = defaultW == FULL_WIDTH

    /** Whether the kind is drawn above the page (it gets a scrim on its screen). */
    val isOverlay: Boolean get() = this == Dialog || this == BottomSheet

    /** Whether a tap on this kind can open a screen (the inspector's trigger tab). */
    val tappable: Boolean get() = Field.Action in fields

    companion object {
        /** [defaultW] for parts as wide as the screen. */
        const val FULL = FULL_WIDTH

        private val byKey = entries.associateBy { it.key }

        fun of(key: String): Kind? = byKey[key]
    }
}
