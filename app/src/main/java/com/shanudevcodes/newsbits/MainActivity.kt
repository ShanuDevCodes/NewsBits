package com.shanudevcodes.newsbits

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.compose.rememberNavController
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.ui.screens.EmptyScreen
import com.shanudevcodes.newsbits.ui.screens.MainUi
import com.shanudevcodes.newsbits.ui.theme.NewsBitsTheme
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val configuration = LocalConfiguration.current
            val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            val dataStore = DataStoreManager(applicationContext)
            val themeOption by dataStore.themeFlow.collectAsState(initial = ThemeOptions.SYSTEM_DEFAULT)
            val navController = rememberNavController()
            NewsBitsTheme(
                themeOption = themeOption,
                dynamicColor = false
            ) {
                if (!isPortrait) {
                    Box() {
                        Row {
                            Box(modifier = Modifier.weight(0.35f)) {
                                MainUi(navController)
                            }
                            Box(
                                modifier = Modifier.weight(0.65f)
                            ) {
                                EmptyScreen()
                            }
                        }
                    }
                }else{
                    MainUi(navController)
                }
            }
        }
    }
}