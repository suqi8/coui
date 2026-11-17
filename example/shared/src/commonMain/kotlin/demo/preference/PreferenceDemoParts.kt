// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import demo.framework.PlaygroundPreview
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.CardListGroupScope
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_end_value
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_long_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_show_end_text
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * The preview card of a preference playground: the rows sit in a [CardListGroup] edge to edge, so
 * they get their COUI card positions and dividers.
 *
 * @param modifier The modifier for the section.
 * @param content The previewed rows.
 */
@Composable
internal fun PreferencePreview(
    modifier: Modifier = Modifier,
    content: CardListGroupScope.() -> Unit,
) {
    PlaygroundPreview(modifier = modifier, contentPadding = PaddingValues(0.dp)) {
        CardListGroup(content = content)
    }
}

/**
 * A switch parameter row of a playground.
 */
internal fun CardListGroupScope.switchControl(
    title: StringResource,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    item(key = title) {
        SwitchPreference(
            checked = checked,
            onCheckedChange = onCheckedChange,
            title = stringResource(title),
        )
    }
}

/**
 * A dropdown parameter row of a playground whose options are localized.
 */
internal fun CardListGroupScope.choiceControl(
    title: StringResource,
    options: List<StringResource>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
) {
    item(key = title) {
        OverlayDropdownPreference(
            items = options.map { stringResource(it) },
            selectedIndex = selectedIndex,
            title = stringResource(title),
            onSelectedIndexChange = onSelectedIndexChange,
        )
    }
}

/**
 * A dropdown parameter row of a playground whose options are literals: API names, enum names or
 * numbers, which stay untranslated.
 */
internal fun CardListGroupScope.literalChoiceControl(
    title: StringResource,
    options: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
) {
    item(key = title) {
        OverlayDropdownPreference(
            items = options,
            selectedIndex = selectedIndex,
            title = stringResource(title),
            onSelectedIndexChange = onSelectedIndexChange,
        )
    }
}

/**
 * The text and decoration parameters shared by most preference playgrounds.
 */
@Stable
internal class PreferenceRowState(
    longTitle: Boolean = false,
    showSummary: Boolean = true,
    longSummary: Boolean = false,
    showIcon: Boolean = false,
    showEndText: Boolean = false,
    enabled: Boolean = true,
) {
    var longTitle by mutableStateOf(longTitle)
    var showSummary by mutableStateOf(showSummary)
    var longSummary by mutableStateOf(longSummary)
    var showIcon by mutableStateOf(showIcon)
    var showEndText by mutableStateOf(showEndText)
    var enabled by mutableStateOf(enabled)

    /** The row title: [short], or the long sample title. */
    @Composable
    fun title(short: StringResource = Res.string.sample_title): String = stringResource(if (longTitle) Res.string.sample_long_title else short)

    /** The row summary, or null when it is hidden. */
    @Composable
    fun summary(short: StringResource = Res.string.sample_summary): String? = when {
        !showSummary -> null
        longSummary -> stringResource(Res.string.sample_long_summary)
        else -> stringResource(short)
    }

    companion object {
        val Saver: Saver<PreferenceRowState, Any> = listSaver(
            save = { listOf(it.longTitle, it.showSummary, it.longSummary, it.showIcon, it.showEndText, it.enabled) },
            restore = { PreferenceRowState(it[0], it[1], it[2], it[3], it[4], it[5]) },
        )
    }
}

/**
 * Remembers a [PreferenceRowState] across configuration changes and list item disposal.
 */
@Composable
internal fun rememberPreferenceRowState(
    showSummary: Boolean = true,
    showIcon: Boolean = false,
    showEndText: Boolean = false,
): PreferenceRowState = rememberSaveable(saver = PreferenceRowState.Saver) {
    PreferenceRowState(showSummary = showSummary, showIcon = showIcon, showEndText = showEndText)
}

/**
 * Adds the parameter rows of a [PreferenceRowState]: title length, summary, icon, end text and
 * enabled. Rows for slots the demoed preference does not have can be left out.
 */
internal fun CardListGroupScope.preferenceRowControls(
    state: PreferenceRowState,
    summary: Boolean = true,
    icon: Boolean = true,
    endText: Boolean = true,
    enabled: Boolean = true,
) {
    switchControl(Res.string.demo_pref_long_title, state.longTitle) { state.longTitle = it }
    if (summary) {
        switchControl(Res.string.common_show_summary, state.showSummary) { state.showSummary = it }
        if (state.showSummary) {
            switchControl(Res.string.demo_pref_long_summary, state.longSummary) { state.longSummary = it }
        }
    }
    if (icon) switchControl(Res.string.common_show_icon, state.showIcon) { state.showIcon = it }
    if (endText) switchControl(Res.string.demo_pref_show_end_text, state.showEndText) { state.showEndText = it }
    if (enabled) switchControl(Res.string.common_enabled, state.enabled) { state.enabled = it }
}

/**
 * The start icon of a demo row.
 *
 * @param modifier The modifier for the icon.
 * @param imageVector The icon.
 */
@Composable
internal fun PreferenceStartIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = COUIIcons.Contacts,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier,
        tint = COUITheme.colorScheme.labelPrimary,
    )
}

/**
 * The secondary end text of a demo row (COUI assignment text).
 *
 * @param modifier The modifier for the text.
 * @param text The text; defaults to a short sample value.
 * @param enabled Whether the row is enabled.
 */
@Composable
internal fun PreferenceEndText(
    modifier: Modifier = Modifier,
    text: String = stringResource(Res.string.demo_pref_end_value),
    enabled: Boolean = true,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = COUITheme.textStyles.bodyM.fontSize,
        color = if (enabled) COUITheme.colorScheme.labelTertiary else COUITheme.colorScheme.disabledNeutral,
    )
}
