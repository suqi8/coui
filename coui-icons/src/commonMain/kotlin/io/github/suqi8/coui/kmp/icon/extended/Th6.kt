// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Th6: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th6: ImageVector
    get() {
        _th6?.let { return it }
        return ImageVector.Builder(
            name = "Th6",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M7.434,3.8C7.434,3.358 7.792,3 8.234,3C8.676,3 9.034,3.358 9.034,3.8V4.656H15.024V3.8C15.024,3.358 15.382,3 15.824,3C16.265,3 16.624,3.358 16.624,3.8V4.656H18.993C19.987,4.656 20.793,5.462 20.793,6.456V19.07C20.793,20.064 19.987,20.87 18.993,20.87H4.993C3.999,20.87 3.193,20.064 3.193,19.07V6.456C3.193,5.462 3.999,4.656 4.993,4.656H7.434V3.8ZM8.139,6.256H4.993C4.882,6.256 4.793,6.345 4.793,6.456V7.853H19.193V6.456C19.193,6.345 19.103,6.256 18.993,6.256H15.918C15.887,6.259 15.856,6.261 15.824,6.261C15.792,6.261 15.76,6.259 15.729,6.256H8.329C8.298,6.259 8.266,6.261 8.234,6.261C8.202,6.261 8.17,6.259 8.139,6.256ZM4.793,19.07V9.453H19.193V19.07C19.193,19.181 19.103,19.27 18.993,19.27H4.993C4.882,19.27 4.793,19.181 4.793,19.07Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M13.124 11.436H13.307V12.448H13.231Q12.734 12.448 12.342 12.595Q11.95 12.743 11.679 13.018Q11.407 13.293 11.266 13.68Q11.124 14.068 11.124 14.546V15.608Q11.124 15.954 11.192 16.215Q11.26 16.475 11.389 16.649Q11.517 16.822 11.694 16.909Q11.871 16.997 12.089 16.997Q12.296 16.997 12.465 16.909Q12.634 16.82 12.756 16.659Q12.878 16.498 12.942 16.28Q13.006 16.063 13.006 15.805Q13.006 15.546 12.942 15.326Q12.878 15.106 12.755 14.945Q12.632 14.784 12.456 14.696Q12.28 14.608 12.059 14.608Q11.759 14.608 11.533 14.746Q11.306 14.884 11.179 15.101Q11.051 15.317 11.043 15.552L10.663 15.306Q10.677 14.969 10.806 14.666Q10.935 14.363 11.165 14.127Q11.396 13.892 11.72 13.757Q12.045 13.623 12.453 13.623Q12.905 13.623 13.245 13.794Q13.585 13.965 13.813 14.265Q14.042 14.564 14.157 14.956Q14.272 15.349 14.272 15.792Q14.272 16.256 14.119 16.656Q13.966 17.056 13.682 17.355Q13.399 17.654 13.003 17.821Q12.607 17.988 12.116 17.988Q11.606 17.988 11.191 17.802Q10.776 17.615 10.478 17.276Q10.18 16.936 10.018 16.471Q9.857 16.006 9.857 15.451V14.961Q9.857 14.186 10.084 13.542Q10.311 12.898 10.736 12.425Q11.162 11.953 11.766 11.695Q12.369 11.436 13.124 11.436Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th6 = it }
    }

private var _th6: ImageVector? = null
