package com.jarvis.voz

/** Apoio a pregações e estudos bíblicos, em formato de roteiro detalhado. */
object Biblia {

    private val VERSOS = listOf(
        "Salmos 23:1" to "O Senhor é o meu pastor; nada me faltará.",
        "João 3:16" to "Porque Deus amou o mundo de tal maneira que deu o seu Filho unigênito, para que todo aquele que nele crê não pereça, mas tenha a vida eterna.",
        "Filipenses 4:13" to "Posso todas as coisas naquele que me fortalece.",
        "Isaías 41:10" to "Não temas, porque eu sou contigo; não te assombres, porque eu sou o teu Deus.",
        "Provérbios 3:5" to "Confia no Senhor de todo o teu coração e não te estribes no teu próprio entendimento.",
        "Romanos 8:28" to "Todas as coisas contribuem juntamente para o bem daqueles que amam a Deus.",
        "Josué 1:9" to "Sê forte e corajoso; não temas, nem te espantes, porque o Senhor teu Deus é contigo.",
        "Mateus 6:33" to "Buscai primeiro o reino de Deus e a sua justiça, e todas estas coisas vos serão acrescentadas.",
        "Salmos 119:105" to "Lâmpada para os meus pés é a tua palavra e luz para o meu caminho.",
        "2 Timóteo 3:16" to "Toda a Escritura é divinamente inspirada e proveitosa para ensinar, repreender, corrigir e instruir em justiça."
    )

    fun versiculo(): Pair<String, String> = VERSOS.random()

    fun esboco(tema: String): String {
        val t = tema.trim().ifEmpty { "a fé" }
        val T = t.replaceFirstChar { it.uppercase() }
        return """
            ESBOÇO DE PREGAÇÃO — ${T.uppercase()}

            I. ABERTURA
               1. Saudação e oração inicial
               2. Leitura do texto base
               3. Tese central: o que a Palavra ensina sobre $t

            II. CONTEXTO DO TEXTO
               1. Autor, destinatários e data
               2. Situação histórica e cultural
               3. Lugar do texto dentro do livro

            III. DESENVOLVIMENTO (3 pontos)
               1. Primeiro ponto — a definição bíblica de $t
                  a) Texto de apoio
                  b) Explicação do termo original
                  c) Aplicação imediata
               2. Segundo ponto — os obstáculos a $t
                  a) Exemplo bíblico negativo
                  b) Diagnóstico do coração
                  c) Advertência pastoral
               3. Terceiro ponto — como viver $t hoje
                  a) Exemplo bíblico positivo
                  b) Passos práticos durante a semana
                  c) Promessa vinculada à obediência

            IV. ILUSTRAÇÃO CENTRAL
               1. História, testemunho ou analogia
               2. Ponte da ilustração para o texto

            V. APLICAÇÃO PESSOAL
               1. Para o novo convertido
               2. Para o membro maduro
               3. Para quem ainda não creu

            VI. CONCLUSÃO
               1. Recapitulação dos três pontos
               2. Apelo e chamada à decisão
               3. Oração final e bênção
        """.trimIndent()
    }

    fun plano(tema: String): String {
        val t = tema.trim().ifEmpty { "a vida cristã" }
        val dias = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo")
        val eixos = listOf(
            "Fundamento bíblico", "Exemplos do Antigo Testamento", "Ensino de Jesus",
            "Ensino apostólico", "Obstáculos e advertências", "Aplicação prática",
            "Consolidação e oração"
        )
        val sb = StringBuilder("PLANO SEMANAL DE ESTUDO — ${t.uppercase()}\n\n")
        dias.forEachIndexed { i, d ->
            sb.append("${d.uppercase()} — ${eixos[i]}\n")
            sb.append("   1. Leitura do texto principal\n")
            sb.append("   2. Três perguntas de observação\n")
            sb.append("   3. Uma verdade para memorizar\n")
            sb.append("   4. Um passo de obediência para o dia\n\n")
        }
        return sb.toString().trimEnd()
    }
}
