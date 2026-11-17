// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.window

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.layout.GuideDialogColors
import io.github.suqi8.coui.kmp.layout.GuideDialogContentLayout
import io.github.suqi8.coui.kmp.layout.GuideDialogDefaults
import io.github.suqi8.coui.kmp.layout.GuideDialogPage
import io.github.suqi8.coui.kmp.utils.RemovePlatformDialogDefaultEffects
import io.github.suqi8.coui.kmp.utils.WindowNavigationEventScope
import io.github.suqi8.coui.kmp.utils.platformDialogProperties

/**
 * A paged guide dialog matching the COUI guide dialog (`COUIGuideDialogBuilder`,
 * `COUIGuideLayoutContentView`), rendered at window level: no Scaffold is required.
 *
 * See [io.github.suqi8.coui.kmp.overlay.OverlayGuideDialog] for the behaviour; both variants share it.
 *
 * @param show Whether the dialog is shown.
 * @param pages The guide pages.
 * @param onStart Invoked by the "Got it" button (`OnButtonClickListener.onStartClick`).
 * @param modifier The modifier applied to the dialog panel.
 * @param backgroundModifier Applied inside the panel's corner clip, under the content and over
 *   [backgroundColor].
 * @param onSkip Invoked by the skip button (`onSkipClick`); `null` falls back to [onStart].
 * @param onNext Invoked with the target page when the next button turns the page (`onNextClick`).
 * @param onDismissRequest Invoked on the back gesture.
 * @param skipText The skip button label.
 * @param nextText The next button label.
 * @param startText The start button label.
 * @param imageHeight The illustration height.
 * @param imagePadding The margins around every illustration.
 * @param colors The title and description colors.
 * @param backgroundColor The panel fill.
 * @param enableWindowDim Whether to dim the window behind the dialog.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred panel.
 * @param onDismissFinished Invoked when the hide animation completes.
 */
@Composable
fun WindowGuideDialog(
    show: Boolean,
    pages: List<GuideDialogPage>,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    onSkip: (() -> Unit)? = null,
    onNext: ((page: Int) -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = null,
    skipText: String = GuideDialogDefaults.SkipText,
    nextText: String = GuideDialogDefaults.NextText,
    startText: String = GuideDialogDefaults.StartText,
    imageHeight: Dp = GuideDialogDefaults.ImageHeight,
    imagePadding: PaddingValues = GuideDialogDefaults.ImagePadding,
    colors: GuideDialogColors = GuideDialogDefaults.guideDialogColors(),
    backgroundColor: Color = DialogDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    materialStroke: Boolean = false,
    onDismissFinished: (() -> Unit)? = null,
) {
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val captionBarPadding = WindowInsets.captionBar.asPaddingValues().calculateTopPadding()
    val displayCutoutPadding = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    val safeTopInset = remember(statusBarsPadding, captionBarPadding, displayCutoutPadding) {
        maxOf(statusBarsPadding, captionBarPadding, displayCutoutPadding)
    }
    val currentOnDismissRequest = rememberUpdatedState(onDismissRequest)

    GuideDialogContentLayout(
        show = show,
        pages = pages,
        onStart = onStart,
        popupHost = { visible, hostContent ->
            if (visible) {
                Dialog(
                    onDismissRequest = { currentOnDismissRequest.value?.invoke() },
                    properties = platformDialogProperties(),
                ) {
                    RemovePlatformDialogDefaultEffects()
                    WindowNavigationEventScope {
                        hostContent()
                    }
                }
            }
        },
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        onSkip = onSkip,
        onNext = onNext,
        onDismissRequest = onDismissRequest,
        skipText = skipText,
        nextText = nextText,
        startText = startText,
        imageHeight = imageHeight,
        imagePadding = imagePadding,
        colors = colors,
        backgroundColor = backgroundColor,
        enableWindowDim = enableWindowDim,
        materialStroke = materialStroke,
        onDismissFinished = onDismissFinished,
        topInset = safeTopInset,
    )
}
