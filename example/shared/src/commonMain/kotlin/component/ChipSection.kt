// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Chip
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.TopTips
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Check
import io.github.suqi8.coui.kmp.icon.basic.Search

fun LazyListScope.chipSection() {
    item(key = "chip") {
        SmallTitle(text = "Chip")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            MultiSelectChipsDemo()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SingleSelectChipsDemo()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            DisabledChipsDemo()
        }
    }
    item(key = "topTips") {
        SmallTitle(text = "TopTips")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            TopTipsDemo()
        }
    }
}

@Composable
private fun MultiSelectChipsDemo() {
    var recommended by remember { mutableStateOf(true) }
    var nearby by remember { mutableStateOf(false) }
    var popular by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Chip(
            selected = recommended,
            onClick = { recommended = !recommended },
            label = "Recommended",
            icon = {
                Icon(
                    imageVector = COUIIcons.Basic.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            },
        )
        Chip(
            selected = nearby,
            onClick = { nearby = !nearby },
            label = "Nearby",
        )
        Chip(
            selected = popular,
            onClick = { popular = !popular },
            label = "Popular",
        )
    }
}

@Composable
private fun SingleSelectChipsDemo() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val labels = remember { listOf("All", "Photos", "Videos", "Documents") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEachIndexed { index, label ->
            Chip(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                label = label,
            )
        }
    }
}

@Composable
private fun DisabledChipsDemo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Chip(
            selected = false,
            onClick = {},
            label = "Disabled",
            enabled = false,
        )
        Chip(
            selected = true,
            onClick = {},
            label = "Disabled Selected",
            enabled = false,
        )
    }
}

@Composable
private fun TopTipsDemo() {
    var closableVisible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TopTips(
            text = "Closable tips with a start icon",
            visible = closableVisible,
            startIcon = {
                Icon(
                    imageVector = COUIIcons.Basic.Search,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            },
            onClose = { closableVisible = false },
            modifier = Modifier.fillMaxWidth(),
        )
        TopTips(
            text = "New security update available",
            actionText = "Update",
            onAction = {},
            modifier = Modifier.fillMaxWidth(),
        )
        TopTips(
            text = "This longer message shows how the action label drops to its own row when the text would collide with it",
            actionText = "Turn on",
            onAction = {},
            modifier = Modifier.fillMaxWidth(),
        )
        if (!closableVisible) {
            TextButton(
                text = "Show closable tips again",
                onClick = { closableVisible = true },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
