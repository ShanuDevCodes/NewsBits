package com.shanudevcodes.newsbits.ui.screens.home

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shanudevcodes.newsbits.data.HomeDestination
import com.shanudevcodes.newsbits.ui.animation.ExpressiveEasing
import com.shanudevcodes.newsbits.ui.screens.AppListUI
import com.shanudevcodes.newsbits.ui.screens.EmptyScreen
import com.shanudevcodes.newsbits.ui.screens.SettingsScreen
import com.shanudevcodes.newsbits.ui.screens.bookmark.BookmarkDetailScreen
import com.shanudevcodes.newsbits.viewmodel.AiViewModel
import com.shanudevcodes.newsbits.viewmodel.AppListUIViewModel
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUI(
    navController : NavHostController,
    rootNavController: NavHostController,
    isPortrait: Boolean,
    newsViewModel: NewsViewModel,
    aiViewModel: AiViewModel
){
    val appListUIViewModel: AppListUIViewModel = viewModel()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val isHome = currentBackStackEntry?.destination
        ?.hierarchy
        ?.any { it.route == HomeDestination.HOMESCREEN::class.qualifiedName } == true
    val navControllerBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navControllerBackStackEntry?.destination?.hierarchy) {
        if (currentBackStackEntry?.destination?.hierarchy?.any { it.route == HomeDestination.HOMESCREEN::class.qualifiedName } == true) {
            newsViewModel.resetCurrentLink()
        }
    }
    BackHandler(enabled = !isHome) {
        navController.popBackStack()
    }
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
                    AppListUI(
                        rootNavController = rootNavController,
                        navController = navController,
                        newsViewModel = newsViewModel,
                        aiViewModel = aiViewModel,
                        viewModel = appListUIViewModel,
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
                            AppListUI(
                                rootNavController = rootNavController,
                                navController = navController,
                                newsViewModel = newsViewModel,
                                aiViewModel = aiViewModel,
                                viewModel = appListUIViewModel,
                            )
                        } else {
                            EmptyScreen()
                        }
                    }
                    composable<HomeDestination.SEARCHRESULTDETAILSCREEN> {
                        SearchResultDetailScreen(
                            navController = navController,
                            link = it.arguments?.getString("link") ?: ""
                        )
                    }
                    composable<HomeDestination.BOOKMARKDETAILSCREEN> {
                        val arg = it.arguments
                        BookmarkDetailScreen(
                            newsId = arg?.getString("newsId") ?: "",
                            navController = navController
                        )
                    }
                    composable<HomeDestination.SETTINGS> {
                        SettingsScreen(navController = navController)
                    }
                    composable<HomeDestination.HELPCENTER> {

                    }
                    composable<HomeDestination.ABOUT> {

                    }
                }
            }
        }
    }
}