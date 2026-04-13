package com.shanudevcodes.newsbits.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.shanudevcodes.newsbits.AuthenticationActivity
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.HomeDestination
import com.shanudevcodes.newsbits.data.firebase.FirebaseEvent
import com.shanudevcodes.newsbits.data.firebase.FirebaseViewModel
import com.shanudevcodes.newsbits.ui.screens.home.openUrlInBrowser
import com.shanudevcodes.newsbits.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    newsViewModel: NewsViewModel
){
    val firebaseViewModel: FirebaseViewModel = hiltViewModel()
    val user by firebaseViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    var showEditNameDialog by remember(user) { mutableStateOf(false) }
    var newUserName by remember(user) { mutableStateOf(user?.displayName ?: "") }
    var currentDisplayName by remember(user) { mutableStateOf(user?.displayName ?: "Guest") }

    val profileScreenItemList = listOf(
        ProfileScreenData(
            name = "Settings",
            icon = Icons.Outlined.Settings,
            description = "Change your preferences",
            onClick = {
                navController.navigate(HomeDestination.SETTINGS) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = false
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                newsViewModel.resetCurrentLink()
            }
        ),
        ProfileScreenData(
            name = "Rate us",
            icon = Icons.Outlined.StarRate,
            description = "Rate the app",
            onClick = {

            }
        ),
        ProfileScreenData(
            name = "Share",
            icon = Icons.Outlined.IosShare,
            description = "Share the app",
            onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "https://github.com/ShanuDevCodes/NewsBits/releases")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share NewsBits")
                context.startActivity(shareIntent)
            }
        ),
        ProfileScreenData(
            name = "Help Center",
            icon = Icons.AutoMirrored.Outlined.Help,
            description = "Get help",
            onClick = {

            }
        ),
        ProfileScreenData(
            name = "About",
            icon = Icons.Outlined.Info,
            description = "About the app",
            onClick = {
                openUrlInBrowser(context,"https://github.com/ShanuDevCodes/NewsBits")
            }
        ),
        ProfileScreenData(
            name = "Github",
            iconId = R.drawable.github,
            description = "Developer's github profile",
            onClick = {
                openUrlInBrowser(context,"https://github.com/ShanuDevCodes")
            }
        )
    )
    LaunchedEffect(Unit) {
        firebaseViewModel.onEvent(FirebaseEvent.ReloadUser)
    }
    
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Change Username") },
            text = {
                OutlinedTextField(
                    value = newUserName,
                    onValueChange = { newUserName = it },
                    label = { Text("Username") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newUserName.isNotBlank()) {
                            firebaseViewModel.onEvent(FirebaseEvent.SetUserName(newUserName))
                            firebaseViewModel.onEvent(FirebaseEvent.UpdateUserName)
                            currentDisplayName = newUserName
                            showEditNameDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Surface {
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            topBar = {
                Row(
                    modifier = Modifier
                        .padding(
                            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                            start = 12.dp,
                            end = 12.dp
                        )
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.displayMediumEmphasized
                    )
                }
            },
        ) {innerPadding->
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp)
            ){
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable(
                                        onClick = {
                                            newUserName = user?.displayName ?: ""
                                            showEditNameDialog = true
                                        }
                                    )
                                    .padding(16.dp)
                                    .fillMaxWidth()
                                    .height(56.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(shape = CircleShape)
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.person),
                                        contentDescription = "Profile Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = currentDisplayName,
                                        style = MaterialTheme.typography.titleLargeEmphasized,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = user?.email?:"Anonymous",
                                        style = MaterialTheme.typography.bodySmallEmphasized,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        newUserName = user?.displayName ?: ""
                                        showEditNameDialog = true
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = "Edit Profile",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    itemsIndexed(profileScreenItemList){index, item->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            modifier = Modifier
                                .padding(bottom = 2.dp),
                            shape = RoundedCornerShape(
                                topStart = if (index == 0) {
                                    24.dp
                                }else {
                                    4.dp
                                },
                                topEnd = if (index == 0) {
                                    24.dp
                                }else {
                                    4.dp
                                },
                                bottomStart = if (index == profileScreenItemList.lastIndex) {
                                    24.dp
                                }else {
                                    4.dp
                                },
                                bottomEnd = if (index == profileScreenItemList.lastIndex) {
                                    24.dp
                                }else {
                                    4.dp
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable(
                                        onClick = item.onClick
                                    )
                                    .padding(16.dp)
                                    .fillMaxWidth()
                                    .height(56.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (item.icon != null) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.name,
                                        modifier = Modifier.size(32.dp),
                                        tint = if (item.name == "Logout") {
                                            Color(0xFFB32727)
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }else if(item.iconId != null){
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = item.iconId),
                                        contentDescription = item.name,
                                        modifier = Modifier.size(32.dp),
                                        tint = if (item.name == "Logout") {
                                            Color(0xFFB32727)
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleMediumEmphasized,
                                        fontWeight = FontWeight.Bold,
                                        color = if(item.name == "Logout"){
                                            Color(0xFFB32727)
                                        }else{
                                            MaterialTheme.colorScheme.primary
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmallEmphasized,
                                        color = if(item.name == "Logout"){
                                            MaterialTheme.colorScheme.onErrorContainer
                                        }else{
                                            MaterialTheme.colorScheme.secondary
                                        }
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        item.onClick()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = "Edit Profile",
                                        tint = if(item.name == "Logout"){
                                            Color(0xFFB32727)
                                        }else{
                                            MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            modifier = Modifier
                                .padding(bottom = 2.dp),
                            shape = RoundedCornerShape(
                                size = 24.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable(
                                        onClick = {
                                            if (user != null) {
                                                firebaseViewModel.onEvent(FirebaseEvent.LogoutUser)
                                            }else{
                                                context.startActivity(Intent(context, AuthenticationActivity::class.java)
                                                    .putExtra("showGuestSignup",false)
                                                )
                                            }
                                        }
                                    )
                                    .padding(16.dp)
                                    .fillMaxWidth()
                                    .height(56.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = "Logout",
                                    modifier = Modifier.size(32.dp),
                                    tint = Color(0xFFB32727)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = if (user != null){
                                            "Logout"
                                        }else{
                                            "Login"
                                        },
                                        style = MaterialTheme.typography.titleMediumEmphasized,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB32727)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (user != null){
                                            "Logout from the app"
                                        }else{
                                            "Login to the app"
                                        },
                                        style = MaterialTheme.typography.bodySmallEmphasized,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        if (user != null){
                                        firebaseViewModel.onEvent(FirebaseEvent.LogoutUser)
                                        }else{
                                            context.startActivity(Intent(context, AuthenticationActivity::class.java)
                                                .putExtra("showGuestSignup",false)
                                            )
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = "Edit Profile",
                                        tint = Color(0xFFB32727)

                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        Spacer(
                            modifier = Modifier.height(
                                WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding() + 54.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

data class ProfileScreenData(
    val name: String,
    val icon: ImageVector? = null,
    val iconId: Int? = null,
    val description: String,
    val onClick: () -> Unit = {}
)