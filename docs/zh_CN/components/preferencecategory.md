# PreferenceCategory

`PreferenceCategory` 是分组标题组件，对应 ColorOS 的 `COUIPreferenceCategory`。支持三档标题、两档起始边距、标题旁的图标或文字红点，以及行尾控件。`SmallTitle` 是它的 small 档纯标题快捷形式；复选框与加载控件分别由 `CheckboxPreferenceCategory` 和 `LoadingPreferenceCategory` 提供。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=preferenceCategory" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryDefaults
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryMarginStartType
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryTitleType
```

## 基本用法

放在它所标注的卡片上方：

```kotlin
PreferenceCategory(
    title = "网络",
    titleType = PreferenceCategoryTitleType.Medium,
    endWidget = {
        PreferenceCategoryDefaults.TextButton(text = "编辑", onClick = { /* ... */ })
    }
)
Card(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
    // 列表行
}
```

## 标题档位

| 档位   | 字号 | 最小高度 | 上 / 下边距 | 颜色                                          |
| ------ | ---- | -------- | ----------- | --------------------------------------------- |
| Small  | 12sp | 16dp     | 8dp / 8dp   | couiColorSecondNeutral（带标题图标时 PrimaryNeutral） |
| Medium | 14sp | 20dp     | 8dp / 8dp   | couiColorPrimaryNeutral                       |
| Large  | 16sp | 22dp     | 7dp / 7dp   | couiColorPrimaryNeutral                       |

字重均为 sans-serif-medium。Medium / Large 标题旁的文字类控件（文字按钮、文字加图标、加载文本）字号升为 14sp，最小高度 28dp。

## 边距档位

| 档位  | 标题起始 | 控件框末端外边距 | 控件内容末端 |
| ----- | -------- | ---------------- | ------------ |
| Large | 32dp     | 16dp             | 32dp         |
| Small | 16dp     | 0dp              | 16dp         |

现成控件在内容与控件框末端之间保留 16dp（`WidgetContentEndPadding`），因此与复选框、加载控件的内容对齐；自定义控件需自行加同样的边距。没有 `endWidget` 时，标题末端也保留与起始相同的边距。

间距约定：本行只带标题自身的上下边距，不含 COUI 分组 16dp 顶部边距。这段间距由卡片自带的 `padding(bottom = 16.dp)` 提供，合起来即 COUI「卡片底到标题文字 24dp」。不跟在这类卡片后面时，可使用 `TopMarginLarge` / `TopMarginSmall`。

## 行尾控件

| 控件                                       | COUI 布局                                      | 说明                                                                     |
| ------------------------------------------ | ---------------------------------------------- | ------------------------------------------------------------------------ |
| `PreferenceCategoryDefaults.TextButton`    | `coui_preference_category_widget_layout_textbutton` | couiColorPrimary 文字，最多两行，12dp / 4dp 胶囊按压区，上下各 6dp |
| `PreferenceCategoryDefaults.SingleIcon`    | `coui_preference_category_widget_layout_singleicon` | 图标内边距 8dp / 4dp，可点击时 16dp 圆形按压遮罩                   |
| `PreferenceCategoryDefaults.TextWithIcon`  | `coui_preference_category_widget_layout_textwithicon` | 文字（两行）+ 图标，间距 4dp，水平内边距为自身高度一半；可点击时文字 couiColorPrimaryNeutral，否则 couiColorSecondNeutral |

## 属性

### PreferenceCategory 属性

| 属性名          | 类型                                 | 说明                                         | 默认值                                              | 必须 |
| --------------- | ------------------------------------ | -------------------------------------------- | --------------------------------------------------- | ---- |
| title           | String                               | 分组标题                                     | -                                                   | 是   |
| modifier        | Modifier                             | 修饰符                                       | Modifier                                            | 否   |
| titleType       | PreferenceCategoryTitleType          | 标题档位                                     | PreferenceCategoryTitleType.Small                   | 否   |
| marginStartType | PreferenceCategoryMarginStartType    | 水平边距档位                                 | PreferenceCategoryMarginStartType.Large             | 否   |
| titleBadge      | String?                              | 标题后的文字红点；有标题图标时不显示         | null                                                | 否   |
| titleColor      | Color                                | 标题颜色；Unspecified 时按 COUI 规则取色     | Color.Unspecified                                   | 否   |
| insideMargin    | PaddingValues                        | 行内边距：标题起始 / 控件框末端              | PreferenceCategoryDefaults.insideMargin(marginStartType) | 否 |
| titleIcon       | (@Composable () -> Unit)?            | 标题后的图标                                 | null                                                | 否   |
| endWidget       | (@Composable RowScope.() -> Unit)?   | 行尾控件                                     | null                                                | 否   |

### PreferenceCategoryDefaults 对象

| 名称                               | 类型          | 默认值        | 说明                                                  |
| ---------------------------------- | ------------- | ------------- | ----------------------------------------------------- |
| TitleMarginStartLarge / Small      | Dp            | 32.dp / 16.dp | 各边距档的标题起始边距                                |
| WidgetFrameMarginEndLarge          | Dp            | 16.dp         | Large 档控件框额外末端外边距                          |
| WidgetContentEndPadding            | Dp            | 16.dp         | 控件内容与控件框末端的间距                            |
| TopMarginLarge / TopMarginSmall    | Dp            | 16.dp / 12.dp | COUI 分组顶部边距（默认不应用）                       |
| TitleMinHeightSmall / Medium / Large | Dp          | 16 / 20 / 22.dp | 标题最小高度                                        |
| TitleVerticalMargin / TitleVerticalMarginLarge | Dp | 8.dp / 7.dp | 标题上下边距                                         |
| TitleIconSpacing / TitleIconSpacingLarge | Dp      | 4.dp / 8.dp   | 标题与图标间距                                        |
| TitleBadgeSpacing                  | Dp            | 8.dp          | 标题与红点间距                                        |
| IconPressRadius                    | Dp            | 16.dp         | 图标圆形按压遮罩半径                                  |
| WidgetMinHeightWhenTitleIsNotSmall | Dp            | 28.dp         | Medium / Large 标题旁文字控件最小高度                 |
| TextButtonMarginEnd / TextButtonMarginVertical | Dp | 4.dp / 6.dp | 文字按钮外边距                                       |
| TextButtonPadding                  | PaddingValues | 12.dp / 4.dp  | 文字按钮按压区内边距                                  |
| SingleIconMarginEnd                | Dp            | 8.dp          | 单图标末端外边距                                      |
| SingleIconPadding                  | PaddingValues | 8.dp / 4.dp   | 单图标内边距                                          |
| TextWithIconSpacing                | Dp            | 4.dp          | 文字与图标间距                                        |
| TextWithIconVerticalPadding        | Dp            | 4.dp          | 可点击文字加图标的垂直按压内边距                      |
| insideMargin(marginStartType)      | PaddingValues | -             | 默认行内边距                                          |
| titleColor(titleType, hasTitleIcon) | Color        | -             | labelSecondary（Small 且无图标）/ labelPrimary      |
| titleTextStyle(titleType) / widgetTextStyle(titleType) | TextStyle | - | 标题与控件文字样式                                |
| TitleIcon / TextButton / SingleIcon / TextWithIcon | @Composable | - | 现成的标题图标与行尾控件                            |

## 进阶用法

### 标题图标

```kotlin
PreferenceCategory(
    title = "快捷方式",
    titleIcon = {
        PreferenceCategoryDefaults.TitleIcon(
            imageVector = COUIIcons.Info,
            contentDescription = "关于快捷方式",
            onClick = { showHelp = true }
        )
    }
)
```

### 文字加图标、Small 边距档

```kotlin
PreferenceCategory(
    title = "附近设备",
    marginStartType = PreferenceCategoryMarginStartType.Small,
    endWidget = {
        PreferenceCategoryDefaults.TextWithIcon(
            text = "刷新",
            icon = COUIIcons.Refresh,
            onClick = { refresh() }
        )
    }
)
```
