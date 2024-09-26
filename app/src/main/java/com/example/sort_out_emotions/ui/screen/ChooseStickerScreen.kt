package com.example.sort_out_emotions.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sort_out_emotions.viewmodel.MainViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ChooseSticker(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    val images by mainViewModel.images.observeAsState(emptyList())
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Surface(color = MaterialTheme.colorScheme.primary) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // 修正
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "好きなステッカーを\n選んでください",
                fontSize = 24.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            if (images.isNotEmpty()) {
                // カルーセル表示
                HorizontalPager(
                    count = images.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp) // 高さを調整
                ) { page ->
                    ShowSticker(
                        image = images[page],
                        navController = navController,
                        mainViewModel = mainViewModel
                    )
                }

                // ページインジケーター
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier.padding(16.dp)
                )

                // 選択ボタン
                Button(
                    onClick = {
                        val selectedImage = images[pagerState.currentPage]
                        // 選択された画像を保存
                        mainViewModel.selectImage(selectedImage)
                        // ステッカー表示画面に遷移
                        navController.navigate(ScreenRoute.StickerPreviewScreen.route)
                    }
                ) {
                    Text(
                        text = "このステッカーを選択",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text("画像を読み込み中...")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ShowSticker(
    image: ImageBitmap,
    navController: NavController,
    mainViewModel: MainViewModel
) {
    Image(
        bitmap = image,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}
