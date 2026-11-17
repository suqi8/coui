# TwoTextButton

`TwoTextButton` 是叠放两段文字、各自有独立透明度的按钮，对应 ColorOS 17 的 `COUITwoTextButton`。引导对话框把它用作主按钮「下一步」/「开始使用」，在翻页时让两段文字交叉淡入淡出。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=twoTextButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.TwoTextButton
import io.github.suqi8.coui.kmp.basic.TwoTextButtonDefaults
```

## 基本用法

```kotlin
// progress 为 0 时显示「下一步」，为 1 时显示「开始使用」
TwoTextButton(
    startText = "下一步",
    nextText = "开始使用",
    startTextAlpha = { TwoTextButtonDefaults.outgoingTextAlpha(progress) },
    nextTextAlpha = { TwoTextButtonDefaults.incomingTextAlpha(progress) },
    onClick = { /* ... */ },
    modifier = Modifier.fillMaxWidth(),
)
```

## 属性

### TwoTextButton 属性

| 属性名            | 类型           | 说明                                                     | 默认值                               | 是否必须 |
| ----------------- | -------------- | -------------------------------------------------------- | ------------------------------------ | -------- |
| startText         | String         | 第一段文字（`setStartText`）                             | -                                    | 是       |
| nextText          | String         | 第二段文字（`setNextText`）                              | -                                    | 是       |
| startTextAlpha    | () -> Float    | `startText` 的透明度，绘制时读取（`setStartTextAlpha`）  | -                                    | 是       |
| nextTextAlpha     | () -> Float    | `nextText` 的透明度，绘制时读取（`setNextTextAlpha`）    | -                                    | 是       |
| onClick           | () -> Unit     | 点击回调                                                 | -                                    | 是       |
| modifier          | Modifier       | 应用于按钮的修饰符                                       | Modifier                             | 否       |
| enabled           | Boolean        | 是否可用                                                 | true                                 | 否       |
| pressScaleEnabled | Boolean        | 按压时是否缩小                                           | true                                 | 否       |
| cornerRadius      | Dp             | 圆角；未指定时为胶囊形                                   | ButtonDefaults.CornerRadius          | 否       |
| minWidth          | Dp             | 最小宽度                                                 | ButtonDefaults.MinWidth              | 否       |
| minHeight         | Dp             | 最小高度                                                 | ButtonDefaults.MinHeight             | 否       |
| colors            | ButtonColors   | 按钮颜色；引导对话框使用 `ButtonType.PRIMARY`，故默认主色 | ButtonDefaults.buttonColorsPrimary() | 否       |
| insideMargin      | PaddingValues  | 按钮内边距                                               | ButtonDefaults.InsideMargin          | 否       |
| textStyle         | TextStyle      | 两段文字的样式                                           | COUITheme.textStyles.buttonL          | 否       |
| material          | ButtonMaterial | COUI 17 材质层，见 [Button](./button)                    | ButtonMaterial.Auto                  | 否       |
| materialBackdrop  | Boolean        | 材质是否可模糊背景，见 [Button](./button)                | true                                 | 否       |

### TwoTextButtonDefaults 对象

对应 `COUIGuideDialogButtonLayout.textAlphaAnimate` 的淡入淡出，由从拖动起始页算起的、经缓动的页面偏移 `f`（0..1）驱动。

| 名称                        | 类型             | 说明                                                   | 值    |
| --------------------------- | ---------------- | ------------------------------------------------------ | ----- |
| TextFadeStartProgress       | Float            | 两段淡变的起点（`TEXT_FADE_START_PROGRESS`）           | 0.5   |
| TextFadeEndProgress         | Float            | 离场文字完全消失处（`TEXT_FADE_END_PROGRESS`）         | 0.85  |
| TextFadeRange               | Float            | 离场淡出的长度（`TEXT_FADE_RANGE`）                    | 0.35  |
| outgoingTextAlpha(progress) | (Float) -> Float | 0.5 之前为 1，之后为 `(0.85 - f) / 0.35`，限制在 0..1  | -     |
| incomingTextAlpha(progress) | (Float) -> Float | `(f - 0.5) / 0.5`，限制在 0..1                         | -     |

## 行为

- 每段文字单行显示，超出内边距内宽度时末尾省略，并在按钮中居中（`COUITwoTextButton.drawText`）。
- 透明度在绘制阶段读取，动画只重绘文字，不会重组按钮。
- 无障碍服务朗读当前更不透明的那段文字。
- 其余部分（按压缩放、触控动效、材质、颜色）与普通 [Button](./button) 相同。

## 进阶用法

### 由 Pager 驱动

```kotlin
val pagerState = rememberPagerState { pageCount }
// 倒数第二页与最后一页之间的偏移，0..1。
val offset = {
    (pagerState.currentPage + pagerState.currentPageOffsetFraction - (pageCount - 2)).coerceIn(0f, 1f)
}

TwoTextButton(
    startText = "下一步",
    nextText = "开始使用",
    startTextAlpha = { TwoTextButtonDefaults.outgoingTextAlpha(offset()) },
    nextTextAlpha = { TwoTextButtonDefaults.incomingTextAlpha(offset()) },
    onClick = { /* 下一页或完成 */ },
)
```
