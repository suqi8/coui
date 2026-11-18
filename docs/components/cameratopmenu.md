# CameraTopMenu

`CameraTopMenu` is the settings bar at the top of the camera viewfinder, the port of the camera's `CameraMenuContainerTopLeft` / `TopMiddle` / `TopRight` and their expand panels (`CameraMenuContainerExpandTopSingle`, `CameraMenuContainerExpandTopGroup`). Up to three capsules hold the setting icons; tapping an icon with options morphs its capsule into an option strip or a grouped option panel over the full width of the bar.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraTopMenu" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraTopMenu
import io.github.suqi8.coui.kmp.basic.CameraTopMenuItem
import io.github.suqi8.coui.kmp.basic.CameraTopMenuOptionRow
```

## Basic Usage

```kotlin
var expanded by remember { mutableStateOf<String?>(null) }
var flash by remember { mutableIntStateOf(1) }

CameraTopMenu(
    onItemClick = { key -> expanded = if (expanded == key) null else key },
    onOptionSelected = { _, _, index ->
        flash = index
        expanded = null
    },
    startItems = listOf(
        CameraTopMenuItem(
            key = "flash",
            icon = flashIcons[flash],
            contentDescription = "Flash",
            options = listOf(
                CameraTopMenuOptionRow(key = "flash", options = listOf("Off", "Auto", "On", "Always on"), selectedIndex = flash),
            ),
        ),
    ),
    expandedItemKey = expanded,
)
```

The bar fills the width it is given and is 32dp high. `startItems` sit 14dp from the start, `endItems` 14dp from the end and `middleItems` 10dp before the end capsule. Each entry is a 32dp cell with a 20dp icon, drawn as is: the camera's menu icons carry their own colors, so pass an icon per value (for example a flash-on icon).

The menu is controlled: `onItemClick` reports every tapped entry and `expandedItemKey` decides which panel is open. The open panel draws on top of the bar and below it, outside the bar's bounds, so leave room under the bar and do not clip it.

### Option Strip

An entry with one option row opens a 32dp strip. The strip grows out of the entry's capsule in 380ms on the COUI critically damped spring (response 0.8); the entry icon slides to the start of the strip in 300ms on (0.3, 0, 0.1, 1) and the options fade in 200ms later over 300ms on (0.33, 0, 0.67, 1). The options share the width equally. With a row `title`, the title (48dp, `#88FFFFFF`) replaces the icon at the start of the strip:

```kotlin
CameraTopMenuOptionRow(key = "ratio", options = listOf("1:1", "4:3", "16:9", "Full"), selectedIndex = 1, title = "Ratio")
```

### Grouped Panel

An entry with several option rows opens a grouped panel: 24dp start, 12dp end and 12dp vertical padding, one 38dp row per option row, a 48dp title column, and the options of every row sharing the remaining width divided by the longest row. The panel springs out of the capsule (COUI spring, bounce 0.2, response 0.5) while its corner grows from 16dp to 24dp, its content scales up from 10%, the capsule shrinks to 10%, and the content fades in 150ms later over 300ms. Closing springs back while the whole panel fades out over 150ms. The panel carries the camera edge light.

```kotlin
CameraTopMenuItem(
    key = "video",
    icon = videoIcon,
    contentDescription = "Video",
    options = listOf(
        CameraTopMenuOptionRow(key = "size", options = listOf("720P", "1080P", "4K"), selectedIndex = 1, title = "Size"),
        CameraTopMenuOptionRow(key = "fps", options = listOf("30", "60"), selectedIndex = 0, title = "FPS"),
    ),
)
```

Options are 12dp text, medium, turning semibold and fading to the accent `#FB822A` in 300ms when selected.

### Material Blur

Over the camera preview the capsules, strips and panels are the camera material blur (80dp radius, a 65% grey glow overlay at 40% over a 60% mix of 15% grey). Pass a `MaterialBlurProvider` that records the content under the bar; without one the capsules and strips keep a 50% black fill and the grouped panel the camera's opaque `#141414` panel with a 0.67dp 10% white border.

```kotlin
CameraTopMenu(
    onItemClick = onItemClick,
    onOptionSelected = onOptionSelected,
    startItems = startItems,
    materialBlur = provider, // records the viewfinder, not the bar
)
```

## Properties

### CameraTopMenu Properties

| Property Name    | Type                                | Description                                              | Default Value                             | Required |
| ---------------- | ----------------------------------- | -------------------------------------------------------- | ----------------------------------------- | -------- |
| onItemClick      | (String) -> Unit                    | Called with the key of a tapped entry                    | -                                         | Yes      |
| onOptionSelected | (String, String, Int) -> Unit       | Called with the entry key, row key and option index      | -                                         | Yes      |
| modifier         | Modifier                            | Modifier applied to the bar                              | Modifier                                  | No       |
| startItems       | List&lt;CameraTopMenuItem&gt;       | Entries of the start capsule                             | emptyList()                               | No       |
| middleItems      | List&lt;CameraTopMenuItem&gt;       | Entries of the capsule before the end capsule            | emptyList()                               | No       |
| endItems         | List&lt;CameraTopMenuItem&gt;       | Entries of the end capsule                               | emptyList()                               | No       |
| expandedItemKey  | String?                             | Key of the entry whose panel is open                     | null                                      | No       |
| materialBlur     | MaterialBlurProvider?               | Paints the material blur behind capsules and panels      | null                                      | No       |
| colors           | CameraTopMenuColors                 | Colors of the menu                                       | CameraTopMenuDefaults.cameraTopMenuColors() | No     |

### CameraTopMenuItem

| Property Name      | Type                                 | Description                                         | Default Value |
| ------------------ | ------------------------------------ | --------------------------------------------------- | ------------- |
| key                | String                               | Unique key of the entry                             | -             |
| icon               | ImageVector                          | Icon, drawn untinted                                | -             |
| contentDescription | String?                              | Accessibility description                           | -             |
| options            | List&lt;CameraTopMenuOptionRow&gt;   | Option rows; one row opens a strip, more a panel    | emptyList()   |

### CameraTopMenuOptionRow

| Property Name | Type               | Description                                  | Default Value |
| ------------- | ------------------ | -------------------------------------------- | ------------- |
| key           | String             | Unique key of the row within its entry       | -             |
| options       | List&lt;String&gt; | Option labels, start to end                  | -             |
| selectedIndex | Int                | Index of the selected option                 | -             |
| title         | String?            | Optional title at the start of the row       | null          |

## Defaults Object

### CameraTopMenuDefaults

| Property Name           | Type | Description                                         | Value   |
| ----------------------- | ---- | --------------------------------------------------- | ------- |
| ItemSize                | Dp   | Bar height and entry cell size                      | 32.dp   |
| IconSize                | Dp   | Entry icon size                                     | 20.dp   |
| HorizontalMargin        | Dp   | Bar inset at both sides                             | 14.dp   |
| MiddleSpacing           | Dp   | Gap between the middle and the end capsule          | 10.dp   |
| CornerRadius            | Dp   | Capsule and strip corner radius (coerced to a capsule) | 32.dp |
| BlurRadius              | Dp   | Material blur radius                                | 80.dp   |
| PanelCornerRadius       | Dp   | Corner radius of an open grouped panel              | 24.dp   |
| PanelPaddingStart       | Dp   | Grouped panel start padding                         | 24.dp   |
| PanelPaddingEnd         | Dp   | Panel end padding                                   | 12.dp   |
| PanelPaddingVertical    | Dp   | Grouped panel top and bottom padding                | 12.dp   |
| PanelRowHeight          | Dp   | Grouped panel row height                            | 38.dp   |
| TitleWidth              | Dp   | Row title width                                     | 48.dp   |
| PanelBorderWidth        | Dp   | Border of the non-blur grouped panel                | 0.67.dp |
| TextSize                | Dp   | Title and option size (dp, not scaled with font scale) | 12.dp |
| OptionPaddingHorizontal | Dp   | Option text side padding                            | 4.dp    |

| Function              | Return Type         | Description                            |
| --------------------- | ------------------- | -------------------------------------- |
| cameraTopMenuColors() | CameraTopMenuColors | The fixed light-on-dark camera palette |

## Colors Classes

### CameraTopMenuColors

| Property Name       | Type  | Description                                   | Default             |
| ------------------- | ----- | --------------------------------------------- | ------------------- |
| containerColor      | Color | Capsules and strips without material blur     | #7F000000           |
| panelContainerColor | Color | Grouped panel without material blur           | #141414             |
| panelBorderColor    | Color | Border of the grouped panel without blur      | #1AFFFFFF           |
| blurMixColor        | Color | Material blur mix color                       | 15% grey, 60% alpha |
| blurBlendColor      | Color | Material blur glow-overlay color              | 65% grey, 40% alpha |
| titleColor          | Color | Row titles                                    | #88FFFFFF           |
| optionColor         | Color | Options                                       | White               |
| selectedOptionColor | Color | Selected option                               | #FB822A             |
