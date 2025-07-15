package com.shanudevcodes.newsbits.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.NewsArticleSearch
import com.shanudevcodes.newsbits.data.formatDateString
import com.shanudevcodes.newsbits.data.savedarticledb.AppDatabase
import com.shanudevcodes.newsbits.data.savedarticledb.RoomEvents
import com.shanudevcodes.newsbits.data.savedarticledb.RoomViewModel
import com.shanudevcodes.newsbits.data.savedarticledb.RoomViewModelFactory
import com.shanudevcodes.newsbits.data.shortenName
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class, FlowPreview::class
)
@Composable
fun HomeListUi(
    navHostController: NavHostController,
//    navigator: ThreePaneScaffoldNavigator<Any>,
    openNavDraw:() -> Unit,
    newsViewModel: NewsViewModel
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
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier.width(
                    when(isPortrait) {
                        false -> screenWidthDp * 0.337f
                        true -> screenWidthDp
                    }
                ),
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = {
                    scope.launch {
                        roomViewModel.onEvent(RoomEvents.UpsertHistory)
                    }
                },
                placeholder = { Text("Search News Bits...") },
                leadingIcon = {
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
                            .clickable(onClick = {
                                scope.launch {

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
//                        newsViewModel.searchNewsInAlgolia(newText)
                        newsViewModel.searchSuggestionInAlgolia(newText)
                    } else {
//                        newsViewModel.resetSearchResults()
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
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
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
                                            modifier = Modifier.offset(y = -5.dp)
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
                    if (!isPortrait) {
                        ExpandedDockedSearchBar(
                            tonalElevation = 48.dp,
                            state = searchBarState,
                            inputField = inputField,
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
//                                        itemsIndexed(searchResults) { index, search ->
//                                            Card(
//                                                shape = RoundedCornerShape(24.dp),
//                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
//                                                modifier = Modifier
//                                                    .fillMaxWidth()
//                                                    .padding(bottom = 8.dp),
//                                            ) {
//                                                Box(
//                                                    modifier = Modifier
//                                                        .fillMaxWidth()
//                                                ) {
//                                                    NewsSearchListItem(news = search)
//                                                }
//                                            }
//                                        }
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
                                                                        it
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
//                                        itemsIndexed(searchResults) { index, search ->
//                                            Card(
//                                                shape = RoundedCornerShape(24.dp),
//                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
//                                                modifier = Modifier
//                                                    .fillMaxWidth()
//                                                    .padding(bottom = 8.dp),
//                                            ) {
//                                                Box(
//                                                    modifier = Modifier
//                                                        .fillMaxWidth()
//                                                ) {
//                                                    NewsSearchListItem(news = search)
//                                                }
//                                            }
//                                        }
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
                                                                        it
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
        Column {

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(start = 12.dp, end = 12.dp)
            ) {
                // Screen content goes here
                HomeScreen(
                    navController = navHostController,
//                navigator = navigator,
                    scrollBehavior = scrollBehavior,
                    viewModel = newsViewModel
                )
            }
        }
    }
}

@Composable
fun NewsSearchListItem(news: NewsArticleSearch) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail Image (landscape rectangle)
        Image(
            painter = if (news.image_url != null) rememberAsyncImagePainter(model = news.image_url) else painterResource(R.drawable.img_6),
            contentDescription = news.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(120.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Text and Metadata
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Headline
            Text(
                text = news.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Writer row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Author",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = news.source_name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom row (tag + time)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Tag",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = shortenName(news.category.joinToString(", ")), // You can make this dynamic
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = formatDateString(news.pubDate),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}