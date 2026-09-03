package com.jarvis.voz

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Roteador de comandos. Cada habilidade declara o módulo a que pertence,
 * exemplos para o menu, o teste de correspondência e a execução.
 * A primeira habilidade cujo teste é verdadeiro atende o comando.
 */
class Comandos(
    private val ctx: Context,
    private val mem: Memoria,
    private val aparelho: Aparelho,
    private val apps: Apps,
    private val com: Comunicacao,
    private val aoDesligar: () -> Unit,
    private val aoAutonomo: (Boolean) -> Unit,
    private val aoCalar: () -> Unit
) {

    class Habilidade(
        val id: String,
        val nome: String,
        val modulo: String,
        val exemplos: List<String>,
        val teste: (String) -> Boolean,
        val executa: (String, String) -> String
    )

    /** Guarda o último texto longo gerado, para poder ser compartilhado depois. */
    var ultimoTextoLongo: String = ""
        private set

    private val lista = mutableListOf<Habilidade>()
    fun habilidades(): List<Habilidade> = lista
    fun modulos(): List<String> = lista.map { it.modulo }.distinct()

    private fun add(
        id: String, nome: String, modulo: String, exemplos: List<String>,
        teste: (String) -> Boolean, executa: (String, String) -> String
    ) { lista += Habilidade(id, nome, modulo, exemplos, teste, executa) }

    private fun acess(): Acessibilidade? = Acessibilidade.get()

    private val semAcesso =
        "Preciso do serviço de acessibilidade ligado para comandar o celular inteiro. " +
        "Abra o aplicativo e toque em Ativar controle do celular."

    init {
        /* ============ NAVEGAÇÃO DO CELULAR ============ */

        add("nav.voltar", "Voltar", "Celular", listOf("volte", "voltar"),
            { it.matches(Regex("^(jarvis\\s+)?volt(e|ar|a)$")) || it.contains("tela anterior") },
            { _, _ -> if (acess()?.voltar() == true) "" else semAcesso })

        add("nav.inicio", "Ir para a tela inicial", "Celular",
            listOf("vá para o início", "tela inicial", "home"),
            { it.contains("tela inicial") || it.contains("va para o inicio") || it.contains("pagina inicial") },
            { _, _ -> if (acess()?.inicio() == true) "" else semAcesso })

        add("nav.recentes", "Aplicativos recentes", "Celular",
            listOf("apps recentes", "abra os recentes"),
            { it.contains("recentes") },
            { _, _ -> if (acess()?.recentes() == true) "" else semAcesso })

        add("nav.notificacoes", "Abrir notificações", "Celular",
            listOf("abra as notificações", "mostre as notificações"),
            { it.contains("notificac") },
            { _, _ -> if (acess()?.notificacoes() == true) "" else semAcesso })

        add("nav.ajustes", "Ajustes rápidos", "Celular",
            listOf("ajustes rápidos", "painel rápido"),
            { it.contains("ajustes rapidos") || it.contains("painel rapido") },
            { _, _ -> if (acess()?.ajustesRapidos() == true) "" else semAcesso })

        add("nav.tocar", "Tocar em um botão pelo nome", "Celular",
            listOf("toque em enviar", "clique em salvar", "aperte em continuar"),
            { it.contains(Regex("\\b(toque|clique|aperte|pressione) (em|no|na) ")) },
            { t, _ ->
                val alvo = Regex("(?:toque|clique|aperte|pressione) (?:em|no|na) (.+)$")
                    .find(t)?.groupValues?.get(1)?.trim() ?: ""
                val a = acess() ?: return@add semAcesso
                if (a.tocarEmTexto(alvo)) "" else "Não encontrei \"$alvo\" na tela."
            })

        add("nav.digitar", "Digitar texto", "Celular",
            listOf("digite bom dia pastor", "escreva estou a caminho"),
            { it.startsWith("digite ") || it.startsWith("escreva ") },
            { t, bruto ->
                val conteudo = Regex("^(?:digite|escreva)\\s+(.+)$", RegexOption.IGNORE_CASE)
                    .find(bruto)?.groupValues?.get(1)?.trim() ?: ""
                val a = acess() ?: return@add semAcesso
                if (a.digitar(conteudo)) "Digitado." else "Nenhum campo de texto está em foco."
            })

        add("nav.rolar", "Rolar a tela", "Celular",
            listOf("role para baixo", "role para cima"),
            { it.contains(Regex("\\brol(e|ar|a)\\b")) || it.contains("desce a tela") || it.contains("sobe a tela") },
            { t, _ ->
                val baixo = !t.contains("cima") && !t.contains("sobe")
                val a = acess() ?: return@add semAcesso
                if (a.rolar(baixo)) "" else "Esta tela não rola."
            })

        add("nav.ler", "Ler a tela em voz alta", "Celular",
            listOf("leia a tela", "o que está escrito na tela"),
            { it.contains("leia a tela") || it.contains("ler a tela") || it.contains("escrito na tela") },
            { _, _ ->
                val texto = acess()?.lerTela() ?: return@add semAcesso
                if (texto.isBlank()) "Não há texto legível nesta tela." else texto
            })

        add("nav.print", "Tirar print", "Celular",
            listOf("tire um print", "capture a tela"),
            { it.contains("print") || it.contains("captur") && it.contains("tela") },
            { _, _ ->
                val a = acess() ?: return@add semAcesso
                if (a.print()) "Print capturado." else "Este Android não permite print por comando."
            })

        add("nav.bloquear", "Bloquear o celular", "Celular",
            listOf("bloqueie o celular", "trave a tela"),
            { it.contains("bloque") || it.contains("trave a tela") },
            { _, _ ->
                val a = acess() ?: return@add semAcesso
                if (a.bloquearTela()) "Bloqueando." else "Este Android não permite bloquear por comando."
            })

        /* ============ APLICATIVOS ============ */

        add("app.abrir", "Abrir qualquer aplicativo", "Aplicativos",
            listOf("abra o whatsapp", "abra a bíblia", "abra o youtube", "abra a câmera"),
            { it.contains(Regex("^(jarvis\\s+)?(abra|abrir|abre|inicie|iniciar)\\b")) },
            { t, _ ->
                val achado = apps.procurar(t)
                when {
                    achado == null -> "Não encontrei esse aplicativo instalado."
                    apps.abrir(achado) -> "Abrindo ${achado.nome}."
                    else -> "Não consegui abrir ${achado.nome}."
                }
            })

        add("app.listar", "Listar aplicativos instalados", "Aplicativos",
            listOf("quais aplicativos eu tenho", "liste os aplicativos"),
            { it.contains("aplicativos") && it.contains(Regex("\\b(quais|liste|listar|tenho)\\b")) },
            { _, _ ->
                val l = apps.listar()
                "Você tem ${l.size} aplicativos instalados. Alguns deles: " +
                        l.take(12).joinToString(", ") { it.nome } + "."
            })

        /* ============ COMUNICAÇÃO ============ */

        add("com.ligar", "Ligar para um número", "Comunicação",
            listOf("ligue para 11 99999 8888"),
            { it.contains(Regex("\\b(ligue|ligar|telefone) para\\b")) },
            { t, _ ->
                val num = com.numeroDaFrase(t) ?: return@add "Diga o número dígito por dígito."
                com.ligar(num)
            })

        add("com.whatsapp", "Mandar WhatsApp", "Comunicação",
            listOf("whatsapp para 11 99999 8888 dizendo estou a caminho"),
            { it.contains("whats") },
            { t, bruto ->
                val num = com.numeroDaFrase(t.substringBefore("dizendo"))
                    ?: return@add "Diga o número dígito por dígito."
                val texto = Regex("dizendo\\s+(.+)$", RegexOption.IGNORE_CASE)
                    .find(bruto)?.groupValues?.get(1)?.trim().orEmpty()
                com.whatsapp(num, texto)
            })

        add("com.sms", "Mandar SMS", "Comunicação",
            listOf("mande sms para 11 99999 8888 dizendo cheguei"),
            { it.contains("sms") || it.contains("torpedo") },
            { t, bruto ->
                val num = com.numeroDaFrase(t.substringBefore("dizendo"))
                    ?: return@add "Diga o número dígito por dígito."
                val texto = Regex("dizendo\\s+(.+)$", RegexOption.IGNORE_CASE)
                    .find(bruto)?.groupValues?.get(1)?.trim().orEmpty()
                if (texto.isBlank()) "Diga também a mensagem, depois da palavra dizendo."
                else com.sms(num, texto)
            })

        add("com.pesquisar", "Pesquisar na internet", "Comunicação",
            listOf("pesquise sobre história da igreja"),
            { it.contains(Regex("\\b(pesquise|pesquisar|procure na internet|busque)\\b")) },
            { t, bruto ->
                val termo = Regex("(?:pesquise|pesquisar|procure|busque)\\s*(?:sobre|por)?\\s*(.+)$",
                    RegexOption.IGNORE_CASE).find(bruto)?.groupValues?.get(1)?.trim() ?: bruto
                if (com.buscarNaWeb(termo)) "Pesquisando por $termo." else "Não consegui abrir o navegador."
            })

        /* ============ CONTROLES DO APARELHO ============ */

        add("dev.lanterna", "Lanterna", "Aparelho", listOf("ligue a lanterna", "apague a lanterna"),
            { it.contains("lanterna") },
            { t, _ ->
                val ligar = !t.contains(Regex("\\b(apague|desligue|desliga|apagar)\\b"))
                if (aparelho.lanterna(ligar)) (if (ligar) "Lanterna ligada." else "Lanterna desligada.")
                else "Este celular não tem lanterna disponível."
            })

        add("dev.volume", "Volume", "Aparelho",
            listOf("volume em 70 por cento", "aumente o volume", "modo silencioso"),
            { it.contains("volume") || it.contains("silencioso") || it.contains("mudo o celular") },
            { t, _ ->
                if (t.contains("silencioso")) { aparelho.silencioso(true); return@add "Celular no silencioso." }
                if (t.contains("normal") || t.contains("som de volta")) { aparelho.silencioso(false); return@add "Som normal restaurado." }
                val n = Regex("(\\d{1,3})").find(t)?.groupValues?.get(1)?.toInt()
                val alvo = when {
                    n != null -> n
                    t.contains(Regex("\\b(aument|sobe|mais)")) -> aparelho.volumeAtual() + 20
                    t.contains(Regex("\\b(dimin|abaix|reduz|menos)")) -> aparelho.volumeAtual() - 20
                    t.contains("maximo") -> 100
                    else -> return@add "Diga: volume em setenta por cento."
                }
                "Volume em ${aparelho.volume(alvo)} por cento."
            })

        add("dev.brilho", "Brilho da tela", "Aparelho",
            listOf("brilho em 40 por cento", "aumente o brilho"),
            { it.contains("brilho") },
            { t, _ ->
                if (!aparelho.podeMexerNoBrilho()) {
                    aparelho.pedirPermissaoBrilho()
                    return@add "Preciso da permissão de modificar configurações. Abri a tela para você autorizar."
                }
                val n = Regex("(\\d{1,3})").find(t)?.groupValues?.get(1)?.toInt()
                val alvo = when {
                    n != null -> n
                    t.contains(Regex("\\b(aument|sobe|mais)")) -> aparelho.brilhoAtual() + 20
                    t.contains(Regex("\\b(dimin|abaix|reduz|menos)")) -> aparelho.brilhoAtual() - 20
                    t.contains("maximo") -> 100
                    else -> return@add "Diga: brilho em quarenta por cento."
                }
                if (aparelho.brilho(alvo)) "Brilho em ${alvo.coerceIn(1, 100)} por cento."
                else "Não consegui ajustar o brilho."
            })

        add("dev.wifi", "Wi-Fi, Bluetooth, dados e modo avião", "Aparelho",
            listOf("abra o wi-fi", "abra o bluetooth", "modo avião"),
            { it.contains("wifi") || it.contains("wi fi") || it.contains("bluetooth") ||
              it.contains("modo aviao") || it.contains("dados moveis") },
            { t, _ ->
                when {
                    t.contains("bluetooth") -> { aparelho.painelBluetooth(); "Abrindo o Bluetooth." }
                    t.contains("aviao") -> { aparelho.painelAviao(); "Abrindo o modo avião." }
                    t.contains("dados") -> { aparelho.painelDados(); "Abrindo os dados móveis." }
                    else -> { aparelho.painelWifi(); "Abrindo o Wi-Fi." }
                }
            })

        add("dev.naoperturbe", "Não perturbe", "Aparelho",
            listOf("ative o não perturbe", "desative o não perturbe"),
            { it.contains("nao perturbe") },
            { t, _ ->
                val ligar = !t.contains(Regex("\\b(desativ|desliga|tire)"))
                if (aparelho.naoPerturbe(ligar))
                    (if (ligar) "Não perturbe ativado." else "Não perturbe desativado.")
                else "Abri a tela de autorização do Não perturbe."
            })

        add("dev.config", "Abrir configurações", "Aparelho",
            listOf("abra as configurações do celular"),
            { it.contains("configurac") && it.contains(Regex("\\b(abra|abrir|abre)\\b")) },
            { _, _ -> aparelho.configuracoes(); "Abrindo as configurações." })

        add("dev.bateria", "Bateria", "Aparelho", listOf("qual o nível da bateria"),
            { it.contains("bateria") || it.contains("carga") },
            { _, _ ->
                val pct = aparelho.bateriaPct()
                val extra = if (aparelho.carregando()) ", carregando no momento"
                            else if (pct <= 20) ". Recomendo conectar o carregador" else ""
                "Bateria em $pct por cento$extra."
            })

        add("dev.rede", "Conexão", "Aparelho", listOf("como está a internet"),
            { it.contains("internet") || it.contains("conexao") || it.contains("rede") },
            { _, _ -> "Conexão atual: ${aparelho.rede()}." })

        add("dev.vibrar", "Vibrar", "Aparelho", listOf("vibre o celular"),
            { it.contains(Regex("\\bvibr")) },
            { _, _ -> aparelho.vibrar(500); "Vibrando." })

        /* ============ TEMPO ============ */

        add("tempo.hora", "Hora e data", "Tempo", listOf("que horas são", "que dia é hoje"),
            { it.contains("que horas") || it.contains("que dia e hoje") || it.contains("qual a data") },
            { t, _ ->
                val agora = Calendar.getInstance()
                if (t.contains("dia") || t.contains("data")) {
                    val f = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
                    "Hoje é ${f.format(agora.time)}."
                } else "São ${agora.get(Calendar.HOUR_OF_DAY)} horas e ${agora.get(Calendar.MINUTE)} minutos."
            })

        add("tempo.alarme", "Alarme no relógio do celular", "Tempo",
            listOf("alarme para as 6 e 30", "me acorde às 5 horas"),
            { it.contains("alarme") || it.contains("me acorde") || it.contains("despert") },
            { t, _ ->
                val m = Regex("(?:as|para as|pras)\\s+(\\d{1,2})(?:[:h\\s e]+(\\d{1,2}))?").find(t)
                val hora: Int
                var minuto = 0
                if (m != null) {
                    hora = m.groupValues[1].toInt()
                    if (m.groupValues[2].isNotEmpty()) minuto = m.groupValues[2].toInt()
                } else {
                    val n = Texto.numero(t.replace(Regex("alarme|me acorde|despert\\w*|horas?"), ""))
                        ?: return@add "Diga o horário. Exemplo: alarme para as seis e trinta."
                    hora = n
                }
                if (hora > 23) return@add "Horário inválido."
                aparelho.alarme(hora, minuto, "J.A.R.V.I.S.")
                "Alarme criado para ${"%02d".format(hora)}:${"%02d".format(minuto)} no relógio do celular."
            })

        add("tempo.timer", "Temporizador", "Tempo",
            listOf("temporizador de 10 minutos", "conte 2 minutos"),
            { it.contains("temporizador") || it.contains("timer") || it.contains("conte ") },
            { t, _ ->
                val seg = Texto.duracaoSegundos(t)
                    ?: return@add "Não entendi a duração. Diga: temporizador de dez minutos."
                aparelho.temporizador(seg, "J.A.R.V.I.S.")
                "Temporizador de ${Texto.duracaoPorExtenso(seg)} iniciado no relógio."
            })

        /* ============ CÁLCULO ============ */

        add("calc", "Calculadora falada", "Cálculo",
            listOf("quanto é 45 vezes 12", "20 por cento de 350", "raiz quadrada de 144"),
            {
                it.contains(Regex("\\b(calcule|calcular|quanto e|quanto da|raiz quadrada|por cento de)\\b")) ||
                (it.contains(Regex("\\d")) && it.contains(Regex("\\b(mais|menos|vezes|dividido)\\b")))
            },
            { t, _ ->
                val r = Texto.calcular(t)
                if (r == null) "Não consegui interpretar a operação."
                else "O resultado é ${Texto.numeroBonito(r)}."
            })

        /* ============ NOTAS ============ */

        add("nota", "Notas de voz", "Notas",
            listOf("anote que a reunião é terça", "leia minhas notas", "apague as notas"),
            { it.contains(Regex("\\b(anote|anotar|anota|minhas notas|leia as notas)\\b")) },
            { t, bruto ->
                when {
                    t.contains(Regex("\\b(apague|apagar|limpe)\\b")) -> {
                        val q = mem.notas().size; mem.limparNotas()
                        "$q nota${if (q == 1) "" else "s"} apagada${if (q == 1) "" else "s"}."
                    }
                    t.contains(Regex("\\b(leia|ler|quais|minhas|mostre)\\b")) && !t.contains("anote") -> {
                        val n = mem.notas()
                        if (n.isEmpty()) "Você não tem notas."
                        else "Suas notas: " + n.take(6).joinToString(". ")
                    }
                    else -> {
                        val texto = Regex("^.*?(?:anote(?: que)?|anotar|anota)\\s*(.+)$", RegexOption.IGNORE_CASE)
                            .find(bruto)?.groupValues?.get(1)?.trim().orEmpty()
                        if (texto.isEmpty()) "O que devo anotar?"
                        else { mem.addNota(texto); "Anotado: $texto." }
                    }
                }
            })

        add("lista", "Listas", "Notas",
            listOf("adicione arroz na lista de compras", "leia a lista de compras"),
            { it.contains("lista") },
            { t, bruto ->
                val nome = Regex("lista de ([a-z]+)").find(t)?.groupValues?.get(1) ?: "geral"
                when {
                    t.contains(Regex("\\b(leia|ler|mostre|quais)\\b")) -> {
                        val l = mem.lista(nome)
                        if (l.isEmpty()) "A lista de $nome está vazia."
                        else "Na lista de $nome: ${l.joinToString(", ")}."
                    }
                    t.contains(Regex("\\b(limpe|apague|esvazie)\\b")) -> {
                        mem.limparLista(nome); "Lista de $nome esvaziada."
                    }
                    else -> {
                        val item = Regex("(?:adicione|coloque|inclua|bota)\\s+(.+?)\\s+(?:na|em|a)\\s+lista",
                            RegexOption.IGNORE_CASE).find(bruto)?.groupValues?.get(1)?.trim()
                        if (item.isNullOrEmpty()) "Diga: adicione arroz na lista de compras."
                        else { mem.addItem(nome, item); "$item adicionado à lista de $nome." }
                    }
                }
            })

        /* ============ BÍBLIA E PREGAÇÃO ============ */

        add("biblia.versiculo", "Versículo", "Bíblia",
            listOf("versículo do dia", "me dê uma palavra"),
            { it.contains("versiculo") || it.contains("palavra do dia") || it.contains("me de uma palavra") },
            { _, _ -> val (r, v) = Biblia.versiculo(); "$r. $v" })

        add("biblia.esboco", "Esboço de pregação", "Bíblia",
            listOf("monte um esboço sobre perdão", "esboço de pregação sobre fé"),
            { it.contains("esboco") || it.contains("pregacao") || it.contains("sermao") },
            { _, bruto ->
                val tema = Regex("sobre\\s+(.+)$", RegexOption.IGNORE_CASE)
                    .find(bruto)?.groupValues?.get(1)?.trim() ?: "a fé"
                Biblia.esboco(tema)
            })

        add("biblia.plano", "Plano de estudo bíblico", "Bíblia",
            listOf("plano de estudo sobre oração"),
            { it.contains("plano de estudo") || it.contains("plano de leitura") || it.contains("devocional") },
            { _, bruto ->
                val tema = Regex("sobre\\s+(.+)$", RegexOption.IGNORE_CASE)
                    .find(bruto)?.groupValues?.get(1)?.trim() ?: "a vida cristã"
                Biblia.plano(tema)
            })

        add("biblia.compartilhar", "Enviar o último roteiro", "Bíblia",
            listOf("compartilhe o esboço", "mande o esboço no whatsapp"),
            { it.contains("compartilh") },
            { _, _ ->
                if (ultimoTextoLongo.isBlank()) "Não há nada para compartilhar ainda."
                else if (com.compartilhar(ultimoTextoLongo, "Roteiro do J.A.R.V.I.S."))
                    "Escolha o aplicativo para enviar." else "Não consegui abrir o compartilhamento."
            })

        /* ============ NÚCLEO ============ */

        add("sys.parar", "Parar de falar", "Núcleo", listOf("pare", "silêncio", "chega"),
            { it.matches(Regex("^(jarvis\\s+)?(pare|parar|cancela|chega|quieto|silencio)$")) ||
              it.contains("pare de falar") },
            { _, _ -> aoCalar(); "" })

        add("sys.dormir", "Entrar em espera", "Núcleo",
            listOf("durma", "modo espera", "desative a escuta"),
            { it.contains("durma") || it.contains("modo espera") || it.contains("desative a escuta") },
            { _, _ -> aoDesligar(); "Entrando em modo de espera." })

        add("sys.autonomo", "Modo autônomo", "Núcleo",
            listOf("ative o modo autônomo", "desative o modo autônomo"),
            { it.contains("modo autonomo") },
            { t, _ ->
                val on = !t.contains(Regex("\\b(desativ|desliga|encerra)"))
                mem.autonomo = on; aoAutonomo(on)
                if (on) "Modo autônomo ativado. Não preciso mais da palavra de ativação."
                else "Modo autônomo desativado."
            })

        add("sys.nome", "Como me chamar", "Núcleo",
            listOf("me chame de pastor Mauro"),
            { it.contains("me chame de") || it.contains("meu nome e") },
            { _, bruto ->
                val n = Regex("(?:me chame de|meu nome é|meu nome e)\\s+(.+)$", RegexOption.IGNORE_CASE)
                    .find(bruto)?.groupValues?.get(1)?.trim()
                if (n.isNullOrEmpty()) "Como devo chamá-lo?"
                else { mem.nome = n; "Perfeito, $n. Passarei a chamá-lo assim." }
            })

        add("sys.status", "Diagnóstico", "Núcleo", listOf("status do sistema", "diagnóstico"),
            { it.contains("status") || it.contains("diagnostico") || it.contains("relatorio") },
            { _, _ ->
                "Sistemas operacionais. Bateria em ${aparelho.bateriaPct()} por cento, " +
                "conexão por ${aparelho.rede()}, controle do celular " +
                (if (Acessibilidade.ativa()) "ativo" else "desligado") +
                ", ${mem.comandos} comandos processados."
            })

        add("sys.saudacao", "Saudações", "Conversa", listOf("bom dia jarvis", "oi"),
            { it.matches(Regex("^(jarvis\\s+)?(bom dia|boa tarde|boa noite|ola|oi|opa)(\\s+jarvis)?$")) },
            { _, _ ->
                val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val s = when { h < 5 -> "Boa madrugada"; h < 12 -> "Bom dia"; h < 18 -> "Boa tarde"; else -> "Boa noite" }
                "$s, ${mem.nome}. Estou à disposição."
            })

        add("sys.quem", "Identidade", "Conversa", listOf("quem é você"),
            { it.contains("quem e voce") || it.contains("qual seu nome") },
            { _, _ ->
                "Sou J.A.R.V.I.S., seu assistente de voz instalado neste celular. " +
                "Escuto o tempo todo e comando o aparelho inteiro por voz."
            })

        add("sys.obrigado", "Agradecimento", "Conversa", listOf("obrigado", "valeu"),
            { it.contains(Regex("\\b(obrigad|valeu|agradeco)")) },
            { _, _ -> listOf("Sempre às ordens.", "Disponha.", "É um prazer servir.").random() })
    }

    fun executar(falaBruta: String): String? {
        val t = Texto.normalizar(falaBruta)
        if (t.isEmpty()) return null
        for (h in lista) {
            if (h.teste(t)) {
                val r = try { h.executa(t, falaBruta) }
                        catch (e: Exception) { "Falha ao executar: ${e.message}" }
                mem.comandos = mem.comandos + 1
                if (r.length > 200) ultimoTextoLongo = r
                return r
            }
        }
        return null
    }
}
