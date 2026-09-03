package com.jarvis.voz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

/**
 * Escuta contínua. O reconhecedor do Android encerra sozinho a cada frase,
 * então ele é reiniciado em laço, com espera progressiva quando falha em
 * sequência, para não consumir bateria à toa.
 */
class Escuta(
    private val ctx: Context,
    private val aoParcial: (String) -> Unit,
    private val aoFinal: (String) -> Unit,
    private val aoEstado: (String) -> Unit,
    private val aoErro: (String) -> Unit
) {

    private var rec: SpeechRecognizer? = null
    private val mao = Handler(Looper.getMainLooper())
    private var ligada = false
    private var pausada = false
    private var falhas = 0

    private val intencao: Intent
        get() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, ctx.packageName)
        }

    private val ouvinte = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) { falhas = 0; aoEstado("ouvindo") }
        override fun onBeginningOfSpeech() = aoEstado("captando")
        override fun onRmsChanged(rms: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() = aoEstado("processando")

        override fun onError(erro: Int) {
            when (erro) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                    ligada = false
                    aoErro("Permissão de microfone negada. Libere o microfone nas configurações do aplicativo.")
                    aoEstado("bloqueado")
                    return
                }
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> falhas++
                SpeechRecognizer.ERROR_NO_MATCH,
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> falhas = 0
                else -> falhas++
            }
            reagendar()
        }

        override fun onResults(res: Bundle?) {
            val lista = res?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val frase = lista?.firstOrNull()?.trim().orEmpty()
            if (frase.isNotEmpty()) aoFinal(frase)
            reagendar()
        }

        override fun onPartialResults(res: Bundle?) {
            val lista = res?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            lista?.firstOrNull()?.let { if (it.isNotBlank()) aoParcial(it.trim()) }
        }

        override fun onEvent(tipo: Int, params: Bundle?) {}
    }

    fun ligar(): Boolean {
        if (!SpeechRecognizer.isRecognitionAvailable(ctx)) {
            aoErro("Este celular não tem o serviço de reconhecimento de voz do Google instalado.")
            return false
        }
        ligada = true
        iniciar()
        return true
    }

    fun desligar() {
        ligada = false
        mao.removeCallbacksAndMessages(null)
        rec?.destroy()
        rec = null
        aoEstado("parado")
    }

    /** Silencia a escuta enquanto o assistente fala, evitando que ele ouça a própria voz. */
    fun pausar() {
        pausada = true
        mao.removeCallbacksAndMessages(null)
        try { rec?.cancel() } catch (e: Exception) { }
    }

    fun retomar() {
        pausada = false
        if (ligada) reagendar()
    }

    fun ligada(): Boolean = ligada

    private fun reagendar() {
        if (!ligada || pausada) return
        val espera = Math.min(200L * Math.pow(1.8, falhas.toDouble()).toLong(), 8000L)
        mao.removeCallbacksAndMessages(null)
        mao.postDelayed({ iniciar() }, espera)
    }

    private fun iniciar() {
        if (!ligada || pausada) return
        try {
            if (rec == null) {
                rec = SpeechRecognizer.createSpeechRecognizer(ctx)
                rec?.setRecognitionListener(ouvinte)
            }
            rec?.cancel()
            rec?.startListening(intencao)
        } catch (e: Exception) {
            falhas++
            reagendar()
        }
    }
}
