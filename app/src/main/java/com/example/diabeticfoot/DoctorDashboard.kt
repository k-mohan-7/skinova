package com.example.diabeticfoot

import android.util.Log
import androidx.compose.foundation.background
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboard(
    onPatientClick: (Patient) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAlertsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onEditDoctorProfileClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onAboutAppClick: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    var unreadAlertsCount by remember { mutableStateOf(0) }
    
    // Fetch unread alerts count
    LaunchedEffect(selectedTab) {
        cloudUserManager.getAlerts().onSuccess { alerts ->
            unreadAlertsCount = alerts.count { it.isRead == 0 }
        }
    }

    BackHandler {
        if (selectedTab != 0) {
            selectedTab = 0
        } else {
            showExitDialog = true
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "Exit App") },
            text = { Text(text = "Are you sure you want to exit?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        (context as? Activity)?.finish()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            Log.d("DoctorDashboard", "DoctorDashboard Composing")
            val userManager = remember { UserManager(context) }
            val doctorEmail = remember { userManager.getCurrentDoctorEmail() ?: "" }
            Log.d("DoctorDashboard", "Current Doctor Email: $doctorEmail")
            
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4A90E2),
                        selectedTextColor = Color(0xFF4A90E2),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4A90E2),
                        selectedTextColor = Color(0xFF4A90E2),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { 
                        BadgedBox(
                            badge = {
                                if (unreadAlertsCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFF44336)
                                    ) {
                                        Text(
                                            text = if (unreadAlertsCount > 99) "99+" else unreadAlertsCount.toString(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Alerts")
                        }
                    },
                    label = { Text("Alerts") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF4A90E2),
                        selectedTextColor = Color(0xFF4A90E2),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4A90E2),
                        selectedTextColor = Color(0xFF4A90E2),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> DoctorHomeScreenContent(
                    onPatientClick = onPatientClick,
                    onProfileClick = { selectedTab = 1 },
                    onSettingsClick = { selectedTab = 3 }
                )
                1 -> DoctorProfileScreen(
                    showBottomBar = false,
                    onHomeClick = { selectedTab = 0 },
                    onSettingsClick = { selectedTab = 3 },
                    onLogoutClick = onLogoutClick,
                    onEditProfileClick = onEditDoctorProfileClick
                )
                2 -> DoctorAlertsScreen(
                    onBackClick = null // No back arrow in tab
                )
                3 -> DoctorSettingsScreen(
                    onBackClick = null, // No back arrow in tab
                    onProfileClick = { selectedTab = 1 },
                    onAboutAppClick = onAboutAppClick,
                    onPrivacyPolicyClick = onPrivacyPolicyClick,
                    onLogoutClick = onLogoutClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorHomeScreenContent(
    onPatientClick: (Patient) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    var doctorName by remember { mutableStateOf(cloudUserManager.getUserFullName() ?: "Doctor") }
    var searchQuery by remember { mutableStateOf("") }
    var patients by remember { mutableStateOf<List<Patient>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var scheduledVisits by remember { mutableStateOf<List<com.example.diabeticfoot.api.models.PatientVisit>>(emptyList()) }
    var isLoadingVisits by remember { mutableStateOf(true) }
    var showVisitDialog by remember { mutableStateOf(false) }
    var selectedVisit by remember { mutableStateOf<com.example.diabeticfoot.api.models.PatientVisit?>(null) }
    
    // Load patients from backend
    LaunchedEffect(Unit) {
        // Sync profile first
        cloudUserManager.syncDoctorProfile().onSuccess { profileResponse ->
            if (profileResponse.success && profileResponse.userData != null) {
                doctorName = profileResponse.userData.fullName
                Log.d("DoctorHomeScreen", "Profile synced: $doctorName")
            }
        }.onFailure {
            Log.e("DoctorHomeScreen", "Failed to sync profile", it)
        }
        
        Log.d("DoctorHomeScreen", "Loading patients...")
        cloudUserManager.getAllPatients().onSuccess { patientList ->
            Log.d("DoctorHomeScreen", "Loaded ${patientList.size} patients")
            patients = patientList.map { p ->
                Log.d("DoctorHomeScreen", "Patient: ${p.fullName}, Sugar: ${p.lastSugarLevel}, Risk: ${p.riskLevel}")
                Patient(
                    initial = p.fullName.firstOrNull()?.uppercase() ?: "?",
                    name = p.fullName,
                    lastSugar = p.lastSugarLevel?.toInt() ?: 0,
                    riskLevel = if (p.riskLevel.isNullOrEmpty() || p.riskLevel == "Not Assessed") "Assessed" else p.riskLevel,
                    age = p.age?.toString() ?: "N/A",
                    gender = p.gender ?: "Unknown",
                    symptoms = emptyList(), // Symptoms will be loaded in patient details screen
                    phone = p.phone ?: "",
                    imageTime = p.lastUploadDate ?: "",
                    id = p.patientId
                )
            }
            isLoading = false
        }.onFailure {
            isLoading = false
            Log.e("DoctorHomeScreen", "Failed to load patients", it)
        }
        
        // Load scheduled visits
        Log.d("DoctorHomeScreen", "Loading scheduled visits...")
        cloudUserManager.getScheduledVisits().onSuccess { visitList ->
            Log.d("DoctorHomeScreen", "Loaded ${visitList.size} visits")
            scheduledVisits = visitList
            isLoadingVisits = false
        }.onFailure {
            isLoadingVisits = false
            Log.e("DoctorHomeScreen", "Failed to load visits", it)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hello Dr. $doctorName!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAF5FD)) // Light blue background
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Doctor Dashboard",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Text(
                text = if (isLoading) "Loading patients..." else "${patients.size} Assigned Patients",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search patients...", color = Color.Gray) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Gray)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent, // No border as per visual typically
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Patient List or Empty State
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4A90E2))
                }
            } else if (patients.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Patients Yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                        Text(
                            text = "Patients will appear here once they register",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                val filteredPatients = remember(searchQuery, patients) {
                    if (searchQuery.isEmpty()) {
                        patients
                    } else {
                        patients.filter { 
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            it.phone.contains(searchQuery, ignoreCase = true)
                        }
                    }
                }
                
                if (filteredPatients.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No patients found",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                            Text(
                                text = "Try a different search term",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        filteredPatients.forEach { patient ->
                            PatientCard(patient, onClick = { onPatientClick(patient) })
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Visits Planned Section in dedicated box
            if (!isLoadingVisits && scheduledVisits.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Visits Planned",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A90E2)
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Show all visits with status messages
                        scheduledVisits.forEachIndexed { index, visit ->
                            VisitCardWithStatus(
                                visit = visit,
                                onClick = { 
                                    selectedVisit = visit
                                    showVisitDialog = true
                                }
                            )
                            if (index < scheduledVisits.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        // Visit Management Dialog
        if (showVisitDialog && selectedVisit != null) {
            VisitManagementBottomSheet(
                visit = selectedVisit!!,
                onDismiss = { 
                    showVisitDialog = false
                    selectedVisit = null
                },
                onStatusUpdate = { visitId, status, notes ->
                    coroutineScope.launch {
                        cloudUserManager.updateVisitStatus(visitId, status, notes).onSuccess {
                            // Reload visits
                            cloudUserManager.getScheduledVisits().onSuccess { visitList ->
                                scheduledVisits = visitList
                            }
                            showVisitDialog = false
                            selectedVisit = null
                        }
                    }
                },
                onReschedule = { visitId, date, time, notes ->
                    coroutineScope.launch {
                        cloudUserManager.rescheduleVisit(visitId, date, time, notes).onSuccess {
                            // Reload visits
                            cloudUserManager.getScheduledVisits().onSuccess { visitList ->
                                scheduledVisits = visitList
                            }
                            showVisitDialog = false
                            selectedVisit = null
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun PatientCard(patient: Patient, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)), // Light Gray
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = patient.initial,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = patient.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    
                    RiskBadge(level = patient.riskLevel)
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (patient.lastSugar > 0) "Last Skin Score: ${patient.lastSugar} / 100" else "Last Skin Score: No data",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
                
                if (patient.imageTime.isNotEmpty()) {
                    Text(
                        text = "Last upload: ${patient.imageTime}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    )
                }
            }
            
             Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun RiskBadge(level: String) {
    val (bgColor, textColor) = when (level) {
        "Low Risk", "Low" -> Color(0xFFE8F5E9) to Color(0xFF4CAF50) // Green
        "Medium Risk", "Medium", "Moderate" -> Color(0xFFFFF8E1) to Color(0xFFFFC107) // Amber/Yellow
        "High Risk", "High" -> Color(0xFFFFEBEE) to Color(0xFFF44336) // Red
        "Not Assessed" -> Color(0xFFE3F2FD) to Color(0xFF2196F3) // Blue
        else -> Color(0xFFF5F5F5) to Color(0xFF9E9E9E) // Gray
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = level,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun VisitCardWithStatus(
    visit: com.example.diabeticfoot.api.models.PatientVisit,
    onClick: () -> Unit
) {
    data class StatusInfo(val bgColor: Color, val statusColor: Color, val statusText: String, val statusMessage: String)
    
    val statusInfo = when (visit.status) {
        "pending" -> StatusInfo(Color(0xFFFFF8E1), Color(0xFFFF9800), "Pending", "This person's visit is pending")
        "completed" -> StatusInfo(Color(0xFFE8F5E9), Color(0xFF4CAF50), "Completed", "This person's visit has been completed")
        "canceled" -> StatusInfo(Color(0xFFFFEBEE), Color(0xFFF44336), "Canceled", "This person's visit has been canceled")
        "rescheduled" -> StatusInfo(Color(0xFFE3F2FD), Color(0xFF2196F3), "Rescheduled", "This person's visit has been rescheduled")
        else -> StatusInfo(Color(0xFFF5F5F5), Color(0xFF9E9E9E), "Unknown", "Status unknown")
    }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = statusInfo.bgColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Patient Initial
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = visit.patientName.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusInfo.statusColor
                        )
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = visit.patientName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${visit.visitDate} at ${visit.visitTime}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
                        )
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Status Badge
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusInfo.statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusInfo.statusColor,
                            fontSize = 10.sp
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status Message
            Text(
                text = statusInfo.statusMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = statusInfo.statusColor,
                    fontWeight = FontWeight.Medium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitManagementBottomSheet(
    visit: com.example.diabeticfoot.api.models.PatientVisit,
    onDismiss: () -> Unit,
    onStatusUpdate: (Int, String, String?) -> Unit,
    onReschedule: (Int, String, String, String?) -> Unit
) {
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var newDate by remember { mutableStateOf(visit.visitDate) }
    var newTime by remember { mutableStateOf(visit.visitTime) }
    var notes by remember { mutableStateOf(visit.notes ?: "") }
    val sheetState = rememberModalBottomSheetState()
    
    if (showRescheduleDialog) {
        AlertDialog(
            onDismissRequest = { showRescheduleDialog = false },
            title = { Text("Reschedule Visit") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newDate,
                        onValueChange = { newDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newTime,
                        onValueChange = { newTime = it },
                        label = { Text("Time (HH:MM:SS)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    onReschedule(visit.visitId, newDate, newTime, notes.ifBlank { null })
                    showRescheduleDialog = false
                }) {
                    Text("Reschedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Manage Visit",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A90E2)
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Patient Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = visit.patientName.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A90E2)
                        )
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = visit.patientName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    Text(
                        text = visit.patientPhone,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Visit Details
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📅", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Date: ${visit.visitDate}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⏰", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Time: ${visit.visitTime}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (!visit.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Text("📝", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Notes: ${visit.notes}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Update Status",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onStatusUpdate(visit.visitId, "completed", null) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("✓", fontSize = 20.sp)
                        Text("Completed", fontSize = 12.sp)
                    }
                }
                Button(
                    onClick = { onStatusUpdate(visit.visitId, "canceled", null) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("✕", fontSize = 20.sp)
                        Text("Cancel", fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { showRescheduleDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text("📅", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reschedule Visit")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorDashboardPreview() {
    DIABETICFootTheme {
        DoctorDashboard()
    }
}
