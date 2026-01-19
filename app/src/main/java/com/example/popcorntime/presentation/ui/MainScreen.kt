package com.example.popcorntime.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import com.example.popcorntime.R
import com.example.popcorntime.data.database.PlaylistManager
import com.example.popcorntime.theme.LocalThemeManager
import com.example.popcorntime.theme.ThemeManager
import com.example.popcorntime.language.LocalLanguageManager
import com.example.popcorntime.language.LanguageManager
import com.example.popcorntime.language.Strings
import com.example.popcorntime.ui.theme.orbitronsFontFamily
import com.example.popcorntime.viewmodel.FirebaseAuthViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.activity.compose.BackHandler
import com.example.popcorntime.language.LanguageManager.currentLanguage

class MainScreen(private val userName: String = "User") : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val themeManager = LocalThemeManager.current
        val isDarkTheme by themeManager.isDarkTheme
        val colors = themeManager.currentColors

        // Initialize ViewModel for logout functionality
        val firebaseViewModel = remember {
            try {
                val activity = context as? androidx.activity.ComponentActivity
                if (activity != null) {
                    ViewModelProvider(activity)[FirebaseAuthViewModel::class.java]
                } else {
                    FirebaseAuthViewModel() // Fallback if not in Activity context
                }
            } catch (e: Exception) {
                android.util.Log.e("MainScreen", "Error creating ViewModel: ${e.message}")
                FirebaseAuthViewModel() // Fallback
            }
        }

        // فحص حالة المصادقة
        val isUserLoggedIn = remember { mutableStateOf(firebaseViewModel.isUserLoggedIn()) }
        
        // تحديث حالة المصادقة عند تغييرها
        LaunchedEffect(Unit) {
            isUserLoggedIn.value = firebaseViewModel.isUserLoggedIn()
        }

        // منع الرجوع من MainScreen إلى Login
        BackHandler(enabled = true) {
            // لا تفعل شيء - يبقى في MainScreen
        }

        val systemUiController = rememberSystemUiController()

        SideEffect {
            systemUiController.setStatusBarColor(colors.background, darkIcons = !isDarkTheme)
            systemUiController.setNavigationBarColor(colors.background, darkIcons = !isDarkTheme)
        }

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var selectedIndex by remember { mutableIntStateOf(0) }
        var showThemeDialog by remember { mutableStateOf(false) }
        var showLanguageDialog by remember { mutableStateOf(false) }

        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val drawerWidth = screenWidth * 0.75f

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(drawerWidth),
                    color = colors.surface,
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp, bottom = 32.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.my_icon),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(60.dp)
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        clip = false
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Popcorn Time",
                                    fontFamily = orbitronsFontFamily,
                                    fontWeight = FontWeight.Light,
                                    fontSize = 18.sp,
                                    color = colors.onSurface,
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                val welcomeText = remember(currentLanguage) { Strings.get("welcome") }
                                Text(
                                    text = "$welcomeText, $userName",
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Light,
                                    color = colors.secondary
                                )
                            }
                        }

        val languageManager = LocalLanguageManager.current
        val currentLanguage = languageManager.currentLanguage.value
        
        val menuItems = remember(currentLanguage) {
            listOf(
                MenuItem(Strings.get("home"), Icons.Filled.Home),
                MenuItem(Strings.get("theme"), Icons.Filled.Palette),
                MenuItem(Strings.get("language"), Icons.Filled.Language),
                MenuItem(Strings.get("help_support"), Icons.Filled.Help),
                MenuItem(Strings.get("logout"), Icons.Filled.ExitToApp)
            ).filterIndexed { index, _ ->
                // إخفاء Logout إذا لم يكن المستخدم مسجل دخول
                if (index == 4) isUserLoggedIn.value else true
            }
        }

                        menuItems.forEachIndexed { index, item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch { drawerState.close() }
                                        when (index) {
                                            0 -> selectedIndex = 0
                                            1 -> {
                                                showThemeDialog = true
                                            }
                                            2 -> {
                                                showLanguageDialog = true
                                            }
                                            4 -> {
                                                // Logout functionality
                                                // مسح PlaylistManager من الذاكرة فقط (ليس من قاعدة البيانات)
                                                PlaylistManager.setUserId(null)
                                                
                                                firebaseViewModel.logout()
                                                FirebaseAuthViewModel.clearLastUserId(context)
                                                // Navigate to LoginScreen
                                                navigator.push(LoginScreen())
                                            }
                                        }
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                            ) {
                                Icon(
                                    item.icon,
                                    contentDescription = item.title,
                                    tint = if (index == 0) colors.primary else colors.onSurface
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    item.title,
                                    color = if (index == 0) colors.primary else colors.onSurface,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            if (index == 2) {
                                Divider(
                                    color = colors.secondary.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            },
            content = {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = when (selectedIndex) {
                                        0 -> "Home"
                                        1 -> "Favorites"
                                        2 -> "My Lists"
                                        3 -> "Profile"
                                        else -> "Popcorn Time"
                                    },
                                    fontFamily = orbitronsFontFamily,
                                    fontWeight = FontWeight.Light,
                                    fontSize = 20.sp,
                                    color = colors.onBackground,
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch { drawerState.open() }
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Menu",
                                        tint = colors.onBackground
                                    )
                                }
                            },
                            actions = {
                                if (selectedIndex == 0) {
                                    IconButton(onClick = {
                                        navigator.push(SearchScreen())
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Search,
                                            contentDescription = "Search",
                                            tint = colors.onBackground
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = colors.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = colors.surface
                        ) {
                            val items = listOf(
                                Triple("Home", Icons.Filled.Home, Icons.Outlined.Home),
                                Triple(
                                    "Favorites",
                                    Icons.Filled.Favorite,
                                    Icons.Outlined.FavoriteBorder
                                ),
                                Triple("My Lists", Icons.Filled.List, Icons.Outlined.List),
                                Triple("Profile", Icons.Filled.Person, Icons.Outlined.Person)
                            )

                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = {
                                        val icon =
                                            if (selectedIndex == index) item.third else item.second
                                        Icon(
                                            icon,
                                            contentDescription = item.first,
                                            tint = if (selectedIndex == index) colors.primary else colors.onSurface // استخدام onSurface
                                        )
                                    },
                                    label = {
                                        Text(
                                            item.first,
                                            fontFamily = orbitronsFontFamily,
                                            fontWeight = FontWeight.Light,
                                            color = if (selectedIndex == index) colors.primary else colors.onSurface // استخدام onSurface
                                        )
                                    },
                                    selected = selectedIndex == index,
                                    onClick = { selectedIndex = index },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent,
                                        selectedIconColor = colors.primary,
                                        selectedTextColor = colors.primary,
                                        unselectedIconColor = colors.onSurface,
                                        unselectedTextColor = colors.onSurface
                                    )
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        if (selectedIndex == 2) {
                            FloatingActionButton(
                                onClick = {
                                    PlaylistManager.showAddPlaylistDialog = true
                                    PlaylistManager.editingPlaylist = null
                                },
                                containerColor = colors.primary,
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add New List",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    containerColor = colors.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(colors.background)
                    ) {
                        when (selectedIndex) {
                            0 -> MoviesContent()
                            1 -> FavoritesContent()
                            2 -> PlaylistsContent()
                            3 -> ProfileContent(userName)
                        }
                    }
                }
            }
        )

        if (showThemeDialog) {
            ThemeSelectionDialog(
                onDismiss = { showThemeDialog = false }
            )
        }
        
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
}

data class MenuItem(val title: String, val icon: ImageVector)