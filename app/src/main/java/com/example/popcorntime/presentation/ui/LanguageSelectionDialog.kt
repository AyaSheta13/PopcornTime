package com.example.popcorntime.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.popcorntime.language.LanguageManager
import com.example.popcorntime.language.Strings
import com.example.popcorntime.theme.ThemeManager

@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit
) {
    val currentLanguage = LanguageManager.currentLanguage.value
    val currentColors = ThemeManager.currentColors
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(300.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = currentColors.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Strings.get("select_language"),
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = currentColors.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                // خيارات اللغة
                LanguageOption(
                    language = LanguageManager.Language.ENGLISH,
                    isSelected = selectedLanguage == LanguageManager.Language.ENGLISH,
                    onClick = { selectedLanguage = LanguageManager.Language.ENGLISH },
                    colors = currentColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                LanguageOption(
                    language = LanguageManager.Language.ARABIC,
                    isSelected = selectedLanguage == LanguageManager.Language.ARABIC,
                    onClick = { selectedLanguage = LanguageManager.Language.ARABIC },
                    colors = currentColors
                )

                Spacer(modifier = Modifier.height(32.dp))

                // أزرار الإجراءات
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = currentColors.secondary
                        )
                    ) {
                        Text(
                            Strings.get("cancel"),
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Light
                        )
                    }
                    
                    Button(
                        onClick = {
                            val previousLanguage = LanguageManager.currentLanguage.value
                            LanguageManager.setLanguage(selectedLanguage)
                            // إعادة تحميل البيانات إذا تغيرت اللغة
                            if (previousLanguage != selectedLanguage) {
                                // سيتم إعادة تحميل البيانات تلقائياً عند تغيير اللغة
                            }
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentColors.primary
                        )
                    ) {
                        Text(
                            Strings.get("apply"),
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageOption(
    language: LanguageManager.Language,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: com.example.popcorntime.theme.AppColors
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) colors.primary.copy(alpha = 0.2f)
                else colors.surface
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = language.displayName,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) colors.primary else colors.onBackground
            )
            
            if (isSelected) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

