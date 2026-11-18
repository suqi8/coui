// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.RollingNumber
import io.github.suqi8.coui.kmp.basic.RollingNumberDefaults
import io.github.suqi8.coui.kmp.basic.Slider
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_size
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_rolling_number_auto_count
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_rolling_number_auto_count_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_rolling_number_min_digits
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_rolling_number_roll_ones
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_rolling_number_roll_ones_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_rolling_number_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_rolling_number_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_rolling_number_variant_burst
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_rolling_number_variant_palette
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** The demo page of `RollingNumber`, listed in its category and found by the search. */
internal val RollingNumberDemo = Demo(
    id = "rolling_number",
    apiName = "RollingNumber",
    title = Res.string.demo_rolling_number_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants { rollingNumberPage() },
    searchTerms = Res.string.demo_rolling_number_search_terms,
    isNew = true,
)

private val RollingSteps = listOf(-100, -10, -1, 1, 10, 100)
private val RollingMinDigits = listOf(1, 2, 3)

/** The burst-shot interval of the burst variant, about the camera's burst rate. */
private const val ROLLING_BURST_INTERVAL_MILLIS = 120L

private fun LazyListScope.rollingNumberPage() {
    playground {
        var value by rememberSaveable { mutableIntStateOf(8) }
        var rollOnes by rememberSaveable { mutableStateOf(false) }
        var autoCount by rememberSaveable { mutableStateOf(false) }
        var minDigitsIndex by rememberSaveable { mutableIntStateOf(0) }
        var size by rememberSaveable { mutableFloatStateOf(40f) }
        LaunchedEffect(autoCount) {
            while (autoCount) {
                delay(ROLLING_BURST_INTERVAL_MILLIS)
                value += 1
            }
        }

        PlaygroundPreview {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 96.dp),
                contentAlignment = Alignment.Center,
            ) {
                RollingNumber(
                    value = value,
                    rollOnesDigit = rollOnes,
                    minDigits = RollingMinDigits[minDigitsIndex],
                    style = COUITheme.textStyles.bodyXL.copy(fontSize = size.sp, fontWeight = FontWeight.Medium),
                )
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RollingSteps.forEach { step ->
                    TextButton(
                        text = if (step > 0) "+$step" else step.toString(),
                        onClick = { value += step },
                    )
                }
            }
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = rollOnes,
                    onCheckedChange = { rollOnes = it },
                    title = stringResource(Res.string.demo_rolling_number_roll_ones),
                    summary = stringResource(Res.string.demo_rolling_number_roll_ones_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = autoCount,
                    onCheckedChange = { autoCount = it },
                    title = stringResource(Res.string.demo_rolling_number_auto_count),
                    summary = stringResource(Res.string.demo_rolling_number_auto_count_summary),
                )
            }
            item {
                OverlayDropdownPreference(
                    items = RollingMinDigits.map { it.toString() },
                    selectedIndex = minDigitsIndex,
                    title = stringResource(Res.string.demo_rolling_number_min_digits),
                    onSelectedIndexChange = { minDigitsIndex = it },
                )
            }
            item {
                SliderPreference(
                    value = size,
                    onValueChange = { size = it },
                    title = stringResource(Res.string.common_size),
                    valueText = "${size.roundToInt()}sp",
                    valueRange = 14f..72f,
                )
            }
        }
    }
    variant(key = "burst", title = Res.string.demo_rolling_number_variant_burst, inCard = false) {
        var shots by remember { mutableIntStateOf(1) }
        LaunchedEffect(Unit) {
            while (true) {
                delay(ROLLING_BURST_INTERVAL_MILLIS)
                shots = if (shots >= 99) 1 else shots + 1
            }
        }
        RollingNumberStage {
            // `burst.xml`: a 32dp disc of 50% black with the two-digit count.
            Box(
                modifier = Modifier
                    .size(RollingNumberDefaults.BurstBadgeSize)
                    .background(Color(0x7F000000), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                RollingNumber(
                    value = shots,
                    minDigits = 2,
                    color = Color.White,
                    style = COUITheme.textStyles.bodyXL.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    rollDistance = RollingNumberDefaults.BurstRollDistance,
                )
            }
        }
    }
    variant(key = "palette", title = Res.string.demo_rolling_number_variant_palette) {
        var tone by remember { mutableFloatStateOf(0f) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            RollingNumber(
                value = tone.roundToInt(),
                style = COUITheme.textStyles.bodyXL.copy(fontSize = 24.sp, fontWeight = FontWeight.Medium),
            )
        }
        Slider(
            value = tone,
            onValueChange = { tone = it },
            modifier = Modifier.padding(16.dp),
            valueRange = -100f..100f,
            startFromMiddle = true,
        )
    }
}

/** A camera-like stage: a photo standing in for the viewfinder behind the counter. */
@Composable
private fun RollingNumberStage(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.blur_test_bg),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.padding(PaddingValues(vertical = 40.dp, horizontal = 8.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}
