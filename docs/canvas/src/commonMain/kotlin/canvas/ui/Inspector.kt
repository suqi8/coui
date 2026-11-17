// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import canvas.model.Action
import canvas.model.CanvasDoc
import canvas.model.DocTheme
import canvas.model.Field
import canvas.model.Frame
import canvas.model.Group
import canvas.model.IconNames
import canvas.model.Item
import canvas.model.Kind
import canvas.model.Tab
import canvas.model.detachItem
import canvas.model.frame
import canvas.model.newId
import canvas.model.updateFrame
import canvas.model.updateItem
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.CardListGroupScope
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.basic.TextFieldMode
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Close
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.roundToInt

/** The right panel: what the selection is and every setting it has. */
@Composable
fun Inspector(state: EditorState, modifier: Modifier = Modifier) {
    val doc = state.doc
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 32.dp)) {
        val group = state.selectedGroup
        val item = group?.items?.firstOrNull { it.id == state.selectedItem }
        val frame = doc.frame(state.selectedFrame)
        when {
            group != null && item != null -> ItemInspector(state, group, item)
            frame != null -> FrameInspector(state, frame)
            else -> DocInspector(state, doc)
        }
    }
}

@Composable
private fun Section(title: String?, content: CardListGroupScope.() -> Unit) {
    Column {
        if (title != null) SmallTitle(title)
        Card(Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)) { CardListGroup(content = content) }
    }
}

/** A labelled text input; [key] merges consecutive keystrokes into one undo step. */
@Composable
private fun Input(label: String, value: String, singleLine: Boolean = true, onChange: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Caption(label, Modifier.padding(bottom = 4.dp))
        TextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            backgroundMode = TextFieldMode.Rectangle,
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 3,
        )
    }
}

@Composable
private fun NumberInput(label: String, value: Float?, placeholder: String, onChange: (Float?) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Caption(label, Modifier.padding(bottom = 4.dp))
        TextField(
            value = value?.let { if (it % 1f == 0f) it.roundToInt().toString() else it.toString() } ?: "",
            onValueChange = { text -> onChange(text.filter { it.isDigit() || it == '.' }.toFloatOrNull()) },
            modifier = Modifier.fillMaxWidth(),
            backgroundMode = TextFieldMode.Rectangle,
            label = placeholder,
            singleLine = true,
        )
    }
}

@Composable
private fun Choice(title: String, options: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    OverlayDropdownPreference(
        items = options,
        selectedIndex = selected.coerceIn(0, (options.size - 1).coerceAtLeast(0)),
        title = title,
        maxHeight = 420.dp,
        onSelectedIndexChange = onSelect,
    )
}

@Composable
private fun IconChoice(title: String, value: String?, allowNone: Boolean = true, onSelect: (String?) -> Unit) {
    val names = (if (allowNone) listOf(tr("None", "无")) else emptyList()) + IconNames
    val index = if (value == null) 0 else IconNames.indexOf(value).let { if (it < 0) 0 else it + (if (allowNone) 1 else 0) }
    OverlayDropdownPreference(
        items = names,
        selectedIndex = index,
        title = title,
        maxHeight = 420.dp,
        startAction = iconFor(value)?.let { v -> @Composable { Icon(v, contentDescription = null, modifier = Modifier.size(22.dp), tint = COUITheme.colorScheme.primary) } },
        onSelectedIndexChange = { i -> onSelect(if (allowNone && i == 0) null else IconNames[i - (if (allowNone) 1 else 0)]) },
    )
}

@Composable
private fun ItemInspector(state: EditorState, group: Group, item: Item) {
    val zh = LocalZh.current
    val kind = Kind.of(item.kind) ?: return
    fun edit(key: String, transform: (Item) -> Item) = state.commit(state.doc.updateItem(item.id) { transform(it) }, key = "$key:${item.id}")

    Column {
        Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
            Text(kindName(kind, zh), style = COUITheme.textStyles.headlineXS, color = COUITheme.colorScheme.labelPrimary)
            Caption(kind.component + if (group.items.size > 1) tr("  ·  in a run of ${group.items.size}", "  ·  属于 ${group.items.size} 项的连组") else "")
        }
        val f = kind.fields
        Section(tr("Content", "内容")) {
            if (Field.Label in f) item { Input(fieldName(Field.Label, kind, zh), item.label) { v -> edit("label") { it.copy(label = v) } } }
            if (Field.Supporting in f) item { Input(fieldName(Field.Supporting, kind, zh), item.supporting.orEmpty()) { v -> edit("supporting") { it.copy(supporting = v.ifEmpty { null }) } } }
            if (Field.End in f) item { Input(fieldName(Field.End, kind, zh), item.end.orEmpty()) { v -> edit("end") { it.copy(end = v.ifEmpty { null }) } } }
            if (Field.Variant in f && kind.variants.isNotEmpty()) {
                item {
                    val i = kind.variants.indexOf(item.variant ?: kind.variants.first()).coerceAtLeast(0)
                    Choice(fieldName(Field.Variant, kind, zh), kind.variants.map { variantName(it, zh) }, i) { v -> edit("variant") { it.copy(variant = kind.variants[v]) } }
                }
            }
            if (Field.Icon in f) {
                item {
                    if (kind == Kind.TopBar) {
                        SwitchPreference(
                            checked = item.icon != null,
                            onCheckedChange = { on -> edit("icon") { it.copy(icon = if (on) "Back" else null) } },
                            title = fieldName(Field.Icon, kind, zh),
                        )
                    } else {
                        IconChoice(fieldName(Field.Icon, kind, zh), item.icon, allowNone = kind != Kind.IconButton && kind != Kind.Fab) { v -> edit("icon") { it.copy(icon = v) } }
                    }
                }
            }
            if (Field.Icon2 in f) item { IconChoice(fieldName(Field.Icon2, kind, zh), item.icon2) { v -> edit("icon2") { it.copy(icon2 = v) } } }
            if (Field.Checked in f) item { SwitchPreference(checked = item.checked == true, onCheckedChange = { v -> edit("checked") { it.copy(checked = v) } }, title = fieldName(Field.Checked, kind, zh)) }
            if (Field.Value in f) {
                item {
                    SliderPreference(
                        value = (item.value ?: 50f) / 100f,
                        onValueChange = { v -> edit("value") { it.copy(value = (v * 100f).roundToInt().toFloat()) } },
                        title = fieldName(Field.Value, kind, zh),
                        valueText = "${(item.value ?: 50f).roundToInt()}",
                    )
                }
            }
            if (Field.Size in f) item { NumberInput(fieldName(Field.Size, kind, zh), item.size, tr("Auto", "自动")) { v -> edit("size") { it.copy(size = v?.coerceIn(24f, 2000f)) } } }
            if (Field.Size2 in f) item { NumberInput(fieldName(Field.Size2, kind, zh), item.size2, tr("Auto", "自动")) { v -> edit("size2") { it.copy(size2 = v?.coerceIn(24f, 4000f)) } } }
        }
        if (Field.Tabs in f) TabsEditor(state, item, kind)
        TapEditor(state, item, kind)
        Section(tr("Note for the prompt", "写给 AI 的说明")) {
            item {
                Input(tr("What it does (goes into the prompt)", "它做什么（会写进提示词）"), item.note.orEmpty(), singleLine = false) { v -> edit("note") { it.copy(note = v.ifEmpty { null }) } }
            }
        }
        Actions(state, group)
    }
}

@Composable
private fun TabsEditor(state: EditorState, item: Item, kind: Kind) {
    val zh = LocalZh.current
    val tabs = item.tabs.orEmpty()
    fun set(next: List<Tab>, key: String = "tabs") = state.commit(state.doc.updateItem(item.id) { it.copy(tabs = next, selected = it.selected?.coerceIn(0, (next.size - 1).coerceAtLeast(0))) }, key = "$key:${item.id}")
    val withIcons = kind == Kind.BottomNav
    Section(fieldName(Field.Tabs, kind, zh)) {
        tabs.forEachIndexed { index, tab ->
            item {
                Row(Modifier.fillMaxWidth().padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        Input(tr("Item ${index + 1}", "第 ${index + 1} 项"), tab.label) { v -> set(tabs.mapIndexed { i, t -> if (i == index) t.copy(label = v) else t }, "tab$index") }
                    }
                    IconButton(onClick = { set(tabs.filterIndexed { i, _ -> i != index }, "remove") }, enabled = tabs.size > 1) {
                        Icon(COUIIcons.Close, contentDescription = tr("Remove", "删除"), tint = COUITheme.colorScheme.labelSecondary)
                    }
                }
            }
            if (withIcons) {
                item { IconChoice(tr("Icon of item ${index + 1}", "第 ${index + 1} 项图标"), tab.icon, allowNone = false) { v -> set(tabs.mapIndexed { i, t -> if (i == index) t.copy(icon = v) else t }, "tabicon") } }
            }
        }
        item {
            Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                TextButton(
                    text = tr("Add item", "添加一项"),
                    onClick = { set(tabs + Tab(tr(zh, "Item ${tabs.size + 1}", "项目 ${tabs.size + 1}"), if (withIcons) "Home" else null), "add") },
                    enabled = !(kind == Kind.BottomNav && tabs.size >= 5) && !(kind == Kind.Dialog && tabs.size >= 3),
                )
            }
        }
        if (Field.Selected in kind.fields && tabs.isNotEmpty()) {
            item {
                Choice(fieldName(Field.Selected, kind, zh), tabs.map { it.label.ifEmpty { "—" } }, item.selected ?: 0) { v ->
                    state.commit(state.doc.updateItem(item.id) { it.copy(selected = v) })
                }
            }
        }
    }
}

/** The tap targets a part has: the part itself, its icons, or each of its tabs. */
private fun tapTargets(item: Item, kind: Kind, zh: Boolean): List<Pair<String, String>> = buildList {
    when (kind) {
        Kind.TopBar -> {
            if (item.icon != null) add("icon" to tr(zh, "Back button", "返回按钮"))
            if (item.icon2 != null) add("icon2" to tr(zh, "Action icon", "操作图标"))
            if (item.variant == "home") add("search" to tr(zh, "Search capsule", "搜索胶囊"))
        }

        Kind.BottomNav, Kind.Tabs, Kind.Dialog -> item.tabs.orEmpty().forEachIndexed { i, t -> add("tab:$i" to t.label.ifEmpty { "#${i + 1}" }) }

        else -> if (kind.tappable) add("" to tr(zh, "Tap", "点击"))
    }
}

@Composable
private fun TapEditor(state: EditorState, item: Item, kind: Kind) {
    val zh = LocalZh.current
    val targets = tapTargets(item, kind, zh)
    if (targets.isEmpty()) return
    val frames = state.doc.frames
    val names = listOf(tr("Nothing", "无"), tr("Go back", "返回上一页")) + frames.map { it.name.ifEmpty { it.id } }
    Section(fieldName(Field.Action, kind, zh)) {
        targets.forEach { (target, label) ->
            val action = if (target.isEmpty()) item.action else item.actions?.get(target)
            item {
                val selected = when {
                    action == null -> 0
                    action.to == Action.BACK -> 1
                    else -> 2 + frames.indexOfFirst { it.id == action.to }.coerceAtLeast(0)
                }
                Choice(label, names, selected) { i ->
                    val next = when (i) {
                        0 -> null
                        1 -> Action(Action.BACK, action?.transition ?: Action.TRANSITION_PUSH)
                        else -> Action(frames[i - 2].id, action?.transition ?: Action.TRANSITION_PUSH)
                    }
                    state.setAction(item.id, target, next)
                }
            }
            if (action != null) {
                item {
                    Choice(tr("Transition", "过渡动画"), Action.TRANSITIONS.map { transitionName(it, zh) }, Action.TRANSITIONS.indexOf(action.transition).coerceAtLeast(0)) { i ->
                        state.setAction(item.id, target, action.copy(transition = Action.TRANSITIONS[i]))
                    }
                }
            }
        }
    }
}

@Composable
private fun Actions(state: EditorState, group: Group) {
    Section(null) {
        item {
            FlowRow(
                Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SmallButton(tr("Duplicate", "复制一份")) { state.duplicateSelection() }
                if (group.items.size > 1) {
                    SmallButton(tr("Split from run", "拆出连组")) {
                        val id = state.selectedItem ?: return@SmallButton
                        state.commit(detach(state.doc, id, state))
                    }
                }
                SmallButton(tr("Bring forward", "上移一层")) { state.reorder(1) }
                SmallButton(tr("Send backward", "下移一层")) { state.reorder(-1) }
                SmallButton(if (group.locked) tr("Unlock", "解锁") else tr("Lock", "锁定")) {
                    state.commit(state.doc.copy(groups = state.doc.groups.map { if (it.id == group.id) it.copy(locked = !it.locked) else it }))
                }
                SmallButton(tr("Delete", "删除"), danger = true) { state.deleteSelection() }
            }
        }
    }
}

private fun detach(doc: CanvasDoc, itemId: String, state: EditorState): CanvasDoc {
    val group = doc.groups.firstOrNull { g -> g.items.any { it.id == itemId } } ?: return doc
    return doc.detachItem(itemId, doc.newId("g"), state.boxOf(group))
}

@Composable
fun SmallButton(text: String, danger: Boolean = false, primary: Boolean = false, onClick: () -> Unit) {
    TextButton(
        text = text,
        onClick = onClick,
        minWidth = 0.dp,
        minHeight = 36.dp,
        insideMargin = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        textStyle = COUITheme.textStyles.buttonS,
        colors = when {
            primary -> ButtonDefaults.textButtonColorsPrimary()
            danger -> ButtonDefaults.textButtonColors(textColor = COUITheme.colorScheme.error)
            else -> ButtonDefaults.textButtonColors()
        },
    )
}

@Composable
private fun FrameInspector(state: EditorState, frame: Frame) {
    val zh = LocalZh.current
    fun edit(key: String, transform: (Frame) -> Frame) = state.commit(state.doc.updateFrame(frame.id) { transform(it) }, key = "$key:${frame.id}")
    Column {
        Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
            Text(tr("Screen", "屏幕"), style = COUITheme.textStyles.headlineXS, color = COUITheme.colorScheme.labelPrimary)
            Caption("${frame.w.roundToInt()} × ${frame.h.roundToInt()} dp")
        }
        Section(null) {
            item { Input(tr("Name", "名称"), frame.name) { v -> edit("name") { it.copy(name = v) } } }
            item { Input(tr("What the screen is for (goes into the prompt)", "这个屏幕做什么（会写进提示词）"), frame.note.orEmpty(), singleLine = false) { v -> edit("note") { it.copy(note = v.ifEmpty { null }) } } }
            item {
                Choice(tr("Device", "设备"), listOf(tr("Phone 360 × 800", "手机 360 × 800"), tr("Wide 1024 × 720", "宽屏 1024 × 720")), if (frame.isWide) 1 else 0) { i ->
                    state.commit(state.doc.updateFrame(frame.id) { if (i == 1) it.copy(w = Frame.WIDE_W, h = Frame.WIDE_H) else it.copy(w = Frame.PHONE_W, h = Frame.PHONE_H) })
                }
            }
            item {
                NumberInput(tr("Scroll length (dp, empty = screen height)", "滚动长度（dp，留空 = 屏幕高度）"), frame.length, "${frame.h.roundToInt()}") { v -> edit("length") { it.copy(length = v?.takeIf { l -> l > it.h }) } }
            }
            item {
                Choice(tr("Background", "背景"), listOf(tr("Window background", "窗口背景"), tr("Card colour", "卡片色")), if (frame.bg == "card") 1 else 0) { i -> edit("bg") { it.copy(bg = if (i == 1) "card" else null) } }
            }
        }
        val others = state.doc.frames.filter { it.id != frame.id }
        if (others.isNotEmpty()) {
            Section(tr("Swipe to open", "滑动打开")) {
                listOf("left" to tr(zh, "Swipe left", "向左滑"), "right" to tr(zh, "Swipe right", "向右滑")).forEach { (dir, label) ->
                    item {
                        val names = listOf(tr("Nothing", "无")) + others.map { it.name }
                        val current = frame.swipe?.get(dir)?.let { id -> others.indexOfFirst { it.id == id } + 1 } ?: 0
                        Choice(label, names, current) { i ->
                            edit("swipe") { f ->
                                val map = f.swipe.orEmpty().toMutableMap()
                                if (i == 0) map.remove(dir) else map[dir] = others[i - 1].id
                                f.copy(swipe = map.takeIf { it.isNotEmpty() })
                            }
                        }
                    }
                }
            }
        }
        Section(null) {
            item {
                FlowRow(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallButton(tr("Preview", "预览"), primary = true) { state.previewFrame = frame.id }
                    SmallButton(tr("Tidy", "整理")) { state.tidy() }
                    SmallButton(tr("Duplicate", "复制一份")) { state.duplicateSelection() }
                    SmallButton(tr("Delete", "删除"), danger = true) { state.deleteSelection() }
                }
            }
        }
    }
}

@Composable
private fun DocInspector(state: EditorState, doc: CanvasDoc) {
    fun editTheme(transform: (DocTheme) -> DocTheme) = state.commit(doc.copy(theme = transform(doc.theme)))
    Column {
        Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
            Text(tr("App", "应用"), style = COUITheme.textStyles.headlineXS, color = COUITheme.colorScheme.labelPrimary)
            Caption(tr("Select a screen or a part to edit it.", "选中屏幕或组件即可编辑。"))
        }
        Section(null) {
            item { Input(tr("App name", "应用名称"), doc.title) { v -> state.commit(state.doc.copy(title = v), key = "title") } }
            item { Input(tr("What the app is for", "应用用途"), doc.brief, singleLine = false) { v -> state.commit(state.doc.copy(brief = v), key = "brief") } }
            item {
                Choice(tr("Target", "目标平台"), listOf(tr("Android app", "Android 应用"), "Compose Multiplatform"), if (doc.theme.platform == DocTheme.PLATFORM_MULTIPLATFORM) 1 else 0) { i ->
                    editTheme { it.copy(platform = if (i == 1) DocTheme.PLATFORM_MULTIPLATFORM else DocTheme.PLATFORM_ANDROID) }
                }
            }
        }
        ThemeSection(state)
        Section(null) {
            item {
                FlowRow(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallButton(tr("Add phone screen", "添加手机屏幕"), primary = true) { state.addFrame() }
                    SmallButton(tr("Add wide screen", "添加宽屏")) { state.addFrame(wide = true) }
                    SmallButton(tr("Tidy all", "整理全部")) { state.tidy() }
                }
            }
        }
    }
}

/** Light / dark and the key colour of the design. */
@Composable
fun ThemeSection(state: EditorState) {
    val doc = state.doc
    fun editTheme(transform: (DocTheme) -> DocTheme) = state.commit(state.doc.copy(theme = transform(state.doc.theme)))
    Section(tr("Theme", "主题")) {
        item { SwitchPreference(checked = doc.theme.dark, onCheckedChange = { v -> editTheme { it.copy(dark = v) } }, title = tr("Show dark mode", "显示深色模式")) }
        item {
            SwitchPreference(
                checked = doc.theme.bothModes,
                onCheckedChange = { v -> editTheme { it.copy(bothModes = v) } },
                title = tr("Light and dark", "支持浅色与深色"),
                summary = tr("The app follows the system", "应用跟随系统切换"),
            )
        }
        item {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Caption(tr("Key colour", "主题色"), Modifier.padding(bottom = 10.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    KeyColors.forEach { hex ->
                        val selected = doc.theme.keyColor == hex
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(parseHex(hex) ?: Color(0xFF3482FF))
                                .border(if (selected) 3.dp else 0.dp, if (selected) COUITheme.colorScheme.labelPrimary else Color.Transparent, CircleShape)
                                .clickable { editTheme { it.copy(keyColor = hex) } },
                        )
                    }
                }
            }
        }
    }
}
