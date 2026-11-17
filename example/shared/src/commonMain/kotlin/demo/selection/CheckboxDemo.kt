// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.selection

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.Checkbox
import io.github.suqi8.coui.kmp.basic.CheckboxDefaults
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_default
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_state
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_state_checked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_state_indeterminate
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_state_unchecked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_style_shape
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_tri_state
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_tri_state_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_variant_shape
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_variant_states
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** The demo page of `Checkbox`, listed in its category and found by the search. */
internal val CheckboxDemo = Demo(
    id = "checkbox",
    apiName = "Checkbox",
    title = Res.string.demo_checkbox_title,
    category = DemoCategory.Selection,
    page = DemoPage.Variants { checkboxPage() },
    searchTerms = Res.string.demo_checkbox_search_terms,
)

/** The checkbox states in the order a tri-state checkbox cycles through them. */
private val CheckboxCycle = listOf(ToggleableState.Off, ToggleableState.Indeterminate, ToggleableState.On)

private fun LazyListScope.checkboxPage() {
    playground {
        var stateIndex by rememberSaveable { mutableIntStateOf(2) }
        var triState by rememberSaveable { mutableStateOf(false) }
        var shapeStyle by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        val state = CheckboxCycle[stateIndex]

        PlaygroundPreview {
            CheckboxStage(onImage = shapeStyle) {
                Checkbox(
                    state = state,
                    onClick = {
                        stateIndex = when {
                            triState -> (stateIndex + 1) % CheckboxCycle.size
                            state == ToggleableState.On -> 0
                            else -> 2
                        }
                    },
                    colors = if (shapeStyle) CheckboxDefaults.shapeCheckboxColors() else CheckboxDefaults.checkboxColors(),
                    enabled = enabled,
                )
            }
            ValueCaption(text = stringResource(Res.string.common_value_format, state.name))
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = listOf(
                        stringResource(Res.string.demo_checkbox_state_unchecked),
                        stringResource(Res.string.demo_checkbox_state_indeterminate),
                        stringResource(Res.string.demo_checkbox_state_checked),
                    ),
                    selectedIndex = stateIndex,
                    title = stringResource(Res.string.demo_checkbox_state),
                    onSelectedIndexChange = { stateIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = triState,
                    onCheckedChange = { triState = it },
                    title = stringResource(Res.string.demo_checkbox_tri_state),
                    summary = stringResource(Res.string.demo_checkbox_tri_state_summary),
                )
            }
            item {
                OverlayDropdownPreference(
                    items = listOf(
                        stringResource(Res.string.common_default),
                        stringResource(Res.string.demo_checkbox_style_shape),
                    ),
                    selectedIndex = if (shapeStyle) 1 else 0,
                    title = stringResource(Res.string.common_style),
                    onSelectedIndexChange = { shapeStyle = it == 1 },
                )
            }
            item {
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = stringResource(Res.string.common_enabled),
                )
            }
        }
    }
    variant(key = "states", title = Res.string.demo_checkbox_variant_states) {
        CheckboxStateRows()
    }
    variant(key = "shape", title = Res.string.demo_checkbox_variant_shape) {
        ShapeCheckboxes()
    }
}

/**
 * The preview stage: the plain page card, or the photo the shape checkbox is meant to sit on.
 */
@Composable
private fun CheckboxStage(
    onImage: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (onImage) {
            Image(
                painter = painterResource(Res.drawable.blur_test_bg),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )
        }
        content()
    }
}

@Composable
private fun ColumnScope.CheckboxStateRows() {
    var unchecked by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(true) }
    var triState by remember { mutableStateOf(ToggleableState.Indeterminate) }

    LabeledControlsRow(label = stringResource(Res.string.common_enabled)) {
        Checkbox(
            state = ToggleableState(unchecked),
            onClick = { unchecked = !unchecked },
        )
        Checkbox(
            state = ToggleableState(checked),
            onClick = { checked = !checked },
        )
        Checkbox(
            state = triState,
            onClick = {
                triState = when (triState) {
                    ToggleableState.Off -> ToggleableState.Indeterminate
                    ToggleableState.Indeterminate -> ToggleableState.On
                    ToggleableState.On -> ToggleableState.Off
                }
            },
        )
    }
    LabeledControlsRow(label = stringResource(Res.string.common_disabled)) {
        Checkbox(state = ToggleableState.Off, onClick = null, enabled = false)
        Checkbox(state = ToggleableState.On, onClick = null, enabled = false)
        Checkbox(state = ToggleableState.Indeterminate, onClick = null, enabled = false)
    }
}

/**
 * The shape checkbox (`couiShapeCheckBoxStyle`) over a photo, where its dark fill and light ring
 * keep it readable.
 */
@Composable
private fun ShapeCheckboxes() {
    var first by remember { mutableStateOf(false) }
    var second by remember { mutableStateOf(true) }

    CheckboxStage(onImage = true, modifier = Modifier.padding(16.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                state = ToggleableState(first),
                onClick = { first = !first },
                colors = CheckboxDefaults.shapeCheckboxColors(),
            )
            Checkbox(
                state = ToggleableState(second),
                onClick = { second = !second },
                colors = CheckboxDefaults.shapeCheckboxColors(),
            )
            Checkbox(
                state = ToggleableState.Off,
                onClick = null,
                colors = CheckboxDefaults.shapeCheckboxColors(),
                enabled = false,
            )
            Checkbox(
                state = ToggleableState.On,
                onClick = null,
                colors = CheckboxDefaults.shapeCheckboxColors(),
                enabled = false,
            )
        }
    }
}
