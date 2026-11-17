# LinkText

`LinkText` shows text with inline clickable links in the ColorOS link style, mirroring COUI's `COUIClickableSpan`. Links are standard Compose `LinkAnnotation`s, so a tap on a link runs only that link's callback, while taps elsewhere still reach a clickable parent.

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=linkText" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.LinkText
import io.github.suqi8.coui.kmp.basic.appendLink
```

## Basic Usage

Build the text with `buildAnnotatedString` and append links with `appendLink`:

```kotlin
LinkText(
    text = buildAnnotatedString {
        append("Read the ")
        appendLink("User Agreement", onClick = { /* open the agreement */ })
        append(" and the ")
        appendLink("Privacy Policy", onClick = { /* open the policy */ })
    },
    color = COUITheme.colorScheme.labelSecondary,
    style = COUITheme.textStyles.bodyS
)
```

::: tip Link style
Every link without its own `TextLinkStyles` gets the COUI link style (`coui_clickable_text_color`): the theme accent at rest, the accent at 30% alpha while pressed, no underline and no highlight behind the link. Links appended with `appendLink` carry no styles, so render them with `LinkText` (or a component built on it); the plain `Text` gives unstyled links its own underlined style instead.
:::

## Why `LinkAnnotation`

`COUIClickableSpan` is a `ClickableSpan` driven by `LinkMovementMethod`. The Compose counterpart is `LinkAnnotation.Clickable` with `TextLinkStyles`: it hit-tests only the link range, exposes the link to accessibility services, and has a built-in `pressedStyle` that maps directly to the COUI pressed color. The deprecated `ClickableText` would need manual offset hit-testing and press tracking.

## Component States

### Disabled State

A disabled `LinkText` draws its links in the disabled color and removes their click handling:

```kotlin
LinkText(
    text = buildAnnotatedString {
        append("A disabled link: ")
        appendLink("Terms of service", onClick = {})
    },
    enabled = false
)
```

## Properties

### LinkText Properties

| Property Name | Type           | Description                                                    | Default Value                     | Required |
| ------------- | -------------- | -------------------------------------------------------------- | --------------------------------- | -------- |
| text          | AnnotatedString | Text holding `LinkAnnotation`s for the links                  | -                                 | Yes      |
| modifier      | Modifier       | Component modifier                                             | Modifier                          | No       |
| enabled       | Boolean        | Whether the links are interactive                              | true                              | No       |
| color         | Color          | Color of the non-link text                                     | Color.Unspecified                 | No       |
| linkColors    | LinkTextColors | Colors of links without their own styles                       | LinkTextDefaults.linkTextColors() | No       |
| style         | TextStyle      | Text style                                                     | COUITheme.textStyles.bodyXL         | No       |
| textAlign     | TextAlign?     | Text alignment                                                 | null                              | No       |
| overflow      | TextOverflow   | Overflow handling                                              | TextOverflow.Clip                 | No       |
| maxLines      | Int            | Maximum number of lines                                        | Int.MAX_VALUE                     | No       |

### appendLink

`AnnotatedString.Builder.appendLink(text: String, onClick: () -> Unit, tag: String = text)` appends `text` as a `LinkAnnotation.Clickable` without styles.

### LinkTextDefaults Object

| Function / Property | Description                                                                  |
| ------------------- | ---------------------------------------------------------------------------- |
| linkTextColors()    | Creates `LinkTextColors`: `color` (theme `primary`), `pressedColor` (`color` at 30% alpha), `disabledColor` (theme `outline`, COUI #26000000 / #26FFFFFF) |
| linkStyles(colors)  | The `TextLinkStyles` of a COUI link, for links built with explicit styles    |

### LinkTextColors Class

| Property Name | Type  | Description                  |
| ------------- | ----- | ---------------------------- |
| color         | Color | Link color at rest           |
| pressedColor  | Color | Link color while pressed     |
| disabledColor | Color | Link color when disabled     |

## Advanced Usage

### Explicit Styles in a Plain Text

```kotlin
val linkStyles = LinkTextDefaults.linkStyles()
Text(
    text = buildAnnotatedString {
        append("Visit the ")
        withLink(LinkAnnotation.Url("https://example.com", styles = linkStyles)) {
            append("website")
        }
    }
)
```
