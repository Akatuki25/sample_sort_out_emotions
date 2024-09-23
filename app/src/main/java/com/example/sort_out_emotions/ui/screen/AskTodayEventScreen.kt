package com.example.sort_out_emotions.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sort_out_emotions.R
import com.example.sort_out_emotions.ui.theme.Barlywoood
import com.example.sort_out_emotions.ui.theme.Saddlebrown
import com.example.sort_out_emotions.ui.theme.Sort_out_emotionsTheme
import com.example.sort_out_emotions.ui.theme.Wheat
import com.example.sort_out_emotions.viewmodel.MainViewModel

@Composable
fun AskTodayEventScreen(
    mainViewModel: MainViewModel = viewModel(),
    modifier: Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val transcribedText by mainViewModel.transcribedText.observeAsState("")
    val images by mainViewModel.images.observeAsState(emptyList())
    val selectedImages by mainViewModel.selectedImages.collectAsState()
    val isRecording by mainViewModel.isRecording.observeAsState(false)

    Sort_out_emotionsTheme {
        Surface(
            color = Wheat
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(45.dp)
                    .background(Wheat),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

                ) {
                Text(
                    text = "今日あったことを聞かせてください",
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    lineHeight = 50.sp,
                    color = Saddlebrown
                )
                Spacer(modifier = Modifier.height(60.dp))

                //ボタン消去

                Row {
                    Button(
                        onClick = {
                            if (checkAudioPermission(context)) {
                                mainViewModel.startSpeechRecognition(context)
                            }
                        },
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 10.dp,
                            pressedElevation = 15.dp,
                            disabledElevation = 0.dp,
                        ),
                        colors = ButtonDefaults.elevatedButtonColors(),
                        enabled = !isRecording // 録音中は無効化
                    ) {
                        Text(
                            text = "録音開始",
                            color = Saddlebrown
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { mainViewModel.stopSpeechRecognition() },
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 10.dp,
                            pressedElevation = 15.dp,
                            disabledElevation = 0.dp,
                        ),
                        colors = ButtonDefaults.elevatedButtonColors(),
                        enabled = isRecording // 録音中のみ有効化
                    ) {
                        Text(
                            text = "録音停止",
                            color = Saddlebrown
                        )
                    }
                }


                Text(
                    text = "文字起こし結果: $transcribedText",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Saddlebrown
                )

                Spacer(modifier = Modifier.height(100.dp))

                Button(
                    onClick = {
                        //ChooseStickerScreenに画面遷移
                        navController.navigate(ScreenRoute.ChooseStickerScreen.route)
                    },
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 15.dp,
                        disabledElevation = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(Barlywoood)
                ) {
                    Text(text = "OK")
                }
            }
        }
    }
}

private fun checkAudioPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}


@Preview
@Composable
fun mainPreview(){
    AskTodayEventScreen(mainViewModel = viewModel(), modifier = Modifier, navController = rememberNavController())
}