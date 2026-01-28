package com.example.diabeticfoot.api.models

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Any? = null
)

// Login/Register Response
data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("user_type") val userType: String? = null,
    @SerializedName("user_data") val userData: UserData? = null
)

data class UserData(
    @SerializedName("patient_id") val patientId: Int? = null,
    @SerializedName("doctor_id") val doctorId: Int? = null,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String,
    @SerializedName("age") val age: Int? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("specialization") val specialization: String? = null,
    @SerializedName("hospital_name") val hospitalName: String? = null
)

// Patient Registration Request
data class PatientRegisterRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("age") val age: Int,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String = ""
)

// Doctor Registration Request
data class DoctorRegisterRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("specialization") val specialization: String,
    @SerializedName("hospital_name") val hospitalName: String
)

// Login Request
data class LoginRequest(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("password") val password: String,
    @SerializedName("user_type") val userType: String
)

// Sugar Level
data class SugarLevelRequest(
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("sugar_level") val sugarLevel: Float,
    @SerializedName("measurement_time") val measurementTime: String
)

data class SugarLevelResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("sugar_levels") val sugarLevels: List<SugarLevel>? = null
)

data class SugarLevel(
    @SerializedName("sugar_value") val sugarLevel: Float,
    @SerializedName("measurement_date") val measurementDate: String,
    @SerializedName("measurement_time") val measurementTime: String,
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("notes") val notes: String? = null
)

// Symptoms
data class SymptomsRequest(
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("severe_pain") val severePain: Int = 0,
    @SerializedName("moderate_pain") val moderatePain: Int = 0,
    @SerializedName("mild_pain") val mildPain: Int = 0,
    @SerializedName("swelling") val swelling: Int = 0,
    @SerializedName("redness_color_change") val rednessColorChange: Int = 0,
    @SerializedName("additional_notes") val additionalNotes: String = ""
)

// Wound Image Upload
data class WoundImageResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("wound_id") val woundId: Int? = null,
    @SerializedName("image_path") val imagePath: String? = null
)

// Doctor Advice
data class DoctorAdviceRequest(
    @SerializedName("doctor_id") val doctorId: Int,
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("advice_text") val adviceText: String,
    @SerializedName("advice_type") val adviceType: String = "general"
)

data class DoctorAdviceResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<DoctorAdvice>? = null
)

data class DoctorAdvice(
    @SerializedName("advice_id") val adviceId: Int,
    @SerializedName("doctor_name") val doctorName: String,
    @SerializedName("specialization") val specialization: String? = null,
    @SerializedName("advice_text") val adviceText: String,
    @SerializedName("prescription") val prescription: String? = null,
    @SerializedName("advice_type") val adviceType: String? = "general",
    @SerializedName("advice_date") val adviceDate: String = "",
    @SerializedName("advice_time") val adviceTime: String? = null,
    @SerializedName("next_visit_date") val nextVisitDate: String? = null,
    @SerializedName("status") val status: String = "Pending",
    @SerializedName("is_read") val isRead: Int? = 0,
    @SerializedName("created_at") val createdAt: String
)

// Patient List for Doctor
data class PatientListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val patients: List<PatientInfo>? = null
)

data class PatientInfo(
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("age") val age: Int,
    @SerializedName("gender") val gender: String,
    @SerializedName("last_sugar") val lastSugarLevel: Float?,
    @SerializedName("risk_level") val riskLevel: String?,
    @SerializedName("last_upload_date") val lastUploadDate: String?,
    @SerializedName("has_emergency") val hasEmergency: Boolean = false
)

// Patient Details
data class PatientDetailsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("patient_info") val patientInfo: PatientInfo? = null,
    @SerializedName("sugar_levels") val sugarLevels: List<SugarLevel>? = null,
    @SerializedName("symptoms") val symptoms: List<Symptom>? = null,
    @SerializedName("wound_images") val woundImages: List<WoundImage>? = null
)

data class Symptom(
    @SerializedName("symptom_id") val symptomId: Int,
    @SerializedName("symptoms") val symptoms: String,
    @SerializedName("reported_date") val reportedDate: String
)

data class WoundImage(
    @SerializedName("wound_id") val woundId: Int,
    @SerializedName("image_path") val imagePath: String,
    @SerializedName("risk_level") val riskLevel: String,
    @SerializedName("ai_confidence") val aiConfidence: Float,
    @SerializedName("upload_date") val uploadDate: String,
    @SerializedName("upload_time") val uploadTime: String,
    @SerializedName("is_emergency") val isEmergency: Boolean
)

// Emergency Alerts
data class EmergencyAlertsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("alerts") val alerts: List<EmergencyAlert>? = null
)

data class EmergencyAlert(
    @SerializedName("alert_id") val alertId: Int,
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("patient_name") val patientName: String,
    @SerializedName("patient_phone") val patientPhone: String,
    @SerializedName("wound_id") val woundId: Int,
    @SerializedName("risk_level") val riskLevel: String,
    @SerializedName("alert_time") val alertTime: String,
    @SerializedName("status") val status: String
)

// Reminder
data class ReminderRequest(
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("reminder_title") val reminderTitle: String,
    @SerializedName("reminder_time") val reminderTime: String,
    @SerializedName("reminder_date") val reminderDate: String,
    @SerializedName("reminder_type") val reminderType: String = "medication",
    @SerializedName("is_recurring") val isRecurring: Int = 0,
    @SerializedName("recurrence_pattern") val recurrencePattern: String = ""
)

data class RemindersResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val reminders: List<Reminder>? = null
)

data class Reminder(
    @SerializedName("reminder_id") val reminderId: Int,
    @SerializedName("reminder_title") val reminderTitle: String,
    @SerializedName("reminder_time") val reminderTime: String,
    @SerializedName("reminder_date") val reminderDate: String,
    @SerializedName("reminder_type") val reminderType: String? = "medication",
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("status") val status: String? = "pending", // pending, completed, missed, cancelled
    @SerializedName("completed_at") val completedAt: String? = null,
    @SerializedName("is_recurring") val isRecurring: Int? = 0,
    @SerializedName("created_at") val createdAt: String? = null
)

// Update Reminder Status
data class UpdateReminderStatusRequestV2(
    @SerializedName("reminder_id") val reminderId: Int,
    @SerializedName("status") val status: String // pending, completed, missed, cancelled
)

// Symptoms History
data class SymptomsHistoryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("symptoms") val symptoms: List<SymptomDetail>? = null,
    @SerializedName("count") val count: Int = 0
)

data class SymptomDetail(
    @SerializedName("symptom_id") val symptomId: Int,
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("severe_pain") val severePain: Int,
    @SerializedName("moderate_pain") val moderatePain: Int,
    @SerializedName("mild_pain") val mildPain: Int,
    @SerializedName("swelling") val swelling: Int,
    @SerializedName("redness_color_change") val rednessColorChange: Int,
    @SerializedName("additional_notes") val additionalNotes: String?,
    @SerializedName("symptom_date") val symptomDate: String,
    @SerializedName("created_at") val createdAt: String
)

// Wound Images History
data class WoundImagesHistoryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("images") val images: List<WoundImageDetail>? = null,
    @SerializedName("count") val count: Int = 0
)

data class WoundImageDetail(
    @SerializedName("wound_id") val woundId: Int,
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("image_path") val imagePath: String,
    @SerializedName("risk_level") val riskLevel: String,
    @SerializedName("ai_confidence") val aiConfidence: Float,
    @SerializedName("upload_date") val uploadDate: String,
    @SerializedName("upload_time") val uploadTime: String,
    @SerializedName("is_emergency") val isEmergency: Int,
    @SerializedName("created_at") val createdAt: String
)

// Patient Visits
data class ScheduleVisitRequest(
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("doctor_id") val doctorId: Int,
    @SerializedName("visit_date") val visitDate: String,
    @SerializedName("visit_time") val visitTime: String,
    @SerializedName("notes") val notes: String? = null
)

data class UpdateVisitStatusRequest(
    @SerializedName("visit_id") val visitId: Int,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String? = null
)

data class RescheduleVisitRequest(
    @SerializedName("visit_id") val visitId: Int,
    @SerializedName("visit_date") val visitDate: String,
    @SerializedName("visit_time") val visitTime: String,
    @SerializedName("notes") val notes: String? = null
)

data class GetScheduledVisitsRequest(
    @SerializedName("doctor_id") val doctorId: Int
)

data class ScheduledVisitsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("visits") val visits: List<PatientVisit>? = null,
    @SerializedName("count") val count: Int = 0
)

data class PatientVisit(
    @SerializedName("visit_id") val visitId: Int,
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("visit_date") val visitDate: String,
    @SerializedName("visit_time") val visitTime: String,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String?,
    @SerializedName("patient_name") val patientName: String,
    @SerializedName("patient_phone") val patientPhone: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

// Patient Alerts
data class AlertRequest(
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("alert_type") val alertType: String,
    @SerializedName("alert_message") val alertMessage: String,
    @SerializedName("priority") val priority: String = "medium"
)

data class AlertsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("alerts") val alerts: List<PatientAlert>? = null,
    @SerializedName("count") val count: Int = 0
)

data class PatientAlert(
    @SerializedName("alert_id") val alertId: Int,
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("patient_name") val patientName: String,
    @SerializedName("alert_type") val alertType: String,
    @SerializedName("alert_message") val alertMessage: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("is_read") val isRead: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("alert_date") val alertDate: String
)

data class MarkAlertReadRequest(
    @SerializedName("alert_id") val alertId: Int
)

data class UpdateReminderStatusRequest(
    @SerializedName("reminder_id") val reminderId: Int,
    @SerializedName("is_active") val isActive: Int
)

// Update Profile Requests
data class UpdatePatientProfileRequest(
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("age") val age: Int,
    @SerializedName("gender") val gender: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String? = null
)

data class UpdateDoctorProfileRequest(
    @SerializedName("doctor_id") val doctorId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("specialization") val specialization: String,
    @SerializedName("hospital_name") val hospitalName: String,
    @SerializedName("password") val password: String? = null
)

data class UpdateProfileResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("user_data") val userData: UserData? = null
)
