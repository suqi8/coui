// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.CouiListItemPosition
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.PullToRefresh
import com.suqi8.coui.kmp.basic.ScrollBehavior
import com.suqi8.coui.kmp.basic.rememberPullToRefreshState
import com.suqi8.coui.kmp.extra.SuperDropdown
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.useful.Scan
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.utils.getWindowSize
import com.suqi8.coui.kmp.utils.overScrollVertical
import com.suqi8.coui.kmp.utils.scrollEndHaptic
import kotlinx.coroutines.delay

@Composable
fun SecondPage(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    scrollEndHaptic: Boolean
) {
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    val dropdownOptions = remember { listOf("Option 1", "Option 2", "Option 3", "Option 4") }
    var dropdownSelectedOption by remember { mutableStateOf(0) }
    var ii by remember { mutableStateOf(6) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(500)
            ii += 6
            isRefreshing = false
        }
    }

    val windowSize = getWindowSize()

    PullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = { isRefreshing = true },
        pullToRefreshState = pullToRefreshState,
        topAppBarScrollBehavior = topAppBarScrollBehavior,
        contentPadding = PaddingValues(top = padding.calculateTopPadding() + 12.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .height(windowSize.height.dp)
                .overScrollVertical()
                .then(
                    if (scrollEndHaptic) Modifier.scrollEndHaptic() else Modifier
                ),
            contentPadding = PaddingValues(top = padding.calculateTopPadding() + 12.dp),
            overscrollEffect = null
        ) {
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp),
                ) {
                    for (i in 0 until ii) {
                        key(i) {
                            SuperDropdown(
                                title = "Dropdown ${i + 1}",
                                items = dropdownOptions,
                                selectedIndex = dropdownSelectedOption,
                                onSelectedIndexChange = { newOption ->
                                    dropdownSelectedOption = newOption
                                },
                                position = when (i) {
                                    0 -> CouiListItemPosition.Top      // 第一个
                                    ii - 1 -> CouiListItemPosition.Bottom // 最后一个
                                    else -> CouiListItemPosition.Middle  // 中间
                                },
                                leftAction = {
                                    Box(
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = MiuixIcons.Useful.Scan,
                                            contentDescription = "Share",
                                            tint = COUITheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(padding.calculateBottomPadding()))
            }
        }
    }
}
