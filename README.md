# Égide — Saída Assistida · API Java (Domain Driven Design Using Java)

**Global Solution 2026/2 · CitizenTech · FIAP ADS**

| Integrante | RM |
|---|---|
| Carlos Eduardo Oliveira Silva | 569103 |
| Gustavo Mendes da Rosa Moreira | 565807 |
| Leonardo de Souza Bernardo | 570951 |
| Luiz Gustavo Albuquerque B. Guedes | 565843 |

- **API publicada (Render):** `https://saida-assistida-api.onrender.com` · [Consultar os setores](https://saida-assistida-api.onrender.com/setores)
- [Assistir à demonstração da API Java no YouTube](https://youtu.be/QHlwHYGYcaY)
- [Abrir a pasta com o vídeo no Google Drive](https://drive.google.com/drive/folders/1CGu0klRNtebCfeWJ16WJp-q2tEa8NuCV?usp=sharing) caso o YouTube esteja indisponível.
- **Front (Vercel):** _(preencher)_

## O que a API resolve

A Defesa Civil municipal recebe o alerta de chuva, mas quem está acamado, usa cadeira de rodas ou depende de um cuidador não consegue sair sozinho. A API organiza essa saída: mantém a lista de moradores que precisam de apoio por setor de risco, abre alertas, inclui as pessoas do setor em atendimentos, designa veículo e destino **compatíveis** com a necessidade de cada uma e registra os marcos até a **chegada confirmada**. O que não foi confirmado continua visível como pendência.

## Domínio (6 entidades, JDBC, Oracle FIAP)

```
Setor 1──N Morador        Alerta 1──N Atendimento        Morador N──N Alerta (via Atendimento)
Setor 1──N Alerta         Veiculo 1──N Atendimento       Destino 1──N Atendimento
```

Tabelas `tb_setor`, `tb_morador`, `tb_alerta`, `tb_veiculo`, `tb_destino`, `tb_atendimento`, com sequences `sq_tb_*` (script em `sql/01_tabelas.sql`).

## Pacotes

| Pacote | Responsabilidade |
|---|---|
| `model` | Entidades do domínio |
| `dao` | Acesso ao Oracle com JDBC puro (`DataSource` no construtor, `PreparedStatement`) |
| `dto` | Entrada (`*RequestDto`, com Bean Validation) e saída (`*ResponseDto`) |
| `service` | Regras de negócio do atendimento (`AtendimentoService`) |
| `controller` | Rotas REST e status HTTP |
| `exception` | `EntidadeNaoEncontradaException` (404), `RegraDeNegocioException` (409), `DadosInvalidosException` (400) |
| `handler` | `@RestControllerAdvice` com JSON de erro padronizado |
| `config` | `ModelMapper` e CORS |

Caminho de uma requisição: `controller` → `DTO` (validado) → `service`/`model` → `dao` → Oracle.

## Regras de negócio (retornam 409)

| Regra | Onde |
|---|---|
| RN01 Um morador só tem um atendimento por alerta | `AtendimentoService.incluir` + `UNIQUE(cd_morador, cd_alerta)` |
| RN02 Veículo e destino compatíveis com a necessidade (maca, cadeira de rodas, acessibilidade) | `AtendimentoService.designar` |
| RN03 Acompanhante ocupa assento e vaga no destino | `AtendimentoService.designar` |
| RN04 Veículo não fica em dois atendimentos ativos | `AtendimentoService.designar` + `VeiculoDao.estaOcupado` |
| RN05 Chegada exige embarque, destino e responsável | `AtendimentoService.registrarMarco` |
| RN07 designação ≤ embarque ≤ chegada | `AtendimentoService.registrarMarco` + `CHECK` no banco |
| RN08 Saída da lista não apaga nada | `PUT /moradores/{id}/saida-da-lista` |
| Remoção com dependentes | FK do Oracle → handler devolve 409 |

## Endpoints

CRUD completo (POST/GET na coleção; GET/PUT/DELETE em `/{id}`) para:
`/setores`, `/moradores`, `/alertas`, `/veiculos`, `/destinos`, `/atendimentos`.

Rotas aninhadas (1:N) e de regra:

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| GET / POST | `/setores/{id}/moradores` | 200 / 201 + Location | 400 · 404 |
| GET / POST | `/alertas/{id}/atendimentos` | 200 / 201 + Location | 400 · 404 · 409 |
| GET | `/alertas/{id}/elegiveis` | 200 | 404 |
| PUT | `/atendimentos/{id}/designacao` | 200 | 400 · 404 · 409 |
| PUT | `/atendimentos/{id}/marcos` | 200 | 400 · 404 · 409 |
| PUT | `/moradores/{id}/saida-da-lista` | 200 | 404 |
| GET / PUT | `/confirmacoes/{codigo}` | 200 | 400 · 404 |
| PUT | `/confirmacoes/{codigo}/saida-da-lista` | 200 | 404 |

Status: 201 + `Location` no cadastro · 200 leitura/atualização · 204 remoção · 400 entrada inválida · 404 inexistente · 405 método não suportado · 409 regra violada ou vínculo · 500 genérico.

Formato de erro:
```json
{ "status": 409, "erro": "Conflict", "mensagem": "Morador precisa de maca e o veiculo Carro DC-03 nao possui maca",
  "instante": "2026-11-15T10:12:03", "caminho": "/atendimentos/7/designacao", "campos": null }
```

Exemplos de JSON de entrada estão em `insomnia/saida-assistida.postman_collection.json` (importar no Insomnia).

## Como rodar

1. Execute `sql/01_tabelas.sql` e `sql/02_carga_teste.sql` no Oracle da FIAP (SQL Developer), **uma vez**.
2. Abra o `pom.xml` no IntelliJ (JDK 21).
3. Em *Run → Edit Configurations → Environment variables* informe:
   `DB_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL;DB_USER=RMxxxxxx;DB_PASSWORD=suasenha`
4. Execute `SaidaAssistidaApplication` e acesse `http://localhost:8080/setores`.

Nenhuma credencial fica no código: `application.properties` lê apenas variáveis de ambiente.

## Deploy no Render

Dockerfile multi-stage (apostila 18). No Render: *New → Web Service → Docker*, repositório do GitHub, e cadastrar as variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD`. Atenção: o plano gratuito tem 750 h/mês por workspace e adormece após 15 min sem uso.

## Uso de IA generativa

Este esqueleto (estrutura de pacotes, SQL, DAOs, DTOs, controllers e regras) foi gerado com apoio do Claude a partir do checkpoint 3 e das apostilas da disciplina, e revisado/adaptado pelo grupo. O registro completo, com prompts e revisão crítica, está no PDF de Software Engineering and Business Model.
