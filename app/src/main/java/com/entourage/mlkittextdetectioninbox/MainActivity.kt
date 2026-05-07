package com.entourage.mlkittextdetectioninbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.entourage.mlkittextdetectioninbox.mlkit.textrecognition.CameraXLivePreviewScreen
import com.entourage.mlkittextdetectioninbox.ui.theme.MlKitTextDetectionInBoxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MlKitTextDetectionInBoxTheme {
                CameraXLivePreviewScreen()
            }
        }
    }
}