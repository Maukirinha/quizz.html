/* ============================================================
   J.A.R.V.I.S. — skills.js
   Roteador de comandos. Cada habilidade tem: id, nome, exemplos,
   teste(t) e executa(t, ctx). O primeiro teste verdadeiro vence.
   Retorno: string  |  { fala, mostra, silencioso }
   ============================================================ */
const Skills = (() => {

  const S = [];
  const add = s => { S.push(s); return s; };
  const t_ = U.norm;

  /* =========================================================
     1. NÚCLEO / SISTEMA
     ========================================================= */
  add({
    id:'sys.parar', nome:'Parar / cancelar fala', grupo:'Núcleo',
    exemplos:['jarvis pare','cancela','silêncio','chega'],
    teste: t => /^(jarvis\s+)?(pare|parar|cancela|cancelar|chega|quieto|silencio|cala a boca|para de falar)$/.test(t.trim())
               || /\b(pare de falar|cala a boca|fica quieto|faz silencio)\b/.test(t),
    executa: () => { Voz.calar(); return { fala:'', mostra:'⏹ Interrompido.', silencioso:true }; }
  });

  add({
    id:'sys.dormir', nome:'Entrar em espera', grupo:'Núcleo',
    exemplos:['jarvis durma','modo espera','desative a escuta'],
    teste: t => /\b(durma|dormir|modo espera|desligar? a escuta|desative a escuta|ate logo|tchau)\b/.test(t),
    executa: (t, ctx) => { ctx.dormir(); return 'Entrando em modo de espera. Diga Jarvis para me acordar.'; }
  });

  add({
    id:'sys.autonomo', nome:'Modo autônomo', grupo:'Núcleo',
    exemplos:['ative o modo autônomo','desative o modo autônomo'],
    teste: t => /\b(modo autonomo|autonomia|piloto automatico)\b/.test(t),
    executa: (t, ctx) => {
      const on = !/\b(desativ|desliga|encerra|sai do)\w*\b/.test(t);
      ctx.setAutonomo(on);
      return on
        ? 'Modo autônomo ativado. Estou monitorando tudo continuamente e agirei por iniciativa própria.'
        : 'Modo autônomo desativado. Voltando à operação por comando.';
    }
  });

  add({
    id:'sys.voz', nome:'Silenciar / voltar a falar', grupo:'Núcleo',
    exemplos:['fique mudo','volte a falar','fale mais rápido','fale mais devagar'],
    teste: t => /\b(mudo|mudinho|sem voz|volte a falar|pode falar|fale mais (rapido|devagar|alto|baixo))\b/.test(t),
    executa: t => {
      if (/mais rapido/.test(t)){ Store.cfg.taxa = Math.min(2, Store.cfg.taxa + .15); Store.save(); return 'Velocidade aumentada.'; }
      if (/mais devagar/.test(t)){ Store.cfg.taxa = Math.max(.5, Store.cfg.taxa - .15); Store.save(); return 'Velocidade reduzida.'; }
      if (/mais alto/.test(t)){ Store.cfg.volume = 1; Store.save(); return 'Volume no máximo.'; }
      if (/mais baixo/.test(t)){ Store.cfg.volume = .5; Store.save(); return 'Volume reduzido.'; }
      const mudo = !/\b(volte a falar|pode falar)\b/.test(t);
      Store.cfg.mudo = mudo; Store.save();
      return mudo ? { fala:'', mostra:'🔇 Voz desativada.', silencioso:true } : 'Voz reativada.';
    }
  });

  add({
    id:'sys.status', nome:'Diagnóstico do sistema', grupo:'Núcleo',
    exemplos:['status do sistema','diagnóstico','relatório'],
    executa: async (t, ctx) => {
      const b = Dev.estadoBateria(), r = Dev.estadoRede();
      const up = U.fmtDur((Date.now() - Store.db.stats.desde) / 1000);
      const L = [
        'RELATÓRIO DE SISTEMA',
        `• Escuta: ${Voz.ligado ? 'ativa' : 'inativa'}`,
        `• Modo autônomo: ${Store.cfg.autonomo ? 'ligado' : 'desligado'}`,
        `• Bateria: ${b ? b.pct + '%' + (b.carregando ? ' (carregando)' : '') : 'indisponível'}`,
        `• Rede: ${r.online ? 'conectado ' + r.tipo : 'offline'}`,
        `• Comandos processados: ${Store.db.stats.comandos}`,
        `• Lembretes pendentes: ${Store.db.lembretes.filter(l => !l.feito).length}`,
        `• Alarmes ativos: ${Store.db.alarmes.filter(a => a.ativo).length}`,
        `• Notas guardadas: ${Store.db.notas.length}`,
        `• Tempo de operação: ${up}`
      ];
      return {
        mostra: L.join('\n'),
        fala: `Todos os sistemas operacionais. Bateria em ${b ? b.pct : 'nível desconhecido'} por cento, ` +
              `rede ${r.online ? 'conectada' : 'offline'}, ${Store.db.stats.comandos} comandos processados.`
      };
    },
    teste: t => /\b(status|diagnostico|relatorio|como voce esta|situacao do sistema)\b/.test(t)
  });

  add({
    id:'sys.menu', nome:'Menu de comandos', grupo:'Núcleo',
    exemplos:['abra o menu','o que você faz','ajuda','lista de comandos'],
    teste: t => /\b(menu|ajuda|comandos|o que voce (faz|sabe)|do que voce e capaz)\b/.test(t),
    executa: (t, ctx) => { ctx.abrirMenu(); return 'Menu completo de comandos aberto na tela, dividido por módulos.'; }
  });

  add({
    id:'sys.limpar', nome:'Limpar tela', grupo:'Núcleo',
    exemplos:['limpe a tela','apague o histórico'],
    teste: t => /\b(limpe? a tela|limpar tela|apagar? (o )?historico)\b/.test(t),
    executa: (t, ctx) => { ctx.limpar(); return 'Tela limpa.'; }
  });

  /* =========================================================
     2. TEMPO
     ========================================================= */
  add({
    id:'tempo.hora', nome:'Hora e data', grupo:'Tempo',
    exemplos:['que horas são','qual a data de hoje','que dia é hoje'],
    teste: t => /\b(que horas|hora certa|qual (a )?hora|que dia (e|eh) hoje|qual (a )?data|data de hoje)\b/.test(t),
    executa: t => {
      const d = new Date();
      if (/dia|data/.test(t)) return `Hoje é ${U.dataExtenso(d)}.`;
      return `São ${d.getHours()} horas e ${d.getMinutes()} minutos.`;
    }
  });

  let crono = null;
  add({
    id:'tempo.crono', nome:'Cronômetro', grupo:'Tempo',
    exemplos:['inicie o cronômetro','pare o cronômetro','quanto tempo passou'],
    teste: t => /\bcronometro\b/.test(t),
    executa: t => {
      if (/\b(pare|parar|para|zere|zerar|encerre)\b/.test(t)){
        if (!crono) return 'Não há cronômetro em execução.';
        const s = (Date.now() - crono) / 1000; crono = null;
        return `Cronômetro parado em ${U.fmtDur(s)}.`;
      }
      if (/\b(quanto|ver|marcar|parcial)\b/.test(t)){
        if (!crono) return 'Não há cronômetro em execução.';
        return `Tempo decorrido: ${U.fmtDur((Date.now() - crono) / 1000)}.`;
      }
      crono = Date.now();
      return 'Cronômetro iniciado agora.';
    }
  });

  add({
    id:'tempo.timer', nome:'Temporizador', grupo:'Tempo',
    exemplos:['temporizador de 10 minutos','me avise em 30 segundos','conte 2 minutos'],
    teste: t => /\b(temporizador|timer|me avise em|conte|contagem regressiva|despertar em)\b/.test(t),
    executa: (t, ctx) => {
      const seg = U.duracaoSegundos(t);
      if (!seg) return 'Não entendi a duração. Diga, por exemplo: temporizador de dez minutos.';
      ctx.timer(seg, 'Temporizador');
      return `Temporizador de ${U.fmtDur(seg)} iniciado.`;
    }
  });

  add({
    id:'tempo.alarme', nome:'Alarme', grupo:'Tempo',
    exemplos:['alarme para as 6 e 30','desperte-me às 7 horas','liste os alarmes'],
    teste: t => /\b(alarmes?|despertador|me acorde|desperte)\b/.test(t),
    executa: t => {
      if (/\b(liste|listar|quais|meus)\b/.test(t)){
        const a = Store.db.alarmes.filter(x => x.ativo);
        if (!a.length) return 'Nenhum alarme programado.';
        return { mostra:'ALARMES ATIVOS\n' + a.map(x => `• ${U.pad(x.h)}:${U.pad(x.m)} ${x.rotulo}`).join('\n'),
                 fala:`Você tem ${a.length} alarme${a.length>1?'s':''}: ` + a.map(x => `${x.h} e ${x.m}`).join(', ') };
      }
      if (/\b(apague|apagar|remova|cancele|cancelar|desative)\b/.test(t)){
        const n = Store.db.alarmes.length;
        Store.db.alarmes = []; Store.save();
        return n ? `${n} alarme${n>1?'s':''} cancelado${n>1?'s':''}.` : 'Não havia alarmes.';
      }
      const m = t.match(/(?:as|para as|pras|de)\s+(\d{1,2})(?:[:h e]+(\d{1,2}))?/);
      let h, mi = 0;
      if (m){ h = +m[1]; mi = m[2] ? +m[2] : 0; }
      else {
        const n = U.palavraParaNumero(t.replace(/alarme|despertador|me acorde|desperte|horas?|para|as/g,''));
        if (n === null || n > 23) return 'Diga o horário, por exemplo: alarme para as seis e trinta.';
        h = n;
      }
      if (h < 1 && /noite|tarde/.test(t)) h += 12;
      Store.addAlarme(h, mi, '');
      return `Alarme programado para ${U.pad(h)}:${U.pad(mi)}.`;
    }
  });

  add({
    id:'tempo.lembrete', nome:'Lembretes', grupo:'Tempo',
    exemplos:['me lembre de tomar água em 20 minutos','quais são meus lembretes'],
    teste: t => /\b(lembre|lembrete|lembra|nao me deixe esquecer)\b/.test(t),
    executa: (t, ctx, raw) => {
      if (/\b(quais|liste|listar|meus|ver)\b/.test(t)){
        const l = Store.db.lembretes.filter(x => !x.feito);
        if (!l.length) return 'Nenhum lembrete pendente.';
        return { mostra:'LEMBRETES PENDENTES\n' + l.map(x =>
                   `• ${x.texto} — ${new Date(x.quando).toLocaleString('pt-BR')}`).join('\n'),
                 fala:`Você tem ${l.length} lembrete${l.length>1?'s':''} pendente${l.length>1?'s':''}.` };
      }
      const seg = U.duracaoSegundos(t);
      let texto = (raw || t)
        .replace(/^.*?(?:lembre(?:-me)? de|lembrete de|lembra de)\s*/i,'')
        .replace(/\bem\s+[^,]*$/i,'').trim();
      if (!texto) texto = 'lembrete';
      if (!seg) return 'Diga também o tempo. Exemplo: me lembre de ligar para o pastor em quinze minutos.';
      Store.addLembrete(texto, Date.now() + seg * 1000);
      return `Anotado. Vou lembrá-lo de ${texto} em ${U.fmtDur(seg)}.`;
    }
  });

  /* =========================================================
     3. CÁLCULO
     ========================================================= */
  add({
    id:'calc', nome:'Calculadora falada', grupo:'Cálculo',
    exemplos:['quanto é 45 vezes 12','calcule 20 por cento de 350','raiz quadrada de 144'],
    teste: t => /\b(calcule|calcular|quanto (e|eh|da|sao)|resultado de|raiz quadrada|por cento de)\b/.test(t)
               || (/\d/.test(t) && /\b(mais|menos|vezes|dividido|elevado)\b/.test(t)),
    executa: t => {
      const r = U.calcular(t);
      if (r === null) return 'Não consegui interpretar a operação. Tente: quanto é quarenta e cinco vezes doze.';
      return `O resultado é ${U.numBonito(r)}.`;
    }
  });

  /* =========================================================
     4. NOTAS E LISTAS
     ========================================================= */
  add({
    id:'nota', nome:'Notas de voz', grupo:'Notas',
    exemplos:['anote que a reunião é terça','leia minhas notas','apague as notas'],
    teste: t => /\b(anote|anotar|anota|nota que|minhas notas|ler notas|leia (as )?notas)\b/.test(t),
    executa: (t, ctx, raw) => {
      if (/\b(leia|ler|quais|minhas|mostrar)\b/.test(t) && !/anote/.test(t)){
        const n = Store.db.notas.slice(0, 10);
        if (!n.length) return 'Você não tem notas registradas.';
        return { mostra:'NOTAS\n' + n.map((x,i) => `${i+1}. ${x.texto}`).join('\n'),
                 fala:'Suas notas: ' + n.slice(0,5).map(x => x.texto).join('. ') };
      }
      if (/\b(apague|apagar|limpe)\b/.test(t)){
        const q = Store.db.notas.length; Store.db.notas = []; Store.save();
        return `${q} nota${q!==1?'s':''} apagada${q!==1?'s':''}.`;
      }
      const texto = (raw || t)
        .replace(/^.*?(anote(?: que)?|anotar|anota|nota que)\s*/i,'').trim();
      if (!texto) return 'O que devo anotar?';
      Store.addNota(texto);
      return `Anotado: ${texto}.`;
    }
  });

  add({
    id:'lista', nome:'Listas', grupo:'Notas',
    exemplos:['adicione arroz na lista de compras','leia a lista de compras'],
    teste: t => /\blista\b/.test(t),
    executa: (t, ctx, raw) => {
      const nome = (t.match(/lista de ([a-z ]+)/) || [,'geral'])[1].trim().split(' ')[0];
      if (/\b(leia|ler|mostre|quais|o que tem)\b/.test(t)){
        const l = Store.db.listas[nome] || [];
        if (!l.length) return `A lista de ${nome} está vazia.`;
        return { mostra:`LISTA DE ${nome.toUpperCase()}\n` + l.map((x,i) => `${i+1}. ${x}`).join('\n'),
                 fala:`Na lista de ${nome}: ${l.join(', ')}.` };
      }
      if (/\b(limpe|apague|esvazie)\b/.test(t)){
        Store.db.listas[nome] = []; Store.save();
        return `Lista de ${nome} esvaziada.`;
      }
      const item = ((raw || t).match(/(?:adicione|coloque|inclua|bota)\s+(.+?)\s+(?:na|a|em)\s+lista/i) || [])[1];
      if (!item) return 'Diga assim: adicione arroz na lista de compras.';
      Store.addItemLista(nome, item);
      return `${item} adicionado à lista de ${nome}.`;
    }
  });

  /* =========================================================
     5. DISPOSITIVO (TABLET)
     ========================================================= */
  add({
    id:'dev.bateria', nome:'Bateria', grupo:'Tablet',
    exemplos:['qual o nível da bateria','está carregando'],
    teste: t => /\b(bateria|carga|carregando|energia do tablet)\b/.test(t),
    executa: () => {
      const b = Dev.estadoBateria();
      if (!b) return 'Este navegador não expõe o status da bateria.';
      let s = `Bateria em ${b.pct} por cento`;
      s += b.carregando ? ', carregando no momento.' : '.';
      if (!b.carregando && b.pct <= 20) s += ' Recomendo conectar o carregador.';
      return s;
    }
  });

  add({
    id:'dev.rede', nome:'Conexão de rede', grupo:'Tablet',
    exemplos:['como está a internet','estou conectado'],
    teste: t => /\b(internet|rede|wifi|wi fi|conexao|conectado|online)\b/.test(t),
    executa: () => {
      const r = Dev.estadoRede();
      return r.online
        ? `Conectado. Qualidade da rede: ${r.tipo}${r.down ? `, cerca de ${r.down} megabits por segundo` : ''}.`
        : 'Sem conexão com a internet. Operando apenas com recursos locais.';
    }
  });

  add({
    id:'dev.lanterna', nome:'Lanterna', grupo:'Tablet',
    exemplos:['ligue a lanterna','apague a lanterna'],
    teste: t => /\b(lanterna|luz do tablet|flash)\b/.test(t),
    executa: async t => {
      const on = !/\b(apague|desligue|desliga|fecha|apagar)\b/.test(t);
      const r = await Dev.lanterna(on);
      if (r === 'sem-torch' || r === false){
        Dev.lanternaTela(on);
        return on ? 'Sem acesso ao LED. Ativei a tela branca como lanterna. Toque na tela para desligar.'
                  : 'Lanterna desligada.';
      }
      Dev.lanternaTela(false);
      return on ? 'Lanterna ligada.' : 'Lanterna desligada.';
    }
  });

  add({
    id:'dev.vibrar', nome:'Vibração', grupo:'Tablet',
    exemplos:['vibre o tablet'],
    teste: t => /\bvibr(e|ar|a)\b/.test(t),
    executa: () => { Dev.vibrar([120,80,120,80,200]); return 'Vibrando.'; }
  });

  add({
    id:'dev.brilho', nome:'Brilho da tela', grupo:'Tablet',
    exemplos:['brilho em 40 por cento','aumente o brilho','diminua o brilho'],
    teste: t => /\bbrilho\b/.test(t),
    executa: t => {
      const atual = Store.cfg.brilhoOverlay || 100;
      let alvo = null;
      const n = (t.match(/(\d{1,3})\s*(?:por cento|%)?/) || [])[1];
      if (n) alvo = +n;
      else if (/aument|sobe|mais/.test(t)) alvo = atual + 20;
      else if (/dimin|abaix|reduz|menos/.test(t)) alvo = atual - 20;
      else if (/maximo|total/.test(t)) alvo = 100;
      if (alvo === null) return 'Diga: brilho em cinquenta por cento.';
      return `Brilho ajustado para ${Dev.brilho(alvo)} por cento.`;
    }
  });

  add({
    id:'dev.tela', nome:'Tela cheia / orientação / manter ligada', grupo:'Tablet',
    exemplos:['tela cheia','sair da tela cheia','trave em paisagem','mantenha a tela ligada'],
    teste: t => /\b(tela cheia|full screen|paisagem|retrato|orientacao|mantenha a tela|nao apague a tela)\b/.test(t),
    executa: async t => {
      if (/tela cheia|full screen/.test(t)){
        const on = !/\b(saia|sair|feche|desligue)\b/.test(t);
        await Dev.telaCheia(on);
        return on ? 'Modo tela cheia ativado.' : 'Saindo da tela cheia.';
      }
      if (/paisagem/.test(t)){ await Dev.travarOrientacao('landscape'); return 'Orientação travada em paisagem.'; }
      if (/retrato/.test(t)){ await Dev.travarOrientacao('portrait'); return 'Orientação travada em retrato.'; }
      const ok = await Dev.manterAcordado(!/\b(desligue|desative|pode apagar)\b/.test(t));
      return ok ? 'Tela permanecerá ligada enquanto eu estiver ativo.' : 'Não consegui manter a tela ligada neste dispositivo.';
    }
  });

  add({
    id:'dev.local', nome:'Localização', grupo:'Tablet',
    exemplos:['onde estou','minha localização'],
    teste: t => /\b(onde estou|minha localizacao|localizacao atual|coordenadas)\b/.test(t),
    executa: async () => {
      const p = await Dev.local();
      if (!p) return 'Não consegui obter a localização. Verifique a permissão de GPS.';
      return { mostra:`📍 ${p.lat.toFixed(5)}, ${p.lon.toFixed(5)}\nhttps://maps.google.com/?q=${p.lat},${p.lon}`,
               fala:`Coordenadas obtidas: latitude ${p.lat.toFixed(3)}, longitude ${p.lon.toFixed(3)}.` };
    }
  });

  /* =========================================================
     6. WEB / NAVEGAÇÃO
     ========================================================= */
  add({
    id:'web.abrir', nome:'Abrir sites e apps', grupo:'Web',
    exemplos:['abra o youtube','abra o whatsapp','abra o google'],
    teste: t => /\b(abra|abrir|abre|inicie|va para)\b/.test(t) && !/menu|lista|nota/.test(t),
    executa: t => {
      const mapa = {
        youtube:'https://m.youtube.com', google:'https://www.google.com',
        gmail:'https://mail.google.com', whatsapp:'https://web.whatsapp.com',
        instagram:'https://www.instagram.com', facebook:'https://www.facebook.com',
        maps:'https://maps.google.com', mapa:'https://maps.google.com',
        biblia:'https://www.bibliaonline.com.br', spotify:'https://open.spotify.com',
        netflix:'https://www.netflix.com', drive:'https://drive.google.com',
        agenda:'https://calendar.google.com', calendario:'https://calendar.google.com',
        tradutor:'https://translate.google.com', noticias:'https://news.google.com',
        chatgpt:'https://chat.openai.com', claude:'https://claude.ai'
      };
      const alvo = Object.keys(mapa).find(k => t.includes(k));
      if (alvo){ window.open(mapa[alvo], '_blank'); return `Abrindo ${alvo}.`; }
      const site = (t.match(/(?:abra|abrir|abre|va para)\s+(?:o |a |os |as )?(?:site )?([a-z0-9.-]+)/) || [])[1];
      if (site && site.includes('.')){ window.open('https://' + site, '_blank'); return `Abrindo ${site}.`; }
      return 'Não conheço esse aplicativo. Diga o nome do site completo.';
    }
  });

  add({
    id:'web.buscar', nome:'Pesquisar', grupo:'Web',
    exemplos:['pesquise sobre história da igreja','procure no youtube por louvor'],
    teste: t => /\b(pesquise|pesquisar|procure|busque|buscar|o que e|quem foi|quem e)\b/.test(t)
               && !/\bquem (e|eh) voce\b/.test(t) && !/\bo que voce (faz|sabe)\b/.test(t),
    executa: t => {
      const yt = /youtube/.test(t);
      const q = t.replace(/^.*?(pesquise|pesquisar|procure|busque|buscar)\s*(sobre|por|no youtube|no google)?\s*/,'')
                 .replace(/no youtube|no google/g,'').trim() || t;
      const url = yt ? 'https://m.youtube.com/results?search_query=' + encodeURIComponent(q)
                     : 'https://www.google.com/search?q=' + encodeURIComponent(q);
      window.open(url, '_blank');
      return `Pesquisando por ${q}.`;
    }
  });

  add({
    id:'web.ligar', nome:'Ligar e mensagens', grupo:'Web',
    exemplos:['ligue para 11 99999 8888','mande whatsapp para 11 99999 8888'],
    teste: t => /\b(ligue para|ligar para|telefone para|mande (uma )?mensagem|whats(app)? para)\b/.test(t),
    executa: t => {
      const num = (t.match(/(\d[\d\s]{7,})/) || [,''])[1].replace(/\s/g,'');
      if (!num) return 'Informe o número, dígito por dígito.';
      if (/whats/.test(t)){ window.open('https://wa.me/55' + num, '_blank'); return `Abrindo conversa com ${num}.`; }
      window.open('tel:' + num); return `Ligando para ${num}.`;
    }
  });

  add({
    id:'web.clima', nome:'Clima', grupo:'Web',
    exemplos:['como está o tempo','vai chover hoje'],
    teste: t => /\b(clima|vai chover|temperatura|previsao|esta (frio|calor|chovendo))\b/.test(t)
               || /\b(como (esta|ta) o tempo|o tempo (hoje|agora|la fora))\b/.test(t),
    executa: async () => {
      const p = await Dev.local();
      if (!p || !navigator.onLine) return 'Preciso de internet e de permissão de localização para consultar o clima.';
      try {
        const r = await fetch(`https://api.open-meteo.com/v1/forecast?latitude=${p.lat}&longitude=${p.lon}&current=temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max&timezone=auto&forecast_days=1`);
        const d = await r.json();
        const c = d.current, dia = d.daily;
        return { mostra:`CLIMA AGORA\n• Temperatura: ${c.temperature_2m}°C\n• Umidade: ${c.relative_humidity_2m}%\n• Vento: ${c.wind_speed_10m} km/h\n• Máxima hoje: ${dia.temperature_2m_max[0]}°C\n• Mínima hoje: ${dia.temperature_2m_min[0]}°C\n• Chance de chuva: ${dia.precipitation_probability_max[0]}%`,
                 fala:`Agora estão ${Math.round(c.temperature_2m)} graus, com ${c.relative_humidity_2m} por cento de umidade. A máxima hoje será de ${Math.round(dia.temperature_2m_max[0])} graus e a chance de chuva é de ${dia.precipitation_probability_max[0]} por cento.` };
      } catch { return 'Não consegui consultar o serviço de meteorologia.'; }
    }
  });

  /* =========================================================
     7. MÓDULO BÍBLICO / PREGAÇÃO
     ========================================================= */
  const VERSOS_OFFLINE = [
    ['Salmos 23:1','O Senhor é o meu pastor; nada me faltará.'],
    ['João 3:16','Porque Deus amou o mundo de tal maneira que deu o seu Filho unigênito, para que todo aquele que nele crê não pereça, mas tenha a vida eterna.'],
    ['Filipenses 4:13','Posso todas as coisas naquele que me fortalece.'],
    ['Isaías 41:10','Não temas, porque eu sou contigo; não te assombres, porque eu sou o teu Deus.'],
    ['Provérbios 3:5','Confia no Senhor de todo o teu coração e não te estribes no teu próprio entendimento.'],
    ['Romanos 8:28','Todas as coisas contribuem juntamente para o bem daqueles que amam a Deus.'],
    ['Josué 1:9','Sê forte e corajoso; não temas, nem te espantes, porque o Senhor teu Deus é contigo.'],
    ['Mateus 6:33','Buscai primeiro o reino de Deus e a sua justiça, e todas estas coisas vos serão acrescentadas.'],
    ['Salmos 119:105','Lâmpada para os meus pés é a tua palavra e luz para o meu caminho.'],
    ['2 Timóteo 3:16','Toda a Escritura é divinamente inspirada e proveitosa para ensinar, repreender, corrigir e instruir em justiça.']
  ];

  add({
    id:'biblia.versiculo', nome:'Versículo bíblico', grupo:'Bíblia',
    exemplos:['versículo do dia','leia João 3:16','me dê uma palavra'],
    teste: t => /\b(versiculo|verso do dia|palavra do dia|leia? (o )?(salmo|joao|mateus|marcos|lucas|romanos|genesis|provérbios|proverbios))\b/.test(t)
               || /\b(me de uma palavra|uma palavra de deus)\b/.test(t),
    executa: async t => {
      const ref = (t.match(/(?:leia|leia o|leiam|versiculo)\s+([1-3]?\s?[a-z]+\s+\d+[:.]?\d*(?:-\d+)?)/) || [])[1];
      if (ref && navigator.onLine){
        try {
          const r = await fetch('https://bible-api.com/' + encodeURIComponent(ref) + '?translation=almeida');
          if (r.ok){
            const d = await r.json();
            if (d.text) return { mostra:`📖 ${d.reference}\n\n“${d.text.trim()}”`, fala:`${d.reference}. ${d.text.trim()}` };
          }
        } catch {}
      }
      const [r0, txt] = VERSOS_OFFLINE[Math.floor(Math.random() * VERSOS_OFFLINE.length)];
      return { mostra:`📖 ${r0}\n\n“${txt}”`, fala:`${r0}. ${txt}` };
    }
  });

  add({
    id:'biblia.esboco', nome:'Esboço de pregação', grupo:'Bíblia',
    exemplos:['monte um esboço sobre fé','esboço de pregação sobre perdão'],
    teste: t => /\b(esboco|esboço|prega(cao|ção)|sermao|estudo biblico|homilia)\b/.test(t),
    executa: (t, ctx, raw) => {
      const tema = ((raw || t).match(/(?:sobre|a respeito de|do tema|acerca de)\s+(.+)$/i)
                    || [,'a fé'])[1].trim();
      const T = tema.charAt(0).toUpperCase() + tema.slice(1);
      const m = [
        `ESBOÇO DE PREGAÇÃO — ${T.toUpperCase()}`,
        '',
        'I. ABERTURA',
        '   1. Saudação e oração inicial',
        '   2. Leitura do texto base',
        `   3. Tese central: o que a Palavra ensina sobre ${tema}`,
        '',
        'II. CONTEXTO DO TEXTO',
        '   1. Autor, destinatários e data',
        '   2. Situação histórica e cultural',
        '   3. Lugar do texto dentro do livro',
        '',
        'III. DESENVOLVIMENTO (3 pontos)',
        `   1. Primeiro ponto — a definição bíblica de ${tema}`,
        '      a) Texto de apoio',
        '      b) Explicação do termo original',
        '      c) Aplicação imediata',
        `   2. Segundo ponto — os obstáculos a ${tema}`,
        '      a) Exemplo bíblico negativo',
        '      b) Diagnóstico do coração',
        '      c) Advertência pastoral',
        `   3. Terceiro ponto — como viver ${tema} hoje`,
        '      a) Exemplo bíblico positivo',
        '      b) Passos práticos durante a semana',
        '      c) Promessa vinculada à obediência',
        '',
        'IV. ILUSTRAÇÃO CENTRAL',
        '   1. História, testemunho ou analogia',
        '   2. Ponte da ilustração para o texto',
        '',
        'V. APLICAÇÃO PESSOAL',
        '   1. Para o novo convertido',
        '   2. Para o membro maduro',
        '   3. Para quem ainda não creu',
        '',
        'VI. CONCLUSÃO',
        '   1. Recapitulação dos três pontos',
        '   2. Apelo e chamada à decisão',
        '   3. Oração final e bênção'
      ].join('\n');
      return { mostra:m, fala:`Esboço completo sobre ${tema} montado na tela, com abertura, contexto, três pontos de desenvolvimento, ilustração, aplicação e conclusão.` };
    }
  });

  add({
    id:'biblia.plano', nome:'Plano de estudo bíblico', grupo:'Bíblia',
    exemplos:['plano de leitura bíblica','plano de estudo sobre oração'],
    teste: t => /\b(plano de (leitura|estudo)|cronograma biblico|devocional)\b/.test(t),
    executa: (t, ctx, raw) => {
      const tema = ((raw || t).match(/sobre\s+(.+)$/i) || [,'a vida cristã'])[1].trim();
      const dias = ['Segunda','Terça','Quarta','Quinta','Sexta','Sábado','Domingo'];
      const eixos = ['Fundamento bíblico','Exemplos do Antigo Testamento','Ensino de Jesus',
                     'Ensino apostólico','Obstáculos e advertências','Aplicação prática',
                     'Consolidação e oração'];
      const m = [`PLANO SEMANAL DE ESTUDO — ${tema.toUpperCase()}`, ''];
      dias.forEach((d, i) => {
        m.push(`${d.toUpperCase()} — ${eixos[i]}`);
        m.push('   1. Leitura do texto principal');
        m.push('   2. Três perguntas de observação');
        m.push('   3. Uma verdade para memorizar');
        m.push('   4. Um passo de obediência para o dia');
        m.push('');
      });
      return { mostra:m.join('\n'), fala:`Plano semanal de estudo sobre ${tema} organizado em sete dias, cada um com leitura, observação, memorização e aplicação.` };
    }
  });

  /* =========================================================
     8. CONVERSA
     ========================================================= */
  add({
    id:'talk.saudacao', nome:'Saudações', grupo:'Conversa',
    exemplos:['bom dia jarvis','oi','obrigado'],
    teste: t => /\b(bom dia|boa tarde|boa noite|ola|oi|opa|e ai)\b/.test(t),
    executa: () => `${U.saudacao()}, ${Store.cfg.nome}. Estou à disposição.`
  });

  add({
    id:'talk.obrigado', nome:'Agradecimento', grupo:'Conversa',
    exemplos:['obrigado','valeu'],
    teste: t => /\b(obrigad|valeu|agradeco|muito bom)\w*\b/.test(t),
    executa: () => ['Sempre às ordens.','Disponha.','É um prazer servir.','Estou aqui para isso.']
                   [Math.floor(Math.random()*4)]
  });

  add({
    id:'talk.quem', nome:'Identidade', grupo:'Conversa',
    exemplos:['quem é você','qual seu nome','como você funciona'],
    teste: t => /\b(quem (e|eh) voce|qual (o )?seu nome|voce e o que|como voce funciona)\b/.test(t),
    executa: () => 'Sou J.A.R.V.I.S., seu assistente de voz autônomo, executando localmente neste tablet. Escuto continuamente, respondo por voz e controlo os recursos do dispositivo.'
  });

  add({
    id:'talk.nome', nome:'Como me chamar', grupo:'Conversa',
    exemplos:['me chame de pastor','meu nome é Mauro'],
    teste: t => /\b(me chame de|meu nome e|pode me chamar de)\b/.test(t),
    executa: t => {
      const n = (t.match(/(?:me chame de|meu nome e|pode me chamar de)\s+(.+)$/) || [])[1];
      if (!n) return 'Como devo chamá-lo?';
      const nome = n.trim().split(' ').map(w => w[0].toUpperCase() + w.slice(1)).join(' ');
      Store.cfg.nome = nome; Store.save();
      return `Perfeito, ${nome}. Passarei a chamá-lo assim.`;
    }
  });

  /* =========================================================
     ROTEADOR
     ========================================================= */
  async function rotear(fala, ctx){
    const t = t_(fala);
    if (!t) return null;
    for (const s of S){
      try {
        if (s.teste(t, fala)) return { skill:s, r: await s.executa(t, ctx, fala) };
      } catch (e){
        return { skill:s, r:'Houve uma falha ao executar esse comando: ' + e.message };
      }
    }
    return null;
  }

  const grupos = () => [...new Set(S.map(s => s.grupo))];
  const porGrupo = g => S.filter(s => s.grupo === g);

  return { rotear, lista:S, grupos, porGrupo };
})();
