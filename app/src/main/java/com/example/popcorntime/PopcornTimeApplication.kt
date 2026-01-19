package com.example.popcorntime

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class PopcornTimeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase - simplest way
        try {
            FirebaseApp.initializeApp(this)
            Log.d("PopcornTimeApp", "Firebase initialized")
        } catch (e: IllegalStateException) {
            // Already initialized - this is fine
            Log.d("PopcornTimeApp", "Firebase already initialized")
        } catch (e: Exception) {
            Log.e("PopcornTimeApp", "Firebase error: ${e.message}", e)
        }
    }
}
