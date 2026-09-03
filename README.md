# J.A.R.V.I.S. — Controle de Voz Autônomo para Tablet

Assistente de voz que roda **inteiramente no navegador do tablet**, com escuta
contínua, palavra de ativação, resposta falada, modo autônomo e controle dos
recursos do aparelho. Instalável como aplicativo (PWA) e funcional offline.

---

## MENU 1 — INSTALAÇÃO NO TABLET

### 1.1 Requisitos
1. Tablet Android com navegador **Google Chrome** (o reconhecimento de voz
   depende da Web Speech API, presente no Chrome e no Edge).
2. Conexão com a internet **na primeira abertura** (depois funciona offline,
   exceto os módulos Clima, Pesquisa e Bíblia online).
3. Permissão de **microfone** concedida ao site.

### 1.2 Publicação pelo GitHub Pages (caminho recomendado)
1. Acesse o repositório no GitHub.
2. Vá em **Settings → Pages**.
3. Em *Source*, escolha **Deploy from a branch**.
4. Selecione a branch `claude/voice-control-jarvis-project-xn3azn`, pasta `/ (root)`.
5. Salve e aguarde de 1 a 2 minutos.
6. O endereço publicado aparecerá no topo da mesma página.

### 1.3 Abertura no tablet
1. Abra o endereço publicado no Chrome do tablet.
2. Toque em **TOCAR PARA ATIVAR** na tela de inicialização.
3. Autorize o microfone quando o Chrome perguntar.
4. Ouça a saudação de confirmação — o sistema está online.

### 1.4 Instalação como aplicativo
1. No Chrome, toque no menu **⋮**.
2. Escolha **Instalar aplicativo** ou **Adicionar à tela inicial**.
3. O ícone do reator aparecerá na tela inicial do tablet.
4. Aberto por esse ícone, o app roda em tela cheia, sem barra do navegador.

### 1.5 Uso local sem publicar (alternativa)
1. Copie a pasta do projeto para o tablet.
2. Sirva por um servidor local (`python3 -m http.server`), pois o microfone
   exige **HTTPS** ou **localhost** — abrir por `file://` não funciona.

---

## MENU 2 — FORMAS DE ACIONAMENTO

1. **Por palavra de ativação**
   1. Diga **“Jarvis”**.
   2. Aguarde o sinal âmbar no reator e a resposta curta (“Pois não”).
   3. Fale o comando dentro da janela de 12 segundos.
2. **Em frase única**
   1. Diga tudo de uma vez: *“Jarvis, que horas são”*.
   2. O sistema separa a ativação do comando automaticamente.
3. **Por toque (push-to-talk)**
   1. Segure o botão **🎙️ Falar** na barra inferior.
   2. Fale o comando — nenhuma palavra de ativação é necessária.
4. **Modo autônomo**
   1. Toque em **🤖 Autônomo** ou diga *“ative o modo autônomo”*.
   2. Qualquer frase passa a ser interpretada como comando.
   3. O sistema também age sozinho: alerta bateria, queda de rede,
      alarmes e lembretes, sem que você peça.

### Variações aceitas da palavra de ativação
`jarvis`, `jarves`, `jarbas`, `darvis`, `javis`, `jarvi` — tolerância a erros
de transcrição do reconhecedor em português.

---

## MENU 3 — MÓDULOS E COMANDOS

### 3.1 Núcleo
1. **Parar fala** — “pare”, “cancela”, “silêncio”, “pare de falar”.
2. **Entrar em espera** — “durma”, “modo espera”, “desative a escuta”, “tchau”.
3. **Modo autônomo** — “ative o modo autônomo”, “desative o modo autônomo”.
4. **Controle de voz** — “fique mudo”, “volte a falar”, “fale mais rápido”,
   “fale mais devagar”, “fale mais alto”, “fale mais baixo”.
5. **Diagnóstico** — “status do sistema”, “diagnóstico”, “relatório”.
6. **Menu de comandos** — “menu”, “ajuda”, “o que você faz”.
7. **Limpar tela** — “limpe a tela”, “apague o histórico”.

### 3.2 Tempo
1. **Hora e data** — “que horas são”, “que dia é hoje”, “qual a data”.
2. **Cronômetro** — “inicie o cronômetro”, “quanto tempo passou”, “pare o cronômetro”.
3. **Temporizador** — “temporizador de 10 minutos”, “me avise em 30 segundos”.
4. **Alarme** — “alarme para as 6 e 30”, “liste os alarmes”, “cancele os alarmes”.
5. **Lembretes** — “me lembre de ligar para o pastor em 15 minutos”,
   “quais são meus lembretes”.

### 3.3 Cálculo
1. **Operações faladas** — “quanto é 45 vezes 12”, “237 mais 88”.
2. **Porcentagem** — “calcule 20 por cento de 350”.
3. **Raiz** — “raiz quadrada de 144”.
4. **Números por extenso** — “quanto é quarenta e cinco vezes doze”.

### 3.4 Notas
1. **Anotar** — “anote que a reunião é terça”.
2. **Ler** — “leia minhas notas”.
3. **Apagar** — “apague as notas”.
4. **Listas** — “adicione arroz na lista de compras”, “leia a lista de compras”,
   “esvazie a lista de compras”.

### 3.5 Tablet (hardware)
1. **Bateria** — “qual o nível da bateria”, “está carregando”.
2. **Rede** — “como está a internet”, “estou conectado”.
3. **Lanterna** — “ligue a lanterna”, “apague a lanterna”
   (usa o LED; sem acesso ao LED, acende a tela branca — toque para desligar).
4. **Vibração** — “vibre o tablet”.
5. **Brilho** — “brilho em 40 por cento”, “aumente o brilho”, “diminua o brilho”.
6. **Tela** — “tela cheia”, “saia da tela cheia”, “trave em paisagem”,
   “trave em retrato”, “mantenha a tela ligada”.
7. **Localização** — “onde estou”, “minha localização”.

### 3.6 Web
1. **Abrir apps e sites** — “abra o youtube”, “abra o whatsapp”, “abra a bíblia”,
   “abra o gmail”, “abra o tradutor”, “abra maps”.
2. **Pesquisar** — “pesquise sobre história da igreja”,
   “procure no youtube por louvor”.
3. **Ligar e mensagens** — “ligue para 11 99999 8888”,
   “mande whatsapp para 11 99999 8888”.
4. **Clima** — “como está o tempo”, “vai chover hoje”, “qual a temperatura”.

### 3.7 Bíblia e pregação
1. **Versículo** — “versículo do dia”, “me dê uma palavra”, “leia João 3:16”.
   - Online: busca a referência exata na tradução Almeida.
   - Offline: usa o acervo interno de versículos.
2. **Esboço de pregação** — “monte um esboço sobre perdão”.
   - Gera roteiro completo: abertura, contexto, três pontos com subdivisões,
     ilustração, aplicação por público e conclusão com apelo.
3. **Plano de estudo** — “plano de estudo sobre oração”.
   - Gera cronograma de sete dias com leitura, observação, memorização
     e passo de obediência.

### 3.8 Conversa
1. **Saudações** — “bom dia jarvis”, “boa noite”, “oi”.
2. **Agradecimento** — “obrigado”, “valeu”.
3. **Identidade** — “quem é você”, “como você funciona”.
4. **Tratamento** — “me chame de pastor Mauro”.

---

## MENU 4 — MODO AUTÔNOMO (INICIATIVA PRÓPRIA)

Com o modo autônomo ligado, o sistema executa um ciclo de vigilância a cada
segundo e fala sem ser chamado nestas situações:

1. **Lembrete vencido** — anuncia o texto e vibra o tablet.
2. **Alarme atingido** — anuncia o horário e o rótulo.
3. **Temporizador concluído** — informa a duração encerrada.
4. **Bateria em 20%** — aviso preventivo.
5. **Bateria em 10%** — alerta crítico, ignora o modo mudo.
6. **Bateria carregada** — sugere retirar o carregador.
7. **Queda ou retorno da internet** — informa a mudança de estado.
8. **Hora cheia** — anuncia a hora, se o anúncio horário estiver habilitado.

---

## MENU 5 — ARQUITETURA DO CÓDIGO

| Arquivo | Responsabilidade |
|---|---|
| `index.html` | Estrutura da interface, boot e barra de controles |
| `css/style.css` | Tema holográfico, HUD, painel de log e menu |
| `js/utils.js` | Normalização de texto, números por extenso, durações, matemática |
| `js/store.js` | Persistência em `localStorage`: config, notas, listas, alarmes |
| `js/speech.js` | Reconhecimento contínuo com auto-recuperação e síntese de voz |
| `js/device.js` | Bateria, rede, vibração, lanterna, brilho, GPS, notificações |
| `js/skills.js` | Roteador e catálogo das habilidades por módulo |
| `js/autonomy.js` | Motor autônomo: vigilância e ações por iniciativa própria |
| `js/hud.js` | Reator de arco desenhado em canvas, reativo ao estado |
| `js/app.js` | Máquina de estados, palavra de ativação, interface e PWA |
| `sw.js` | Service worker: cache e operação offline |

### Decisões técnicas relevantes
1. **Auto-recuperação da escuta** — o Android encerra o reconhecimento
   periodicamente; o sistema reinicia com backoff progressivo (150 ms a 6 s),
   evitando travar o tablet em laço de reinício.
2. **Supressão de eco** — o reconhecimento é pausado enquanto o assistente
   fala, para que ele não interprete a própria voz como comando.
3. **Watchdog de fala** — alguns Androids nunca disparam o evento `onend` do
   sintetizador; um temporizador proporcional ao tamanho do texto destrava a fila.
4. **Matemática sem `eval` livre** — a expressão é filtrada para conter apenas
   dígitos e operadores antes de ser avaliada.
5. **Brilho virtual** — o navegador não altera o brilho real do aparelho; o
   ajuste é feito por uma camada escura sobreposta.

---

## MENU 6 — SOLUÇÃO DE PROBLEMAS

1. **“Nenhum microfone detectado”**
   - Verifique se outro aplicativo está usando o microfone.
   - Feche e reabra o app.
2. **“Permissão de microfone negada”**
   - Chrome → **⋮** → Configurações → Configurações do site → Microfone →
     permita para o endereço do app.
3. **Não reconhece a palavra de ativação**
   - Fale “Jarvis” com pausa curta antes do comando.
   - Use o botão **🎙️ Falar** como alternativa.
   - Reduza o ruído ambiente.
4. **Não fala nada**
   - Verifique se o botão **🔊 Voz** está aceso.
   - Verifique o volume de mídia do tablet.
5. **A escuta para sozinha depois de um tempo**
   - Mantenha o app em primeiro plano; navegadores suspendem abas em segundo plano.
   - Diga “mantenha a tela ligada”.
6. **Clima ou versículo online não respondem**
   - Exigem internet; sem rede o sistema usa o acervo local.

---

## MENU 7 — ÍCONE DE INICIALIZAÇÃO

### 7.1 Arquivos gerados
| Arquivo | Uso |
|---|---|
| `icons/reactor.svg` | Reator **animado** da tela de inicialização (vetor) |
| `icons/icon-512.png` | Ícone principal do aplicativo instalado |
| `icons/icon-192.png` | Ícone da tela inicial do Android |
| `icons/icon-maskable-512.png` | Versão *maskable*, para recorte circular do Android |
| `icons/apple-touch-icon.png` | Ícone do iPad e do Safari |
| `icons/favicon-32.png` | Ícone da aba do navegador |

### 7.2 Como o desenho foi construído
1. Reator de arco em **vetor puro**, sem imagem externa nem fonte especial.
2. Quatro camadas concêntricas: 60 tiques de escala, anel de 8 segmentos,
   anel de progresso com 3 arcos e o núcleo com 6 bobinas radiais.
3. Núcleo com gradiente radial branco-ciano e halo luminoso.
4. Rasterizado em navegador real, para que o brilho e os gradientes
   fiquem idênticos ao que aparece na tela.

### 7.3 Animação da tela de inicialização
1. **Tiques externos** giram em 26 segundos por volta.
2. **Anel segmentado** gira em 9 segundos.
3. **Anel de progresso** gira em sentido contrário, em 6 segundos.
4. **Núcleo** pulsa em ciclo de 2,4 segundos.
5. O conjunto entra em cena com expansão de 0,6 a 1,0 em 1,4 segundo.
6. As animações estão dentro do próprio SVG, então funcionam offline e
   sem JavaScript.

### 7.4 Versão *maskable*
O Android recorta ícones em círculo, losango ou quadrado arredondado,
conforme o fabricante. Por isso o arquivo *maskable* mantém todo o desenho
dentro de 62% do quadro — a zona segura — evitando que os anéis externos
sejam cortados na tela inicial.

### 7.5 Ícone de inicialização dentro do menu
1. **Onde fica** — no topo do menu geral de comandos, logo abaixo do título,
   junto de uma marca menor do mesmo reator no cabeçalho.
2. **O que mostra** — três informações de estado, atualizadas em tempo real:
   1. `NÚCLEO ATIVO`, `NÚCLEO EM ESPERA`, `MICROFONE BLOQUEADO` ou `FALHA NO NÚCLEO`.
   2. A instrução do toque: inicializar ou reinicializar.
   3. O rodapé técnico: quantidade de habilidades, de módulos e o modo de
      operação atual (por comando ou autônomo).
3. **O que faz ao ser tocado** — reinicializa o núcleo por completo:
   1. Interrompe a fala em andamento e encerra a escuta.
   2. Fecha a janela de comando aberta.
   3. Exibe `REINICIALIZANDO…` durante o processo.
   4. Recarrega bateria, wake-lock, brilho salvo e motor autônomo.
   5. Restabelece a escuta e confirma por voz.
4. **Como se comporta visualmente**
   1. Núcleo **apagado e dessaturado** quando o sistema está em espera.
   2. Núcleo **aceso, com halo e anel pulsante** quando está ativo.
   3. Afunda ao toque, com vibração curta de retorno.
