package com.example.diabeticfoot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun WelcomeScreen(onGetStartedClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF5FD)), // Light blue background
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_new),
            contentDescription = "DFU Care Logo",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        // Title
        Text(
            text = "DFU Follow-Up",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800), // Orange color
                fontSize = 28.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Description
        Text(
            text = "Post-Discharge Diabetic Foot\nCare",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Get Started Button
        Button(
            onClick = onGetStartedClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 48.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A90E2) // Blue button color
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Get Started",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    DIABETICFootTheme {
        WelcomeScreen()
    }
}
