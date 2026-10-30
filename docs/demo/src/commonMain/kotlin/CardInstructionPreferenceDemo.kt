// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.preference.CardInstructionIllustration
import io.github.suqi8.coui.kmp.preference.CardInstructionPage
import io.github.suqi8.coui.kmp.preference.CardInstructionPreference
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun CardInstructionPreferenceDemo() {
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
            val primary = COUITheme.colorScheme.primary
            val pages = remember(primary) {
                listOf(
                    CardInstructionPage(
                        illustrations = listOf(
                            CardInstructionIllustration(
                                content = { Box(Modifier.size(96.dp).background(primary, CircleShape)) },
                            ),
                        ),
                        title = "Swipe up from the bottom",
                        summary = "Swipe up from the bottom edge of the screen to go home.",
                    ),
                    CardInstructionPage(
                        illustrations = listOf(
                            CardInstructionIllustration(
                                content = { Box(Modifier.size(96.dp).background(primary, RoundedCornerShape(16.dp))) },
                                title = "Left edge",
                            ),
                            CardInstructionIllustration(
                                content = { Box(Modifier.size(96.dp).background(primary.copy(alpha = 0.6f), RoundedCornerShape(16.dp))) },
                                title = "Right edge",
                            ),
                        ),
                        title = "Swipe in from either side",
                        summary = "Swipe in from the left or right edge to go back.",
                    ),
                )
            }
            CardInstructionPreference(pages = pages)
        }
    }
}
