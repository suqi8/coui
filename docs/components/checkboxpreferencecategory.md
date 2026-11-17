# CheckboxPreferenceCategory

`CheckboxPreferenceCategory` is a category title with a trailing select-all checkbox, mirroring ColorOS's `COUICheckBoxPreferenceCategory`. The checkbox is tri-state: `ToggleableState.Indeterminate` reads as "some items selected". Tapping it follows `COUICheckBox.toggle`: a fully checked box turns `Off`, any other state turns `On`, and the new state is passed to `onStateChange`. The title itself is not clickable. The row uses the COUI category metrics (12sp medium title, 32dp margins) and the checkbox has 9dp above and below it.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=checkboxPreferenceCategory" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.CheckboxPreferenceCategory
import io.github.suqi8.coui.kmp.preference.CheckboxPreferenceCategoryDefaults
```

## Basic Usage

Derive the category state from the items and write the toggled state back to all of them:

```kotlin
val checked = remember { mutableStateListOf(true, false, false) }
val categoryState = when (checked.count { it }) {
    0 -> ToggleableState.Off
    checked.size -> ToggleableState.On
    else -> ToggleableState.Indeterminate
}

CheckboxPreferenceCategory(
    title = "Items to migrate",
    state = categoryState,
    onStateChange = { newState ->
        val value = newState == ToggleableState.On
        for (i in checked.indices) checked[i] = value
    }
)
Card(modifier = Modifier.padding(horizontal = 16.dp)) {
    // CheckboxPreference rows bound to checked[i]
}
```

## Properties

### CheckboxPreferenceCategory Properties

| Property Name  | Type                         | Description                                  | Default Value                                     | Required |
| -------------- | ---------------------------- | -------------------------------------------- | ------------------------------------------------- | -------- |
| title          | String                       | Category title                               | -                                                 | Yes      |
| state          | ToggleableState              | State of the select-all checkbox             | -                                                 | Yes      |
| onStateChange  | (ToggleableState) -> Unit    | Callback with the toggled state              | -                                                 | Yes      |
| modifier       | Modifier                     | Component modifier                           | Modifier                                          | No       |
| enabled        | Boolean                      | Whether the checkbox is enabled              | true                                              | No       |
| titleType      | PreferenceCategoryTitleType  | Title size (Small / Medium / Large), see [PreferenceCategory](./preferencecategory) | PreferenceCategoryTitleType.Small | No |
| marginStartType | PreferenceCategoryMarginStartType | Horizontal margin type (Large 32dp / Small 16dp) | PreferenceCategoryMarginStartType.Large | No |
| titleColor     | Color                        | Title color                                  | CheckboxPreferenceCategoryDefaults.titleColor(titleType) | No |
| checkboxColors | CheckboxColors               | Checkbox colors                              | CheckboxDefaults.checkboxColors()                 | No       |
| insideMargin   | PaddingValues                | Horizontal margin of the row                 | CheckboxPreferenceCategoryDefaults.insideMargin(marginStartType) | No |

### CheckboxPreferenceCategoryDefaults Object

| Name                    | Type          | Default Value                             | Description                                                         |
| ----------------------- | ------------- | ----------------------------------------- | ------------------------------------------------------------------- |
| InsideMargin            | PaddingValues | PaddingValues(horizontal = 32.dp)         | Large margin type: title start margin; widget margin 16dp + checkbox paddingEnd 16dp |
| InsideMarginSmall       | PaddingValues | PaddingValues(horizontal = 16.dp)         | Small margin type: title start margin; checkbox paddingEnd 16dp     |
| insideMargin(marginStartType) | PaddingValues | InsideMargin / InsideMarginSmall    | Margin for the given margin type                                    |
| CheckboxVerticalPadding | Dp            | 9.dp                                      | Checkbox padding 5dp + coui_preference_category_widget_layout_margin_vertical 4dp |
| titleColor(titleType)   | Color         | labelSecondary (Small) / labelPrimary   | COUI couiColorSecondNeutral (Small) / couiColorPrimaryNeutral (Medium, Large) |
