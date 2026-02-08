// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.theme.LocalContentColor

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    minWidth: Dp = ButtonDefaults.MediumWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val containerColor = if (enabled) colors.containerColor else colors.disabledContainerColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.semantics { role = Role.Button },
        shape = shape,
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    minWidth: Dp = ButtonDefaults.MediumWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val containerColor = if (enabled) colors.containerColor else colors.disabledContainerColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.semantics { role = Role.Button },
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = border
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TextButtonColors = ButtonDefaults.textButtonColors(),
    shape: Shape = ButtonDefaults.shape,
    minWidth: Dp = ButtonDefaults.MediumWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    textStyle: TextStyle = COUITheme.textStyles.button
) {
    val containerColor = if (enabled) colors.containerColor else colors.disabledContainerColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.semantics { role = Role.Button },
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Text(
                    text = text,
                    color = contentColor, // 使用计算后的颜色
                    style = textStyle
                )
            }
        )
    }
}

@Composable
fun DescriptionButtonContent(
    text: String,
    description: String,
    // 默认使用当前环境的 contentColor (已被 Surface 设置好)
    textColor: Color = LocalContentColor.current,
    descriptionColor: Color = textColor.copy(alpha = 0.7f) // 副标题稍微淡一点
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = textColor,
            // 使用传入的排版样式，或者硬编码为 16sp Medium
            style = COUITheme.textStyles.button.copy(textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = description,
            color = descriptionColor,
            // 副标题使用 10sp Normal
            style = COUITheme.textStyles.overline.copy(
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                letterSpacing = 0.sp // 按钮内的副标题通常不需要额外的字间距
            )
        )
    }
}

object ButtonDefaults {
    val MinHeight = 44.dp
    val MediumWidth = 152.dp
    val LargeWidth = 174.dp
    val CornerRadius = 21.dp
    val ContentPadding = PaddingValues(horizontal = 12.dp)

    val shape: Shape
        @Composable
        get() = remember { RoundedCornerShape(CornerRadius) }

    @Composable
    fun buttonColors(
        containerColor: Color = COUITheme.colorScheme.primary,
        contentColor: Color = COUITheme.colorScheme.onPrimary,
        // [精调] 禁用时保持色相，透明度降为 50%
        disabledContainerColor: Color = containerColor.copy(alpha = 0.5f),
        disabledContentColor: Color = contentColor.copy(alpha = 0.5f)
    ): ButtonColors = ButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor
    )

    @Composable
    fun outlinedButtonColors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = COUITheme.colorScheme.primary,
        disabledContainerColor: Color = Color.Transparent,
        // [精调] 禁用时文字透明度降为 40%
        disabledContentColor: Color = contentColor.copy(alpha = 0.4f)
    ): ButtonColors = ButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor
    )

    @Composable
    fun outlinedButtonBorder(
        enabled: Boolean,
        borderColor: Color = COUITheme.colorScheme.outline,
        // [精调] 禁用边框透明度降为 30%
        disabledBorderColor: Color = borderColor.copy(alpha = 0.3f)
    ): BorderStroke {
        val color = if (enabled) borderColor else disabledBorderColor
        return remember(color) { BorderStroke(1.dp, color) }
    }

    @Composable
    fun textButtonColors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = COUITheme.colorScheme.primary,
        disabledContainerColor: Color = Color.Transparent,
        // [精调] 禁用时文字透明度降为 40%
        disabledContentColor: Color = contentColor.copy(alpha = 0.4f)
    ): TextButtonColors = TextButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor
    )

    @Composable
    fun textButtonColorsNeutral(
        containerColor: Color = Color.Transparent,
        // 次要操作使用次级文本色 (灰色)
        contentColor: Color = COUITheme.colorScheme.onSurfaceVariantActions,
        disabledContainerColor: Color = Color.Transparent,
        disabledContentColor: Color = contentColor.copy(alpha = 0.4f)
    ): TextButtonColors = TextButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor
    )
}

@Immutable
data class ButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color
)

@Immutable
data class TextButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color
)
