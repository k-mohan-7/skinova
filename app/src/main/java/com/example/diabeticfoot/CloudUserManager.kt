package com.example.diabeticfoot

import android.content.Context
import android.util.Log
import com.example.diabeticfoot.api.RetrofitClient
import com.example.diabeticfoot.api.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CloudUserManager(private val context: Context) {
    private val apiService = RetrofitClient.apiService
    private val prefs = context.getSharedPreferences("DFU_CARE_USERS", Context.MODE_PRIVATE)
    
    // ==================== Authentication ====================
    
    suspend fun registerPatient(
        phone: String,
        password: String,
        name: String,
        age: String,
        gender: String
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = PatientRegisterRequest(
                fullName = name,
                phone = phone,
                password = password,
                age = age.toIntOrNull() ?: 0,
                gender = gender,
                email = ""
            )
            val response = apiService.registerPatient(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error registering patient", e)
            Result.failure(e)
        }
    }
    
    suspend fun registerDoctor(
        email: String,
        password: String,
        name: String,
        specialization: String,
        hospital: String,
        phone: String
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = DoctorRegisterRequest(
                fullName = name,
                email = email,
                phone = phone,
                password = password,
                specialization = specialization,
                hospitalName = hospital
            )
            val response = apiService.registerDoctor(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error registering doctor", e)
            Result.failure(e)
        }
    }
    
    suspend fun login(identifier: String, password: String, isPatient: Boolean): Result<AuthResponse> = 
        withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(
                    identifier = identifier,
                    password = password,
                    userType = if (isPatient) "patient" else "doctor"
                )
                val response = apiService.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    if (authResponse.success) {
                        // Save user ID locally for quick access
                        saveLoggedInUser(
                            identifier,
                            authResponse.userId ?: 0,
                            isPatient,
                            authResponse.userData?.fullName ?: ""
                        )
                    }
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception(response.message() ?: "Login failed"))
                }
            } catch (e: Exception) {
                Log.e("CloudUserManager", "Error during login", e)
                Result.failure(e)
            }
        }
    
    // ==================== Local Session Management ====================
    
    fun saveLoggedInUser(identifier: String, userId: Int, isPatient: Boolean, fullName: String) {
        prefs.edit().apply {
            putString("LOGGED_IN_USER", identifier)
            putInt("USER_ID", userId)
            putBoolean("IS_PATIENT", isPatient)
            putString("FULL_NAME", fullName)
            apply()
        }
    }
    
    fun getLoggedInUserId(): Int {
        return prefs.getInt("USER_ID", 0)
    }
    
    fun isPatient(): Boolean {
        return prefs.getBoolean("IS_PATIENT", true)
    }
    
    fun getLoggedInUserIdentifier(): String? {
        return prefs.getString("LOGGED_IN_USER", null)
    }
    
    fun getLoggedInUser(): Pair<String?, Boolean>? {
        val user = prefs.getString("LOGGED_IN_USER", null)
        return if (user != null) {
            val isPatient = prefs.getBoolean("IS_PATIENT", true)
            Pair(user, isPatient)
        } else null
    }
    
    fun clearLoggedInUser() {
        prefs.edit().apply {
            remove("LOGGED_IN_USER")
            remove("USER_ID")
            remove("IS_PATIENT")
            remove("FULL_NAME")
            apply()
        }
    }
    
    // ==================== Patient Operations ====================
    
    suspend fun logSugarLevel(sugarLevel: Float, measurementTime: String): Result<ApiResponse> = 
        withContext(Dispatchers.IO) {
            try {
                val patientId = getLoggedInUserId()
                val request = SugarLevelRequest(patientId, sugarLevel, measurementTime)
                val response = apiService.logSugarLevel(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to log sugar level"))
                }
            } catch (e: Exception) {
                Log.e("CloudUserManager", "Error logging sugar level", e)
                Result.failure(e)
            }
        }
    
    suspend fun getSugarLevels(): Result<List<SugarLevel>> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val response = apiService.getSugarLevels(patientId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.sugarLevels ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get sugar levels"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error getting sugar levels", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateSymptoms(symptoms: String): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val request = SymptomsRequest(patientId, symptoms)
            val response = apiService.updateSymptoms(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update symptoms"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error updating symptoms", e)
            Result.failure(e)
        }
    }
    
    suspend fun uploadWoundImage(
        imageFile: File,
        riskLevel: String,
        aiConfidence: Float,
        isEmergency: Boolean
    ): Result<WoundImageResponse> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            
            val patientIdBody = patientId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val riskLevelBody = riskLevel.toRequestBody("text/plain".toMediaTypeOrNull())
            val confidenceBody = aiConfidence.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val emergencyBody = (if (isEmergency) "1" else "0").toRequestBody("text/plain".toMediaTypeOrNull())
            
            val response = apiService.uploadWoundImage(
                patientIdBody,
                riskLevelBody,
                confidenceBody,
                emergencyBody,
                imagePart
            )
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to upload wound image"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error uploading wound image", e)
            Result.failure(e)
        }
    }
    
    suspend fun getDoctorAdvice(): Result<List<DoctorAdvice>> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val response = apiService.getDoctorAdvice(patientId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.advices ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get doctor advice"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error getting doctor advice", e)
            Result.failure(e)
        }
    }
    
    suspend fun setReminder(
        title: String,
        time: String,
        date: String,
        type: String
    ): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val request = ReminderRequest(patientId, title, time, date, type)
            val response = apiService.setReminder(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to set reminder"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error setting reminder", e)
            Result.failure(e)
        }
    }
    
    suspend fun getReminders(): Result<List<Reminder>> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val response = apiService.getReminders(patientId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.reminders ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get reminders"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error getting reminders", e)
            Result.failure(e)
        }
    }
    
    // ==================== Doctor Operations ====================
    
    suspend fun getAllPatients(): Result<List<PatientInfo>> = withContext(Dispatchers.IO) {
        try {
            val doctorId = getLoggedInUserId()
            val response = apiService.getAllPatients(doctorId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.patients ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get patients"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error getting patients", e)
            Result.failure(e)
        }
    }
    
    suspend fun getPatientDetails(patientId: Int): Result<PatientDetailsResponse> = 
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPatientDetails(patientId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to get patient details"))
                }
            } catch (e: Exception) {
                Log.e("CloudUserManager", "Error getting patient details", e)
                Result.failure(e)
            }
        }
    
    suspend fun sendAdvice(patientId: Int, adviceText: String, adviceType: String = "general"): 
        Result<ApiResponse> = withContext(Dispatchers.IO) {
            try {
                val doctorId = getLoggedInUserId()
                val request = DoctorAdviceRequest(doctorId, patientId, adviceText, adviceType)
                val response = apiService.sendAdvice(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to send advice"))
                }
            } catch (e: Exception) {
                Log.e("CloudUserManager", "Error sending advice", e)
                Result.failure(e)
            }
        }
    
    suspend fun getEmergencyAlerts(): Result<List<EmergencyAlert>> = withContext(Dispatchers.IO) {
        try {
            val doctorId = getLoggedInUserId()
            val response = apiService.getEmergencyAlerts(doctorId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.alerts ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get emergency alerts"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error getting emergency alerts", e)
            Result.failure(e)
        }
    }
}
