package com.example.diabeticfoot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onBackClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userManager = remember { UserManager(context) }
    val phone = userManager.getCurrentPatientPhone() ?: ""

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

            val reminderItems = listOf(
                ReminderItem(Icons.Rounded.Medication, Color(0xFFE3F2FD), Color(0xFF4A90E2), "Morning Medicine", "08:00 AM"),
                ReminderItem(Icons.Rounded.WaterDrop, Color(0xFFFCE4EC), Color(0xFFE91E63), "Check Sugar Level", "09:00 AM"),
                ReminderItem(Icons.Rounded.PhotoCamera, Color(0xFFF3E5F5), Color(0xFF9C27B0), "Wound Cleaning", "10:30 AM"),
                ReminderItem(Icons.Rounded.Medication, Color(0xFFE3F2FD), Color(0xFF4A90E2), "Evening Medicine", "08:00 PM")
            )

            reminderItems.forEach { item ->
                var isChecked by remember { 
                    mutableStateOf(userManager.getReminderStatus(phone, item.title))
                }

                ReminderCard(
                    icon = item.icon,
                    iconBgColor = item.bgColor,
                    iconTint = item.tint,
                    title = item.title,
                    time = item.time,
                    checked = isChecked,
                    onCheckedChange = { newValue ->
                        isChecked = newValue
                        userManager.saveReminderStatus(item.title, newValue, item.time)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
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

@Preview(showBackground = true)
@Composable
fun RemindersScreenPreview() {
    DIABETICFootTheme {
        RemindersScreen(onBackClick = {})
    }
}
