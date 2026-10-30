// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TagView
import io.github.suqi8.coui.kmp.basic.TagViewDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.tagViewSection() {
    item(key = "tagView") {
        SmallTitle(text = "TagView")
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Dark mode")
                    TagView(text = "NEW")
                    TagView(
                        text = "BETA",
                        colors = TagViewDefaults.tagViewColors(containerColor = Color(0xFFFF8A00)),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TagView(
                        text = "Outlined",
                        colors = TagViewDefaults.tagViewColors(
                            containerColor = Color.Transparent,
                            contentColor = COUITheme.colorScheme.primary,
                            strokeColor = COUITheme.colorScheme.primary,
                        ),
                        strokeWidth = 1.dp,
                    )
                    TagView(
                        text = "Corner",
                        shape = RoundedCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                    )
                    TagView(
                        text = "Large",
                        textSize = 12.dp,
                        insideMargin = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        trailingIcon = {
                            Icon(imageVector = COUIIcons.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                        },
                    )
                }
            }
        }
    }
}
