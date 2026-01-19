package com.example.popcorntime.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.popcorntime.R
import com.example.popcorntime.ui.theme.orbitronsFontFamily
import com.google.accompanist.systemuicontroller.rememberSystemUiController

data class OnBoardingPage(
    val image: Int,
    val title: String,
    val description: String
)

class OnBoardingScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val backgroundColor = Color(0xFF100A10)

        val systemUiController = rememberSystemUiController()

        val pages = listOf(
            OnBoardingPage(
                image = R.drawable.onboarding1,
                title = "Discover New Movies",
                description = "Browse the latest releases and trending hits. Never miss a movie you'll love"
            ),
            OnBoardingPage(
                image = R.drawable.onboarding2,
                title = "Create Your Playlists",
                description = "Organize your favorite movies into custom playlists for easy access"
            ),
            OnBoardingPage(
                image = R.drawable.onboarding3,
                title = "Watch Trailers",
                description = "Check out trailers before watching. Get a glimpse of the story and excitement"
            ),
            OnBoardingPage(
                image = R.drawable.onboarding4,
                title = "Your Favorites",
                description = "Save movies you love and access them anytime in your Favorites list"
            )
        )

        var currentPage by remember { mutableStateOf(0) }

        // التعامل مع زر الرجوع للجهاز
        BackHandler(enabled = currentPage > 0) {
            currentPage--
        }

        fun goToNextPage() {
            if (currentPage < pages.size - 1) {
                currentPage++
            } else {
                navigator.push(HelloScreen())
            }
        }
        // تغيير ألوان شريط الحالة والشريط السفلي بعد كل إعادة رسم
        LaunchedEffect(Unit) {
            systemUiController.setStatusBarColor(backgroundColor, darkIcons = false)
            systemUiController.setNavigationBarColor(backgroundColor, darkIcons = false)
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = backgroundColor // لون الخلفية يغطي Scaffold بالكامل
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = painterResource(id = pages[currentPage].image),
                        contentDescription = "Onboarding Image ${currentPage + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentScale = ContentScale.Crop
                    )

                    TextButton(
                        onClick = { navigator.push(HelloScreen()) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 16.dp, end = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Skip",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Skip",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // مؤشرات التنقل تحت الصورة مباشرة
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    pages.forEachIndexed { index, _ ->
                        val color = if (index == currentPage) Color(0xFF9B27B0) else Color.LightGray
                        val width = animateDpAsState(targetValue = if (index == currentPage) 24.dp else 8.dp)
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(width.value)
                                .clip(RoundedCornerShape(50))
                                .background(color)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                        .padding(horizontal = 32.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = pages[currentPage].title,
                        fontSize = 28.sp,
                        fontFamily = orbitronsFontFamily,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFF9B27B0),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = pages[currentPage].description,
                        fontSize = 16.sp,
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { goToNextPage() },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B27B0)),
                        modifier = Modifier
                            .widthIn(min = 140.dp, max = 200.dp)
                            .height(56.dp)
                    ) {
                        Text(
                            text = if (currentPage == pages.size - 1) "Start" else "Next",
                            color = Color.White,
                            fontFamily = orbitronsFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}