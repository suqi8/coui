# MarkPreference

`MarkPreference` 是带选中标记的单选行，对应 ColorOS 的 `COUIMarkPreference`。标记渲染 COUI 16 的 `coui_btn_check_mark` 选择器——它实际解析为 radio 矢量（`coui_btn_radio_on` / `coui_btn_radio_off`，24dp）：选中时为主色圆盘加白色圆心，未选中时为灰色描边圆环。标记默认位于行尾（COUI `TAIL_MARK`），整行是点击目标。

多个 `MarkPreference` 行组成单选组：点击某行触发 `onClick`，由调用方将选中状态移动到该行。

## 引入

```kotlin
import com.suqi8.coui.kmp.preference.MarkPreference
import com.suqi8.coui.kmp.preference.MarkLocation
```

## 基本用法

```kotlin
var selected by remember { mutableIntStateOf(0) }
val options = listOf("每天", "仅工作日", "从不")

Column {
    options.forEachIndexed { index, option ->
        MarkPreference(
            title = option,
            checked = selected == index,
            onClick = { selected = index }
        )
    }
}
```

## 标记位于行首（HEAD_MARK）

```kotlin
MarkPreference(
    title = "行首标记",
    checked = true,
    onClick = {},
    markLocation = MarkLocation.Start
)
```

## 组件状态

### 禁用状态

```kotlin
MarkPreference(
    title = "禁用选项",
    checked = true,
    onClick = {},
    enabled = false
)
```

## 属性

### MarkPreference 属性

| 属性名        | 类型                               | 说明                                                   | 默认值                                  | 是否必须 |
| ------------- | ---------------------------------- | ------------------------------------------------------ | --------------------------------------- | -------- |
| title         | String                             | 设置项标题                                             | -                                       | 是       |
| checked       | Boolean                            | 当前行是否持有选中标记                                 | -                                       | 是       |
| onClick       | (() -> Unit)?                      | 点击该行时的回调                                       | -                                       | 是       |
| modifier      | Modifier                           | 应用于组件的修饰符                                     | Modifier                                | 否       |
| titleColor    | BasicComponentColors               | 标题文本的颜色配置                                     | BasicComponentDefaults.titleColor()     | 否       |
| summary       | String?                            | 设置项摘要                                             | null                                    | 否       |
| summaryColor  | BasicComponentColors               | 摘要文本的颜色配置                                     | BasicComponentDefaults.summaryColor()   | 否       |
| markColors    | RadioButtonColors                  | 标记的颜色配置（与 COUI radio 矢量共用）               | RadioButtonDefaults.radioButtonColors() | 否       |
| startAction   | @Composable (() -> Unit)?          | 左侧显示的自定义内容                                   | null                                    | 否       |
| endActions    | @Composable (RowScope.() -> Unit)? | 右侧显示的自定义内容（行尾标记前）                     | null                                    | 否       |
| markLocation  | MarkLocation                       | 标记位置：End（COUI TAIL_MARK）或 Start（COUI HEAD_MARK） | MarkLocation.End                     | 否       |
| bottomAction  | @Composable (() -> Unit)?          | 底部显示的自定义内容                                   | null                                    | 否       |
| insideMargin  | PaddingValues                      | 组件内部内容的边距                                     | BasicComponentDefaults.InsideMargin     | 否       |
| holdDownState | Boolean                            | 组件是否处于按下状态                                   | false                                   | 否       |
| enabled       | Boolean                            | 组件是否可交互                                         | true                                    | 否       |

### MarkLocation 选项

| 值                 | 说明             | COUI 对应 |
| ------------------ | ---------------- | --------- |
| MarkLocation.End   | 标记位于行尾     | TAIL_MARK |
| MarkLocation.Start | 标记位于行首     | HEAD_MARK |

## 进阶用法

### 带摘要与自定义标记颜色

```kotlin
MarkPreference(
    title = "高音质",
    summary = "消耗更多流量",
    checked = selected == 0,
    onClick = { selected = 0 },
    markColors = RadioButtonDefaults.radioButtonColors(
        selectedColor = COUITheme.colorScheme.primary
    )
)
```

### 卡片内的单选组

```kotlin
var selected by remember { mutableIntStateOf(0) }
val options = listOf("选项 A", "选项 B", "选项 C")

Card {
    options.forEachIndexed { index, option ->
        if (index > 0) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        MarkPreference(
            title = option,
            checked = selected == index,
            onClick = { selected = index }
        )
    }
}
```
