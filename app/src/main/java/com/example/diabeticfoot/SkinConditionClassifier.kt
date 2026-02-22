package com.example.diabeticfoot

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class SkinConditionClassifier(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private val modelFileName = "dfu_model_final_with_high_accu_hand_leg.tflite"  // Model for skin condition analysis
    
    // Model specifications
    private val inputSize = 224
    private val pixelSize = 3 // RGB
    private val imageMean = 0f
    private val imageStd = 255f
    
    // Class labels - matches training: High, Low, Moderate severity
    private val labels = arrayOf("High", "Low", "Moderate")
    
    init {
        loadModel()
    }
    
    private fun loadModel() {
        try {
            val assetFileDescriptor = context.assets.openFd(modelFileName)
            val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            
            // Simple options - TFLite 2.15.0 supports FULLY_CONNECTED v12
            val options = Interpreter.Options().apply {
                setNumThreads(4)
                setUseNNAPI(false)
            }
            
            interpreter = Interpreter(modelBuffer, options)
            Log.d("SkinConditionClassifier", "✅ Model loaded successfully - TFLite 2.15.0")
            Log.d("SkinConditionClassifier", "Model has ${interpreter?.inputTensorCount} inputs and ${interpreter?.outputTensorCount} outputs")
        } catch (e: Exception) {
            Log.e("SkinConditionClassifier", "Error loading model: ${e.message}", e)
            Log.e("SkinConditionClassifier", "Stack trace:", e)
        }
    }
    
    fun classifyImage(bitmap: Bitmap): ClassificationResult {
        if (interpreter == null) {
            return ClassificationResult("Error", 0f, "Model not loaded", "High")
        }
        
        try {
            // Convert HARDWARE bitmap to software bitmap for pixel access
            val softwareBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
                bitmap.copy(Bitmap.Config.ARGB_8888, false)
            } else {
                bitmap
            }
            
            // Resize bitmap to 224x224
            val resizedBitmap = Bitmap.createScaledBitmap(softwareBitmap, inputSize, inputSize, true)
            
            // Convert to ByteBuffer
            val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)
            
            // Run inference
            val outputArray = Array(1) { FloatArray(labels.size) }
            interpreter?.run(inputBuffer, outputArray)
            
            // Get results
            val probabilities = outputArray[0]
            val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
            val confidence = probabilities[maxIndex]
            val severityLevel = labels[maxIndex]
            
            Log.d("SkinConditionClassifier", "Classification: $severityLevel with confidence $confidence")
            Log.d("SkinConditionClassifier", "All probabilities: High=${probabilities[0]}, Low=${probabilities[1]}, Moderate=${probabilities[2]}")
            
            return ClassificationResult(
                severityLevel = severityLevel,
                confidence = confidence,
                message = getSeverityMessage(severityLevel, confidence),
                riskLevel = severityLevel
            )
            
        } catch (e: Exception) {
            Log.e("SkinConditionClassifier", "Classification error: ${e.message}", e)
            return ClassificationResult("Error", 0f, "Classification failed: ${e.message}", "High")
        }
    }
    
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * pixelSize)
        byteBuffer.order(ByteOrder.nativeOrder())
        
        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]
                
                // Extract RGB values and normalize
                byteBuffer.putFloat(((value shr 16 and 0xFF) - imageMean) / imageStd)
                byteBuffer.putFloat(((value shr 8 and 0xFF) - imageMean) / imageStd)
                byteBuffer.putFloat(((value and 0xFF) - imageMean) / imageStd)
            }
        }
        
        return byteBuffer
    }
    
    private fun getSeverityMessage(severity: String, confidence: Float): String {
        val confidencePercent = (confidence * 100).toInt()
        
        return when (severity) {
            "Low" -> "Low severity skin condition detected ($confidencePercent% confidence). Monitor regularly and maintain basic skincare routine."
            "Moderate" -> "Moderate severity skin condition detected ($confidencePercent% confidence). Consult dermatologist for proper treatment."
            "High" -> "High severity skin condition detected ($confidencePercent% confidence). URGENT: Seek immediate medical attention!"
            else -> "Unable to classify skin condition. Please try again with a clearer image."
        }
    }
    
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}

data class ClassificationResult(
    val severityLevel: String,
    val confidence: Float,
    val message: String,
    val riskLevel: String
)
