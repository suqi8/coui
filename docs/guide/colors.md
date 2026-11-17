# Color System

This page lists all colors in the COUI color scheme with their default light and dark values. Values are shown in ARGB hex.

## Accessing Colors

- Use `COUITheme.colorScheme.<name>` in composables.
- The colors shown below apply to non-Monet mode.

## Core and Component Slots

The primary and error colors, the state layers and the component-level tokens.

| Name | Role | Light | Dark |
|------|------|-------|------|
| `primary` | Primary color | `#FF0080FF` | `#FF1A8CFF` |
| `onPrimary` | Text on primary | `#FFFFFFFF` | `#FFFFFFFF` |
| `error` | Error color | `#FFDB382C` | `#FFFF6C61` |
| `onError` | Text on error | `#FFFFFFFF` | `#FFFFFFFF` |
| `disabledOnPrimaryButton` | Text on disabled primary button (COUI `coui_button_main_disable_text`) | `#8AFFFFFF` | `#29FFFFFF` |
| `secondaryVariant` | Secondary variant | `#14000000` | `#26FFFFFF` |
| `disabledSecondaryVariant` | Disabled secondary variant | `#14000000` | `#1AFFFFFF` |
| `secondaryContainer` | Secondary container | `#14000000` | `#26FFFFFF` |
| `surfaceContainerHighest` | Highest surface container | `#FFFFFFFF` | `#FF1E1E1E` |
| `outline` | Outlines/borders | `#26000000` | `#26FFFFFF` |
| `sliderBackground` | Slider background | `#1F000000` | `#33FFFFFF` |
| `dialogButton` | Dialog bar button fill (COUI `couiColorContainer8`) | `#14000000` | `#1AFFFFFF` |
| `dragHandle` | Bottom sheet drag handle (COUI `couiColorControls`) | `#29000000` | `#40FFFFFF` |
| `secondaryButton` | Secondary button fill (COUI `couiColorContainer8` over `couiColorBackgroundElevated`) | `#FFEBEBEB` | `#FF353535` |
| `disabledSecondaryButton` | Disabled secondary button fill (COUI `couiColorContainer8`) | `#14000000` | `#1AFFFFFF` |
| `disabledOnSecondaryButton` | Disabled secondary button label (COUI `coui_btn_secondary_text_color_disable`) | `#42000000` | `#28FFFFFF` |
| `transparentButton` | Transparent button fill (COUI `coui_button_translate_color`) | `#33FFFFFF` | `#33FFFFFF` |
| `onTransparentButton` | Transparent button label | `#FFFFFFFF` | `#FFFFFFFF` |
| `disabledOnTransparentButton` | Disabled transparent button label | `#4DFFFFFF` | `#4DFFFFFF` |
| `backgroundTop` | Topmost floating surface, e.g. Snackbar (COUI `couiColorBackgroundTop`) | `#FFFFFFFF` | `#FF333333` |
| `primaryTextOnPopup` | Accent text on floating surfaces, e.g. Snackbar action (COUI `couiColorPrimaryTextOnPopup`) | `#FF0080FF` | `#FF5C9DFF` |
| `press` | Pressed state layer (COUI `couiColorPress`), drawn by `COUIIndication` | `#1F000000` | `#33FFFFFF` |
| `hover` | Hovered state layer (COUI `couiColorHover`), drawn by `COUIIndication` | `#14000000` | `#26FFFFFF` |
| `focus` | Focused / selected list row layer (COUI `couiColorFocus`), drawn by `COUIIndication` | `#1F0080FF` | `#331A8CFF` |
| `disabledProgress` | Disabled linear progress bar fill (COUI `coui_progressbar_color_disabled`) | `#FFE6E6E6` | `#FF262626` |
| `disabledProgressBackground` | Disabled linear progress bar track (COUI `coui_progressbar_background_color_disabled`) | `#08000000` | `#33FFFFFF` |
| `disabledSliderThumb` | Thumb and on-progress key points of a disabled Slider (COUI `coui_seekbar_thumb_disable_color`) | `#8AFFFFFF` | `#29FFFFFF` |
| `disabledSliderKeyPoint` | Off-progress key points of a disabled Slider (COUI `coui_seekbar_inactive_mark_disable_color`) | `#42000000` | `#29FFFFFF` |
| `sliderThumbShadow` | Slider thumb shadow (COUI `coui_seekbar_thumb_shadow_color`) | `#1A000000` | `#40000000` |
| `disabledTextFieldBorder` | Border / underline of a disabled TextField (COUI `coui_textinput_stroke_color_disabled`) | `#19000000` | `#19FFFFFF` |
| `quarternaryNeutral` | Faintest neutral, e.g. disabled Checkbox / RadioButton rings (COUI `couiColorQuarternaryNeutral`) | `#14000000` | `#26FFFFFF` |
| `container8` | Translucent container fill, e.g. the unselected Chip (COUI `couiColorContainer8`) | `#14000000` | `#1AFFFFFF` |
| `disabledChipText` | Label of a disabled unselected Chip (COUI `chip_unchecked_text_disable_color`) | `#42000000` | `#29FFFFFF` |
| `focusOutline` | Keyboard focus outline, e.g. the Chip focus stroke (COUI `couiColorFocusOutline`) | `#660080FF` | `#665C9DFF` |

## COUI Semantic Colors

These slots carry the COUI `Theme.COUI` / `Theme.COUI.Dark` color attributes under their official names
(`couiColorXxx` → `xxx`).

| Name | COUI attribute | Light | Dark |
|------|----------------|-------|------|
| `primaryText` | `couiColorPrimaryText` (also `couiColorOnSecondary`) | `#FF0080FF` | `#FF5C9DFF` |
| `labelTheme` | `couiColorLabelTheme` | `#FF0080FF` | `#FF1A8CFF` |
| `link` | `couiColorLink` | `#FF0080FF` | `#FF1A8CFF` |
| `disable` | `couiColorDisable` | `#4D0080FF` | `#4D1A8CFF` |
| `containerTheme` | `couiColorContainerTheme` | `#FF0080FF` | `#FF1A8CFF` |
| `containerThemeHalftone` | `couiColorContainerThemeHalftone` | `#260080FF` | `#401A8CFF` |
| `secondary` | `couiColorSecondary` (the theme color at 15% / 25%) | `#260080FF` | `#401A8CFF` |
| `textColorHighlight` | `android:textColorHighlight` (`?couiColorSecondary`) | `#260080FF` | `#401A8CFF` |
| `errorDisabled` | `couiColorErrorDisabled` | `#4DDB382C` | `#4DFF6C61` |
| `errorTextBg` | `couiColorErrorTextBg` | `#4DE82A18` | `#FFEA3939` |
| `success` | `couiColorSuccess` | `#FF00BD13` | `#FF24B232` |
| `tips` | `couiColorTips` | `#FF0080FF` | `#FF1A8CFF` |
| `additional` | `couiColorAdditional` | `#FF0080FF` | `#FF1A8CFF` |
| `assist` | `couiColorAssist` | `#FF5C93E5` | `#FF5C93E5` |
| `background` | `couiColorBackground` | `#FFFFFFFF` | `#FF000000` |
| `surface` | `couiColorSurface` (dialog, bottom sheet and popup panels) | `#FFFFFFFF` | `#FF1E1E1E` |
| `backgroundElevated` | `couiColorBackgroundElevated` | `#FFFFFFFF` | `#FF1E1E1E` |
| `backgroundElevatedWithCard` | `couiColorBackgroundElevatedWithCard` | `#FFF0F1F2` | `#FF1E1E1E` |
| `backgroundWithCard` | `couiColorBackgroundWithCard` (`Scaffold` and page backgrounds) | `#FFF0F1F2` | `#FF000000` |
| `surfaceTop` | `couiColorSurfaceTop` | `#FFFFFFFF` | `#FF333333` |
| `surfaceWithCard` | `couiColorSurfaceWithCard` | `#FFF0F1F2` | `#FF1E1E1E` |
| `bar` | `couiColorBar` | `#FFFAFAFA` | `#FF1E1E1E` |
| `bottomBar` | `couiColorBottomBar` | `#FFFAFAFA` | `#FF1E1E1E` |
| `card` | `couiColorCard` | `#FFFFFFFF` | `#1AFFFFFF` |
| `cardBackground` | `couiColorCardBackground` | `#FFFFFFFF` | `#1AFFFFFF` |
| `cardPressed` | `couiColorCardPressed` | `#FFE6E6E6` | `#33FFFFFF` |
| `labelPrimary` | `couiColorLabelPrimary` | `#E6000000` | `#E6FFFFFF` |
| `labelPrimaryVariant` | `couiColorLabelPrimaryVariant` | `#B8000000` | `#B8FFFFFF` |
| `labelSecondary` | `couiColorLabelSecondary` | `#8A000000` | `#8AFFFFFF` |
| `labelSecondaryVariant` | `couiColorLabelSecondaryVariant` | `#66000000` | `#66FFFFFF` |
| `labelTertiary` | `couiColorLabelTertiary` | `#42000000` | `#4DFFFFFF` |
| `labelQuaternary` | `couiColorLabelQuaternary` | `#14000000` | `#26FFFFFF` |
| `labelOnColor` | `couiColorLabelOnColor` | `#FFFFFFFF` | `#FFFFFFFF` |
| `disabledNeutral` | `couiColorDisabledNeutral` | `#42000000` | `#4DFFFFFF` |
| `hintNeutral` | `couiColorHintNeutral` | `#42000000` | `#4DFFFFFF` |
| `container4` | `couiColorContainer4` | `#0A000000` | `#1AFFFFFF` |
| `container12` | `couiColorContainer12` | `#1F000000` | `#33FFFFFF` |
| `container16` | `couiColorContainer16` | `#29000000` | `#40FFFFFF` |
| `containerSolid` | `couiColorContainerSolid` | `#FFEBEBEB` | `#FF262626` |
| `fillSolid` | `couiColorFillSolid` | `#FFEBEBEB` | `#FF262626` |
| `fillThin` | `couiColorFillThin` | `#0A000000` | `#14FFFFFF` |
| `controls` | `couiColorControls` | `#29000000` | `#40FFFFFF` |
| `divider` | `couiColorDivider` | `#1F000000` | `#33FFFFFF` |
| `mask` | `couiColorMask` | `#33000000` | `#99000000` |
| `pressBackground` | `couiColorPressBackground` | `#14000000` | `#26FFFFFF` |
| `ripplePressBackground` | `couiColorRipplePressBackground` | `#99B4B4B4` | `#80FFFFFF` |

### Hue Palette

Each hue has the color (`couiColor<Hue>`), a `<hue>Variant` (`couiColor<Hue>Variant`) and a `container<Hue>` tint
(`couiColorContainer<Hue>`). The palette is fixed: theme color families and Monet do not change it.

| Hue | Color (light / dark) | Variant (light / dark) | Container (light / dark) |
|-----|----------------------|------------------------|--------------------------|
| `red` | `#EB3B2F` / `#EB493D` | `#E56E65` / `#CC615A` | `#EEDADC` / `#3A3030` |
| `orange` | `#FF7700` / `#F08222` | `#E58F45` / `#CC7F3D` | `#FAE7D6` / `#39332C` |
| `yellow` | `#FFB200` / `#E5A100` | `#E5AF2E` / `#C29427` | `#F6E7C6` / `#373530` |
| `green` | `#00BD13` / `#24B232` | `#4BBD57` / `#43A84E` | `#BADECA` / `#353E36` |
| `mint` | `#00CC88` / `#1AB883` | `#39BD91` / `#32A881` | `#BADEDB` / `#394342` |
| `cyan` | `#1DBACC` / `#25A9B8` | `#39AFBD` / `#329DA8` | `#BEDADD` / `#343E3D` |
| `azure` | `#00A1FF` / `#209DE5` | `#45ABE5` / `#3D98CC` | `#CEE5EB` / `#373F42` |
| `blue` | `#0080FF` / `#1A8CFF` | `#5C93E5` / `#5C93E5` | `#C9DAE4` / `#323739` |
| `violet` | `#5A47FF` / `#7161FF` | `#7E73E5` / `#7E73E5` | `#D6D5EE` / `#3A3A43` |
| `purple` | `#A723FF` / `#AC33FF` | `#B873E5` / `#A466CC` | `#E0D6EB` / `#3A3540` |

## Dark Flag

`Colors.isDark` (also `COUITheme.isDark`) tells whether the scheme is dark, like COUI `isCOUIDarkTheme`.
`lightColorScheme()`, `darkColorScheme()` and the Monet schemes set it; use it instead of guessing from a color's
luminance.
