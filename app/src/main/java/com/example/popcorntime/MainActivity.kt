package com.example.popcorntime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.navigator.Navigator
import com.example.popcorntime.data.database.FavoritesManager
import com.example.popcorntime.data.database.PlaylistManager
import com.example.popcorntime.presentation.ui.HelloScreen
import com.example.popcorntime.presentation.ui.LoginScreen
import com.example.popcorntime.presentation.ui.MainScreen
import com.example.popcorntime.presentation.ui.OnBoardingScreen
import com.example.popcorntime.theme.LocalThemeManager
import com.example.popcorntime.theme.ThemeManager
import com.example.popcorntime.viewmodel.FirebaseAuthViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ تهيئة Firebase أولاً (إذا لم يتم تهيئته في Application)
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                android.util.Log.d("MainActivity", "Firebase initialized in MainActivity")
            } else {
                android.util.Log.d("MainActivity", "Firebase already initialized")
            }
        } catch (e: IllegalStateException) {
            // Already initialized - this is fine
            android.util.Log.d("MainActivity", "Firebase already initialized (caught)")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Firebase initialization error: ${e.message}", e)
        }

        // ✅ تهيئة Managers مرة واحدة فقط هنا
        FavoritesManager.initialize(applicationContext)
        // PlaylistManager سيتم تهيئته بعد معرفة userId في AppContent

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                LocalThemeManager provides ThemeManager,
                com.example.popcorntime.language.LocalLanguageManager provides com.example.popcorntime.language.LanguageManager
            ) {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    // حالة لمعرفة متى انتهت التهيئة
    var isInitialized by remember { mutableStateOf(false) }
    var isCheckingAuth by remember { mutableStateOf(true) }
    var isUserLoggedIn by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("User") }

    // فحص حالة المصادقة عند بدء التطبيق
    LaunchedEffect(Unit) {
        delay(1000) // تأخير لضمان تهيئة Firebase بشكل كامل
        
        try {
            // التحقق من أن Firebase مهيأ
            val firebaseApps = FirebaseApp.getApps(context)
            if (firebaseApps.isNotEmpty()) {
                // استخدام FirebaseAuth مباشرة
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                
                // فحص دقيق - التأكد من أن المستخدم موجود وصالح
                isUserLoggedIn = currentUser != null && !currentUser.isAnonymous
                
                // إذا كان المستخدم مسجل دخول، احصل على اسمه وتهيئة PlaylistManager
                if (isUserLoggedIn && currentUser != null) {
                    userName = currentUser.displayName 
                        ?: currentUser.email?.substringBefore("@") 
                        ?: "User"
                    
                    // تهيئة PlaylistManager مع userId الحالي
                    PlaylistManager.initialize(context, currentUser.uid)
                } else {
                    // التحقق من Remember Me
                    val rememberMe = FirebaseAuthViewModel.getRememberMe(context)
                    if (rememberMe) {
                        val lastUserId = FirebaseAuthViewModel.getLastUserId(context)
                        if (lastUserId != null) {
                            // المستخدم موجود في Remember Me لكن ليس مسجل دخول حالياً
                            // سيحتاج إلى تسجيل دخول مرة أخرى
                            isUserLoggedIn = false
                        }
                    }
                    
                    isUserLoggedIn = false
                    userName = "User"
                    // تهيئة PlaylistManager بدون userId
                    PlaylistManager.initialize(context, null)
                }
            } else {
                android.util.Log.w("AppContent", "Firebase not initialized yet")
                isUserLoggedIn = false
                PlaylistManager.initialize(context, null)
            }
        } catch (e: IllegalStateException) {
            // Firebase not initialized
            android.util.Log.e("AppContent", "Firebase not initialized: ${e.message}")
            isUserLoggedIn = false
            PlaylistManager.initialize(context, null)
        } catch (e: Exception) {
            android.util.Log.e("AppContent", "Error checking auth state: ${e.message}", e)
            isUserLoggedIn = false
            PlaylistManager.initialize(context, null)
        }
        
        isCheckingAuth = false
        delay(300) // تأخير إضافي بسيط
        isInitialized = true
    }

    if (!isInitialized || isCheckingAuth) {
        //  عرض شاشة تحميل أثناء التهيئة
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // إذا كان المستخدم مسجل دخول، انتقل مباشرة إلى MainScreen
        // وإلا ابدأ من OnBoardingScreen
        if (isUserLoggedIn) {
            Navigator(MainScreen(userName = userName))
        } else {
            Navigator(OnBoardingScreen())
        }
    }
}