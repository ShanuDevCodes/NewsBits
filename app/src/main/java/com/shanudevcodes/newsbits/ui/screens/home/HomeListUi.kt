package com.shanudevcodes.newsbits.ui.screens.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExpandedDockedSearchBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shanudevcodes.newsbits.data.HomeDestination
import com.shanudevcodes.newsbits.data.SearchDestination
import com.shanudevcodes.newsbits.data.savedarticledb.data.mapper.toEntity
import com.shanudevcodes.newsbits.data.savedarticledb.data.roomdatabase.AppDatabase
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.events.RoomEvents
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal.RoomViewModel
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal.RoomViewModelFactory
import com.shanudevcodes.newsbits.viewmodel.AiViewModel
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@SuppressLint("ConfigurationScreenWidthHeight", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class, FlowPreview::class
)
@Composable
fun HomeListUi(
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    searchNavController: NavHostController = rememberNavController(),
    navHostController: NavHostController,
    newsViewModel: NewsViewModel,
    aiViewModel: AiViewModel
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val dao = db.RoomDao()
    val roomViewModel: RoomViewModel = viewModel(
        factory = RoomViewModelFactory(dao)
    )
    val roomState by roomViewModel.state.collectAsState()
    val history = roomState.historyList
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()
//    val searchResults by newsViewModel.searchResults.collectAsState()
    val searchSuggestion by newsViewModel.searchSuggestions.collectAsState()
    val screenWidthDp = configuration.screenWidthDp.dp
    val currentBackStackEntry by navHostController.currentBackStackEntryAsState()
    val searchBackStackEntry by searchNavController.currentBackStackEntryAsState()
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                colors = SearchBarDefaults.inputFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                modifier = Modifier
                    .width(width = when(isPortrait) {
                        false -> screenWidthDp * 0.337f
                        true -> screenWidthDp
                    }),
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = {
                    scope.launch {
                        roomViewModel.onEvent(RoomEvents.UpsertHistory)
                        searchBarState.animateToCollapsed()
                        if (currentBackStackEntry?.destination?.hierarchy?.any {it.route == HomeDestination.HOMESCREEN::class.qualifiedName } == false) {
                            navHostController.popBackStack()
                        }
                    }
                    searchNavController.navigate(
                        SearchDestination.SEARCHRESULTSCREEN(
                            query = textFieldState.text.toString()
                        )
                    ){
                        popUpTo(searchNavController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
                placeholder = { Text(
                    text = "Search News Bits...",
                    style = MaterialTheme.typography.titleMediumEmphasized
                ) },
                leadingIcon = {
                    if (searchBackStackEntry?.destination?.hierarchy?.any{it.route == SearchDestination.HOMESEARCHSCREEN::class.qualifiedName} == true) {
                        if (searchBarState.currentValue == SearchBarValue.Expanded) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        textFieldState.clearText()
                                        newsViewModel.resetSearchResults()
                                        searchBarState.animateToCollapsed()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        searchBarState.animateToExpanded()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null)
                            }
                        }
                    }else{
                        IconButton(
                            onClick = {
                                scope.launch {
                                    searchNavController.popBackStack()
                                    if (currentBackStackEntry?.destination?.hierarchy?.any { it.route == HomeDestination.HOMESCREEN::class.qualifiedName } == false) {
                                        navHostController.popBackStack()
                                    }
                                    textFieldState.clearText()
                                    searchBarState.animateToCollapsed()
                                }
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(onClick = {
                                scope.launch {
                                    roomViewModel.onEvent(RoomEvents.UpsertHistory)
                                    searchBarState.animateToCollapsed()
                                    if (currentBackStackEntry?.destination?.hierarchy?.any { it.route == HomeDestination.HOMESCREEN::class.qualifiedName } == false) {
                                        navHostController.popBackStack()
                                    }
                                }
                                searchNavController.navigate(
                                    SearchDestination.SEARCHRESULTSCREEN(
                                        query = textFieldState.text.toString()
                                    )
                                ) {
                                    popUpTo(searchNavController.graph.findStartDestination().id)
                                    launchSingleTop = true
                                }
                            }), // Acts like a button
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
    LaunchedEffect(textFieldState) {
        launch {
            snapshotFlow { textFieldState.text.toString() }
                .debounce(300)
                .collect { newText ->
                    if (newText.isNotBlank()) {
                        newsViewModel.searchSuggestionInAlgolia(newText)
                    } else {
                        newsViewModel.resetSearchSuggestions()
                    }
                }
        }
        launch {
            snapshotFlow { textFieldState.text.toString() }
                .collect { newText ->
                    roomViewModel.onEvent(RoomEvents.SetHistoryQuery(newText))
                }
        }
    }
    LaunchedEffect(searchBackStackEntry?.destination?.hierarchy) {
        if (searchBackStackEntry?.destination?.hierarchy?.any { it.route == SearchDestination.HOMESEARCHSCREEN::class.qualifiedName } == true) {
            Log.d( "BackStack",  "Home Search Screen")
            //textFieldState.clearText()
            delay(600)
            newsViewModel.resetSearchResults()
            newsViewModel.resetSearchResultsLoaded()
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = MaterialTheme.colorScheme.surfaceDim,
        topBar = {
            Column{
                Box (
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceDim)
                ){
                    Row(
                        modifier = Modifier
                            .padding(
                                top = WindowInsets.statusBars.asPaddingValues()
                                    .calculateTopPadding(),
                                start = 12.dp,
                                end = 12.dp,
                                bottom = 4.dp
                            )
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Explore",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.displayMediumEmphasized
                        )
                    }
                }
                Box {
                    TopSearchBar(
                        shadowElevation = 2.dp,
                        scrollBehavior = scrollBehavior,
                        state = searchBarState,
                        inputField = inputField,
                        windowInsets = WindowInsets(0),
                    )
                    if (!isPortrait) {
                        ExpandedDockedSearchBar(
                            tonalElevation = 48.dp,
                            state = searchBarState,
                            inputField = inputField,
                            colors = SearchBarDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            LaunchedEffect(Unit) {
                                roomViewModel.onEvent(RoomEvents.GetHistory)
                            }
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp)
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .padding(top = 8.dp, bottom = 8.dp)
                                ) {
                                    LazyColumn {
                                        if (textFieldState.text.toString().isNotEmpty()) {
                                            itemsIndexed(searchSuggestion) {index, it->
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                                    modifier = Modifier
                                                        .height(48.dp)
                                                        .clickable(onClick = {
                                                            textFieldState.edit {
                                                                replace(0, length, it.query)
                                                            }
                                                            scope.launch {
                                                                roomViewModel.onEvent(
                                                                    RoomEvents.SaveHistory(
                                                                        it.query
                                                                    )
                                                                )
                                                                searchBarState.animateToCollapsed()
                                                                if (currentBackStackEntry?.destination?.hierarchy?.any { it.route == HomeDestination.HOMESCREEN::class.qualifiedName } == false) {
                                                                    navHostController.popBackStack()
                                                                }
                                                            }
                                                            searchNavController.navigate(
                                                                SearchDestination.SEARCHRESULTSCREEN(
                                                                    query = it.query
                                                                )
                                                            ) {
                                                                popUpTo(searchNavController.graph.findStartDestination().id)
                                                                launchSingleTop = true
                                                            }
                                                        })
                                                        .padding(start = 8.dp, end = 8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Search,
                                                        contentDescription = "Search"
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = it.query,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                        contentDescription = "Insert",
                                                    )
                                                }
                                                if (index != searchSuggestion.lastIndex){
                                                    HorizontalDivider(
                                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                        thickness = 2.dp
                                                    )
                                                }
                                            }
                                        } else {
                                            itemsIndexed(history) {index,it->
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                                    modifier = Modifier
                                                        .height(48.dp)
                                                        .clickable(onClick = {
                                                            textFieldState.edit {
                                                                replace(0, length, it.query)
                                                            }
                                                            scope.launch {
                                                                searchBarState.animateToCollapsed()
                                                                if (currentBackStackEntry?.destination?.hierarchy?.any { it.route == HomeDestination.HOMESCREEN::class.qualifiedName } == false) {
                                                                    navHostController.popBackStack()
                                                                }
                                                            }
                                                            searchNavController.navigate(
                                                                SearchDestination.SEARCHRESULTSCREEN(
                                                                    query = it.query
                                                                )
                                                            ) {
                                                                popUpTo(searchNavController.graph.findStartDestination().id)
                                                                launchSingleTop = true
                                                            }
                                                        })
                                                        .padding(start = 8.dp, end = 4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.History,
                                                        contentDescription = "History"
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = it.query,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    IconButton(
                                                        onClick = {
                                                            scope.launch {
                                                                roomViewModel.onEvent(
                                                                    RoomEvents.DeleteHistory(
                                                                        it.toEntity()
                                                                    )
                                                                )
                                                                roomViewModel.onEvent(RoomEvents.GetHistory)
                                                            }
                                                        },
                                                        modifier =
                                                            Modifier.size(
                                                                IconButtonDefaults.largeIconSize
                                                            )
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = "Delete",
                                                        )
                                                    }
                                                }
                                                if (index != history.lastIndex){
                                                    HorizontalDivider(
                                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                        thickness = 2.dp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }else {
                        ExpandedFullScreenSearchBar(
                            tonalElevation = 48.dp,
                            state = searchBarState,
                            inputField = inputField,
                            colors = SearchBarDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            windowInsets = { WindowInsets.statusBars },
                        ) {
                            LaunchedEffect(Unit) {
                                roomViewModel.onEvent(RoomEvents.GetHistory)
                            }
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp)
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .padding(top = 8.dp, bottom = if (WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() > 0.dp ) WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() else 8.dp)
                                ) {
                                    LazyColumn {
                                        if (textFieldState.text.toString().isNotEmpty()) {
                                            itemsIndexed(searchSuggestion) {index, it->
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                                    modifier = Modifier
                                                        .height(48.dp)
                                                        .clickable(onClick = {
                                                            textFieldState.edit {
                                                                replace(0, length, it.query)
                                                            }
                                                            scope.launch {
                                                                roomViewModel.onEvent(
                                                                    RoomEvents.SaveHistory(
                                                                        it.query
                                                                    )
                                                                )
                                                                searchBarState.animateToCollapsed()
                                                                if (currentBackStackEntry?.destination?.hierarchy?.any { it.route == HomeDestination.HOMESCREEN::class.qualifiedName } == false) {
                                                                    navHostController.popBackStack()
                                                                }
                                                            }
                                                            searchNavController.navigate(
                                                                SearchDestination.SEARCHRESULTSCREEN(
                                                                    query = it.query
                                                                )
                                                            ) {
                                                                popUpTo(searchNavController.graph.findStartDestination().id)
                                                                launchSingleTop = true
                                                            }
                                                        })
                                                        .padding(start = 8.dp, end = 8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Search,
                                                        contentDescription = "Search"
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = it.query,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                        contentDescription = "Insert",
                                                    )
                                                }
                                                if (index != searchSuggestion.lastIndex){
                                                    HorizontalDivider(
                                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                        thickness = 2.dp
                                                    )
                                                }
                                            }
                                        } else {
                                            itemsIndexed(history) {index, it->
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                                    modifier = Modifier
                                                        .height(48.dp)
                                                        .clickable(onClick = {
                                                            textFieldState.edit {
                                                                replace(0, length, it.query)
                                                            }
                                                            scope.launch {
                                                                searchBarState.animateToCollapsed()
                                                                if (currentBackStackEntry?.destination?.hierarchy?.any { it.route == HomeDestination.HOMESCREEN::class.qualifiedName } == false) {
                                                                    navHostController.popBackStack()
                                                                }
                                                            }
                                                            searchNavController.navigate(
                                                                SearchDestination.SEARCHRESULTSCREEN(
                                                                    query = it.query
                                                                )
                                                            ) {
                                                                popUpTo(searchNavController.graph.findStartDestination().id)
                                                                launchSingleTop = true
                                                            }
                                                        })
                                                        .padding(start = 8.dp, end = 4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.History,
                                                        contentDescription = "History"
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = it.query,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    IconButton(
                                                        onClick = {
                                                            scope.launch {
                                                                roomViewModel.onEvent(
                                                                    RoomEvents.DeleteHistory(
                                                                        it.toEntity()
                                                                    )
                                                                )
                                                                roomViewModel.onEvent(RoomEvents.GetHistory)
                                                            }
                                                        },
                                                        modifier =
                                                            Modifier.size(
                                                                IconButtonDefaults.largeIconSize
                                                            )
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = "Delete",
                                                        )
                                                    }
                                                }
                                                if (index != history.lastIndex){
                                                    HorizontalDivider(
                                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                        thickness = 2.dp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Box{
            NavHost(
                startDestination = SearchDestination.HOMESEARCHSCREEN,
                navController = searchNavController,
            ){
                composable<SearchDestination.HOMESEARCHSCREEN> {
                    Column {
                        Box(
                            modifier = Modifier
                        ) {
                            HomeScreen(
                                navController = navHostController,
                                scrollBehavior = scrollBehavior,
                                viewModel = newsViewModel,
                                aiViewModel = aiViewModel,
                                bottomAppBarScrollBehavior = bottomAppBarScrollBehavior
                            )
                        }
                    }
                }
                composable<SearchDestination.SEARCHRESULTSCREEN> {
                    SearchResultScreen(
                        navController = navHostController,
                        newsViewModel = newsViewModel,
                        query = it.arguments?.getString("query") ?: "",
                        scrollBehavior = scrollBehavior,
                        bottomAppBarScrollBehavior = bottomAppBarScrollBehavior
                    )
                }
            }
        }
    }
}