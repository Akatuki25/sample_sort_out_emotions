package com.example.sort_out_emotions.ui.screen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sort_out_emotions.R
import com.example.sort_out_emotions.ui.theme.Barlywoood
import com.example.sort_out_emotions.ui.theme.Saddlebrown
import com.example.sort_out_emotions.ui.theme.Sort_out_emotionsTheme
import com.example.sort_out_emotions.ui.theme.Wheat

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
fun StickerPreviewScreen(){
    Surface (color = Wheat){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center
        ){
            //月,年を表示
            Text(
                text = "Sep,2024",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 25.sp,
                color = Saddlebrown
            )

            Spacer(modifier = Modifier.height(100.dp))

            //５週分縦に並べる
            for(week in daysSeptember){
                StickerWeekPreview(dayList = week)
            }
            StickerPreviewLastWeek()
        }
    }
}

@Composable
fun StickerWeekPreview(dayList: A){
    //一週間分のステッカーの表示
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ){
        //一週間分のステッカーの表示
        ShowSticker2(dayList[0])
        ShowSticker2(dayList[1])
        ShowSticker2(dayList[2])
        ShowSticker2(dayList[3])
        ShowSticker2(dayList[4])
        ShowSticker2(dayList[5])
        ShowSticker2(dayList[6])

    }
}

@Composable
fun StickerPreviewLastWeek(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 1.dp),
        horizontalArrangement = Arrangement.Start

    ){
        ShowSticker3(day = 29)
        ShowSticker3(day = 30)
    }
}

@Composable
fun ShowSticker2(day: Int){
    //1日分ステッカーの表示
    Column() {
        //日付の表示
        Text(
            text = day.toString(),
            fontSize = 11.sp,
            color = Saddlebrown
        )
        //ステッカーの表示
        Image(
            painter = painterResource(R.drawable.no),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .padding(bottom = 5.dp)
        )
        //ステッカー下の線の表示
        HorizontalDivider(
            modifier = Modifier.width(50.dp),
            thickness = 2.dp,
            color = Barlywoood
        )
    }
}

@Composable
fun ShowSticker3(day: Int) {
    //1日分ステッカーの表示
    Column(
        modifier = Modifier.padding(end = 3.dp)
    ) {
        //日付の表示
        Text(
            text = day.toString(),
            fontSize = 11.sp,
            color = Saddlebrown
        )
        //ステッカーの表示
        Image(
            painter = painterResource(R.drawable.no),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .padding(bottom = 5.dp)
        )
        //ステッカー下の線の表示
        HorizontalDivider(
            modifier = Modifier.width(50.dp),
            thickness = 2.dp,
            color = Barlywoood
        )
    }
}

@Preview(
    showBackground = true,
    apiLevel = 34
)
@Composable
fun GreetingPreview() {
    Sort_out_emotionsTheme {
        StickerPreviewScreen()
    }
}