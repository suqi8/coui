// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.internal.PreferenceCategoryRow
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * The size of a category title (COUIPreferenceCategory `title_type`).
 */
enum class PreferenceCategoryTitleType {
    /** 12sp, 16dp minimum height, 8dp above and below (TITLE_TYPE_SMALL, the default). */
    Small,

    /** 14sp, 20dp minimum height, 8dp above and below (TITLE_TYPE_MEDIUM). */
    Medium,

    /** 16sp, 22dp minimum height, 7dp above and below (TITLE_TYPE_LARGE). */
    Large,
}

/**
 * The horizontal margin of a category row (COUIPreferenceCategory `title_margin_start_type`).
 */
enum class PreferenceCategoryMarginStartType {
    /**
     * The title starts 16dp from the row (support_preference_category_layout_title_margin_start_small)
     * and the widget ends flush with the widget frame (TITLE_MARGIN_START_TYPE_SMALL).
     */
    Small,

    /**
     * The title starts 32dp from the row (support_preference_category_layout_title_margin_start_large)
     * and the widget frame keeps an extra 16dp end margin
     * (support_preference_category_layout_title_margin_end_large) (TITLE_MARGIN_START_TYPE_LARGE, the
     * default). This lines the title up with the content of a card that has 16dp page margins.
     */
    Large,
}

/**
 * A category title with COUI style, mirroring COUIPreferenceCategory.
 *
 * - **Title** ([titleType]): small 12sp `couiColorSecondNeutral`, medium 14sp and large 16sp
 *   `couiColorPrimaryNeutral`, all sans-serif-medium (couiTextAppearanceSmallButton with the size
 *   overridden by `setTextSize`). A small title next to a [titleIcon] turns `couiColorPrimaryNeutral`.
 * - **Title icon** ([titleIcon], COUI `icon_with_title`): placed right after the title, 4dp away (8dp
 *   for large titles). See [PreferenceCategoryDefaults.TitleIcon].
 * - **Title badge** ([titleBadge], COUI `text_in_reddot`): a text red dot 8dp after the title. It is
 *   hidden when a [titleIcon] is present, as in COUI.
 * - **Widget** ([endWidget]): the end-aligned `widget_frame`, 8dp after the title area. Use the ready
 *   made [PreferenceCategoryDefaults.TextButton], [PreferenceCategoryDefaults.SingleIcon] and
 *   [PreferenceCategoryDefaults.TextWithIcon] widgets; [CheckboxPreferenceCategory] and
 *   [LoadingPreferenceCategory] cover the checkbox and loading widgets.
 *
 * Spacing convention: the row only carries the title's own vertical margin (8dp for small titles), not
 * the COUI item top margin (`coui_preference_category_margintop_*`, 16dp by default). The cards in a
 * page bring that space with their own `padding(bottom = 16.dp)`, which adds up to the COUI 24dp from a
 * card bottom to the title text. Use [PreferenceCategoryDefaults.TopMarginLarge] /
 * [PreferenceCategoryDefaults.TopMarginSmall] only when the category does not follow such a card.
 *
 * @param title The category title.
 * @param modifier The modifier to be applied to the [PreferenceCategory].
 * @param titleType The size of the title.
 * @param marginStartType The horizontal margin type, which picks the default [insideMargin].
 * @param titleBadge The optional text shown in a red dot after the title (COUI `text_in_reddot`).
 *   Ignored when [titleIcon] is set.
 * @param titleColor The color of the title. [Color.Unspecified] picks
 *   [PreferenceCategoryDefaults.titleColor] for [titleType].
 * @param insideMargin The padding of the row. The start is the title margin; the end is the margin of
 *   the widget frame, inside which the ready-made widgets keep their own
 *   [PreferenceCategoryDefaults.WidgetContentEndPadding]. Without an [endWidget] the title keeps the
 *   start margin at its end too (COUI `mTitleOnly`).
 * @param titleIcon The optional icon after the title (COUI `icon_with_title`).
 * @param endWidget The optional widget at the end of the row (COUI `widget_frame`).
 */
@Composable
fun PreferenceCategory(
    title: String,
    modifier: Modifier = Modifier,
    titleType: PreferenceCategoryTitleType = PreferenceCategoryTitleType.Small,
    marginStartType: PreferenceCategoryMarginStartType = PreferenceCategoryMarginStartType.Large,
    titleBadge: String? = null,
    titleColor: Color = Color.Unspecified,
    insideMargin: PaddingValues = PreferenceCategoryDefaults.insideMargin(marginStartType),
    titleIcon: (@Composable () -> Unit)? = null,
    endWidget: (@Composable RowScope.() -> Unit)? = null,
) {
    val layoutDirection = LocalLayoutDirection.current
    val rowMargin = if (endWidget == null) {
        // COUIPreferenceCategory.initEndRect: a title-only row mirrors the start margin at the end.
        remember(insideMargin, layoutDirection) {
            val start = insideMargin.calculateStartPadding(layoutDirection)
            PaddingValues(
                start = start,
                top = insideMargin.calculateTopPadding(),
                end = maxOf(start, insideMargin.calculateEndPadding(layoutDirection)),
                bottom = insideMargin.calculateBottomPadding(),
            )
        }
    } else {
        insideMargin
    }
    PreferenceCategoryRow(
        title = title,
        titleColor = titleColor.takeOrElse {
            PreferenceCategoryDefaults.titleColor(titleType = titleType, hasTitleIcon = titleIcon != null)
        },
        insideMargin = rowMargin,
        modifier = modifier,
        titleType = titleType,
        titleIcon = titleIcon,
        titleBadge = titleBadge,
        widget = endWidget,
    )
}

/**
 * The title type of the enclosing category row, read by the ready-made widgets to pick their text
 * size and minimum height.
 */
internal val LocalPreferenceCategoryTitleType = staticCompositionLocalOf { PreferenceCategoryTitleType.Small }

/** Contains default values used by [PreferenceCategory]. */
object PreferenceCategoryDefaults {

    /**
     * The title start margin of [PreferenceCategoryMarginStartType.Large]
     * (support_preference_category_layout_title_margin_start_large).
     */
    val TitleMarginStartLarge = 32.dp

    /**
     * The title start margin of [PreferenceCategoryMarginStartType.Small]
     * (support_preference_category_layout_title_margin_start_small).
     */
    val TitleMarginStartSmall = 16.dp

    /**
     * The extra end margin of the widget frame for [PreferenceCategoryMarginStartType.Large]
     * (support_preference_category_layout_title_margin_end_large).
     */
    val WidgetFrameMarginEndLarge = 16.dp

    /**
     * The space every ready-made widget keeps between its content and the widget frame end
     * (coui_preference_category_widget_layout_padding_end). Add it to a custom [PreferenceCategory]
     * widget to line its content up with the ready-made ones.
     */
    val WidgetContentEndPadding = 16.dp

    /**
     * The item top margin of COUI `top_margin_type` large (coui_preference_category_margintop_large).
     * Not applied by default, see [PreferenceCategory].
     */
    val TopMarginLarge = 16.dp

    /** The item top margin of COUI `top_margin_type` small (coui_preference_category_margintop_small). */
    val TopMarginSmall = 12.dp

    /** The minimum height of a small title (coui_preference_category_text_height). */
    val TitleMinHeightSmall = 16.dp

    /** The minimum height of a medium title (coui_preference_category_text_height_medium). */
    val TitleMinHeightMedium = 20.dp

    /** The minimum height of a large title (coui_preference_category_text_height_large). */
    val TitleMinHeightLarge = 22.dp

    /**
     * The space above and below a small or medium title (support_preference_category_layout_title_margin_end_new,
     * coui_common_category_text_padding_top / _bottom_medium_style).
     */
    val TitleVerticalMargin = 8.dp

    /** The space above and below a large title (coui_common_category_text_padding_top_Large_style / _bottom_large_style). */
    val TitleVerticalMarginLarge = 7.dp

    /** The gap between a small or medium title and its icon (coui_category_title_margin_end_with_icon_small). */
    val TitleIconSpacing = 4.dp

    /** The gap between a large title and its icon (coui_category_title_margin_end_with_icon_large). */
    val TitleIconSpacingLarge = 8.dp

    /** The gap between the title and the title badge (coui_category_title_pading_end_with_reddot_default). */
    val TitleBadgeSpacing = 8.dp

    /**
     * The radius of the circular press mask of the single icon and the clickable title icon
     * (coui_preference_widget_layout_single_icon_radius).
     */
    val IconPressRadius = 16.dp

    /**
     * The minimum height of a text widget next to a medium or large title
     * (coui_preference_widget_layout_min_height_when_title_isnot_small).
     */
    val WidgetMinHeightWhenTitleIsNotSmall = 28.dp

    /** The end margin of the text button (coui_preference_category_textbutton_marginend). */
    val TextButtonMarginEnd = 4.dp

    /** The space above and below the text button (coui_preference_category_widget_layout_textbutton layout_marginVertical). */
    val TextButtonMarginVertical = 6.dp

    /**
     * The press area padding of the text button (COUITextViewCompatUtil.setPressRippleDrawable:
     * text_ripple_bg_padding_horizontal / text_ripple_bg_padding_vertical).
     */
    val TextButtonPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)

    /** The end margin of the single icon (coui_preference_category_Loading_marginend). */
    val SingleIconMarginEnd = 8.dp

    /** The padding of the single icon (coui_preference_category_widget_layout_singleicon.xml). */
    val SingleIconPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)

    /** The gap between the text and the icon of [TextWithIcon] (text_in_composition layout_marginEnd). */
    val TextWithIconSpacing = 4.dp

    /** The vertical press area padding of a clickable [TextWithIcon] (text_ripple_bg_padding_vertical). */
    val TextWithIconVerticalPadding = 4.dp

    /** The default padding of the row for the given [marginStartType], see [PreferenceCategory]. */
    fun insideMargin(
        marginStartType: PreferenceCategoryMarginStartType = PreferenceCategoryMarginStartType.Large,
    ): PaddingValues = when (marginStartType) {
        PreferenceCategoryMarginStartType.Large -> InsideMarginLarge
        PreferenceCategoryMarginStartType.Small -> InsideMarginSmall
    }

    private val InsideMarginLarge = PaddingValues(start = TitleMarginStartLarge, end = WidgetFrameMarginEndLarge)
    private val InsideMarginSmall = PaddingValues(start = TitleMarginStartSmall, end = 0.dp)

    /** The minimum height of the title for [titleType]. */
    fun titleMinHeight(titleType: PreferenceCategoryTitleType): Dp = when (titleType) {
        PreferenceCategoryTitleType.Small -> TitleMinHeightSmall
        PreferenceCategoryTitleType.Medium -> TitleMinHeightMedium
        PreferenceCategoryTitleType.Large -> TitleMinHeightLarge
    }

    /** The space above and below the title for [titleType]. */
    fun titleVerticalMargin(titleType: PreferenceCategoryTitleType): Dp = if (titleType == PreferenceCategoryTitleType.Large) TitleVerticalMarginLarge else TitleVerticalMargin

    /** The gap between the title and its icon for [titleType]. */
    fun titleIconSpacing(titleType: PreferenceCategoryTitleType): Dp = if (titleType == PreferenceCategoryTitleType.Large) TitleIconSpacingLarge else TitleIconSpacing

    /**
     * The title text style for [titleType]: couiTextAppearanceSmallButton (sans-serif-medium,
     * coui_spacing_multiplier_button_s) at 12 / 14 / 16sp.
     */
    @Composable
    fun titleTextStyle(titleType: PreferenceCategoryTitleType): TextStyle {
        val base = COUITheme.textStyles.bodyXS
        return remember(base, titleType) {
            base.copy(
                fontSize = when (titleType) {
                    PreferenceCategoryTitleType.Small -> 12.sp
                    PreferenceCategoryTitleType.Medium -> 14.sp
                    PreferenceCategoryTitleType.Large -> 16.sp
                },
                fontWeight = FontWeight.Medium,
            )
        }
    }

    /**
     * The text style of a widget text for [titleType]: couiTextButtonS (12sp medium), raised to 14sp
     * next to a medium or large title (COUIPreferenceCategory.initEndRect).
     */
    @Composable
    fun widgetTextStyle(titleType: PreferenceCategoryTitleType): TextStyle {
        val base = COUITheme.textStyles.bodyXS
        return remember(base, titleType) {
            base.copy(
                fontSize = if (titleType == PreferenceCategoryTitleType.Small) 12.sp else 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }

    /** The minimum height of a text widget for [titleType] (0dp next to a small title). */
    fun widgetMinHeight(titleType: PreferenceCategoryTitleType): Dp = if (titleType == PreferenceCategoryTitleType.Small) 0.dp else WidgetMinHeightWhenTitleIsNotSmall

    /**
     * The default title color: couiColorSecondNeutral for a small title without an icon, otherwise
     * couiColorPrimaryNeutral.
     */
    @Composable
    fun titleColor(
        titleType: PreferenceCategoryTitleType = PreferenceCategoryTitleType.Small,
        hasTitleIcon: Boolean = false,
    ): Color = if (titleType == PreferenceCategoryTitleType.Small && !hasTitleIcon) {
        COUITheme.colorScheme.labelSecondary
    } else {
        COUITheme.colorScheme.labelPrimary
    }

    /** The default text button color (COUI text_button ?couiColorPrimary). */
    @Composable
    fun textButtonColor(): Color = COUITheme.colorScheme.primary

    /**
     * The default [TextWithIcon] text color: couiColorPrimaryNeutral when it is clickable, otherwise
     * couiColorSecondNeutral (COUIPreferenceCategory.initEndRect).
     */
    @Composable
    fun textWithIconColor(clickable: Boolean): Color = if (clickable) COUITheme.colorScheme.labelPrimary else COUITheme.colorScheme.labelSecondary

    /** The default icon tint of the ready-made icons (COUI couiColorPrimaryNeutral). */
    @Composable
    fun iconTint(): Color = COUITheme.colorScheme.labelPrimary

    /**
     * An icon after the category title (COUI `icon_with_title`), drawn at its intrinsic size. When
     * [onClick] is set it gets the 16dp circular press mask (COUIPreferenceCategory
     * setTitleIconClickListener).
     *
     * @param imageVector The icon.
     * @param contentDescription The content description (COUI `icon_with_title_content_description`).
     * @param modifier The modifier to be applied to the icon.
     * @param onClick The optional click callback.
     * @param tint The tint of the icon.
     */
    @Composable
    fun TitleIcon(
        imageVector: ImageVector,
        contentDescription: String?,
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        tint: Color = iconTint(),
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = if (onClick != null) {
                modifier
                    .clip(CircleMaskShape(IconPressRadius))
                    .clickable(role = Role.Button, onClick = onClick)
            } else {
                modifier
            },
            tint = tint,
        )
    }

    /**
     * The text button widget (coui_preference_category_widget_layout_textbutton.xml): a
     * couiColorPrimary label of at most two lines, padded 12dp / 4dp inside a capsule press area,
     * 6dp above and below and 4dp before the widget frame end.
     *
     * @param text The label (COUI `text_in_right`).
     * @param onClick The click callback.
     * @param modifier The modifier to be applied to the widget.
     * @param color The color of the label.
     */
    @Composable
    fun TextButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        color: Color = textButtonColor(),
    ) {
        val titleType = LocalPreferenceCategoryTitleType.current
        Box(
            modifier = modifier
                .padding(end = TextButtonMarginEnd)
                .padding(vertical = TextButtonMarginVertical)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onClick)
                .heightIn(min = widgetMinHeight(titleType))
                .padding(TextButtonPadding),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = text,
                style = widgetTextStyle(titleType),
                color = color,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    /**
     * The single icon widget (coui_preference_category_widget_layout_singleicon.xml): an icon padded
     * 8dp / 4dp, 8dp before the widget frame end. When [onClick] is set it gets the 16dp circular
     * press mask.
     *
     * @param imageVector The icon (COUI `icon_in_right`).
     * @param contentDescription The content description (COUI `icon_in_right_content_description`).
     * @param modifier The modifier to be applied to the widget.
     * @param onClick The optional click callback.
     * @param tint The tint of the icon.
     */
    @Composable
    fun SingleIcon(
        imageVector: ImageVector,
        contentDescription: String?,
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        tint: Color = iconTint(),
    ) {
        Box(
            modifier = modifier
                .padding(end = SingleIconMarginEnd)
                .then(
                    if (onClick != null) {
                        Modifier
                            .clip(CircleMaskShape(IconPressRadius))
                            .clickable(role = Role.Button, onClick = onClick)
                    } else {
                        Modifier
                    },
                )
                .padding(SingleIconPadding),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = imageVector, contentDescription = contentDescription, tint = tint)
        }
    }

    /**
     * The text-with-icon widget (coui_preference_category_widget_layout_textwithicon.xml): a text of
     * at most two lines followed 4dp later by an icon, either of which may be omitted.
     *
     * Like COUIHalfHeightHorizontalPaddingLinearLayout, the widget is padded horizontally by half its
     * height and moves the rest of the 16dp fixed end padding into its end margin, so the content
     * always ends [WidgetContentEndPadding] before the widget frame end while the capsule press area
     * hugs it. When [onClick] is set it gets 4dp vertical press padding and the text turns
     * couiColorPrimaryNeutral.
     *
     * @param modifier The modifier to be applied to the widget.
     * @param text The optional text (COUI `text_in_right`).
     * @param icon The optional icon (COUI `icon_in_right`).
     * @param iconContentDescription The content description of [icon].
     * @param onClick The optional click callback (COUI setWidgetLayoutClickListener).
     * @param textColor The color of [text].
     * @param iconTint The tint of [icon].
     */
    @Composable
    fun TextWithIcon(
        modifier: Modifier = Modifier,
        text: String? = null,
        icon: ImageVector? = null,
        iconContentDescription: String? = null,
        onClick: (() -> Unit)? = null,
        textColor: Color = textWithIconColor(clickable = onClick != null),
        iconTint: Color = iconTint(),
    ) {
        val titleType = LocalPreferenceCategoryTitleType.current
        Row(
            modifier = modifier
                .halfHeightEndMargin(WidgetContentEndPadding)
                .then(
                    if (onClick != null) {
                        Modifier
                            .clip(CircleShape)
                            .clickable(onClick = onClick)
                    } else {
                        Modifier
                    },
                )
                .halfHeightHorizontalPadding(WidgetContentEndPadding)
                .heightIn(min = widgetMinHeight(titleType))
                .padding(vertical = if (onClick != null) TextWithIconVerticalPadding else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!text.isNullOrEmpty()) {
                Text(
                    text = text,
                    modifier = Modifier.weight(1f, fill = false),
                    style = widgetTextStyle(titleType),
                    color = textColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (icon != null) {
                if (!text.isNullOrEmpty()) Spacer(Modifier.width(TextWithIconSpacing))
                Icon(imageVector = icon, contentDescription = iconContentDescription, tint = iconTint)
            }
        }
    }
}

/**
 * COUIMaskRippleDrawable.setCircleRippleMask: a circle of [radius] around the center of the view,
 * shrunk to fit inside smaller bounds.
 */
@Immutable
private class CircleMaskShape(private val radius: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val r = minOf(with(density) { radius.toPx() }, size.minDimension / 2f)
        return Outline.Generic(
            Path().apply { addOval(Rect(center = Offset(size.width / 2f, size.height / 2f), radius = r)) },
        )
    }

    override fun equals(other: Any?): Boolean = other is CircleMaskShape && other.radius == radius

    override fun hashCode(): Int = radius.hashCode()
}

/**
 * The inner half of COUIHalfHeightHorizontalPaddingLinearLayout: pads the content horizontally by
 * half of its height, at most [fixPaddingEnd].
 */
private fun Modifier.halfHeightHorizontalPadding(fixPaddingEnd: Dp): Modifier = layout { measurable, constraints ->
    val reserve = fixPaddingEnd.roundToPx() * 2
    val placeable = measurable.measure(constraints.copy(minWidth = 0).offset(horizontal = -reserve))
    val pad = minOf(placeable.height / 2, fixPaddingEnd.roundToPx())
    val width = constraints.constrainWidth(placeable.width + pad * 2)
    val height = constraints.constrainHeight(placeable.height)
    layout(width, height) {
        placeable.placeRelative(pad, (height - placeable.height) / 2)
    }
}

/**
 * The outer half of COUIHalfHeightHorizontalPaddingLinearLayout: the part of [fixPaddingEnd] not
 * used by [halfHeightHorizontalPadding] (half of the padded widget height) becomes the end margin.
 */
private fun Modifier.halfHeightEndMargin(fixPaddingEnd: Dp): Modifier = layout { measurable, constraints ->
    val fix = fixPaddingEnd.roundToPx()
    val placeable = measurable.measure(constraints.copy(minWidth = 0).offset(horizontal = -fix))
    val margin = (fix - placeable.height / 2).coerceAtLeast(0)
    val width = constraints.constrainWidth(placeable.width + margin)
    val height = constraints.constrainHeight(placeable.height)
    layout(width, height) {
        placeable.placeRelative(0, (height - placeable.height) / 2)
    }
}
