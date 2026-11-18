# CameraModeBar

`CameraModeBar` 是相机的模式条，移植自相机的 headline（`ModePickerView`，尺寸由 `DimenConstants` 装配）：一行模式名（夜景、人像、拍照、录像……）在居中固定的胶囊下滚动，胶囊标出当前模式。`CameraModeTitle` 是从“更多”打开某个模式时，相机在模式条位置显示的标题：胶囊里是模式名和一个关闭叉（`ModeTitleView`，`mode_title.xml`）。

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraModeBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraModeBar
import io.github.suqi8.coui.kmp.basic.CameraModeBarDefaults
import io.github.suqi8.coui.kmp.basic.CameraModeTitle
import io.github.suqi8.coui.kmp.basic.CameraModeTitleDefaults
```

## 基本用法

```kotlin
val modes = listOf("夜景", "人像", "拍照", "录像", "专业", "更多")
var index by remember { mutableIntStateOf(2) } // 拍照

CameraModeBar(
    modes = modes,
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
)
```

模式条占满给定宽度，高 40dp。模式名为 13sp 中粗体（随字体缩放，最多 1.35 倍），间距 32dp。选中项位于 34dp 高的胶囊中，左右内边距各 16dp，胶囊最宽 198dp，更长的名字以省略号截断。模式条两端最外侧 72dp 渐隐。

### 交互

- 拖动时模式条跟随手指位移的 67%；越过首个和最后一个模式后进入橡皮筋拉伸（超出量 `o` 显示为 `(1 - 1 / (o / w + 1)) * w`，`w` 为模式条宽度的四分之一）。
- 中点沿移动方向越过下一个名字边缘 28px 后，胶囊下的模式随之切换，并触发一次轻触感振动。
- 松手后模式条以弹簧动画回到该模式（`COUISpringForce`，bounce 0.05，response 0.3s）。松手速度超过 5500px/s 时，会再多走拖过模式数的三倍。
- 点击某个名字（其宽度左右各加 8dp）会以弹簧动画切到该模式；模式条滑动期间的点击会被忽略。
- 胶囊在两个模式之间移动时，宽度从一个名字过渡到另一个名字。名字距中点 40dp 内开始变为强调色，15dp 内完全变色，曲线为 (0, 0, 0.1, 1)。
- 手势停在新模式上时调用一次 `onSelectedIndexChange`。`selectedIndex` 从外部改变时，模式条以弹簧动画移到对应模式。
- 向上滑动（超过 30px、横向不足 150px）交给父布局处理，与相机把它交给底部菜单一致。

### 材质模糊

在相机预览上方，胶囊是材质模糊（半径 80dp，30% 灰 60% 混合之上叠加 15% 灰的辉光层），并带相机的边缘光。传入记录模式条下方内容的 `MaterialBlurProvider` 即可；不传时胶囊保持相机的普通填充，即 12% 白。

```kotlin
CameraModeBar(
    modes = modes,
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
    materialBlur = provider, // 记录取景画面，不包含模式条本身
)
```

### 模式标题

从“更多”打开某个模式时，相机用该模式的标题替换模式条。点击标题任意位置即退出该模式。

```kotlin
if (moreModeOpen) {
    CameraModeTitle(
        onClick = { moreModeOpen = false },
        title = "慢动作",
    )
} else {
    CameraModeBar(
        modes = modes,
        selectedIndex = index,
        onSelectedIndexChange = { index = it },
    )
}
```

标题高 36dp，内含 34dp 高的胶囊：强调色的模式名（14sp 中粗体）距起始端 16dp，其后 4dp 是 16dp 的关闭叉，关闭叉距末端 12dp。宽于 198dp 的名字以跑马灯滚动，两侧渐隐 16dp。胶囊为 50% 黑填充，传入 `materialBlur` 时为材质模糊。按下时标题在 200ms 内降到 50% 透明度，松开后 340ms 恢复；名字变化时胶囊在 250ms 内以 (0.3, 0, 0.1, 1) 调整宽度。

## 属性

### CameraModeBar 属性

| 属性名                | 类型                  | 说明                                       | 默认值                                      | 必须 |
| --------------------- | --------------------- | ------------------------------------------ | ------------------------------------------- | ---- |
| modes                 | List&lt;String&gt;    | 模式名，从起始到末尾（不能为空）           | -                                           | 是   |
| selectedIndex         | Int                   | 选中模式的索引                             | -                                           | 是   |
| onSelectedIndexChange | (Int) -> Unit         | 拖动、甩动或点击停在新模式上时调用         | -                                           | 是   |
| modifier              | Modifier              | 应用于模式条的修饰符                       | Modifier                                    | 否   |
| enabled               | Boolean               | 是否响应拖动和点击                         | true                                        | 否   |
| materialBlur          | MaterialBlurProvider? | 在胶囊后绘制材质模糊；为 null 时保持填充   | null                                        | 否   |
| colors                | CameraModeBarColors   | 模式条的颜色                               | CameraModeBarDefaults.cameraModeBarColors() | 否   |

### CameraModeTitle 属性

| 属性名       | 类型                  | 说明                                     | 默认值                                          | 必须 |
| ------------ | --------------------- | ---------------------------------------- | ----------------------------------------------- | ---- |
| onClick      | () -> Unit            | 点击标题时调用                           | -                                               | 是   |
| title        | String                | 当前打开模式的名称                       | -                                               | 是   |
| modifier     | Modifier              | 应用于标题的修饰符                       | Modifier                                        | 否   |
| enabled      | Boolean               | 是否响应点击                             | true                                            | 否   |
| materialBlur | MaterialBlurProvider? | 在胶囊后绘制材质模糊；为 null 时保持填充 | null                                            | 否   |
| colors       | CameraModeTitleColors | 标题的颜色                               | CameraModeTitleDefaults.cameraModeTitleColors() | 否   |

## 默认值对象

### CameraModeBarDefaults

| 属性名                 | 类型  | 说明                               | 值      |
| ---------------------- | ----- | ---------------------------------- | ------- |
| Height                 | Dp    | 模式条高度                         | 40.dp   |
| CapsuleHeight          | Dp    | 胶囊高度                           | 34.dp   |
| CapsulePadding         | Dp    | 名字两侧的胶囊内边距               | 16.dp   |
| CapsuleMaxWidth        | Dp    | 胶囊最大宽度，更长的名字被截断     | 198.dp  |
| ItemGap                | Dp    | 两个名字之间的间距                 | 32.dp   |
| TextSize               | Dp    | 名字字号（按 sp 缩放至 `MaxFontScale`） | 13.dp |
| MaxFontScale           | Float | 名字跟随的最大字体缩放             | 1.35f   |
| TextStrokeWidth        | Dp    | 名字外围深色描边宽度               | 0.33.dp |
| EdgeFadeWidth          | Dp    | 模式条两端的渐隐宽度               | 72.dp   |
| HighlightStartDistance | Dp    | 距中点多远开始变为强调色           | 40.dp   |
| HighlightEndDistance   | Dp    | 距中点多近完全变为强调色           | 15.dp   |
| TouchExtension         | Dp    | 名字两侧额外的触摸宽度             | 8.dp    |
| BlurRadius             | Dp    | 胶囊的材质模糊半径                 | 80.dp   |

| 函数                  | 返回类型            | 说明                     |
| --------------------- | ------------------- | ------------------------ |
| cameraModeBarColors() | CameraModeBarColors | 固定的深底浅色相机配色   |

### CameraModeTitleDefaults

| 属性名             | 类型  | 说明                         | 值     |
| ------------------ | ----- | ---------------------------- | ------ |
| Height             | Dp    | 标题高度                     | 36.dp  |
| CapsuleHeight      | Dp    | 胶囊高度                     | 34.dp  |
| TextSize           | Dp    | 名字字号（按 sp 缩放至 1.35 倍） | 14.dp |
| TextStartPadding   | Dp    | 胶囊起始端到名字的距离       | 16.dp  |
| TextMaxWidth       | Dp    | 名字开始滚动前的最大宽度     | 198.dp |
| FadingEdgeLength   | Dp    | 滚动名字两侧的渐隐长度       | 16.dp  |
| MarqueeVelocity    | Dp    | 跑马灯每秒滚动距离           | 30.dp  |
| MarqueeDelayMillis | Int   | 名字开始滚动前的延迟         | 1200   |
| IconSpacing        | Dp    | 名字与关闭叉之间的间距       | 4.dp   |
| IconEndPadding     | Dp    | 关闭叉到胶囊末端的距离       | 12.dp  |
| IconSize           | Dp    | 关闭叉尺寸                   | 16.dp  |
| PressedAlpha       | Float | 按下时标题的透明度           | 0.5f   |

| 函数                    | 返回类型              | 说明           |
| ----------------------- | --------------------- | -------------- |
| cameraModeTitleColors() | CameraModeTitleColors | 固定的相机配色 |

## 颜色类

### CameraModeBarColors

| 属性名            | 类型  | 说明                     | 默认值          |
| ----------------- | ----- | ------------------------ | --------------- |
| textColor         | Color | 模式名                   | White           |
| selectedTextColor | Color | 胶囊下的名字             | #FB822A         |
| textStrokeColor   | Color | 每个名字外围的细描边     | #66000000       |
| capsuleColor      | Color | 无材质模糊时的胶囊填充   | #1EFFFFFF       |
| blurMixColor      | Color | 材质模糊混合色           | 30% 灰，60% 透明度 |
| blurBlendColor    | Color | 材质模糊辉光叠加色       | 15% 灰，15% 透明度 |

### CameraModeTitleColors

| 属性名         | 类型  | 说明                   | 默认值          |
| -------------- | ----- | ---------------------- | --------------- |
| textColor      | Color | 模式名                 | #FB822A         |
| iconColor      | Color | 关闭叉                 | #FB822A         |
| containerColor | Color | 无材质模糊时的胶囊填充 | #80000000       |
| blurMixColor   | Color | 材质模糊混合色         | 30% 灰，60% 透明度 |
| blurBlendColor | Color | 材质模糊辉光叠加色     | 15% 灰，15% 透明度 |
