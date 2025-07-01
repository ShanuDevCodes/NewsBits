package com.shanudevcodes.newsbits.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shanudevcodes.newsbits.R

@Preview
@Composable
fun EmptyScreen(){
    Scaffold {innerPadding->
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopStart
            ) {
                VerticalDivider(
                    modifier = Modifier
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.news_icon),
                    contentDescription = "News Icon",
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Text(
                    text = "No news to show",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Try opening a News from the list",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "News Bits",
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Powered by NewsData.io",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}