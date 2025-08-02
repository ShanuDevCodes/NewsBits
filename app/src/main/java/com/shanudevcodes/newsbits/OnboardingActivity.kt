package com.shanudevcodes.newsbits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.ui.screens.onboarding.OnBoardingScreen
import com.shanudevcodes.newsbits.ui.theme.NewsBitsTheme
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dataStore = DataStoreManager(applicationContext)
            val themeOption by dataStore.themeFlow.collectAsState(initial = ThemeOptions.SYSTEM_DEFAULT)
            val dynamicColor by dataStore.dynamicColorFlow.collectAsState(initial = false)
            NewsBitsTheme(
                themeOption = themeOption,
                dynamicColor = dynamicColor
            ) {
                OnBoardingScreen(dataStore)
            }
        }
    }
}