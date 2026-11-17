# IndividualStatementDialog

`IndividualStatementDialog` 是以底部面板形式展示的功能隐私声明,对应 ColorOS 的 `COUIIndividualStatementDialog`(`coui_component_full_page_function_privacy.xml`):无拖拽把手的面板,包含居中标题、放在最高 384dp 滚动区里的声明正文与可选的勾选项、确认按钮和退出文字按钮。

- 每个勾选项(`COUICheckBoxItemView`)显示标题、摘要和复选框,点击整行切换勾选。行之间的分割线左右缩进 24dp / 20dp。
- 有勾选项时,至少勾选一项后确认按钮才可用,声明正文字号从 14sp 降为 12sp。`onConfirm` 回传已勾选的项。每次显示面板都会清空勾选。
- 小屏竖屏(最小宽度 < 480dp)时确认按钮宽 220dp,其他情况 280dp;小屏横屏时退出与确认并排显示。
- 面板不可拖动,点击外部不会关闭;只有设置了 `onDismissRequest` 时返回手势才会关闭。

提供两种变体:`OverlayIndividualStatementDialog`(在 `Scaffold` 内)与 `WindowIndividualStatementDialog`(窗口级)。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=individualStatementDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger 前置条件
`OverlayIndividualStatementDialog` 依赖 `Scaffold` 提供的 `COUIPopupHost` 来渲染弹出内容。没有 `Scaffold` 时请使用 `WindowIndividualStatementDialog`。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayIndividualStatementDialog
// 或
import io.github.suqi8.coui.kmp.window.WindowIndividualStatementDialog

import io.github.suqi8.coui.kmp.layout.StatementPrivacyItem
import io.github.suqi8.coui.kmp.layout.IndividualStatementDialogDefaults
```

## 基本用法

```kotlin
var showDialog by remember { mutableStateOf(false) }

Scaffold {
    OverlayIndividualStatementDialog(
        show = showDialog,
        onConfirm = { checked ->
            enable(checked)
            showDialog = false
        },
        onExit = { showDialog = false },
        title = "可选功能",
        statement = AnnotatedString("请选择要开启的可选功能。"),
        privacyItems = listOf(
            StatementPrivacyItem("个性化推荐", "根据使用数据为你推荐内容"),
            StatementPrivacyItem("崩溃报告", "发送匿名崩溃日志"),
        ),
        onDismissRequest = { showDialog = false },
    )
}
```

## 属性

| 属性名               | 类型                              | 说明                                          | 默认值                                        | 是否必须 |
| -------------------- | --------------------------------- | --------------------------------------------- | --------------------------------------------- | -------- |
| show                 | Boolean                           | 是否显示面板                                  | -                                             | 是       |
| onConfirm            | (List&lt;StatementPrivacyItem&gt;) -> Unit | 确认回调,回传已勾选的项             | -                                             | 是       |
| onExit               | () -> Unit                        | 退出按钮回调                                  | -                                             | 是       |
| modifier             | Modifier                          | 应用于面板的修饰符                            | Modifier                                      | 否       |
| backgroundModifier   | Modifier                          | 作用在面板圆角裁剪内、内容之下                | Modifier                                      | 否       |
| title                | String?                           | 居中标题(18sp 中等字重)                     | null                                          | 否       |
| statement            | AnnotatedString?                  | 声明正文(14sp,有勾选项时 12sp),可带链接   | null                                          | 否       |
| privacyItems         | List&lt;StatementPrivacyItem&gt;  | 勾选项                                        | emptyList()                                   | 否       |
| confirmText          | String                            | 确认按钮文字                                  | IndividualStatementDialogDefaults.ConfirmText | 否       |
| exitText             | String                            | 退出按钮文字                                  | IndividualStatementDialogDefaults.ExitText    | 否       |
| backgroundColor      | Color                             | 面板背景色                                    | BottomSheetDefaults.backgroundColor()         | 否       |
| enableWindowDim      | Boolean                           | 是否显示遮罩                                  | true                                          | 否       |
| onDismissRequest     | (() -> Unit)?                     | 返回手势回调;为 null 时忽略返回              | null                                          | 否       |
| onDismissFinished    | (() -> Unit)?                     | 面板完全关闭后回调                            | null                                          | 否       |
| renderInRootScaffold | Boolean                           | (仅 Overlay)是否在根 Scaffold 中渲染         | true                                          | 否       |
| materialStroke       | Boolean                           | 是否绘制 COUI 17 模糊面板的材质描边           | false                                         | 否       |

### StatementPrivacyItem

| 属性名  | 类型    | 说明         |
| ------- | ------- | ------------ |
| title   | String  | 行标题       |
| summary | String? | 标题下方文字 |

### IndividualStatementDialogDefaults

| 常量        | 值                   | COUI 来源           |
| ----------- | -------------------- | ------------------- |
| ConfirmText | "Agree and continue" | `coui_allow_text`   |
| ExitText    | "Exit"               | `coui_reject_text`  |
