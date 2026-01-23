package com.example.diabeticfoot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.diabeticfoot.CloudUserManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SugarLevelScreen(
    onBackClick: () -> Unit,
    onConfirmClick: (Float, String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    var sugarValue by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isEditMode by remember { mutableStateOf(false) }
    var todaysSugarValue by remember { mutableStateOf<String?>(null) }
    var isLoadingToday by remember { mutableStateOf(true) }
    
    // Check if today's sugar level exists
    LaunchedEffect(Unit) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        cloudUserManager.getSugarLevels().onSuccess { levels ->
            val todaysRecord = levels.find { 
                it.measurementDate == today
            }
            if (todaysRecord != null) {
                todaysSugarValue = todaysRecord.sugarLevel.toInt().toInt().toString()
                isEditMode = false // Start in view mode
            } else {
                todaysSugarValue = null
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
                        text = "Log Sugar Level",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF1976D2), // Medium Blue
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1976D2)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
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
        } else if (!isEditMode && todaysSugarValue != null) {
            // View Mode - Show current value with edit button
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE3F2FD))
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                
                // Display current value with color indicator
                val sugarInt = todaysSugarValue!!.toIntOrNull() ?: 0
                
                data class StatusColors(val bgColor: Color, val statusColor: Color, val statusText: String, val iconColor: Color)
                
                val colors = when {
                    sugarInt < 70 -> StatusColors(Color(0xFFFFF8E1), Color(0xFFFFC107), "Low", Color(0xFFFFC107))
                    sugarInt in 70..140 -> StatusColors(Color(0xFFE8F5E9), Color(0xFF4CAF50), "Normal", Color(0xFF4CAF50))
                    sugarInt in 141..200 -> StatusColors(Color(0xFFFFF3E0), Color(0xFFFF9800), "High", Color(0xFFFF9800))
                    else -> StatusColors(Color(0xFFFFEBEE), Color(0xFFF44336), "Critical", Color(0xFFF44336))
                }
                
                val bgColor = colors.bgColor
                val statusColor = colors.statusColor
                val statusText = colors.statusText
                val iconColor = colors.iconColor
                
                // Status Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            sugarInt < 70 -> Icons.Outlined.Warning
                            sugarInt in 70..140 -> Icons.Outlined.CheckCircle
                            sugarInt in 141..200 -> Icons.Outlined.Warning
                            else -> Icons.Outlined.Dangerous
                        },
                        contentDescription = statusText,
                        tint = iconColor,
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Today's Sugar Level",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = todaysSugarValue!!,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                        Text(
                            text = "mg/dL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        sugarValue = "" // Start with empty field for update
                        isEditMode = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Update Sugar Level",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        } else {
            // Edit Mode - Original input screen
            Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD)) // Light Blue Background
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Drop Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFE1F5FE).copy(alpha = 0.7f)), // Slightly different shade or opacity
                contentAlignment = Alignment.Center
            ) {
                 // Inner circle
                Box(
                  modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(0xFFE8EAF6)) // Light indigoish
                )
                Icon(
                    imageVector = Icons.Outlined.WaterDrop,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Enter Reading",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Please enter your blood sugar level from your glucometer. No glucometer? Check at a nearby clinic or medical shop.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Input Field
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A90E2))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                         horizontalArrangement = Arrangement.Center
                    ) {
                        
                        TextField(
                            value = sugarValue,
                            onValueChange = { newValue -> 
                                if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                                    sugarValue = newValue
                                }
                            },
                            textStyle = TextStyle(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF000000), // Black color for visible text
                                textAlign = TextAlign.End 
                            ),
                             placeholder = {
                                Text(
                                    "000",
                                    style = TextStyle(
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE0E0E0)
                                    )
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color(0xFF000000),
                                unfocusedTextColor = Color(0xFF000000),
                                cursorColor = Color(0xFF4A90E2)
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.width(140.dp)
                        )
                        Text(
                            text = "mg/dL",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Gray
                            ),
                            modifier = Modifier.padding(top = 16.dp) 
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Show success message
            if (showSuccessMessage) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sugar level saved successfully!",
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

            Button(
                onClick = {
                    if (sugarValue.isNotEmpty()) {
                        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        onConfirmClick(sugarValue.toFloat(), timeStamp)
                    }
                },
                enabled = !isSaving && sugarValue.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SugarLevelScreenPreview() {
    DIABETICFootTheme {
        SugarLevelScreen(onBackClick = {}, onConfirmClick = { _, _ -> })
    }
}