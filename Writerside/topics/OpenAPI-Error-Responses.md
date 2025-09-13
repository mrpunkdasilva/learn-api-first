# Padronizando Respostas de Erro

Imagine que em um grande prédio, cada tipo de emergência (fogo, vazamento de gás, alerta de segurança) tivesse um alarme com um som completamente diferente. Seria confuso e difícil para as pessoas saberem como reagir. É muito mais eficaz ter um único som de alarme que todos reconhecem e que significa "Atenção, há um problema!".

Em uma API, a lógica é a mesma. Em vez de retornar uma estrutura de dados diferente para cada tipo de erro, padronizamos uma única "mensagem de erro". Isso torna a vida dos desenvolvedores que consomem sua API muito mais fácil, pois eles podem criar um manipulador de erros genérico que funciona para qualquer problema que a API reporte.

## Os Pilares da Padronização de Erros

1.  **Previsibilidade e Contrato:** O cliente da API sabe exatamente qual formato esperar quando algo dá errado. Ele pode, por exemplo, sempre contar com um campo `message` para exibir ao usuário.
2.  **Consistência (Princípio DRY):** Você define a estrutura do erro uma única vez (`Don't Repeat Yourself`). Se precisar adicionar um novo campo à mensagem de erro (como um `traceId`), você só precisa alterar em um lugar: o schema reutilizável.
3.  **Experiência do Desenvolvedor (DX):** Uma API com erros padronizados é mais fácil de integrar, depurar e manter, o que melhora drasticamente a experiência de quem a consome.

## Arquitetura de Erros Reutilizáveis na OpenAPI

A estratégia, que seu projeto já aplica muito bem, consiste em centralizar as definições em `components` e referenciá-las onde for necessário.

### Passo 1: O Schema de Erro Canônico

O coração da padronização é um schema de erro robusto. O seu `ErrorMessage` é um ótimo ponto de partida.

```c#
# components/schemas/ErrorMessage
ErrorMessage:
  description: It's an error in a request
  type: object
  # ... properties ...
```

**Exemplo de payload de erro (o que o cliente recebe):**
```json
{
  "status": 404,
  "timestamp": "2025-09-08T10:30:00Z",
  "error": "Not Found",
  "message": "Reminder with ID 99 was not found.",
  "path": "/reminders/99"
}
```

### Passo 2: Respostas Reutilizáveis

Definimos as respostas comuns em `components/responses`, cada uma apontando para o schema `ErrorMessage`.

```c#
# components/responses
components:
  responses:
    NotFound:
      description: Resource Not found
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorMessage'
    # ... outras respostas ...
```

### Passo 3: Referenciar nos Endpoints

Finalmente, aplicamos as respostas aos endpoints usando `$ref`.

```c#
# paths/~1reminders~1{reminderId}/get
get:
  summary: Get a reminder by ID
  responses:
    '200':
      # ... resposta de sucesso ...
    '404':
      $ref: '#/components/responses/NotFound'
```

## Guia Rápido para Códigos de Erro HTTP

Usar o código HTTP correto é parte crucial da padronização. Aqui estão os mais comuns para APIs REST:

| Código | Título | Quando Usar |
| :--- | :--- | :--- |
| `400` | Bad Request | O cliente enviou dados inválidos, como um JSON malformado ou um campo com tipo errado. É o erro de validação mais genérico. |
| `401` | Unauthorized | O cliente não está autenticado. Ele precisa fazer login ou fornecer uma chave de API válida. |
| `403` | Forbidden | O cliente está autenticado, mas não tem permissão para acessar o recurso. (Ex: um usuário comum tentando acessar uma rota de administrador). |
| `404` | Not Found | O recurso solicitado não existe. (Ex: `GET /reminders/99` e o lembrete 99 não foi encontrado). |
| `422` | Unprocessable Entity | O cliente enviou dados com a sintaxe correta, mas que violam regras de negócio. (Ex: tentar criar um lembrete com uma data no passado). |
| `500` | Internal Server Error | Um erro inesperado aconteceu no servidor. O problema não é do cliente. A resposta não deve conter detalhes sensíveis da infraestrutura. |

## Evoluindo o Modelo: Lidando com Múltiplos Erros de Validação

Para um formulário complexo, o usuário pode cometer vários erros de uma vez (ex: e-mail inválido E senha muito curta). Retornar todos os erros de uma vez melhora a experiência. Podemos evoluir nosso `ErrorMessage` para suportar isso:

```c#
# components/schemas/ErrorMessage (versão melhorada)
ErrorMessage:
  type: object
  properties:
    # ... status, timestamp, error, message, path ...
    details: # <-- Novo campo!
      type: array
      items:
        type: object
        properties:
          field:
            type: string
            example: 'email'
          issue:
            type: string
            example: 'must be a well-formed email address'
```

Com essa mudança, uma resposta `400 Bad Request` poderia ser assim:

```json
{
  "status": 400,
  "timestamp": "2025-09-08T11:00:00Z",
  "error": "Validation Error",
  "message": "Input data is invalid. See details.",
  "path": "/users",
  "details": [
    {
      "field": "email",
      "issue": "must be a well-formed email address"
    },
    {
      "field": "password",
      "issue": "size must be between 8 and 50"
    }
  ]
}
```

## Conclusão

Padronizar respostas de erro é um investimento na qualidade e na manutenibilidade da sua API. Ao definir um schema canônico e respostas reutilizáveis, você cria um sistema previsível, robusto e agradável de se trabalhar, tanto para sua equipe quanto para os desenvolvedores que consumirão seu trabalho.