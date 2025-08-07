package com.shanudevcodes.newsbits.ui.screens.authentication

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shanudevcodes.newsbits.AuthenticationActivity
import com.shanudevcodes.newsbits.MainActivity
import com.shanudevcodes.newsbits.R
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.data.firebase.FirebaseEvent
import com.shanudevcodes.newsbits.data.firebase.FirebaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginScreen(
    showGuestSignup: Boolean = false,
    dataStore: DataStoreManager,
    onSignupClick: () -> Unit
) {
    val firebaseViewModel: FirebaseViewModel = viewModel()
    val firebaseState by firebaseViewModel.state.collectAsState()
    val onEvent: (FirebaseEvent) -> Unit = firebaseViewModel::onEvent
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isGuestLoading by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.surfaceDim
    ) {innerPadding->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Log In",
                    style = MaterialTheme.typography.headlineLargeEmphasized,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Login to your account to get started",
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    enabled = !isLoading && !isGuestLoading,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                        )
                    },
                    value = firebaseState.email,
                    onValueChange = {
                        onEvent(FirebaseEvent.SetUserEmail(it))
                    },
                    shape = CircleShape,
                    placeholder = {
                        Text(
                            text = "Email Address",
                            style = MaterialTheme.typography.bodyMediumEmphasized,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        focusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    enabled = !isLoading && !isGuestLoading,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                        )
                    },
                    value = firebaseState.password,
                    onValueChange = {
                        onEvent(FirebaseEvent.SetUserPassword(it))
                    },
                    shape = CircleShape,
                    placeholder = {
                        Text(
                            text = "Password",
                            style = MaterialTheme.typography.bodyMediumEmphasized,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        focusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        val description =
                            if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    enabled = !isLoading && !isGuestLoading,
                    onClick = {
                        if (firebaseState.password.isNotBlank() && firebaseState.email.isNotBlank()) {
                            onEvent(FirebaseEvent.LoginUser)
                            scope.launch {
                                if (dataStore.firstLaunch.first()) {
                                    isLoading = true
                                    delay(500L)
                                    dataStore.setFirstLaunch(false)
                                    context.startActivity(
                                        Intent(
                                            context,
                                            MainActivity::class.java
                                        )
                                    )
                                    (context as? AuthenticationActivity)?.finish()
                                } else {
                                    isLoading = true
                                    delay(500L)
                                    (context as? AuthenticationActivity)?.finish()
                                }
                            }
                        }else{
                            Toast.makeText(
                                context,
                                "Please fill all the fields.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                ) {
                    if (!isLoading) {
                        Text(
                            text = "Login"
                        )
                    }else{
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LoadingIndicator(

                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Logging In...",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account?",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        enabled = !isLoading && !isGuestLoading,
                        onClick = {
                            onSignupClick()
                        }
                    ) {
                        Text(
                            text = "Signup",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    HorizontalDivider(
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "OR",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    if (showGuestSignup) {
                        Button(
                            enabled = !isLoading && !isGuestLoading,
                            onClick = {
                                scope.launch {
                                    if (dataStore.firstLaunch.first()) {
                                        isGuestLoading = true
                                        delay(500L)

                                        dataStore.setFirstLaunch(false)
                                        context.startActivity(
                                            Intent(
                                                context,
                                                MainActivity::class.java
                                            )
                                        )
                                        (context as? AuthenticationActivity)?.finish()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            if (!isGuestLoading) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Lock"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Guest Signup",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }else{
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LoadingIndicator(
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Loading...",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Button(
                        enabled = !isLoading && !isGuestLoading,
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.google),
                            contentDescription = "Google",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (!showGuestSignup) {
                IconButton(
                    enabled = !isLoading,
                    onClick = {
                        scope.launch {
                            (context as? AuthenticationActivity)?.finish()
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.padding(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
            }
        }
    }
}