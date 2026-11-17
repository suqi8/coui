// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import canvas.model.Field
import canvas.model.Kind
import canvas.model.Section

/** Whether the editor speaks Chinese; the canvas has two languages, chosen in the toolbar. */
val LocalZh = staticCompositionLocalOf { false }

/** The text for the current editor language. */
@Composable
@ReadOnlyComposable
fun tr(en: String, zh: String): String = if (LocalZh.current) zh else en

/** The text for a given language outside composition. */
fun tr(zhLang: Boolean, en: String, zh: String): String = if (zhLang) zh else en

fun sectionName(section: Section, zh: Boolean): String = when (section) {
    Section.Bars -> tr(zh, "Bars", "栏")
    Section.Rows -> tr(zh, "Lists", "列表")
    Section.Buttons -> tr(zh, "Buttons", "按钮")
    Section.Content -> tr(zh, "Content", "内容")
    Section.Overlays -> tr(zh, "Overlays", "弹层")
}

fun kindName(kind: Kind, zh: Boolean): String = when (kind) {
    Kind.TopBar -> tr(zh, "Top bar", "顶栏")
    Kind.SearchBar -> tr(zh, "Search bar", "搜索框")
    Kind.Tabs -> tr(zh, "Tabs", "标签栏")
    Kind.BottomNav -> tr(zh, "Navigation bar", "底部导航")
    Kind.Category -> tr(zh, "Category title", "分类标题")
    Kind.SwitchRow -> tr(zh, "Switch row", "开关行")
    Kind.ArrowRow -> tr(zh, "Arrow row", "箭头行")
    Kind.BasicRow -> tr(zh, "Action row", "操作行")
    Kind.DropdownRow -> tr(zh, "Dropdown row", "下拉行")
    Kind.CheckboxRow -> tr(zh, "Checkbox row", "复选行")
    Kind.RadioRow -> tr(zh, "Radio row", "单选行")
    Kind.MarkRow -> tr(zh, "Check-mark row", "勾选行")
    Kind.SliderRow -> tr(zh, "Slider row", "滑块行")
    Kind.InputRow -> tr(zh, "Input row", "输入行")
    Kind.Button -> tr(zh, "Button", "按钮")
    Kind.IconButton -> tr(zh, "Icon button", "图标按钮")
    Kind.Fab -> tr(zh, "FAB", "悬浮按钮")
    Kind.Chip -> tr(zh, "Chip", "标签片")
    Kind.Card -> tr(zh, "Card", "卡片")
    Kind.Text -> tr(zh, "Text", "文字")
    Kind.Image -> tr(zh, "Image", "图片")
    Kind.TopTips -> tr(zh, "Top tips", "顶部提示")
    Kind.EmptyState -> tr(zh, "Empty state", "空状态")
    Kind.TextField -> tr(zh, "Text field", "输入框")
    Kind.Slider -> tr(zh, "Slider", "滑块")
    Kind.Progress -> tr(zh, "Progress", "进度")
    Kind.Divider -> tr(zh, "Divider", "分割线")
    Kind.Dialog -> tr(zh, "Dialog", "对话框")
    Kind.BottomSheet -> tr(zh, "Bottom sheet", "底部面板")
    Kind.Snackbar -> tr(zh, "Snackbar", "消息条")
}

/** What a field is called in the inspector, per kind where the meaning differs. */
fun fieldName(field: Field, kind: Kind, zh: Boolean): String = when (field) {
    Field.Label -> when (kind) {
        Kind.TopBar, Kind.Dialog, Kind.BottomSheet, Kind.Card, Kind.EmptyState, Kind.Category -> tr(zh, "Title", "标题")
        Kind.SearchBar, Kind.TextField -> tr(zh, "Placeholder", "占位文字")
        Kind.Image -> tr(zh, "Description", "描述")
        Kind.TopTips, Kind.Snackbar, Kind.Text -> tr(zh, "Text", "文字")
        else -> tr(zh, "Title", "标题")
    }

    Field.Supporting -> when (kind) {
        Kind.TopBar -> tr(zh, "Subtitle / search placeholder", "副标题 / 搜索占位")
        Kind.Dialog -> tr(zh, "Message", "正文")
        Kind.TopTips, Kind.Snackbar -> tr(zh, "Action label", "操作文字")
        Kind.EmptyState -> tr(zh, "Subtitle", "副标题")
        Kind.InputRow, Kind.TextField -> tr(zh, "Hint", "提示")
        else -> tr(zh, "Summary", "摘要")
    }

    Field.End -> when (kind) {
        Kind.EmptyState -> tr(zh, "Button label", "按钮文字")
        Kind.SliderRow -> tr(zh, "Value text", "数值文字")
        else -> tr(zh, "Value on the right", "右侧值")
    }

    Field.Icon -> when (kind) {
        Kind.TopBar -> tr(zh, "Back button", "返回按钮")
        else -> tr(zh, "Icon", "图标")
    }

    Field.Icon2 -> tr(zh, "Action icon", "操作图标")

    Field.Variant -> tr(zh, "Style", "样式")

    Field.Checked -> when (kind) {
        Kind.Chip -> tr(zh, "Selected", "选中")
        else -> tr(zh, "On", "开启")
    }

    Field.Value -> tr(zh, "Value", "数值")

    Field.Size -> tr(zh, "Width (dp)", "宽度 (dp)")

    Field.Size2 -> tr(zh, "Height (dp)", "高度 (dp)")

    Field.Tabs -> when (kind) {
        Kind.DropdownRow -> tr(zh, "Options", "选项")
        Kind.Dialog -> tr(zh, "Buttons", "按钮")
        else -> tr(zh, "Items", "项目")
    }

    Field.Selected -> tr(zh, "Selected", "选中项")

    Field.Action -> tr(zh, "On tap", "点击后")
}

fun variantName(variant: String, zh: Boolean): String = when (variant) {
    "large" -> tr(zh, "Large title", "大标题")
    "home" -> tr(zh, "Home + search", "首页 + 搜索")
    "small" -> tr(zh, "Small", "小标题")
    "primary" -> tr(zh, "Primary", "主要")
    "secondary" -> tr(zh, "Secondary", "次要")
    "title" -> tr(zh, "Title", "标题")
    "body" -> tr(zh, "Body", "正文")
    "caption" -> tr(zh, "Caption", "说明")
    "line" -> tr(zh, "Line", "下划线")
    "filled" -> tr(zh, "Filled", "填充")
    "linear" -> tr(zh, "Linear", "线性")
    "circular" -> tr(zh, "Circular", "环形")
    "loading" -> tr(zh, "Loading", "加载中")
    "underline" -> tr(zh, "Underline", "下划线")
    "contour" -> tr(zh, "Contour", "胶囊")
    else -> variant
}

fun transitionName(transition: String, zh: Boolean): String = when (transition) {
    "push" -> tr(zh, "Push (slide in)", "推入（侧滑）")
    "fade" -> tr(zh, "Fade", "淡入")
    "up" -> tr(zh, "Slide up", "从下方滑入")
    "none" -> tr(zh, "None", "无动画")
    else -> transition
}
