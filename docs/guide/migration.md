# Migrating from Miuix

COUI is based on [Miuix](https://github.com/compose-miuix-ui/miuix) and keeps the same overall API surface, so migrating an existing Miuix project is mostly a mechanical rename: swap the dependency coordinates, update the package prefix, rename a handful of `Miuix*` symbols to their `COUI*` counterparts, and rename the colour and text style slots to the official COUI names. This page walks through every step for COUI 2.0, the ColorOS 17 (COUI 17) design.

::: tip
After migration your app keeps working with the same component set — but the visuals change from Xiaomi HyperOS to the ColorOS 17 design (colors, corner radii, motion curves, press feedback, material blur). See [Behavioral and visual differences](#behavioral-and-visual-differences) below.
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
            implementation("io.github.suqi8.coui.kmp:coui-ui:2.1.0")
            // Optional modules, same pattern:
            implementation("io.github.suqi8.coui.kmp:coui-preference:2.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-icons:2.1.0")
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

### Colour and text style slots

COUI names the `Colors` and `TextStyles` slots after the official COUI attributes (`couiColorXxx` → `xxx`, `couiTextXxx` → `xxx`), while Miuix uses Material-like names (`onBackground`, `surfaceContainer`, `main`, `title4`, …). There are no aliases, so every `COUITheme.colorScheme.*` and `COUITheme.textStyles.*` reference needs its new name. Several Miuix slots merge into one COUI slot:

| Old color slot | New color slot |
| :------------- | :------------- |
| `onBackground`, `onSurface`, `onSurfaceContainer`, `onSecondaryVariant` | `labelPrimary` |
| `onBackgroundVariant`, `onSurfaceSecondary` | `labelSecondary` |
| `onSurfaceVariantSummary`, `onSurfaceContainerVariant` | `labelSecondaryVariant` |
| `onSurfaceVariantActions`, `onSurfaceContainerHigh`, `sliderKeyPoint` | `labelTertiary` |
| `disabledOnSurface`, `disabledOnSecondaryVariant` | `disabledNeutral` |
| `disabledPrimary`, `disabledPrimaryButton`, `disabledPrimarySlider` | `disable` |
| `onSecondary`, `sliderKeyPointForeground` | `labelOnColor` |
| `onTertiaryContainer` | `primaryText` |
| `tertiaryContainer` | `secondary` |
| `surface` | `backgroundWithCard` |
| `background` | `surface` for dialogs, bottom sheets, popups and other floating panels; `backgroundElevated` elsewhere |
| `surfaceContainer` | `cardBackground` |
| `surfaceContainerHigh` | `cardPressed` |
| `dividerLine` | `divider` |
| `windowDimming` | `mask` |
| `textFieldError` | `errorTextBg` |
| `primaryVariant` | Removed; use `primary` |
| `primaryContainer` | Removed; use `containerTheme` |
| `onPrimaryVariant`, `onPrimaryContainer` | Removed; use `labelOnColor` |
| `surfaceVariant` | Removed; use `card` |
| `errorContainer`, `onErrorContainer`, `disabledOnPrimary`, `disabledSecondary`, `disabledOnSecondary`, `onSecondaryContainer`, `secondaryContainerVariant`, `onSecondaryContainerVariant`, `tertiaryContainerVariant`, `onSurfaceContainerHighest`, `secondary` (the grey `#E5E5E5` / `#757575`) | Removed |

| Old text style | New text style |
| :------------- | :------------- |
| `main`, `body1` | `bodyXL` |
| `paragraph` | `bodyL` |
| `body2` | `bodyM` |
| `button` | `buttonL` |
| `footnote1` | `bodyS` |
| `footnote2` | `bodyXS` |
| `headline1`, `headline2` | `headlineXS` |
| `subtitle` | `buttonM` |
| `title1` | `displayS` |
| `title2` | `displayXS` |
| `title3` | `headlineM` |
| `title4` | `headlineS` |

`background`, `surface` and `secondary` exist in both libraries but mean different things: in COUI, `background` is the page background, `surface` is the colour of dialogs, bottom sheets, popups and other floating panels, and `secondary` is the theme colour at 15% (light) / 25% (dark), not a grey. Rename `surface` before `background`, since `background` becomes `surface` in places, and check each use instead of keeping the name.

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

The horizontal padding of the navigation icon, the title and the actions follows the window width: 16dp on phones, 24dp on medium and 40dp on expanded windows (`TopAppBarDefaults.horizontalPadding()`). Remove any start padding you added around the back button under Miuix. The `SmallTopAppBar` title is 18dp Medium.

For a search box under a top bar, use `TopAppBarSearchBar` with `TopAppBar(searchState = …)`: the title fades away and the search bar rises to the top, like the ColorOS 17 Settings home. See [Searching in place](../components/topappbar.md#searching-in-place).

### TabRow

`TabRowWithContour` is visually rebuilt as ColorOS segment buttons: `itemSpacing` defaults to `0.dp` (was `5.dp`) and it gains a `contourPadding` parameter. The plain `TabRow` becomes the ColorOS sliding tab layout (48dp, text tabs with a 2dp underline indicator) and takes its colors from `TabRowDefaults.underlineColors()`. Its signature stays source compatible for named arguments, with new `enabled`, `horizontalMargin` and `pagerState` parameters and a `TabRow(items = ...)` overload for icon, red dot and disabled tabs, but `cornerRadius` now shapes the press mask, `itemSpacing` is the minimum gap between labels (responsive by default), and the indication defaults to `LocalIndication.current`.

### Switch

`Switch` gains `isLoading: Boolean = false` — while loading, the thumb shows the COUI spinner and the switch cannot be toggled (mirroring ColorOS async settings).

### Tooltip

Miuix's `PlainTooltip`, `RichTooltip` and `TooltipBox(positionProvider, …)` are replaced by the COUI 17 tooltips: `TooltipBox(state, tooltip)` with `Tooltip`, `IconTooltip` or `ImageTooltip`. COUI tooltips are shown from code and have no timeout by default. The [upgrading guide](./upgrading.md#tooltip-is-the-coui-17-tooltip) has the mapping and an example.

### Bottom sheets

`BottomSheetDefaults.insideMargin` is `0.dp`: COUI panels add no padding, so the content sets its own (`DpSize(24.dp, 0.dp)` restores the Miuix inset). The drag handle is hidden unless you pass `showDragHandle = true`, and on windows at least 600dp wide the sheet floats centered, at most 540dp wide.

### Dropdown and spinner types

The spinner types are merged into the dropdown ones: `SpinnerEntry`, `SpinnerColors`, `SpinnerDefaults` and `SpinnerItemImpl` become `DropdownItem`, `DropdownColors`, `DropdownDefaults` and `DropdownImpl`, and `DropdownItem(icon, title, summary)` becomes `DropdownItem(text = …, icon = …, summary = …)`.

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

The `Colors` slots use the official COUI names (`couiColorXxx` → `xxx`), not Miuix's, so a fully custom scheme has to be ported to the new parameter names with the table in [Colour and text style slots](#colour-and-text-style-slots). Consider deleting your custom palette entirely — `lightColorScheme()` / `darkColorScheme()` return the native ColorOS 17 palette, which is usually what a COUI app wants; a real-world migration removed ~220 lines of custom colors this way.

## 7. Behavioral and visual differences

The API stays familiar, but every component follows ColorOS 17 (COUI 17):

- **Colors**: the color scheme values follow ColorOS (accent, container and surface colors differ from HyperOS); keep using `COUITheme.colorScheme.*` with the new slot names.
- **Shape and metrics**: corner radii, paddings and component sizes follow the ColorOS specification, and many components adapt to medium and expanded windows (wider margins, floating bottom sheets, side-by-side navigation labels).
- **Motion**: animation curves and press feedback follow COUI's motion design. Lists use the COUI overscroll, a rubber band with a spring back and a scroll-edge haptic.
- **Material**: floating bars, buttons, search bars and popups draw the COUI 17 material (blur, glow, shadow edge). `LocalCOUIMaterialEffectEnabled provides false` turns it off app-wide, for example in battery saver.
- **List rows**: `CardListGroup` draws the dividers between rows itself, so do not add your own.
- **Dynamic color**: Monet / dynamic color is still supported through `ThemeController` (`ColorSchemeMode.MonetSystem`, `MonetLight`, `MonetDark`), exactly as before — see [Theme System](../guide/theme.md).

## 8. New components

Migrating also unlocks components that do not exist in Miuix:

- [TopAppBar search in place](../components/topappbar.md#searching-in-place) — the ColorOS 17 Settings search linked with the top bar.
- [CardListGroup](../components/cardlistgroup.md) — grouped rows in one card, with the COUI dividers.
- [Tooltip](../components/tooltip.md) — the COUI 17 bubbles with an arrow.
- [ListDialog](../components/listdialog.md), [ProgressDialog](../components/progressdialog.md), [InputDialog](../components/inputdialog.md), [UserStatementDialog](../components/userstatementdialog.md) and [IndividualStatementDialog](../components/individualstatementdialog.md).
- [FloatingToolbar](../components/floatingtoolbar.md) and [ChipGroup](../components/chipgroup.md).
- Camera controls from the ColorOS camera, such as [CameraShutterButton](../components/camerashutterbutton.md), [CameraModeBar](../components/cameramodebar.md), [CameraZoomDial](../components/camerazoomdial.md) and [ExposureBar](../components/exposurebar.md).

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

To see how a ColorOS 17 screen should be built before porting yours, sketch it in [COUI Canvas](./canvas.md) or open the <a href="/coui/example/" target="_blank">example app in the browser</a>.
