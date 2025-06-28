package com.shanudevcodes.newsbits.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.navigation.NavHostController
import com.shanudevcodes.newsbits.data.News
import com.shanudevcodes.newsbits.data.NewsList
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(navController: NavHostController,scrollBehavior: SearchBarScrollBehavior) {
    val baseItems = NewsList


    // Create a large repeated list AFTER the base items
    val repeatFactor = 100  // Enough for looping
    val items = List(baseItems.size * repeatFactor + baseItems.size) {
        baseItems[it % baseItems.size]
    }

    val state = rememberCarouselState { items.size }
    val animationScope = rememberCoroutineScope()
    val options = listOf(
        "All",
        "Science",
        "Health",
        "Culture",
        "Technology",
        "Business",
        "Sports",
        "Politics",
        "Entertainment",
        "Lifestyle",
        "Food",
        "Travel",
        "Art",
    )
    var selectedIndex by remember { mutableIntStateOf(0) }

    // ✅ Start at the true beginning (index 0, img_1)
    LaunchedEffect(Unit) {
        state.scrollToItem(0)
    }

    LazyColumn(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Latest News",
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
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            HorizontalCenteredHeroCarousel(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                itemSpacing = 8.dp,
            ) { index ->
                val currentItem = items[index]
                val activeIndex = state.currentItem

                val offsetFraction = (index - activeIndex).toFloat().coerceIn(-1f, 1f).absoluteValue

                val targetAlpha = (1f - offsetFraction).coerceIn(0f, 1f)
                val animatedAlpha by animateFloatAsState(
                    targetValue = targetAlpha,
                    animationSpec = tween(durationMillis = 500),
                    label = "carouselAlpha"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(205.dp),
                ) {
                    // Image with rounded corners
                    Image(
                        painter = painterResource(currentItem.imageResId),
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
                            text = currentItem.headline,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 2,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentItem.writer, // mock writer
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = currentItem.timeAgo, // mock time
                                color = MaterialTheme.colorScheme.primary, // soft orange
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                itemsIndexed(options) { index, label ->
                    ToggleButton(
                        checked = selectedIndex == index,
                        onCheckedChange = { selectedIndex = index },
//                    shapes =
//                        when (index) {
//                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
//                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
//                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
//                        },
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
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

        itemsIndexed (items) { index, news ->
            NewsListItem(news = news)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@Composable
fun NewsListItem(news: News) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail Image (landscape rectangle)
        Image(
            painter = painterResource(news.imageResId),
            contentDescription = news.contentDescription,
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
                text = news.headline,
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
                    text = news.writer,
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
                        text = "Innovation", // You can make this dynamic
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = news.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


