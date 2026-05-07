package com.entourage.mlkittextdetectioninbox.mlkit.overlay

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.abs

@Composable
fun TextGraphicOverlay(
    text: String,
    boundingBox: Rect, // Rect from ML Kit in image coordinates
    transformer: CoordinateTransformer,
    color: Color = Color.White,
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Map the image coordinates to canvas coordinates
        val left = transformer.translateX(boundingBox.left.toFloat())
        val top = transformer.translateY(boundingBox.top.toFloat())
        val right = transformer.translateX(boundingBox.right.toFloat())
        val bottom = transformer.translateY(boundingBox.bottom.toFloat())

        drawText(
            textMeasurer = textMeasurer,
            text = text,
            topLeft = Offset(minOf(left, right), minOf(top, bottom)),
            style = TextStyle(
                color = color,
            )
        )

        drawRect(
            color = color,
            topLeft = Offset(minOf(left, right), minOf(top, bottom)),
            size = Size(
                width = abs(right - left),
                height = abs(bottom - top)
            ),
            style = Stroke(width = 4f)
        )
    }
}