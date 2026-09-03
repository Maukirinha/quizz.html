package com.jarvis.voz

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * É este serviço que permite comandar o celular inteiro: navegar, tocar em
 * botões pelo nome, digitar, rolar a tela, abrir notificações, tirar print
 * e bloquear o aparelho — em qualquer aplicativo, não só neste.
 */
class Acessibilidade : AccessibilityService() {

    companion object {
        @Volatile private var instancia: Acessibilidade? = null

        fun ativa(): Boolean = instancia != null

        fun get(): Acessibilidade? = instancia

        /** Confere no sistema se o usuário já autorizou o serviço. */
        fun autorizada(ctx: Context): Boolean {
            val ativos = Settings.Secure.getString(
                ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            return ativos.contains("${ctx.packageName}/${Acessibilidade::class.java.name}")
        }

        fun abrirConfiguracoes(ctx: Context) {
            ctx.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instancia = this
    }

    override fun onDestroy() {
        instancia = null
        super.onDestroy()
    }

    override fun onAccessibilityEvent(evento: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    /* ---------------- navegação global ---------------- */

    fun voltar() = performGlobalAction(GLOBAL_ACTION_BACK)
    fun inicio() = performGlobalAction(GLOBAL_ACTION_HOME)
    fun recentes() = performGlobalAction(GLOBAL_ACTION_RECENTS)
    fun notificacoes() = performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    fun ajustesRapidos() = performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)

    fun bloquearTela(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN) else false

    fun print(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT) else false

    /* ---------------- interação com a tela ---------------- */

    /** Toca no primeiro elemento visível cujo texto contenha o alvo. */
    fun tocarEmTexto(alvo: String): Boolean {
        val raiz = rootInActiveWindow ?: return false
        val procurado = Texto.normalizar(alvo)
        val candidatos = mutableListOf<AccessibilityNodeInfo>()
        coletar(raiz, candidatos)
        for (no in candidatos) {
            val rotulo = Texto.normalizar(
                (no.text ?: no.contentDescription ?: "").toString()
            )
            if (rotulo.isNotEmpty() && rotulo.contains(procurado)) {
                if (clicar(no)) return true
            }
        }
        return false
    }

    /** Escreve em um campo de texto que esteja em foco. */
    fun digitar(conteudo: String): Boolean {
        val foco = rootInActiveWindow?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
            ?: return false
        val args = Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, conteudo)
        }
        return foco.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
    }

    fun rolar(paraBaixo: Boolean): Boolean {
        val raiz = rootInActiveWindow ?: return false
        val acao = if (paraBaixo) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
                   else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
        return rolavel(raiz)?.performAction(acao) ?: false
    }

    /** Lê em voz alta o que está escrito na tela atual. */
    fun lerTela(): String {
        val raiz = rootInActiveWindow ?: return ""
        val achados = mutableListOf<AccessibilityNodeInfo>()
        coletar(raiz, achados)
        return achados
            .mapNotNull { it.text?.toString()?.trim() }
            .filter { it.length in 2..120 }
            .distinct()
            .take(25)
            .joinToString(". ")
    }

    /* ---------------- apoio ---------------- */

    private fun coletar(no: AccessibilityNodeInfo?, saida: MutableList<AccessibilityNodeInfo>) {
        if (no == null || saida.size > 400) return
        if (!TextUtils.isEmpty(no.text) || !TextUtils.isEmpty(no.contentDescription)) saida += no
        for (i in 0 until no.childCount) coletar(no.getChild(i), saida)
    }

    /** Sobe na hierarquia até achar um ancestral que aceite clique. */
    private fun clicar(no: AccessibilityNodeInfo): Boolean {
        var atual: AccessibilityNodeInfo? = no
        var nivel = 0
        while (atual != null && nivel < 6) {
            if (atual.isClickable) return atual.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            atual = atual.parent
            nivel++
        }
        return false
    }

    private fun rolavel(no: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (no == null) return null
        if (no.isScrollable) return no
        for (i in 0 until no.childCount) rolavel(no.getChild(i))?.let { return it }
        return null
    }
}
