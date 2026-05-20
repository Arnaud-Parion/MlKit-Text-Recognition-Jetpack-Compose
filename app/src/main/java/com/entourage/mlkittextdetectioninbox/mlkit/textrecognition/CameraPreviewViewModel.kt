package com.entourage.mlkittextdetectioninbox.mlkit.textrecognition

import android.graphics.Rect
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.text.Text
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

    private var scanAreaInScreenSpace: Rect? = null

    fun updateScanArea(scanArea: Rect) {
        this.scanAreaInScreenSpace = scanArea
    }

    fun handleResult(text: Text?) {
        if (text == null) {
            _uiState.update { it.copy(inside = emptyList(), outside = emptyList()) }
            return
        }

        val scanArea = scanAreaInScreenSpace
        if (scanArea == null) {
            _uiState.update { it.copy(inside = emptyList(), outside = text.textBlocks) }
            return
        }

        val (inside, outside) = text.textBlocks.partition { block ->
            val rect = block.boundingBox ?: return@partition false
            scanArea.contains(rect)
        }

        _uiState.update { it.copy(inside = inside, outside = outside) }
    }
}