package com.example.popcorntime.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelProvider
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.popcorntime.R
import com.example.popcorntime.theme.LocalThemeManager
import com.example.popcorntime.theme.ThemeManager
import com.example.popcorntime.ui.theme.orbitronsFontFamily
import com.example.popcorntime.viewmodel.FirebaseAuthViewModel
import com.example.popcorntime.data.database.PlaylistManager
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        ProfileContent("User Name")
    }
}

@Composable
fun ProfileContent(userName: String) {
    val context = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow
    val themeManager = LocalThemeManager.current
    val colors = themeManager.currentColors
    
    // Initialize ViewModel
    val firebaseViewModel = remember {
        try {
            val activity = context as? androidx.activity.ComponentActivity
            if (activity != null) {
                ViewModelProvider(activity)[FirebaseAuthViewModel::class.java]
            } else {
                FirebaseAuthViewModel()
            }
        } catch (e: Exception) {
            android.util.Log.e("ProfileContent", "Error creating ViewModel: ${e.message}")
            FirebaseAuthViewModel()
        }
    }
    
    // Get current user info
    val currentUser = remember { firebaseViewModel.getCurrentUser() }
    val userEmail = currentUser?.email ?: ""
    val displayName = currentUser?.displayName ?: userName
    val actualUserName = if (displayName.isNotBlank()) displayName else userEmail.substringBefore("@")
    
    // State variables
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showPrivacySettings by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var privateProfile by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Profile Image
            Image(
                painter = painterResource(R.drawable.my_icon),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // User Name
            Text(
                actualUserName,
                color = colors.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )

            // User Email
            Text(
                userEmail,
                color = colors.secondary,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(top = 4.dp)
            )

            // User Title
            Text(
                "Movie Enthusiast",
                color = colors.secondary,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Profile Options
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface
                )
            ) {
                Column {
                    // Edit Profile Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showEditProfileDialog = true
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Profile",
                            tint = colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Edit Profile",
                            color = colors.onSurface,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = colors.secondary
                        )
                    }

                    Divider(
                        color = colors.secondary.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Change Password Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showChangePasswordDialog = true
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Change Password",
                            tint = colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Change Password",
                            color = colors.onSurface,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = colors.secondary
                        )
                    }

                    Divider(
                        color = colors.secondary.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Privacy Settings Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showPrivacySettings = true
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PrivacyTip,
                            contentDescription = "Privacy Settings",
                            tint = colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Privacy Settings",
                            color = colors.onSurface,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = colors.secondary
                        )
                    }

                    Divider(
                        color = colors.secondary.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Notifications Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                notificationsEnabled = !notificationsEnabled
                                Toast.makeText(
                                    context,
                                    if (notificationsEnabled) "Notifications enabled" else "Notifications disabled",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Notifications",
                            color = colors.onSurface,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                                Toast.makeText(
                                    context,
                                    if (it) "Notifications enabled" else "Notifications disabled",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.primary,
                                checkedTrackColor = colors.primary.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            OutlinedButton(
                onClick = {
                    // مسح PlaylistManager من الذاكرة فقط (ليس من قاعدة البيانات)
                    PlaylistManager.setUserId(null)
                    
                    firebaseViewModel.logout()
                    FirebaseAuthViewModel.clearLastUserId(context)
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    // Navigate to LoginScreen
                    navigator.push(LoginScreen())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.primary
                ),
                border = BorderStroke(1.dp, colors.primary)
            ) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = "Logout",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Logout",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
    
    // Change Password Dialog
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            colors = colors,
            firebaseViewModel = firebaseViewModel
        )
    }
    
    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            onDismiss = { showEditProfileDialog = false },
            colors = colors,
            currentUser = currentUser,
            firebaseViewModel = firebaseViewModel
        )
    }
    
    // Privacy Settings Dialog
    if (showPrivacySettings) {
        PrivacySettingsDialog(
            onDismiss = { showPrivacySettings = false },
            colors = colors,
            privateProfile = privateProfile,
            onPrivateProfileChange = { privateProfile = it }
        )
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    colors: com.example.popcorntime.theme.AppColors,
    firebaseViewModel: FirebaseAuthViewModel
) {
    val context = LocalContext.current
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Change Password",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface,
                    fontFamily = FontFamily.Serif
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it; errorMessage = "" },
                    label = { Text("Current Password", color = colors.onSurface) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface,
                        focusedContainerColor = colors.background,
                        unfocusedContainerColor = colors.background,
                        focusedIndicatorColor = colors.primary,
                        unfocusedIndicatorColor = colors.secondary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it; errorMessage = "" },
                    label = { Text("New Password", color = colors.onSurface) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface,
                        focusedContainerColor = colors.background,
                        unfocusedContainerColor = colors.background,
                        focusedIndicatorColor = colors.primary,
                        unfocusedIndicatorColor = colors.secondary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMessage = "" },
                    label = { Text("Confirm New Password", color = colors.onSurface) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface,
                        focusedContainerColor = colors.background,
                        unfocusedContainerColor = colors.background,
                        focusedIndicatorColor = colors.primary,
                        unfocusedIndicatorColor = colors.secondary
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.onSurface
                        )
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            when {
                                currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                                    errorMessage = "Please fill all fields"
                                }
                                newPassword.length < 6 -> {
                                    errorMessage = "Password must be at least 6 characters"
                                }
                                newPassword != confirmPassword -> {
                                    errorMessage = "Passwords do not match"
                                }
                                else -> {
                                    isLoading = true
                                    errorMessage = ""
                            
                                    // Change password using Firebase
                                    val auth = FirebaseAuth.getInstance()
                                    val user = auth.currentUser
                                    
                                    if (user != null && user.email != null) {
                                        // Re-authenticate first
                                        val credential = EmailAuthProvider.getCredential(
                                            user.email!!,
                                            currentPassword
                                        )
                                        
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                user.reauthenticate(credential).await()
                                                user.updatePassword(newPassword).await()
                                                
                                                withContext(Dispatchers.Main) {
                                                    isLoading = false
                                                    Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                                    onDismiss()
                                                }
                                            } catch (e: FirebaseAuthException) {
                                                withContext(Dispatchers.Main) {
                                                    isLoading = false
                                                    errorMessage = when (e.errorCode) {
                                                        "ERROR_WRONG_PASSWORD" -> "Current password is incorrect"
                                                        "ERROR_WEAK_PASSWORD" -> "New password is too weak"
                                                        else -> e.localizedMessage ?: "Failed to change password"
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    isLoading = false
                                                    errorMessage = e.localizedMessage ?: "An error occurred"
                                                }
                                            }
                                        }
                                    } else {
                                        isLoading = false
                                        errorMessage = "User not found"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Change")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    onDismiss: () -> Unit,
    colors: com.example.popcorntime.theme.AppColors,
    currentUser: com.google.firebase.auth.FirebaseUser?,
    firebaseViewModel: FirebaseAuthViewModel
) {
    val context = LocalContext.current
    var displayName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Edit Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface,
                    fontFamily = FontFamily.Serif
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it; errorMessage = "" },
                    label = { Text("Display Name", color = colors.onSurface) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface,
                        focusedContainerColor = colors.background,
                        unfocusedContainerColor = colors.background,
                        focusedIndicatorColor = colors.primary,
                        unfocusedIndicatorColor = colors.secondary
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Email: ${currentUser?.email ?: "N/A"}",
                    color = colors.secondary,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.onSurface
                        )
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            if (displayName.isBlank()) {
                                errorMessage = "Display name cannot be empty"
                            } else {
                                isLoading = true
                                errorMessage = ""
                                
                                val user = FirebaseAuth.getInstance().currentUser
                                if (user != null) {
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName)
                                        .build()
                                    
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            user.updateProfile(profileUpdates).await()
                                            
                                            withContext(Dispatchers.Main) {
                                                isLoading = false
                                                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                                onDismiss()
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                isLoading = false
                                                errorMessage = e.localizedMessage ?: "Failed to update profile"
                                            }
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    errorMessage = "User not found"
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacySettingsDialog(
    onDismiss: () -> Unit,
    colors: com.example.popcorntime.theme.AppColors,
    privateProfile: Boolean,
    onPrivateProfileChange: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    "Privacy Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface,
                    fontFamily = FontFamily.Serif
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Private Profile",
                            color = colors.onSurface,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            "Hide your profile from other users",
                            color = colors.secondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Switch(
                        checked = privateProfile,
                        onCheckedChange = onPrivateProfileChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.primary,
                            checkedTrackColor = colors.primary.copy(alpha = 0.5f)
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    )
                ) {
                    Text("Done")
                }
            }
        }
    }
}

