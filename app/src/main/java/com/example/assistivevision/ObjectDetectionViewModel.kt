package com.example.assistivevision

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.label.ImageLabel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class ObjectDetectionUiState(
    val isLoading: Boolean = false,
    val detectedObjects: ImageLabel? = null,
    val error: String? = null
)

class ObjectDetectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ObjectDetectionUiState())
    val uiState: StateFlow<ObjectDetectionUiState> = _uiState.asStateFlow()


    fun detectObjects(bitmap: Bitmap) {
        _uiState.value = ObjectDetectionUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val labels = ObjectDetectionHelper.detectObjects(bitmap)
                val bestLabel = labels.maxByOrNull { it.confidence }
                _uiState.value = ObjectDetectionUiState(
                    detectedObjects = bestLabel
                )
            } catch (e: Exception) {
                _uiState.value = ObjectDetectionUiState(
                    error = e.message ?: "Object detection failed"
                )
            }
        }
    }

    fun reset() {
        _uiState.value = ObjectDetectionUiState()
    }
}