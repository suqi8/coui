# GuideDialog

`GuideDialog` is the paged guide dialog of ColorOS (`COUIGuideDialogBuilder`, `COUIGuideLayoutContentView`): a bottom dialog (centred on large screens) holding a swipeable pager of pages, each an illustration cropped to the full width, a title and a description, with a page indicator and a "Skip" / "Next" button pair.

"Next" turns the page on COUI's 650ms spring curve. A swipe released with a fling settles on the next or previous page in 500–1000ms on the same spring curve, the time following the fling speed (`COUIViewPager2` `FlingScrollConfig`); a slow release settles on the nearest page on the RecyclerView quintic curve. A page whose content outgrows the pager scrolls with 32dp fading edges. Scrolling from the second-to-last page to the last one widens the next button over the fading skip button while its label cross-fades to "Got it", which then calls `onStart`. A single page shows only the "Got it" button and no indicator. Taps outside the panel are ignored; the back gesture calls `onDismissRequest`.

Two variants are provided:

- `OverlayGuideDialog` — rendered inside `Scaffold`'s `COUIPopupHost` (must be used within `Scaffold`).
- `WindowGuideDialog` — rendered at window level, no `Scaffold` required.

::: danger Prerequisite
`OverlayGuideDialog` depends on `Scaffold` providing `COUIPopupHost` to render popup content. Use `WindowGuideDialog` when no `Scaffold` is available.
:::

<div style="position: relative; height: 560px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=guideDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayGuideDialog
// or
import io.github.suqi8.coui.kmp.window.WindowGuideDialog

import io.github.suqi8.coui.kmp.layout.GuideDialogPage
import io.github.suqi8.coui.kmp.layout.GuideDialogDefaults
import io.github.suqi8.coui.kmp.layout.GuideDialogColors
```

## Basic Usage

```kotlin
var showGuide by remember { mutableStateOf(false) }
val pages = listOf(
    GuideDialogPage(title = "Swipe to switch", description = "Swipe on the bottom bar to switch apps.", image = painterResource(Res.drawable.guide_1)),
    GuideDialogPage(title = "Quick reply", description = "Reply from the notification.", image = painterResource(Res.drawable.guide_2)),
)

Scaffold {
    TextButton(text = "Show guide", onClick = { showGuide = true })

    OverlayGuideDialog(
        show = showGuide,
        pages = pages,
        onStart = { showGuide = false },
        onSkip = { showGuide = false },
        onDismissRequest = { showGuide = false },
    )
}
```

- A missing `image` and a blank `title` or `description` are left out of the page.
- `onSkip = null` (the default) makes "Skip" call `onStart`.

## Properties

### OverlayGuideDialog / WindowGuideDialog Properties

| Property Name        | Type                   | Description                                                                         | Default Value                            | Required |
| -------------------- | ---------------------- | ----------------------------------------------------------------------------------- | ---------------------------------------- | -------- |
| show                 | Boolean                | Whether the dialog is shown                                                         | -                                        | Yes      |
| pages                | List\<GuideDialogPage> | The guide pages                                                                     | -                                        | Yes      |
| onStart              | () -> Unit             | Called by the "Got it" button                                                       | -                                        | Yes      |
| modifier             | Modifier               | Modifier applied to the dialog panel                                                | Modifier                                 | No       |
| backgroundModifier   | Modifier               | Applied inside the panel's corner clip, under the content (for a blurred background) | Modifier                                | No       |
| onSkip               | (() -> Unit)?          | Called by the skip button; `null` falls back to `onStart`                           | null                                     | No       |
| onNext               | ((Int) -> Unit)?       | Called with the target page when "Next" turns the page                              | null                                     | No       |
| onDismissRequest     | (() -> Unit)?          | Called on the back gesture (outside taps are ignored)                               | null                                     | No       |
| skipText             | String                 | Skip button label                                                                   | GuideDialogDefaults.SkipText             | No       |
| nextText             | String                 | Next button label                                                                   | GuideDialogDefaults.NextText             | No       |
| startText            | String                 | Start button label                                                                  | GuideDialogDefaults.StartText            | No       |
| imageHeight          | Dp                     | Illustration height                                                                 | GuideDialogDefaults.ImageHeight          | No       |
| imagePadding         | PaddingValues          | Margins around every illustration                                                   | GuideDialogDefaults.ImagePadding         | No       |
| colors               | GuideDialogColors      | Title and description colors                                                        | GuideDialogDefaults.guideDialogColors()  | No       |
| backgroundColor      | Color                  | Panel fill                                                                          | DialogDefaults.backgroundColor()         | No       |
| enableWindowDim      | Boolean                | Whether to dim the window behind the dialog                                         | true                                     | No       |
| materialStroke       | Boolean                | Draw the COUI 17 material stroke of a blurred panel; the buttons turn material too  | false                                    | No       |
| onDismissFinished    | (() -> Unit)?          | Called after the hide animation completes                                           | null                                     | No       |
| renderInRootScaffold | Boolean                | (Overlay only) Render in the root Scaffold                                          | true                                     | No       |

### GuideDialogPage Properties

| Property Name           | Type     | Description                                         | Default Value |
| ----------------------- | -------- | --------------------------------------------------- | ------------- |
| title                   | String?  | Page title (`?couiTextHeadlineS`)                   | null          |
| description             | String?  | Page description (`?couiTextBodyS`)                 | null          |
| image                   | Painter? | Illustration, cropped to the full width             | null          |
| imageContentDescription | String?  | Accessibility label of the illustration             | null          |

### GuideDialogDefaults Object

| Property Name    | Type          | Description                                     | Value               |
| ---------------- | ------------- | ----------------------------------------------- | ------------------- |
| SkipText         | String        | `coui_guide_dialog_skip_text`                   | "Skip"              |
| NextText         | String        | `coui_guide_dialog_next_text`                   | "Next"              |
| StartText        | String        | `coui_guide_dialog_known_text`                  | "Got it"            |
| ImageHeight      | Dp            | `ImageHeightStyle.DEFAULT`                      | 328.dp              |
| ImageHeightSmall | Dp            | `ImageHeightStyle.HEIGHT_240DP`                 | 240.dp              |
| ImagePadding     | PaddingValues | No margins                                      | PaddingValues(0.dp) |

| Function Name       | Return Type       | Description                                                                 |
| ------------------- | ----------------- | --------------------------------------------------------------------------- |
| guideDialogColors() | GuideDialogColors | Title `?couiColorLabelPrimary`, description `?couiColorLabelSecondary`      |

### GuideDialogColors Properties

| Property Name    | Type  | Description             |
| ---------------- | ----- | ----------------------- |
| titleColor       | Color | Page title color        |
| descriptionColor | Color | Page description color  |

## Advanced Usage

### Single Page

```kotlin
OverlayGuideDialog(
    show = showGuide,
    pages = listOf(GuideDialogPage(title = "New look", description = "The toolbar now floats above your content.")),
    onStart = { showGuide = false },
    startText = "OK",
)
```

### Shorter Illustrations

```kotlin
OverlayGuideDialog(
    show = showGuide,
    pages = pages,
    onStart = { showGuide = false },
    imageHeight = GuideDialogDefaults.ImageHeightSmall,
    imagePadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp),
)
```
