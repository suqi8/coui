// Copyright 2026, compose-coui-ui contributors
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Banner
import io.github.suqi8.coui.kmp.basic.Text

@Composable
fun BannerDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(vertical = 16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Demo artwork only: solid colors stand in for the banner images.
            val colors = listOf(Color(0xFF3D7BF7), Color(0xFFE5793B), Color(0xFF2FA572), Color(0xFF8A56E2))
            Banner(
                pageCount = colors.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
            ) { page ->
                BannerDemoPage(color = colors[page], title = "Banner ${page + 1}")
            }
            Banner(
                pageCount = colors.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                autoLoop = false,
                pageMargin = 8.dp,
                startItemWidth = 16.dp,
                endItemWidth = 16.dp,
            ) { page ->
                BannerDemoPage(color = colors[page], title = "Card ${page + 1}")
            }
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
