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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDoctorProfileScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    // Load current user data
    var name by remember { mutableStateOf(cloudUserManager.getUserFullName() ?: "") }
    var specialization by remember { mutableStateOf(cloudUserManager.getUserSpecialization() ?: "") }
    var hospital by remember { mutableStateOf(cloudUserManager.getUserHospital() ?: "") }
    var phone by remember { mutableStateOf(cloudUserManager.getUserPhone() ?: "") }
    var email by remember { mutableStateOf(cloudUserManager.getUserEmail() ?: "") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

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
            Spacer(modifier = Modifier.height(24.dp))
            
            EditProfileField(label = "Change Password (optional)", value = password, onValueChange = { password = it }, isPassword = true)
            Spacer(modifier = Modifier.height(24.dp))
            
            EditProfileField(label = "Confirm Password", value = confirmPassword, onValueChange = { confirmPassword = it }, isPassword = true)
            
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

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
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Show success message
            if (successMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = successMessage!!,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (name.isBlank() || specialization.isBlank() || hospital.isBlank() || phone.isBlank() || email.isBlank()) {
                        errorMessage = "Please fill all required fields"
                    } else if (password.isNotEmpty() && password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                    } else {
                        isLoading = true
                        errorMessage = null
                        successMessage = null
                        
                        coroutineScope.launch {
                            cloudUserManager.updateDoctorProfile(
                                fullName = name,
                                email = email,
                                phone = phone,
                                specialization = specialization,
                                hospitalName = hospital,
                                password = if (password.isEmpty()) null else password
                            ).onSuccess { response ->
                                isLoading = false
                                if (response.success) {
                                    successMessage = "Profile updated successfully!"
                                    kotlinx.coroutines.delay(1500)
                                    onSaveClick()
                                } else {
                                    errorMessage = response.message
                                }
                            }.onFailure { e ->
                                isLoading = false
                                errorMessage = "Error: ${e.message}"
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2962FF) // Vibrant blue for Save button
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
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
}

@Composable
fun EditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
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
            singleLine = true,
            visualTransformation = if (isPassword) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None
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
