package com.shanudevcodes.newsbits.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.data.GeminiSummaryType
import com.shanudevcodes.newsbits.ui.animation.LottieLoader
import com.shanudevcodes.newsbits.viewmodel.AiViewModel
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AiBottomSheetContent(
    aiViewModel: AiViewModel = viewModel()
){
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val newsViewModel: NewsViewModel = viewModel()
    val topNews by newsViewModel.topNews.collectAsState()
    val isFetched by aiViewModel.isResponseFetched.collectAsState()
    newsViewModel.loadTopNews()
    val summaryType by dataStore.geminiSummaryTypeFlow.collectAsState(initial = GeminiSummaryType.CONCISE)
    LaunchedEffect(topNews) {
        if (topNews.isNotEmpty() && !isFetched) {
            val geminiSummaryType = dataStore.geminiSummaryTypeFlow.first()
            aiViewModel.getGeminiResponse(topNews,geminiSummaryType)
        }
    }
    val summary by aiViewModel.geminiResponse.collectAsState()
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.sparkler),
                contentDescription = "AI",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AI Top News Summary",
                style = MaterialTheme.typography.titleSmallEmphasized,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Box {
                IconButton(
                    onClick = {
                        isDropdownExpanded = true
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "AI Mode",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                DropdownMenu(
                    shape = RoundedCornerShape(24.dp),
                    expanded = isDropdownExpanded,
                    onDismissRequest = {
                        isDropdownExpanded = false
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .align(Alignment.Center),
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Summary Style",
                        style = MaterialTheme.typography.titleSmallEmphasized,
                        modifier = Modifier.padding(start = 16.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ){
                                RadioButton(
                                    selected = summaryType == GeminiSummaryType.CONCISE,
                                    onClick = {
                                        scope.launch {
                                            dataStore.setGeminiType(GeminiSummaryType.CONCISE)
                                            aiViewModel.resetIsResponseFetched()
                                            val geminiSummaryType = dataStore.geminiSummaryTypeFlow.first()
                                            aiViewModel.getGeminiResponse(topNews,geminiSummaryType)
                                        }
                                    }
                                )
                                Text(
                                    text = "Concise",
                                    style = MaterialTheme.typography.bodySmallEmphasized,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        onClick = {
                            scope.launch {
                                dataStore.setGeminiType(GeminiSummaryType.CONCISE)
                                aiViewModel.resetIsResponseFetched()
                                val geminiSummaryType = dataStore.geminiSummaryTypeFlow.first()
                                aiViewModel.getGeminiResponse(topNews, geminiSummaryType)
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ){
                                RadioButton(
                                    selected = summaryType == GeminiSummaryType.DETAILED,
                                    onClick = {
                                        scope.launch {
                                            dataStore.setGeminiType(GeminiSummaryType.DETAILED)
                                            aiViewModel.resetIsResponseFetched()
                                            val geminiSummaryType = dataStore.geminiSummaryTypeFlow.first()
                                            aiViewModel.getGeminiResponse(topNews,geminiSummaryType)
                                        }
                                    }
                                )
                                Text(
                                    text = "Detailed",
                                    style = MaterialTheme.typography.bodySmallEmphasized,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        onClick = {
                            scope.launch {
                                dataStore.setGeminiType(GeminiSummaryType.DETAILED)
                                aiViewModel.resetIsResponseFetched()
                                val geminiSummaryType = dataStore.geminiSummaryTypeFlow.first()
                                aiViewModel.getGeminiResponse(topNews, geminiSummaryType)
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isFetched) {
//                    AnimatedVisibility(
//                        visible = true
//                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            item {
                                Spacer(Modifier.height(8.dp))
                            }
                            item {
                                Text(
                                    text = when(summary.responseType){
                                        GeminiSummaryType.CONCISE -> "Concise"
                                        GeminiSummaryType.DETAILED -> "Detailed"
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.bodySmallEmphasized,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            item {
                                Spacer(Modifier.height(8.dp))
                            }
                            items(summary.Responses) {
                                Text(
                                    text = "- " + it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                                )
                            }
                        }
//                    }
                }else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LottieLoader(true)
                        FadingSummarisingText()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FadingSummarisingText(
    text: String = "Summarising Top News...",
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fade")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnim"
    )

    Text(
        text = text,
        style = MaterialTheme.typography.titleSmallEmphasized,
        color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
    )
}