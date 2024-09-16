package com.example.sort_out_emotions.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sort_out_emotions.ui.component.StickerCanvas
import com.example.sort_out_emotions.viewmodel.MainViewModel

@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val transcribedText by mainViewModel.transcribedText.observeAsState("")
    val images by mainViewModel.images.observeAsState(emptyList())
    val selectedImages by mainViewModel.selectedImages.collectAsState()

    Column {
        Button(onClick = {
            if (checkAudioPermission(context)) {
                mainViewModel.startSpeechRecognition(context)
            }
        }) {
            Text("音声入力開始")
        }

        Text(text = "文字起こし結果: $transcribedText")

        if (images.isNotEmpty()) {
            LazyRow {
                items(images.size) { index ->
                    Image(bitmap = images[index], contentDescription = null, modifier = Modifier.clickable {
                        mainViewModel.selectImage(images[index])
                    })
                }
            }
        }

        if (selectedImages.isNotEmpty()) {
            StickerCanvas(stickers = selectedImages)
        }
    }
}

private fun checkAudioPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
}
