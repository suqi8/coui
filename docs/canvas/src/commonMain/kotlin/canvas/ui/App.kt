// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import canvas.model.CanvasDoc
import canvas.model.parseDoc
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.VerticalDivider
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Redo
import io.github.suqi8.coui.kmp.icon.extended.Undo
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.ColorSchemeMode
import io.github.suqi8.coui.kmp.theme.ThemeController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce

/** The document a visit starts from: the one kept in the browser, or the sample. */
fun initialDoc(platform: CanvasPlatform, zh: Boolean): CanvasDoc = platform.load(EditorState.STORE_KEY)?.let { parseDoc(it).doc } ?: sampleDoc(zh)

/** COUI Canvas: sketch ColorOS 17 screens with real COUI components and turn them into a prompt or code. */
@Composable
fun CanvasApp(platform: CanvasPlatform) {
    val state = remember {
        val zh = platform.load(EditorState.LANG_KEY)?.let { it == "zh" } ?: platform.prefersChinese
        EditorState(platform, initialDoc(platform, zh), zh)
    }
    val focus = remember { FocusRequester() }
    state.requestFocus = { runCatching { focus.requestFocus() } }
    val controller = remember { ThemeController(if (platform.prefersDark) ColorSchemeMode.Dark else ColorSchemeMode.Light) }
    LaunchedEffect(Unit) {
        loadInitial(state)
        runCatching { focus.requestFocus() }
        // A share link pasted into this tab only changes the hash; open it too.
        val changes = kotlinx.coroutines.channels.Channel<Unit>(kotlinx.coroutines.channels.Channel.CONFLATED)
        platform.onHashChange { changes.trySend(Unit) }
        for (unit in changes) {
            if (platform.hash.isNotEmpty()) loadInitial(state)
        }
    }
    // Fit the view once everything on the canvas has been measured.
    LaunchedEffect(Unit) {
        snapshotFlow { state.viewport to state.measured.size }.collect { (vp, n) ->
            if (!state.fitted && vp.width > 0 && n >= state.doc.groups.size) {
                delay(400)
                state.fit()
                state.fitted = true
            }
        }
    }
    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(3500)
            state.message = null
        }
    }
    CompositionLocalProvider(LocalZh provides state.zh) {
        COUITheme(controller = controller) {
            Scaffold(containerColor = COUITheme.colorScheme.background) { _ ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .focusRequester(focus)
                        .focusable()
                        .onKeyEvent { e -> e.type == KeyEventType.KeyDown && handleKey(state, e.key, e.isCtrlPressed || e.isMetaPressed, e.isShiftPressed) },
                ) {
                    Column(Modifier.fillMaxSize()) {
                        Toolbar(state)
                        Row(Modifier.weight(1f).fillMaxWidth()) {
                            LeftPanel(state, Modifier.width(272.dp).fillMaxHeight().background(COUITheme.colorScheme.backgroundWithCard))
                            CanvasView(state, Modifier.weight(1f))
                            Inspector(state, Modifier.width(336.dp).fillMaxHeight().background(COUITheme.colorScheme.backgroundWithCard))
                        }
                    }
                    PreviewOverlay(state)
                    ExportPanel(state)
                    PaletteGhost(state)
                    state.message?.let { Toast(it, Modifier.align(Alignment.BottomCenter).padding(bottom = 28.dp)) }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { state.zh }.collect { platform.save(EditorState.LANG_KEY, if (it) "zh" else "en") }
    }
}

@Composable
private fun Toast(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xE6202124))
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(text, color = Color.White, style = COUITheme.textStyles.bodyM)
    }
}

/** Keyboard shortcuts; text fields consume their own keys before they get here. */
private fun handleKey(state: EditorState, key: Key, ctrl: Boolean, shift: Boolean): Boolean {
    if (state.previewFrame != null) {
        if (key == Key.Escape) state.previewFrame = null
        return true
    }
    if (state.panel != null) {
        if (key == Key.Escape) state.panel = null
        return key == Key.Escape
    }
    val step = if (shift) 8f else 1f
    when {
        ctrl && key == Key.Z && shift -> state.redo()

        ctrl && key == Key.Z -> state.undo()

        ctrl && key == Key.Y -> state.redo()

        ctrl && key == Key.D -> state.duplicateSelection()

        ctrl && (key == Key.Equals || key == Key.Plus) -> state.zoomBy(1.25f)

        ctrl && key == Key.Minus -> state.zoomBy(0.8f)

        ctrl && key == Key.Zero -> state.fit()

        key == Key.Delete || key == Key.Backspace -> state.deleteSelection()

        key == Key.DirectionLeft -> state.nudge(-step, 0f)

        key == Key.DirectionRight -> state.nudge(step, 0f)

        key == Key.DirectionUp -> state.nudge(0f, -step)

        key == Key.DirectionDown -> state.nudge(0f, step)

        key == Key.Escape -> {
            state.selectItem(null)
            state.selectFrame(null)
        }

        key == Key.P -> state.previewFrame = state.activeFrame?.id

        key == Key.T -> state.tidy()

        else -> return false
    }
    return true
}

@Composable
private fun Toolbar(state: EditorState) {
    val colors = COUITheme.colorScheme
    Row(
        Modifier.fillMaxWidth().height(56.dp).background(colors.backgroundWithCard).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(SelectionBlue), contentAlignment = Alignment.Center) {
            Text("C", color = Color.White, style = COUITheme.textStyles.bodyL.copy(fontWeight = FontWeight.Bold))
        }
        Column(Modifier.padding(start = 6.dp, end = 12.dp)) {
            Text("COUI Canvas", style = COUITheme.textStyles.bodyL.copy(fontWeight = FontWeight.Medium), color = colors.labelPrimary)
            Caption(state.doc.title.ifEmpty { tr("Untitled", "未命名") })
        }
        IconButton(onClick = state::undo, enabled = state.canUndo) { Icon(COUIIcons.Undo, contentDescription = tr("Undo", "撤销")) }
        IconButton(onClick = state::redo, enabled = state.canRedo) { Icon(COUIIcons.Redo, contentDescription = tr("Redo", "重做")) }
        VerticalDivider(Modifier.height(24.dp).padding(horizontal = 4.dp))
        SmallButton(tr("+ Screen", "+ 屏幕")) { state.addFrame() }
        SmallButton(tr("Tidy", "整理")) { state.tidy() }
        SmallButton(if (state.doc.theme.dark) tr("Dark", "深色") else tr("Light", "浅色")) {
            state.commit(state.doc.copy(theme = state.doc.theme.copy(dark = !state.doc.theme.dark)))
        }
        Spacer(Modifier.weight(1f))
        SmallButton(if (state.zh) "EN" else "中文") { state.zh = !state.zh }
        SmallButton(tr("Share & files", "分享与文件")) { state.panel = Panel.Share }
        SmallButton(tr("Code", "代码")) { state.panel = Panel.Code }
        SmallButton(tr("Prompt", "提示词")) { state.panel = Panel.Prompt }
        SmallButton(tr("Preview", "预览"), primary = true) { state.previewFrame = state.activeFrame?.id }
    }
}
