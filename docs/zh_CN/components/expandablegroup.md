# ExpandableGroup

可展开分组复刻 ColorOS 的可展开列表（`COUIExpandableRecyclerView` / `COUIExpandableRecyclerAdapter`，配合 `COUIRotateView` 箭头）：分组标题展开或收起其子项，子项在伸缩的同时淡入淡出，标题箭头随之翻转。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=expandableGroup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.ExpandableGroupArrow
import io.github.suqi8.coui.kmp.basic.ExpandableGroupContent
import io.github.suqi8.coui.kmp.basic.ExpandableGroupDefaults
import io.github.suqi8.coui.kmp.basic.ExpandableGroupHeader
import io.github.suqi8.coui.kmp.basic.expandableGroup
```

## 基本用法

在 `LazyColumn` 中，`expandableGroup` 会把标题和子项作为两个 item 加入：

```kotlin
var expanded by remember { mutableStateOf(false) }

LazyColumn {
    expandableGroup(
        key = "more",
        title = "More settings",
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        BasicComponent(title = "Accessibility", onClick = {})
        BasicComponent(title = "Backup and reset", onClick = {})
    }
}
```

## 在懒加载列表之外使用

两个构件也可以直接使用，例如放在 `Card` 里：

```kotlin
ExpandableGroupHeader(
    title = "Network",
    expanded = expanded,
    onExpandedChange = { expanded = it },
    summary = "Wi-Fi, mobile network, hotspot",
)
ExpandableGroupContent(expanded = expanded) {
    BasicComponent(title = "Wi-Fi", onClick = {})
    BasicComponent(title = "Mobile network", onClick = {})
}
```

`ExpandableGroupArrow` 是单独的翻转箭头，可用于自定义标题。

## 属性

### expandableGroup（LazyListScope）

| 属性名           | 类型                               | 说明                                      | 默认值 | 是否必须 |
| ---------------- | ---------------------------------- | ----------------------------------------- | ------ | -------- |
| key              | Any                                | 稳定的分组 key（item key 由其 `toString` 派生） | -  | 是       |
| title            | String                             | 标题                                      | -      | 是       |
| expanded         | Boolean                            | 是否展开                                  | -      | 是       |
| onExpandedChange | (Boolean) -> Unit                  | 点击标题时以期望状态回调                  | -      | 是       |
| summary          | String?                            | 标题摘要                                  | null   | 否       |
| enabled          | Boolean                            | 标题是否可点击                            | true   | 否       |
| content          | @Composable ColumnScope.() -> Unit | 分组子项                                  | -      | 是       |

### ExpandableGroupHeader

| 属性名           | 类型              | 说明                       | 默认值                               | 是否必须 |
| ---------------- | ----------------- | -------------------------- | ------------------------------------ | -------- |
| title            | String            | 标题                       | -                                    | 是       |
| expanded         | Boolean           | 是否展开                   | -                                    | 是       |
| onExpandedChange | (Boolean) -> Unit | 点击时以期望状态回调       | -                                    | 是       |
| modifier         | Modifier          | 应用于标题的修饰符         | Modifier                             | 否       |
| summary          | String?           | 标题摘要                   | null                                 | 否       |
| enabled          | Boolean           | 是否可点击                 | true                                 | 否       |
| arrowColor       | Color             | 箭头颜色                   | ExpandableGroupDefaults.arrowColor() | 否       |

### ExpandableGroupContent

| 属性名   | 类型                               | 说明               | 默认值   | 是否必须 |
| -------- | ---------------------------------- | ------------------ | -------- | -------- |
| expanded | Boolean                            | 是否展开           | -        | 是       |
| modifier | Modifier                           | 应用于子项的修饰符 | Modifier | 否       |
| content  | @Composable ColumnScope.() -> Unit | 分组子项           | -        | 是       |

### ExpandableGroupArrow

| 属性名   | 类型     | 说明               | 默认值                               | 是否必须 |
| -------- | -------- | ------------------ | ------------------------------------ | -------- |
| expanded | Boolean  | 是否显示为展开     | -                                    | 是       |
| modifier | Modifier | 应用于箭头的修饰符 | Modifier                             | 否       |
| color    | Color    | 箭头颜色           | ExpandableGroupDefaults.arrowColor() | 否       |
| size     | Dp       | 箭头尺寸           | ExpandableGroupDefaults.ArrowSize    | 否       |

### ExpandableGroupDefaults

| 成员         | 类型  | 默认值                           | COUI 来源                                   |
| ------------ | ----- | -------------------------------- | ------------------------------------------- |
| ArrowSize    | Dp    | 24.dp                            | coui_app_expander_close_default             |
| arrowColor() | Color | COUITheme.colorScheme.dragHandle | coui_color_controls（#29000000 / #40FFFFFF） |

## 行为

- 子项在 400ms 内按 COUIMoveEaseInterpolator (0.3, 0, 0.1, 1) 从零高度伸展到完整高度（或反向收起），透明度等于可见高度比例，与 `ExpandableRecyclerConnector` 的占位视图一致。中途再次切换时从当前高度开始，时长按剩余距离等比缩短。
- 箭头在 400ms 内按 (0.133, 0, 0.3, 1) 从 0 度转到 180 度（`COUIRotateView`，z 轴旋转类型）。
- 仅在展开或动画进行时组合子项。标题提供展开 / 收起的无障碍操作。
