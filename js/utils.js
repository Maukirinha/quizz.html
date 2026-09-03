/* ============================================================
   J.A.R.V.I.S. — utils.js
   Normalização de texto, números por extenso, tempo e matemática
   ============================================================ */
const U = (() => {

  /* -- texto -------------------------------------------------- */
  const norm = s => (s || '')
    .toLowerCase()
    .normalize('NFD').replace(/[\u0300-\u036f]/g, '')
    .replace(/[^\w\s:+\-*/.,%]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim();

  const has = (t, ...words) => words.some(w => t.includes(w));
  const hasAll = (t, ...words) => words.every(w => t.includes(w));

  /* -- números por extenso ------------------------------------ */
  const UNI = {
    zero:0, um:1, uma:1, dois:2, duas:2, tres:3, quatro:4, cinco:5, seis:6,
    sete:7, oito:8, nove:9, dez:10, onze:11, doze:12, treze:13, catorze:14,
    quatorze:14, quinze:15, dezesseis:16, dezessete:17, dezoito:18,
    dezenove:19, vinte:20, trinta:30, quarenta:40, cinquenta:50, sessenta:60,
    setenta:70, oitenta:80, noventa:90, cem:100, cento:100, duzentos:200,
    trezentos:300, quatrocentos:400, quinhentos:500, seiscentos:600,
    setecentos:700, oitocentos:800, novecentos:900, mil:1000, meia:30
  };

  function palavraParaNumero(txt){
    const parts = norm(txt).split(' ');
    let total = 0, cur = 0, achou = false;
    for (const p of parts){
      if (/^\d+$/.test(p)) { cur += +p; achou = true; continue; }
      if (p === 'e') continue;
      if (UNI[p] === undefined) continue;
      achou = true;
      const v = UNI[p];
      if (v === 1000){ cur = (cur || 1) * 1000; total += cur; cur = 0; }
      else cur += v;
    }
    return achou ? total + cur : null;
  }

  /* -- extrai duração em segundos ----------------------------- */
  function duracaoSegundos(txt){
    const t = norm(txt);
    let seg = 0, achou = false;
    const re = /(\d+|[a-z]+(?:\s+e\s+[a-z]+)*)\s*(horas?|h\b|minutos?|min\b|segundos?|seg\b|s\b)/g;
    let m;
    while ((m = re.exec(t))){
      const n = /^\d+$/.test(m[1]) ? +m[1] : palavraParaNumero(m[1]);
      if (n === null) continue;
      achou = true;
      if (/^h/.test(m[2])) seg += n * 3600;
      else if (/^m/.test(m[2])) seg += n * 60;
      else seg += n;
    }
    return achou ? seg : null;
  }

  const fmtDur = s => {
    s = Math.max(0, Math.round(s));
    const h = Math.floor(s/3600), m = Math.floor(s%3600/60), x = s%60;
    const p = [];
    if (h) p.push(h + (h===1?' hora':' horas'));
    if (m) p.push(m + (m===1?' minuto':' minutos'));
    if (x || !p.length) p.push(x + (x===1?' segundo':' segundos'));
    return p.join(' e ');
  };

  const pad = n => String(n).padStart(2,'0');
  const hhmm = d => pad(d.getHours()) + ':' + pad(d.getMinutes());

  const DIAS = ['domingo','segunda-feira','terça-feira','quarta-feira',
                'quinta-feira','sexta-feira','sábado'];
  const MESES = ['janeiro','fevereiro','março','abril','maio','junho','julho',
                 'agosto','setembro','outubro','novembro','dezembro'];

  const dataExtenso = d =>
    `${DIAS[d.getDay()]}, ${d.getDate()} de ${MESES[d.getMonth()]} de ${d.getFullYear()}`;

  const saudacao = () => {
    const h = new Date().getHours();
    if (h < 5)  return 'Boa madrugada';
    if (h < 12) return 'Bom dia';
    if (h < 18) return 'Boa tarde';
    return 'Boa noite';
  };

  /* -- matemática segura -------------------------------------- */
  const MATH_WORDS = [
    [/\bmais\b|\bsomado (?:a|com)\b|\bsoma de\b|\badicionar?\b/g,'+'],
    [/\bmenos\b|\bsubtrair\b|\bsubtraido de\b/g,'-'],
    [/\bvezes\b|\bmultiplicado por\b|\bmultiplicar por\b|\bx\b/g,'*'],
    [/\bdividido por\b|\bdividir por\b|\bsobre\b/g,'/'],
    [/\belevado a\b|\bna potencia de\b/g,'**'],
    [/\bpor cento de\b/g,'% de'],
    [/\braiz (?:quadrada )?de\b/g,'sqrt'],
    [/\bvirgula\b/g,'.'],
    [/\bponto\b/g,'.']
  ];

  function calcular(txt){
    let t = norm(txt)
      .replace(/^(jarvis|calcule|calcular|quanto e|quanto eh|quanto da|qual e o resultado de)\s*/g,'');
    MATH_WORDS.forEach(([re, rep]) => { t = t.replace(re, rep); });

    // "20 % de 350"
    const pc = t.match(/(\d+(?:\.\d+)?)\s*%\s*de\s*(\d+(?:\.\d+)?)/);
    if (pc) return (+pc[1] / 100) * (+pc[2]);

    // "sqrt 81"
    const sq = t.match(/sqrt\s*(\d+(?:\.\d+)?)/);
    if (sq) return Math.sqrt(+sq[1]);

    // converte números por extenso soltos
    t = t.replace(/[a-z]+(?:\s+e\s+[a-z]+)*/g, w => {
      const n = palavraParaNumero(w);
      return n === null ? ' ' : ' ' + n + ' ';
    });

    t = t.replace(/[^0-9+\-*/().\s]/g,'').trim();
    if (!/\d/.test(t) || !/[+\-*/]/.test(t)) return null;
    try {
      const r = Function('"use strict";return (' + t + ')')();
      return Number.isFinite(r) ? r : null;
    } catch { return null; }
  }

  const numBonito = n =>
    Math.abs(n % 1) < 1e-9 ? String(Math.round(n))
                           : n.toFixed(2).replace('.', ' vírgula ');

  const uid = () => Date.now().toString(36) + Math.random().toString(36).slice(2,6);

  return { norm, has, hasAll, palavraParaNumero, duracaoSegundos, fmtDur,
           pad, hhmm, DIAS, MESES, dataExtenso, saudacao, calcular, numBonito, uid };
})();
