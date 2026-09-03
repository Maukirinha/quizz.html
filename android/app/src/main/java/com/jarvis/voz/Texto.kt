package com.jarvis.voz

import java.text.Normalizer

/** Normalização de fala, números por extenso e durações em português. */
object Texto {

    fun normalizar(s: String): String =
        Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("[\\p{InCombiningDiacriticalMarks}]"), "")
            .replace(Regex("[^a-z0-9\\s:.,%+\\-]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    private val UNIDADES = mapOf(
        "zero" to 0, "um" to 1, "uma" to 1, "dois" to 2, "duas" to 2, "tres" to 3,
        "quatro" to 4, "cinco" to 5, "seis" to 6, "sete" to 7, "oito" to 8,
        "nove" to 9, "dez" to 10, "onze" to 11, "doze" to 12, "treze" to 13,
        "catorze" to 14, "quatorze" to 14, "quinze" to 15, "dezesseis" to 16,
        "dezessete" to 17, "dezoito" to 18, "dezenove" to 19, "vinte" to 20,
        "trinta" to 30, "quarenta" to 40, "cinquenta" to 50, "sessenta" to 60,
        "setenta" to 70, "oitenta" to 80, "noventa" to 90, "cem" to 100,
        "cento" to 100, "duzentos" to 200, "trezentos" to 300, "quatrocentos" to 400,
        "quinhentos" to 500, "seiscentos" to 600, "setecentos" to 700,
        "oitocentos" to 800, "novecentos" to 900, "mil" to 1000, "meia" to 30
    )

    /** Converte "quarenta e cinco" ou "45" em número. Devolve null se não houver número. */
    fun numero(texto: String): Int? {
        var total = 0
        var atual = 0
        var achou = false
        for (p in normalizar(texto).split(" ")) {
            when {
                p.matches(Regex("\\d+")) -> { atual += p.toInt(); achou = true }
                p == "e" -> {}
                UNIDADES.containsKey(p) -> {
                    achou = true
                    val v = UNIDADES.getValue(p)
                    if (v == 1000) { atual = (if (atual == 0) 1 else atual) * 1000; total += atual; atual = 0 }
                    else atual += v
                }
            }
        }
        return if (achou) total + atual else null
    }

    /** Extrai duração falada em segundos: "10 minutos", "uma hora e meia". */
    fun duracaoSegundos(texto: String): Int? {
        val t = normalizar(texto)
        var seg = 0
        var achou = false
        val re = Regex("(\\d+|[a-z]+(?:\\s+e\\s+[a-z]+)*)\\s*(horas?|h|minutos?|min|segundos?|seg|s)\\b")
        for (m in re.findAll(t)) {
            val n = numero(m.groupValues[1]) ?: continue
            achou = true
            val u = m.groupValues[2]
            seg += when {
                u.startsWith("h") -> n * 3600
                u.startsWith("m") -> n * 60
                else -> n
            }
        }
        return if (achou) seg else null
    }

    fun duracaoPorExtenso(s: Int): String {
        val h = s / 3600; val m = (s % 3600) / 60; val x = s % 60
        val partes = mutableListOf<String>()
        if (h > 0) partes += "$h ${if (h == 1) "hora" else "horas"}"
        if (m > 0) partes += "$m ${if (m == 1) "minuto" else "minutos"}"
        if (x > 0 || partes.isEmpty()) partes += "$x ${if (x == 1) "segundo" else "segundos"}"
        return partes.joinToString(" e ")
    }

    /** Avaliador de expressões simples, sem biblioteca externa. */
    fun calcular(falado: String): Double? {
        var t = normalizar(falado)
            .replace(Regex("^(jarvis\\s+)?(calcule|calcular|quanto e|quanto da|resultado de)\\s*"), "")
            .replace(Regex("\\bmais\\b|\\bsomado com\\b"), "+")
            .replace(Regex("\\bmenos\\b"), "-")
            .replace(Regex("\\bvezes\\b|\\bmultiplicado por\\b"), "*")
            .replace(Regex("\\bdividido por\\b|\\bsobre\\b"), "/")
            .replace(Regex("\\bvirgula\\b|\\bponto\\b"), ".")

        Regex("(\\d+(?:\\.\\d+)?)\\s*(?:%|por cento)\\s*de\\s*(\\d+(?:\\.\\d+)?)").find(t)?.let {
            return it.groupValues[1].toDouble() / 100.0 * it.groupValues[2].toDouble()
        }
        Regex("raiz (?:quadrada )?de\\s*(\\d+(?:\\.\\d+)?)").find(t)?.let {
            return Math.sqrt(it.groupValues[1].toDouble())
        }

        t = Regex("[a-z]+(?:\\s+e\\s+[a-z]+)*").replace(t) { m ->
            numero(m.value)?.toString() ?: " "
        }
        t = t.replace(Regex("[^0-9+\\-*/().\\s]"), "").trim()
        if (!t.contains(Regex("\\d")) || !t.contains(Regex("[+\\-*/]"))) return null
        return try { Aritmetica(t).avaliar() } catch (e: Exception) { null }
    }

    fun numeroBonito(n: Double): String =
        if (Math.abs(n % 1.0) < 1e-9) n.toLong().toString()
        else String.format("%.2f", n).replace(".", " vírgula ")

    /** Analisador recursivo: soma, subtração, multiplicação, divisão e parênteses. */
    private class Aritmetica(private val exp: String) {
        private var i = 0
        fun avaliar(): Double { val v = soma(); pular(); return v }
        private fun pular() { while (i < exp.length && exp[i] == ' ') i++ }
        private fun soma(): Double {
            var v = produto()
            while (true) {
                pular()
                when {
                    i < exp.length && exp[i] == '+' -> { i++; v += produto() }
                    i < exp.length && exp[i] == '-' -> { i++; v -= produto() }
                    else -> return v
                }
            }
        }
        private fun produto(): Double {
            var v = termo()
            while (true) {
                pular()
                when {
                    i < exp.length && exp[i] == '*' -> { i++; v *= termo() }
                    i < exp.length && exp[i] == '/' -> { i++; v /= termo() }
                    else -> return v
                }
            }
        }
        private fun termo(): Double {
            pular()
            if (i < exp.length && exp[i] == '(') { i++; val v = soma(); pular(); if (i < exp.length && exp[i] == ')') i++; return v }
            if (i < exp.length && exp[i] == '-') { i++; return -termo() }
            val ini = i
            while (i < exp.length && (exp[i].isDigit() || exp[i] == '.')) i++
            if (ini == i) throw IllegalArgumentException("expressão inválida")
            return exp.substring(ini, i).toDouble()
        }
    }
}
