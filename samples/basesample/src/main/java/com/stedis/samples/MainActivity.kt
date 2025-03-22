package com.stedis.samples

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.stedis.samples.panes.root.RootPane
import com.stedis.samples.ui.theme.SamplesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SamplesTheme {
                RootPane(
                    onOpenWebPage = { openWebPage(it) },
                    onRootBack = { finish() },
                )
            }
        }
    }

    private fun openWebPage(url: String) {
        try {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url)),
            )
        } catch (e: ActivityNotFoundException) {
            e.localizedMessage?.let { Log.e("ActivityNotFoundException", it) }
        }
    }
}