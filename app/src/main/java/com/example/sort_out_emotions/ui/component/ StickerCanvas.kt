package com.example.sort_out_emotions.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.ImageBitmap

@Composable
fun StickerCanvas(stickers: List<ImageBitmap>) {
    val positions = remember { mutableStateListOf<Offset>() }

    // 初期位置を設定
    if (positions.size != stickers.size) {
        positions.clear()
        positions.addAll(List(stickers.size) { Offset.Zero })
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures { _, dragAmount ->
                // ドラッグしたステッカーの位置を更新
                val index = stickers.indexOfFirst {
                    // ステッカーのタップ判定（簡易的に）
                    true
                }
                if (index != -1) {
                    positions[index] = positions[index] + dragAmount
                }
            }
        }
    ) {
        stickers.forEachIndexed { index, sticker ->
            drawImage(
                image = sticker,
                topLeft = positions[index]
            )
        }
    }
}
