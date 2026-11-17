// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import canvas.model.Action
import canvas.model.CanvasDoc
import canvas.model.DocTheme
import canvas.model.Frame
import canvas.model.Group
import canvas.model.Item
import canvas.model.Kind
import canvas.model.MARGIN
import canvas.model.Tab

/** A new part of [kind] with sample texts in the editor language. */
fun defaultItem(kind: Kind, id: String, zh: Boolean): Item {
    fun t(en: String, cn: String) = if (zh) cn else en
    val k = kind.key
    return when (kind) {
        Kind.TopBar -> Item(id, k, t("Settings", "设置"), variant = "large")

        Kind.SearchBar -> Item(id, k, t("Search", "搜索"))

        Kind.Tabs -> Item(id, k, tabs = listOf(Tab(t("All", "全部")), Tab(t("Photos", "照片")), Tab(t("Albums", "图集"))), selected = 0)

        Kind.BottomNav -> Item(
            id,
            k,
            tabs = listOf(Tab(t("Home", "首页"), "Home"), Tab(t("Explore", "发现"), "Store"), Tab(t("Me", "我的"), "Contacts")),
            selected = 0,
        )

        Kind.Category -> Item(id, k, t("General", "通用"))

        Kind.SwitchRow -> Item(id, k, t("Switch", "开关"), checked = true)

        Kind.ArrowRow -> Item(id, k, t("Open a page", "进入页面"))

        Kind.BasicRow -> Item(id, k, t("Clear cache", "清除缓存"), end = "128 MB")

        Kind.DropdownRow -> Item(id, k, t("Text size", "字体大小"), tabs = listOf(Tab(t("Small", "小")), Tab(t("Medium", "中")), Tab(t("Large", "大"))), selected = 1)

        Kind.CheckboxRow -> Item(id, k, t("Option", "选项"), checked = true)

        Kind.RadioRow -> Item(id, k, t("Option", "选项"), checked = false)

        Kind.MarkRow -> Item(id, k, t("Option", "选项"), checked = true)

        Kind.SliderRow -> Item(id, k, t("Brightness", "亮度"), value = 60f)

        Kind.InputRow -> Item(id, k, t("Name", "名称"), supporting = t("Enter a name", "输入名称"))

        Kind.Button -> Item(id, k, t("Confirm", "确定"), variant = "primary")

        Kind.IconButton -> Item(id, k, icon = "Add")

        Kind.Fab -> Item(id, k, icon = "Add")

        Kind.Chip -> Item(id, k, t("Tag", "标签"), checked = false)

        Kind.Card -> Item(id, k, t("Card title", "卡片标题"), supporting = t("A line of body text.", "一行正文内容。"))

        Kind.Text -> Item(id, k, t("Headline", "标题文字"), variant = "title")

        Kind.Image -> Item(id, k, t("Photo", "图片"), size2 = 180f)

        Kind.TopTips -> Item(id, k, t("Battery saver is on", "省电模式已开启"), supporting = t("Turn off", "关闭"))

        Kind.EmptyState -> Item(id, k, t("Nothing here yet", "暂无内容"), supporting = t("Items you add show up here.", "添加的内容会显示在这里。"), end = t("Add", "添加"))

        Kind.TextField -> Item(id, k, t("Enter text", "输入文字"), variant = "line")

        Kind.Slider -> Item(id, k, value = 40f)

        Kind.Progress -> Item(id, k, value = 60f, variant = "linear")

        Kind.Divider -> Item(id, k)

        Kind.Dialog -> Item(
            id,
            k,
            t("Delete this item?", "删除此项？"),
            supporting = t("It can't be restored.", "删除后无法恢复。"),
            tabs = listOf(Tab(t("Cancel", "取消")), Tab(t("Delete", "删除"))),
        )

        Kind.BottomSheet -> Item(id, k, t("Share to", "分享到"), size2 = 360f)

        Kind.Snackbar -> Item(id, k, t("Saved", "已保存"), supporting = t("Undo", "撤销"))
    }
}

/** The design a first visit opens: a small settings app, to show what a sketch looks like. */
fun sampleDoc(zh: Boolean): CanvasDoc {
    fun t(en: String, cn: String) = if (zh) cn else en
    val w = Frame.PHONE_W
    val gap = Frame.GAP
    val home = Frame("home", t("Settings", "设置"), 0f, 0f, note = t("The home page: entries into every settings page.", "首页：进入各个设置页的入口。"))
    val display = Frame("display", t("Display", "显示与亮度"), w + gap, 0f, note = t("Display options.", "显示相关的选项。"))
    val about = Frame("about", t("About", "关于本机"), 2 * (w + gap), 0f)
    val m = MARGIN
    val groups = listOf(
        // Home
        Group("g1", 0f, 0f, items = listOf(Item("bar1", "topBar", t("Settings", "设置"), supporting = t("Search settings", "搜索设置项"), variant = "home"))),
        Group(
            "g2",
            m,
            232f,
            items = listOf(
                Item("wifi", "switchRow", "WLAN", icon = "Location", checked = true),
                Item("bt", "arrowRow", t("Bluetooth", "蓝牙"), icon = "Share", end = t("On", "已开启")),
            ),
        ),
        Group(
            "g3",
            m,
            376f,
            items = listOf(
                Item("disp", "arrowRow", t("Display & brightness", "显示与亮度"), icon = "Theme", action = Action("display")),
                Item("sound", "arrowRow", t("Sounds & vibration", "声音与振动"), icon = "VolumeUp"),
                Item("about", "arrowRow", t("About device", "关于本机"), icon = "Info", action = Action("about")),
            ),
        ),
        // Display
        Group("g4", w + gap, 0f, items = listOf(Item("bar2", "topBar", t("Display & brightness", "显示与亮度"), icon = "Back", action = null, actions = mapOf("icon" to Action(Action.BACK))))),
        Group("g5", w + gap, 184f, items = listOf(Item("cat1", "category", t("Appearance", "外观")))),
        Group(
            "g6",
            w + gap + m,
            224f,
            items = listOf(
                Item("dark", "switchRow", t("Dark mode", "深色模式"), summary(zh), checked = false),
                Item("size", "dropdownRow", t("Text size", "字体大小"), tabs = listOf(Tab(t("Small", "小")), Tab(t("Medium", "中")), Tab(t("Large", "大"))), selected = 1),
            ),
        ),
        Group("g7", w + gap, 384f, items = listOf(Item("cat2", "category", t("Brightness", "亮度")))),
        Group("g8", w + gap + m, 424f, items = listOf(Item("bright", "sliderRow", t("Brightness", "亮度"), value = 70f), Item("auto", "switchRow", t("Auto brightness", "自动调节"), checked = true))),
        // About
        Group("g9", 2 * (w + gap), 0f, items = listOf(Item("bar3", "topBar", t("About device", "关于本机"), icon = "Back", actions = mapOf("icon" to Action(Action.BACK))))),
        Group(
            "g10",
            2 * (w + gap) + m,
            184f,
            items = listOf(
                Item("model", "basicRow", t("Model", "型号"), end = "Find X9"),
                Item("version", "basicRow", t("Version", "版本"), end = "ColorOS 17"),
                Item("reset", "basicRow", t("Reset", "恢复出厂设置"), note = t("Asks for confirmation before resetting.", "重置前先弹窗确认。")),
            ),
        ),
        Group(
            "g11",
            2 * (w + gap) + 16f,
            600f,
            items = listOf(Item("dlg", "dialog", t("Reset this device?", "恢复出厂设置？"), supporting = t("All data will be erased.", "所有数据都将被清除。"), tabs = listOf(Tab(t("Cancel", "取消")), Tab(t("Reset", "重置"))))),
        ),
    )
    return CanvasDoc(
        title = t("Settings", "设置"),
        brief = t("A small settings app.", "一个简单的设置应用。"),
        theme = DocTheme(),
        frames = listOf(home, display, about),
        groups = groups,
    )
}

private fun summary(zh: Boolean): String = if (zh) "跟随系统时自动切换" else "Follows the system when off"
