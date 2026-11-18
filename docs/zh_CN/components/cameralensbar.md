# CameraLensBar

`CameraLensBar` 是相机的镜头切换条（0.6× · 1× · 3× …），移植自相机 `SwitchCameraBar` 的分离样式。每颗镜头是一个深色圆钮，里面写着它的倍率；选中的圆钮更大，倍率以强调色显示，后面跟一个小 "×"。

<div style="position: relative; height: 220px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraLensBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraLensBar
import io.github.suqi8.coui.kmp.basic.CameraLensBarDefaults
```

## 基本用法

```kotlin
val lenses = listOf(0.6f, 1f, 3f).map(CameraLensBarDefaults::formatZoomRatio) // "0.6", "1", "3"
var index by remember { mutableIntStateOf(1) }

CameraLensBar(
    labels = lenses,
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
)
```

圆钮半径 14dp，间距 17dp；选中的圆钮放大到 16dp。切换条高 36dp，宽度正好容纳所有圆钮（`CameraLensBarDefaults.width(count)`），请用父布局的对齐方式摆放。倍率文字 10dp；选中的倍率加粗，其后 1.5dp 处是 6dp x 14dp 的 "×" 图形。

`CameraLensBarDefaults.formatZoomRatio` 按相机的方式格式化倍率：最多一位小数、向下取整、不保留末尾的 ".0"（1.0 得到 "1"，0.6 得到 "0.6"）。

### 交互

- 轻点某颗镜头会立即选中它并播放轻点振动；切换动画进行中时忽略轻点。
- 切换时，新圆钮放大、旧圆钮缩小，历时 400ms，曲线 (0.3, 0, 0.1, 1)；同时倍率文字向一侧滑开，为 "×" 让出位置。
- 颜色在延迟 100ms 后以 400ms、曲线 (0.33, 0, 0.67, 1) 交叉过渡：新倍率由白色变为强调色并淡入 "×"，旧倍率反之。
- 按住选中的圆钮会在 500ms 内缩小到 85%，并由 50% 黑加深到 65% 黑（临界阻尼弹簧）；松手后以 280ms 带轻微回弹恢复。
- `selectedIndex` 从外部改变时，切换条播放同样的切换动画。

### 浅色背景

在明亮画面上，相机改用半透明白色圆钮：

```kotlin
CameraLensBar(
    labels = lenses,
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
    colors = CameraLensBarDefaults.lightBackgroundColors(),
)
```

### 大屏

在大屏设备上，相机使用更大的圆钮：

```kotlin
CameraLensBar(
    labels = lenses,
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
    radius = CameraLensBarDefaults.EnlargedRadius,
    selectedRadius = CameraLensBarDefaults.EnlargedSelectedRadius,
)
```

## 属性

### CameraLensBar 属性

| 属性名                | 类型                | 说明                                     | 默认值                                      | 必填 |
| --------------------- | ------------------- | ---------------------------------------- | ------------------------------------------- | ---- |
| labels                | List&lt;String&gt;  | 各镜头的倍率，由广到长（不能为空）       | -                                           | 是   |
| selectedIndex         | Int                 | 选中镜头的索引                           | -                                           | 是   |
| onSelectedIndexChange | (Int) -> Unit       | 以被轻点镜头的索引回调                   | -                                           | 是   |
| modifier              | Modifier            | 应用于切换条的修饰符                     | Modifier                                    | 否   |
| enabled               | Boolean             | 镜头是否响应轻点                         | true                                        | 否   |
| radius                | Dp                  | 未选中圆钮的半径                         | CameraLensBarDefaults.Radius                | 否   |
| selectedRadius        | Dp                  | 选中圆钮的半径                           | CameraLensBarDefaults.SelectedRadius        | 否   |
| itemSpacing           | Dp                  | 两个圆钮之间的间距                       | CameraLensBarDefaults.ItemSpacing           | 否   |
| textSize              | Dp                  | 倍率文字大小（dp，不随字体缩放）         | CameraLensBarDefaults.TextSize              | 否   |
| colors                | CameraLensBarColors | 切换条的颜色                             | CameraLensBarDefaults.cameraLensBarColors() | 否   |

## 默认值对象

### CameraLensBarDefaults

| 属性名                 | 类型               | 说明                               | 值                  |
| ---------------------- | ------------------ | ---------------------------------- | ------------------- |
| Labels                 | List&lt;String&gt; | 相机的默认镜头                     | "0.6", "1", "2"     |
| Height                 | Dp                 | 切换条高度                         | 36.dp               |
| Radius                 | Dp                 | 未选中圆钮的半径                   | 14.dp               |
| SelectedRadius         | Dp                 | 选中圆钮的半径                     | 16.dp               |
| ItemSpacing            | Dp                 | 两个圆钮之间的间距                 | 17.dp               |
| EnlargedRadius         | Dp                 | 大屏上未选中圆钮的半径             | 15.dp               |
| EnlargedSelectedRadius | Dp                 | 大屏上选中圆钮的半径               | 18.dp               |
| TextSize               | Dp                 | 倍率文字大小                       | 10.dp               |
| SuffixWidth            | Dp                 | "×" 图形宽度                       | 6.dp                |
| SuffixHeight           | Dp                 | "×" 图形高度                       | 14.dp               |
| SuffixSpacing          | Dp                 | 倍率与 "×" 之间的间距              | 1.5.dp              |
| SuffixOffsetY          | Dp                 | "×" 相对文字中心下移的距离         | 0.5.dp              |

| 函数                    | 返回类型            | 说明                                   |
| ----------------------- | ------------------- | -------------------------------------- |
| width(count, …)         | Dp                  | `count` 颗镜头时切换条的宽度            |
| formatZoomRatio(ratio)  | String              | 按相机方式格式化倍率（"1"、"0.6"）     |
| cameraLensBarColors()   | CameraLensBarColors | 深色取景画面上的固定配色               |
| lightBackgroundColors() | CameraLensBarColors | 浅色取景画面上的配色                   |

## 颜色类

### CameraLensBarColors

| 属性名                        | 类型  | 说明                     | 默认值    |
| ----------------------------- | ----- | ------------------------ | --------- |
| containerColor                | Color | 未选中的圆钮             | #7F000000 |
| selectedContainerColor        | Color | 选中的圆钮               | #80000000 |
| pressedSelectedContainerColor | Color | 按下时的选中圆钮         | #A6000000 |
| contentColor                  | Color | 未选中的倍率             | White     |
| selectedContentColor          | Color | 选中的倍率               | #FB822A   |
| suffixColor                   | Color | 选中倍率后的 "×"         | #FB822A   |
