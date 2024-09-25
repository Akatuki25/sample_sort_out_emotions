package com.example.sort_out_emotions.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sort_out_emotions.R
import com.example.sort_out_emotions.ui.theme.Barlywoood
import com.example.sort_out_emotions.ui.theme.Saddlebrown
import com.example.sort_out_emotions.ui.theme.Sort_out_emotionsTheme
import com.example.sort_out_emotions.ui.theme.Wheat
import com.example.sort_out_emotions.viewmodel.MainViewModel
import java.util.Calendar

data class A(val numbers:List<Int>) {
    operator fun get(i: Int): Int {
        return numbers[i]
    }
}

val week1= A(listOf(1,2,3,4,5,6,7))
val week2= A(listOf(8,9,10,11,12,13,14))
val week3= A(listOf(15,16,17,18,19,20,21))
val week4= A(listOf(22,23,24,25,26,27,28))
val daysSeptember:List<A> = listOf(week1,week2,week3,week4)


@Composable
fun StickerPreview(mainViewModel: MainViewModel = viewModel()) {
    val dailyImages by mainViewModel.dailyImages.observeAsState(emptyMap())

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTHは0始まり

    val weeks = generateWeeks(year, month)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // 月,年を表示
        Text(
            text = "${month}, $year",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 25.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 各週を表示
        weeks.forEach { week ->
            StickerWeekPreview(week, dailyImages)
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun StickerWeekPreview(dayList: List<Int>, dailyImages: Map<String, ImageBitmap>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        dayList.forEach { day ->
            ShowSticker2(day, dailyImages)
        }
    }
}

@Composable
fun ShowSticker2(day: Int, dailyImages: Map<String, ImageBitmap>) {
    val date = getDateString(day)
    val image = dailyImages[date]

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 日付の表示
        Text(
            text = if (day > 0) day.toString() else "",
            fontSize = 11.sp
        )
        // ステッカーの表示
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(bottom = 5.dp)
            )
        } else {
            // ステッカーがない時の処理、画像は適宜入れ替えてください
            Image(
                painter = painterResource(R.drawable.placeholder),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(bottom = 5.dp)
            )
        }
        // ステッカー下の線の表示
        HorizontalDivider(
            modifier = Modifier.width(50.dp),
            thickness = 2.dp,
            color = Color.Gray
        )
    }
}

@SuppressLint("DefaultLocale")
private fun getDateString(day: Int): String {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTHは0始まり
    return if (day > 0) {
        String.format("%04d-%02d-%02d", year, month, day)
    } else {
        ""
    }
}

// 月の日付リストを週ごとに生成
private fun generateWeeks(year: Int, month: Int): List<List<Int>> {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1:日曜, 7:土曜

    val days = mutableListOf<Int>()
    for (i in 1 until firstDayOfWeek) {
        days.add(0)
    }
    for (day in 1..maxDay) {
        days.add(day)
    }
    while (days.size % 7 != 0) {
        days.add(0)
    }

    return days.chunked(7)
}


@Preview(
    showBackground = true,
    apiLevel = 34
)
@Composable
fun GreetingPreview() {
    Sort_out_emotionsTheme {
        ScreenRoute.StickerPreviewScreen
    }
}