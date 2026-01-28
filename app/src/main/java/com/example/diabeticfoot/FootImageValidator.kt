package com.example.diabeticfoot

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class FootImageValidator(private val context: Context) {

    fun validate(imageUri: Uri, onResult: (Boolean) -> Unit) {
        try {
            // Check if the URI is accessible before processing
            val inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                println("FootImageValidator: Cannot access image URI")
                onResult(true) // Fail-safe: allow upload if we can't validate
                return
            }
            inputStream.close()

            val image = InputImage.fromFilePath(context, imageUri)
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    // Logs for debugging
                    val labelsText = labels.joinToString { "${it.text} (${it.confidence})" }
                    println("Image Labels: $labelsText")

                    // Keywords for foot/leg/wound image validation
                    // Accept common labels from ML Kit for medical wound images
                    val validKeywords = listOf(
                        "Foot", "Toe", "Ankle", "Leg", "Barefoot", "Sole", "Heel", 
                        "Human leg", "Sock", "Shoe", "Footwear", "Limb", "Extremity",
                        "Flesh", "Skin", "Human body", "Body part", "Joint"
                    )

                    // Accept if any keyword matches with reasonable confidence (>50%)
                    val isValid = labels.any { label ->
                        validKeywords.any { keyword -> 
                            label.text.contains(keyword, ignoreCase = true)
                        } && label.confidence > 0.5f
                    }
                    
                    onResult(isValid) 
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    println("FootImageValidator: ML Kit failed - ${e.message}")
                    // Fail-safe: allow upload if validation fails
                    onResult(true)
                }
        } catch (e: Exception) {
            e.printStackTrace()
            println("FootImageValidator: Exception during validation - ${e.message}")
            // Fail-safe: allow upload if validation throws exception
            onResult(true)
        }
    }
}
