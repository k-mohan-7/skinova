package com.example.diabeticfoot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme
import com.example.diabeticfoot.api.models.SymptomDetail
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.diabeticfoot.CloudUserManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsScreen(
    onBackClick: () -> Unit,
    onConfirmClick: (List<String>, String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    val symptoms = listOf("Severe Pain", "Swelling", "Redness/Color Change", "Moderate Pain", "Mild Pain")
    val selectedSymptoms = remember { mutableStateListOf<String>() }
    var customSymptom by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isEditMode by remember { mutableStateOf(false) }
    var todaysSymptoms by remember { mutableStateOf<SymptomDetail?>(null) }
    var isLoadingToday by remember { mutableStateOf(true) }
    
    // Check if today's symptoms exist
    LaunchedEffect(Unit) {
        cloudUserManager.getSymptomsHistory().onSuccess { symptomsList ->
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val todaysRecord = symptomsList.find { it.symptomDate == today }
            if (todaysRecord != null) {
                todaysSymptoms = todaysRecord
                // Populate selected symptoms from today's data
                if (todaysRecord.severePain == 1) selectedSymptoms.add("Severe Pain")
                if (todaysRecord.swelling == 1) selectedSymptoms.add("Swelling")
                if (todaysRecord.rednessColorChange == 1) selectedSymptoms.add("Redness/Color Change")
                if (todaysRecord.moderatePain == 1) selectedSymptoms.add("Moderate Pain")
                if (todaysRecord.mildPain == 1) selectedSymptoms.add("Mild Pain")
                customSymptom = todaysRecord.additionalNotes ?: ""
                isEditMode = false // Start in view mode
            } else {
                isEditMode = true // No record, start in edit mode
            }
            isLoadingToday = false
        }.onFailure {
            isEditMode = true // Error, allow input
            isLoadingToday = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Symptoms",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A90E2) // Blue title per design
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF4A90E2) // Blue back arrow matching title
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        if (isLoadingToday) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF4A90E2))
            }
        } else if (!isEditMode && todaysSymptoms != null) {
            // View Mode - Show today's symptoms with edit button
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Today's Symptoms",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Recorded Symptoms:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (selectedSymptoms.isEmpty()) {
                            Text(
                                text = "• No symptoms selected",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray
                                )
                            )
                        } else {
                            selectedSymptoms.forEach { symptom ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "✓",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = Color(0xFF4CAF50),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = symptom,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.Black
                                        )
                                    )
                                }
                            }
                        }
                        
                        if (customSymptom.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color.Gray.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Additional Notes:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = customSymptom,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Black
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { isEditMode = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Update Symptoms",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            // Edit Mode - Original symptoms input screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Instructions Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF8E1), RoundedCornerShape(12.dp)) // Light Orange/Cream background
                    .border(1.dp, Color(0xFFFFE0B2), RoundedCornerShape(12.dp))
                    .padding(24.dp)
            ) {
                Text(
                    text = "Select any symptoms you are experiencing today.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF333333)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Symptom Items
            symptoms.forEach { symptom ->
                SymptomItem(
                    icon = when(symptom) {
                        "Severe Pain" -> Icons.Outlined.MonitorHeart
                        "Swelling" -> Icons.Outlined.Air
                        "Redness/Color Change" -> Icons.Outlined.DeviceThermostat
                        "Moderate Pain" -> Icons.Outlined.PriorityHigh
                        else -> Icons.Outlined.Info
                    },
                    label = symptom,
                    isChecked = selectedSymptoms.contains(symptom),
                    onCheckedChange = { checked ->
                        if (checked) selectedSymptoms.add(symptom)
                        else selectedSymptoms.remove(symptom)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Custom Symptom Input
            Text(
                text = "Additional Notes (Optional)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = customSymptom,
                onValueChange = { customSymptom = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Describe any other symptoms you're experiencing...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A90E2),
                    unfocusedBorderColor = Color(0xFFBDBDBD),
                    focusedTextColor = Color(0xFF333333),
                    unfocusedTextColor = Color(0xFF333333)
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )


            Spacer(modifier = Modifier.height(32.dp))

            // Show success message
            if (showSuccessMessage) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Symptoms saved successfully!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Show error message
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Submit Button
            Button(
                onClick = {
                    if (selectedSymptoms.isNotEmpty() || customSymptom.isNotBlank()) {
                        // Navigate to confirmation screen
                        onConfirmClick(selectedSymptoms.toList(), customSymptom)
                    }
                },
                enabled = !isSubmitting && (selectedSymptoms.isNotEmpty() || customSymptom.isNotBlank()),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        }
    }
}

@Composable
fun SymptomItem(
    icon: ImageVector,
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Checkbox items usually flat but design looks like cards
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF757575),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                ),
                modifier = Modifier.weight(1f)
            )
            
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4A90E2),
                    uncheckedColor = Color(0xFFBDBDBD)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SymptomsScreenPreview() {
    DIABETICFootTheme {
        SymptomsScreen(
            onBackClick = {},
            onConfirmClick = { _, _ -> }
        )
    }
}
