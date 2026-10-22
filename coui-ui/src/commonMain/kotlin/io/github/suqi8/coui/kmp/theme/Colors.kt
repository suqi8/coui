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
 * @param primaryVariant The variant color of the primary color.Cases:Card
 * @param onPrimaryVariant The color of the text on primary variant color.
 * @param disabledPrimary The disabled primary color of the switch.
 * @param disabledOnPrimary The color of the switch on disabled primary color.
 * @param disabledPrimaryButton The disabled primary color of the button.
 * @param disabledOnPrimaryButton The color of the button on disabled primary color.
 * @param disabledPrimarySlider The disabled progress color of the slider (COUI couiColorDisable, the disabled state of
 *   coui_seekbar_progress_selector).
 * @param primaryContainer The container color of the primary color.
 * @param onPrimaryContainer The color of the text on primary container color.
 * @param secondary The secondary color.
 * @param onSecondary The color of the text on secondary color.
 * @param secondaryVariant The variant color of the secondary color.
 * @param onSecondaryVariant The color of the text on secondary variant color.
 * @param disabledSecondary The disabled secondary color.
 * @param disabledOnSecondary The color of the text on disabled secondary color.
 * @param disabledSecondaryVariant The disabled secondary color.
 * @param disabledOnSecondaryVariant The color of the text on disabled secondary variant color.
 * @param secondaryContainer The container color of the secondary color.
 * @param onSecondaryContainer The color of the text on secondary container color.
 * @param secondaryContainerVariant The variant color of the secondary container color.
 * @param onSecondaryContainerVariant The color of the text on secondary container variant color.
 * @param tertiaryContainer The container color of the tertiary color.
 * @param onTertiaryContainer The color of the text on tertiary container color.
 * @param tertiaryContainerVariant The variant color of the tertiary container color.
 * @param background The background color.
 * @param onBackground The color of the text on background color.
 * @param onBackgroundVariant The color of the text on background variant color.
 * @param surface The surface color.
 * @param onSurface The color of the text on surface color.
 * @param surfaceVariant The variant color of the surface color.
 * @param onSurfaceSecondary The color of the text on surface secondary color.
 * @param onSurfaceVariantSummary The color of the summary on surface variant color.
 * @param onSurfaceVariantActions The color of the actions on surface variant color.
 * @param disabledOnSurface The color of the text on disabled surface color.
 * @param outline The outline color.
 * @param dividerLine The divider line color.
 * @param surfaceContainer The container color of the surface color.
 * @param onSurfaceContainer The color of the text on surface container color.
 * @param onSurfaceContainerVariant The color of the text on surface container variant color.
 * @param surfaceContainerHigh The container color of the surface color.
 * @param onSurfaceContainerHigh The color of the text on surface container high color.
 * @param surfaceContainerHighest The container color of the surface color.
 * @param onSurfaceContainerHighest The color of the text on surface container highest color.
 * @param windowDimming The color of the window dimming. Cases: Dialog, Dropdown, Spinner, BottomSheet.
 * @param sliderKeyPoint The color of the slider key point.
 * @param sliderKeyPointForeground The foreground color of the slider key point.
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
 */
@Stable
class Colors(
    primary: Color,
    onPrimary: Color,
    primaryVariant: Color,
    onPrimaryVariant: Color,
    error: Color,
    onError: Color,
    errorContainer: Color,
    onErrorContainer: Color,
    disabledPrimary: Color,
    disabledOnPrimary: Color,
    disabledPrimaryButton: Color,
    disabledOnPrimaryButton: Color,
    disabledPrimarySlider: Color,
    primaryContainer: Color,
    onPrimaryContainer: Color,
    secondary: Color,
    onSecondary: Color,
    secondaryVariant: Color,
    onSecondaryVariant: Color,
    disabledSecondary: Color,
    disabledOnSecondary: Color,
    disabledSecondaryVariant: Color,
    disabledOnSecondaryVariant: Color,
    secondaryContainer: Color,
    onSecondaryContainer: Color,
    secondaryContainerVariant: Color,
    onSecondaryContainerVariant: Color,
    tertiaryContainer: Color,
    onTertiaryContainer: Color,
    tertiaryContainerVariant: Color,
    background: Color,
    onBackground: Color,
    onBackgroundVariant: Color,
    surface: Color,
    onSurface: Color,
    surfaceVariant: Color,
    onSurfaceSecondary: Color,
    onSurfaceVariantSummary: Color,
    onSurfaceVariantActions: Color,
    disabledOnSurface: Color,
    surfaceContainer: Color,
    onSurfaceContainer: Color,
    onSurfaceContainerVariant: Color,
    surfaceContainerHigh: Color,
    onSurfaceContainerHigh: Color,
    surfaceContainerHighest: Color,
    onSurfaceContainerHighest: Color,
    outline: Color,
    dividerLine: Color,
    windowDimming: Color,
    sliderKeyPoint: Color,
    sliderKeyPointForeground: Color,
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
) {
    var primary by mutableStateOf(primary, structuralEqualityPolicy())
        internal set
    var onPrimary by mutableStateOf(onPrimary, structuralEqualityPolicy())
        internal set
    var primaryVariant by mutableStateOf(primaryVariant, structuralEqualityPolicy())
        internal set
    var onPrimaryVariant by mutableStateOf(onPrimaryVariant, structuralEqualityPolicy())
        internal set
    var error by mutableStateOf(error, structuralEqualityPolicy())
        internal set
    var onError by mutableStateOf(onError, structuralEqualityPolicy())
        internal set
    var errorContainer by mutableStateOf(errorContainer, structuralEqualityPolicy())
        internal set
    var onErrorContainer by mutableStateOf(onErrorContainer, structuralEqualityPolicy())
        internal set
    var disabledPrimary by mutableStateOf(disabledPrimary, structuralEqualityPolicy())
        internal set
    var disabledOnPrimary by mutableStateOf(disabledOnPrimary, structuralEqualityPolicy())
        internal set
    var disabledPrimaryButton by mutableStateOf(disabledPrimaryButton, structuralEqualityPolicy())
        internal set
    var disabledOnPrimaryButton by mutableStateOf(disabledOnPrimaryButton, structuralEqualityPolicy())
        internal set
    var disabledPrimarySlider by mutableStateOf(disabledPrimarySlider, structuralEqualityPolicy())
        internal set
    var primaryContainer by mutableStateOf(primaryContainer, structuralEqualityPolicy())
        internal set
    var onPrimaryContainer by mutableStateOf(onPrimaryContainer, structuralEqualityPolicy())
        internal set
    var secondary by mutableStateOf(secondary, structuralEqualityPolicy())
        internal set
    var onSecondary by mutableStateOf(onSecondary, structuralEqualityPolicy())
        internal set
    var secondaryVariant by mutableStateOf(secondaryVariant, structuralEqualityPolicy())
        internal set
    var onSecondaryVariant by mutableStateOf(onSecondaryVariant, structuralEqualityPolicy())
        internal set
    var disabledSecondary by mutableStateOf(disabledSecondary, structuralEqualityPolicy())
        internal set
    var disabledOnSecondary by mutableStateOf(disabledOnSecondary, structuralEqualityPolicy())
        internal set
    var disabledSecondaryVariant by mutableStateOf(disabledSecondaryVariant, structuralEqualityPolicy())
        internal set
    var disabledOnSecondaryVariant by mutableStateOf(disabledOnSecondaryVariant, structuralEqualityPolicy())
        internal set
    var secondaryContainer by mutableStateOf(secondaryContainer, structuralEqualityPolicy())
        internal set
    var onSecondaryContainer by mutableStateOf(onSecondaryContainer, structuralEqualityPolicy())
        internal set
    var secondaryContainerVariant by mutableStateOf(secondaryContainerVariant, structuralEqualityPolicy())
        internal set
    var onSecondaryContainerVariant by mutableStateOf(onSecondaryContainerVariant, structuralEqualityPolicy())
        internal set
    var tertiaryContainer by mutableStateOf(tertiaryContainer, structuralEqualityPolicy())
        internal set
    var onTertiaryContainer by mutableStateOf(onTertiaryContainer, structuralEqualityPolicy())
        internal set
    var tertiaryContainerVariant by mutableStateOf(tertiaryContainerVariant, structuralEqualityPolicy())
        internal set
    var background by mutableStateOf(background, structuralEqualityPolicy())
        internal set
    var onBackground by mutableStateOf(onBackground, structuralEqualityPolicy())
        internal set
    var onBackgroundVariant by mutableStateOf(onBackgroundVariant, structuralEqualityPolicy())
        internal set
    var surface by mutableStateOf(surface, structuralEqualityPolicy())
        internal set
    var onSurface by mutableStateOf(onSurface, structuralEqualityPolicy())
        internal set
    var surfaceVariant by mutableStateOf(surfaceVariant, structuralEqualityPolicy())
        internal set
    var onSurfaceSecondary by mutableStateOf(onSurfaceSecondary, structuralEqualityPolicy())
        internal set
    var onSurfaceVariantSummary by mutableStateOf(onSurfaceVariantSummary, structuralEqualityPolicy())
        internal set
    var onSurfaceVariantActions by mutableStateOf(onSurfaceVariantActions, structuralEqualityPolicy())
        internal set
    var disabledOnSurface by mutableStateOf(disabledOnSurface, structuralEqualityPolicy())
        internal set
    var surfaceContainer by mutableStateOf(surfaceContainer, structuralEqualityPolicy())
        internal set
    var onSurfaceContainer by mutableStateOf(onSurfaceContainer, structuralEqualityPolicy())
        internal set
    var onSurfaceContainerVariant by mutableStateOf(onSurfaceContainerVariant, structuralEqualityPolicy())
        internal set
    var surfaceContainerHigh by mutableStateOf(surfaceContainerHigh, structuralEqualityPolicy())
        internal set
    var onSurfaceContainerHigh by mutableStateOf(onSurfaceContainerHigh, structuralEqualityPolicy())
        internal set
    var surfaceContainerHighest by mutableStateOf(surfaceContainerHighest, structuralEqualityPolicy())
        internal set
    var onSurfaceContainerHighest by mutableStateOf(onSurfaceContainerHighest, structuralEqualityPolicy())
        internal set
    var outline by mutableStateOf(outline, structuralEqualityPolicy())
        internal set
    var dividerLine by mutableStateOf(dividerLine, structuralEqualityPolicy())
        internal set
    var windowDimming by mutableStateOf(windowDimming, structuralEqualityPolicy())
        internal set
    var sliderKeyPoint by mutableStateOf(sliderKeyPoint, structuralEqualityPolicy())
        internal set
    var sliderKeyPointForeground by mutableStateOf(sliderKeyPointForeground, structuralEqualityPolicy())
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

    fun copy(
        primary: Color = this.primary,
        onPrimary: Color = this.onPrimary,
        primaryVariant: Color = this.primaryVariant,
        onPrimaryVariant: Color = this.onPrimaryVariant,
        error: Color = this.error,
        onError: Color = this.onError,
        errorContainer: Color = this.errorContainer,
        onErrorContainer: Color = this.onErrorContainer,
        disabledPrimary: Color = this.disabledPrimary,
        disabledOnPrimary: Color = this.disabledOnPrimary,
        disabledPrimaryButton: Color = this.disabledPrimaryButton,
        disabledOnPrimaryButton: Color = this.disabledOnPrimaryButton,
        disabledPrimarySlider: Color = this.disabledPrimarySlider,
        primaryContainer: Color = this.primaryContainer,
        onPrimaryContainer: Color = this.onPrimaryContainer,
        secondary: Color = this.secondary,
        onSecondary: Color = this.onSecondary,
        secondaryVariant: Color = this.secondaryVariant,
        onSecondaryVariant: Color = this.onSecondaryVariant,
        disabledSecondary: Color = this.disabledSecondary,
        disabledOnSecondary: Color = this.disabledOnSecondary,
        disabledSecondaryVariant: Color = this.disabledSecondaryVariant,
        disabledOnSecondaryVariant: Color = this.disabledOnSecondaryVariant,
        secondaryContainer: Color = this.secondaryContainer,
        onSecondaryContainer: Color = this.onSecondaryContainer,
        secondaryContainerVariant: Color = this.secondaryContainerVariant,
        onSecondaryContainerVariant: Color = this.onSecondaryContainerVariant,
        tertiaryContainer: Color = this.tertiaryContainer,
        onTertiaryContainer: Color = this.onTertiaryContainer,
        tertiaryContainerVariant: Color = this.tertiaryContainerVariant,
        background: Color = this.background,
        onBackground: Color = this.onBackground,
        onBackgroundVariant: Color = this.onBackgroundVariant,
        surface: Color = this.surface,
        onSurface: Color = this.onSurface,
        surfaceVariant: Color = this.surfaceVariant,
        onSurfaceSecondary: Color = this.onSurfaceSecondary,
        onSurfaceVariantSummary: Color = this.onSurfaceVariantSummary,
        onSurfaceVariantActions: Color = this.onSurfaceVariantActions,
        disabledOnSurface: Color = this.disabledOnSurface,
        surfaceContainer: Color = this.surfaceContainer,
        onSurfaceContainer: Color = this.onSurfaceContainer,
        onSurfaceContainerVariant: Color = this.onSurfaceContainerVariant,
        surfaceContainerHigh: Color = this.surfaceContainerHigh,
        onSurfaceContainerHigh: Color = this.onSurfaceContainerHigh,
        surfaceContainerHighest: Color = this.surfaceContainerHighest,
        onSurfaceContainerHighest: Color = this.onSurfaceContainerHighest,
        outline: Color = this.outline,
        dividerLine: Color = this.dividerLine,
        windowDimming: Color = this.windowDimming,
        sliderKeyPoint: Color = this.sliderKeyPoint,
        sliderKeyPointForeground: Color = this.sliderKeyPointForeground,
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
    ): Colors = Colors(
        primary,
        onPrimary,
        primaryVariant,
        onPrimaryVariant,
        error,
        onError,
        errorContainer,
        onErrorContainer,
        disabledPrimary,
        disabledOnPrimary,
        disabledPrimaryButton,
        disabledOnPrimaryButton,
        disabledPrimarySlider,
        primaryContainer,
        onPrimaryContainer,
        secondary,
        onSecondary,
        secondaryVariant,
        onSecondaryVariant,
        disabledSecondary,
        disabledOnSecondary,
        disabledSecondaryVariant,
        disabledOnSecondaryVariant,
        secondaryContainer,
        onSecondaryContainer,
        secondaryContainerVariant,
        onSecondaryContainerVariant,
        tertiaryContainer,
        onTertiaryContainer,
        tertiaryContainerVariant,
        background,
        onBackground,
        onBackgroundVariant,
        surface,
        onSurface,
        surfaceVariant,
        onSurfaceSecondary,
        onSurfaceVariantSummary,
        onSurfaceVariantActions,
        disabledOnSurface,
        surfaceContainer,
        onSurfaceContainer,
        onSurfaceContainerVariant,
        surfaceContainerHigh,
        onSurfaceContainerHigh,
        surfaceContainerHighest,
        onSurfaceContainerHighest,
        outline,
        dividerLine,
        windowDimming,
        sliderKeyPoint,
        sliderKeyPointForeground,
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
    )
}

/** COUI light scheme. COUI: coui_color_blue #0080FF; gray page surface, white cards and dialog panels. */
fun lightColorScheme(
    primary: Color = Color(0xFF0080FF),
    onPrimary: Color = Color.White,
    primaryVariant: Color = Color(0xFF0080FF),
    onPrimaryVariant: Color = Color(0xFFAECDFF),
    error: Color = Color(0xFFDB382C),
    onError: Color = Color(0xFFFFFFFF),
    errorContainer: Color = Color(0x26EB3B2F),
    onErrorContainer: Color = Color(0xFFDB382C),
    disabledPrimary: Color = Color(0x4D0080FF),
    disabledOnPrimary: Color = Color.White,
    disabledPrimaryButton: Color = Color(0x4D0080FF),
    disabledOnPrimaryButton: Color = Color(0x8AFFFFFF),
    disabledPrimarySlider: Color = Color(0x4D0080FF),
    primaryContainer: Color = Color(0xFF0080FF),
    onPrimaryContainer: Color = Color.White,
    secondary: Color = Color(0xFFE5E5E5),
    onSecondary: Color = Color.White,
    secondaryVariant: Color = Color(0x14000000),
    onSecondaryVariant: Color = Color(0xE6000000),
    disabledSecondary: Color = Color(0xFFF2F2F2),
    disabledOnSecondary: Color = Color(0x42000000),
    disabledSecondaryVariant: Color = Color(0x14000000),
    disabledOnSecondaryVariant: Color = Color(0x42000000),
    secondaryContainer: Color = Color(0x14000000),
    onSecondaryContainer: Color = Color(0x66000000),
    secondaryContainerVariant: Color = Color(0x14000000),
    onSecondaryContainerVariant: Color = Color(0x66000000),
    tertiaryContainer: Color = Color(0x260080FF),
    onTertiaryContainer: Color = Color(0xFF0080FF),
    tertiaryContainerVariant: Color = Color(0x260080FF),
    background: Color = Color.White,
    onBackground: Color = Color(0xE6000000),
    onBackgroundVariant: Color = Color(0x8A000000),
    surface: Color = Color(0xFFF0F1F2),
    onSurface: Color = Color(0xE6000000),
    surfaceVariant: Color = Color.White,
    onSurfaceSecondary: Color = Color(0x8A000000),
    onSurfaceVariantSummary: Color = Color(0x66000000),
    onSurfaceVariantActions: Color = Color(0x42000000),
    disabledOnSurface: Color = Color(0x42000000),
    surfaceContainer: Color = Color.White,
    onSurfaceContainer: Color = Color(0xE6000000),
    onSurfaceContainerVariant: Color = Color(0x66000000),
    surfaceContainerHigh: Color = Color(0xFFE6E6E6),
    onSurfaceContainerHigh: Color = Color(0x42000000),
    surfaceContainerHighest: Color = Color.White,
    onSurfaceContainerHighest: Color = Color(0xE6000000),
    outline: Color = Color(0x26000000),
    dividerLine: Color = Color(0x1F000000),
    windowDimming: Color = Color.Black.copy(alpha = 0.2F),
    sliderKeyPoint: Color = Color(0xFFF2F2F2),
    sliderKeyPointForeground: Color = Color(0xFF6EB5FF),
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
): Colors = Colors(
    primary,
    onPrimary,
    primaryVariant,
    onPrimaryVariant,
    error,
    onError,
    errorContainer,
    onErrorContainer,
    disabledPrimary,
    disabledOnPrimary,
    disabledPrimaryButton,
    disabledOnPrimaryButton,
    disabledPrimarySlider,
    primaryContainer,
    onPrimaryContainer,
    secondary,
    onSecondary,
    secondaryVariant,
    onSecondaryVariant,
    disabledSecondary,
    disabledOnSecondary,
    disabledSecondaryVariant,
    disabledOnSecondaryVariant,
    secondaryContainer,
    onSecondaryContainer,
    secondaryContainerVariant,
    onSecondaryContainerVariant,
    tertiaryContainer,
    onTertiaryContainer,
    tertiaryContainerVariant,
    background,
    onBackground,
    onBackgroundVariant,
    surface,
    onSurface,
    surfaceVariant,
    onSurfaceSecondary,
    onSurfaceVariantSummary,
    onSurfaceVariantActions,
    disabledOnSurface,
    surfaceContainer,
    onSurfaceContainer,
    onSurfaceContainerVariant,
    surfaceContainerHigh,
    onSurfaceContainerHigh,
    surfaceContainerHighest,
    onSurfaceContainerHighest,
    outline,
    dividerLine,
    windowDimming,
    sliderKeyPoint,
    sliderKeyPointForeground,
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
)

/** COUI dark scheme. COUI: coui_color_blue_dark #1A8CFF; pure black page surface, translucent white cards. */
fun darkColorScheme(
    primary: Color = Color(0xFF1A8CFF),
    onPrimary: Color = Color.White,
    primaryVariant: Color = Color(0xFF1A8CFF),
    onPrimaryVariant: Color = Color(0xFF99C7F1),
    error: Color = Color(0xFFFF6C61),
    onError: Color = Color.White,
    errorContainer: Color = Color(0x40EB493D),
    onErrorContainer: Color = Color(0xFFFF6C61),
    disabledPrimary: Color = Color(0x4D1A8CFF),
    disabledOnPrimary: Color = Color(0xFF404040),
    disabledPrimaryButton: Color = Color(0x4D1A8CFF),
    disabledOnPrimaryButton: Color = Color(0x28FFFFFF),
    disabledPrimarySlider: Color = Color(0x4D1A8CFF),
    primaryContainer: Color = Color(0xFF1A8CFF),
    onPrimaryContainer: Color = Color.White,
    secondary: Color = Color(0xFF757575),
    onSecondary: Color = Color.White,
    secondaryVariant: Color = Color(0x26FFFFFF),
    onSecondaryVariant: Color = Color(0xE6FFFFFF),
    disabledSecondary: Color = Color(0x4D757575),
    disabledOnSecondary: Color = Color(0x4DFFFFFF),
    disabledSecondaryVariant: Color = Color(0x1AFFFFFF),
    disabledOnSecondaryVariant: Color = Color(0x4DFFFFFF),
    secondaryContainer: Color = Color(0x26FFFFFF),
    onSecondaryContainer: Color = Color(0x66FFFFFF),
    secondaryContainerVariant: Color = Color(0x26FFFFFF),
    onSecondaryContainerVariant: Color = Color(0x66FFFFFF),
    tertiaryContainer: Color = Color(0x401A8CFF),
    onTertiaryContainer: Color = Color(0xFF5C9DFF),
    tertiaryContainerVariant: Color = Color(0x401A8CFF),
    background: Color = Color(0xFF1E1E1E),
    onBackground: Color = Color(0xE6FFFFFF),
    onBackgroundVariant: Color = Color(0x8AFFFFFF),
    surface: Color = Color.Black,
    onSurface: Color = Color(0xE6FFFFFF),
    surfaceVariant: Color = Color(0x1AFFFFFF),
    onSurfaceSecondary: Color = Color(0x8AFFFFFF),
    onSurfaceVariantSummary: Color = Color(0x66FFFFFF),
    onSurfaceVariantActions: Color = Color(0x4DFFFFFF),
    disabledOnSurface: Color = Color(0x4DFFFFFF),
    surfaceContainer: Color = Color(0x1AFFFFFF),
    onSurfaceContainer: Color = Color(0xE6FFFFFF),
    onSurfaceContainerVariant: Color = Color(0x66FFFFFF),
    surfaceContainerHigh: Color = Color(0x33FFFFFF),
    onSurfaceContainerHigh: Color = Color(0x4DFFFFFF),
    surfaceContainerHighest: Color = Color(0xFF1E1E1E),
    onSurfaceContainerHighest: Color = Color(0xE6FFFFFF),
    outline: Color = Color(0x26FFFFFF),
    dividerLine: Color = Color(0x33FFFFFF),
    windowDimming: Color = Color.Black.copy(alpha = 0.6F),
    sliderKeyPoint: Color = Color(0xFF595959),
    sliderKeyPointForeground: Color = Color(0xFF5DAAFF),
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
): Colors = Colors(
    primary,
    onPrimary,
    primaryVariant,
    onPrimaryVariant,
    error,
    onError,
    errorContainer,
    onErrorContainer,
    disabledPrimary,
    disabledOnPrimary,
    disabledPrimaryButton,
    disabledOnPrimaryButton,
    disabledPrimarySlider,
    primaryContainer,
    onPrimaryContainer,
    secondary,
    onSecondary,
    secondaryVariant,
    onSecondaryVariant,
    disabledSecondary,
    disabledOnSecondary,
    disabledSecondaryVariant,
    disabledOnSecondaryVariant,
    secondaryContainer,
    onSecondaryContainer,
    secondaryContainerVariant,
    onSecondaryContainerVariant,
    tertiaryContainer,
    onTertiaryContainer,
    tertiaryContainerVariant,
    background,
    onBackground,
    onBackgroundVariant,
    surface,
    onSurface,
    surfaceVariant,
    onSurfaceSecondary,
    onSurfaceVariantSummary,
    onSurfaceVariantActions,
    disabledOnSurface,
    surfaceContainer,
    onSurfaceContainer,
    onSurfaceContainerVariant,
    surfaceContainerHigh,
    onSurfaceContainerHigh,
    surfaceContainerHighest,
    onSurfaceContainerHighest,
    outline,
    dividerLine,
    windowDimming,
    sliderKeyPoint,
    sliderKeyPointForeground,
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
)

@Stable
internal fun Colors.updateColorsFrom(other: Colors) {
    primary = other.primary
    onPrimary = other.onPrimary
    primaryVariant = other.primaryVariant
    onPrimaryVariant = other.onPrimaryVariant
    error = other.error
    onError = other.onError
    errorContainer = other.errorContainer
    onErrorContainer = other.onErrorContainer
    disabledPrimary = other.disabledPrimary
    disabledOnPrimary = other.disabledOnPrimary
    disabledPrimaryButton = other.disabledPrimaryButton
    disabledOnPrimaryButton = other.disabledOnPrimaryButton
    disabledPrimarySlider = other.disabledPrimarySlider
    primaryContainer = other.primaryContainer
    onPrimaryContainer = other.onPrimaryContainer
    secondary = other.secondary
    onSecondary = other.onSecondary
    secondaryVariant = other.secondaryVariant
    onSecondaryVariant = other.onSecondaryVariant
    disabledSecondary = other.disabledSecondary
    disabledOnSecondary = other.disabledOnSecondary
    disabledSecondaryVariant = other.disabledSecondaryVariant
    disabledOnSecondaryVariant = other.disabledOnSecondaryVariant
    secondaryContainer = other.secondaryContainer
    onSecondaryContainer = other.onSecondaryContainer
    secondaryContainerVariant = other.secondaryContainerVariant
    onSecondaryContainerVariant = other.onSecondaryContainerVariant
    tertiaryContainer = other.tertiaryContainer
    onTertiaryContainer = other.onTertiaryContainer
    tertiaryContainerVariant = other.tertiaryContainerVariant
    background = other.background
    onBackground = other.onBackground
    onBackgroundVariant = other.onBackgroundVariant
    surface = other.surface
    onSurface = other.onSurface
    surfaceVariant = other.surfaceVariant
    onSurfaceSecondary = other.onSurfaceSecondary
    onSurfaceVariantSummary = other.onSurfaceVariantSummary
    onSurfaceVariantActions = other.onSurfaceVariantActions
    disabledOnSurface = other.disabledOnSurface
    surfaceContainer = other.surfaceContainer
    onSurfaceContainer = other.onSurfaceContainer
    onSurfaceContainerVariant = other.onSurfaceContainerVariant
    surfaceContainerHigh = other.surfaceContainerHigh
    onSurfaceContainerHigh = other.onSurfaceContainerHigh
    surfaceContainerHighest = other.surfaceContainerHighest
    onSurfaceContainerHighest = other.onSurfaceContainerHighest
    outline = other.outline
    dividerLine = other.dividerLine
    windowDimming = other.windowDimming
    sliderKeyPoint = other.sliderKeyPoint
    sliderKeyPointForeground = other.sliderKeyPointForeground
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
}

internal val LocalColors = staticCompositionLocalOf { lightColorScheme() }
