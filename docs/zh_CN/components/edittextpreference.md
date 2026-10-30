# EditTextPreference

`EditTextPreference` 是点击后在输入对话框中编辑文本的跳转行，对应 ColorOS 的 `COUIEditTextPreference` 与 `COUIEditTextPreferenceDialogFragment`。行尾在箭头前显示当前值。点击后弹出 `OverlayDialog`，内含单行下划线输入框（左右 24dp 边距，带快速删除），弹出即获得焦点，底部为取消 / 确定按钮栏。输入框为空时确定按钮不可用，除非设置 `supportEmptyInput`。

::: warning
对话框是 `OverlayDialog`，组件需要放在 `Scaffold` 内。
:::

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=editTextPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.EditTextPreference
import io.github.suqi8.coui.kmp.preference.EditTextPreferenceDefaults
```

## 基本用法

```kotlin
var nickname by remember { mutableStateOf("Suqi") }

Scaffold {
    EditTextPreference(
        value = nickname,
        onValueChange = { nickname = it },
        title = "Nickname",
        positiveButtonText = "OK",
        negativeButtonText = "Cancel",
        hint = "Enter a nickname"
    )
}
```

只有点击确定时才会调用 `onValueChange`；取消或点击外部会保留原值。

## 组件状态

### 允许空输入

```kotlin
EditTextPreference(
    value = signature,
    onValueChange = { signature = it },
    title = "Signature",
    positiveButtonText = "OK",
    negativeButtonText = "Cancel",
    dialogMessage = "Shown under your name.",
    supportEmptyInput = true
)
```

### 禁用状态

```kotlin
EditTextPreference(
    value = "Locked",
    onValueChange = {},
    title = "Disabled",
    positiveButtonText = "OK",
    negativeButtonText = "Cancel",
    enabled = false
)
```

## 属性

### EditTextPreference 属性

| 属性名               | 类型                        | 说明                                   | 默认值                                 | 是否必须 |
| -------------------- | --------------------------- | -------------------------------------- | -------------------------------------- | -------- |
| value                | String                      | 当前文本值                             | -                                      | 是       |
| onValueChange        | (String) -> Unit            | 确定时回调编辑后的文本                 | -                                      | 是       |
| title                | String                      | 行标题                                 | -                                      | 是       |
| positiveButtonText   | String                      | 确定按钮文字                           | -                                      | 是       |
| negativeButtonText   | String                      | 取消按钮文字                           | -                                      | 是       |
| modifier             | Modifier                    | 组件修饰符                             | Modifier                               | 否       |
| titleColor           | BasicComponentColors        | 标题颜色                               | BasicComponentDefaults.titleColor()    | 否       |
| summary              | String?                     | 行摘要                                 | null                                   | 否       |
| summaryColor         | BasicComponentColors        | 摘要颜色                               | BasicComponentDefaults.summaryColor()  | 否       |
| dialogTitle          | String?                     | 对话框标题                             | title                                  | 否       |
| dialogMessage        | String?                     | 对话框说明文字                         | null                                   | 否       |
| hint                 | String                      | 输入框提示                             | ""                                     | 否       |
| showValue            | Boolean                     | 在行尾显示当前值                       | true                                   | 否       |
| supportEmptyInput    | Boolean                     | 输入为空时确定按钮仍可用               | false                                  | 否       |
| textFieldColors      | TextFieldColors             | 输入框颜色                             | TextFieldDefaults.textFieldColors()    | 否       |
| keyboardOptions      | KeyboardOptions             | 输入框键盘选项                         | KeyboardOptions.Default                | 否       |
| startAction          | @Composable (() -> Unit)?   | 左侧内容                               | null                                   | 否       |
| bottomAction         | @Composable (() -> Unit)?   | 底部内容                               | null                                   | 否       |
| insideMargin         | PaddingValues               | 行内边距                               | BasicComponentDefaults.InsideMargin    | 否       |
| cardListPosition     | CardListPosition            | 行在卡片分组中的位置                   | LocalCardListPosition.current          | 否       |
| enabled              | Boolean                     | 行是否可点击                           | true                                   | 否       |
| renderInRootScaffold | Boolean                     | 是否在根 Scaffold 中渲染对话框         | true                                   | 否       |
| onExpandedChange     | ((Boolean) -> Unit)?        | 对话框显示 / 关闭时回调                | null                                   | 否       |

### EditTextPreferenceDefaults 对象

| 常量                  | 类型 | 默认值 | 说明                                                  |
| --------------------- | ---- | ------ | ----------------------------------------------------- |
| FieldHorizontalMargin | Dp   | 24.dp  | support_preference_dialog_edittext_margin_horizontal  |
| FieldBottomMargin     | Dp   | 14.dp  | coui_edittextdialog_marginbottom                      |
| FieldMinHeight        | Dp   | 34.dp  | support_preference_dialog_edittext_min_height         |
