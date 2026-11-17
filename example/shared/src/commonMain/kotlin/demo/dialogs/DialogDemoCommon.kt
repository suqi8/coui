// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.CardListGroupScope
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.blur.MaterialBlurDefaults
import io.github.suqi8.coui.kmp.blur.MaterialBlurStyle
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialogs_host
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialogs_host_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialogs_material_overlay_only
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialogs_material_summary
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import utils.PanelBlur
import utils.panelBlur

/** The hosts a dialog playground switches between; API names, never translated. */
internal val DialogHostNames = listOf("Overlay", "Window")

/** The index of the `Window…` host in [DialogHostNames]. */
internal const val HOST_WINDOW = 1

/**
 * Returns the COUI 17 blurred panel material when [enabled], or null for the opaque surface.
 *
 * Only the Overlay hosts take it: the Window hosts render in their own platform window, outside the
 * coordinates of the page backdrop.
 *
 * @param enabled Whether the playground asks for the material.
 * @param style Picks the [MaterialBlurStyle] for the current scheme.
 */
@Composable
internal fun demoPanelBlur(
    enabled: Boolean,
    style: (isDark: Boolean) -> MaterialBlurStyle = MaterialBlurDefaults::dialog,
): PanelBlur? {
    val blur = panelBlur(style)
    return if (enabled) blur else null
}

/**
 * The full-width primary button of a playground preview that opens the dialog.
 */
@Composable
internal fun DialogLaunchButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        text = text,
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColorsPrimary(),
    )
}

/**
 * A secondary line of text under a preview, e.g. the API name or the last result.
 */
@Composable
internal fun DialogDemoCaption(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        style = COUITheme.textStyles.bodyM,
        color = COUITheme.colorScheme.labelSecondaryVariant,
    )
}

/** An arrow row that opens a dialog; it stays pressed while the dialog is up. */
internal fun CardListGroupScope.dialogLaunchRow(
    title: StringResource,
    summary: StringResource?,
    holdDown: Boolean,
    onClick: () -> Unit,
) {
    item {
        ArrowPreference(
            title = stringResource(title),
            summary = summary?.let { stringResource(it) },
            onClick = onClick,
            holdDownState = holdDown,
        )
    }
}

/** The Overlay / Window host row of a playground. */
internal fun CardListGroupScope.dialogHostRow(
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
) {
    item {
        OverlayDropdownPreference(
            items = DialogHostNames,
            selectedIndex = selectedIndex,
            title = stringResource(Res.string.demo_dialogs_host),
            summary = stringResource(Res.string.demo_dialogs_host_summary),
            onSelectedIndexChange = onSelectedIndexChange,
        )
    }
}

/** The material blur row of a playground; disabled for the Window host. */
internal fun CardListGroupScope.dialogMaterialRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
) {
    item {
        SwitchPreference(
            checked = checked,
            onCheckedChange = onCheckedChange,
            title = stringResource(Res.string.common_material),
            summary = stringResource(
                if (enabled) Res.string.demo_dialogs_material_summary else Res.string.demo_dialogs_material_overlay_only,
            ),
            enabled = enabled,
        )
    }
}

/** A switch row of a playground. */
internal fun CardListGroupScope.dialogSwitchRow(
    title: StringResource,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    summary: StringResource? = null,
    enabled: Boolean = true,
) {
    item {
        SwitchPreference(
            checked = checked,
            onCheckedChange = onCheckedChange,
            title = stringResource(title),
            summary = summary?.let { stringResource(it) },
            enabled = enabled,
        )
    }
}

/**
 * A dropdown row of a playground.
 *
 * @param items The option labels, already resolved by the caller.
 */
internal fun CardListGroupScope.dialogDropdownRow(
    title: StringResource,
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    summary: StringResource? = null,
    enabled: Boolean = true,
) {
    item {
        OverlayDropdownPreference(
            items = items,
            selectedIndex = selectedIndex,
            title = stringResource(title),
            summary = summary?.let { stringResource(it) },
            enabled = enabled,
            onSelectedIndexChange = onSelectedIndexChange,
        )
    }
}
