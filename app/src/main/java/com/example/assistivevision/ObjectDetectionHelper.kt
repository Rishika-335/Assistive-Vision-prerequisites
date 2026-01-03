package com.example.assistivevision

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object ObjectDetectionHelper {

    /**
     * This function will later use ML Kit
     * to detect objects from the bitmap.
     *
     * For now, it returns an empty list.
     */

    suspend fun detectObjects(bitmap: Bitmap) =
        suspendCancellableCoroutine { cont ->

            val image = InputImage.fromBitmap(bitmap, 0)

            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    cont.resume(labels)
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }!!
}