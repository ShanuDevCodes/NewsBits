package com.shanudevcodes.newsbits.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    navController: NavHostController
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
    var categoriesExpanded by remember { mutableStateOf(false) }
    var isDropDownEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val themeOption by dataStore.themeFlow.collectAsState(initial = ThemeOptions.SYSTEM_DEFAULT)
    val dynamicColor by dataStore.dynamicColorFlow.collectAsState(initial = false)
    val preference by dataStore.categoryPreferenceFlow.collectAsState(initial = null)
    val categoryList = remember(preference) {
        preference?.split(" ")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    }
    val displayPreference = categoryList.sorted().joinToString(", ")
    val scope = rememberCoroutineScope()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceDim,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        text = "Settings",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.titleLargeEmphasized
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {innerPadding->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(bottom = 2.dp),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                        onClick = {
                            scope.launch {
                                dataStore.setDynamicColor(!dynamicColor)
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ){
                                Text(
                                    text = "Use Dynamic Colors",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLargeEmphasized,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Switch(
                                    checked = dynamicColor,
                                    onCheckedChange = {
                                        scope.launch {
                                            dataStore.setDynamicColor(!dynamicColor)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(bottom = 2.dp),
                        shape = RoundedCornerShape(4.dp),
                        onClick = {
                            isDropDownEnabled = true
                        }
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
                                        text = "Choose App Theme",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelLargeEmphasized
                                    )
                                    Text(
                                        text = themeOption.name,
                                        style = MaterialTheme.typography.bodySmallEmphasized,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Box() {
                                    IconButton(
                                        onClick = {
                                            isDropDownEnabled = true
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Change Theme"
                                        )
                                    }
                                    DropdownMenu(
                                        shape = RoundedCornerShape(24.dp),
                                        expanded = isDropDownEnabled,
                                        onDismissRequest = {
                                            isDropDownEnabled = false
                                        },
                                        modifier = Modifier
                                            .width(200.dp)
                                            .align(Alignment.CenterEnd),
                                    ) {
                                        ThemeOptions.entries.forEach {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = it.name,
                                                        style = MaterialTheme.typography.bodySmallEmphasized,
                                                        modifier = Modifier.padding(start = 16.dp),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                },
                                                onClick = {
                                                    scope.launch {
                                                        dataStore.saveThemeOption(it)
                                                        isDropDownEnabled = false
                                                    }
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 2.dp),
                        shape = RoundedCornerShape(4.dp),
                        onClick = {
                            categoriesExpanded = !categoriesExpanded
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Interested Categories",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelLargeEmphasized,
                                    )
                                    Text(
                                        text = displayPreference.ifBlank { "None" },
                                        style = MaterialTheme.typography.bodySmallEmphasized,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        categoriesExpanded = !categoriesExpanded
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (categoriesExpanded)Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = "Change Theme"
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = categoriesExpanded
                            ) {
                                Column {
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
                                            options.forEach { text ->
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
                        }
                    }
                }
                item {
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

                                    },
                                    modifier = Modifier.size(24.dp)
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
            }
        }
    }
}