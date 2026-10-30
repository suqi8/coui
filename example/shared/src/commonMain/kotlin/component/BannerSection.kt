// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Banner
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text

fun LazyListScope.bannerSection() {
    item(key = "banner") {
        SmallTitle(text = "Banner")
        // Demo artwork only: solid colors stand in for the banner images.
        val pages = remember {
            listOf(
                Color(0xFF3D7BF7) to "Summer themes",
                Color(0xFFE5793B) to "New wallpapers",
                Color(0xFF2FA572) to "Weekly picks",
                Color(0xFF8A56E2) to "Live fonts",
            )
        }
        Banner(
            pageCount = pages.size,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .height(160.dp),
        ) { page ->
            BannerDemoPage(color = pages[page].first, title = pages[page].second)
        }
        // Neighbouring cards peeking in, without the auto loop.
        Banner(
            pageCount = pages.size,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .height(140.dp),
            autoLoop = false,
            pageMargin = 8.dp,
            startItemWidth = 16.dp,
            endItemWidth = 16.dp,
        ) { page ->
            BannerDemoPage(color = pages[page].first, title = pages[page].second)
        }
    }
}

@Composable
private fun BannerDemoPage(color: Color, title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = title, color = Color.White)
    }
}
