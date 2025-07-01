package com.shanudevcodes.newsbits.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainUi(navController: NavHostController, openNavDraw:() -> Unit,newsViewModel: NewsViewModel ) {

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier
                    .fillMaxWidth(),
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = { Text("Search News Bits...") },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    searchBarState.animateToCollapsed()
                                }
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
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
                },
            )
        }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            val notificationCount = 11 // Replace with actual notification count
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { openNavDraw() },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.size(48.dp),
                            )
                        }
                    },
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NEWS BITS",
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                contentAlignment = Alignment.BottomEnd,
                            ) {
                                IconButton(
                                    onClick = { },
                                    modifier = Modifier.offset(x = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                if (notificationCount > 0) {
                                    val notificationCountString by remember {
                                        mutableStateOf(
                                            if (notificationCount > 9) {
                                                "9+"
                                            } else {
                                                "$notificationCount"
                                            }
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .offset(x = 1.dp, y = -5.dp)
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
                    },
                    modifier = Modifier.padding(0.dp)
                )
                Box {
                    TopSearchBar(
                        scrollBehavior = scrollBehavior,
                        state = searchBarState,
                        inputField = inputField,
                        windowInsets = WindowInsets(0),
                    )
                    ExpandedFullScreenSearchBar(
                        tonalElevation = 48.dp,
                        state = searchBarState,
                        inputField = inputField,
                        modifier = Modifier
                            .then(
                                if (!isPortrait) {
                                    Modifier
                                        .widthIn(max = 490.dp)
                                        .heightIn(max = 450.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                } else {
                                    Modifier // no additional constraints
                                },
                            )
                    ) {
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 12.dp, end = 12.dp)
        ) {
            // Screen content goes here
            HomeScreen(navController, scrollBehavior, newsViewModel)
        }
    }
}