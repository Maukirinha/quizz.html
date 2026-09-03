/* ============================================================
   Gera dist/jarvis-artifact.html — a mesma aplicação de arquivo
   único, sem as tags de documento (doctype, html, head, body),
   no formato exigido para publicação como página hospedada.

   Uso:  node tools/build-artifact.js   (depois de build-single.js)
   ============================================================ */
const fs = require('fs');
const path = require('path');
const raiz = path.join(__dirname, '..');

let s = fs.readFileSync(path.join(raiz, 'jarvis.html'), 'utf8');

const estilo = s.match(/<style>[\s\S]*?<\/style>/)[0];
const corpo  = s.match(/<body>([\s\S]*)<\/body>/)[1].trim();

const saida =
`<title>Núcleo J.A.R.V.I.S.</title>
${estilo}

${corpo}
`;

fs.mkdirSync(path.join(raiz, 'dist'), { recursive: true });
fs.writeFileSync(path.join(raiz, 'dist/jarvis-artifact.html'), saida);

const kb = (Buffer.byteLength(saida) / 1024).toFixed(0);
console.log(`dist/jarvis-artifact.html — ${kb} KB`);
for (const tag of ['<!doctype', '<html>', '<head>', '<body>'])
  if (saida.toLowerCase().includes(tag)) console.log('ATENÇÃO: sobrou ' + tag);
console.log('scripts embutidos:', (saida.match(/<script>/g) || []).length);
