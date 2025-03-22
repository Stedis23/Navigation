package com.stedis.samples.panes.info

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stedis.navigation.compose.LocalNavigationManager
import com.stedis.navigation.core.ForwardCommand
import com.stedis.navigation.core.execute
import com.stedis.samples.R
import com.stedis.samples.navigation.commands.BackToMainCommand
import com.stedis.samples.navigation.destinations.WebPageDestination

private const val GITHUB_URL = "https://github.com/Stedis23/Navigation"

@Composable
fun MoreInfoPane() {
    val navigationManager = LocalNavigationManager.current

    BackHandler { navigationManager.execute(BackToMainCommand) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        ToolBar(onClick = { navigationManager.execute(BackToMainCommand) })

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.info),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
            )

            Text(
                text = stringResource(R.string.more_info_description),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )
        }


        Column {
            Button(
                onClick = { navigationManager.execute(ForwardCommand(WebPageDestination(GITHUB_URL))) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.go_to_github))
            }

            Button(
                onClick = { navigationManager.execute(BackToMainCommand) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors()
                    .copy(containerColor = MaterialTheme.colorScheme.secondary),
            ) {
                Text(text = stringResource(R.string.back))
            }
        }
    }
}

@Composable
private fun ToolBar(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_back),
            contentDescription = null,
            modifier = Modifier.clickable { onClick() },
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.main_friends),
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}