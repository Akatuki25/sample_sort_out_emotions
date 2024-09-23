package com.example.sort_out_emotions.ui.screen

import android.media.Image
import android.provider.ContactsContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sort_out_emotions.R
import com.example.sort_out_emotions.data.model.StableDiffusionResponse
import com.example.sort_out_emotions.ui.theme.Saddlebrown
import com.example.sort_out_emotions.ui.theme.Wheat

var chosenSticker: String? = null

@Composable
fun ChooseStickerScreen(
    navController: NavController
){

    //ステッカーを3枚横並びで表示
    Surface (color = Wheat){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                ShowSticker(navController = navController, "R.drawable.imopro")
                ShowSticker(navController = navController, "R.drawable.imopro")
                ShowSticker(navController = navController, "R.drawable.imopro")
            }
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "好きなステッカーを\n選んでください",
                fontSize = 30.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Saddlebrown
            )
        }
    }
}



@Composable
fun ShowSticker(navController: NavController, sticker: String){

    //ステッカーの表示
    //Columnにして影をつけたい
    Image(
        painter = painterResource(R.drawable.no),
        contentDescription = null,
        modifier = Modifier
            .size(100.dp)
            .padding(horizontal = 5.dp)
            .clickable(
                onClick = {
                    //選択されたステッカーを保存
                    chosenSticker = sticker

                    //カレンダー画面に遷移
                    navController.navigate("sticker_preview_screen/$chosenSticker")
                }
            )
    )
}

@Preview(apiLevel = 34)
@Composable
fun ChooseStickerPreview(){
    ChooseStickerScreen(navController = rememberNavController())
}