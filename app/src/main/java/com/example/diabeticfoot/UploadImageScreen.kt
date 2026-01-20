package com.example.diabeticfoot

import android.Manifest
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.diabeticfoot.ui.theme.DIABETICFootTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImageScreen(
    onBackClick: () -> Unit,
    onSubmitClick: (String) -> Unit  // Pass risk level to navigation
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val userManager = remember { UserManager(context) }
    
    // Validation States
    var isChecking by remember { mutableStateOf(false) }
    var isValid by remember { mutableStateOf(false) }
    val validator = remember { FootImageValidator(context) }

    // Run validation when image is selected
    LaunchedEffect(selectedImageUri) {
        if (selectedImageUri != null) {
            isChecking = true
            isValid = false // Reset
            validator.validate(selectedImageUri!!) { success ->
                isValid = success
                isChecking = false
                if (!success) {
                    Toast.makeText(context, "Please upload a clear foot or wound image.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Helper to create a temp file for camera
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", context.cacheDir)
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = tempImageUri
        }
    }

    // Permission Launcher for Camera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = createImageFile()
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            tempImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission needed", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Upload Image",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            // Submit Button
            Button(
                onClick = {
                    val phone = userManager.getCurrentPatientPhone()
                    if (phone != null && selectedImageUri != null) {
                        userManager.savePatientImageUri(phone, selectedImageUri.toString())
                        
                        // Classify the image - create classifier only when needed
                        var classifier: DFUSeverityClassifier? = null
                        try {
                            classifier = DFUSeverityClassifier(context)
                            
                            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                android.graphics.ImageDecoder.decodeBitmap(
                                    android.graphics.ImageDecoder.createSource(context.contentResolver, selectedImageUri!!)
                                )
                            } else {
                                @Suppress("DEPRECATION")
                                android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImageUri)
                            }
                            
                            val result = classifier.classifyImage(bitmap)
                            bitmap.recycle()
                            classifier.close()
                            
                            // Pass the risk level (Low, Moderate, High) to navigation
                            onSubmitClick(result.riskLevel)
                        } catch (e: Exception) {
                            Log.e("UploadImageScreen", "Error classifying image", e)
                            classifier?.close()
                            Toast.makeText(context, "Error analyzing image. Please try again.", Toast.LENGTH_SHORT).show()
                            // Fallback to Medium if error
                            onSubmitClick("Medium")
                        }
                    } else {
                        onSubmitClick("Medium")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2),
                    disabledContainerColor = Color(0xFFB0C4DE),
                    contentColor = Color.White
                ),
                enabled = selectedImageUri != null && !isChecking && isValid
            ) {
                 if (isChecking) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Validating...")
                } else {
                    Text(
                        text = "Submit for Analysis",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Info Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEAF5FD), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFD0E4F7), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Please upload one clear wound image\nper day for accurate analysis.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF4A90E2),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Upload Area
            if (selectedImageUri != null) {
                // Show Preview if Image Selected
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF9F9F9))
                        .border(
                            width = if (!isChecking && !isValid) 2.dp else 1.dp, 
                            color = if (!isChecking && !isValid) Color.Red else Color(0xFFE0E0E0), 
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .run {
                                if (isChecking) this.then(Modifier.background(Color.White.copy(alpha = 0.5f))) else this
                            },
                        contentScale = ContentScale.Crop
                    )
                    
                    if (isChecking) {
                        CircularProgressIndicator(color = Color(0xFF4A90E2))
                    }
                    
                    if (!isChecking && !isValid) {
                         Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Warning,

                                contentDescription = "Invalid",
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Not a foot image",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.background(Color.White.copy(alpha=0.7f), RoundedCornerShape(4.dp)).padding(4.dp)
                            )
                        }
                    }
                    
                    // Remove Button (X)
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.White.copy(alpha = 0.7f), CircleShape)
                    ) {
                         Text("X", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Show Take Photo Button
                DashedBorderCard(
                    onClick = {
                        val permission = Manifest.permission.CAMERA
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            val file = createImageFile()
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            tempImageUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(permission)
                        }
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFF0F0F0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = "Take Photo",
                                tint = Color(0xFF4A90E2),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Take Photo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Choose from Gallery Button
            if (selectedImageUri == null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { galleryLauncher.launch("image/*") }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 24.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Choose from Gallery",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                        )
                    }
                }
            } else {
                // Simple Replace Option
                 TextButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                 ) {
                     Text("Choose a different image", color = Color(0xFF4A90E2))
                 }
            }
        }
    }
}

@Composable
fun DashedBorderCard(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    val color = Color(0xFFCCCCCC)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF9F9F9))
            .clickable(onClick = onClick)
            .drawBehind {
                drawRoundRect(
                    color = color,
                    style = stroke,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun UploadImageScreenPreview() {
    DIABETICFootTheme {
        UploadImageScreen(
            onBackClick = {},
            onSubmitClick = {}
        )
    }
}
