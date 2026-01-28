package com.example.diabeticfoot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.diabeticfoot.CloudUserManager
import com.example.diabeticfoot.api.models.Reminder
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onBackClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    var reminders by remember { mutableStateOf<List<Reminder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddReminderDialog by remember { mutableStateOf(false) }
    
    // Load reminders from backend
    LaunchedEffect(Unit) {
        cloudUserManager.getRemindersV2().onSuccess { list ->
            reminders = list
            isLoading = false
        }.onFailure {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daily Reminders",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A90E2)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF4A90E2)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddReminderDialog = true },
                containerColor = Color(0xFF4A90E2),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Reminder"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAF5FD)) // Light blue background
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4A90E2))
                }
            } else if (reminders.isEmpty()) {
                // Empty state
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Medication,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Reminders Set",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add a reminder to stay on track with your health routine",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                reminders.forEach { reminder ->
                    ReminderCardV2(
                        icon = Icons.Rounded.Medication,
                        iconBgColor = Color(0xFFE3F2FD),
                        iconTint = Color(0xFF4A90E2),
                        reminder = reminder,
                        onStatusChange = { newStatus ->
                            coroutineScope.launch {
                                cloudUserManager.updateReminderStatusV2(reminder.reminderId, newStatus).onSuccess {
                                    // Reload reminders
                                    cloudUserManager.getRemindersV2().onSuccess { list ->
                                        reminders = list
                                    }
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Add Reminder Dialog
        if (showAddReminderDialog) {
            AddReminderDialog(
                onDismiss = { showAddReminderDialog = false },
                onConfirm = { title, date, time, isRecurring, recurrencePattern ->
                    coroutineScope.launch {
                        cloudUserManager.setReminder(
                            title = title,
                            time = time,
                            date = date,
                            type = "medication",
                            isRecurring = isRecurring,
                            recurrencePattern = recurrencePattern
                        ).onSuccess { response ->
                            // Schedule notification
                            val notificationHelper = NotificationHelper(context)
                            val reminderId = (response.data as? Map<*, *>)?.get("reminder_id")?.toString()?.toIntOrNull() ?: System.currentTimeMillis().toInt()
                            notificationHelper.scheduleReminder(
                                reminderId = reminderId, 
                                title = title, 
                                date = date, 
                                time = time,
                                isRecurring = isRecurring,
                                recurrencePattern = recurrencePattern
                            )
                            
                            // Reload reminders
                            cloudUserManager.getRemindersV2().onSuccess { list ->
                                reminders = list
                            }
                            showAddReminderDialog = false
                            
                            // Show success message
                            android.widget.Toast.makeText(
                                context,
                                "Reminder added successfully!",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }.onFailure { error ->
                            android.widget.Toast.makeText(
                                context,
                                "Failed to add reminder: ${error.message}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }
    }
}

data class ReminderItem(
    val icon: ImageVector,
    val bgColor: Color,
    val tint: Color,
    val title: String,
    val time: String
)

@Composable
fun ReminderCard(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    time: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    .clip(RoundedCornerShape(24.dp))
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Clock icon or just text? Text based on screenshot
                     Text(
                        text = "🕒", // Using emoji as placeholder for clock icon
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                    )
                     Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
            }
            RadioButton(
                selected = checked,
                onClick = { onCheckedChange(!checked) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF4A90E2),
                    unselectedColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun ReminderCardV2(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    reminder: Reminder,
    onStatusChange: (String) -> Unit
) {
    val status = reminder.status ?: "pending"
    val statusColor = when (status) {
        "completed" -> Color(0xFF4CAF50)
        "missed" -> Color(0xFFF44336)
        "cancelled" -> Color(0xFF9E9E9E)
        else -> Color(0xFFFF9800) // pending = orange
    }
    val statusText = when (status) {
        "completed" -> "Completed"
        "missed" -> "Missed"
        "cancelled" -> "Cancelled"
        else -> "Pending"
    }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
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
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
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
                        text = reminder.reminderTitle,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🕒",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${reminder.reminderTime} • ${reminder.reminderDate}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }
                // Status Badge
                Card(
                    colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            // Action Buttons (only show for pending reminders)
            if (status == "pending") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onStatusChange("completed") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("✓ Complete", fontSize = 14.sp)
                    }
                    OutlinedButton(
                        onClick = { onStatusChange("cancelled") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF9E9E9E)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("✕ Cancel", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RemindersScreenPreview() {
    DIABETICFootTheme {
        RemindersScreen(onBackClick = {})
    }
}
