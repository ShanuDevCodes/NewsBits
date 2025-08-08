package com.shanudevcodes.newsbits.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.data.HomeDestination
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.data.savedarticledb.data.roomdatabase.AppDatabase
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.events.RoomEvents
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal.RoomViewModel
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal.RoomViewModelFactory
import com.shanudevcodes.newsbits.data.shimmerEffect
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ForYouPage(
    newsViewModel: NewsViewModel,
    navController: NavHostController,
    onRefreshCall: Boolean,
    onRefresh: () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val newsList = newsViewModel.forYouNewsPagingFlow.collectAsLazyPagingItems()
    val currentLink by newsViewModel.currentLink.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { newsList.itemCount })
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val dao = db.RoomDao()
    val roomViewModel: RoomViewModel = viewModel(
        factory = RoomViewModelFactory(dao)
    )
    val dataStore = DataStoreManager(context)
    LaunchedEffect(Unit) {
        dataStore.categoryPreferenceFlow.collect {
            Log.d("Category Preference", it?: "null")
            newsViewModel.setPreference(it)
        }
    }

    LaunchedEffect(onRefreshCall) {
        if (onRefreshCall){
            isRefreshing = true
        }
    }
    LaunchedEffect(newsList.loadState.refresh) {
        isLoading = newsList.itemCount == 0
    }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing){
            delay(1000)
            newsList.refresh()
            delay(1000)
            pagerState.animateScrollToPage(0)
            isRefreshing = false
            onRefresh()
        }
    }
    Surface(
        color = MaterialTheme.colorScheme.surfaceDim
    ) {
        Scaffold(
            contentColor = MaterialTheme.colorScheme.surfaceDim,
            containerColor = MaterialTheme.colorScheme.surfaceDim,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                Row(
                    modifier = Modifier
                        .padding(
                            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                            start = 12.dp,
                            end = 12.dp
                        )
                        .fillMaxWidth()
                        .height(50.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.newsbits_logo_new),
                        contentDescription = "News Bits Logo",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        ) {
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                },
                indicator = {
                    PullToRefreshDefaults.LoadingIndicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
                    )
                },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    when (isLoading) {
                        false -> {
                            VerticalPager(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                contentPadding = PaddingValues(vertical = 120.dp),
                                state = pagerState
                            ) { page ->
                                newsList[page]?.let { article ->
                                    var isAdded by remember { mutableStateOf(false) }
                                    var refreshTrigger by remember { mutableStateOf(false) }
                                    LaunchedEffect(article.article_id, refreshTrigger) {
                                        roomViewModel.onEvent(RoomEvents.CheckEachArticleSaved(article.article_id) { exists ->
                                            isAdded = exists
                                        })
                                    }
                                    VerticalCarouselItem(
                                        news = article,
                                        modifier = Modifier.carouselTransition(
                                            page = page,
                                            pagerState = pagerState,
                                        ),
                                        onClick = {
                                            if (currentLink != article.link) {
                                                navController.navigate(
                                                    HomeDestination.SEARCHRESULTDETAILSCREEN(
                                                        article.link,
                                                    )
                                                ) {
                                                    popUpTo(navController.graph.findStartDestination().id)
                                                    launchSingleTop = true
                                                }
                                                newsViewModel.setCurrentLink(article.link)
                                            }
                                        },
                                        isAdded = isAdded,
                                        onBookmarkClick = {
                                            if (isAdded) {
                                                roomViewModel.onEvent(RoomEvents.DeleteArticle(article))
                                            } else {
                                                roomViewModel.onEvent(RoomEvents.SaveArticle(article))
                                            }
                                            refreshTrigger = !refreshTrigger
                                        }
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .height(
                                        WindowInsets.statusBars.asPaddingValues()
                                            .calculateTopPadding() + 120.dp
                                    )
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.surfaceDim,
                                                Color.Transparent
                                            )
                                        )
                                    ),
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .height(
                                        WindowInsets.navigationBars.asPaddingValues()
                                            .calculateTopPadding() + 180.dp
                                    )
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.surfaceDim
                                            ),
                                        )
                                    ),
                            )
                        }

                        true -> {
                            val dummyPagerState = rememberPagerState(pageCount = { 10 })
                            VerticalPager(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                contentPadding = PaddingValues(vertical = 120.dp),
                                state = dummyPagerState
                            ) { page ->
                                VerticalCarouselItem(
                                    modifier = Modifier.carouselTransition(
                                        page = page,
                                        pagerState = dummyPagerState,
                                    ),
                                    news = NewsArticle(),
                                    isLoading = true,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VerticalCarouselItem(
    news: NewsArticle,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isLoading: Boolean = false,
    isAdded: Boolean = false,
    onBookmarkClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = modifier.fillMaxSize(),
        onClick = { if (!isLoading) onClick() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 🖼 IMAGE
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(8.dp))
                    .then(if (isLoading) Modifier.shimmerEffect() else Modifier)
            ) {
                if (!isLoading) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(news.image_url)
                            .crossfade(true)
                            .error(R.drawable.img_6)
                            .placeholder(R.drawable.img_6)
                            .build(),
                        contentDescription = news.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }

            // 📰 TITLE
            if (isLoading) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth(if (it < 2) 1f else 0.6f)
                            .height(28.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
            } else {
                Text(
                    text = news.title,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.headlineMediumEmphasized,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 📄 DESCRIPTION
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                if (isLoading) {
                    Column {
                        Spacer(modifier = Modifier.height(18.dp))
                        repeat(5) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp,vertical = 4.dp)
                                    .fillMaxWidth()
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmerEffect()
                            )
                        }
                    }
                } else {
                    Text(
                        text = news.description ?: "",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodyLargeEmphasized,
                        color = Color.Gray,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 🏷 SOURCE ROW
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                if (isLoading) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = news.source_name,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = news.source_name,
                        style = MaterialTheme.typography.labelLargeEmphasized,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                IconButton(
                    onClick = {
                        onBookmarkClick()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isAdded){
                            Icons.Filled.BookmarkAdded
                        }else{
                            Icons.Outlined.BookmarkAdd
                        },
                        contentDescription = news.source_name,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

fun Modifier.carouselTransition(page: Int, pagerState: PagerState) =
    graphicsLayer {
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val scale = lerp(
            start = 0.9f,
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
