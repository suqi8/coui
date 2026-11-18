// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.ui.NavDisplay
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.ColorSchemeMode
import io.github.suqi8.coui.kmp.theme.ThemeController

@Composable
fun Demo(demoId: String? = null) {
    val controller = remember { ThemeController(ColorSchemeMode.System) }
    COUITheme(controller = controller) {
        if (demoId == null) {
            DemoSelection()
        } else {
            availableComponents.first { it.id == demoId }.demo()
        }
    }
}

private data class AvailableComponent(val name: String, val id: String, val demo: @Composable () -> Unit)

private val availableComponents = listOf(
    AvailableComponent("Scaffold", "scaffold") { ScaffoldDemo() },
    AvailableComponent("Surface", "surface") { SurfaceDemo() },
    AvailableComponent("TopAppBar", "topAppBar") { TopAppBarDemo() },
    AvailableComponent("NavigationBar", "navigationBar") { NavigationBarDemo() },
    AvailableComponent("NavigationPopupMenu", "navigationPopupMenu") { NavigationPopupMenuDemo() },
    AvailableComponent("FloatingAccessoryNavigationBar", "floatingAccessoryNavigationBar") { FloatingAccessoryNavigationBarDemo() },
    AvailableComponent("NavigationRail", "navigationRail") { NavigationRailDemo() },
    AvailableComponent("SideNavigationBar", "sideNavigationBar") { SideNavigationBarDemo() },
    AvailableComponent("SidePaneLayout", "sidePaneLayout") { SidePaneLayoutDemo() },
    AvailableComponent("ListDetailLayout", "listDetailLayout") { ListDetailLayoutDemo() },
    AvailableComponent("TabRow", "tabRow") { TabRowDemo() },
    AvailableComponent("TabWithSearchLayout", "tabWithSearchLayout") { TabWithSearchLayoutDemo() },
    AvailableComponent("SegmentButton", "segmentButton") { SegmentButtonDemo() },
    AvailableComponent("ChipTab", "chipTab") { ChipTabDemo() },
    AvailableComponent("ChipGroup", "chipGroup") { ChipGroupDemo() },
    AvailableComponent("Card", "card") { CardDemo() },
    AvailableComponent("PageIndicator", "pageIndicator") { PageIndicatorDemo() },
    AvailableComponent("Banner", "banner") { BannerDemo() },
    AvailableComponent("ResponsiveGrid", "responsiveGrid") { ResponsiveGridDemo() },
    AvailableComponent("SpringChain", "springChain") { SpringChainDemo() },
    AvailableComponent("BasicComponent", "basicComponent") { BasicComponentDemo() },
    AvailableComponent("Button", "button") { ButtonDemo() },
    AvailableComponent("TwoTextButton", "twoTextButton") { TwoTextButtonDemo() },
    AvailableComponent("IconButton", "iconButton") { IconButtonDemo() },
    AvailableComponent("Text", "text") { TextDemo() },
    AvailableComponent("LinkText", "linkText") { LinkTextDemo() },
    AvailableComponent("SmallTitle", "smallTitle") { SmallTitleDemo() },
    AvailableComponent("PreferenceCategory", "preferenceCategory") { PreferenceCategoryDemo() },
    AvailableComponent("TextField", "textField") { TextFieldDemo() },
    AvailableComponent("LockScreenPasswordInput", "lockScreenPasswordInput") { LockScreenPasswordInputDemo() },
    AvailableComponent("NumericKeyboard", "numericKeyboard") { NumericKeyboardDemo() },
    AvailableComponent("SimpleLock", "simpleLock") { SimpleLockDemo() },
    AvailableComponent("LockPatternView", "lockPatternView") { LockPatternViewDemo() },
    AvailableComponent("Switch", "switch") { SwitchDemo() },
    AvailableComponent("Checkbox", "checkbox") { CheckboxDemo() },
    AvailableComponent("RadioButton", "radioButton") { RadioButtonDemo() },
    AvailableComponent("Slider", "slider") { SliderDemo() },
    AvailableComponent("IntentSlider", "intentSlider") { IntentSliderDemo() },
    AvailableComponent("NumberPicker", "numberPicker") { NumberPickerDemo() },
    AvailableComponent("ExposureBar", "exposureBar") { ExposureBarDemo() },
    AvailableComponent("CalendarPicker", "calendarPicker") { CalendarPickerDemo() },
    AvailableComponent("LunarDatePicker", "lunarDatePicker") { LunarDatePickerDemo() },
    AvailableComponent("ProgressIndicator", "progressIndicator") { ProgressIndicatorDemo() },
    AvailableComponent("InstallLoadProgress", "installLoadProgress") { InstallLoadProgressDemo() },
    AvailableComponent("Snackbar", "snackbar") { SnackbarDemo() },
    AvailableComponent("CustomSnackbar", "customSnackbar") { CustomSnackbarDemo() },
    AvailableComponent("TagView", "tagView") { TagViewDemo() },
    AvailableComponent("MarqueeText", "marqueeText") { MarqueeTextDemo() },
    AvailableComponent("Icon", "icon") { IconDemo() },
    AvailableComponent("FloatingActionButton", "floatingActionButton") { FloatingActionButtonDemo() },
    AvailableComponent("FloatingToolbar", "floatingToolbar") { FloatingToolbarDemo() },
    AvailableComponent("FloatingEditToolbar", "floatingEditToolbar") { FloatingEditToolbarDemo() },
    AvailableComponent("Divider", "divider") { DividerDemo() },
    AvailableComponent("PullToRefresh", "pullToRefresh") { PullToRefreshDemo() },
    AvailableComponent("BreadcrumbBar", "breadcrumbBar") { BreadcrumbBarDemo() },
    AvailableComponent("Tooltip", "tooltip") { TooltipDemo() },
    AvailableComponent("ButtonPreference", "buttonPreference") { ButtonPreferenceDemo() },
    AvailableComponent("ListPreference", "listPreference") { ListPreferenceDemo() },
    AvailableComponent("MarkPreference", "markPreference") { MarkPreferenceDemo() },
    AvailableComponent("RangeSliderPreference", "rangeSliderPreference") { RangeSliderPreferenceDemo() },
    AvailableComponent("RecommendedPreference", "recommendedPreference") { RecommendedPreferenceDemo() },
    AvailableComponent("Chip", "chip") { ChipDemo() },
    AvailableComponent("ColorSwatchPicker", "colorSwatchPicker") { ColorSwatchPickerDemo() },
    AvailableComponent("DatePicker", "datePicker") { DatePickerDemo() },
    AvailableComponent("DateTimePicker", "dateTimePicker") { DateTimePickerDemo() },
    AvailableComponent("TimePicker", "timePicker") { TimePickerDemo() },
    AvailableComponent("SliderPreference", "sliderPreference") { SliderPreferenceDemo() },
    AvailableComponent("SwitchLoadingPreference", "switchLoadingPreference") { SwitchLoadingPreferenceDemo() },
    AvailableComponent("Badge", "badge") { BadgeDemo() },
    AvailableComponent("LoadingButton", "loadingButton") { LoadingButtonDemo() },
    AvailableComponent("CardListGroup", "cardListGroup") { CardListGroupDemo() },
    AvailableComponent("DialogButtonBar", "dialogButtonBar") { DialogButtonBarDemo() },
    AvailableComponent("LoadingDialog", "loadingDialog") { LoadingDialogDemo() },
    AvailableComponent("SecurityDialog", "securityDialog") { SecurityDialogDemo() },
    AvailableComponent("FullPageStatement", "fullPageStatement") { FullPageStatementDemo() },
    AvailableComponent("TopTips", "topTips") { TopTipsDemo() },
    AvailableComponent("CodeTextField", "codeTextField") { CodeTextFieldDemo() },
    AvailableComponent("InputView", "inputView") { InputViewDemo() },
    AvailableComponent("ToolbarSearch", "toolbarSearch") { ToolbarSearchDemo() },
    AvailableComponent("ScrollBar", "scrollBar") { ScrollBarDemo() },
    AvailableComponent("Stepper", "stepper") { StepperDemo() },
    AvailableComponent("SearchBar", "searchBar") { SearchBarDemo() },
    AvailableComponent("SearchHistory", "searchHistory") { SearchHistoryDemo() },
    AvailableComponent("AlphabetIndexBar", "alphabetIndexBar") { AlphabetIndexBarDemo() },
    AvailableComponent("SwipeActionItem", "swipeActionItem") { SwipeActionItemDemo() },
    AvailableComponent("ExpandableGroup", "expandableGroup") { ExpandableGroupDemo() },
    AvailableComponent("EmptyState", "emptyState") { EmptyStateDemo() },
    AvailableComponent("CompleteState", "completeState") { CompleteStateDemo() },
    AvailableComponent("ColorPicker", "colorPicker") { ColorPickerDemo() },
    AvailableComponent("ColorPalette", "colorPalette") { ColorPaletteDemo() },
    AvailableComponent("ArrowPreference", "arrowPreference") { ArrowPreferenceDemo() },
    AvailableComponent("SwitchPreference", "switchPreference") { SwitchPreferenceDemo() },
    AvailableComponent("SwitchWithDividerPreference", "switchWithDividerPreference") { SwitchWithDividerPreferenceDemo() },
    AvailableComponent("CheckboxPreference", "checkboxPreference") { CheckboxPreferenceDemo() },
    AvailableComponent("MarkWithDividerPreference", "markWithDividerPreference") { MarkWithDividerPreferenceDemo() },
    AvailableComponent("SlideSelectPreference", "slideSelectPreference") { SlideSelectPreferenceDemo() },
    AvailableComponent("MultiSelectListPreference", "multiSelectListPreference") { MultiSelectListPreferenceDemo() },
    AvailableComponent("CheckboxWithDividerPreference", "checkboxWithDividerPreference") { CheckboxWithDividerPreferenceDemo() },
    AvailableComponent("RadioButtonPreference", "radioButtonPreference") { RadioButtonPreferenceDemo() },
    AvailableComponent("InputPreference", "inputPreference") { InputPreferenceDemo() },
    AvailableComponent("EditTextPreference", "editTextPreference") { EditTextPreferenceDemo() },
    AvailableComponent("StepperPreference", "stepperPreference") { StepperPreferenceDemo() },
    AvailableComponent("CardInstructionPreference", "cardInstructionPreference") { CardInstructionPreferenceDemo() },
    AvailableComponent("CardEntrancePreference", "cardEntrancePreference") { CardEntrancePreferenceDemo() },
    AvailableComponent("AppInfoPreference", "appInfoPreference") { AppInfoPreferenceDemo() },
    AvailableComponent("StatusSwitchingPreference", "statusSwitchingPreference") { StatusSwitchingPreferenceDemo() },
    AvailableComponent("LoadInstallProgressPreference", "loadInstallProgressPreference") { LoadInstallProgressPreferenceDemo() },
    AvailableComponent("LoadingPreferenceCategory", "loadingPreferenceCategory") { LoadingPreferenceCategoryDemo() },
    AvailableComponent("CheckboxPreferenceCategory", "checkboxPreferenceCategory") { CheckboxPreferenceCategoryDemo() },
    AvailableComponent("ActivityDialogPreference", "activityDialogPreference") { ActivityDialogPreferenceDemo() },
    AvailableComponent("CardButtonPreference", "cardButtonPreference") { CardButtonPreferenceDemo() },
    AvailableComponent("SpannablePreference", "spannablePreference") { SpannablePreferenceDemo() },
    AvailableComponent("PagerHeaderPreference", "pagerHeaderPreference") { PagerHeaderPreferenceDemo() },
    AvailableComponent("PagerFooterPreference", "pagerFooterPreference") { PagerFooterPreferenceDemo() },
    AvailableComponent("BottomPreference", "bottomPreference") { BottomPreferenceDemo() },
    AvailableComponent("OverlayListPopup", "overlayListPopup") { OverlayListPopupDemo() },
    AvailableComponent("OverlayCascadingListPopup", "overlayCascadingListPopup") { OverlayCascadingListPopupDemo() },
    AvailableComponent("OverlayDropdownPreference", "overlayDropdownPreference") { OverlayDropdownPreferenceDemo() },
    AvailableComponent("OverlaySpinnerPreference", "overlaySpinnerPreference") { OverlaySpinnerPreferenceDemo() },
    AvailableComponent("OverlayDropdownMenu", "overlayDropdownMenu") { OverlayDropdownMenuDemo() },
    AvailableComponent("OverlayIconDropdownMenu", "overlayIconDropdownMenu") { OverlayIconDropdownMenuDemo() },
    AvailableComponent("OverlayIconCascadingDropdownMenu", "overlayIconCascadingDropdownMenu") { OverlayIconCascadingDropdownMenuDemo() },
    AvailableComponent("OverlayBottomSheet", "overlayBottomSheet") { OverlayBottomSheetDemo() },
    AvailableComponent("OverlayDialog", "overlayDialog") { OverlayDialogDemo() },
    AvailableComponent("WindowListPopup", "windowListPopup") { WindowListPopupDemo() },
    AvailableComponent("WindowCascadingListPopup", "windowCascadingListPopup") { WindowCascadingListPopupDemo() },
    AvailableComponent("WindowDropdownPreference", "windowDropdownPreference") { WindowDropdownPreferenceDemo() },
    AvailableComponent("WindowSpinnerPreference", "windowSpinnerPreference") { WindowSpinnerPreferenceDemo() },
    AvailableComponent("WindowDropdownMenu", "windowDropdownMenu") { WindowDropdownMenuDemo() },
    AvailableComponent("WindowIconDropdownMenu", "windowIconDropdownMenu") { WindowIconDropdownMenuDemo() },
    AvailableComponent("WindowIconCascadingDropdownMenu", "windowIconCascadingDropdownMenu") { WindowIconCascadingDropdownMenuDemo() },
    AvailableComponent("ContextMenu", "contextMenu") { ContextMenuDemo() },
    AvailableComponent("WindowBottomSheet", "windowBottomSheet") { WindowBottomSheetDemo() },
    AvailableComponent("WindowDialog", "windowDialog") { WindowDialogDemo() },
    AvailableComponent("GuideDialog", "guideDialog") { GuideDialogDemo() },
    AvailableComponent("ListDialog", "listDialog") { ListDialogDemo() },
    AvailableComponent("ProgressDialog", "progressDialog") { ProgressDialogDemo() },
    AvailableComponent("InputDialog", "inputDialog") { InputDialogDemo() },
    AvailableComponent("UserStatementDialog", "userStatementDialog") { UserStatementDialogDemo() },
    AvailableComponent("IndividualStatementDialog", "individualStatementDialog") { IndividualStatementDialogDemo() },
    AvailableComponent("CameraModeBar", "cameraModeBar") { CameraModeBarDemo() },
    AvailableComponent("CameraLensBar", "cameraLensBar") { CameraLensBarDemo() },
    AvailableComponent("CameraShutterButton", "cameraShutterButton") { CameraShutterButtonDemo() },
)

@Composable
private fun DemoSelection() {
    val backStack = remember { mutableStateListOf<NavKey>(DemoScreen.Home) }
    val entryProvider = remember(backStack) {
        entryProvider<NavKey> {
            entry(DemoScreen.Home) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(demoBackground()),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                            .widthIn(max = 600.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        availableComponents.forEach { demo ->
                            TextButton(
                                text = demo.name,
                                onClick = { backStack.add(DemoScreen.Component(demo.id)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.textButtonColorsPrimary(),
                            )
                        }
                    }
                }
            }

            availableComponents.forEach { component ->
                entry(DemoScreen.Component(component.id)) {
                    Column {
                        component.demo()
                    }
                }
            }
        }
    }

    val entries = rememberDecoratedNavEntries(
        backStack = backStack,
        entryProvider = entryProvider,
    )

    NavDisplay(
        entries = entries,
        onBack = { backStack.removeLast() },
    )
}

private sealed interface DemoScreen : NavKey {
    data object Home : DemoScreen
    data class Component(val id: String) : DemoScreen
}
