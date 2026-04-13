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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shanudevcodes.newsbits.data.AuthenticationDestination
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.ui.animation.ExpressiveEasing
import com.shanudevcodes.newsbits.ui.screens.authentication.EmailVerificationScreen
import com.shanudevcodes.newsbits.ui.screens.authentication.LoginScreen
import com.shanudevcodes.newsbits.ui.screens.authentication.SignupScreen
import com.shanudevcodes.newsbits.ui.theme.NewsBitsTheme
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val showGuestSignup = intent.getBooleanExtra("showGuestSignup", true)
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
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
                            val to = targetState.destination.route
                            if (to?.contains(AuthenticationDestination.EMAILVERIFICATIONDESTINATION::class.qualifiedName?:"") == true) {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(
                                        durationMillis = 600,
                                        easing = ExpressiveEasing.Emphasized
                                    )
                                )
                            } else {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                                ) + fadeIn(initialAlpha = 0.8f)
                            }
                        },
                        exitTransition = {
                            val to = targetState.destination.route
                            if (to?.contains(AuthenticationDestination.EMAILVERIFICATIONDESTINATION::class.qualifiedName?:"") == true) {
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
                                    animationSpec = tween(
                                        durationMillis = 600,
                                        easing = ExpressiveEasing.Emphasized
                                    )
                                )
                            }else{
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                                ) + fadeOut(targetAlpha = 0.9f)
                            }
                        },
                        popEnterTransition = {
                            val to = targetState.destination.route
                            if (to?.contains(AuthenticationDestination.EMAILVERIFICATIONDESTINATION::class.qualifiedName?:"") == true) {
                                slideInHorizontally(
                                    initialOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = ExpressiveEasing.Emphasized
                                    )
                                )
                            }else{
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                                ) + fadeIn(initialAlpha = 0.8f)
                            }
                        },
                        popExitTransition = {
                            val to = targetState.destination.route
                            if (to?.contains(AuthenticationDestination.EMAILVERIFICATIONDESTINATION::class.qualifiedName?:"") == true) {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = ExpressiveEasing.Emphasized
                                    )
                                )
                            }else{
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                                ) + fadeOut(targetAlpha = 0.9f)
                            }
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
                                },
                                onEmailVerification = {
                                    navController.navigate(AuthenticationDestination.EMAILVERIFICATIONDESTINATION) {
                                        popUpTo(0) { inclusive = true }
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
                                },
                                onEmailVerification = {
                                    navController.navigate(AuthenticationDestination.EMAILVERIFICATIONDESTINATION) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable<AuthenticationDestination.EMAILVERIFICATIONDESTINATION>(
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(
                                        durationMillis = 600,
                                        easing = ExpressiveEasing.Emphasized
                                    )
                                )
                            },
                            exitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
                                    animationSpec = tween(
                                        durationMillis = 600,
                                        easing = ExpressiveEasing.Emphasized
                                    )
                                )

                            },
                            popEnterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = ExpressiveEasing.Emphasized
                                    )
                                )
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = ExpressiveEasing.Emphasized
                                    )
                                )
                            }
                        ){
                            EmailVerificationScreen(
                                dataStore = dataStore
                            )
                        }
                    }
                }
            }
        }
    }
}