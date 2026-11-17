# TwoTextButton

`TwoTextButton` is a button with two labels drawn on top of each other, each at its own opacity, after ColorOS 17's `COUITwoTextButton`. The guide dialog uses it as its primary "Next" / "Start" button and cross-fades the labels while its pages scroll.

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=twoTextButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.TwoTextButton
import io.github.suqi8.coui.kmp.basic.TwoTextButtonDefaults
```

## Basic Usage

```kotlin
// progress: 0 shows "Next", 1 shows "Get started"
TwoTextButton(
    startText = "Next",
    nextText = "Get started",
    startTextAlpha = { TwoTextButtonDefaults.outgoingTextAlpha(progress) },
    nextTextAlpha = { TwoTextButtonDefaults.incomingTextAlpha(progress) },
    onClick = { /* ... */ },
    modifier = Modifier.fillMaxWidth(),
)
```

## Properties

### TwoTextButton Properties

| Property Name     | Type          | Description                                                           | Default Value                       | Required |
| ----------------- | ------------- | --------------------------------------------------------------------- | ----------------------------------- | -------- |
| startText         | String        | First label (`setStartText`)                                          | -                                   | Yes      |
| nextText          | String        | Second label (`setNextText`)                                          | -                                   | Yes      |
| startTextAlpha    | () -> Float   | Opacity of `startText`, read while drawing (`setStartTextAlpha`)      | -                                   | Yes      |
| nextTextAlpha     | () -> Float   | Opacity of `nextText`, read while drawing (`setNextTextAlpha`)        | -                                   | Yes      |
| onClick           | () -> Unit    | Called when the button is clicked                                     | -                                   | Yes      |
| modifier          | Modifier      | Modifier applied to the button                                        | Modifier                            | No       |
| enabled           | Boolean       | Whether the button is enabled                                         | true                                | No       |
| pressScaleEnabled | Boolean       | Whether the button shrinks while pressed                              | true                                | No       |
| cornerRadius      | Dp            | Corner radius; unspecified is the capsule                             | ButtonDefaults.CornerRadius         | No       |
| minWidth          | Dp            | Minimum width                                                         | ButtonDefaults.MinWidth             | No       |
| minHeight         | Dp            | Minimum height                                                        | ButtonDefaults.MinHeight            | No       |
| colors            | ButtonColors  | Button colors; primary, as the guide dialog uses `ButtonType.PRIMARY` | ButtonDefaults.buttonColorsPrimary() | No      |
| insideMargin      | PaddingValues | Margin inside the button                                              | ButtonDefaults.InsideMargin         | No       |
| textStyle         | TextStyle     | Style of both labels                                                  | COUITheme.textStyles.buttonL         | No       |
| material          | ButtonMaterial | COUI 17 material layer, see [Button](./button)                       | ButtonMaterial.Auto                 | No       |
| materialBackdrop  | Boolean       | Whether the material may blur the backdrop, see [Button](./button)    | true                                | No       |

### TwoTextButtonDefaults Object

The fade of `COUIGuideDialogButtonLayout.textAlphaAnimate`, driven by the eased page offset `f` (0..1) measured from the page the drag started on.

| Name                        | Type            | Description                                                              | Value |
| --------------------------- | --------------- | ------------------------------------------------------------------------ | ----- |
| TextFadeStartProgress       | Float           | Where both fades start (`TEXT_FADE_START_PROGRESS`)                      | 0.5   |
| TextFadeEndProgress         | Float           | Where the outgoing label is gone (`TEXT_FADE_END_PROGRESS`)              | 0.85  |
| TextFadeRange               | Float           | Length of the outgoing fade (`TEXT_FADE_RANGE`)                          | 0.35  |
| outgoingTextAlpha(progress) | (Float) -> Float | 1 up to 0.5, then `(0.85 - f) / 0.35`, clamped to 0..1                  | -     |
| incomingTextAlpha(progress) | (Float) -> Float | `(f - 0.5) / 0.5`, clamped to 0..1                                      | -     |

## Behavior

- Each label is one line, ellipsized at the end to the width inside the padding, and centred in the button (`COUITwoTextButton.drawText`).
- The alphas are read in the draw phase, so animating them redraws the labels without recomposing the button.
- Accessibility services read the label that is currently the more opaque one.
- Everything else (press scale, touch motion, material, colors) is the regular [Button](./button).

## Advanced Usage

### Driven by a Pager

```kotlin
val pagerState = rememberPagerState { pageCount }
// Offset between the second-to-last and the last page, 0..1.
val offset = {
    (pagerState.currentPage + pagerState.currentPageOffsetFraction - (pageCount - 2)).coerceIn(0f, 1f)
}

TwoTextButton(
    startText = "Next",
    nextText = "Get started",
    startTextAlpha = { TwoTextButtonDefaults.outgoingTextAlpha(offset()) },
    nextTextAlpha = { TwoTextButtonDefaults.incomingTextAlpha(offset()) },
    onClick = { /* next page or finish */ },
)
```
