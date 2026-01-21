package com.revenuecat.samplecat.ui.components.spinner

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path

/**
 * Creates a yarn ball path with decorative thread patterns.
 */
fun ballPath(size: Size): Path {
    val width = size.width
    val height = size.height

    return Path().apply {
        // Outer circle
        moveTo(0.5f * width, 0.125f * height)
        cubicTo(
            0.29289f * width, 0.125f * height,
            0.125f * width, 0.29289f * height,
            0.125f * width, 0.5f * height
        )
        cubicTo(
            0.125f * width, 0.70711f * height,
            0.29289f * width, 0.875f * height,
            0.5f * width, 0.875f * height
        )
        cubicTo(
            0.70711f * width, 0.875f * height,
            0.875f * width, 0.70711f * height,
            0.875f * width, 0.5f * height
        )
        cubicTo(
            0.875f * width, 0.29289f * height,
            0.70711f * width, 0.125f * height,
            0.5f * width, 0.125f * height
        )
        close()

        // Thread pattern 1
        moveTo(0.32953f * width, 0.25692f * height)
        lineTo(0.32181f * width, 0.24982f * height)
        cubicTo(
            0.48933f * width, 0.40036f * height,
            0.55523f * width, 0.58103f * height,
            0.52117f * width, 0.79608f * height
        )
        cubicTo(
            0.51418f * width, 0.79662f * height,
            0.50712f * width, 0.79688f * height,
            0.5f * width, 0.79688f * height
        )
        cubicTo(
            0.48594f * width, 0.79688f * height,
            0.47212f * width, 0.7959f * height,
            0.45858f * width, 0.79401f * height
        )
        cubicTo(
            0.49946f * width, 0.60932f * height,
            0.43497f * width, 0.44418f * height,
            0.28463f * width, 0.2958f * height
        )
        cubicTo(
            0.29821f * width, 0.28135f * height,
            0.31327f * width, 0.26834f * height,
            0.32953f * width, 0.25692f * height
        )
        close()

        // Thread pattern 2
        moveTo(0.4373f * width, 0.20976f * height)
        lineTo(0.43205f * width, 0.20488f * height)
        cubicTo(
            0.60487f * width, 0.36018f * height,
            0.67368f * width, 0.54276f * height,
            0.64067f * width, 0.75666f * height
        )
        lineTo(0.64742f * width, 0.75774f * height)
        cubicTo(
            0.63215f * width, 0.7665f * height,
            0.61602f * width, 0.77394f * height,
            0.59919f * width, 0.7799f * height
        )
        cubicTo(
            0.62574f * width, 0.56384f * height,
            0.55897f * width, 0.37632f * height,
            0.40011f * width, 0.22047f * height
        )
        cubicTo(
            0.41209f * width, 0.21606f * height,
            0.42454f * width, 0.2125f * height,
            0.4373f * width, 0.20976f * height
        )
        close()

        // Thread pattern 3
        moveTo(0.39787f * width, 0.68636f * height)
        lineTo(0.39768f * width, 0.6967f * height)
        cubicTo(
            0.39626f * width, 0.72214f * height,
            0.39257f * width, 0.74801f * height,
            0.38663f * width, 0.77433f * height
        )
        cubicTo(
            0.36109f * width, 0.76388f * height,
            0.33746f * width, 0.74994f * height,
            0.31619f * width, 0.73314f * height
        )
        cubicTo(
            0.33955f * width, 0.71756f * height,
            0.36474f * width, 0.70334f * height,
            0.39787f * width, 0.68636f * height
        )
        close()

        // Thread pattern 4
        moveTo(0.37954f * width, 0.5508f * height)
        cubicTo(
            0.38789f * width, 0.57731f * height,
            0.39344f * width, 0.60402f * height,
            0.39623f * width, 0.63123f * height
        )
        lineTo(0.39971f * width, 0.62944f * height)
        cubicTo(
            0.34731f * width, 0.6555f * height,
            0.31232f * width, 0.67481f * height,
            0.27882f * width, 0.69795f * height
        )
        cubicTo(
            0.26301f * width, 0.68038f * height,
            0.2493f * width, 0.66087f * height,
            0.23805f * width, 0.63984f * height
        )
        lineTo(0.22957f * width, 0.64615f * height)
        cubicTo(
            0.27974f * width, 0.60801f * height,
            0.32973f * width, 0.57623f * height,
            0.37954f * width, 0.5508f * height
        )
        close()

        // Thread pattern 5
        moveTo(0.72259f * width, 0.61673f * height)
        lineTo(0.72925f * width, 0.61801f * height)
        cubicTo(
            0.74075f * width, 0.62039f * height,
            0.75374f * width, 0.62353f * height,
            0.76819f * width, 0.62741f * height
        )
        cubicTo(
            0.75623f * width, 0.65261f * height,
            0.74077f * width, 0.67588f * height,
            0.72248f * width, 0.69657f * height
        )
        cubicTo(
            0.72405f * width, 0.66947f * height,
            0.72409f * width, 0.64288f * height,
            0.72259f * width, 0.61673f * height
        )
        close()

        // Thread pattern 6
        moveTo(0.32647f * width, 0.44097f * height)
        lineTo(0.32867f * width, 0.44422f * height)
        cubicTo(
            0.34132f * width, 0.46387f * height,
            0.35224f * width, 0.48379f * height,
            0.36143f * width, 0.504f * height
        )
        cubicTo(
            0.31355f * width, 0.52802f * height,
            0.26571f * width, 0.55755f * height,
            0.21791f * width, 0.59248f * height
        )
        cubicTo(
            0.21031f * width, 0.56961f * height,
            0.20549f * width, 0.54535f * height,
            0.2038f * width, 0.52023f * height
        )
        lineTo(0.20457f * width, 0.52115f * height)
        cubicTo(
            0.24528f * width, 0.49021f * height,
            0.28591f * width, 0.46349f * height,
            0.32647f * width, 0.44097f * height
        )
        close()

        // Thread pattern 7
        moveTo(0.69845f * width, 0.47625f * height)
        lineTo(0.70401f * width, 0.4767f * height)
        cubicTo(
            0.73025f * width, 0.47885f * height,
            0.76113f * width, 0.48281f * height,
            0.79657f * width, 0.48859f * height
        )
        cubicTo(
            0.7968f * width, 0.49238f * height,
            0.79688f * width, 0.49618f * height,
            0.79688f * width, 0.5f * height
        )
        cubicTo(
            0.79688f * width, 0.52788f * height,
            0.79303f * width, 0.55485f * height,
            0.78585f * width, 0.58043f * height
        )
        cubicTo(
            0.75885f * width, 0.57301f * height,
            0.73609f * width, 0.5679f * height,
            0.71734f * width, 0.56505f * height
        )
        cubicTo(
            0.71333f * width, 0.53486f * height,
            0.70696f * width, 0.50524f * height,
            0.69845f * width, 0.47625f * height
        )
        close()

        // Thread pattern 8
        moveTo(0.24715f * width, 0.34436f * height)
        lineTo(0.25925f * width, 0.35676f * height)
        cubicTo(
            0.27287f * width, 0.3711f * height,
            0.28548f * width, 0.38558f * height,
            0.29709f * width, 0.40022f * height
        )
        cubicTo(
            0.26672f * width, 0.41722f * height,
            0.23639f * width, 0.43652f * height,
            0.20609f * width, 0.458f * height
        )
        cubicTo(
            0.21189f * width, 0.41684f * height,
            0.22619f * width, 0.37833f * height,
            0.24715f * width, 0.34436f * height
        )
        close()

        // Thread pattern 9
        moveTo(0.64694f * width, 0.35248f * height)
        lineTo(0.65612f * width, 0.35277f * height)
        cubicTo(
            0.68681f * width, 0.35413f * height,
            0.7226f * width, 0.35756f * height,
            0.7634f * width, 0.36307f * height
        )
        cubicTo(
            0.77547f * width, 0.3861f * height,
            0.78452f * width, 0.41088f * height,
            0.79016f * width, 0.43692f * height
        )
        cubicTo(
            0.74729f * width, 0.43031f * height,
            0.71094f * width, 0.42638f * height,
            0.68091f * width, 0.42517f * height
        )
        cubicTo(
            0.67128f * width, 0.40048f * height,
            0.65993f * width, 0.37623f * height,
            0.64694f * width, 0.35248f * height
        )
        close()

        // Thread pattern 10
        moveTo(0.53996f * width, 0.20578f * height)
        cubicTo(
            0.61473f * width, 0.21593f * height,
            0.68041f * width, 0.25363f * height,
            0.72668f * width, 0.30828f * height
        )
        cubicTo(
            0.68393f * width, 0.30361f * height,
            0.64727f * width, 0.3016f * height,
            0.61639f * width, 0.30219f * height
        )
        cubicTo(
            0.59438f * width, 0.26901f * height,
            0.56887f * width, 0.23686f * height,
            0.53996f * width, 0.20578f * height
        )
        close()
    }
}
