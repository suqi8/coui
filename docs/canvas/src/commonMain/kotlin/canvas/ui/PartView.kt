// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import canvas.model.BUTTON_GAP
import canvas.model.Frame
import canvas.model.Group
import canvas.model.Item
import canvas.model.Kind
import canvas.model.STATUS_BAR_H
import canvas.model.itemHeight
import canvas.model.itemWidth
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Chip
import io.github.suqi8.coui.kmp.basic.CircularProgressIndicator
import io.github.suqi8.coui.kmp.basic.EmptyState
import io.github.suqi8.coui.kmp.basic.FloatingActionButton
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.InfiniteProgressIndicator
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.LinearProgressIndicator
import io.github.suqi8.coui.kmp.basic.Slider
import io.github.suqi8.coui.kmp.basic.SmallTopAppBar
import io.github.suqi8.coui.kmp.basic.Surface
import io.github.suqi8.coui.kmp.basic.TabRow
import io.github.suqi8.coui.kmp.basic.TabRowWithContour
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.basic.TextFieldMode
import io.github.suqi8.coui.kmp.basic.TopAppBar
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchSlot
import io.github.suqi8.coui.kmp.basic.TopTips
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Back
import io.github.suqi8.coui.kmp.icon.extended.Home
import io.github.suqi8.coui.kmp.icon.extended.Image
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.CheckboxPreference
import io.github.suqi8.coui.kmp.preference.InputPreference
import io.github.suqi8.coui.kmp.preference.MarkPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.preference.RadioButtonPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * How parts behave where they are drawn. On the canvas ([live] false) every callback is a no-op
 * and an overlay above the part takes the pointer; in the preview ([live] true) switches flip,
 * sliders slide and taps report to [onTap] with the tapped target (`""` for the part itself,
 * `tab:N`, `icon` or `icon2`).
 */
@Stable
class PartContext(
    val live: Boolean,
    val onTap: (item: Item, target: String) -> Unit = { _, _ -> },
) {
    /** Preview-only state that overrides the document (switch states, slider values, selected tabs). */
    val overrides = mutableStateMapOf<String, Any>()

    fun checked(item: Item): Boolean = overrides[item.id + ":checked"] as? Boolean ?: (item.checked == true)

    fun value(item: Item): Float = overrides[item.id + ":value"] as? Float ?: (item.value ?: 50f)

    fun selected(item: Item): Int = overrides[item.id + ":selected"] as? Int ?: (item.selected ?: 0)

    fun set(item: Item, key: String, value: Any) {
        if (live) overrides[item.id + ":" + key] = value
    }

    fun tap(item: Item, target: String = "") {
        if (live) onTap(item, target)
    }
}

/** Reports where each item of a group sits inside the group, for selection and hit testing. */
typealias ItemPlaced = (itemId: String, coordinates: LayoutCoordinates) -> Unit

private fun Modifier.reportItem(item: Item, onPlaced: ItemPlaced?): Modifier = if (onPlaced == null) this else onGloballyPositioned { onPlaced(item.id, it) }

private fun icon(name: String?): ImageVector? = iconFor(name)

/** One group: a connected run of rows in one card, a row of buttons or chips, or a single part. */
@Composable
fun GroupView(
    group: Group,
    frame: Frame?,
    ctx: PartContext,
    modifier: Modifier = Modifier,
    onItemPlaced: ItemPlaced? = null,
) {
    val kind = group.kind ?: return
    when {
        kind.family == canvas.model.Family.Row -> {
            val w = itemWidth(group.first, frame)
            Card(modifier = modifier.width(w.dp)) {
                CardListGroup {
                    group.items.forEach { row ->
                        item { RowPart(row, ctx, Modifier.reportItem(row, onItemPlaced)) }
                    }
                }
            }
        }

        group.axis == Group.AXIS_X && group.items.size > 1 -> {
            Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(BUTTON_GAP.dp), verticalAlignment = Alignment.CenterVertically) {
                group.items.forEach { SinglePart(it, frame, ctx, Modifier.reportItem(it, onItemPlaced)) }
            }
        }

        else -> Column(modifier = modifier) {
            group.items.forEach { SinglePart(it, frame, ctx, Modifier.reportItem(it, onItemPlaced)) }
        }
    }
}

@Composable
private fun RowStartIcon(item: Item) {
    val vector = icon(item.icon) ?: return
    Icon(vector, contentDescription = null, modifier = Modifier.size(24.dp), tint = COUITheme.colorScheme.primary)
}

@Composable
private fun EndText(text: String?) {
    if (text.isNullOrEmpty()) return
    Text(text, style = COUITheme.textStyles.bodyM, color = COUITheme.colorScheme.labelTertiary)
}

/** A preference row inside a card run. */
@Composable
fun RowPart(item: Item, ctx: PartContext, modifier: Modifier = Modifier) {
    val kind = Kind.of(item.kind) ?: return
    val summary = item.supporting?.takeIf { it.isNotEmpty() }
    val start: (@Composable () -> Unit)? = if (icon(item.icon) != null) {
        { RowStartIcon(item) }
    } else {
        null
    }
    when (kind) {
        Kind.SwitchRow -> SwitchPreference(
            checked = ctx.checked(item),
            onCheckedChange = { ctx.set(item, "checked", it) },
            title = item.label,
            modifier = modifier,
            summary = summary,
            startAction = start,
        )

        Kind.ArrowRow -> ArrowPreference(
            title = item.label,
            modifier = modifier,
            summary = summary,
            startAction = start,
            endActions = { EndText(item.end) },
            onClick = { ctx.tap(item) },
        )

        Kind.BasicRow -> BasicComponent(
            modifier = modifier,
            title = item.label,
            summary = summary,
            startAction = start,
            endActions = if (item.end.isNullOrEmpty()) null else ({ EndText(item.end) }),
            onClick = { ctx.tap(item) },
        )

        Kind.DropdownRow -> {
            val options = item.tabs.orEmpty().map { it.label }.ifEmpty { listOf("—") }
            OverlayDropdownPreference(
                items = options,
                selectedIndex = ctx.selected(item).coerceIn(0, options.lastIndex),
                title = item.label,
                modifier = modifier,
                summary = summary,
                startAction = start,
                enabled = true,
                onSelectedIndexChange = { ctx.set(item, "selected", it) },
            )
        }

        Kind.CheckboxRow -> CheckboxPreference(
            title = item.label,
            checked = ctx.checked(item),
            onCheckedChange = { ctx.set(item, "checked", it) },
            modifier = modifier,
            summary = summary,
            startAction = start,
        )

        Kind.RadioRow -> RadioButtonPreference(
            title = item.label,
            selected = ctx.checked(item),
            onClick = { ctx.set(item, "checked", true) },
            modifier = modifier,
            summary = summary,
        )

        Kind.MarkRow -> MarkPreference(
            title = item.label,
            checked = ctx.checked(item),
            onClick = { ctx.set(item, "checked", !ctx.checked(item)) },
            modifier = modifier,
            summary = summary,
        )

        Kind.SliderRow -> SliderPreference(
            value = ctx.value(item) / 100f,
            onValueChange = { ctx.set(item, "value", it * 100f) },
            modifier = modifier,
            title = item.label.takeIf { it.isNotEmpty() },
            summary = summary,
            startAction = start,
            valueText = item.end?.takeIf { it.isNotEmpty() },
        )

        Kind.InputRow -> InputPreference(
            value = "",
            onValueChange = {},
            modifier = modifier,
            title = item.label.takeIf { it.isNotEmpty() },
            hint = item.supporting.orEmpty(),
            readOnly = !ctx.live,
        )

        else -> SinglePart(item, null, ctx, modifier)
    }
}

/** The top bar, with the status bar strip the frame draws its clock and icons on. */
@Composable
private fun TopBarPart(item: Item, ctx: PartContext, width: Dp, wide: Boolean, modifier: Modifier = Modifier) {
    val back: @Composable () -> Unit = {
        if (item.icon != null) {
            IconButton(onClick = { ctx.tap(item, "icon") }) {
                Icon(icon(item.icon) ?: COUIIcons.Back, contentDescription = null, tint = COUITheme.colorScheme.labelPrimary)
            }
        }
    }
    val actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {
        icon(item.icon2)?.let { vector ->
            IconButton(onClick = { ctx.tap(item, "icon2") }) {
                Icon(vector, contentDescription = null, tint = COUITheme.colorScheme.labelPrimary)
            }
        }
    }
    val color = COUITheme.colorScheme.backgroundWithCard
    Column(modifier = modifier.width(width).background(color)) {
        Spacer(Modifier.height(STATUS_BAR_H.dp))
        // Like CouiPage(wide = isWideWindow()): wide windows pin the small bar instead of the large title.
        when (if (wide && item.variant != "home") "small" else item.variant) {
            "small" -> SmallTopAppBar(
                title = item.label,
                color = color,
                subtitle = item.supporting.orEmpty(),
                navigationIcon = back,
                actions = actions,
                defaultWindowInsetsPadding = false,
                showDivider = false,
            )

            "home" -> TopAppBar(
                title = item.label,
                color = color,
                navigationIcon = back,
                actions = actions,
                defaultWindowInsetsPadding = false,
                showDivider = false,
                expandedHeight = 116.dp,
                largeTitleFontSize = 40.sp,
                titlePadding = 24.dp,
                largeTitleBottomMargin = 18.dp,
                bottomContent = {
                    TopAppBarSearchSlot(
                        inputField = {
                            InputField(
                                query = "",
                                onQueryChange = {},
                                onSearch = {},
                                expanded = false,
                                onExpandedChange = { if (it) ctx.tap(item, "search") },
                                label = item.supporting ?: "",
                            )
                        },
                    )
                },
            )

            else -> TopAppBar(
                title = item.label,
                color = color,
                subtitle = item.supporting.orEmpty(),
                navigationIcon = back,
                actions = actions,
                defaultWindowInsetsPadding = false,
                showDivider = false,
            )
        }
    }
}

/** Any part that is not a row of a card run. */
@Composable
fun SinglePart(item: Item, frame: Frame?, ctx: PartContext, modifier: Modifier = Modifier) {
    val kind = Kind.of(item.kind) ?: return
    val width = itemWidth(item, frame).dp
    val colors = COUITheme.colorScheme
    val styles = COUITheme.textStyles
    when (kind) {
        Kind.TopBar -> TopBarPart(item, ctx, width, frame?.isWide == true, modifier)

        Kind.SearchBar -> Box(modifier.width(width)) {
            InputField(
                query = "",
                onQueryChange = {},
                onSearch = {},
                expanded = false,
                onExpandedChange = { if (it) ctx.tap(item) },
                label = item.label,
            )
        }

        Kind.Tabs -> {
            val tabs = item.tabs.orEmpty().map { it.label }.ifEmpty { listOf("—") }
            val selected = ctx.selected(item).coerceIn(0, tabs.lastIndex)
            Box(modifier.width(width)) {
                if (item.variant == "contour") {
                    TabRowWithContour(tabs = tabs, selectedTabIndex = selected, onTabSelected = {
                        ctx.set(item, "selected", it)
                        ctx.tap(item, "tab:$it")
                    })
                } else {
                    TabRow(tabs = tabs, selectedTabIndex = selected, onTabSelected = {
                        ctx.set(item, "selected", it)
                        ctx.tap(item, "tab:$it")
                    })
                }
            }
        }

        Kind.BottomNav -> Box(modifier.width(width).padding(bottom = 16.dp), contentAlignment = Alignment.BottomCenter) {
            val selected = ctx.selected(item)
            FloatingNavigationBar(defaultWindowInsetsPadding = false) {
                item.tabs.orEmpty().forEachIndexed { index, tab ->
                    FloatingNavigationBarItem(
                        selected = index == selected,
                        onClick = { ctx.tap(item, "tab:$index") },
                        icon = icon(tab.icon) ?: COUIIcons.Home,
                        label = tab.label,
                    )
                }
            }
        }

        Kind.Category -> Box(modifier.width(width)) { PreferenceCategory(title = item.label) }

        Kind.Button -> {
            val primary = item.variant != "secondary"
            TextButton(
                text = item.label,
                onClick = { ctx.tap(item) },
                modifier = if (item.size != null) modifier.width(width) else modifier,
                colors = if (primary) ButtonDefaults.textButtonColorsPrimary() else ButtonDefaults.textButtonColors(),
            )
        }

        Kind.IconButton -> IconButton(onClick = { ctx.tap(item) }, modifier = modifier, backgroundColor = colors.secondaryButton) {
            Icon(icon(item.icon) ?: COUIIcons.Home, contentDescription = null, tint = colors.labelPrimary)
        }

        Kind.Fab -> FloatingActionButton(onClick = { ctx.tap(item) }, modifier = modifier) {
            Icon(icon(item.icon) ?: COUIIcons.Home, contentDescription = null, tint = colors.onPrimary)
        }

        Kind.Chip -> Chip(
            selected = ctx.checked(item),
            onClick = {
                ctx.set(item, "checked", !ctx.checked(item))
                ctx.tap(item)
            },
            label = item.label,
            modifier = modifier,
            icon = icon(item.icon)?.let { vector -> @Composable { Icon(vector, contentDescription = null, modifier = Modifier.size(16.dp)) } },
        )

        Kind.Card -> Card(
            modifier = modifier.width(width).heightIn(min = (item.size2 ?: 0f).dp),
            onClick = { ctx.tap(item) },
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                icon(item.icon)?.let { Icon(it, contentDescription = null, modifier = Modifier.size(28.dp).padding(bottom = 4.dp), tint = colors.primary) }
                if (item.label.isNotEmpty()) Text(item.label, style = styles.headlineXS, color = colors.labelPrimary)
                item.supporting?.takeIf { it.isNotEmpty() }?.let { Text(it, style = styles.bodyS, color = colors.labelSecondary) }
            }
        }

        Kind.Text -> {
            val style = when (item.variant) {
                "body" -> styles.bodyL
                "caption" -> styles.bodyXS
                else -> styles.headlineM
            }
            val color = if (item.variant == "caption") colors.labelSecondary else colors.labelPrimary
            Text(
                item.label,
                modifier = if (item.size != null) modifier.width(width) else modifier,
                style = if (item.variant == null || item.variant == "title") style.copy(fontWeight = FontWeight.Medium) else style,
                color = color,
            )
        }

        Kind.Image -> Box(
            modifier.width(width).height(itemHeight(item).dp).clip(RoundedCornerShape(16.dp)).background(colors.fillThin),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(COUIIcons.Image, contentDescription = null, modifier = Modifier.size(36.dp), tint = colors.labelTertiary)
                if (item.label.isNotEmpty()) Text(item.label, style = styles.bodyXS, color = colors.labelTertiary, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }

        Kind.TopTips -> TopTips(
            text = item.label,
            modifier = modifier.width(width),
            startIcon = icon(item.icon)?.let { vector -> @Composable { Icon(vector, contentDescription = null, modifier = Modifier.size(20.dp), tint = colors.primary) } },
            actionText = item.supporting?.takeIf { it.isNotEmpty() },
            onAction = if (item.supporting.isNullOrEmpty()) null else ({ ctx.tap(item) }),
        )

        Kind.EmptyState -> EmptyState(
            title = item.label,
            modifier = modifier.width(width),
            subtitle = item.supporting?.takeIf { it.isNotEmpty() },
            actionText = item.end?.takeIf { it.isNotEmpty() },
            onActionClick = if (item.end.isNullOrEmpty()) null else ({ ctx.tap(item) }),
        )

        Kind.TextField -> TextField(
            value = "",
            onValueChange = {},
            modifier = modifier.width(width),
            backgroundMode = if (item.variant == "filled") TextFieldMode.Rectangle else TextFieldMode.Line,
            label = item.label,
            readOnly = !ctx.live,
            singleLine = true,
        )

        Kind.Slider -> Box(modifier.width(width).height(36.dp), contentAlignment = Alignment.Center) {
            Slider(value = ctx.value(item) / 100f, onValueChange = { ctx.set(item, "value", it * 100f) })
        }

        Kind.Progress -> Box(modifier.width(width), contentAlignment = Alignment.Center) {
            when (item.variant) {
                "circular" -> CircularProgressIndicator(progress = ctx.value(item) / 100f)
                "loading" -> InfiniteProgressIndicator()
                else -> LinearProgressIndicator(progress = ctx.value(item) / 100f, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
            }
        }

        Kind.Divider -> Box(modifier.width(width).height(16.dp), contentAlignment = Alignment.Center) { HorizontalDivider() }

        Kind.Dialog -> DialogPart(item, ctx, width, modifier)

        Kind.BottomSheet -> Surface(
            modifier = modifier.width(width).height(itemHeight(item).dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = colors.surface,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.padding(top = 10.dp).size(width = 32.dp, height = 4.dp).clip(CircleShape).background(colors.dragHandle))
                if (item.label.isNotEmpty()) {
                    Text(item.label, modifier = Modifier.padding(top = 14.dp, bottom = 8.dp), style = styles.headlineXS, color = colors.labelPrimary)
                }
            }
        }

        Kind.Snackbar -> Surface(
            modifier = modifier.width(width),
            shape = RoundedCornerShape(16.dp),
            color = colors.backgroundTop,
            shadowElevation = 6.dp,
        ) {
            Row(Modifier.padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(item.label, modifier = Modifier.weight(1f), style = styles.bodyM, color = colors.labelPrimary)
                item.supporting?.takeIf { it.isNotEmpty() }?.let { Text(it, style = styles.buttonM, color = colors.primary) }
            }
        }

        else -> RowPart(item, ctx, modifier.width(width))
    }
}

@Composable
private fun DialogPart(item: Item, ctx: PartContext, width: Dp, modifier: Modifier = Modifier) {
    val colors = COUITheme.colorScheme
    val styles = COUITheme.textStyles
    val buttons = item.tabs.orEmpty()
    fun action(index: Int, recommended: Boolean = false) = buttons.getOrNull(index)?.let { DialogButtonBarAction(it.label, recommended = recommended) { ctx.tap(item, "tab:$index") } }
    Surface(modifier = modifier.width(width), shape = RoundedCornerShape(32.dp), color = colors.surface) {
        Column(Modifier.padding(top = 28.dp, bottom = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (item.label.isNotEmpty()) {
                Text(item.label, modifier = Modifier.padding(horizontal = 24.dp), style = styles.headlineXS, color = colors.labelPrimary)
            }
            item.supporting?.takeIf { it.isNotEmpty() }?.let {
                Text(it, modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp), style = styles.bodyM, color = colors.labelSecondary)
            }
            if (buttons.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Box(Modifier.padding(horizontal = 20.dp)) {
                    when (buttons.size) {
                        1 -> DialogButtonBar(negative = null, positive = action(0, recommended = true))
                        2 -> DialogButtonBar(negative = action(0), positive = action(1, recommended = true))
                        else -> DialogButtonBar(negative = action(0), positive = action(2, recommended = true), neutral = action(1))
                    }
                }
            }
        }
    }
}
