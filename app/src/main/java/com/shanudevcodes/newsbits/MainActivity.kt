package com.shanudevcodes.newsbits

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.data.Destination
import com.shanudevcodes.newsbits.data.News
import com.shanudevcodes.newsbits.ui.screens.EmptyScreen
import com.shanudevcodes.newsbits.ui.screens.MainUi
import com.shanudevcodes.newsbits.ui.screens.NewsDetailScreen
import com.shanudevcodes.newsbits.ui.theme.NewsBitsTheme
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            requestedOrientation = if (!isTablet()) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
            val configuration = LocalConfiguration.current
            val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            val dataStore = DataStoreManager(applicationContext)
            val themeOption by dataStore.themeFlow.collectAsState(initial = ThemeOptions.SYSTEM_DEFAULT)
            val dynamicColor by dataStore.dynamicColorFlow.collectAsState(initial = false)
            var themeLoaded by remember { mutableStateOf(false) }
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            var selected by remember { mutableIntStateOf(0) }
            val newsViewModel: NewsViewModel = viewModel()
            newsViewModel.loadTopNews()
            val newsList = newsViewModel.allNewsPagingFlow.collectAsLazyPagingItems()
            val isNewsLoaded = newsViewModel.isNewsLoaded.collectAsState()
            LaunchedEffect(Unit) {
                val isFirstLaunch = dataStore.firstLaunch.first()
                delay(300)
                if (!isOnline(applicationContext)) {
                    if (!isFirstLaunch) {
                        Toast.makeText(
                            applicationContext,
                            "You're offline. Showing old news.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        Toast.makeText(
                            applicationContext,
                            "You're offline",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                dataStore.setFirstLaunch(false)
            }
            LaunchedEffect(Unit) {
                var refreshTime = 0
                while (!isNewsLoaded.value) {
                    if (isOnline(applicationContext)) {
                        newsViewModel.loadTopNews()
                        newsList.refresh()
                        newsViewModel.newsLoaded()
                        delay(300)
                        if (refreshTime > 0){
                            Toast.makeText(applicationContext, "You're online now. Showing recent news.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    refreshTime++
                    delay(2000)
                }
            }
            LaunchedEffect(Unit) {
                dataStore.dynamicColorFlow.first()
                dataStore.themeFlow.first()
                themeLoaded = true
            }
            splashScreen.setKeepOnScreenCondition { !themeLoaded }
            NewsBitsTheme(
                themeOption = themeOption,
                dynamicColor = dynamicColor
            ) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.width(300.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            ) {
                                Column {
                                    Text("News Bits", color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(16.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    NavigationDrawerItem(
                                        label = { Text(text = "Drawer Item") },
                                        selected = selected == 1,
                                        onClick = { if(selected==0) selected = 1 else selected = 0},
                                        colors = NavigationDrawerItemDefaults.colors(
                                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            unselectedContainerColor = Color.Transparent, // or your custom color
                                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    dataStore.setDynamicColor(!dynamicColor)
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (dynamicColor) Icons.Filled.Palette else Icons.Outlined.Palette,
                                                contentDescription = "Color Scheme Change"
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    dataStore.saveThemeOption(
                                                        when (themeOption) {
                                                            ThemeOptions.SYSTEM_DEFAULT -> ThemeOptions.LIGHT
                                                            ThemeOptions.LIGHT -> ThemeOptions.DARK
                                                            ThemeOptions.DARK -> ThemeOptions.SYSTEM_DEFAULT
                                                        }
                                                    )
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = when (themeOption) {
                                                    ThemeOptions.SYSTEM_DEFAULT -> Icons.Default.Contrast
                                                    ThemeOptions.LIGHT -> Icons.Default.LightMode
                                                    ThemeOptions.DARK ->Icons.Default.DarkMode
                                                },
                                                contentDescription = "Theme Change"
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Version: ${BuildConfig.VERSION_NAME}",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    Text(
                                        text = "Powered By NewsData.io",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Box(){
                        Row {
                            val animatedWeight = remember { Animatable(0f) }
                            LaunchedEffect(isPortrait) {
                                if (!isPortrait) {
                                    delay(100)
                                    animatedWeight.animateTo(
                                        targetValue = 0.35f,
                                        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                                    )
                                } else {
                                    animatedWeight.snapTo(0f)
                                }
                            }
                            if (animatedWeight.value > 0f) {
                                Box(modifier = Modifier.weight(animatedWeight.value)) {
                                    MainUi(
                                        navController,
                                        openNavDraw = { scope.launch { drawerState.open() } },
                                        newsViewModel
                                    )
                                }
                            }
                            if (!isPortrait) {
                                VerticalDivider(
                                    modifier = Modifier
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(0.65f)
                                    .clipToBounds()
                            ){
                                NavHost(
                                    startDestination = Destination.HOMESCREEN,
                                    navController = navController,
                                    enterTransition = {
                                        slideIntoContainer(
                                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                            animationSpec = tween(
                                                200,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                    },
                                    exitTransition = {
                                        slideOutHorizontally(
                                            targetOffsetX = { -200 }, // Slide left by 50 pixels
                                            animationSpec = tween(
                                                durationMillis = 200,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                    },
                                    popEnterTransition = {
                                        slideInHorizontally(
                                            initialOffsetX = { -200 },
                                            animationSpec = tween(
                                                durationMillis = 200,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                    },
                                    popExitTransition = {
                                        slideOutOfContainer(
                                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                            animationSpec = tween(
                                                200,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                    }
                                ) {
                                    composable<Destination.HOMESCREEN> {
                                        if (isPortrait) {
                                            MainUi(navController,openNavDraw = {scope.launch { drawerState.open() }},newsViewModel)
                                        } else {
                                            EmptyScreen()
                                        }
                                    }
                                    composable<Destination.NEWSDETAILSCREEN> {
                                        NewsDetailScreen(
                                            it.arguments?.getInt("newsId") ?: 1,
                                            navController,
                                            newsViewModel,
                                            it.arguments?.getString("news")?: News.NEWS_ALL.name
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    fun isTablet(): Boolean {
        return resources.configuration.smallestScreenWidthDp >= 600
    }
}
fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}