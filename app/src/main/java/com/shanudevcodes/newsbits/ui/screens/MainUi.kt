package com.shanudevcodes.newsbits.ui.screens

import android.R
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUi(navController: NavHostController) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            val notificationCount = 11 // Replace with actual notification count
            var query by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }
            Box(
                Modifier.padding(WindowInsets.statusBars.asPaddingValues()),
            ) {
                if (!active) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { /* TODO: handle menu click */ }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.size(48.dp),
                            )
                        }

                        Text(
                            text = "NEWS BITS",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            IconButton(onClick = { /* TODO: handle notifications */ }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            if (notificationCount > 0) {
                                val notificationCountString by remember { mutableStateOf (
                                    if (notificationCount>9){
                                        "9+"
                                    }else{
                                        "$notificationCount"
                                    }
                                ) }
                                Box(
                                    modifier = Modifier
                                        .offset(x = -5.dp, y = -5.dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.dp, Color.Black, CircleShape),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Text(
                                        text = notificationCountString,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.offset(y = -3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                var shouldExpand by remember { mutableStateOf(false) }
                var isAnimatingPadding by remember { mutableStateOf(false) }
                val topInset = 40.dp

                val animatedTopInset by animateDpAsState(
                    targetValue = if (shouldExpand) 0.dp else topInset,
                    label = "AnimatedTopInset"
                )
                LaunchedEffect(shouldExpand) {
                    if (shouldExpand && !active) {
                        // Shrink padding first
                        isAnimatingPadding = true
                        delay(200)
                        active = true
                    } else if (!shouldExpand && active) {
                        // Collapse search view first
                        active = false
                        delay(320)
                        isAnimatingPadding = false
                    }
                }
                SearchBarDefaults.inputFieldColors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.DarkGray,
                )
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = animatedTopInset),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SearchBar(
                            modifier = Modifier,
                            colors = SearchBarDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            ),
                            inputField = {
                                SearchBarDefaults.InputField(
                                    query = query,
                                    onQueryChange = { query = it },
                                    onSearch = {
                                        shouldExpand = false
                                    },
                                    expanded = active,
                                    onExpandedChange = { shouldExpand = it },
                                    placeholder = { Text("Search") },
                                    leadingIcon = {
                                        IconButton(
                                            onClick = {
                                                shouldExpand = !shouldExpand
                                            }
                                        ) {
                                            Icon(
                                                if (!active) Icons.Default.Search else Icons.Default.Close,
                                                contentDescription = if (!active) {
                                                    "Search"
                                                } else {
                                                    "Close"
                                                }
                                            )
                                        }
                                    },
                                    trailingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary)
                                                .clickable(onClick = { /* your action */ }), // Acts like a button
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = "Search",
                                                tint = MaterialTheme.colorScheme.surfaceContainerLow,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                )
                            },
                            expanded = shouldExpand,
                            onExpandedChange = { shouldExpand = it },
                            windowInsets = WindowInsets(0)
                        ) {
                            // Display search results in a scrollable column
                            Column(modifier = Modifier.fillMaxSize()) {

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .padding(start = 16.dp, end = 16.dp)
        ) {
            // Screen content goes here
            HomeScreen(navController)
        }
    }
}
