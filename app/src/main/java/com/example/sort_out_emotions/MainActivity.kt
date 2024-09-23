package com.example.sort_out_emotions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sort_out_emotions.ui.screen.AskTodayEventScreen
import com.example.sort_out_emotions.ui.screen.ChooseStickerScreen
import com.example.sort_out_emotions.ui.screen.MainScreen
import com.example.sort_out_emotions.ui.screen.ScreenRoute
import com.example.sort_out_emotions.ui.screen.StickerPreviewScreen
import com.example.sort_out_emotions.viewmodel.MainViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sort_out_emotions.ui.screen.chosenSticker

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // パーミッションのリクエスト
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // パーミッションの結果を処理
        }

        requestPermissions()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = ScreenRoute.AskTodayEventScreen.route
                    ) {
                        //音声入力画面
                        composable(route = ScreenRoute.AskTodayEventScreen.route) {
                            AskTodayEventScreen(modifier = Modifier, navController = navController)
                        }
                        //ステッカー選択画面
                        composable(route = ScreenRoute.ChooseStickerScreen.route) {
                            ChooseStickerScreen(navController)
                        }
                        //ステッカー表示画面
                        composable(route = ScreenRoute.StickerPreviewScreen.route) {
                            StickerPreviewScreen()
                        }
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        val notGranted = permissions.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted) {
            permissionLauncher.launch(permissions)
        }
    }
}

@Preview
@Composable
fun AppPreview(){
    AskTodayEventScreen(modifier = Modifier, navController = rememberNavController())
}