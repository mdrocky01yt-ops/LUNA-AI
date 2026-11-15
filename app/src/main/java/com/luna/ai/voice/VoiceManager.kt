
package com.luna.ai.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class VoiceManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null

    private var isTtsReady = false
    private var isDestroyed = false
    private var pendingSpeech: String? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    var onTtsFinished: (() -> Unit)? = null

    private var currentPitch = 1.0f
    private var currentSpeed = 1.0f
    private var currentVoiceName = "Luna Default"

    init {
        initializeTextToSpeech()
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (isDestroyed) return@TextToSpeech

            if (status == TextToSpeech.SUCCESS) {
                val tts = textToSpeech ?: return@TextToSpeech

                val languageResult = tts.setLanguage(Locale("bn", "BD"))

                if (
                    languageResult == TextToSpeech.LANG_MISSING_DATA ||
                    languageResult == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    tts.language = Locale.getDefault()
                }

                tts.setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {

                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                            onTtsFinished?.invoke()
                        }

                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                            onTtsFinished?.invoke()
                        }
                    }
                )

                isTtsReady = true

                pendingSpeech?.let { text ->
                    pendingSpeech = null
                    speak(text)
                }
            } else {
                isTtsReady = false
                _isSpeaking.value = false
            }
        }
    }

    fun updateVoiceParameters(
        pitch: Float,
        speed: Float,
        voiceName: String
    ) {
        currentPitch = pitch.coerceIn(0.5f, 2.0f)
        currentSpeed = speed.coerceIn(0.5f, 2.0f)
        currentVoiceName = voiceName
    }

    fun speak(text: String) {
        if (isDestroyed || text.isBlank()) return

        if (!isTtsReady || textToSpeech == null) {
            pendingSpeech = text
            return
        }

        val tts = textToSpeech ?: return

        tts.setPitch(currentPitch)
        tts.setSpeechRate(currentSpeed)

        val selectedVoice = tts.voices?.find {
            it.name == currentVoiceName
        }

        if (selectedVoice != null) {
            tts.voice = selectedVoice
        }

        val params = Bundle()
        val utteranceId = "LunaSpeak_${System.currentTimeMillis()}"

        _isSpeaking.value = true

        val result = tts.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            params,
            utteranceId
        )

        if (result == TextToSpeech.ERROR) {
            _isSpeaking.value = false
            onTtsFinished?.invoke()
        }
    }

    fun stopSpeaking() {
        pendingSpeech = null
        textToSpeech?.stop()
        _isSpeaking.value = false
    }

    fun startListening(
        onResult: (String) -> Unit,
        onEnd: () -> Unit
    ) {
        if (isDestroyed || _isSpeaking.value) return

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _isListening.value = false
            onEnd()
            return
        }

        try {
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            speechRecognizer = null

            val recognizer =
                SpeechRecognizer.createSpeechRecognizer(context)

            speechRecognizer = recognizer

            recognizer.setRecognitionListener(
                object : RecognitionListener {

                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) = Unit

                    override fun onBufferReceived(buffer: ByteArray?) = Unit

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        onEnd()
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false

                        val matches = results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                        if (!matches.isNullOrEmpty()) {
                            onResult(matches[0])
                        } else {
                            onEnd()
                        }
                    }

                    override fun onPartialResults(
                        partialResults: Bundle?
                    ) = Unit

                    override fun onEvent(
                        eventType: Int,
                        params: Bundle?
                    ) = Unit
                }
            )

            val intent = Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            ).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    "bn-BD"
                )
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                    "bn-BD"
                )
                putExtra(
                    RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                    false
                )
            }

            recognizer.startListening(intent)

        } catch (error: Exception) {
            _isListening.value = false
            onEnd()
        }
    }

    fun stopListening() {
        _isListening.value = false

        try {
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {
            // Recognizer বন্ধ করার সময় সমস্যা হলে অ্যাপ চালু থাকবে।
        }

        speechRecognizer = null
    }

    fun destroy() {
        if (isDestroyed) return

        isDestroyed = true
        pendingSpeech = null
        onTtsFinished = null

        try {
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {
            // Cleanup
        }

        speechRecognizer = null

        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null

        isTtsReady = false
        _isListening.value = false
        _isSpeaking.value = false
    }
}
