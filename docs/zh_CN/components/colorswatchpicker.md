# ColorSwatchPicker

圆点色板选择器,一行圆形色块加动画选中描边环,对应 ColorOS 16 图标单色选择控件(UxColorSelectableView):44dp 单元格内 34dp 色点,选中时淡入 2dp 主题色描边环。

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.ColorSwatchPicker
import io.github.suqi8.coui.kmp.basic.ColorSwatchPickerDefaults
```

## 基本用法

```kotlin
val colors = remember {
    listOf(
        Color(0xFF0066FF),
        Color(0xFF24B232),
        Color(0xFFF08222),
        Color(0xFFDB382C),
    )
}
var selectedIndex by remember { mutableIntStateOf(0) }

ColorSwatchPicker(
    colors = colors,
    selectedIndex = selectedIndex,
    onSwatchSelected = { selectedIndex = it },
)
```

> 请在 `remember { }` 中构建 `colors` 列表(或跨重组保持同一实例):标准 `List` 对 Compose 不稳定,每次新建实例会破坏重组跳过。

## 属性

### ColorSwatchPicker

| 属性             | 类型          | 说明                             | 默认值                                    | 必需 |
| ---------------- | ------------- | -------------------------------- | ----------------------------------------- | ---- |
| colors           | List\<Color\> | 以色点展示的颜色列表             | -                                         | 是   |
| selectedIndex    | Int           | 当前选中色点下标(负值表示无选中) | -                                         | 是   |
| onSwatchSelected | (Int) -> Unit | 用户点选色点时回调其下标         | -                                         | 是   |
| modifier         | Modifier      | 应用于选择器的修饰符             | Modifier                                  | 否   |
| enabled          | Boolean       | 是否启用                         | true                                      | 否   |
| swatchSize       | Dp            | 每个色点的直径                   | ColorSwatchPickerDefaults.SwatchSize      | 否   |
| cellSize         | Dp            | 触控目标 / 选中环外径            | ColorSwatchPickerDefaults.CellSize        | 否   |
| ringStrokeWidth  | Dp            | 选中环描边宽度                   | ColorSwatchPickerDefaults.RingStrokeWidth | 否   |
| ringColor        | Color         | 选中环颜色                       | COUITheme.colorScheme.primary            | 否   |
| spacing          | Dp            | 相邻单元格间距                   | ColorSwatchPickerDefaults.Spacing         | 否   |

### ColorSwatchPickerDefaults

| 常量                | 类型              | 默认值                              | COUI 来源                    |
| ------------------- | ----------------- | ----------------------------------- | ---------------------------- |
| SwatchSize          | Dp                | 34.dp                               | ux_icon_color_select_inside  |
| CellSize            | Dp                | 44.dp                               | ux_icon_color_select_out     |
| RingStrokeWidth     | Dp                | 2.dp                                | ux_theme_shadow_stroke       |
| Spacing             | Dp                | 0.dp                                | 单元格边贴边排布             |
| RingFadeInDuration  | Int               | 280                                 | 选中动画时长                 |
| RingFadeOutDuration | Int               | 150                                 | 取消选中动画时长             |
| RingEasing          | CubicBezierEasing | CubicBezierEasing(0.33f, 0f, 0.67f, 1f) | PathInterpolator(0.33, 0, 0.67, 1) |

## 行为

- 点按单元格时以其下标回调 `onSwatchSelected`;组件无内部状态,选中态由调用方持有。
- 选中环以 280ms 淡入、150ms 淡出,使用 COUI 缓动曲线。
- 每个单元格具备 `Role.RadioButton` 语义并置于 selectable group 中,便于无障碍访问。
