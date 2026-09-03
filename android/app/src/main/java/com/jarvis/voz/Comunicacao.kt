package com.jarvis.voz

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Ligações, mensagens e WhatsApp.
 *
 * Tudo é feito pelos aplicativos do próprio celular, sem pedir as permissões
 * de ligar e de enviar SMS diretamente. Essas duas permissões fazem o Google
 * Play Protect bloquear a instalação de aplicativos vindos de fora da loja,
 * e não são necessárias: o discador e o aplicativo de mensagens abrem já
 * preenchidos, e o comando "toque em enviar" conclui o envio.
 */
class Comunicacao(private val ctx: Context) {

    private fun nova(i: Intent) = i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    /** Extrai um número de telefone de uma frase falada. */
    fun numeroDaFrase(frase: String): String? {
        val digitos = Texto.normalizar(frase).replace(Regex("[^0-9]"), "")
        return if (digitos.length in 8..13) digitos else null
    }

    fun ligar(numero: String): String = try {
        ctx.startActivity(nova(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero"))))
        "Discador aberto com $numero. Diga: toque em ligar."
    } catch (e: Exception) {
        "Não encontrei o discador neste celular."
    }

    fun sms(numero: String, texto: String): String = try {
        val i = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$numero"))
        if (texto.isNotBlank()) i.putExtra("sms_body", texto)
        ctx.startActivity(nova(i))
        if (texto.isBlank()) "Conversa aberta com $numero."
        else "Mensagem pronta para $numero. Diga: toque em enviar."
    } catch (e: Exception) {
        "Não encontrei o aplicativo de mensagens neste celular."
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
