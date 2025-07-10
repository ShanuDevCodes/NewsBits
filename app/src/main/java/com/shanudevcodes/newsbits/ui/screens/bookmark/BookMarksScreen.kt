package com.shanudevcodes.newsbits.ui.screens.bookmark

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.BookmarkDestination
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.data.formatDateString
import com.shanudevcodes.newsbits.data.savedarticledb.AppDatabase
import com.shanudevcodes.newsbits.data.savedarticledb.RoomEvents
import com.shanudevcodes.newsbits.data.savedarticledb.RoomViewModel
import com.shanudevcodes.newsbits.data.savedarticledb.RoomViewModelFactory
import com.shanudevcodes.newsbits.data.savedarticledb.SavedArticle
import com.shanudevcodes.newsbits.data.shortenName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookMarksScreen(
    openNavDraw: () -> Unit,
    navController: NavHostController
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
    val notificationCount = 11
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0),
        topBar = {
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
                            text = "BookMarks",
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 12.dp, end = 12.dp)
        ) {
            LazyColumn {
                itemsIndexed(newsList) { index, news ->
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
                                .clickable (
                                    onClick = {
                                        navController.navigate(
                                            BookmarkDestination.BOOKMARKDETAILSCREEN(
                                                news.article_id,
                                            )
                                        ) {
                                            popUpTo(navController.graph.findStartDestination().id)
                                            launchSingleTop = true
                                        }
                                    }
                                )
                        ) {
                            BookMarkedNewsListItem(news = news)
                        }
                    }
                }
            }
        }
    }
}

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
                        text = shortenName(news.category), // You can make this dynamic
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

val mockNewsList = listOf(
    NewsArticle(
    ai_org = "OpenAI",
    ai_region = "Global",
    ai_tag = "AI, Technology",
    article_id = "001",
    category = listOf("Technology", "AI"),
    content = "OpenAI has released a new model that is changing the AI landscape.",
    country = listOf("US"),
    createdAt = Timestamp.now(),
    creator = listOf("John Doe"),
    description = "A major leap in artificial intelligence by OpenAI.",
    duplicate = false,
    image_url = "https://wpengine.com/wp-content/uploads/2021/05/optimize-images.jpg",
    keywords = listOf("OpenAI", "GPT", "AI"),
    language = "en",
    link = "https://example.com/news/openai-release",
    pubDate = "2025-07-06T14:30:00Z",
    pubDateTZ = "UTC+0",
    sentiment = "positive",
    sentiment_stats = "85% positive",
    source_icon = "https://example.com/icons/source1.png",
    source_id = "openai-news",
    source_name = "TechCrunch",
    source_priority = 1,
    source_url = "https://techcrunch.com",
    title = "OpenAI Releases Groundbreaking AI Model",
    video_url = "https://example.com/video1.mp4"
    ),
    NewsArticle(
    ai_org = "World Health Org",
    ai_region = "India",
    ai_tag = "Health, Alert",
    article_id = "002",
    category = listOf("Health"),
    content = "WHO has issued a new health advisory for the monsoon season in India.",
    country = listOf("IN"),
    createdAt = Timestamp.now(),
    creator = listOf("Dr. Meera Singh"),
    description = "New monsoon health advisory issued by WHO.",
    duplicate = false,
    image_url = "https://www.industrialempathy.com/img/remote/ZiClJf-640w.avif",
    keywords = listOf("WHO", "Health", "India", "Monsoon"),
    language = "en",
    link = "https://example.com/news/who-advisory",
    pubDate = "2025-07-06T10:00:00Z",
    pubDateTZ = "UTC+5:30",
    sentiment = "neutral",
    sentiment_stats = "70% neutral",
    source_icon = "https://example.com/icons/source2.png",
    source_id = "who-health",
    source_name = "WHO Bulletin",
    source_priority = 2,
    source_url = "https://www.who.int",
    title = "WHO Issues New Health Advisory for India",
    video_url = null
    ),
    NewsArticle(
    ai_org = "UNESCO",
    ai_region = "Europe",
    ai_tag = "Culture, Heritage",
    article_id = "003",
    category = listOf("World", "Culture"),
    content = "UNESCO adds new European sites to the World Heritage List.",
    country = listOf("FR", "IT"),
    createdAt = Timestamp.now(),
    creator = listOf("Anna Müller"),
    description = "New cultural sites gain recognition from UNESCO.",
    duplicate = false,
    image_url = null,
    keywords = listOf("UNESCO", "Heritage", "Europe"),
    language = "en",
    link = "https://example.com/news/unesco-sites",
    pubDate = "2025-07-06T09:15:00Z",
    pubDateTZ = "UTC+1",
    sentiment = "positive",
    sentiment_stats = "90% positive",
    source_icon = "https://example.com/icons/source3.png",
    source_id = "unesco-news",
    source_name = "World Culture News",
    source_priority = 3,
    source_url = "https://unesco.org",
    title = "UNESCO Recognizes New Heritage Sites in Europe",
    video_url = null
    )
)
