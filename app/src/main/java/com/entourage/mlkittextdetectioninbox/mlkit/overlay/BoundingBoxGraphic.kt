package com.entourage.mlkittextdetectioninbox.mlkit.overlay

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun BoundingBoxGraphic(
    boundingBox: Rect,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            color = Color.Black,
            alpha = 0.7f
        )
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(boundingBox.left.toFloat(), boundingBox.top.toFloat()),
            size = Size(
                width = (boundingBox.right - boundingBox.left).toFloat(),
                height = (boundingBox.bottom - boundingBox.top).toFloat(),
            ),
            blendMode = BlendMode.Clear,
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(boundingBox.left.toFloat(), boundingBox.top.toFloat()),
            size = Size(
                width = (boundingBox.right - boundingBox.left).toFloat(),
                height = (boundingBox.bottom - boundingBox.top).toFloat(),
            ),
            style = Stroke(width = 4.dp.toPx()),
        )
    }
}