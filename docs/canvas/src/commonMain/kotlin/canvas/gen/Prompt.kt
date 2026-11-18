// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.gen

import canvas.model.Action
import canvas.model.Box
import canvas.model.CanvasDoc
import canvas.model.DocTheme
import canvas.model.Group
import canvas.model.Item
import canvas.model.Kind
import canvas.model.estimatedBox
import kotlin.math.roundToInt

/** The language of a generated prompt. */
enum class Lang { En, Zh }

/** Where AI tools without the `coui-app` skill learn the library. */
object CouiLinks {
    const val LLMS_TXT = "https://suqi8.github.io/coui/llms.txt"
    const val AI_GUIDE = "https://suqi8.github.io/coui/guide/ai"
    const val GROUP = "io.github.suqi8.coui.kmp"
    const val SKILL = "coui-app"
}

/** Writes the build brief an AI coding tool (Claude Code, Codex, Cursor) turns into the app. */
object PromptGenerator {
    /**
     * Returns the brief as Markdown.
     *
     * @param frameId limits the screens to this one (the app and theme header stays).
     * @param boxOf the measured box of a group; the estimate before the canvas has drawn it.
     */
    fun generate(doc: CanvasDoc, lang: Lang, frameId: String? = null, boxOf: (Group) -> Box = { doc.estimatedBox(it) }): String {
        val plan = Structure.analyze(doc, boxOf)
        return PromptWriter(doc, plan, lang).write(frameId)
    }
}

private class PromptWriter(val doc: CanvasDoc, val plan: AppPlan, val lang: Lang) {
    private val out = StringBuilder()
    private val zh = lang == Lang.Zh

    private fun t(en: String, zh: String): String = if (this.zh) zh else en

    private fun line(s: String = "") {
        out.append(s).append('\n')
    }

    private fun q(s: String): String = if (zh) "“$s”" else "\"$s\""

    private fun code(s: String): String = "`$s`"

    /** A parenthetical in the prose language: ` (x)` or `（x）`. */
    private fun p(s: String): String = if (zh) "（$s）" else " ($s)"

    /** The clause separator. */
    private val comma: String get() = if (zh) "，" else ", "

    /** The list separator. */
    private val lj: String get() = if (zh) "、" else ", "

    private fun icon(name: String): String = code("COUIIcons.$name")

    fun write(frameId: String?): String {
        header()
        val screens = if (frameId == null) plan.screens else plan.screens.filter { it.frame.id == frameId }
        if (screens.isNotEmpty()) {
            line(t("## Screens", "## 页面"))
            line()
            screens.forEachIndexed { i, s -> screen(s, plan.screens.indexOf(s) + 1, single = frameId != null && i == 0) }
        }
        if (frameId == null) navigation()
        rules(frameId != null)
        return out.toString().trimEnd() + "\n"
    }

    private fun header() {
        val title = doc.title.ifBlank { t("Untitled app", "未命名应用") }
        line("# $title")
        line()
        if (doc.brief.isNotBlank()) {
            line(doc.brief.trim())
            line()
        }
        val theme = doc.theme
        val platform = if (theme.platform == DocTheme.PLATFORM_MULTIPLATFORM) {
            t("Compose Multiplatform app; UI in `commonMain`.", "Compose Multiplatform 应用；UI 写在 `commonMain`。")
        } else {
            t("Android app (Kotlin, Jetpack Compose).", "Android 应用（Kotlin + Jetpack Compose）。")
        }
        // A key colour only takes effect in the Monet modes of ThemeController.
        val monet = if (theme.keyColor != null) "Monet" else ""
        val mode = when {
            theme.bothModes -> t("light and dark, following the system", "浅色与深色，跟随系统") + p(code("ColorSchemeMode.${monet}System"))
            theme.dark -> t("dark only", "仅深色") + p(code("ColorSchemeMode.${monet}Dark"))
            else -> t("light only", "仅浅色") + p(code("ColorSchemeMode.${monet}Light"))
        }
        val key = theme.keyColor?.let { t("key colour `$it`", "主题色 `$it`") + p(code("ThemeController(…, keyColor = …)")) }
            ?: t("the default COUI blue", "默认 COUI 蓝")
        line(t("## Build", "## 构建要求"))
        line()
        line(t("- Target: $platform", "- 目标：$platform"))
        line(t("- Theme: $mode; $key.", "- 主题：$mode；$key。"))
        line(
            t(
                "- Build it with the COUI library `${CouiLinks.GROUP}` (ColorOS 17 design: coui-ui, coui-preference, coui-icons, coui-blur, coui-nav). " +
                    "Follow the `${CouiLinks.SKILL}` skill if you have it; otherwise read ${CouiLinks.LLMS_TXT} and ${CouiLinks.AI_GUIDE} first.",
                "- 使用 COUI 组件库 `${CouiLinks.GROUP}`（ColorOS 17 设计：coui-ui、coui-preference、coui-icons、coui-blur、coui-nav）。" +
                    "有 `${CouiLinks.SKILL}` 技能就按它写；没有就先读 ${CouiLinks.LLMS_TXT} 和 ${CouiLinks.AI_GUIDE}。",
            ),
        )
        line()
    }

    private fun screen(s: ScreenPlan, number: Int, single: Boolean) {
        val f = s.frame
        val tags = buildList {
            add(code("${s.ident}Page"))
            if (s == plan.start && !single) add(t("start screen", "启动页"))
            if (f.isWide) add(t("wide window ${f.w.roundToInt()}×${f.h.roundToInt()} dp", "宽屏 ${f.w.roundToInt()}×${f.h.roundToInt()} dp"))
            if (f.bg == "card") add(t("card-coloured background", "卡片色背景"))
        }
        line("### $number. ${f.name.ifBlank { s.ident }}" + p(tags.joinToString(lj)))
        line()
        f.note?.takeIf { it.isNotBlank() }?.let {
            line(t("Purpose: ", "用途：") + it.trim())
            line()
        }
        val page = if (s.bottomNav != null || s.fab != null || s.topBar == null) "CouiPageScaffold" else "CouiPage"
        line(t("- Page: ", "- 页面骨架：") + code(page) + if (s.topBar == null) t(" without a top bar.", "，无顶栏。") else "")
        s.topBar?.let { line(t("- Top bar: ", "- 顶栏：") + describe(it, it.item, s)) }
        if (s.body.isNotEmpty()) {
            line(t("- Body, top to bottom:", "- 内容（从上到下）："))
            s.body.forEachIndexed { i, b -> block(b, "  ${i + 1}. ", "     ", s) }
        }
        s.bottomNav?.let { line(t("- Bottom: ", "- 底部：") + describe(it, it.item, s)) }
        s.fab?.let { line(t("- FAB (scaffold `floatingActionButton` slot): ", "- 悬浮按钮（Scaffold 的 `floatingActionButton` 槽）：") + describe(it, it.item, s)) }
        s.snackbar?.let { line(t("- Snackbar (page `SnackbarHostState`): ", "- Snackbar（页面的 `SnackbarHostState`）：") + describe(it, it.item, s)) }
        s.overlays.forEach { o ->
            val opener = o.opener?.let { t("; opened by tapping ${q(it.label)}", "；点击 ${q(it.label)} 打开") }
                ?: t("; drawn open in the design — open it from the tap its notes describe", "；设计稿里是打开状态——由备注描述的点击打开")
            line(
                t("- Overlay (in the page's `overlays`, `show` flag in page state, closed by default$opener): ", "- 弹层（放在页面 `overlays` 里，页面状态持有 `show`，默认关闭$opener）：") +
                    describe(o.part, o.part.item, s),
            )
            o.content.forEachIndexed { i, b -> block(b, "  ${i + 1}. ", "     ", s) }
        }
        val swipes = plan.edgesFrom(f.id).filter { it.isSwipe }
        swipes.forEach { e -> line(t("- Swipe ${e.trigger.removePrefix(NavEdge.SWIPE)} -> ", "- ${swipeZh(e.trigger.removePrefix(NavEdge.SWIPE))}滑动 -> ") + target(e)) }
        line()
    }

    private fun swipeZh(dir: String): String = when (dir) {
        "left" -> "向左"
        "right" -> "向右"
        "up" -> "向上"
        "down" -> "向下"
        else -> dir
    }

    private fun block(b: Block, first: String, indent: String, s: ScreenPlan) {
        when (b) {
            is Block.CardRun -> {
                val head = b.category?.let { code("PreferenceCategory") + " " + q(it.item.label) + note(it.item) + " + " } ?: ""
                line(first + head + t("one card (`Card` + `CardListGroup`) of ${b.run.items.size} rows:", "一张卡片（`Card` + `CardListGroup`），${b.run.items.size} 行："))
                rows(b.run, indent, s)
            }

            is Block.Single -> {
                if (b.part.items.size > 1) {
                    line(first + t("side by side in one `Row`:", "同一行（`Row`）并排："))
                    b.part.items.forEach { line("$indent- " + describe(b.part, it, s)) }
                } else {
                    line(first + describe(b.part, b.part.item, s))
                }
            }

            is Block.Line -> {
                line(first + t("side by side, left to right:", "并排（从左到右）："))
                b.parts.forEach { p -> partLines(p, "$indent- ", s) }
            }

            is Block.Grid -> {
                line(first + t("grid of ${b.lines.size} rows × ${b.columns} columns:", "${b.lines.size} 行 × ${b.columns} 列的网格："))
                b.lines.forEach { l ->
                    line("$indent- " + l.joinToString(" | ") { p -> p.items.joinToString(", ") { describe(p, it, s) } })
                }
            }
        }
    }

    private fun partLines(p: Part, prefix: String, s: ScreenPlan) {
        if (p.isRowRun) {
            line(prefix + t("a card (`Card` + `CardListGroup`):", "一张卡片（`Card` + `CardListGroup`）："))
            rows(p, "  " + " ".repeat(prefix.length - 2), s)
        } else {
            p.items.forEach { line(prefix + describe(p, it, s)) }
        }
    }

    private fun rows(run: Part, indent: String, s: ScreenPlan) {
        run.items.forEach { line("$indent- " + describe(run, it, s)) }
    }

    private fun note(item: Item): String = item.note?.takeIf { it.isNotBlank() }?.let { t(" — note: ", " ——备注：") + it.trim() } ?: ""

    private fun target(e: NavEdge): String {
        if (e.isBack) return t("back", "返回上一页")
        val screen = plan.screen(e.to)
        val name = screen?.frame?.name?.ifBlank { screen.ident } ?: e.to
        return "**$name**" + p(transition(e.transition))
    }

    private fun transition(tr: String): String = when (tr) {
        Action.TRANSITION_UP -> t("slides up, `NavTransitions.Modal`", "从底部上滑，`NavTransitions.Modal`")
        Action.TRANSITION_FADE -> t("fades in", "淡入")
        Action.TRANSITION_NONE -> t("no animation, `NavTransitions.None`", "无动画，`NavTransitions.None`")
        else -> t("push, `NavTransitions.COUIDefault`", "推入，`NavTransitions.COUIDefault`")
    }

    /** The tap destination of [item] (for [trigger]) on [s], as ` -> target`. */
    private fun goes(s: ScreenPlan, item: Item, trigger: String = NavEdge.TAP): String {
        val e = plan.edgesFrom(s.frame.id).firstOrNull { it.item?.id == item.id && it.trigger == trigger } ?: return ""
        return (if (e.isBack) " -> " else t(" -> opens ", " -> 打开 ")) + target(e)
    }

    private fun onOff(b: Boolean?, on: String, off: String): String = if (b == true) on else off

    /** `, label "value"` (or `，标签“value”`) when [value] is set. */
    private fun opt(en: String, zh: String, value: String?): String = value?.takeIf { it.isNotBlank() }?.let { if (this.zh) "，$zh ${q(it)}" else ", $en ${q(it)}" } ?: ""

    private fun iconOpt(name: String?): String = name?.let { t(", icon ", "，图标 ") + icon(it) } ?: ""

    /** One part described: the COUI component, its texts, state, icons, where a tap goes, and the author's note. */
    private fun describe(part: Part, item: Item, s: ScreenPlan): String {
        val kind = Kind.of(item.kind) ?: return q(item.label)
        val text = when (kind) {
            Kind.TopBar -> topBar(item, s)

            Kind.SearchBar -> code("SearchBar") + " + " + code("InputField") + t(", placeholder ", "，占位文字 ") + q(item.label) + goes(s, item)

            Kind.Tabs -> {
                val comp = if (item.variant == "contour") "TabRowWithContour" else "TabRow"
                code(comp) + t(" with tabs ", "，标签 ") + tabList(item, s, selectedDefault = 0)
            }

            Kind.BottomNav -> code("FloatingNavigationBar") + t(" with items ", "，项 ") + tabList(item, s, selectedDefault = 0, icons = true)

            Kind.Category -> code("PreferenceCategory") + " " + q(item.label)

            Kind.SwitchRow -> code("SwitchPreference") + " " + q(item.label) + summary(item) + iconOpt(item.icon) + comma + onOff(item.checked, t("on", "开"), t("off", "关"))

            Kind.ArrowRow -> code("ArrowPreference") + " " + q(item.label) + summary(item) + opt("value", "值", item.end) + iconOpt(item.icon) + goes(s, item)

            Kind.BasicRow -> code("BasicComponent") + " " + q(item.label) + summary(item) + opt("value", "值", item.end) + iconOpt(item.icon) + goes(s, item)

            Kind.DropdownRow -> {
                val options = item.tabs.orEmpty()
                val sel = options.getOrNull(item.selected ?: 0)?.label
                code("OverlayDropdownPreference") + " " + q(item.label) + summary(item) + iconOpt(item.icon) +
                    t(", options ", "，选项 ") + options.joinToString(lj) { q(it.label) } + (sel?.let { t(", selected ", "，选中 ") + q(it) } ?: "")
            }

            Kind.CheckboxRow -> code("CheckboxPreference") + " " + q(item.label) + summary(item) + comma + onOff(item.checked, t("checked", "已勾选"), t("unchecked", "未勾选"))

            Kind.RadioRow -> code("RadioButtonPreference") + " " + q(item.label) + summary(item) + comma + onOff(item.checked, t("selected", "选中"), t("not selected", "未选中")) +
                t(" (one choice among the radio rows of this card)", "（本卡片单选行中只能选一个）").takeIf { part.items.count { Kind.of(it.kind) == Kind.RadioRow } > 1 && item == part.items.first { Kind.of(it.kind) == Kind.RadioRow } }.orEmpty()

            Kind.MarkRow -> code("MarkPreference") + " " + q(item.label) + summary(item) + comma + onOff(item.checked, t("marked", "打勾"), t("not marked", "未打勾"))

            Kind.SliderRow -> code("SliderPreference") + " " + q(item.label) + summary(item) + t(", value ", "，数值 ") + pct(item.value) + opt("value text", "数值文字", item.end)

            Kind.InputRow -> code("InputPreference") + " " + q(item.label) + opt("hint", "提示", item.supporting)

            Kind.Button -> {
                val colors = if (item.variant == "secondary") code("ButtonDefaults.textButtonColors()") else code("ButtonDefaults.textButtonColorsPrimary()")
                code("TextButton") + " " + q(item.label) + p((if (item.variant == "secondary") t("secondary", "次要") else t("primary", "主要")) + comma + colors) +
                    (item.size?.let { t(", ${it.roundToInt()} dp wide", "，宽 ${it.roundToInt()} dp") } ?: "") + goes(s, item)
            }

            Kind.IconButton -> code("IconButton") + t(" with ", "，图标 ") + icon(item.icon ?: FALLBACK_ICON) + goes(s, item)

            Kind.Fab -> code("FloatingActionButton") + t(" with ", "，图标 ") + icon(item.icon ?: "Add") + goes(s, item)

            Kind.Chip -> code("Chip") + " " + q(item.label) + iconOpt(item.icon) + comma + onOff(item.checked, t("selected", "选中"), t("not selected", "未选中")) + goes(s, item)

            Kind.Card -> code("Card") + t(" with title ", "，标题 ") + q(item.label) + opt("text", "正文", item.supporting) + iconOpt(item.icon) +
                (item.size2?.let { t(", ${it.roundToInt()} dp tall", "，高 ${it.roundToInt()} dp") } ?: "") + goes(s, item)

            Kind.Text -> {
                val style = when (item.variant) {
                    "title" -> "COUITheme.textStyles.headlineXS"
                    "caption" -> "COUITheme.textStyles.bodyS"
                    else -> "COUITheme.textStyles.bodyL"
                }
                code("Text") + " " + q(item.label) + p(code(style))
            }

            Kind.Image -> t("image", "图片") + (if (item.label.isNotBlank()) " " + q(item.label) else "") +
                comma + "${(item.size ?: part.box.w).roundToInt()}×${(item.size2 ?: part.box.h).roundToInt()} dp" + t(" (artwork to supply)", "（需提供图片资源）")

            Kind.TopTips -> code("TopTips") + " " + q(item.label) + opt("action", "操作", item.supporting) + goes(s, item)

            Kind.EmptyState -> code("EmptyState") + " " + q(item.label) + opt("subtitle", "副标题", item.supporting) + opt("action button", "按钮", item.end) + goes(s, item)

            Kind.TextField -> code("TextField") + p(code(if (item.variant == "filled") "TextFieldMode.Rectangle" else "TextFieldMode.Line")) + t(", label ", "，标签 ") + q(item.label) +
                opt("hint", "提示", item.supporting)

            Kind.Slider -> code("Slider") + t(", value ", "，数值 ") + pct(item.value)

            Kind.Progress -> when (item.variant) {
                "circular" -> code("CircularProgressIndicator") + t(" at ", "，进度 ") + pct(item.value)
                "loading" -> code("InfiniteProgressIndicator") + t(" (indeterminate)", "（不确定进度）")
                else -> code("LinearProgressIndicator") + t(" at ", "，进度 ") + pct(item.value)
            }

            Kind.Divider -> code("HorizontalDivider")

            Kind.Dialog -> dialog(item, s)

            Kind.BottomSheet -> code("OverlayBottomSheet") + (if (item.label.isNotBlank()) t(" titled ", "，标题 ") + q(item.label) else "") +
                t(", content below", "，内容如下")

            Kind.Snackbar -> q(item.label) + opt("action", "操作", item.supporting)
        }
        return text + note(item)
    }

    private fun summary(item: Item): String = opt("summary", "摘要", item.supporting)

    private fun pct(v: Float?): String = "${(v ?: 0f).roundToInt()}%"

    private fun tabList(item: Item, s: ScreenPlan, selectedDefault: Int, icons: Boolean = false): String {
        val sel = item.selected ?: selectedDefault
        return item.tabs.orEmpty().mapIndexed { i, tab ->
            q(tab.label) + listOfNotNull(
                (tab.icon ?: if (icons) FALLBACK_ICON else null)?.let { icon(it) },
                if (i == sel) t("selected", "选中") else null,
            ).takeIf { it.isNotEmpty() }?.let { p(it.joinToString(comma)) }.orEmpty() + goes(s, item, NavEdge.TAB + i)
        }.joinToString(lj)
    }

    private fun topBar(item: Item, s: ScreenPlan): String {
        val parts = mutableListOf<String>()
        when (item.variant) {
            "home" -> {
                parts += t("ColorOS Settings home bar", "ColorOS 设置首页顶栏") + p(code("CouiTopBarStyle.SettingsHome")) + t(" titled ", "，标题 ") + q(item.label)
                val opensPage = plan.edgesFrom(s.frame.id).any { it.item?.id == item.id && it.trigger == NavEdge.SEARCH }
                val hint = item.supporting?.takeIf { it.isNotBlank() }?.let { t(" with placeholder ", "，占位文字 ") + q(it) } ?: ""
                parts += if (opensPage) {
                    t("search capsule", "搜索胶囊") + p(code("TopAppBarSearchSlot") + " + " + code("InputField")) + hint + goes(s, item, NavEdge.SEARCH)
                } else {
                    t("search bar", "搜索栏") + p(code("TopAppBarSearchBar") + " + " + code("rememberTopAppBarSearchState")) + hint +
                        t(
                            " that searches in place, linked with the top bar like the ColorOS Settings search (the title fades, the bar rises with a cancel button, the page fades out and the results come in)",
                            "，原地搜索并与顶栏联动，和 ColorOS 设置的搜索一样（标题淡出、搜索栏带着取消按钮升到顶部、页面淡出、结果从下方进入）",
                        )
                }
            }

            "small" -> parts += t("small pinned bar", "小标题固定栏") + p(code("CouiTopBarStyle.Small")) + t(" titled ", "，标题 ") + q(item.label) +
                opt("subtitle", "副标题", item.supporting)

            else -> parts += t("collapsing large title", "可折叠大标题") + p(code("CouiTopBarStyle.Large") + comma + code("wide = isWideWindow()")) +
                t(" titled ", "，标题 ") + q(item.label) + opt("subtitle", "副标题", item.supporting)
        }
        item.icon?.let { name ->
            val e = plan.edgesFrom(s.frame.id).firstOrNull { it.item?.id == item.id && it.trigger == NavEdge.ICON }
            parts += t("navigation icon ", "导航图标 ") + icon(name) + (if (e != null) (if (e.isBack) t(" -> back (`onBack`)", " -> 返回（`onBack`）") else t(" -> opens ", " -> 打开 ") + target(e)) else "")
        }
        item.icon2?.let { name -> parts += t("trailing action ", "右侧操作 ") + icon(name) + goes(s, item, NavEdge.ICON2) }
        return parts.joinToString(if (zh) "；" else "; ")
    }

    private fun dialog(item: Item, s: ScreenPlan): String {
        val buttons = item.tabs.orEmpty()
        val slots = dialogSlots(buttons.size)
        val bar = if (buttons.isEmpty()) {
            item.end?.let { t(", one button ", "，一个按钮 ") + q(it) + p(code("positive")) } ?: ""
        } else {
            t(", `DialogButtonBar` buttons ", "，`DialogButtonBar` 按钮 ") + buttons.mapIndexed { i, b ->
                val slot = slots[i]
                q(b.label) + p(code(slot) + (if (slot == "positive" && buttons.size > 1) comma + t("recommended", "推荐") else "")) + goes(s, item, NavEdge.TAB + i)
            }.joinToString(lj)
        }
        return code("OverlayDialog") + t(" titled ", "，标题 ") + q(item.label) + opt("message", "内容", item.supporting) + bar
    }

    private fun navigation() {
        val edges = plan.edges
        line(t("## Navigation", "## 导航"))
        line()
        line(
            t(
                "Use coui-nav: a `@Serializable sealed interface Route : NavKey` with one entry per screen, `NavDisplay` + `rememberNavBackStack<Route>(…)`; system back and swipe back pop the stack.",
                "使用 coui-nav：`@Serializable sealed interface Route : NavKey`，每个页面一个 entry，`NavDisplay` + `rememberNavBackStack<Route>(…)`；系统返回与侧滑返回出栈。",
            ),
        )
        line()
        plan.start?.let { line(t("- Start: ", "- 启动页：") + "**${it.frame.name.ifBlank { it.ident }}**" + p(code("Route.${it.ident}"))) }
        edges.forEach { e ->
            val from = plan.screen(e.from) ?: return@forEach
            val what = when {
                e.isSwipe -> t("swipe ${e.trigger.removePrefix(NavEdge.SWIPE)}", "${swipeZh(e.trigger.removePrefix(NavEdge.SWIPE))}滑动")
                e.item != null && e.trigger == NavEdge.ICON -> t("top bar navigation icon", "顶栏导航图标")
                e.item != null && e.trigger == NavEdge.SEARCH -> t("search capsule", "搜索胶囊")
                e.item != null && e.trigger == NavEdge.ICON2 -> t("top bar action ", "顶栏操作 ") + icon(e.label)
                else -> t("tap ", "点击 ") + q(e.label.ifBlank { e.item?.icon ?: "" })
            }
            line("- **${from.frame.name.ifBlank { from.ident }}**" + t(": ", "：") + "$what -> ${target(e)}")
        }
        line()
    }

    private fun rules(single: Boolean) {
        line(t("## Ground rules", "## 基本规则"))
        line()
        val rules = listOf(
            t(
                "Each screen is a page from the skill's scaffold (`CouiPage`, or `CouiPageScaffold` with a bottom bar / FAB) inside `COUITheme`; navigation via callbacks (`onBack`, `onOpenX`).",
                "每个页面用技能脚手架里的 `CouiPage`（有底栏 / 悬浮按钮时用 `CouiPageScaffold`），套在 `COUITheme` 里；导航通过回调参数（`onBack`、`onOpenX`）。",
            ),
            t(
                "Rows live in `Card { CardListGroup { item { … } } }`, each `Card` with `Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)`; never draw rows, dividers or row paddings by hand.",
                "列表行放在 `Card { CardListGroup { item { … } } }` 里，每个 `Card` 加 `Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)`；不要手绘行、分割线或行内边距。",
            ),
            t(
                "Dialogs and sheets go in the page's `overlays` (never inside a lazy item) with a `show` flag in page state; dialog buttons via `DialogButtonBar`.",
                "对话框和底部面板放在页面的 `overlays` 里（不要放进 lazy item），`show` 标志由页面状态持有；对话框按钮用 `DialogButtonBar`。",
            ),
            t(
                "Colours only from `COUITheme.colorScheme`, text styles only from `COUITheme.textStyles`; no hex colours.",
                "颜色只用 `COUITheme.colorScheme`，字体只用 `COUITheme.textStyles`；不写十六进制颜色。",
            ),
            t(
                "Every user-visible text is a Compose resource in `composeResources/values/` (English) and `values-zh/` (Simplified Chinese).",
                "所有用户可见文字放进 Compose 资源：`composeResources/values/`（英文）和 `values-zh/`（简体中文）。",
            ),
            t(
                "Icons are `COUIIcons.<Name>` from coui-icons (`io.github.suqi8.coui.kmp.icon.extended.<Name>`).",
                "图标用 coui-icons 的 `COUIIcons.<Name>`（`io.github.suqi8.coui.kmp.icon.extended.<Name>`）。",
            ),
            t(
                "A search typed into under a top bar is linked with the bar like the ColorOS Settings search: `TopAppBarSearchBar` + `rememberTopAppBarSearchState` on `CouiPageScaffold`, the page under `Modifier.topAppBarSearchContent`, the results in their own list under `Modifier.topAppBarSearchResults`; never a static capsule that filters in place.",
                "顶栏下方可输入的搜索要像 ColorOS 设置的搜索一样与顶栏联动：在 `CouiPageScaffold` 上用 `TopAppBarSearchBar` + `rememberTopAppBarSearchState`，页面挂 `Modifier.topAppBarSearchContent`，结果放独立列表并挂 `Modifier.topAppBarSearchResults`；不要用原地过滤的静态胶囊。",
            ),
            t(
                "A list popup shares a `Box` with the button that opens it (`Box { IconButton(…); OverlayListPopup(…) }`); directly in a top bar `actions` row it pushes the button aside while shown.",
                "列表弹出菜单和打开它的按钮一起包在 `Box` 里（`Box { IconButton(…); OverlayListPopup(…) }`）；直接放进顶栏 `actions` 行时，显示期间会把按钮挤开。",
            ),
            t(
                "Check each component's signature in the library source / KDoc before using it; do not invent parameters.",
                "使用组件前先看库源码 / KDoc 里的签名，不要臆造参数。",
            ),
        ) + if (single) listOf(t("Only this screen is described; keep the rest of the app as it is.", "这里只描述了这一个页面；应用的其他部分保持不变。")) else emptyList()
        rules.forEach { line("- $it") }
    }

    companion object {
        const val FALLBACK_ICON = "All"
    }
}

/** The `DialogButtonBar` slot of each of [count] dialog buttons: the last one is the positive one. */
internal fun dialogSlots(count: Int): List<String> = when (count) {
    0 -> emptyList()
    1 -> listOf("positive")
    2 -> listOf("negative", "positive")
    else -> listOf("negative", "neutral", "positive") + List(count - 3) { "extra" }
}
