package com.shanudevcodes.newsbits.ui.screens.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.ConnectivityObserver
import com.shanudevcodes.newsbits.data.HomeDestination
import com.shanudevcodes.newsbits.data.News
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.data.formatDateString
import com.shanudevcodes.newsbits.data.rememberNetworkStatus
import com.shanudevcodes.newsbits.data.shimmerEffect
import com.shanudevcodes.newsbits.data.shortenName
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun HomeScreen(
    navController: NavHostController,
    scrollBehavior: SearchBarScrollBehavior,
    viewModel: NewsViewModel
) {
    val networkStatus = rememberNetworkStatus()
    val newsList =viewModel.allNewsPagingFlow.collectAsLazyPagingItems()
    val newsTopList by viewModel.topNews.collectAsState()
    val state = rememberCarouselState { newsTopList.size }
    val options = listOf(
        "All",
        "business",
        "crime",
        "domestic",
        "education",
        "entertainment",
        "environment",
        "food",
        "health",
        "lifestyle",
        "other",
        "politics",
        "science",
        "sports",
        "technology",
        "top",
        "tourism",
        "world"
    )
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()
    val lazyColumnSate = rememberLazyListState()
    val visible = remember { mutableStateOf(false) }
    val lastViewedIndex = rememberSaveable { mutableStateOf<Int?>(null) }
    val lastViewedType = rememberSaveable { mutableStateOf<String?>(null) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var loadError by remember { mutableStateOf(true) }
    val isTopNewsLoaded by viewModel.isTopNewsLoaded.collectAsState()

    LaunchedEffect(newsList.loadState.refresh) {
        if (newsList.loadState.refresh is LoadState.Error){
            Log.d("NewsBitsLoadError", "Error: 1")
        }
        if (newsList.loadState.refresh is LoadState.NotLoading){
            Log.d("NewsBitsLoadError", "Error: 2")
        }
        if (newsList.itemCount == 0){
            loadError = true
        }else{
            loadError = false
        }
    }

    LaunchedEffect(currentBackStackEntry?.destination) {
        if (currentBackStackEntry?.destination?.hierarchy?.any { it.route == HomeDestination.HOMESCREEN::class.qualifiedName } == true) {
            lastViewedIndex.value = null
            lastViewedType.value = null
        }
    }
    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                delay(1000)
                navController.popBackStack(
                    route = navController.graph.startDestinationRoute
                        ?: navController.graph.findStartDestination().route!!,
                    inclusive = false
                )
                viewModel.loadTopNews()
                state.animateScrollToItem(0)
                newsList.refresh()
                delay(1000)
                isRefreshing = false
            }
        },
        indicator = {
            PullToRefreshDefaults.LoadingIndicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        },
    ) {
        LazyColumn(
            state = lazyColumnSate,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top News",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {},
                        modifier = Modifier.offset(x = 15.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "All Latest News",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            item {
                if (isTopNewsLoaded){
                    HorizontalCenteredHeroCarousel(
                        state = state,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        itemSpacing = 8.dp,
                    ) { index ->
                        val currentItem = newsTopList[index]
                        val activeIndex = state.currentItem

                        val offsetFraction =
                            (index - activeIndex).toFloat().coerceIn(-1f, 1f).absoluteValue

                        val targetAlpha = (1f - offsetFraction).coerceIn(0f, 1f)
                        val animatedAlpha by animateFloatAsState(
                            targetValue = targetAlpha,
                            animationSpec = tween(durationMillis = 500),
                            label = "carouselAlpha"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(205.dp)
                                .clip(MaterialTheme.shapes.extraLarge),
                        ) {
                            // Image with rounded corners
                            Image(
                                painter = if (currentItem.image_url != null) rememberAsyncImagePainter(
                                    model = currentItem.image_url
                                ) else painterResource(R.drawable.img_6),
                                contentDescription = "item $index",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .maskClip(MaterialTheme.shapes.extraLarge)
                            )

                            // Gradient overlay (black at bottom, transparent at top)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .maskClip(MaterialTheme.shapes.extraLarge)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color(0xCC000000) // more visible black
                                            ),
                                            startY = 100f,
                                            endY = Float.POSITIVE_INFINITY
                                        )
                                    )
                                    .clickable(
                                        onClick = {
                                            if (lastViewedIndex.value != index || lastViewedType.value != News.NEWS_TOP.name) {
                                                lastViewedIndex.value = index
                                                lastViewedType.value = News.NEWS_TOP.name
                                                navController.navigate(
                                                    HomeDestination.NEWSDETAILSCREEN(
                                                        index,
                                                        News.NEWS_TOP.name
                                                    )
                                                ) {
                                                    popUpTo(navController.graph.findStartDestination().id)
                                                    launchSingleTop = true
                                                }
                                            }
                                        }
                                    )
                            )

                            // Text content over image and gradient
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .padding(12.dp)
                                    .alpha(animatedAlpha), // padding inside image
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = currentItem.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 2,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = shortenName(currentItem.source_name), // mock writer
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = formatDateString(currentItem.pubDate), // mock time
                                        color = MaterialTheme.colorScheme.primary, // soft orange
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }else{
                    HorizontalCenteredHeroCarousel(
                        state = rememberCarouselState{10},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        itemSpacing = 8.dp,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(205.dp)
                                .clip(MaterialTheme.shapes.extraLarge),
                        ){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .maskClip(MaterialTheme.shapes.extraLarge)
                                    .shimmerEffect()
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                ) {
                    itemsIndexed(options) { index, label ->
                        ToggleButton(
                            checked = selectedIndex == index,
                            onCheckedChange = {
                                selectedIndex = index
                                val selectedCategory = options[index]
                                viewModel.setCategory(if (selectedCategory == "All") null else selectedCategory.lowercase())
                            },
                            shapes =
                                when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                checkedContainerColor = MaterialTheme.colorScheme.primary,
                            )
                        ) {
                            Text(label)
                        }
                    }
//                    item {
//                        ToggleButton(
//                            checked = false,
//                            onCheckedChange = {},
//                            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
//                            colors = ToggleButtonDefaults.toggleButtonColors(
//                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
//                                checkedContainerColor = MaterialTheme.colorScheme.primary,
//                            )
//                        ) {
//                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
//                        }
//                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(newsList.itemCount) { index ->
                val news = newsList[index]
                if (news != null) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (lastViewedIndex.value != index || lastViewedType.value != News.NEWS_ALL.name) {
                                        lastViewedIndex.value = index
                                        lastViewedType.value = News.NEWS_ALL.name
                                        navController.navigate(
                                            HomeDestination.NEWSDETAILSCREEN(
                                                index,
                                                News.NEWS_ALL.name
                                            )
                                        ) {
                                            popUpTo(navController.graph.findStartDestination().id)
                                            launchSingleTop = true
                                        }
                                    }
                                }
                        ) {
                            NewsListItem(news = news)
                        }
                    }
                }
            }

            item{
                val appendState = newsList.loadState.append
                if (appendState is LoadState.Error) {
                    Log.d("NewsBits", "Append failed: ${appendState.error}")
                }
                when(loadError) {
                    false -> {
                        if (newsList.loadState.append is LoadState.Loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CircularWavyProgressIndicator()
                                    LaunchedEffect(newsList.loadState.append) {
                                        if (newsList.loadState.append is LoadState.Loading) {
                                            delay(5000)
                                            visible.value = true // Show button if it takes too long
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    AnimatedVisibility(
                                        visible = visible.value
                                    ) {
                                        Button(onClick = {
                                            scope.launch {
                                                lazyColumnSate.animateScrollToItem(0) // 👈 scroll to top
                                                newsList.refresh()
                                            }
                                        }) {
                                            Text(text = "Refresh")
                                        }
                                    }
                                }
                            }
                        } else {
                            if (networkStatus != ConnectivityObserver.Status.Available) {
                                visible.value = false
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = "Network Error")
                                        Button(onClick = {
                                            scope.launch {
                                                lazyColumnSate.animateScrollToItem(0) // 👈 scroll to top
                                                newsList.refresh()
                                            }
                                        }) {
                                            Text(text = "Refresh")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    true -> {
                        for (i in 1 .. 10){
                            DummySearchResultItem()
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp+WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
            }
        }
    }
}
@Composable
fun NewsListItem(news: NewsArticle) {
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