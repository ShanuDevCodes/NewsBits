package com.shanudevcodes.newsbits.ui.screens.home

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.formatDateString
import com.shanudevcodes.newsbits.data.shimmerEffect
import com.shanudevcodes.newsbits.viewModel.SearchResultDetailScreenViewModel
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchResultDetailScreen(
    navController: NavHostController,
    link: String
){
    val viewModel: SearchResultDetailScreenViewModel = viewModel()
    val news by viewModel.news.collectAsState()
    val isNewsFetched by viewModel.isNewsFetched.collectAsState()
    viewModel.fetchNews(link)
    val context = LocalContext.current
    val scaffoldState = rememberBottomSheetScaffoldState()
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp // Screen height in dp
    val peekHeight = screenHeightDp
    val screenWidthDp = configuration.screenWidthDp.dp
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val timeZoneAbbreviation = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        BottomSheetScaffold(
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
                                text = news?.title?:"",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMediumEmphasized,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }

                        IconButton(
                            onClick = {
//                                Log.d("BookmarkToggle", "Clicked! isBookMarked = $isBookMarked")
//                                if (isBookMarked){
//                                    roomViewModel.onEvent(
//                                        RoomEvents.DeleteArticle(
//                                            article = newsArticle
//                                        )
//                                    )
//                                }else {
//                                    roomViewModel.onEvent(
//                                        RoomEvents.SaveArticle(
//                                            article = newsArticle
//                                        )
//                                    )
//                                }
//                                roomViewModel.onEvent(RoomEvents.CheckArticleSaved(newsArticle.article_id))
                            }
                        ) {
                            Icon(
                                imageVector = /*if (isBookMarked) Icons.Filled.BookmarkAdded else*/ Icons.Outlined.BookmarkAdd,
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
                            text = news?.source_name?:"",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDateString(news?.pubDate?:""),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " $timeZoneAbbreviation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (isNewsFetched) {
                        BottomSheetContent(news)
                    }else{
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                .fillMaxWidth()
                                .height(300.dp)
                                .shimmerEffect()
                        )
                    }
                }
            },
        ) { paddingValues ->
            if (isNewsFetched) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 12f)
                ) {
                    val overlayColor = MaterialTheme.colorScheme.surface
                    Image(
                        painter = if (news?.image_url != null) rememberAsyncImagePainter(
                            model = news?.image_url ?: ""
                        ) else painterResource(R.drawable.img_6),
                        contentDescription = news?.source_id ?: "",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                WindowInsets.statusBars.asPaddingValues()
                                    .calculateTopPadding() + 20.dp
                            )
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
        }
    }
    if (isNewsFetched) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + 8.dp, end = 8.dp, start = 8.dp
                ),
            contentAlignment = Alignment.BottomEnd
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    openUrlInBrowser(context, news?.link ?: "")
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.LocalLibrary, // your icon
                        contentDescription = "Read Article"
                    )
                },
                text = { Text(text = "Read Full Article") },
                elevation = FloatingActionButtonDefaults.elevation(2.dp)
            )
        }
    }
}