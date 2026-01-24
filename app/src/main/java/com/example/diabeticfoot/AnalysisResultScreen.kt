package com.example.diabeticfoot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun AnalysisResultScreen(
    riskLevel: String, // "High", "Medium", "Low"
    onViewAlertClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    // Normalize riskLevel - handle both "Moderate" and "Medium"
    val normalizedRisk = when (riskLevel.trim()) {
        "Moderate" -> "Medium"
        else -> riskLevel.trim()
    }
    
    val (color, bgColor, icon, title, description) = when (normalizedRisk) {
        "High" -> ResultStyle(
            Color(0xFFE53935), // Red
            Color(0xFFFFEBEE),
            Icons.Outlined.PriorityHigh,
            "High Risk",
            "Immediate medical attention\nrequired"
        )
        "Medium" -> ResultStyle(
             Color(0xFFFF9800), // Orange
             Color(0xFFFFF3E0), // Light Orange
             Icons.Filled.Warning,
             "Medium Risk",
             "Monitor closely and consult\nyour doctor soon"
        ) 
        else -> ResultStyle(
            Color(0xFF4CAF50), // Green
            Color(0xFFE8F5E9),
            Icons.Filled.Check,
            "Low Risk",
            "No immediate concerns.\nContinue regular care."
        )
    }
    
    val actualBgColor = bgColor

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Icon and Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circle with Icon
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(actualBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                         modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                            .border(BorderStroke(5.dp, color), CircleShape),
                         contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = color,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Button
            if (normalizedRisk == "High") {
                Button(
                    onClick = onViewAlertClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Send Alert to Doctor",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            } else {
                // For Low and Medium risk, just show Done button
                Button(
                    onClick = onDoneClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class ResultStyle(
    val color: Color,
    val bgColor: Color,
    val icon: ImageVector,
    val title: String,
    val description: String
)

@Preview(showBackground = true)
@Composable
fun AnalysisResultScreenPreview() {
    DIABETICFootTheme {
        AnalysisResultScreen(
            riskLevel = "High",
            onViewAlertClick = {},
            onDoneClick = {}
        )
    }
}
