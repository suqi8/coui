// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.playground
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.basic.CardShadow
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.blur.MaterialBlurDefaults
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_confirm
import io.github.suqi8.coui.kmp.shared.generated.resources.common_default
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_off
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_clickable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_corner_radius
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_dialog_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_dialog_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_hold_down
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_long_press
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_long_press_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_long_pressed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_press_feedback
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_shadow
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_shadow_angle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_shadow_card
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_shadow_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_shadow_top_hidden
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_show_indication
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_variant_feedback
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_variant_long_press
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_variant_shadow
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preview
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_text
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.PressFeedbackType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import utils.panelBlur

internal val CardDemo = Demo(
    id = "card",
    apiName = "Card",
    title = Res.string.demo_card_title,
    category = DemoCategory.Display,
    page = DemoPage.Variants { cardPage() },
    keywords = listOf("shadow", "阴影", "press", "按压"),
)

/** The shadow choices of the playground: none, the default, top edge hidden and a 45 degree angle. */
private val PlaygroundShadows: List<CardShadow?> = listOf(
    null,
    CardDefaults.shadow(),
    CardDefaults.shadow(hideTop = true),
    CardDefaults.shadow(size = 24.dp, angle = 45, offset = 6.dp),
)

private fun LazyListScope.cardPage() {
    playground {
        var feedbackIndex by rememberSaveable { mutableIntStateOf(PressFeedbackType.Tint.ordinal) }
        var shadowIndex by rememberSaveable { mutableIntStateOf(0) }
        var cornerRadius by rememberSaveable { mutableFloatStateOf(CardDefaults.CornerRadius.value) }
        var clickable by rememberSaveable { mutableStateOf(true) }
        var longPress by rememberSaveable { mutableStateOf(false) }
        var holdDown by rememberSaveable { mutableStateOf(false) }
        var showIndication by rememberSaveable { mutableStateOf(false) }
        var longText by rememberSaveable { mutableStateOf(false) }

        val snackbarHostState = LocalDemoSnackbarHostState.current
        val scope = rememberCoroutineScope()
        val feedbackType = PressFeedbackType.entries[feedbackIndex]

        // The preview card sits on the page background, outside a card: a card clips its content,
        // which would cut the shadow off.
        OpenSection(title = stringResource(Res.string.demo_preview)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = cornerRadius.dp,
                insideMargin = PaddingValues(16.dp),
                shadow = PlaygroundShadows[shadowIndex],
                pressFeedbackType = feedbackType,
                showIndication = showIndication,
                holdDownState = holdDown,
                onClick = if (clickable) {
                    { scope.launch { snackbarHostState.showSnackbar(getString(Res.string.demo_card_clicked)) } }
                } else {
                    null
                },
                onLongPress = if (longPress) {
                    { scope.launch { snackbarHostState.showSnackbar(getString(Res.string.demo_card_long_pressed)) } }
                } else {
                    null
                },
            ) {
                Text(
                    text = stringResource(Res.string.demo_card_title),
                    style = COUITheme.textStyles.headlineS,
                    color = COUITheme.colorScheme.labelPrimary,
                )
                Text(
                    text = if (longText) stringResource(Res.string.sample_long_text) else "PressFeedbackType.${feedbackType.name}",
                    style = COUITheme.textStyles.bodyL,
                    color = COUITheme.colorScheme.labelSecondaryVariant,
                )
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_card_press_feedback),
                    summary = "pressFeedbackType",
                    items = PressFeedbackType.entries.map { it.name },
                    selectedIndex = feedbackIndex,
                    onSelectedIndexChange = { feedbackIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_card_shadow),
                    summary = "shadow",
                    items = listOf(
                        stringResource(Res.string.common_off),
                        stringResource(Res.string.common_default),
                        stringResource(Res.string.demo_card_shadow_top_hidden),
                        stringResource(Res.string.demo_card_shadow_angle),
                    ),
                    selectedIndex = shadowIndex,
                    onSelectedIndexChange = { shadowIndex = it },
                )
            }
            item {
                SliderPreference(
                    value = cornerRadius,
                    onValueChange = { cornerRadius = it },
                    title = stringResource(Res.string.demo_card_corner_radius),
                    valueText = "${cornerRadius.toInt()} dp",
                    valueRange = 0f..40f,
                    steps = 39,
                    showKeyPoints = false,
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_card_clickable),
                    summary = "onClick",
                    checked = clickable,
                    onCheckedChange = { clickable = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_card_long_press),
                    summary = "onLongPress",
                    checked = longPress,
                    onCheckedChange = { longPress = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_card_hold_down),
                    summary = "holdDownState",
                    checked = holdDown,
                    onCheckedChange = { holdDown = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_card_show_indication),
                    summary = "showIndication",
                    checked = showIndication,
                    onCheckedChange = { showIndication = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_long_text),
                    checked = longText,
                    onCheckedChange = { longText = it },
                )
            }
        }
    }
    item(key = "feedback") {
        OpenSection(title = stringResource(Res.string.demo_card_variant_feedback)) {
            FeedbackCard(type = PressFeedbackType.Tint, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FeedbackCard(type = PressFeedbackType.Sink, modifier = Modifier.weight(1f))
                FeedbackCard(type = PressFeedbackType.Tilt, modifier = Modifier.weight(1f))
            }
        }
    }
    item(key = "long_press") {
        OpenSection(title = stringResource(Res.string.demo_card_variant_long_press)) {
            LongPressDialogCard()
        }
    }
    item(key = "shadow") {
        OpenSection(title = stringResource(Res.string.demo_card_variant_shadow)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = PaddingValues(16.dp),
                shadow = CardDefaults.shadow(),
            ) {
                Text(text = stringResource(Res.string.demo_card_shadow_card))
                Text(
                    text = stringResource(Res.string.demo_card_shadow_summary),
                    color = COUITheme.colorScheme.labelSecondaryVariant,
                    style = COUITheme.textStyles.bodyM,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    insideMargin = PaddingValues(16.dp),
                    shadow = CardDefaults.shadow(hideTop = true),
                    onClick = {},
                ) {
                    Text(text = stringResource(Res.string.demo_card_shadow_top_hidden))
                }
                Card(
                    modifier = Modifier.weight(1f),
                    insideMargin = PaddingValues(16.dp),
                    shadow = CardDefaults.shadow(size = 24.dp, angle = 45, offset = 6.dp),
                ) {
                    Text(text = stringResource(Res.string.demo_card_shadow_angle))
                }
            }
        }
    }
}

/**
 * A titled section whose content sits on the page background with the card side margins, instead
 * of inside a clipping card, so card shadows and press motion are not cut off.
 */
@Composable
private fun OpenSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        PreferenceCategory(title = title)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

/** A clickable card labelled with its press feedback type. */
@Composable
private fun FeedbackCard(
    type: PressFeedbackType,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        insideMargin = PaddingValues(16.dp),
        pressFeedbackType = type,
        onClick = {},
    ) {
        Text(
            text = stringResource(Res.string.demo_card_title),
            style = COUITheme.textStyles.headlineXS,
            color = COUITheme.colorScheme.labelPrimary,
        )
        Text(
            text = "PressFeedbackType\n.${type.name}",
            style = COUITheme.textStyles.bodyL,
            color = COUITheme.colorScheme.labelSecondaryVariant,
        )
    }
}

/** A card held down while the dialog its long press opened is showing. */
@Composable
private fun LongPressDialogCard() {
    var showDialog by remember { mutableStateOf(false) }
    var holdDown by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(16.dp),
        holdDownState = holdDown,
        onLongPress = {
            showDialog = true
            holdDown = true
        },
    ) {
        Text(
            text = stringResource(Res.string.demo_card_title),
            style = COUITheme.textStyles.headlineXS,
            color = COUITheme.colorScheme.labelPrimary,
        )
        Text(
            text = stringResource(Res.string.demo_card_long_press_hint),
            style = COUITheme.textStyles.bodyL,
            color = COUITheme.colorScheme.labelSecondaryVariant,
        )
    }

    val blur = panelBlur(MaterialBlurDefaults::dialog)
    OverlayDialog(
        show = showDialog,
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        title = stringResource(Res.string.demo_card_dialog_title),
        summary = stringResource(Res.string.demo_card_dialog_summary),
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = { showDialog = false },
        onDismissFinished = { holdDown = false },
        materialStroke = blur != null,
        content = {
            DialogButtonBar(
                negative = DialogButtonBarAction(text = stringResource(Res.string.common_cancel), onClick = { showDialog = false }),
                positive = DialogButtonBarAction(text = stringResource(Res.string.common_confirm), onClick = { showDialog = false }),
            )
        },
    )
}
