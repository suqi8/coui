# ActivityDialogPreference

`ActivityDialogPreference` 是在整页上展示选项的列表偏好设置，对应 ColorOS 的 `COUIActivityDialogPreference`。在 COUI 中它是一个 `COUIListPreference`，由 `COUIActivityDialogFragment` 显示：一个像 Activity 一样从末端滑入（350ms）的全屏窗口，顶部是工具栏（返回箭头 + 标题），下方是单选卡片列表。点击某一行即提交该值并关闭页面；返回箭头或系统返回手势则不做修改直接关闭。

本库把这一页渲染为 `Scaffold` 弹出层宿主中的一个全尺寸、无遮罩图层（与 Overlay 弹窗使用同一图层），因此**必须放在 `Scaffold` 内使用**。页面由 `SmallTopAppBar` 和一张 `RadioButtonPreference` 卡片列表组成，单选按钮位于行尾。偏好设置行本身是显示当前选中项的 `ArrowPreference`。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=activityDialogPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.ActivityDialogPreference
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry
```

## 基本用法

```kotlin
val entries = remember {
    listOf(
        ListPreferenceEntry("跟随系统"),
        ListPreferenceEntry("浅色"),
        ListPreferenceEntry("深色", summary = "夜间更护眼")
    )
}
var selected by remember { mutableIntStateOf(0) }

Scaffold {
    ActivityDialogPreference(
        entries = entries,
        selectedIndex = selected,
        onSelectedIndexChange = { selected = it },
        title = "外观"
    )
}
```

## 属性

### ActivityDialogPreference 属性

| 属性名                | 类型                       | 说明                           | 默认值                                | 必须 |
| --------------------- | -------------------------- | ------------------------------ | ------------------------------------- | ---- |
| entries               | List\<ListPreferenceEntry> | 可选项列表                     | -                                     | 是   |
| selectedIndex         | Int                        | 选中项索引，-1 表示未选        | -                                     | 是   |
| onSelectedIndexChange | (Int) -> Unit              | 在页面上选中某项的回调         | -                                     | 是   |
| title                 | String                     | 标题                           | -                                     | 是   |
| modifier              | Modifier                   | 修饰符                         | Modifier                              | 否   |
| titleColor            | BasicComponentColors       | 标题颜色配置                   | BasicComponentDefaults.titleColor()   | 否   |
| summary               | String?                    | 摘要                           | null                                  | 否   |
| summaryColor          | BasicComponentColors       | 摘要颜色配置                   | BasicComponentDefaults.summaryColor() | 否   |
| dialogTitle           | String                     | 页面工具栏标题                 | title                                 | 否   |
| startAction           | @Composable (() -> Unit)?  | 起始侧自定义内容               | null                                  | 否   |
| bottomAction          | @Composable (() -> Unit)?  | 底部自定义内容                 | null                                  | 否   |
| insideMargin          | PaddingValues              | 内部边距                       | BasicComponentDefaults.InsideMargin   | 否   |
| cardListPosition      | CardListPosition           | 在卡片分组中的位置             | LocalCardListPosition.current         | 否   |
| enabled               | Boolean                    | 是否可点击                     | true                                  | 否   |
| showValue             | Boolean                    | 是否在行尾显示当前选中项       | true                                  | 否   |
| renderInRootScaffold  | Boolean                    | 是否在最外层 Scaffold 中渲染   | true                                  | 否   |

### ActivityDialogPreferenceDefaults 对象

| 名称                     | 类型          | 默认值                                                     | 说明                                                          |
| ------------------------ | ------------- | ---------------------------------------------------------- | ------------------------------------------------------------- |
| ListContentPadding       | PaddingValues | PaddingValues(start = 16.dp, top = 6.dp, end = 16.dp, bottom = 32.dp) | 卡片列表边距、support_preference_listview_margin_top、coui_list_to_ex_bottom_padding |
| DividerHorizontalPadding | Dp            | 16.dp                                                      | coui_preference_divider_default_horizontal_padding 减去卡片边距 |
| TransitionDurationMillis | Int           | 350                                                        | coui_open_slide_enter / coui_close_slide_exit 时长             |
