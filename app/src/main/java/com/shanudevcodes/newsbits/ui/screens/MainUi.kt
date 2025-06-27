package com.shanudevcodes.newsbits.ui.screens

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUi(navController: NavHostController) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            val notificationCount = 5 // Replace with actual notification count
            var query by androidx.compose.runtime.remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }
            Column(
                Modifier.padding(WindowInsets.statusBars.asPaddingValues())
            ) {
                if (!active) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { /* TODO: handle menu click */ }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.size(48.dp)
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
                                        text = notificationCount.toString(),
                                        color = Color.Red,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.offset(y = -3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (!active){
                Spacer(modifier = Modifier.height(8.dp))
                }

                SearchBarDefaults.inputFieldColors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.DarkGray,
                )
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = query,
                            onQueryChange = { query = it },
                            onSearch = { active = false },
                            expanded = active,
                            onExpandedChange = { active = it },
                            placeholder = { Text("Search news...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search")
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
//                                IconButton(
//                                    onClick = {}
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
//                                        contentDescription = "Search",
//                                        tint = MaterialTheme.colorScheme.primary,
//                                    )
//                                }
                            }
                        )
                    },
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    expanded = active,
                    onExpandedChange = { active = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = if (!active)16.dp else 0.dp, end = if (!active)16.dp else 0.dp), // ✅ reduce or remove top padding
                    tonalElevation = 2.dp,
                    content = {
                        Text("Trending News")
                        Text("Sports Highlights")
                    },
                    windowInsets = WindowInsets(0)
                )
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
