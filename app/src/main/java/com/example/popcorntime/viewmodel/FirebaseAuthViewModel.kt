package com.example.popcorntime.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthViewModel : ViewModel() {
    private val auth: FirebaseAuth by lazy {
        try {
            // محاولة الحصول على FirebaseAuth instance
            // Firebase يجب أن يكون مهيأ في Application class
            FirebaseAuth.getInstance()
        } catch (e: IllegalStateException) {
            // Firebase not initialized
            Log.e("FirebaseAuth", "Firebase not initialized. Make sure FirebaseApp.initializeApp() is called in Application.onCreate()")
            // إعادة رمي الخطأ مع رسالة واضحة
            throw IllegalStateException("Firebase not initialized. Please ensure Firebase is initialized in your Application class.", e)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error initializing Firebase Auth: ${e.message}", e)
            throw e
        }
    }

    fun login(email: String, password: String, onLogin: (FirebaseUser?) -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onError("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            try {
                val response = auth.signInWithEmailAndPassword(email.trim(), password).await()
                onLogin(response.user)
            } catch (e: FirebaseAuthException) {
                val errorMessage = when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Invalid email address"
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email"
                    "ERROR_WRONG_PASSWORD" -> "Incorrect password"
                    "ERROR_USER_DISABLED" -> "This account has been disabled"
                    "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please try again later"
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your connection"
                    else -> {
                        // التحقق من رسالة الخطأ لمعرفة إذا كان reCAPTCHA
                        val message = e.localizedMessage ?: e.message ?: ""
                        if (message.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) || 
                            message.contains("reCAPTCHA", ignoreCase = true) ||
                            message.contains("recaptcha", ignoreCase = true)) {
                            "Firebase configuration error. Please contact support or check Firebase Console settings."
                        } else {
                            message.ifEmpty { "Login failed. Please try again" }
                        }
                    }
                }
                Log.e("FirebaseAuth", "Login error: ${e.errorCode} - ${e.message}", e)
                onError(errorMessage)
            } catch (e: Exception) {
                val message = e.message ?: e.localizedMessage ?: ""
                if (message.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) || 
                    message.contains("reCAPTCHA", ignoreCase = true) ||
                    message.contains("recaptcha", ignoreCase = true)) {
                    Log.e("FirebaseAuth", "reCAPTCHA configuration error", e)
                    onError("Firebase configuration error. Please contact support.")
                } else {
                    Log.e("FirebaseAuth", "Login error", e)
                    onError(message.ifEmpty { "An unexpected error occurred" })
                }
            }
        }
    }

    fun register(email: String, password: String, onRegister: (FirebaseUser?) -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onError("Please fill in all fields")
            return
        }

        if (password.length < 6) {
            onError("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            try {
                val response = auth.createUserWithEmailAndPassword(email.trim(), password).await()
                onRegister(response.user)
            } catch (e: FirebaseAuthException) {
                val errorMessage = when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Invalid email address"
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists"
                    "ERROR_WEAK_PASSWORD" -> "Password is too weak. Use at least 6 characters"
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your connection"
                    else -> {
                        // التحقق من رسالة الخطأ لمعرفة إذا كان reCAPTCHA
                        val message = e.localizedMessage ?: e.message ?: ""
                        if (message.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) || 
                            message.contains("reCAPTCHA", ignoreCase = true) ||
                            message.contains("recaptcha", ignoreCase = true)) {
                            "Firebase configuration error. Please contact support or check Firebase Console settings."
                        } else {
                            message.ifEmpty { "Registration failed. Please try again" }
                        }
                    }
                }
                Log.e("FirebaseAuth", "Registration error: ${e.errorCode} - ${e.message}", e)
                onError(errorMessage)
            } catch (e: Exception) {
                val message = e.message ?: e.localizedMessage ?: ""
                if (message.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) || 
                    message.contains("reCAPTCHA", ignoreCase = true) ||
                    message.contains("recaptcha", ignoreCase = true)) {
                    Log.e("FirebaseAuth", "reCAPTCHA configuration error", e)
                    onError("Firebase configuration error. Please contact support.")
                } else {
                    Log.e("FirebaseAuth", "Registration error", e)
                    onError(message.ifEmpty { "An unexpected error occurred" })
                }
            }
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
    
    fun logout() {
        auth.signOut()
    }
    
    // Remember Me functionality
    companion object {
        private const val PREFS_NAME = "PopcornTimePrefs"
        private const val KEY_LAST_USER_ID = "last_user_id"
        private const val KEY_REMEMBER_ME = "remember_me"
        
        fun saveLastUserId(context: Context, userId: String?) {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_LAST_USER_ID, userId).apply()
        }
        
        fun getLastUserId(context: Context): String? {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_LAST_USER_ID, null)
        }
        
        fun setRememberMe(context: Context, remember: Boolean) {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_REMEMBER_ME, remember).apply()
        }
        
        fun getRememberMe(context: Context): Boolean {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_REMEMBER_ME, false)
        }
        
        fun clearLastUserId(context: Context) {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().remove(KEY_LAST_USER_ID).apply()
        }
    }
}
