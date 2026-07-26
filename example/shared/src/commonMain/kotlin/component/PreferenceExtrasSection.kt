// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.HorizontalDivider
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.preference.ButtonPreference
import com.suqi8.coui.kmp.preference.MarkPreference
import com.suqi8.coui.kmp.preference.RecommendedItem
import com.suqi8.coui.kmp.preference.RecommendedPreference
import com.suqi8.coui.kmp.preference.SwitchLoadingPreference
import kotlinx.coroutines.delay

fun LazyListScope.preferenceExtrasSection() {
    item(key = "preferenceExtras") {
        // SwitchLoadingPreference: simulate an asynchronous toggle that resolves after a delay.
        val loadingChecked = remember { mutableStateOf(false) }
        val loadingTarget = remember { mutableStateOf(false) }
        val isLoading = remember { mutableStateOf(false) }
        LaunchedEffect(isLoading.value) {
            if (isLoading.value) {
                delay(1500)
                loadingChecked.value = loadingTarget.value
                isLoading.value = false
            }
        }

        SmallTitle(text = "SwitchLoadingPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            SwitchLoadingPreference(
                checked = loadingChecked.value,
                onCheckedChange = {
                    loadingTarget.value = it
                    isLoading.value = true
                },
                title = "Async Switch",
                summary = "Applies the change after a short delay",
                isLoading = isLoading.value,
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SwitchLoadingPreference(
                checked = true,
                onCheckedChange = {},
                title = "Always Loading",
                summary = "A switch stuck in the loading state",
                isLoading = true,
            )
        }

        // MarkPreference: a single-select group where the mark follows the checked row.
        val selectedMark = remember { mutableIntStateOf(0) }
        val markOptions = remember { listOf("Every day", "Workdays only", "Never") }

        SmallTitle(text = "MarkPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            markOptions.forEachIndexed { index, option ->
                if (index > 0) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                MarkPreference(
                    title = option,
                    checked = selectedMark.intValue == index,
                    onClick = { selectedMark.intValue = index },
                )
            }
        }

        // ButtonPreference: an inline small button whose click is independent from the row.
        val signInCount = remember { mutableIntStateOf(0) }

        SmallTitle(text = "ButtonPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            ButtonPreference(
                title = "Account",
                summary = if (signInCount.intValue > 0) {
                    "Button clicked ${signInCount.intValue} time(s)"
                } else {
                    "Sign in to sync your data"
                },
                buttonText = "Sign in",
                onButtonClick = { signInCount.intValue++ },
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            ButtonPreference(
                title = "Disabled Row",
                summary = "Button and row are disabled",
                buttonText = "Action",
                onButtonClick = {},
                enabled = false,
            )
        }

        // RecommendedPreference: a standalone rounded card with related settings entries.
        val recommendedItems = remember {
            listOf(
                RecommendedItem(text = "Display & brightness", onClick = {}),
                RecommendedItem(text = "Wallpapers & style", onClick = {}),
                RecommendedItem(text = "Battery", onClick = {}),
            )
        }

        SmallTitle(text = "RecommendedPreference")
        RecommendedPreference(
            title = "You might be looking for:",
            items = recommendedItems,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        )
    }
}
