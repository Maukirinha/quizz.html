package com.jarvis.voz

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

/** Reator de arco desenhado em tempo real, reagindo ao estado do assistente. */
class ReatorView @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : View(ctx, attrs) {

    private val tinta = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val cheia = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val caixa = RectF()

    private var angulo = 0f
    private var energia = 0f
    private var alvo = 0.12f

    var estado: String = "parado"
        set(v) {
            field = v
            alvo = when (v) {
                "ouvindo", "captando" -> 0.45f
                "ativo" -> 0.80f
                "processando" -> 0.85f
                "falando" -> 0.95f
                "bloqueado", "erro" -> 0.30f
                else -> 0.12f
            }
            invalidate()
        }

    private fun cor(): Int = when (estado) {
        "ouvindo", "captando" -> Color.parseColor("#39D7FF")
        "ativo" -> Color.parseColor("#FFB648")
        "processando" -> Color.parseColor("#B072FF")
        "falando" -> Color.parseColor("#37F0A0")
        "bloqueado", "erro" -> Color.parseColor("#FF4D5E")
        else -> Color.parseColor("#3A5A70")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        energia += (alvo - energia) * 0.06f
        angulo += 0.6f + energia * 2.4f

        val cx = width / 2f
        val cy = height / 2f
        val raio = Math.min(width, height) / 2f * 0.92f
        val c = cor()
        val pulso = 1f + Math.sin(System.currentTimeMillis() / 380.0).toFloat() * 0.035f

        // anel externo segmentado
        tinta.color = c
        tinta.strokeWidth = raio * 0.012f
        caixa.set(cx - raio, cy - raio, cx + raio, cy + raio)
        for (i in 0 until 16) {
            tinta.alpha = (60 + energia * 150).toInt().coerceIn(0, 255)
            canvas.drawArc(caixa, angulo + i * 22.5f, 12f, false, tinta)
        }

        // anel de progresso, girando ao contrário
        val r2 = raio * 0.86f
        caixa.set(cx - r2, cy - r2, cx + r2, cy + r2)
        tinta.strokeWidth = raio * 0.025f
        for (i in 0 until 4) {
            tinta.alpha = (45 + energia * 130).toInt().coerceIn(0, 255)
            canvas.drawArc(caixa, -angulo * 1.7f + i * 90f, 52f, false, tinta)
        }

        // barras radiais
        tinta.strokeWidth = raio * 0.010f
        val n = 48
        for (i in 0 until n) {
            val a = Math.toRadians((i * 360.0 / n) + angulo * 0.5)
            val amp = ((Math.sin(i * 1.7 + System.currentTimeMillis() / 160.0) * 0.5 + 0.5) * energia).toFloat()
            val r1 = raio * 0.54f
            val rf = r1 + raio * (0.04f + amp * 0.16f)
            tinta.alpha = (40 + amp * 150).toInt().coerceIn(0, 255)
            canvas.drawLine(
                cx + (Math.cos(a) * r1).toFloat(), cy + (Math.sin(a) * r1).toFloat(),
                cx + (Math.cos(a) * rf).toFloat(), cy + (Math.sin(a) * rf).toFloat(), tinta
            )
        }

        // núcleo
        val rn = raio * 0.46f * pulso
        cheia.shader = RadialGradient(cx, cy, rn, c, Color.TRANSPARENT, Shader.TileMode.CLAMP)
        cheia.alpha = (60 + energia * 160).toInt().coerceIn(0, 255)
        canvas.drawCircle(cx, cy, rn, cheia)
        cheia.shader = null

        tinta.alpha = (120 + energia * 120).toInt().coerceIn(0, 255)
        tinta.strokeWidth = raio * 0.014f
        canvas.drawCircle(cx, cy, raio * 0.34f * pulso, tinta)

        postInvalidateOnAnimation()
    }
}
