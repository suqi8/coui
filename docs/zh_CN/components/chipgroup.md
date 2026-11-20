# ChipGroup

`ChipGroup` 将多个 Chip 按可用宽度自动换行排列，对应 COUIChipGroup：Chip 之间与行之间间距 8dp，支持 COUI 的单选与必选规则，以及只显示前几行、通过展开 / 收起按钮切换的折叠模式。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=chipGroup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.ChipGroup
import io.github.suqi8.coui.kmp.basic.ChipGroupDefaults
import io.github.suqi8.coui.kmp.basic.ChipGroupState
import io.github.suqi8.coui.kmp.basic.rememberChipGroupState
```

## 基本用法

选中状态保存在 `ChipGroupState` 中：每个 Chip 点击时调用 `toggle(index)`，用 `isSelected(index)` 读取选中态。

```kotlin
val state = rememberChipGroupState()
val labels = listOf("推荐", "附近", "热门", "最新")

ChipGroup(state = state) {
    labels.forEachIndexed { index, label ->
        Chip(
            selected = state.isSelected(index),
            onClick = { state.toggle(index) },
            label = label,
        )
    }
}
```

## 单选

`singleSelection` 保证最多只有一个 Chip 被选中；`selectionRequired` 拒绝取消最后一个选中的 Chip（COUI `setSingleSelection` / `setSelectionRequired`）：

```kotlin
val state = rememberChipGroupState(
    singleSelection = true,
    selectionRequired = true,
    initialSelection = setOf(0),
)
```

## 折叠

`collapsable = true` 且行数多于 `collapsedMaxRows` 时，组内显示 28dp 圆形的展开 / 收起按钮（`IconChip`）。折叠时只显示前 `collapsedMaxRows` 行，最后一行为展开按钮预留位置；展开后收起按钮跟在最后一个 Chip 之后。高度变化使用 COUI 弹簧（response 0.35，bounce 0）。

## 动画

`animationsEnabled` 开启时（默认，对应 COUI `setChipGroupAnimationsEnabled`），Chip 的运动对齐 COUIChip 的 `ChipGroupAnimatorImpl`：位置变化的 Chip 以弹簧移到新位置（response 0.3，bounce 0）；组不再显示的 Chip（折叠时）在原位淡出，重新显示的 Chip 淡入（透明度弹簧，response 0.2）；新加入组的 Chip 还会从 0.8 放大；展开与收起按钮是两个 Chip，相互交叉淡化。首次布局与可用宽度变化后不播放动画。Chip 有 `Modifier.layoutId` 时按它跟踪，否则按下标跟踪；在中间插入或移除 Chip 时请为其设置稳定的 `layoutId`。

```kotlin
val state = rememberChipGroupState(initialCollapsed = true)

ChipGroup(state = state, collapsable = true, collapsedMaxRows = 2) {
    // chips
}
```

## 单行

`singleLine = true` 时所有 Chip 保持在同一行，请放进可横向滚动的容器：

```kotlin
ChipGroup(
    modifier = Modifier.horizontalScroll(rememberScrollState()),
    singleLine = true,
) {
    // chips
}
```

## 属性

### ChipGroup

| 属性                 | 类型                   | 说明                     | 默认值                              | 必需 |
| -------------------- | ---------------------- | ------------------------ | ----------------------------------- | ---- |
| modifier             | Modifier               | 应用于组的修饰符         | Modifier                            | 否   |
| state                | ChipGroupState         | 选中与折叠状态           | rememberChipGroupState()            | 否   |
| singleLine           | Boolean                | 是否保持单行             | false                               | 否   |
| collapsable          | Boolean                | 是否可折叠               | false                               | 否   |
| collapsedMaxRows     | Int                    | 折叠时显示的行数         | ChipGroupDefaults.CollapsedMaxRows  | 否   |
| horizontalSpacing    | Dp                     | 同一行 Chip 之间的间距   | ChipGroupDefaults.HorizontalSpacing | 否   |
| verticalSpacing      | Dp                     | 行与行之间的间距         | ChipGroupDefaults.VerticalSpacing   | 否   |
| collapseButtonColors | ChipColors             | 展开 / 收起按钮的颜色    | ChipDefaults.chipColors()           | 否   |
| animationsEnabled    | Boolean                | 布局变化时 Chip 是否播放动画 | true                            | 否   |
| content              | @Composable () -> Unit | 组内的 Chip              | -                                   | 是   |

### ChipGroupState

| 成员              | 类型     | 说明                                     |
| ----------------- | -------- | ---------------------------------------- |
| singleSelection   | Boolean  | 最多只能选中一个                         |
| selectionRequired | Boolean  | 不能取消最后一个选中项                   |
| selectedIndices   | Set\<Int> | 选中的 Chip 下标                         |
| collapsed         | Boolean  | 可折叠的组当前是否折叠（可写）           |
| isSelected(index) | Boolean  | 下标为 `index` 的 Chip 是否选中          |
| toggle(index)     | Unit     | 按选择规则切换下标为 `index` 的 Chip     |
| clearSelection()  | Unit     | 清空选择                                 |

`rememberChipGroupState(singleSelection, selectionRequired, initialSelection, initialCollapsed)` 用于创建并记住状态。

### ChipGroupDefaults

| 常量              | 类型 | 默认值 | COUI 来源                                  |
| ----------------- | ---- | ------ | ------------------------------------------ |
| HorizontalSpacing | Dp   | 8.dp   | coui_chip_group_default_spacing_horizontal |
| VerticalSpacing   | Dp   | 8.dp   | coui_chip_group_default_spacing_vertical   |
| CollapsedMaxRows  | Int  | 3      | COUIChipGroup `collapsedMaxRows` 默认值    |
