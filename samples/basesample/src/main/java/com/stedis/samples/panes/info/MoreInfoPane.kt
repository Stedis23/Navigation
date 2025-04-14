package com.stedis.samples.panes.info

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stedis.navigation.compose.LocalNavigationManager
import com.stedis.samples.R
import com.stedis.samples.navigation.destinations.WebPageDestination
import com.stedis.samples.navigation.ext.close
import com.stedis.samples.navigation.ext.open

private const val GITHUB_URL = "https://github.com/Stedis23/Navigation"

@Composable
fun MoreInfoPane() {
    val navigationManager = LocalNavigationManager.current

    BackHandler { navigationManager.close() }

    Scaffold(
        topBar = {
            ToolBar(onClick = { navigationManager.close() })
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(modifier = Modifier.weight(1f))

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

            Spacer(modifier = Modifier.weight(1f))

            Column {
                Button(
                    onClick = { navigationManager.open(WebPageDestination(GITHUB_URL)) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.go_to_github))
                }

                Button(
                    onClick = { navigationManager.close() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors()
                        .copy(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Text(text = stringResource(R.string.back))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolBar(onClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(R.string.more_info))
        },
        navigationIcon = {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = null,
                modifier = Modifier.clickable { onClick() }
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            .copy(containerColor = MaterialTheme.colorScheme.background)
    )
}