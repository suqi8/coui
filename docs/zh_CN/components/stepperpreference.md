# StepperPreference

`StepperPreference` 是行尾带 [Stepper](./stepper.md) 的设置项，对应 ColorOS 的 `COUIStepperPreference`。COUI 沿用普通设置项布局，只把右侧控件换成步进器，并透传最小值（默认 -999）、最大值（默认 9999）和步长（couiUnit，默认 1）。与 COUI 不带垂直内边距的 widget_frame 一致，32dp 的步进器可以伸入行的垂直内边距，因此行高与普通设置项相同（48dp）。

<div style="position: relative; height: 160px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=stepperPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.StepperPreference
```

## 基本用法

```kotlin
var copies by remember { mutableIntStateOf(1) }

StepperPreference(
    value = copies,
    onValueChange = { copies = it },
    title = "Copies",
    minValue = 1,
    maxValue = 10
)
```

## 组件状态

### 自定义步长

```kotlin
StepperPreference(
    value = fontScale,
    onValueChange = { fontScale = it },
    title = "Font scale",
    summary = "Steps of 10, from 50 to 200",
    minValue = 50,
    maxValue = 200,
    step = 10
)
```

### 禁用状态

```kotlin
StepperPreference(
    value = 3,
    onValueChange = {},
    title = "Disabled",
    enabled = false
)
```

## 属性

### StepperPreference 属性

| 属性名           | 类型                      | 说明                           | 默认值                                 | 是否必须 |
| ---------------- | ------------------------- | ------------------------------ | -------------------------------------- | -------- |
| value            | Int                       | 当前值                         | -                                      | 是       |
| onValueChange    | (Int) -> Unit             | 每次步进时回调新值             | -                                      | 是       |
| title            | String                    | 行标题                         | -                                      | 是       |
| modifier         | Modifier                  | 组件修饰符                     | Modifier                               | 否       |
| titleColor       | BasicComponentColors      | 标题颜色                       | BasicComponentDefaults.titleColor()    | 否       |
| summary          | String?                   | 行摘要                         | null                                   | 否       |
| summaryColor     | BasicComponentColors      | 摘要颜色                       | BasicComponentDefaults.summaryColor()  | 否       |
| minValue         | Int                       | 最小值（couiMinimum）          | StepperDefaults.MinValue (-999)        | 否       |
| maxValue         | Int                       | 最大值（couiMaximum）          | StepperDefaults.MaxValue (9999)        | 否       |
| step             | Int                       | 步长（couiUnit）               | 1                                      | 否       |
| stepperColors    | StepperColors             | 步进器颜色                     | StepperDefaults.stepperColors()        | 否       |
| startAction      | @Composable (() -> Unit)? | 左侧内容                       | null                                   | 否       |
| bottomAction     | @Composable (() -> Unit)? | 底部内容                       | null                                   | 否       |
| insideMargin     | PaddingValues             | 行内边距                       | BasicComponentDefaults.InsideMargin    | 否       |
| cardListPosition | CardListPosition          | 行在卡片分组中的位置           | LocalCardListPosition.current          | 否       |
| enabled          | Boolean                   | 行与步进器是否可用             | true                                   | 否       |
