package com.example.sort_out_emotions.viewmodel

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sort_out_emotions.data.repository.ChatGPTRepository
import com.example.sort_out_emotions.data.repository.StableDiffusionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun startSpeechRecognition(context: Context) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                _transcribedText.postValue(text)
                processText(text)
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizer?.startListening(intent)
    }

    private fun processText(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val keywords = chatGPTRepository.summarizeText(text)
            val sentiment = chatGPTRepository.analyzeSentiment(text)
            val prompt = createPrompt(keywords, sentiment)
            val imagesBase64 = stableDiffusionRepository.generateImages(prompt)
            val bitmaps = imagesBase64.mapNotNull { base64 ->
                val decodedString = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)?.asImageBitmap()
            }
            _images.postValue(bitmaps)
        }
    }

    private fun createPrompt(keywords: List<String>, sentiment: Float): String {
        return "Keywords: ${keywords.joinToString(", ")}, Sentiment: $sentiment"
    }

    fun selectImage(image: ImageBitmap) {
        _selectedImages.value += image
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }
}
