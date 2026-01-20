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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsScreen(
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userManager = remember { UserManager(context) }
    
    val symptoms = listOf("Severe Pain", "Swelling", "Redness/Color Change", "Moderate Pain", "Mild Pain")
    val selectedSymptoms = remember { mutableStateListOf<String>() }

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


            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = {
                    val phone = userManager.getCurrentPatientPhone()
                    if (phone != null) {
                        userManager.saveSymptoms(phone, selectedSymptoms.joinToString(", "))
                    }
                    onSubmitClick()
                },
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
                    text = "Submit Symptoms",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
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
            onSubmitClick = {}
        )
    }
}
