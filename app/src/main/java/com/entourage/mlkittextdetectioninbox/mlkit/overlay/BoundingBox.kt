package com.entourage.mlkittextdetectioninbox.mlkit.overlay

import android.graphics.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BoundingBox(
    val width: Dp = 300.dp,
    val height: Dp = 200.dp,
) {
    fun toBoundingBoxPx(density: Density): BoundingBoxPx =
        with(density) {
            BoundingBoxPx(
                width = this@BoundingBox.width.toPx(),
                height = this@BoundingBox.height.toPx(),
            )
        }

    fun toRect(containerSize: Size, density: Density): Rect = this.toBoundingBoxPx(density).toRect(containerSize)
}

data class BoundingBoxPx(
    val width: Float,
    val height: Float,
) {
    fun toRect(containerSize: Size): Rect =
        Rect(
            ((containerSize.width - this.width) / 2).toInt(),
            ((containerSize.height - this.height) / 2).toInt(),
            ((containerSize.width + this.width) / 2).toInt(),
            ((containerSize.height + this.height) / 2).toInt(),
        )
}
