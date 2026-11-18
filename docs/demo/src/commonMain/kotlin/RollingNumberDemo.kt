// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.basic.RollingNumber
import io.github.suqi8.coui.kmp.basic.RollingNumberDefaults
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay

@Composable
fun RollingNumberDemo() {
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
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var value by remember { mutableIntStateOf(8) }
            RollingNumber(
                value = value,
                rollOnesDigit = true,
                style = COUITheme.textStyles.bodyXL.copy(fontSize = 40.sp, fontWeight = FontWeight.Medium),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(text = "-10", onClick = { value -= 10 })
                TextButton(text = "-1", onClick = { value -= 1 })
                TextButton(text = "+1", onClick = { value += 1 })
                TextButton(text = "+10", onClick = { value += 10 })
            }
            var shots by remember { mutableIntStateOf(1) }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(120)
                    shots = if (shots >= 99) 1 else shots + 1
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    // A dark stand-in for the camera viewfinder the burst counter sits on.
                    .background(Brush.verticalGradient(listOf(Color(0xFF5A4636), Color(0xFF1E1A17))))
                    .padding(horizontal = 48.dp, vertical = 24.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(RollingNumberDefaults.BurstBadgeSize)
                        .background(Color(0x7F000000), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    RollingNumber(
                        value = shots,
                        minDigits = 2,
                        color = Color.White,
                        style = COUITheme.textStyles.bodyXL.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        rollDistance = RollingNumberDefaults.BurstRollDistance,
                    )
                }
            }
        }
    }
}
