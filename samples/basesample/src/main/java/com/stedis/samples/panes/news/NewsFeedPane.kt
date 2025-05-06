package com.stedis.samples.panes.news

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stedis.samples.R
import com.stedis.samples.ui.component.TopBar

@Composable
fun NewsFeedPane() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBar(text = stringResource(R.string.main_news))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for (id in 1..100) {
                item {
                    NewsCard(id)
                }
            }
        }
    }
}

@Composable
private fun NewsCard(newsId: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.newspaper),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.news_title) + newsId,
            fontSize = 18.sp,
            modifier = Modifier.padding(8.dp),
        )

        Text(
            text = stringResource(R.string.news_description),
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
    }
}