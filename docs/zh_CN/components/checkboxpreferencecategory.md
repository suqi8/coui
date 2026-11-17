# CheckboxPreferenceCategory

`CheckboxPreferenceCategory` 是尾部带全选复选框的分组标题，对应 ColorOS 的 `COUICheckBoxPreferenceCategory`。复选框为三态：`ToggleableState.Indeterminate` 表示「部分选中」。点击遵循 `COUICheckBox.toggle`：全选状态变为 `Off`，其他状态变为 `On`，新状态通过 `onStateChange` 传出。标题本身不可点击。整行沿用 COUI 分组标题尺寸（12sp 中等字重标题、32dp 边距），复选框上下各留 9dp。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=checkboxPreferenceCategory" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.CheckboxPreferenceCategory
import io.github.suqi8.coui.kmp.preference.CheckboxPreferenceCategoryDefaults
```

## 基本用法

由子项推导分组状态，并把切换后的状态写回所有子项：

```kotlin
val checked = remember { mutableStateListOf(true, false, false) }
val categoryState = when (checked.count { it }) {
    0 -> ToggleableState.Off
    checked.size -> ToggleableState.On
    else -> ToggleableState.Indeterminate
}

CheckboxPreferenceCategory(
    title = "要迁移的内容",
    state = categoryState,
    onStateChange = { newState ->
        val value = newState == ToggleableState.On
        for (i in checked.indices) checked[i] = value
    }
)
Card(modifier = Modifier.padding(horizontal = 16.dp)) {
    // 与 checked[i] 绑定的 CheckboxPreference 行
}
```

## 属性

### CheckboxPreferenceCategory 属性

| 属性名         | 类型                      | 说明                     | 默认值                                            | 必须 |
| -------------- | ------------------------- | ------------------------ | ------------------------------------------------- | ---- |
| title          | String                    | 分组标题                 | -                                                 | 是   |
| state          | ToggleableState           | 全选复选框状态           | -                                                 | 是   |
| onStateChange  | (ToggleableState) -> Unit | 切换后状态的回调         | -                                                 | 是   |
| modifier       | Modifier                  | 修饰符                   | Modifier                                          | 否   |
| enabled        | Boolean                   | 复选框是否可用           | true                                              | 否   |
| titleType      | PreferenceCategoryTitleType | 标题档位（Small / Medium / Large），见 [PreferenceCategory](./preferencecategory) | PreferenceCategoryTitleType.Small | 否 |
| marginStartType | PreferenceCategoryMarginStartType | 水平边距档位（Large 32dp / Small 16dp） | PreferenceCategoryMarginStartType.Large | 否 |
| titleColor     | Color                     | 标题颜色                 | CheckboxPreferenceCategoryDefaults.titleColor(titleType) | 否 |
| checkboxColors | CheckboxColors            | 复选框颜色               | CheckboxDefaults.checkboxColors()                 | 否   |
| insideMargin   | PaddingValues             | 行的水平边距             | CheckboxPreferenceCategoryDefaults.insideMargin(marginStartType) | 否 |

### CheckboxPreferenceCategoryDefaults 对象

| 名称                    | 类型          | 默认值                                    | 说明                                                           |
| ----------------------- | ------------- | ----------------------------------------- | -------------------------------------------------------------- |
| InsideMargin            | PaddingValues | PaddingValues(horizontal = 32.dp)         | Large 边距档：标题起始边距；控件外边距 16dp + 复选框 paddingEnd 16dp |
| InsideMarginSmall       | PaddingValues | PaddingValues(horizontal = 16.dp)         | Small 边距档：标题起始边距；复选框 paddingEnd 16dp             |
| insideMargin(marginStartType) | PaddingValues | InsideMargin / InsideMarginSmall    | 按边距档取值                                                   |
| CheckboxVerticalPadding | Dp            | 9.dp                                      | 复选框内边距 5dp + coui_preference_category_widget_layout_margin_vertical 4dp |
| titleColor(titleType)   | Color         | labelSecondary（Small）/ labelPrimary   | COUI couiColorSecondNeutral（Small）/ couiColorPrimaryNeutral（Medium、Large） |
