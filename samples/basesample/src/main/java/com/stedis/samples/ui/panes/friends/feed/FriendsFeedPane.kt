package com.stedis.samples.ui.panes.friends.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.stedis.navigation.compose.LocalNavigationHost
import com.stedis.navigation.compose.LocalNavigationManager
import com.stedis.navigation.core.inside
import com.stedis.samples.R
import com.stedis.samples.navigation.destinations.FriendInfoDestination
import com.stedis.samples.navigation.ext.forward
import com.stedis.samples.ui.component.TopBar

@Composable
fun FriendsFeedPane() {
    val navigationManager = LocalNavigationManager.current
    val currentNavigationHost = LocalNavigationHost.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBar(text = stringResource(R.string.main_friends))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for (id in 1..100) {
                item {
                    FriendCard(
                        name = id.toString(),
                        onClick = {
                            navigationManager.forward(FriendInfoDestination(id)) {
                                inside(currentNavigationHost.hostName)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendCard(
    name: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.person),
            contentDescription = null,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(R.string.friend) + name,
            fontSize = 18.sp,
        )
    }
}