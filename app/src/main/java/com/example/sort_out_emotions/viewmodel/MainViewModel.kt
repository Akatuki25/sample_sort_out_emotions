package com.example.sort_out_emotions.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sort_out_emotions.data.repository.ChatGPTRepository
import com.example.sort_out_emotions.data.repository.StableDiffusionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NAME_SHADOWING")
class MainViewModel : ViewModel() {

    private val chatGPTRepository = ChatGPTRepository()
    private val stableDiffusionRepository = StableDiffusionRepository()

    private val _transcribedText = MutableLiveData<String>()
    val transcribedText: LiveData<String> = _transcribedText

    private val _images = MutableLiveData<List<ImageBitmap>>()
    val images: LiveData<List<ImageBitmap>> = _images

    private val _selectedImages = MutableStateFlow<List<ImageBitmap>>(emptyList())
    val selectedImages: StateFlow<List<ImageBitmap>> = _selectedImages

    //日付と選択された画像を保存するためのリスト
    private val _dailyImages = MutableLiveData<MutableMap<String, ImageBitmap>>(mutableMapOf())
    val dailyImages: LiveData<MutableMap<String, ImageBitmap>> = _dailyImages

    private var speechRecognizer: SpeechRecognizer? = null

    // 録音中かどうかを示す状態を追加
    private val _isRecording = MutableLiveData<Boolean>(false)
    val isRecording: LiveData<Boolean> = _isRecording

    // 音声認識のリスナーをプロパティとして保持
    private var recognitionListener: android.speech.RecognitionListener? = null

    // 画面遷移のためのLiveDataを追加
    private val _navigateToChooseStickerScreen = MutableLiveData<Boolean>()
    val navigateToChooseStickerScreen: LiveData<Boolean> = _navigateToChooseStickerScreen

    fun startSpeechRecognition(context: Context) {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            recognitionListener = object : android.speech.RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d("SpeechRecognizer", "onReadyForSpeech")
                }
                override fun onBeginningOfSpeech() {
                    Log.d("SpeechRecognizer", "onBeginningOfSpeech")
                }
                override fun onRmsChanged(rmsdB: Float) {
                    // 不要な場合は省略可能
                }
                override fun onBufferReceived(buffer: ByteArray?) {
                    // 不要な場合は省略可能
                }
                override fun onEndOfSpeech() {
                    Log.d("SpeechRecognizer", "onEndOfSpeech")
                }
                override fun onError(error: Int) {
                    Log.e("SpeechRecognizer", "onError: $error")
                    _isRecording.postValue(false)
                }
                override fun onResults(results: Bundle?) {
                    Log.d("SpeechRecognizer", "onResults")
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull() ?: ""
                    _transcribedText.postValue(text)
                    _isRecording.postValue(false)
                    processText(text)
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    // 不要な場合は省略可能
                }
                override fun onEvent(eventType: Int, params: Bundle?) {
                    // 不要な場合は省略可能
                }
            }
            speechRecognizer?.setRecognitionListener(recognitionListener)
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        speechRecognizer?.startListening(intent)
        _isRecording.postValue(true)
    }

    fun stopSpeechRecognition() {
        speechRecognizer?.stopListening()
        _isRecording.postValue(false)
    }

    private fun processText(text: String) {
        Log.d("MainViewModel", "音声認識の文字起こし結果: $text")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ChatGPT APIを使用して要約と感情分析を行う
                val keywords = chatGPTRepository.summarizeText(text)
                val sentimentMap = chatGPTRepository.analyzeSentiment(text)

                // ログに出力
                Log.d("MainViewModel", "ChatGPTの要約結果（キーワード）: $keywords")
                Log.d("MainViewModel", "ChatGPTの感情分析結果: $sentimentMap")

                val keywordss = keywords.trim()

                val features = listOf(
                    sentimentMap["joy"] ?: 0f,
                    sentimentMap["sadness"] ?: 0f,
                    sentimentMap["anticipation"] ?: 0f,
                    sentimentMap["surprise"] ?: 0f,
                    sentimentMap["anger"] ?: 0f,
                    sentimentMap["fear"] ?: 0f,
                    sentimentMap["disgust"] ?: 0f,
                    sentimentMap["trust"] ?: 0f
                )

                // Gradio APIを使用して画像を生成
                Log.d("MainViewModel", "Gradio APIリクエストを送信します")

                val bitmapList = stableDiffusionRepository.generateImages(
                    keywordss = keywordss,
                    features = features
                )

                if (bitmapList.isNotEmpty()) {
                    _images.postValue(bitmapList.map { it.asImageBitmap() })
                } else {
                    Log.e("MainViewModel", "画像の取得に失敗しました")
                }

                // メインスレッドで画面遷移
                withContext(Dispatchers.Main) {
                    // ナビゲーションのイベントを発行するLiveDataやStateFlowを使用して画面遷移を制御
                    _navigateToChooseStickerScreen.postValue(true)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "処理中にエラーが発生しました: ${e.message}")
                // エラー処理（ユーザーへのフィードバックなど）
            }
        }
    }


    fun selectImage(image: ImageBitmap) {
        // 選択された画像をリストに追加
        _selectedImages.value = listOf(image)
        saveImageForToday(image)
    }

    private fun saveImageForToday(image: ImageBitmap){
        val date = getCurrentDate()
        _dailyImages.value?.put(date, image)
    }

    private fun getCurrentDate(): String{
        val sdf = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )
        return sdf.format(Date())
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }
}
