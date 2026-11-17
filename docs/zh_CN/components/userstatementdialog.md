# UserStatementDialog

`UserStatementDialog` 是首次启动时以底部面板形式展示的用户声明,对应 ColorOS 的 `COUIUserStatementDialog`(`coui_component_full_page_statement_with_protocol.xml`):以最大高度显示、无拖拽把手的面板,依次展示应用 logo、标题、应用名,以及说明文字 / 图标列表 / 自定义内容之一,然后是放在短滚动区里的声明正文(末尾追加协议文字),最后是确认按钮与其下方的退出按钮。

面板不可拖动,点击外部不会关闭(`setCanceledOnTouchOutside(false)`);只有设置了 `onDismissRequest` 时返回手势才会关闭。窗口宽度 ≥ 600dp 时与所有 COUI 面板一样居中浮窗显示。

提供两种变体:

- `OverlayUserStatementDialog` —— 渲染在 `Scaffold` 的 `COUIPopupHost` 中(必须在 `Scaffold` 内使用)。
- `WindowUserStatementDialog` —— 窗口级渲染,不需要 `Scaffold`。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=userStatementDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger 前置条件
`OverlayUserStatementDialog` 依赖 `Scaffold` 提供的 `COUIPopupHost` 来渲染弹出内容。没有 `Scaffold` 时请使用 `WindowUserStatementDialog`。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayUserStatementDialog
// 或
import io.github.suqi8.coui.kmp.window.WindowUserStatementDialog

import io.github.suqi8.coui.kmp.layout.UserStatementListItem
import io.github.suqi8.coui.kmp.layout.UserStatementDialogDefaults
```

## 基本用法

```kotlin
var showStatement by remember { mutableStateOf(true) }
val linkStyles = FullPageStatementDefaults.linkStyles()
val statement = buildAnnotatedString {
    append("继续即表示你同意")
    withLink(LinkAnnotation.Clickable("agreement", linkStyles) { openAgreement() }) {
        append("《用户协议》")
    }
    append("。")
}

Scaffold {
    OverlayUserStatementDialog(
        show = showStatement,
        onConfirm = { showStatement = false },
        onExit = { finishApp() },
        logo = painterResource(Res.drawable.app_icon),
        title = "欢迎使用",
        appName = "便签",
        appMessage = "随时记录、整理并同步你的笔记。",
        statement = statement,
    )
}
```

## 属性

| 属性名               | 类型                          | 说明                                                         | 默认值                                  | 是否必须 |
| -------------------- | ----------------------------- | ------------------------------------------------------------ | --------------------------------------- | -------- |
| show                 | Boolean                       | 是否显示面板                                                 | -                                       | 是       |
| onConfirm            | () -> Unit                    | 确认按钮回调                                                 | -                                       | 是       |
| onExit               | () -> Unit                    | 退出按钮回调                                                 | -                                       | 是       |
| modifier             | Modifier                      | 应用于面板的修饰符                                           | Modifier                                | 否       |
| backgroundModifier   | Modifier                      | 作用在面板圆角裁剪内、内容之下(模糊材质)                   | Modifier                                | 否       |
| logo                 | Painter?                      | 80dp 应用 logo(20dp 圆角);设置 `customContent` 时隐藏     | null                                    | 否       |
| title                | String?                       | 应用名上方的一行文字(16sp 中等字重,单行)                  | null                                    | 否       |
| appName              | String?                       | 应用名(28sp 中等字重)                                      | null                                    | 否       |
| appMessage           | String?                       | 居中说明文字(13sp),仅在没有 `listItems` 与 `customContent` 时显示 | null                            | 否       |
| listItems            | List&lt;UserStatementListItem&gt; | 应用名下方的图标列表                                     | emptyList()                             | 否       |
| onListItemClick      | ((Int) -> Unit)?              | 点击列表行时以下标回调                                       | null                                    | 否       |
| statement            | AnnotatedString?              | 声明正文(10sp,行高 1.2em),可带链接                       | null                                    | 否       |
| protocolText         | AnnotatedString?              | 追加在 `statement` 之后的协议文字                            | null                                    | 否       |
| confirmText          | String                        | 确认按钮文字                                                 | UserStatementDialogDefaults.ConfirmText | 否       |
| exitText             | String                        | 退出按钮文字                                                 | UserStatementDialogDefaults.ExitText    | 否       |
| backgroundColor      | Color                         | 面板背景色                                                   | BottomSheetDefaults.backgroundColor()   | 否       |
| enableWindowDim      | Boolean                       | 是否显示遮罩                                                 | true                                    | 否       |
| onDismissRequest     | (() -> Unit)?                 | 返回手势回调;为 null 时忽略返回                             | null                                    | 否       |
| onDismissFinished    | (() -> Unit)?                 | 面板完全关闭后回调                                           | null                                    | 否       |
| renderInRootScaffold | Boolean                       | (仅 Overlay)是否在根 Scaffold 中渲染                        | true                                    | 否       |
| materialStroke       | Boolean                       | 是否绘制 COUI 17 模糊面板的材质描边                          | false                                   | 否       |
| customContent        | (@Composable () -> Unit)?     | 替换 logo、说明与列表的自定义内容(`setCustomView`)         | null                                    | 否       |

### UserStatementListItem

| 属性名  | 类型     | 说明                     |
| ------- | -------- | ------------------------ |
| title   | String   | 行标题(16sp 中等字重)  |
| summary | String?  | 标题下方文字(14sp)     |
| icon    | Painter? | 行首 36dp 图标           |

### UserStatementDialogDefaults

| 常量        | 值                   | COUI 来源           |
| ----------- | -------------------- | ------------------- |
| ConfirmText | "Agree and continue" | `coui_allow_text`   |
| ExitText    | "Exit"               | `coui_reject_text`  |

## 布局

- 内容左右边距均为 24dp(`coui_component_statement_panel_start_padding`、`_end_padding` + `_content_item_end_padding`)。
- logo 区距面板顶部 40dp;只有说明文字时,紧凑窗口为 110dp,更宽的窗口为 80dp。
- 声明滚动区距头部 16dp、距按钮 12dp,最高 80dp(紧凑窗口且只有说明文字时 150dp),带 32dp 渐隐。
- 两个按钮左右各缩进 32dp,间距 16dp;退出按钮距面板底部 24dp。
