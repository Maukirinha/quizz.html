/* ============================================================
   J.A.R.V.I.S. — app.js
   Orquestração geral: palavra de ativação, máquina de estados,
   interface, menu de comandos e ciclo de vida do PWA.
   ============================================================ */
(() => {

  const $ = id => document.getElementById(id);
  const el = {
    boot:$('boot'), bootBtn:$('bootBtn'), stateDot:$('stateDot'), stateLabel:$('stateLabel'),
    chipBat:$('chipBat'), chipNet:$('chipNet'), chipClock:$('chipClock'),
    hudMode:$('hudMode'), hudHint:$('hudHint'), live:$('liveText'), log:$('log'),
    btnPower:$('btnPower'), btnAuto:$('btnAuto'), btnPush:$('btnPush'),
    btnMute:$('btnMute'), btnMenu:$('btnMenu'),
    sheet:$('sheet'), sheetTabs:$('sheetTabs'), sheetBody:$('sheetBody'),
    sheetClose:$('sheetClose'), flash:$('flash')
  };

  let ativo = false;          // janela de comando aberta (pós wake word)
  let janela = null;          // timeout da janela
  let processando = false;

  /* =========================================================
     SAÍDA (log + voz)
     ========================================================= */
  function linha(quem, texto, classe){
    const d = document.createElement('div');
    d.className = 'l ' + (classe || '');
    d.innerHTML = `<b>${quem} · ${U.hhmm(new Date())}</b>` +
                  texto.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/\n/g,'<br>');
    el.log.appendChild(d);
    el.log.scrollTop = el.log.scrollHeight;
    while (el.log.children.length > 60) el.log.removeChild(el.log.firstChild);
  }

  async function responder(texto, tipo){
    if (!texto) return;
    linha('JARVIS', texto, tipo === 'alerta' ? 'sys' : '');
    await Voz.falar(texto);
  }

  /* saída usada pelo motor autônomo */
  async function saidaAutonoma(texto, tipo){
    linha(tipo === 'alerta' ? 'ALERTA' : 'AUTÔNOMO', texto, 'sys');
    await Voz.falar(texto, { forcar: tipo === 'alerta' });
  }

  /* =========================================================
     ESTADO VISUAL
     ========================================================= */
  function pinta(estado){
    const mapa = {
      parado:      ['MODO ESPERA',   'toque em Escuta para ativar', ''],
      ouvindo:     ['MONITORANDO',   'diga “Jarvis”',               'on'],
      reconectando:['RECONECTANDO',  'reativando o microfone…',     'on'],
      ativo:       ['ESCUTANDO VOCÊ','pode falar o comando',        'wake'],
      processando: ['PROCESSANDO',   'analisando o comando…',       'wake'],
      falando:     ['RESPONDENDO',   '…',                           'on'],
      bloqueado:   ['SEM MICROFONE', 'libere a permissão',          ''],
      erro:        ['FALHA',         'verifique o microfone',       '']
    };
    const [m, h, dot] = mapa[estado] || mapa.parado;
    el.hudMode.textContent = m;
    el.hudHint.textContent = h;
    el.stateDot.className = 'dot ' + dot;
    el.stateLabel.textContent = estado;
    if (estado === 'bloqueado' || estado === 'erro' || estado === 'parado')
      el.btnPower.classList.remove('on');
    Hud.set(estado === 'reconectando' ? 'ouvindo' : estado);
  }

  /* =========================================================
     CONTEXTO ENTREGUE ÀS HABILIDADES
     ========================================================= */
  const ctx = {
    dormir(){ fecharJanela(); Voz.desligar(); pinta('parado'); el.btnPower.classList.remove('on'); },
    setAutonomo(on){
      Store.cfg.autonomo = on; Store.save();
      el.btnAuto.classList.toggle('on', on);
      if (on && !Voz.ligado) ligarEscuta();
    },
    abrirMenu(){ abrirSheet(); },
    limpar(){ el.log.innerHTML = ''; },
    timer(seg, rot){ Auto.novoTimer(seg, rot); }
  };

  /* =========================================================
     PALAVRA DE ATIVAÇÃO E JANELA DE COMANDO
     ========================================================= */
  function achaWake(t){
    for (const w of Store.cfg.wake){
      const i = t.indexOf(w);
      if (i >= 0) return { i, w };
    }
    return null;
  }

  function abrirJanela(){
    ativo = true;
    pinta('ativo');
    Dev.vibrar(35);
    clearTimeout(janela);
    janela = setTimeout(() => {
      ativo = false;
      if (Voz.ligado) pinta('ouvindo');
    }, Store.cfg.janelaAtiva);
  }

  function fecharJanela(){ ativo = false; clearTimeout(janela); }

  /* =========================================================
     PROCESSAMENTO DE COMANDO
     ========================================================= */
  async function processar(fala){
    if (!fala || processando) return;
    processando = true;
    fecharJanela();
    pinta('processando');
    linha('VOCÊ', fala, 'me');

    const res = await Skills.rotear(fala, ctx);

    if (!res){
      const alt = [
        'Não reconheci esse comando. Diga “menu” para ver tudo o que posso fazer.',
        'Comando não identificado. Posso abrir o menu de habilidades, se preferir.',
        'Isso está fora dos meus módulos atuais. Diga “ajuda” para a lista completa.'
      ][Math.floor(Math.random()*3)];
      await responder(alt);
      Store.logCmd(fala, alt);
    } else {
      const r = res.r;
      const mostra = typeof r === 'string' ? r : (r.mostra || r.fala || '');
      const diz    = typeof r === 'string' ? r : (r.fala ?? r.mostra ?? '');
      if (mostra) linha('JARVIS', mostra);
      if (diz && !(typeof r === 'object' && r.silencioso)) await Voz.falar(diz);
      Store.logCmd(fala, mostra);
    }

    processando = false;
    if (Voz.ligado) pinta(Store.cfg.autonomo ? 'ouvindo' : 'ouvindo');
    else pinta('parado');
  }

  /* =========================================================
     LIGAÇÕES DA CAMADA DE VOZ
     ========================================================= */
  Voz.on('parcial', txt => {
    el.live.textContent = txt || '—';
    if (!ativo && !Store.cfg.autonomo && achaWake(U.norm(txt))) abrirJanela();
  });

  Voz.on('final', (txt) => {
    el.live.textContent = '—';
    const t = U.norm(txt);
    if (!t) return;

    // modo autônomo: qualquer frase é candidata a comando
    if (Store.cfg.autonomo){
      const w = achaWake(t);
      const limpo = w ? txt.slice(txt.toLowerCase().indexOf(w.w) + w.w.length).trim() || txt : txt;
      processar(limpo);
      return;
    }

    if (ativo){ processar(txt); return; }

    const w = achaWake(t);
    if (!w) return;                       // ignora conversa ambiente
    const resto = t.slice(w.i + w.w.length).trim();
    if (resto.length > 2) processar(resto);
    else {
      abrirJanela();
      Voz.falar(['Pois não.','Às ordens.','Sim?','Estou ouvindo.'][Math.floor(Math.random()*4)]);
    }
  });

  Voz.on('estado', e => {
    if (processando) return;
    if (e === 'falando') { pinta('falando'); return; }
    if (ativo && (e === 'ouvindo' || e === 'reconectando')) { pinta('ativo'); return; }
    pinta(e);
  });

  Voz.on('erro', msg => linha('SISTEMA', msg, 'sys'));

  /* =========================================================
     CONTROLES
     ========================================================= */
  function ligarEscuta(){
    if (Voz.ligar()){
      el.btnPower.classList.add('on');
      pinta('ouvindo');
    }
  }

  el.btnPower.onclick = () => {
    if (Voz.ligado){ ctx.dormir(); linha('SISTEMA','Escuta desativada.','sys'); }
    else { ligarEscuta(); responder('Escuta ativada. Diga Jarvis a qualquer momento.'); }
  };

  el.btnAuto.onclick = () => {
    const on = !Store.cfg.autonomo;
    ctx.setAutonomo(on);
    responder(on
      ? 'Modo autônomo ativado. Não preciso mais da palavra de ativação e agirei por conta própria em alertas.'
      : 'Modo autônomo desativado.');
  };

  el.btnMute.onclick = () => {
    Store.cfg.mudo = !Store.cfg.mudo; Store.save();
    el.btnMute.classList.toggle('on', !Store.cfg.mudo);
    el.btnMute.firstChild.nodeValue = Store.cfg.mudo ? '🔇' : '🔊';
    linha('SISTEMA', Store.cfg.mudo ? 'Voz silenciada.' : 'Voz reativada.', 'sys');
    if (!Store.cfg.mudo) Voz.falar('Voz reativada.');
  };

  // botão "falar": abre janela de comando sem palavra de ativação
  const push = e => {
    e.preventDefault();
    if (!Voz.ligado) ligarEscuta();
    abrirJanela();
    el.btnPush.classList.add('on');
    Voz.calar();
  };
  const pushOff = () => el.btnPush.classList.remove('on');
  el.btnPush.addEventListener('touchstart', push, { passive:false });
  el.btnPush.addEventListener('mousedown', push);
  ['touchend','mouseup','mouseleave'].forEach(ev => el.btnPush.addEventListener(ev, pushOff));

  el.flash.addEventListener('click', () => { el.flash.hidden = true; Dev.lanterna(false); });

  /* =========================================================
     MENU DE COMANDOS (detalhado, por módulo)
     ========================================================= */
  function abrirSheet(grupo){
    el.sheet.hidden = false;
    const gs = Skills.grupos();
    const atual = grupo || gs[0];
    el.sheetTabs.innerHTML = '';
    gs.forEach(g => {
      const b = document.createElement('button');
      b.textContent = g;
      b.className = g === atual ? 'on' : '';
      b.onclick = () => abrirSheet(g);
      el.sheetTabs.appendChild(b);
    });
    const itens = Skills.porGrupo(atual);
    el.sheetBody.innerHTML =
      `<h3>MÓDULO ${atual.toUpperCase()} — ${itens.length} HABILIDADE(S)</h3>` +
      itens.map((s, i) => `
        <div class="cmd">
          <i>${i+1}. ${s.nome}</i>
          ${(s.exemplos||[]).map(e => `<s>“${e}”</s>`).join('')}
        </div>`).join('') +
      `<h3>COMO ACIONAR</h3>
       <div class="cmd"><i>1. Por palavra de ativação</i><s>Diga “Jarvis” e aguarde o sinal âmbar, depois fale o comando.</s></div>
       <div class="cmd"><i>2. Em frase única</i><s>“Jarvis, que horas são” — comando direto, sem pausa.</s></div>
       <div class="cmd"><i>3. Por toque</i><s>Segure o botão 🎙️ Falar e diga o comando, sem palavra de ativação.</s></div>
       <div class="cmd"><i>4. Modo autônomo</i><s>Com 🤖 ativo, qualquer frase é interpretada como comando e o sistema alerta sozinho sobre bateria, rede, alarmes e lembretes.</s></div>`;
  }
  el.btnMenu.onclick = () => abrirSheet();
  el.sheetClose.onclick = () => el.sheet.hidden = true;

  /* =========================================================
     BARRA SUPERIOR
     ========================================================= */
  function relogio(){ el.chipClock.textContent = U.hhmm(new Date()); }
  setInterval(relogio, 1000); relogio();

  function pintaBateria(b){
    if (!b) { el.chipBat.textContent = '🔋 —'; return; }
    el.chipBat.textContent = (b.carregando ? '⚡ ' : '🔋 ') + b.pct + '%';
  }
  Dev.onBateria = pintaBateria;

  function pintaRede(){
    const r = Dev.estadoRede();
    el.chipNet.textContent = r.online ? '📶 ' + r.tipo : '✈️ offline';
  }
  addEventListener('online', pintaRede);
  addEventListener('offline', pintaRede);
  pintaRede();

  /* =========================================================
     BOOT
     ========================================================= */
  function sequenciaBoot(){
    const ls = [...document.querySelectorAll('.boot-text p')];
    ls.forEach((p, i) => setTimeout(() => p.classList.add('on'), 350 + i * 420));
  }
  sequenciaBoot();

  el.bootBtn.onclick = async () => {
    el.boot.classList.add('hide');
    setTimeout(() => el.boot.style.display = 'none', 800);

    // desbloqueia áudio e microfone no gesto do usuário
    try { await navigator.mediaDevices.getUserMedia({ audio:true })
            .then(s => s.getTracks().forEach(t => t.stop())); } catch {}
    Voz.falar(' ', { forcar:true });

    await Dev.iniciarBateria().then(pintaBateria);
    Dev.manterAcordado(true);
    if (Store.cfg.brilhoOverlay) Dev.brilho(Store.cfg.brilhoOverlay);

    el.btnMute.classList.toggle('on', !Store.cfg.mudo);
    el.btnAuto.classList.toggle('on', Store.cfg.autonomo);

    Auto.iniciar(saidaAutonoma);
    ligarEscuta();

    linha('SISTEMA', 'Núcleo iniciado. ' + Skills.lista.length +
          ' habilidades carregadas em ' + Skills.grupos().length + ' módulos.', 'sys');
    await responder(`${U.saudacao()}, ${Store.cfg.nome}. Sistema J.A.R.V.I.S. online e monitorando. ` +
                    `Diga Jarvis para me acionar, ou toque no menu para ver todos os comandos.`);
  };

  // reativa a escuta ao voltar para o app
  document.addEventListener('visibilitychange', () => {
    if (!document.hidden && Voz.ligado) setTimeout(() => Voz.ligar(), 400);
  });

  /* =========================================================
     PWA
     ========================================================= */
  if ('serviceWorker' in navigator)
    addEventListener('load', () => navigator.serviceWorker.register('sw.js').catch(()=>{}));

})();
