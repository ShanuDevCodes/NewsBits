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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.BookmarkAdded
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.Timestamp
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.NewsArticle
import kotlin.math.absoluteValue

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

val sampleNewsList = listOf(
    NewsArticle(
        ai_org = "OpenAI",
        ai_region = "US",
        ai_tag = "AI, ChatGPT",
        article_id = "001",
        category = listOf("Technology", "AI"),
        content = "OpenAI has released the new GPT-4.5 model with improved reasoning capabilities.",
        country = listOf("US"),
        createdAt = Timestamp.now(),
        creator = listOf("John Doe"),
        description = "GPT-4.5 brings more accuracy and better response generation.",
        duplicate = false,
        image_url = "https://wpengine.com/wp-content/uploads/2021/05/optimize-images-768x511.jpg",
        keywords = listOf("AI", "GPT-4.5", "OpenAI"),
        language = "en",
        link = "https://example.com/openai-gpt4.5",
        pubDate = "2025-07-28",
        pubDateTZ = "GMT",
        sentiment = "Positive",
        sentiment_stats = "80% Positive",
        source_icon = "https://example.com/icons/openai.png",
        source_id = "openai-news",
        source_name = "OpenAI Blog",
        source_priority = 1,
        source_url = "https://openai.com/blog",
        title = "OpenAI Launches GPT-4.5",
        video_url = null
    ),
    NewsArticle(
        ai_org = "Google",
        ai_region = "US",
        ai_tag = "Cloud, AI",
        article_id = "002",
        category = listOf("Technology", "Cloud"),
        content = "Google Cloud introduces AI-powered security tools.",
        country = listOf("US"),
        createdAt = Timestamp.now(),
        creator = listOf("Jane Smith"),
        description = "Google Cloud Security AI Suite now available for enterprises.",
        duplicate = false,
        image_url = "https://wpengine.com/wp-content/uploads/2021/05/optimize-images-768x511.jpg",
        keywords = listOf("Google", "Cloud", "AI"),
        language = "en",
        link = "https://example.com/google-security-ai",
        pubDate = "2025-07-27",
        pubDateTZ = "GMT",
        sentiment = "Neutral",
        sentiment_stats = "60% Neutral",
        source_icon = "https://example.com/icons/google.png",
        source_id = "google-news",
        source_name = "Google Cloud Blog",
        source_priority = 2,
        source_url = "https://cloud.google.com/blog",
        title = "Google Cloud Launches AI Security Tools",
        video_url = null
    ),
    NewsArticle(
        ai_org = "Meta",
        ai_region = "Global",
        ai_tag = "Social Media, AI",
        article_id = "003",
        category = listOf("Technology", "Social Media"),
        content = "Meta integrates AI-driven features into Instagram Reels.",
        country = listOf("US"),
        createdAt = Timestamp.now(),
        creator = listOf("Michael Johnson"),
        description = "AI personalization in Reels promises better recommendations.",
        duplicate = false,
        image_url = "https://wpengine.com/wp-content/uploads/2021/05/optimize-images-768x511.jpg",
        keywords = listOf("Meta", "Instagram", "AI"),
        language = "en",
        link = "https://example.com/meta-instagram-ai",
        pubDate = "2025-07-26",
        pubDateTZ = "GMT",
        sentiment = "Positive",
        sentiment_stats = "75% Positive",
        source_icon = "https://example.com/icons/meta.png",
        source_id = "meta-news",
        source_name = "Meta Newsroom",
        source_priority = 3,
        source_url = "https://about.meta.com/news",
        title = "Meta Adds AI to Instagram Reels",
        video_url = null
    ),
    NewsArticle(
        ai_org = "Microsoft",
        ai_region = "US",
        ai_tag = "Copilot, AI",
        article_id = "004",
        category = listOf("Technology", "AI"),
        content = "Microsoft expands Copilot to Windows 12.",
        country = listOf("US"),
        createdAt = Timestamp.now(),
        creator = listOf("Sarah Williams"),
        description = "Copilot will be natively integrated into Windows 12.",
        duplicate = false,
        image_url = "https://wpengine.com/wp-content/uploads/2021/05/optimize-images-768x511.jpg",
        keywords = listOf("Microsoft", "Copilot", "Windows 12"),
        language = "en",
        link = "https://example.com/microsoft-windows12-copilot",
        pubDate = "2025-07-25",
        pubDateTZ = "GMT",
        sentiment = "Positive",
        sentiment_stats = "85% Positive",
        source_icon = "https://example.com/icons/microsoft.png",
        source_id = "microsoft-news",
        source_name = "Microsoft Blog",
        source_priority = 4,
        source_url = "https://blogs.microsoft.com",
        title = "Windows 12 Brings AI Copilot",
        video_url = null
    ),
    NewsArticle(
        ai_org = "Apple",
        ai_region = "US",
        ai_tag = "AI, iOS",
        article_id = "005",
        category = listOf("Technology", "Mobile"),
        content = "Apple introduces on-device AI in iOS 19.",
        country = listOf("US"),
        createdAt = Timestamp.now(),
        creator = listOf("David Brown"),
        description = "AI-powered Siri now processes requests offline.",
        duplicate = false,
        image_url = "https://wpengine.com/wp-content/uploads/2021/05/optimize-images-768x511.jpg",
        keywords = listOf("Apple", "Siri", "AI"),
        language = "en",
        link = "https://example.com/apple-ios19-ai",
        pubDate = "2025-07-24",
        pubDateTZ = "GMT",
        sentiment = "Positive",
        sentiment_stats = "90% Positive",
        source_icon = "https://example.com/icons/apple.png",
        source_id = "apple-news",
        source_name = "Apple Newsroom",
        source_priority = 5,
        source_url = "https://www.apple.com/newsroom",
        title = "iOS 19 Gets AI Upgrade",
        video_url = null
    ),
    NewsArticle(
        ai_org = "Tesla",
        ai_region = "Global",
        ai_tag = "Autopilot, AI",
        article_id = "006",
        category = listOf("Technology", "Automobile"),
        content = "Tesla announces AI-driven autopilot improvements.",
        country = listOf("US"),
        createdAt = Timestamp.now(),
        creator = listOf("Emily Davis"),
        description = "Full Self Driving gets a major update.",
        duplicate = false,
        image_url = "https://wpengine.com/wp-content/uploads/2021/05/optimize-images-768x511.jpg",
        keywords = listOf("Tesla", "Autopilot", "AI"),
        language = "en",
        link = "https://example.com/tesla-autopilot-update",
        pubDate = "2025-07-23",
        pubDateTZ = "GMT",
        sentiment = "Positive",
        sentiment_stats = "88% Positive",
        source_icon = "https://example.com/icons/tesla.png",
        source_id = "tesla-news",
        source_name = "Tesla Blog",
        source_priority = 6,
        source_url = "https://www.tesla.com/blog",
        title = "Tesla Updates Full Self Driving",
        video_url = null
    ),
    NewsArticle(
        ai_org = "Amazon",
        ai_region = "Global",
        ai_tag = "Alexa, AI",
        article_id = "007",
        category = listOf("Technology", "Smart Home"),
        content = "Amazon Alexa now supports custom AI models.",
        country = listOf("US"),
        createdAt = Timestamp.now(),
        creator = listOf("Robert White"),
        description = "Developers can integrate their own AI models into Alexa.",
        duplicate = false,
        image_url = "https://wpengine.com/wp-content/uploads/2021/05/optimize-images-768x511.jpg",
        keywords = listOf("Amazon", "Alexa", "AI"),
        language = "en",
        link = "https://example.com/amazon-alexa-ai",
        pubDate = "2025-07-22",
        pubDateTZ = "GMT",
        sentiment = "Neutral",
        sentiment_stats = "70% Positive",
        source_icon = "https://example.com/icons/amazon.png",
        source_id = "amazon-news",
        source_name = "Amazon Developer Blog",
        source_priority = 7,
        source_url = "https://developer.amazon.com/blogs",
        title = "Amazon Alexa Gets Custom AI Models",
        video_url = null
    ),
    NewsArticle(
        ai_org = "Netflix",
        ai_region = "Global",
        ai_tag = "AI, Streaming",
        article_id = "008",
        category = listOf("Entertainment", "AI"),
        content = "Netflix adds AI-powered recommendations for better personalization.",
        country = listOf("US"),
        createdAt = Timestamp.now(),
        creator = listOf("Sophia Green"),
        description = "AI enhances movie suggestions on the platform.",
        duplicate = false,
        image_url = "https://wpengine.com/wp-content/uploads/2021/05/optimize-images-768x511.jpg",
        keywords = listOf("Netflix", "AI", "Recommendations"),
        language = "en",
        link = "https://example.com/netflix-ai-recommendations",
        pubDate = "2025-07-21",
        pubDateTZ = "GMT",
        sentiment = "Positive",
        sentiment_stats = "85% Positive",
        source_icon = "https://example.com/icons/netflix.png",
        source_id = "netflix-news",
        source_name = "Netflix Tech Blog",
        source_priority = 8,
        source_url = "https://netflixtechblog.com",
        title = "Netflix Improves Recommendations with AI",
        video_url = null
    ),
    NewsArticle(
        ai_org = "Twitter",
        ai_region = "Global",
        ai_tag = "Social Media, AI",
        article_id = "009",
        category = listOf("Technology", "Social Media"),
        content = "Twitter tests AI content moderation tools.",
        country = listOf("US"),
        createdAt = Timestamp.now(),
        creator = listOf("Liam Harris"),
        description = "AI tools aim to reduce harmful content.",
        duplicate = false,
        image_url = "https://wpengine.com/wp-content/uploads/2021/05/optimize-images-768x511.jpg",
        keywords = listOf("Twitter", "AI", "Moderation"),
        language = "en",
        link = "https://example.com/twitter-ai-moderation",
        pubDate = "2025-07-20",
        pubDateTZ = "GMT",
        sentiment = "Neutral",
        sentiment_stats = "65% Neutral",
        source_icon = "https://example.com/icons/twitter.png",
        source_id = "twitter-news",
        source_name = "Twitter Blog",
        source_priority = 9,
        source_url = "https://blog.twitter.com",
        title = "Twitter Tests AI Moderation",
        video_url = null
    ),
    NewsArticle(
        ai_org = "Spotify",
        ai_region = "Global",
        ai_tag = "AI, Music",
        article_id = "010",
        category = listOf("Entertainment", "AI"),
        content = "Spotify launches AI-generated playlists.",
        country = listOf("US"),
        createdAt = Timestamp.now(),
        creator = listOf("Olivia Martin"),
        description = "AI can now create mood-based playlists automatically.",
        duplicate = false,
        image_url = "https://wpengine.com/wp-content/uploads/2021/05/optimize-images-768x511.jpg",
        keywords = listOf("Spotify", "AI", "Music"),
        language = "en",
        link = "https://example.com/spotify-ai-playlists",
        pubDate = "2025-07-19",
        pubDateTZ = "GMT",
        sentiment = "Positive",
        sentiment_stats = "92% Positive",
        source_icon = "https://example.com/icons/spotify.png",
        source_id = "spotify-news",
        source_name = "Spotify Newsroom",
        source_priority = 10,
        source_url = "https://newsroom.spotify.com",
        title = "Spotify Introduces AI Playlists",
        video_url = null
    )
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(device = "id:pixel_9_pro")
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ForYouPage() {
    val pagerState = rememberPagerState(pageCount = { sampleNewsList.size })
    var selectedIndex by remember { mutableIntStateOf(0) }

    Surface {
        Scaffold(
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
                    VerticalCarouselItem(
                        news = sampleNewsList[page],
                        modifier = Modifier.carouselTransition(page = page, pagerState = pagerState)
                    )
                }
                Box(
                    modifier = Modifier
                        .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 120.dp)
                        .fillMaxWidth()
                        .background(brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
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
                                MaterialTheme.colorScheme.surface
                            ),
                        )),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VerticalCarouselItem(news: NewsArticle, modifier: Modifier) {
    Card(
        modifier = modifier.fillMaxSize()
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
                    text = news.content ?: "",
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
