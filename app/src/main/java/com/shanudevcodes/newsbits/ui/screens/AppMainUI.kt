package com.shanudevcodes.newsbits.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shanudevcodes.newsbits.BuildConfig
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.data.Destination
import com.shanudevcodes.newsbits.data.NoRippleInteractionSource
import com.shanudevcodes.newsbits.data.items
import com.shanudevcodes.newsbits.ui.screens.bookmark.BookMarksScreen
import com.shanudevcodes.newsbits.ui.screens.home.HomeUI
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppMainUI(
    rootNavController: NavHostController,
    rootCurrentDestination: NavDestination?,
    homeNavController: NavHostController,
    dataStore: DataStoreManager,
    themeOption: ThemeOptions,
    dynamicColor: Boolean,
    newsViewModel: NewsViewModel,
    isPortrait: Boolean
){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (drawerState.isOpen) {
        BackHandler {
            scope.launch {
                drawerState.close()
            }
        }
    }


    Box {
//                    ModalWideNavigationRail(
//                        state = wideNavigationRailState,
//                        header = {
//                            Column {
//                                IconButton(
//                                    onClick = {
//                                        scope.launch {
//                                            if (wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded)
//                                                wideNavigationRailState.collapse()
//                                            else wideNavigationRailState.expand()
//                                        }
//                                    }
//                                ) {
//                                    Icon(
//                                        imageVector = if (wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded) Icons.AutoMirrored.Filled.MenuOpen else Icons.Filled.Menu,
//                                        contentDescription = "Menu",
//                                        modifier = Modifier
//                                            .padding(start = 8.dp),
//                                    )
//                                }
//                                Text(
//                                    "News Bits",
//                                    color = MaterialTheme.colorScheme.tertiary,
//                                    modifier = Modifier.padding(16.dp)
//                                )
//                                HorizontalDivider()
//                            }
//                        },
//                        colors = WideNavigationRailDefaults.colors(
//                            modalContainerColor = if (!dynamicColor) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainer,
//                        ),
//                        hideOnCollapse = true
//                    ) {
//                        Box(
//                            modifier = Modifier.fillMaxSize()
//                        ) {
//                            Column {
//                                items.forEachIndexed { index, item ->
//                                    AnimatedWideNavigationRailItem(
//                                        label = item,
//                                        selected = selectedItem == index,
//                                        onClick = { selectedItem = index },
//                                        icon = {
//                                            Icon(
//                                                if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
//                                                contentDescription = null
//                                            )
//                                        },
//                                    )
//                                }
//                                Spacer(modifier = Modifier.weight(1f))
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.Center
//                                ) {
//                                    IconButton(
//                                        onClick = {
//                                            scope.launch {
//                                                dataStore.setDynamicColor(!dynamicColor)
//                                            }
//                                        }
//                                    ) {
//                                        Icon(
//                                            imageVector = if (dynamicColor) Icons.Filled.Palette else Icons.Outlined.Palette,
//                                            contentDescription = "Color Scheme Change"
//                                        )
//                                    }
//                                    IconButton(
//                                        onClick = {
//                                            scope.launch {
//                                                dataStore.saveThemeOption(
//                                                    when (themeOption) {
//                                                        ThemeOptions.SYSTEM_DEFAULT -> ThemeOptions.LIGHT
//                                                        ThemeOptions.LIGHT -> ThemeOptions.DARK
//                                                        ThemeOptions.DARK -> ThemeOptions.SYSTEM_DEFAULT
//                                                    }
//                                                )
//                                            }
//                                        }
//                                    ) {
//                                        Icon(
//                                            imageVector = when (themeOption) {
//                                                ThemeOptions.SYSTEM_DEFAULT -> Icons.Default.Contrast
//                                                ThemeOptions.LIGHT -> Icons.Default.LightMode
//                                                ThemeOptions.DARK -> Icons.Default.DarkMode
//                                            },
//                                            contentDescription = "Theme Change"
//                                        )
//                                    }
//                                }
//                                Text(
//                                    text = "Version: ${BuildConfig.VERSION_NAME}",
//                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                )
//                                Text(
//                                    text = "Powered By NewsData.io",
//                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                )
//                                Spacer(
//                                    modifier = Modifier.height(
//                                        WindowInsets.navigationBars.asPaddingValues()
//                                            .calculateBottomPadding() * 2
//                                    )
//                                )
//                            }
//                        }
//                    }
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column {
                            Text(
                                "News Bits",
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(16.dp)
                            )
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            items.forEachIndexed { index, item ->
                                val isSelected = rootCurrentDestination?.hierarchy?.any { it.route == item.destination::class.qualifiedName } == true
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        ) // Provide space for scale animation
                                        .height(56.dp)
                                ) {
                                    val scale by animateFloatAsState(
                                        targetValue = if (isSelected) 1f else 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        ),
                                        label = "scale"
                                    )

                                    // Animated background
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .scale(scaleX = scale, scaleY = 1f)
                                            .alpha(scale)
                                            .background(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = RoundedCornerShape(48.dp)
                                            )
                                    )

                                    // Navigation item
                                    NavigationDrawerItem(
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.title
                                            )
                                        },
                                        label = { Text(text = item.title) },
                                        selected = isSelected,
                                        onClick = {
                                            scope.launch {
                                                rootNavController.navigate(item.destination) {
                                                    popUpTo(rootNavController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                                delay(300)
                                                drawerState.close()
                                            }
                                        },
                                        colors = NavigationDrawerItemDefaults.colors(
                                            selectedContainerColor = Color.Transparent,
                                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        ),
                                        interactionSource = NoRippleInteractionSource,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                            Spacer(modifier = Modifier.height(8.dp))

                            Spacer(modifier = Modifier.height(8.dp))

                            var isDropDownEnabled by remember { mutableStateOf(false) }
                            val regions = listOf(
                                "Global",
                                "India",
                                "USA",
                                "Canada",
                                "Germany",
                                "Japan"
                            )
                            var selectedRegion by remember { mutableStateOf(regions.first()) }
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                        .clip(shape = RoundedCornerShape(48.dp))
                                        .clickable {
                                            isDropDownEnabled = !isDropDownEnabled
                                        }, // toggles dropdown
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically

                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                        ) {
                                            Text(
                                                text = "Region",
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                            Text(
                                                text = selectedRegion,
                                                style = MaterialTheme.typography.bodySmallEmphasized
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                isDropDownEnabled = !isDropDownEnabled
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Change Region"
                                            )
                                        }
                                    }
                                }
                                DropdownMenu(
                                    shape = RoundedCornerShape(24.dp),
                                    expanded = isDropDownEnabled,
                                    onDismissRequest = {
                                        isDropDownEnabled = false
                                    },
                                    modifier = Modifier
                                        .width(200.dp)
                                        .align(Alignment.Center),
                                ) {
                                    regions.forEach { region ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = region,
                                                    style = MaterialTheme.typography.bodySmallEmphasized,
                                                    modifier = Modifier.padding(start = 16.dp),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            },
                                            onClick = {
                                                selectedRegion = region
                                                isDropDownEnabled = false
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }

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
                                            ThemeOptions.DARK -> Icons.Default.DarkMode
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
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                NavHost(
                    navController = rootNavController,
                    startDestination = Destination.HOME,
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    },
                    popEnterTransition = {
                        fadeIn()
                    },
                    popExitTransition = {
                        fadeOut()
                    }
                ) {
                    composable<Destination.HOME> {
                        HomeUI(
                            isPortrait = isPortrait,
                            navController = homeNavController,
                            drawerState = drawerState,
                            newsViewModel = newsViewModel
                        )
                    }
                    composable<Destination.BOOKMARKS> {
                        BookMarksScreen(
                            openNavDraw = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }
                }
            }
        }
//      }
    }
}

//@OptIn(ExperimentalMaterial3ExpressiveApi::class)
//@Composable
//fun AnimatedWideNavigationRailItem(
//    label: String,
//    selected: Boolean,
//    onClick: () -> Unit,
//    icon: @Composable () -> Unit,
//    selectedColor: Color = MaterialTheme.colorScheme.secondaryContainer,
//    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
//    unselectedColor: Color = MaterialTheme.colorScheme.onSurface,
//) {
//    val indicatorWidth by animateFloatAsState(
//        targetValue = if (selected) 1f else 0f, // adjust to your rail width
//        animationSpec = spring(
//            dampingRatio = Spring.DampingRatioLowBouncy,
//            stiffness = Spring.StiffnessMedium,
//        ),
//        label = "indicatorWidth"
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(56.dp)
//            .padding(horizontal = 8.dp)
//    ) {
//        // Center-expanding background
//        Box(
//            modifier = Modifier
//                .align(Alignment.Center)
//                .height(54.dp)
//                .fillMaxWidth()
//                .scale(scaleX = indicatorWidth, scaleY = 1f)
//                .alpha(indicatorWidth)
//                .background(color = selectedColor, shape = RoundedCornerShape(56.dp))
//        )
//
//        WideNavigationRailItem(
//            selected = selected,
//            onClick = onClick,
//            railExpanded = true,
//            icon = icon,
//            label = { Text(text = label) },
//            colors = WideNavigationRailItemDefaults.colors(
//                selectedIndicatorColor = Color.Transparent,
//                selectedIconColor = contentColor,
//                selectedTextColor = contentColor,
//                unselectedIconColor = unselectedColor,
//                unselectedTextColor = unselectedColor,
//            ),
//            interactionSource = NoRippleInteractionSource
//        )
//    }
//}