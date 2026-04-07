// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mocharealm.gaze.capsule.ContinuousCapsule
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.basic.Search
import com.suqi8.coui.kmp.icon.icons.basic.SearchCleanup
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.utils.BackHandler
import kotlinx.coroutines.delay

private object COUISearchBarDimens {
    val Height = 40.dp
    val HeaderHeight = 48.dp
    val HeaderMinHeight = 33.dp
    val InnerIconSize = 36.dp
    val InnerIconStartGap = 4.dp
    val EditTextEndGap = 4.dp
    val FunctionalButtonStartGap = 8.dp
    val FunctionalButtonMaxWidth = 152.dp
    val InputTextSize = 16.sp
    val HintTextSize = 12.sp // coui_search_view_text_hint_size

    val ResponsivePaddingCompat = 16.dp
    val ResponsivePaddingMedium = 24.dp
    val ResponsivePaddingExpanded = 40.dp
    val CollapsePaddingStartThreshold = 10.dp
    val OrnamentFadeThreshold = 20.dp
}

/**
 * A comprehensive SearchBar component that replicates the COUI behavior.
 *
 * This component includes:
 * 1. Rolling hint animation when the query is empty (COUIHintAnimationLayout).
 * 2. Smooth expansion animation where the Cancel button slides in and the search box shrinks.
 * 3. Responsive horizontal padding based on screen width.
 *
 * @param query The current text in the search field.
 * @param onQueryChange Callback when the query text changes.
 * @param onSearch Callback when the user triggers the search action.
 * @param onCancel Callback when the user clicks the Cancel button.
 * @param active Whether the search bar is currently active (expanded).
 * @param onActiveChange Callback when the active state changes.
 * @param modifier Modifier to be applied to the layout.
 * @param enabled Controls the enabled state of the search bar.
 * @param hintTexts A list of strings to be displayed as rolling hints.
 * @param content The content to be displayed below the search bar when active.
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onCancel: () -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hintTexts: List<String> = listOf("Search..."),
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SearchHeaderField(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            onCancel = onCancel,
            active = active,
            onActiveChange = onActiveChange,
            enabled = enabled,
            hintTexts = hintTexts,
            inactiveHeight = COUISearchBarDimens.Height,
            collapsedFraction = 0f,
        )

        AnimatedVisibility(visible = active) {
            content()
        }
    }
}

@Composable
fun SearchHeaderField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onCancel: () -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hintTexts: List<String> = listOf("Search..."),
    collapsedFraction: Float = 0f,
    inactiveHeight: Dp = COUISearchBarDimens.HeaderHeight,
    collapsePaddingToZero: Boolean = false,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val collapseProgress = if (active) 0f else collapsedFraction.coerceIn(0f, 1f)
    val collapseDistance = (inactiveHeight - COUISearchBarDimens.HeaderMinHeight).coerceAtLeast(0.dp)
    val currentHeight = inactiveHeight - (collapseDistance * collapseProgress)
    val collapsedAmount = (inactiveHeight - currentHeight).coerceAtLeast(0.dp)
    val paddingProgress = when {
        !collapsePaddingToZero -> 0f
        collapseDistance == 0.dp -> 0f
        else -> ((collapsedAmount - COUISearchBarDimens.CollapsePaddingStartThreshold).value / collapseDistance.value)
            .coerceIn(0f, 1f)
    }
    val ornamentFadeProgress = when {
        collapseDistance == 0.dp -> collapseProgress
        else -> (collapsedAmount.value / COUISearchBarDimens.OrnamentFadeThreshold.value)
            .coerceIn(0f, 1f)
    }

    LaunchedEffect(active) {
        if (active) {
            focusRequester.requestFocus()
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val baseHorizontalPadding = responsiveSearchPadding(maxWidth)
        val horizontalPadding by animateDpAsState(
            targetValue = when {
                active -> baseHorizontalPadding
                collapsePaddingToZero -> baseHorizontalPadding * (1f - paddingProgress)
                else -> baseHorizontalPadding
            },
            animationSpec = tween(220)
        )
        val fieldHeight by animateDpAsState(
            targetValue = currentHeight.coerceAtLeast(COUISearchBarDimens.HeaderMinHeight),
            animationSpec = tween(220)
        )
        val ornamentAlpha by animateFloatAsState(
            targetValue = if (active) 1f else 1f - ornamentFadeProgress,
            animationSpec = tween(220)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(fieldHeight)
                    .background(
                        color = COUITheme.colorScheme.surfaceContainerHigh,
                        shape = ContinuousCapsule
                    )
                    .clip(ContinuousCapsule)
                    .graphicsLayer {
                        translationY = -collapsedAmount.toPx() / 4f
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(COUISearchBarDimens.InnerIconStartGap))

                    Box(
                        modifier = Modifier
                            .size(COUISearchBarDimens.InnerIconSize)
                            .graphicsLayer(alpha = ornamentAlpha),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Basic.Search,
                            tint = COUITheme.colorScheme.onSurfaceContainerHighest,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = COUISearchBarDimens.EditTextEndGap),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.isEmpty()) {
                            RollingHint(
                                texts = hintTexts,
                                textStyle = TextStyle(
                                    fontSize = COUISearchBarDimens.HintTextSize,
                                    color = COUITheme.colorScheme.onSurfaceContainerHighest
                                ),
                                modifier = Modifier.graphicsLayer(alpha = ornamentAlpha)
                            )
                        }

                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusChanged { if (it.isFocused) onActiveChange(true) },
                            enabled = enabled,
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = COUISearchBarDimens.InputTextSize,
                                color = COUITheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(COUITheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onSearch(query) })
                        )
                    }

                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(COUISearchBarDimens.InnerIconSize)
                                .clickable { onQueryChange("") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Basic.SearchCleanup,
                                tint = COUITheme.colorScheme.onSurfaceContainerHighest,
                                contentDescription = "Clear",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = active,
                enter = slideInHorizontally { it / 2 } + expandHorizontally() + fadeIn(),
                exit = slideOutHorizontally { it / 2 } + shrinkHorizontally() + fadeOut()
            ) {
                Row {
                    Spacer(Modifier.width(COUISearchBarDimens.FunctionalButtonStartGap))
                    Box(
                        modifier = Modifier
                            .height(inactiveHeight)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onCancel()
                                onActiveChange(false)
                                focusManager.clearFocus()
                                onQueryChange("")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = "Cancel",
                            style = COUITheme.textStyles.headline.copy(
                                color = COUITheme.colorScheme.primary,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }

    BackHandler(enabled = active) {
        onCancel()
        onActiveChange(false)
        focusManager.clearFocus()
    }
}

@Composable
private fun responsiveSearchPadding(width: Dp): Dp = when {
    width < 600.dp -> COUISearchBarDimens.ResponsivePaddingCompat
    width < 840.dp -> COUISearchBarDimens.ResponsivePaddingMedium
    else -> COUISearchBarDimens.ResponsivePaddingExpanded
}

/**
 * Implements the vertical rolling hint animation found in COUIHintAnimationLayout.
 */
@Composable
private fun RollingHint(
    texts: List<String>,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    if (texts.isEmpty()) return

    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(texts) {
        while (true) {
            delay(3000) // INTERVAL_TIME
            currentIndex = (currentIndex + 1) % texts.size
        }
    }

    AnimatedContent(
        targetState = currentIndex,
        transitionSpec = {
            // COUI uses TranslationY + Alpha.
            // Slide In from Bottom + Fade In
            (slideInVertically { height -> height } + fadeIn(animationSpec = tween(600)))
                .togetherWith(
                    // Slide Out to Top + Fade Out
                    slideOutVertically { height -> -height } + fadeOut(animationSpec = tween(600))
                )
        },
        modifier = modifier
    ) { index ->
        BasicText(
            text = texts[index],
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
