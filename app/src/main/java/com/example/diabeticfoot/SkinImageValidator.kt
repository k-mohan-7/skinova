package com.example.diabeticfoot

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class SkinImageValidator(private val context: Context) {

    fun validate(imageUri: Uri, onResult: (Boolean) -> Unit) {
        try {
            // Check if the URI is accessible before processing
            val inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                println("SkinImageValidator: Cannot access image URI")
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

                    // Keywords for skin/body image validation
                    // Accept common labels from ML Kit for medical skin condition images
                    val validKeywords = listOf(
                        "Skin", "Hand", "Arm", "Leg", "Face", "Body", "Shoulder",
                        "Neck", "Chest", "Back", "Abdomen", "Thigh", "Calf",
                        "Flesh", "Human body", "Body part", "Limb", "Extremity",
                        "Joint", "Finger", "Wrist", "Elbow", "Knee", "Ankle"
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
                    println("SkinImageValidator: ML Kit failed - ${e.message}")
                    // Fail-safe: allow upload if validation fails
                    onResult(true)
                }
        } catch (e: Exception) {
            e.printStackTrace()
            println("SkinImageValidator: Exception during validation - ${e.message}")
            // Fail-safe: allow upload if validation throws exception
            onResult(true)
        }
    }
}
