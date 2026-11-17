# LoadingPreferenceCategory

`LoadingPreferenceCategory` 是尾部带加载动画的分组标题，对应 ColorOS 的 `COUILoadingPreferenceCategory`（例如扫描中的「可用设备」）。加载时行尾显示 16dp 的小号 COUI 旋转加载动画，可在其前方显示加载文本（12sp 中等字重，最多两行，与动画间距 4dp）。加载结束后由 `endContent` 替换加载动画；不传则控件消失。标题沿用 COUI 分组标题尺寸：12sp 中等字重，上下各 8dp，距起始边 32dp，行尾控件距末端 32dp。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=loadingPreferenceCategory" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.LoadingPreferenceCategory
import io.github.suqi8.coui.kmp.preference.LoadingPreferenceCategoryDefaults
```

## 基本用法

把分组标题放在它所标注的卡片上方，替代 `SmallTitle`：

```kotlin
var scanning by remember { mutableStateOf(true) }

LoadingPreferenceCategory(
    title = "可用设备",
    loading = scanning,
    loadingText = "正在搜索"
)
Card(modifier = Modifier.padding(horizontal = 16.dp)) {
    // 设备行
}
```

## 加载状态

| COUI LoadingType               | 参数                                        |
| ------------------------------ | ------------------------------------------- |
| LOADING                        | `loading = true`                            |
| INVISIBLE                      | `loading = false`，`endContent = null`      |
| AFTER_LOADING / BEFORE_LOADING | `loading = false` 并传入 `endContent`       |

不提供 `PAUSE`（动画停在当前帧）：旋转加载动画没有可暂停的帧。

## 属性

### LoadingPreferenceCategory 属性

| 属性名           | 类型                               | 说明                         | 默认值                                                | 必须 |
| ---------------- | ---------------------------------- | ---------------------------- | ----------------------------------------------------- | ---- |
| title            | String                             | 分组标题                     | -                                                     | 是   |
| modifier         | Modifier                           | 修饰符                       | Modifier                                              | 否   |
| loading          | Boolean                            | 是否显示加载动画             | true                                                  | 否   |
| loadingText      | String?                            | 加载动画前的文本             | null                                                  | 否   |
| titleType        | PreferenceCategoryTitleType        | 标题档位（Small / Medium / Large），见 [PreferenceCategory](./preferencecategory)；Medium / Large 时加载文本为 14sp、控件最小高 28dp | PreferenceCategoryTitleType.Small | 否 |
| marginStartType  | PreferenceCategoryMarginStartType  | 水平边距档位（Large 32dp / Small 16dp） | PreferenceCategoryMarginStartType.Large   | 否   |
| titleColor       | Color                              | 标题颜色                     | LoadingPreferenceCategoryDefaults.titleColor(titleType) | 否 |
| loadingTextColor | Color                              | 加载文本颜色                 | LoadingPreferenceCategoryDefaults.loadingTextColor()  | 否   |
| indicatorColor   | Color                              | 加载动画颜色                 | ProgressIndicatorDefaults.rotatingProgressIndicatorColor() | 否 |
| insideMargin     | PaddingValues                      | 行的水平边距                 | LoadingPreferenceCategoryDefaults.insideMargin(marginStartType) | 否 |
| endContent       | @Composable (RowScope.() -> Unit)? | 非加载状态下显示的尾部内容   | null                                                  | 否   |

### LoadingPreferenceCategoryDefaults 对象

| 名称               | 类型          | 默认值                                    | 说明                                   |
| ------------------ | ------------- | ----------------------------------------- | -------------------------------------- |
| InsideMargin       | PaddingValues | PaddingValues(horizontal = 32.dp)         | Large 边距档：标题起始边距 / 控件末端内缩 |
| InsideMarginSmall  | PaddingValues | PaddingValues(horizontal = 16.dp)         | Small 边距档：标题起始边距 / 控件末端内缩 |
| insideMargin(marginStartType) | PaddingValues | InsideMargin / InsideMarginSmall | 按边距档取值                           |
| LoadingTextSpacing | Dp            | 4.dp                                      | 加载文本与动画的间距                   |
| titleColor(titleType) | Color      | labelSecondary（Small）/ labelPrimary   | COUI couiColorSecondNeutral（Small）/ couiColorPrimaryNeutral（Medium、Large） |
| loadingTextColor() | Color         | COUITheme.colorScheme.labelPrimary           | COUI couiColorPrimaryNeutral           |

## 进阶用法

### 加载结束后的操作

```kotlin
LoadingPreferenceCategory(
    title = "可用设备",
    loading = scanning,
    endContent = {
        Text(
            text = "刷新",
            modifier = Modifier.clickable { scanning = true },
            style = COUITheme.textStyles.bodyXS.copy(fontWeight = FontWeight.Medium),
            color = COUITheme.colorScheme.primary
        )
    }
)
```
