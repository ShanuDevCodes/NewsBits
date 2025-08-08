package com.shanudevcodes.newsbits.ui.screens.authentication

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shanudevcodes.newsbits.AuthenticationActivity
import com.shanudevcodes.newsbits.CategorySelectionActivity
import com.shanudevcodes.newsbits.data.DataStoreManager
import com.shanudevcodes.newsbits.data.firebase.FirebaseEvent
import com.shanudevcodes.newsbits.data.firebase.FirebaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EmailVerificationScreen(
    dataStore: DataStoreManager,
){
    val firebaseViewModel: FirebaseViewModel = viewModel()
    val currentUser by firebaseViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    var emailVerificationTimer by rememberSaveable { mutableStateOf(90) }
    var isTimerRunning by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (isTimerRunning && emailVerificationTimer > 0) {
                delay(1000)
                emailVerificationTimer--
            }
            isTimerRunning = false
        }else{
            emailVerificationTimer = 90
        }
    }
    LaunchedEffect(Unit) {
        val isFirstLaunch = dataStore.firstLaunch.first()
        firebaseViewModel.onEvent(FirebaseEvent.SendEmailVerification)
        while (isActive) {
            firebaseViewModel.onEvent(FirebaseEvent.ReloadUser)
            if (currentUser == null) {
                return@LaunchedEffect
            }
            if (currentUser?.isEmailVerified == true) {
                break
            }
            delay(20)
        }
        if (isFirstLaunch) {
            dataStore.setFirstLaunch(false)
            context.startActivity(Intent(context, CategorySelectionActivity::class.java))
        }
        (context as? AuthenticationActivity)?.finish()
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceDim
    ) {innerPadding->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator()
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Verification Indicator",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = "Verifying Email",
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = "We have sent a verification link to your email. Click on the link to verify your email",
                    style = MaterialTheme.typography.bodyMediumEmphasized,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Didn't receive the email?",
                        style = MaterialTheme.typography.bodySmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Resend",
                        style = MaterialTheme.typography.bodySmallEmphasized,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable(
                            onClick = {
                                if (isTimerRunning){
                                    Toast.makeText(context, "Wait for $emailVerificationTimer sec before resending the email", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context, "Email resent", Toast.LENGTH_SHORT).show()
                                    isTimerRunning = true
                                }
                            }
                        )
                    )
                }
            }
            Text(
                text = "Note : Please check your spam folder",
                style = MaterialTheme.typography.bodySmallEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}