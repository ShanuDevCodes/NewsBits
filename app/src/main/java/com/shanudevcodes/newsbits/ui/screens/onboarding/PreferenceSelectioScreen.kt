package com.shanudevcodes.newsbits.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shanudevcodes.newsbits.CategorySelectionActivity
import com.shanudevcodes.newsbits.MainActivity
import com.shanudevcodes.newsbits.data.DataStoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PreferenceSelectionScreen(
    dataStore: DataStoreManager
){
    val options = listOf(
        "Top",
        "Business",
        "Crime",
        "Domestic",
        "Education",
        "Entertainment",
        "Environment",
        "Food",
        "Health",
        "Lifestyle",
        "Politics",
        "Science",
        "Sports",
        "Technology",
        "Tourism",
        "World",
        "Other",
    )
    var isLoading by remember{ mutableStateOf(false) }
    val context = LocalContext.current
    val preference by dataStore.categoryPreferenceFlow.collectAsState(initial = null)
    val categoryList = remember(preference) {
        preference?.split(" ")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    }
    val isSelected = categoryList.isNotEmpty()
    val scope = rememberCoroutineScope()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceDim
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ){
            Column {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                    onClick = {

                    },
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Tell us about you interests",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLargeEmphasized
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .fillMaxWidth()
                        ) {
                            FlowRow(
                            ) {
                                options.forEachIndexed { index, text ->
                                    val isSelected = text in categoryList
                                    ToggleButton(
                                        checked = isSelected,
                                        onCheckedChange = { it ->
                                            val string = preference?.split(" ")?.filter { it.isNotBlank() }?.toMutableList() ?: mutableListOf()
                                            if (it) {
                                                if (text !in string) string.add(text)
                                            } else {
                                                string.remove(text)
                                            }
                                            scope.launch {
                                                dataStore.setCategoryPreference(string.joinToString(" "))
                                            }
                                        },
                                        colors = ToggleButtonDefaults.toggleButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            checkedContainerColor = MaterialTheme.colorScheme.primary,
                                        ),
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Text(
                                            text = text,
                                            style = MaterialTheme.typography.labelSmallEmphasized,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 2.dp),
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
                    onClick = {}
                ){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ){
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Region",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLargeEmphasized
                                )
                                Text(
                                    text = "Global",
                                    style = MaterialTheme.typography.bodySmallEmphasized,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = {

                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Change Region"
                                )
                            }
                        }
                    }
                }
            }
            ExtendedFloatingActionButton(
                expanded = true,
                onClick = {
                    isLoading = true
                    scope.launch {
                        delay(500L)

                        dataStore.setCategorySelectionOnboardingComplete(true)

                        context.startActivity(
                            android.content.Intent(
                                context,
                                MainActivity::class.java
                            )
                        )
                        (context as CategorySelectionActivity).finish()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                elevation = FloatingActionButtonDefaults.elevation(
                    0.dp,
                    pressedElevation = 0.dp
                ),
                icon = {
                    if (isSelected) {
                        if (!isLoading) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Done",
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            LoadingIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }else{
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Skip",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                text = {
                    if (isSelected) {
                        Text(
                            text = "Done"
                        )
                    }else{
                        Text(
                            text = "Skip"
                        )
                    }
                }
            )
        }
    }
}