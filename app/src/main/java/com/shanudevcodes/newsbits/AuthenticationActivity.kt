package com.shanudevcodes.newsbits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shanudevcodes.newsbits.data.AuthenticationDestination
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.ui.screens.authentication.LoginScreen
import com.shanudevcodes.newsbits.ui.screens.authentication.SignupScreen
import com.shanudevcodes.newsbits.ui.theme.NewsBitsTheme
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions

class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val showGuestSignup = intent.getBooleanExtra("showGuestSignup", true)
            val navController = rememberNavController()
            val dataStore = DataStoreManager(applicationContext)
            val themeOption by dataStore.themeFlow.collectAsState(initial = ThemeOptions.SYSTEM_DEFAULT)
            val dynamicColor by dataStore.dynamicColorFlow.collectAsState(initial = false)
            NewsBitsTheme(
                themeOption = themeOption,
                dynamicColor = dynamicColor
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceDim
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = AuthenticationDestination.SIGNUPDESTINATION,
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(400, easing = FastOutSlowInEasing)
                            ) + fadeIn(initialAlpha = 0.8f)
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(400, easing = FastOutSlowInEasing)
                            ) + fadeOut(targetAlpha = 0.9f)
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(400, easing = FastOutSlowInEasing)
                            ) + fadeIn(initialAlpha = 0.8f)
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(400, easing = FastOutSlowInEasing)
                            ) + fadeOut(targetAlpha = 0.9f)
                        }
                    ) {
                        composable<AuthenticationDestination.SIGNUPDESTINATION> {
                            SignupScreen(
                                showGuestSignup = showGuestSignup,
                                dataStore = dataStore,
                                onLoginClick = {
                                    navController.navigate(AuthenticationDestination.LOGINDESTINATION) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                        composable<AuthenticationDestination.LOGINDESTINATION> {
                            LoginScreen(
                                showGuestSignup = showGuestSignup,
                                dataStore = dataStore,
                                onSignupClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}