package com.stedis.samples.panes.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.stedis.navigation.compose.ComposeDestination
import com.stedis.navigation.compose.LocalNavigationManager
import com.stedis.navigation.compose.Pane
import com.stedis.navigation.compose.rememberCurrentDestination
import com.stedis.navigation.core.execute
import com.stedis.samples.R
import com.stedis.samples.navigation.Hosts
import com.stedis.samples.navigation.commands.ChangeCurrentSubHostOnMainCommand
import com.stedis.samples.navigation.commands.ForwardToMoreInfoCommand

@Composable
fun MainPane(currentSubHost: String) {
    val navigationManager = LocalNavigationManager.current

    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .weight(1f),
        ) {
            Pane(rememberCurrentDestination() as ComposeDestination)
        }

        BottomBar(
            currentPage = currentSubHost,
            onFriendsClick = { navigationManager.execute(ChangeCurrentSubHostOnMainCommand(Hosts.FRIENDS.name)) },
            onNewsClick = { navigationManager.execute(ChangeCurrentSubHostOnMainCommand(Hosts.NEWS.name)) },
            onMoreInfoClick = { navigationManager.execute(ForwardToMoreInfoCommand) }
        )
    }
}

@Composable
private fun BottomBar(
    currentPage: String,
    onFriendsClick: () -> Unit,
    onNewsClick: () -> Unit,
    onMoreInfoClick: () -> Unit,
) {
    HorizontalDivider()

    BottomAppBar {
        IconButton(onClick = { onFriendsClick() }) {
            Icon(
                painter = painterResource(R.drawable.group),
                contentDescription = stringResource(R.string.main_friends),
                tint = if (currentPage == Hosts.FRIENDS.name) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
            )
        }

        IconButton(onClick = { onNewsClick() }) {
            Icon(
                painter = painterResource(R.drawable.newspaper),
                contentDescription = stringResource(R.string.main_news),
                tint = if (currentPage == Hosts.NEWS.name) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
            )
        }

        IconButton(onClick = { onMoreInfoClick() }) {
            Icon(
                painter = painterResource(R.drawable.info),
                contentDescription = stringResource(R.string.more_info),
                tint = if (currentPage == Hosts.MAIN.name) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
            )
        }
    }
}

@Composable
fun BottomAppBar(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomAppBarDefaults.ContainerElevation,
    contentPadding: PaddingValues = BottomAppBarDefaults.ContentPadding,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        modifier = modifier
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(windowInsets)
                .padding(contentPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}