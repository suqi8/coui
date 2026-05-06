// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(ExperimentalScrollBarApi::class)

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.DropdownEntry
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.VerticalScrollBar
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.basic.rememberScrollBarAdapter
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.MoreCircle
import top.yukonga.miuix.kmp.icon.extended.SelectAll
import top.yukonga.miuix.kmp.icon.extended.Sort
import top.yukonga.miuix.kmp.interfaces.ExperimentalScrollBarApi
import top.yukonga.miuix.kmp.menu.OverlayIconDropdownMenu
import top.yukonga.miuix.kmp.menu.WindowIconDropdownMenu
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import utils.AdaptiveTopAppBar
import utils.BlurredBar
import utils.pageContentPadding
import utils.pageScrollModifiers
import utils.rememberBlurBackdrop

private val DropdownListTopShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
private val DropdownListBottomShape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)

@Composable
fun PopupPage(
    snackbarHostState: SnackbarHostState,
    padding: PaddingValues,
) {
    val appState = LocalAppState.current
    val isWideScreen = LocalIsWideScreen.current
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()
    val topAppBarScrollBehavior = MiuixScrollBehavior()

    val backdrop = rememberBlurBackdrop()
    val blurActive = backdrop != null
    val barColor = if (blurActive) Color.Transparent else MiuixTheme.colorScheme.surface

    val dropdownOptions = remember { listOf("Option 1", "Option 2", "Option 3", "Option 4") }
    var dropdownSelectedOption by remember { mutableIntStateOf(0) }
    var dropdownCount by remember { mutableIntStateOf(6) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(200)
            dropdownCount += 6
            isRefreshing = false
        }
    }

    val coroutineScope = rememberCoroutineScope()

    fun snackbar(msg: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(msg)
        }
    }

    val menuItems = remember(snackbarHostState) {
        listOf(
            DropdownEntry(
                items = listOf("Item A-1", "Item A-2")
                    .map {
                        DropdownItem(
                            text = it,
                            onClick = { snackbar("$it clicked") },
                        )
                    },
            ),
            DropdownEntry(
                items = listOf("Item B-1", "Item B-2", "Item B-3")
                    .map {
                        DropdownItem(
                            text = it,
                            onClick = { snackbar("$it clicked") },
                        )
                    },
            ),
            DropdownEntry(
                items = listOf("Item C-1", "Item C-2", "Item C-3", "Item C-4")
                    .mapIndexed { index, string ->
                        DropdownItem(
                            text = string,
                            onClick = { snackbar("$string clicked") },
                            enabled = index % 2 == 0,
                        )
                    },
            ),
        )
    }
    var selectedIndex1 by remember { mutableIntStateOf(0) }
    var selectedIndex2 by remember { mutableIntStateOf(1) }
    var selectedIndex3 by remember { mutableIntStateOf(2) }
    val optionItems = remember(
        selectedIndex1,
        selectedIndex2,
        selectedIndex3,
    ) {
        listOf(
            DropdownEntry(
                items = listOf("Selection A-1", "Selection A-2")
                    .mapIndexed { index, text ->
                        DropdownItem(
                            text = text,
                            selected = selectedIndex1 == index,
                            onClick = { selectedIndex1 = index },
                        )
                    },
            ),
            DropdownEntry(
                items = listOf("Selection B-1", "Selection B-2", "Selection B-3")
                    .mapIndexed { index, text ->
                        DropdownItem(
                            text = text,
                            selected = selectedIndex2 == index,
                            onClick = { selectedIndex2 = index },
                        )
                    },
            ),
            DropdownEntry(
                items = listOf("Selection C-1", "Selection C-2", "Selection C-3", "Selection C-4")
                    .mapIndexed { index, text ->
                        DropdownItem(
                            text = text,
                            selected = selectedIndex3 == index,
                            onClick = { selectedIndex3 = index },
                        )
                    },
            ),
        )
    }
    var multiSelectedItems by remember {
        mutableStateOf(
            setOf(
                "Multi selection A-1",
                "Multi selection B-2",
                "Multi selection B-3",
            ),
        )
    }
    val multiSelectItems = remember(multiSelectedItems) {
        listOf(
            DropdownEntry(
                items = listOf(
                    "Multi selection A-1",
                    "Multi selection A-2",
                ).map { text ->
                    DropdownItem(
                        text = text,
                        selected = text in multiSelectedItems,
                        onClick = {
                            multiSelectedItems =
                                if (text in multiSelectedItems) {
                                    multiSelectedItems - text
                                } else {
                                    multiSelectedItems + text
                                }
                        },
                    )
                },
            ),
            DropdownEntry(
                items = listOf(
                    "Multi selection B-1",
                    "Multi selection B-2",
                    "Multi selection B-3",
                ).map { text ->
                    DropdownItem(
                        text = text,
                        selected = text in multiSelectedItems,
                        onClick = {
                            multiSelectedItems =
                                if (text in multiSelectedItems) {
                                    multiSelectedItems - text
                                } else {
                                    multiSelectedItems + text
                                }
                        },
                    )
                },
            ),
        )
    }

    Scaffold(
        topBar = {
            BlurredBar(backdrop, blurActive) {
                AdaptiveTopAppBar(
                    title = "Popup",
                    showTopAppBar = appState.showTopAppBar,
                    isWideScreen = isWideScreen,
                    scrollBehavior = topAppBarScrollBehavior,
                    color = barColor,
                    actions = {
                        OverlayIconDropdownMenu(
                            entries = optionItems,
                            collapseOnSelection = false,
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Sort,
                                contentDescription = "Sort",
                            )
                        }
                        OverlayIconDropdownMenu(
                            entries = multiSelectItems,
                            collapseOnSelection = false,
                        ) {
                            Icon(
                                imageVector = MiuixIcons.SelectAll,
                                contentDescription = "Multiple selection",
                            )
                        }
                        WindowIconDropdownMenu(
                            entries = menuItems,
                            collapseOnSelection = true,
                        ) {
                            Icon(
                                imageVector = MiuixIcons.MoreCircle,
                                contentDescription = "More",
                            )
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier) {
            PullToRefresh(
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                pullToRefreshState = pullToRefreshState,
                topAppBarScrollBehavior = if (appState.showTopAppBar) topAppBarScrollBehavior else null,
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 12.dp,
                    bottom = if (isWideScreen) {
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    } else {
                        0.dp
                    },
                ),
            ) {
                val lazyListState = rememberLazyListState()
                val contentPadding = pageContentPadding(innerPadding, padding, isWideScreen, extraTop = 12.dp)
                Box {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.pageScrollModifiers(
                            appState.enableScrollEndHaptic,
                            appState.showTopAppBar,
                            topAppBarScrollBehavior,
                        ),
                        contentPadding = contentPadding,
                    ) {
                        items(
                            count = dropdownCount,
                            key = { "dropdown_$it" },
                        ) { i ->
                            val isFirst = i == 0
                            val isLast = i == dropdownCount - 1
                            val shape = when {
                                isFirst -> DropdownListTopShape
                                isLast -> DropdownListBottomShape
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .clip(shape)
                                    .background(MiuixTheme.colorScheme.surfaceContainer),
                            ) {
                                if (i % 2 == 0) {
                                    OverlayDropdownPreference(
                                        title = "OverlayDropdownPref ${i + 1}",
                                        items = dropdownOptions,
                                        selectedIndex = dropdownSelectedOption,
                                        onSelectedIndexChange = { newOption ->
                                            dropdownSelectedOption = newOption
                                        },
                                    )
                                } else {
                                    WindowDropdownPreference(
                                        title = "WindowDropdownPref ${i + 1}",
                                        items = dropdownOptions,
                                        selectedIndex = dropdownSelectedOption,
                                        onSelectedIndexChange = { newOption ->
                                            dropdownSelectedOption = newOption
                                        },
                                    )
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(12.dp)) }
                    }
                    VerticalScrollBar(
                        adapter = rememberScrollBarAdapter(lazyListState),
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        trackPadding = contentPadding,
                    )
                }
            }
        }
    }
}
