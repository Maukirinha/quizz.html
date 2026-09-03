/* ============================================================
   J.A.R.V.I.S. — autonomy.js
   Motor autônomo: vigia o relógio, os lembretes, os alarmes,
   a bateria e a rede, e age por iniciativa própria.
   ============================================================ */
const Auto = (() => {

  let tick = null;
  const timers = [];              // temporizadores voláteis
  let ultimaHoraAnunciada = -1;
  let avisoBateria = { b20:false, b10:false, cheia:false };
  let ultimoOnline = navigator.onLine;

  let saida = () => {};           // injetado pelo app (fala + log)

  /* ---------- temporizadores ---------- */
  function novoTimer(segundos, rotulo){
    const t = {
      id: U.uid(), rotulo,
      fim: Date.now() + segundos * 1000,
      handle: setTimeout(() => {
        Dev.vibrar([300,150,300,150,500]);
        Dev.notificar('J.A.R.V.I.S.', `${rotulo} concluído`);
        saida(`${rotulo} concluído. O tempo de ${U.fmtDur(segundos)} terminou, ${Store.cfg.nome}.`, 'alerta');
        remover(t.id);
      }, segundos * 1000)
    };
    timers.push(t);
    return t;
  }
  function remover(id){
    const i = timers.findIndex(x => x.id === id);
    if (i >= 0){ clearTimeout(timers[i].handle); timers.splice(i, 1); }
  }
  const listarTimers = () => timers.map(t => ({
    rotulo: t.rotulo, restante: Math.max(0, (t.fim - Date.now()) / 1000)
  }));

  /* ---------- ciclo de vigilância (1 s) ---------- */
  function ciclo(){
    const agora = new Date();

    /* lembretes vencidos */
    let mudou = false;
    for (const l of Store.db.lembretes){
      if (!l.feito && l.quando <= Date.now()){
        l.feito = true; mudou = true;
        Dev.vibrar([200,100,200]);
        Dev.notificar('Lembrete', l.texto);
        saida(`Lembrete, ${Store.cfg.nome}: ${l.texto}.`, 'alerta');
      }
    }
    if (mudou) Store.save();

    /* alarmes */
    for (const a of Store.db.alarmes){
      if (!a.ativo) continue;
      const chave = agora.toDateString() + a.h + ':' + a.m;
      if (agora.getHours() === a.h && agora.getMinutes() === a.m && a.ultimo !== chave){
        a.ultimo = chave; Store.save();
        Dev.vibrar([400,200,400,200,600]);
        Dev.notificar('Alarme', `${U.pad(a.h)}:${U.pad(a.m)} ${a.rotulo}`);
        saida(`Alarme das ${a.h} e ${U.pad(a.m)}. ${a.rotulo || 'Hora de acordar'}, ${Store.cfg.nome}.`, 'alerta');
      }
    }

    /* somente no modo autônomo: iniciativa própria */
    if (Store.cfg.autonomo){
      /* anúncio de hora cheia */
      if (Store.cfg.anunciarHora && agora.getMinutes() === 0 &&
          agora.getHours() !== ultimaHoraAnunciada){
        ultimaHoraAnunciada = agora.getHours();
        saida(`São ${agora.getHours()} horas em ponto.`, 'auto');
      }

      /* bateria */
      const b = Dev.estadoBateria();
      if (b){
        if (!b.carregando && b.pct <= 10 && !avisoBateria.b10){
          avisoBateria.b10 = true;
          saida(`Alerta crítico: bateria em ${b.pct} por cento. Conecte o carregador imediatamente, ${Store.cfg.nome}.`, 'alerta');
        } else if (!b.carregando && b.pct <= 20 && !avisoBateria.b20){
          avisoBateria.b20 = true;
          saida(`Atenção: a bateria caiu para ${b.pct} por cento.`, 'auto');
        } else if (b.carregando && b.pct >= 100 && !avisoBateria.cheia){
          avisoBateria.cheia = true;
          saida('Bateria totalmente carregada. Pode desconectar o carregador.', 'auto');
        }
        if (b.carregando){ avisoBateria.b20 = avisoBateria.b10 = false; }
        if (!b.carregando) avisoBateria.cheia = false;
      }

      /* rede */
      if (navigator.onLine !== ultimoOnline){
        ultimoOnline = navigator.onLine;
        saida(ultimoOnline
          ? 'Conexão com a internet restabelecida.'
          : 'Perdi a conexão com a internet. Continuo operando com os módulos locais.', 'auto');
      }
    }
  }

  function iniciar(fnSaida){
    saida = fnSaida;
    clearInterval(tick);
    tick = setInterval(ciclo, 1000);
  }

  return { iniciar, novoTimer, remover, listarTimers };
})();
