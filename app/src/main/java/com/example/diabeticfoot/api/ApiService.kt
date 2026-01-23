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
    
    @GET("patient/get_symptoms_history.php")
    suspend fun getSymptomsHistory(@Query("patient_id") patientId: Int): Response<SymptomsHistoryResponse>
    
    @GET("patient/get_wound_images_history.php")
    suspend fun getWoundImagesHistory(@Query("patient_id") patientId: Int): Response<WoundImagesHistoryResponse>
    
    @GET("patient/get_doctor_advice.php")
    suspend fun getDoctorAdvice(@Query("patient_id") patientId: Int): Response<DoctorAdviceResponse>
    
    @Multipart
    @POST("patient/upload_wound_image.php")
    suspend fun uploadWoundImage(
        @Part("patient_id") patientId: RequestBody,
        @Part("risk_level") riskLevel: RequestBody,
        @Part("ai_confidence") aiConfidence: RequestBody,
        @Part("is_emergency") isEmergency: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<WoundImageResponse>
    
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
    
    // ==================== Visit Management APIs ====================
    
    @POST("doctor/schedule_visit.php")
    suspend fun scheduleVisit(@Body request: ScheduleVisitRequest): Response<ApiResponse>
    
    @POST("doctor/get_scheduled_visits.php")
    suspend fun getScheduledVisits(@Body request: GetScheduledVisitsRequest): Response<ScheduledVisitsResponse>
    
    @POST("doctor/update_visit_status.php")
    suspend fun updateVisitStatus(@Body request: UpdateVisitStatusRequest): Response<ApiResponse>
    
    @POST("doctor/reschedule_visit.php")
    suspend fun rescheduleVisit(@Body request: RescheduleVisitRequest): Response<ApiResponse>
    
    // ==================== Alert APIs ====================
    
    @GET("doctor/get_alerts.php")
    suspend fun getAlerts(@Query("doctor_id") doctorId: Int): Response<AlertsResponse>
    
    @POST("doctor/mark_alert_read.php")
    suspend fun markAlertRead(@Body request: MarkAlertReadRequest): Response<ApiResponse>
    
    @POST("patient/create_alert.php")
    suspend fun createAlert(@Body request: AlertRequest): Response<ApiResponse>
    
    @POST("patient/update_reminder_status.php")
    suspend fun updateReminderStatus(@Body request: UpdateReminderStatusRequest): Response<ApiResponse>
    
    @POST("patient/update_reminder_status_v2.php")
    suspend fun updateReminderStatusV2(@Body request: UpdateReminderStatusRequestV2): Response<ApiResponse>
    
    @GET("patient/get_reminders_v2.php")
    suspend fun getRemindersV2(@Query("patient_id") patientId: Int): Response<RemindersResponse>
}
