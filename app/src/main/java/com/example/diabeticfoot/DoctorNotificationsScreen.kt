package com.example.diabeticfoot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme

data class DoctorNotification(
    val title: String,
    val description: String,
    val timestamp: String,
    val type: NotificationType,
    val actionRequired: Boolean = false
)

enum class NotificationType {
    HIGH_RISK,
    NEW_PATIENT,
    PATIENT_UPDATE,
    ABNORMAL_SUGAR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorNotificationsScreen(
    onBackClick: (() -> Unit)? = null,
    showBottomBar: Boolean = true
) {
    val notifications = listOf(
        DoctorNotification(
            title = "High Risk Detected",
            description = "Critical wound deterioration detected for a patient. Immediate review required.",
            timestamp = "Just now",
            type = NotificationType.HIGH_RISK,
            actionRequired = true
        ),
        DoctorNotification(
            title = "New Patient Assigned",
            description = "A new DFU patient has been assigned to you for follow-up care.",
            timestamp = "10 min ago",
            type = NotificationType.NEW_PATIENT
        ),
        DoctorNotification(
            title = "Patient Update Received",
            description = "Patient has uploaded a new wound image for review.",
            timestamp = "9:30 AM",
            type = NotificationType.PATIENT_UPDATE
        ),
        DoctorNotification(
            title = "Abnormal Skin Score",
            description = "Patient reported an unusual skin condition score. Review recommended.",
            timestamp = "Yesterday",
            type = NotificationType.ABNORMAL_SUGAR
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
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
                .background(Color(0xFFF5F5F5))
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    DoctorNotificationCard(notification)
                }
            }
        }
    }
}

@Composable
fun DoctorNotificationCard(notification: DoctorNotification) {
    val (bgColor, iconColor, icon) = when (notification.type) {
        NotificationType.HIGH_RISK -> Triple(
            Color(0xFFFFF0F0),
            Color(0xFFE53935),
            Icons.Default.Warning
        )
        NotificationType.NEW_PATIENT -> Triple(
            Color(0xFFE3F2FD),
            Color(0xFF1976D2),
            Icons.Default.PersonAdd
        )
        NotificationType.PATIENT_UPDATE -> Triple(
            Color(0xFFF5F5F5),
            Color(0xFF757575),
            Icons.Default.Message
        )
        NotificationType.ABNORMAL_SUGAR -> Triple(
            Color(0xFFFFF8E1),
            Color(0xFFFFA726),
            Icons.Default.WaterDrop
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (notification.actionRequired) {
                        Text(
                            text = "Action Required",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53935),
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF424242),
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notification.timestamp,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (notification.type == NotificationType.HIGH_RISK) 
                            Color(0xFFE53935) else Color(0xFF9E9E9E),
                        fontWeight = if (notification.type == NotificationType.HIGH_RISK)
                            FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorNotificationsScreenPreview() {
    DIABETICFootTheme {
        DoctorNotificationsScreen()
    }
}
