package com.example.diabeticfoot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diabeticfoot.api.models.PatientAlert
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAlertsScreen(
    onBackClick: (() -> Unit)? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    var alerts by remember { mutableStateOf<List<PatientAlert>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var unreadCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        cloudUserManager.getAlerts().onSuccess { list ->
            alerts = list
            unreadCount = list.count { it.isRead == 0 }
            isLoading = false
        }.onFailure {
            isLoading = false
        }
    }

    // Group alerts by date
    val groupedAlerts = remember(alerts) {
        alerts.groupBy { alert ->
            getDateCategory(alert.alertDate)
        }.toSortedMap(compareBy { 
            when (it) {
                "Today" -> 0
                "Yesterday" -> 1
                else -> 2
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Patient Alerts",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        if (unreadCount > 0) {
                            Text(
                                text = "$unreadCount unread alerts",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A90E2)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF4A90E2)
                    )
                }
                alerts.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Alerts",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Patient activities will appear here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        groupedAlerts.forEach { (dateCategory, alertsForDate) ->
                            item {
                                Text(
                                    text = dateCategory,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF333333),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            // Group by patient within each date
                            val patientGroups = alertsForDate.groupBy { it.patientName }
                            patientGroups.forEach { (patientName, patientAlerts) ->
                                item {
                                    PatientAlertGroup(
                                        patientName = patientName,
                                        alerts = patientAlerts,
                                        onAlertClick = { alert ->
                                            if (alert.isRead == 0) {
                                                coroutineScope.launch {
                                                    cloudUserManager.markAlertRead(alert.alertId).onSuccess {
                                                        // Reload alerts
                                                        cloudUserManager.getAlerts().onSuccess { list ->
                                                            alerts = list
                                                            unreadCount = list.count { it.isRead == 0 }
                                                        }
                                                    }
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
}

@Composable
fun PatientAlertGroup(
    patientName: String,
    alerts: List<PatientAlert>,
    onAlertClick: (PatientAlert) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Patient Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = Color(0xFF4A90E2),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patientName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = "${alerts.size} alert${if (alerts.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height(12.dp))

            // Alerts for this patient
            alerts.forEach { alert ->
                AlertItem(
                    alert = alert,
                    onClick = { onAlertClick(alert) }
                )
                if (alert != alerts.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun AlertItem(
    alert: PatientAlert,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = if (alert.isRead == 0) Color(0xFFFFF3E0) else Color(0xFFF5F5F5),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Alert Icon based on type and priority
            val (icon, iconColor, iconBg) = when (alert.alertType) {
                "wound" -> Triple(Icons.Filled.Warning, getColorForPriority(alert.priority), getBackgroundForPriority(alert.priority))
                "sugar_level" -> Triple(Icons.Filled.Favorite, Color(0xFFE91E63), Color(0xFFFCE4EC))
                "symptom" -> Triple(Icons.Filled.Info, Color(0xFFFF9800), Color(0xFFFFF3E0))
                "emergency" -> Triple(Icons.Filled.Warning, Color(0xFFF44336), Color(0xFFFFEBEE))
                else -> Triple(Icons.Filled.Notifications, Color(0xFF4A90E2), Color(0xFFE3F2FD))
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getAlertTypeLabel(alert.alertType),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = iconColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (alert.priority == "high") {
                        Surface(
                            color = Color(0xFFF44336),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "HIGH",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alert.alertMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTime(alert.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (alert.isRead == 0) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4A90E2))
                )
            }
        }
    }
}

private fun getDateCategory(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val alertDate = inputFormat.parse(dateString)
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        
        val alertCal = Calendar.getInstance().apply { time = alertDate ?: Date() }
        
        when {
            isSameDay(alertCal, today) -> "Today"
            isSameDay(alertCal, yesterday) -> "Yesterday"
            else -> SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(alertDate ?: Date())
        }
    } catch (e: Exception) {
        dateString
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun formatTime(dateTimeString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = inputFormat.parse(dateTimeString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateTimeString
    }
}

private fun getAlertTypeLabel(type: String): String {
    return when (type) {
        "wound" -> "Wound Update"
        "sugar_level" -> "Sugar Level"
        "symptom" -> "New Symptom"
        "emergency" -> "Emergency Alert"
        else -> "Alert"
    }
}

private fun getColorForPriority(priority: String): Color {
    return when (priority) {
        "high" -> Color(0xFFF44336)
        "medium" -> Color(0xFFFF9800)
        "low" -> Color(0xFF4CAF50)
        else -> Color(0xFF4A90E2)
    }
}

private fun getBackgroundForPriority(priority: String): Color {
    return when (priority) {
        "high" -> Color(0xFFFFEBEE)
        "medium" -> Color(0xFFFFF3E0)
        "low" -> Color(0xFFE8F5E9)
        else -> Color(0xFFE3F2FD)
    }
}
