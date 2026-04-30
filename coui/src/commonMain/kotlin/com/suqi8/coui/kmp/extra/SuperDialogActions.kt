// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.extra

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mocharealm.gaze.capsule.ContinuousRoundedRectangle
import com.suqi8.coui.kmp.basic.HorizontalDivider
import com.suqi8.coui.kmp.basic.Surface
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.theme.COUITheme

@Immutable
enum class SuperDialogActionTone {
    Primary,
    Neutral,
    Destructive,
}

@Immutable
enum class SuperDialogActionStyle {
    Default,
    Recommend,
}

@Immutable
enum class SuperDialogActionsLayout {
    Horizontal,
    Vertical,
    CompactVertical,
}

@Immutable
data class SuperDialogAction(
    val text: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val tone: SuperDialogActionTone = SuperDialogActionTone.Primary,
    val style: SuperDialogActionStyle = SuperDialogActionStyle.Default,
)

@Composable
fun SuperDialogActions(
    actions: List<SuperDialogAction>,
    modifier: Modifier = Modifier,
    layout: SuperDialogActionsLayout = SuperDialogActionsLayout.Horizontal,
    dividerColor: Color = COUITheme.colorScheme.dividerLine,
) {
    if (actions.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = dividerColor, thickness = 0.33.dp)
        when (layout) {
            SuperDialogActionsLayout.Horizontal -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 58.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    actions.forEachIndexed { index, action ->
                        SuperDialogHorizontalActionItem(
                            action = action,
                            modifier = Modifier.weight(1f),
                        )
                        if (index != actions.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(0.75.dp)
                                    .height(20.dp)
                                    .background(dividerColor)
                            )
                        }
                    }
                }
            }

            SuperDialogActionsLayout.Vertical -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    actions.forEach { action ->
                        SuperDialogVerticalActionItem(action = action)
                    }
                }
            }

            SuperDialogActionsLayout.CompactVertical -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    actions.forEach { action ->
                        SuperDialogVerticalActionItem(
                            action = action,
                            compact = true,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuperDialogHorizontalActionItem(
    action: SuperDialogAction,
    modifier: Modifier = Modifier,
) {
    val baseContentColor = action.baseContentColor()
    val contentColor = if (action.enabled) baseContentColor else baseContentColor.copy(alpha = 0.4f)

    Surface(
        onClick = action.onClick,
        enabled = action.enabled,
        modifier = modifier,
        color = Color.Transparent,
        contentColor = contentColor,
        shape = ContinuousRoundedRectangle(18.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = action.text,
                color = contentColor,
                fontSize = COUITheme.textStyles.body1.fontSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun SuperDialogVerticalActionItem(
    action: SuperDialogAction,
    compact: Boolean = false,
) {
    val isRecommend = action.style == SuperDialogActionStyle.Recommend
    val baseContentColor = if (isRecommend) {
        COUITheme.colorScheme.onPrimary
    } else {
        action.baseContentColor()
    }
    val contentColor = if (action.enabled) baseContentColor else baseContentColor.copy(alpha = 0.4f)
    val containerColor = if (isRecommend) {
        if (action.enabled) COUITheme.colorScheme.primary else COUITheme.colorScheme.primary.copy(alpha = 0.5f)
    } else {
        Color.Transparent
    }
    val shape = when {
        isRecommend -> 22.dp
        compact -> 20.dp
        else -> 18.dp
    }
    val minHeight = when {
        isRecommend -> 44.dp
        compact -> 40.dp
        else -> 58.dp
    }
    val horizontalPadding = if (isRecommend) 16.dp else 12.dp
    val verticalPadding = when {
        isRecommend -> 10.dp
        compact -> 10.dp
        else -> 16.dp
    }

    Surface(
        onClick = action.onClick,
        enabled = action.enabled,
        modifier = Modifier.fillMaxWidth(),
        color = containerColor,
        contentColor = contentColor,
        shape = ContinuousRoundedRectangle(shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = minHeight)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = action.text,
                color = contentColor,
                fontSize = COUITheme.textStyles.body1.fontSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = if (isRecommend || compact) 1 else 2,
            )
        }
    }
}

@Composable
private fun SuperDialogAction.baseContentColor(): Color = when (tone) {
    SuperDialogActionTone.Primary -> COUITheme.colorScheme.primary
    SuperDialogActionTone.Neutral -> COUITheme.colorScheme.onSurfaceVariantActions
    SuperDialogActionTone.Destructive -> COUITheme.colorScheme.error
}
