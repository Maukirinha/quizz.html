package com.jarvis.voz

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import androidx.core.content.ContextCompat

/** Ligações, mensagens e WhatsApp. */
class Comunicacao(private val ctx: Context) {

    private fun nova(i: Intent) = i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    private fun temPermissao(p: String) =
        ContextCompat.checkSelfPermission(ctx, p) == PackageManager.PERMISSION_GRANTED

    /** Extrai um número de telefone de uma frase falada. */
    fun numeroDaFrase(frase: String): String? {
        val digitos = Texto.normalizar(frase)
            .replace(Regex("[^0-9]"), "")
        return if (digitos.length in 8..13) digitos else null
    }

    fun ligar(numero: String): String {
        return if (temPermissao(Manifest.permission.CALL_PHONE)) {
            ctx.startActivity(nova(Intent(Intent.ACTION_CALL, Uri.parse("tel:$numero"))))
            "Ligando para $numero."
        } else {
            ctx.startActivity(nova(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero"))))
            "Número discado na tela. Autorize a permissão de chamadas para eu ligar sozinho."
        }
    }

    @Suppress("DEPRECATION")
    fun sms(numero: String, texto: String): String {
        if (!temPermissao(Manifest.permission.SEND_SMS))
            return "Preciso da permissão de mensagens para enviar SMS."
        return try {
            val sm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                ctx.getSystemService(SmsManager::class.java)
            else SmsManager.getDefault()
            sm.sendTextMessage(numero, null, texto, null, null)
            "Mensagem enviada para $numero."
        } catch (e: Exception) {
            "Não consegui enviar a mensagem: ${e.message}"
        }
    }

    /** Abre a conversa do WhatsApp já com o texto digitado. */
    fun whatsapp(numero: String, texto: String): String {
        val url = "https://wa.me/55$numero" +
                if (texto.isNotBlank()) "?text=${Uri.encode(texto)}" else ""
        return try {
            ctx.startActivity(nova(Intent(Intent.ACTION_VIEW, Uri.parse(url))))
            if (texto.isBlank()) "Conversa aberta com $numero."
            else "Mensagem pronta para $numero. Diga: toque em enviar."
        } catch (e: Exception) {
            "WhatsApp não encontrado neste celular."
        }
    }

    fun compartilhar(texto: String, titulo: String): Boolean = try {
        val i = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, titulo)
            putExtra(Intent.EXTRA_TEXT, texto)
        }
        ctx.startActivity(nova(Intent.createChooser(i, titulo)))
        true
    } catch (e: Exception) { false }

    fun buscarNaWeb(termo: String): Boolean = try {
        ctx.startActivity(nova(Intent(Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/search?q=${Uri.encode(termo)}"))))
        true
    } catch (e: Exception) { false }
}
