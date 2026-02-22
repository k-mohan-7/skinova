package com.example.diabeticfoot.api

object ApiConfig {
    // Using actual computer IP for real device testing (192.168.1.64)
    // Make sure your phone and computer are on the same WiFi network
    const val BASE_URL = "http://192.168.1.64/skinova_api/"
    
    // For emulator use: http://10.0.2.2/skinova_api/
    
    fun getImageUrl(imagePath: String): String {
        return if (imagePath.startsWith("http")) {
            imagePath
        } else {
            "http://192.168.1.64/skinova_api/$imagePath"
        }
    }
}
