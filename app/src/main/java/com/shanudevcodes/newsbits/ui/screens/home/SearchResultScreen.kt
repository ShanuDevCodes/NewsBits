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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.NewsArticleSearch
import com.shanudevcodes.newsbits.data.formatDateString
import com.shanudevcodes.newsbits.data.shortenName
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(){
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val notificationCount = 11
    val scope = rememberCoroutineScope()
    val screenWidthDp = configuration.screenWidthDp.dp
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
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
//                    scope.launch {
//                        roomViewModel.onEvent(RoomEvents.UpsertHistory)
//                    }
                },
                placeholder = { Text("Search News Bits...") },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        IconButton(
                            onClick = {
                                scope.launch {
//                                    textFieldState.clearText()
//                                    newsViewModel.resetSearchResults()
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
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        if (isPortrait) {
                            IconButton(
                                onClick = { },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    modifier = Modifier.size(48.dp),
                                )
                            }
                        }
                    },
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Search Results",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                            if (isPortrait) {
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
                        }
                    }
                )
                if (isPortrait) {
                    TopSearchBar(
                        state = searchBarState,
                        inputField = inputField,
                        windowInsets = WindowInsets(0),
                    )
                    ExpandedFullScreenSearchBar(
                        tonalElevation = 48.dp,
                        state = searchBarState,
                        inputField = inputField,
                        windowInsets = { WindowInsets.statusBars },
                    ) {

                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 12.dp, end = 12.dp)
        ) {
            LazyColumn {
                itemsIndexed(mockNewsArticles) { index, news ->
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
//                                    scope.launch {
//                                        navigator.navigateTo(
//                                            pane = ListDetailPaneScaffoldRole.Detail,
//                                            contentKey = "${News.NEWS_ALL.name}::$index"
//                                        )
//                                    }

                                    }
                            ) {
                                NewsSearchListItem(news = news)
                            }
                        }
                    }
                }
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

val mockNewsArticles = listOf(
    NewsArticleSearch(
        ai_org = "OpenAI",
        ai_region = "Global",
        ai_tag = "AI",
        article_id = "001",
        category = listOf("Technology", "AI"),
        content = "OpenAI has announced its new model that surpasses previous benchmarks in reasoning and safety.",
        country = listOf("US"),
        createdAt = System.currentTimeMillis(),
        creator = listOf("OpenAI News Team"),
        description = "OpenAI introduces a powerful new language model.",
        duplicate = false,
        image_url = "https://www.industrialempathy.com/img/remote/ZiClJf-640w.avif",
        keywords = listOf("AI", "OpenAI", "model"),
        language = "en",
        link = "https://openai.com/blog/new-model",
        pubDate = "2025-07-14",
        pubDateTZ = "UTC",
        sentiment = "positive",
        sentiment_stats = "{\"positive\":0.9,\"neutral\":0.1,\"negative\":0.0}",
        source_icon = "https://example.com/icons/openai.png",
        source_id = "openai_blog",
        source_name = "OpenAI Blog",
        source_priority = 1,
        source_url = "https://openai.com",
        title = "OpenAI Unveils Its Most Capable Model Yet",
        video_url = null
    ),
    NewsArticleSearch(
        ai_org = "Google DeepMind",
        ai_region = "Europe",
        ai_tag = "AI",
        article_id = "002",
        category = listOf("Technology", "Research"),
        content = "DeepMind's AlphaFold continues to revolutionize protein structure prediction in the scientific community.",
        country = listOf("UK"),
        createdAt = System.currentTimeMillis(),
        creator = listOf("DeepMind Press"),
        description = "AlphaFold makes significant breakthroughs in biology.",
        duplicate = false,
        image_url = "https://cdn.pixabay.com/photo/2018/08/04/11/30/draw-3583548_960_720.png",
        keywords = listOf("DeepMind", "AlphaFold", "protein"),
        language = "en",
        link = "https://deepmind.com/blog/alphafold",
        pubDate = "2025-07-13",
        pubDateTZ = "UTC",
        sentiment = "positive",
        sentiment_stats = "{\"positive\":0.85,\"neutral\":0.1,\"negative\":0.05}",
        source_icon = "https://example.com/icons/deepmind.png",
        source_id = "deepmind_blog",
        source_name = "DeepMind Blog",
        source_priority = 2,
        source_url = "https://deepmind.com",
        title = "AlphaFold Achieves New Milestone in Protein Prediction",
        video_url = null
    ),
    NewsArticleSearch(
        ai_org = "Meta AI",
        ai_region = "North America",
        ai_tag = "AI",
        article_id = "003",
        category = listOf("Social Media", "Technology"),
        content = "Meta's new LLM aims to bring contextual awareness to Facebook and Instagram.",
        country = listOf("US"),
        createdAt = System.currentTimeMillis(),
        creator = listOf("Meta Newsroom"),
        description = "Meta’s AI integrates deeper into social platforms.",
        duplicate = false,
        image_url = "https://cloudinary-marketing-res.cloudinary.com/image/upload/w_700/hiking_dog_mountain",
        keywords = listOf("Meta", "LLM", "social media"),
        language = "en",
        link = "https://meta.com/ai/news",
        pubDate = "2025-07-12",
        pubDateTZ = "UTC",
        sentiment = "neutral",
        sentiment_stats = "{\"positive\":0.4,\"neutral\":0.5,\"negative\":0.1}",
        source_icon = "https://example.com/icons/meta.png",
        source_id = "meta_news",
        source_name = "Meta News",
        source_priority = 3,
        source_url = "https://meta.com",
        title = "Meta AI Pushes LLM Integration in Social Platforms",
        video_url = "https://example.com/videos/meta_llm.mp4"
    ),
    NewsArticleSearch(
        ai_org = "Anthropic",
        ai_region = "US",
        ai_tag = "AI",
        article_id = "004",
        category = listOf("Ethics", "AI"),
        content = "Anthropic emphasizes AI alignment in its latest Claude model release.",
        country = listOf("US"),
        createdAt = System.currentTimeMillis(),
        creator = listOf("Anthropic Team"),
        description = "Claude 3 focuses on safe AI development.",
        duplicate = false,
        image_url = "https://imgv3.fotor.com/images/slider-image/A-clear-close-up-photo-of-a-woman.jpg",
        keywords = listOf("Anthropic", "Claude", "alignment"),
        language = "en",
        link = "https://www.anthropic.com/blog/claude-3",
        pubDate = "2025-07-11",
        pubDateTZ = "UTC",
        sentiment = "positive",
        sentiment_stats = "{\"positive\":0.75,\"neutral\":0.2,\"negative\":0.05}",
        source_icon = "https://example.com/icons/anthropic.png",
        source_id = "anthropic_blog",
        source_name = "Anthropic Blog",
        source_priority = 2,
        source_url = "https://www.anthropic.com",
        title = "Claude 3 from Anthropic Prioritizes AI Safety",
        video_url = null
    )
)


@Preview
@Composable
fun Preview(){
    SearchResultScreen()
}