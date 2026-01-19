package com.example.popcorntime.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModelProvider
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.popcorntime.R
import com.example.popcorntime.ui.theme.orbitronsFontFamily
import com.example.popcorntime.viewmodel.FirebaseAuthViewModel
import com.example.popcorntime.data.database.PlaylistManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class RegisterScreen : Screen {
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
                android.util.Log.e("RegisterScreen", "Error creating ViewModel: ${e.message}")
                FirebaseAuthViewModel() // Fallback
            }
        }

        val username = rememberSaveable { mutableStateOf("") }
        val email = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo
            Image(
                painter = painterResource(R.drawable.my_icon),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text("Create account", fontSize = 38.sp, color = Color.White,fontFamily = orbitronsFontFamily,
                fontWeight = FontWeight.Light)
            Spacer(modifier = Modifier.height(20.dp))

            // Username Field
            Text("Username", color = Color.White,fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light, modifier = Modifier.align(Alignment.Start,))
            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                placeholder = { Text("Enter your Name", color = Color.Gray,fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = { Icon(Icons.Filled.AccountBox, null, tint = Color.White) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = backgroundColor,
                    unfocusedContainerColor = backgroundColor,
                    focusedIndicatorColor = Color(0xFF6C0E9B),
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.White
                )
            )

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

            Spacer(modifier = Modifier.height(50.dp))

            // Sign Up Button (Gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF9B27B0), Color(0xFF3974D3)))
                    )
                    .clickable {
                        if (email.value.isBlank() || password.value.isBlank() || username.value.isBlank()) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        } else if (password.value.length < 6) {
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        } else {
                            firebaseViewModel.register(
                                email.value,
                                password.value,
                                onRegister = { user ->
                                    // تحديث PlaylistManager مع userId الجديد
                                    if (user != null) {
                                        PlaylistManager.setUserId(user.uid)
                                    }
                                    
                                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                    // Use username from the form, or email if username is empty
                                    val userName = if (username.value.isNotBlank()) username.value 
                                                  else user?.email?.substringBefore("@") ?: "User"
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
                    Text("Sign Up", color = Color.White, fontSize = 18.sp,fontFamily = orbitronsFontFamily,
                        fontWeight = FontWeight.Light)
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Login Prompt
            Row {
                Text("Already have an account?", fontSize = 18.sp, color = Color.White,fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    "sign in",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp,
                    color = Color(0xFF9B27B0),
                    modifier = Modifier.clickable { navigator.push(LoginScreen()) }
                )
            }
        }
    }
}
