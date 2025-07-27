package com.shanudevcodes.newsbits.ui.screens.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExpandedDockedSearchBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.HomeDestination
import com.shanudevcodes.newsbits.data.NoRippleInteractionSource
import com.shanudevcodes.newsbits.data.SearchDestination
import com.shanudevcodes.newsbits.data.savedarticledb.data.mapper.toEntity
import com.shanudevcodes.newsbits.data.savedarticledb.data.roomdatabase.AppDatabase
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.events.RoomEvents
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal.RoomViewModel
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal.RoomViewModelFactory
import com.shanudevcodes.newsbits.ui.animation.ExpressiveEasing
import com.shanudevcodes.newsbits.viewmodel.AiViewModel
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

data class HomeUiDestination(
    val name: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
)

val homeUiDestinations = listOf(
    HomeUiDestination(
        name = "For You",
        selectedIcon = R.drawable.home_filled,
        unselectedIcon = R.drawable.home
    ),
    HomeUiDestination(
        name = "Explore",
        selectedIcon = R.drawable.explore_filled,
        unselectedIcon = R.drawable.explore
    ),
    HomeUiDestination(
        name = "AI",
        selectedIcon = R.drawable.sparkler,
        unselectedIcon = R.drawable.sparkler
    ),
    HomeUiDestination(
        name = "Bookmarks",
        selectedIcon = R.drawable.bookmark_filled,
        unselectedIcon = R.drawable.bookmark
    ),
    HomeUiDestination(
        name = "Profile",
        selectedIcon = R.drawable.account_filled,
        unselectedIcon = R.drawable.account
    )
)
@SuppressLint("ConfigurationScreenWidthHeight", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class, FlowPreview::class
)
@Composable
fun HomeListUi(
    searchNavController: NavHostController,
    navHostController: NavHostController,
    openNavDraw:() -> Unit,
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
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val bottomBatScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
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
                placeholder = { Text("Search News Bits...") },
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
            textFieldState.clearText()
            delay(600)
            newsViewModel.resetSearchResults()
            newsViewModel.resetSearchResultsLoaded()
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            FlexibleBottomAppBar(
                scrollBehavior = bottomBatScrollBehavior,
                horizontalArrangement = Arrangement.SpaceAround,
                containerColor = Color.Transparent,
                content = {
                    val density = LocalDensity.current
                    val iconPositions = remember { mutableStateListOf<Dp>() }
                    var selected by remember { mutableIntStateOf(0) }
                    val animatedOffsetX by animateDpAsState(
                        targetValue = if (iconPositions.size > selected) 4.dp + iconPositions[selected] else 4.dp,
                        animationSpec = tween(
                            easing = ExpressiveEasing.EmphasizedDecelerate,
                            durationMillis = 200
                        ),
                        label = "CircleSlide"
                    )
                    HorizontalFloatingToolbar(
                        expanded = true,
                        expandedShadowElevation = 2.dp
                    ) {
                        Box{
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .offset(x = animatedOffsetX, y = 4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        shape = CircleShape
                                    )
                            )
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .height(56.dp)
                                    .background(
                                        color = Color.Transparent,
                                        shape = RoundedCornerShape(28.dp)
                                    ),
                            ) {
                                homeUiDestinations.forEachIndexed { index, item ->
                                    IconToggleButton(
                                        checked = selected == index,
                                        onCheckedChange = {
                                            selected = index
                                            scope.launch {
                                                delay(100)
                                                selected = index
                                            }
                                        },
                                        shapes = IconToggleButtonShapes(
                                            shape = CircleShape,
                                            pressedShape = CircleShape,
                                            checkedShape = CircleShape
                                        ),
                                        colors = IconToggleButtonColors(
                                            containerColor = Color.Transparent,
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                            disabledContainerColor = Color.Gray,
                                            disabledContentColor = Color.Gray,
                                            checkedContainerColor = Color.Transparent,
                                            checkedContentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .onGloballyPositioned { coordinates ->
                                                val xPx = coordinates.positionInParent().x
                                                val xDp = with(density) { xPx.toDp() }

                                                if (iconPositions.size <= index) {
                                                    iconPositions.add(xDp)
                                                } else {
                                                    iconPositions[index] = xDp
                                                }
                                            },
                                        interactionSource = NoRippleInteractionSource
                                    ) {
                                        Icon(
                                            painterResource(
                                                if (selected == index) {
                                                    item.selectedIcon
                                                } else {
                                                    item.unselectedIcon
                                                }
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        },
        topBar = {
            Column{
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(
                            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                            start = 16.dp,
                            end = 16.dp
                        )
                        .fillMaxWidth()
                        .height(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(R.drawable.newsbits_logo_new),
                        contentDescription = "News Bits Logo",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(100.dp)
                    )
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
                                .padding(start = 12.dp, end = 12.dp)
                        ) {
                            HomeScreen(
                                navController = navHostController,
                                scrollBehavior = scrollBehavior,
                                viewModel = newsViewModel,
                                aiViewModel = aiViewModel,
                                bottomAppBarScrollBehavior = bottomBatScrollBehavior
                            )
                        }
                    }
                }
                composable<SearchDestination.SEARCHRESULTSCREEN> {
                    SearchResultScreen(navController = navHostController, newsViewModel = newsViewModel, query = it.arguments?.getString("query") ?: "",)
                }
            }
        }
    }
}