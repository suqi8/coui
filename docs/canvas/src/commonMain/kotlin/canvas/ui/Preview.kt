// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import canvas.model.Action
import canvas.model.Frame
import canvas.model.Group
import canvas.model.Item
import canvas.model.Kind
import canvas.model.Pin
import canvas.model.frame
import canvas.model.groupsOn
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Back
import io.github.suqi8.coui.kmp.icon.extended.Close
import io.github.suqi8.coui.kmp.icon.extended.Refresh
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.min

/** One step of the preview's back stack: the screen and how it was entered. */
private data class Step(val frameId: String, val transition: String, val depth: Int)

/** Plays the design: taps follow the connections between screens, back pops, switches flip. */
@Composable
fun PreviewOverlay(state: EditorState) {
    val start = state.previewFrame ?: return
    val doc = state.doc
    val stack = remember(start) { mutableStateListOf(Step(start, Action.TRANSITION_NONE, 0)) }
    androidx.compose.runtime.LaunchedEffect(start) { state.requestFocus() }
    var popping by remember(start) { mutableStateOf(false) }
    fun go(action: Action) {
        if (action.to == Action.BACK) {
            if (stack.size > 1) {
                popping = true
                stack.removeAt(stack.lastIndex)
            }
        } else if (doc.frame(action.to) != null) {
            popping = false
            stack.add(Step(action.to, action.transition, stack.size))
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .pointerInput(Unit) { detectTapGestures { state.requestFocus() } },
    ) {
        Row(
            Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = { go(Action(Action.BACK)) }, enabled = stack.size > 1, backgroundColor = Color.White.copy(alpha = 0.12f)) {
                Icon(COUIIcons.Back, contentDescription = tr("Back", "返回"), tint = Color.White)
            }
            Text(
                doc.frame(stack.last().frameId)?.name.orEmpty(),
                color = Color.White,
                style = COUITheme.textStyles.bodyL,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
            IconButton(onClick = {
                popping = true
                while (stack.size > 1) stack.removeAt(stack.lastIndex)
            }, backgroundColor = Color.White.copy(alpha = 0.12f)) {
                Icon(COUIIcons.Refresh, contentDescription = tr("Restart", "从头开始"), tint = Color.White)
            }
            IconButton(onClick = { state.previewFrame = null }, backgroundColor = Color.White.copy(alpha = 0.12f)) {
                Icon(COUIIcons.Close, contentDescription = tr("Close", "关闭"), tint = Color.White)
            }
        }
        Text(
            tr("Tap through the flow · Esc to close", "点一点走完流程 · Esc 关闭"),
            color = Color.White.copy(alpha = 0.6f),
            style = COUITheme.textStyles.bodyXS,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
        )
        BoxWithConstraints(Modifier.fillMaxSize().padding(top = 72.dp, bottom = 40.dp), contentAlignment = Alignment.Center) {
            val first = doc.frame(start) ?: return@BoxWithConstraints
            val scale = min(1f, min(maxWidth.value / first.w, maxHeight.value / first.h))
            DesignTheme(doc.theme) {
                Box(
                    Modifier
                        .requiredSize(first.w.dp, first.h.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .shadow(24.dp, RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp)),
                ) {
                    AnimatedContent(
                        targetState = stack.last(),
                        transitionSpec = { transitionFor(if (popping) initialState.transition else targetState.transition, popping) },
                        label = "preview",
                    ) { step ->
                        val frame = doc.frame(step.frameId)
                        if (frame != null) PreviewScreen(state, frame, onAction = ::go)
                    }
                }
            }
        }
    }
}

private fun transitionFor(transition: String, back: Boolean): ContentTransform {
    val spec = tween<androidx.compose.ui.unit.IntOffset>(380, easing = androidx.compose.animation.core.CubicBezierEasing(0.3f, 0f, 0.1f, 1f))
    val fade = tween<Float>(300)
    return when (transition) {
        Action.TRANSITION_FADE -> fadeIn(fade) togetherWith fadeOut(fade)

        Action.TRANSITION_UP -> if (back) {
            fadeIn(fade) togetherWith slideOutVertically(spec) { it }
        } else {
            slideInVertically(spec) { it } togetherWith fadeOut(fade)
        }

        Action.TRANSITION_NONE -> EnterTransition.None togetherWith ExitTransition.None

        else -> if (back) {
            slideInHorizontally(spec) { -it / 4 } togetherWith slideOutHorizontally(spec) { it }
        } else {
            slideInHorizontally(spec) { it } togetherWith slideOutHorizontally(spec) { -it / 4 }
        }
    }.apply { targetContentZIndex = if (back) 0f else 1f }
}

/** A screen in the preview: the body scrolls under the pinned bars, and parts react to taps. */
@Composable
private fun PreviewScreen(state: EditorState, frame: Frame, onAction: (Action) -> Unit) {
    val doc = state.doc
    var dismissed by remember(frame.id) { mutableStateOf(emptySet<String>()) }
    val on = doc.groupsOn(frame).filterNot { it.id in dismissed }
    val overlays = on.filter { it.kind?.isOverlay == true }
    val inside = on.filter { g -> overlays.any { o -> o.id != g.id && state.boxOf(o).encloses(state.boxOf(g), 4f) } }
    val pinned = on.filter { g -> g !in inside && g.kind?.pin.let { it == Pin.Top || it == Pin.Fab || g.kind == Kind.BottomNav || g.kind == Kind.Snackbar } }
    val body = on.filter { it !in pinned && it !in overlays && it !in inside }
    val ctx = remember(frame.id) {
        PartContext(live = true) { item, target -> tap(item, target, overlays, onAction) { id -> dismissed = dismissed + id } }
    }
    val colors = COUITheme.colorScheme
    Box(
        Modifier
            .fillMaxSize()
            .background(if (frame.bg == "card") colors.card else colors.backgroundWithCard)
            .pointerInput(frame.id) {
                var total = 0f
                detectHorizontalDragGestures(onDragStart = { total = 0f }, onDragEnd = {
                    val dir = if (total < -80f) {
                        "left"
                    } else if (total > 80f) {
                        "right"
                    } else {
                        null
                    }
                    frame.swipe?.get(dir)?.let { onAction(Action(it, if (dir == "left") Action.TRANSITION_PUSH else Action.TRANSITION_FADE)) }
                }) { _, d -> total += d }
            },
    ) {
        Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(Modifier.requiredSize(frame.w.dp, frame.drawnHeight.dp)) {
                body.forEach { g -> PreviewGroup(g, frame, ctx) }
            }
        }
        pinned.forEach { g -> PreviewGroup(g, frame, ctx) }
        if (overlays.isNotEmpty()) {
            Box(
                Modifier.fillMaxSize().background(colors.mask).pointerInput(Unit) {
                    detectTapGestures { dismissed = dismissed + overlays.map { it.id } + inside.map { it.id } }
                },
            )
            (overlays + inside).sortedBy { doc.groups.indexOf(it) }.forEach { g -> PreviewGroup(g, frame, ctx) }
        }
        StatusBar(Modifier.fillMaxWidth())
    }
}

private fun tap(item: Item, target: String, overlays: List<Group>, onAction: (Action) -> Unit, dismiss: (String) -> Unit) {
    val action = if (target.isEmpty()) item.action else item.actions?.get(target)
    val kind = Kind.of(item.kind)
    when {
        action != null -> onAction(action)

        // An unwired back button still goes back; an unwired dialog button closes the dialog.
        kind == Kind.TopBar && target == "icon" -> onAction(Action(Action.BACK))

        kind == Kind.Dialog -> overlays.firstOrNull { g -> g.items.any { it.id == item.id } }?.let { dismiss(it.id) }
    }
}

@Composable
private fun PreviewGroup(group: Group, frame: Frame, ctx: PartContext) {
    Box(Modifier.offset((group.x - frame.x).dp, (group.y - frame.y).dp)) {
        GroupView(group, frame, ctx)
    }
}
