// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas

import canvas.gen.Block
import canvas.gen.CodeGenerator
import canvas.gen.Lang
import canvas.gen.NavEdge
import canvas.gen.PromptGenerator
import canvas.gen.Structure
import canvas.gen.escapeXml
import canvas.model.Action
import canvas.model.CanvasDoc
import canvas.model.DocTheme
import canvas.model.Frame
import canvas.model.Group
import canvas.model.Item
import canvas.model.Kind
import canvas.model.Problem
import canvas.model.Tab
import canvas.model.parseDoc
import canvas.model.repair
import canvas.model.toJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GenTest {
    private val dx = Frame.PHONE_W + Frame.GAP

    private fun doc(): CanvasDoc = CanvasDoc(
        title = "Demo",
        brief = "A small settings app.",
        frames = listOf(
            Frame(id = "detail", name = "Detail", x = dx),
            Frame(id = "home", name = "Home", note = "The settings home."),
        ),
        groups = listOf(
            Group("g_bar", 0f, 0f, items = listOf(Item("bar", "topBar", label = "Settings", supporting = "Search", variant = "home"))),
            Group("g_cat1", 0f, 160f, items = listOf(Item("cat1", "category", label = "General"))),
            Group(
                "g_run1",
                16f,
                200f,
                items = listOf(
                    Item("wifi", "switchRow", label = "Wi-Fi", supporting = "Connected", icon = "WorldClock", checked = true),
                    Item("display", "arrowRow", label = "Display", end = "Auto", action = Action("detail")),
                ),
            ),
            Group("g_cat2", 0f, 360f, items = listOf(Item("cat2", "category", label = "More"))),
            Group(
                "g_run2",
                16f,
                400f,
                items = listOf(
                    Item("clear", "basicRow", label = "Clear cache", note = "Ask before clearing."),
                    Item("theme", "dropdownRow", label = "Theme", tabs = listOf(Tab("Light"), Tab("Dark")), selected = 1),
                ),
            ),
            Group("g_nav", 0f, 704f, items = listOf(Item("nav", "bottomNav", tabs = listOf(Tab("Home", "Settings"), Tab("Me", "Contacts")), selected = 0))),
            // Detail screen.
            Group("g_bar2", dx, 0f, items = listOf(Item("bar2", "topBar", label = "Detail", icon = "Back", variant = "large"))),
            Group("g_text", dx + 16f, 170f, items = listOf(Item("text", "text", label = "Hello", variant = "caption"))),
            Group(
                "g_buttons",
                dx + 16f,
                220f,
                axis = Group.AXIS_X,
                items = listOf(Item("ok", "button", label = "OK", variant = "primary"), Item("cancel", "button", label = "Cancel", variant = "secondary")),
            ),
            Group(
                "g_dialog",
                dx + 16f,
                568f,
                items = listOf(
                    Item(
                        "dialog",
                        "dialog",
                        label = "Delete it?",
                        supporting = "It's \"gone\" & 100% <lost>",
                        tabs = listOf(Tab("Cancel"), Tab("Delete")),
                        actions = mapOf("tab:1" to Action(Action.BACK)),
                    ),
                ),
            ),
            // Loose part outside any frame: ignored.
            Group("g_loose", -900f, 0f, items = listOf(Item("loose", "text", label = "Loose"))),
        ),
    )

    @Test
    fun structureOrdersScreensAndBlocks() {
        val plan = Structure.analyze(doc())
        assertEquals(listOf("Home", "Detail"), plan.screens.map { it.ident })
        assertEquals("home", plan.start?.frame?.id)
        val home = plan.screens[0]
        assertEquals("bar", home.topBar?.item?.id)
        assertEquals("nav", home.bottomNav?.item?.id)
        assertEquals(2, home.body.size)
        val first = home.body[0] as Block.CardRun
        assertEquals("cat1", first.category?.item?.id)
        assertEquals(listOf("wifi", "display"), first.run.items.map { it.id })
        val second = home.body[1] as Block.CardRun
        assertEquals("cat2", second.category?.item?.id)

        val detail = plan.screens[1]
        assertEquals(listOf("text", "ok"), detail.body.map { it.parts.first().item.id })
        assertTrue(detail.body[1] is Block.Single)
        assertEquals(1, detail.overlays.size)
        assertTrue(plan.screens.none { s -> s.parts.any { it.item.id == "loose" } })

        val open = plan.edges.single { it.item?.id == "display" }
        assertEquals("detail", open.to)
        val implicitBack = plan.edges.single { it.item?.id == "bar2" }
        assertTrue(implicitBack.isBack && implicitBack.implicit && implicitBack.trigger == NavEdge.ICON)
        assertTrue(plan.edges.any { it.item?.id == "dialog" && it.trigger == "tab:1" && it.isBack })
    }

    @Test
    fun linesAndGrids() {
        val frame = Frame(id = "f", name = "Grid")
        fun chip(id: String, x: Float, y: Float) = Group(id, x, y, items = listOf(Item(id, "card", label = id, size = 150f, size2 = 80f)))
        val d = CanvasDoc(frames = listOf(frame), groups = listOf(chip("a", 16f, 100f), chip("b", 190f, 104f), chip("c", 16f, 200f), chip("d", 190f, 200f)))
        val body = Structure.analyze(d).screens.single().body
        val grid = body.single() as Block.Grid
        assertEquals(2, grid.columns)
        assertEquals(listOf("a", "b", "c", "d"), grid.parts.map { it.item.id })
    }

    @Test
    fun promptNamesComponentsAndNavigation() {
        val components = listOf(
            "SwitchPreference", "ArrowPreference", "BasicComponent", "OverlayDropdownPreference", "PreferenceCategory",
            "FloatingNavigationBar", "CouiTopBarStyle.SettingsHome", "TopAppBarSearchSlot", "OverlayDialog", "DialogButtonBar",
            "TextButton", "COUIIcons.WorldClock", "COUIIcons.Back",
        )
        for (lang in Lang.entries) {
            val p = PromptGenerator.generate(doc(), lang)
            components.forEach { assertTrue(it in p, "$lang prompt misses $it\n$p") }
            assertTrue("**Detail**" in p, p)
            assertTrue("Ask before clearing." in p, p)
            assertTrue("https://suqi8.github.io/coui/llms.txt" in p)
            assertTrue('→' !in p, "the web font has no arrow glyph")
            assertTrue(("## Navigation" in p) == (lang == Lang.En))
            assertTrue(("## 导航" in p) == (lang == Lang.Zh))
        }
        val zh = PromptGenerator.generate(doc(), Lang.Zh)
        assertTrue("打开 **Detail**" in zh, zh)
        assertTrue("返回上一页" in zh, zh)
        val en = PromptGenerator.generate(doc(), Lang.En)
        assertTrue("opens **Detail**" in en, en)

        val one = PromptGenerator.generate(doc(), Lang.En, frameId = "detail")
        assertTrue("# Demo" in one && "OverlayDialog" in one)
        assertTrue("SwitchPreference" !in one, one)
    }

    @Test
    fun codegenWritesPagesNavigationAppAndStrings() {
        val files = CodeGenerator.generate(doc(), "com.acme.demo")
        val byName = files.associateBy { it.path.substringAfterLast('/') }
        val root = "app/src/main/java/com/acme/demo/"
        assertEquals(
            setOf("${root}HomePage.kt", "${root}DetailPage.kt", "${root}Navigation.kt", "${root}App.kt", "app/src/main/composeResources/values/strings_canvas.xml", "app/src/main/composeResources/values-zh/strings_canvas.xml"),
            files.map { it.path }.toSet(),
        )
        val home = byName.getValue("HomePage.kt").content
        assertTrue("fun HomePage(" in home && "onOpenDetail: () -> Unit," in home, home)
        assertTrue("CouiPageScaffold(" in home && "FloatingNavigationBar {" in home, home)
        assertTrue("SwitchPreference(" in home && "CardListGroup {" in home && "Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)" in home, home)
        assertTrue("// TODO: Ask before clearing." in home, home)
        val detail = byName.getValue("DetailPage.kt").content
        assertTrue("onBack: () -> Unit," in detail && "onBack = onBack" in detail, detail)
        assertTrue("CouiPage(" in detail && "overlays = {" in detail && "DialogButtonBar(" in detail && "recommended = true" in detail, detail)
        val nav = byName.getValue("Navigation.kt").content
        assertTrue("sealed interface Route : NavKey" in nav && "rememberNavBackStack<Route>(Route.Home)" in nav, nav)
        assertTrue("entry<Route.Detail>" in nav && "backStack.open(Route.Detail)" in nav, nav)
        val app = byName.getValue("App.kt").content
        assertTrue("ColorSchemeMode.System" in app && "CouiPage.kt" in app, app)

        val xml = files.first { it.path.endsWith("values/strings_canvas.xml") }.content
        val keys = Regex("<string name=\"([a-z0-9_]+)\">").findAll(xml).map { it.groupValues[1] }.toSet()
        files.filter { it.path.endsWith(".kt") }.forEach { f ->
            Regex("Res\\.string\\.([A-Za-z0-9_]+)").findAll(f.content).forEach { m -> assertTrue(m.groupValues[1] in keys, "${f.path}: missing ${m.groupValues[1]}") }
            assertBalanced(f.path, f.content)
        }
        assertTrue("It\\'s \\\"gone\\\" &amp; 100\\u0025 &lt;lost&gt;" in xml, xml)
        assertTrue("TODO: translate these strings into Simplified Chinese" in files.first { "values-zh" in it.path }.content)
    }

    @Test
    fun multiplatformKeyColourAndChineseLabels() {
        val d = doc().let { d ->
            d.copy(
                theme = DocTheme(platform = DocTheme.PLATFORM_MULTIPLATFORM, keyColor = "#3366ff", bothModes = false, dark = true),
                frames = d.frames.map { if (it.id == "home") it.copy(name = "设置") else it },
                groups = d.groups.map { g -> if (g.id == "g_cat1") g.copy(items = listOf(g.first.copy(label = "常规"))) else g },
            )
        }
        val files = CodeGenerator.generate(d)
        assertTrue(files.any { it.path == "composeApp/src/commonMain/kotlin/com/example/app/Screen1Page.kt" }, files.map { it.path }.toString())
        val app = files.first { it.path.endsWith("App.kt") }.content
        assertTrue("ColorSchemeMode.MonetDark" in app && "Color(0xFF3366FF)" in app, app)
        val en = files.first { it.path.endsWith("values/strings_canvas.xml") }.content
        val zh = files.first { it.path.endsWith("values-zh/strings_canvas.xml") }.content
        assertTrue("TODO: translate these strings into English" in en && "常规" in en && "常规" in zh, en)
        files.filter { it.path.endsWith(".kt") }.forEach { assertBalanced(it.path, it.content) }
    }

    /** A screen with every kind, a sheet with content, a FAB, a snackbar, swipes and every transition. */
    internal fun everything(): CanvasDoc {
        var y = 160f
        fun g(id: String, vararg items: Item, x: Float = 16f, h: Float = 72f, axis: String = Group.AXIS_Y): Group = Group(id, x, y, axis = axis, items = items.toList()).also { y += h }
        val groups = listOf(
            Group("bar", 0f, 0f, items = listOf(Item("bar", "topBar", label = "Library", icon = "Back", icon2 = "Add", actions = mapOf("icon2" to Action("other", Action.TRANSITION_UP))))),
            g("search", Item("search", "searchBar", label = "Search books", action = Action("other", Action.TRANSITION_FADE))),
            g("tabs", Item("tabs", "tabs", tabs = listOf(Tab("All"), Tab("Read")), variant = "contour", actions = mapOf("tab:1" to Action("other")))),
            g("tips", Item("tips", "topTips", label = "Sync is off", supporting = "Turn on")),
            g(
                "radios",
                Item("r1", "radioRow", label = "Small"), Item("r2", "radioRow", label = "Large", checked = true),
                Item("m1", "markRow", label = "Mark A"), Item("m2", "markRow", label = "Mark B"),
                Item("cb", "checkboxRow", label = "Agree", checked = true), Item("sl", "sliderRow", label = "Size", value = 40f, end = "40%"),
                Item("in", "inputRow", label = "Name", supporting = "Your name"),
                h = 6 * 64f + 96f + 16f,
            ),
            g("chips", Item("c1", "chip", label = "Fiction", checked = true), Item("c2", "chip", label = "Poetry", icon = "Album"), axis = Group.AXIS_X, h = 48f),
            g("card", Item("card", "card", label = "Daily pick", supporting = "A book a day", icon = "Album", size2 = 120f, action = Action("other", Action.TRANSITION_NONE)), h = 136f),
            g("img", Item("img", "image", label = "Cover", size2 = 100f), h = 116f),
            g("field", Item("field", "textField", label = "Note", variant = "filled")),
            g("slider", Item("slider", "slider", value = 25f), h = 52f),
            g("p1", Item("p1", "progress", value = 33f), h = 40f),
            g("div", Item("div", "divider"), h = 24f),
            g("empty", Item("empty", "emptyState", label = "No books", supporting = "Add one", end = "Add", note = "Show when the list is empty."), h = 270f),
            g("ib", Item("ib", "iconButton", icon = "Delete"), h = 48f),
            Group("p2", 200f, y - 48f, items = listOf(Item("p2", "progress", variant = "loading"))),
            Group("nav", 0f, 704f, items = listOf(Item("nav", "bottomNav", tabs = listOf(Tab("Books"), Tab("Me", "Contacts")), actions = mapOf("tab:1" to Action("other"))))),
            Group("fab", 280f, 620f, items = listOf(Item("fab", "fab", icon = "Add"))),
            Group("snack", 16f, 560f, items = listOf(Item("snack", "snackbar", label = "Saved 100%", supporting = "Undo"))),
            Group("sheet", 0f, 1950f, items = listOf(Item("sheet", "bottomSheet", label = "Sort by", size2 = 300f))),
            Group("sheetRows", 16f, 2010f, items = listOf(Item("s1", "radioRow", label = "Title"), Item("s2", "radioRow", label = "Author"))),
            Group(
                "dlg",
                16f,
                1700f,
                items = listOf(Item("dlg", "dialog", label = "Reset sorting?", supporting = "Pick one", tabs = listOf(Tab("Cancel"), Tab("Reset"), Tab("OK")))),
            ),
            Group("open", 16f, 2300f, items = listOf(Item("open", "basicRow", label = "Sort by"))),
        )
        return CanvasDoc(
            title = "Books",
            theme = DocTheme(keyColor = "#00AA88", platform = DocTheme.PLATFORM_MULTIPLATFORM),
            frames = listOf(
                Frame(id = "main", name = "Library", length = 2400f, swipe = mapOf("left" to "other")),
                Frame(id = "other", name = "Other", x = dx),
            ),
            groups = groups + Group("o_bar", dx, 0f, items = listOf(Item("o_bar", "topBar", label = "Other", variant = "small", icon = "Back", actions = mapOf("icon" to Action("back"))))),
        )
    }

    @Test
    fun everyKindGeneratesBalancedCodeAndPrompt() {
        val d = everything()
        val plan = Structure.analyze(d)
        val main = plan.screens.first()
        assertEquals("fab", main.fab?.item?.id)
        assertEquals("snack", main.snackbar?.item?.id)
        assertEquals(listOf("sheet", "dlg"), main.overlays.map { it.part.item.id })
        assertEquals(listOf("sheetRows"), main.overlays[0].content.flatMap { b -> b.parts.map { it.group.id } })
        assertEquals("open", main.overlays[0].opener?.id)
        assertTrue(plan.edges.any { it.isSwipe && it.to == "other" })
        assertEquals(Action.TRANSITION_UP, plan.transitionInto("other"))

        val files = CodeGenerator.generate(d)
        val page = files.first { it.path.endsWith("LibraryPage.kt") }.content
        listOf(
            "SearchBar(", "TabRowWithContour(", "TopTips(", "RadioButtonPreference(", "MarkPreference(", "CheckboxPreference(", "SliderPreference(",
            "InputPreference(", "Chip(", "Card(", "TextField(", "Slider(", "LinearProgressIndicator(", "InfiniteProgressIndicator(", "HorizontalDivider(",
            "EmptyState(", "IconButton(", "FloatingActionButton(", "OverlayBottomSheet(", "OverlayDialog(", "neutral = DialogButtonBarAction(",
            "showResetSortingDialog", "showSortBySheet = true", "snackbarHostState", "floatingActionButton = {", "wide = isWideWindow()", "actions = {",
        ).forEach { assertTrue(it in page, "missing $it\n$page") }
        val nav = files.first { it.path.endsWith("Navigation.kt") }.content
        assertTrue("entry<Route.Other>(transition = NavTransitions.Modal)" in nav, nav)
        files.filter { it.path.endsWith(".kt") }.forEach { assertBalanced(it.path, it.content) }
        val keys = Regex("<string name=\"([a-z0-9_]+)\">").findAll(files.first { it.path.endsWith("values/strings_canvas.xml") }.content).map { it.groupValues[1] }.toSet()
        files.filter { it.path.endsWith(".kt") }.forEach { f ->
            Regex("Res\\.string\\.([A-Za-z0-9_]+)").findAll(f.content).forEach { m -> assertTrue(m.groupValues[1] in keys, "${f.path}: missing ${m.groupValues[1]}") }
        }
        for (lang in Lang.entries) {
            val p = PromptGenerator.generate(d, lang)
            listOf("OverlayBottomSheet", "RadioButtonPreference", "FloatingActionButton", "EmptyState", "Show when the list is empty.", "NavTransitions.Modal", "ColorSchemeMode.MonetSystem")
                .forEach { assertTrue(it in p, "$lang misses $it\n$p") }
        }
    }

    @Test
    fun escaping() {
        assertEquals("It\\'s \\\"x\\\" &amp; &lt;b&gt; 5\\u0025\\nnext", escapeXml("It's \"x\" & <b> 5%\nnext"))
        assertEquals("\\@home", escapeXml("@home"))
    }

    @Test
    fun frameIdentifiersAreUnique() {
        val names = listOf("Home", "Home", "设置", "", "home page", "2fa", "Route", "My-settings screen")
        val frames = names.mapIndexed { i, n -> Frame(id = "f$i", name = n) }
        val ids = Structure.frameIdentifiers(frames)
        assertEquals(listOf("Home", "Home2", "Screen3", "Screen4", "Home3", "S2fa", "RouteScreen", "MySettings"), frames.map { ids.getValue(it.id) })
        assertEquals(ids.size, ids.values.toSet().size)
    }

    @Test
    fun jsonRoundTripAndRepair() {
        val d = doc()
        val parsed = parseDoc(d.toJson())
        assertEquals(d, parsed.doc)
        assertTrue(parsed.problems.isEmpty(), parsed.problems.toString())
        assertEquals(d, parseDoc(d.toJson(pretty = true)).doc)

        val broken = d.copy(
            groups = d.groups + Group("g_bad", 0f, 500f, items = listOf(Item("bad", "hologram"))) +
                Group("g_dead", 0f, 600f, items = listOf(Item("dead", "arrowRow", label = "Nowhere", action = Action("missing")))),
        )
        val fixed = repair(broken)
        val doc = assertNotNull(fixed.doc)
        assertNull(doc.groups.firstOrNull { it.id == "g_bad" })
        assertNull(doc.groups.first { it.id == "g_dead" }.first.action)
        assertTrue(fixed.problems.any { it.type == Problem.Type.UnknownKind && it.subject == "hologram" })
        assertTrue(fixed.problems.any { it.type == Problem.Type.MissingTarget && it.subject == "missing" })
        assertTrue(fixed.problems.any { it.type == Problem.Type.EmptyGroup })
        assertNull(parseDoc("not json").doc)
        assertEquals(Kind.SwitchRow, Kind.of("switchRow"))
    }

    /** Braces, brackets and parentheses balance outside string literals and comments. */
    private fun assertBalanced(path: String, code: String) {
        val stack = ArrayDeque<Char>()
        var i = 0
        while (i < code.length) {
            val c = code[i]
            when {
                code.startsWith("//", i) -> i = code.indexOf('\n', i).let { if (it < 0) code.length else it }

                code.startsWith("/*", i) -> i = code.indexOf("*/", i) + 1

                c == '"' -> {
                    i++
                    while (i < code.length && code[i] != '"') {
                        if (code[i] == '\\') i++
                        i++
                    }
                }

                c == '(' || c == '{' || c == '[' -> stack.addLast(c)

                c == ')' || c == '}' || c == ']' -> {
                    val open = stack.removeLastOrNull()
                    val want = when (c) {
                        ')' -> '('
                        '}' -> '{'
                        else -> '['
                    }
                    assertEquals(want, open, "$path: unbalanced '$c' at $i\n$code")
                }
            }
            i++
        }
        assertTrue(stack.isEmpty(), "$path: unclosed $stack\n$code")
    }
}
