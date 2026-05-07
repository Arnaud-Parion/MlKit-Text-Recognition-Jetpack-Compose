package com.entourage.mlkittextdetectioninbox.mlkit.textrecognition

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface

class TextRecognitionProcessor(
  textRecognizerOptions: TextRecognizerOptionsInterface
) {
  private val textRecognizer: TextRecognizer = TextRecognition.getClient(textRecognizerOptions)

  @ExperimentalGetImage
  fun processImageProxy(image: ImageProxy, handleResult: (Text) -> Unit ) {
    textRecognizer.process(InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees))
      .addOnSuccessListener{ results ->
        Log.d("", "On-device Text detection successful")
        handleResult(results)
      }
      .addOnFailureListener { e: Exception ->
        e.printStackTrace()
        Log.w("", "Text detection failed.$e")
      }
      .addOnCompleteListener { image.close() }
  }
}
