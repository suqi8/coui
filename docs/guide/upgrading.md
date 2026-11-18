# Upgrading

## 1.1.0 → 2.0.0

### Popup menus are blurred by default

List popups, cascading menus and the dropdown / spinner popups now use the COUI 17 popup material
(160px blur, glow-overlay blend, shadow-edge stroke, touch spotlight) by default, the ColorOS 17
look, where COUI itself leaves popup blur opt-in. The blur is drawn by the new
`LocalPopupMaterialBlur` provider (`MaterialBlurProvider`, `MaterialBlurSpec`); without one the
popups keep their opaque surface, so nothing changes until you provide it.

- New `materialBlur` parameter (default `true`) on `OverlayListPopup`, `WindowListPopup`,
  `OverlayCascadingListPopup`, `WindowCascadingListPopup`, `OverlayDropdownPopup` and
  `WindowDropdownPopup`; pass `false` to opt out. Menus and preferences built on them follow the
  provider; provide `LocalPopupMaterialBlur provides null` around them to opt out.
- `materialEffect` now defaults to `true` and only draws on a blurred surface (the default blur, or
  a translucent `backgroundColor` with your own blurring `backgroundModifier`).
- `backgroundColor` is the non-blur fill; a popup drawn with the default blur ignores it.
- Dialogs and bottom sheets reset the provider for their content, so menus opened on a panel stay
  opaque.

### TabRow is the sliding tab layout

The plain `TabRow` used to draw the segment button without its container — a shape COUI does not
have. It is now the ColorOS sliding tab layout (`COUITabLayout`): a 48dp row of labels over a 2dp
track, with a 2dp underline indicator under the selected label. `TabRowWithContour` is unchanged and
remains the segment button.

Your code still compiles, but the row looks and behaves differently:

- The default height is 48dp (was 40dp), and 72dp when a tab has an icon and a label.
- `colors` now come from `TabRowDefaults.underlineColors()`: `backgroundColor` is the track and
  `selectedBackgroundColor` the indicator. Pass `TabRowDefaults.tabRowColors()` explicitly only if
  you want the segment colors on the new layout.
- `cornerRadius` shapes the 8dp press mask instead of a capsule indicator, `minWidth` defaults to
  0dp, and `maxWidth` defaults to 70% of the row.
- `itemSpacing` is the minimum gap between labels and, like the new `horizontalMargin`, follows the
  COUI screen size when left unspecified.
- The press mask comes from `LocalIndication`; pass `indication = null` to remove it.
- An overflowing row has fading edges and the COUI rubber-band overscroll.

New: `enabled`, `pagerState` (the row follows a dragged pager) and a `TabRow(items = ...)` overload
taking `TabRowItem`s with an icon, a red dot badge or a disabled state. If you need the old
look, use `TabRowWithContour` with a transparent `backgroundColor`.

### Tooltip is the COUI 17 tooltip

The Miuix tooltip (a caret-less Material-style `PlainTooltip` / `RichTooltip`) is replaced by a
port of `COUIToolTips`. The new entry point is `TooltipBox(state, tooltip)` with one of the three
COUI bubble styles: `Tooltip` (the accent tooltips bubble with a close icon, or
`mode = TooltipMode.Info` for the white bubble), `IconTooltip` and `ImageTooltip`. Every bubble draws
an arrow at the anchor, grows from it on a spring, and places itself the COUI way (above by default,
16dp from the window edges).

The old API is removed: `TooltipBox(positionProvider, …)`, `TooltipBox(text, …)`,
`RichTooltipBox`, `PlainTooltip`, `RichTooltip`, `RichTooltipColors`,
`TooltipDefaults.rememberTooltipPositionProvider`, `TooltipDefaults.caretShape()` / `caretSize` and
the `PlainTooltip*` / `RichTooltip*` constants. Migrate like this:

```kotlin
// Before
TooltipBox(text = "Edit") { anchor() }

// After
val state = rememberTooltipState()
TooltipBox(
    state = state,
    tooltip = { Tooltip(text = "Edit", mode = TooltipMode.Info) },
    enableUserInput = true, // keep the old long-press / hover trigger
) { anchor() }
```

`RichTooltipBox(title, text, actionText)` maps to `IconTooltip(title = …, text = …, dismissText = …)`
or `ImageTooltip`.

Behaviour changes:

- `rememberTooltipState()` now defaults to `isPersistent = true`: COUI tooltips have no timeout. Pass
  `isPersistent = false` to keep the auto-dismiss.
- The new `TooltipBox` defaults to `enableUserInput = false` (COUI shows tooltips programmatically)
  and `dismissOnTouchOutside = false`; set them to keep the old long-press / outside-tap behaviour.
- The new bubbles still render inside the deprecated `TooltipBox(positionProvider, …)`, without an
  arrow.
- A tooltip anchored inside `TopAppBar`, `SmallTopAppBar` or `NavigationBar` uses the COUI toolbar /
  navigation spacing, and one whose anchor leaves the composition, scrolls out of view or collapses to
  zero size disappears without an exit animation.

### Compatibility APIs are removed

| Removed | Use instead |
| :------ | :---------- |
| `SpinnerEntry`, `SpinnerColors`, `SpinnerDefaults`, `SpinnerItemImpl` | `DropdownItem`, `DropdownColors`, `DropdownDefaults`, `DropdownImpl` |
| `DropdownItem(icon, title, summary)` constructor | `DropdownItem(text = …, icon = …, summary = …)` |
| `DropdownImpl(isFirst, isLast)`, `DropdownHeaderItem(isFirst)` | Drop the arguments; every popup row has the same padding |
| `DropdownDefaults.FirstLastVerticalPadding` | `DropdownDefaults.MiddleVerticalPadding` |
| `DropdownDefaults.PopupHeaderItemMinHeight` | Nothing; the header row has no minimum height |
| `ButtonDefaults.PressedScale` / `PressedBrightness`, `FloatingActionButtonDefaults.PressedScale` | Nothing; the press scale follows the button size |
| `SquircleDefaults.Extension` / `ExtensionMin` / `ExtensionMax` | `SquircleDefaults.Weight` / `WeightMin` / `WeightMax` |

### Colour and text style slots use the official names

`Colors` and `TextStyles` now name their slots after the official COUI attributes (`couiColorXxx` → `xxx`,
`couiTextXxx` → `xxx`). Slots that only duplicated an official slot are merged into it, unused slots are removed,
and there are no deprecated aliases, so rename the references below. A scheme built with positional
`Colors(...)` arguments has to switch to named arguments. `TextStyles(...)` and `defaultTextStyles(...)` only take
the official steps, all with defaults.

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

These names changed meaning or value, so code that still compiles can look different:

| Slot | Before | Now |
| :--- | :----- | :-- |
| `background` | Panel white, `#FFFFFF` / `#1E1E1E` | `couiColorBackground`, `#FFFFFF` / `#000000` |
| `surface` | Grey page background, `#F0F1F2` / `#000000` | `couiColorSurface`, the panel color `#FFFFFF` / `#1E1E1E` |
| `secondary` | Opaque grey, `#E5E5E5` / `#757575` | `couiColorSecondary`, the theme color at 15% / 25% |
| `bodyL` (was `paragraph`) | 16sp | 15sp (`couiTextBodyL`) |
| `headlineS` (was `title4`) | Weight 500 | Weight 550 (`couiTextHeadlineS`) |

### Top bars and progressive blur

Breaking changes:

- `TopAppBarDefaults.TitlePadding`, `NavigationIconPadding` and `ActionIconPadding` are removed. The
  `titlePadding`, `navigationIconPadding` and `actionIconPadding` parameters of `TopAppBar` and
  `SmallTopAppBar` now default to `Dp.Unspecified`, which follows the bar width
  (`TopAppBarDefaults.horizontalPadding()`): `HorizontalPaddingCompact` (16dp),
  `HorizontalPaddingMedium` (24dp) or `HorizontalPaddingExpanded` (40dp). Pass one of these, or your
  own value, where you used the old constants.
- `glow` is now nullable, `(@Composable BoxScope.() -> Unit)?`, and defaults to `null`.

Behaviour changes:

- The `SmallTopAppBar` title is 18dp Medium (was 24dp SemiBold), and a `TopAppBar` whose subtitle
  stays visible collapses its title to 18dp.
- On medium and expanded windows the bars keep 24dp / 40dp from the edges instead of 16dp.
- Any non-null `glow` hides the divider. If you passed `glow = {}` to mean "no glow", pass `null` or
  drop the argument. Without shader support the glow band paints its opaque base.
- The `ProgressiveBlur` falloff is now `pow(raw, curve)` instead of `pow(smoothstep(raw), curve)`,
  matching the ColorOS 17 renderer. This affects every progressive blur, the top bar glow included:
  with the default `curve = 1f` the radius now changes linearly across the band instead of along an
  S-curve.

New: `SmallTopAppBar(centerTitle = …)`, `IconButton(redDot = …)` and `ToolbarTextButton`; see
[TopAppBar](../components/topappbar.md) and [IconButton](../components/iconbutton.md).

### Navigation and floating bars

Breaking changes:

- `NavigationRail` takes `content: @Composable () -> Unit` instead of
  `@Composable ColumnScope.() -> Unit`. Items that used `ColumnScope` modifiers (`Modifier.weight`,
  `Modifier.align`) need their own `Column`.
- `NavigationRailDefaults.LabelFontSize` is `10.dp` (a `Dp`, so the label ignores the font scale)
  instead of `10.sp`.

Behaviour changes:

- On windows at least 600dp wide (`NavigationBarDefaults.WideLayoutBreakpoint`), `NavigationBar`
  items in `IconAndText` mode put the icon beside a 14dp label, 8dp apart, and the row keeps 44dp
  from the edges (also in landscape).
- `FloatingToolbar`: `shadowElevation` 4dp → 24dp (`FloatingToolbarDefaults.ShadowElevation`),
  `FloatingToolbarDefaults.OutSidePadding` (12dp, 8dp) → (16dp, 16dp), and `defaultColor()` is now
  `colorScheme.bar`, like the floating navigation bar (was the card background).
- `Banner` shows the list form (`BannerType.List`) on windows at least 600dp wide. Pass
  `type = BannerType.Pager` to keep the pager everywhere.

New: `FloatingToolbar(items = …)` with `FloatingToolbarItem`, `Banner` `listItemWidth` /
`listItemSpacing` / `scalePages`, and `NavigationRailDefaults.MinItemSpacing`; see
[FloatingToolbar](../components/floatingtoolbar.md),
[FloatingNavigationBar](../components/floatingnavigationbar.md) and
[Banner](../components/banner.md).

### Dialogs

Behaviour changes:

- `DialogDefaults.MaxWidth` 392dp → 360dp (the visible panel).
- Loading dialog: `LoadingDialogDefaults.CornerRadius` 9dp → 45dp (the dialog radius),
  `SpinnerSize` 26dp → 36dp, `textColor()` → `labelSecondary`, and `spinnerColor()` returns
  `ProgressIndicatorDefaults.rotatingProgressIndicatorColor()`. The card is 152dp wide, 180dp with a
  cancel button.
- Security dialog: `SecurityDialogDefaults.CheckboxText` is "Don't ask me again" (was "Don't remind me
  again"), and tapping outside no longer dismisses it or calls `onCancel`.
- A dialog with a title and no message, custom content or list has 16dp more space under the title.
- Dialog button labels wrap to at most two lines (`DialogButtonBarDefaults.LabelMaxLines`).

New: `customContent` on `OverlayDialog` / `WindowDialog`, `title` and `cancelAction` on the loading
dialog, `onCheckedChange` on the security dialog, and list, single-choice, multi-choice, progress and
input dialogs; see [ListDialog](../components/listdialog.md),
[ProgressDialog](../components/progressdialog.md) and [InputDialog](../components/inputdialog.md).

### Bottom sheets and statements

Breaking changes:

- `FullPageStatement` takes `content: AnnotatedString` instead of `String`:

```kotlin
// Before
FullPageStatement(title = title, content = text, /* … */)

// After
FullPageStatement(title = title, content = AnnotatedString(text), /* … */)
```

Behaviour changes:

- `BottomSheetDefaults.insideMargin` is `DpSize(0.dp, 0.dp)` (was 24dp horizontally): COUI panels add
  no padding, the content sets its own. Pass `insideMargin = DpSize(24.dp, 0.dp)` to keep the old
  inset. The list and multi-select list preference panels keep 24dp
  (`ListPreferenceDefaults.PanelInsideMargin`).
- The drag handle is hidden by default; pass `showDragHandle = true`.
- On windows at least 600dp wide (`BottomSheetDefaults.LargeScreenWidthThreshold`) the sheet floats
  centered, 64dp above the bottom, at most 540dp wide (`PanelMaxWidth`), and fades and scales in
  instead of sliding up.
- A sheet without a title or actions no longer reserves the 42dp title row.
- A drag dismisses the sheet past half its height or on a fling faster than 500px/s (was 150dp or
  800dp/s).
- `FullPageStatement` no longer forces `fillMaxSize`, the statement body scrolls past 248dp
  (`statementMaxHeight`), and the scroll fade is 32dp (was 46dp).

New: `showDragHandle`, `handlePanel`, `showInMaxHeight`, `dismissOnClickOutside`, `draggable` and
`enablePullUp` on the bottom sheets, and the user / individual statement dialogs; see
[OverlayBottomSheet](../components/overlaybottomsheet.md),
[UserStatementDialog](../components/userstatementdialog.md) and
[IndividualStatementDialog](../components/individualstatementdialog.md).

### List rows and preferences

Behaviour changes:

- `CardListGroup` draws the row dividers itself: below every head or middle `BasicComponent` row,
  from the title to 16dp before the card edge, hidden next to a `selected` row. Remove the
  `HorizontalDivider`s you placed between rows, or opt out with `CardListGroup(showDividers = false)`
  or `item(showDivider = false)`.
- `BasicComponent` puts 16dp (was 8dp) between the start content and the title, and between the
  title and the end content. Drop any end padding you added to `startAction` icons.
- Start content of 24 / 32 / 36 / 50dp gets the COUI vertical margins, and rows of more than two lines
  align it to the top. When the title and the end content compete for width, the title is measured
  first and the end content keeps at least 80dp.
- `CheckboxPreference` defaults to `checkboxLocation = CheckboxLocation.End` (was `Start`).
- `SliderPreference` defaults to `hapticEffect = SliderDefaults.SliderHapticEffect.Step` (was `Edge`) and
  `showKeyPoints = true` (was `false`).
- `RecommendedPreference` uses `container4`: the dark background is `#1AFFFFFF` (was `#14FFFFFF`).

New: `selected`, `startBadge`, `endBadgeCount` and `endIcon` on `BasicComponent`,
`SwipeActionItem(menuStyle = …)`, `SmallTitleDefaults.InsideMarginSmall`, and the
`MarkWithDividerPreference`, `SlideSelectPreference` and `PreferenceCategory` components; see
[CardListGroup](../components/cardlistgroup.md) and
[BasicComponent](../components/basiccomponent.md).

### Text fields

Breaking changes:

- `TextFieldColors.backgroundColor` and the `backgroundColor` parameter of
  `TextFieldDefaults.textFieldColors()` are removed: COUI fields have no fill. The `TextFieldColors`
  constructor gained `counterErrorColor` and `disabledIconColor`.
- `InputViewDefaults.MultiCountSpacing` is removed; the multi-line card is a fixed 152dp
  (`InputViewDefaults.MultiInputHeight`).

Behaviour changes:

- `Line` and `Rectangle` text fields are at least 62dp tall (`TextFieldDefaults.minHeight(mode)`).
  Pass `minHeight = 0.dp` for a compact field, for example inside a dialog.
- The `Rectangle` stroke stays 0.33dp when focused (was 1dp); only its color changes.
- The floating label follows COUI: in `Line` mode the field reserves 10dp + the collapsed label + 5dp
  above the text; in `Rectangle` mode the border starts half a collapsed label below the top and the
  label sits in a 4dp cutout. A disabled label uses `disabledBorderColor`.
- `TextField` has a new `initialPasswordVisible` (default `false`, masked). `InputView` and
  `InputPreference` pass `true`, so their passwords start in plain text, as `COUIInputView` does.
- `InputPreference` defaults to `showClearButton = true` (was `false`), and `InputView` /
  `InputPreference` default `textFieldColors` to `InputViewDefaults.textFieldColors()`.
- `CodeTextField` cells scale with the window width (`min(window, 360dp) / 360dp`) instead of the
  field's own width.

### Search, index bar and lock screen

Breaking changes:

- `InputField(trailingIcon = …)` is removed; use `innerPrimaryButton` / `innerSecondaryButton`.
- `SearchBarDefaults.TrailingIconStartPadding` / `TrailingIconEndPadding` /
  `TrailingIconTouchTargetSize` / `TrailingIconVisualSize` → `EditTextEndGap`, `InnerButtonGap`,
  `InnerButtonSize` and `ClearButtonVisualSize`.
- The `SearchBarColors` constructor gained `disabledLabelColor`, `disabledIconColor` and
  `disabledCancelTextColor`.
- `SearchHistoryDefaults.InsideMargin` → `SearchHistoryDefaults.insideMargin()` (20dp horizontally on
  windows at least 600dp wide).
- `LockScreenPasswordInputColors` gained `eyeColor`.
- `LockPatternDisplayMode` gained `FingerprintMatch` and `FingerprintNoMatch`; an exhaustive `when`
  over it needs the new branches.

Behaviour changes:

- `SearchBarDefaults.InsideMargin` has an unspecified width, so the horizontal padding follows the
  window (`responsiveHorizontalPadding`).
- Collapsing the search bar clears the query.
- `SearchHistory` calls `onClear` as soon as the clear button is clicked, then fades the panel out
  over 300ms with the old records still on screen.
- `NumericKeyboard` has no delete key by default (`rightKey = null`); pass
  `rightKey = NumericKeyboardDefaults.deleteKey()`. Custom side-key icons are not tinted unless
  `NumericKeyboardSideKey(tintIcon = true)`. In dark mode the digits are white and the side keys have
  no background.
- In stealth mode `LockPatternView` no longer draws a wrong pattern either.

New: `SearchBar` `type` (`SearchBarType.NonInstant`), `navigationButton` and outer buttons,
`AlphabetIndexBar(selectedKey = …)`; see [SearchBar](../components/searchbar.md) and
[ToolbarSearch](../components/toolbarsearch.md).

### Pickers and states

Breaking changes:

- `DatePicker(yearRange = …)` → `minDate` / `maxDate`, and `DatePickerDefaults.YearRange` →
  `MinDate` / `MaxDate`:

```kotlin
// Before
DatePicker(value, onValueChange, yearRange = 2000..2030)

// After
DatePicker(value, onValueChange, minDate = DateValue(2000, 1, 1), maxDate = DateValue(2030, 12, 31))
```

- The label constants are now `@Composable` functions that read the locale:

| Removed | Use instead |
| :------ | :---------- |
| `DatePickerDefaults.YearLabel` / `MonthLabel` / `DayLabel` | `yearLabel()` / `monthLabel()` / `dayLabel()` |
| `TimePickerDefaults.AmLabel` / `PmLabel` | `amLabel()` / `pmLabel()` |
| `DateTimePickerDefaults.DateLabel` / `TodayLabel` | `dateLabel()` / `todayLabel()` |
| `CalendarPickerDefaults.MonthYearLabel` / `WeekdayLabel` | `monthYearLabel()` / `weekdayLabel()` |

- `NumberPickerDefaults.colors()` and `NumberPickerColors` drop `disabledSelectedTextColor` and
  `disabledUnselectedTextColor`.

Behaviour changes:

- `NumberPicker` defaults to `wrapAround = true` (was `false`); pass `false` for a bounded wheel.
  Without a width from its modifier the wheel is 80dp wide (`NumberPickerDefaults.MinWidth`) instead
  of filling the width.
- `amPmFirst` on `TimePicker` and `DateTimePicker` follows the locale (was `false` and `true`).
- `LunarDatePickerDefaults.MinDate` is 1910-03-10 (was 1910-01-01).
- `EmptyState` illustration defaults to 120 × 100dp (was 280 × 200dp), and the `Medium` size no longer
  scales it to 0.6.
- `CompleteState` defaults to `type = CompleteStateType.None` (was `Success`).

### Sliders, progress and overscroll

Breaking changes:

- `VerticalSlider` lost `width` and `effect`: it is the COUI 4dp track that widens while dragged, at
  least 36dp wide. Give it a height. New `text` / `textColor` draw a label at the top.
- `ProgressIndicatorDefaults.DefaultCircularProgressIndicatorSize` / `StrokeWidth` →
  `MediumCircularProgressIndicatorSize` (30dp) / `MediumCircularProgressIndicatorStrokeWidth` (3dp).
- `InstallLoadStyle` gained `Circle`; an exhaustive `when` over it needs the new branch.

Behaviour changes:

- `Slider`, `VerticalSlider`, `IntentSlider` and `SliderPreference` default to
  `hapticEffect = SliderDefaults.SliderHapticEffect.Step` (was `Edge`; `RangeSlider` is unchanged),
  and `Slider`
  defaults to `showKeyPoints = true` (was `false`).
- `CircularProgressIndicator` defaults: with a `progress` 40dp / 5dp (was 30dp / 3dp), laid out with
  4dp of padding on each side; without one 18dp / 2.67dp in the theme accent (was 30dp / 3dp).
- Overscroll follows COUI: a drag past the edge moves the content by
  `0.4 * (1 - |overscroll| / window size)` of the finger, and a release springs back on the
  `SpringOverScroller` RK4 spring. The default `COUIOverscrollEffect` plays the scroll-edge haptic
  (307); pass `edgeHapticEnabled = false` to turn it off.
- `Modifier.scrollEndHaptic()` defaults to the COUI scroll-edge haptic (307); its
  `hapticFeedbackType` is now nullable (was `TextHandleMove`).

New: `CompProgressIndicator`, `CircularProgressIndicator` `state` / `type`,
`Slider(startFromMiddle = …)` and the `Circle` install style; see
[ProgressIndicator](../components/progressindicator.md), [Slider](../components/slider.md) and
[ScrollBar](../components/scrollbar.md).

### Buttons and selection controls

Breaking changes:

- `CheckboxDefaults.checkboxColors()` drops `uncheckedForegroundColor` and
  `disabledUncheckedForegroundColor`; the `CheckboxColors` constructor lost them and gained
  `uncheckedFillColor` / `disabledUncheckedFillColor`.

Behaviour changes:

- A fixed `SegmentButton` width is capped to the COUI grid (4 columns on small windows, 6 on medium,
  8 on large) and the control is centered in it. Pass `responsiveWidthEnabled = false` to restore the
  old width.
- `SegmentButton` items and `Stepper` buttons are keyboard focusable (Enter / D-pad), and `Button`,
  `Switch`, `FloatingActionButton`, `SegmentButton` and `Stepper` draw the COUI focus ring.

New: `DescTextButton`, `ButtonDefaults` width constants, `Chip` `showRedDot` / `onClose` / `count`,
`SuggestionChip`, `IconChip`, `ChipGroup`, `CheckboxDefaults.shapeCheckboxColors()`, `Badge(text)`
and `visible`, and the FAB `visible`, tiny size, secondary / translate presets and
`rememberFloatingActionButtonScrollState()`; see [Button](../components/button.md),
[Chip](../components/chip.md), [ChipGroup](../components/chipgroup.md) and
[FloatingActionButton](../components/floatingactionbutton.md).

### TopTips, snackbars and menus

Breaking changes:

- `TopTipsDefaults.ContentPadding` (12dp) → `TopTipsDefaults.HorizontalPadding` (16dp).
- `TopTipsColors` gained `titleColor` and `actionContentColor`, and `actionColor` is now the fill of
  the action button. Use named arguments with the constructor.
- `NotificationSnackbar(title, actionLabel, …)` takes a non-null `actionLabel: String`. The
  `NotificationSnackbar(data)` overload still accepts a `SnackbarData` without an action label and
  draws the card without a button.

Behaviour changes:

- `TopTips`: the message is 13sp (`bodyS`, was 14sp); the action is a small solid button (was accent
  text) and can show together with the close button; the container uses `container4` (dark
  `#1AFFFFFF`, was `#14FFFFFF`); the close button is a `labelTertiary` circle with a white cross; show
  and hide animate the height.
- Navigation popup menu rows (`OverlayNavigationPopupMenu` / `WindowNavigationPopupMenu`) use 12dp /
  16dp padding and a 16sp bold title, and no longer mark the checked item.
- `EditTextPreference` and the dialog mode of the dropdown popups place their field / list as the
  dialog's custom content: the summary has 4dp below it (was 16dp), the field has 8dp below it, and
  a dialog with only a title loses the 16dp a title-only dialog otherwise gets.

New: `TopTips` `title` (with a 36dp icon) and `marquee`, `DropdownImpl(reserveIconSlot = …)` and
`DropdownItem.stateIcon`; see [TopTips](../components/toptips.md) and
[Snackbar](../components/snackbar.md).

### Material, blur and haptics

Breaking changes:

- `CouiHapticEffect` gained `LongVibrate`, `KeyboardTouch`, `ShortTripleVibrate`, `SteppingShort`,
  `EdgeShort` and `EdgeSqueeze`. An exhaustive `when` over it no longer compiles:

```kotlin
// After
when (effect) {
    CouiHapticEffect.Switch -> …
    CouiHapticEffect.Strength -> …
    CouiHapticEffect.ScrollEdge -> …
    else -> … // the new effects
}
```

- `MaterialBlurDefaults.popup(isDark, hdr = false)` and `Modifier.couiMaterialBlur(…,
  noiseCoefficient)` gained defaulted parameters. Your source still compiles, but a library compiled
  against the previous coui-blur must be recompiled.

Behaviour changes:

- The new app-wide switch `LocalCOUIMaterialEffectEnabled` (default `true`) is the default of the
  material flags of `FloatingNavigationBar`, `FloatingToolbar`, `FloatingEditToolbar`,
  `FloatingAccessoryNavigationBar`, `ChipTab`, the `Button` family (`materialBackdrop`,
  `ButtonMaterial.Auto`), `FloatingActionButton` (`material`), `IconButton`, `SearchBar` and the
  list, cascading and dropdown popups (`materialBlur`, `materialEffect`). `SegmentButton` keeps
  `materialEffect = false`. Provide `false` to turn them all off; a flag passed explicitly still wins:

```kotlin
CompositionLocalProvider(LocalCOUIMaterialEffectEnabled provides !batterySaver) {
    AppContent()
}
```

- The material blur now matches the native Kawase filter: `couiMaterialBlurRadius()` maps the COUI
  radius to the Gaussian radius of the same spread (200px → ~192.4, 400px → ~384.9, 160px → ~154),
  so the blur spreads about 4% less, and a ±0.005 dither is added
  (`MaterialBlurDefaults.NoiseCoefficient`; `noiseCoefficient = 0f` disables it). A `fraction`ed
  radius is rounded to whole pixels.
- Components play the COUI haptics: slider steps `SteppingShort`, sections `Strength`, ends
  `EdgeShort`; lock pattern and simple lock errors `ShortTripleVibrate`; numeric keyboard
  `KeyboardTouch`; the alphabet index bar `Strength`; scroll edges `ScrollEdge`.

New: the `ViewBlurDefaults` presets `frameworkFab`, `frameworkBottomBar`, `searchBarNoMaterial`,
`contentSegmentButton`, `contentChipTab` and `NoEffect`, `ViewBlurDefaults.interpolate()`, the
`COUIEaseEasing` family, `Modifier.couiFocusStroke` and `COUITouchSelectInteraction`; see
[Blur](./blur.md).

### Visual changes worth a device check

These compile without changes but are easy to miss in a preview; look at them on a device:

- Overscroll: the new rubber band and spring-back, and the scroll-edge haptic (307) on every list
  that uses the default overscroll.
- Progressive blur: the linear `pow(raw, curve)` falloff on top bar glows and your own
  `ProgressiveBlur` bands.
- Material blur: about 4% less spread and the added dither on dialogs, bottom sheets and popups.
- Wide windows (at least 600dp): bottom sheets float centered at up to 540dp, and `Banner` switches
  to the list form.
- `TextField` floating labels: the new label offsets and cutout, and the 62dp minimum height.

### New in this release

- **Search linked with the top bar.** `TopAppBarSearchBar`, `rememberTopAppBarSearchState`,
  `TopAppBar(searchState = …)` and `Modifier.topAppBarSearchContent` / `topAppBarSearchResults`
  search in place like the ColorOS 17 Settings home: the title fades away, the search bar rises to
  the top with a cancel button, the page fades out and the results come up from below. Use it for
  every search typed into under a top bar; keep `TopAppBarSearchSlot` for a capsule that only opens
  something else. See [TopAppBar](../components/topappbar.md#searching-in-place).
- **Camera controls** from the ColorOS camera: `ExposureBar`, `ScaleBar`, `CameraShutterButton`,
  `CameraSwitchButton`, `CameraModeBar`, `CameraModeTitle`, `CameraLensBar`, `CameraZoomBar`,
  `CameraZoomDial`, `CameraFocusIndicator`, `CameraMenuButton`, `CameraEffectButton`,
  `CameraTopMenu`, `CameraRecordTimer`, `CameraCountdown`, `RollingNumber`,
  `CameraLevelIndicator`, `CameraGridOverlay`, `CameraThumbnail` and `CameraHint`.
- **Baseline profiles** for every library module, regenerated for the `io.github.suqi8.coui.kmp`
  packages.

## 1.0.0 → 1.1.0

Update the version in every COUI coordinate you depend on:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-ui:1.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-preference:1.1.0")
            implementation("io.github.suqi8.coui.kmp:coui-icons:1.1.0")
        }
    }
}
```

This release recalibrates most components against ColorOS 16 — partly from decompiled resources,
partly from measuring the real thing on a device. Two of them corrected values we had inherited
from Miuix and never checked. **Expect visible changes even where you changed nothing**, and give
your screens a look after upgrading.

### One compile break

`InfiniteProgressIndicator` lost its `orbitingDotSize` parameter, along with
`ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorOrbitingDotSize`. The indicator used to
be a ring with a dot orbiting inside it — a Miuix design with no counterpart in COUI, which draws a
bare arc whose sweep pulses as it spins. There is no dot left to size.

```kotlin
// Before
InfiniteProgressIndicator(size = 40.dp, strokeWidth = 3.dp, orbitingDotSize = 4.dp)

// After — drop the argument
InfiniteProgressIndicator(size = 40.dp, strokeWidth = 3.dp)
```

The same arc is also available directly as `RotatingProgressIndicator`, with the two size tiers COUI
ships (16dp and 26dp).

### Text is taller

Every entry in `COUITheme.textStyles` now carries the line-spacing multiplier its COUI text
appearance declares, so text occupies more vertical space than it did in 1.0.0. `headline1` also
dropped from 17sp to **16sp**: a preference title measured 21.71dp on a ColorOS 16 device, which is
16sp at COUI's multiplier, not the 17sp Miuix used.

If you sized a container to fit a specific string, re-check it. If you were compensating for the
tightness with your own `lineHeight`, remove it — you will now be adding leading twice.

### Rows and dialogs shifted

- A row's height now depends on its position in its card group: a standalone row is 4dp taller than
  a middle row, and a first or last row 2dp taller, on the edge that is rounded. Nothing changes
  unless you pass `cardListPosition`, which every preference component now accepts.
- Dialog button bars stack vertically when a label is too wide to fit, instead of squeezing it. The
  stacked tier has its own metrics, and the buttons reverse order — cancel moves from the far left
  to the bottom.
- Dialog bar buttons no longer shrink when pressed, and their press tint fills the whole cell as a
  square rather than a capsule. `Button` and `TextButton` gained `pressScaleEnabled` for this;
  it defaults to `true`, so your own buttons are unaffected.

### Dropdown menus behave differently

The flat dropdown was rebuilt around COUI's own gesture model, so the interaction changed rather
than just the pixels:

- On a menu short enough not to scroll, **dragging moves the highlight between rows** and releasing
  selects whichever row the finger is over — not the one first pressed. Each row crossed fires a
  haptic. Once the menu scrolls, dragging scrolls it instead.
- A dropdown opened from a preference row now appears **at the point you tapped**, not centred under
  the row.
- Menus no longer dim the content behind them. `enableWindowDim` now defaults to `false` on
  `OverlayListPopup`, `WindowListPopup` and both cascading popups; pass `true` if you want the old
  behaviour.
- Rows are separated by hairlines, and the ones next to the pressed row fade out while it is held.

### New in this release

`DialogButtonBar` (with `DialogButtonBarAction`) replaces hand-rolled dialog button rows and handles
the vertical stacking for you. Dropdown items gained a `hint` slot for a trailing badge, an `alert`
flag for destructive entries, and `DropdownEntry.title` for in-menu group headers.

## Older versions

Coming from Miuix rather than an earlier COUI release? See [Migrating from Miuix](./migration.md).
