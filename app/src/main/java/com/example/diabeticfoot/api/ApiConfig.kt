package com.example.diabeticfoot.api

object ApiConfig {
    // Using actual computer IP for real device testing
    // Make sure your phone and computer are on the same WiFi network
    const val BASE_URL = "http://10.93.42.57/diabetic_foot_api/"
    
    // For emulator use: http://10.0.2.2/diabetic_foot_api/
    
    fun getImageUrl(imagePath: String): String {
        return if (imagePath.startsWith("http")) {
            imagePath
        } else {
            "http://10.93.42.57/diabetic_foot_api/$imagePath"
        }
    }
}
