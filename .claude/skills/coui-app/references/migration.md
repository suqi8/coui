# Migrating code to COUI 2.0

Use this when the code you are editing still uses Miuix (`top.yukonga.miuix.kmp`) or COUI 1.x (the ColorOS 16 line). Port the whole file, then compile: there are no deprecated aliases, so every old name below is a compile error, except the slots that changed meaning (last table), which compile and look wrong.

Full guides: https://suqi8.github.io/coui/guide/migration (from Miuix) and https://suqi8.github.io/coui/guide/upgrading (from COUI 1.x).

## From Miuix only

| Miuix | COUI |
| --- | --- |
| `top.yukonga.miuix.kmp.*` | `io.github.suqi8.coui.kmp.*` (same sub-packages) |
| Artifacts `miuix-ui`, `miuix-preference`, … | `io.github.suqi8.coui.kmp:coui-ui`, `coui-preference`, … `:2.1.0` |
| `MiuixTheme`, `MiuixIcons`, `MiuixScrollBehavior`, `MiuixPopupUtils`, `MiuixIndication`, `MiuixOverscrollEffect` | `COUITheme`, `COUIIcons`, `COUIScrollBehavior`, `COUIPopupUtils`, `COUIIndication`, `COUIOverscrollEffect` |
| `BadgedBox(badge = { Badge { Text("8") } })` | `BadgeBox(badge = { Badge(count = 8) })` |
| `extra.SuperSwitch` / `SuperArrow` / `SuperCheckbox` / `SuperDropdown` / `SuperSpinner` | `preference.SwitchPreference` / `ArrowPreference` / `CheckboxPreference` / `OverlayDropdownPreference` / `OverlaySpinnerPreference` (artifact `coui-preference`) |
| `SuperDialog(show = mutableState)` | `OverlayDialog(show = Boolean)` |
| Dialog content padded by the dialog | No padding: wrap the content in `Modifier.padding(horizontal = 24.dp)` |
| `when (pressFeedbackType)` without `Tint` | Add the `PressFeedbackType.Tint` branch |

Then apply everything in the next section too: Miuix uses the same slot names as COUI 1.x.

## From Miuix or COUI 1.x

Colour slots:

| Old | New |
| --- | --- |
| `onBackground`, `onSurface`, `onSurfaceContainer`, `onSecondaryVariant` | `labelPrimary` |
| `onBackgroundVariant`, `onSurfaceSecondary` | `labelSecondary` |
| `onSurfaceVariantSummary`, `onSurfaceContainerVariant` | `labelSecondaryVariant` |
| `onSurfaceVariantActions`, `onSurfaceContainerHigh`, `sliderKeyPoint` | `labelTertiary` |
| `disabledOnSurface`, `disabledOnSecondaryVariant` | `disabledNeutral` |
| `disabledPrimary`, `disabledPrimaryButton`, `disabledPrimarySlider` | `disable` |
| `onSecondary`, `sliderKeyPointForeground`, `onPrimaryVariant`, `onPrimaryContainer` | `labelOnColor` |
| `onTertiaryContainer` | `primaryText` |
| `tertiaryContainer` | `secondary` |
| `surface` | `backgroundWithCard` |
| `background` | `surface` on dialogs, sheets, popups and other floating panels; `backgroundElevated` elsewhere |
| `surfaceContainer` | `cardBackground` |
| `surfaceContainerHigh` | `cardPressed` |
| `surfaceVariant` | `card` |
| `primaryVariant` | `primary` |
| `primaryContainer` | `containerTheme` |
| `dividerLine` | `divider` |
| `windowDimming` | `mask` |
| `textFieldError` | `errorTextBg` |

Rename `surface` before `background`, since `background` becomes `surface` in places.

Text styles:

| Old | New |
| --- | --- |
| `main`, `body1` | `bodyXL` |
| `paragraph` | `bodyL` |
| `body2` | `bodyM` |
| `button` | `buttonL` |
| `footnote1` / `footnote2` | `bodyS` / `bodyXS` |
| `headline1`, `headline2` | `headlineXS` |
| `subtitle` | `buttonM` |
| `title1` / `title2` / `title3` / `title4` | `displayS` / `displayXS` / `headlineM` / `headlineS` |

Compile, but changed meaning; check every use:

| Slot | Now means |
| --- | --- |
| `background` | Page background (`#FFFFFF` / `#000000`) |
| `surface` | Floating panel colour (`#FFFFFF` / `#1E1E1E`) |
| `secondary` | Theme colour at 15% / 25%, not a grey |

API changes:

| Old | New |
| --- | --- |
| `TooltipBox(text = …)`, `PlainTooltip`, `RichTooltip`, `RichTooltipBox` | `TooltipBox(state = rememberTooltipState(), tooltip = { Tooltip(text = …, mode = TooltipMode.Info) }, enableUserInput = true)`; `IconTooltip` / `ImageTooltip` for the rich forms |
| `SpinnerEntry`, `SpinnerColors`, `SpinnerDefaults`, `SpinnerItemImpl` | `DropdownItem`, `DropdownColors`, `DropdownDefaults`, `DropdownImpl` |
| `DropdownItem(icon, title, summary)` | `DropdownItem(text = …, icon = …, summary = …)` |
| `TopAppBarDefaults.TitlePadding` / `NavigationIconPadding` / `ActionIconPadding` | Leave the padding parameters unset (they follow the window width) |
| `InputField(trailingIcon = …)` | `innerPrimaryButton` / `innerSecondaryButton` |
| `TextFieldDefaults.textFieldColors(backgroundColor = …)` | Drop it; COUI fields have no fill |
| `DatePicker(yearRange = …)` | `minDate = DateValue(…)`, `maxDate = DateValue(…)` |
| `DatePickerDefaults.YearLabel` and the other label constants | `yearLabel()`, … (`@Composable`) |
| `FullPageStatement(content = String)` | `content = AnnotatedString(…)` |
| `NavigationRail { Modifier.weight(…) }` | `content` is no longer `ColumnScope`; add your own `Column` |
| `SquircleDefaults.Extension*` | `SquircleDefaults.Weight*` |

Layout changes to apply while porting:

- Remove every `HorizontalDivider` between rows inside `CardListGroup`; the group draws them.
- Bottom sheets add no inside padding: give the content its own `padding(horizontal = 24.dp)`. Pass `showDragHandle = true` if the screen had a handle.
- A search box under a top bar becomes the linked search (`TopAppBarSearchBar` + `rememberTopAppBarSearchState`), see SKILL.md rule 8.
- Remove start padding added around a top bar back button.
