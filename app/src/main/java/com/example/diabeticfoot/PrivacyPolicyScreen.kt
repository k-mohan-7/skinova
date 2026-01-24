package com.example.diabeticfoot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.VisibilityOff
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
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A90E2) // Matches other screens
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
                .background(Color.White) 
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your Privacy Matters",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "At DFU Care, we are committed to protecting your medical information. We believe transparency is key to building trust in your healthcare journey.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrivacyCard(
                icon = Icons.Outlined.Security,
                iconBgColor = Color(0xFFE3F2FD),
                iconTint = Color(0xFF4A90E2),
                title = "Data Security",
                description = "Your patient data is stored safely using encrypted medical-grade servers that comply with healthcare standards."
            )

            PrivacyCard(
                icon = Icons.Outlined.MedicalServices, // Fallback icon if MedicalBag not available easily or use Kit
                iconBgColor = Color(0xFFE3F2FD), // Keeping blue theme consistent or use light blue
                iconTint = Color(0xFF4A90E2),
                title = "Medical Usage",
                description = "Wound images and health details are collected strictly for the purpose of monitoring your condition and treatment progress."
            )

            PrivacyCard(
                icon = Icons.Outlined.Lock,
                iconBgColor = Color(0xFFE3F2FD),
                iconTint = Color(0xFF4A90E2),
                title = "Confidentiality",
                description = "Your data is shared exclusively with your assigned healthcare provider. No unauthorized personnel have access to your records."
            )

            PrivacyCard(
                icon = Icons.Outlined.VisibilityOff,
                iconBgColor = Color(0xFFE3F2FD),
                iconTint = Color(0xFF4A90E2),
                title = "Privacy Guarantee",
                description = "We do not sell, trade, or share your personal information with third-party advertisers or marketing agencies."
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PrivacyCard(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    description: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)), // Very light grey/white bg for cards
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat style as per image usually, or subtle
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF5F6368), // Dark Gray
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyScreenPreview() {
    DIABETICFootTheme {
        PrivacyPolicyScreen(onBackClick = {})
    }
}
