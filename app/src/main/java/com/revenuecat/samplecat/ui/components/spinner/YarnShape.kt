package com.revenuecat.samplecat.ui.components.spinner

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path

/**
 * Creates a yarn/string path that extends from the ball.
 */
fun yarnPath(size: Size): Path {
    val width = size.width
    val height = size.height

    return Path().apply {
        moveTo(0.03637f * width, 0.78444f * height)
        lineTo(0.08115f * width, 0.76884f * height)
        cubicTo(
            0.11555f * width, 0.75686f * height,
            0.15344f * width, 0.76019f * height,
            0.18522f * width, 0.77799f * height
        )
        cubicTo(
            0.2182f * width, 0.79645f * height,
            0.25457f * width, 0.80807f * height,
            0.29215f * width, 0.81214f * height
        )
        lineTo(0.45597f * width, 0.82989f * height)
        cubicTo(
            0.46568f * width, 0.83094f * height,
            0.47544f * width, 0.83147f * height,
            0.4852f * width, 0.83147f * height
        )
    }
}
