package com.shanudevcodes.newsbits

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
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
import com.shanudevcodes.newsbits.ui.animation.ExpressiveEasing
import com.shanudevcodes.newsbits.ui.screens.EmptyScreen
import com.shanudevcodes.newsbits.ui.screens.MainUi
import com.shanudevcodes.newsbits.ui.screens.NewsDetailScreen
import com.shanudevcodes.newsbits.ui.theme.NewsBitsTheme
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
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
            val items = listOf(
                "Home",
                "Bookmarked",
            )
            val selectedIcons =
                listOf(
                    Icons.Filled.Home,
                    Icons.Filled.Bookmark,
                )
            val unselectedIcons =
                listOf(
                    Icons.Outlined.Home,
                    Icons.Outlined.BookmarkBorder,
                )
            val configuration = LocalConfiguration.current
            val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            val dataStore = DataStoreManager(applicationContext)
            val themeOption by dataStore.themeFlow.collectAsState(initial = ThemeOptions.SYSTEM_DEFAULT)
            val dynamicColor by dataStore.dynamicColorFlow.collectAsState(initial = false)
            var themeLoaded by remember { mutableStateOf(false) }
            val navController = rememberNavController()
//            val wideNavigationRailState = rememberWideNavigationRailState(initialValue = WideNavigationRailValue.Collapsed)
            val scope = rememberCoroutineScope()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            var selectedItem by remember { mutableIntStateOf(0) }
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
                        delay(300)
                        newsViewModel.newsLoaded()
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
            if (drawerState.isOpen){
                BackHandler {
                    scope.launch {
                        drawerState.close()
                    }
                }
            }
            splashScreen.setKeepOnScreenCondition { !themeLoaded && !isNewsLoaded.value}
            NewsBitsTheme(
                themeOption = themeOption,
                dynamicColor = dynamicColor
            ) {
//                Box {
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
                                        HorizontalDivider()
                                        Spacer(modifier = Modifier.height(8.dp))
                                        items.forEachIndexed { index, item ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 8.dp, vertical = 4.dp) // Provide space for scale animation
                                                    .height(56.dp)
                                            ) {
                                                val scale by animateFloatAsState(
                                                    targetValue = if (selectedItem == index) 1f else 0f,
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
                                                            imageVector = if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                                                            contentDescription = item
                                                        )
                                                    },
                                                    label = { Text(text = item) },
                                                    selected = selectedItem == index,
                                                    onClick = { selectedItem = index },
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
                        Box {
                            Row {
                                val animatedWeight = remember { Animatable(0f) }
                                LaunchedEffect(isPortrait) {
                                    if (!isPortrait) {
                                        delay(100)
                                        animatedWeight.animateTo(
                                            targetValue = 0.35f,
                                            animationSpec = tween(
                                                durationMillis = 600,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                    } else {
                                        animatedWeight.snapTo(0f)
                                    }
                                }
                                if (animatedWeight.value > 0f) {
                                    Box(modifier = Modifier.weight(animatedWeight.value)) {
                                        MainUi(
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
                                        startDestination = Destination.HOMESCREEN,
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
                                                targetOffsetX = { -350 },
                                                animationSpec = tween(
                                                    durationMillis = 600,
                                                    easing = ExpressiveEasing.Emphasized
                                                )
                                            )
                                        },
                                        popEnterTransition = {
                                            slideInHorizontally(
                                                initialOffsetX = { -350 },
                                                animationSpec = tween(
                                                    durationMillis = 300,
                                                    easing = ExpressiveEasing.EmphasizedDecelerate
                                                )
                                            )
                                        },
                                        popExitTransition = {
                                            slideOutOfContainer(
                                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                                animationSpec = tween(
                                                    durationMillis = 300,
                                                    easing = ExpressiveEasing.EmphasizedDecelerate
                                                )
                                            )
                                        }
                                    ) {
                                        composable<Destination.HOMESCREEN> {
                                            if (isPortrait) {
                                                MainUi(navController, openNavDraw = {
                                                    scope.launch {
                                                        drawerState.open()
 //                                                       wideNavigationRailState.expand()
                                                    }
                                                }, newsViewModel)
                                            } else {
                                                EmptyScreen()
                                            }
                                        }
                                        composable<Destination.NEWSDETAILSCREEN> {
                                            NewsDetailScreen(
                                                it.arguments?.getInt("newsId") ?: 1,
                                                navController,
                                                newsViewModel,
                                                it.arguments?.getString("news")
                                                    ?: News.NEWS_ALL.name
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
//                }
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
private object NoRippleInteractionSource : MutableInteractionSource {

    override val interactions: Flow<Interaction> = emptyFlow()

    override suspend fun emit(interaction: Interaction) {}

    override fun tryEmit(interaction: Interaction) = true
}