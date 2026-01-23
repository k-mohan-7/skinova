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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.diabeticfoot.CloudUserManager
import com.example.diabeticfoot.api.models.DoctorAdvice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAdviceScreen(
    onBackClick: () -> Unit,
    onViewFullHistory: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    var adviceList by remember { mutableStateOf<List<DoctorAdvice>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Load advice from backend
    LaunchedEffect(Unit) {
        cloudUserManager.getDoctorAdvice().onSuccess { list ->
            adviceList = list
            isLoading = false
        }.onFailure {
            isLoading = false
        }
    }
    
    val advice = adviceList.firstOrNull() // Get the most recent advice
    
    // Helper function to extract sections from advice text
    fun extractSection(text: String, startMarker: String, endMarker: String?): String {
        val startIndex = text.indexOf(startMarker)
        if (startIndex == -1) return ""
        
        val contentStart = startIndex + startMarker.length
        val endIndex = if (endMarker != null) {
            val idx = text.indexOf(endMarker, contentStart)
            if (idx == -1) text.length else idx
        } else {
            text.length
        }
        
        return text.substring(contentStart, endIndex).trim()
    }

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

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4A90E2))
                }
            } else if (advice != null) {
                // Parse advice text to extract components
                val adviceText = advice.adviceText
                val clinicalNotes = extractSection(adviceText, "Clinical Notes:", "Medication:")
                val medications = extractSection(adviceText, "Medication:", "Dosage:")
                val dosage = extractSection(adviceText, "Dosage:", "Next Visit:")
                val nextVisitFromText = extractSection(adviceText, "Next Visit:", null)
                
                // Status Badge
                val (statusColor, statusBg) = when (advice.status) {
                    "Completed" -> Color(0xFF4CAF50) to Color(0xFFE8F5E9)
                    "Ongoing" -> Color(0xFFFF9800) to Color(0xFFFFF3E0)
                    else -> Color(0xFF2196F3) to Color(0xFFE3F2FD) // Pending
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(statusBg, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status: ${advice.status}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Dr. ${advice.doctorName}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Latest Clinical Note Section
                SectionHeader(
                    icon = Icons.Outlined.Description,
                    title = "Clinical Notes"
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
                            text = if (clinicalNotes.isNotBlank()) clinicalNotes else advice.adviceText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Date: ${advice.adviceDate}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Current Medications Section
                SectionHeader(
                    icon = Icons.Outlined.Medication,
                    title = "Current Medications"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        if (medications.isNotBlank() || dosage.isNotBlank() || !advice.prescription.isNullOrBlank()) {
                            if (medications.isNotBlank()) {
                                Text(
                                    text = "Medication: $medications",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                )
                            }
                            if (dosage.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Dosage: $dosage",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Black
                                    )
                                )
                            }
                            if (!advice.prescription.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = advice.prescription,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Black
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = "No medications prescribed yet",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                
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
                    Box(modifier = Modifier.padding(24.dp)) {
                        if (!advice.nextVisitDate.isNullOrBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Scheduled for",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.Gray
                                        )
                                    )
                                    Text(
                                        text = advice.nextVisitDate,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4CAF50)
                                        )
                                    )
                                }
                            }
                        } else if (nextVisitFromText.isNotBlank()) {
                            Text(
                                text = nextVisitFromText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.Black
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = "No upcoming visit scheduled",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                
                // View Full Details Button
                Button(
                    onClick = onViewFullHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "View Full Advice History",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                // No Advice Case
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
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Advice Yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your doctor will provide guidance here once they review your health data",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
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
