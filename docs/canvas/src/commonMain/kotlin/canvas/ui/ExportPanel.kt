// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import canvas.gen.CodeGenerator
import canvas.gen.Lang
import canvas.gen.PromptGenerator
import canvas.model.frame
import canvas.model.toJson
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.TabRowWithContour
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Close
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch

/** The prompt, the generated code and the ways to share or save the design, in one modal. */
@Composable
fun ExportPanel(state: EditorState) {
    val panel = state.panel ?: return
    val tabs = listOf(Panel.Prompt, Panel.Code, Panel.Share)
    LaunchedEffect(Unit) { state.requestFocus() }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .pointerInput(Unit) { detectTapGestures { state.panel = null } },
        contentAlignment = Alignment.Center,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .width(minOf(maxWidth, 980.dp))
                    .fillMaxHeight()
                    .pointerInput(Unit) { detectTapGestures { } },
            ) {
                Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        TabRowWithContour(
                            tabs = listOf(tr("Prompt", "提示词"), tr("Kotlin code", "Kotlin 代码"), tr("Share & files", "分享与文件")),
                            selectedTabIndex = tabs.indexOf(panel).coerceAtLeast(0),
                            onTabSelected = { state.panel = tabs[it] },
                        )
                    }
                    IconButton(onClick = { state.panel = null }) { Icon(COUIIcons.Close, contentDescription = tr("Close", "关闭")) }
                }
                when (panel) {
                    Panel.Prompt -> PromptTab(state)
                    Panel.Code -> CodeTab(state)
                    else -> ShareTab(state)
                }
            }
        }
    }
}

@Composable
private fun CodeBlock(text: String, modifier: Modifier = Modifier, wrap: Boolean = false) {
    val colors = COUITheme.colorScheme
    Box(
        modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.fillThin),
    ) {
        SelectionContainer {
            Text(
                text,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .then(if (wrap) Modifier else Modifier.horizontalScroll(rememberScrollState()))
                    .padding(16.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.5.sp,
                color = colors.labelPrimary,
                style = COUITheme.textStyles.bodyS,
            )
        }
    }
}

@Composable
private fun PromptTab(state: EditorState) {
    var zhPrompt by remember { mutableStateOf(state.zh) }
    var onlyScreen by remember { mutableStateOf(false) }
    val screen = state.activeFrame
    val doc = state.doc
    val text = remember(doc, zhPrompt, onlyScreen, screen?.id) {
        PromptGenerator.generate(doc, if (zhPrompt) Lang.Zh else Lang.En, frameId = if (onlyScreen) screen?.id else null, boxOf = state::boxOf)
    }
    Column(Modifier.fillMaxSize()) {
        FlowRow(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            SmallButton("中文", primary = zhPrompt) { zhPrompt = true }
            SmallButton("English", primary = !zhPrompt) { zhPrompt = false }
            Box(Modifier.size(12.dp))
            SmallButton(tr("Whole app", "整个应用"), primary = !onlyScreen) { onlyScreen = false }
            if (screen != null) SmallButton(tr("This screen: ", "仅此屏幕：") + screen.name, primary = onlyScreen) { onlyScreen = true }
            Box(Modifier.size(12.dp))
            SmallButton(tr("Copy prompt", "复制提示词"), primary = true) {
                state.platform.copy(text)
                state.message = tr(state.zh, "Prompt copied. Paste it into Claude Code, Codex or Cursor.", "已复制提示词，粘贴到 Claude Code、Codex 或 Cursor 即可。")
            }
            SmallButton(tr("Download .md", "下载 .md")) { state.platform.download(fileBase(state) + ".md", text, "text/markdown") }
        }
        Caption(
            tr("${text.length} characters · the coding tool builds it with the coui-app skill", "${text.length} 字符 · 编程工具会按 coui-app 技能来实现"),
            Modifier.padding(horizontal = 20.dp).padding(bottom = 8.dp),
        )
        CodeBlock(text, wrap = true)
    }
}

@Composable
private fun CodeTab(state: EditorState) {
    val doc = state.doc
    val files = remember(doc) { CodeGenerator.generate(doc, boxOf = state::boxOf) }
    var index by remember { mutableIntStateOf(0) }
    val file = files.getOrNull(index.coerceIn(0, (files.size - 1).coerceAtLeast(0)))
    Row(Modifier.fillMaxSize()) {
        Column(Modifier.width(260.dp).fillMaxHeight().verticalScroll(rememberScrollState()).padding(start = 12.dp, bottom = 16.dp)) {
            Card {
                CardListGroup {
                    files.forEachIndexed { i, f ->
                        item {
                            BasicComponent(
                                title = f.path.substringAfterLast('/'),
                                summary = f.path.substringBeforeLast('/', ""),
                                selected = i == index,
                                onClick = { index = i },
                            )
                        }
                    }
                }
            }
            Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallButton(tr("Copy this file", "复制此文件"), primary = true) {
                    file?.let {
                        state.platform.copy(it.content)
                        state.message = tr(state.zh, "Copied ${it.path.substringAfterLast('/')}", "已复制 ${it.path.substringAfterLast('/')}")
                    }
                }
                SmallButton(tr("Download this file", "下载此文件")) { file?.let { state.platform.download(it.path.substringAfterLast('/'), it.content) } }
                SmallButton(tr("Copy all files", "复制全部文件")) {
                    state.platform.copy(files.joinToString("\n\n") { "// ===== ${it.path} =====\n${it.content}" })
                    state.message = tr(state.zh, "Copied ${files.size} files", "已复制 ${files.size} 个文件")
                }
            }
            Caption(tr("CouiPage.kt and CouiBlur.kt come from the coui-app skill.", "CouiPage.kt 与 CouiBlur.kt 来自 coui-app 技能。"), Modifier.padding(top = 12.dp, end = 8.dp))
        }
        CodeBlock(file?.content.orEmpty(), Modifier.weight(1f))
    }
}

@Composable
private fun ShareTab(state: EditorState) {
    val scope = rememberCoroutineScope()
    var link by remember { mutableStateOf<String?>(null) }
    val doc = state.doc
    LaunchedEffect(doc) { link = state.platform.baseUrl + "#docz=" + state.platform.deflate(doc.toJson()) }
    val agentText = agentInstructions(state.platform.baseUrl, state.zh)
    Column(Modifier.fillMaxSize().padding(bottom = 16.dp)) {
        Card(Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)) {
            CardListGroup {
                item {
                    BasicComponent(
                        title = tr("Copy share link", "复制分享链接"),
                        summary = tr(
                            "The whole design travels in the link (${link?.length ?: 0} characters); nothing is uploaded.",
                            "整份设计都在链接里（${link?.length ?: 0} 字符），不会上传到任何地方。",
                        ),
                        onClick = {
                            scope.launch {
                                val l = state.platform.baseUrl + "#docz=" + state.platform.deflate(doc.toJson())
                                state.platform.copy(l)
                                state.message = tr(state.zh, "Link copied", "已复制链接")
                            }
                        },
                    )
                }
                item {
                    BasicComponent(
                        title = tr("Copy instructions for an AI agent", "复制给 AI 代理的指令"),
                        summary = tr(
                            "Paste into Claude Code or Codex with what you want; the agent sketches it and replies with a link that opens here.",
                            "连同你的需求粘贴给 Claude Code 或 Codex，代理会画出草图并回复一个能在这里打开的链接。",
                        ),
                        onClick = {
                            state.platform.copy(agentText)
                            state.message = tr(state.zh, "Instructions copied", "已复制指令")
                        },
                    )
                }
            }
        }
        Card(Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)) {
            CardListGroup {
                item {
                    BasicComponent(
                        title = tr("Save project (.json)", "保存项目 (.json)"),
                        summary = tr("A file you can open again or hand to an agent.", "可以再次打开，或交给 AI 代理继续改。"),
                        onClick = { state.platform.download(fileBase(state) + ".json", doc.toJson(pretty = true), "application/json") },
                    )
                }
                item {
                    BasicComponent(
                        title = tr("Open project…", "打开项目…"),
                        summary = tr("A .json saved from here or written by an agent.", "从这里保存的，或 AI 代理写的 .json。"),
                        onClick = { openProject(state) },
                    )
                }
                item {
                    BasicComponent(
                        title = tr("Start a blank design", "新建空白设计"),
                        onClick = {
                            state.replace(canvas.model.CanvasDoc(title = "", frames = listOf(canvas.model.Frame("screen1", tr(state.zh, "Home", "首页")))))
                            state.panel = null
                        },
                    )
                }
                item {
                    BasicComponent(
                        title = tr("Load the sample", "载入示例"),
                        onClick = {
                            state.replace(sampleDoc(state.zh))
                            state.panel = null
                        },
                    )
                }
            }
        }
        Caption(tr("Agent instructions", "代理指令预览"), Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
        CodeBlock(agentText, Modifier.weight(1f).padding(top = 4.dp), wrap = true)
    }
}

/** Opens a project file and replaces the design, reporting what had to be repaired. */
fun openProject(state: EditorState) {
    state.platform.openFile(".json,application/json") { text ->
        val parsed = canvas.model.parseDoc(text)
        val doc = parsed.doc
        if (doc == null) {
            state.message = tr(state.zh, "That file is not a COUI Canvas project.", "这个文件不是 COUI Canvas 项目。")
        } else {
            state.replace(doc)
            state.panel = null
            state.message = problemsMessage(parsed.problems, state.zh)
        }
    }
}

fun problemsMessage(problems: List<canvas.model.Problem>, zh: Boolean): String? {
    if (problems.isEmpty()) return null
    val first = problems.first()
    val what = when (first.type) {
        canvas.model.Problem.Type.NotADocument -> tr(zh, "not a document", "不是设计文档")
        canvas.model.Problem.Type.UnknownKind -> tr(zh, "unknown part \"${first.subject}\" dropped", "已移除未知组件「${first.subject}」")
        canvas.model.Problem.Type.DuplicateId -> tr(zh, "duplicate id \"${first.subject}\" renamed", "已重命名重复 id「${first.subject}」")
        canvas.model.Problem.Type.MissingTarget -> tr(zh, "link to missing screen \"${first.subject}\" dropped", "已移除指向不存在屏幕「${first.subject}」的跳转")
        canvas.model.Problem.Type.EmptyGroup -> tr(zh, "empty group \"${first.subject}\" dropped", "已移除空组「${first.subject}」")
    }
    val more = if (problems.size > 1) tr(zh, " (+${problems.size - 1} more)", "（另有 ${problems.size - 1} 处）") else ""
    return tr(zh, "Opened with fixes: ", "已打开并修正：") + what + more
}

private fun fileBase(state: EditorState): String {
    val name = state.doc.title.trim().replace(Regex("[\\\\/:*?\"<>|]+"), " ").trim()
    return if (name.isEmpty()) "coui-canvas" else "coui-canvas $name"
}

/** What to paste into an AI agent so it sketches a design and answers with a link. */
fun agentInstructions(baseUrl: String, zh: Boolean): String {
    val guide = baseUrl.substringBeforeLast('/') + "/agent.md"
    return if (zh) {
        "请用 COUI Canvas 为下面的需求画一份 ColorOS 17 风格的界面草图。先阅读 $guide 了解文档格式，然后按它的说明生成设计文档，并用其中的脚本生成分享链接，回复我这个链接（不能运行代码时直接回复 JSON）。\n\n需求：\n"
    } else {
        "Sketch the screens below as a ColorOS 17 design in COUI Canvas. Read $guide for the document format, write the design document it describes, build the share link with its script and reply with that link (or with the JSON if you cannot run code).\n\nWhat I want:\n"
    }
}

/** Opens the design a share link carries (`#docz=` deflated, or `#doc=` plain JSON), if any. */
suspend fun loadInitial(state: EditorState) {
    val hash = state.platform.hash
    val params = hash.split('&').mapNotNull { p -> p.split('=', limit = 2).takeIf { it.size == 2 }?.let { it[0] to it[1] } }.toMap()
    val packed = params["docz"]
    val plain = params["doc"]
    if (packed != null || plain != null) {
        val json = packed?.let { state.platform.inflate(it) } ?: plain?.let { decodeUriComponent(it) }
        val parsed = json?.let { canvas.model.parseDoc(it) }
        state.platform.clearHash()
        if (parsed?.doc != null) {
            state.replace(parsed.doc)
            state.message = problemsMessage(parsed.problems, state.zh) ?: tr(state.zh, "Opened the shared design", "已打开分享的设计")
            return
        }
        state.message = tr(state.zh, "The link is broken; opened your last design instead.", "链接已损坏，已打开你上次的设计。")
    }
}

private fun decodeUriComponent(s: String): String {
    val bytes = ArrayList<Byte>()
    var i = 0
    while (i < s.length) {
        val c = s[i]
        if (c == '%' && i + 2 < s.length) {
            bytes += s.substring(i + 1, i + 3).toInt(16).toByte()
            i += 3
        } else {
            c.toString().encodeToByteArray().forEach { bytes += it }
            i++
        }
    }
    return bytes.toByteArray().decodeToString()
}
