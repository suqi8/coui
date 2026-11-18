// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import canvas.model.Group
import canvas.model.Kind
import canvas.model.Section
import canvas.model.frameOf
import canvas.model.groupsOn
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TabRowWithContour
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

/** The left panel: the parts palette, or the layers of every screen. */
@Composable
fun LeftPanel(state: EditorState, modifier: Modifier = Modifier) {
    var tab by remember { mutableIntStateOf(0) }
    Column(modifier) {
        Box(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            TabRowWithContour(
                tabs = listOf(tr("Parts", "组件"), tr("Layers", "图层")),
                selectedTabIndex = tab,
                onTabSelected = { tab = it },
            )
        }
        if (tab == 0) Palette(state, Modifier.weight(1f)) else Layers(state, Modifier.weight(1f))
    }
}

@Composable
private fun Palette(state: EditorState, modifier: Modifier = Modifier) {
    val zh = LocalZh.current
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 24.dp)) {
        Text(
            tr("Click to add to the selected screen, or drag onto the canvas.", "点击添加到当前屏幕，或拖到画布上。"),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            style = COUITheme.textStyles.bodyXS,
            color = COUITheme.colorScheme.labelTertiary,
        )
        Section.entries.forEach { section ->
            SmallTitle(sectionName(section, zh))
            Card(Modifier.padding(horizontal = 12.dp).padding(bottom = 8.dp)) {
                CardListGroup {
                    Kind.entries.filter { it.section == section }.forEach { kind ->
                        item { PaletteRow(state, kind) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaletteRow(state: EditorState, kind: Kind) {
    val zh = LocalZh.current
    var origin by remember { mutableStateOf(Offset.Zero) }
    BasicComponent(
        modifier = Modifier
            .onGloballyPositioned { origin = it.positionInWindow() }
            .pointerInput(kind) {
                // Watched in the initial pass: the row's own click handling would otherwise
                // take the gesture before a drag can start.
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    var travelled = 0f
                    var dragging = false
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (change.changedToUp()) {
                            if (dragging) {
                                change.consume()
                                dropFromPalette(state)
                            }
                            break
                        }
                        travelled += (change.position - change.previousPosition).getDistance()
                        if (!dragging && travelled > viewConfiguration.touchSlop) dragging = true
                        if (dragging) {
                            change.consume()
                            state.paletteDrag = kind to origin + change.position
                        }
                    }
                    if (!dragging) state.paletteDrag = null
                }
            },
        title = kindName(kind, zh),
        summary = kind.component,
        onClick = { state.addPart(kind) },
    )
}

/** Drops the part dragged from the palette where the pointer is, when that is over the canvas. */
fun dropFromPalette(state: EditorState) {
    val (kind, p) = state.paletteDrag ?: return
    state.paletteDrag = null
    val local = p - state.canvasOrigin
    if (local.x < 0 || local.y < 0 || local.x > state.viewport.width || local.y > state.viewport.height) return
    state.addPart(kind, state.screenToWorld(local))
}

/** The ghost that follows the pointer while a part is dragged in from the palette. */
@Composable
fun PaletteGhost(state: EditorState) {
    val (kind, p) = state.paletteDrag ?: return
    val zh = LocalZh.current
    Box(
        Modifier
            .padding(start = (p.x / state.density + 8f).dp, top = (p.y / state.density + 8f).dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SelectionBlue)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(kindName(kind, zh), color = androidx.compose.ui.graphics.Color.White, style = COUITheme.textStyles.bodyS)
    }
}

@Composable
private fun Layers(state: EditorState, modifier: Modifier = Modifier) {
    val zh = LocalZh.current
    val doc = state.doc
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 24.dp)) {
        doc.frames.forEach { frame ->
            val on = doc.groupsOn(frame)
            Card(Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)) {
                CardListGroup {
                    item {
                        BasicComponent(
                            title = frame.name.ifEmpty { "—" },
                            summary = tr(zh, "${on.size} parts", "${on.size} 个组件"),
                            selected = state.selectedFrame == frame.id,
                            onClick = {
                                state.selectFrame(frame.id)
                                state.focusFrame(frame.id)
                            },
                        )
                    }
                    // Top layer first, like a design tool.
                    on.asReversed().forEach { g -> item { LayerRow(state, g) } }
                }
            }
        }
        val loose = doc.groups.filter { doc.frameOf(it) == null }
        if (loose.isNotEmpty()) {
            SmallTitle(tr("Off-screen", "屏幕外"))
            Card(Modifier.padding(horizontal = 12.dp)) {
                CardListGroup { loose.asReversed().forEach { g -> item { LayerRow(state, g) } } }
            }
        }
    }
}

@Composable
private fun LayerRow(state: EditorState, group: Group) {
    val zh = LocalZh.current
    val kind = group.kind ?: return
    val selected = state.selectedGroup?.id == group.id
    BasicComponent(
        title = kindName(kind, zh) + if (group.items.size > 1) " ×${group.items.size}" else "",
        summary = group.items.joinToString(" · ") { it.label.ifEmpty { it.icon ?: "" } }.takeIf { it.isNotBlank() },
        selected = selected,
        insideMargin = androidx.compose.foundation.layout.PaddingValues(start = 32.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
        onClick = { state.selectItem(group.items.first().id) },
    )
}

/** A compact label for toolbars. */
@Composable
fun Caption(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier = modifier, style = COUITheme.textStyles.bodyXS, color = COUITheme.colorScheme.labelTertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
}
