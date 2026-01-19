package com.example.popcorntime.presentation.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.popcorntime.theme.ThemeManager

@Composable
fun ThemeSelectionDialog(
    onDismiss: () -> Unit
) {
    val isDarkTheme by ThemeManager.isDarkTheme
    val currentColors = ThemeManager.currentColors

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
                    text = "Select Theme",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = currentColors.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                // زر التبديل بين السمتين
                ThemeToggleSwitch()

                Spacer(modifier = Modifier.height(32.dp))

                // أزرار الإجراءات
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = currentColors.secondary
                        )
                    ) {
                        Text(
                            "Close",
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
fun ThemeToggleSwitch() {
    val isDarkTheme by ThemeManager.isDarkTheme
    val currentColors = ThemeManager.currentColors

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(if (isDarkTheme) Color(0xFF2A1E2A) else Color(0xFFF0F0F0))
            .clickable { ThemeManager.toggleTheme() },
        contentAlignment = Alignment.Center
    ) {
        val circleSize = 40.dp        // تصغير الدائرة
        val padding = 8.dp
        val boxWidthDp = with(LocalDensity.current) { constraints.maxWidth.toDp() }

        val circleOffset by animateDpAsState(
            targetValue = if (isDarkTheme) (boxWidthDp - circleSize - padding) else padding
        )

        // الخلفية للنصوص
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp)  // زيادة الحشو للنصوص
            ) {
                Icon(
                    Icons.Filled.LightMode,
                    contentDescription = "Light Mode",
                    tint = if (!isDarkTheme) currentColors.primary else currentColors.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Light",
                    color = if (!isDarkTheme) currentColors.primary else currentColors.secondary,
                    fontFamily = FontFamily.Serif,
                    fontWeight = if (!isDarkTheme) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 32.dp)   // زيادة الحشو للنصوص
            ) {
                Text(
                    "Dark",
                    color = if (isDarkTheme) currentColors.primary else currentColors.secondary,
                    fontFamily = FontFamily.Serif,
                    fontWeight = if (isDarkTheme) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Filled.DarkMode,
                    contentDescription = "Dark Mode",
                    tint = if (isDarkTheme) currentColors.primary else currentColors.secondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // الدائرة المتحركة فوق الخلفية
        Box(
            modifier = Modifier
                .size(circleSize)
                .offset(x = circleOffset)
                .shadow(4.dp, CircleShape, clip = false)
                .background(currentColors.primary, CircleShape)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
