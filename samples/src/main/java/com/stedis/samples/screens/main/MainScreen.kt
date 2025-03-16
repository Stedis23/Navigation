package com.stedis.samples.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.ComposeScreen
import com.stedis.navigation.compose.LocalNavigationManager
import com.stedis.navigation.compose.rememberCurrentDestination
import com.stedis.navigation.core.ChangeCurrentHostCommand
import com.stedis.navigation.core.execute
import com.stedis.samples.R
import com.stedis.samples.navigation.Hosts

@Composable
fun MainScreen() {
    val navigationManager = LocalNavigationManager.current

    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .weight(1f),
        ) {
            ComposeScreen(rememberCurrentDestination() as ComposeDestination)
        }

        BottomBar(
            onMessengerClick = { navigationManager.execute(ChangeCurrentHostCommand(Hosts.MESSENGER.name)) },
            onNewsClick = { navigationManager.execute(ChangeCurrentHostCommand(Hosts.NEWS.name)) }
        )
    }
}

@Composable
private fun BottomBar(
    onMessengerClick: () -> Unit,
    onNewsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMessengerClick) {
            Icon(
                painter = painterResource(R.drawable.chat),
                contentDescription = stringResource(R.string.main_messenger),
            )
        }

        IconButton(onClick = onNewsClick) {
            Icon(
                painter = painterResource(R.drawable.newspaper),
                contentDescription = stringResource(R.string.main_news),
            )
        }
    }
}