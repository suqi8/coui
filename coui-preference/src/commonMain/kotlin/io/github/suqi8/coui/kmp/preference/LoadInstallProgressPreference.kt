// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.InstallLoadProgress
import io.github.suqi8.coui.kmp.basic.InstallLoadProgressColors
import io.github.suqi8.coui.kmp.basic.InstallLoadProgressDefaults
import io.github.suqi8.coui.kmp.basic.InstallLoadState
import io.github.suqi8.coui.kmp.basic.InstallLoadStyle
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.preference.internal.EndActionsSlot
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A preference row with a download / install progress button at the end, mirroring
 * COUILoadInstallProgressPreference.
 *
 * The widget is the small [InstallLoadProgress] capsule
 * (coui_preference_load_progress.xml: COUIInstallLoadProgress with
 * Widget.COUI.COUILoadProgress.InstallDownloadSmall). The preference only forwards its text, state,
 * progress and colors (COUILoadInstallProgressPreference.onBindViewHolder); clicking the capsule
 * calls [onProgressClick], where the caller advances [state] the way COUI's OnStateChangeListener
 * reports it. The row click ([onClick]) is independent from the capsule click.
 *
 * @param title The title of the [LoadInstallProgressPreference].
 * @param progressText The label of the progress button (COUI progressText).
 * @param onProgressClick The callback when the progress button is clicked.
 * @param modifier The modifier to be applied to the [LoadInstallProgressPreference].
 * @param state The [InstallLoadState] of the progress button.
 * @param progress The progress between 0.0f and 1.0f, shown while loading or paused.
 * @param titleColor The color of the title.
 * @param summary The summary of the [LoadInstallProgressPreference].
 * @param summaryColor The color of the summary.
 * @param progressColors The [InstallLoadProgressColors] of the progress button (COUI
 *   progressTextColor / backgroundColor / installBackgroundColor / installProgressTextColor).
 * @param progressTextStyle The text style of the progress label (COUI progressTextSize,
 *   coui_install_download_progress_textsize 14sp).
 * @param startAction The [Composable] content on the start side of the [LoadInstallProgressPreference].
 * @param endActions The [Composable] content on the end side, placed before the progress button.
 * @param bottomAction The [Composable] content at the bottom of the [LoadInstallProgressPreference].
 * @param insideMargin The margin inside the [LoadInstallProgressPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param onClick The callback when the row (not the progress button) is clicked.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [LoadInstallProgressPreference] and its progress button are enabled.
 */
@Composable
fun LoadInstallProgressPreference(
    title: String,
    progressText: String,
    onProgressClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: InstallLoadState = InstallLoadState.Idle,
    progress: Float = 0f,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    progressColors: InstallLoadProgressColors = InstallLoadProgressDefaults.installLoadProgressColors(),
    progressTextStyle: TextStyle = COUITheme.textStyles.buttonM,
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable RowScope.() -> Unit = {},
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    onClick: (() -> Unit)? = null,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
) {
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            EndActionsSlot(endSpacing = 8.dp) { endActions() }
            InstallLoadProgress(
                text = progressText,
                onClick = onProgressClick,
                state = state,
                progress = progress,
                enabled = enabled,
                style = InstallLoadStyle.Small,
                colors = progressColors,
                textStyle = progressTextStyle,
            )
        },
        bottomAction = bottomAction,
        onClick = onClick,
        holdDownState = holdDownState,
        enabled = enabled,
    )
}
