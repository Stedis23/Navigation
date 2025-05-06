package com.stedis.samples.panes.friends.friend

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.stedis.samples.navigation.ext.back
import com.stedis.samples.ui.component.TopBar

@Composable
fun FriendInfoPane(friendId: Int) {
    val navigationManager = LocalNavigationManager.current
    val currentNavigationHost = LocalNavigationHost.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBar(
            onClick = { navigationManager.back { inside(currentNavigationHost.hostName) } }
        )

        FriendProfile(
            friendName = stringResource(R.string.friend) + friendId,
            friendDescription = stringResource(R.string.friend_description),
            friendAvatar = R.drawable.person,
        )
    }
}

@Composable
private fun FriendProfile(friendName: String, friendDescription: String, friendAvatar: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .height(IntrinsicSize.Max)
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = friendAvatar),
            contentDescription = "Friend Avatar",
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(BorderStroke(3.dp, MaterialTheme.colorScheme.surface), CircleShape),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = friendName,
                fontSize = 24.sp,
            )

            Text(text = friendDescription)
        }
    }
}