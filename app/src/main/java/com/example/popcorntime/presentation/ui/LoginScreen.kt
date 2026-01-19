package com.example.popcorntime.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.popcorntime.R
import com.example.popcorntime.ui.theme.orbitronsFontFamily
import com.example.popcorntime.viewmodel.FirebaseAuthViewModel
import com.example.popcorntime.data.database.PlaylistManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class LoginScreen : Screen {
    @Composable
    override fun Content() {

        val backgroundColor = Color(0xFF100A10)
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(backgroundColor, darkIcons = false)
            systemUiController.setNavigationBarColor(backgroundColor, darkIcons = false)
        }

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        
        // Initialize ViewModel safely - using remember to keep instance across recompositions
        val firebaseViewModel = remember {
            try {
                val activity = context as? androidx.activity.ComponentActivity
                if (activity != null) {
                    ViewModelProvider(activity)[FirebaseAuthViewModel::class.java]
                } else {
                    FirebaseAuthViewModel() // Fallback if not in Activity context
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginScreen", "Error creating ViewModel: ${e.message}")
                FirebaseAuthViewModel() // Fallback
            }
        }

        val email = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }
        var rememberMe by remember { mutableStateOf(false) }
        
        // تحميل Remember Me state من SharedPreferences
        LaunchedEffect(Unit) {
            rememberMe = FirebaseAuthViewModel.getRememberMe(context)
            val lastUserId = FirebaseAuthViewModel.getLastUserId(context)
            if (lastUserId != null && rememberMe) {
                // يمكن إضافة منطق لتعبئة البريد الإلكتروني من آخر مستخدم
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Logo
                Image(
                    painter = painterResource(R.drawable.my_icon),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .shadow(12.dp, RoundedCornerShape(20.dp), clip = false)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Login Title
                Text("Login", fontSize = 40.sp, color = Color.White, fontFamily = orbitronsFontFamily,
                    fontWeight = FontWeight.Light)
                Spacer(modifier = Modifier.height(20.dp))

                // Email Field
                Text("Email", color = Color.White,fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light, modifier = Modifier.align(Alignment.Start))
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    placeholder = { Text("Enter your Email", color = Color.Gray,fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = { Icon(Icons.Filled.Email, null, tint = Color.White) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = backgroundColor,
                        unfocusedContainerColor = backgroundColor,
                        focusedIndicatorColor = Color(0xFF9B27B0),
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Password Field
                Text("Password", color = Color.White,fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light, modifier = Modifier.align(Alignment.Start))
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    placeholder = { Text("Enter your Password", color = Color.Gray,fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    trailingIcon = { Icon(Icons.Filled.Lock, null, tint = Color.White) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = backgroundColor,
                        unfocusedContainerColor = backgroundColor,
                        focusedIndicatorColor = Color(0xFF9B27B0),
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { rememberMe = !rememberMe }
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF9B27B0),
                                uncheckedColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(0.dp))
                        Text(
                            "Remember me",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Light,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    
                    Text(
                        "Forgot password?",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Login Button (Gradient)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF9B27B0),
                                    Color(0xFF3974D3)
                                )
                            )
                        )
                        .clickable {
                            if (email.value.isBlank() || password.value.isBlank()) {
                                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            } else {
                                firebaseViewModel.login(
                                    email.value,
                                    password.value,
                                    onLogin = { user ->
                                        // حفظ Remember Me state
                                        FirebaseAuthViewModel.setRememberMe(context, rememberMe)
                                        
                                        // حفظ user ID إذا كان Remember Me مفعّل
                                        if (rememberMe && user != null) {
                                            FirebaseAuthViewModel.saveLastUserId(context, user.uid)
                                        } else if (!rememberMe) {
                                            FirebaseAuthViewModel.clearLastUserId(context)
                                        }
                                        
                                        // تحديث PlaylistManager مع userId الجديد
                                        if (user != null) {
                                            PlaylistManager.setUserId(user.uid)
                                        }
                                        
                                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                        // Get user name from Firebase user (display name or email)
                                        val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "User"
                                        navigator.push(MainScreen(userName = userName)) 
                                    },
                                    onError = {
                                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ExitToApp, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Login", color = Color.White, fontSize = 18.sp,fontFamily = orbitronsFontFamily,
                            fontWeight = FontWeight.Light)
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Signup Prompt
                Row {
                    Text("Don't have an account?", fontSize = 19.sp, color = Color.White,fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        "sign up",
                        fontSize = 19.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFF9B27B0),
                        modifier = Modifier.clickable { navigator.push(RegisterScreen()) }
                    )
                }
            }
        }
    }
}
