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

/** Info: ColorOS `ic_info` (com.coloros.phonemanager_17.5.3). */
val COUIIcons.Info: ImageVector
    get() {
        _info?.let { return it }
        return ImageVector.Builder(
            name = "Info",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M11.999,2.00195 C17.5208,2.0023,21.9971,6.47912,21.9971,12.001 C21.9965,17.5223,17.5204,21.9987,11.999,21.999 C6.47733,21.999,2.00061,17.5225,2,12.001 C2,6.4789,6.47695,2.00195,11.999,2.00195 Z M11.999,3.40234 C7.25015,3.40234,3.40039,7.2521,3.40039,12.001 C3.401,16.7493,7.25053,20.5996,11.999,20.5996 C16.7472,20.5993,20.597,16.7491,20.5977,12.001 C20.5977,7.25232,16.7476,3.40269,11.999,3.40234 Z M11.7588,10.0146 C11.975,9.89025,12.2418,9.89006,12.458,10.0146 C12.6743,10.1396,12.8084,10.3713,12.8086,10.6211 L12.8086,15.7041 L13.1982,15.4795 C13.5328,15.2869,13.9611,15.401,14.1543,15.7354 C14.3474,16.07,14.2318,16.498,13.8975,16.6914 L12.458,17.5225 C12.2416,17.6473,11.9752,17.6472,11.7588,17.5225 C11.5424,17.3975,11.4084,17.1668,11.4082,16.917 L11.4082,11.833 L10.9834,12.0781 C10.6487,12.2712,10.2206,12.1569,10.0273,11.8223 C9.83438,11.4876,9.94962,11.0595,10.2842,10.8662 L11.7588,10.0146 Z M12.001,6.3916 C12.5982,6.39182,13.0828,6.8764,13.083,7.47363 C13.083,8.07108,12.5984,8.55544,12.001,8.55566 C11.4035,8.55557,10.9189,8.07115,10.9189,7.47363 C10.9192,6.87633,11.4036,6.3917,12.001,6.3916 Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _info = it }
    }

private var _info: ImageVector? = null
