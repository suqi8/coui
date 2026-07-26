// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.suqi8.coui.kmp.basic.Badge
import com.suqi8.coui.kmp.basic.BadgeBox
import com.suqi8.coui.kmp.basic.BadgeDefaults
import com.suqi8.coui.kmp.basic.ButtonDefaults
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.LoadingButton
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.basic.Stepper
import com.suqi8.coui.kmp.icon.COUIIcons
import com.suqi8.coui.kmp.icon.extended.Settings

fun LazyListScope.badgeSection() {
    item(key = "badge") {
        var dotsLoading by remember { mutableStateOf(false) }
        var textLoading by remember { mutableStateOf(false) }
        LaunchedEffect(dotsLoading) {
            if (dotsLoading) {
                delay(3000)
                dotsLoading = false
            }
        }
        LaunchedEffect(textLoading) {
            if (textLoading) {
                delay(3000)
                textLoading = false
            }
        }

        SmallTitle(text = "Loading Button")
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            LoadingButton(
                text = "Download",
                onClick = { dotsLoading = true },
                modifier = Modifier.weight(1f),
                isLoading = dotsLoading,
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
            Spacer(Modifier.width(12.dp))
            LoadingButton(
                text = "Sign in",
                onClick = { textLoading = true },
                modifier = Modifier.weight(1f),
                isLoading = textLoading,
                loadingText = "Signing in",
            )
        }

        SmallTitle(text = "Badge")
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Badge()
            Badge(count = 5)
            Badge(count = 66)
            Badge(count = 666)
            Badge(count = 1000)
            Badge(stroke = true)
            Badge(count = 99, stroke = true)
        }
        var badgeCount by remember { mutableIntStateOf(8) }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                BadgeBox(badge = { Badge() }) {
                    Icon(
                        imageVector = COUIIcons.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(32.dp),
                    )
                }
                BadgeBox(
                    badge = { Badge(count = badgeCount) },
                    overhang = BadgeDefaults.CountOverhang,
                ) {
                    Icon(
                        imageVector = COUIIcons.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
            Stepper(
                value = badgeCount,
                onValueChange = { badgeCount = it },
                minValue = 1,
                maxValue = 1000,
                step = 111,
            )
        }
    }
}
