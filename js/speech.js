/* ============================================================
   J.A.R.V.I.S. — speech.js
   Camada de voz: reconhecimento contínuo com auto-recuperação
   e síntese de fala (TTS) com fila.
   ============================================================ */
const Voz = (() => {
  const SR = window.SpeechRecognition || window.webkitSpeechRecognition;
  const suportaOuvir = !!SR;
  const suportaFalar = 'speechSynthesis' in window;

  let rec = null;
  let ligado = false;          // usuário quer escutar
  let rodando = false;         // engine ativa neste instante
  let falando = false;         // TTS em execução
  let reinicios = 0;
  let ultimoReinicio = 0;
  let timerRestart = null;

  const ev = {
    parcial: () => {}, final: () => {},
    estado: () => {}, erro: () => {}
  };

  /* ---------- reconhecimento ---------- */
  function criar(){
    const r = new SR();
    r.lang = Store.cfg.idioma;
    r.continuous = true;
    r.interimResults = true;
    r.maxAlternatives = 3;

    r.onstart = () => { rodando = true; reinicios = 0; ev.estado('ouvindo'); };

    r.onresult = e => {
      for (let i = e.resultIndex; i < e.results.length; i++){
        const res = e.results[i];
        const alts = [];
        for (let a = 0; a < res.length; a++) alts.push(res[a].transcript);
        if (res.isFinal) ev.final(alts[0].trim(), alts);
        else ev.parcial(alts[0].trim());
      }
    };

    r.onerror = e => {
      // no-speech / aborted / network são recuperáveis
      if (e.error === 'not-allowed' || e.error === 'service-not-allowed'){
        ligado = false;
        ev.erro('Permissão de microfone negada. Libere o microfone nas configurações do navegador.');
        ev.estado('bloqueado');
        return;
      }
      if (e.error === 'audio-capture'){
        ligado = false;
        ev.erro('Nenhum microfone detectado neste tablet.');
        ev.estado('erro');
        return;
      }
      if (e.error === 'network') ev.erro('Rede instável no serviço de voz. Reconectando…');
    };

    r.onend = () => {
      rodando = false;
      ev.estado(ligado ? 'reconectando' : 'parado');
      if (ligado && !falando) agendarRestart();
    };
    return r;
  }

  function agendarRestart(){
    clearTimeout(timerRestart);
    const agora = Date.now();
    if (agora - ultimoReinicio < 1200) reinicios++; else reinicios = 0;
    ultimoReinicio = agora;
    // backoff progressivo para não travar o tablet em loop
    const espera = Math.min(150 * Math.pow(1.8, reinicios), 6000);
    timerRestart = setTimeout(start, espera);
  }

  function start(){
    if (!suportaOuvir || !ligado || rodando || falando) return;
    if (!rec) rec = criar();
    rec.lang = Store.cfg.idioma;
    try { rec.start(); }
    catch (err){ if (!/already started/i.test(err.message)) agendarRestart(); }
  }

  function ligar(){
    if (!suportaOuvir) { ev.erro('Este navegador não suporta reconhecimento de voz. Use o Chrome.'); return false; }
    ligado = true; start(); return true;
  }

  function desligar(){
    ligado = false; clearTimeout(timerRestart);
    try { rec && rec.stop(); } catch {}
    ev.estado('parado');
  }

  /* ---------- síntese ---------- */
  let vozes = [];
  function carregarVozes(){
    if (!suportaFalar) return;
    vozes = speechSynthesis.getVoices() || [];
  }
  if (suportaFalar){
    carregarVozes();
    speechSynthesis.onvoiceschanged = carregarVozes;
  }

  function escolherVoz(){
    if (!vozes.length) carregarVozes();
    const alvo = Store.cfg.voz;
    if (alvo){
      const v = vozes.find(v => v.name === alvo);
      if (v) return v;
    }
    const pt = vozes.filter(v => /pt[-_]?BR/i.test(v.lang) || /pt/i.test(v.lang));
    return pt.find(v => /google/i.test(v.name))
        || pt.find(v => /(luciana|daniel|felipe|brasil)/i.test(v.name))
        || pt[0] || vozes[0] || null;
  }

  const fila = [];
  function falar(texto, opts = {}){
    if (!texto) return Promise.resolve();
    if (Store.cfg.mudo && !opts.forcar) return Promise.resolve();
    if (!suportaFalar) return Promise.resolve();
    return new Promise(resolve => {
      fila.push({ texto, opts, resolve });
      if (!falando) proximo();
    });
  }

  function proximo(){
    const item = fila.shift();
    if (!item){ falando = false; ev.estado(ligado ? 'ouvindo' : 'parado'); start(); return; }

    falando = true;
    ev.estado('falando');
    // pausa a escuta para não ouvir a própria voz (eco)
    try { rec && rodando && rec.stop(); } catch {}

    const u = new SpeechSynthesisUtterance(item.texto);
    const v = escolherVoz();
    if (v) u.voice = v;
    u.lang = Store.cfg.idioma;
    u.rate = item.opts.taxa ?? Store.cfg.taxa;
    u.pitch = item.opts.tom ?? Store.cfg.tom;
    u.volume = item.opts.volume ?? Store.cfg.volume;

    let terminou = false;
    const fim = () => {
      if (terminou) return;
      terminou = true;
      clearTimeout(item.wd);
      item.resolve();
      setTimeout(proximo, 120);
    };
    u.onend = fim;
    u.onerror = fim;

    try { speechSynthesis.cancel(); speechSynthesis.speak(u); }
    catch { fim(); }

    // watchdog: alguns Androids nunca disparam onend
    item.wd = setTimeout(() => {
      if (!terminou){ try { speechSynthesis.cancel(); } catch {} fim(); }
    }, 3500 + item.texto.length * 95);
  }

  function calar(){
    fila.length = 0;
    try { speechSynthesis.cancel(); } catch {}
    falando = false;
    start();
  }

  return {
    suportaOuvir, suportaFalar,
    on(nome, fn){ ev[nome] = fn; },
    ligar, desligar, falar, calar,
    get ligado(){ return ligado; },
    get falando(){ return falando; },
    get vozes(){ if(!vozes.length) carregarVozes(); return vozes; }
  };
})();
