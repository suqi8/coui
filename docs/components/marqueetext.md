# MarqueeText

`MarqueeText` is single-line text that scrolls endlessly when it does not fit, with both edges faded, after ColorOS's `COUIMarqueeTextView` (the content view of the COUI top tips). Text that fits is drawn still and unfaded.

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=marqueeText" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.MarqueeText
import io.github.suqi8.coui.kmp.basic.MarqueeTextDefaults
```

## Basic Usage

```kotlin
MarqueeText(
    text = "Battery optimisation is limiting background activity for several apps, tap to review them",
    style = COUITheme.textStyles.bodyS,
    modifier = Modifier.fillMaxWidth(),
)
```

## Properties

### MarqueeText Properties

| Property Name      | Type      | Description                                                   | Default Value                        | Required |
| ------------------ | --------- | ------------------------------------------------------------- | ------------------------------------ | -------- |
| text               | String    | Text to display                                               | -                                    | Yes      |
| modifier           | Modifier  | Modifier applied to the text                                  | Modifier                             | No       |
| enabled            | Boolean   | Whether the marquee is on; when off the text wraps normally   | true                                 | No       |
| color              | Color     | Text color; Unspecified falls back to `style` and the content color | Color.Unspecified              | No       |
| style              | TextStyle | Text style                                                    | LocalTextStyles.current.main         | No       |
| velocity           | Dp        | Scroll speed in dp per second                                 | MarqueeTextDefaults.Velocity         | No       |
| initialDelayMillis | Int       | Delay before the first scroll                                 | MarqueeTextDefaults.InitialDelayMillis | No     |
| spacing            | Dp        | Gap between the end of the text and its next copy             | MarqueeTextDefaults.Spacing          | No       |
| startOffset        | Dp        | Inset of the text from the start edge before it scrolls      | MarqueeTextDefaults.StartOffset      | No       |
| fadingEdgeLength   | Dp        | Length of the fade at each edge while scrolling               | MarqueeTextDefaults.FadingEdgeLength | No       |

### MarqueeTextDefaults Object

| Constant Name      | Type | Description                                                   | Default Value |
| ------------------ | ---- | ------------------------------------------------------------- | ------------- |
| Velocity           | Dp   | Scroll speed per second (`coui_top_tips_scroll_speed`)        | 25.dp         |
| InitialDelayMillis | Int  | Delay before the first scroll (`DEFAULT_SCROLL_DELAY_DURATION`) | 1000        |
| Spacing            | Dp   | Gap between copies (`coui_top_tips_scroll_text_interval`)     | 80.dp         |
| StartOffset        | Dp   | Resting start inset (`coui_top_tips_scroll_text_start_location`) | 10.dp      |
| FadingEdgeLength   | Dp   | Length of each fading edge (`coui_top_tips_fading_edge_size`) | 26.dp         |

## Behavior

- **Fit**: when the measured text fits, it is drawn still with no fading edges.
- **Scroll**: otherwise the text starts 10dp in from the start edge and, after 1 second, scrolls continuously at 25dp per second (COUI moves `25dp / refreshRate` per frame) with copies 80dp apart and no pause between loops. RTL text scrolls the other way.
- **Fade**: while scrolling both edges fade over 26dp, like a `TextView` horizontal fading edge at strength 1.
- **Disabled**: with `enabled = false` the text is a plain wrapping `Text`.
