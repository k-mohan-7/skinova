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
    onUploadImageClick: () -> Unit,
    onSymptomsClick: () -> Unit,
    onDoctorAdviceClick: () -> Unit,
    onRemindersClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf(cloudUserManager.getUserFullName() ?: "User") }
    var risk by remember { mutableStateOf("No Data") }
    var isLoading by remember { mutableStateOf(true) }
    var doctorAdviceList by remember { mutableStateOf<List<DoctorAdvice>>(emptyList()) }
    
    // Load latest skin risk from wound images and doctor advice from backend
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
            
            // Load latest skin risk from wound images
            cloudUserManager.getWoundImagesHistory().onSuccess { images ->
                if (images.isNotEmpty()) {
                    val latestImage = images.first()
                    risk = latestImage.riskLevel ?: "No Data"
                    Log.d("PatientHomeScreen", "Loaded skin risk: $risk from images")
                }
                isLoading = false
            }.onFailure {
                isLoading = false
                Log.e("PatientHomeScreen", "Failed to load wound images", it)
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
    
    Log.d("PatientHomeScreen", "Displaying dashboard for: $name, Skin Risk: $risk")

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

            // Current Status Card — dynamic messages based on latest AI skin analysis
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    // Header row with icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Current Status",
                                style = MaterialTheme.typography.labelLarge.copy(color = Color.Gray)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Today's Skin Health",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = when (risk) {
                                "High", "High Risk", "Critical" -> Color(0xFFFFEBEE)
                                "Moderate", "Medium Risk" -> Color(0xFFFFF3E0)
                                "Low", "Low Risk" -> Color(0xFFE8F5E9)
                                else -> Color(0xFFF5F5F5)
                            },
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.MonitorHeart,
                                    contentDescription = "Skin Status",
                                    tint = when (risk) {
                                        "High", "High Risk", "Critical" -> Color(0xFFF44336)
                                        "Moderate", "Medium Risk" -> Color(0xFFFF9800)
                                        "Low", "Low Risk" -> Color(0xFF4CAF50)
                                        else -> Color(0xFF9E9E9E)
                                    },
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterHorizontally),
                            color = Color(0xFF4A90E2),
                            strokeWidth = 2.dp
                        )
                    } else {
                        // Risk Level Badge
                        Box(
                            modifier = Modifier
                                .background(
                                    color = when (risk) {
                                        "Low", "Low Risk" -> Color(0xFFE8F5E9)
                                        "Moderate", "Medium Risk" -> Color(0xFFFFF3E0)
                                        "High", "High Risk", "Critical" -> Color(0xFFFFEBEE)
                                        else -> Color(0xFFF5F5F5)
                                    },
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = when (risk) {
                                    "No Data", "" -> "No Scan Yet"
                                    else -> risk
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = when (risk) {
                                        "Low", "Low Risk" -> Color(0xFF4CAF50)
                                        "Moderate", "Medium Risk" -> Color(0xFFFF9800)
                                        "High", "High Risk", "Critical" -> Color(0xFFF44336)
                                        else -> Color.Gray
                                    }
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Descriptive status message
                        val msgText = when (risk) {
                            "Low", "Low Risk" ->
                                "Your skin is looking great today! Keep up your skincare routine. 🌟"
                            "Moderate", "Medium Risk" ->
                                "Your skin needs some attention. Monitor closely and consider consulting your dermatologist."
                            "High", "High Risk", "Critical" ->
                                "⚠️ High risk detected! Please follow your doctor's precautions and seek medical attention immediately."
                            else ->
                                "No recent scan found. Upload a skin image to get your risk assessment."
                        }
                        val msgColor = when (risk) {
                            "Low", "Low Risk" -> Color(0xFF2E7D32)
                            "Moderate", "Medium Risk" -> Color(0xFFE65100)
                            "High", "High Risk", "Critical" -> Color(0xFFC62828)
                            else -> Color.Gray
                        }
                        val msgBg = when (risk) {
                            "Low", "Low Risk" -> Color(0xFFF1F8E9)
                            "Moderate", "Medium Risk" -> Color(0xFFFFF8E1)
                            "High", "High Risk", "Critical" -> Color(0xFFFFEBEE)
                            else -> Color(0xFFF5F5F5)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(msgBg, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = msgText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = msgColor,
                                    lineHeight = 18.sp
                                )
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
                icon = Icons.Rounded.PhotoCamera,
                iconBgColor = Color(0xFFF3E5F5),
                iconTint = Color(0xFF9C27B0),
                title = "Upload Skin Image",
                subtitle = "Daily skin check-up",
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

            Spacer(modifier = Modifier.height(16.dp))

            TaskCard(
                icon = Icons.Rounded.MedicalServices,
                iconBgColor = Color(0xFFE8F5E9),
                iconTint = Color(0xFF4CAF50),
                title = "Check Doctor's Advice",
                subtitle = "View today's advice",
                onClick = onDoctorAdviceClick
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
