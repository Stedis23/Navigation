package com.stedis.samples.screens.messenger

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.ComposeScreen
import com.stedis.navigation.compose.LocalNavigationManager
import com.stedis.navigation.compose.rememberCurrentDestination
import com.stedis.samples.R

@Composable
fun MessengerFeedScreen() {
    val navigationManager = LocalNavigationManager.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ToolBar()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for(id in 1..100) {
                item{
                    ContactCard(
                        name = id.toString(),
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolBar(){
    Row( modifier = Modifier
        .fillMaxWidth()
        .background(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(20.dp)
        )
        .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(R.string.main_messenger))
    }
}

@Composable
private fun ContactCard(
    name: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
                )
            .padding(16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.person), // Замените на ваш ресурс аватарки
            contentDescription = null,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.messenger_user) + name,
            fontSize = 18.sp,
        )
    }
}