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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChevronRight
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
                    icon = { Icon(Icons.Filled.Notifications, contentDescription = "Alerts") },
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
                2 -> DoctorNotificationsScreen(
                    onBackClick = null, // No back arrow in tab
                    showBottomBar = false
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
    val userManager = remember { UserManager(context) }
    var searchQuery by remember { mutableStateOf("") }
    
    val patients = remember { 
        Log.d("DoctorHomeScreen", "Fetching patients list")
        userManager.getPatients().also {
            Log.d("DoctorHomeScreen", "Patients list size: ${it.size}")
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hello Doctor!",
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
                text = "4 Assigned Patients",
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

            // Patient List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(patients) { patient ->
                    PatientCard(patient, onClick = { onPatientClick(patient) })
                }
            }
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
                    text = "Last Sugar:   ${patient.lastSugar} mg/dL",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
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
        "Low Risk" -> Color(0xFFE8F5E9) to Color(0xFF4CAF50) // Green
        "Medium Risk" -> Color(0xFFFFF8E1) to Color(0xFFFFC107) // Amber/Yellow
        "High Risk" -> Color(0xFFFFEBEE) to Color(0xFFF44336) // Red
        else -> Color.Gray to Color.White
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

@Preview(showBackground = true)
@Composable
fun DoctorDashboardPreview() {
    DIABETICFootTheme {
        DoctorDashboard()
    }
}
