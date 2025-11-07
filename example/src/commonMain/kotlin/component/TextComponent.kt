// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.BasicComponent
import com.suqi8.coui.kmp.basic.ButtonDefaults
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.CardDefaults
import com.suqi8.coui.kmp.basic.Checkbox
import com.suqi8.coui.kmp.basic.ColorPalette
import com.suqi8.coui.kmp.basic.CouiListItemPosition
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.IconButton
import com.suqi8.coui.kmp.basic.Slider
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.basic.Switch
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.basic.TextButton
import com.suqi8.coui.kmp.basic.TextField
import com.suqi8.coui.kmp.extra.CheckboxLocation
import com.suqi8.coui.kmp.extra.SpinnerEntry
import com.suqi8.coui.kmp.extra.SuperArrow
import com.suqi8.coui.kmp.extra.SuperBottomSheet
import com.suqi8.coui.kmp.extra.SuperCheckbox
import com.suqi8.coui.kmp.extra.SuperDialog
import com.suqi8.coui.kmp.extra.SuperDropdown
import com.suqi8.coui.kmp.extra.SuperSpinner
import com.suqi8.coui.kmp.extra.SuperSwitch
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.useful.Cancel
import com.suqi8.coui.kmp.icon.icons.useful.Confirm
import com.suqi8.coui.kmp.icon.icons.useful.Personal
import com.suqi8.coui.kmp.theme.COUITheme

@Composable
fun TextComponent(
    showDialog: MutableState<Boolean>,
    dialogTextFieldValue: MutableState<String>,
    showBottomSheet: MutableState<Boolean>,
    bottomSheetDropdownSelectedOption: MutableState<Int>,
    bottomSheetSuperSwitchState: MutableState<Boolean>,
    checkbox: MutableState<Boolean>,
    checkboxTrue: MutableState<Boolean>,
    switch: MutableState<Boolean>,
    switchTrue: MutableState<Boolean>,
    dropdownOptionSelected: MutableState<Int>,
    spinnerOptionSelected: MutableState<Int>,
    spinnerOptionSelectedDialog: MutableState<Int>,
    miuixSuperCheckbox: MutableState<String>,
    miuixSuperCheckboxState: MutableState<Boolean>,
    miuixSuperRightCheckbox: MutableState<String>,
    miuixSuperRightCheckboxState: MutableState<Boolean>,
    miuixSuperSwitch: MutableState<String>,
    miuixSuperSwitchState: MutableState<Boolean>,
    miuixSuperSwitchAnimState: MutableState<Boolean>,
) {
    val dropdownOptions = remember { listOf("Option 1", "Option 2", "Option 3", "Option 4") }
    val spinnerOptions = remember {
        listOf(
            SpinnerEntry(
                icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFFFF5B29)) },
                "Option 1",
                "Red"
            ),
            SpinnerEntry(
                icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFF36D167)) },
                "Option 2",
                "Green"
            ),
            SpinnerEntry(
                icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFF3482FF)) },
                "Option 3",
                "Blue"
            ),
            SpinnerEntry(
                icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFFFFB21D)) },
                "Option 4",
                "Yellow"
            ),
        )
    }

    SmallTitle(text = "Basic Component")
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
    ) {
        BasicComponent(
            title = "Title",
            summary = "Summary",
            leftAction = {
                Text(
                    text = "Left",
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            rightActions = {
                Text(text = "Right1")
                Spacer(Modifier.width(8.dp))
                Text(text = "Right2")
            },
            onClick = {},
            enabled = true
        )
        BasicComponent(
            title = "Title",
            summary = "Summary",
            leftAction = {
                Text(
                    text = "Left",
                    modifier = Modifier.padding(end = 8.dp),
                    color = COUITheme.colorScheme.disabledOnSecondaryVariant
                )
            },
            rightActions = {
                Text(
                    text = "Right1",
                    color = COUITheme.colorScheme.disabledOnSecondaryVariant
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Right2",
                    color = COUITheme.colorScheme.disabledOnSecondaryVariant
                )
            },
            enabled = false
        )
    }

    SmallTitle(text = "Arrow & Dialog & BottomSheet")
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
    ) {
        SuperArrow(
            leftAction = {
                Box(
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.Useful.Personal,
                        contentDescription = "Personal",
                        tint = COUITheme.colorScheme.onBackground
                    )
                }
            },
            title = "Arrow",
            summary = "Click to show a Dialog",
            onClick = {
                showDialog.value = true
            },
            holdDownState = showDialog.value
        )

        SuperArrow(
            title = "Arrow",
            summary = "Click to show a BottomSheet",
            onClick = {
                showBottomSheet.value = true
            },
            holdDownState = showBottomSheet.value
        )

        SuperArrow(
            title = "Disabled Arrow",
            onClick = {},
            enabled = false
        )
    }

    SmallTitle(text = "Checkbox")
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(
                checked = checkbox.value,
                onCheckedChange = { checkbox.value = it }
            )
            Checkbox(
                checked = checkboxTrue.value,
                onCheckedChange = { checkboxTrue.value = it },
                modifier = Modifier.padding(start = 8.dp)
            )
            Checkbox(
                checked = false,
                onCheckedChange = { },
                modifier = Modifier.padding(start = 8.dp),
                enabled = false
            )
            Checkbox(
                checked = true,
                onCheckedChange = { },
                modifier = Modifier.padding(start = 8.dp),
                enabled = false
            )
        }

        SuperCheckbox(
            checkboxLocation = CheckboxLocation.Right,
            title = "Checkbox",
            checked = miuixSuperRightCheckboxState.value,
            rightActions = {
                Text(
                    modifier = Modifier.padding(end = 6.dp),
                    text = miuixSuperRightCheckbox.value,
                    color = COUITheme.colorScheme.onSurfaceVariantActions
                )
            },
            onCheckedChange = {
                miuixSuperRightCheckboxState.value = it
                miuixSuperRightCheckbox.value = "$it"
            },
        )

        SuperCheckbox(
            title = "Checkbox",
            summary = miuixSuperCheckbox.value,
            checked = miuixSuperCheckboxState.value,
            onCheckedChange = {
                miuixSuperCheckboxState.value = it
                miuixSuperCheckbox.value = "State: $it"
            },
        )

        SuperCheckbox(
            title = "Disabled Checkbox",
            checked = true,
            enabled = false,
            onCheckedChange = {},
        )
    }

    SmallTitle(text = "Switch")
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Switch(
                checked = switch.value,
                onCheckedChange = { switch.value = it }
            )
            Switch(
                checked = switchTrue.value,
                onCheckedChange = { switchTrue.value = it },
                modifier = Modifier.padding(start = 6.dp)
            )
            Switch(
                checked = false,
                onCheckedChange = { },
                modifier = Modifier.padding(start = 6.dp),
                enabled = false
            )
            Switch(
                checked = true,
                onCheckedChange = { },
                modifier = Modifier.padding(start = 6.dp),
                enabled = false
            )
        }

        SuperSwitch(
            title = "Switch",
            summary = "Click to expand a Switch",
            checked = miuixSuperSwitchAnimState.value,
            onCheckedChange = {
                miuixSuperSwitchAnimState.value = it
            },
        )

        AnimatedVisibility(
            visible = miuixSuperSwitchAnimState.value,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SuperSwitch(
                title = "Switch",
                checked = miuixSuperSwitchState.value,
                rightActions = {
                    Text(
                        modifier = Modifier.padding(end = 6.dp),
                        text = miuixSuperSwitch.value,
                        color = COUITheme.colorScheme.onSurfaceVariantActions
                    )
                },
                onCheckedChange = {
                    miuixSuperSwitchState.value = it
                    miuixSuperSwitch.value = "$it"
                },
            )
        }

        SuperSwitch(
            title = "Disabled Switch",
            checked = true,
            enabled = false,
            position = CouiListItemPosition.Bottom,
            onCheckedChange = {},
        )
    }

    SmallTitle(text = "Dropdown")
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
    ) {
        SuperDropdown(
            title = "Dropdown",
            items = dropdownOptions,
            selectedIndex = dropdownOptionSelected.value,
            onSelectedIndexChange = { newOption -> dropdownOptionSelected.value = newOption },
        )

        SuperDropdown(
            title = "Disabled Dropdown",
            items = listOf("Option 3"),
            selectedIndex = 0,
            onSelectedIndexChange = {},
            enabled = false
        )
    }

    SmallTitle(text = "Spinner")
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
    ) {
        SuperSpinner(
            title = "Spinner",
            items = spinnerOptions,
            selectedIndex = spinnerOptionSelected.value,
            onSelectedIndexChange = { newOption -> spinnerOptionSelected.value = newOption },
        )
        SuperSpinner(
            title = "Spinner",
            summary = "Spinner as Dialog",
            dialogButtonString = "Cancel",
            items = spinnerOptions,
            selectedIndex = spinnerOptionSelectedDialog.value,
            onSelectedIndexChange = { newOption -> spinnerOptionSelectedDialog.value = newOption },
        )
        SuperSpinner(
            title = "Disabled Spinner",
            items = listOf(SpinnerEntry(icon = null, title = "Option 4")),
            selectedIndex = 0,
            onSelectedIndexChange = {},
            enabled = false
        )
    }
    Dialog(showDialog, dialogTextFieldValue)
    BottomSheet(showBottomSheet, bottomSheetDropdownSelectedOption, bottomSheetSuperSwitchState)
}

@Composable
fun Dialog(
    showDialog: MutableState<Boolean>,
    dialogTextFieldValue: MutableState<String>
) {
    SuperDialog(
        title = "Dialog",
        summary = "Summary",
        show = showDialog,
        onDismissRequest = {
            showDialog.value = false
        }
    ) {
        TextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = dialogTextFieldValue.value,
            maxLines = 1,
            onValueChange = { dialogTextFieldValue.value = it }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                text = "Cancel",
                onClick = {
                    showDialog.value = false
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                text = "Confirm",
                onClick = {
                    showDialog.value = false
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
    }
}

@Composable
fun BottomSheet(
    showBottomSheet: MutableState<Boolean>,
    bottomSheetDropdownSelectedOption: MutableState<Int>,
    bottomSheetSuperSwitchState: MutableState<Boolean>
) {
    val dropdownOptions = listOf("Option 1", "Option 2")
    SuperBottomSheet(
        title = "BottomSheet",
        show = showBottomSheet,
        onDismissRequest = {
            showBottomSheet.value = false
        },
        leftAction = {
            IconButton(
                onClick = { showBottomSheet.value = false },
            ) {
                Icon(
                    imageVector = MiuixIcons.Useful.Cancel,
                    contentDescription = "Cancel",
                    tint = COUITheme.colorScheme.onBackground
                )
            }
        },
        rightAction = {
            IconButton(
                onClick = { showBottomSheet.value = false },
            ) {
                Icon(
                    imageVector = MiuixIcons.Useful.Confirm,
                    contentDescription = "Confirm",
                    tint = COUITheme.colorScheme.onBackground
                )
            }
        }
    ) {
        LazyColumn {
            item {
                var sliderValue by remember { mutableStateOf(0.5f) }
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Card(
                    modifier = Modifier.padding(bottom = 12.dp),
                    colors = CardDefaults.defaultColors(
                        color = COUITheme.colorScheme.secondaryContainer,
                    )
                ) {
                    SuperDropdown(
                        title = "Dropdown",
                        items = dropdownOptions,
                        selectedIndex = bottomSheetDropdownSelectedOption.value,
                        onSelectedIndexChange = { newOption -> bottomSheetDropdownSelectedOption.value = newOption }
                    )
                    SuperSwitch(
                        title = "Switch",
                        checked = bottomSheetSuperSwitchState.value,
                        onCheckedChange = {
                            bottomSheetSuperSwitchState.value = it
                        }
                    )
                }
                AnimatedVisibility(
                    visible = bottomSheetSuperSwitchState.value,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    val miuixColor = COUITheme.colorScheme.primary
                    var selectedColor by remember { mutableStateOf(miuixColor) }
                    ColorPalette(
                        modifier = Modifier.padding(bottom = 12.dp),
                        initialColor = selectedColor,
                        onColorChanged = { selectedColor = it },
                        showPreview = false
                    )
                }
                Spacer(
                    Modifier.padding(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + WindowInsets.captionBar.asPaddingValues().calculateBottomPadding()
                    )
                )
            }
        }
    }
}

class RoundedRectanglePainter(
    private val cornerRadius: Dp = 6.dp
) : Painter() {
    override val intrinsicSize = Size.Unspecified

    override fun DrawScope.onDraw() {
        drawRoundRect(
            color = Color.White,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
        )
    }
}
