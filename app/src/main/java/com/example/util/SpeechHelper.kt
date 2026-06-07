package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SpeechHelper(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()
    
    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()
    
    private val _finalText = MutableStateFlow("")
    val finalText: StateFlow<String> = _finalText.asStateFlow()
    
    private val _rmsDb = MutableStateFlow(0f)
    val rmsDb: StateFlow<Float> = _rmsDb.asStateFlow()
    
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    init {
        initializeRecognizer()
    }

    private fun initializeRecognizer() {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            } else {
                _errorState.value = "Soporte de reconocimiento de voz no disponible."
            }
        } catch (e: Exception) {
            _errorState.value = "Error al inicializar el reconocedor: ${e.message}"
        }
    }
    
    fun startListening() {
        _errorState.value = null
        _finalText.value = ""
        _partialText.value = ""
        _rmsDb.value = 0f
        _isListening.value = true
        
        if (speechRecognizer == null) {
            initializeRecognizer()
        }
        
        if (speechRecognizer == null) {
            _errorState.value = "El servicio de voz no se pudo inicializar."
            _isListening.value = false
            return
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, arrayListOf("es-ES"))
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
                _errorState.value = null
            }

            override fun onBeginningOfSpeech() {
                _isListening.value = true
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Typical RMS range is ~ -2dB to 10dB, normalize to 0..1 for rendering dynamic waves
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                _rmsDb.value = normalized
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _isListening.value = false
            }

            override fun onError(error: Int) {
                _isListening.value = false
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Error de grabación de audio local."
                    SpeechRecognizer.ERROR_CLIENT -> "El cliente de voz detuvo el flujo."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permiso de micrófono requerido por el S.A.F."
                    SpeechRecognizer.ERROR_NETWORK -> "Fallo en la conexión de Internet para traducir voz."
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Tiempo de respuesta de red agotado en la lattice."
                    SpeechRecognizer.ERROR_NO_MATCH -> "No se identificó una frase clara. Reintente."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "El motor de voz está ocupado."
                    SpeechRecognizer.ERROR_SERVER -> "Error externo del traductor neural de voz."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No se detectó voz. Hable de nuevo."
                    else -> "Resonancia acústica inestable (Error $error)."
                }
                _errorState.value = message
            }

            override fun onResults(results: Bundle?) {
                _isListening.value = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val transcript = matches[0]
                    _finalText.value = transcript
                    _partialText.value = transcript
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _partialText.value = matches[0]
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        
        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _errorState.value = "Fallo de conexión neural: ${e.localizedMessage}"
            _isListening.value = false
        }
    }
    
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("SpeechHelper", "Error stopping voice listener", e)
        }
        _isListening.value = false
    }
    
    fun destroy() {
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("SpeechHelper", "Error destroying speech recognizer", e)
        }
        speechRecognizer = null
    }
}
