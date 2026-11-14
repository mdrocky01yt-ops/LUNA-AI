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

// ক্লাসটি ফাইলের একদম উপরে ডিক্লেয়ার করা হলো
data class VoiceProfile(
    val name: String,
    val pitch: Float,
    val speed: Float,
    val description: String
)

class VoiceManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    var onTtsFinished: (() -> Unit)? = null 

    private var currentPitch = 1.0f
    private var currentSpeed = 1.0f
    private var currentVoiceName = "Luna Default"

    init {
        initTTS()
    }

    private fun initTTS() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("bn", "BD"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.language = Locale.getDefault()
                }
                
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) { _isSpeaking.value = true }
                    override fun onDone(utteranceId: String?) { 
                        _isSpeaking.value = false 
                        onTtsFinished?.invoke()
                    }
                    override fun onError(utteranceId: String?) { 
                        _isSpeaking.value = false 
                        onTtsFinished?.invoke()
                    }
                })
            }
        }
    }

    fun updateVoiceParameters(pitch: Float, speed: Float, voiceName: String) {
        currentPitch = pitch
        currentSpeed = speed
        currentVoiceName = voiceName
    }

    fun speak(text: String) {
        _isSpeaking.value = true
        textToSpeech?.setPitch(currentPitch)
        textToSpeech?.setSpeechRate(currentSpeed)
        
        val voices = textToSpeech?.voices
        if (voices != null) {
            val selectedVoice = voices.find { it.name == currentVoiceName }
            if (selectedVoice != null) {
                textToSpeech?.voice = selectedVoice
            }
        }

        val params = Bundle()
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "LunaSpeak")
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        _isSpeaking.value = false
    }

    fun startListening(onResult: (String) -> Unit, onEnd: () -> Unit) {
        if (_isSpeaking.value) return 

        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { _isListening.value = true }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { _isListening.value = false }
                override fun onError(error: Int) { 
                    _isListening.value = false
                    onEnd()
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        onResult(matches[0])
                    }
                    _isListening.value = false
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault())
            }
            speechRecognizer?.startListening(intent)
        } else {
            _isListening.value = false
            onEnd()
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    fun destroy() {
        speechRecognizer?.destroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
    
    fun getAvailableVoiceProfiles(): List<VoiceProfile> {
        return listOf(
            VoiceProfile("Luna Default", 1.0f, 1.0f, "Soft & Friendly"),
            VoiceProfile("Cute Girl", 1.3f, 1.1f, "Playful"),
            VoiceProfile("Professional", 0.9f, 1.0f, "Confident"),
            VoiceProfile("Relaxed", 0.95f, 0.9f, "Calm"),
            VoiceProfile("Energetic", 1.1f, 1.2f, "Excited"),
            VoiceProfile("Storyteller", 0.9f, 0.95f, "Slow & Clear"),
            VoiceProfile("Whisper", 0.85f, 0.8f, "Very Soft"),
            VoiceProfile("Sweet", 1.2f, 1.0f, "Pleasant"),
            VoiceProfile("Robot", 0.7f, 1.1f, "Mechanical"),
            VoiceProfile("Deep Voice", 0.6f, 0.9f, "Heavy"),
            VoiceProfile("Male Calm", 0.8f, 0.9f, "Calm Male"),
            VoiceProfile("Male Energetic", 0.85f, 1.1f, "Energetic Male")
        )
    }
}
