// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The [CardListPosition] the rows of a [CardListGroup] read as the default of their
 * `cardListPosition` parameter. [CardListPosition.None] outside a group.
 */
val LocalCardListPosition = staticCompositionLocalOf { CardListPosition.None }

/**
 * The receiver of a [CardListGroup] builder: each [item] is one row of the group.
 */
interface CardListGroupScope {
    /**
     * Adds a row to the group.
     *
     * @param key An optional stable key for the row, so a row keeps its state when rows before it
     *   appear or disappear.
     * @param content The row, typically a preference or a [BasicComponent].
     */
    fun item(key: Any? = null, content: @Composable () -> Unit)
}

/**
 * A group of card list rows that resolves each row's [CardListPosition] from its index, as COUI
 * does for the preferences of a group (`COUICardListHelper.getPositionInGroup(Preference)`): the
 * first row is [CardListPosition.Head], the last [CardListPosition.Tail], a single row
 * [CardListPosition.Full]. Each row reads it through [LocalCardListPosition] as the default of its
 * `cardListPosition`, so the rounded outer edges get the COUI head / tail padding without the
 * caller numbering the rows; a row that passes `cardListPosition` explicitly keeps its own.
 *
 * The group adds no layout of its own: its rows are emitted straight into the parent, for example
 * the `Column` of a [Card]. Rows can be added conditionally; positions follow the rows present.
 *
 * @param content Declares the rows with [CardListGroupScope.item].
 */
@Composable
fun CardListGroup(content: CardListGroupScope.() -> Unit) {
    val items = CardListGroupScopeImpl().apply(content).items
    items.forEachIndexed { index, item ->
        key(item.key ?: CardListGroupIndexKey(index)) {
            CompositionLocalProvider(LocalCardListPosition provides cardListPositionOf(index, items.size)) {
                item.content()
            }
        }
    }
}

private class CardListGroupItem(val key: Any?, val content: @Composable () -> Unit)

/** The key of a row given no key, so it never collides with a caller's key. */
private data class CardListGroupIndexKey(val index: Int)

private class CardListGroupScopeImpl : CardListGroupScope {
    val items = mutableListOf<CardListGroupItem>()

    override fun item(key: Any?, content: @Composable () -> Unit) {
        items += CardListGroupItem(key, content)
    }
}
