package com.example.wheelsonwheels.ui.screens

import android.widget.VideoView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.example.wheelsonwheels.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onVideoFinished: () -> Unit) {
    val context = LocalContext.current
    val currentOnVideoFinished by rememberUpdatedState(onVideoFinished)
    val isFinished = remember { mutableStateOf(false) }

    val navigate = remember {
        {
            if (!isFinished.value) {
                isFinished.value = true
                currentOnVideoFinished()
            }
        }
    }

    val videoUri = remember(context) {
        "android.resource://${context.packageName}/${R.raw.introvideo}".toUri()
    }

    // Fixed warning by removing Box wrapper and putting clickable directly on AndroidView
    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(videoUri)
                setOnCompletionListener { navigate() }
                setOnErrorListener { _, _, _ ->
                    navigate()
                    true
                }
                start()
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .clickable { navigate() },
        update = { /* No-op */ }
    )

    LaunchedEffect(Unit) {
        delay(3500)
        navigate()
    }
}
