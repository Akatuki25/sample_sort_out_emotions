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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sort_out_emotions.ui.component.StickerCanvas
import com.example.sort_out_emotions.viewmodel.MainViewModel
import androidx.navigation.NavController

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val transcribedText by mainViewModel.transcribedText.observeAsState("")
    val images by mainViewModel.images.observeAsState(emptyList())
    val selectedImages by mainViewModel.selectedImages.collectAsState()
    val isRecording by mainViewModel.isRecording.observeAsState(false)

    Column {
        Row {
            Button(
                onClick = {
                    if (checkAudioPermission(context)) {
                        mainViewModel.startSpeechRecognition(context)
                    }
                },
                enabled = !isRecording // 録音中は無効化
            ) {
                Text("録音開始")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    mainViewModel.stopSpeechRecognition()
                    //ChooseStickerScreenに画面遷移
                    navController.navigate(ScreenRoute.ChooseStickerScreen.route)
                },
                enabled = isRecording // 録音中のみ有効化
            ) {
                Text("録音停止")
            }
        }

        Text(text = "文字起こし結果: $transcribedText")

        //if文消去


    }
}

private fun checkAudioPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}
