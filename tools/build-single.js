/* ============================================================
   Gera jarvis.html — versão de arquivo único, com CSS, JavaScript,
   ícones e o reator animado embutidos. Serve para hospedar o app
   em qualquer lugar que aceite um único arquivo HTML.

   Uso:  node tools/build-single.js
   ============================================================ */
const fs = require('fs');
const path = require('path');
const raiz = path.join(__dirname, '..');
const ler = p => fs.readFileSync(path.join(raiz, p), 'utf8');
const b64 = p => fs.readFileSync(path.join(raiz, p)).toString('base64');

let html = ler('index.html');

/* 1. CSS embutido */
html = html.replace(
  /<link rel="stylesheet" href="css\/style\.css">/,
  '<style>\n' + ler('css/style.css') + '\n</style>'
);

/* 2. Ícones como data URI */
const reator = 'data:image/svg+xml;base64,' + b64('icons/reactor.svg');
const favicon = 'data:image/png;base64,' + b64('icons/favicon-32.png');
html = html.replace(/icons\/reactor\.svg/g, reator);
html = html.replace(
  /<link rel="icon"[^>]*>\s*<link rel="icon"[^>]*>\s*<link rel="apple-touch-icon"[^>]*>/,
  `<link rel="icon" href="${favicon}">`
);

/* 3. Sem manifesto nem service worker: não há arquivos externos a cachear */
html = html.replace(/<link rel="manifest"[^>]*>\s*/, '');

/* 4. JavaScript embutido, na mesma ordem das tags originais */
html = html.replace(/<script src="(js\/[^"]+)"><\/script>/g,
  (_, arq) => '<script>\n' + ler(arq) + '\n</script>');

/* 5. Registro do service worker é inócuo aqui, mas removê-lo evita erro no console */
html = html.replace(
  /  if \('serviceWorker' in navigator\)\n\s+addEventListener\('load'[^\n]*\n/,
  '  /* versão de arquivo único: sem service worker */\n'
);

fs.writeFileSync(path.join(raiz, 'jarvis.html'), html);
const kb = (fs.statSync(path.join(raiz, 'jarvis.html')).size / 1024).toFixed(0);
console.log(`jarvis.html gerado — ${kb} KB, sem nenhuma dependência externa`);

/* conferência: nada de referências locais deve sobrar */
const restos = [...html.matchAll(/(?:src|href)="(?!data:|https?:|#)([^"]+)"/g)].map(m => m[1]);
console.log(restos.length ? 'REFERÊNCIAS EXTERNAS RESTANTES: ' + restos.join(', ')
                          : 'nenhuma referência externa restante');
