package com.shanudevcodes.newsbits.ui.screens.home

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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shanudevcodes.newsbits.data.HomeDestination
import com.shanudevcodes.newsbits.data.News
import com.shanudevcodes.newsbits.ui.animation.ExpressiveEasing
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeUI(isPortrait: Boolean, drawerState: DrawerState, newsViewModel: NewsViewModel){
    val navController = rememberNavController()
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
                    HomeListUi(
                        navController,
                        openNavDraw = {
                            scope.launch {
                                drawerState.open()
//                                                    wideNavigationRailState.expand()
                            }
                        },
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
            ) {
                NavHost(
                    startDestination = HomeDestination.HOMESCREEN,
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
                    composable<HomeDestination.HOMESCREEN> {
                        if (isPortrait) {
                            SearchResultScreen()
//                            HomeListUi(navController, openNavDraw = {
//                                scope.launch {
//                                    drawerState.open()
//                                }
//                            }, newsViewModel)
                        } else {
                            SearchResultScreen()
                            //EmptyScreen()
                        }
                    }
                    composable<HomeDestination.NEWSDETAILSCREEN> {
                        HomeDetailScreen(
                            it.arguments?.getInt("newsId") ?: 1,
                            navController,
                            newsViewModel,
                            it.arguments?.getString("news")
                                ?: News.NEWS_ALL.name
                        )
                    }
//                                        composable<HomeDestination.HOMESCREEN> {
//                                            NavigableListDetailPaneScaffold(
//                                                navigator = listDetailNavigator,
//                                                listPane = {
//                                                    AnimatedPane(
//                                                        enterTransition = slideInHorizontally(
//                                                            animationSpec = tween(
//                                                                durationMillis = 600,
//                                                                easing = ExpressiveEasing.Emphasized
//                                                            )
//                                                        ),
//                                                        exitTransition = slideOutHorizontally (
//                                                            targetOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
//                                                            animationSpec = tween(
//                                                                durationMillis = 600,
//                                                                easing = ExpressiveEasing.Emphasized
//                                                            )
//                                                        ),
//                                                    ) {
//                                                        HomeListUi(
//                                                            navigator = listDetailNavigator,
//                                                            openNavDraw = {
//                                                                scope.launch {
//                                                                    drawerState.open()
////                                                    wideNavigationRailState.expand()
//                                                                }
//                                                            },
//                                                            newsViewModel
//                                                        )
//                                                    }
//                                                },
//                                                detailPane = {
//                                                    AnimatedPane(
//                                                        enterTransition = slideInHorizontally(
//                                                            animationSpec = tween(
//                                                                durationMillis = 600,
//                                                                easing = ExpressiveEasing.Emphasized
//                                                            ),
//                                                            initialOffsetX = { fullWidth -> fullWidth }
//                                                        ),
//                                                        exitTransition = slideOutHorizontally (
//                                                            targetOffsetX = { fullWidth -> -(fullWidth * 0.3f).toInt() },
//                                                            animationSpec = tween(
//                                                                durationMillis = 600,
//                                                                easing = ExpressiveEasing.Emphasized
//                                                            )
//                                                        ),
//                                                    ) {
//                                                        // Show the detail pane content if selected item is available
//                                                        listDetailNavigator.currentDestination?.contentKey?.let {
//                                                            val contentKey = it
//                                                            val (type, indexStr) = contentKey.toString()
//                                                                .split("::")
//                                                            HomeDetailScreen(
//                                                                indexStr.toInt(),
//                                                                navController,
//                                                                newsViewModel,
//                                                                type
//                                                            )
//                                                        }?:EmptyScreen()
//                                                    }
//                                                },
//                                                defaultBackBehavior = BackNavigationBehavior.PopUntilContentChange
//                                            )
//                                        }

                }
            }
        }
    }
}