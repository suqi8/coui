# Components

COUI provides a rich set of UI components that strictly follow the OPPO ColorOS design language. Each component is carefully designed to ensure visual and interactive consistency with the native ColorOS experience.

## Scaffold Components

| Component                          | Description                   | Common Usage                    |
| ---------------------------------- | ----------------------------- | ------------------------------- |
| [Scaffold](../components/scaffold) | Basic layout for applications | Page structure, content display |

::: warning
The Scaffold component provides a suitable container for cross-platform popup windows. Components such as `OverlayDialog`, `OverlayDropdownPreference`, `OverlaySpinnerPreference`, and `OverlayListPopup` are all implemented based on this and therefore need to be wrapped by this component.
:::

## Basic Components

| Component                                                  | Description                                | Common Usage                          |
| ---------------------------------------------------------- | ------------------------------------------ | ------------------------------------- |
| [Surface](../components/surface)                           | Basic container component                  | Content display, background container |
| [TopAppBar](../components/topappbar)                       | Application top navigation bar             | Page title, primary actions           |
| [NavigationBar](../components/navigationbar)               | Bottom navigation component                | Main page switching                   |
| [NavigationRail](../components/navigationrail)             | Side navigation component                  | Main page switching (Large screen)    |
| [TabRow](../components/tabrow)                             | Horizontal tab bar                         | Content category browsing             |
| [BreadcrumbBar](../components/breadcrumbbar)               | Horizontal breadcrumb navigation bar         | File path navigation, location trail   |
| [Card](../components/card)                                 | Container with related information         | Information display, content grouping |
| [BasicComponent](../components/basiccomponent)             | Universal base component                   | Custom component development          |
| [Button](../components/button)                             | Interactive element for triggering actions | Form submission, action confirmation  |
| [LoadingButton](../components/loadingbutton)               | Text button with a built-in loading state  | Asynchronous action submission        |
| [IconButton](../components/iconbutton)                     | Icon button component                      | Auxiliary actions, toolbars           |
| [Chip](../components/chip)                                 | Checkable capsule filter tag               | Content filtering, tag selection      |
| [Text](../components/text)                                 | Display text content with various styles   | Titles, body text, descriptive text   |
| [SmallTitle](../components/smalltitle)                     | Small title component                      | Auxiliary titles, category labels     |
| [TextField](../components/textfield)                       | Receives user text input                   | Form filling, search boxes            |
| [InputView](../components/inputview)                       | Card-style input field                     | Form filling, dialog input            |
| [CodeTextField](../components/codetextfield)               | Verification code input with cells         | OTP / verification code entry         |
| [Switch](../components/switch)                             | Binary state toggle control                | Setting switches, feature enabling    |
| [Checkbox](../components/checkbox)                         | Multiple selection control                 | Multiple choices, terms agreement     |
| [RadioButton](../components/radiobutton)                   | Single selection control                   | Exclusive choices, option selection   |
| [Slider](../components/slider)                             | Sliding control for value adjustment       | Volume control, range selection       |
| [NumberPicker](../components/numberpicker)                 | Vertical scroll picker for number selection | Time picker, quantity selection       |
| [DatePicker](../components/datepicker)                     | Date wheel picker (year / month / day)     | Date selection, forms                 |
| [TimePicker](../components/timepicker)                     | Time wheel picker (hour / minute)          | Time selection, alarms                |
| [Stepper](../components/stepper)                           | Increment / decrement integer control      | Quantity adjustment, value tuning     |
| [ProgressIndicator](../components/progressindicator)       | Displays operation progress status         | Loading, progress display             |
| [Snackbar](../components/snackbar)                         | Temporary message bar for brief feedback   | Operation result, status notification |
| [TopTips](../components/toptips)                           | Tip banner at the top of a page            | Page hints, announcements             |
| [Tooltip](../components/tooltip)                           | Brief label shown on hover or long press   | Icon button labels, element hints     |
| [Badge](../components/badge)                               | Small status overlay on an anchor          | Unread counts, status dots            |
| [Icon](../components/icon)                                 | Icon display component                     | Icon buttons, status indicators       |
| [FloatingActionButton](../components/floatingactionbutton) | Floating action button                     | Primary actions, quick functions      |
| [FloatingToolbar](../components/floatingtoolbar)           | Floating toolbar                           | Quick actions, information display    |
| [Divider](../components/divider)                           | Content separator                          | Block separation, hierarchy division  |
| [PullToRefresh](../components/pulltorefresh)               | Pull-down refresh operation                | Data update, page refresh             |
| [SearchBar](../components/searchbar)                       | Search input field                         | Content search, quick find            |
| [FullPageStatement](../components/fullpagestatement)       | Full-page user agreement statement         | First-launch agreement, privacy statement |
| [ColorPalette](../components/colorpalette)                 | Grid palette with alpha slider             | Theme settings, color selection       |
| [ColorPicker](../components/colorpicker)                   | Color selection control                    | Theme settings, color selection       |
| [ColorSwatchPicker](../components/colorswatchpicker)       | Circular color swatch row with selection ring | Theme settings, color selection    |

## Extended Components

| Component                                            | Description                                                                              | Common Usage                           |
| ---------------------------------------------------- | ---------------------------------------------------------------------------------------- | -------------------------------------- |
| [ArrowPreference](../components/arrowpreference)               | Arrow component based on BasicComponent                                                  | Clickable indication, navigation hints |
| [SwitchPreference](../components/switchpreference)             | Switch component based on BasicComponent                                                 | Setting switches, feature enabling     |
| [SwitchLoadingPreference](../components/switchloadingpreference) | Switch preference with a loading state                                                   | Asynchronous setting switches          |
| [CheckboxPreference](../components/checkboxpreference)         | Checkbox component based on BasicComponent                                               | Multiple selection, terms agreement    |
| [RadioButtonPreference](../components/radiobuttonpreference)   | Radio button component based on BasicComponent                                           | Exclusive choices, option selection    |
| [MarkPreference](../components/markpreference)                 | Single-select row with a selection mark                                                  | Single-select lists, option selection  |
| [ButtonPreference](../components/buttonpreference)             | Preference row with a small inline button                                                | Inline actions in settings lists       |
| [ListPreference](../components/listpreference)                 | Preference row opening a bottom-sheet choice panel (requires `Scaffold`)                 | Single / multiple choice settings      |
| [RecommendedPreference](../components/recommendedpreference)   | Rounded card recommending related settings                                               | Related settings recommendations       |
| [SliderPreference](../components/sliderpreference)             | Slider component based on BasicComponent                                                 | Value adjustment, volume/brightness    |
| [RangeSliderPreference](../components/rangesliderpreference)   | Range slider component based on BasicComponent                                           | Range selection, price filter          |
| [OverlayListPopup](../components/overlaylistpopup)             | List popup component based on BasicComponent (uses COUIPopupUtils; requires `Scaffold`) | Option selection, feature list         |
| [OverlayCascadingListPopup](../components/overlaycascadinglistpopup) | Two-level cascading list popup (uses COUIPopupUtils; requires `Scaffold`)               | Submenus, grouped action panels        |
| [OverlayDropdownPreference](../components/overlaydropdownpreference) | Dropdown selector based on BasicComponent (uses COUIPopupUtils; requires `Scaffold`)    | Option selection, feature list         |
| [OverlaySpinnerPreference](../components/overlayspinnerpreference) | Advanced selector based on BasicComponent (uses COUIPopupUtils; requires `Scaffold`)    | Advanced options, feature list         |
| [OverlayDropdownMenu](../components/overlaydropdownmenu)       | Action menu based on BasicComponent (uses COUIPopupUtils; requires `Scaffold`)          | Action menus, multi-select choices     |
| [OverlayIconDropdownMenu](../components/overlayicondropdownmenu) | Icon-button action menu (uses COUIPopupUtils; requires `Scaffold`)                      | Toolbar actions, overflow menu         |
| [OverlayIconCascadingDropdownMenu](../components/overlayiconcascadingdropdownmenu) | Icon-button cascading two-level menu (uses COUIPopupUtils; requires `Scaffold`)         | Toolbar actions with submenus          |
| [OverlayBottomSheet](../components/overlaybottomsheet)         | Bottom sheet based on BasicComponent (uses COUIPopupUtils; requires `Scaffold`)         | Bottom drawer, additional options      |
| [OverlayDialog](../components/overlaydialog)                   | Dialog window based on BasicComponent (uses COUIPopupUtils; requires `Scaffold`)        | Prompts, action confirmation           |
| [LoadingDialog](../components/loadingdialog)                   | Loading dialog with spinner (Overlay and Window variants)                                | Blocking loading feedback              |
| [SecurityDialog](../components/securitydialog)                 | Security statement dialog (Overlay and Window variants)                                  | Privacy statement, security confirmation |
| [DialogButtonBar](../components/dialogbuttonbar)               | Dialog button bar that auto-stacks vertically when labels do not fit                      | Dialog actions                         |
| [WindowListPopup](../components/windowlistpopup)     | Window-level list popup component                                                        | Option selection, feature list         |
| [WindowCascadingListPopup](../components/windowcascadinglistpopup) | Window-level two-level cascading list popup                                              | Submenus, grouped action panels        |
| [WindowDropdownPreference](../components/windowdropdownpreference) | Window-level dropdown selector component                                                 | Option selection, feature list         |
| [WindowSpinnerPreference](../components/windowspinnerpreference)   | Window-level advanced selector component                                                 | Advanced options, feature list         |
| [WindowDropdownMenu](../components/windowdropdownmenu) | Window-level action menu component                                                       | Action menus, multi-select choices     |
| [WindowIconDropdownMenu](../components/windowicondropdownmenu) | Window-level icon-button action menu component                                           | Toolbar actions, overflow menu         |
| [WindowIconCascadingDropdownMenu](../components/windowiconcascadingdropdownmenu) | Window-level icon-button cascading two-level menu                                        | Toolbar actions with submenus          |
| [WindowBottomSheet](../components/windowbottomsheet) | Window-level bottom sheet component                                                      | Bottom drawer, additional options      |
| [WindowDialog](../components/windowdialog)           | Window-level dialog component                                                            | Prompts, action confirmation           |
