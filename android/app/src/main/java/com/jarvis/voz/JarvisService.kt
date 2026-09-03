package com.jarvis.voz

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper

/**
 * Serviço em primeiro plano: mantém a escuta viva com a tela apagada e em
 * segundo plano, detecta a palavra de ativação e executa os comandos.
 */
class JarvisService : Service() {

    companion object {
        const val CANAL = "jarvis_escuta"
        const val ID_NOTIF = 42
        const val ACAO_INICIAR = "com.jarvis.voz.INICIAR"
        const val ACAO_PARAR = "com.jarvis.voz.PARAR"
        const val ACAO_FALAR = "com.jarvis.voz.FALAR"

        /** Espelho do estado, para a tela principal desenhar sem se acoplar ao serviço. */
        @Volatile var estado: String = "parado"
        @Volatile var ultimaFala: String = ""
        @Volatile var ultimaResposta: String = ""
        var aoAtualizar: (() -> Unit)? = null

        private val ATIVACOES = listOf("jarvis", "jarves", "jarbas", "darvis", "javis", "jarvi")
    }

    private lateinit var mem: Memoria
    private lateinit var fala: Fala
    private lateinit var escuta: Escuta
    private lateinit var comandos: Comandos
    private lateinit var aparelho: Aparelho

    private val mao = Handler(Looper.getMainLooper())
    private var janelaAberta = false
    private var fechaJanela: Runnable? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        mem = Memoria(this)
        aparelho = Aparelho(this)

        fala = Fala(this) { mao.post { escuta.retomar() } }

        comandos = Comandos(
            ctx = this,
            mem = mem,
            aparelho = aparelho,
            apps = Apps(this),
            com = Comunicacao(this),
            aoDesligar = { pararTudo() },
            aoAutonomo = { mem.autonomo = it },
            aoCalar = { fala.calar() }
        )

        escuta = Escuta(
            ctx = this,
            aoParcial = { texto ->
                ultimaFala = texto
                if (!janelaAberta && !mem.autonomo && temAtivacao(Texto.normalizar(texto))) abrirJanela()
                notificarTela()
            },
            aoFinal = { texto -> processar(texto) },
            aoEstado = { e -> estado = if (janelaAberta && e == "ouvindo") "ativo" else e; notificarTela() },
            aoErro = { msg -> ultimaResposta = msg; notificarTela() }
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACAO_PARAR -> { pararTudo(); return START_NOT_STICKY }
            ACAO_FALAR -> { abrirJanela(); dizer("Pois não.") }
        }
        subirEmPrimeiroPlano()
        if (!escuta.ligada()) {
            if (escuta.ligar()) {
                estado = "ouvindo"
                dizer("Sistema J.A.R.V.I.S. online, ${mem.nome}. Diga Jarvis para me acionar.")
            }
        }
        notificarTela()
        return START_STICKY
    }

    override fun onDestroy() {
        escuta.desligar()
        fala.encerrar()
        estado = "parado"
        notificarTela()
        super.onDestroy()
    }

    /* ---------------- palavra de ativação ---------------- */

    private fun temAtivacao(t: String): Boolean = ATIVACOES.any { t.contains(it) }

    private fun abrirJanela() {
        janelaAberta = true
        estado = "ativo"
        aparelho.vibrar(40)
        notificarTela()
        fechaJanela?.let { mao.removeCallbacks(it) }
        fechaJanela = Runnable {
            janelaAberta = false
            if (escuta.ligada()) estado = "ouvindo"
            notificarTela()
        }
        mao.postDelayed(fechaJanela!!, 12000)
    }

    /* ---------------- processamento ---------------- */

    private fun processar(frase: String) {
        val t = Texto.normalizar(frase)
        var comando = frase

        if (!janelaAberta && !mem.autonomo) {
            if (!temAtivacao(t)) return                       // conversa ambiente: ignora
            val chave = ATIVACOES.first { t.contains(it) }
            val resto = t.substringAfter(chave).trim()
            if (resto.length <= 2) {                          // só chamou o nome
                abrirJanela()
                dizer(listOf("Pois não.", "Às ordens.", "Sim?", "Estou ouvindo.").random())
                return
            }
            comando = frase.substring(
                minOf(frase.length, frase.lowercase().indexOf(chave).coerceAtLeast(0) + chave.length)
            ).trim().ifEmpty { frase }
        } else if (mem.autonomo && temAtivacao(t)) {
            val chave = ATIVACOES.first { t.contains(it) }
            comando = frase.substring(
                minOf(frase.length, frase.lowercase().indexOf(chave).coerceAtLeast(0) + chave.length)
            ).trim().ifEmpty { frase }
        }

        fecharJanela()
        ultimaFala = comando
        estado = "processando"
        notificarTela()

        val r = comandos.executar(comando)
        ultimaResposta = r ?: "Não reconheci esse comando. Abra o menu para ver a lista completa."
        dizer(ultimaResposta)
        notificarTela()
    }

    private fun fecharJanela() {
        janelaAberta = false
        fechaJanela?.let { mao.removeCallbacks(it) }
    }

    private fun dizer(texto: String) {
        if (texto.isBlank()) { escuta.retomar(); return }
        if (mem.mudo) return
        escuta.pausar()
        estado = "falando"
        notificarTela()
        fala.dizer(texto)
    }

    private fun pararTudo() {
        escuta.desligar()
        fala.calar()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun notificarTela() = mao.post { aoAtualizar?.invoke() }

    /* ---------------- notificação obrigatória ---------------- */

    private fun subirEmPrimeiroPlano() {
        val nm = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(CANAL, getString(R.string.canal_nome),
                NotificationManager.IMPORTANCE_LOW)
            canal.setShowBadge(false)
            nm.createNotificationChannel(canal)
        }

        val abrir = PendingIntent.getActivity(this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val parar = PendingIntent.getService(this, 1,
            Intent(this, JarvisService::class.java).setAction(ACAO_PARAR),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val n: Notification = Notification.Builder(this, CANAL)
            .setContentTitle("J.A.R.V.I.S. ativo")
            .setContentText("Escutando. Diga \"Jarvis\" a qualquer momento.")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(abrir)
            .addAction(Notification.Action.Builder(null, "Encerrar", parar).build())
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startForeground(ID_NOTIF, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        else startForeground(ID_NOTIF, n)
    }
}
