package com.example.diabeticfoot

import android.content.Context
import android.util.Log

/**
 * DEPRECATED: This is a legacy compatibility layer.
 * All NEW features should use CloudUserManager instead.
 * 
 * This class only provides basic session management for screens
 * that haven't been fully migrated yet. All data is now stored in cloud.
 */
class UserManager(context: Context) {
    private val cloudUserManager = CloudUserManager(context)
    
    // Session Management (delegates to CloudUserManager)
    fun getCurrentPatientPhone(): String? {
        return cloudUserManager.getLoggedInUserIdentifier()
    }
    
    fun getCurrentDoctorEmail(): String? {
        return cloudUserManager.getLoggedInUserIdentifier()
    }
    
    fun clearLoggedInUser() {
        cloudUserManager.clearLoggedInUser()
    }
    
    // These methods are deprecated - data is now stored in cloud via PHP API
    fun getPatientName(phone: String?): String {
        Log.w("UserManager", "DEPRECATED: getPatientName() - Use CloudUserManager.getPatientDetails() instead")
        return "User" // Placeholder - should fetch from cloud
    }
    
    fun getPatientAge(phone: String?): String {
        Log.w("UserManager", "DEPRECATED: getPatientAge() - Use CloudUserManager.getPatientDetails() instead")
        return "0" // Placeholder
    }
    
    fun getPatientGender(phone: String?): String {
        Log.w("UserManager", "DEPRECATED: getPatientGender() - Use CloudUserManager.getPatientDetails() instead")
        return "Male" // Placeholder
    }
    
    fun getDoctorName(email: String?): String {
        Log.w("UserManager", "DEPRECATED: getDoctorName() - Use CloudUserManager instead")
        return "Doctor" // Placeholder
    }
    
    fun getDoctorSpec(email: String?): String {
        Log.w("UserManager", "DEPRECATED: getDoctorSpec() - Use CloudUserManager instead")
        return "General" // Placeholder
    }
    
    fun getDoctorHospital(email: String?): String {
        Log.w("UserManager", "DEPRECATED: getDoctorHospital() - Use CloudUserManager instead")
        return "Hospital" // Placeholder
    }
    
    fun getDoctorPhone(email: String?): String {
        Log.w("UserManager", "DEPRECATED: getDoctorPhone() - Use CloudUserManager instead")
        return "" // Placeholder
    }
    
    fun updatePatientProfile(phone: String, name: String, age: String, gender: String, address: String = ""): Boolean {
        Log.w("UserManager", "DEPRECATED: updatePatientProfile() - Data should be updated via cloud API")
        return true // Just return success - actual update should be via cloud
    }
    
    fun updateDoctorProfile(email: String, name: String, specialization: String, hospital: String, phone: String): Boolean {
        Log.w("UserManager", "DEPRECATED: updateDoctorProfile() - Data should be updated via cloud API")
        return true // Just return success
    }
    
    fun saveSugarLevel(phone: String, level: String, date: String = "", time: String = "") {
        Log.w("UserManager", "DEPRECATED: saveSugarLevel() - Use CloudUserManager.logSugarLevel() instead")
        // Do nothing - should use CloudUserManager
    }
    
    fun saveSymptoms(phone: String, symptoms: String, date: String = "", time: String = "") {
        Log.w("UserManager", "DEPRECATED: saveSymptoms() - Use CloudUserManager.updateSymptoms() instead")
        // Do nothing
    }
    
    fun saveDoctorAdvice(phone: String, advice: String, date: String = "", time: String = "", doctorName: String = "") {
        Log.w("UserManager", "DEPRECATED: saveDoctorAdvice() - Use CloudUserManager.sendAdvice() instead")
        // Do nothing
    }
    
    fun getDoctorAdvice(phone: String?): List<Map<String, Any>> {
        Log.w("UserManager", "DEPRECATED: getDoctorAdvice() - Use CloudUserManager.getDoctorAdvice() instead")
        return emptyList() // Should fetch from cloud
    }
    
    fun saveReminderStatus(reminderType: String, enabled: Boolean, time: String = "") {
        Log.w("UserManager", "DEPRECATED: saveReminderStatus() - Use CloudUserManager.setReminder() instead")
        // Do nothing
    }
    
    fun getReminderStatus(reminderType: String, defaultTime: String = ""): Boolean {
        Log.w("UserManager", "DEPRECATED: getReminderStatus() - Use CloudUserManager.getReminders() instead")
        return false // Placeholder
    }
    
    fun registerDoctor(email: String, password: String, name: String, specialization: String, hospital: String, phone: String): Boolean {
        Log.w("UserManager", "DEPRECATED: registerDoctor() - Use CloudUserManager.registerDoctor() instead")
        return true // Placeholder
    }
    
    fun saveRiskLevel(phone: String, riskLevel: String) {
        Log.w("UserManager", "DEPRECATED: saveRiskLevel() - Risk level is saved automatically with wound upload")
        // Do nothing - risk level saved with uploadWoundImage()
    }
    
    fun saveImageUploaded(phone: String, uploaded: Boolean) {
        Log.w("UserManager", "DEPRECATED: saveImageUploaded() - Handled by CloudUserManager.uploadWoundImage()")
        // Do nothing
    }
    
    fun savePatientImageUri(phone: String, uri: String) {
        Log.w("UserManager", "DEPRECATED: savePatientImageUri() - Use CloudUserManager.uploadWoundImage() instead")
        // Do nothing
    }
    
    fun getPatients(): List<Patient> {
        Log.w("UserManager", "DEPRECATED: getPatients() - Use CloudUserManager.getAllPatients() instead")
        return emptyList() // Should fetch from cloud
    }
}
