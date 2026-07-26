# Text

`Text` component is a basic text component in Miuix, used to display text content. It supports customizing various text styles, alignment, and decoration effects.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=text" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Text
```

## Basic Usage

The simplest text display:

```kotlin
Text("This is a basic text")
```

## Text Styles

Miuix provides multiple predefined text styles:

```kotlin
Text(
    text = "Title Text",
    style = COUITheme.textStyles.headline1
)

Text(
    text = "Subtitle Text",
    style = COUITheme.textStyles.subtitle
)
Text(
    text = "Summary Text",
    style = COUITheme.textStyles.body2
)

Text(
    text = "Main Text",
    style = COUITheme.textStyles.main
)

```

## Text Color

```kotlin
Text(
    text = "Default Color Text",
    color = COUITheme.colorScheme.onBackground
)

Text(
    text = "Primary Color Text",
    color = COUITheme.colorScheme.primary
)

Text(
    text = "Secondary Text",
    color = COUITheme.colorScheme.onSurfaceContainerVariant
)
```

## Properties

### Text (String) Properties

| Property Name  | Type           | Description                             | Default Value              | Required |
| -------------- | -------------- | --------------------------------------- | -------------------------- | -------- |
| text           | String         | The text content to display             | -                          | Yes      |
| modifier       | Modifier       | Modifiers applied to the text           | Modifier                   | No       |
| color          | Color          | Text color                              | Color.Unspecified          | No       |
| autoSize       | TextAutoSize?  | Auto-sizing behavior for text           | null                       | No       |
| fontSize       | TextUnit       | Text size                               | TextUnit.Unspecified       | No       |
| fontStyle      | FontStyle?     | Text font style (e.g., italic)          | null                       | No       |
| fontWeight     | FontWeight?    | Text font weight                        | null                       | No       |
| fontFamily     | FontFamily?    | Text font family                        | null                       | No       |
| letterSpacing  | TextUnit       | Letter spacing                          | TextUnit.Unspecified       | No       |
| textDecoration | TextDecoration? | Text decoration (e.g., underline)      | null                       | No       |
| textAlign      | TextAlign?     | Text alignment                          | null                       | No       |
| lineHeight     | TextUnit       | Line height                             | TextUnit.Unspecified       | No       |
| overflow       | TextOverflow   | Text overflow handling                  | TextOverflow.Clip          | No       |
| softWrap       | Boolean        | Whether to wrap text automatically      | true                       | No       |
| maxLines       | Int            | Maximum number of lines                 | Int.MAX_VALUE              | No       |
| minLines       | Int            | Minimum number of lines                 | 1                          | No       |
| onTextLayout   | ((TextLayoutResult) -> Unit)? | Callback after layout | null                       | No       |
| style          | TextStyle      | Text style                              | COUITheme.textStyles.main | No       |

### Text (AnnotatedString) Properties

| Property Name  | Type                           | Description                                   | Default Value              | Required |
| -------------- | ------------------------------ | --------------------------------------------- | -------------------------- | -------- |
| text           | AnnotatedString                | Rich text with annotations                    | -                          | Yes      |
| modifier       | Modifier                       | Modifiers applied to the text                 | Modifier                   | No       |
| color          | Color                          | Text color                                    | Color.Unspecified          | No       |
| autoSize       | TextAutoSize?                  | Auto-sizing behavior for text                 | null                       | No       |
| fontSize       | TextUnit                       | Text size                                     | TextUnit.Unspecified       | No       |
| fontStyle      | FontStyle?                     | Text font style                               | null                       | No       |
| fontWeight     | FontWeight?                    | Text font weight                              | null                       | No       |
| fontFamily     | FontFamily?                    | Text font family                              | null                       | No       |
| letterSpacing  | TextUnit                       | Letter spacing                                | TextUnit.Unspecified       | No       |
| textDecoration | TextDecoration?                | Text decoration                               | null                       | No       |
| textAlign      | TextAlign?                     | Text alignment                                | null                       | No       |
| lineHeight     | TextUnit                       | Line height                                   | TextUnit.Unspecified       | No       |
| overflow       | TextOverflow                   | Text overflow handling                        | TextOverflow.Clip          | No       |
| softWrap       | Boolean                        | Whether to wrap text automatically            | true                       | No       |
| maxLines       | Int                            | Maximum number of lines                       | Int.MAX_VALUE              | No       |
| minLines       | Int                            | Minimum number of lines                       | 1                          | No       |
| inlineContent  | Map<String, InlineTextContent> | Mapping for inserting inline composables      | mapOf()                    | No       |
| onTextLayout   | (TextLayoutResult) -> Unit     | Callback after text layout is completed       | {}                         | No       |
| style          | TextStyle                      | Text style                                    | COUITheme.textStyles.main | No       |

## Advanced Usage

### Multi-line Text Truncation

```kotlin
Text(
    text = "This is a very long text that will be truncated and show ellipsis when there is not enough space. This is useful for displaying long content summaries.",
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
)
```

### Text Decoration

```kotlin
Text(
    text = "Underlined Text",
    textDecoration = TextDecoration.Underline
)

Text(
    text = "Strikethrough Text",
    textDecoration = TextDecoration.LineThrough
)
```

### Rich Text Mixing

```kotlin
Text(
    buildAnnotatedString {
        withStyle(style = SpanStyle(color = COUITheme.colorScheme.primary)) {
            append("Miuix ")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("UI Library")
        }
        append(", this is a piece of rich text")
    }
)
```

### Auto-sizing Text

Automatically fit text within available space using `TextAutoSize`:

```kotlin
Text(
    text = "Adaptive text",
    autoSize = TextAutoSize.StepBased(
        minFontSize = 12.sp,
        maxFontSize = 17.sp,
        stepSize = 0.3.sp
    )
)
```

### Frequently Changing Color

Both `Text(String, ...)` and `Text(AnnotatedString, ...)` have a companion overload that takes a `ColorProducer` instead of a `Color`:

```kotlin
fun Text(text: String, color: ColorProducer, modifier: Modifier = Modifier, ...)
fun Text(text: AnnotatedString, color: ColorProducer, modifier: Modifier = Modifier, ...)
```

All other parameters are identical to the tables above. The producer is read directly during draw, so when the text color changes on every frame (animations, scroll listeners, gesture-driven values), the `Text` itself is not recomposed:

```kotlin
val scrollState = rememberScrollState()
Text(
    text = "Scroll-tinted text",
    color = {
        lerp(
            COUITheme.colorScheme.onSurface,
            COUITheme.colorScheme.primary,
            (scrollState.value / 500f).coerceIn(0f, 1f),
        )
    },
)
```

### Clickable Links

Use `AnnotatedString` with `LinkAnnotation` to create clickable links:

```kotlin
val annotated = buildAnnotatedString {
    append("Visit ")
    val start = length
    append("Miuix Docs")
    addLink(
        LinkAnnotation.Url(
            url = "https://compose-coui-ui.github.io/miuix/",
            styles = TextLinkStyles(
                SpanStyle(color = COUITheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            )
        ),
        start = start,
        end = length
    )
}

Text(text = annotated)
```
