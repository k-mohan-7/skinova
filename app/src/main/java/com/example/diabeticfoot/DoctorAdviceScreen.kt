package com.example.diabeticfoot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAdviceScreen(
    onBackClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userManager = androidx.compose.runtime.remember { UserManager(context) }
    val phone = userManager.getCurrentPatientPhone()
    val adviceList = phone?.let { userManager.getDoctorAdvice(it) }
    val advice = adviceList?.firstOrNull() // Get the first (most recent) advice

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Doctor's Advice",
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

            if (advice != null) {
                // Latest Note Section
                SectionHeader(
                    icon = Icons.Outlined.Description,
                    title = "Latest Note"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "\"${advice["notes"]}\"",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "- Dr. Smith, ${advice["time"] as? String ?: ""}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Current Medications Section
                if ((advice["medication"] as? String)?.isNotBlank() == true) {
                    SectionHeader(
                        icon = Icons.Outlined.Medication,
                        title = "Current Medications"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MedicationCard(
                        name = advice["medication"] as? String ?: "",
                        dosage = advice["dosage"] as? String ?: "",
                        status = "Active"
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                // No Advice Case
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No advice received from doctor yet.",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Next Visit Section
            SectionHeader(
                icon = Icons.Outlined.CalendarMonth,
                title = "Next Visit"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                val nextVisit = (advice?.get("nextVisit") as? String) ?: "Oct 24, 10:00 AM" // Fallback to placeholder or specified
                val parts = nextVisit.split(",")
                val datePart = parts.getOrNull(0)?.trim() ?: "Oct 24"
                val timePart = parts.getOrNull(1)?.trim() ?: "10:00 AM"
                
                val month = datePart.split(" ").getOrNull(0) ?: "Oct"
                val day = datePart.split(" ").getOrNull(1) ?: "24"

                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date Box
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE3F2FD), // Light Blue
                        modifier = Modifier.size(60.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = month,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF4A90E2),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = day,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color(0xFF4A90E2),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Scheduled Visit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                        Text(
                            text = "$timePart at City Clinic",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4A90E2),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )
    }
}

@Composable
fun MedicationCard(name: String, dosage: String, status: String) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Text(
                    text = dosage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFE8F5E9) // Light Green
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorAdviceScreenPreview() {
    DIABETICFootTheme {
        DoctorAdviceScreen(onBackClick = {})
    }
}
