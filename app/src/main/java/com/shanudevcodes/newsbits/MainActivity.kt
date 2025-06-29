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
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            NewsBitsTheme(
                themeOption = themeOption,
                dynamicColor = false
            ) {
                BackHandler {
                    if(drawerState.isOpen) {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                }
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Text("Drawer title", modifier = Modifier.padding(16.dp))
                            HorizontalDivider()
                            NavigationDrawerItem(
                                label = { Text(text = "Drawer Item") },
                                selected = false,
                                onClick = { /*TODO*/ }
                            )
                        }
                    }
                ) {
                    Box() {
                        Row {
                            Box(modifier = Modifier.weight(0.35f)) {
                                if (!isPortrait) {
                                    MainUi(navController, openNavDraw = {scope.launch { drawerState.open() }})
                                } else {
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
                            if (!isPortrait) {
                                Box(
                                    modifier = Modifier.weight(0.65f)
                                ) {
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
    }
    fun isTablet(): Boolean {
        return resources.configuration.smallestScreenWidthDp >= 600
    }
}