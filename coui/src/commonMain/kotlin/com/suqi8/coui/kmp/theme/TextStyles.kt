// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * COUI 风格的排版系统。
 * 包含了精确复刻的核心样式，以及为了兼容旧代码而保留的映射样式。
 */
@Immutable
data class Typography(
    // ========================================================================
    // 1. COUI 核心标准样式 (Core Standard Styles) - 基于原生 XML 精确复刻
    // ========================================================================
    /** 列表项主标题 (Native: couiTextAppearanceHeadline6) - 16sp Medium */
    val headline: TextStyle = DefaultHeadline,
    /** 正文/摘要 (Native: couiTextAppearanceBody) - 14sp Normal */
    val body: TextStyle = DefaultBody,
    /** 分类小标题 (Native: couiTextAppearanceSmallButton) - 12sp Medium */
    val subtitle: TextStyle = DefaultSubtitle,
    /** 辅助说明/页脚 (Native: Footnote/Caption) - 11sp Normal */
    val caption: TextStyle = DefaultCaption,
    /** 微标/角标 (Native: couiTextAppearanceTag) - 10sp Medium */
    val overline: TextStyle = DefaultOverline,

    // ========================================================================
    // 2. 兼容性样式映射 (Compatibility Styles) - 保持旧代码兼容性
    // ========================================================================
    val main: TextStyle = DefaultHeadline,      // 旧 17sp -> 映射到标准 16sp Headline
    val paragraph: TextStyle = DefaultBody,     // 旧 Paragraph -> 映射到标准 Body
    val body1: TextStyle = DefaultHeadline,     // 旧 16sp -> 映射到标准 16sp Headline
    val body2: TextStyle = DefaultBody,         // 旧 14sp -> 映射到标准 14sp Body (完美匹配)
    val button: TextStyle = DefaultHeadline,    // COUI 按钮文字与 Headline 规格一致 (16sp Medium)
    val footnote1: TextStyle = DefaultSubtitle, // 旧 13sp -> 映射到相近的 12sp Medium Subtitle
    val footnote2: TextStyle = DefaultCaption,  // 旧 11sp -> 映射到标准 11sp Caption (完美匹配)
    val headline1: TextStyle = DefaultHeadline,
    val headline2: TextStyle = DefaultHeadline,
    // 大标题系列 (保留结构，数值微调以更符合现代标准)
    val displayLarge: TextStyle = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.Normal, lineHeight = 1.1.em),
    val displayMedium: TextStyle = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Medium, lineHeight = 1.2.em),
    val title1: TextStyle = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Medium, lineHeight = 1.2.em),
    val title2: TextStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium, lineHeight = 1.2.em),
    val title3: TextStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium, lineHeight = 1.2.em),
    val title4: TextStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium, lineHeight = 1.2.em)
) {
    /**
     * 返回一个新的 Typography，其中所有没有指定颜色的样式都将使用给定的 [defaultColor]。
     * 这对于根据背景色自动调整文本颜色非常有用。
     */
    fun withDefaultColor(defaultColor: Color): Typography {
        return this.copy(
            headline = headline.merge(TextStyle(color = defaultColor)),
            body = body.merge(TextStyle(color = defaultColor)),
            subtitle = subtitle.merge(TextStyle(color = defaultColor)),
            caption = caption.merge(TextStyle(color = defaultColor)),
            overline = overline.merge(TextStyle(color = defaultColor)),
            main = main.merge(TextStyle(color = defaultColor)),
            paragraph = paragraph.merge(TextStyle(color = defaultColor)),
            body1 = body1.merge(TextStyle(color = defaultColor)),
            body2 = body2.merge(TextStyle(color = defaultColor)),
            button = button.merge(TextStyle(color = defaultColor)),
            footnote1 = footnote1.merge(TextStyle(color = defaultColor)),
            footnote2 = footnote2.merge(TextStyle(color = defaultColor)),
            headline1 = headline1.merge(TextStyle(color = defaultColor)),
            headline2 = headline2.merge(TextStyle(color = defaultColor)),
            displayLarge = displayLarge.merge(TextStyle(color = defaultColor)),
            displayMedium = displayMedium.merge(TextStyle(color = defaultColor)),
            title1 = title1.merge(TextStyle(color = defaultColor)),
            title2 = title2.merge(TextStyle(color = defaultColor)),
            title3 = title3.merge(TextStyle(color = defaultColor)),
            title4 = title4.merge(TextStyle(color = defaultColor))
        )
    }
}

// ========================================================================
// COUI 原生核心样式定义 (Private Base Styles)
// ========================================================================

// [精确数值] 16sp Medium, 行高约 1.2倍
private val DefaultHeadline = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 1.2.em
)

// [精确数值] 14sp Normal, 行高约 1.3倍
private val DefaultBody = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 1.3.em
)

// [精确数值] 12sp Medium
private val DefaultSubtitle = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 1.4.em
)

// [精确数值] 11sp Normal
private val DefaultCaption = TextStyle(
    fontSize = 11.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 1.4.em
)

// [精确数值] 10sp Medium (用于角标等)
private val DefaultOverline = TextStyle(
    fontSize = 10.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.5.sp
)

internal val LocalTypography = staticCompositionLocalOf { Typography() }
