// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.effect.SpringChainDefaults
import io.github.suqi8.coui.kmp.effect.SpringChainTransCalculator
import io.github.suqi8.coui.kmp.effect.rememberSpringChainState
import io.github.suqi8.coui.kmp.effect.springChain
import io.github.suqi8.coui.kmp.effect.springChainItem
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_accumulate
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_accumulate_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_attachment_friction
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_attachment_tension
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_main_friction
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_main_tension
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_max_rows
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_reset
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_row
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_target
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_target_grid
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_target_list
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_trans_factor
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_trans_factor_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_variant_grid
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spring_chain_variant_list
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** The demo page of `springChain`, listed in its category and found by the search. */
internal val SpringChainDemo = Demo(
    id = "spring_chain",
    apiName = "springChain",
    title = Res.string.demo_spring_chain_title,
    category = DemoCategory.Layout,
    page = DemoPage.Variants {
        springChainPlayground()
        variant(key = "list", title = Res.string.demo_spring_chain_variant_list) {
            SpringChainSample(grid = false, modifier = Modifier.padding(16.dp))
        }
        variant(key = "grid", title = Res.string.demo_spring_chain_variant_grid) {
            SpringChainSample(grid = true, modifier = Modifier.padding(16.dp))
        }
    },
    searchTerms = Res.string.demo_spring_chain_search_terms,
)

private const val GRID_COLUMNS = 4

private const val TARGET_LIST = 0
private const val TARGET_GRID = 1

/**
 * The spring chain parameters of the playground.
 *
 * @property transFactor The share of the pull distance one chain step adds, see
 *   [SpringChainDefaults.TransFactor].
 */
@Immutable
private data class SpringChainParams(
    val mainTension: Float = SpringChainDefaults.MainTension,
    val mainFriction: Float = SpringChainDefaults.MainFriction,
    val attachmentTension: Float = SpringChainDefaults.AttachmentTension,
    val attachmentFriction: Float = SpringChainDefaults.AttachmentFriction,
    val transFactor: Float = SpringChainDefaults.TransFactor,
    val maxRows: Int = SpringChainDefaults.MaxRows,
    val accumulate: Boolean = true,
)

private val DefaultParams = SpringChainParams()

private fun LazyListScope.springChainPlayground() {
    playground {
        var target by rememberSaveable { mutableIntStateOf(TARGET_LIST) }
        var mainTension by rememberSaveable { mutableFloatStateOf(DefaultParams.mainTension) }
        var mainFriction by rememberSaveable { mutableFloatStateOf(DefaultParams.mainFriction) }
        var attachmentTension by rememberSaveable { mutableFloatStateOf(DefaultParams.attachmentTension) }
        var attachmentFriction by rememberSaveable { mutableFloatStateOf(DefaultParams.attachmentFriction) }
        var transFactor by rememberSaveable { mutableFloatStateOf(DefaultParams.transFactor) }
        var maxRows by rememberSaveable { mutableIntStateOf(DefaultParams.maxRows) }
        var accumulate by rememberSaveable { mutableStateOf(DefaultParams.accumulate) }
        val params = SpringChainParams(
            mainTension = mainTension,
            mainFriction = mainFriction,
            attachmentTension = attachmentTension,
            attachmentFriction = attachmentFriction,
            transFactor = transFactor,
            maxRows = maxRows,
            accumulate = accumulate,
        )

        PlaygroundPreview {
            SpringChainSample(grid = target == TARGET_GRID, params = params)
        }

        val targetTitle = stringResource(Res.string.demo_spring_chain_target)
        val targetLabels = listOf(
            stringResource(Res.string.demo_spring_chain_target_list),
            stringResource(Res.string.demo_spring_chain_target_grid),
        )
        val mainTensionTitle = stringResource(Res.string.demo_spring_chain_main_tension)
        val mainFrictionTitle = stringResource(Res.string.demo_spring_chain_main_friction)
        val attachmentTensionTitle = stringResource(Res.string.demo_spring_chain_attachment_tension)
        val attachmentFrictionTitle = stringResource(Res.string.demo_spring_chain_attachment_friction)
        val transFactorTitle = stringResource(Res.string.demo_spring_chain_trans_factor)
        val transFactorSummary = stringResource(Res.string.demo_spring_chain_trans_factor_summary)
        val maxRowsTitle = stringResource(Res.string.demo_spring_chain_max_rows)
        val accumulateTitle = stringResource(Res.string.demo_spring_chain_accumulate)
        val accumulateSummary = stringResource(Res.string.demo_spring_chain_accumulate_summary)
        val resetTitle = stringResource(Res.string.demo_spring_chain_reset)
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = targetLabels,
                    selectedIndex = target,
                    title = targetTitle,
                    onSelectedIndexChange = { target = it },
                )
            }
            item {
                SliderPreference(
                    value = mainTension,
                    onValueChange = { mainTension = it },
                    title = mainTensionTitle,
                    valueText = "${mainTension.roundToInt()}",
                    valueRange = 10f..300f,
                )
            }
            item {
                SliderPreference(
                    value = mainFriction,
                    onValueChange = { mainFriction = it },
                    title = mainFrictionTitle,
                    valueText = "${mainFriction.roundToInt()}",
                    valueRange = 1f..40f,
                )
            }
            item {
                SliderPreference(
                    value = attachmentTension,
                    onValueChange = { attachmentTension = it },
                    title = attachmentTensionTitle,
                    valueText = "${attachmentTension.roundToInt()}",
                    valueRange = 10f..300f,
                )
            }
            item {
                SliderPreference(
                    value = attachmentFriction,
                    onValueChange = { attachmentFriction = it },
                    title = attachmentFrictionTitle,
                    valueText = "${attachmentFriction.roundToInt()}",
                    valueRange = 1f..40f,
                )
            }
            item {
                SliderPreference(
                    value = transFactor,
                    onValueChange = { transFactor = it },
                    title = transFactorTitle,
                    summary = transFactorSummary,
                    valueText = "${(transFactor * 100).roundToInt()}%",
                    valueRange = 0.02f..0.3f,
                    steps = 13,
                    showKeyPoints = false,
                )
            }
            item {
                SliderPreference(
                    value = maxRows.toFloat(),
                    onValueChange = { maxRows = it.roundToInt() },
                    title = maxRowsTitle,
                    valueText = "$maxRows",
                    valueRange = 1f..50f,
                )
            }
            item {
                SwitchPreference(
                    checked = accumulate,
                    onCheckedChange = { accumulate = it },
                    title = accumulateTitle,
                    summary = accumulateSummary,
                )
            }
            item {
                ArrowPreference(
                    title = resetTitle,
                    onClick = {
                        mainTension = DefaultParams.mainTension
                        mainFriction = DefaultParams.mainFriction
                        attachmentTension = DefaultParams.attachmentTension
                        attachmentFriction = DefaultParams.attachmentFriction
                        transFactor = DefaultParams.transFactor
                        maxRows = DefaultParams.maxRows
                        accumulate = DefaultParams.accumulate
                    },
                )
            }
        }
    }
}

/** A hint above a spring-chained list or grid of [params]. */
@Composable
private fun SpringChainSample(
    grid: Boolean,
    modifier: Modifier = Modifier,
    params: SpringChainParams = DefaultParams,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.demo_spring_chain_hint),
            color = COUITheme.colorScheme.labelSecondaryVariant,
            style = COUITheme.textStyles.bodyM,
        )
        if (grid) SpringChainGrid(params = params) else SpringChainList(params = params)
    }
}

@Composable
private fun SpringChainList(
    params: SpringChainParams,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val transFactor = params.transFactor
    val transCalculator = remember(transFactor) {
        SpringChainTransCalculator { _, distance, _ -> distance * transFactor }
    }
    val chainState = rememberSpringChainState(
        lazyListState = listState,
        mainTension = params.mainTension,
        mainFriction = params.mainFriction,
        attachmentTension = params.attachmentTension,
        attachmentFriction = params.attachmentFriction,
        maxRows = params.maxRows,
        transCalculator = transCalculator,
        accumulate = params.accumulate,
    )
    val rowLabel = stringResource(Res.string.demo_spring_chain_row)
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .springChain(chainState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(20) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .springChainItem(chainState, row = index),
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardPressed),
            ) {
                Text(text = "$rowLabel ${index + 1}")
            }
        }
    }
}

@Composable
private fun SpringChainGrid(
    params: SpringChainParams,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    val transFactor = params.transFactor
    val transCalculator = remember(transFactor) {
        SpringChainTransCalculator { _, distance, _ -> distance * transFactor }
    }
    val chainState = rememberSpringChainState(
        lazyGridState = gridState,
        mainTension = params.mainTension,
        mainFriction = params.mainFriction,
        attachmentTension = params.attachmentTension,
        attachmentFriction = params.attachmentFriction,
        maxRows = params.maxRows,
        transCalculator = transCalculator,
        accumulate = params.accumulate,
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        state = gridState,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .springChain(chainState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(24) { index ->
            Card(
                modifier = Modifier
                    .height(56.dp)
                    .springChainItem(chainState, row = index / GRID_COLUMNS),
                cornerRadius = 16.dp,
                colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardPressed),
            ) {
                Box(modifier = Modifier.height(56.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "${index + 1}")
                }
            }
        }
    }
}
