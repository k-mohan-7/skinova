package com.example.diabeticfoot

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailsScreen(
    name: String,
    info: String,
    sugarLevel: String,
    riskLevel: String,
    symptoms: List<String>,
    imageTime: String,
    imageUri: String? = null,
    onBackClick: () -> Unit,
    onSendAdviceClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Patient Details",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A90E2)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name.split(" ").filter { it.isNotBlank() }.map { it.first() }.joinToString("").uppercase(), // Initials
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A90E2)
                        )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                        Text(
                            text = info,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }
            }

            // Latest Sugar Level Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Latest Sugar Level",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sugarLevel,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "mg/dL",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val (bgColor, textColor) = when (riskLevel) {
                        "Low Risk" -> Color(0xFFE8F5E9) to Color(0xFF4CAF50)
                        "Medium Risk" -> Color(0xFFFFF8E1) to Color(0xFFFFC107)
                        "High Risk" -> Color(0xFFFFEBEE) to Color(0xFFF44336)
                        else -> Color.Gray to Color.White
                    }

                    Box(
                        modifier = Modifier
                            .background(bgColor, RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = riskLevel,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Latest Wound Image Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Latest Wound Image",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Image Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null && imageUri != "none") {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Wound Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = "No image available",
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = imageTime,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reported Symptoms Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Reported Symptoms",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        symptoms.forEach { symptom ->
                            SymptomChip(
                                text = symptom, 
                                color = if (symptom.contains("Pain", ignoreCase = true)) Color(0xFFFFEBEE) else Color(0xFFFFF3E0), 
                                textColor = if (symptom.contains("Pain", ignoreCase = true)) Color(0xFFF44336) else Color(0xFFFF9800)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Send Advice Button
            Button(
                onClick = onSendAdviceClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline, 
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send Advice",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SymptomChip(text: String, color: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PatientDetailsScreenPreview() {
    DIABETICFootTheme {
        PatientDetailsScreen(
            name = "John Doe",
            info = "Male, 65 Years",
            sugarLevel = "126",
            riskLevel = "Low Risk",
            symptoms = listOf("Mild Pain", "Swelling"),
            imageTime = "Uploaded Today, 9:00 AM",
            imageUri = null,
            onBackClick = {}
        )
    }
}
