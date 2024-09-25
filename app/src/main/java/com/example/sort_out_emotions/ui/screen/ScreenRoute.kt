package com.example.sort_out_emotions.ui.screen

sealed class ScreenRoute (val route: String){
    object AskTodayEventScreen: ScreenRoute("ask_today_event_screen")
    object ChooseStickerScreen: ScreenRoute("choose_sticker_screen")
    object StickerPreviewScreen: ScreenRoute("sticker_preview_screen/{chosenSticker}")
}