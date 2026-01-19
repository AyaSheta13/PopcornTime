package com.example.popcorntime.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.popcorntime.data.Result
import com.example.popcorntime.theme.ThemeManager
import com.example.popcorntime.ui.theme.orbitronsFontFamily

@Composable
fun <T> ResultHandler(
    result: Result<T>,
    onSuccess: @Composable (T) -> Unit,
    onError: @Composable ((String) -> Unit)? = null,
    onLoading: @Composable (() -> Unit)? = null
) {
    when (result) {
        is Result.Loading -> {
            onLoading?.invoke() ?: DefaultLoadingState()
        }
        is Result.Success -> {
            onSuccess(result.data)
        }
        is Result.Error -> {
            onError?.invoke(result.message) ?: DefaultErrorState(result.message)
        }
    }
}

@Composable
fun DefaultLoadingState() {
    val colors = ThemeManager.currentColors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = colors.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                color = colors.onBackground,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun DefaultErrorState(
    message: String = "An error occurred",
    onRetry: (() -> Unit)? = null
) {
    val colors = ThemeManager.currentColors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error",
                color = colors.onBackground,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = colors.secondary,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.Button(
                    onClick = onRetry,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    )
                ) {
                    Text(
                        text = "Retry",
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String = "No items found"
) {
    val colors = ThemeManager.currentColors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = colors.secondary,
            fontFamily = FontFamily.Serif,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

