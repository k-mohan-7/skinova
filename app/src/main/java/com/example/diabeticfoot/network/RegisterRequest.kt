package com.example.diabeticfoot.network

data class RegisterRequest(
    val role: String = "patient",
    val full_name: String,
    val age: Int,
    val phone: String,
    val password: String,
    val confirm_password: String
)
