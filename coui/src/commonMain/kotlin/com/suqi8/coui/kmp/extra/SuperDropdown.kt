package com.suqi8.coui.kmp.extra

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.BasicComponent
import com.suqi8.coui.kmp.basic.BasicComponentColors
import com.suqi8.coui.kmp.basic.BasicComponentDefaults
import com.suqi8.coui.kmp.basic.CouiListItemPosition
import com.suqi8.coui.kmp.basic.ListPopup
import com.suqi8.coui.kmp.basic.ListPopupColumn
import com.suqi8.coui.kmp.basic.PopupPositionProvider
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.basic.ArrowUpDownIntegrated
import com.suqi8.coui.kmp.icon.icons.basic.Check
import com.suqi8.coui.kmp.theme.COUITheme

enum class DropDownMode {
    Normal,
    AlwaysOnRight
}

@Composable
fun SuperDropdown(
    items: List<String>,
    selectedIndex: Int,
    title: String,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    mode: DropDownMode = DropDownMode.Normal,
    modifier: Modifier = Modifier,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    maxHeight: Dp? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    position: CouiListItemPosition = CouiListItemPosition.Middle,
    onClick: (() -> Unit)? = null,
    onSelectedIndexChange: ((Int) -> Unit)?,
    onDismissRequest: (() -> Unit)? = null,
    leftAction: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    val itemsNotEmpty = items.isNotEmpty()
    val actualEnabled = enabled && itemsNotEmpty

    val actionColor = if (actualEnabled) {
        COUITheme.colorScheme.onSurfaceVariantActions
    } else {
        COUITheme.colorScheme.disabledOnSecondaryVariant
    }

    var alignLeft by remember { mutableStateOf(true) }

    val componentModifier = modifier.pointerInput(actualEnabled) {
        if (!actualEnabled) return@pointerInput
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (event.type != PointerEventType.Move) {
                    val eventChange = event.changes.first()
                    if (eventChange.pressed) {
                        alignLeft = eventChange.position.x < (size.width / 2)
                    }
                }
            }
        }
    }

    val handleClick: () -> Unit = {
        if (actualEnabled) {
            onClick?.invoke()
            isDropdownExpanded.value = !isDropdownExpanded.value
            if (isDropdownExpanded.value) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
            }
        }
    }

    val popupContent: @Composable () -> Unit = {
        SuperDropdownPopup(
            items = items,
            selectedIndex = selectedIndex,
            isDropdownExpanded = isDropdownExpanded,
            mode = mode,
            alignLeft = alignLeft,
            maxHeight = maxHeight,
            dropdownColors = dropdownColors,
            hapticFeedback = hapticFeedback,
            onSelectedIndexChange = onSelectedIndexChange,
            onDismissRequest = onDismissRequest
        )
    }

    BasicComponent(
        modifier = componentModifier,
        interactionSource = interactionSource,
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        position = position,
        leftAction = leftAction ?: if (itemsNotEmpty) {
            {
                popupContent()
            }
        } else null,
        rightActions = {
            SuperDropdownRightActions(
                showValue = showValue,
                itemsNotEmpty = itemsNotEmpty,
                items = items,
                selectedIndex = selectedIndex,
                actionColor = actionColor
            )
            if (leftAction != null && itemsNotEmpty) {
                popupContent()
            }
        },
        onClick = handleClick,
        holdDownState = isDropdownExpanded.value,
        enabled = actualEnabled
    )
}

@Composable
private fun SuperDropdownPopup(
    items: List<String>,
    selectedIndex: Int,
    isDropdownExpanded: MutableState<Boolean>,
    mode: DropDownMode,
    alignLeft: Boolean,
    maxHeight: Dp?,
    dropdownColors: DropdownColors,
    hapticFeedback: HapticFeedback,
    onSelectedIndexChange: ((Int) -> Unit)?,
    onDismissRequest: (() -> Unit)? = null
) {
    var hoveredIndex by remember { mutableStateOf(-1) }
    var pressedIndex by remember { mutableStateOf(-1) }

    ListPopup(
        show = isDropdownExpanded,
        alignment = if (mode == DropDownMode.AlwaysOnRight || !alignLeft) {
            PopupPositionProvider.Align.Right
        } else {
            PopupPositionProvider.Align.Left
        },
        onDismissRequest = {
            isDropdownExpanded.value = false
            hoveredIndex = -1
            pressedIndex = -1
            onDismissRequest?.invoke()
        },
        maxHeight = maxHeight
    ) {
        ListPopupColumn(
            onPressedIndexChange = { pressedIndex = it },
            onDragHover = {
                hoveredIndex = it
            },
            onTap = { index ->
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                onSelectedIndexChange?.invoke(index)
                isDropdownExpanded.value = false
                pressedIndex = -1
                hoveredIndex = -1
            },
            onDragEnd = {
                if (hoveredIndex != -1) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    onSelectedIndexChange?.invoke(hoveredIndex)
                    isDropdownExpanded.value = false
                }
                pressedIndex = -1
                hoveredIndex = -1
            },
            onDragCancel = {
                pressedIndex = -1
                hoveredIndex = -1
            }
        ) {
            items.forEachIndexed { index, string ->
                key(index) {
                    val dividerColor = COUITheme.colorScheme.dividerLine

                    Box(
                        modifier = Modifier.drawWithContent {
                            drawContent()
                            if (index < items.size - 1) {
                                val thicknessPx = 0.5.dp.toPx()
                                drawLine(
                                    color = dividerColor,
                                    start = Offset(16.dp.toPx(), size.height - thicknessPx / 2),
                                    end = Offset(size.width - 16.dp.toPx(), size.height - thicknessPx / 2),
                                    strokeWidth = thicknessPx
                                )
                            }
                        }
                    ) {
                        val showCheckmark = (selectedIndex == index)
                        val showDarkBackground = (pressedIndex == index) || (hoveredIndex == index)

                        DropdownImpl(
                            text = string,
                            optionSize = items.size,
                            isSelected = showCheckmark,
                            isPressed = showDarkBackground,
                            dropdownColors = dropdownColors,
                            index = index
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.SuperDropdownRightActions(
    showValue: Boolean,
    itemsNotEmpty: Boolean,
    items: List<String>,
    selectedIndex: Int,
    actionColor: Color
) {
    if (showValue && itemsNotEmpty) {
        Text(
            modifier = Modifier.widthIn(max = 130.dp),
            text = items[selectedIndex],
            fontSize = COUITheme.textStyles.body2.fontSize,
            color = actionColor,
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }

    Image(
        modifier = Modifier
            .padding(start = 8.dp)
            .size(10.dp, 16.dp)
            .align(Alignment.CenterVertically),
        imageVector = MiuixIcons.Basic.ArrowUpDownIntegrated,
        colorFilter = ColorFilter.tint(actionColor),
        contentDescription = null
    )
}

@Composable
fun DropdownImpl(
    text: String,
    optionSize: Int,
    isSelected: Boolean,
    isPressed: Boolean,
    index: Int,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors()
) {
    val additionalTopPadding = if (index == 0) 16.dp else 12.dp
    val additionalBottomPadding = if (index == optionSize - 1) 16.dp else 12.dp

    val textColor = if (isSelected) dropdownColors.selectedContentColor
    else dropdownColors.contentColor

    val checkColor = if (isSelected) dropdownColors.selectedContentColor
    else Color.Transparent

    val finalBackgroundColor = if (isPressed) {
        COUITheme.colorScheme.onSurface
            .copy(alpha = 0.10f)
            .compositeOver(dropdownColors.containerColor)
    } else {
        dropdownColors.containerColor
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .widthIn(min = 178.dp)
            .background(finalBackgroundColor)
            .padding(horizontal = 16.dp)
            .padding(top = additionalTopPadding, bottom = additionalBottomPadding)
    ) {
        Text(
            modifier = Modifier.widthIn(max = 178.dp),
            text = text,
            fontSize = COUITheme.textStyles.body1.fontSize,
            fontWeight = FontWeight.Normal,
            color = textColor
        )

        Image(
            modifier = Modifier
                .padding(start = 12.dp)
                .size(20.dp),
            imageVector = MiuixIcons.Basic.Check,
            colorFilter = BlendModeColorFilter(checkColor, BlendMode.SrcIn),
            contentDescription = null
        )
    }
}

@Immutable
data class DropdownColors(
    val contentColor: Color,
    val containerColor: Color,
    val selectedContentColor: Color
)

object DropdownDefaults {

    @Composable
    fun dropdownColors(
        contentColor: Color = COUITheme.colorScheme.onSurface,
        containerColor: Color = COUITheme.colorScheme.surface,
        selectedContentColor: Color = COUITheme.colorScheme.onTertiaryContainer
    ): DropdownColors = DropdownColors(
        contentColor = contentColor,
        containerColor = containerColor,
        selectedContentColor = selectedContentColor
    )
}