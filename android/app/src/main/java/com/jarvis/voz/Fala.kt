package com.jarvis.voz

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

/** Voz do assistente. Avisa quando começa e quando termina, para a escuta não ouvir a si mesma. */
class Fala(ctx: Context, private val aoTerminar: () -> Unit) {

    private var pronto = false
    private var tts: TextToSpeech? = null
    private val pendentes = mutableListOf<String>()

    init {
        tts = TextToSpeech(ctx.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("pt", "BR")
                tts?.setSpeechRate(1.02f)
                tts?.setPitch(0.9f)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(id: String?) {}
                    override fun onDone(id: String?) = aoTerminar()
                    @Deprecated("compatibilidade com versões antigas")
                    override fun onError(id: String?) = aoTerminar()
                })
                pronto = true
                pendentes.forEach { dizer(it) }
                pendentes.clear()
            }
        }
    }

    fun dizer(texto: String) {
        if (texto.isBlank()) return
        if (!pronto) { pendentes += texto; return }
        val extras = Bundle()
        extras.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "jarvis")
        tts?.speak(texto, TextToSpeech.QUEUE_ADD, extras, "jarvis")
    }

    fun calar() { tts?.stop() }

    fun falando(): Boolean = tts?.isSpeaking == true

    fun encerrar() { tts?.stop(); tts?.shutdown(); tts = null }
}
