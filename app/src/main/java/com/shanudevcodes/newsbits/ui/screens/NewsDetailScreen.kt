package com.shanudevcodes.newsbits.ui.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.News
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.data.NewsList
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewsDetailScreen(newsIndex: Int,navController: NavHostController,viewModel: NewsViewModel) {
    val allNews by viewModel.allNews.collectAsState()

    // Safe access to news item
    val news: NewsArticle? = allNews.getOrNull(newsIndex)

    if (news == null) {
        // Show error state when news isn't available
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("News content not available", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    var isBookMarked by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )
    val screenHeightDp = configuration.screenHeightDp.dp // Screen height in dp
    val peekHeight = screenHeightDp
    val screenWidthDp = configuration.screenWidthDp.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        BottomSheetScaffold(
            topBar = {},
            scaffoldState = scaffoldState,
            sheetMaxWidth = screenWidthDp,
            sheetPeekHeight = if (isPortrait) (peekHeight * 0.75f) else (peekHeight * 0.6f),
            sheetContent = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f), // 👈 Don't fill max width
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = news.title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMediumEmphasized,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }

                        IconButton(
                            onClick = {
                                Log.d("BookmarkToggle", "Clicked! isBookMarked = $isBookMarked")
                                isBookMarked = !isBookMarked
                            }
                        ) {
                            Icon(
                                imageVector = if (isBookMarked) Icons.Filled.BookmarkAdded else Icons.Outlined.BookmarkAdd,
                                contentDescription = "Bookmark",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = news.source_name,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = news.pubDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    BottomSheetContent(news)
                }
            },
        ) {paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 12f)
            ) {
                val overlayColor = MaterialTheme.colorScheme.surface
                Image(
                    painter = if (news.image_url != null) rememberAsyncImagePainter(model = news.image_url) else painterResource(R.drawable.img_6),
                    contentDescription = news.source_id,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()+20.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    overlayColor.copy(alpha = 0.4f),
                                    overlayColor.copy(alpha = 0f)
                                )
                            )
                        )
                )
            }
        }
        if (!isPortrait) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopStart
            ) {
                VerticalDivider(
                    modifier = Modifier
                        .width(5.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(news: NewsArticle){
    val listState = rememberLazyListState()
    val scrollInterop = rememberNestedScrollInteropConnection()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .nestedScroll(scrollInterop)
                .fillMaxSize()
        ){
            item {
                Text(
                    text = news.description?:"",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                )
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
