// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.regex.Pattern

// The example app follows the system language, so every label matches English or Chinese.
private val ComponentsTabLabel = exact("Components|组件")
private val DesignTabLabel = exact("Design|设计")
private val SettingsTabLabel = exact("Settings|设置")

// The category rows on the components home, in display order.
private val CategoryLabels = listOf(
    "Display|基础展示",
    "Buttons|按钮",
    "Selection|选择与滑块",
    "Input and search|输入与搜索",
    "Pickers|选择器",
    "Dialogs and sheets|对话框与面板",
    "Menus and tips|菜单与提示",
    "Feedback|反馈与状态",
    "Navigation and bars|导航与栏",
    "Layout|布局与容器",
    "Preferences|设置项",
).map(::exact)

// How many demo pages to open in each category per iteration.
private const val DEMOS_PER_CATEGORY = 4

// How many swipes to try when looking for a category row further down the home list.
private const val FIND_SWIPES = 4

private const val WAIT_MILLIS = 2_000L

// Each swipe step takes about 5 ms: 10 steps fling the list, 40 steps scroll it and stop.
private const val FLING_STEPS = 10
private const val SCROLL_STEPS = 40

/**
 * This test class generates a baseline profile for the target package.
 *
 * It covers app startup and the critical user journeys: the components home, the first demo
 * pages of every category, and the design and settings tabs, so that the code paths of most
 * COUI components end up in the profile.
 *
 * The journey never acts outside the target app: every lookup is limited to the target package,
 * a step only presses back after it has confirmed that a new page opened, and the iteration stops
 * as soon as the app is no longer in the foreground.
 *
 * Run the generator on a connected device (API 33+, or rooted API 28+) with:
 * ```
 * ./gradlew :coui-ui:generateBaselineProfile \
 *     -Pandroidx.baselineprofile.forceonlyconnecteddevices \
 *     -Pandroid.injected.androidTest.leaveApksInstalledAfterRun=true
 * ```
 * The library task also splits the result into each library module's `baseline-prof.txt`. On
 * ColorOS, allow both the example app and this test app to run in the background first, or the
 * system freezes the test runner between iterations.
 *
 * After you run the generator, you can verify the improvements running the
 * [StartupBenchmarks] benchmark.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    private val packageName: String
        get() = InstrumentationRegistry.getArguments().getString("targetAppId")
            ?: throw Exception("targetAppId not passed as instrumentation runner arg")

    @Test
    fun startup() {
        rule.collect(
            packageName = packageName,
            includeInStartupProfile = true,
        ) {
            pressHome()
            startActivityAndWait()
            device.wait(Until.hasObject(By.scrollable(true)), WAIT_MILLIS)
        }
    }

    @Test
    fun interactions() {
        rule.collect(
            packageName = packageName,
            maxIterations = 5,
            includeInStartupProfile = false,
        ) {
            pressHome()
            startActivityAndWait()
            device.wait(Until.hasObject(inApp(By.text(ComponentsTabLabel))), WAIT_MILLIS)

            for (label in CategoryLabels) {
                if (!browseCategory(label)) break
            }

            openTab(DesignTabLabel)
            openTab(SettingsTabLabel)
        }
    }
}

private fun exact(alternatives: String): Pattern = Pattern.compile("^($alternatives)$")

/** Limits [selector] to the target app, so a lookup never matches another app's window. */
private fun MacrobenchmarkScope.inApp(selector: BySelector): BySelector = selector.pkg(packageName)

private fun MacrobenchmarkScope.isAppInForeground(): Boolean = device.currentPackageName == packageName

/**
 * The page's main list: the tallest scrollable node of the target app. The floating bottom bar
 * is scrollable too, so taking the first match would drag the bar instead of the page.
 */
private fun MacrobenchmarkScope.mainList(): UiObject2? = try {
    device.findObjects(inApp(By.scrollable(true))).maxByOrNull { it.visibleBounds.height() }
} catch (_: StaleObjectException) {
    null
}

/**
 * Scrolls the main list down by one swipe through the middle of the list. `UiObject2.scroll` and
 * `fling` start their gesture near the list's bottom edge, which lies under the floating bottom
 * bar, so they would drag the bar instead; this swipe stays between 65% and 30% of the list.
 *
 * @param fast Whether to swipe quickly enough to fling, or slowly enough to stop where it lands.
 */
private fun MacrobenchmarkScope.swipeMainList(fast: Boolean): Boolean {
    val bounds = mainList()?.visibleBounds ?: return false
    val x = bounds.centerX()
    val from = bounds.top + bounds.height() * 65 / 100
    val to = bounds.top + bounds.height() * 30 / 100
    return device.swipe(x, from, x, to, if (fast) FLING_STEPS else SCROLL_STEPS)
}

/** Whether the main screen with its bottom tabs is showing. */
private fun MacrobenchmarkScope.isOnMainScreen(): Boolean = device.hasObject(inApp(By.text(ComponentsTabLabel)))

/**
 * Opens the category row matching [label] on the components home, visits its first demo pages
 * and returns to the home. Returns false when the journey can no longer continue inside the app.
 */
private fun MacrobenchmarkScope.browseCategory(label: Pattern): Boolean {
    if (!isAppInForeground() || !isOnMainScreen()) return false

    val entry = inApp(By.text(label))
    var found = device.hasObject(entry)
    var swipes = 0
    while (!found && swipes < FIND_SWIPES) {
        if (!uiAction { swipeMainList(fast = false).takeIf { it } }) break
        device.waitForIdle()
        found = device.hasObject(entry)
        swipes++
    }
    if (!found) return true

    // The category page has no bottom tabs; only continue once it has really opened.
    if (!uiAction { device.findObject(entry)?.click() }) return true
    if (!device.wait(Until.gone(inApp(By.text(ComponentsTabLabel))), WAIT_MILLIS)) return isAppInForeground()
    device.waitForIdle()

    repeat(DEMOS_PER_CATEGORY) { index ->
        if (!isAppInForeground()) return false
        openDemo(index, categoryTitle = label)
    }

    if (!isAppInForeground()) return false
    device.pressBack()
    return device.wait(Until.hasObject(inApp(By.text(ComponentsTabLabel))), WAIT_MILLIS) == true
}

/**
 * Opens the demo row at [index] on a category page, scrolls the demo and returns to the category,
 * whose top bar shows [categoryTitle]. Presses back only when the demo page has opened.
 */
private fun MacrobenchmarkScope.openDemo(index: Int, categoryTitle: Pattern) {
    val clicked = uiAction {
        mainList()
            ?.findObjects(By.clickable(true))
            ?.getOrNull(index)
            ?.click()
    }
    if (!clicked) return
    // The demo page replaces the category title with the demo's own title.
    if (!device.wait(Until.gone(inApp(By.text(categoryTitle))), WAIT_MILLIS)) return
    device.waitForIdle()
    uiAction { swipeMainList(fast = true) }
    device.waitForIdle()

    // A back press may first close a keyboard or an overlay; never press past the category page.
    repeat(2) {
        if (!isAppInForeground()) return
        device.pressBack()
        if (device.wait(Until.hasObject(inApp(By.text(categoryTitle))), WAIT_MILLIS) == true) return
    }
}

/** Switches to the main tab matching [label] and scrolls through it. */
private fun MacrobenchmarkScope.openTab(label: Pattern) {
    if (!isAppInForeground() || !isOnMainScreen()) return
    if (!uiAction { device.findObject(inApp(By.text(label)))?.click() }) return
    device.waitForIdle()
    uiAction { swipeMainList(fast = true) }
    device.waitForIdle()
}

/**
 * Runs one UI Automator step and reports whether it found its target. A node that goes stale
 * while the page recomposes or animates is skipped instead of failing the whole journey.
 */
private inline fun uiAction(action: () -> Any?): Boolean = try {
    action() != null
} catch (_: StaleObjectException) {
    false
}
