# Especificando Campos Obrigatórios: O Contrato da Sua API

Imagine que você está preenchendo um formulário de cadastro online. Alguns campos, como "Nome" e "E-mail", têm um asterisco (*) ao lado, indicando que você *precisa* preenchê-los para continuar. Outros, como "Telefone secundário", são opcionais. Essa é uma forma de **contrato** entre você e o sistema: ele espera certas informações, e não continuará sem elas.

Na definição de uma API, usamos a palavra-chave `required` para estabelecer um contrato semelhante, garantindo que os dados trocados sejam completos e válidos.

## Por Que `required` é Essencial?

Definir campos como obrigatórios vai além de uma simples validação. É um pilar fundamental para a robustez e a clareza da API.

- **Para o Servidor (Backend):** Garante a **integridade dos dados**. O servidor pode confiar que, se a requisição foi aceita, os campos essenciais para a lógica de negócio (como salvar no banco de dados) estarão presentes. Isso evita uma infinidade de verificações de `null` no código.
- **Para o Cliente (Frontend/Consumidor):** Cria um **contrato claro**. O desenvolvedor que consome a API sabe exatamente quais dados precisa enviar, o que acelera o desenvolvimento e previne bugs. Ferramentas de documentação, como o Swagger UI, usam essa informação para indicar visualmente (com o famoso asterisco vermelho) quais campos são mandatórios.
- **Para a Geração de Código:** Ferramentas que geram código a partir da sua especificação OpenAPI usarão a palavra-chave `required` para criar modelos mais seguros, por exemplo, usando tipos não-nuláveis em linguagens como Kotlin, Swift ou TypeScript.

## Como Funciona na Prática

A `required` é uma lista dentro da definição de um schema. Nessa lista, você coloca os nomes de todas as propriedades que devem ser consideradas obrigatórias.

Vamos revisitar o modelo `ReminderInput` do seu projeto:

```c#
# components/schemas/ReminderInput
ReminderInput:
  description: Model of create and update to Reminder
  type: object
  required:
    - title
    - date
  properties:
    title:
      type: string
      example: 'Week Meeting'
    description:
      type: string
      example: 'Week Meeting'
    date:
      type: string
      format: date
      example: '2025-01-06'
```

Com esta definição, veja o que acontece:

**Exemplo de Requisição VÁLIDA:**
```json
{
  "title": "Reunião de Planejamento",
  "description": "Discutir as metas do próximo sprint.",
  "date": "2025-10-20"
}
```

**Exemplo de Requisição INVÁLIDA (falta `title`):**
```json
{
  "description": "Não esquecer!",
  "date": "2025-10-21"
}
```
Neste segundo caso, a API deve rejeitar a requisição com um erro **`400 Bad Request`**, pois o contrato não foi cumprido.

## Indo Além: `readOnly` e `writeOnly`

Um cenário muito comum é ter campos que são obrigatórios na *leitura*, mas não na *escrita*. O exemplo clássico é o `id` de um recurso.

-   Quando você **cria** um lembrete (`POST /reminders`), você não envia o `id`. O servidor o gera.
-   Quando você **lê** um lembrete (`GET /reminders/{id}`), o `id` é uma informação essencial e obrigatória na resposta.

Para modelar isso, combinamos `required` com a palavra-chave `readOnly`.

Veja o seu modelo `ReminderDetail`:

```c#
# components/schemas/ReminderDetail
ReminderDetail:
  description: 'Detailed Reminder'
  type: object
  required:
    - id
    - title
    - description
    - date
  properties:
    id:
      type: integer
      format: int64
      example: 1
      readOnly: true # <-- Ponto chave!
    title:
      type: string
      # ...
```

O que `readOnly: true` significa:
-   **Em respostas (GET):** O campo `id` DEVE estar presente, pois está na lista `required`.
-   **Em requisições (POST, PUT):** Ferramentas e validadores devem ignorar o campo `id` se ele for enviado no corpo da requisição. O cliente não deve enviá-lo.

Existe também o `writeOnly` (ex: para senhas), que funciona de forma inversa: o campo pode ser enviado em requisições, mas não deve aparecer em respostas.

## Diagrama do Contrato

O fluxo de validação pode ser visualizado assim:

```mermaid
graph TD
    A[Cliente envia Requisição POST com JSON] --> B{API Recebe e Valida};
    B -->|JSON tem 'title' e 'date'?| C[Sim: Contrato Cumprido];
    B -->|Falta um campo de 'required'| D[Não: Contrato Quebrado];
    C --> E[Processa a Requisição e Salva no BD];
    D --> F[Retorna Erro 400 Bad Request];
    E --> G[Retorna Resposta 201 Created com o objeto completo (incluindo 'id')];
```

## Conclusão

Usar `required` de forma eficaz, combinado com `readOnly` e `writeOnly` quando necessário, é a marca de uma API bem projetada. Você estabelece contratos claros, melhora a experiência de quem usa sua API e constrói um sistema mais seguro e à prova de erros. É um pequeno esforço na definição que economiza horas de depuração no futuro.