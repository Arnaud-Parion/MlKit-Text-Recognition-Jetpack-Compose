package com.entourage.mlkittextdetectioninbox.mlkit.textrecognition

import android.graphics.Rect
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
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
import androidx.lifecycle.LifecycleOwner
import com.entourage.mlkittextdetectioninbox.mlkit.overlay.BoundingBox
import com.entourage.mlkittextdetectioninbox.mlkit.overlay.BoundingBoxGraphic
import com.entourage.mlkittextdetectioninbox.mlkit.overlay.CoordinateTransformer
import com.entourage.mlkittextdetectioninbox.mlkit.overlay.TextGraphicOverlay

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    viewModel: CameraPreviewViewModel = CameraPreviewViewModel(),
    previewScaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val uiState by viewModel.uiState.collectAsState()
    val sourceInfo by viewModel.sourceInfo.collectAsState()

    val previewView = remember { PreviewView(context) }

    BoxWithConstraints(modifier = modifier) {
        val size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val scanArea = remember(size) { BoundingBox().toRect(size, density) }

        // Transform coordinates reactively in the UI layer
        val transformer = remember(sourceInfo, size) {
            sourceInfo?.let { CoordinateTransformer(it, size, previewScaleType) }
        }

        // 3. Sync the Scan Area to the ViewModel in "Image Space"
        LaunchedEffect(transformer, size) {
            val t = transformer ?: return@LaunchedEffect
            val screenRect = BoundingBox().toRect(size, density)

            // Map the screen box back to image coordinates
            val imageRect = Rect(
                t.inverseTranslateX(screenRect.left.toFloat()).toInt(),
                t.inverseTranslateY(screenRect.top.toFloat()).toInt(),
                t.inverseTranslateX(screenRect.right.toFloat()).toInt(),
                t.inverseTranslateY(screenRect.bottom.toFloat()).toInt()
            )
            viewModel.updateImageBounds(imageRect)
        }

        // Bind Camera UseCases
        LaunchedEffect(cameraProvider) {
            val analysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                viewModel.processImage(imageProxy)
            }

            val previewUseCase = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                previewUseCase,
                analysisUseCase
            )
        }

        // The View Hierarchy
        AndroidView(
            factory = { previewView.apply { scaleType = previewScaleType } },
            modifier = Modifier.fillMaxSize()
        )
        BoundingBoxGraphic(
            boundingBox = scanArea
        )
        if (transformer != null) {
            uiState.inside.forEach {
                TextGraphicOverlay(
                    text = it.text,
                    boundingBox = it.boundingBox ?: Rect(0, 0, 0, 0),
                    transformer = transformer,
                    color = Color.Green
                )
            }
            uiState.outside.forEach {
                TextGraphicOverlay(
                    text = it.text,
                    boundingBox = it.boundingBox ?: Rect(0, 0, 0, 0),
                    transformer = transformer,
                    color = Color.Red
                )
            }
        }
    }
}