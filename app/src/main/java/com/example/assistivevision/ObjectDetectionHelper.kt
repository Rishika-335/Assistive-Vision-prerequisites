package com.example.assistivevision

import android.graphics.Bitmap

object ObjectDetectionHelper {

    /**
     * This function will later use ML Kit
     * to detect objects from the bitmap.
     *
     * For now, it returns an empty list.
     */

    suspend fun detectObjects(
        bitmap: Bitmap
    ): List<DetectedObjectUi> {
        // ML Kit logic will be added live
        return emptyList()
    }
}