package com.shanudevcodes.newsbits.ui.screens.bookmark

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.formatDateString
import com.shanudevcodes.newsbits.data.savedarticledb.data.entity.SavedArticle
import com.shanudevcodes.newsbits.data.savedarticledb.data.mapper.toEntity
import com.shanudevcodes.newsbits.data.savedarticledb.data.roomdatabase.AppDatabase
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.events.RoomEvents
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal.RoomViewModel
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal.RoomViewModelFactory
import com.shanudevcodes.newsbits.ui.screens.home.openUrlInBrowser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.TimeZone

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BookmarkDetailScreen(
    newsId: String,
    navController: NavHostController
){
    val scope = rememberCoroutineScope()
    val isDialogVisible = rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val dao = db.RoomDao()
    val roomViewModel: RoomViewModel = viewModel(
        factory = RoomViewModelFactory(dao)
    )
    val viewModelState = roomViewModel.state.collectAsState()
    val newsArticle = viewModelState.value.article
    val isBookMarked = viewModelState.value.isArticleSaved
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )
    val lazyListState = rememberLazyListState()
    val isAtTop = remember { derivedStateOf { lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0 } }
    val screenHeightDp = configuration.screenHeightDp.dp // Screen height in dp
    val peekHeight = screenHeightDp
    val screenWidthDp = configuration.screenWidthDp.dp
    val timeZoneAbbreviation = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
    LaunchedEffect(newsId) {
        roomViewModel.onEvent(RoomEvents.GetArticleById(newsId))
    }

    LaunchedEffect(viewModelState.value.article?.article_id) {
        val articleId = viewModelState.value.article?.article_id
        if (!articleId.isNullOrEmpty()) {
            roomViewModel.onEvent(RoomEvents.CheckArticleSaved(articleId))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        BottomSheetScaffold(
            sheetSwipeEnabled = isAtTop.value,
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
                                text = newsArticle?.title?:"",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMediumEmphasized,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }

                        IconButton(
                            onClick = {
                                isDialogVisible.value = true
                                Log.d("BookmarkToggle", "Clicked! isBookMarked = $isBookMarked")
                                roomViewModel.onEvent(RoomEvents.CheckArticleSaved(newsArticle?.article_id?:""))
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
                            text = newsArticle?.source_name?:"",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDateString(newsArticle?.pubDate?:""),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " $timeZoneAbbreviation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    BottomSheetContentBookMarked(newsArticle?.toEntity(), lazyListState)
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
                    painter = if (newsArticle?.image_url != null) rememberAsyncImagePainter(model = newsArticle.image_url) else painterResource(R.drawable.img_6),
                    contentDescription = newsArticle?.source_id,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 20.dp
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + 8.dp, end = 8.dp, start = 8.dp
            ),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, newsArticle?.link ?: "")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share this news")
                    context.startActivity(shareIntent)
                },
                elevation = FloatingActionButtonDefaults.elevation(2.dp),
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ExtendedFloatingActionButton(
                onClick = {
                    openUrlInBrowser(context, newsArticle?.link?:"")
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
    if (isDialogVisible.value) {
        DeleteWarningDialogBox(
            onDismissRequest = {
                isDialogVisible.value = false
            },
            onConfirm = {
                scope.launch {
                    navController.popBackStack()
                    delay(300)
                    roomViewModel.onEvent(
                        RoomEvents.DeleteArticleById(
                            articleId = newsArticle?.article_id ?: ""
                        )
                    )
                }
                isDialogVisible.value = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContentBookMarked(news: SavedArticle?, listState: LazyListState){
    val scrollInterop = rememberNestedScrollInteropConnection()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LazyRow {
                itemsIndexed(news?.category?.split(", ")?.filter { it.isNotBlank() }?: emptyList()){index, category ->
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = {
                            Text(text = category)
                        },
                        shape = RoundedCornerShape(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            if (news?.description != null) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .nestedScroll(scrollInterop)
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    item {
                        Text(
                            text = news.description,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.fillMaxWidth(),
                            softWrap = true,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()+ 126.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier.heightIn(max = 600.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(modifier = Modifier.height(500.dp))
                    Text(
                        text = "No description available",
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteWarningDialogBox(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning Icon",
                tint = Color(0xFFB32727)
            )
        },
        title = {
            Text(text = "Delete Bookmark")
        },
        text = {
            Text(
                text = "Are you sure you want to delete the bookmark? This action is irreversible.",
                textAlign = TextAlign.Center
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    onConfirm()
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFFB32727), // Material Red 700
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Confirm",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(
                    text = "Cancel"
                )
            }
        }
    )
}