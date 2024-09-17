package com.example.sort_out_emotions.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
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
import android.graphics.BitmapFactory
import java.util.*

class MainViewModel : ViewModel() {

    private val chatGPTRepository = ChatGPTRepository()
    private val stableDiffusionRepository = StableDiffusionRepository()

    private val _transcribedText = MutableLiveData<String>()
    val transcribedText: LiveData<String> = _transcribedText

    private val _images = MutableLiveData<List<ImageBitmap>>()
    val images: LiveData<List<ImageBitmap>> = _images

    private val _selectedImages = MutableStateFlow<List<ImageBitmap>>(emptyList())
    val selectedImages: StateFlow<List<ImageBitmap>> = _selectedImages

    private var speechRecognizer: SpeechRecognizer? = null

    // 録音中かどうかを示す状態を追加
    private val _isRecording = MutableLiveData<Boolean>(false)
    val isRecording: LiveData<Boolean> = _isRecording

    // 音声認識のリスナーをプロパティとして保持
    private var recognitionListener: android.speech.RecognitionListener? = null

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
        // 音声認識の結果をログに出力
        Log.d("MainViewModel", "音声認識の文字起こし結果: $text")

        CoroutineScope(Dispatchers.IO).launch {
            val keywords = chatGPTRepository.summarizeText(text)
            val sentimentMap = chatGPTRepository.analyzeSentiment(text)

            Log.d("MainViewModel", "ChatGPTの要約結果（キーワード）: ${keywords.joinToString(", ")}")
            Log.d("MainViewModel", "ChatGPTの感情分析結果: $sentimentMap")

            try {
                val imageData = stableDiffusionRepository.generateImages(
                    summary = keywords,
                    sentiment = sentimentMap
                )

                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)?.asImageBitmap()

                if (bitmap != null) {
                    _images.postValue(listOf(bitmap))
                } else {
                    Log.e("MainViewModel", "画像のデコードに失敗しました")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "画像生成中にエラーが発生しました: ${e.message}")
            }
        }
    }

    fun selectImage(image: ImageBitmap) {
        _selectedImages.value = _selectedImages.value + image
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }
}
