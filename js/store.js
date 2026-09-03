/* ============================================================
   J.A.R.V.I.S. — store.js
   Persistência local (localStorage) de config, notas, listas,
   lembretes, alarmes, rotinas e memória de contexto.
   ============================================================ */
const Store = (() => {
  const KEY = 'jarvis.v1';

  const DEFAULT = {
    cfg: {
      nome: 'Senhor',
      wake: ['jarvis', 'jarves', 'jarbas', 'darvis', 'javis', 'jarvi'],
      idioma: 'pt-BR',
      voz: null,
      taxa: 1.02,
      tom: 0.9,
      volume: 1,
      autonomo: false,
      mudo: false,
      janelaAtiva: 12000,      // ms que fica "acordado" após wake word
      confirmarAcao: true,
      anunciarHora: false,
      brilhoOverlay: 0
    },
    notas: [],
    listas: {},              // { compras: [itens...] }
    lembretes: [],           // { id, texto, quando(ts), feito }
    alarmes: [],             // { id, h, m, rotulo, dias[], ativo }
    rotinas: [],             // { id, gatilho, acoes[] }
    historico: [],           // últimos comandos
    stats: { comandos: 0, desde: Date.now() }
  };

  let db = load();

  function load(){
    try {
      const raw = localStorage.getItem(KEY);
      if (!raw) return structuredClone(DEFAULT);
      const parsed = JSON.parse(raw);
      return deepMerge(structuredClone(DEFAULT), parsed);
    } catch { return structuredClone(DEFAULT); }
  }

  function deepMerge(base, extra){
    for (const k in extra){
      if (extra[k] && typeof extra[k] === 'object' && !Array.isArray(extra[k]))
        base[k] = deepMerge(base[k] || {}, extra[k]);
      else base[k] = extra[k];
    }
    return base;
  }

  let t = null;
  function save(){
    clearTimeout(t);
    t = setTimeout(() => {
      try { localStorage.setItem(KEY, JSON.stringify(db)); } catch {}
    }, 150);
  }

  return {
    get db(){ return db; },
    get cfg(){ return db.cfg; },
    save,
    reset(){ db = structuredClone(DEFAULT); save(); },

    addNota(texto){
      db.notas.unshift({ id: U.uid(), texto, ts: Date.now() });
      db.notas = db.notas.slice(0, 200); save();
    },
    addLembrete(texto, quando){
      const l = { id: U.uid(), texto, quando, feito: false };
      db.lembretes.push(l); save(); return l;
    },
    addAlarme(h, m, rotulo){
      const a = { id: U.uid(), h, m, rotulo: rotulo || '', ativo: true, ultimo: 0 };
      db.alarmes.push(a); save(); return a;
    },
    addItemLista(lista, item){
      db.listas[lista] = db.listas[lista] || [];
      db.listas[lista].push(item); save();
    },
    logCmd(texto, resposta){
      db.historico.unshift({ texto, resposta, ts: Date.now() });
      db.historico = db.historico.slice(0, 120);
      db.stats.comandos++; save();
    }
  };
})();
