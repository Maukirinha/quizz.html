/* J.A.R.V.I.S. — service worker: opera offline (cache-first com atualização) */
const CACHE = 'jarvis-v1';
const ARQ = [
  './', './index.html', './manifest.webmanifest',
  './css/style.css',
  './js/utils.js', './js/store.js', './js/speech.js', './js/device.js',
  './js/skills.js', './js/autonomy.js', './js/hud.js', './js/app.js',
  './icons/icon-192.png', './icons/icon-512.png'
];

self.addEventListener('install', e => {
  e.waitUntil(caches.open(CACHE).then(c => c.addAll(ARQ)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', e => {
  e.waitUntil(caches.keys()
    .then(ks => Promise.all(ks.filter(k => k !== CACHE).map(k => caches.delete(k))))
    .then(() => self.clients.claim()));
});

self.addEventListener('fetch', e => {
  const req = e.request;
  if (req.method !== 'GET') return;
  const url = new URL(req.url);
  if (url.origin !== location.origin) return;   // APIs externas passam direto

  e.respondWith(
    caches.match(req).then(hit => {
      const rede = fetch(req).then(res => {
        if (res && res.status === 200)
          caches.open(CACHE).then(c => c.put(req, res.clone()));
        return res;
      }).catch(() => hit);
      return hit || rede;
    })
  );
});
