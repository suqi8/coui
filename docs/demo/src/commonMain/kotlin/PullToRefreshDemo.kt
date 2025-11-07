// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.BasicComponent
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.PullToRefresh
import com.suqi8.coui.kmp.basic.rememberPullToRefreshState
import com.suqi8.coui.kmp.utils.getWindowSize
import com.suqi8.coui.kmp.utils.overScrollVertical
import kotlinx.coroutines.delay

@Composable
fun PullToRefreshDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xfff77062), Color(0xfffe5196)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pullToRefreshState = rememberPullToRefreshState()
            var isRefreshing by rememberSaveable { mutableStateOf(false) }
            var ii by remember { mutableStateOf(1) }

            LaunchedEffect(isRefreshing) {
                if (isRefreshing) {
                    delay(500)
                    ii += 6
                    isRefreshing = false
                }
            }

            PullToRefresh(
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                pullToRefreshState = pullToRefreshState,
            ) {
                LazyColumn(
                    modifier = Modifier
                        .overScrollVertical()
                        .height(getWindowSize().height.dp),
                    overscrollEffect = null
                ) {
                    item {
                        Card {
                            for (i in 0 until ii) {
                                BasicComponent(
                                    title = "Component $i"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
