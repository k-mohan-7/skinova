package com.example.diabeticfoot

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme
import kotlinx.coroutines.launch

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    
    // Permission launcher for notifications
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        // Global exception handler to catch any ultimate crash cause
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("MainActivity", "UNCUT EXCEPTION in thread ${thread.name}", throwable)
            oldHandler?.uncaughtException(thread, throwable)
        }

        Log.d("MainActivity", "onCreate called")
        enableEdgeToEdge()
        setContent {
            DIABETICFootTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        val navController = rememberNavController()
                        val cloudUserManager = remember { CloudUserManager(this@MainActivity) }
                        
                        // Check if user is already logged in (session cached in SharedPreferences)
                        val loggedInUser = cloudUserManager.getLoggedInUser()
                        
                        // Determine start destination based on login status
                        val startDestination = if (loggedInUser != null) {
                            // User is logged in - skip welcome/login, go directly to dashboard
                            if (cloudUserManager.isPatient()) "patient_dashboard" else "doctor_dashboard"
                        } else {
                            // Not logged in - show welcome screen
                            "welcome"
                        }
                        
                        NavHost(navController = navController, startDestination = startDestination) {
                            composable("welcome") {
                                WelcomeScreen(
                                    onGetStartedClick = { navController.navigate("login") }
                                )
                            }
                            composable("login") {
                                LoginScreen(
                                    onLoginClick = { role ->
                                        Log.d("MainActivity", "Navigating to dashboard for role: $role")
                                        try {
                                            if (role == "Patient") {
                                                navController.navigate("patient_dashboard")
                                            } else {
                                                navController.navigate("doctor_dashboard")
                                            }
                                        } catch (e: Exception) {
                                            Log.e("MainActivity", "Navigation failed", e)
                                        }
                                    },
                                    onPatientRegisterClick = { navController.navigate("register") },
                                    onDoctorRegisterClick = { navController.navigate("doctor_register") }
                                )
                            }
                            composable("register") {
                                RegisterScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onRegisterClick = { 
                                        // TODO: Implement actual registration logic
                                        navController.navigate("login") {
                                            popUpTo("register") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("doctor_register") {
                                DoctorRegisterScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onRegisterClick = { 
                                        // TODO: Implement actual registration logic
                                        navController.navigate("login") {
                                            popUpTo("doctor_register") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("patient_dashboard") {
                                PatientDashboard(
                                    onSugarLevelClick = { navController.navigate("sugar_level") },
                                    onUploadImageClick = { navController.navigate("upload_image") },
                                    onSymptomsClick = { navController.navigate("symptoms") },
                                    onDoctorAdviceClick = { navController.navigate("doctor_advice") },
                                    onRemindersClick = { navController.navigate("reminders") },
                                    onProfileClick = { /* Handled internally by tabs */ },
                                    onSettingsClick = { /* Handled internally by tabs */ },
                                    onNotificationsClick = { /* Handled internally by tabs */ },
                                    onLogoutClick = {
                                        navController.navigate("logout_success") {
                                             popUpTo("patient_dashboard") { inclusive = true }
                                        }
                                    },
                                    onEditProfileClick = { navController.navigate("edit_profile") },
                                    onPrivacyPolicyClick = { navController.navigate("privacy_policy") },
                                    onAboutAppClick = { navController.navigate("about_app") }
                                )
                            }
                            composable("doctor_advice") {
                                DoctorAdviceScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onViewFullHistory = { navController.navigate("advice_history") }
                                )
                            }
                            composable("advice_history") {
                                AdviceHistoryScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            composable("reminders") {
                                RemindersScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            composable("profile") {
                                ProfileScreen(
                                    onBackClick = {
                                        navController.navigate("patient_dashboard") {
                                            popUpTo("patient_dashboard") { inclusive = true }
                                        }
                                    },
                                    onLogoutClick = {
                                        cloudUserManager.clearLoggedInUser()
                                        navController.navigate("logout_success") {
                                             popUpTo("patient_dashboard") { inclusive = true }
                                        }
                                    },
                                    onSettingsClick = { navController.navigate("settings") },
                                    onEditProfileClick = { navController.navigate("edit_profile") }
                                )
                            }
                            composable("logout_success") {
                                LogoutSuccessScreen(
                                    onLoginDataClick = {
                                        navController.navigate("login") {
                                            popUpTo("welcome") { inclusive = false }
                                        }
                                    }
                                )
                            }
                            composable("settings") {
                                SettingsScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onProfileClick = { navController.navigate("profile") },
                                    onAboutAppClick = { navController.navigate("about_app") },
                                    onPrivacyPolicyClick = { navController.navigate("privacy_policy") },
                                    onLogoutClick = {
                                        cloudUserManager.clearLoggedInUser()
                                        navController.navigate("logout_success") {
                                             popUpTo("patient_dashboard") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("edit_profile") {
                                EditProfileScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onSaveClick = { navController.popBackStack() }
                                )
                            }
                            composable("privacy_policy") {
                                PrivacyPolicyScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            composable("sugar_level") {
                                SugarLevelScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onConfirmClick = { sugarLevel, time ->
                                        navController.navigate("confirmSugarLevel/$sugarLevel/$time")
                                    }
                                )
                            }
                            composable(
                                "confirmSugarLevel/{sugarLevel}/{time}",
                                arguments = listOf(
                                    androidx.navigation.navArgument("sugarLevel") { 
                                        type = androidx.navigation.NavType.FloatType 
                                    },
                                    androidx.navigation.navArgument("time") { 
                                        type = androidx.navigation.NavType.StringType 
                                    }
                                )
                            ) { backStackEntry ->
                                val sugarLevel = backStackEntry.arguments?.getFloat("sugarLevel") ?: 0f
                                val time = backStackEntry.arguments?.getString("time") ?: ""
                                
                                ConfirmSugarLevelScreen(
                                    sugarLevel = sugarLevel,
                                    measurementTime = time,
                                    onBackClick = { navController.popBackStack() },
                                    onConfirmClick = { 
                                        navController.navigate("patient_dashboard") {
                                            popUpTo("sugar_level") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("upload_image") {
                                UploadImageScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onSubmitClick = { riskLevel -> 
                                        // riskLevel will be "Low", "Moderate", or "High" from classifier
                                        navController.navigate("analysing/$riskLevel")
                                    }
                                )
                            }
                            composable("symptoms") {
                                SymptomsScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onConfirmClick = { symptoms, notes ->
                                        val symptomsEncoded = java.net.URLEncoder.encode(symptoms.joinToString(","), "UTF-8")
                                        val notesEncoded = java.net.URLEncoder.encode(notes, "UTF-8")
                                        navController.navigate("confirmSymptoms/$symptomsEncoded/$notesEncoded")
                                    }
                                )
                            }
                            composable(
                                "confirmSymptoms/{symptoms}/{notes}",
                                arguments = listOf(
                                    androidx.navigation.navArgument("symptoms") { 
                                        type = androidx.navigation.NavType.StringType 
                                        defaultValue = ""
                                    },
                                    androidx.navigation.navArgument("notes") { 
                                        type = androidx.navigation.NavType.StringType 
                                        defaultValue = ""
                                    }
                                )
                            ) { backStackEntry ->
                                val symptomsEncoded = backStackEntry.arguments?.getString("symptoms") ?: ""
                                val notesEncoded = backStackEntry.arguments?.getString("notes") ?: ""
                                val symptomsString = java.net.URLDecoder.decode(symptomsEncoded, "UTF-8")
                                val notes = java.net.URLDecoder.decode(notesEncoded, "UTF-8")
                                val symptomsList = if (symptomsString.isNotEmpty()) symptomsString.split(",") else emptyList()
                                
                                ConfirmSymptomsScreen(
                                    selectedSymptoms = symptomsList,
                                    additionalNotes = notes,
                                    onBackClick = { navController.popBackStack() },
                                    onConfirmClick = { 
                                        navController.navigate("patient_dashboard") {
                                            popUpTo("patient_dashboard") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable(
                                "analysing/{riskLevel}",
                                arguments = listOf(
                                    androidx.navigation.navArgument("riskLevel") { 
                                        type = androidx.navigation.NavType.StringType 
                                        defaultValue = "Medium"
                                    }
                                )
                            ) { backStackEntry ->
                                val context = androidx.compose.ui.platform.LocalContext.current
                                val detectedRiskLevel = backStackEntry.arguments?.getString("riskLevel") ?: "Medium"
                                
                                AnalysingScreen(
                                    onAnalysisComplete = {
                                        // Risk level will be saved when wound image is uploaded to cloud
                                        // cloudUserManager.uploadWoundImage() handles this
                                        
                                        navController.navigate("analysis_result/$detectedRiskLevel") {
                                            popUpTo("upload_image") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable(
                                "analysis_result/{riskLevel}",
                                arguments = listOf(
                                    androidx.navigation.navArgument("riskLevel") { type = androidx.navigation.NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val riskLevel = backStackEntry.arguments?.getString("riskLevel") ?: "Low"
                                AnalysisResultScreen(
                                    riskLevel = riskLevel,
                                    onViewAlertClick = { 
                                         navController.navigate("immediate_action")
                                    },
                                    onDoneClick = {
                                        navController.navigate("patient_dashboard") {
                                            popUpTo("patient_dashboard") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("immediate_action") {
                                ImmediateActionScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onSendAlertClick = {
                                        // For now, simulate sending alert and return to dashboard
                                        navController.navigate("patient_dashboard") {
                                            popUpTo("patient_dashboard") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("doctor_dashboard") {
                                DoctorDashboard(
                                    onPatientClick = { patient ->
                                        // Navigate with arguments - Encode all string parameters to avoid crashes with spaces/special characters
                                        val encodedName = java.net.URLEncoder.encode(patient.name, "UTF-8")
                                        val encodedPhone = java.net.URLEncoder.encode(patient.phone, "UTF-8")
                                        val encodedInfo = java.net.URLEncoder.encode("Patient Info", "UTF-8")
                                        val encodedSugar = patient.lastSugar
                                        val encodedRisk = java.net.URLEncoder.encode(patient.riskLevel, "UTF-8")
                                        val encodedSymptoms = java.net.URLEncoder.encode("none", "UTF-8")
                                        val encodedTime = java.net.URLEncoder.encode("", "UTF-8")
                                        val encodedUri = java.net.URLEncoder.encode("none", "UTF-8")
                                        val patientId = patient.id
                                        
                                        navController.navigate("patient_details/$encodedName/$encodedPhone/$encodedInfo/$encodedSugar/$encodedRisk/$encodedSymptoms/$encodedTime/$encodedUri/$patientId")
                                    },
                                    onProfileClick = { navController.navigate("doctor_profile") },
                                    onAlertsClick = { navController.navigate("doctor_alerts") },
                                    onSettingsClick = { navController.navigate("doctor_settings") },
                                    onLogoutClick = {
                                        cloudUserManager.clearLoggedInUser()
                                        navController.navigate("logout_success") {
                                            popUpTo("doctor_dashboard") { inclusive = true }
                                        }
                                    },
                                    onEditDoctorProfileClick = { navController.navigate("edit_doctor_profile") },
                                    onPrivacyPolicyClick = { navController.navigate("privacy_policy") },
                                    onAboutAppClick = { navController.navigate("about_app") }
                                )
                            }
                            composable("doctor_alerts") {
                                DoctorAlertsScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            composable(
                                "patient_details/{name}/{phone}/{info}/{sugar}/{risk}/{symptoms}/{imageTime}/{imageUri}/{patientId}",
                                arguments = listOf(
                                    androidx.navigation.navArgument("name") { type = androidx.navigation.NavType.StringType },
                                    androidx.navigation.navArgument("phone") { type = androidx.navigation.NavType.StringType },
                                    androidx.navigation.navArgument("info") { type = androidx.navigation.NavType.StringType },
                                    androidx.navigation.navArgument("sugar") { type = androidx.navigation.NavType.StringType },
                                    androidx.navigation.navArgument("risk") { type = androidx.navigation.NavType.StringType },
                                    androidx.navigation.navArgument("symptoms") { type = androidx.navigation.NavType.StringType },
                                    androidx.navigation.navArgument("imageTime") { type = androidx.navigation.NavType.StringType },
                                    androidx.navigation.navArgument("imageUri") { type = androidx.navigation.NavType.StringType },
                                    androidx.navigation.navArgument("patientId") { type = androidx.navigation.NavType.IntType }
                                )
                            ) { backStackEntry ->
                                val name = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("name") ?: "", "UTF-8")
                                val phone = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("phone") ?: "", "UTF-8")
                                val info = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("info") ?: "", "UTF-8")
                                val sugar = backStackEntry.arguments?.getString("sugar") ?: ""
                                val risk = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("risk") ?: "", "UTF-8")
                                val symptomsString = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("symptoms") ?: "none", "UTF-8")
                                val imageTime = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("imageTime") ?: "", "UTF-8")
                                val encodedUri = backStackEntry.arguments?.getString("imageUri") ?: "none"
                                val imageUri = if (encodedUri == "none") null else java.net.URLDecoder.decode(encodedUri, "UTF-8")
                                val patientId = backStackEntry.arguments?.getInt("patientId") ?: 0
                                
                                val symptoms = if (symptomsString == "none") emptyList() else symptomsString.split(",").filter { it.isNotEmpty() }
 
                                PatientDetailsScreen(
                                    name = name,
                                    info = info,
                                    sugarLevel = sugar,
                                    riskLevel = risk,
                                    symptoms = symptoms,
                                    imageTime = imageTime,
                                    imageUri = imageUri,
                                    patientId = patientId,
                                    onBackClick = { navController.popBackStack() },
                                    onSendAdviceClick = {
                                        navController.navigate("send_advice/$name/$phone/$patientId")
                                    },
                                    onSeeAllSugarReports = { id ->
                                        navController.navigate("sugar_history/$id")
                                    },
                                    onSeeAllSymptoms = { id ->
                                        navController.navigate("symptoms_history/$id")
                                    },
                                    onSeeAllImages = { id ->
                                        navController.navigate("images_history/$id")
                                    }
                                )
                            }
                            
                            // History screens
                            composable(
                                "sugar_history/{patientId}",
                                arguments = listOf(
                                    androidx.navigation.navArgument("patientId") { type = androidx.navigation.NavType.IntType }
                                )
                            ) { backStackEntry ->
                                val patientId = backStackEntry.arguments?.getInt("patientId") ?: 0
                                SugarLevelHistoryScreen(
                                    patientId = patientId,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            composable(
                                "symptoms_history/{patientId}",
                                arguments = listOf(
                                    androidx.navigation.navArgument("patientId") { type = androidx.navigation.NavType.IntType }
                                )
                            ) { backStackEntry ->
                                val patientId = backStackEntry.arguments?.getInt("patientId") ?: 0
                                SymptomsHistoryScreen(
                                    patientId = patientId,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            composable(
                                "images_history/{patientId}",
                                arguments = listOf(
                                    androidx.navigation.navArgument("patientId") { type = androidx.navigation.NavType.IntType }
                                )
                            ) { backStackEntry ->
                                val patientId = backStackEntry.arguments?.getInt("patientId") ?: 0
                                WoundImagesHistoryScreen(
                                    patientId = patientId,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            
                            composable("doctor_profile") {
                                DoctorProfileScreen(
                                    onHomeClick = { navController.navigate("doctor_dashboard") },
                                    onSettingsClick = { navController.navigate("doctor_settings") },
                                    onLogoutClick = {
                                        cloudUserManager.clearLoggedInUser()
                                        navController.navigate("logout_success") {
                                             popUpTo("doctor_dashboard") { inclusive = true }
                                        }
                                    },
                                    onEditProfileClick = { navController.navigate("edit_doctor_profile") }
                                )
                            }
                            composable("doctor_settings") {
                                DoctorSettingsScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onProfileClick = { navController.navigate("doctor_profile") },
                                    onAboutAppClick = { navController.navigate("about_app") },
                                    onPrivacyPolicyClick = { navController.navigate("privacy_policy") },
                                    onLogoutClick = {
                                        cloudUserManager.clearLoggedInUser()
                                        navController.navigate("logout_success") {
                                             popUpTo("doctor_dashboard") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("edit_doctor_profile") {
                                EditDoctorProfileScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onSaveClick = { navController.popBackStack() }
                                )
                            }

                            composable("about_app") {
                                AboutAppScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(
                                "send_advice/{name}/{phone}/{patientId}",
                                arguments = listOf(
                                    androidx.navigation.navArgument("name") { type = androidx.navigation.NavType.StringType },
                                    androidx.navigation.navArgument("phone") { type = androidx.navigation.NavType.StringType },
                                    androidx.navigation.navArgument("patientId") { type = androidx.navigation.NavType.IntType }
                                )
                            ) { backStackEntry ->
                                val name = backStackEntry.arguments?.getString("name") ?: ""
                                val phone = backStackEntry.arguments?.getString("phone") ?: ""
                                val patientId = backStackEntry.arguments?.getInt("patientId") ?: 0
                                val context = androidx.compose.ui.platform.LocalContext.current
                                val cloudUserManager = remember { CloudUserManager(context) }
                                val coroutineScope = rememberCoroutineScope()

                                SendAdviceScreen(
                                    patientName = name,
                                    onBackClick = { navController.popBackStack() },
                                    onSendClick = { notes, medication, dosage, nextVisit ->
                                        // Build advice text from all fields
                                        val adviceText = buildString {
                                            if (notes.isNotBlank()) {
                                                append("Clinical Notes: $notes\n")
                                            }
                                            if (medication.isNotBlank()) {
                                                append("Medication: $medication\n")
                                            }
                                            if (dosage.isNotBlank()) {
                                                append("Dosage: $dosage\n")
                                            }
                                            if (nextVisit.isNotBlank()) {
                                                append("Next Visit: $nextVisit")
                                            }
                                        }
                                        
                                        // Send advice to patient via API
                                        coroutineScope.launch {
                                            cloudUserManager.sendAdvice(patientId, adviceText).onSuccess {
                                                android.widget.Toast.makeText(context, "Advice sent successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                                navController.popBackStack()
                                            }.onFailure {
                                                android.widget.Toast.makeText(context, "Failed to send advice: ${it.message}", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
