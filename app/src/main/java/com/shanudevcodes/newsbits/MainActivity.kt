package com.shanudevcodes.newsbits

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.ui.screens.home.HomeUI
import com.shanudevcodes.newsbits.ui.theme.NewsBitsTheme
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions
import com.shanudevcodes.newsbits.viewmodel.AiViewModel
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
        ExperimentalMaterial3AdaptiveApi::class
    )
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
            val rootNavController = rememberNavController()
            val newsViewModel: NewsViewModel = viewModel()
            val newsList = newsViewModel.allNewsPagingFlow.collectAsLazyPagingItems()
            val isNewsLoaded = newsViewModel.isNewsLoaded.collectAsState()
            val aiViewModel: AiViewModel = viewModel()
            val navController = rememberNavController()
            var keepOnScreenCondition by remember { mutableStateOf(true) }
//            LaunchedEffect(Unit) {
//                val isFirstLaunch = dataStore.firstLaunch.first()
//                delay(300)
//                if (!isOnline(applicationContext)) {
//                    if (!isFirstLaunch) {
//                        Toast.makeText(
//                            applicationContext,
//                            "You're offline. Showing old news.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        Toast.makeText(
//                            applicationContext,
//                            "You're offline",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//                dataStore.setFirstLaunch(false)
//            }
            LaunchedEffect(Unit) {
                val onBoardingComplete = dataStore.onboardingComplete.first()
                val isFirstLaunch = dataStore.firstLaunch.first()
                if (!onBoardingComplete){
                    startActivity(Intent(this@MainActivity, OnboardingActivity::class.java)
                        .putExtra("showGuestSignup",true)
                        .apply {
                            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        }
                    )
                    finish()
                    keepOnScreenCondition = false
                }else if (isFirstLaunch) {
                    startActivity(Intent(this@MainActivity, AuthenticationActivity::class.java)
                        .apply {
                            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        }
                    )
                    finish()
                    keepOnScreenCondition = false
                }else{
                    keepOnScreenCondition = false
                }
            }
            LaunchedEffect(Unit) {
                var refreshTime = 0
                while (!isNewsLoaded.value) {
                    if (isOnline(applicationContext)) {
                        newsViewModel.loadTopNews()
                        newsList.refresh()
                        delay(300)
                        newsViewModel.newsLoaded()
                        if (refreshTime > 0) {
                            Toast.makeText(
                                applicationContext,
                                "You're online now. Showing recent news.",
                                Toast.LENGTH_SHORT
                            ).show()
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
            splashScreen.setKeepOnScreenCondition { !themeLoaded && !isNewsLoaded.value || keepOnScreenCondition }
            NewsBitsTheme(
                themeOption = themeOption,
                dynamicColor = dynamicColor
            ) {
                HomeUI(
                    rootNavController = rootNavController,
                    isPortrait = isPortrait,
                    newsViewModel = newsViewModel,
                    aiViewModel = aiViewModel,
                    navController = navController,
                )
//                SignupScreen()
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