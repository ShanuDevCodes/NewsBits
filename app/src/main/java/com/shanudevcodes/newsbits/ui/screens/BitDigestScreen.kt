package com.shanudevcodes.newsbits.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shanudevcodes.newsbits.ui.animation.ComingSoonAnimation

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BitDigestScreen(){
    Surface(
        color = MaterialTheme.colorScheme.surfaceDim
    ) {
        if (false) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.surfaceDim,
                contentWindowInsets = WindowInsets(0),
                topBar = {
                    Row(
                        modifier = Modifier
                            .padding(
                                top = WindowInsets.statusBars.asPaddingValues()
                                    .calculateTopPadding(),
                                start = 12.dp,
                                end = 12.dp
                            )
                            .fillMaxWidth()
                            .height(40.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bit Digest",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {

                }
            }
        }else {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.surfaceDim
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(
                            bottom = 16.dp + WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding(),
                            start = 12.dp, end = 12.dp
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = "Coming\nSoon,",
                            style = MaterialTheme.typography.displayLargeEmphasized,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bit Digest — AI Curated Daily & Weekly Updates",
                            style = MaterialTheme.typography.bodyLargeEmphasized,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(),
                            contentAlignment = Alignment.Center
                        ) {
                            ComingSoonAnimation(true)
                        }
                        Spacer(
                            modifier = Modifier.height(
                                16.dp + WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding()
                            )
                        )
                    }
//                ExtendedFloatingActionButton(
//                    onClick = {  },
//                    icon = {
//                        Icon(
//                            imageVector = Icons.Outlined.Notifications,
//                            contentDescription = "Notify me"
//                        )
//                    },
//                    text = {
//                        Text(
//                            text = "Notify me",
//                        )
//                    },
//                    modifier = Modifier.align(alignment = Alignment.BottomEnd)
//                )
                }
            }
        }
    }
}