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
                        // Save user data locally for quick access
                        saveLoggedInUser(
                            identifier,
                            authResponse.userId ?: 0,
                            isPatient,
                            authResponse.userData?.fullName ?: "",
                            authResponse.userData?.phone ?: "",
                            authResponse.userData?.email ?: "",
                            authResponse.userData?.specialization ?: "",
                            authResponse.userData?.hospitalName ?: ""
                        )
                    }
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception(response.message() ?: "Login failed"))
                }
            } catch (e: com.google.gson.stream.MalformedJsonException) {
                Log.e("CloudUserManager", "Server returned invalid response (database error)", e)
                Result.failure(Exception("Server database error. Please contact administrator."))
            } catch (e: java.net.ConnectException) {
                Log.e("CloudUserManager", "Cannot connect to server", e)
                Result.failure(Exception("Cannot connect to server. Check your network connection."))
            } catch (e: java.net.SocketTimeoutException) {
                Log.e("CloudUserManager", "Connection timeout", e)
                Result.failure(Exception("Connection timeout. Please try again."))
            } catch (e: Exception) {
                Log.e("CloudUserManager", "Error during login", e)
                Result.failure(Exception("Login failed: ${e.message ?: "Unknown error"}"))
            }
        }
    
    // ==================== Local Session Management ====================
    
    fun saveLoggedInUser(identifier: String, userId: Int, isPatient: Boolean, fullName: String, phone: String = "", email: String = "", specialization: String = "", hospitalName: String = "") {
        prefs.edit().apply {
            putString("LOGGED_IN_USER", identifier)
            putInt("USER_ID", userId)
            putBoolean("IS_PATIENT", isPatient)
            putString("FULL_NAME", fullName)
            putString("USER_PHONE", phone)
            putString("USER_EMAIL", email)
            putString("SPECIALIZATION", specialization)
            putString("HOSPITAL_NAME", hospitalName)
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
            remove("USER_PHONE")
            remove("USER_EMAIL")
            apply()
        }
    }
    
    fun getUserFullName(): String? {
        return prefs.getString("FULL_NAME", null)
    }
    
    fun getUserPhone(): String? {
        return prefs.getString("USER_PHONE", null)
    }
    
    fun getUserEmail(): String? {
        return prefs.getString("USER_EMAIL", null)
    }
    
    fun getUserSpecialization(): String? {
        return prefs.getString("SPECIALIZATION", null)
    }
    
    fun getUserHospital(): String? {
        return prefs.getString("HOSPITAL_NAME", null)
    }
    
    // ==================== Profile Update Operations ====================
    
    suspend fun updatePatientProfile(
        fullName: String,
        age: Int,
        gender: String,
        phone: String,
        password: String? = null
    ): Result<UpdateProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val request = UpdatePatientProfileRequest(
                patientId = patientId,
                fullName = fullName,
                age = age,
                gender = gender,
                phone = phone,
                password = if (password.isNullOrEmpty()) null else password
            )
            val response = apiService.updatePatientProfile(request)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                // Update local storage with new data
                if (result.success && result.userData != null) {
                    prefs.edit().apply {
                        putString("FULL_NAME", result.userData.fullName)
                        putString("USER_PHONE", result.userData.phone)
                        putString("USER_EMAIL", result.userData.email ?: "")
                        apply()
                    }
                }
                Result.success(result)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error updating patient profile", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateDoctorProfile(
        fullName: String,
        email: String,
        phone: String,
        specialization: String,
        hospitalName: String,
        password: String? = null
    ): Result<UpdateProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val doctorId = getLoggedInUserId()
            val request = UpdateDoctorProfileRequest(
                doctorId = doctorId,
                fullName = fullName,
                email = email,
                phone = phone,
                specialization = specialization,
                hospitalName = hospitalName,
                password = if (password.isNullOrEmpty()) null else password
            )
            val response = apiService.updateDoctorProfile(request)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                // Update local storage with new data
                if (result.success && result.userData != null) {
                    prefs.edit().apply {
                        putString("FULL_NAME", result.userData.fullName)
                        putString("USER_PHONE", result.userData.phone)
                        putString("USER_EMAIL", result.userData.email ?: "")
                        putString("SPECIALIZATION", result.userData.specialization ?: "")
                        putString("HOSPITAL_NAME", result.userData.hospitalName ?: "")
                        apply()
                    }
                }
                Result.success(result)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error updating doctor profile", e)
            Result.failure(e)
        }
    }
    
    // ==================== Profile Sync Operations ====================
    
    suspend fun syncPatientProfile(): Result<UpdateProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val response = apiService.getPatientProfile(patientId)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                // Update local storage with fresh data from backend
                if (result.success && result.userData != null) {
                    prefs.edit().apply {
                        putString("FULL_NAME", result.userData.fullName)
                        putString("USER_PHONE", result.userData.phone)
                        apply()
                    }
                    Log.d("CloudUserManager", "Patient profile synced: ${result.userData.fullName}")
                }
                Result.success(result)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to fetch profile"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error syncing patient profile", e)
            Result.failure(e)
        }
    }
    
    suspend fun syncDoctorProfile(): Result<UpdateProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val doctorId = getLoggedInUserId()
            val response = apiService.getDoctorProfile(doctorId)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                // Update local storage with fresh data from backend
                if (result.success && result.userData != null) {
                    prefs.edit().apply {
                        putString("FULL_NAME", result.userData.fullName)
                        putString("USER_PHONE", result.userData.phone)
                        putString("USER_EMAIL", result.userData.email ?: "")
                        putString("SPECIALIZATION", result.userData.specialization ?: "")
                        putString("HOSPITAL_NAME", result.userData.hospitalName ?: "")
                        apply()
                    }
                    Log.d("CloudUserManager", "Doctor profile synced: ${result.userData.fullName}")
                }
                Result.success(result)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to fetch profile"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error syncing doctor profile", e)
            Result.failure(e)
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
    
    suspend fun getSugarLevels(providedPatientId: Int? = null): Result<List<SugarLevel>> = withContext(Dispatchers.IO) {
        try {
            val patientId = providedPatientId ?: getLoggedInUserId()
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
    
    suspend fun updateSymptoms(
        severePain: Boolean,
        moderatePain: Boolean,
        mildPain: Boolean,
        swelling: Boolean,
        rednessColorChange: Boolean,
        additionalNotes: String
    ): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val request = SymptomsRequest(
                patientId = patientId,
                severePain = if (severePain) 1 else 0,
                moderatePain = if (moderatePain) 1 else 0,
                mildPain = if (mildPain) 1 else 0,
                swelling = if (swelling) 1 else 0,
                rednessColorChange = if (rednessColorChange) 1 else 0,
                additionalNotes = additionalNotes
            )
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
                // Backend returns 'data' field, not 'advices'
                val adviceList = response.body()!!.data ?: emptyList()
                Result.success(adviceList)
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
        type: String,
        isRecurring: Boolean = false,
        recurrencePattern: String = ""
    ): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val request = ReminderRequest(
                patientId = patientId,
                reminderTitle = title,
                reminderTime = time,
                reminderDate = date,
                reminderType = type,
                isRecurring = if (isRecurring) 1 else 0,
                recurrencePattern = recurrencePattern
            )
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
    
    suspend fun getSymptomsHistory(providedPatientId: Int? = null): Result<List<SymptomDetail>> = withContext(Dispatchers.IO) {
        try {
            val patientId = providedPatientId ?: getLoggedInUserId()
            val response = apiService.getSymptomsHistory(patientId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.symptoms ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get symptoms history"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error getting symptoms history", e)
            Result.failure(e)
        }
    }
    
    suspend fun getWoundImagesHistory(providedPatientId: Int? = null): Result<List<WoundImageDetail>> = withContext(Dispatchers.IO) {
        try {
            val patientId = providedPatientId ?: getLoggedInUserId()
            val response = apiService.getWoundImagesHistory(patientId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.images ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get wound images history"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error getting wound images history", e)
            Result.failure(e)
        }
    }
    
    suspend fun getPatientDoctorAdvice(): Result<List<DoctorAdvice>> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val response = apiService.getDoctorAdvice(patientId)
            if (response.isSuccessful && response.body()?.success == true) {
                // Backend returns 'data' field, not 'advices'
                val adviceList = response.body()!!.data ?: emptyList()
                Result.success(adviceList)
            } else {
                Result.failure(Exception("Failed to get doctor advice"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error getting doctor advice", e)
            Result.failure(e)
        }
    }
    
    // ==================== Visit Management Operations ====================
    
    suspend fun scheduleVisit(
        patientId: Int,
        visitDate: String,
        visitTime: String,
        notes: String? = null
    ): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val doctorId = getLoggedInUserId()
            val request = ScheduleVisitRequest(patientId, doctorId, visitDate, visitTime, notes)
            val response = apiService.scheduleVisit(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to schedule visit"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error scheduling visit", e)
            Result.failure(e)
        }
    }
    
    suspend fun getScheduledVisits(): Result<List<PatientVisit>> = withContext(Dispatchers.IO) {
        try {
            val doctorId = getLoggedInUserId()
            val request = GetScheduledVisitsRequest(doctorId)
            val response = apiService.getScheduledVisits(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.visits ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get scheduled visits"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error getting scheduled visits", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateVisitStatus(
        visitId: Int,
        status: String,
        notes: String? = null
    ): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateVisitStatusRequest(visitId, status, notes)
            val response = apiService.updateVisitStatus(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update visit status"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error updating visit status", e)
            Result.failure(e)
        }
    }
    
    suspend fun rescheduleVisit(
        visitId: Int,
        visitDate: String,
        visitTime: String,
        notes: String? = null
    ): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val request = RescheduleVisitRequest(visitId, visitDate, visitTime, notes)
            val response = apiService.rescheduleVisit(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to reschedule visit"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error rescheduling visit", e)
            Result.failure(e)
        }
    }
    
    // ==================== Alert Operations ====================
    
    suspend fun getAlerts(): Result<List<PatientAlert>> = withContext(Dispatchers.IO) {
        try {
            val doctorId = getLoggedInUserId()
            val response = apiService.getAlerts(doctorId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.alerts ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get alerts"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error getting alerts", e)
            Result.failure(e)
        }
    }
    
    suspend fun markAlertRead(alertId: Int): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val request = MarkAlertReadRequest(alertId)
            val response = apiService.markAlertRead(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to mark alert as read"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error marking alert as read", e)
            Result.failure(e)
        }
    }
    
    suspend fun createAlert(
        patientId: Int,
        alertType: String,
        alertMessage: String,
        priority: String = "medium"
    ): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val request = AlertRequest(patientId, alertType, alertMessage, priority)
            val response = apiService.createAlert(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create alert"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error creating alert", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateReminderStatus(reminderId: Int, isActive: Boolean): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateReminderStatusRequest(reminderId, if (isActive) 1 else 0)
            val response = apiService.updateReminderStatus(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update reminder status"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error updating reminder status", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateReminderStatusV2(reminderId: Int, status: String): Result<ApiResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateReminderStatusRequestV2(reminderId, status)
            val response = apiService.updateReminderStatusV2(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update reminder status"))
            }
        } catch (e: Exception) {
            Log.e("CloudUserManager", "Error updating reminder status", e)
            Result.failure(e)
        }
    }
    
    suspend fun getRemindersV2(): Result<List<Reminder>> = withContext(Dispatchers.IO) {
        try {
            val patientId = getLoggedInUserId()
            val response = apiService.getRemindersV2(patientId)
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
}
