# TagView

`TagView` 是「新」「BETA」这类小型文字标签，对应 ColorOS 的 `COUITagView` 及其 `COUITagBackgroundView`：单行文字放在带填充色并裁剪的背景上，可带前置与后置图标。

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=tagView" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.TagView
import io.github.suqi8.coui.kmp.basic.TagViewDefaults
```

## 基本用法

```kotlin
Row(verticalAlignment = Alignment.CenterVertically) {
    Text(text = "深色模式")
    Spacer(Modifier.width(8.dp))
    TagView(text = "新")
}
```

## 属性

### TagView 属性

| 属性名          | 类型                        | 说明                                              | 默认值                             | 是否必须 |
| ------------ | ------------------------- | ----------------------------------------------- | ------------------------------- | ---- |
| text         | String                    | 标签文字，单行，末尾省略                                    | -                               | 是    |
| modifier     | Modifier                  | 应用于标签的修饰符                                       | Modifier                        | 否    |
| shape        | Shape                     | 背景与裁剪形状；可用 `RoundedCornerShape` 分别设置四角           | TagViewDefaults.Shape           | 否    |
| colors       | TagViewColors             | 标签颜色                                            | TagViewDefaults.tagViewColors() | 否    |
| strokeWidth  | Dp                        | 描边宽度                                            | TagViewDefaults.StrokeWidth     | 否    |
| textSize     | Dp                        | 文字大小，以 dp 计，不随字体缩放                              | TagViewDefaults.TextSize        | 否    |
| insideMargin | PaddingValues             | 标签内边距                                           | TagViewDefaults.InsideMargin    | 否    |
| leadingIcon  | (@Composable () -> Unit)? | 文字前的图标，放在 5dp x 10dp 区域内（COUI `couiTagViewLeftDrawable`） | null                            | 否    |
| trailingIcon | (@Composable () -> Unit)? | 文字后的图标，按自身尺寸布局（COUI `couiDrawableTagViewImage`）    | null                            | 否    |

### TagViewDefaults 对象

| 常量名               | 类型            | 说明                                       | 默认值                              |
| ----------------- | ------------- | ---------------------------------------- | -------------------------------- |
| CornerRadius      | Dp            | 默认圆角（COUI 的 `couiTagCornerRadius` 默认为 0） | 4.dp                             |
| Shape             | Shape         | 默认形状                                     | RoundedCornerShape(CornerRadius) |
| StrokeWidth       | Dp            | 默认描边宽度（`couiTagStrokeWidth`）             | 0.dp                             |
| TextSize          | Dp            | 默认文字大小（`coui_default_tag_textsize`）      | 10.dp                            |
| InsideMargin      | PaddingValues | 默认内边距（`coui_tag_view_layout`）            | PaddingValues(horizontal = 3.dp) |
| LeadingIconWidth  | Dp            | 前置图标区域宽度                                 | 5.dp                             |
| LeadingIconHeight | Dp            | 前置图标区域高度                                 | 10.dp                            |

| 函数名             | 说明                                    |
| --------------- | ------------------------------------- |
| tagViewColors() | 创建 `TagViewColors`：主题主色填充、白色文字、透明描边 |

### TagViewColors 类

| 属性名            | 类型    | 说明                          |
| -------------- | ----- | --------------------------- |
| containerColor | Color | 背景填充（`couiTagBackgroundColor`） |
| contentColor   | Color | 文字颜色（`couiTagViewTextColor`）   |
| strokeColor    | Color | 描边颜色，描边宽度大于 0 时绘制           |

## 行为

- 文字为 sans-serif-medium（`couiTextTag`），使用 ColorOS 15+ 的 1.2 倍行距，纯文字标签高约 14dp。
- 背景按同一形状裁剪内容（`COUITagBackgroundView.dispatchDraw`）。
- COUI 的默认填充读取 `couiColorBackgroundWithTag`，而系统主题并未设置该属性，因此本库默认使用主题主色与 4dp 圆角；需要匹配宿主时传入自己的 `colors` 与 `shape`。

## 进阶用法

### 描边标签

```kotlin
TagView(
    text = "描边",
    colors = TagViewDefaults.tagViewColors(
        containerColor = Color.Transparent,
        contentColor = COUITheme.colorScheme.primary,
        strokeColor = COUITheme.colorScheme.primary,
    ),
    strokeWidth = 1.dp,
)
```

### 分别设置四角

```kotlin
TagView(
    text = "Corner",
    shape = RoundedCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
)
```
