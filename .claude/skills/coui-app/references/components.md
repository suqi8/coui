# ColorOS widget → COUI component

Packages (all under `io.github.suqi8.coui.kmp`): coui-ui — `basic` (components), `overlay` / `window` (dialogs, sheets), `layout` (`DialogButtonBar`, `DialogButtonBarAction`, `DialogDefaults`), `theme` (`COUITheme`, `ThemeController`, `ColorSchemeMode`), `effect` (`LocalMaterialBackdrop`, `LocalPopupMaterialBlur`, `LocalCOUIMaterialEffectEnabled`), `utils` (`overScrollVertical`, `scrollEndHaptic`, haptics), `icon` (`COUIIcons` + basic icons), `interfaces` (`ExperimentalScrollBarApi`); coui-preference — `preference`, `menu`, `popup`; coui-blur — `blur`; coui-icons — `icon.extended` (`COUIIcons.<Name>`); coui-squircle — `squircle`; coui-nav — `nav.core`, `nav.transition`. Before using a component, open its source for the exact signature; the KDoc names the ColorOS widget it ports.

## Overlay vs Window

Most pop-ups exist twice:

- `Overlay*` (`OverlayDialog`, `OverlayBottomSheet`, `OverlayDropdownPreference`, `OverlayListPopup`, `OverlayContextMenu`, …) draw in the root `Scaffold`'s popup host. Default choice. Need a `Scaffold`. Can use the page material blur.
- `Window*` (`WindowDialog`, `WindowBottomSheet`, `WindowDropdownPreference`, …) open a real platform window. Use when the caller is outside any Scaffold or must cover other windows. No page-backdrop blur.

## Page structure

| ColorOS | COUI |
| --- | --- |
| Page with collapsing title | `Scaffold` + `TopAppBar(scrollBehavior = COUIScrollBehavior())` (or the skill's `CouiPage`) |
| Pinned small toolbar | `SmallTopAppBar` |
| Settings home bar with search | `TopAppBar(expandedHeight = 116.dp, largeTitleFontSize = 40.sp, titlePadding = 24.dp, largeTitleBottomMargin = 18.dp, searchState = searchState, bottomContent = { TopAppBarSearchBar(…) })` with `val searchState = rememberTopAppBarSearchState(active = searching)` |
| Search under any top bar (in place, linked with the bar like the ColorOS Settings search) | `TopAppBarSearchBar` + `rememberTopAppBarSearchState` + `Modifier.topAppBarSearchContent` / `topAppBarSearchResults` |
| Capsule under a top bar that only opens something else | `TopAppBarSearchSlot(inputField = { InputField(expanded = false, …) })` |
| Toolbar search that grows out of the search icon | `ToolbarSearchAnchor`, `ToolbarFloatingSearchBar`, `rememberToolbarSearchState`, `Modifier.toolbarSearchEntry/Item` |
| Top bar glow / blur | `TopAppBarGlow` (coui-blur) in the bar's `glow` slot |
| Bottom tab bar (COUI 17) | `FloatingNavigationBar` + `FloatingNavigationBarItem`; classic: `NavigationBar` |
| Side rail (tablet) | `NavigationRail`; drawer-like: `SideNavigationBar` |
| Tabs | `TabRow`, `TabRowWithContour`; capsule tabs `ChipTabRow` |
| Floating toolbar / edit bar | `FloatingToolbar`, `FloatingEditToolbar` |
| FAB | `FloatingActionButton`, `ExpandableFloatingActionButton` (Scaffold `floatingActionButton` slot) |
| Two-pane | `ListDetailLayout`, `SidePaneLayout`; grid `ResponsiveGrid` |
| Pull to refresh | `PullToRefresh` (pass the top bar scroll behaviour) |
| Scroll bar | `VerticalScrollBar` + `rememberScrollBarAdapter` (`@OptIn(ExperimentalScrollBarApi::class)`) |
| Over-scroll / edge haptic | `Modifier.overScrollVertical()`, `Modifier.scrollEndHaptic()` |

## Lists and settings

| ColorOS | COUI |
| --- | --- |
| Rounded card group | `Card { CardListGroup { item { … } } }` |
| Category header | `PreferenceCategory(title)` (+ `LoadingPreferenceCategory`) |
| Plain row | `BasicComponent(title, summary, startAction, endActions, onClick)` |
| Jump row (chevron) | `ArrowPreference` |
| Switch row | `SwitchPreference`; with loading `SwitchLoadingPreference`; split row `SwitchWithDividerPreference` |
| Checkbox / radio row | `CheckboxPreference`, `RadioButtonPreference`, `MarkPreference` (check mark list) |
| Pick one from a menu | `OverlayDropdownPreference` / `WindowDropdownPreference`; spinner style `OverlaySpinnerPreference` |
| List dialog row | `ListPreference`, `MultiSelectListPreference` |
| Slider row | `SliderPreference`, `RangeSliderPreference` |
| Text entry row | `InputPreference`, `EditTextPreference`, `StepperPreference` |
| App header (About) | `AppInfoPreference` |
| Cards with buttons / entries | `CardButtonPreference`, `CardEntrancePreference`, `CardInstructionPreference`, `RecommendedPreference` |
| Header / footer text, links | `PagerHeaderPreference`, `PagerFooterPreference`, `LinkText`, `SpannablePreference` |
| Expandable section | `ExpandableGroupHeader` + `ExpandableGroupContent` (+ `ExpandableGroupArrow`) |
| Swipe actions | `SwipeActionItem` |

## Controls

`Button` / `TextButton` (`ButtonDefaults.buttonColorsPrimary()`, `textButtonColorsTransparent()`), `LoadingButton`, `Switch`, `Checkbox`, `RadioButton`, `Slider` / `RangeSlider` / `VerticalSlider`, `IntentSlider`, `SegmentButton`, `Stepper`, `Chip` / `ChipGroup`, `TextField`, `InputView`, `CodeTextField`, `SearchBar` + `InputField` + `CancelButton`, `SearchHistory`, `NumberPicker`, `DatePicker` / `TimePicker` / `DateTimePicker`, `LunarDatePicker`, `CalendarPicker`, `ColorPicker`, `NumericKeyboard`, `LockPatternView`, `SimpleLock`.

## Overlays and feedback

| ColorOS | COUI |
| --- | --- |
| Alert dialog | `OverlayDialog` / `WindowDialog` + `DialogButtonBar` (capsule buttons, stacking automatic) |
| List / single / multi choice dialog | `OverlayListDialog`, single / multi choice variants |
| Progress / loading dialog | `OverlayProgressDialog`, `OverlayLoadingDialog` |
| Input dialog | `OverlayInputDialog`; security prompt `OverlaySecurityDialog` |
| Privacy statement | `OverlayUserStatementDialog` / `WindowUserStatementDialog`, `OverlayIndividualStatementDialog` / `WindowIndividualStatementDialog`, `FullPageStatement` |
| Onboarding | `OverlayGuideDialog` |
| Bottom sheet | `OverlayBottomSheet` / `WindowBottomSheet` |
| Dropdown / cascading menu | `OverlayDropdownMenu`, `OverlayIconDropdownMenu`, `OverlayIconCascadingDropdownMenu` (entries: `DropdownEntry` / `DropdownItem`) |
| Long-press menu | `OverlayContextMenu` / `WindowContextMenu` |
| Tooltip | `Tooltip`, `IconTooltip`; top notice `TopTips` |
| Toast-like message | `Snackbar` via `SnackbarHostState.showSnackbar`; custom `CustomSnackbar`, `NotificationSnackbar` |
| Progress | `LinearProgressIndicator`, `CircularProgressIndicator`, `InfiniteProgressIndicator`, `CompProgressIndicator`, `InstallLoadProgress` |
| Empty / done states | `EmptyState`, `CompleteState` |
| Badge / tag / marquee | `Badge`, `BadgeBox`, `TagView`, `MarqueeText`, `Banner`, `PageIndicator` |

## Theme and effects

- `COUITheme(controller = ThemeController(ColorSchemeMode.…, keyColor = …))`; colours `COUITheme.colorScheme` (official COUI slot names: `primary`, `labelPrimary`, `labelSecondary`, `labelTertiary`, `backgroundWithCard`, `cardBackground`, `divider`, `container4`, …; check `theme/Colors.kt`), text `COUITheme.textStyles` (`headlineXS`, `bodyL`, `bodyM`, `bodyS`, `buttonM`, …).
- Material switch: `LocalCOUIMaterialEffectEnabled` (turn off for a flat, opaque look).
- Smooth corners: `LocalSquircleEnabled`, `Modifier.squircleBackground` / `squircleBorder` / `squircleClip` (coui-squircle).
- Haptics: `rememberCouiHaptic()` with `CouiHapticEffect` (`Switch`, `Strength`, `ScrollEdge`, `SteppingShort`, `EdgeShort`, …).
- Keyboard focus ring: `Modifier.couiFocusStroke`.
