// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mocharealm.gaze.capsule.ContinuousRoundedRectangle
import com.suqi8.coui.kmp.theme.COUITheme

/**
 * A COUI-style single-select choice chip row.
 */
@Composable
fun ChoiceChipRow(
    items: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChoiceChipRowColors = ChoiceChipRowDefaults.choiceChipRowColors(),
    enabled: Boolean = true,
) {
    if (items.isEmpty()) return

    val clampedSelectedIndex = selectedIndex.coerceIn(0, items.lastIndex)
    val state = rememberLazyListState()

    LaunchedEffect(clampedSelectedIndex) {
        state.animateScrollToItem(index = clampedSelectedIndex)
    }

    LazyRow(
        state = state,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = ChoiceChipRowDefaults.HorizontalContentPadding,
            top = ChoiceChipRowDefaults.TopContentPadding,
            end = ChoiceChipRowDefaults.HorizontalContentPadding,
            bottom = ChoiceChipRowDefaults.BottomContentPadding,
        ),
        horizontalArrangement = Arrangement.spacedBy(ChoiceChipRowDefaults.ItemSpacing),
    ) {
        itemsIndexed(items) { index, item ->
            ChoiceChipRowItem(
                text = item,
                isSelected = index == clampedSelectedIndex,
                enabled = enabled,
                onClick = {
                    if (index != clampedSelectedIndex) {
                        onSelectionChange(index)
                    }
                },
                colors = colors,
            )
        }
    }
}

@Composable
private fun ChoiceChipRowItem(
    text: String,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    colors: ChoiceChipRowColors,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val containerColor by animateColorAsState(
        targetValue = colors.containerColor(
            selected = isSelected,
            enabled = enabled,
            pressed = pressed,
        ),
        label = "choice-chip-container"
    )
    val contentColor by animateColorAsState(
        targetValue = colors.contentColor(
            selected = isSelected,
            enabled = enabled,
            pressed = pressed,
        ),
        label = "choice-chip-content"
    )
    val borderColor by animateColorAsState(
        targetValue = colors.borderColor(
            selected = isSelected,
            enabled = enabled,
            pressed = pressed,
        ),
        label = "choice-chip-border"
    )
    val shape = remember { ContinuousRoundedRectangle(ChoiceChipRowDefaults.CornerRadius) }

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .defaultMinSize(minHeight = ChoiceChipRowDefaults.Height)
            .semantics {
                selected = isSelected
                role = Role.RadioButton
            },
        interactionSource = interactionSource,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (isSelected && enabled) ChoiceChipRowDefaults.SelectedShadowElevation else 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(
                start = ChoiceChipRowDefaults.HorizontalItemPadding + ChoiceChipRowDefaults.HorizontalTextPadding,
                top = ChoiceChipRowDefaults.VerticalItemPadding,
                end = ChoiceChipRowDefaults.HorizontalItemPadding + ChoiceChipRowDefaults.HorizontalTextPadding,
                bottom = ChoiceChipRowDefaults.VerticalItemPadding,
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = COUITheme.textStyles.body,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            )
        }
    }
}

object ChoiceChipRowDefaults {
    val Height = 32.dp
    val CornerRadius = 16.dp
    val HorizontalItemPadding = 11.dp
    val VerticalItemPadding = 0.dp
    val HorizontalTextPadding = 5.dp
    val HorizontalContentPadding = 16.dp
    val TopContentPadding = 10.dp
    val BottomContentPadding = 18.dp
    val ItemSpacing = 8.dp
    val SelectedShadowElevation = 0.dp

    @Composable
    fun choiceChipRowColors(
        containerColor: androidx.compose.ui.graphics.Color = if (COUITheme.colorScheme.isDark) {
            androidx.compose.ui.graphics.Color.White.copy(alpha = 0.10f)
        } else {
            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.04f)
        },
        selectedContainerColor: androidx.compose.ui.graphics.Color = COUITheme.colorScheme.primary,
        pressedContainerColor: androidx.compose.ui.graphics.Color = if (COUITheme.colorScheme.isDark) {
            androidx.compose.ui.graphics.Color.White.copy(alpha = 0.14f)
        } else {
            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.06f)
        },
        pressedSelectedContainerColor: androidx.compose.ui.graphics.Color = COUITheme.colorScheme.primary,
        contentColor: androidx.compose.ui.graphics.Color = COUITheme.colorScheme.onSurface.copy(alpha = 0.9f),
        selectedContentColor: androidx.compose.ui.graphics.Color = COUITheme.colorScheme.onPrimary.copy(alpha = 0.85f),
        pressedContentColor: androidx.compose.ui.graphics.Color = COUITheme.colorScheme.onSurface.copy(alpha = 0.9f),
        pressedSelectedContentColor: androidx.compose.ui.graphics.Color = COUITheme.colorScheme.onPrimary.copy(alpha = 0.85f),
        borderColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent,
        selectedBorderColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent,
        disabledContainerColor: androidx.compose.ui.graphics.Color = if (COUITheme.colorScheme.isDark) {
            androidx.compose.ui.graphics.Color.White.copy(alpha = 0.10f)
        } else {
            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.04f)
        },
        disabledSelectedContainerColor: androidx.compose.ui.graphics.Color = COUITheme.colorScheme.disabledPrimaryButton,
        disabledContentColor: androidx.compose.ui.graphics.Color = if (COUITheme.colorScheme.isDark) {
            androidx.compose.ui.graphics.Color.White.copy(alpha = 0.16f)
        } else {
            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.26f)
        },
        disabledSelectedContentColor: androidx.compose.ui.graphics.Color = if (COUITheme.colorScheme.isDark) {
            androidx.compose.ui.graphics.Color.White.copy(alpha = 0.16f)
        } else {
            androidx.compose.ui.graphics.Color.White.copy(alpha = 0.54f)
        },
        disabledBorderColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent,
    ): ChoiceChipRowColors = ChoiceChipRowColors(
        containerColor = containerColor,
        selectedContainerColor = selectedContainerColor,
        pressedContainerColor = pressedContainerColor,
        pressedSelectedContainerColor = pressedSelectedContainerColor,
        contentColor = contentColor,
        selectedContentColor = selectedContentColor,
        pressedContentColor = pressedContentColor,
        pressedSelectedContentColor = pressedSelectedContentColor,
        borderColor = borderColor,
        selectedBorderColor = selectedBorderColor,
        disabledContainerColor = disabledContainerColor,
        disabledSelectedContainerColor = disabledSelectedContainerColor,
        disabledContentColor = disabledContentColor,
        disabledSelectedContentColor = disabledSelectedContentColor,
        disabledBorderColor = disabledBorderColor,
    )
}

@Immutable
class ChoiceChipRowColors(
    private val containerColor: androidx.compose.ui.graphics.Color,
    private val selectedContainerColor: androidx.compose.ui.graphics.Color,
    private val pressedContainerColor: androidx.compose.ui.graphics.Color,
    private val pressedSelectedContainerColor: androidx.compose.ui.graphics.Color,
    private val contentColor: androidx.compose.ui.graphics.Color,
    private val selectedContentColor: androidx.compose.ui.graphics.Color,
    private val pressedContentColor: androidx.compose.ui.graphics.Color,
    private val pressedSelectedContentColor: androidx.compose.ui.graphics.Color,
    private val borderColor: androidx.compose.ui.graphics.Color,
    private val selectedBorderColor: androidx.compose.ui.graphics.Color,
    private val disabledContainerColor: androidx.compose.ui.graphics.Color,
    private val disabledSelectedContainerColor: androidx.compose.ui.graphics.Color,
    private val disabledContentColor: androidx.compose.ui.graphics.Color,
    private val disabledSelectedContentColor: androidx.compose.ui.graphics.Color,
    private val disabledBorderColor: androidx.compose.ui.graphics.Color,
) {
    @Stable
    internal fun containerColor(selected: Boolean, enabled: Boolean, pressed: Boolean): androidx.compose.ui.graphics.Color = when {
        enabled && pressed && selected -> pressedSelectedContainerColor
        enabled && pressed -> pressedContainerColor
        enabled && selected -> selectedContainerColor
        enabled -> containerColor
        selected -> disabledSelectedContainerColor
        else -> disabledContainerColor
    }

    @Stable
    internal fun contentColor(selected: Boolean, enabled: Boolean, pressed: Boolean): androidx.compose.ui.graphics.Color = when {
        enabled && pressed && selected -> pressedSelectedContentColor
        enabled && pressed -> pressedContentColor
        enabled && selected -> selectedContentColor
        enabled -> contentColor
        selected -> disabledSelectedContentColor
        else -> disabledContentColor
    }

    @Stable
    internal fun borderColor(selected: Boolean, enabled: Boolean, pressed: Boolean): androidx.compose.ui.graphics.Color = when {
        !enabled -> disabledBorderColor
        selected || pressed -> selectedBorderColor
        else -> borderColor
    }
}
