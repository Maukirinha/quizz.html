package com.jarvis.voz

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Menu detalhado: todos os comandos, agrupados por módulo. */
class MenuActivity : AppCompatActivity() {

    override fun onCreate(estadoSalvo: Bundle?) {
        super.onCreate(estadoSalvo)

        val mem = Memoria(this)
        val comandos = Comandos(
            this, mem, Aparelho(this), Apps(this), Comunicacao(this),
            aoDesligar = {}, aoAutonomo = {}, aoCalar = {}
        )

        val raiz = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#03070D"))
            setPadding(dp(20), dp(28), dp(20), dp(40))
        }

        raiz.addView(titulo("MENU GERAL DE COMANDOS", 16f, "#39D7FF", 4f))
        raiz.addView(corpo(
            "Diga “Jarvis” e espere o sinal, ou segure o botão Falar na tela principal. " +
            "Com o modo autônomo ligado, qualquer frase é interpretada como comando.",
            "#7FA6BD"))

        for (modulo in comandos.modulos()) {
            val itens = comandos.habilidades().filter { it.modulo == modulo }
            raiz.addView(espaco(dp(22)))
            raiz.addView(titulo("MÓDULO ${modulo.uppercase()} — ${itens.size} COMANDO(S)", 13f, "#39D7FF", 2f))
            itens.forEachIndexed { i, h ->
                raiz.addView(espaco(dp(12)))
                raiz.addView(titulo("${i + 1}. ${h.nome}", 15f, "#FFB648", 0f))
                h.exemplos.forEach { raiz.addView(corpo("“$it”", "#9FC4D8")) }
            }
        }

        setContentView(ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#03070D"))
            addView(raiz, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        })
    }

    private fun dp(v: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics).toInt()

    private fun titulo(t: String, tam: Float, cor: String, espacamento: Float) = TextView(this).apply {
        text = t
        setTextColor(Color.parseColor(cor))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, tam)
        letterSpacing = espacamento / 100f
        gravity = Gravity.START
    }

    private fun corpo(t: String, cor: String) = TextView(this).apply {
        text = t
        setTextColor(Color.parseColor(cor))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
        setPadding(dp(10), dp(2), 0, 0)
    }

    private fun espaco(altura: Int) = TextView(this).apply {
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, altura)
    }
}
