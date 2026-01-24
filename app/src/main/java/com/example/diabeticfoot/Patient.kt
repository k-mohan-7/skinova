package com.example.diabeticfoot

data class Patient(
    val initial: String,
    val name: String,
    val lastSugar: Int, // mg/dL
    val riskLevel: String, // "Low Risk", "Medium Risk", "High Risk"
    val age: String,
    val gender: String,
    val symptoms: List<String>,
    val phone: String,
    val imageTime: String,
    val imageUri: String? = null,
    val id: Int = 0
)
