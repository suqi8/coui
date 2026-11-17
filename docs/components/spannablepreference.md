# SpannablePreference

`SpannablePreference` is a preference row whose summary holds inline clickable links, mirroring COUI's `COUISpannablePreference` (a `COUIPreference` with `couiEnalbeClickSpan` on). The summary is drawn with [LinkText](./linktext): a tap on a link runs only that link's callback, a tap anywhere else runs the row's `onClick`.

<div style="position: relative; height: 220px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=spannablePreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.appendLink
import io.github.suqi8.coui.kmp.preference.SpannablePreference
```

## Basic Usage

```kotlin
SpannablePreference(
    title = "Location services",
    summary = buildAnnotatedString {
        append("Apps use your location to show nearby results. ")
        appendLink("Learn more", onClick = { /* open the help page */ })
    },
    onClick = { /* open the location page */ }
)
```

Without `onClick` the row is not clickable while its links stay interactive.

## Component States

### Disabled State

A disabled row greys out its title, summary and links, and the links stop reacting:

```kotlin
SpannablePreference(
    title = "Disabled",
    summary = buildAnnotatedString {
        append("The link of a disabled row is greyed out: ")
        appendLink("Privacy policy", onClick = {})
    },
    enabled = false
)
```

## Properties

### SpannablePreference Properties

| Property Name | Type                               | Description                                | Default Value                         | Required |
| ------------- | ---------------------------------- | ------------------------------------------ | ------------------------------------- | -------- |
| title         | String                             | Preference title                           | -                                     | Yes      |
| summary       | AnnotatedString                    | Summary holding the links                  | -                                     | Yes      |
| modifier      | Modifier                           | Component modifier                         | Modifier                              | No       |
| titleColor    | BasicComponentColors               | Title text color configuration             | BasicComponentDefaults.titleColor()   | No       |
| summaryColor  | BasicComponentColors               | Non-link summary text color configuration  | BasicComponentDefaults.summaryColor() | No       |
| linkColors    | LinkTextColors                     | Summary link colors                        | LinkTextDefaults.linkTextColors()     | No       |
| startAction   | @Composable (() -> Unit)?          | Custom start side content                  | null                                  | No       |
| endActions    | @Composable (RowScope.() -> Unit)? | Custom end side content                    | null                                  | No       |
| bottomAction  | @Composable (() -> Unit)?          | Custom bottom side content                 | null                                  | No       |
| insideMargin  | PaddingValues                      | Component internal content padding         | BasicComponentDefaults.InsideMargin   | No       |
| cardListPosition | CardListPosition | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); rounded outer edges gain extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No |
| onClick       | (() -> Unit)?                      | Row click callback (outside the links)     | null                                  | No       |
| holdDownState | Boolean                            | Whether the component is held down         | false                                 | No       |
| enabled       | Boolean                            | Component interactive state                | true                                  | No       |

See [LinkText](./linktext) for `LinkTextColors` and `LinkTextDefaults`.
