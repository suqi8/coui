# Badge

A hint red dot mirroring ColorOS's COUIHintRedDot, with three forms: a plain 6dp dot, a 16dp-high number capsule that widens with the digit count, and a three-dot ellipsis for counts of 1000 and above. `BadgeBox` anchors a badge at the top end corner of an icon like COUIRedDotFrameLayout.

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Badge
import io.github.suqi8.coui.kmp.basic.BadgeBox
import io.github.suqi8.coui.kmp.basic.BadgeDefaults
```

## Basic Usage

```kotlin
Badge()                // plain dot
Badge(count = 8)       // number capsule
Badge(count = 1000)    // three-dot ellipsis (1000+)
Badge(stroke = true)   // white outline for colored surfaces
```

## Badge Forms

### Dot Badge

Counts of 0 or below (the default) render a plain 6dp dot (COUI POINT_ONLY_MODE):

```kotlin
Badge()
```

### Count Badge

Counts from 1 to 999 render a 16dp-high capsule that widens with the digit count (16dp under 10, 20dp under 100, 26dp under 1000). Count changes ease the capsule width over 517ms while the old and new numbers crossfade over 150ms:

```kotlin
var unread by remember { mutableIntStateOf(8) }

Badge(count = unread)
```

### Overflow Ellipsis

Counts of 1000 and above render a 20dp-wide capsule with a three-dot ellipsis (COUI `red_dot_more`):

```kotlin
Badge(count = 1000)
```

### Stroke Outline

`stroke = true` draws a 1dp white outline around the dot or capsule, for badges placed on colored surfaces (COUI POINT_ONLY_MODE_STROKE / POINT_NUM_MODE_STROKE):

```kotlin
Badge(stroke = true)              // dot with outline
Badge(count = 99, stroke = true)  // capsule with outline
```

## Anchoring with BadgeBox

`BadgeBox` anchors a badge at the top end corner of its content. A positive `overhang` lets the badge stick out of the corner by that amount and grows the layout accordingly (COUI rectangular anchors such as icons):

```kotlin
BadgeBox(
    badge = { Badge(count = 8) },
    overhang = BadgeDefaults.CountOverhang,
) {
    Icon(
        imageVector = COUIIcons.Settings,
        contentDescription = "Settings",
    )
}
```

A negative `overhang` insets the badge inside the corner instead (COUI circular anchors such as avatars):

```kotlin
BadgeBox(
    badge = { Badge() },
    overhang = (-2).dp,
) {
    Image(
        painter = avatarPainter,
        contentDescription = "Avatar",
        modifier = Modifier.size(40.dp).clip(CircleShape),
    )
}
```

## Properties

### Badge

| Property    | Type        | Description                                       | Default Value               | Required |
| ----------- | ----------- | ------------------------------------------------- | --------------------------- | -------- |
| modifier    | Modifier    | Modifier applied to the badge                     | Modifier                    | No       |
| count       | Int         | Number shown in the badge; values <= 0 show a dot | 0                           | No       |
| stroke      | Boolean     | Whether to draw a white outline                   | false                       | No       |
| colors      | BadgeColors | Color configuration                               | BadgeDefaults.badgeColors() | No       |
| dotDiameter | Dp          | Diameter of the plain dot form                    | BadgeDefaults.DotDiameter   | No       |
| height      | Dp          | Height of the number capsule form                 | BadgeDefaults.Height        | No       |

### BadgeBox

| Property | Type       | Description                                          | Default Value            | Required |
| -------- | ---------- | ---------------------------------------------------- | ------------------------ | -------- |
| badge    | @Composable () -> Unit | The badge to anchor                      | -                        | Yes      |
| modifier | Modifier   | Modifier applied to the layout                       | Modifier                 | No       |
| overhang | Dp         | How far the badge extends beyond the top end corner  | BadgeDefaults.DotOverhang | No      |
| content  | @Composable () -> Unit | The anchor content, typically an icon    | -                        | Yes      |

### BadgeDefaults

| Constant                | Type | Default Value |
| ----------------------- | ---- | ------------- |
| DotDiameter             | Dp   | 6.dp          |
| Height                  | Dp   | 16.dp         |
| SmallWidth              | Dp   | 16.dp         |
| MediumWidth             | Dp   | 20.dp         |
| LargeWidth              | Dp   | 26.dp         |
| TextSize                | Dp   | 10.dp         |
| StrokeWidth             | Dp   | 1.dp          |
| EllipsisDotDiameter     | Dp   | 2.dp          |
| EllipsisSpacing         | Dp   | 2.dp          |
| DotOverhang             | Dp   | 2.dp          |
| CountOverhang           | Dp   | 3.dp          |
| ScaleAnimDurationMillis | Int  | 520           |

### `badgeColors()` factory

| Parameter      | Type  | Default                                                          |
| -------------- | ----- | ---------------------------------------------------------------- |
| containerColor | Color | COUI additional red (#EB3B2F in light themes, #EB493D in dark)   |
| contentColor   | Color | Color.White                                                      |
| strokeColor    | Color | Color.White                                                      |

## Behavior

- `count <= 0` shows the plain dot; 1..9 a 16dp capsule, 10..99 a 20dp capsule, 100..999 a 26dp capsule; 1000 and above a 20dp capsule with a three-dot ellipsis (COUI `red_dot_more`).
- Count changes animate like COUIHintRedDot: the capsule width eases over 517ms (COUIMoveEaseInterpolator) while the old and new numbers crossfade over 150ms.
- The count text renders at a density-scaled 10dp (sans-serif medium), ignoring the user font scale like the original.
- `stroke = true` draws a 1dp white outline; the dot form shrinks its red core by the stroke width, the capsule form insets the red fill inside the white outline.
- A positive `BadgeBox` overhang lets the badge stick out beyond the content's top end corner and grows the layout accordingly (COUI rectangular anchors); a negative overhang insets the badge inside the corner (COUI circular anchors such as avatars).
- To animate badge visibility like COUIHintRedDot's show / hide scale animation, wrap it in `AnimatedVisibility` with `scaleIn` / `scaleOut` over `BadgeDefaults.ScaleAnimDurationMillis`.
