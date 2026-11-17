// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.overlay

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.layout.GuideDialogColors
import io.github.suqi8.coui.kmp.layout.GuideDialogContentLayout
import io.github.suqi8.coui.kmp.layout.GuideDialogDefaults
import io.github.suqi8.coui.kmp.layout.GuideDialogPage
import io.github.suqi8.coui.kmp.utils.COUIPopupUtils.Companion.DialogLayout

/**
 * A paged guide dialog matching the COUI guide dialog (`COUIGuideDialogBuilder`,
 * `COUIGuideLayoutContentView`), rendered in the [io.github.suqi8.coui.kmp.basic.Scaffold] popup host.
 *
 * The dialog sits at the bottom of compact windows and in the centre of large ones, like every COUI
 * bottom-warning dialog. It holds a swipeable pager of [pages] (an illustration cropped to the full
 * width, a title and a description each), a page indicator and two buttons, "Skip" and "Next". The
 * next button turns the page on COUI's 650ms spring curve; scrolling from the second-to-last page to
 * the last one widens it over the fading skip button while its label cross-fades to "Got it", which
 * then calls [onStart]. A single page shows only the "Got it" button and no indicator.
 *
 * Taps outside the panel are ignored (`setCanceledOnTouchOutside(false)`); the back gesture calls
 * [onDismissRequest].
 *
 * @param show Whether the dialog is shown.
 * @param pages The guide pages.
 * @param onStart Invoked by the "Got it" button (`OnButtonClickListener.onStartClick`).
 * @param modifier The modifier applied to the dialog panel.
 * @param backgroundModifier Applied inside the panel's corner clip, under the content and over
 *   [backgroundColor]: the place for the COUI 17 blurred window background
 *   (`setBlurBackgroundDrawable`), together with a transparent [backgroundColor] and [materialStroke].
 * @param onSkip Invoked by the skip button (`onSkipClick`); `null` falls back to [onStart].
 * @param onNext Invoked with the target page when the next button turns the page (`onNextClick`).
 * @param onDismissRequest Invoked on the back gesture.
 * @param skipText The skip button label.
 * @param nextText The next button label.
 * @param startText The start button label.
 * @param imageHeight The illustration height (`setImageHeightStyle`): [GuideDialogDefaults.ImageHeight]
 *   or [GuideDialogDefaults.ImageHeightSmall].
 * @param imagePadding The margins around every illustration (`setImagePadding`).
 * @param colors The title and description colors.
 * @param backgroundColor The panel fill.
 * @param enableWindowDim Whether to dim the window behind the dialog.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred panel. The buttons
 *   then take their drawable material too (`COUIGuideDialogButtonLayout.setBlurState`).
 * @param onDismissFinished Invoked when the hide animation completes.
 * @param renderInRootScaffold Whether to render the dialog in the root (outermost) Scaffold.
 */
@Composable
fun OverlayGuideDialog(
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
    renderInRootScaffold: Boolean = true,
) {
    GuideDialogContentLayout(
        show = show,
        pages = pages,
        onStart = onStart,
        popupHost = { visible, hostContent ->
            val visibleState = remember { mutableStateOf(false) }
            visibleState.value = visible
            DialogLayout(
                visible = visibleState,
                enableWindowDim = false,
                enterTransition = EnterTransition.None,
                exitTransition = ExitTransition.None,
                enableAutoLargeScreen = false,
                renderInRootScaffold = renderInRootScaffold,
            ) {
                hostContent()
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
    )
}
