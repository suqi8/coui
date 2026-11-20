# NavigationRail

`NavigationRail` 是 COUI 中的侧边导航组件，适用于宽屏设备。提供不同的显示模式（仅图标、仅文本、图标和文本、仅选中项显示文本）。

头部固定在侧栏顶部。导航项按 `COUINavigationRailMenuView` 的规则分布在侧栏中部：每项之前与最后一项之后各有一段间隔，间隔为 `max((侧栏高度 x 0.4 - 各项高度之和) / (项数 + 1), 8dp)`，整组垂直居中。整组不会上移到头部下方，放不下时侧栏可滚动。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=navigationRail" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.NavigationRail
import io.github.suqi8.coui.kmp.basic.NavigationRailItem
import io.github.suqi8.coui.kmp.basic.NavigationRailDisplayMode
```

## 基本用法

NavigationRail 组件可用于创建侧边导航菜单：

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Home", "Profile", "Settings")
val icons = listOf(COUIIcons.VerticalSplit, COUIIcons.Contacts, COUIIcons.Settings)

Row {
    NavigationRail {
        items.forEachIndexed { index, label ->
            NavigationRailItem(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                icon = icons[index],
                label = label
            )
        }
    }
    // Content area
}
```

## 组件状态

### 选中状态

`NavigationRailItem` 使用 COUI 侧栏配色（`coui_navigation_rail_icon_tint_color_selector` / `coui_navigation_rail_label_tint_color_selector`）：常态 `?couiColorSecondNeutral`，选中或按压时 `?couiColorLabelPrimary`，禁用时 `?couiColorDisabledNeutral`，颜色瞬时切换。标签始终使用 Medium 字重（`sans-serif-medium`），最多两行。

### 悬停与焦点

指针悬停在导航项上或键盘焦点落在导航项上时，图标与标签后方会淡入 COUI 的悬停 / 焦点蒙层：距导航项左右各 4dp 的 8dp 圆角矩形（`COUINavigationRailItemView`，`?couiColorHover` / `?couiColorFocus`，各自走 response 0.3 弹簧）。点击不显示蒙层。

### 红点

`badge` 在图标上显示 COUI 红点，位置与 `NavigationBarItem` 相同：`0` 为 6dp 圆点，超出图标右上角 1dp；正数为数字红点，右边缘超出图标 8dp（小于 10 时 -2dp，100 到 999 时 +1dp），上边缘高出图标 5dp。

## 属性

### NavigationRail 属性

| 属性名                     | 类型                                   | 说明                                          | 默认值                            | 是否必须 |
| -------------------------- | -------------------------------------- | --------------------------------------------- | --------------------------------- | -------- |
| modifier                   | Modifier                               | 应用于 NavigationRail 的修饰符                | Modifier                          | 否       |
| header                     | @Composable (ColumnScope.() -> Unit)?  | 头部内容（通常是 FAB 或 Logo）                | null                              | 否       |
| color                      | Color                                  | NavigationRail 的背景颜色                     | COUITheme.colorScheme.backgroundWithCard    | 否       |
| showDivider                | Boolean                                | 是否在 NavigationRail 和内容之间显示分割线    | true                              | 否       |
| defaultWindowInsetsPadding | Boolean                                | 是否对 NavigationRail 应用默认的窗口边距      | true                              | 否       |
| minWidth                   | Dp                                     | NavigationRail 的最小宽度                     | NavigationRailDefaults.MinWidth   | 否       |
| mode                       | NavigationRailDisplayMode                  | 项目的显示模式                                | NavigationRailDisplayMode.IconAndText | 否       |
| content                    | @Composable () -> Unit                 | 侧栏的导航项；每个根组合项按侧栏全宽作为一项布局 | -                              | 是       |

### NavigationRailItem 属性

| 属性名 | 类型        | 说明             | 默认值 | 是否必须 |
| ------ | ----------- | ---------------- | ------ | -------- |
| selected | Boolean     | 是否选中         | -      | 是       |
| onClick  | () -> Unit  | 点击时的回调     | -      | 是       |
| icon     | ImageVector | 该项的图标       | -      | 是       |
| label    | String      | 该项的标签文本   | -      | 是       |
| modifier | Modifier    | 应用于 NavigationRailItem 的修饰符 | Modifier | 否       |
| enabled  | Boolean     | 是否启用该项     | true     | 否       |
| badge    | Int?        | 图标上的红点：`null` 无，`0` 圆点，正数为数字红点；`TextOnly` 模式与禁用项不显示 | null | 否 |
| colors   | NavigationRailItemColors | 图标与标签的内容颜色 | NavigationRailDefaults.itemColors() | 否 |

### NavigationRailItemColors

| 属性名          | 类型  | 说明                     |
| --------------- | ----- | ------------------------ |
| selectedColor   | Color | 选中项的颜色             |
| unselectedColor | Color | 未选中项的颜色           |
| pressedColor    | Color | 按压时的颜色（不论选中与否） |
| disabledColor   | Color | 禁用项的颜色             |

### NavigationRailDefaults 对象

NavigationRailDefaults 对象提供了 NavigationRail 和 NavigationRailItem 组件的默认值。

#### 常量

| 常量名                 | 类型     | 说明                         | 默认值 |
| ---------------------- | -------- | ---------------------------- | ------ |
| MinWidth               | Dp       | 导航栏最小宽度               | 72.dp  |
| VerticalPadding        | Dp       | 头部距侧栏顶部的距离         | 24.dp  |
| HeaderSpacing          | Dp       | 头部与第一项之间的最小间距   | 24.dp  |
| MinItemSpacing         | Dp       | 每项之前的最小间隔（`coui_navigation_rail_root_view_divider_height`） | 8.dp |
| ItemAreaFraction       | Float    | 导航项分布所占的侧栏高度比例 | 0.4f   |
| IconSize               | Dp       | 图标尺寸                     | 24.dp  |
| IconTextSpacing        | Dp       | 图标与文字间距               | 2.dp   |
| ItemVerticalPadding    | Dp       | 图标上方与标签下方的间距（`coui_navigation_margin_vertical`） | 12.dp |
| LabelFontSize          | Dp       | 标签字号，单位 dp（不随字体缩放） | 10.dp |
| LabelMaxLines          | Int      | 标签最多行数                 | 2      |
| ItemMaskCornerRadius   | Dp       | 悬停 / 焦点蒙层的圆角        | 8.dp   |
| ItemMaskHorizontalInset | Dp      | 悬停 / 焦点蒙层距导航项左右的距离 | 4.dp |
| TextOnlyFontSize       | TextUnit | TextOnly 模式字号            | 14.sp  |
| TextOnlyVerticalPadding| Dp       | TextOnly 模式垂直内边距      | 4.dp   |

#### 函数

| 函数名       | 返回类型                 | 说明 |
| ------------ | ------------------------ | ---- |
| itemColors() | NavigationRailItemColors | COUI 侧栏配色：选中 `?couiColorLabelPrimary`，常态 `?couiColorSecondNeutral`，按压 `?couiColorPrimaryNeutral`，禁用 `?couiColorDisabledNeutral` |

### NavigationRailDisplayMode

| 值                    | 描述                                    |
| --------------------- | --------------------------------------- |
| IconAndText           | 同时显示图标和文本。                    |
| IconOnly              | 仅显示图标。                            |
| TextOnly              | 仅显示文本。                            |
| IconWithSelectedLabel | 始终显示图标，仅在选中时显示文本。      |

设置在 `NavigationRail` 上的显示模式会通过 `LocalNavigationRailDisplayMode` 组合局部值传递给各导航项。

## 进阶用法

### 显示模式

```kotlin
NavigationRail(
    // IconAndText (default) / IconOnly / TextOnly / IconWithSelectedLabel
    mode = NavigationRailDisplayMode.IconWithSelectedLabel
) {
    // ... items ...
}
```

### 使用头部内容

```kotlin
NavigationRail(
    header = {
        // Usually a FloatingActionButton or a logo
        FloatingActionButton(
            onClick = { /* Handle click */ }
        ) {
            Icon(
                imageVector = COUIIcons.Add,
                contentDescription = "Add"
            )
        }
    }
) {
    // ... items ...
}
```

### 自定义颜色和宽度

```kotlin
NavigationRail(
    color = COUITheme.colorScheme.backgroundElevated,
    minWidth = 96.dp
) {
    // ... items ...
}
```

### 无分割线

```kotlin
NavigationRail(
    showDivider = false
) {
    // ... items ...
}
```
