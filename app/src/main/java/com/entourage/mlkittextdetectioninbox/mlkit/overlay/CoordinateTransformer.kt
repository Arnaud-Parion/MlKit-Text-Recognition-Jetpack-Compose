package com.entourage.mlkittextdetectioninbox.mlkit.overlay

import androidx.camera.view.PreviewView
import androidx.compose.ui.geometry.Size

class CoordinateTransformer(
    imageSourceInfo: ImageSourceInfo,
    canvasSize: Size,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER
) {
    private val imageAspectRatio = imageSourceInfo.width.toFloat() / imageSourceInfo.height
    private val canvasAspectRatio = canvasSize.width / canvasSize.height

    private val scaleFactor: Float
    private val postScaleWidthOffset: Float
    private val postScaleHeightOffset: Float

    init {
        val isFit = scaleType == PreviewView.ScaleType.FIT_CENTER
        // Logical check: are we scaling based on width or height?
        // For FIT: we choose the smaller scale factor.
        // For FILL: we choose the larger scale factor.
        val scaleByWidth = if (isFit) {
            canvasAspectRatio <= imageAspectRatio
        } else {
            canvasAspectRatio > imageAspectRatio
        }

        if (scaleByWidth) {
            scaleFactor = canvasSize.width / imageSourceInfo.width
            postScaleHeightOffset = (canvasSize.width / imageAspectRatio - canvasSize.height) / 2
            postScaleWidthOffset = 0f
        } else {
            scaleFactor = canvasSize.height / imageSourceInfo.height
            postScaleWidthOffset = (canvasSize.height * imageAspectRatio - canvasSize.width) / 2
            postScaleHeightOffset = 0f
        }
    }

    // --- Image -> Canvas ---
    fun translateX(x: Float): Float {
        val scaledX = x * scaleFactor
        return scaledX - postScaleWidthOffset
    }

    fun translateY(y: Float): Float {
        return y * scaleFactor - postScaleHeightOffset
    }

    // --- Canvas -> Image (The Inverse) ---
    /**
     * Converts a Canvas X coordinate back to the ML Kit Image X coordinate.
     */
    fun inverseTranslateX(canvasX: Float): Float {
        return (canvasX + postScaleWidthOffset) / scaleFactor
    }

    /**
     * Converts a Canvas Y coordinate back to the ML Kit Image Y coordinate.
     */
    fun inverseTranslateY(canvasY: Float): Float {
        // 1. Undo Offset and 2. Undo Scale
        return (canvasY + postScaleHeightOffset) / scaleFactor
    }
}