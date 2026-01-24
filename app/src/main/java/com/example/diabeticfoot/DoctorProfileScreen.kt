package com.example.diabeticfoot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme

@Composable
fun DoctorProfileScreen(
    showBottomBar: Boolean = true,
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val name = remember { cloudUserManager.getUserFullName() ?: "Doctor" }
    val email = remember { cloudUserManager.getUserEmail() ?: "" }
    val spec = remember { cloudUserManager.getUserSpecialization() ?: "Specialist" }
    val hospital = remember { cloudUserManager.getUserHospital() ?: "Hospital" }
    val phone = remember { cloudUserManager.getUserPhone() ?: "" }


    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    color = Color.White,
                    shadowElevation = 16.dp
                ) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 12.sp) },
                            selected = false,
                            onClick = onHomeClick,
                            colors = NavigationBarItemDefaults.colors(
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.LightGray
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 12.sp) },
                            selected = true,
                            onClick = { },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF4A90E2),
                                selectedTextColor = Color(0xFF4A90E2),
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                            label = { Text("Settings", fontSize = 12.sp) },
                            selected = false,
                            onClick = onSettingsClick,
                            colors = NavigationBarItemDefaults.colors(
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.LightGray
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FBFE)) // Light bluish background
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // White Curved Top Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
                    )
                    .padding(top = 32.dp, bottom = 48.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Doctor Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF19213D),
                            fontSize = 24.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Avatar with Shadow
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 10.dp,
                        modifier = Modifier.size(120.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F1FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = Color(0xFF4A90E2)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF19213D)
                        )
                    )

                    Text(
                        text = spec,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF4A90E2),
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Business,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = hospital,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Reg Number Container
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF7F9FC), RoundedCornerShape(16.dp))
                            .padding(horizontal = 40.dp, vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Reg. Number",
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                            )
                            Text(
                                text = "MD-12345-X",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF19213D),
                                    fontSize = 18.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Options
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DoctorProfileMenuCard(
                    icon = Icons.Default.Edit,
                    text = "Edit Profile",
                    iconColor = Color(0xFF4A90E2),
                    iconContainerColor = Color(0xFFF0F7FF),
                    onClick = onEditProfileClick
                )

                DoctorProfileMenuCard(
                    icon = Icons.Default.Settings,
                    text = "Settings",
                    iconColor = Color.Gray,
                    iconContainerColor = Color(0xFFF8F9FB),
                    onClick = onSettingsClick
                )

            }
        }
    }
}

@Composable
fun DoctorProfileMenuCard(
    icon: ImageVector,
    text: String,
    iconColor: Color,
    iconContainerColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = iconColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF19213D)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinalDoctorProfilePreview() {
    DIABETICFootTheme {
        DoctorProfileScreen(
            onHomeClick = {},
            onSettingsClick = {},
            onLogoutClick = {},
            onEditProfileClick = {}
        )
    }
}
