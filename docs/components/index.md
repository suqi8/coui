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
EN after NavigationBar: | [FloatingAccessoryNavigationBar](../components/floatingaccessorynavigationbar) | Floating navigation capsule with side accessories | Main page switching with create / search actions |
| [NavigationRail](../components/navigationrail)             | Side navigation component                  | Main page switching (Large screen)    |
| [SideNavigationBar](../components/sidenavigationbar) | Responsive side navigation drawer (docked / overlay) | Main page switching on tablets and desktop |
| [SidePaneLayout](../components/sidepanelayout) | Collapsible side pane + content split | Folder / category pane on large screens |
| [ListDetailLayout](../components/listdetaillayout) | Two-pane list-detail container | Settings-style master-detail pages |
| [TabRow](../components/tabrow)                             | Horizontal tab bar                         | Content category browsing             |
| [SegmentButton](../components/segmentbutton)               | Segmented control with a sliding indicator | Switching between a few views, filters |
| [BreadcrumbBar](../components/breadcrumbbar)               | Horizontal breadcrumb navigation bar         | File path navigation, location trail   |
| [Card](../components/card)                                 | Container with related information         | Information display, content grouping |
EN after Card: | [PageIndicator](../components/pageindicator) | Dot page indicator with trace animation | Carousels, onboarding pages |
| [Banner](../components/banner) | Auto-scrolling rounded card carousel | Promotions, featured content |
| [ResponsiveGrid](../components/responsivegrid) | Responsive column grid and percent-width sizing | Tile grids, centred content on wide screens |
| [BasicComponent](../components/basiccomponent)             | Universal base component                   | Custom component development          |
| [Button](../components/button)                             | Interactive element for triggering actions | Form submission, action confirmation  |
| [LoadingButton](../components/loadingbutton)               | Text button with a built-in loading state  | Asynchronous action submission        |
| [IconButton](../components/iconbutton)                     | Icon button component                      | Auxiliary actions, toolbars           |
| [Chip](../components/chip)                                 | Checkable capsule filter tag               | Content filtering, tag selection      |
| [ChipTab](../components/chiptab)                           | Chip-style single-selection tabs           | Page filtering, category switching    |
| [Text](../components/text)                                 | Display text content with various styles   | Titles, body text, descriptive text   |
| [SmallTitle](../components/smalltitle)                     | Small title component                      | Auxiliary titles, category labels     |
| [TextField](../components/textfield)                       | Receives user text input                   | Form filling, search boxes            |
| [InputView](../components/inputview)                       | Card-style input field                     | Form filling, dialog input            |
| [CodeTextField](../components/codetextfield)               | Verification code input with cells         | OTP / verification code entry         |
| [LockScreenPasswordInput](../components/lockscreenpasswordinput) | Lock-screen password field with a round next button | Lock screen / password confirmation |
| [NumericKeyboard](../components/numerickeyboard) | Secure numeric keyboard with round keys | PIN entry, lock screen |
| [SimpleLock](../components/simplelock) | PIN dot row with fill / shake animations | PIN progress display |
| [LockPatternView](../components/lockpatternview) | 3x3 pattern lock with error state | Pattern unlock / setup |
| [Switch](../components/switch)                             | Binary state toggle control                | Setting switches, feature enabling    |
| [Checkbox](../components/checkbox)                         | Multiple selection control                 | Multiple choices, terms agreement     |
| [RadioButton](../components/radiobutton)                   | Single selection control                   | Exclusive choices, option selection   |
| [Slider](../components/slider)                             | Sliding control for value adjustment       | Volume control, range selection       |
| [IntentSlider](../components/intentslider)                 | Slim media progress bar with buffering     | Video/music playback progress         |
| [NumberPicker](../components/numberpicker)                 | Vertical scroll picker for number selection | Time picker, quantity selection       |
| [DatePicker](../components/datepicker)                     | Date wheel picker (year / month / day)     | Date selection, forms                 |
| [TimePicker](../components/timepicker)                     | Time wheel picker (hour / minute)          | Time selection, alarms                |
| [DateTimePicker](../components/datetimepicker)             | Date + time wheel picker                   | Reminders, scheduling                 |
| [CalendarPicker](../components/calendarpicker)             | Month grid calendar with paging and a date wheel | Date selection, scheduling        |
| [LunarDatePicker](../components/lunardatepicker)           | Chinese lunar calendar wheel picker        | Birthdays, lunar dates                |
| [Stepper](../components/stepper)                           | Increment / decrement integer control      | Quantity adjustment, value tuning     |
| [ProgressIndicator](../components/progressindicator)       | Displays operation progress status         | Loading, progress display             |
| [InstallLoadProgress](../components/installloadprogress)   | Download / install button with progress    | App store download, install actions   |
| [Snackbar](../components/snackbar)                         | Temporary message bar for brief feedback   | Operation result, status notification |
| [TopTips](../components/toptips)                           | Tip banner at the top of a page            | Page hints, announcements             |
| [Tooltip](../components/tooltip)                           | Brief label shown on hover or long press   | Icon button labels, element hints     |
| [Badge](../components/badge)                               | Small status overlay on an anchor          | Unread counts, status dots            |
| [TagView](../components/tagview)                           | Small text tag such as NEW / BETA          | Feature labels, status tags           |
| [MarqueeText](../components/marqueetext)                   | Auto-scrolling single-line text with fading edges | Tip banners, long one-line hints |
| [Icon](../components/icon)                                 | Icon display component                     | Icon buttons, status indicators       |
| [FloatingActionButton](../components/floatingactionbutton) | Floating action button                     | Primary actions, quick functions      |
| [FloatingToolbar](../components/floatingtoolbar)           | Floating toolbar                           | Quick actions, information display    |
| [Divider](../components/divider)                           | Content separator                          | Block separation, hierarchy division  |
| [PullToRefresh](../components/pulltorefresh)               | Pull-down refresh operation                | Data update, page refresh             |
| [SearchBar](../components/searchbar)                       | Search input field                         | Content search, quick find            |
| [SearchHistory](../components/searchhistory)               | Search history chips with fold and clear   | Search pages, recent queries          |
| [AlphabetIndexBar](../components/alphabetindexbar)         | Side letter index with popup bubble        | Contact lists, app lists              |
| [SwipeActionItem](../components/swipeactionitem)           | List row that slides to reveal actions     | Swipe to delete, quick actions        |
| [ExpandableGroup](../components/expandablegroup)           | Expandable list group with rotating arrow  | Collapsible settings groups           |
| [FullPageStatement](../components/fullpagestatement)       | Full-page user agreement statement         | First-launch agreement, privacy statement |
| [EmptyState](../components/emptystate)                     | Empty page with illustration, text and action | Empty lists, no search results     |
| [CompleteState](../components/completestate)               | Animated success / failure / waiting result | Operation result pages              |
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
| [InputPreference](../components/inputpreference)               | Card list row with an inline input field                                                 | Device names, inline text settings     |
| [EditTextPreference](../components/edittextpreference)         | Preference row that edits its value in an input dialog (requires `Scaffold`)             | Nicknames, short text settings         |
| [StepperPreference](../components/stepperpreference)           | Preference row with a stepper                                                            | Counts, small numeric settings         |
| [CardInstructionPreference](../components/cardinstructionpreference) | Page-top card with an illustration pager and description                           | Feature introductions, gesture guides  |
| [CardEntrancePreference](../components/cardentrancepreference) | Grid of small / large entrance cards                                                     | Settings home entrances                |
| [AppInfoPreference](../components/appinfopreference)           | About-page header with app icon, name and version                                        | About pages                            |
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
| [ContextMenu](../components/contextmenu) | Overlay / window menu opened at the long-press or right-click point | File and list item actions |
| [WindowBottomSheet](../components/windowbottomsheet) | Window-level bottom sheet component                                                      | Bottom drawer, additional options      |
| [WindowDialog](../components/windowdialog)           | Window-level dialog component                                                            | Prompts, action confirmation           |
