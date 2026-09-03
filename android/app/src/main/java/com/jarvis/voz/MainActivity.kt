package com.jarvis.voz

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/** Tela principal: estado do núcleo, permissões e acionamento manual. */
class MainActivity : AppCompatActivity() {

    private lateinit var reator: ReatorView
    private lateinit var rotulo: TextView
    private lateinit var dica: TextView
    private lateinit var ouvido: TextView
    private lateinit var resposta: TextView
    private lateinit var btnEscuta: Button
    private lateinit var btnAcesso: Button
    private lateinit var mem: Memoria

    private val PEDIDO = 7

    override fun onCreate(estadoSalvo: Bundle?) {
        super.onCreate(estadoSalvo)
        mem = Memoria(this)
        setContentView(montarTela())

        JarvisService.aoAtualizar = { runOnUiThread { pintar() } }
        pedirPermissoes()
        pintar()
    }

    override fun onResume() { super.onResume(); pintar() }

    override fun onDestroy() {
        if (JarvisService.aoAtualizar != null) JarvisService.aoAtualizar = null
        super.onDestroy()
    }

    /* ---------------- montagem da tela ---------------- */

    private fun montarTela(): ViewGroup {
        val raiz = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#050A12"))
            setPadding(dp(18), dp(26), dp(18), dp(18))
        }

        raiz.addView(TextView(this).apply {
            text = "J . A . R . V . I . S ."
            setTextColor(Color.parseColor("#DFF2FF"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            letterSpacing = 0.18f
            gravity = Gravity.CENTER
        })

        reator = ReatorView(this)
        raiz.addView(reator, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(260)).apply { topMargin = dp(6) })

        rotulo = texto("MODO ESPERA", 15f, "#39D7FF", Gravity.CENTER, 0.3f)
        dica = texto("toque em Ativar escuta", 12f, "#7FA6BD", Gravity.CENTER, 0f)
        raiz.addView(rotulo); raiz.addView(dica)

        ouvido = texto("—", 15f, "#FFFFFF", Gravity.START, 0f)
        resposta = texto("", 14f, "#9FC4D8", Gravity.START, 0f)

        raiz.addView(espaco(dp(18)))
        raiz.addView(texto("ESCUTA", 10f, "#0A6F92", Gravity.START, 0.4f))
        raiz.addView(ouvido)
        raiz.addView(espaco(dp(12)))
        raiz.addView(texto("RESPOSTA", 10f, "#0A6F92", Gravity.START, 0.4f))
        raiz.addView(resposta)
        raiz.addView(espaco(dp(20)))

        btnEscuta = botao("ATIVAR ESCUTA") { alternarEscuta() }
        btnAcesso = botao("ATIVAR CONTROLE DO CELULAR") { Acessibilidade.abrirConfiguracoes(this) }
        raiz.addView(btnEscuta)
        raiz.addView(btnAcesso)
        raiz.addView(botao("FALAR AGORA") {
            iniciarServico(JarvisService.ACAO_FALAR)
        })
        raiz.addView(botao("MENU DE COMANDOS") {
            startActivity(Intent(this, MenuActivity::class.java))
        })

        return ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#050A12"))
            addView(raiz, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    /* ---------------- estado ---------------- */

    private fun pintar() {
        val e = JarvisService.estado
        reator.estado = e
        rotulo.text = when (e) {
            "ouvindo", "captando" -> "MONITORANDO"
            "ativo" -> "ESCUTANDO VOCÊ"
            "processando" -> "PROCESSANDO"
            "falando" -> "RESPONDENDO"
            "bloqueado" -> "SEM MICROFONE"
            else -> "MODO ESPERA"
        }
        dica.text = when (e) {
            "ouvindo", "captando" -> "diga “Jarvis”"
            "ativo" -> "pode falar o comando"
            "bloqueado" -> "libere o microfone nas configurações"
            "parado" -> "toque em Ativar escuta"
            else -> "…"
        }
        ouvido.text = JarvisService.ultimaFala.ifBlank { "—" }
        resposta.text = JarvisService.ultimaResposta

        btnEscuta.text = if (e == "parado") "ATIVAR ESCUTA" else "DESATIVAR ESCUTA"
        btnAcesso.text = if (Acessibilidade.ativa()) "CONTROLE DO CELULAR ATIVO"
                         else "ATIVAR CONTROLE DO CELULAR"
        btnAcesso.setTextColor(Color.parseColor(
            if (Acessibilidade.ativa()) "#37F0A0" else "#FFB648"))
    }

    private fun alternarEscuta() {
        if (JarvisService.estado == "parado") iniciarServico(JarvisService.ACAO_INICIAR)
        else iniciarServico(JarvisService.ACAO_PARAR)
    }

    private fun iniciarServico(acao: String) {
        val i = Intent(this, JarvisService::class.java).setAction(acao)
        if (acao == JarvisService.ACAO_PARAR) startService(i) else startForegroundService(i)
    }

    /* ---------------- permissões ---------------- */

    private fun pedirPermissoes() {
        val faltando = mutableListOf<String>()
        val desejadas = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            desejadas += Manifest.permission.POST_NOTIFICATIONS

        for (p in desejadas)
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                faltando += p

        if (faltando.isNotEmpty()) requestPermissions(faltando.toTypedArray(), PEDIDO)
    }

    /* ---------------- utilidades de layout ---------------- */

    private fun dp(v: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics).toInt()

    private fun texto(t: String, tam: Float, cor: String, alinhamento: Int, esp: Float) =
        TextView(this).apply {
            text = t
            setTextColor(Color.parseColor(cor))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, tam)
            gravity = alinhamento
            letterSpacing = esp
        }

    private fun botao(rotuloBotao: String, aoTocar: () -> Unit) = Button(this).apply {
        text = rotuloBotao
        setTextColor(Color.parseColor("#39D7FF"))
        setBackgroundColor(Color.parseColor("#0B1C2E"))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
        letterSpacing = 0.1f
        setOnClickListener { aoTocar() }
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(10) }
    }

    private fun espaco(altura: Int) = TextView(this).apply {
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, altura)
    }
}
