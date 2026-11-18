# RollingNumber

`RollingNumber` 显示一个整数，数值变化时各位数字像滚轮一样滚动，移植自相机的 `RollingNumView`（连拍计数）与 `PaletteRollingNumView`（调色盘与滤镜强度数值）。每位数字占一个与样式中 "0" 等宽的格子，因此计数时数字整体宽度保持不变。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=rollingNumber" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.RollingNumber
import io.github.suqi8.coui.kmp.basic.RollingNumberDefaults
```

## 基本用法

```kotlin
var count by remember { mutableIntStateOf(8) }

RollingNumber(value = count)
```

### 滚动效果

- 数字发生变化的每一位在 200ms 内按 (0.6, 0, 0.3, 1) 曲线滚动：旧数字移出 `rollDistance` 的距离，同时向后倾斜 45° 并淡出；新数字从另一侧移入，由 45° 倾斜转为平放并淡入。
- 数值增大时向上滚动，减小时向下滚动。
- 某一位在滚动途中再次变化时，从它正滚向的数字重新开始滚动。
- 位数或正负号改变时（9 → 10、1 → -1），整个数字一起滚动。

### 个位

与相机一致，个位默认不滚动：它每一步都会变化，因此直接切换，只有进位（十位、百位……）滚动。设置 `rollOnesDigit` 让每一位都滚动：

```kotlin
RollingNumber(value = count, rollOnesDigit = true)
```

### 连拍计数

相机的连拍计数：两位数字（`minDigits = 2`），14dp 白色文字滚动 13dp，置于 32dp 的 50% 黑色圆底中：

```kotlin
Box(
    modifier = Modifier
        .size(RollingNumberDefaults.BurstBadgeSize)
        .background(Color(0x7F000000), CircleShape),
    contentAlignment = Alignment.Center,
) {
    RollingNumber(
        value = shots,
        minDigits = 2,
        color = Color.White,
        style = COUITheme.textStyles.bodyXL.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
        rollDistance = RollingNumberDefaults.BurstRollDistance,
    )
}
```

### 负数

负数前带负号；跨过零时整个数字一起滚动：

```kotlin
RollingNumber(value = tone) // -100..100
```

## 属性

### RollingNumber 属性

| 属性名        | 类型      | 说明                                                       | 默认值                        | 是否必须 |
| ------------- | --------- | ---------------------------------------------------------- | ----------------------------- | -------- |
| value         | Int       | 要显示的数字                                               | -                             | 是       |
| modifier      | Modifier  | 应用于数字的修饰符                                         | Modifier                      | 否       |
| rollOnesDigit | Boolean   | 个位是否也滚动                                             | false                         | 否       |
| minDigits     | Int       | 最少位数，不足时补前导零（1..10）                          | 1                             | 否       |
| color         | Color     | 数字颜色；未指定时依次取样式颜色、`LocalContentColor`       | Color.Unspecified             | 否       |
| style         | TextStyle | 数字的文字样式                                             | COUITheme.textStyles.bodyXL   | 否       |
| rollDistance  | Dp        | 数字滚动的距离；未指定时使用字号                           | Dp.Unspecified                | 否       |

## 默认值对象

### RollingNumberDefaults

| 属性名              | 类型  | 说明                         | 值     |
| ------------------- | ----- | ---------------------------- | ------ |
| RollDurationMillis  | Int   | 每一位的滚动时长             | 200    |
| RollAngle           | Float | 滚动两端数字的倾斜角度       | 45f    |
| BurstTextSize       | Dp    | 连拍计数数字字号             | 14.dp  |
| BurstRollDistance   | Dp    | 连拍计数滚动距离             | 13.dp  |
| BurstBadgeSize      | Dp    | 连拍计数圆底尺寸             | 32.dp  |
| PaletteTextSize     | Dp    | 调色盘数值字号               | 10.dp  |
| PaletteRollDistance | Dp    | 调色盘数值滚动距离           | 12.dp  |
