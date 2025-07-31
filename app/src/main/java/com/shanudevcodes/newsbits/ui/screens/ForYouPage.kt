package com.shanudevcodes.newsbits.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.BookmarkAdded
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.HomeDestination
import com.shanudevcodes.newsbits.data.News
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ForYouPage(
    newsViewModel: NewsViewModel,
    navController: NavHostController
) {
    val newsList = newsViewModel.allNewsPagingFlow.collectAsLazyPagingItems()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val lastViewedIndex = rememberSaveable { mutableStateOf<Int?>(null) }
    val lastViewedType = rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(newsList) {
        newsList.refresh()
    }
    LaunchedEffect(currentBackStackEntry?.destination) {
        if (currentBackStackEntry?.destination?.hierarchy?.any { it.route == HomeDestination.HOMESCREEN::class.qualifiedName } == true) {
            lastViewedIndex.value = null
            lastViewedType.value = null
        }
    }
    val pagerState = rememberPagerState(pageCount = { newsList.itemCount })
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
                            start = 16.dp,
                            end = 16.dp
                        )
                        .fillMaxWidth()
                        .height(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "For You",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                VerticalPager(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 150.dp),
                    state = pagerState
                ) { page ->
                    newsList[page]?.let { article ->
                        VerticalCarouselItem(
                            news = article,
                            modifier = Modifier.carouselTransition(
                                page = page,
                                pagerState = pagerState,
                            ),
                            onClick = {
                                if (lastViewedIndex.value != page || lastViewedType.value != News.NEWS_TOP.name) {
                                    lastViewedIndex.value = page
                                    lastViewedType.value = News.NEWS_TOP.name
                                    navController.navigate(
                                        HomeDestination.SEARCHRESULTDETAILSCREEN(
                                            article.link,
                                        )
                                    ) {
                                        popUpTo(navController.graph.findStartDestination().id)
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 120.dp)
                        .fillMaxWidth()
                        .background(brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceDim,
                                Color.Transparent
                            )
                        )),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(WindowInsets.navigationBars.asPaddingValues().calculateTopPadding() + 180.dp)
                        .fillMaxWidth()
                        .background(brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surfaceDim
                            ),
                        )),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VerticalCarouselItem(news: NewsArticle, modifier: Modifier, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = modifier
            .fillMaxSize(),
        onClick = {
            onClick()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(8.dp))
            ) {
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
            Text(
                text = news.title,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLargeEmphasized,
                fontWeight = FontWeight.SemiBold
            )
            Box(
                modifier = Modifier
                    .weight(1f).fillMaxSize()
            ) {
                Text(
                    text = news.description?: "",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = news.source_name,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = news.source_name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {},
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Gray.copy(alpha = 0.1f),
                    ),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkAdded,
                        contentDescription = news.source_name,
                        tint = MaterialTheme.colorScheme.secondary
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
