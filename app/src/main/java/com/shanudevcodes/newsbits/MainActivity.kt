package com.shanudevcodes.newsbits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.ui.screens.MainUi
import com.shanudevcodes.newsbits.ui.theme.NewsBitsTheme
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dataStore = DataStoreManager(applicationContext)
            val themeOption by dataStore.themeFlow.collectAsState(initial = ThemeOptions.SYSTEM_DEFAULT)
            val navController = rememberNavController()
            NewsBitsTheme(
                themeOption = themeOption,
                dynamicColor = false
            ) {
                MainUi(navController)
            }
        }
    }
}