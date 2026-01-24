package com.example.diabeticfoot

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginClick: (String) -> Unit,
    onPatientRegisterClick: () -> Unit = {},
    onDoctorRegisterClick: () -> Unit = {}
) {
    var isPatient by remember { mutableStateOf(true) }
    
    LoginScreenContent(
        isPatient = isPatient,
        onPatientClick = { isPatient = true },
        onDoctorClick = { isPatient = false },
        onLoginClick = onLoginClick,
        onPatientRegisterClick = onPatientRegisterClick,
        onDoctorRegisterClick = onDoctorRegisterClick
    )
}

@Composable
fun LoginScreenContent(
    isPatient: Boolean,
    onPatientClick: () -> Unit,
    onDoctorClick: () -> Unit,
    onLoginClick: (String) -> Unit,
    onPatientRegisterClick: () -> Unit,
    onDoctorRegisterClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cloudUserManager = remember { CloudUserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_new),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Welcome Back
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )
        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Gray
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Patient / Doctor Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = onPatientClick,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPatient) Color.White else Color.Transparent,
                    contentColor = if (isPatient) Color(0xFF4A90E2) else Color.Gray
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = if (isPatient) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else ButtonDefaults.buttonElevation(0.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp).size(18.dp)
                )
                Text("Patient")
            }
            Button(
                onClick = onDoctorClick,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isPatient) Color.White else Color.Transparent,
                    contentColor = if (!isPatient) Color(0xFF4A90E2) else Color.Gray
                ),
                shape = RoundedCornerShape(8.dp),
                 elevation = if (!isPatient) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else ButtonDefaults.buttonElevation(0.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.MedicalServices,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp).size(18.dp)
                )
                Text("Doctor")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Email or Phone (Username for Doctor)
        Text(
            text = if (isPatient) "Phone Number" else "Doctor Username",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = emailOrPhone,
            onValueChange = { emailOrPhone = it },
            placeholder = { Text(if (isPatient) "Enter your phone number" else "Enter doctor username", color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp),
             colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF4A90E2),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        Text(
            text = "Password",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Enter your password", color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp),
             colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF4A90E2),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = { 
                Log.d("LoginScreen", "Login button clicked. isPatient: $isPatient")
                if (emailOrPhone.isBlank() || password.isBlank()) {
                    android.widget.Toast.makeText(context, "Please fill all fields", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = true
                    coroutineScope.launch {
                        cloudUserManager.login(emailOrPhone, password, isPatient)
                            .onSuccess { response ->
                                isLoading = false
                                if (response.success) {
                                    val role = if (isPatient) "Patient" else "Doctor"
                                    Log.d("LoginScreen", "Cloud login successful, navigating to $role dashboard")
                                    onLoginClick(role)
                                } else {
                                    android.widget.Toast.makeText(context, response.message, android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                            .onFailure { e ->
                                isLoading = false
                                Log.e("LoginScreen", "Cloud login failed", e)
                                android.widget.Toast.makeText(context, e.message ?: "Login failed", android.widget.Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A90E2)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = if (isPatient) "Login as Patient" else "Login as Doctor",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Register - only shown for Patient
        if (isPatient) {
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", color = Color.Gray)
                TextButton(onClick = onPatientRegisterClick) {
                    Text("Register", color = Color(0xFF4A90E2), fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // For doctors, show a message
            Text(
                text = "Doctor accounts are provided by administration",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Patient Login")
@Composable
fun LoginScreenPatientPreview() {
    DIABETICFootTheme {
        LoginScreenContent(
            isPatient = true,
            onPatientClick = {},
            onDoctorClick = {},
            onLoginClick = {},
            onPatientRegisterClick = {},
            onDoctorRegisterClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Doctor Login")
@Composable
fun LoginScreenDoctorPreview() {
    DIABETICFootTheme {
        LoginScreenContent(
            isPatient = false,
            onPatientClick = {},
            onDoctorClick = {},
            onLoginClick = {},
            onPatientRegisterClick = {},
            onDoctorRegisterClick = {}
        )
    }
}
