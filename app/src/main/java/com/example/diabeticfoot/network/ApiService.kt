package com.example.diabeticfoot.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("register.php")
    fun registerPatient(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>
}
