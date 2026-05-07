package com.entourage.mlkittextdetectioninbox.mlkit.textrecognition

import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraXLivePreviewScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // UI State for the CameraProvider
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    // Initialize the CameraProvider
    LaunchedEffect(Unit) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            cameraProvider = providerFuture.get()
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraProvider != null) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                cameraProvider = cameraProvider!!,
                lifecycleOwner = lifecycleOwner,
                previewScaleType = PreviewView.ScaleType.FILL_CENTER
            )
        } else {
            // Loading state
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}