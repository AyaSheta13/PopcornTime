package com.example.popcorntime.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

// استخدم CompositionLocal لنقل حالة اللغة
val LocalLanguageManager = staticCompositionLocalOf { LanguageManager }

object LanguageManager {
    val currentLanguage = mutableStateOf(Language.ENGLISH)
    private var onLanguageChangeListener: (() -> Unit)? = null
    
    enum class Language(val code: String, val apiCode: String, val displayName: String) {
        ENGLISH("en", "en-US", "English"),
        ARABIC("ar", "ar-SA", "العربية")
    }
    
    fun setLanguage(language: Language) {
        currentLanguage.value = language
        // تحديث Locale للتطبيق
        updateLocale(language.code)
        // إشعار المستمعين بتغيير اللغة
        onLanguageChangeListener?.invoke()
    }
    
    fun setOnLanguageChangeListener(listener: (() -> Unit)?) {
        onLanguageChangeListener = listener
    }
    
    fun getApiLanguageCode(): String {
        return currentLanguage.value.apiCode
    }
    
    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
    }
    
    fun isArabic(): Boolean = currentLanguage.value == Language.ARABIC
    fun isEnglish(): Boolean = currentLanguage.value == Language.ENGLISH
}

// Helper functions للترجمة
object Strings {
    fun get(key: String): String {
        val isArabic = LanguageManager.isArabic()
        return if (isArabic) {
            arabicStrings[key] ?: key
        } else {
            englishStrings[key] ?: key
        }
    }
    
    private val englishStrings = mapOf(
        "home" to "Home",
        "theme" to "Theme",
        "language" to "Language",
        "help_support" to "Help & Support",
        "logout" to "Logout",
        "welcome" to "Welcome",
        "select_language" to "Select Language",
        "english" to "English",
        "arabic" to "العربية",
        "cancel" to "Cancel",
        "apply" to "Apply"
    )
    
    private val arabicStrings = mapOf(
        "home" to "الرئيسية",
        "theme" to "المظهر",
        "language" to "اللغة",
        "help_support" to "المساعدة والدعم",
        "logout" to "تسجيل الخروج",
        "welcome" to "مرحباً",
        "select_language" to "اختر اللغة",
        "english" to "English",
        "arabic" to "العربية",
        "cancel" to "إلغاء",
        "apply" to "تطبيق"
    )
}

