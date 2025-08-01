package com.shanudevcodes.newsbits.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.Destination
import com.shanudevcodes.newsbits.data.NoRippleInteractionSource
import com.shanudevcodes.newsbits.ui.animation.ExpressiveEasing
import com.shanudevcodes.newsbits.ui.screens.bookmark.BookMarksScreen
import com.shanudevcodes.newsbits.ui.screens.home.HomeListUi
import com.shanudevcodes.newsbits.viewmodel.AiViewModel
import com.shanudevcodes.newsbits.viewmodel.AppListUIViewModel
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel

data class HomeUiDestination(
    val name: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val destination: Destination,
)

val homeUiDestinations = listOf(
    HomeUiDestination(
        name = "Home",
        selectedIcon = R.drawable.home_filled,
        unselectedIcon = R.drawable.home,
        destination = Destination.HOME
    ),
    HomeUiDestination(
        name = "Explore",
        selectedIcon = R.drawable.explore_filled,
        unselectedIcon = R.drawable.explore,
        destination = Destination.EXPLORE
    ),
    HomeUiDestination(
        name = "Bit Digest",
        selectedIcon = R.drawable.digest_400,
        unselectedIcon = R.drawable.digest_300,
        destination = Destination.BITDIGEST
    ),
    HomeUiDestination(
        name = "Bookmarks",
        selectedIcon = R.drawable.bookmark_filled,
        unselectedIcon = R.drawable.bookmark,
        destination = Destination.BOOKMARKS
    ),
    HomeUiDestination(
        name = "Profile",
        selectedIcon = R.drawable.account_filled,
        unselectedIcon = R.drawable.account,
        destination = Destination.PROFILE
    )
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppListUI(
    rootNavController: NavHostController,
    navController: NavHostController,
    newsViewModel: NewsViewModel,
    aiViewModel: AiViewModel,
    viewModel: AppListUIViewModel
){
    val saveableStateHolder = rememberSaveableStateHolder()
    val rootNavBackStackEntry by rootNavController.currentBackStackEntryAsState()
    val rootCurrentDestination = rootNavBackStackEntry?.destination
    val bottomBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
    LaunchedEffect(rootCurrentDestination) {
        if (rootCurrentDestination?.hierarchy?.any { it.route == Destination.HOME::class.qualifiedName } == true){
            viewModel.updatePosition(0)
        }else if (rootCurrentDestination?.hierarchy?.any { it.route == Destination.EXPLORE::class.qualifiedName } == true){
            viewModel.updatePosition(1)
        }else if (rootCurrentDestination?.hierarchy?.any { it.route == Destination.BITDIGEST::class.qualifiedName } == true){
            viewModel.updatePosition(2)
        }else if (rootCurrentDestination?.hierarchy?.any { it.route == Destination.BOOKMARKS::class.qualifiedName } == true){
            viewModel.updatePosition(3)
        }else if (rootCurrentDestination?.hierarchy?.any { it.route == Destination.PROFILE::class.qualifiedName } == true){
            viewModel.updatePosition(4)
        }
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceDim,
        bottomBar = {
            FlexibleBottomAppBar(
                scrollBehavior = bottomBarScrollBehavior,
                horizontalArrangement = Arrangement.SpaceAround,
                containerColor = Color.Transparent,
                content = {
                    val density = LocalDensity.current
                    val iconPositions = remember { mutableStateListOf<Dp>() }
                    val position by viewModel.position.collectAsState()
                    val animatedOffsetX by animateDpAsState(
                        targetValue = if (iconPositions.size > position) 4.dp + iconPositions[position] else 4.dp,
                        animationSpec = tween(
                            easing = ExpressiveEasing.EmphasizedDecelerate,
                            durationMillis = 200
                        ),
                        label = "CircleSlide"
                    )
                    HorizontalFloatingToolbar(
                        expanded = true,
                        expandedShadowElevation = 2.dp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Box{
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .offset(x = animatedOffsetX, y = 0.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = CircleShape
                                    )
                            )
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .height(56.dp)
                                    .background(
                                        color = Color.Transparent,
                                        shape = RoundedCornerShape(28.dp)
                                    ),
                            ) {
                                homeUiDestinations.forEachIndexed { index, item ->
                                    val selected = rootCurrentDestination?.hierarchy?.any { it.route == item.destination::class.qualifiedName } == true
                                    IconToggleButton(
                                        checked = selected,
                                        onCheckedChange = {
                                            rootNavController.navigate(item.destination) {
                                                popUpTo(rootNavController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        shapes = IconToggleButtonShapes(
                                            shape = CircleShape,
                                            pressedShape = CircleShape,
                                            checkedShape = CircleShape
                                        ),
                                        colors = IconToggleButtonColors(
                                            containerColor = Color.Transparent,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            disabledContainerColor = Color.Gray,
                                            disabledContentColor = Color.Gray,
                                            checkedContainerColor = Color.Transparent,
                                            checkedContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .onGloballyPositioned { coordinates ->
                                                val xPx = coordinates.positionInParent().x
                                                val xDp = with(density) { xPx.toDp() }

                                                if (iconPositions.size <= index) {
                                                    iconPositions.add(xDp)
                                                } else {
                                                    iconPositions[index] = xDp
                                                }
                                            },
                                        interactionSource = NoRippleInteractionSource
                                    ) {
                                        Icon(
                                            painterResource(
                                                if (selected) {
                                                    item.selectedIcon
                                                } else {
                                                    item.unselectedIcon
                                                }
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        },
    ) {
        NavHost(
            startDestination = Destination.HOME,
            navController = rootNavController
        ) {
            composable<Destination.HOME> {
                saveableStateHolder.SaveableStateProvider("explore") {
                    ForYouPage(
                        newsViewModel = newsViewModel,
                        navController = navController
                    )
                }
            }
            composable<Destination.EXPLORE> {
                saveableStateHolder.SaveableStateProvider("home") {
                    HomeListUi(
                        navHostController = navController,
                        newsViewModel = newsViewModel,
                        aiViewModel = aiViewModel,
                        bottomAppBarScrollBehavior = bottomBarScrollBehavior
                    )
                }
            }
            composable<Destination.BITDIGEST> {
                saveableStateHolder.SaveableStateProvider("bit_digest") {
                    BitDigestScreen()
                }
            }
            composable<Destination.BOOKMARKS> {
                saveableStateHolder.SaveableStateProvider("bookmark") {
                    BookMarksScreen(
                        bottomAppBarScrollBehavior = bottomBarScrollBehavior,
                        navController = navController
                    )
                }
            }
            composable<Destination.PROFILE> {
                saveableStateHolder.SaveableStateProvider("profile") {
                    ProfileScreen(navController = navController)
                }
            }
        }
    }
}