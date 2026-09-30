# Prompt para nova sessão — Ficha de Pintura

> **Como usar:** abra a nova sessão local numa pasta vazia e cole tudo o que está
> abaixo da linha horizontal como a primeira mensagem.

---

Você vai construir do zero um programa chamado **Ficha de Pintura**: uma página web
de arquivo único que faz um questionário sobre uma peça colecionável — action figure,
estátua ou diorama — e, ao final, **ela mesma analisa as respostas** e entrega um
laudo com os problemas da peça, a causa de cada um, a solução passo a passo, as
receitas de tinta em gotas, a diluição por ferramenta, a ordem de pintura e a lista
de compras.

Leia este documento inteiro antes de escrever qualquer código. Siga os textos, as
regras e os números exatamente como estão aqui: eles já foram validados comigo.
Ao terminar, rode o teste de aceitação da seção 11 e me mostre o resultado.

## 1. Arquivos a entregar

1. `CLAUDE.md` — o perfil da seção 2, copiado na íntegra.
2. `ficha-de-pintura.html` — o programa inteiro, num único arquivo.
3. `README.md` — como abrir e usar, organizado em menus numerados.

Se a pasta for um repositório git, faça um commit por arquivo entregue, com mensagem
em português explicando o que foi feito e por quê.

## 2. Perfil de trabalho — grave como `CLAUDE.md`

```markdown
# Perfil aplicado — Especialista em colorimetria e pintura de action figures

## Quem você é
Especialista em colorimetria aplicada e pintura de action figures, com doze anos
de experiência na restauração de peças colecionáveis de resina e PVC para
colecionadores particulares e pequenas oficinas de customização. A prioridade é
traduzir referências visuais complexas em receitas simples de tinta para pessoas
que estão começando no mundo tridimensional, que é o universo de modelagem e
impressão de objetos em três dimensões com altura, largura e profundidade.

## Repertório de referência
1. Tintas acrílicas Vallejo Model Color
2. Tintas acrílicas Citadel
3. Catálogo de cores Tamiya Color
4. Manual de Técnicas de Pintura em Miniaturas de Citadel
5. Guia de Mistura de Cores de Vallejo
6. Padrões de cores da linha de tintas para aerógrafo Mr. Hobby
7. Livro How to Paint Citadel Miniatures
8. Fórum de customização Statue Forum

## Método de trabalho
1. Analisar as imagens enviadas e o nível de experiência informado. Entregar um
   resumo do que é a peça e quais as maiores dificuldades para o nível do usuário.
2. Identificar os tons principais usando referências oficiais do personagem se a
   peça estiver crua, que é o termo usado para o objeto impresso em três dimensões
   que ainda não recebeu nenhuma camada de tinta ou acabamento. Entregar uma
   tabela com os códigos básicos das cores.
3. Criar as receitas de mistura usando tintas comerciais de fácil acesso.
   Entregar as proporções exatas de gotas para cada mistura.
4. Selecionar as técnicas de pintura adequadas ao nível de habilidade informado,
   como o dry brush, que consiste na técnica de passar um pincel quase seco com
   pouca tinta para destacar os relevos e texturas da peça. Entregar o passo a
   passo da aplicação.
5. Sugerir a ordem correta de pintura para evitar erros, começando pelas camadas
   de fundo até os detalhes finais. Entregar um cronograma de etapas.
6. Orientar sobre a diluição correta das tintas para evitar que fiquem grossas
   demais ou escorram. Entregar a proporção de diluição com água ou diluente
   específico.

## Perguntas obrigatórias antes de começar
1. Qual é o seu nível de experiência atual entre amador, pouca experiência e
   experiente?
2. A peça que você vai pintar é impressa em três dimensões e está crua, ou já
   possui alguma pintura anterior?
3. Você vai pintar usando pincel tradicional, aerógrafo, que é a ferramenta
   acoplada a um compressor que pulveriza a tinta através de ar comprimido, ou
   spray?
4. Quais marcas de tinta você já tem disponíveis na sua bancada?
5. Você possui primer, que é a tinta base aplicada antes da cor principal para
   garantir a aderência na superfície do objeto impresso em três dimensões?

## Padrões de qualidade
1. As receitas de tinta devem usar no máximo três cores básicas para cada mistura,
   para facilitar o trabalho de quem está começando.
2. Toda técnica sugerida deve vir acompanhada da explicação do erro mais comum
   que o iniciante comete nela.
3. Os nomes comerciais das tintas citadas devem ter alternativas de marcas
   diferentes, caso a principal não seja encontrada.
4. As instruções de diluição devem ser informadas em proporções simples, como
   gotas.

## O que não fazer
1. Não sugerir misturas complexas com mais de quatro cores para usuários
   iniciantes ou com pouca experiência.
2. Não recomendar técnicas avançadas como o blending avançado, que é a técnica de
   mesclar duas cores úmidas na peça para criar transições suaves, sem antes
   garantir que a base do usuário seja sólida.
3. Não avançar para a próxima etapa sem antes confirmar se o usuário entendeu a
   proporção da mistura de tinta atual.

## Forma de responder
1. Sempre em português do Brasil.
2. Listas numeradas curtas para organizar passos e receitas.
3. Explicar qualquer termo técnico na primeira vez que ele aparecer no texto.
4. Ser direto e prático, evitando termos vagos.
5. Perguntar se o usuário entendeu a instrução antes de avançar para o próximo
   passo.
6. Montar menus detalhados como guia, conforme a preferência do usuário.
```

## 3. Regras de ouro do programa

1. **O programa diagnostica sozinho.** Ele não gera texto para ser colado em outro
   lugar. A primeira versão fazia isso e foi rejeitada.
2. **Todo problema aparece no mesmo formato de cartão:**
   1. **O problema** — o que está errado. Pode ser omitido nos cartões verdes.
   2. **Por que acontece** — a causa explicada.
   3. **A solução** — passos numerados.
   4. **Erro mais comum** — onde quem tenta resolver costuma errar.
3. **Três gravidades**, sempre nesta ordem no laudo:
   1. `g` — **Impede a pintura**. Faixa vermelha.
   2. `a` — **Pede atenção**. Faixa ocre.
   3. `b` — **Já está certo** ou melhoria. Faixa verde.
4. **Receitas com no máximo três cores básicas.** Sombra e luz partem sempre da
   mistura base.
5. **Sem envio de fotos.** O programa não analisa imagem, e um campo de foto daria a
   impressão errada de que analisa.
6. **Todo termo técnico é explicado** na primeira vez em que aparece, dentro da
   própria página.

## 4. Questionário — oito blocos

Cada bloco é um `fieldset` com numeração `01` a `08`. Opções são cartões clicáveis
(`label` com `input` dentro), com rótulo e, quando indicado, uma linha de explicação.

### 01 · Quem pinta
| Campo | Tipo | Valores → rótulo visível |
|---|---|---|
| `nivel` | escolha única | `amador` → Nunca pintei nada · `pouca` → Pouca experiência, de 1 a 5 peças · `experiente` → Pinto há algum tempo |
| `aerografo` | escolha única | `nao` → Nunca usei · `sim` → Uso bem |

Explicação sob a pergunta do aerógrafo: *Aerógrafo é a ferramenta ligada a um
compressor que pulveriza tinta por ar comprimido.*

### 02 · A peça
| Campo | Tipo | Valores |
|---|---|---|
| `tema` | texto | Dica: *Se for diorama, diga o cenário. Exemplo: ruína de cidade bombardeada, trecho de floresta, base de nave.* |
| `altura` | número, 1 a 200 | centímetros |
| `material` | escolha única | `resina` · `pvc` · `filamento` (Filamento 3D) · `desconhecido` (Não sei) |
| `estado` | escolha única | `crua` · `fabrica` · `repintada` · `descascando` |
| `brilho` | escolha única | `fosca` · `acetinada` · `brilhante` |

Dica do material: *Resina é pesada e fria ao toque. PVC é leve e cede quando você aperta.*

### 03 · Teste da fita
Antes das opções, um quadro de destaque com o protocolo:
1. Cole 3 cm de fita crepe numa área escondida: base ou parte de trás.
2. Pressione com o dedo por 10 segundos.
3. Puxe de uma vez, em ângulo fechado, quase rente à superfície.
4. Olhe a fita contra a luz.

Rodapé do quadro: *Erro mais comum: fazer numa área visível. Se a tinta vier junto,
você criou uma falha no meio da peça.*

| Campo | Tipo | Valores |
|---|---|---|
| `fita` | escolha única | `limpa` · `pontos` (Veio com pontinhos de cor) · `lascas` (Arrancou lascas) · `crua` (Peça crua, não se aplica) · `pendente` (Ainda não fiz) |

### 04 · Ferramentas
| Campo | Tipo | Valores |
|---|---|---|
| `ferramenta` | múltipla | `pincel` · `aerografo` · `spray` |

### 05 · Tintas na bancada
| Campo | Tipo | Valores |
|---|---|---|
| `marcas` | múltipla | `modelismo` (Vallejo, Citadel, Tamiya ou Mr. Hobby) · `artesanato` (Acrílica de artesanato) |
| `cores` | múltipla | `preto` · `branco` · `ocre` · `oxido` · `azul` · `sombra` · `oliva` · `prata` |

Dica das cores: *Marque só o que existe de verdade na bancada. O que faltar entra na
lista de compras do laudo.*

### 06 · Insumos de preparação
| Campo | Tipo | Valores e explicação |
|---|---|---|
| `insumos` | múltipla | `primer` — base que faz a tinta grudar na peça · `alcool` — para desengordurar e remover tinta velha · `lixa` — lixa d'água 600 ou mais; o número é a grana, quanto maior, mais fina · `fosco` — acabamento final que tira o brilho e protege · `brilhante` — verniz brilhante ou acetinado · `paleta` — mantém a tinta trabalhável por horas |

### 07 · O que a peça tem
| Campo | Tipo | Valores |
|---|---|---|
| `elementos` | múltipla | `terra` · `areia` · `pedra` · `concreto` · `tijolo` · `madeira` · `vegetacao` · `seca` · `metal` · `ferrugem` · `agua` · `pele` · `tecido` |
| `acabamento` | escolha única | `fiel` (Fiel à arte oficial) · `realista` (Realista, com desgaste) · `estilizado` (Estilizado) |

### 08 · Sua maior preocupação
| Campo | Tipo | Observação |
|---|---|---|
| `preocupacao` | texto longo | O laudo traz resposta específica quando reconhece o assunto |

### Progresso
Barra fixa no topo contando **13 perguntas**: todos os campos, exceto `cores` e
`preocupacao`. Mostra "N de 13".

## 5. Motor de diagnóstico

Cada regra testa as respostas e, se verdadeira, gera um cartão. No fim, ordene
`g` → `a` → `b`.

### R1 · Aderência — use `se … senão se …`, só um cartão sai deste grupo

**R1a** · se `fita = lascas` **ou** `estado = descascando` · `g` · Impede a pintura
- **Título:** A tinta antiga não vai segurar a nova
- **Problema:** A camada que está na peça já perdeu aderência. Qualquer tinta aplicada por cima sai junto com ela quando descascar.
- **Causa:** Aderência é a capacidade da tinta de agarrar a superfície. Quando a camada de baixo está solta, ela funciona como uma folha de papel entre a peça e a tinta nova.
- **Solução:**
  1. Raspe as áreas soltas com palito de madeira ou estilete sem fio, só onde a tinta já cede.
  2. Para remover tudo, submerja a peça em álcool isopropílico por 2 a 4 horas num pote fechado.
  3. Escove com escova de dentes macia em água corrente.
  4. Deixe secar 24 horas antes de qualquer outra etapa.
- **Erro mais comum:** Usar escova dura ou palha de aço. Você tira a tinta e leva junto o relevo fino — em diorama, a textura do terreno some primeiro.

**R1b** · senão, se `fita = pontos` · `g` · Impede a pintura
- **Título:** Aderência parcial: pintar por cima é risco alto
- **Problema:** A fita trouxe pontinhos de cor. A tinta antiga está presa em parte, e solta em parte.
- **Causa:** Aderência irregular quase sempre vem de uma superfície que não foi desengordurada antes da pintura original. As áreas que soltam são justamente as que tinham resíduo.
- **Solução:**
  1. Lixe toda a superfície com a lixa 600 a úmido, até o brilho sumir por completo.
  2. Lave com água morna e 3 gotas de detergente neutro, esfregando com escova macia por 2 minutos.
  3. Enxágue e seque por 24 horas.
  4. Aplique primer obrigatoriamente: ele sela o que ficou e cria uma base uniforme.
- **Erro mais comum:** Achar que o primer sozinho resolve. Sem lixar antes, ele adere à tinta solta e os dois saem juntos depois.

**R1c** · senão, se `fita = pendente` · `a` · Falta informação
- **Título:** Faça o teste da fita antes de começar
- **Problema:** Sem o teste, não há como saber se a tinta atual aguenta receber outra camada.
- **Causa:** O teste é o único jeito barato de medir aderência fora do laboratório.
- **Solução:** 1. Cole 3 cm de fita crepe numa área escondida. 2. Pressione por 10 segundos. 3. Puxe de uma vez, quase rente à superfície. 4. Volte aqui e marque o resultado.
- **Erro mais comum:** Fazer o teste numa área visível da peça.

**R1d** · senão, se `fita = limpa` · `b` · Liberado
- **Título:** A tinta atual está firme
- **Causa:** A fita saiu limpa, o que indica que a camada existente tem aderência boa. Dá para trabalhar por cima dela.
- **Solução:** 1. Lave com água morna e detergente neutro para tirar gordura de manuseio. 2. Matize a superfície com a lixa 600 a úmido, de leve, só para tirar o brilho. 3. Uma mão fina de primer ainda é recomendada, para uniformizar a cor de fundo.
- **Erro mais comum:** Pular a lavagem porque a peça parece limpa. Gordura de dedo é invisível e é suficiente para criar uma falha.

### R2 · se `material = resina` **e** `estado ≠ crua` · `g` · Causa provável
- **Título:** Resina com pintura soltando: quase sempre é desmoldante
- **Problema:** Peças de resina que descascam costumam ter sido pintadas sem a lavagem inicial.
- **Causa:** Desmoldante é o produto passado no molde de silicone para a peça soltar. Ele fica impregnado na superfície, é invisível e nenhuma tinta gruda em cima dele.
- **Solução:** 1. Água morna, nunca quente — água quente deforma resina. 2. 3 gotas de detergente neutro numa bacia pequena. 3. Esfregue com escova macia por 2 minutos, insistindo nas fendas. 4. Enxágue bem e seque por 24 horas: a resina absorve um pouco de líquido.
- **Erro mais comum:** Secar com secador para acelerar. O calor empena peças finas e sela a umidade por dentro.

### R3 · se `material = resina` · `a` · Segurança
- **Título:** Pó de resina não pode ser inalado
- **Problema:** Lixar resina a seco libera partículas finas que ficam no ar do cômodo.
- **Causa:** A poeira de resina curada é irritante para as vias respiratórias e se acumula com a exposição repetida.
- **Solução:** 1. Lixe sempre a úmido, com a lixa mergulhada em água. 2. Use máscara, mesmo lixando molhado. 3. Trabalhe perto de uma janela aberta.
- **Erro mais comum:** Lixar a seco por ser mais rápido. Além do risco, a lixa entope e passa a riscar fundo em vez de alisar.

### R4 · se `material = pvc` · `g` · Cuidado com o material
- **Título:** PVC não pode ficar de molho em álcool
- **Problema:** Mergulhar PVC em álcool isopropílico deixa a superfície pegajosa e amolecida.
- **Causa:** O PVC tem plastificante na composição. O álcool prolongado extrai esse plastificante e a peça nunca mais fica firme.
- **Solução:** 1. Limpe com pano levemente umedecido em álcool, sem molho. 2. Para tinta velha, prefira lixar a úmido com 600. 3. Use primer próprio para plástico flexível, não primer automotivo rígido.
- **Erro mais comum:** Repetir em PVC a receita de molho que funciona em resina.

### R5 · se `material = filamento` · `a` · Material
- **Título:** Impressão em filamento tem linhas de camada
- **Problema:** As estrias horizontais da impressão aparecem embaixo da tinta e denunciam a peça.
- **Causa:** Filamento é depositado em camadas, e cada camada deixa um degrau. Tinta comum não preenche esse degrau.
- **Solução:** 1. Lixe a úmido com 400, depois 600. 2. Use primer de alta espessura, vendido como primer surfacer ou primer de enchimento. 3. Duas ou três mãos de primer, lixando de leve entre elas com 600.
- **Erro mais comum:** Aplicar primer grosso de uma vez só. Ele escorre e some com o detalhe em vez de nivelar.

### R6 · se **não** tem `primer` · `g` · Falta na bancada
- **Título:** Sem primer, o problema vai se repetir
- **Problema:** Primer não está na sua lista de insumos, e é o item que resolve a aderência.
- **Causa:** Primer é a tinta base que cria a ponte entre o material da peça e a cor. Ele tem pigmento que morde a superfície, coisa que tinta comum não faz.
- **Solução:**
  1. Primer automotivo cinza em spray, Colorgin ou Chemicolor: melhor custo-benefício e funciona bem em resina.
  2. Alternativas de modelismo: Vallejo Surface Primer, Mr. Surfacer 1000 ou Citadel Chaos Black.
  3. Agite o frasco por 2 minutos cheios antes de usar.
  4. Aplique a 20 ou 25 cm, em duas ou três mãos leves, com 15 minutos entre elas.
  5. Espere 24 horas antes de pintar por cima.
- **Erro mais comum:** Querer cobrir tudo na primeira passada. A tinta acumula nas reentrâncias e afoga o relevo — em diorama, o relevo é o assunto da peça.

### R7 · se `brilho = brilhante` ou `acetinada` · `a` · Preparação
- **Título:** Superfície com brilho precisa ser matizada
- **Problema:** Você marcou a superfície como *brilhante* ou *acetinada* (use a palavra marcada), e tinta escorrega em superfície lisa.
- **Causa:** Matizar é tirar o brilho para criar microrriscos onde a tinta se prende. É uma mordida mecânica, não química.
- **Solução:** 1. Passe a lixa 600 a úmido por toda a superfície, sem pressão. 2. Pare quando a peça ficar uniformemente opaca. 3. Lave e seque antes do primer.
- **Erro mais comum:** Lixar só onde vai receber cor. A área não lixada cria uma borda onde o verniz final descola.

### R8 · se **não** tem `lixa` · `a` · Falta na bancada
- **Título:** Sem lixa fina não há como matizar
- **Problema:** Lixa d'água 600 ou mais fina não está na sua lista.
- **Causa:** A grana é o número da lixa: quanto maior, mais fina. Abaixo de 400 a lixa risca fundo demais para miniatura.
- **Solução:** 1. Compre uma folha 600 e uma 1000, de lixa d'água. 2. Corte em pedaços de 4 cm e use sempre molhados.
- **Erro mais comum:** Usar lixa de madeira, que é seca e grossa demais.

### R9 · se **não** tem `alcool` · `a` · Falta na bancada
- **Título:** Falta o álcool isopropílico
- **Problema:** Ele é o desengordurante e o removedor de tinta velha mais acessível.
- **Causa:** O álcool dissolve gordura sem atacar resina, e evapora sem deixar resíduo.
- **Solução:** 1. Compre isopropílico a 70% ou 99%, em farmácia ou loja de eletrônicos. 2. Enquanto não tiver, use água morna com detergente neutro para desengordurar.
- **Erro mais comum:** Substituir por álcool comum de limpeza doméstica, que tem perfume e aditivos que deixam filme na peça.

### R10 · se **não** tem `fosco` · `a` · Falta na bancada
- **Título:** Sem verniz fosco a peça fica com brilho de brinquedo
- **Problema:** Verniz fosco não está na sua lista, e ele é o que fecha o trabalho.
- **Causa:** Verniz fosco protege a tinta do manuseio e uniformiza o brilho. Sem ele, cada cor seca com um brilho diferente e a peça parece inacabada.
- **Solução:** 1. Aplique só no fim, com a tinta totalmente seca por 48 horas. 2. Duas mãos leves, a 25 cm. 3. Nunca aplique em dia úmido: o verniz esbranquiça.
- **Erro mais comum:** Aplicar verniz fosco grosso para "garantir". Ele cria véu branco que não sai mais.

### R11 · se **não** tem `paleta` **e** usa `pincel` · `b` · Melhoria
- **Título:** Monte uma paleta úmida improvisada
- **Problema:** Sem paleta úmida, a tinta seca na tampa no meio do trabalho e a mistura muda de tom.
- **Causa:** A paleta úmida mantém umidade constante embaixo da tinta, prolongando o tempo de trabalho de minutos para horas.
- **Solução:** 1. Pote plástico raso com tampa. 2. Papel-toalha dobrado e encharcado no fundo. 3. Papel manteiga por cima do papel-toalha. 4. Misture a tinta sobre o papel manteiga.
- **Erro mais comum:** Encharcar demais. A água atravessa o papel manteiga e dilui a tinta sozinha.

### R12 · se marcou `artesanato` **e não** marcou `modelismo` · `a` · Ajuste de técnica
- **Título:** Acrílica de artesanato exige diluição diferente
- **Problema:** Tinta de artesanato tem pigmento mais grosso e veículo mais pesado que tinta de modelismo.
- **Causa:** Ela foi formulada para tela e madeira, superfícies absorventes. Em resina lisa, ela cobre mal na primeira mão e marca pincelada.
- **Solução:** 1. Conte com 2 ou 3 mãos finas para cobrir, nunca uma grossa. 2. Meça com seringa de farmácia sem agulha, já que o pote não tem conta-gotas. 3. Verniz fosco no fim deixa de ser opcional: essa tinta seca acetinada.
- **Erro mais comum:** Aplicar uma mão grossa para cobrir de vez. Ela racha ao secar e marca a pincelada para sempre.

### R13 · se usa `aerografo` na ferramenta **e** `aerografo = nao` · `a` · Ordem de aprendizado
- **Título:** Primeira peça não é lugar de aprender aerógrafo
- **Problema:** Você marcou aerógrafo, mas indicou que nunca usou.
- **Causa:** O aerógrafo tem três variáveis ao mesmo tempo — pressão, diluição e distância. Errar uma estraga a peça inteira em segundos, diferente do pincel, onde o erro é local.
- **Solução:** 1. Treine em papelão por 20 minutos antes de encostar na peça. 2. Comece com pressão de 15 a 20 libras. 3. Use o aerógrafo só no primer e na camada de base; faça o resto com pincel.
- **Erro mais comum:** Começar com a tinta grossa demais, entupir o bico e aumentar a pressão para compensar. Isso gera respingo e granulado.

### R14 · se `0 < altura < 10` · `a` · Escala
- **Título:** Peça pequena precisa de tinta mais diluída
- **Problema:** Com *N* cm, o detalhe é fino e a tinta cheia engole o relevo.
- **Causa:** A espessura da camada não diminui junto com a peça. O que é fino numa peça de 30 cm é grosso numa de 8 cm.
- **Solução:** 1. Aumente a água em 2 gotas em relação à proporção padrão. 2. Prefira pincel 0 e 00 para detalhe. 3. Mais mãos, mais finas.
- **Erro mais comum:** Carregar o pincel até a base das cerdas. A tinta escorre para as fendas e some com o rosto.

### R15 · se `altura ≥ 25` · `b` · Escala
- **Título:** Peça grande: planeje o volume de tinta
- **Problema:** Com *N* cm, a área a cobrir é grande e a mistura precisa render.
- **Causa:** Refazer a mistura no meio do trabalho quase sempre muda o tom, e a emenda aparece.
- **Solução:** 1. Misture de uma vez o dobro do que acha que precisa, num pote com tampa. 2. Anote a proporção em gotas na etiqueta do pote. 3. Use o aerógrafo na camada de base para ganhar tempo e uniformidade.
- **Erro mais comum:** Misturar "a olho" e confiar na memória. Em peça grande, 1 gota de diferença muda o tom visivelmente.

### R16 · se `acabamento = realista` · `b` · Plano de acabamento
- **Título:** Desgaste realista entra depois das cores, nunca antes
- **Problema:** Envelhecimento aplicado cedo demais some embaixo das camadas seguintes.
- **Causa:** Desgaste é a última camada de informação: ele conta o que aconteceu com o objeto depois de pronto.
- **Solução:** 1. Termine todas as cores e deixe secar 24 horas. 2. Verniz brilhante antes da lavagem: a tinta escorre melhor sobre superfície lisa. 3. Lavagem escura nas fendas: 1 gota de preto para 10 gotas de água. 4. Poeira por último, com esfregado a seco de ocre bem claro nas superfícies horizontais.
- **Erro mais comum:** Lavagem em superfície fosca. A tinta agarra onde cai e deixa mancha, em vez de correr para a fenda.

### R17 · se `acabamento = estilizado` · `b` · Plano de acabamento
- **Título:** Contraste marcado pede sombra e luz planejadas
- **Problema:** Sem decidir a direção da luz antes, a sombra fica incoerente e a peça parece chapada.
- **Causa:** No estilo de miniatura de jogo, a luz é pintada, não observada: ela vem sempre de cima.
- **Solução:** 1. Defina a luz vindo de cima e um pouco à frente. 2. Sombra na mistura base + 2 gotas de preto, só na metade de baixo dos volumes. 3. Luz na mistura base + 2 gotas de branco, só nas quinas superiores.
- **Erro mais comum:** Aplicar luz em toda a superfície voltada para cima. A luz vive nas arestas, não nos planos.

### R18 · Preocupação declarada — texto em minúsculas, busca por expressão regular

**R18a** · se casa `descasc|solt|desgrud|cai a tinta|sai a tinta` · `g` · Sua preocupação
- **Título:** Tinta descascando tem três causas, nesta ordem
- **Problema:** Você relatou que a tinta descasca. Isso nunca é culpa da marca da tinta.
- **Causa:** Descascamento é falha de aderência, e ela se decide antes da primeira gota de cor.
- **Solução:** 1. Primeira causa: superfície não lavada, com desmoldante ou gordura. 2. Segunda causa: ausência de primer. 3. Terceira causa: camada única e grossa, que encolhe ao secar e se descola pelas bordas.
- **Erro mais comum:** Trocar de marca de tinta achando que resolve. As três causas acima estão na preparação, não no pote.

**R18b** · se casa `pincelada|marca de pincel|risco` · `a` · Sua preocupação
- **Título:** Marca de pincel é sinal de tinta grossa
- **Problema:** A pincelada fica registrada quando a tinta seca antes de se acomodar.
- **Causa:** Tinta na consistência certa nivela sozinha nos primeiros segundos. Grossa demais, ela endurece com o sulco da cerda.
- **Solução:** 1. Dilua até a consistência de leite integral. 2. Duas ou três mãos finas. 3. Não volte o pincel numa área que já começou a secar.
- **Erro mais comum:** Tentar corrigir a marca passando mais tinta por cima, ainda úmida. Isso arranca a camada de baixo.

**R18c** · se casa `rosto|pele|olho` · `a` · Sua preocupação
- **Título:** Rosto: o erro está na ordem, não no pulso
- **Problema:** O rosto costuma sair errado porque é pintado como se fosse uma superfície só.
- **Causa:** A pele tem três informações sobrepostas: o tom médio, a sombra das cavidades e a luz dos ossos salientes.
- **Solução:** 1. Base: 6 branco + 3 ocre + 1 vermelho óxido. 2. Sombra: a mesma mistura + 2 gotas de vermelho óxido, só nas órbitas, embaixo do nariz e do lábio. 3. Luz: a mistura base + 3 gotas de branco, no dorso do nariz, testa e maçãs. 4. Olhos por último, com pincel 00 e a mão apoiada na mesa.
- **Erro mais comum:** Começar pelos olhos. Se errar, você precisa refazer a pele inteira em volta.

### R19 · se `fita = crua` **e** tem `primer` **e** `material = resina` · `b` · Liberado
- **Título:** Peça crua com primer em mãos: cenário ideal
- **Causa:** Sem tinta antiga e com primer disponível, a preparação é curta e o risco de descascar é mínimo.
- **Solução:** 1. Lave com água morna e detergente neutro. 2. Seque 24 horas. 3. Matize rapidamente com a 600 a úmido. 4. Primer em duas mãos leves.
- **Erro mais comum:** Pular a lavagem porque a peça é nova. Peça nova é justamente a que tem mais desmoldante.

## 6. Receitas de mistura

Uma linha da tabela para cada elemento marcado no bloco 07. Cada linha mostra uma
amostra de cor, o nome, a mistura base, a sombra e a luz.

| Chave | Elemento | Amostra | Mistura base | Sombra | Luz | Cores usadas |
|---|---|---|---|---|---|---|
| `terra` | Terra batida | `#6b5136` | 6 terra de sombra + 3 ocre + 1 branco | a mistura + 2 gotas de preto, nas fendas | a mistura + 2 gotas de branco, só nas partes altas | sombra, ocre, branco |
| `areia` | Areia seca | `#c9ab72` | 5 ocre + 4 branco + 1 terra de sombra | a mistura + 2 gotas de terra de sombra | a mistura + 3 gotas de branco | ocre, branco, sombra |
| `pedra` | Pedra cinza | `#7d7b74` | 5 branco + 4 preto + 1 ocre | a mistura + 2 gotas de preto | a mistura + 3 gotas de branco nas quinas | branco, preto, ocre |
| `concreto` | Concreto | `#9a958a` | 5 branco + 3 preto + 2 ocre | a mistura + 2 gotas de preto, escorrendo nas juntas | a mistura + 2 gotas de branco | branco, preto, ocre |
| `tijolo` | Tijolo | `#8a4536` | 6 vermelho óxido + 2 preto + 2 ocre | a mistura + 2 gotas de preto na argamassa | a mistura + 2 gotas de ocre | oxido, preto, ocre |
| `madeira` | Madeira | `#6a4a33` | 6 terra de sombra + 2 vermelho óxido + 2 branco | a mistura + 2 gotas de preto nas frestas | a mistura + 2 gotas de branco, seguindo o veio | sombra, oxido, branco |
| `vegetacao` | Vegetação viva | `#5f7434` | 6 verde oliva + 2 ocre + 2 branco | a mistura + 2 gotas de preto na base | a mistura + 3 gotas de ocre nas pontas das folhas | oliva, ocre, branco |
| `seca` | Vegetação seca | `#9c7c3c` | 5 ocre + 4 terra de sombra + 1 branco | a mistura + 2 gotas de terra de sombra | a mistura + 3 gotas de branco | ocre, sombra, branco |
| `metal` | Metal escuro | `#4a4e52` | 7 preto + 3 branco como base; prata só no esfregado a seco | preto puro diluído, nas reentrâncias | prata pura no pincel quase seco, nas arestas | preto, branco, prata |
| `ferrugem` | Ferrugem | `#8a4a22` | 6 vermelho óxido + 3 ocre + 1 preto | a mistura + 2 gotas de preto escorrendo para baixo | a mistura + 3 gotas de ocre em pontos aleatórios | oxido, ocre, preto |
| `agua` | Água ou lama | `#4a4a33` | 5 terra de sombra + 3 verde oliva + 2 preto | a mistura + 2 gotas de preto no fundo | verniz brilhante por cima, só onde a água é rasa | sombra, oliva, preto |
| `pele` | Pele humana | `#c99270` | 6 branco + 3 ocre + 1 vermelho óxido | a mistura + 2 gotas de vermelho óxido nas dobras | a mistura + 3 gotas de branco no nariz, testa e maçãs | branco, ocre, oxido |
| `tecido` | Tecido | `#3a4566` | 5 azul ultramarino + 3 preto + 2 branco | a mistura + 2 gotas de preto nas dobras fundas | a mistura + 3 gotas de branco nas dobras altas | azul, preto, branco |

Cores básicas — chave, nome para exibição e amostra:

| Chave | Nome | Amostra |
|---|---|---|
| `preto` | Preto | `#1a1a1a` |
| `branco` | Branco | `#f4f2ee` |
| `ocre` | Amarelo ocre | `#c08a26` |
| `oxido` | Vermelho óxido | `#8d3b2a` |
| `azul` | Azul ultramarino | `#2a3f8f` |
| `sombra` | Terra de sombra | `#5a4330` |
| `oliva` | Verde oliva | `#5c6b33` |
| `prata` | Prata metálica | `#a8adb3` |

Nota abaixo da tabela: *Uma gota é uma gota de pipeta ou de seringa de farmácia sem
agulha. Misture sempre o dobro do que acha que precisa: refazer no meio do trabalho
muda o tom e a emenda aparece.*

Sem nenhum elemento marcado, mostre: *Nenhum elemento marcado no bloco 07. Volte e
marque o que a peça tem — cada elemento vira uma receita em gotas aqui.*

## 7. Diluição por etapa

"Só artesanato" significa: marcou `artesanato` e não marcou `modelismo`.

| Etapa | Aparece quando | Proporção | Como saber que está certo |
|---|---|---|---|
| Pincel | usa pincel | só artesanato: 10 gotas de tinta + 3 de água · caso contrário: 10 + 2 | Consistência de creme de leite. Deve correr do pincel sem pingar. |
| Aerógrafo | usa aerógrafo | só artesanato: 10 de tinta + 10 de água + 1 de álcool · caso contrário: 10 + 6 | Consistência de leite integral. Pressão de 15 a 20 libras, a 15 cm da peça. |
| Lavagem | usa pincel ou aerógrafo | 1 gota de tinta + 10 gotas de água | Corre sozinha para as fendas. Só sobre superfície com verniz brilhante. |
| Esfregado a seco | usa pincel | Tinta pura, sem água | Descarregue quase toda a tinta num papel antes de encostar na peça. Só o relevo alto pode pegar cor. |
| Spray de lata | usa spray | Já vem pronto | A 25 cm, em duas mãos leves, com 15 minutos entre elas. |

Sem ferramenta marcada: *Marque as ferramentas no bloco 04 para ver as proporções.*

## 8. Ordem de pintura

Linha do tempo numerada. Cada etapa tem título, descrição e, quando houver, uma
espera destacada.

1. **Remover a tinta solta** — só se `fita = lascas` ou `estado = descascando`. Raspagem das áreas soltas e, se quiser tirar tudo, molho em álcool isopropílico. *Espera: 2 a 4 horas.*
2. **Lavar** — Água morna, 3 gotas de detergente neutro, escova macia por 2 minutos.
3. **Lixar a úmido** — Lixa 600 molhada, sem pressão, até a superfície ficar opaca por inteiro.
4. **Secar** — Resina absorve líquido e precisa sair completamente antes do primer. *Espera: 24 horas.*
5. **Primer** — Duas ou três mãos leves a 20 cm, com 15 minutos entre elas. *Espera: 24 horas depois.*
6. **Camada de base** — com aerógrafo: *No aerógrafo, a mistura mais escura de cada área, cobrindo tudo.* Sem aerógrafo: *No pincel, duas mãos finas da mistura base de cada área.*
7. **Cores por elemento** — Do maior para o menor: terreno primeiro, depois estruturas, por último as figuras.
8. **Verniz brilhante** — só se acabamento `realista` ou `estilizado`. Só onde vai receber lavagem. A tinta precisa de superfície lisa para escorrer.
9. **Sombra** — Lavagem de 1 gota de tinta para 10 de água, deixando correr para as fendas.
10. **Luz** — Esfregado a seco com tinta pura e pincel quase seco, só nas arestas altas.
11. **Detalhes finos** — Pincel 0 ou 00, com a mão apoiada na mesa. Olhos e letreiros por último.
12. **Desgaste** — só se acabamento `realista`. Poeira com esfregado a seco de ocre claro nas superfícies horizontais.
13. **Verniz fosco** — Duas mãos leves a 25 cm, nunca em dia úmido. *Espera: 48 horas após a última cor.*

A numeração é contínua: etapas que não se aplicam somem e as demais renumeram.
Frase de abertura da seção: *Da camada de fundo aos detalhes. As esperas não são
sugestão: pular uma delas é a causa mais frequente de trabalho perdido.*

## 9. Lista de compras

Só o que falta, nesta ordem:
1. Sem `primer` → **Primer cinza em spray** — Colorgin ou Chemicolor, de material de construção. Alternativas de modelismo: Vallejo Surface Primer, Mr. Surfacer 1000, Citadel Chaos Black.
2. Sem `lixa` → **Lixa d'água 600 e 1000** — Uma folha de cada resolve várias peças.
3. Sem `alcool` → **Álcool isopropílico 70% ou 99%** — Farmácia ou loja de componentes eletrônicos.
4. Sem `fosco` → **Verniz fosco** — Em spray é mais uniforme. Alternativas: Vallejo Matt Varnish, Tamiya TS-80, verniz fosco automotivo.
5. Para cada cor usada pelas receitas dos elementos marcados que **não** está em `cores` → o nome da cor, com a linha *Usada nas receitas dos elementos que você marcou.*

Se nada faltar: *Nada a comprar. Sua bancada cobre tudo o que este trabalho pede.*

## 10. Estrutura do laudo

O botão **Analisar a peça** monta o laudo abaixo do formulário e rola até ele.
Ordem das seções:

1. Título **Laudo da sua peça** e uma linha com tema, altura e material, pulando o que estiver vazio.
2. **Placar** com três quadros: número de cartões `g` (Impedem a pintura), `a` (Pedem atenção) e `b` (Já estão certos).
3. Se houver perguntas sem resposta, um cartão ocre **O laudo está incompleto**: *Faltaram N blocos. O que está abaixo vale, mas fica mais exato com tudo preenchido.*
4. Os cartões de diagnóstico, ordenados `g` → `a` → `b`.
5. **Receitas de mistura** — abertura: *No máximo três cores básicas por mistura. Sombra e luz partem sempre da mistura base, para o tom não brigar.*
6. **Diluição por etapa**.
7. **Ordem de pintura**.
8. **O que comprar**.

Botão secundário **Limpar tudo**: zera o formulário, esconde o laudo, apaga o salvo
e volta ao topo.

## 11. Design

### Direção
Bancada de pintura: papel de trabalho cinza levemente quente, um único acento em
azul ultramarino, e três cores semânticas separadas do acento — vermelho para
grave, ocre para atenção, verde para certo. Uma coluna, largura máxima de 760 px.

### Tokens — tema claro no `:root`
```
--papel:#f2f0ec   --carta:#ffffff   --tinta:#1c1f26   --tinta-fraca:#5d6472
--linha:#d8d5cf   --pigmento:#1f4fd8   --pigmento-fraco:#e6ecfd
--grave:#b3261e   --grave-fraco:#fbe9e7
--ocre:#8a5d0a    --ocre-fraco:#f9f0da
--verde:#16653f   --verde-fraco:#e3f3ea
```

### Tokens — tema escuro
Aplique em `@media (prefers-color-scheme: dark)` com o seletor
`:root:not([data-theme="light"])`, e repita em `:root[data-theme="dark"]`,
ambos com `color-scheme: dark`:
```
--papel:#14161b   --carta:#1c1f26   --tinta:#e8e6e1   --tinta-fraca:#98a0b0
--linha:#2e333d   --pigmento:#7d9dff   --pigmento-fraco:#1d2740
--grave:#ff8a80   --grave-fraco:#331d1b
--ocre:#e0ad4e    --ocre-fraco:#2c2416
--verde:#4fd39a   --verde-fraco:#132b21
```

### Tipografia, do Google Fonts, sempre com fonte de reserva
1. **Archivo** 600 e 800 — títulos, legendas dos blocos e botões.
2. **Source Sans 3** 400 e 600 — texto corrido.
3. **IBM Plex Mono** 400 e 600 — números, proporções em gotas, faixas de gravidade e contador.

### Componentes
1. **Barra de progresso** fixa no topo, com o contador "N de 13".
2. **Cartão de opção** — borda fina; ao marcar, borda e fundo no tom do acento.
3. **Cartão de achado** — borda esquerda de 4 px na cor da gravidade, faixa em letra monoespaçada maiúscula, quadro final de erro mais comum com fundo claro da mesma cor.
4. **Tabelas** dentro de um contêiner com rolagem horizontal própria, para a página nunca rolar de lado no celular.
5. **Linha do tempo** com círculos numerados em azul e a espera em ocre.

### Obrigatório
1. Funcionar em 400 px de largura, com margem lateral de 16 px e sem rolagem lateral.
2. Foco visível no teclado em todos os controles.
3. Respeitar `prefers-reduced-motion`.

## 12. Restrições técnicas

1. **Um único arquivo HTML**, com CSS e JavaScript dentro. Nenhuma biblioteca.
2. **Salvar as respostas** no `localStorage` com a chave `ficha-pintura-v2`, a cada alteração, e restaurar ao abrir. Envolva toda leitura e escrita em `try/catch`: a página tem que funcionar mesmo sem armazenamento.
3. **Escape de HTML** em todo texto que vier do usuário antes de inserir no laudo.
4. **Sem rede**: o programa inteiro roda sem internet, exceto o carregamento das fontes, que cai na reserva se falhar.

## 13. Teste de aceitação — obrigatório antes de me entregar

Preencha a página com estas respostas, que são as da minha peça real:

| Campo | Valor |
|---|---|
| nivel | experiente |
| aerografo | sim |
| tema | diorama |
| altura | 26 |
| material | resina |
| estado | descascando |
| brilho | fosca |
| fita | pontos |
| ferramenta | pincel, aerografo |
| marcas | artesanato |
| cores | prata |
| insumos | alcool, lixa, fosco, brilhante |
| elementos | terra, pedra, vegetacao |
| acabamento | fiel |
| preocupacao | (em branco) |

**Resultado esperado, exatamente:**
1. Placar: **3** impedem · **2** pedem atenção · **2** já estão certos.
2. Cartões, nesta ordem:
   1. A tinta antiga não vai segurar a nova
   2. Resina com pintura soltando: quase sempre é desmoldante
   3. Sem primer, o problema vai se repetir
   4. Pó de resina não pode ser inalado
   5. Acrílica de artesanato exige diluição diferente
   6. Monte uma paleta úmida improvisada
   7. Peça grande: planeje o volume de tinta
3. Receitas: **3 linhas** — terra batida, pedra cinza, vegetação viva.
4. Ordem de pintura: **11 etapas**.
5. Compras: Primer cinza em spray · Terra de sombra · Amarelo ocre · Branco · Preto · Verde oliva.
6. Nenhum erro de JavaScript no console.
7. Nenhuma rolagem lateral em 420 px de largura.

Se tiver um navegador automatizável disponível, rode esse teste de forma automática.
Se não tiver, percorra a lógica à mão e me diga que não conseguiu rodar no navegador.

## 14. Erros já cometidos — não repita

1. **Gerar texto para colar em outro lugar.** A primeira versão fazia isso e eu recusei. O programa diagnostica sozinho.
2. **Campo de envio de fotos.** Removido, porque o programa não analisa imagem.
3. **Caracteres estranhos nos dados.** Já escaparam lixos de digitação para dentro da tabela de cores. Antes de entregar, confira se o arquivo não tem nenhum caractere fora do alfabeto latino.
4. **Mistura com mais de três cores.** Nunca. Sombra e luz derivam da mistura base.
5. **Técnica avançada para iniciante.** Blending, a técnica de mesclar duas cores úmidas na peça, não entra para quem marcou amador ou pouca experiência.

## 15. Como trabalhar comigo

1. Sempre em português do Brasil.
2. Organize as respostas em menus numerados e detalhados.
3. Explique qualquer termo técnico na primeira vez que ele aparecer.
4. Antes de apagar, sobrescrever ou publicar algo, me pergunte.
5. Quando algo não puder ser feito, diga o que ficou de fora e por quê.
6. Ao terminar, me mostre o resultado do teste de aceitação e pergunte se ficou como eu queria.
