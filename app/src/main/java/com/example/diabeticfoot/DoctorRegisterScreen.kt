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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorRegisterScreen(
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userManager = remember { UserManager(context) }
    
    var doctorName by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var hospitalName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Doctor Registration",
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Doctor Name
            DoctorRegisterField(
                label = "Doctor Name",
                value = doctorName,
                onValueChange = { doctorName = it },
                placeholder = "Dr. Sarah Smith"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Specialization
            DoctorRegisterField(
                label = "Specialization",
                value = specialization,
                onValueChange = { specialization = it },
                placeholder = "Podiatrist / Endocrinologist"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hospital Name
            DoctorRegisterField(
                label = "Hospital Name",
                value = hospitalName,
                onValueChange = { hospitalName = it },
                placeholder = "City General Hospital"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            DoctorRegisterField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholder = "doctor@hospital.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone
            DoctorRegisterField(
                label = "Phone",
                value = phone,
                onValueChange = { phone = it },
                placeholder = "+1 234 567 8900"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            DoctorRegisterPasswordField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeholder = "Create a password"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            DoctorRegisterPasswordField(
                label = "Confirm Password",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirm password"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Register Button
            Button(
                onClick = {
                    if (doctorName.isBlank() || specialization.isBlank() || hospitalName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                        android.widget.Toast.makeText(context, "Please fill all fields", android.widget.Toast.LENGTH_SHORT).show()
                    } else if (password != confirmPassword) {
                        android.widget.Toast.makeText(context, "Passwords do not match", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        val success = userManager.registerDoctor(email, password, doctorName, specialization, hospitalName, phone)
                        if (success) {
                            android.widget.Toast.makeText(context, "Registration Successful", android.widget.Toast.LENGTH_SHORT).show()
                            onRegisterClick()
                        } else {
                            android.widget.Toast.makeText(context, "Email already registered", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                )
            ) {
                Text(
                    text = "Register as Doctor",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DoctorRegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF19213D),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.LightGray
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4A90E2),
                unfocusedBorderColor = Color(0xFFE0E0E0),
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

@Composable
fun DoctorRegisterPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF19213D),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.LightGray
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4A90E2),
                unfocusedBorderColor = Color(0xFFE0E0E0),
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
fun DoctorRegisterScreenPreview() {
    DIABETICFootTheme {
        DoctorRegisterScreen(onBackClick = {}, onRegisterClick = {})
    }
}
