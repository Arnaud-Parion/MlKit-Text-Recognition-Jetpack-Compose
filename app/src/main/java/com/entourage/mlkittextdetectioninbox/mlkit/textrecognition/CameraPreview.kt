package com.entourage.mlkittextdetectioninbox.mlkit.textrecognition

import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.entourage.mlkittextdetectioninbox.mlkit.overlay.BoundingBox
import com.entourage.mlkittextdetectioninbox.mlkit.overlay.BoundingBoxGraphic
import com.entourage.mlkittextdetectioninbox.mlkit.overlay.TextGraphicOverlay
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraController: LifecycleCameraController,
    viewModel: CameraPreviewViewModel = CameraPreviewViewModel(),
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val uiState by viewModel.uiState.collectAsState()

    val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.Builder().build()) }
    
    LaunchedEffect(cameraController, textRecognizer) {
        val executor = ContextCompat.getMainExecutor(context)
        cameraController.setImageAnalysisAnalyzer(
            executor,
            MlKitAnalyzer(
                listOf(textRecognizer),
                ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                executor
            ) { result ->
                val textResult = result?.getValue(textRecognizer)
                viewModel.handleResult(textResult)
            }
        )
    }

    BoxWithConstraints(modifier = modifier) {
        val size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val scanArea = remember(size) { BoundingBox().toRect(size, density) }

        // Update the screen space scan area to the ViewModel
        LaunchedEffect(scanArea) {
            val screenRect = Rect(
                scanArea.left.toInt(),
                scanArea.top.toInt(),
                scanArea.right.toInt(),
                scanArea.bottom.toInt()
            )
            viewModel.updateScanArea(screenRect)
        }

        // The View Hierarchy
        AndroidView(
            factory = { 
                PreviewView(context).apply { 
                    controller = cameraController 
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                } 
            },
            modifier = Modifier.fillMaxSize()
        )
        BoundingBoxGraphic(
            boundingBox = scanArea
        )
        uiState.inside.forEach {
            TextGraphicOverlay(
                text = it.text,
                boundingBox = it.boundingBox ?: Rect(0, 0, 0, 0),
                color = Color.Green
            )
        }
        uiState.outside.forEach {
            TextGraphicOverlay(
                text = it.text,
                boundingBox = it.boundingBox ?: Rect(0, 0, 0, 0),
                color = Color.Red
            )
        }
    }
}