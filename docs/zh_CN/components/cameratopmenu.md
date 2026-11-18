# CameraTopMenu

`CameraTopMenu` 是相机取景器顶部的设置栏，移植自相机的 `CameraMenuContainerTopLeft` / `TopMiddle` / `TopRight` 及其展开面板（`CameraMenuContainerExpandTopSingle`、`CameraMenuContainerExpandTopGroup`）。最多三个胶囊承载设置图标；点击带选项的图标时，它所在的胶囊会变形为横跨整个设置栏的选项条或分组选项面板。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraTopMenu" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraTopMenu
import io.github.suqi8.coui.kmp.basic.CameraTopMenuItem
import io.github.suqi8.coui.kmp.basic.CameraTopMenuOptionRow
```

## 基本用法

```kotlin
var expanded by remember { mutableStateOf<String?>(null) }
var flash by remember { mutableIntStateOf(1) }

CameraTopMenu(
    onItemClick = { key -> expanded = if (expanded == key) null else key },
    onOptionSelected = { _, _, index ->
        flash = index
        expanded = null
    },
    startItems = listOf(
        CameraTopMenuItem(
            key = "flash",
            icon = flashIcons[flash],
            contentDescription = "闪光灯",
            options = listOf(
                CameraTopMenuOptionRow(key = "flash", options = listOf("关闭", "自动", "开启", "常亮"), selectedIndex = flash),
            ),
        ),
    ),
    expandedItemKey = expanded,
)
```

设置栏填满给定宽度，高 32dp。`startItems` 距起始边 14dp，`endItems` 距结束边 14dp，`middleItems` 位于结束胶囊之前 10dp。每个条目是一个 32dp 单元格，内含 20dp 图标，图标按原样绘制：相机的菜单图标自带颜色，因此请为每个取值传入对应图标（例如闪光灯开启图标）。

菜单是受控的：`onItemClick` 回报每次被点击的条目，`expandedItemKey` 决定哪个面板展开。展开的面板绘制在设置栏之上并向下延伸，超出设置栏自身的边界，所以请在设置栏下方留出空间，且不要裁剪它。

### 选项条

只有一行选项的条目会展开为 32dp 的选项条。选项条在 380ms 内以 COUI 临界阻尼弹簧（response 0.8）从条目所在胶囊长出；条目图标在 300ms 内以 (0.3, 0, 0.1, 1) 滑到选项条起始处，选项在 200ms 后以 (0.33, 0, 0.67, 1) 在 300ms 内淡入。各选项平分宽度。设置了行 `title` 时，标题（48dp，`#88FFFFFF`）代替图标显示在选项条起始处：

```kotlin
CameraTopMenuOptionRow(key = "ratio", options = listOf("1:1", "4:3", "16:9", "全屏"), selectedIndex = 1, title = "画幅")
```

### 分组面板

带多行选项的条目会展开为分组面板：起始内边距 24dp、结束 12dp、上下 12dp，每行选项一行 38dp，标题列宽 48dp，每行的选项按最长一行的数量平分剩余宽度。面板以 COUI 弹簧（bounce 0.2，response 0.5）从胶囊弹出，同时圆角从 16dp 增大到 24dp、内容从 10% 放大、胶囊缩小到 10%，内容在 150ms 后于 300ms 内淡入。收起时弹簧回弹，整个面板在 150ms 内淡出。面板带有相机边缘光。

```kotlin
CameraTopMenuItem(
    key = "video",
    icon = videoIcon,
    contentDescription = "视频",
    options = listOf(
        CameraTopMenuOptionRow(key = "size", options = listOf("720P", "1080P", "4K"), selectedIndex = 1, title = "画质"),
        CameraTopMenuOptionRow(key = "fps", options = listOf("30", "60"), selectedIndex = 0, title = "帧率"),
    ),
)
```

选项为 12dp 中等字重文字，选中时变为半粗体，并在 300ms 内渐变为强调色 `#FB822A`。

### 材质模糊

在相机预览之上，胶囊、选项条和面板都是相机材质模糊（80dp 半径，65% 灰以 40% 辉光叠加在 15% 灰 60% 混合之上）。传入录制设置栏下方内容的 `MaterialBlurProvider` 即可；未传入时，胶囊和选项条保持 50% 黑色填充，分组面板保持相机不透明的 `#141414` 面板及 0.67dp 10% 白色描边。

```kotlin
CameraTopMenu(
    onItemClick = onItemClick,
    onOptionSelected = onOptionSelected,
    startItems = startItems,
    materialBlur = provider, // 录制取景画面，而不是设置栏本身
)
```

## 属性

### CameraTopMenu 属性

| 属性名           | 类型                                | 说明                                     | 默认值                                    | 是否必须 |
| ---------------- | ----------------------------------- | ---------------------------------------- | ----------------------------------------- | -------- |
| onItemClick      | (String) -> Unit                    | 以被点击条目的 key 回调                  | -                                         | 是       |
| onOptionSelected | (String, String, Int) -> Unit       | 以条目 key、行 key 与选项索引回调        | -                                         | 是       |
| modifier         | Modifier                            | 应用于设置栏的修饰符                     | Modifier                                  | 否       |
| startItems       | List&lt;CameraTopMenuItem&gt;       | 起始胶囊的条目                           | emptyList()                               | 否       |
| middleItems      | List&lt;CameraTopMenuItem&gt;       | 结束胶囊之前的胶囊的条目                 | emptyList()                               | 否       |
| endItems         | List&lt;CameraTopMenuItem&gt;       | 结束胶囊的条目                           | emptyList()                               | 否       |
| expandedItemKey  | String?                             | 面板展开的条目 key                       | null                                      | 否       |
| materialBlur     | MaterialBlurProvider?               | 在胶囊与面板后绘制材质模糊               | null                                      | 否       |
| colors           | CameraTopMenuColors                 | 菜单颜色                                 | CameraTopMenuDefaults.cameraTopMenuColors() | 否     |

### CameraTopMenuItem

| 属性名             | 类型                                 | 说明                                   | 默认值      |
| ------------------ | ------------------------------------ | -------------------------------------- | ----------- |
| key                | String                               | 条目的唯一 key                         | -           |
| icon               | ImageVector                          | 图标，不着色绘制                       | -           |
| contentDescription | String?                              | 无障碍描述                             | -           |
| options            | List&lt;CameraTopMenuOptionRow&gt;   | 选项行；一行展开为选项条，多行展开为面板 | emptyList() |

### CameraTopMenuOptionRow

| 属性名        | 类型               | 说明                       | 默认值 |
| ------------- | ------------------ | -------------------------- | ------ |
| key           | String             | 行在条目内的唯一 key       | -      |
| options       | List&lt;String&gt; | 选项文字，从起始到结束     | -      |
| selectedIndex | Int                | 选中选项的索引             | -      |
| title         | String?            | 行起始处的可选标题         | null   |

## 默认值对象

### CameraTopMenuDefaults

| 属性名                  | 类型 | 说明                                   | 值      |
| ----------------------- | ---- | -------------------------------------- | ------- |
| ItemSize                | Dp   | 设置栏高度与条目单元格尺寸             | 32.dp   |
| IconSize                | Dp   | 条目图标尺寸                           | 20.dp   |
| HorizontalMargin        | Dp   | 设置栏两侧缩进                         | 14.dp   |
| MiddleSpacing           | Dp   | 中间胶囊与结束胶囊的间距               | 10.dp   |
| CornerRadius            | Dp   | 胶囊与选项条圆角（限制为胶囊形）       | 32.dp   |
| BlurRadius              | Dp   | 材质模糊半径                           | 80.dp   |
| PanelCornerRadius       | Dp   | 展开后分组面板的圆角                   | 24.dp   |
| PanelPaddingStart       | Dp   | 分组面板起始内边距                     | 24.dp   |
| PanelPaddingEnd         | Dp   | 面板结束内边距                         | 12.dp   |
| PanelPaddingVertical    | Dp   | 分组面板上下内边距                     | 12.dp   |
| PanelRowHeight          | Dp   | 分组面板行高                           | 38.dp   |
| TitleWidth              | Dp   | 行标题宽度                             | 48.dp   |
| PanelBorderWidth        | Dp   | 非模糊分组面板的描边宽度               | 0.67.dp |
| TextSize                | Dp   | 标题与选项字号（dp，不随字体缩放）     | 12.dp   |
| OptionPaddingHorizontal | Dp   | 选项文字两侧内边距                     | 4.dp    |

| 函数                  | 返回类型            | 说明                       |
| --------------------- | ------------------- | -------------------------- |
| cameraTopMenuColors() | CameraTopMenuColors | 固定的相机深底浅色配色     |

## 颜色类

### CameraTopMenuColors

| 属性名              | 类型  | 说明                             | 默认值             |
| ------------------- | ----- | -------------------------------- | ------------------ |
| containerColor      | Color | 无材质模糊时的胶囊与选项条       | #7F000000          |
| panelContainerColor | Color | 无材质模糊时的分组面板           | #141414            |
| panelBorderColor    | Color | 无材质模糊时分组面板的描边       | #1AFFFFFF          |
| blurMixColor        | Color | 材质模糊混合色                   | 15% 灰，60% 透明度 |
| blurBlendColor      | Color | 材质模糊辉光叠加色               | 65% 灰，40% 透明度 |
| titleColor          | Color | 行标题                           | #88FFFFFF          |
| optionColor         | Color | 选项                             | White              |
| selectedOptionColor | Color | 选中选项                         | #FB822A            |
