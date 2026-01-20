package com.example.diabeticfoot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDoctorProfileScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userManager = remember { UserManager(context) }
    val currentEmail = remember { userManager.getCurrentDoctorEmail() ?: "" }
    
    var name by remember { mutableStateOf(userManager.getDoctorName(currentEmail)) }
    var specialization by remember { mutableStateOf(userManager.getDoctorSpec(currentEmail)) }
    var hospital by remember { mutableStateOf(userManager.getDoctorHospital(currentEmail)) }
    var phone by remember { mutableStateOf(userManager.getDoctorPhone(currentEmail)) }
    var email by remember { mutableStateOf(currentEmail) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF19213D)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF19213D)
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
                .background(Color(0xFFF8FBFE))
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            EditProfileField(label = "Name", value = name, onValueChange = { name = it })
            Spacer(modifier = Modifier.height(24.dp))
            
            EditProfileField(label = "Specialization", value = specialization, onValueChange = { specialization = it })
            Spacer(modifier = Modifier.height(24.dp))
            
            EditProfileField(label = "Hospital", value = hospital, onValueChange = { hospital = it })
            Spacer(modifier = Modifier.height(24.dp))
            
            EditProfileField(label = "Phone", value = phone, onValueChange = { phone = it })
            Spacer(modifier = Modifier.height(24.dp))
            
            EditProfileField(label = "Email", value = email, onValueChange = { email = it })
            
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            val context = androidx.compose.ui.platform.LocalContext.current
            Button(
                onClick = {
                    if (name.isBlank() || specialization.isBlank() || hospital.isBlank() || phone.isBlank() || email.isBlank()) {
                        android.widget.Toast.makeText(context, "Please fill all fields", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        userManager.updateDoctorProfile(currentEmail, name, specialization, hospital, phone)
                        onSaveClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2962FF) // Vibrant blue for Save button
                )
            ) {
                Text(
                    text = "Save Changes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun EditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color(0xFF4A90E2),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditDoctorProfileScreenPreview() {
    DIABETICFootTheme {
        EditDoctorProfileScreen(onBackClick = {}, onSaveClick = {})
    }
}
