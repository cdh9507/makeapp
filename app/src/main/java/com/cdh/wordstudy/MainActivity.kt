package com.cdh.wordstudy

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cdh.wordstudy.data.ProgressStore
import com.cdh.wordstudy.data.WordRepository
import com.cdh.wordstudy.ui.WordStudyApp
import com.cdh.wordstudy.ui.WordStudyTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val words = WordRepository.load(this)
        val progress = ProgressStore(this)
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(0.9f)
            }
        }

        setContent {
            WordStudyTheme {
                WordStudyApp(words, progress, speak = ::speak)
            }
        }
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "word")
    }

    override fun onDestroy() {
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }
}
