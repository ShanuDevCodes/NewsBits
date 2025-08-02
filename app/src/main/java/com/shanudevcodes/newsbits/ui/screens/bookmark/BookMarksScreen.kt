package com.shanudevcodes.newsbits.ui.screens.bookmark

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.HomeDestination
import com.shanudevcodes.newsbits.data.formatDateString
import com.shanudevcodes.newsbits.data.savedarticledb.data.entity.SavedArticle
import com.shanudevcodes.newsbits.data.savedarticledb.data.mapper.toEntity
import com.shanudevcodes.newsbits.data.savedarticledb.data.roomdatabase.AppDatabase
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.events.RoomEvents
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal.RoomViewModel
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal.RoomViewModelFactory
import com.shanudevcodes.newsbits.data.shortenName
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BookMarksScreen(
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    navController: NavHostController,
    newsViewModel: NewsViewModel
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val dao = db.RoomDao()
    val roomViewModel: RoomViewModel = viewModel(
        factory = RoomViewModelFactory(dao)
    )
    roomViewModel.onEvent(RoomEvents.GetArticles)
    val viewModelState = roomViewModel.state.collectAsState()
    val newsList = viewModelState.value.savedArticles
    val currentLink by newsViewModel.currentLink.collectAsState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceDim,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                        start = 12.dp,
                        end = 12.dp
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BookMarks",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.displayMediumEmphasized,
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 12.dp, end = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection)
            ) {
                item{
                    Spacer(modifier = Modifier.height(16.dp))
                }
                itemsIndexed(newsList) { index, news ->
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
                            bottomStart = if (index == newsList.lastIndex) {
                                24.dp
                            }else {
                                4.dp
                            },
                            bottomEnd = if (index == newsList.lastIndex) {
                                24.dp
                            }else {
                                4.dp
                            }
                        ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 2.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable (
                                    onClick = {
                                        if (currentLink != news.link) {
                                            navController.navigate(
                                                HomeDestination.BOOKMARKDETAILSCREEN(
                                                    news.article_id,
                                                )
                                            ) {
                                                popUpTo(navController.graph.findStartDestination().id)
                                                launchSingleTop = true
                                            }
                                            newsViewModel.setCurrentLink(news.link)
                                        }
                                    }
                                )
                        ) {
                            BookMarkedNewsListItem(news = news.toEntity())
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BookMarkedNewsListItem(news: SavedArticle) {
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
                        text = shortenName(news.category), // You can make this dynamic
                        style = MaterialTheme.typography.labelMediumEmphasized,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = formatDateString(news.pubDate),
                    style = MaterialTheme.typography.labelMediumEmphasized,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
