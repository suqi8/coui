// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * The application header of an "About" page, mirroring COUIAppInfoPreference
 * (coui_component_preference_application_info.xml).
 *
 * A centered column: the 60dp app [icon] clipped to 16dp corners (COUIRoundImageView
 * couiBorderRadius) 42dp from the top, the 20sp medium [appName] 8dp below it, and the 12sp
 * [appVersion] 4dp below that, with 8dp of space under the block. The header is not a card and
 * draws no background, so it sits directly on the page.
 *
 * When [copyText] and [onCopyClick] are both set, a long press on the version opens a small
 * popup above it holding [copyText] (COUIAppInfoPreference.W0 / Z0: coui_component_popup_window_layout,
 * at least 76 x 48dp, 24dp horizontal padding, 16dp above the version). Tapping it calls
 * [onCopyClick] and dismisses the popup. COUI copies [appVersion] to the clipboard and toasts
 * copyFinishText there; the caller does both in [onCopyClick], since clipboard and toast APIs are
 * platform specific.
 *
 * @param appName The application name (COUI appName).
 * @param appVersion The version text (COUI appVersion).
 * @param modifier The modifier to be applied to the [AppInfoPreference].
 * @param icon The application icon (COUI appIcon). It is sized to [AppInfoPreferenceDefaults.IconSize]
 *   and clipped to [iconCornerRadius].
 * @param iconCornerRadius The corner radius of the icon clip.
 * @param copyText The label of the copy popup (COUI copyText). No popup without it.
 * @param onCopyClick The callback when the copy popup is tapped. No popup without it.
 * @param colors The [AppInfoPreferenceColors] of the header.
 */
@Composable
fun AppInfoPreference(
    appName: String,
    appVersion: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    iconCornerRadius: Dp = AppInfoPreferenceDefaults.IconCornerRadius,
    copyText: String? = null,
    onCopyClick: (() -> Unit)? = null,
    colors: AppInfoPreferenceColors = AppInfoPreferenceDefaults.appInfoPreferenceColors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = AppInfoPreferenceDefaults.BottomMargin),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .padding(top = AppInfoPreferenceDefaults.IconTopMargin)
                    .size(AppInfoPreferenceDefaults.IconSize)
                    .clip(RoundedCornerShape(iconCornerRadius))
                    // COUIAppInfoPreference binds the app name as the icon's content description.
                    .semantics { contentDescription = appName },
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
        }
        Text(
            text = appName,
            modifier = Modifier.padding(
                top = if (icon != null) AppInfoPreferenceDefaults.NameTopMargin else AppInfoPreferenceDefaults.IconTopMargin,
            ),
            style = COUITheme.textStyles.title3,
            color = colors.nameColor,
            textAlign = TextAlign.Center,
        )
        AppInfoVersion(
            appVersion = appVersion,
            copyText = copyText,
            onCopyClick = onCopyClick,
            colors = colors,
        )
    }
}

@Composable
private fun AppInfoVersion(
    appVersion: String,
    copyText: String?,
    onCopyClick: (() -> Unit)?,
    colors: AppInfoPreferenceColors,
) {
    var showPopup by remember { mutableStateOf(false) }
    val canCopy = copyText != null && onCopyClick != null
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)
    Box(modifier = Modifier.padding(top = AppInfoPreferenceDefaults.VersionTopMargin)) {
        Text(
            text = appVersion,
            modifier = if (canCopy) {
                Modifier.combinedClickable(
                    interactionSource = null,
                    indication = null,
                    onLongClick = {
                        currentHapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        showPopup = true
                    },
                    onClick = {},
                )
            } else {
                Modifier
            },
            style = COUITheme.textStyles.footnote2,
            color = colors.versionColor,
        )
        if (showPopup && copyText != null && onCopyClick != null) {
            AppInfoCopyPopup(
                copyText = copyText,
                colors = colors,
                onCopy = {
                    onCopyClick()
                    showPopup = false
                },
                onDismissRequest = { showPopup = false },
            )
        }
    }
}

@Composable
private fun AppInfoCopyPopup(
    copyText: String,
    colors: AppInfoPreferenceColors,
    onCopy: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    // The popup pads its content by the shadow elevation, so take that padding off the gap.
    val marginPx = with(LocalDensity.current) {
        (AppInfoPreferenceDefaults.CopyPopupMarginBottom - AppInfoPreferenceDefaults.CopyPopupShadowElevation).roundToPx()
    }
    val positionProvider = remember(marginPx) { AboveAnchorPositionProvider(marginPx) }
    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        val shape = RoundedCornerShape(AppInfoPreferenceDefaults.CopyPopupCornerRadius)
        Box(
            modifier = Modifier
                .padding(AppInfoPreferenceDefaults.CopyPopupShadowElevation)
                .shadow(AppInfoPreferenceDefaults.CopyPopupShadowElevation, shape)
                .clip(shape)
                .background(colors.copyPopupColor)
                .clickable(role = Role.Button, onClick = onCopy)
                .defaultMinSize(
                    minWidth = AppInfoPreferenceDefaults.CopyPopupMinWidth,
                    minHeight = AppInfoPreferenceDefaults.CopyPopupMinHeight,
                )
                .padding(horizontal = AppInfoPreferenceDefaults.CopyPopupHorizontalPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = copyText,
                style = COUITheme.textStyles.body2,
                color = colors.copyPopupContentColor,
                maxLines = 1,
            )
        }
    }
}

/** Centers the popup horizontally on the anchor and puts its bottom [marginPx] above the anchor. */
private class AboveAnchorPositionProvider(private val marginPx: Int) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
        val y = anchorBounds.top - popupContentSize.height - marginPx
        return IntOffset(
            x.coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0)),
            y.coerceAtLeast(0),
        )
    }
}

object AppInfoPreferenceDefaults {

    /** The size of the app icon (COUI about_app_icon 60dp). */
    val IconSize = 60.dp

    /** The corner radius of the app icon (COUI about_app_icon couiBorderRadius). */
    val IconCornerRadius = 16.dp

    /** The space above the app icon (COUI about_app_icon marginTop). */
    val IconTopMargin = 42.dp

    /** The gap between the icon and the app name (COUI about_app_name marginTop). */
    val NameTopMargin = 8.dp

    /** The gap between the app name and the version (COUI about_app_version marginTop). */
    val VersionTopMargin = 4.dp

    /** The space below the header (COUI coui_component_preference_application_info.xml marginBottom). */
    val BottomMargin = 8.dp

    /** The minimum width of the copy popup (COUI coui_component_copy_window_width). */
    val CopyPopupMinWidth = 76.dp

    /** The minimum height of the copy popup (COUI coui_component_copy_window_height). */
    val CopyPopupMinHeight = 48.dp

    /** The horizontal padding of the copy popup text (COUI coui_component_copy_text_padding_hor). */
    val CopyPopupHorizontalPadding = 24.dp

    /** The gap between the copy popup and the version (COUI coui_component_copy_window_margin_bottom). */
    val CopyPopupMarginBottom = 16.dp

    /** The corner radius of the copy popup (COUI coui_component_copy_popup_window_ripple_bg mask: 12dp). */
    val CopyPopupCornerRadius = 12.dp

    /** The shadow elevation standing in for the coui_popup_window_bg nine-patch shadow. */
    val CopyPopupShadowElevation = 8.dp

    /**
     * The default [AppInfoPreferenceColors].
     *
     * @param nameColor The app name color (COUI couiColorPrimaryNeutral).
     * @param versionColor The version color (COUI couiColorSecondNeutral).
     * @param copyPopupColor The copy popup fill (COUI coui_popup_window_bg, the floating surface).
     * @param copyPopupContentColor The copy popup label color.
     */
    @Composable
    fun appInfoPreferenceColors(
        nameColor: Color = COUITheme.colorScheme.onSurface,
        versionColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        copyPopupColor: Color = COUITheme.colorScheme.backgroundTop,
        copyPopupContentColor: Color = COUITheme.colorScheme.onSurface,
    ): AppInfoPreferenceColors = remember(nameColor, versionColor, copyPopupColor, copyPopupContentColor) {
        AppInfoPreferenceColors(
            nameColor = nameColor,
            versionColor = versionColor,
            copyPopupColor = copyPopupColor,
            copyPopupContentColor = copyPopupContentColor,
        )
    }
}

/**
 * Colors used by an [AppInfoPreference].
 *
 * @param nameColor The color of the app name.
 * @param versionColor The color of the version.
 * @param copyPopupColor The fill of the copy popup.
 * @param copyPopupContentColor The label color of the copy popup.
 */
@Immutable
data class AppInfoPreferenceColors(
    val nameColor: Color,
    val versionColor: Color,
    val copyPopupColor: Color,
    val copyPopupContentColor: Color,
)
