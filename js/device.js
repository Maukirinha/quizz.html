/* ============================================================
   J.A.R.V.I.S. — device.js
   Acesso ao hardware do tablet: bateria, rede, vibração,
   lanterna, tela cheia, wake-lock, brilho, área de
   transferência, compartilhamento e localização.
   ============================================================ */
const Dev = (() => {

  let bat = null, wakeLock = null;
  let torchTrack = null, torchOn = false;

  /* ---------- bateria ---------- */
  async function iniciarBateria(){
    if (!navigator.getBattery) return null;
    try {
      bat = await navigator.getBattery();
      ['levelchange','chargingchange'].forEach(e =>
        bat.addEventListener(e, () => Dev.onBateria(estadoBateria())));
      return estadoBateria();
    } catch { return null; }
  }

  function estadoBateria(){
    if (!bat) return null;
    return {
      pct: Math.round(bat.level * 100),
      carregando: bat.charging,
      restante: bat.dischargingTime && bat.dischargingTime !== Infinity
                ? bat.dischargingTime : null
    };
  }

  /* ---------- rede ---------- */
  function estadoRede(){
    const c = navigator.connection || {};
    return {
      online: navigator.onLine,
      tipo: c.effectiveType || '—',
      down: c.downlink || null
    };
  }

  /* ---------- vibração ---------- */
  const vibrar = p => { try { navigator.vibrate && navigator.vibrate(p); } catch {} };

  /* ---------- tela ---------- */
  async function telaCheia(on){
    try {
      if (on) await document.documentElement.requestFullscreen();
      else if (document.fullscreenElement) await document.exitFullscreen();
      return true;
    } catch { return false; }
  }

  async function travarOrientacao(modo){
    try { await screen.orientation.lock(modo); return true; } catch { return false; }
  }

  async function manterAcordado(on){
    try {
      if (on){
        if (!('wakeLock' in navigator)) return false;
        wakeLock = await navigator.wakeLock.request('screen');
        wakeLock.addEventListener('release', () => { wakeLock = null; });
        return true;
      }
      if (wakeLock){ await wakeLock.release(); wakeLock = null; }
      return true;
    } catch { return false; }
  }

  // brilho "virtual": overlay escuro (o navegador não muda o brilho real)
  function brilho(pct){
    pct = Math.min(100, Math.max(10, pct));
    const el = document.getElementById('dim');
    el.style.opacity = String((100 - pct) / 130);
    Store.cfg.brilhoOverlay = pct; Store.save();
    return pct;
  }

  /* ---------- lanterna (torch da câmera) ---------- */
  async function lanterna(on){
    try {
      if (on){
        if (!torchTrack){
          const s = await navigator.mediaDevices.getUserMedia({
            video: { facingMode: 'environment' }
          });
          torchTrack = s.getVideoTracks()[0];
        }
        const cap = torchTrack.getCapabilities ? torchTrack.getCapabilities() : {};
        if (!cap.torch){ pararLanterna(); return 'sem-torch'; }
        await torchTrack.applyConstraints({ advanced: [{ torch: true }] });
        torchOn = true; return true;
      }
      if (torchTrack){
        try { await torchTrack.applyConstraints({ advanced: [{ torch: false }] }); } catch {}
        pararLanterna();
      }
      torchOn = false; return true;
    } catch { return false; }
  }

  function pararLanterna(){
    if (torchTrack){ try { torchTrack.stop(); } catch {} torchTrack = null; }
    torchOn = false;
  }

  // fallback: tela branca no máximo como lanterna
  function lanternaTela(on){
    const f = document.getElementById('flash');
    f.hidden = !on;
    return on;
  }

  /* ---------- clipboard / share ---------- */
  async function copiar(txt){
    try { await navigator.clipboard.writeText(txt); return true; } catch { return false; }
  }
  async function compartilhar(texto, titulo){
    try {
      if (!navigator.share) return false;
      await navigator.share({ title: titulo || 'J.A.R.V.I.S.', text: texto });
      return true;
    } catch { return false; }
  }

  /* ---------- localização ---------- */
  function local(){
    return new Promise(res => {
      if (!navigator.geolocation) return res(null);
      navigator.geolocation.getCurrentPosition(
        p => res({ lat: p.coords.latitude, lon: p.coords.longitude }),
        () => res(null),
        { timeout: 8000, maximumAge: 300000 }
      );
    });
  }

  /* ---------- notificações ---------- */
  async function notificar(titulo, corpo){
    try {
      if (!('Notification' in window)) return false;
      if (Notification.permission !== 'granted'){
        const p = await Notification.requestPermission();
        if (p !== 'granted') return false;
      }
      new Notification(titulo, { body: corpo, icon: 'icons/icon-192.png' });
      return true;
    } catch { return false; }
  }

  return {
    iniciarBateria, estadoBateria, estadoRede, vibrar, telaCheia,
    travarOrientacao, manterAcordado, brilho, lanterna, lanternaTela,
    pararLanterna, copiar, compartilhar, local, notificar,
    get torchAtiva(){ return torchOn; },
    onBateria: () => {}
  };
})();
