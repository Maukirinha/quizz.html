package com.jarvis.voz

import android.content.Context
import org.json.JSONArray

/** Guarda configuração, notas e listas no próprio aparelho. */
class Memoria(ctx: Context) {

    private val p = ctx.getSharedPreferences("jarvis", Context.MODE_PRIVATE)

    var nome: String
        get() = p.getString("nome", "Senhor") ?: "Senhor"
        set(v) = p.edit().putString("nome", v).apply()

    var autonomo: Boolean
        get() = p.getBoolean("autonomo", false)
        set(v) = p.edit().putBoolean("autonomo", v).apply()

    var mudo: Boolean
        get() = p.getBoolean("mudo", false)
        set(v) = p.edit().putBoolean("mudo", v).apply()

    var iniciarNoBoot: Boolean
        get() = p.getBoolean("boot", true)
        set(v) = p.edit().putBoolean("boot", v).apply()

    var comandos: Int
        get() = p.getInt("comandos", 0)
        set(v) = p.edit().putInt("comandos", v).apply()

    fun notas(): List<String> = ler("notas")
    fun addNota(t: String) = gravar("notas", listOf(t) + notas().take(199))
    fun limparNotas() = gravar("notas", emptyList())

    fun lista(nome: String): List<String> = ler("lista_$nome")
    fun addItem(nome: String, item: String) = gravar("lista_$nome", lista(nome) + item)
    fun limparLista(nome: String) = gravar("lista_$nome", emptyList())

    private fun ler(chave: String): List<String> {
        val bruto = p.getString(chave, "[]") ?: "[]"
        return try {
            val a = JSONArray(bruto)
            (0 until a.length()).map { a.getString(it) }
        } catch (e: Exception) { emptyList() }
    }

    private fun gravar(chave: String, valores: List<String>) {
        val a = JSONArray()
        valores.forEach { a.put(it) }
        p.edit().putString(chave, a.toString()).apply()
    }
}
