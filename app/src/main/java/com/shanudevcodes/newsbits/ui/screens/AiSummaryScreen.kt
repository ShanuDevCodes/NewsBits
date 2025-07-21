package com.shanudevcodes.newsbits.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.ui.animation.LottieLoader

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AiSummaryScreen(
    summary: List<String>,
    isFetched: Boolean = false,
    isDark: Boolean
){
    val context = LocalContext.current
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            (context as? Activity)?.finish()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.sparkler),
                            contentDescription = "AI",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Top News Summary",
                            style = MaterialTheme.typography.titleSmallEmphasized
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
        ){
            if (isFetched) {
                LazyColumn {
                    items(summary) {
                        Text(
                            text = "- " + it,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    item{
                        Spacer(modifier = Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 8.dp))
                    }
                }
            }
        }
    }
    if (!isFetched){
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LottieLoader(isDark)
        }
    }
}
