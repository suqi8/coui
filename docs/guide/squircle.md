# Squircle Shapes

`coui-squircle` is a standalone library that provides squircle (continuous-corner)
rounded-rectangle shapes for Compose Multiplatform. It exposes `Modifier` extensions
for fill / clip / border and a low-level `Path` builder. The library supports
Android, Desktop (JVM), iOS, macOS, and Web (WasmJs/Js).

::: tip Squircle vs. RoundedCornerShape
A `RoundedCornerShape` corner is a pure quarter circle. A squircle corner here is
the ColorOS smooth round rect corner (Oplus `Path.addSmoothRoundRect`): a Lamé
superellipse quarter `|x|^n + |y|^n = r^n` whose exponent `n` is the corner
`weight`, tangent to both straight sides at the corner radius `r`. Weight `2` is
the circular arc; larger weights keep the curve on the straight sides longer and
square the corner off. Visual differences are most apparent at moderate to large
radii.
:::

::: warning Platform floor
Shader-backed modifiers (`squircleBackground`, `squircleSurface`,
`squircleClip`, `squircleBorder`) require Android API 33+. On older API levels
they fall back to the matching `RoundedCornerShape` rendering — no crash, no
manual gating. See [Global Toggle](#global-toggle) for the runtime opt-out.
:::

## Setup

Add the `coui-squircle` dependency to your project:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-squircle:<version>")
        }
    }
}
```

For Android-only projects:

```kotlin
dependencies {
    implementation("io.github.suqi8.coui.kmp:coui-squircle-android:<version>")
}
```

## Platform Support

| Platform      | Shader-backed modifiers                  | Path-based APIs |
| ------------- | ---------------------------------------- | --------------- |
| Android       | API 33+ (else falls back to rounded arc) | All API levels  |
| Desktop (JVM) | Supported                                | Supported       |
| iOS / macOS   | Supported                                | Supported       |
| WasmJs / Js   | Supported                                | Supported       |

Fallback is automatic — call sites do not branch. The path-based
`Path.addSquircleRect` works on every platform but accepts an explicit
`squircleEnabled` flag so callers can mirror the modifier fallback when they
need consistency.

## Basic Usage

```kotlin
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.squircle.squircleBackground

Box(
    modifier = Modifier
        .size(120.dp, 80.dp)
        .squircleBackground(
            color = COUITheme.colorScheme.primaryVariant,
            cornerRadius = 20.dp,
        ),
)
```

## Variants

### squircleBackground — fill only

Fills the squircle silhouette with a solid color. Descendants are **not**
clipped to the corner — useful when you want to paint a background but still
let children overflow or paint their own corners.

```kotlin
Modifier.squircleBackground(
    color = COUITheme.colorScheme.primaryVariant,
    cornerRadius = 16.dp,
)
```

### squircleSurface — fill + clip

Drop-in replacement for `Modifier.clip(RoundedCornerShape(r)).background(color)`
with a squircle outline. Children are clipped to the squircle silhouette in
the same offscreen layer as the fill, so there is no double-pass cost.

```kotlin
Modifier.squircleSurface(
    color = COUITheme.colorScheme.primaryVariant,
    cornerRadius = 16.dp,
)
```

### squircleClip — clip only

Clips children to the squircle silhouette without drawing a fill. Useful when
the fill is supplied by something else (an `Image`, another modifier chain,
etc.).

```kotlin
Modifier.squircleClip(cornerRadius = 16.dp)
```

::: warning Offscreen cost
`squircleSurface` and `squircleClip` each open one offscreen GPU layer per
node. The shader output is cached as long as size and parameters are stable;
pass constants where you can and avoid driving `cornerRadius` from a
continuously animating state if you don't have to.
:::

### squircleBorder — stroke only

Draws a squircle-outlined stroke around the layout, inset by half the stroke
width so it lines up with a same-radius `squircleBackground` / `squircleSurface`.
Path-based (no shader required); cheaper than the shader variants.

```kotlin
import io.github.suqi8.coui.kmp.squircle.squircleBorder

Modifier.squircleBorder(
    width = 1.dp,
    color = COUITheme.colorScheme.outline,
    cornerRadius = 16.dp,
)
```

## Per-corner Radii

Every shader-backed modifier has a per-corner overload that matches
`RoundedCornerShape`'s parameter ordering (`topStart`, `topEnd`, `bottomEnd`,
`bottomStart`):

```kotlin
Modifier.squircleSurface(
    color = COUITheme.colorScheme.secondaryVariant,
    topStart = 24.dp,
    topEnd = 24.dp,
    bottomEnd = 0.dp,
    bottomStart = 0.dp,
)
```

Mix and match to produce asymmetric shapes (a "card stuck to the top of the
screen", an inset chip with one squared edge, and so on).

## Absolute Corners (layout-direction-agnostic)

`absoluteSquircleBackground`, `absoluteSquircleClip`, and `absoluteSquircleSurface`
mirror the per-corner overloads but take physical corners (`topLeft`, `topRight`,
`bottomRight`, `bottomLeft`, clockwise from top-left) that are **not** flipped by
`LocalLayoutDirection` — the same relationship `AbsoluteRoundedCornerShape` has to
`RoundedCornerShape`. Reach for these when corners must anchor to physical sides
regardless of RTL/LTR (e.g. a transition reveal tied to a swipe edge).

```kotlin
Modifier.absoluteSquircleSurface(
    color = COUITheme.colorScheme.surface,
    topLeft = 24.dp,
    topRight = 24.dp,
    bottomRight = 0.dp,
    bottomLeft = 0.dp,
)
```

## Customizing the Curve

`weight` is the superellipse exponent of every corner, the same value ColorOS
passes to `addSmoothRoundRect`. The corner always meets the straight sides at
`cornerRadius`; the weight only changes how square the curve between them is.

| Parameter | Default | Range    | Effect                                                                                  |
| --------- | :-----: | -------- | --------------------------------------------------------------------------------------- |
| `weight`  |  `2.5`  | `[1, 8]` | Superellipse exponent. `2` is a circular arc; `3` is squarer; below `2`, a soft capsule. |

ColorOS 17 uses these values, exposed on `COUIRoundCorner` in `coui-ui`:

| Weight | COUI source                       | Used by                                                                   |
| :----: | --------------------------------- | ------------------------------------------------------------------------- |
| `1.0`  | `FULL_RRECT_G2_WEIGHT`            | full-round capsules: capsule buttons, chips, floating toolbars, top tips  |
| `2.0`  | circular                          | base `coui_round_corner_*` radii, circles                                 |
| `2.5`  | `weight_16_1_normal`              | card lists, input cards, popup menus (`*_radius_normal_16_1`, e.g. 24dp)  |
| `3.0`  | `weight_16_1_large`               | buttons, card views, tooltips, alert dialogs, bottom sheets (45dp)        |

```kotlin
Modifier.squircleSurface(
    color = COUITheme.colorScheme.surface,
    cornerRadius = 45.dp,
    weight = 3f,
)
```

Reference the default via `SquircleDefaults.Weight`. On a capsule (`cornerRadius`
equal to half the height) a larger weight squares the round ends.

### Soft capsule (weight below 2)

A weight below `2` reproduces the Oplus G2 `superN < 2` branch: the outline blends
from the circular capsule (`weight = 2`) towards the Oplus soft capsule, reached at
`weight <= 1.005` (`SquircleDefaults.CapsuleWeight` = 1.0, the weight ColorOS gives
every fully rounded `addSmoothRoundRect`). The soft capsule takes its radius from
half the shorter side and joins each end cap to the straight sides through a smooth
maximum instead of a tangent arc, so the joint sits up to about 0.02 r inside the
circular capsule. Like `OplusPathSmooth`, this only applies to a **fully rounded**
rect (four equal radii of at least half the shorter side); any other rect draws a
weight below 2 as 2. Pass a radius that always covers half the height (ColorOS uses
`couiRoundCornerFULL`, 72dp):

```kotlin
Modifier.squircleSurface(
    color = COUITheme.colorScheme.primary,
    cornerRadius = 72.dp,
    weight = SquircleDefaults.CapsuleWeight,
)
```

The shader evaluates the native distance function; `addSquircleRect` traces its
zero contour (within 0.001 px).

::: info Migrating from `extension`
Earlier versions took an `extension` (corner-tile multiplier, default 1.2819)
and drew a cubic corner that reached past `cornerRadius`. That parameter is now
`weight`; `SquircleDefaults.Extension*` remain only as deprecated constants.
:::

## Path API

When you need a squircle outline outside the modifier pipeline (a `clipPath`
reveal that rebuilds per frame, a custom `Canvas` draw, an `Outline` for a
non-shader Shape, etc.), build the path directly:

```kotlin
import androidx.compose.ui.graphics.Path
import io.github.suqi8.coui.kmp.squircle.addSquircleRect

val path = Path().apply {
    addSquircleRect(
        width = sizePx.width,
        height = sizePx.height,
        cornerRadius = 20.dp.toPx(),
    )
}
```

Each corner is a few cubic Bézier segments fitted to the superellipse (within
0.03% of the radius). For static fills and clips prefer the shader-backed
modifiers — they evaluate the exact superellipse distance per pixel.

## Legacy COUI Path

ColorOS 17 still draws a few components with COUI's Java fallback path
`COUIShapePath.getRoundRectPath` instead of the superellipse: the hint red dot
(`Badge`), `COUICodeInputView` (`CodeTextField`) and `COUIRecommendedDrawable`
(`RecommendedPreference`). `addLegacySquircleRect` ports it exactly — three cubics
per corner with control points at fixed percentages of the radius, leaving the
straight side at 128.19 % of the radius (shrinking to 110.4 % once the radius
reaches 90 % of the shorter half-side). `LegacySquircleShape` wraps it as a
`Shape` for `background` / `clip` / `border`:

```kotlin
import io.github.suqi8.coui.kmp.squircle.LegacySquircleShape

Modifier.background(COUITheme.colorScheme.surface, LegacySquircleShape(8.dp))
```

It is path based (no shader) and ignores `LocalSquircleEnabled`.

## Global Toggle

`LocalSquircleEnabled` (default `true`) gates every shader-backed squircle
modifier. Set it to `false` to force the `RoundedCornerShape` fallback at
runtime — useful for visual preferences or A/B comparisons:

```kotlin
import io.github.suqi8.coui.kmp.squircle.LocalSquircleEnabled

CompositionLocalProvider(LocalSquircleEnabled provides userPrefersRoundedRects) {
    AppContent()
}
```

`isSquircleEnabled()` combines `LocalSquircleEnabled.current` with the runtime
shader-support check. Use it inside `@Composable` to keep mixed shader+path
visuals in sync — capture the boolean and pass it to `addSquircleRect`:

```kotlin
@Composable
fun OutlinedSurface(cornerRadius: Dp) {
    val squircleEnabled = isSquircleEnabled()
    val path = remember { Path() }
    Box(
        Modifier
            .squircleBackground(COUITheme.colorScheme.surface, cornerRadius)
            .drawWithContent {
                drawContent()
                path.rewind()
                path.addSquircleRect(
                    width = size.width,
                    height = size.height,
                    cornerRadius = cornerRadius.toPx(),
                    squircleEnabled = squircleEnabled,
                )
                drawPath(path, COUITheme.colorScheme.outline, style = Stroke(1.dp.toPx()))
            },
    )
}
```

## Properties

### squircleBackground / squircleSurface / squircleClip

| Parameter      | Type    | Description                                              | Default                         |
| -------------- | ------- | -------------------------------------------------------- | ------------------------------- |
| `color`        | `Color` | Fill color (omit on `squircleClip`)                      | -                               |
| `cornerRadius` | `Dp`    | Uniform corner radius                                    | -                               |
| `topStart`     | `Dp`    | Per-corner radius (per-corner overload)                  | -                               |
| `topEnd`       | `Dp`    | Per-corner radius (per-corner overload)                  | -                               |
| `bottomEnd`    | `Dp`    | Per-corner radius (per-corner overload)                  | -                               |
| `bottomStart`  | `Dp`    | Per-corner radius (per-corner overload)                  | -                               |
| `weight`       | `Float` | Superellipse exponent, clamped to `[1, 8]`; below 2 the soft capsule (fully rounded rects only) | `SquircleDefaults.Weight` = 2.5     |

### squircleBorder

| Parameter      | Type    | Description                                  | Default                         |
| -------------- | ------- | -------------------------------------------- | ------------------------------- |
| `width`        | `Dp`    | Stroke width                                 | -                               |
| `color`        | `Color` | Stroke color                                 | -                               |
| `cornerRadius` | `Dp`    | Uniform corner radius                        | -                               |
| `weight`       | `Float` | Superellipse exponent                        | `SquircleDefaults.Weight`       |

### Path.addSquircleRect

| Parameter         | Type      | Description                                                        | Default                         |
| ----------------- | --------- | ------------------------------------------------------------------ | ------------------------------- |
| `width`           | `Float`   | Path width in pixels                                               | -                               |
| `height`          | `Float`   | Path height in pixels                                              | -                               |
| `cornerRadius`    | `Float`   | Corner radius in pixels                                            | -                               |
| `weight`          | `Float`   | Superellipse exponent                                              | `SquircleDefaults.Weight`       |
| `squircleEnabled` | `Boolean` | When `false`, appends a plain rounded rect of the same dimensions  | `true`                          |

### Path.addLegacySquircleRect / LegacySquircleShape

| Parameter      | Type      | Description                                                   | Default |
| -------------- | --------- | ------------------------------------------------------------- | ------- |
| `width`        | `Float`   | Path width in pixels                                          | -       |
| `height`       | `Float`   | Path height in pixels                                         | -       |
| `cornerRadius` | `Float`   | Corner radius in pixels (`Dp` on `LegacySquircleShape`); not clamped, like COUI | -       |
| `left` / `top` | `Float`   | Offset of the rect                                            | `0f`    |
| `topLeft` ... `bottomLeft` | `Boolean` | Which corners are rounded                         | `true`  |

### SquircleDefaults

| Constant       | Type    | Description                          | Value  |
| -------------- | ------- | ------------------------------------ | -----: |
| `Weight`       | `Float` | Default weight (`weight_16_1_normal`) | `2.5`  |
| `WeightMin`    | `Float` | Lower bound (soft capsule)           | `1.0`  |
| `CapsuleWeight`| `Float` | Full-round capsule (`FULL_RRECT_G2_WEIGHT`) | `1.0`  |
| `WeightMax`    | `Float` | Upper bound                          | `8.0`  |
