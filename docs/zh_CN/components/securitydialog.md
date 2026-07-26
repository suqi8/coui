# SecurityDialog

`SecurityDialog` 是安全声明对话框, 对应 ColorOS 的 COUISecurityAlertDialogBuilder (`coui_security_alert_dialog_statement_or_checkbox.xml`): 在常规对话框基础上扩展声明文字段落 (含可点击链接)、「不再提醒」勾选行, 以及取消 / 确认双按钮 (无填充强调色文字按钮)。

提供两种形态:

- `OverlaySecurityDialog` — 渲染在 `Scaffold` 的 `COUIPopupHost` 中 (必须在 `Scaffold` 内使用)。
- `WindowSecurityDialog` — 渲染在窗口层级, 无需 `Scaffold`。

::: danger 前提条件
`OverlaySecurityDialog` 依赖 `Scaffold` 提供的 `COUIPopupHost` 渲染弹窗内容。没有 `Scaffold` 时请使用 `WindowSecurityDialog`。
:::

## 引入

```kotlin
import com.suqi8.coui.kmp.overlay.OverlaySecurityDialog
// 或
import com.suqi8.coui.kmp.window.WindowSecurityDialog

import com.suqi8.coui.kmp.layout.SecurityDialogDefaults
import com.suqi8.coui.kmp.layout.SecurityDialogColors
```

## 基本用法

```kotlin
var showDialog by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示安全声明弹窗",
        onClick = { showDialog = true }
    )

    OverlaySecurityDialog(
        show = showDialog,
        title = "安全提示",
        summary = "该功能需要连接网络。",
        statement = "点击查看隐私政策了解更多信息。",
        statementLinkText = "隐私政策",
        onLinkClick = { /* 打开隐私政策 */ },
        onConfirm = { dontRemind ->
            showDialog = false
            if (dontRemind) { /* 持久化用户选择 */ }
        },
        onCancel = { showDialog = false }
    )
}
```

- `statement` 中与 `statementLinkText` 相同的子串会以强调色渲染, 点按时回调 `onLinkClick`。
- 点击外部与按返回键均回调 `onCancel`, 对应 COUI 将返回键上报为负向选择的行为。
- 传入 `checkboxText = null` 可隐藏勾选行 (等价于 `setHasCheckBox(false)`)。

## 属性

### OverlaySecurityDialog / WindowSecurityDialog 属性

| 属性名               | 类型                 | 说明                                                              | 默认值                                        | 必需 |
| -------------------- | -------------------- | ----------------------------------------------------------------- | --------------------------------------------- | ---- |
| show                 | Boolean              | 是否显示对话框                                                    | -                                             | 是   |
| onConfirm            | (Boolean) -> Unit    | 点击确认按钮时回调, 携带当前勾选状态                              | -                                             | 是   |
| onCancel             | () -> Unit           | 点击取消按钮、点击外部或按返回键时回调                            | -                                             | 是   |
| modifier             | Modifier             | 应用于对话框的修饰符                                              | Modifier                                      | 否   |
| title                | String?              | 对话框标题                                                        | null                                          | 否   |
| summary              | String?              | 对话框摘要 (message)                                              | null                                          | 否   |
| statement            | String?              | 声明文字段落; 为 null 时隐藏                                      | null                                          | 否   |
| statementLinkText    | String?              | `statement` 中渲染为可点击链接的子串                              | null                                          | 否   |
| onLinkClick          | (() -> Unit)?        | 点击声明链接时回调                                                | null                                          | 否   |
| checkboxText         | String?              | 勾选行文字; 为 null 时隐藏                                        | SecurityDialogDefaults.CheckboxText           | 否   |
| initialChecked       | Boolean              | 初始勾选状态, 每次显示对话框时重新应用                            | false                                         | 否   |
| confirmText          | String               | 确认 (positive) 按钮文字                                          | SecurityDialogDefaults.ConfirmText            | 否   |
| cancelText           | String               | 取消 (negative) 按钮文字                                          | SecurityDialogDefaults.CancelText             | 否   |
| titleColor           | Color                | 标题颜色                                                          | DialogDefaults.titleColor()                   | 否   |
| summaryColor         | Color                | 摘要颜色                                                          | DialogDefaults.summaryColor()                 | 否   |
| backgroundColor      | Color                | 对话框背景色                                                      | DialogDefaults.backgroundColor()              | 否   |
| colors               | SecurityDialogColors | 声明、链接与勾选文字的颜色                                        | SecurityDialogDefaults.securityDialogColors() | 否   |
| enableWindowDim      | Boolean              | 是否启用背景遮罩                                                  | true                                          | 否   |
| onDismissFinished    | (() -> Unit)?        | 隐藏动画完成后回调                                                | null                                          | 否   |
| renderInRootScaffold | Boolean              | 是否渲染在根 (最外层) Scaffold 中 (仅 `OverlaySecurityDialog`)    | true                                          | 否   |

### SecurityDialogDefaults 对象

#### 属性

| 属性名       | 类型   | 说明                                        |
| ------------ | ------ | ------------------------------------------- |
| CheckboxText | String | 默认勾选行文字 ("Don't remind me again")    |
| ConfirmText  | String | 默认确认按钮文字 ("OK")                     |
| CancelText   | String | 默认取消按钮文字 ("Cancel")                 |

#### 函数

| 函数名                 | 返回类型             | 说明                                 |
| ---------------------- | -------------------- | ------------------------------------ |
| securityDialogColors() | SecurityDialogColors | 创建默认的声明 / 链接 / 勾选文字颜色 |

### SecurityDialogColors 属性

| 属性名            | 类型  | 说明                                    |
| ----------------- | ----- | --------------------------------------- |
| statementColor    | Color | 声明文字颜色 (次级标签色)               |
| linkColor         | Color | 链接颜色 (强调色, 按下时 30% 透明度)    |
| checkboxTextColor | Color | 勾选文字颜色 (次级标签色)               |

## 进阶用法

### 不带声明与勾选行

```kotlin
OverlaySecurityDialog(
    show = showDialog,
    title = "开启功能",
    summary = "是否开启该功能?",
    checkboxText = null, // 隐藏勾选行
    onConfirm = { _ -> showDialog = false },
    onCancel = { showDialog = false }
)
```

### 自定义文案与初始状态

```kotlin
OverlaySecurityDialog(
    show = showDialog,
    title = "流量使用提醒",
    statement = "详情请查看用户协议。",
    statementLinkText = "用户协议",
    checkboxText = "不再询问",
    initialChecked = true,
    confirmText = "同意",
    cancelText = "不同意",
    onConfirm = { dontRemind -> showDialog = false },
    onCancel = { showDialog = false }
)
```
