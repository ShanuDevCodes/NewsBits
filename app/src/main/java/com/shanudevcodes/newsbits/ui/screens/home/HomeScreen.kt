package com.shanudevcodes.newsbits.ui.screens.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.BottomAppBarScrollBehavior
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.util.lerp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.ConnectivityObserver
import com.shanudevcodes.newsbits.data.HomeDestination
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.data.formatDateString
import com.shanudevcodes.newsbits.data.getTimeAgo
import com.shanudevcodes.newsbits.data.rememberNetworkStatus
import com.shanudevcodes.newsbits.data.shimmerEffect
import com.shanudevcodes.newsbits.data.shortenName
import com.shanudevcodes.newsbits.viewmodel.AiViewModel
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun HomeScreen(
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    navController: NavHostController,
    scrollBehavior: SearchBarScrollBehavior,
    viewModel: NewsViewModel,
    aiViewModel: AiViewModel
) {
    val networkStatus = rememberNetworkStatus()
    val newsList =viewModel.allNewsPagingFlow.collectAsLazyPagingItems()
    val newsTopList by viewModel.topNews.collectAsState()
    val options = listOf(
        "All",
        "Business",
        "Crime",
        "Domestic",
        "Education",
        "Entertainment",
        "Environment",
        "Food",
        "Health",
        "Lifestyle",
        "Other",
        "Politics",
        "Science",
        "Sports",
        "Technology",
        "Top",
        "Tourism",
        "World"
    )
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()
    val lazyColumnSate = rememberLazyListState()
    val visible = remember { mutableStateOf(false) }
    val currentLink by viewModel.currentLink.collectAsState()
    var loadError by remember { mutableStateOf(true) }
    val isTopNewsLoaded by viewModel.isTopNewsLoaded.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val scrollPx = scrollBehavior.scrollOffset
    val maxPx = scrollBehavior.scrollOffsetLimit // e.g. -180f
    val pageCount = Int.MAX_VALUE
    val startPage = (pageCount / 2) - ((pageCount / 2) % 10)
    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = {pageCount}
    )
    val dummyPageCount = 10
    val dummyStartPage = (pageCount / 2) - ((pageCount / 2) % dummyPageCount)
    val dummyPagerState = rememberPagerState(
        initialPage = dummyStartPage,
        pageCount = { pageCount }
    )

    val topPaddingDp = with(density) {
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 115.dp
    }

    val dynamicTopPadding = with(density) {
        val collapsedDp = (-scrollPx).toDp()
        val maxDp = (-maxPx).toDp()
        val clampedDp = collapsedDp.coerceIn(0.dp, maxDp)
        max(0.dp, topPaddingDp - clampedDp)
    }
    LaunchedEffect(newsTopList) {
        viewModel.loadTopNews()
        newsList.refresh()
    }
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
    if (showBottomSheet) {
        ModalBottomSheet(
            containerColor = MaterialTheme.colorScheme.surfaceDim,
            sheetGesturesEnabled = false,
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            dragHandle = null
        ) {
            AiBottomSheetContent(aiViewModel)
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
                newsList.refresh()
                delay(1000)
                pagerState.animateScrollToPage(startPage)
                isRefreshing = false
            }
        },
        indicator = {
            PullToRefreshDefaults.LoadingIndicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = dynamicTopPadding)
                ,
            )
        },
    ) {
        LazyColumn(
            state = lazyColumnSate,
            contentPadding = PaddingValues(top = dynamicTopPadding),
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection)
        ) {
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top News",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLargeEmphasized
                    )
                    IconButton(
                        onClick = {
                            scope.launch {
                                showBottomSheet = true
                            }
                        },
                        modifier = Modifier
                            .size(30.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sparkler),
                            contentDescription = "AI",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            item{
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isTopNewsLoaded) {
                        HorizontalPager(
                            contentPadding = PaddingValues(horizontal = 28.dp),
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                        ) { page ->
                            val currentItem = newsTopList.getOrNull(page % newsTopList.size)
                            if (currentItem != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .clip(MaterialTheme.shapes.extraLarge)
                                        .horizontalCarouselTransition(page, pagerState),
                                ) {
                                    // Image with rounded corners
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth()
                                            .clip(MaterialTheme.shapes.extraLarge)
                                            .clickable(
                                                onClick = {
                                                    if (currentLink != currentItem.link) {
                                                        navController.navigate(
                                                            HomeDestination.SEARCHRESULTDETAILSCREEN(
                                                                currentItem.link,
                                                            )
                                                        ) {
                                                            popUpTo(navController.graph.findStartDestination().id)
                                                            launchSingleTop = true
                                                        }
                                                        viewModel.setCurrentLink(currentItem.link)
                                                    }
                                                }
                                            )
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(currentItem.image_url)
                                                .crossfade(true)
                                                .error(R.drawable.img_6)
                                                .placeholder(R.drawable.img_6)
                                                .build(),
                                            contentDescription = currentItem.description,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .clip(MaterialTheme.shapes.extraLarge)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(alignment = Alignment.BottomCenter)
                                                .fillMaxWidth()
                                                .height(140.dp)
                                                .clip(MaterialTheme.shapes.extraLarge)
                                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight()
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.SpaceEvenly
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.AccessTimeFilled,
                                                        contentDescription = "Time",
                                                        modifier = Modifier.size(16.dp),
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = getTimeAgo(currentItem.pubDate),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        style = MaterialTheme.typography.labelLargeEmphasized
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = currentItem.source_name,
                                                        color = Color.Gray,
                                                        style = MaterialTheme.typography.labelLargeEmphasized
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = currentItem.title,
                                                    style = MaterialTheme.typography.titleMediumEmphasized,
                                                    maxLines = 3,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        HorizontalPager(
                            contentPadding = PaddingValues(horizontal = 28.dp),
                            state = dummyPagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                        ) { page ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .horizontalCarouselTransition(page, dummyPagerState),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp)
                                        .clip(MaterialTheme.shapes.extraLarge)
                                        .shimmerEffect()
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    DotIndicators(
                        pageCount = 10,
                        pagerState = pagerState,
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                ) {
                    itemsIndexed(options) { index, label ->
//                        ToggleButton(
//                            checked = selectedIndex == index,
//                            onCheckedChange = {
//                                selectedIndex = index
//                                val selectedCategory = options[index]
//                                viewModel.setCategory(if (selectedCategory == "All") null else selectedCategory.lowercase())
//                            },
//                            shapes =
//                                when (index) {
//                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
//                                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
//                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
//                                },
//                            colors = ToggleButtonDefaults.toggleButtonColors(
//                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
//                                checkedContainerColor = MaterialTheme.colorScheme.primary,
//                            )
//                        ) {
//                            Text(
//                                text = label,
//                                style = MaterialTheme.typography.titleMediumEmphasized
//                            )
//                        }
                        ToggleButton(
                            checked = selectedIndex == index,
                            onCheckedChange = {
                                selectedIndex = index
                                val selectedCategory = options[index]
                                viewModel.setCategory(if (selectedCategory == "All") null else selectedCategory.lowercase())
                            },
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                checkedContainerColor = MaterialTheme.colorScheme.primary,
                            )
                        ) {
                            Text(label)
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(newsList.itemCount) { index ->
                val news = newsList[index]
                if (news != null) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = if (index == 0) {
                                24.dp
                            }else {
                                4.dp
                            },
                            topEnd = if (index == 0) {
                                24.dp
                            }else {
                                4.dp
                            },
                            bottomStart = if (index == newsList.itemCount-1) {
                                24.dp
                            }else {
                                4.dp
                            },
                            bottomEnd = if (index == newsList.itemCount-1) {
                                24.dp
                            }else {
                                4.dp
                            }
                        ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, bottom = 2.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (currentLink != news.link) {
                                        viewModel.setCurrentLink(news.link)
                                        navController.navigate(
                                            HomeDestination.SEARCHRESULTDETAILSCREEN(
                                                news.link,
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
                        repeat(10){
                            Card(
                                shape = RoundedCornerShape(
                                    topStart = if (it == 0) {
                                        24.dp
                                    }else {
                                        4.dp
                                    },
                                    topEnd = if (it == 0) {
                                        24.dp
                                    }else {
                                        4.dp
                                    },
                                    bottomStart = if (it == 9) {
                                        24.dp
                                    }else {
                                        4.dp
                                    },
                                    bottomEnd = if (it == 9) {
                                        24.dp
                                    }else {
                                        4.dp
                                    }
                                ),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 2.dp),
                            ) {
                                DummySearchResultItem()
                            }
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
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewsListItem(news: NewsArticle) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail Image (landscape rectangle)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(news.image_url)
                .crossfade(true)
                .error(R.drawable.img_6)
                .placeholder(R.drawable.img_6)
                .build(),
            contentDescription = news.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(140.dp)
                .height(100.dp)
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
                style = MaterialTheme.typography.titleMediumEmphasized,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                    style = MaterialTheme.typography.labelMediumEmphasized,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                        style = MaterialTheme.typography.labelMediumEmphasized,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = formatDateString(news.pubDate),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMediumEmphasized,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DotIndicators(
    pageCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { iteration ->
            val color by animateColorAsState(
                targetValue = if (pagerState.currentPage % pageCount == iteration) MaterialTheme.colorScheme.onSurface else Color.Gray,
                animationSpec = tween(durationMillis = 300),
                label = "DotColorAnim"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (pagerState.currentPage % pageCount == iteration) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

fun Modifier.horizontalCarouselTransition(page: Int, pagerState: PagerState) =
    graphicsLayer {
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val scale = lerp(
            start = 0.92f,
            stop = 1f,
            fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
        )
        scaleX = scale
        scaleY = scale
        alpha = lerp(
            start = 0.5f,
            stop = 1f,
            fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
        )
    }