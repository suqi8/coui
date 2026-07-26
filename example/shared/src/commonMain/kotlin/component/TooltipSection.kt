// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.IconButton
import com.suqi8.coui.kmp.basic.RichTooltipBox
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.basic.TooltipBox
import com.suqi8.coui.kmp.basic.rememberTooltipState
import com.suqi8.coui.kmp.icon.COUIIcons
import com.suqi8.coui.kmp.icon.extended.Edit
import com.suqi8.coui.kmp.icon.extended.Info

fun LazyListScope.tooltipSection() {
    item(key = "tooltip") {
        SmallTitle(text = "Tooltip")
        val richState = rememberTooltipState(isPersistent = true)
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    TooltipBox(text = "Edit") {
                        IconButton(onClick = {}) {
                            Icon(imageVector = COUIIcons.Edit, contentDescription = "Edit")
                        }
                    }
                    RichTooltipBox(
                        title = "Rich tooltip",
                        text = "Rich tooltips show a title, supporting text, and an optional action. " +
                            "Move onto the tooltip to use the action, or tap outside to dismiss.",
                        actionText = "Got it",
                        onActionClick = {},
                        state = richState,
                    ) {
                        IconButton(onClick = {}) {
                            Icon(imageVector = COUIIcons.Info, contentDescription = "Rich tooltip")
                        }
                    }
                }
            }
        }
    }
}
