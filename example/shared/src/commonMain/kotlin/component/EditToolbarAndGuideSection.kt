// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbar
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbarItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.NavigationBar
import io.github.suqi8.coui.kmp.basic.NavigationBarItem
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TabRow
import io.github.suqi8.coui.kmp.basic.TabWithSearchLayout
import io.github.suqi8.coui.kmp.basic.TabWithSearchLayoutDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Copy
import io.github.suqi8.coui.kmp.icon.extended.Delete
import io.github.suqi8.coui.kmp.icon.extended.Favorites
import io.github.suqi8.coui.kmp.icon.extended.Home
import io.github.suqi8.coui.kmp.icon.extended.More
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.icon.extended.Share
import io.github.suqi8.coui.kmp.layout.GuideDialogPage
import io.github.suqi8.coui.kmp.overlay.OverlayGuideDialog
import io.github.suqi8.coui.kmp.overlay.OverlayNavigationPopupMenu
import io.github.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.editToolbarAndGuideSection() {
    item(key = "floatingEditToolbar") {
        SmallTitle(text = "FloatingEditToolbar")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            EditToolbarDemo()
        }
    }
    item(key = "guideDialog") {
        SmallTitle(text = "GuideDialog")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            GuideDialogDemoContent()
        }
    }
    item(key = "tabWithSearchLayout") {
        SmallTitle(text = "TabWithSearchLayout")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            TabWithSearchDemo()
        }
    }
    item(key = "navigationPopupMenu") {
        SmallTitle(text = "NavigationPopupMenu")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            NavigationPopupMenuDemoContent()
        }
    }
}

@Composable
private fun EditToolbarDemo() {
    var visible by remember { mutableStateOf(true) }
    var itemCount by remember { mutableIntStateOf(4) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(
                text = if (visible) "Hide" else "Show",
                onClick = { visible = !visible },
                minWidth = 0.dp,
            )
            TextButton(
                text = "Items: $itemCount",
                onClick = { itemCount = itemCount % 4 + 1 },
                minWidth = 0.dp,
            )
        }
        // Compact: icons over labels.
        EditToolbarSample(visible = visible, itemCount = itemCount, modifier = Modifier.fillMaxWidth())
        Text(
            text = "From 600dp wide: icons beside labels",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = COUITheme.textStyles.bodyXS,
            color = COUITheme.colorScheme.labelSecondaryVariant,
        )
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            EditToolbarSample(visible = visible, itemCount = itemCount, modifier = Modifier.width(720.dp))
        }
    }
}

@Composable
private fun EditToolbarSample(
    visible: Boolean,
    itemCount: Int,
    modifier: Modifier = Modifier,
) {
    FloatingEditToolbar(
        modifier = modifier,
        visible = visible,
        defaultWindowInsetsPadding = false,
    ) {
        FloatingEditToolbarItem(onClick = {}, icon = COUIIcons.Share, label = "Share")
        if (itemCount >= 2) FloatingEditToolbarItem(onClick = {}, icon = COUIIcons.Copy, label = "Copy", badge = 0)
        if (itemCount >= 3) FloatingEditToolbarItem(onClick = {}, icon = COUIIcons.Favorites, label = "Favorite", enabled = false)
        if (itemCount >= 4) FloatingEditToolbarItem(onClick = {}, icon = COUIIcons.Delete, label = "Delete", badge = 3)
    }
}

@Composable
private fun GuideDialogDemoContent() {
    var showGuide by remember { mutableStateOf(false) }
    var showSingle by remember { mutableStateOf(false) }
    val primary = COUITheme.colorScheme.primary
    val pages = remember(primary) {
        listOf(
            GuideDialogPage(
                title = "Swipe to switch",
                description = "Swipe left or right on the bottom bar to switch between recent apps.",
                image = ColorPainter(primary.copy(alpha = 0.25f)),
            ),
            GuideDialogPage(
                title = "Quick reply",
                description = "Pull down a notification to reply without leaving the current app.",
                image = ColorPainter(primary.copy(alpha = 0.4f)),
            ),
            GuideDialogPage(
                title = "All set",
                description = "You can find these tips again in Settings.",
                image = ColorPainter(primary.copy(alpha = 0.55f)),
            ),
        )
    }
    val singlePage = remember(primary) {
        listOf(
            GuideDialogPage(
                title = "New look",
                description = "The toolbar now floats above your content.",
                image = ColorPainter(primary.copy(alpha = 0.3f)),
            ),
        )
    }
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextButton(text = "3 pages", onClick = { showGuide = true }, minWidth = 0.dp)
        TextButton(text = "Single page", onClick = { showSingle = true }, minWidth = 0.dp)
    }
    OverlayGuideDialog(
        show = showGuide,
        pages = pages,
        onStart = { showGuide = false },
        onDismissRequest = { showGuide = false },
        imageHeight = 240.dp,
    )
    OverlayGuideDialog(
        show = showSingle,
        pages = singlePage,
        onStart = { showSingle = false },
        onDismissRequest = { showSingle = false },
        imageHeight = 240.dp,
    )
}

@Composable
private fun TabWithSearchDemo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Compact: the search bar above the tabs.
        TabWithSearchSample(modifier = Modifier.fillMaxWidth())
        Text(
            text = "From 600dp wide: tabs and search bar side by side",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = COUITheme.textStyles.bodyXS,
            color = COUITheme.colorScheme.labelSecondaryVariant,
        )
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            TabWithSearchSample(modifier = Modifier.width(720.dp))
        }
    }
}

@Composable
private fun TabWithSearchSample(modifier: Modifier = Modifier) {
    val tabs = remember { listOf("All", "Photos", "Videos", "Albums") }
    var selected by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    TabWithSearchLayout(
        tabs = {
            TabRow(
                tabs = tabs,
                selectedTabIndex = selected,
                onTabSelected = { selected = it },
            )
        },
        modifier = modifier,
    ) {
        SearchBar(
            inputField = {
                InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    label = "Search",
                )
            },
            onExpandedChange = {},
            insideMargin = TabWithSearchLayoutDefaults.SearchBarInsideMargin,
        ) {}
    }
}

@Composable
private fun NavigationPopupMenuDemoContent() {
    var selected by remember { mutableIntStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    val destinations = remember { listOf("Home", "Photos", "Music", "Settings") }
    val icons = remember { listOf(COUIIcons.Home, COUIIcons.Photos, COUIIcons.Music, COUIIcons.Settings) }
    val menuItems = destinations.indices.drop(2).map { index ->
        DropdownItem(
            text = destinations[index],
            selected = selected == index,
            onClick = { selected = index },
            icon = { iconModifier -> Icon(imageVector = icons[index], contentDescription = null, modifier = iconModifier) },
        )
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(16.dp),
            contentAlignment = Alignment.TopStart,
        ) {
            Text(text = "Current: ${destinations[selected]}. Tap More for the other destinations.")
        }
        // The Box around the navigation bar is the menu's anchor.
        Box {
            NavigationBar(defaultWindowInsetsPadding = false) {
                NavigationBarItem(
                    selected = selected == 0,
                    onClick = { selected = 0 },
                    icon = icons[0],
                    label = destinations[0],
                )
                NavigationBarItem(
                    selected = selected == 1,
                    onClick = { selected = 1 },
                    icon = icons[1],
                    label = destinations[1],
                )
                NavigationBarItem(
                    selected = selected >= 2,
                    onClick = { showMenu = true },
                    icon = COUIIcons.More,
                    label = "More",
                )
            }
            OverlayNavigationPopupMenu(
                show = showMenu,
                items = menuItems,
                onDismissRequest = { showMenu = false },
            )
        }
    }
}
