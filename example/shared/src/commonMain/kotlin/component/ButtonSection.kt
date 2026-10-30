// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.ButtonMaterial
import io.github.suqi8.coui.kmp.basic.ExpandableFloatingActionButton
import io.github.suqi8.coui.kmp.basic.FloatingActionButton
import io.github.suqi8.coui.kmp.basic.FloatingActionButtonItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Stepper
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.icon.extended.Edit
import io.github.suqi8.coui.kmp.icon.extended.Scan
import io.github.suqi8.coui.kmp.icon.extended.Share
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.painterResource
import utils.rememberMaterialBackdropProvider

fun LazyListScope.buttonSection() {
    item(key = "button") {
        var buttonText by remember { mutableStateOf("Cancel") }
        var submitButtonText by remember { mutableStateOf("Submit") }
        var clickCount by remember { mutableIntStateOf(0) }
        var submitClickCount by remember { mutableIntStateOf(0) }

        SmallTitle(text = "Button")
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                text = buttonText,
                onClick = {
                    clickCount++
                    buttonText = "Click: $clickCount"
                },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                text = submitButtonText,
                onClick = {
                    submitClickCount++
                    submitButtonText = "Click: $submitClickCount"
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                text = "Disabled",
                onClick = {},
                modifier = Modifier.weight(1f),
                enabled = false,
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                text = "Disabled",
                onClick = {},
                enabled = false,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                text = "Borderless",
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsBorderless(),
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                text = "Disabled",
                onClick = {},
                enabled = false,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsBorderless(),
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // COUI 17 material layer (edge lighting + touch spotlight); off by default on ColorOS 17.
            TextButton(
                text = "Material",
                onClick = {},
                modifier = Modifier.weight(1f),
                material = ButtonMaterial.Secondary,
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                text = "Material",
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                material = ButtonMaterial.Primary,
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(COUITheme.colorScheme.primary)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Small size tier, and the transparent type laid over a colored backdrop.
            TextButton(
                text = "Small",
                onClick = {},
                minWidth = ButtonDefaults.MinWidthSmall,
                minHeight = ButtonDefaults.MinHeightSmall,
                insideMargin = ButtonDefaults.InsideMarginSmall,
                textStyle = ButtonDefaults.textStyleSmall(),
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                text = "Transparent",
                onClick = {},
                minWidth = ButtonDefaults.MinWidthSmall,
                minHeight = ButtonDefaults.MinHeightSmall,
                insideMargin = ButtonDefaults.InsideMarginSmall,
                textStyle = ButtonDefaults.textStyleSmall(),
                colors = ButtonDefaults.textButtonColorsTransparent(),
                material = ButtonMaterial.Transparent,
            )
        }
        MaterialBackdropButtons()
        var stepperValue by remember { mutableIntStateOf(2) }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Stepper")
            Stepper(
                value = stepperValue,
                onValueChange = { stepperValue = it },
                minValue = 0,
                maxValue = 10,
            )
        }
        // The COUI default range (-999..9999): hold a button to repeat every 100ms.
        var wideStepperValue by remember { mutableIntStateOf(0) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Stepper (hold to repeat)")
            Stepper(
                value = wideStepperValue,
                onValueChange = { wideStepperValue = it },
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Stepper (disabled)")
            Stepper(
                value = 3,
                onValueChange = {},
                enabled = false,
            )
        }
        FloatingActionButtons()
        ExpandableFloatingActionButtonDemo()
    }
}

/** The COUI 17 speed dial: the main button expands into labelled child buttons. */
@Composable
private fun ExpandableFloatingActionButtonDemo() {
    var expanded by remember { mutableStateOf(false) }
    var lastAction by remember { mutableStateOf("None") }
    val items = remember {
        listOf(
            FloatingActionButtonItem(
                icon = { Icon(imageVector = COUIIcons.Edit, contentDescription = "Edit") },
                onClick = {
                    lastAction = "Edit"
                    expanded = false
                },
                label = "Edit",
            ),
            FloatingActionButtonItem(
                icon = { Icon(imageVector = COUIIcons.Share, contentDescription = "Share") },
                onClick = {
                    lastAction = "Share"
                    expanded = false
                },
                label = "Share",
            ),
            FloatingActionButtonItem(
                icon = { Icon(imageVector = COUIIcons.Scan, contentDescription = "Scan") },
                onClick = {
                    lastAction = "Scan"
                    expanded = false
                },
            ),
        )
    }
    // The button measures as the main button alone and opens upwards out of its bounds: the box
    // keeps room above it for the three items (232dp) so they stay inside the card.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(330.dp)
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
    ) {
        Text(
            text = "Last action: $lastAction",
            modifier = Modifier.align(Alignment.BottomStart),
        )
        ExpandableFloatingActionButton(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            items = items,
            modifier = Modifier.align(Alignment.BottomEnd),
        ) {
            Icon(imageVector = COUIIcons.Add, contentDescription = "Add")
        }
    }
}

/** The COUI 17 floating action button: material on and off, and disabled. */
@Composable
private fun FloatingActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 40.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        FloatingActionButton(onClick = {}) {
            Icon(imageVector = COUIIcons.Add, contentDescription = "Add")
        }
        FloatingActionButton(onClick = {}, material = false) {
            Icon(imageVector = COUIIcons.Add, contentDescription = "Add")
        }
        FloatingActionButton(onClick = {}, enabled = false) {
            Icon(imageVector = COUIIcons.Add, contentDescription = "Add")
        }
    }
}

/**
 * The COUI 17 button backdrop blur: the buttons sit on top of a recorded image (a sibling of the
 * recorded layer), so the material replaces their fill with the blurred, tinted image.
 */
@Composable
private fun MaterialBackdropButtons() {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberMaterialBackdropProvider(backdrop)
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
    ) {
        Image(
            painter = painterResource(Res.drawable.blur_test_bg),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .layerBackdrop(backdrop),
            contentScale = ContentScale.Crop,
        )
        CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row {
                    TextButton(
                        text = "Backdrop",
                        onClick = {},
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(12.dp))
                    TextButton(
                        text = "Backdrop",
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        text = "Transparent",
                        onClick = {},
                        minWidth = ButtonDefaults.MinWidthSmall,
                        minHeight = ButtonDefaults.MinHeightSmall,
                        insideMargin = ButtonDefaults.InsideMarginSmall,
                        textStyle = ButtonDefaults.textStyleSmall(),
                        colors = ButtonDefaults.textButtonColorsTransparent(),
                    )
                    Spacer(Modifier.width(12.dp))
                    TextButton(
                        text = "Disabled",
                        onClick = {},
                        enabled = false,
                        minWidth = ButtonDefaults.MinWidthSmall,
                        minHeight = ButtonDefaults.MinHeightSmall,
                        insideMargin = ButtonDefaults.InsideMarginSmall,
                        textStyle = ButtonDefaults.textStyleSmall(),
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                    Spacer(Modifier.weight(1f))
                    // The FAB takes the TYPE_FRAMEWORK_FAB backdrop blur in place of its fill.
                    FloatingActionButton(onClick = {}, shadowElevation = 0.dp) {
                        Icon(imageVector = COUIIcons.Add, contentDescription = "Add")
                    }
                }
            }
        }
    }
}
