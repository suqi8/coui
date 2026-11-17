// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color

/**
 * The default color scheme for the COUI components.
 *
 * @param primary The primary color. Cases: Switch, Button, Slider.
 * @param onPrimary The color of the text on primary color. Cases: Switch, Button, Slider.
 * @param disabledOnPrimaryButton The color of the button on disabled primary color (COUI coui_button_main_disable_text
 *   #8AFFFFFF / #29FFFFFF, also the disabled checkbox tick and radio centre).
 * @param secondaryVariant The variant color of the secondary color.
 * @param disabledSecondaryVariant The disabled secondary color.
 * @param secondaryContainer The container color of the secondary color.
 * @param secondary The translucent theme tint (COUI couiColorSecondary, coui_color_secondary_blue
 *   #260080FF / #401A8CFF). Cases: Dropdown, ProgressIndicator, InstallLoadProgress.
 * @param outline The outline color.
 * @param surfaceContainerHighest The container color of the surface color.
 * @param sliderBackground The track color of the slider (COUI couiColorContainer12 via coui_seekbar_background_selector).
 * @param dialogButton The fill of the dialog bar buttons (COUI couiColorContainer8). Cases: Dialog, BottomSheet.
 * @param dragHandle The color of the bottom sheet drag handle (COUI couiColorControls).
 * @param secondaryButton The opaque fill of secondary buttons (COUI couiColorContainer8 composited over couiColorBackgroundElevated). Cases: Button, TextButton, LoadingButton.
 * @param disabledSecondaryButton The fill of disabled secondary buttons (COUI couiColorContainer8, not composited).
 * @param disabledOnSecondaryButton The label of disabled secondary buttons (COUI coui_btn_secondary_text_color disabled state).
 * @param transparentButton The translucent fill of transparent buttons laid over images (COUI coui_button_translate_color).
 * @param onTransparentButton The label of transparent buttons (COUI coui_btn_tiny_translate_text_color).
 * @param disabledOnTransparentButton The label of disabled transparent buttons.
 * @param backgroundTop The topmost floating surface (COUI couiColorBackgroundTop). Cases: Snackbar.
 * @param primaryTextOnPopup The accent text on floating surfaces (COUI couiColorPrimaryTextOnPopup). Cases: Snackbar action.
 * @param press The pressed state layer drawn over a component (COUI couiColorPress). Cases: COUIIndication.
 * @param hover The hovered state layer drawn over a component (COUI couiColorHover). Cases: COUIIndication.
 * @param focus The focused / selected state layer of list rows (COUI couiColorFocus). Cases: COUIIndication.
 * @param disabledProgress The progress color of a disabled linear progress bar (COUI coui_progressbar_color_disabled,
 *   the candidate for couiProgressbarProgressColorDisabled). Cases: LinearProgressIndicator.
 * @param disabledProgressBackground The track color of a disabled linear progress bar (COUI
 *   coui_progressbar_background_color_disabled, the candidate for couiProgressbarBackgroundColorDisabled).
 *   Cases: LinearProgressIndicator.
 * @param disabledSliderThumb The thumb and the on-progress key points of a disabled slider (COUI
 *   coui_seekbar_thumb_disable_color / coui_seekbar_active_mark_disable_color). Cases: Slider.
 * @param disabledSliderKeyPoint The off-progress key points of a disabled slider (COUI
 *   coui_seekbar_inactive_mark_disable_color). Cases: Slider.
 * @param sliderThumbShadow The drop shadow of the slider thumb (COUI coui_seekbar_thumb_shadow_color). Cases: Slider.
 * @param disabledTextFieldBorder The border / underline of a disabled text field (COUI
 *   coui_textinput_stroke_color_disabled via couiDisabledStrokeColor). Cases: TextField.
 * @param quarternaryNeutral The faintest neutral (COUI couiColorQuarternaryNeutral #14000000 / #26FFFFFF).
 *   Cases: the disabled Checkbox and RadioButton rings.
 * @param container8 The translucent container fill (COUI couiColorContainer8 #14000000 / #1AFFFFFF).
 *   Cases: the unselected Chip fill.
 * @param disabledChipText The label of a disabled unselected chip (COUI chip_unchecked_text_disable_color
 *   #42000000 / #29FFFFFF). Cases: Chip.
 * @param focusOutline The keyboard focus outline (COUI couiColorFocusOutline, coui_color_focus_outline_blue
 *   #660080FF / #665C9DFF). Cases: Chip.
 * @param primaryText Theme-colored text and icons (COUI couiColorPrimaryText, coui_color_primary_text_blue
 *   #0080FF / #5C9DFF; also couiColorOnSecondary).
 * @param labelTheme Theme-colored labels (COUI couiColorLabelTheme, coui_color_label_theme_blue #0080FF / #1A8CFF).
 * @param link Link text (COUI couiColorLink = ?couiColorLabelTheme).
 * @param disable The disabled theme color (COUI couiColorDisable, coui_color_disable_blue #4D0080FF / #4D1A8CFF).
 * @param containerTheme The opaque theme container (COUI couiColorContainerTheme, coui_color_container_theme_blue
 *   #0080FF / #1A8CFF).
 * @param containerThemeHalftone The translucent theme container (COUI couiColorContainerThemeHalftone,
 *   coui_color_container_theme_halftone_blue #260080FF / #401A8CFF).
 * @param textColorHighlight The text selection highlight (COUI android:textColorHighlight = ?couiColorSecondary,
 *   coui_color_secondary_blue #260080FF / #401A8CFF; Theme.COUI.Orange / Red / Yellow override it).
 * @param errorDisabled The disabled error color (COUI couiColorErrorDisabled, coui_color_error_disabled
 *   #4DDB382C / #4DFF6C61).
 * @param errorTextBg The error underline / selection tint of inputs (COUI couiColorErrorTextBg,
 *   coui_color_error_text_bg #4DE82A18 / #EA3939).
 * @param success The success color (COUI couiColorSuccess, coui_color_success #00BD13 / #24B232).
 * @param tips The tips color (COUI couiColorTips, coui_color_tips #0080FF / #1A8CFF).
 * @param additional The additional accent (COUI couiColorAdditional, coui_color_additional_blue #0080FF / #1A8CFF).
 * @param assist The assist accent (COUI couiColorAssist, coui_color_assist_blue #5C93E5).
 * @param background The page background (COUI couiColorBackground, coui_color_background #FFFFFF / #000000).
 * @param surface Panels floating over the page: dialogs, bottom sheets and popups (COUI couiColorSurface,
 *   coui_color_surface #FFFFFF / #1E1E1E).
 * @param backgroundElevated The elevated page background (COUI couiColorBackgroundElevated,
 *   coui_color_background_elevated #FFFFFF / #1E1E1E).
 * @param backgroundElevatedWithCard The elevated page background behind cards (COUI
 *   couiColorBackgroundElevatedWithCard, coui_color_background_elevatedWithCard #F0F1F2 / #1E1E1E).
 * @param backgroundWithCard The page background behind cards (COUI couiColorBackgroundWithCard,
 *   coui_color_background_with_card #F0F1F2 / #000000). Cases: Scaffold and other page backgrounds.
 * @param surfaceTop The topmost panel (COUI couiColorSurfaceTop, coui_color_surface_top #FFFFFF / #333333).
 * @param surfaceWithCard The panel background behind cards (COUI couiColorSurfaceWithCard,
 *   coui_color_surface_with_card #F0F1F2 / #1E1E1E).
 * @param bar Bars (COUI couiColorBar, coui_color_bar #FAFAFA / #1E1E1E).
 * @param bottomBar Bottom bars (COUI couiColorBottomBar, coui_color_bottom_bar #FAFAFA; values-night #1E1E1E).
 * @param card Cards (COUI couiColorCard, coui_color_card #FFFFFF / #1AFFFFFF).
 * @param cardBackground Card backgrounds (COUI couiColorCardBackground, coui_color_card_background
 *   #FFFFFF / #1AFFFFFF).
 * @param cardPressed Pressed cards (COUI couiColorCardPressed, coui_color_card_pressed #E6E6E6 / #33FFFFFF).
 * @param labelPrimary Primary labels (COUI couiColorLabelPrimary, coui_color_label_primary #E6000000 / #E6FFFFFF).
 * @param labelPrimaryVariant (COUI couiColorLabelPrimaryVariant, coui_color_label_primary_variant
 *   #B8000000 / #B8FFFFFF).
 * @param labelSecondary Secondary labels (COUI couiColorLabelSecondary, coui_color_label_secondary
 *   #8A000000 / #8AFFFFFF).
 * @param labelSecondaryVariant (COUI couiColorLabelSecondaryVariant, coui_color_label_secondary_variant
 *   #66000000 / #66FFFFFF).
 * @param labelTertiary Tertiary labels (COUI couiColorLabelTertiary, coui_color_label_tertiary #42000000 / #4DFFFFFF).
 * @param labelQuaternary Quaternary labels (COUI couiColorLabelQuaternary, coui_color_label_quaternary
 *   #14000000 / #26FFFFFF).
 * @param labelOnColor Labels on colored fills (COUI couiColorLabelOnColor, coui_color_label_on_color #FFFFFF).
 * @param disabledNeutral Disabled neutral content (COUI couiColorDisabledNeutral, coui_color_disabled_neutral
 *   #42000000 / #4DFFFFFF).
 * @param hintNeutral Hint text (COUI couiColorHintNeutral, coui_color_hint_neutral #42000000 / #4DFFFFFF).
 * @param container4 (COUI couiColorContainer4, coui_color_container4 #0A000000 / #1AFFFFFF).
 * @param container12 (COUI couiColorContainer12, coui_color_container12 #1F000000 / #33FFFFFF).
 * @param container16 (COUI couiColorContainer16, coui_color_container16 #29000000 / #40FFFFFF).
 * @param containerSolid (COUI couiColorContainerSolid, coui_color_container_solid #EBEBEB / #262626).
 * @param fillSolid (COUI couiColorFillSolid, coui_color_fill_solid #EBEBEB / #262626).
 * @param fillThin (COUI couiColorFillThin, coui_color_fill_thin #0A000000 / #14FFFFFF).
 * @param controls Control fills such as drag handles (COUI couiColorControls, coui_color_controls
 *   #29000000 / #40FFFFFF).
 * @param divider Dividers (COUI couiColorDivider, coui_color_divider #1F000000 / #33FFFFFF).
 * @param mask Window dimming (COUI couiColorMask, coui_color_mask #33000000 / #99000000).
 * @param pressBackground (COUI couiColorPressBackground, coui_color_press_background #14000000 / #26FFFFFF).
 * @param ripplePressBackground (COUI couiColorRipplePressBackground, coui_color_ripple_press_background
 *   #99B4B4B4 / #80FFFFFF).
 * @param red COUI couiColorRed (coui_color_red). The ten hue groups each carry the hue, its `*Variant`
 *   (couiColor<Hue>Variant, coui_color_<hue>_variant) and its `container*` tint (couiColorContainer<Hue>,
 *   coui_color_container_<hue>), with the `_dark` values in the dark scheme.
 * @param neutralButton Neutral button fill (COUI coui_button_neutral_color #0F000000 / #4CFFFFFF).
 * @param isDark Whether this is a dark scheme (COUI isCOUIDarkTheme). Set by [lightColorScheme],
 *   [darkColorScheme] and the dynamic color schemes.
 */
@Stable
class Colors(
    primary: Color,
    onPrimary: Color,
    error: Color,
    onError: Color,
    disabledOnPrimaryButton: Color,
    secondaryVariant: Color,
    disabledSecondaryVariant: Color,
    secondaryContainer: Color,
    secondary: Color,
    surfaceContainerHighest: Color,
    outline: Color,
    sliderBackground: Color,
    dialogButton: Color,
    dragHandle: Color,
    secondaryButton: Color,
    disabledSecondaryButton: Color,
    disabledOnSecondaryButton: Color,
    transparentButton: Color,
    onTransparentButton: Color,
    disabledOnTransparentButton: Color,
    backgroundTop: Color,
    primaryTextOnPopup: Color,
    press: Color,
    hover: Color,
    focus: Color,
    disabledProgress: Color,
    disabledProgressBackground: Color,
    disabledSliderThumb: Color,
    disabledSliderKeyPoint: Color,
    sliderThumbShadow: Color,
    disabledTextFieldBorder: Color,
    quarternaryNeutral: Color = Color(0x14000000),
    container8: Color = Color(0x14000000),
    disabledChipText: Color = Color(0x42000000),
    focusOutline: Color = Color(0x660080FF),
    primaryText: Color = Color(0xFF0080FF),
    labelTheme: Color = Color(0xFF0080FF),
    link: Color = Color(0xFF0080FF),
    disable: Color = Color(0x4D0080FF),
    containerTheme: Color = Color(0xFF0080FF),
    containerThemeHalftone: Color = Color(0x260080FF),
    textColorHighlight: Color = Color(0x260080FF),
    errorDisabled: Color = Color(0x4DDB382C),
    errorTextBg: Color = Color(0x4DE82A18),
    success: Color = Color(0xFF00BD13),
    tips: Color = Color(0xFF0080FF),
    additional: Color = Color(0xFF0080FF),
    assist: Color = Color(0xFF5C93E5),
    background: Color = Color(0xFFFFFFFF),
    surface: Color = Color(0xFFFFFFFF),
    backgroundElevated: Color = Color(0xFFFFFFFF),
    backgroundElevatedWithCard: Color = Color(0xFFF0F1F2),
    backgroundWithCard: Color = Color(0xFFF0F1F2),
    surfaceTop: Color = Color(0xFFFFFFFF),
    surfaceWithCard: Color = Color(0xFFF0F1F2),
    bar: Color = Color(0xFFFAFAFA),
    bottomBar: Color = Color(0xFFFAFAFA),
    card: Color = Color(0xFFFFFFFF),
    cardBackground: Color = Color(0xFFFFFFFF),
    cardPressed: Color = Color(0xFFE6E6E6),
    labelPrimary: Color = Color(0xE6000000),
    labelPrimaryVariant: Color = Color(0xB8000000),
    labelSecondary: Color = Color(0x8A000000),
    labelSecondaryVariant: Color = Color(0x66000000),
    labelTertiary: Color = Color(0x42000000),
    labelQuaternary: Color = Color(0x14000000),
    labelOnColor: Color = Color(0xFFFFFFFF),
    disabledNeutral: Color = Color(0x42000000),
    hintNeutral: Color = Color(0x42000000),
    container4: Color = Color(0x0A000000),
    container12: Color = Color(0x1F000000),
    container16: Color = Color(0x29000000),
    containerSolid: Color = Color(0xFFEBEBEB),
    fillSolid: Color = Color(0xFFEBEBEB),
    fillThin: Color = Color(0x0A000000),
    controls: Color = Color(0x29000000),
    divider: Color = Color(0x1F000000),
    mask: Color = Color(0x33000000),
    pressBackground: Color = Color(0x14000000),
    ripplePressBackground: Color = Color(0x99B4B4B4),
    red: Color = Color(0xFFEB3B2F),
    redVariant: Color = Color(0xFFE56E65),
    containerRed: Color = Color(0xFFEEDADC),
    orange: Color = Color(0xFFFF7700),
    orangeVariant: Color = Color(0xFFE58F45),
    containerOrange: Color = Color(0xFFFAE7D6),
    yellow: Color = Color(0xFFFFB200),
    yellowVariant: Color = Color(0xFFE5AF2E),
    containerYellow: Color = Color(0xFFF6E7C6),
    green: Color = Color(0xFF00BD13),
    greenVariant: Color = Color(0xFF4BBD57),
    containerGreen: Color = Color(0xFFBADECA),
    mint: Color = Color(0xFF00CC88),
    mintVariant: Color = Color(0xFF39BD91),
    containerMint: Color = Color(0xFFBADEDB),
    cyan: Color = Color(0xFF1DBACC),
    cyanVariant: Color = Color(0xFF39AFBD),
    containerCyan: Color = Color(0xFFBEDADD),
    azure: Color = Color(0xFF00A1FF),
    azureVariant: Color = Color(0xFF45ABE5),
    containerAzure: Color = Color(0xFFCEE5EB),
    blue: Color = Color(0xFF0080FF),
    blueVariant: Color = Color(0xFF5C93E5),
    containerBlue: Color = Color(0xFFC9DAE4),
    violet: Color = Color(0xFF5A47FF),
    violetVariant: Color = Color(0xFF7E73E5),
    containerViolet: Color = Color(0xFFD6D5EE),
    purple: Color = Color(0xFFA723FF),
    purpleVariant: Color = Color(0xFFB873E5),
    containerPurple: Color = Color(0xFFE0D6EB),
    neutralButton: Color = Color(0x0F000000),
    isDark: Boolean = false,
) {
    var primary by mutableStateOf(primary, structuralEqualityPolicy())
        internal set
    var onPrimary by mutableStateOf(onPrimary, structuralEqualityPolicy())
        internal set
    var error by mutableStateOf(error, structuralEqualityPolicy())
        internal set
    var onError by mutableStateOf(onError, structuralEqualityPolicy())
        internal set
    var disabledOnPrimaryButton by mutableStateOf(disabledOnPrimaryButton, structuralEqualityPolicy())
        internal set
    var secondaryVariant by mutableStateOf(secondaryVariant, structuralEqualityPolicy())
        internal set
    var disabledSecondaryVariant by mutableStateOf(disabledSecondaryVariant, structuralEqualityPolicy())
        internal set
    var secondaryContainer by mutableStateOf(secondaryContainer, structuralEqualityPolicy())
        internal set
    var secondary by mutableStateOf(secondary, structuralEqualityPolicy())
        internal set
    var surfaceContainerHighest by mutableStateOf(surfaceContainerHighest, structuralEqualityPolicy())
        internal set
    var outline by mutableStateOf(outline, structuralEqualityPolicy())
        internal set
    var sliderBackground by mutableStateOf(sliderBackground, structuralEqualityPolicy())
        internal set
    var dialogButton by mutableStateOf(dialogButton, structuralEqualityPolicy())
        internal set
    var dragHandle by mutableStateOf(dragHandle, structuralEqualityPolicy())
        internal set
    var secondaryButton by mutableStateOf(secondaryButton, structuralEqualityPolicy())
        internal set
    var disabledSecondaryButton by mutableStateOf(disabledSecondaryButton, structuralEqualityPolicy())
        internal set
    var disabledOnSecondaryButton by mutableStateOf(disabledOnSecondaryButton, structuralEqualityPolicy())
        internal set
    var transparentButton by mutableStateOf(transparentButton, structuralEqualityPolicy())
        internal set
    var onTransparentButton by mutableStateOf(onTransparentButton, structuralEqualityPolicy())
        internal set
    var disabledOnTransparentButton by mutableStateOf(disabledOnTransparentButton, structuralEqualityPolicy())
        internal set
    var backgroundTop by mutableStateOf(backgroundTop, structuralEqualityPolicy())
        internal set
    var primaryTextOnPopup by mutableStateOf(primaryTextOnPopup, structuralEqualityPolicy())
        internal set
    var press by mutableStateOf(press, structuralEqualityPolicy())
        internal set
    var hover by mutableStateOf(hover, structuralEqualityPolicy())
        internal set
    var focus by mutableStateOf(focus, structuralEqualityPolicy())
        internal set
    var disabledProgress by mutableStateOf(disabledProgress, structuralEqualityPolicy())
        internal set
    var disabledProgressBackground by mutableStateOf(disabledProgressBackground, structuralEqualityPolicy())
        internal set
    var disabledSliderThumb by mutableStateOf(disabledSliderThumb, structuralEqualityPolicy())
        internal set
    var disabledSliderKeyPoint by mutableStateOf(disabledSliderKeyPoint, structuralEqualityPolicy())
        internal set
    var sliderThumbShadow by mutableStateOf(sliderThumbShadow, structuralEqualityPolicy())
        internal set
    var disabledTextFieldBorder by mutableStateOf(disabledTextFieldBorder, structuralEqualityPolicy())
        internal set
    var quarternaryNeutral by mutableStateOf(quarternaryNeutral, structuralEqualityPolicy())
        internal set
    var container8 by mutableStateOf(container8, structuralEqualityPolicy())
        internal set
    var disabledChipText by mutableStateOf(disabledChipText, structuralEqualityPolicy())
        internal set
    var focusOutline by mutableStateOf(focusOutline, structuralEqualityPolicy())
        internal set
    var primaryText by mutableStateOf(primaryText, structuralEqualityPolicy())
        internal set
    var labelTheme by mutableStateOf(labelTheme, structuralEqualityPolicy())
        internal set
    var link by mutableStateOf(link, structuralEqualityPolicy())
        internal set
    var disable by mutableStateOf(disable, structuralEqualityPolicy())
        internal set
    var containerTheme by mutableStateOf(containerTheme, structuralEqualityPolicy())
        internal set
    var containerThemeHalftone by mutableStateOf(containerThemeHalftone, structuralEqualityPolicy())
        internal set
    var textColorHighlight by mutableStateOf(textColorHighlight, structuralEqualityPolicy())
        internal set
    var errorDisabled by mutableStateOf(errorDisabled, structuralEqualityPolicy())
        internal set
    var errorTextBg by mutableStateOf(errorTextBg, structuralEqualityPolicy())
        internal set
    var success by mutableStateOf(success, structuralEqualityPolicy())
        internal set
    var tips by mutableStateOf(tips, structuralEqualityPolicy())
        internal set
    var additional by mutableStateOf(additional, structuralEqualityPolicy())
        internal set
    var assist by mutableStateOf(assist, structuralEqualityPolicy())
        internal set
    var background by mutableStateOf(background, structuralEqualityPolicy())
        internal set
    var surface by mutableStateOf(surface, structuralEqualityPolicy())
        internal set
    var backgroundElevated by mutableStateOf(backgroundElevated, structuralEqualityPolicy())
        internal set
    var backgroundElevatedWithCard by mutableStateOf(backgroundElevatedWithCard, structuralEqualityPolicy())
        internal set
    var backgroundWithCard by mutableStateOf(backgroundWithCard, structuralEqualityPolicy())
        internal set
    var surfaceTop by mutableStateOf(surfaceTop, structuralEqualityPolicy())
        internal set
    var surfaceWithCard by mutableStateOf(surfaceWithCard, structuralEqualityPolicy())
        internal set
    var bar by mutableStateOf(bar, structuralEqualityPolicy())
        internal set
    var bottomBar by mutableStateOf(bottomBar, structuralEqualityPolicy())
        internal set
    var card by mutableStateOf(card, structuralEqualityPolicy())
        internal set
    var cardBackground by mutableStateOf(cardBackground, structuralEqualityPolicy())
        internal set
    var cardPressed by mutableStateOf(cardPressed, structuralEqualityPolicy())
        internal set
    var labelPrimary by mutableStateOf(labelPrimary, structuralEqualityPolicy())
        internal set
    var labelPrimaryVariant by mutableStateOf(labelPrimaryVariant, structuralEqualityPolicy())
        internal set
    var labelSecondary by mutableStateOf(labelSecondary, structuralEqualityPolicy())
        internal set
    var labelSecondaryVariant by mutableStateOf(labelSecondaryVariant, structuralEqualityPolicy())
        internal set
    var labelTertiary by mutableStateOf(labelTertiary, structuralEqualityPolicy())
        internal set
    var labelQuaternary by mutableStateOf(labelQuaternary, structuralEqualityPolicy())
        internal set
    var labelOnColor by mutableStateOf(labelOnColor, structuralEqualityPolicy())
        internal set
    var disabledNeutral by mutableStateOf(disabledNeutral, structuralEqualityPolicy())
        internal set
    var hintNeutral by mutableStateOf(hintNeutral, structuralEqualityPolicy())
        internal set
    var container4 by mutableStateOf(container4, structuralEqualityPolicy())
        internal set
    var container12 by mutableStateOf(container12, structuralEqualityPolicy())
        internal set
    var container16 by mutableStateOf(container16, structuralEqualityPolicy())
        internal set
    var containerSolid by mutableStateOf(containerSolid, structuralEqualityPolicy())
        internal set
    var fillSolid by mutableStateOf(fillSolid, structuralEqualityPolicy())
        internal set
    var fillThin by mutableStateOf(fillThin, structuralEqualityPolicy())
        internal set
    var controls by mutableStateOf(controls, structuralEqualityPolicy())
        internal set
    var divider by mutableStateOf(divider, structuralEqualityPolicy())
        internal set
    var mask by mutableStateOf(mask, structuralEqualityPolicy())
        internal set
    var pressBackground by mutableStateOf(pressBackground, structuralEqualityPolicy())
        internal set
    var ripplePressBackground by mutableStateOf(ripplePressBackground, structuralEqualityPolicy())
        internal set
    var red by mutableStateOf(red, structuralEqualityPolicy())
        internal set
    var redVariant by mutableStateOf(redVariant, structuralEqualityPolicy())
        internal set
    var containerRed by mutableStateOf(containerRed, structuralEqualityPolicy())
        internal set
    var orange by mutableStateOf(orange, structuralEqualityPolicy())
        internal set
    var orangeVariant by mutableStateOf(orangeVariant, structuralEqualityPolicy())
        internal set
    var containerOrange by mutableStateOf(containerOrange, structuralEqualityPolicy())
        internal set
    var yellow by mutableStateOf(yellow, structuralEqualityPolicy())
        internal set
    var yellowVariant by mutableStateOf(yellowVariant, structuralEqualityPolicy())
        internal set
    var containerYellow by mutableStateOf(containerYellow, structuralEqualityPolicy())
        internal set
    var green by mutableStateOf(green, structuralEqualityPolicy())
        internal set
    var greenVariant by mutableStateOf(greenVariant, structuralEqualityPolicy())
        internal set
    var containerGreen by mutableStateOf(containerGreen, structuralEqualityPolicy())
        internal set
    var mint by mutableStateOf(mint, structuralEqualityPolicy())
        internal set
    var mintVariant by mutableStateOf(mintVariant, structuralEqualityPolicy())
        internal set
    var containerMint by mutableStateOf(containerMint, structuralEqualityPolicy())
        internal set
    var cyan by mutableStateOf(cyan, structuralEqualityPolicy())
        internal set
    var cyanVariant by mutableStateOf(cyanVariant, structuralEqualityPolicy())
        internal set
    var containerCyan by mutableStateOf(containerCyan, structuralEqualityPolicy())
        internal set
    var azure by mutableStateOf(azure, structuralEqualityPolicy())
        internal set
    var azureVariant by mutableStateOf(azureVariant, structuralEqualityPolicy())
        internal set
    var containerAzure by mutableStateOf(containerAzure, structuralEqualityPolicy())
        internal set
    var blue by mutableStateOf(blue, structuralEqualityPolicy())
        internal set
    var blueVariant by mutableStateOf(blueVariant, structuralEqualityPolicy())
        internal set
    var containerBlue by mutableStateOf(containerBlue, structuralEqualityPolicy())
        internal set
    var violet by mutableStateOf(violet, structuralEqualityPolicy())
        internal set
    var violetVariant by mutableStateOf(violetVariant, structuralEqualityPolicy())
        internal set
    var containerViolet by mutableStateOf(containerViolet, structuralEqualityPolicy())
        internal set
    var purple by mutableStateOf(purple, structuralEqualityPolicy())
        internal set
    var purpleVariant by mutableStateOf(purpleVariant, structuralEqualityPolicy())
        internal set
    var containerPurple by mutableStateOf(containerPurple, structuralEqualityPolicy())
        internal set
    var neutralButton by mutableStateOf(neutralButton, structuralEqualityPolicy())
        internal set
    var isDark by mutableStateOf(isDark, structuralEqualityPolicy())
        internal set

    fun copy(
        primary: Color = this.primary,
        onPrimary: Color = this.onPrimary,
        error: Color = this.error,
        onError: Color = this.onError,
        disabledOnPrimaryButton: Color = this.disabledOnPrimaryButton,
        secondaryVariant: Color = this.secondaryVariant,
        disabledSecondaryVariant: Color = this.disabledSecondaryVariant,
        secondaryContainer: Color = this.secondaryContainer,
        secondary: Color = this.secondary,
        surfaceContainerHighest: Color = this.surfaceContainerHighest,
        outline: Color = this.outline,
        sliderBackground: Color = this.sliderBackground,
        dialogButton: Color = this.dialogButton,
        dragHandle: Color = this.dragHandle,
        secondaryButton: Color = this.secondaryButton,
        disabledSecondaryButton: Color = this.disabledSecondaryButton,
        disabledOnSecondaryButton: Color = this.disabledOnSecondaryButton,
        transparentButton: Color = this.transparentButton,
        onTransparentButton: Color = this.onTransparentButton,
        disabledOnTransparentButton: Color = this.disabledOnTransparentButton,
        backgroundTop: Color = this.backgroundTop,
        primaryTextOnPopup: Color = this.primaryTextOnPopup,
        press: Color = this.press,
        hover: Color = this.hover,
        focus: Color = this.focus,
        disabledProgress: Color = this.disabledProgress,
        disabledProgressBackground: Color = this.disabledProgressBackground,
        disabledSliderThumb: Color = this.disabledSliderThumb,
        disabledSliderKeyPoint: Color = this.disabledSliderKeyPoint,
        sliderThumbShadow: Color = this.sliderThumbShadow,
        disabledTextFieldBorder: Color = this.disabledTextFieldBorder,
        quarternaryNeutral: Color = this.quarternaryNeutral,
        container8: Color = this.container8,
        disabledChipText: Color = this.disabledChipText,
        focusOutline: Color = this.focusOutline,
        primaryText: Color = this.primaryText,
        labelTheme: Color = this.labelTheme,
        link: Color = this.link,
        disable: Color = this.disable,
        containerTheme: Color = this.containerTheme,
        containerThemeHalftone: Color = this.containerThemeHalftone,
        textColorHighlight: Color = this.textColorHighlight,
        errorDisabled: Color = this.errorDisabled,
        errorTextBg: Color = this.errorTextBg,
        success: Color = this.success,
        tips: Color = this.tips,
        additional: Color = this.additional,
        assist: Color = this.assist,
        background: Color = this.background,
        surface: Color = this.surface,
        backgroundElevated: Color = this.backgroundElevated,
        backgroundElevatedWithCard: Color = this.backgroundElevatedWithCard,
        backgroundWithCard: Color = this.backgroundWithCard,
        surfaceTop: Color = this.surfaceTop,
        surfaceWithCard: Color = this.surfaceWithCard,
        bar: Color = this.bar,
        bottomBar: Color = this.bottomBar,
        card: Color = this.card,
        cardBackground: Color = this.cardBackground,
        cardPressed: Color = this.cardPressed,
        labelPrimary: Color = this.labelPrimary,
        labelPrimaryVariant: Color = this.labelPrimaryVariant,
        labelSecondary: Color = this.labelSecondary,
        labelSecondaryVariant: Color = this.labelSecondaryVariant,
        labelTertiary: Color = this.labelTertiary,
        labelQuaternary: Color = this.labelQuaternary,
        labelOnColor: Color = this.labelOnColor,
        disabledNeutral: Color = this.disabledNeutral,
        hintNeutral: Color = this.hintNeutral,
        container4: Color = this.container4,
        container12: Color = this.container12,
        container16: Color = this.container16,
        containerSolid: Color = this.containerSolid,
        fillSolid: Color = this.fillSolid,
        fillThin: Color = this.fillThin,
        controls: Color = this.controls,
        divider: Color = this.divider,
        mask: Color = this.mask,
        pressBackground: Color = this.pressBackground,
        ripplePressBackground: Color = this.ripplePressBackground,
        red: Color = this.red,
        redVariant: Color = this.redVariant,
        containerRed: Color = this.containerRed,
        orange: Color = this.orange,
        orangeVariant: Color = this.orangeVariant,
        containerOrange: Color = this.containerOrange,
        yellow: Color = this.yellow,
        yellowVariant: Color = this.yellowVariant,
        containerYellow: Color = this.containerYellow,
        green: Color = this.green,
        greenVariant: Color = this.greenVariant,
        containerGreen: Color = this.containerGreen,
        mint: Color = this.mint,
        mintVariant: Color = this.mintVariant,
        containerMint: Color = this.containerMint,
        cyan: Color = this.cyan,
        cyanVariant: Color = this.cyanVariant,
        containerCyan: Color = this.containerCyan,
        azure: Color = this.azure,
        azureVariant: Color = this.azureVariant,
        containerAzure: Color = this.containerAzure,
        blue: Color = this.blue,
        blueVariant: Color = this.blueVariant,
        containerBlue: Color = this.containerBlue,
        violet: Color = this.violet,
        violetVariant: Color = this.violetVariant,
        containerViolet: Color = this.containerViolet,
        purple: Color = this.purple,
        purpleVariant: Color = this.purpleVariant,
        containerPurple: Color = this.containerPurple,
        neutralButton: Color = this.neutralButton,
        isDark: Boolean = this.isDark,
    ): Colors = Colors(
        primary,
        onPrimary,
        error,
        onError,
        disabledOnPrimaryButton,
        secondaryVariant,
        disabledSecondaryVariant,
        secondaryContainer,
        secondary,
        surfaceContainerHighest,
        outline,
        sliderBackground,
        dialogButton,
        dragHandle,
        secondaryButton,
        disabledSecondaryButton,
        disabledOnSecondaryButton,
        transparentButton,
        onTransparentButton,
        disabledOnTransparentButton,
        backgroundTop,
        primaryTextOnPopup,
        press,
        hover,
        focus,
        disabledProgress,
        disabledProgressBackground,
        disabledSliderThumb,
        disabledSliderKeyPoint,
        sliderThumbShadow,
        disabledTextFieldBorder,
        quarternaryNeutral,
        container8,
        disabledChipText,
        focusOutline,
        primaryText = primaryText,
        labelTheme = labelTheme,
        link = link,
        disable = disable,
        containerTheme = containerTheme,
        containerThemeHalftone = containerThemeHalftone,
        textColorHighlight = textColorHighlight,
        errorDisabled = errorDisabled,
        errorTextBg = errorTextBg,
        success = success,
        tips = tips,
        additional = additional,
        assist = assist,
        background = background,
        surface = surface,
        backgroundElevated = backgroundElevated,
        backgroundElevatedWithCard = backgroundElevatedWithCard,
        backgroundWithCard = backgroundWithCard,
        surfaceTop = surfaceTop,
        surfaceWithCard = surfaceWithCard,
        bar = bar,
        bottomBar = bottomBar,
        card = card,
        cardBackground = cardBackground,
        cardPressed = cardPressed,
        labelPrimary = labelPrimary,
        labelPrimaryVariant = labelPrimaryVariant,
        labelSecondary = labelSecondary,
        labelSecondaryVariant = labelSecondaryVariant,
        labelTertiary = labelTertiary,
        labelQuaternary = labelQuaternary,
        labelOnColor = labelOnColor,
        disabledNeutral = disabledNeutral,
        hintNeutral = hintNeutral,
        container4 = container4,
        container12 = container12,
        container16 = container16,
        containerSolid = containerSolid,
        fillSolid = fillSolid,
        fillThin = fillThin,
        controls = controls,
        divider = divider,
        mask = mask,
        pressBackground = pressBackground,
        ripplePressBackground = ripplePressBackground,
        red = red,
        redVariant = redVariant,
        containerRed = containerRed,
        orange = orange,
        orangeVariant = orangeVariant,
        containerOrange = containerOrange,
        yellow = yellow,
        yellowVariant = yellowVariant,
        containerYellow = containerYellow,
        green = green,
        greenVariant = greenVariant,
        containerGreen = containerGreen,
        mint = mint,
        mintVariant = mintVariant,
        containerMint = containerMint,
        cyan = cyan,
        cyanVariant = cyanVariant,
        containerCyan = containerCyan,
        azure = azure,
        azureVariant = azureVariant,
        containerAzure = containerAzure,
        blue = blue,
        blueVariant = blueVariant,
        containerBlue = containerBlue,
        violet = violet,
        violetVariant = violetVariant,
        containerViolet = containerViolet,
        purple = purple,
        purpleVariant = purpleVariant,
        containerPurple = containerPurple,
        neutralButton = neutralButton,
        isDark = isDark,
    )
}

/** COUI light scheme. COUI: coui_color_blue #0080FF; gray page surface, white cards and dialog panels. */
fun lightColorScheme(
    primary: Color = Color(0xFF0080FF),
    onPrimary: Color = Color.White,
    error: Color = Color(0xFFDB382C),
    onError: Color = Color(0xFFFFFFFF),
    disabledOnPrimaryButton: Color = Color(0x8AFFFFFF),
    secondaryVariant: Color = Color(0x14000000),
    disabledSecondaryVariant: Color = Color(0x14000000),
    secondaryContainer: Color = Color(0x14000000),
    secondary: Color = Color(0x260080FF),
    surfaceContainerHighest: Color = Color.White,
    outline: Color = Color(0x26000000),
    sliderBackground: Color = Color(0x1F000000),
    dialogButton: Color = Color(0x14000000),
    dragHandle: Color = Color(0x29000000),
    secondaryButton: Color = Color(0xFFEBEBEB),
    disabledSecondaryButton: Color = Color(0x14000000),
    disabledOnSecondaryButton: Color = Color(0x42000000),
    transparentButton: Color = Color(0x33FFFFFF),
    onTransparentButton: Color = Color(0xFFFFFFFF),
    disabledOnTransparentButton: Color = Color(0x4DFFFFFF),
    backgroundTop: Color = Color(0xFFFFFFFF),
    primaryTextOnPopup: Color = Color(0xFF0080FF),
    press: Color = Color(0x1F000000),
    hover: Color = Color(0x14000000),
    focus: Color = Color(0x1F0080FF),
    disabledProgress: Color = Color(0xFFE6E6E6),
    disabledProgressBackground: Color = Color(0x08000000),
    disabledSliderThumb: Color = Color(0x8AFFFFFF),
    disabledSliderKeyPoint: Color = Color(0x42000000),
    sliderThumbShadow: Color = Color(0x1A000000),
    disabledTextFieldBorder: Color = Color(0x19000000),
    quarternaryNeutral: Color = Color(0x14000000),
    container8: Color = Color(0x14000000),
    disabledChipText: Color = Color(0x42000000),
    focusOutline: Color = Color(0x660080FF),
    primaryText: Color = Color(0xFF0080FF),
    labelTheme: Color = Color(0xFF0080FF),
    link: Color = Color(0xFF0080FF),
    disable: Color = Color(0x4D0080FF),
    containerTheme: Color = Color(0xFF0080FF),
    containerThemeHalftone: Color = Color(0x260080FF),
    textColorHighlight: Color = Color(0x260080FF),
    errorDisabled: Color = Color(0x4DDB382C),
    errorTextBg: Color = Color(0x4DE82A18),
    success: Color = Color(0xFF00BD13),
    tips: Color = Color(0xFF0080FF),
    additional: Color = Color(0xFF0080FF),
    assist: Color = Color(0xFF5C93E5),
    background: Color = Color(0xFFFFFFFF),
    surface: Color = Color(0xFFFFFFFF),
    backgroundElevated: Color = Color(0xFFFFFFFF),
    backgroundElevatedWithCard: Color = Color(0xFFF0F1F2),
    backgroundWithCard: Color = Color(0xFFF0F1F2),
    surfaceTop: Color = Color(0xFFFFFFFF),
    surfaceWithCard: Color = Color(0xFFF0F1F2),
    bar: Color = Color(0xFFFAFAFA),
    bottomBar: Color = Color(0xFFFAFAFA),
    card: Color = Color(0xFFFFFFFF),
    cardBackground: Color = Color(0xFFFFFFFF),
    cardPressed: Color = Color(0xFFE6E6E6),
    labelPrimary: Color = Color(0xE6000000),
    labelPrimaryVariant: Color = Color(0xB8000000),
    labelSecondary: Color = Color(0x8A000000),
    labelSecondaryVariant: Color = Color(0x66000000),
    labelTertiary: Color = Color(0x42000000),
    labelQuaternary: Color = Color(0x14000000),
    labelOnColor: Color = Color(0xFFFFFFFF),
    disabledNeutral: Color = Color(0x42000000),
    hintNeutral: Color = Color(0x42000000),
    container4: Color = Color(0x0A000000),
    container12: Color = Color(0x1F000000),
    container16: Color = Color(0x29000000),
    containerSolid: Color = Color(0xFFEBEBEB),
    fillSolid: Color = Color(0xFFEBEBEB),
    fillThin: Color = Color(0x0A000000),
    controls: Color = Color(0x29000000),
    divider: Color = Color(0x1F000000),
    mask: Color = Color(0x33000000),
    pressBackground: Color = Color(0x14000000),
    ripplePressBackground: Color = Color(0x99B4B4B4),
    red: Color = Color(0xFFEB3B2F),
    redVariant: Color = Color(0xFFE56E65),
    containerRed: Color = Color(0xFFEEDADC),
    orange: Color = Color(0xFFFF7700),
    orangeVariant: Color = Color(0xFFE58F45),
    containerOrange: Color = Color(0xFFFAE7D6),
    yellow: Color = Color(0xFFFFB200),
    yellowVariant: Color = Color(0xFFE5AF2E),
    containerYellow: Color = Color(0xFFF6E7C6),
    green: Color = Color(0xFF00BD13),
    greenVariant: Color = Color(0xFF4BBD57),
    containerGreen: Color = Color(0xFFBADECA),
    mint: Color = Color(0xFF00CC88),
    mintVariant: Color = Color(0xFF39BD91),
    containerMint: Color = Color(0xFFBADEDB),
    cyan: Color = Color(0xFF1DBACC),
    cyanVariant: Color = Color(0xFF39AFBD),
    containerCyan: Color = Color(0xFFBEDADD),
    azure: Color = Color(0xFF00A1FF),
    azureVariant: Color = Color(0xFF45ABE5),
    containerAzure: Color = Color(0xFFCEE5EB),
    blue: Color = Color(0xFF0080FF),
    blueVariant: Color = Color(0xFF5C93E5),
    containerBlue: Color = Color(0xFFC9DAE4),
    violet: Color = Color(0xFF5A47FF),
    violetVariant: Color = Color(0xFF7E73E5),
    containerViolet: Color = Color(0xFFD6D5EE),
    purple: Color = Color(0xFFA723FF),
    purpleVariant: Color = Color(0xFFB873E5),
    containerPurple: Color = Color(0xFFE0D6EB),
    neutralButton: Color = Color(0x0F000000),
): Colors = Colors(
    primary,
    onPrimary,
    error,
    onError,
    disabledOnPrimaryButton,
    secondaryVariant,
    disabledSecondaryVariant,
    secondaryContainer,
    secondary,
    surfaceContainerHighest,
    outline,
    sliderBackground,
    dialogButton,
    dragHandle,
    secondaryButton,
    disabledSecondaryButton,
    disabledOnSecondaryButton,
    transparentButton,
    onTransparentButton,
    disabledOnTransparentButton,
    backgroundTop,
    primaryTextOnPopup,
    press,
    hover,
    focus,
    disabledProgress,
    disabledProgressBackground,
    disabledSliderThumb,
    disabledSliderKeyPoint,
    sliderThumbShadow,
    disabledTextFieldBorder,
    quarternaryNeutral,
    container8,
    disabledChipText,
    focusOutline,
    primaryText = primaryText,
    labelTheme = labelTheme,
    link = link,
    disable = disable,
    containerTheme = containerTheme,
    containerThemeHalftone = containerThemeHalftone,
    textColorHighlight = textColorHighlight,
    errorDisabled = errorDisabled,
    errorTextBg = errorTextBg,
    success = success,
    tips = tips,
    additional = additional,
    assist = assist,
    background = background,
    surface = surface,
    backgroundElevated = backgroundElevated,
    backgroundElevatedWithCard = backgroundElevatedWithCard,
    backgroundWithCard = backgroundWithCard,
    surfaceTop = surfaceTop,
    surfaceWithCard = surfaceWithCard,
    bar = bar,
    bottomBar = bottomBar,
    card = card,
    cardBackground = cardBackground,
    cardPressed = cardPressed,
    labelPrimary = labelPrimary,
    labelPrimaryVariant = labelPrimaryVariant,
    labelSecondary = labelSecondary,
    labelSecondaryVariant = labelSecondaryVariant,
    labelTertiary = labelTertiary,
    labelQuaternary = labelQuaternary,
    labelOnColor = labelOnColor,
    disabledNeutral = disabledNeutral,
    hintNeutral = hintNeutral,
    container4 = container4,
    container12 = container12,
    container16 = container16,
    containerSolid = containerSolid,
    fillSolid = fillSolid,
    fillThin = fillThin,
    controls = controls,
    divider = divider,
    mask = mask,
    pressBackground = pressBackground,
    ripplePressBackground = ripplePressBackground,
    red = red,
    redVariant = redVariant,
    containerRed = containerRed,
    orange = orange,
    orangeVariant = orangeVariant,
    containerOrange = containerOrange,
    yellow = yellow,
    yellowVariant = yellowVariant,
    containerYellow = containerYellow,
    green = green,
    greenVariant = greenVariant,
    containerGreen = containerGreen,
    mint = mint,
    mintVariant = mintVariant,
    containerMint = containerMint,
    cyan = cyan,
    cyanVariant = cyanVariant,
    containerCyan = containerCyan,
    azure = azure,
    azureVariant = azureVariant,
    containerAzure = containerAzure,
    blue = blue,
    blueVariant = blueVariant,
    containerBlue = containerBlue,
    violet = violet,
    violetVariant = violetVariant,
    containerViolet = containerViolet,
    purple = purple,
    purpleVariant = purpleVariant,
    containerPurple = containerPurple,
    neutralButton = neutralButton,
    isDark = false,
)

/** COUI dark scheme. COUI: coui_color_blue_dark #1A8CFF; pure black page surface, translucent white cards. */
fun darkColorScheme(
    primary: Color = Color(0xFF1A8CFF),
    onPrimary: Color = Color.White,
    error: Color = Color(0xFFFF6C61),
    onError: Color = Color.White,
    disabledOnPrimaryButton: Color = Color(0x29FFFFFF),
    secondaryVariant: Color = Color(0x26FFFFFF),
    disabledSecondaryVariant: Color = Color(0x1AFFFFFF),
    secondaryContainer: Color = Color(0x26FFFFFF),
    secondary: Color = Color(0x401A8CFF),
    surfaceContainerHighest: Color = Color(0xFF1E1E1E),
    outline: Color = Color(0x26FFFFFF),
    sliderBackground: Color = Color(0x33FFFFFF),
    dialogButton: Color = Color(0x1AFFFFFF),
    dragHandle: Color = Color(0x40FFFFFF),
    secondaryButton: Color = Color(0xFF353535),
    disabledSecondaryButton: Color = Color(0x1AFFFFFF),
    disabledOnSecondaryButton: Color = Color(0x28FFFFFF),
    transparentButton: Color = Color(0x33FFFFFF),
    onTransparentButton: Color = Color(0xFFFFFFFF),
    disabledOnTransparentButton: Color = Color(0x4DFFFFFF),
    backgroundTop: Color = Color(0xFF333333),
    primaryTextOnPopup: Color = Color(0xFF5C9DFF),
    press: Color = Color(0x33FFFFFF),
    hover: Color = Color(0x26FFFFFF),
    focus: Color = Color(0x331A8CFF),
    disabledProgress: Color = Color(0xFF262626),
    disabledProgressBackground: Color = Color(0x33FFFFFF),
    disabledSliderThumb: Color = Color(0x29FFFFFF),
    disabledSliderKeyPoint: Color = Color(0x29FFFFFF),
    sliderThumbShadow: Color = Color(0x40000000),
    disabledTextFieldBorder: Color = Color(0x19FFFFFF),
    quarternaryNeutral: Color = Color(0x26FFFFFF),
    container8: Color = Color(0x1AFFFFFF),
    disabledChipText: Color = Color(0x29FFFFFF),
    focusOutline: Color = Color(0x665C9DFF),
    primaryText: Color = Color(0xFF5C9DFF),
    labelTheme: Color = Color(0xFF1A8CFF),
    link: Color = Color(0xFF1A8CFF),
    disable: Color = Color(0x4D1A8CFF),
    containerTheme: Color = Color(0xFF1A8CFF),
    containerThemeHalftone: Color = Color(0x401A8CFF),
    textColorHighlight: Color = Color(0x401A8CFF),
    errorDisabled: Color = Color(0x4DFF6C61),
    errorTextBg: Color = Color(0xFFEA3939),
    success: Color = Color(0xFF24B232),
    tips: Color = Color(0xFF1A8CFF),
    additional: Color = Color(0xFF1A8CFF),
    assist: Color = Color(0xFF5C93E5),
    background: Color = Color(0xFF000000),
    surface: Color = Color(0xFF1E1E1E),
    backgroundElevated: Color = Color(0xFF1E1E1E),
    backgroundElevatedWithCard: Color = Color(0xFF1E1E1E),
    backgroundWithCard: Color = Color(0xFF000000),
    surfaceTop: Color = Color(0xFF333333),
    surfaceWithCard: Color = Color(0xFF1E1E1E),
    bar: Color = Color(0xFF1E1E1E),
    bottomBar: Color = Color(0xFF1E1E1E),
    card: Color = Color(0x1AFFFFFF),
    cardBackground: Color = Color(0x1AFFFFFF),
    cardPressed: Color = Color(0x33FFFFFF),
    labelPrimary: Color = Color(0xE6FFFFFF),
    labelPrimaryVariant: Color = Color(0xB8FFFFFF),
    labelSecondary: Color = Color(0x8AFFFFFF),
    labelSecondaryVariant: Color = Color(0x66FFFFFF),
    labelTertiary: Color = Color(0x4DFFFFFF),
    labelQuaternary: Color = Color(0x26FFFFFF),
    labelOnColor: Color = Color(0xFFFFFFFF),
    disabledNeutral: Color = Color(0x4DFFFFFF),
    hintNeutral: Color = Color(0x4DFFFFFF),
    container4: Color = Color(0x1AFFFFFF),
    container12: Color = Color(0x33FFFFFF),
    container16: Color = Color(0x40FFFFFF),
    containerSolid: Color = Color(0xFF262626),
    fillSolid: Color = Color(0xFF262626),
    fillThin: Color = Color(0x14FFFFFF),
    controls: Color = Color(0x40FFFFFF),
    divider: Color = Color(0x33FFFFFF),
    mask: Color = Color(0x99000000),
    pressBackground: Color = Color(0x26FFFFFF),
    ripplePressBackground: Color = Color(0x80FFFFFF),
    red: Color = Color(0xFFEB493D),
    redVariant: Color = Color(0xFFCC615A),
    containerRed: Color = Color(0xFF3A3030),
    orange: Color = Color(0xFFF08222),
    orangeVariant: Color = Color(0xFFCC7F3D),
    containerOrange: Color = Color(0xFF39332C),
    yellow: Color = Color(0xFFE5A100),
    yellowVariant: Color = Color(0xFFC29427),
    containerYellow: Color = Color(0xFF373530),
    green: Color = Color(0xFF24B232),
    greenVariant: Color = Color(0xFF43A84E),
    containerGreen: Color = Color(0xFF353E36),
    mint: Color = Color(0xFF1AB883),
    mintVariant: Color = Color(0xFF32A881),
    containerMint: Color = Color(0xFF394342),
    cyan: Color = Color(0xFF25A9B8),
    cyanVariant: Color = Color(0xFF329DA8),
    containerCyan: Color = Color(0xFF343E3D),
    azure: Color = Color(0xFF209DE5),
    azureVariant: Color = Color(0xFF3D98CC),
    containerAzure: Color = Color(0xFF373F42),
    blue: Color = Color(0xFF1A8CFF),
    blueVariant: Color = Color(0xFF5C93E5),
    containerBlue: Color = Color(0xFF323739),
    violet: Color = Color(0xFF7161FF),
    violetVariant: Color = Color(0xFF7E73E5),
    containerViolet: Color = Color(0xFF3A3A43),
    purple: Color = Color(0xFFAC33FF),
    purpleVariant: Color = Color(0xFFA466CC),
    containerPurple: Color = Color(0xFF3A3540),
    neutralButton: Color = Color(0x4CFFFFFF),
): Colors = Colors(
    primary,
    onPrimary,
    error,
    onError,
    disabledOnPrimaryButton,
    secondaryVariant,
    disabledSecondaryVariant,
    secondaryContainer,
    secondary,
    surfaceContainerHighest,
    outline,
    sliderBackground,
    dialogButton,
    dragHandle,
    secondaryButton,
    disabledSecondaryButton,
    disabledOnSecondaryButton,
    transparentButton,
    onTransparentButton,
    disabledOnTransparentButton,
    backgroundTop,
    primaryTextOnPopup,
    press,
    hover,
    focus,
    disabledProgress,
    disabledProgressBackground,
    disabledSliderThumb,
    disabledSliderKeyPoint,
    sliderThumbShadow,
    disabledTextFieldBorder,
    quarternaryNeutral,
    container8,
    disabledChipText,
    focusOutline,
    primaryText = primaryText,
    labelTheme = labelTheme,
    link = link,
    disable = disable,
    containerTheme = containerTheme,
    containerThemeHalftone = containerThemeHalftone,
    textColorHighlight = textColorHighlight,
    errorDisabled = errorDisabled,
    errorTextBg = errorTextBg,
    success = success,
    tips = tips,
    additional = additional,
    assist = assist,
    background = background,
    surface = surface,
    backgroundElevated = backgroundElevated,
    backgroundElevatedWithCard = backgroundElevatedWithCard,
    backgroundWithCard = backgroundWithCard,
    surfaceTop = surfaceTop,
    surfaceWithCard = surfaceWithCard,
    bar = bar,
    bottomBar = bottomBar,
    card = card,
    cardBackground = cardBackground,
    cardPressed = cardPressed,
    labelPrimary = labelPrimary,
    labelPrimaryVariant = labelPrimaryVariant,
    labelSecondary = labelSecondary,
    labelSecondaryVariant = labelSecondaryVariant,
    labelTertiary = labelTertiary,
    labelQuaternary = labelQuaternary,
    labelOnColor = labelOnColor,
    disabledNeutral = disabledNeutral,
    hintNeutral = hintNeutral,
    container4 = container4,
    container12 = container12,
    container16 = container16,
    containerSolid = containerSolid,
    fillSolid = fillSolid,
    fillThin = fillThin,
    controls = controls,
    divider = divider,
    mask = mask,
    pressBackground = pressBackground,
    ripplePressBackground = ripplePressBackground,
    red = red,
    redVariant = redVariant,
    containerRed = containerRed,
    orange = orange,
    orangeVariant = orangeVariant,
    containerOrange = containerOrange,
    yellow = yellow,
    yellowVariant = yellowVariant,
    containerYellow = containerYellow,
    green = green,
    greenVariant = greenVariant,
    containerGreen = containerGreen,
    mint = mint,
    mintVariant = mintVariant,
    containerMint = containerMint,
    cyan = cyan,
    cyanVariant = cyanVariant,
    containerCyan = containerCyan,
    azure = azure,
    azureVariant = azureVariant,
    containerAzure = containerAzure,
    blue = blue,
    blueVariant = blueVariant,
    containerBlue = containerBlue,
    violet = violet,
    violetVariant = violetVariant,
    containerViolet = containerViolet,
    purple = purple,
    purpleVariant = purpleVariant,
    containerPurple = containerPurple,
    neutralButton = neutralButton,
    isDark = true,
)

@Stable
internal fun Colors.updateColorsFrom(other: Colors) {
    primary = other.primary
    onPrimary = other.onPrimary
    error = other.error
    onError = other.onError
    disabledOnPrimaryButton = other.disabledOnPrimaryButton
    secondaryVariant = other.secondaryVariant
    disabledSecondaryVariant = other.disabledSecondaryVariant
    secondaryContainer = other.secondaryContainer
    secondary = other.secondary
    surfaceContainerHighest = other.surfaceContainerHighest
    outline = other.outline
    sliderBackground = other.sliderBackground
    dialogButton = other.dialogButton
    dragHandle = other.dragHandle
    secondaryButton = other.secondaryButton
    disabledSecondaryButton = other.disabledSecondaryButton
    disabledOnSecondaryButton = other.disabledOnSecondaryButton
    transparentButton = other.transparentButton
    onTransparentButton = other.onTransparentButton
    disabledOnTransparentButton = other.disabledOnTransparentButton
    backgroundTop = other.backgroundTop
    primaryTextOnPopup = other.primaryTextOnPopup
    press = other.press
    hover = other.hover
    focus = other.focus
    disabledProgress = other.disabledProgress
    disabledProgressBackground = other.disabledProgressBackground
    disabledSliderThumb = other.disabledSliderThumb
    disabledSliderKeyPoint = other.disabledSliderKeyPoint
    sliderThumbShadow = other.sliderThumbShadow
    disabledTextFieldBorder = other.disabledTextFieldBorder
    quarternaryNeutral = other.quarternaryNeutral
    container8 = other.container8
    disabledChipText = other.disabledChipText
    focusOutline = other.focusOutline
    primaryText = other.primaryText
    labelTheme = other.labelTheme
    link = other.link
    disable = other.disable
    containerTheme = other.containerTheme
    containerThemeHalftone = other.containerThemeHalftone
    textColorHighlight = other.textColorHighlight
    errorDisabled = other.errorDisabled
    errorTextBg = other.errorTextBg
    success = other.success
    tips = other.tips
    additional = other.additional
    assist = other.assist
    background = other.background
    surface = other.surface
    backgroundElevated = other.backgroundElevated
    backgroundElevatedWithCard = other.backgroundElevatedWithCard
    backgroundWithCard = other.backgroundWithCard
    surfaceTop = other.surfaceTop
    surfaceWithCard = other.surfaceWithCard
    bar = other.bar
    bottomBar = other.bottomBar
    card = other.card
    cardBackground = other.cardBackground
    cardPressed = other.cardPressed
    labelPrimary = other.labelPrimary
    labelPrimaryVariant = other.labelPrimaryVariant
    labelSecondary = other.labelSecondary
    labelSecondaryVariant = other.labelSecondaryVariant
    labelTertiary = other.labelTertiary
    labelQuaternary = other.labelQuaternary
    labelOnColor = other.labelOnColor
    disabledNeutral = other.disabledNeutral
    hintNeutral = other.hintNeutral
    container4 = other.container4
    container12 = other.container12
    container16 = other.container16
    containerSolid = other.containerSolid
    fillSolid = other.fillSolid
    fillThin = other.fillThin
    controls = other.controls
    divider = other.divider
    mask = other.mask
    pressBackground = other.pressBackground
    ripplePressBackground = other.ripplePressBackground
    red = other.red
    redVariant = other.redVariant
    containerRed = other.containerRed
    orange = other.orange
    orangeVariant = other.orangeVariant
    containerOrange = other.containerOrange
    yellow = other.yellow
    yellowVariant = other.yellowVariant
    containerYellow = other.containerYellow
    green = other.green
    greenVariant = other.greenVariant
    containerGreen = other.containerGreen
    mint = other.mint
    mintVariant = other.mintVariant
    containerMint = other.containerMint
    cyan = other.cyan
    cyanVariant = other.cyanVariant
    containerCyan = other.containerCyan
    azure = other.azure
    azureVariant = other.azureVariant
    containerAzure = other.containerAzure
    blue = other.blue
    blueVariant = other.blueVariant
    containerBlue = other.containerBlue
    violet = other.violet
    violetVariant = other.violetVariant
    containerViolet = other.containerViolet
    purple = other.purple
    purpleVariant = other.purpleVariant
    containerPurple = other.containerPurple
    neutralButton = other.neutralButton
    isDark = other.isDark
}

internal val LocalColors = staticCompositionLocalOf { lightColorScheme() }
