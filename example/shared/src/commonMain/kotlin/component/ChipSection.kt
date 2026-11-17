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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Chip
import io.github.suqi8.coui.kmp.basic.ChipGroup
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconChip
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.SuggestionChip
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.TopTips
import io.github.suqi8.coui.kmp.basic.rememberChipGroupState
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
            ChipExtrasDemo()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SuggestionChipsDemo()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            DisabledChipsDemo()
        }
    }
    item(key = "chipGroup") {
        SmallTitle(text = "ChipGroup")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            SingleSelectChipsDemo()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            CollapsableChipGroupDemo()
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
private fun ChipExtrasDemo() {
    var downloads by remember { mutableStateOf(false) }
    var closableVisible by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Chip(
            selected = downloads,
            onClick = { downloads = !downloads },
            label = "Downloads",
            count = "12",
        )
        Chip(
            selected = false,
            onClick = {},
            label = "Updates",
            showRedDot = true,
        )
        if (closableVisible) {
            Chip(
                selected = false,
                onClick = {},
                label = "Closable",
                onClose = { closableVisible = false },
            )
        }
    }
}

@Composable
private fun SuggestionChipsDemo() {
    val records = remember { mutableStateListOf("Wallpaper", "Dark mode", "Battery", "Bluetooth") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        IconChip(onClick = { records.clear() }) {
            Icon(
                imageVector = COUIIcons.Basic.Search,
                contentDescription = "Clear",
            )
        }
        records.forEach { record ->
            SuggestionChip(
                onClick = {},
                label = record,
                onClose = { records.remove(record) },
            )
        }
    }
}

@Composable
private fun SingleSelectChipsDemo() {
    val state = rememberChipGroupState(singleSelection = true, selectionRequired = true, initialSelection = setOf(0))
    val labels = remember { listOf("All", "Photos", "Videos", "Documents") }

    ChipGroup(
        modifier = Modifier.padding(16.dp),
        state = state,
    ) {
        labels.forEachIndexed { index, label ->
            Chip(
                selected = state.isSelected(index),
                onClick = { state.toggle(index) },
                label = label,
            )
        }
    }
}

@Composable
private fun CollapsableChipGroupDemo() {
    val state = rememberChipGroupState(initialCollapsed = true)
    val labels = remember {
        listOf(
            "Wi-Fi", "Bluetooth", "Mobile network", "Battery", "Display", "Sound",
            "Notifications", "Privacy", "Security", "Location", "Apps", "Storage",
            "Accessibility", "Wallpaper", "Lock screen", "Family space", "About phone",
        )
    }

    ChipGroup(
        modifier = Modifier.padding(16.dp),
        state = state,
        collapsable = true,
        collapsedMaxRows = 2,
    ) {
        labels.forEachIndexed { index, label ->
            Chip(
                selected = state.isSelected(index),
                onClick = { state.toggle(index) },
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
            text = "With a start icon, a message longer than two lines moves the action below it, aligned with the text",
            startIcon = {
                Icon(
                    imageVector = COUIIcons.Basic.Search,
                    contentDescription = null,
                )
            },
            actionText = "Turn on",
            onAction = {},
            onClose = {},
            modifier = Modifier.fillMaxWidth(),
        )
        TopTips(
            title = "Backup paused",
            text = "Connect to Wi-Fi to continue backing up your photos",
            startIcon = {
                Icon(
                    imageVector = COUIIcons.Basic.Search,
                    contentDescription = null,
                )
            },
            actionText = "Retry",
            onAction = {},
            onClose = {},
            modifier = Modifier.fillMaxWidth(),
        )
        TopTips(
            text = "Marquee tips scroll a message that is too long to fit on a single line",
            marquee = true,
            onClose = {},
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
