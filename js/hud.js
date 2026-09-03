/* ============================================================
   J.A.R.V.I.S. — hud.js
   Interface holográfica: reator de arco desenhado em canvas,
   reagindo ao estado do assistente.
   ============================================================ */
const Hud = (() => {
  const cv = document.getElementById('reactor');
  const ctx = cv.getContext('2d');
  const W = cv.width, H = cv.height, cx = W/2, cy = H/2;

  let estado = 'parado';   // parado | ouvindo | ativo | falando | processando
  let energia = 0;         // 0..1 suavizado
  let alvo = 0;
  let ang = 0;

  const COR = {
    parado:      '#3a5a70',
    ouvindo:     '#39d7ff',
    ativo:       '#ffb648',
    falando:     '#37f0a0',
    processando: '#b072ff',
    bloqueado:   '#ff4d5e'
  };

  function set(e){
    estado = e;
    alvo = e === 'parado' ? .12
         : e === 'ouvindo' ? .40
         : e === 'ativo' ? .75
         : e === 'falando' ? .95
         : e === 'processando' ? .85 : .3;
  }

  function anel(raio, largura, cor, de, ate, alpha){
    ctx.beginPath();
    ctx.arc(cx, cy, raio, de, ate);
    ctx.strokeStyle = cor;
    ctx.globalAlpha = alpha;
    ctx.lineWidth = largura;
    ctx.lineCap = 'round';
    ctx.stroke();
    ctx.globalAlpha = 1;
  }

  function frame(){
    energia += (alvo - energia) * .06;
    ang += .004 + energia * .02;
    const c = COR[estado] || COR.parado;
    const puls = 1 + Math.sin(Date.now()/380) * .035 * (0.3 + energia);

    ctx.clearRect(0, 0, W, H);
    ctx.shadowBlur = 26 * energia;
    ctx.shadowColor = c;

    // anel externo segmentado
    for (let i = 0; i < 16; i++){
      const a = ang + i * (Math.PI*2/16);
      anel(270 * puls, 3, c, a, a + .22, .25 + energia * .5);
    }
    // anel de progresso girando ao contrário
    for (let i = 0; i < 4; i++){
      const a = -ang * 1.7 + i * (Math.PI/2);
      anel(238, 6, c, a, a + .9, .18 + energia * .45);
    }
    // anel fino contínuo
    anel(205, 1.5, c, 0, Math.PI*2, .2 + energia * .3);

    // barras radiais (equalizador)
    const n = 56;
    for (let i = 0; i < n; i++){
      const a = i * (Math.PI*2/n) + ang * .5;
      const amp = (Math.sin(i * 1.7 + Date.now()/160) * .5 + .5) * energia;
      const r1 = 150, r2 = r1 + 12 + amp * 46;
      ctx.beginPath();
      ctx.moveTo(cx + Math.cos(a)*r1, cy + Math.sin(a)*r1);
      ctx.lineTo(cx + Math.cos(a)*r2, cy + Math.sin(a)*r2);
      ctx.strokeStyle = c;
      ctx.globalAlpha = .18 + amp * .6;
      ctx.lineWidth = 2.4;
      ctx.stroke();
      ctx.globalAlpha = 1;
    }

    // núcleo
    const g = ctx.createRadialGradient(cx, cy, 4, cx, cy, 130 * puls);
    g.addColorStop(0, c);
    g.addColorStop(.28, c + '66');
    g.addColorStop(1, 'transparent');
    ctx.globalAlpha = .25 + energia * .6;
    ctx.fillStyle = g;
    ctx.beginPath(); ctx.arc(cx, cy, 130 * puls, 0, Math.PI*2); ctx.fill();
    ctx.globalAlpha = 1;

    anel(96 * puls, 2, c, 0, Math.PI*2, .5 + energia * .4);
    ctx.shadowBlur = 0;

    requestAnimationFrame(frame);
  }
  requestAnimationFrame(frame);

  return { set };
})();
