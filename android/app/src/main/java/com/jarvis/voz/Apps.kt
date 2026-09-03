package com.jarvis.voz

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/** Encontra e abre qualquer aplicativo instalado a partir do nome falado. */
class Apps(private val ctx: Context) {

    data class Instalado(val nome: String, val pacote: String, val chave: String)

    private var cache: List<Instalado> = emptyList()

    fun listar(): List<Instalado> {
        if (cache.isNotEmpty()) return cache
        val pm = ctx.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        cache = pm.queryIntentActivities(intent, 0).mapNotNull { r ->
            val rotulo = r.loadLabel(pm)?.toString() ?: return@mapNotNull null
            Instalado(rotulo, r.activityInfo.packageName, Texto.normalizar(rotulo))
        }.distinctBy { it.pacote }.sortedBy { it.nome }
        return cache
    }

    /** Casa o nome falado com o app instalado mais parecido. */
    fun procurar(falado: String): Instalado? {
        val alvo = Texto.normalizar(falado)
            .replace(Regex("^(abrir?|abra|abre|inicie|iniciar|va para|entrar? n[oa])\\s+"), "")
            .replace(Regex("^(o|a|os|as|no|na|aplicativo|app)\\s+"), "")
            .trim()
        if (alvo.isEmpty()) return null
        val apps = listar()

        apps.firstOrNull { it.chave == alvo }?.let { return it }
        apps.firstOrNull { it.chave.startsWith(alvo) }?.let { return it }
        apps.firstOrNull { it.chave.contains(alvo) }?.let { return it }
        apps.firstOrNull { alvo.contains(it.chave) && it.chave.length >= 4 }?.let { return it }

        // último recurso: menor distância de edição, aceitando pequenas trocas
        return apps.map { it to distancia(alvo, it.chave) }
            .filter { it.second <= Math.max(2, alvo.length / 4) }
            .minByOrNull { it.second }?.first
    }

    fun abrir(app: Instalado): Boolean {
        val i = ctx.packageManager.getLaunchIntentForPackage(app.pacote) ?: return false
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try { ctx.startActivity(i); true } catch (e: Exception) { false }
    }

    fun fecharTeclado() {}

    private fun distancia(a: String, b: String): Int {
        val d = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) d[i][0] = i
        for (j in 0..b.length) d[0][j] = j
        for (i in 1..a.length) for (j in 1..b.length) {
            val custo = if (a[i - 1] == b[j - 1]) 0 else 1
            d[i][j] = minOf(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + custo)
        }
        return d[a.length][b.length]
    }
}
