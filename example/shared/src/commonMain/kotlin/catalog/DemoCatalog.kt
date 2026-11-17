// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog

import catalog.entries.ButtonsDemos
import catalog.entries.DialogsDemos
import catalog.entries.DisplayDemos
import catalog.entries.FeedbackDemos
import catalog.entries.InputDemos
import catalog.entries.LayoutDemos
import catalog.entries.MenusDemos
import catalog.entries.NavigationDemos
import catalog.entries.PickersDemos
import catalog.entries.PreferenceDemos
import catalog.entries.SelectionDemos

/**
 * Every component demo, in category order. Each category registers its demos in its own file
 * under `catalog/entries/`; add a demo there.
 */
object DemoCatalog {
    val all: List<Demo> = DisplayDemos +
        ButtonsDemos +
        SelectionDemos +
        InputDemos +
        PickersDemos +
        DialogsDemos +
        MenusDemos +
        FeedbackDemos +
        NavigationDemos +
        LayoutDemos +
        PreferenceDemos

    private val byId: Map<String, Demo> = all.associateBy { it.id }

    private val byCategory: Map<DemoCategory, List<Demo>> = all.groupBy { it.category }

    init {
        check(byId.size == all.size) {
            "Duplicate demo ids: " + all.groupBy { it.id }.filterValues { it.size > 1 }.keys
        }
        all.forEach { demo ->
            check(demo.category.entriesOwnerMatches(demo)) { "Demo ${demo.id} is registered under the wrong category" }
        }
    }

    fun byId(id: String): Demo? = byId[id]

    fun byCategory(category: DemoCategory): List<Demo> = byCategory[category].orEmpty()
}

private fun DemoCategory.entriesOwnerMatches(demo: Demo): Boolean = when (this) {
    DemoCategory.Display -> demo in DisplayDemos
    DemoCategory.Buttons -> demo in ButtonsDemos
    DemoCategory.Selection -> demo in SelectionDemos
    DemoCategory.Input -> demo in InputDemos
    DemoCategory.Pickers -> demo in PickersDemos
    DemoCategory.Dialogs -> demo in DialogsDemos
    DemoCategory.Menus -> demo in MenusDemos
    DemoCategory.Feedback -> demo in FeedbackDemos
    DemoCategory.Navigation -> demo in NavigationDemos
    DemoCategory.Layout -> demo in LayoutDemos
    DemoCategory.Preference -> demo in PreferenceDemos
}
