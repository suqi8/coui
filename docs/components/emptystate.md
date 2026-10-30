# EmptyState

`EmptyState` is the empty-page placeholder mirroring ColorOS's COUIEmptyStateView: an illustration slot, a title, a subtitle and a borderless action text button, stacked and centered. When the height is bounded the group is placed 45% of the way down the free height (50% for the small size type). The illustration is scaled by the size type: hidden for `Small`, 60% for `Medium`, full size for `Large`; `Auto` picks Small below 200dp of height, Medium below 320dp of width or 480dp of height, else Large. The title is capped at two lines and the subtitle at three; both are 280dp wide and scroll once they exceed 92dp together.

COUI fills the illustration with a Lottie animation or an image; here `illustration` is a composable slot drawn inside a box of the resolved illustration size.

<div style="position: relative; height: 460px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=emptyState" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.EmptyState
import io.github.suqi8.coui.kmp.basic.EmptyStateDefaults
import io.github.suqi8.coui.kmp.basic.EmptyStateSizeType
```

## Basic Usage

```kotlin
EmptyState(
    title = "No content",
    modifier = Modifier.fillMaxSize(),
    subtitle = "Nothing has been added yet",
    actionText = "Refresh",
    onActionClick = { /* reload */ },
    illustration = { Image(painterResource(Res.drawable.empty), contentDescription = null) }
)
```

## Properties

### EmptyState Properties

| Property Name      | Type                       | Description                                         | Default Value                          | Required |
| ------------------ | -------------------------- | --------------------------------------------------- | -------------------------------------- | -------- |
| title              | String                     | The title; hidden when empty                        | -                                      | Yes      |
| modifier           | Modifier                   | Modifier applied to the component                   | Modifier                               | No       |
| subtitle           | String?                    | The subtitle; hidden when null or empty             | null                                   | No       |
| actionText         | String?                    | Label of the action text button; hidden when empty  | null                                   | No       |
| onActionClick      | (() -> Unit)?              | Called when the action text button is clicked       | null                                   | No       |
| sizeType           | EmptyStateSizeType         | Size class that scales the illustration             | EmptyStateSizeType.Auto                | No       |
| illustrationWidth  | Dp                         | Full illustration width                             | EmptyStateDefaults.IllustrationWidth   | No       |
| illustrationHeight | Dp                         | Full illustration height                            | EmptyStateDefaults.IllustrationHeight  | No       |
| colors             | EmptyStateColors           | Color configuration                                 | EmptyStateDefaults.emptyStateColors()  | No       |
| titleStyle         | TextStyle                  | Text style of the title                             | COUITheme.textStyles.headline2         | No       |
| subtitleStyle      | TextStyle                  | Text style of the subtitle                          | COUITheme.textStyles.footnote2         | No       |
| actionStyle        | TextStyle                  | Text style of the action                            | COUITheme.textStyles.subtitle          | No       |
| illustration       | (@Composable () -> Unit)?  | The illustration slot; not composed for Small       | null                                   | No       |

### EmptyStateSizeType Enum

`Auto`, `Small`, `Medium`, `Large`.

### EmptyStateDefaults Object

| Property Name         | Type          | Description                                      | Default Value                                   |
| --------------------- | ------------- | ------------------------------------------------ | ----------------------------------------------- |
| IllustrationWidth     | Dp            | Full illustration width                          | 280.dp                                          |
| IllustrationHeight    | Dp            | Full illustration height                         | 200.dp                                          |
| TextWidth             | Dp            | Width of the title and subtitle                  | 280.dp                                          |
| TextMaxHeight         | Dp            | Maximum height of the scrolling text block       | 92.dp                                           |
| SubtitleMarginTop     | Dp            | Top margin of the subtitle                       | 2.dp                                            |
| ActionMarginTop       | Dp            | Top margin of the action                         | 8.dp                                            |
| ActionPadding         | PaddingValues | Press area padding of the action                 | PaddingValues(horizontal = 12.dp, vertical = 4.dp) |
| HeightThresholdSmall  | Dp            | Auto resolves to Small below this height         | 200.dp                                          |
| WidthThresholdMedium  | Dp            | Auto resolves to Medium below this width         | 320.dp                                          |
| HeightThresholdMedium | Dp            | Auto resolves to Medium below this height        | 480.dp                                          |

`EmptyStateDefaults.resolveSizeType(sizeType, width, height)` resolves `Auto` against a size.

### EmptyStateColors Class

| Property Name | Type  | Description                  |
| ------------- | ----- | ---------------------------- |
| titleColor    | Color | Color of the title           |
| subtitleColor | Color | Color of the subtitle        |
| actionColor   | Color | Color of the action button   |
