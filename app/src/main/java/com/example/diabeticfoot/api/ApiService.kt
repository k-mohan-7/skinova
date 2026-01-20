package com.example.diabeticfoot.api

import com.example.diabeticfoot.api.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ==================== Authentication ====================
    
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("auth/register.php")
    suspend fun registerPatient(@Body request: PatientRegisterRequest): Response<AuthResponse>
    
    @POST("auth/register.php")
    suspend fun registerDoctor(@Body request: DoctorRegisterRequest): Response<AuthResponse>
    
    // ==================== Patient APIs ====================
    
    @POST("patient/log_sugar_level.php")
    suspend fun logSugarLevel(@Body request: SugarLevelRequest): Response<ApiResponse>
    
    @GET("patient/get_sugar_levels.php")
    suspend fun getSugarLevels(@Query("patient_id") patientId: Int): Response<SugarLevelResponse>
    
    @POST("patient/update_symptoms.php")
    suspend fun updateSymptoms(@Body request: SymptomsRequest): Response<ApiResponse>
    
    @Multipart
    @POST("patient/upload_wound_image.php")
    suspend fun uploadWoundImage(
        @Part("patient_id") patientId: RequestBody,
        @Part("risk_level") riskLevel: RequestBody,
        @Part("ai_confidence") aiConfidence: RequestBody,
        @Part("is_emergency") isEmergency: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<WoundImageResponse>
    
    @GET("patient/get_doctor_advice.php")
    suspend fun getDoctorAdvice(@Query("patient_id") patientId: Int): Response<DoctorAdviceResponse>
    
    @POST("patient/set_reminder.php")
    suspend fun setReminder(@Body request: ReminderRequest): Response<ApiResponse>
    
    @GET("patient/get_reminders.php")
    suspend fun getReminders(@Query("patient_id") patientId: Int): Response<RemindersResponse>
    
    // ==================== Doctor APIs ====================
    
    @GET("doctor/get_all_patients.php")
    suspend fun getAllPatients(@Query("doctor_id") doctorId: Int): Response<PatientListResponse>
    
    @GET("doctor/get_patient_details.php")
    suspend fun getPatientDetails(@Query("patient_id") patientId: Int): Response<PatientDetailsResponse>
    
    @POST("doctor/send_advice.php")
    suspend fun sendAdvice(@Body request: DoctorAdviceRequest): Response<ApiResponse>
    
    @GET("doctor/get_emergency_alerts.php")
    suspend fun getEmergencyAlerts(@Query("doctor_id") doctorId: Int): Response<EmergencyAlertsResponse>
    
    @POST("doctor/resolve_alert.php")
    suspend fun resolveAlert(@Body alertId: Map<String, Int>): Response<ApiResponse>
}
