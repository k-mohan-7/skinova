package com.example.diabeticfoot

import android.util.Log
import androidx.compose.foundation.background
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.sp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.Schedule
import com.example.diabeticfoot.api.models.DoctorAdvice
import kotlinx.coroutines.launch

@Composable
fun PatientDashboard(
    onSugarLevelClick: () -> Unit,
    onUploadImageClick: () -> Unit,
    onSymptomsClick: () -> Unit,
    onDoctorAdviceClick: () -> Unit,
    onRemindersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    // Callbacks for Profile and Settings when inside tabs
    onLogoutClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
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
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4A90E2),
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = Color(0xFF4A90E2),
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(if (selectedTab == 1) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4A90E2),
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = Color(0xFF4A90E2),
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(if (selectedTab == 2) Icons.Filled.Notifications else Icons.Outlined.Notifications, contentDescription = "Notifications") },
                    label = { Text("Notifications") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4A90E2),
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = Color(0xFF4A90E2),
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(if (selectedTab == 3) Icons.Filled.Settings else Icons.Outlined.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4A90E2),
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = Color(0xFF4A90E2),
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> PatientHomeScreenContent(
                    onSugarLevelClick = onSugarLevelClick,
                    onUploadImageClick = onUploadImageClick,
                    onSymptomsClick = onSymptomsClick,
                    onDoctorAdviceClick = onDoctorAdviceClick,
                    onRemindersClick = onRemindersClick
                )
                1 -> ProfileScreen(
                    onLogoutClick = onLogoutClick,
                    onSettingsClick = { selectedTab = 3 },
                    onEditProfileClick = onEditProfileClick
                )
                2 -> NotificationsScreen()
                3 -> SettingsScreen(
                    onProfileClick = { selectedTab = 1 },
                    onAboutAppClick = onAboutAppClick,
                    onPrivacyPolicyClick = onPrivacyPolicyClick,
                    onLogoutClick = onLogoutClick
                )
            }
        }
    }
}

@Composable
fun PatientHomeScreenContent(
    onSugarLevelClick: () -> Unit,
    onUploadImageClick: () -> Unit,
    onSymptomsClick: () -> Unit,
    onDoctorAdviceClick: () -> Unit,
    onRemindersClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf(cloudUserManager.getUserFullName() ?: "User") }
    var sugar by remember { mutableIntStateOf(0) }
    var risk by remember { mutableStateOf("No Data") }
    var isLoading by remember { mutableStateOf(true) }
    var doctorAdviceList by remember { mutableStateOf<List<DoctorAdvice>>(emptyList()) }
    
    // Load latest sugar level and doctor advice from backend
    LaunchedEffect(Unit) {
        val userId = cloudUserManager.getLoggedInUserId()
        if (userId > 0) {
            // Sync profile from backend to get latest name
            cloudUserManager.syncPatientProfile().onSuccess { profileResponse ->
                if (profileResponse.success && profileResponse.userData != null) {
                    name = profileResponse.userData.fullName
                    Log.d("PatientHomeScreen", "Profile synced: $name")
                }
            }.onFailure {
                Log.e("PatientHomeScreen", "Failed to sync profile", it)
            }
            
            // Load sugar levels
            cloudUserManager.getSugarLevels().onSuccess { levels ->
                if (levels.isNotEmpty()) {
                    val latestSugar = levels.first()
                    sugar = latestSugar.sugarLevel.toInt()
                    risk = when {
                        sugar < 70 -> "Low Risk"
                        sugar in 70..140 -> "Normal"
                        sugar in 141..200 -> "High Risk"
                        else -> "Critical"
                    }
                    Log.d("PatientHomeScreen", "Loaded sugar: $sugar from backend")
                }
                isLoading = false
            }.onFailure {
                isLoading = false
                Log.e("PatientHomeScreen", "Failed to load sugar levels", it)
            }
            
            // Load doctor advice
            cloudUserManager.getPatientDoctorAdvice().onSuccess { adviceList ->
                doctorAdviceList = adviceList
                Log.d("PatientHomeScreen", "Loaded ${adviceList.size} doctor advice entries")
            }.onFailure { error ->
                Log.e("PatientHomeScreen", "Failed to load doctor advice", error)
            }
        } else {
            isLoading = false
        }
    }
    
    Log.d("PatientHomeScreen", "Displaying dashboard for: $name, Sugar: $sugar, Risk: $risk")

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF5FD)) // Light blue background
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Greeting
            Text(
                text = "Hello, $name \uD83D\uDC4B",

                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Text(
                text = "Here is your daily summary",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Current Status Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Current Status",
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.Gray)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = if (sugar == 0) "--" else "$sugar",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "mg/dL",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = risk,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = when (risk) {
                                    "Low Risk", "Normal" -> Color(0xFF4CAF50)
                                    "High Risk" -> Color(0xFFFF9800)
                                    "Critical" -> Color(0xFFF44336)
                                    else -> Color.Gray
                                }
                            )
                        )
                    }
                    
                    // Pulse Icon representation in a container
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF5F5F5),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.MonitorHeart,
                                contentDescription = "Heartbeat",
                                tint = when(risk) {
                                    "High Risk", "Critical" -> Color(0xFFF44336)
                                    "Medium Risk" -> Color(0xFFFFC107)
                                    else -> Color(0xFF4CAF50)
                                },
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Doctor Advice Section
            if (doctorAdviceList.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.MedicalServices,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Doctor's Advice",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                doctorAdviceList.forEach { advice ->
                    DoctorAdviceCard(advice)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Today's Tasks
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Today's Tasks",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            TaskCard(
                icon = Icons.Rounded.WaterDrop,
                iconBgColor = Color(0xFFEAF5FD),
                iconTint = Color(0xFF4A90E2),
                title = "Check Sugar Level",
                subtitle = "Before breakfast",
                onClick = onSugarLevelClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            TaskCard(
                icon = Icons.Rounded.PhotoCamera,
                iconBgColor = Color(0xFFF3E5F5),
                iconTint = Color(0xFF9C27B0),
                title = "Upload Wound Image",
                subtitle = "Daily check-up",
                onClick = onUploadImageClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            TaskCard(
                icon = Icons.Rounded.ErrorOutline,
                iconBgColor = Color(0xFFFFF3E0),
                iconTint = Color(0xFFFF9800),
                title = "Update Symptoms",
                subtitle = "How do you feel?",
                onClick = onSymptomsClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Quick Access
            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                 QuickAccessCard(
                    icon = Icons.Rounded.Gesture,
                    iconBgColor = Color(0xFFEAF7EB),
                    iconTint = Color(0xFF4CAF50),
                    title = "Doctor Advice",
                    modifier = Modifier.weight(1f).clickable(onClick = onDoctorAdviceClick)
                )
                 QuickAccessCard(
                    icon = Icons.Rounded.Schedule,
                    iconBgColor = Color(0xFFFFFDE7),
                    iconTint = Color(0xFFFBC02D),
                    title = "Reminders",
                    modifier = Modifier.weight(1f).clickable(onClick = onRemindersClick)
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }

@Composable
fun TaskCard(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Go",
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun DoctorAdviceCard(advice: DoctorAdvice) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dr. ${advice.doctorName}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A90E2)
                    )
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Badge
                    val (statusColor, statusBg) = when (advice.status) {
                        "Completed" -> Color(0xFF4CAF50) to Color(0xFFE8F5E9)
                        "Ongoing" -> Color(0xFFFF9800) to Color(0xFFFFF3E0)
                        else -> Color(0xFF2196F3) to Color(0xFFE3F2FD) // Pending
                    }
                    Box(
                        modifier = Modifier
                            .background(statusBg, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = advice.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    
                    // Date Badge
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = advice.adviceDate,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = advice.adviceText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black
                ),
                lineHeight = 22.sp
            )
            
            // Highlight next visit date if present
            if (!advice.nextVisitDate.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarToday,
                        contentDescription = "Next Visit",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Next Visit",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray
                            )
                        )
                        Text(
                            text = advice.nextVisitDate,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF57C00)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickAccessCard(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(140.dp).shadow(4.dp, RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
             Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PatientDashboardPreview() {
    DIABETICFootTheme {
        PatientDashboard(
            onSugarLevelClick = {},
            onUploadImageClick = {},
            onSymptomsClick = {},
            onDoctorAdviceClick = {},
            onRemindersClick = {},
            onProfileClick = {},
            onSettingsClick = {}
        )
    }
}
