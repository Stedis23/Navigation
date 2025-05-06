package com.stedis.samples

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import com.stedis.samples.panes.root.RootPane
import com.stedis.samples.ui.theme.SamplesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                Intent(Intent.ACTION_VIEW, url.toUri()),
            )
        } catch (e: ActivityNotFoundException) {
            e.localizedMessage?.let { Log.e("ActivityNotFoundException", it) }
        }
    }
}