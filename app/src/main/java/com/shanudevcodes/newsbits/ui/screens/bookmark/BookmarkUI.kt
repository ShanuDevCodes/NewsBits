package com.shanudevcodes.newsbits.ui.screens.bookmark

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shanudevcodes.newsbits.data.BookmarkDestination
import com.shanudevcodes.newsbits.ui.animation.ExpressiveEasing
import com.shanudevcodes.newsbits.ui.screens.EmptyScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BookmarkUI(drawerState: DrawerState){
    val navController = rememberNavController()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row {
            val animatedWeight = remember { Animatable(0f) }
            LaunchedEffect(isPortrait) {
                if (!isPortrait) {
                    delay(100)
                    animatedWeight.animateTo(
                        targetValue = 0.35f,
                        animationSpec = tween(
                            durationMillis = 600,
                            easing = ExpressiveEasing.EmphasizedDecelerate
                        )
                    )
                } else {
                    animatedWeight.snapTo(0f)
                }
            }
            if (animatedWeight.value > 0f) {
                Box(modifier = Modifier.weight(animatedWeight.value)) {
                    BookMarksScreen(
                        openNavDraw = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        navController = navController
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
            ) {
                NavHost(
                    startDestination = BookmarkDestination.BOOKMARKSCREEN,
                    navController = navController,
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
                ) {
                    composable<BookmarkDestination.BOOKMARKSCREEN> {
                        if (isPortrait) {
                            BookMarksScreen(
                                openNavDraw = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                },
                                navController = navController
                            )
                        } else {
                            EmptyScreen()
                        }
                    }
                    composable<BookmarkDestination.BOOKMARKDETAILSCREEN> {
                        val arg = it.arguments
                        BookmarkDetailScreen(
                            newsId = arg?.getInt("newsId")?: 0,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}