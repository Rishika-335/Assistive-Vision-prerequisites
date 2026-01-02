package com.example.assistivevision

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DetectedObjectUi(
    val name: String,
    val confidence: Int
)

data class ObjectDetectionUiState(
    val isLoading: Boolean = false,
    val detectedObjects: List<DetectedObjectUi> = emptyList(),
    val error: String? = null
)

class ObjectDetectionViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(ObjectDetectionUiState())
    val uiState: StateFlow<ObjectDetectionUiState> = _uiState

    /**
     * This will later trigger ML Kit object detection.
     * For now, it just shows loading state.
     */

    fun detectObjects(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = ObjectDetectionUiState(
                isLoading = true
            )
        }
    }

    fun reset() {
        _uiState.value = ObjectDetectionUiState()
    }
}