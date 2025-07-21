package com.shanudevcodes.newsbits.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberNetworkStatus(): ConnectivityObserver.Status {
    val context = LocalContext.current
    val observer = remember { NetworkConnectivityObserver(context) }
    val status by observer.observe().collectAsState(
        initial = ConnectivityObserver.Status.Available
    )
    return status
}