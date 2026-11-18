// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.ExposureBar
import io.github.suqi8.coui.kmp.basic.ExposureBarDefaults
import io.github.suqi8.coui.kmp.basic.ScaleBar

@Composable
fun ExposureBarDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                // A dark stand-in for the camera viewfinder the panel sits on.
                .background(Brush.verticalGradient(listOf(Color(0xFF5A4636), Color(0xFF1E1A17))))
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var ev by remember { mutableIntStateOf(ExposureBarDefaults.Values.size / 2) }
            ExposureBar(
                selectedIndex = ev,
                onSelectedIndexChange = { ev = it },
            )
            var iso by remember { mutableIntStateOf(3) }
            val isoValues = remember { listOf("50", "64", "80", "100", "125", "160", "200", "250", "320", "400", "500", "640", "800") }
            ScaleBar(
                labels = isoValues,
                selectedIndex = iso,
                onSelectedIndexChange = { iso = it },
                valueSuffix = " ISO",
            )
        }
    }
}
