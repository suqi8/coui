// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package page

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.AppSettings
import app.LocalAppSettings
import app.LocalNavigator
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.CardListGroupScope
import io.github.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.app_tab_settings
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_about
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_about_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_color_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_color_mode_dark
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_color_mode_light
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_color_mode_monet_dark
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_color_mode_monet_light
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_color_mode_monet_system
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_color_mode_system
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_color_spec
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_fps_monitor
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_fps_monitor_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_group_appearance
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_group_developer
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_group_effects
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_group_interaction
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_group_other
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_key_color
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_key_color_blue
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_key_color_default
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_key_color_green
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_key_color_orange
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_key_color_pink
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_key_color_purple
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_key_color_teal
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_key_color_yellow
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_material_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_material_blur_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_palette_style
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_scroll_end_haptic
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_scroll_end_haptic_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_squircle
import io.github.suqi8.coui.kmp.shared.generated.resources.settings_squircle_summary
import io.github.suqi8.coui.kmp.theme.ThemeColorSpec
import io.github.suqi8.coui.kmp.theme.ThemePaletteStyle
import misc.VersionInfo
import navigation3.Route
import org.jetbrains.compose.resources.stringResource
import ui.AppPage
import ui.ColorMode

private val PaletteStyleOptions = ThemePaletteStyle.entries.map { it.name }
private val ColorSpecOptions = ThemeColorSpec.entries.map { it.name }

/**
 * The settings tab: only the app-wide settings. Component options live on their demo pages.
 */
@Composable
fun SettingsPage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    val shaderSupported = isRuntimeShaderSupported()
    AppPage(
        title = stringResource(Res.string.app_tab_settings),
        modifier = modifier,
        subtitle = "v${VersionInfo.VERSION_NAME} (${VersionInfo.VERSION_CODE})",
    ) {
        settingsGroup("appearance", { stringResource(Res.string.settings_group_appearance) }) { appearanceRows(it) }
        if (shaderSupported) {
            settingsGroup("effects", { stringResource(Res.string.settings_group_effects) }) { effectRows(it) }
        }
        settingsGroup("interaction", { stringResource(Res.string.settings_group_interaction) }) { settings ->
            item {
                SwitchPreference(
                    title = stringResource(Res.string.settings_scroll_end_haptic),
                    summary = stringResource(Res.string.settings_scroll_end_haptic_summary),
                    checked = settings.enableScrollEndHaptic,
                    onCheckedChange = { settings.enableScrollEndHaptic = it },
                )
            }
        }
        settingsGroup("developer", { stringResource(Res.string.settings_group_developer) }) { settings ->
            item {
                SwitchPreference(
                    title = stringResource(Res.string.settings_fps_monitor),
                    summary = stringResource(Res.string.settings_fps_monitor_summary),
                    checked = settings.showFpsMonitor,
                    onCheckedChange = { settings.showFpsMonitor = it },
                )
            }
        }
        settingsGroup("other", { stringResource(Res.string.settings_group_other) }) {
            item {
                ArrowPreference(
                    title = stringResource(Res.string.settings_about),
                    summary = stringResource(Res.string.settings_about_summary),
                    onClick = { navigator.push(Route.About) },
                )
            }
        }
    }
}

private fun LazyListScope.settingsGroup(
    key: String,
    title: @Composable () -> String,
    rows: CardListGroupScope.(AppSettings) -> Unit,
) {
    item(key = key) {
        val settings = LocalAppSettings.current
        PreferenceCategory(title = title())
        Card(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
            // The builder runs inside the group's composition, so rows can depend on the settings.
            CardListGroup { rows(settings) }
        }
    }
}

private fun CardListGroupScope.appearanceRows(settings: AppSettings) {
    item(key = "color_mode") {
        OverlayDropdownPreference(
            title = stringResource(Res.string.settings_color_mode),
            items = listOf(
                stringResource(Res.string.settings_color_mode_system),
                stringResource(Res.string.settings_color_mode_light),
                stringResource(Res.string.settings_color_mode_dark),
                stringResource(Res.string.settings_color_mode_monet_system),
                stringResource(Res.string.settings_color_mode_monet_light),
                stringResource(Res.string.settings_color_mode_monet_dark),
            ),
            selectedIndex = settings.colorMode.ordinal,
            onSelectedIndexChange = { settings.colorMode = ColorMode.entries[it] },
        )
    }
    if (settings.colorMode.isMonet) {
        item(key = "key_color") {
            OverlayDropdownPreference(
                title = stringResource(Res.string.settings_key_color),
                items = listOf(
                    stringResource(Res.string.settings_key_color_default),
                    stringResource(Res.string.settings_key_color_blue),
                    stringResource(Res.string.settings_key_color_green),
                    stringResource(Res.string.settings_key_color_purple),
                    stringResource(Res.string.settings_key_color_yellow),
                    stringResource(Res.string.settings_key_color_orange),
                    stringResource(Res.string.settings_key_color_pink),
                    stringResource(Res.string.settings_key_color_teal),
                ),
                selectedIndex = settings.seedIndex,
                onSelectedIndexChange = { settings.seedIndex = it },
            )
        }
        if (settings.seedIndex > 0) {
            item(key = "palette_style") {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.settings_palette_style),
                    items = PaletteStyleOptions,
                    selectedIndex = settings.paletteStyle,
                    onSelectedIndexChange = { settings.paletteStyle = it },
                )
            }
            item(key = "color_spec") {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.settings_color_spec),
                    items = ColorSpecOptions,
                    selectedIndex = settings.colorSpec,
                    onSelectedIndexChange = { settings.colorSpec = it },
                )
            }
        }
    }
}

private fun CardListGroupScope.effectRows(settings: AppSettings) {
    item(key = "material_blur") {
        SwitchPreference(
            title = stringResource(Res.string.settings_material_blur),
            summary = stringResource(Res.string.settings_material_blur_summary),
            checked = settings.enableMaterialBlur,
            onCheckedChange = { settings.enableMaterialBlur = it },
        )
    }
    item(key = "squircle") {
        SwitchPreference(
            title = stringResource(Res.string.settings_squircle),
            summary = stringResource(Res.string.settings_squircle_summary),
            checked = settings.enableSquircle,
            onCheckedChange = { settings.enableSquircle = it },
        )
    }
}
