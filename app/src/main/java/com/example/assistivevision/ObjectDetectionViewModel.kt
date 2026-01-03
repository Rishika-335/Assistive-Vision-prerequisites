package com.example.assistivevision

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.label.ImageLabel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DetectedObjectUi(
    val name: String,
    val confidence: Int
)

data class ObjectDetectionUiState(
    val isLoading: Boolean = false,
    val detectedObjects: ImageLabel? = null,
    val error: String? = null
)

class ObjectDetectionViewModel : ViewModel() {

    /**
     * This will later trigger ML Kit object detection.
     */

}