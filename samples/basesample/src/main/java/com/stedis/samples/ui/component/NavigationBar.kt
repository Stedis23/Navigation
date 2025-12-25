package com.stedis.samples.ui.component

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.stedis.samples.R
import com.stedis.samples.navigation.Hosts

@Composable
fun NavigationBar(
    isVertical: Boolean = false,
    currentPage: String,
    onFriendsClick: () -> Unit,
    onNewsClick: () -> Unit,
    onMoreInfoClick: () -> Unit,
) {
    if (isVertical) {
        NavigationRail(
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            NavigationRailItem(
                selected = currentPage == Hosts.FRIENDS.name,
                onClick = onFriendsClick,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.group),
                        contentDescription = stringResource(R.string.main_friends),
                    )
                },
                label = {
                    Text(stringResource(R.string.main_friends))
                }
            )

            NavigationRailItem(
                selected = currentPage == Hosts.NEWS.name,
                onClick = onNewsClick,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.newspaper),
                        contentDescription = stringResource(R.string.main_news),
                    )
                },
                label = {
                    Text(stringResource(R.string.main_news))
                }
            )

            NavigationRailItem(
                selected = false,
                onClick = onMoreInfoClick,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.info),
                        contentDescription = stringResource(R.string.more_info),
                    )
                },
                label = {
                    Text(stringResource(R.string.more_info))
                }
            )
        }
    } else {
        HorizontalDivider()
        BottomAppBar {
            IconButton(onClick = onFriendsClick) {
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

            IconButton(onClick = onNewsClick) {
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

            IconButton(onClick = onMoreInfoClick) {
                Icon(
                    painter = painterResource(R.drawable.info),
                    contentDescription = stringResource(R.string.more_info),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun BottomAppBar(
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