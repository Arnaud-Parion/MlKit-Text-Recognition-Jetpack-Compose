package com.entourage.mlkittextdetectioninbox.mlkit.textrecognition

import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import com.entourage.mlkittextdetectioninbox.mlkit.overlay.ImageSourceInfo
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CameraPreviewUiState(
    val inside: List<Text.TextBlock> = emptyList(),
    val outside: List<Text.TextBlock> = emptyList(),
)

class CameraPreviewViewModel: ViewModel() {
    private val _uiState = MutableStateFlow<CameraPreviewUiState>(CameraPreviewUiState())
    val uiState: StateFlow<CameraPreviewUiState> = _uiState.asStateFlow()

    private val _sourceInfo = MutableStateFlow<ImageSourceInfo?>(null)
    val sourceInfo = _sourceInfo.asStateFlow()

    // This is the Scan Area mapped back to the Image coordinate system
    private var scanAreaInImageSpace: Rect? = null

    private val imageProcessor = TextRecognitionProcessor(TextRecognizerOptions.Builder().build())

    fun updateImageBounds(imageScanArea: Rect) {
        this.scanAreaInImageSpace = imageScanArea
    }

    @OptIn(ExperimentalGetImage::class)
    fun processImage(imageProxy: ImageProxy) {
        if (_sourceInfo.value == null) {
            val rotation = imageProxy.imageInfo.rotationDegrees
            val info = if (rotation == 0 || rotation == 180) {
                ImageSourceInfo(imageProxy.width, imageProxy.height)
            } else {
                ImageSourceInfo(imageProxy.height, imageProxy.width)
            }
            _sourceInfo.value = info
        }

        try {
            imageProcessor.processImageProxy(imageProxy) { handleResult(it) }
        } catch (e: Exception) {
            Log.e("CameraVM", "Processing failed", e)
        }
    }

    private fun handleResult(
        text: Text,
    ) {
        if (scanAreaInImageSpace == null) {
            _uiState.update { it.copy(inside = emptyList(), outside = text.textBlocks) }
            return
        }

        val (inside, outside) = text.textBlocks.partition { block ->
            val imageRect = block.boundingBox ?: return@partition false
            scanAreaInImageSpace!!.contains(imageRect)
        }

        _uiState.update { it.copy(inside = inside, outside = outside) }
    }
}