package com.shanudevcodes.newsbits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.ui.screens.AiSummaryScreen
import com.shanudevcodes.newsbits.ui.theme.NewsBitsTheme
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions
import com.shanudevcodes.newsbits.viewmodel.AiViewModel
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel

class AiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dataStore = DataStoreManager(applicationContext)
            val themeOption by dataStore.themeFlow.collectAsState(initial = ThemeOptions.SYSTEM_DEFAULT)
            val dynamicColor by dataStore.dynamicColorFlow.collectAsState(initial = false)
            val aiViewModel: AiViewModel = viewModel()
            val newsViewModel: NewsViewModel = viewModel()
            val topNews by newsViewModel.topNews.collectAsState()
            newsViewModel.loadTopNews()
            aiViewModel.getGeminiResponse(topNews)
            val summary by aiViewModel.geminiResponse.collectAsState()
            val isFetched by aiViewModel.isResponseFetched.collectAsState()
            NewsBitsTheme(
                themeOption = themeOption,
                dynamicColor = dynamicColor
            ){
                AiSummaryScreen(summary,isFetched, themeOption == ThemeOptions.DARK)
            }
        }
    }
}