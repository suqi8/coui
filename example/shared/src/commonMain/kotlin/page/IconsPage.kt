// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.LocalNavigator
import io.github.suqi8.coui.kmp.basic.EmptyState
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchBar
import io.github.suqi8.coui.kmp.basic.rememberTopAppBarSearchState
import io.github.suqi8.coui.kmp.basic.topAppBarSearchContent
import io.github.suqi8.coui.kmp.basic.topAppBarSearchResults
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.ExpandLess
import io.github.suqi8.coui.kmp.icon.extended.ExpandMore
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.design_icons
import io.github.suqi8.coui.kmp.shared.generated.resources.design_icons_compare_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.design_icons_name
import io.github.suqi8.coui.kmp.shared.generated.resources.design_icons_no_match
import io.github.suqi8.coui.kmp.shared.generated.resources.design_icons_search
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.COUITheme.colorScheme
import org.jetbrains.compose.resources.stringResource
import ui.AppPageList
import ui.AppPageScaffold
import utils.All

private val IconListTopShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
private val IconListBottomShape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
private val WeightNames = listOf("Light", "Normal", "Regular", "Medium", "Demibold")

/**
 * The icon reference: every extended icon by name; tapping a row compares its weights. The search
 * bar below the title searches in place: the full list fades out and the matching icons come up.
 */
@Composable
fun IconsPage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    var searching by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    val searchState = rememberTopAppBarSearchState(active = searching)
    // Hoisted, so the full list keeps its scroll position while the search hides it.
    val allListState = rememberLazyListState()

    val allIcons = remember { COUIIcons.All }
    val regularIcons = remember(allIcons) { allIcons["Regular"].orEmpty() }
    val weightVariants: List<Pair<String, List<ImageVector>>> = remember(allIcons) {
        WeightNames.map { name -> name to allIcons[name].orEmpty() }
    }
    val iconNames = remember(regularIcons) { regularIcons.map { it.name.substringBefore(".") } }
    val allIndices = remember(iconNames) { iconNames.indices.toList() }
    val matches = remember(query, iconNames) {
        val q = query.trim()
        if (q.isEmpty()) allIndices else iconNames.indices.filter { iconNames[it].contains(q, ignoreCase = true) }
    }
    // Only one row is expanded at a time; -1 means none.
    var expandedIndex by rememberSaveable { mutableIntStateOf(-1) }
    val iconList: LazyListScope.(List<Int>) -> Unit = { indices ->
        iconRows(
            indices = indices,
            names = iconNames,
            icons = regularIcons,
            weightVariants = weightVariants,
            expandedIndex = expandedIndex,
            onToggle = { index -> expandedIndex = if (expandedIndex == index) -1 else index },
        )
    }

    AppPageScaffold(
        title = stringResource(Res.string.design_icons),
        modifier = modifier,
        onBack = { navigator.pop() },
        topBarSearchState = searchState,
        topBarBottomContent = {
            TopAppBarSearchBar(
                query = query,
                onQueryChange = { query = it },
                expanded = searching,
                onExpandedChange = { active ->
                    searching = active
                    if (!active) query = ""
                },
                cancelText = stringResource(Res.string.common_cancel),
                label = stringResource(Res.string.design_icons_search),
            )
        },
    ) { scope ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (searchState.isContentVisible) {
                AppPageList(
                    scope = scope,
                    modifier = Modifier.topAppBarSearchContent(searchState),
                    listState = allListState,
                ) { iconList(allIndices) }
            }
            if (searchState.isResultsVisible) {
                AppPageList(scope = scope, modifier = Modifier.topAppBarSearchResults(searchState)) {
                    iconList(matches)
                }
            }
        }
    }
}

/** The icons at [indices] in one card with a header, or an empty state when there are none. */
private fun LazyListScope.iconRows(
    indices: List<Int>,
    names: List<String>,
    icons: List<ImageVector>,
    weightVariants: List<Pair<String, List<ImageVector>>>,
    expandedIndex: Int,
    onToggle: (Int) -> Unit,
) {
    if (indices.isEmpty()) {
        item(key = "empty") {
            EmptyState(
                title = stringResource(Res.string.design_icons_no_match),
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
            )
        }
        return
    }
    item(key = "iconsHeader") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(IconListTopShape)
                .background(colorScheme.cardBackground)
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.design_icons_name),
                modifier = Modifier.weight(1f),
                style = COUITheme.textStyles.bodyS,
                color = colorScheme.labelTertiary,
            )
            Text(
                text = stringResource(Res.string.design_icons_compare_hint),
                style = COUITheme.textStyles.bodyXS,
                color = colorScheme.labelTertiary,
            )
        }
    }
    items(count = indices.size, key = { "icon_${indices[it]}" }) { position ->
        val index = indices[position]
        IconRow(
            name = names[index],
            icon = icons[index],
            weights = weightVariants.map { (label, variants) -> label to variants[index] },
            expanded = expandedIndex == index,
            onToggle = { onToggle(index) },
            isLast = position == indices.lastIndex,
        )
    }
}

@Composable
private fun IconRow(
    name: String,
    icon: ImageVector,
    weights: List<Pair<String, ImageVector>>,
    expanded: Boolean,
    onToggle: () -> Unit,
    isLast: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(if (isLast) IconListBottomShape else RectangleShape)
            .background(colorScheme.cardBackground)
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp)
            .padding(vertical = 6.dp)
            .padding(bottom = if (isLast) 6.dp else 0.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                style = COUITheme.textStyles.bodyM,
                color = colorScheme.labelPrimary,
            )
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = colorScheme.labelPrimary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
                imageVector = if (expanded) COUIIcons.ExpandLess else COUIIcons.ExpandMore,
                contentDescription = null,
                tint = colorScheme.labelTertiary,
                modifier = Modifier.size(18.dp),
            )
        }
        AnimatedVisibility(visible = expanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                verticalAlignment = Alignment.Top,
            ) {
                weights.forEach { (label, weightIcon) ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = weightIcon,
                            contentDescription = "$name ($label)",
                            tint = colorScheme.labelPrimary,
                            modifier = Modifier.size(28.dp),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            style = COUITheme.textStyles.bodyXS,
                            color = colorScheme.labelTertiary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
