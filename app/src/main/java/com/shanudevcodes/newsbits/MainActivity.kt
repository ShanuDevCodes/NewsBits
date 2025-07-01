package com.shanudevcodes.newsbits

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.data.Destination
import com.shanudevcodes.newsbits.ui.screens.EmptyScreen
import com.shanudevcodes.newsbits.ui.screens.MainUi
import com.shanudevcodes.newsbits.ui.screens.NewsDetailScreen
import com.shanudevcodes.newsbits.ui.theme.NewsBitsTheme
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
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
            LaunchedEffect(Unit) {
                dataStore.themeFlow.first()
                dataStore.dynamicColorFlow.first()
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
                                        text = "Powered By NewsAPI",
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
                                        openNavDraw = { scope.launch { drawerState.open() } }
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier.weight(0.65f)
                            ){
                                NavHost(
                                    startDestination = Destination.HOMESCREEN,
                                    navController = navController,
                                    enterTransition = {
                                        slideIntoContainer(
                                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                            animationSpec = tween(
                                                400,
                                                easing = FastOutSlowInEasing
                                            )
                                        ) + fadeIn(initialAlpha = 0.8f)
                                    },
                                    exitTransition = { ExitTransition.None },
                                    popEnterTransition = { EnterTransition.None },
                                    popExitTransition = {
                                        slideOutOfContainer(
                                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                            animationSpec = tween(
                                                400,
                                                easing = FastOutSlowInEasing
                                            )
                                        ) + fadeOut(targetAlpha = 0.9f)
                                    }
                                ) {
                                    composable<Destination.HOMESCREEN> {
                                        if (isPortrait) {
                                            MainUi(navController,openNavDraw = {scope.launch { drawerState.open() }})
                                        } else {
                                            EmptyScreen()
                                        }
                                    }
                                    composable<Destination.NEWSDETAILSCREEN> {
                                        NewsDetailScreen(
                                            it.arguments?.getInt("newsId") ?: 1,
                                            navController
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