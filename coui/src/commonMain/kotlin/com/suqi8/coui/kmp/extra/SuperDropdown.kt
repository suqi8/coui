// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0
//
// Final version with updated APIs to resolve deprecation warnings.

package com.suqi8.coui.kmp.extra

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.suqi8.coui.kmp.basic.CouiListItemPosition
import com.suqi8.coui.kmp.basic.ListPopupColumn
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.basic.Check
import com.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Immutable
data class DropdownOption(
    val text: String,
    val value: String
)

@Composable
fun SuperDropdown(
    items: List<DropdownOption>,
    selectedValue: String,
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    position: CouiListItemPosition = CouiListItemPosition.Middle,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    maxHeight: Dp? = null,
    onClick: (() -> Unit)? = null,
    onValueChange: ((String) -> Unit)?,
) {
    val selectedOptionText = items.find { it.value == selectedValue }?.text ?: ""
    val effectiveSummary = if (showValue) selectedOptionText else summary

    val isDropdownExpanded = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    val handleClick = {
        onClick?.invoke()
        if (enabled && items.isNotEmpty()) {
            isDropdownExpanded.value = !isDropdownExpanded.value
            if (isDropdownExpanded.value) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
            }
        }
    }

    CouiDropdownPreferenceImpl(
        modifier = modifier,
        title = title,
        summary = effectiveSummary,
        enabled = enabled,
        isDropdownExpanded = isDropdownExpanded,
        onClick = handleClick
    ) {
        if (items.isNotEmpty()) {
            CouiDropdownMenuWithGestures(
                expanded = isDropdownExpanded.value,
                onDismissRequest = { isDropdownExpanded.value = false },
                options = items,
                selectedValue = selectedValue,
                onOptionSelected = { newValue ->
                    onValueChange?.invoke(newValue)
                    isDropdownExpanded.value = false
                },
                colors = dropdownColors,
                hapticFeedback = hapticFeedback
            )
        }
    }
}

@Composable
private fun CouiDropdownPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    summary: String?,
    enabled: Boolean,
    isDropdownExpanded: State<Boolean>,
    onClick: () -> Unit,
    popupContent: @Composable () -> Unit
) {
    Box {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 32.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val titleColor = if (enabled) COUITheme.colorScheme.onSurface else COUITheme.colorScheme.onSurface.copy(alpha = 0.4f)
                val summaryColor = if (enabled) COUITheme.colorScheme.onSurfaceVariantActions else COUITheme.colorScheme.onSurfaceVariantActions.copy(alpha = 0.4f)
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!summary.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = summary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = summaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        popupContent()
    }
}

@Composable
private fun CouiDropdownMenuWithGestures(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<DropdownOption>,
    selectedValue: String,
    onOptionSelected: (String) -> Unit,
    colors: DropdownColors,
    hapticFeedback: HapticFeedback
) {
    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded

    if (expandedStates.currentState || expandedStates.targetState) {
        val density = LocalDensity.current

        Popup(
            onDismissRequest = onDismissRequest,
            alignment = Alignment.TopEnd,
            offset = IntOffset(
                x = with(density) { -16.dp.roundToPx() },
                y = with(density) { 8.dp.roundToPx() }
            ),
            properties = PopupProperties(focusable = true)
        ) {
            val transition = updateTransition(expandedStates, "CouiDropdownMenu")
            val scale by transition.animateFloat(
                transitionSpec = { tween(durationMillis = 150) },
                label = "scale"
            ) { if (it) 1f else 0.8f }
            val alpha by transition.animateFloat(
                transitionSpec = { tween(durationMillis = 150) },
                label = "alpha"
            ) { if (it) 1f else 0f }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                        transformOrigin = TransformOrigin(1f, 0f)
                    }
                    .widthIn(min = 178.dp, max = 232.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
            ) {
                val currentlyPressedIndex = remember { mutableStateOf<Int?>(null) }
                var itemHeightPx by remember { mutableStateOf(0f) }

                Column(
                    modifier = Modifier
                        .onSizeChanged {
                            if (options.isNotEmpty()) {
                                itemHeightPx = it.height.toFloat() / options.size
                            }
                        }
                        .couiDragGesture(
                            itemHeightPx = itemHeightPx,
                            itemCount = options.size,
                            onPress = { index -> currentlyPressedIndex.value = index },
                            onDrag = { index -> currentlyPressedIndex.value = index },
                            onRelease = { index ->
                                if (index != null) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                    onOptionSelected(options[index].value)
                                }
                                currentlyPressedIndex.value = null
                            },
                            onCancel = { currentlyPressedIndex.value = null }
                        )
                ) {
                    options.forEachIndexed { index, option ->
                        key(option.value) {
                            val cornerRadius = 12.dp
                            val shape = when {
                                options.size == 1 -> RoundedCornerShape(cornerRadius)
                                index == 0 -> RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
                                index == options.size - 1 -> RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
                                else -> RoundedCornerShape(0.dp)
                            }

                            val interactionSource = remember { MutableInteractionSource() }
                            val isVisuallySelected = (selectedValue == option.value || currentlyPressedIndex.value == index)
                            val isPressed by interactionSource.collectIsPressedAsState()

                            LaunchedEffect(currentlyPressedIndex.value) {
                                val press = PressInteraction.Press(Offset.Zero)
                                if (currentlyPressedIndex.value == index) {
                                    interactionSource.tryEmit(press)
                                } else {
                                    interactionSource.tryEmit(PressInteraction.Release(press))
                                }
                            }

                            val backgroundColor = if (isPressed) {
                                val pressOverlay = if (COUITheme.colorScheme.isDark) Color(0x0DFFFFFF) else Color(0x08000000)
                                pressOverlay.compositeOver(colors.containerColor)
                            } else {
                                colors.containerColor
                            }

                            Box(
                                modifier = Modifier
                                    .clip(shape)
                                    .background(backgroundColor)
                                    .indication(interactionSource, NoOpIndication)
                            ) {
                                DropdownImpl(
                                    option = option,
                                    isSelected = isVisuallySelected,
                                    dropdownColors = colors
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.couiDragGesture(
    itemHeightPx: Float,
    itemCount: Int,
    onPress: (Int) -> Unit,
    onDrag: (Int) -> Unit,
    onRelease: (Int?) -> Unit,
    onCancel: () -> Unit
): Modifier = pointerInput(itemHeightPx, itemCount) {
    if (itemHeightPx <= 0f) return@pointerInput

    coroutineScope {
        awaitPointerEventScope {
            while (true) {
                try {
                    val down = awaitPointerEvent().changes.first()
                    down.consume()
                    val startIndex = (down.position.y / itemHeightPx).toInt().coerceIn(0, itemCount - 1)
                    onPress(startIndex)

                    var isReleased = false
                    val dragJob = launch {
                        detectDragGestures(
                            onDragStart = { },
                            onDrag = { change, _ ->
                                change.consume()
                                val currentIndex = (change.position.y / itemHeightPx).toInt().coerceIn(0, itemCount - 1)
                                onDrag(currentIndex)
                            },
                            onDragEnd = {
                                isReleased = true
                                val finalIndex = (down.position.y / itemHeightPx).toInt().coerceIn(0, itemCount - 1)
                                onRelease(finalIndex)
                            },
                            onDragCancel = {
                                isReleased = true
                                onCancel()
                            }
                        )
                    }

                    val up = awaitPointerEvent().changes.first()
                    if (!isReleased) {
                        dragJob.cancel()
                        if (up.pressed) {
                            onCancel()
                        } else {
                            val finalIndex = (up.position.y / itemHeightPx).toInt().coerceIn(0, itemCount - 1)
                            onRelease(finalIndex)
                        }
                    }
                } catch (e: Exception) {
                    onCancel()
                }
            }
        }
    }
}

@Composable
fun DropdownImpl(
    option: DropdownOption,
    isSelected: Boolean,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
) {
    val textColor = if (isSelected) dropdownColors.selectedContentColor else dropdownColors.contentColor
    val checkmarkColor = dropdownColors.selectedContentColor

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp
            )
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = option.text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = textColor,
        )
        if (isSelected) {
            Image(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(24.dp),
                imageVector = MiuixIcons.Basic.Check,
                colorFilter = ColorFilter.tint(checkmarkColor),
                contentDescription = "Selected",
            )
        }
    }
}

@Immutable
class DropdownColors(
    val contentColor: Color,
    val containerColor: Color,
    val selectedContentColor: Color,
)

object DropdownDefaults {
    @Composable
    fun dropdownColors(
        contentColor: Color = if (COUITheme.colorScheme.isDark) Color(0xFFFFFFFF) else Color(0xFF000000),
        containerColor: Color = if (COUITheme.colorScheme.isDark) Color(0xFF242424) else Color(0xFFFFFFFF),
        selectedContentColor: Color = Color(0xFF39BF56)
    ): DropdownColors {
        return DropdownColors(
            contentColor = contentColor,
            containerColor = containerColor,
            selectedContentColor = selectedContentColor
        )
    }
}

/**
 * An Indication that draws nothing, implemented using the modern Modifier.Node API
 * to avoid deprecation warnings.
 */
private object NoOpIndication : Indication {
    // This is the new factory method that replaces rememberUpdatedInstance
    fun create(interactionSource: InteractionSource): Modifier.Node {
        return NoOpIndicationNode
    }

    // The node itself can be a singleton object since it holds no state
    private object NoOpIndicationNode : Modifier.Node(), DrawModifierNode {
        override fun ContentDrawScope.draw() {
            // Draw the component's content without any visual effects.
            drawContent()
        }
    }
}
