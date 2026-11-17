// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.framework

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.CardListGroupScope
import io.github.suqi8.coui.kmp.basic.SnackbarHostState
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_parameters
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preview
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * The snackbar host state of the demo page, so variants can show snackbars.
 */
val LocalDemoSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No demo SnackbarHostState provided!")
}

/**
 * Adds a titled variant: a category title above the content, which sits in a [Card] unless
 * [inCard] is false.
 *
 * Remember: this builder is not composable. Read resources and state inside [content].
 *
 * @param key A key unique within the page.
 * @param title The localized section title.
 * @param inCard Whether to wrap [content] in a card.
 * @param content The variant; a `Column` scope inside the card.
 */
fun LazyListScope.variant(
    key: String,
    title: StringResource,
    inCard: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    item(key = key) {
        DemoSection(title = stringResource(title), inCard = inCard, content = content)
    }
}

/**
 * [variant] with a literal title, for titles that are API names and stay untranslated.
 */
fun LazyListScope.variant(
    key: String,
    title: String,
    inCard: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    item(key = key) {
        DemoSection(title = title, inCard = inCard, content = content)
    }
}

/**
 * Adds the playground: one list item holding a live preview and the adjustable parameters that
 * drive it. Keep the shared state at the top of [content] (prefer `rememberSaveable`, the item is
 * disposed when it scrolls away) and lay out a [PlaygroundPreview] followed by
 * [PlaygroundControls]:
 *
 * ```
 * playground {
 *     var enabled by rememberSaveable { mutableStateOf(true) }
 *     PlaygroundPreview { Switch(checked = …, enabled = enabled, …) }
 *     PlaygroundControls {
 *         item { SwitchPreference(title = stringResource(Res.string.common_enabled), checked = enabled, onCheckedChange = { enabled = it }) }
 *     }
 * }
 * ```
 *
 * @param key A key unique within the page.
 * @param content The preview and the controls.
 */
fun LazyListScope.playground(
    key: String = "playground",
    content: @Composable ColumnScope.() -> Unit,
) {
    item(key = key) {
        Column(content = content)
    }
}

/**
 * The preview card of a [playground], titled "Preview".
 *
 * @param modifier The modifier for the section.
 * @param inCard Whether to wrap [content] in a card; whole-width previews may draw their own.
 * @param contentPadding The padding inside the card around [content].
 * @param content The previewed component.
 */
@Composable
fun PlaygroundPreview(
    modifier: Modifier = Modifier,
    inCard: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    DemoSection(
        title = stringResource(Res.string.demo_preview),
        modifier = modifier,
        inCard = inCard,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(contentPadding), content = content)
    }
}

/**
 * The parameter card of a [playground], titled "Parameters": a card list group of preference rows
 * (switch, dropdown, slider, … preferences) that edit the preview's state.
 *
 * @param modifier The modifier for the section.
 * @param content The parameter rows.
 */
@Composable
fun PlaygroundControls(
    modifier: Modifier = Modifier,
    content: CardListGroupScope.() -> Unit,
) {
    DemoSection(
        title = stringResource(Res.string.demo_parameters),
        modifier = modifier,
    ) {
        CardListGroup(content = content)
    }
}

/**
 * A titled section of a demo page: a COUI preference category title above a card.
 *
 * @param title The section title.
 * @param modifier The modifier for the section.
 * @param inCard Whether to wrap [content] in a card.
 * @param content The section content.
 */
@Composable
fun DemoSection(
    title: String,
    modifier: Modifier = Modifier,
    inCard: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        PreferenceCategory(title = title)
        // One call site for the slot: without the card the content sits on the page background.
        Card(
            modifier = if (inCard) Modifier.padding(horizontal = 16.dp) else Modifier,
            cornerRadius = if (inCard) CardDefaults.CornerRadius else 0.dp,
            colors = if (inCard) CardDefaults.defaultColors() else CardDefaults.defaultColors(color = Color.Transparent),
            content = content,
        )
    }
}
