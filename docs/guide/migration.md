# Migrating from Miuix

COUI is based on [Miuix](https://github.com/compose-miuix-ui/miuix) and keeps the same overall API surface, so migrating an existing Miuix project is mostly a mechanical rename: swap the dependency coordinates, update the package prefix, and rename a handful of `Miuix*` symbols to their `COUI*` counterparts. This page walks through every step.

::: tip
After migration your app keeps working with the same component set — but the visuals change from Xiaomi HyperOS to ColorOS design (colors, corner radii, motion curves, press feedback). See [Behavioral and visual differences](#behavioral-and-visual-differences) below.
:::

## 1. Update dependencies

Replace the Miuix Maven coordinates (`top.yukonga.miuix.kmp`) with the COUI ones (`io.github.suqi8.coui.kmp`). Every module has a 1:1 counterpart:

| Miuix module | COUI module |
| :-- | :-- |
| `top.yukonga.miuix.kmp:miuix-ui` | `io.github.suqi8.coui.kmp:coui-ui` |
| `top.yukonga.miuix.kmp:miuix-preference` | `io.github.suqi8.coui.kmp:coui-preference` |
| `top.yukonga.miuix.kmp:miuix-icons` | `io.github.suqi8.coui.kmp:coui-icons` |
| `top.yukonga.miuix.kmp:miuix-blur` | `io.github.suqi8.coui.kmp:coui-blur` |
| `top.yukonga.miuix.kmp:miuix-squircle` | `io.github.suqi8.coui.kmp:coui-squircle` |
| `top.yukonga.miuix.kmp:miuix-nav` | `io.github.suqi8.coui.kmp:coui-nav` |
| `top.yukonga.miuix.kmp:miuix-shader` | `io.github.suqi8.coui.kmp:coui-shader` |
| `top.yukonga.miuix.kmp:miuix-core` | `io.github.suqi8.coui.kmp:coui-core` |

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-ui:1.1.0")
            // Optional modules, same pattern:
            implementation("io.github.suqi8.coui.kmp:coui-preference:1.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-icons:1.1.0")
        }
    }
}
```

COUI additionally ships `io.github.suqi8.coui.kmp:coui-navigation3-ui`, an integration for androidx Navigation 3 — see [Navigation 3](../guide/navigation3.md).

## 2. Update the package prefix

Every package moves from `top.yukonga.miuix.kmp.*` to `io.github.suqi8.coui.kmp.*` with the sub-package structure unchanged (`basic`, `theme`, `utils`, `icon`, `extra`/`preference`, `blur`, `nav`, …). A single find-and-replace over your sources handles it:

```shell
# macOS / Linux (run from your project root)
grep -rl "top.yukonga.miuix.kmp" src | xargs sed -i 's/top\.yukonga\.miuix\.kmp/io.github.suqi8.coui.kmp/g'
```

On Windows, use your IDE's "Replace in Files" (`Ctrl+Shift+R` in Android Studio / IntelliJ) with `top.yukonga.miuix.kmp` → `io.github.suqi8.coui.kmp`.

## 3. Renamed APIs

All `Miuix*`-prefixed symbols are renamed to `COUI*`. The mapping is exactly 1:1:

| Miuix | COUI |
| :-- | :-- |
| `MiuixTheme` (composable + object) | `COUITheme` |
| `MiuixIndication` | `COUIIndication` |
| `MiuixPopupUtils` | `COUIPopupUtils` |
| `MiuixPopupHost` | `COUIPopupHost` |
| `MiuixScrollBehavior` | `COUIScrollBehavior` |
| `MiuixIcons` | `COUIIcons` |
| `MiuixOverscrollEffect` | `COUIOverscrollEffect` |
| `MiuixOverscrollFactory` | `COUIOverscrollFactory` |
| `NavTransitions.MiuixDefault` | `NavTransitions.COUIDefault` |
| `BadgedBox` | `BadgeBox` (signature also changed, see below) |

A second find-and-replace of `MiuixTheme` → `COUITheme`, `MiuixIcons` → `COUIIcons`, etc. covers these. The `folmeSpring` easing helper (formerly in `MiuixEasing.kt`, now `COUIEasing.kt`) keeps its name — no code change needed there.

## 4. Changed APIs

A few components were rebuilt for ColorOS and their signatures differ.

### Badge

Miuix's `Badge` takes free-form content; COUI's `Badge` is count-based and picks its form automatically — a dot when `count <= 0`, a number capsule for a positive count, and an ellipsis form for large counts. `BadgedBox` is renamed to `BadgeBox` and gains an `overhang` parameter controlling how far the badge sticks out of the content.

```kotlin
// Miuix
BadgedBox(badge = { Badge { Text("8") } }) { Icon(...) }

// COUI
BadgeBox(badge = { Badge(count = 8) }) { Icon(...) }
```

### TextField

COUI's `TextField` adds a `backgroundMode: TextFieldMode` parameter with three ColorOS forms — `Rectangle` (stroked rounded rectangle), `Line` (bottom underline, the default) and `None` (bare text). Related default changes and additions:

- `useLabelAsPlaceholder` now defaults to `true` (ColorOS fields use a hint instead of a floating label; Miuix defaulted to `false`).
- New parameters: `justShowFocusLine`, `isError`, `maxCount` (character counter), `showClearButton`, `showPasswordToggle`.
- `insideMargin` and `textStyle` defaults now depend on the selected mode.

For the ColorOS "card input" with a separate title above the field, use the new [InputView](../components/inputview.md) instead.

### PressFeedbackType

`PressFeedbackType` (used by `Card` and other pressables) gains a new `Tint` value — the COUI press-mask feedback. Any exhaustive `when` over it stops compiling until you add the new branch (or an `else`):

```kotlin
when (pressFeedbackType) {
    PressFeedbackType.None -> ...
    PressFeedbackType.Sink -> ...
    PressFeedbackType.Tilt -> ...
    PressFeedbackType.Tint -> ... // new in COUI
}
```

### Dialog content padding

`OverlayDialog` / `WindowDialog` deliberately apply **no padding** to the `content` slot, so button bars and dividers can span the full dialog width (matching ColorOS). If your Miuix dialogs relied on built-in content insets, wrap your content in its own padding — `24.dp` horizontal matches the COUI title/summary insets:

```kotlin
OverlayDialog(show = showDialog, title = "Title", onDismissRequest = { ... }) {
    Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp)) {
        // dialog body
    }
}
```

If you show many dialogs, wrap this once in your own `AppDialog` composable instead of padding every call site.

### TopAppBar

The navigation icon already carries a built-in `navigationIconPadding` of `16.dp`. If you added your own start padding around the back button under Miuix, remove it — otherwise the icon sits too far from the edge.

### TabRow

`TabRow` is visually rebuilt as ColorOS segment buttons. The signature is compatible, with small differences: `itemSpacing` defaults to `0.dp` (was `9.dp`; `TabRowWithContour` was `5.dp`), the plain `TabRow` defaults to a transparent background and accepts an `indication` parameter, and `TabRowWithContour` gains a `contourPadding` parameter.

### Switch

`Switch` gains `isLoading: Boolean = false` — while loading, the thumb shows the COUI spinner and the switch cannot be toggled (mirroring ColorOS async settings).

## 5. Migrating from an older Miuix (the `extra` package)

Older Miuix releases shipped a `top.yukonga.miuix.kmp.extra` package with `Super*`-prefixed components. Both Miuix and COUI have since split it up; if your project still uses `extra.*`, map as follows:

| Old Miuix (`extra.*`) | COUI |
| :-- | :-- |
| `SuperDialog` | `overlay.OverlayDialog` (or `window.WindowDialog`) — `show` is now a plain `Boolean` instead of `MutableState<Boolean>` |
| `SuperCheckbox` | `coui-preference`: `preference.CheckboxPreference` |
| `SuperSwitch` | `coui-preference`: `preference.SwitchPreference` |
| `SuperArrow` | `coui-preference`: `preference.ArrowPreference` |
| `SuperDropdown` | `coui-preference`: `preference.OverlayDropdownPreference` / `WindowDropdownPreference` |
| `SuperSpinner` | `coui-preference`: `preference.OverlaySpinnerPreference` / `WindowSpinnerPreference` |
| `CheckboxLocation` | `coui-preference`: `preference.CheckboxLocation` |
| `WindowBottomSheet` | `window.WindowBottomSheet` (package changed only) |

The preference components live in the separate `coui-preference` artifact — add it to your dependencies if you only had `miuix` before. Old direct styling parameters (such as `TextField`'s `backgroundColor`) have generally moved into the corresponding `Defaults.…Colors(...)` factories.

## 6. Custom color schemes

The `Colors` constructor keeps Miuix's parameter order, so a fully custom scheme built with positional arguments carries over unchanged and keeps compiling. That said, consider deleting your custom palette entirely — `lightColorScheme()` / `darkColorScheme()` now return the native ColorOS 16 palette, which is usually what a COUI app wants; a real-world migration removed ~220 lines of custom colors this way.

## 7. Behavioral and visual differences

The API stays familiar, but every component is rethemed to ColorOS 16:

- **Colors**: the color scheme values follow ColorOS (accent, container and surface colors differ from HyperOS); keep using `COUITheme.colorScheme.*` and your code picks the new values up automatically.
- **Shape and metrics**: corner radii, paddings and component sizes follow the ColorOS specification.
- **Motion**: animation curves and press feedback (scale/tint) follow COUI's motion design.
- **Dynamic color**: Monet / dynamic color is still supported through `ThemeController` (`ColorSchemeMode.MonetSystem`, `MonetLight`, `MonetDark`), exactly as before — see [Theme System](../guide/theme.md).

## 8. New components

Migrating also unlocks components that do not exist in Miuix:

- [Chip](../components/chip.md) — capsule-shaped checkable filter tag.
- [TopTips](../components/toptips.md) — tip banner shown at the top of a page.
- [Stepper](../components/stepper.md) — minus/plus integer stepper.
- [InputView](../components/inputview.md) — ColorOS card input with title and error caption.
- [CodeTextField](../components/codetextfield.md) — verification-code input with per-digit cells.
- [DatePicker](../components/datepicker.md) — year/month/day wheel picker.
- [TimePicker](../components/timepicker.md) — hour/minute (and AM/PM) wheel picker.
- [FullPageStatement](../components/fullpagestatement.md) — first-launch user agreement page.
- [ColorSwatchPicker](../components/colorswatchpicker.md) — swatch-based color picker.
- [LoadingButton](../components/loadingbutton.md) — button with built-in loading dots.
- [LoadingDialog](../components/loadingdialog.md) — rotating-spinner progress dialog (Overlay/Window variants).
- [SecurityDialog](../components/securitydialog.md) — security statement dialog (Overlay/Window variants).
- New preferences: [ButtonPreference](../components/buttonpreference.md), [ListPreference / MultiSelectListPreference](../components/listpreference.md), [MarkPreference](../components/markpreference.md), [RecommendedPreference](../components/recommendedpreference.md), [SwitchLoadingPreference](../components/switchloadingpreference.md).
