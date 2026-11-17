# IconButton

`IconButton` 是 COUI 中的图标按钮组件，用于提供辅助操作的交互点。它们通常用于需要紧凑按钮的场景，如工具栏或图片列表中。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=iconButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.ToolbarTextButton
```

## 基本用法

IconButton 组件可以用于触发操作或事件：

```kotlin
IconButton(
    onClick = { /* Handle click event */ }
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites"
    )
}
```

## 组件状态

### 禁用状态

```kotlin
IconButton(
    onClick = { /* Handle click event */ },
    enabled = false
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites"
    )
}
```

### 按下状态

IconButton 支持通过 `holdDownState` 参数控制按下状态，通常用于显示弹出对话框时的视觉反馈：

```kotlin
var showDialog by remember { mutableStateOf(false) }

Scaffold {
    IconButton(
        onClick = { showDialog = true },
        holdDownState = showDialog
    ) {
        Icon(
            imageVector = COUIIcons.Favorites,
            contentDescription = "Favorites"
        )
    }
    // Define dialog elsewhere
    OverlayDialog(
        title = "Dialog",
        show = showDialog,
        onDismissRequest = { showDialog = false }
    ) {
        // Dialog content
    }
}
```

### 红点

`redDot` 显示 COUI 工具栏菜单红点（`COUIToolbar.setRedDot`）：`0` 为纯红点，正数为数字角标。红点挂在居中 24dp 图标区域的右上角外侧，不改变按钮尺寸：

```kotlin
IconButton(
    onClick = { /* Handle click event */ },
    redDot = 3
) {
    Icon(
        imageVector = COUIIcons.More,
        contentDescription = "More"
    )
}
```

## ToolbarTextButton

`ToolbarTextButton` 是用于顶栏 `actions` 的 COUI 17 文字菜单项：同一套顶栏按钮材质的胶囊，宽 60–104dp、高 40dp，单行 14dp 中等字重主题色文字，超出最大宽度时省略。

```kotlin
SmallTopAppBar(
    title = "标题",
    actions = {
        ToolbarTextButton(
            text = "完成",
            onClick = { /* Handle click event */ }
        )
    }
)
```

| 属性名          | 类型                    | 说明                 | 默认值                                         | 是否必须 |
| --------------- | ----------------------- | -------------------- | ---------------------------------------------- | -------- |
| text            | String                  | 按钮文字             | -                                              | 是       |
| onClick         | () -> Unit              | 点击按钮时触发的回调 | -                                              | 是       |
| modifier        | Modifier                | 应用于按钮的修饰符   | Modifier                                       | 否       |
| enabled         | Boolean                 | 按钮是否可点击       | true                                           | 否       |
| holdDownState   | Boolean                 | 是否处于按下状态     | false                                          | 否       |
| material        | Boolean                 | 是否绘制 COUI 17 顶栏按钮材质 | `LocalCOUIMaterialEffectEnabled.current` | 否       |
| backgroundColor | Color                   | 按钮的背景颜色       | Color.Unspecified                              | 否       |
| minWidth        | Dp                      | 最小宽度             | IconButtonDefaults.TextButtonMinWidth          | 否       |
| maxWidth        | Dp                      | 最大宽度             | IconButtonDefaults.TextButtonMaxWidth          | 否       |
| minHeight       | Dp                      | 最小高度             | IconButtonDefaults.MinHeight                   | 否       |
| insideMargin    | PaddingValues           | 文字四周的内边距     | IconButtonDefaults.TextButtonInsideMargin      | 否       |
| colors          | ToolbarTextButtonColors | 文字颜色             | IconButtonDefaults.toolbarTextButtonColors()   | 否       |

## 触摸动效

`material = true` 时按钮运行 `ToolbarMaterialEffectDelegate` 挂载的 `COUIViewTouchAnimator`（形变、橡皮筋、按压缩放全开），与 [Button](./button#按压反馈) 的动效相同：

- **按压缩放**：40dp 圆形按钮缩到约 0.889（jelly 弹簧：按下 k 631.65 / ζ 1，松开 k 157.91 / ζ 0.4），手指按住期间保持，即使移出按钮。
- **跟手拖动**：越过 5px 死区后按橡皮筋曲线 `(1 - 1 / (0.05·|d| / 28dp + 1)) · 28dp` 跟随手指，沿拖动方向最多拉伸 1.2 倍、垂直方向收窄（面积守恒），弹簧 response 0.15 / bounce 0.15。
- **松手**：所有形变以 response 0.42 / bounce 0.65 弹回，并带入手指速度。
- **蒙版**：按压蒙版（`couiColorPress`）在手指移出按钮后立即淡出；悬停蒙版（`couiColorHover`）在桌面端和 Web 上跟随指针。
- 禁用的按钮不响应触摸。

## 属性

### IconButton 属性

| 属性名          | 类型                   | 说明                  | 默认值                          | 是否必须 |
| --------------- | ---------------------- | --------------------- | ------------------------------- | -------- |
| onClick         | () -> Unit             | 点击按钮时触发的回调  | -                               | 是       |
| modifier        | Modifier               | 应用于按钮的修饰符    | Modifier                        | 否       |
| enabled         | Boolean                | 按钮是否可点击        | true                            | 否       |
| holdDownState   | Boolean                | 是否处于按下状态      | false                           | 否       |
| material        | Boolean                | 是否绘制 COUI 17 顶栏按钮材质（背景滤镜、阴影、边缘光、跟手聚光、按压与悬停蒙版）及其触摸动效（按压缩放、跟手橡皮筋、拖动形变） | `LocalCOUIMaterialEffectEnabled.current` | 否       |
| backgroundColor | Color                  | 按钮的背景颜色        | Color.Unspecified               | 否       |
| cornerRadius    | Dp                     | 按钮圆角半径          | IconButtonDefaults.CornerRadius | 否       |
| minHeight       | Dp                     | 按钮最小高度          | IconButtonDefaults.MinHeight    | 否       |
| minWidth        | Dp                     | 按钮最小宽度          | IconButtonDefaults.MinWidth     | 否       |
| redDot          | Int?                   | 工具栏红点：null 无，0 纯红点，正数为数字 | null              | 否       |
| content         | @Composable () -> Unit | 按钮内容，通常是 Icon | -                               | 是       |

### IconButtonDefaults 对象

IconButtonDefaults 对象提供了图标按钮组件的默认值。

#### 常量

| 常量名       | 类型 | 说明           | 默认值 |
| ------------ | ---- | -------------- | ------ |
| MinWidth     | Dp   | 按钮的最小宽度 | 40.dp  |
| MinHeight    | Dp   | 按钮的最小高度 | 40.dp  |
| CornerRadius | Dp   | 按钮的圆角半径 | 20.dp  |
| TextButtonMinWidth | Dp | ToolbarTextButton 最小宽度 | 60.dp |
| TextButtonMaxWidth | Dp | ToolbarTextButton 最大宽度 | 104.dp |
| TextButtonInsideMargin | PaddingValues | ToolbarTextButton 文字内边距 | PaddingValues(horizontal = 12.dp) |
| TextButtonTextSize | Dp | ToolbarTextButton 字号（不随字体缩放） | 14.dp |
| RedDotAnchorSize | Dp | 红点所依附的图标区域 | 24.dp |
| RedDotHorizontalOffset / RedDotVerticalOffset | Dp | 纯红点超出图标区域右 / 上边缘的距离 | 1.dp / 1.dp |
| RedDotWithSmallNumberHorizontalOffset | Dp | 数字角标右侧超出量（小于 10） | 6.dp |
| RedDotWithNumberHorizontalOffset | Dp | 数字角标右侧超出量（小于 100） | 8.dp |
| RedDotWithBigNumberHorizontalOffset | Dp | 数字角标右侧超出量（100 及以上） | 9.dp |
| RedDotWithNumberVerticalOffset | Dp | 数字角标上方超出量 | 5.dp |

#### 函数

| 函数名 | 说明 |
| ------ | ---- |
| toolbarTextButtonColors(contentColor, disabledContentColor) | ToolbarTextButton 文字颜色；默认 `colorScheme.primary` / `colorScheme.disabledNeutral` |

## 进阶用法

### 自定义背景颜色

```kotlin
IconButton(
    onClick = { /* Handle click event */ },
    backgroundColor = Color.LightGray.copy(alpha = 0.3f)
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites"
    )
}
```

### 自定义大小和圆角

```kotlin
IconButton(
    onClick = { /* Handle click event */ },
    minWidth = 48.dp,
    minHeight = 48.dp,
    cornerRadius = 12.dp
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites"
    )
}
```

### 结合其他组件使用

```kotlin
Surface {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* Handle click event */ }
        ) {
            Icon(
                imageVector = COUIIcons.Add,
                tint = COUITheme.colorScheme.labelPrimary,
                contentDescription = "Add"
            )
        }
        Text("Add New Item")
    }
}
```

### 动态图标按钮

```kotlin
var isLiked by remember { mutableStateOf(false) }

IconButton(
    onClick = { isLiked = !isLiked }
) {
    Icon(
        imageVector = if (isLiked) COUIIcons.FavoritesFill else COUIIcons.Favorites,
        contentDescription = if (isLiked) "Like" else "Unlike",
        tint = if (isLiked) Color.Red else COUITheme.colorScheme.labelPrimary
    )
}
```

