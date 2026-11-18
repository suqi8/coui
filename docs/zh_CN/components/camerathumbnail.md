# CameraThumbnail

`CameraThumbnail` 是相机的相册缩略图按钮，移植自相机的 `ThumbImageView`。一个小圆角方块居中裁切显示最新的照片，并带一圈细描边；新照片会从中心放大覆盖上一张。没有照片时显示相机的深色占位图。

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraThumbnail" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraThumbnail
import io.github.suqi8.coui.kmp.basic.CameraThumbnailDefaults
import io.github.suqi8.coui.kmp.basic.CameraThumbnailTransition
```

## 基本用法

```kotlin
val latest: Painter? = remember(lastPhoto) { lastPhoto?.let { BitmapPainter(it) } }

CameraThumbnail(
    image = latest,
    onClick = { openGallery() },
)
```

缩略图是 40dp 的方块，圆角 6dp，边缘内侧画一圈 0.67dp 的 10% 白色描边。照片按方块居中裁切。`image` 为 null 时显示相机的占位图，即 `#262626` 底色。

缩略图按对象身份区分照片：每张新照片传入一个新的 `Painter`，并在重组之间保持不变（用 `remember` 包住）。`ImageBitmap` 重载会替你完成这一步：

```kotlin
CameraThumbnail(
    bitmap = lastBitmap, // ImageBitmap?
    onClick = { openGallery() },
)
```

### 新照片动画

`transition` 决定新照片如何出现：

| 过渡方式 | 表现                                                                                         |
| -------- | -------------------------------------------------------------------------------------------- |
| ScaleIn  | 默认。新照片在旧照片上方从中心由 6% 放大到完整尺寸，350ms，曲线 (0.3, 0, 0.1, 1)                |
| ScaleOut | 新照片立即显示，旧照片在其上方从 94% 缩小到中心消失，例如删除之后                               |
| None     | 新照片直接替换旧照片                                                                           |

连拍时，如果新照片在正在放大的照片达到 80% 之前到达，会原地替换它而不是重新开始动画，缩略图因此能跟上拍摄节奏。

```kotlin
CameraThumbnail(
    image = latest,
    onClick = { openGallery() },
    transition = CameraThumbnailTransition.ScaleOut,
)
```

### 交互

- 按下时缩略图在 200ms 内变为 50% 不透明度；松开后 340ms 恢复，曲线均为 (0.33, 0, 0.67, 1)。
- 禁用的缩略图保持 40% 不透明度且不响应点击。
- 切换 `visible` 时，缩略图在 200ms 内于 80% 与完整尺寸之间淡入淡出并缩放，曲线 (0.3, 0, 0.1, 1)；显示前有 200ms 延迟。隐藏的缩略图仍占据原有空间。
- `rotation` 让照片随设备方向沿较短方向旋转，每 270 度 1000ms，曲线 (0.3, 0, 0.1, 1)。传入设备方向的相反数即可让照片保持正向。

```kotlin
CameraThumbnail(
    image = latest,
    onClick = { openGallery() },
    visible = !recording,
    rotation = -deviceOrientation, // 0、90、180 或 270
)
```

## 属性

| 属性名             | 类型                      | 说明                                   | 默认值                                        | 是否必须 |
| ------------------ | ------------------------- | -------------------------------------- | --------------------------------------------- | -------- |
| image              | Painter?                  | 最新照片；null 显示占位图              | -                                             | 是       |
| onClick            | () -> Unit                | 点击缩略图时调用                       | -                                             | 是       |
| modifier           | Modifier                  | 应用于缩略图的修饰符                   | Modifier                                      | 否       |
| enabled            | Boolean                   | 是否响应点击                           | true                                          | 否       |
| visible            | Boolean                   | 是否显示                               | true                                          | 否       |
| transition         | CameraThumbnailTransition | 新图片的出现方式                       | CameraThumbnailTransition.ScaleIn             | 否       |
| rotation           | Int                       | 照片旋转到的角度，顺时针度数           | 0                                             | 否       |
| contentDescription | String?                   | 无障碍描述                             | null                                          | 否       |
| size               | Dp                        | 方块边长                               | CameraThumbnailDefaults.Size                  | 否       |
| cornerRadius       | Dp                        | 圆角半径                               | CameraThumbnailDefaults.CornerRadius          | 否       |
| strokeWidth        | Dp                        | 描边宽度；0dp 不画描边                 | CameraThumbnailDefaults.StrokeWidth           | 否       |
| colors             | CameraThumbnailColors     | 缩略图颜色                             | CameraThumbnailDefaults.cameraThumbnailColors() | 否     |

`ImageBitmap` 重载用 `bitmap: ImageBitmap?` 代替 `image`，其余参数相同。

## 默认值对象

### CameraThumbnailDefaults

| 属性名               | 类型  | 说明                         | 值      |
| -------------------- | ----- | ---------------------------- | ------- |
| Size                 | Dp    | 缩略图边长                   | 40.dp   |
| CornerRadius         | Dp    | 圆角半径                     | 6.dp    |
| RetroCornerRadius    | Dp    | 复古胶片风格的圆角半径       | 4.dp    |
| StrokeWidth          | Dp    | 描边宽度                     | 0.67.dp |
| TransitionDuration   | Int   | 新照片动画时长，毫秒         | 350     |
| VisibilityDuration   | Int   | 显示与隐藏动画时长，毫秒     | 200     |
| RotationMillisPer270 | Int   | 旋转速度，每 270 度的毫秒数  | 1000    |
| HiddenScale          | Float | 隐藏时的缩放                 | 0.8f    |
| PressedAlpha         | Float | 按下时的不透明度             | 0.5f    |
| DisabledAlpha        | Float | 禁用时的不透明度             | 0.4f    |

| 函数                    | 返回类型              | 说明             |
| ----------------------- | --------------------- | ---------------- |
| cameraThumbnailColors() | CameraThumbnailColors | 固定的相机配色   |

## 颜色类

### CameraThumbnailColors

| 属性名           | 类型  | 说明               | 默认值    |
| ---------------- | ----- | ------------------ | --------- |
| placeholderColor | Color | 无照片时的底色     | #262626   |
| strokeColor      | Color | 描边               | #1AFFFFFF |
