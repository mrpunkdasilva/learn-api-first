# Especificando a Segurança da API

A segurança é um dos pilares no desenvolvimento de APIs. A especificação OpenAPI oferece uma forma robusta e clara para definir os esquemas de segurança que sua API utiliza. Isso não apenas documenta como proteger sua API, mas também permite que ferramentas de desenvolvimento e gateways de API configurem a segurança automaticamente.

## Definindo Esquemas de Segurança

O primeiro passo é definir os esquemas de segurança que sua API suporta. Isso é feito na seção `components.securitySchemes` do seu arquivo `openapi.yaml`. Cada esquema recebe um nome único que será usado para referenciá-lo posteriormente.

A especificação OpenAPI suporta vários tipos de esquemas de segurança, como:

*   `apiKey`: Uma chave de API passada no cabeçalho, query string ou cookie.
*   `http`: Esquemas de autenticação HTTP, como Basic ou Bearer.
*   `oauth2`: Fluxos padrão do OAuth2.
*   `openIdConnect`: Autenticação baseada no OpenID Connect.

No nosso projeto, usamos uma `apiKey` para autenticação. Veja como ela é definida:

```yaml
components:
  securitySchemes:
    ApiKey:
      type: apiKey
      name: X-Authorization
      in: header
      description: Authentication token
```

Neste exemplo, definimos um esquema chamado `ApiKey`. Ele é do tipo `apiKey`, é esperado no cabeçalho (`header`) da requisição e o nome do cabeçalho é `X-Authorization`.

## Aplicando Segurança Globalmente

Após definir os esquemas, você pode aplicá-los a toda a sua API. Isso é feito através do objeto `security` no nível raiz da especificação.

Ao definir a segurança globalmente, todos os endpoints da sua API exigirão a autenticação especificada, a menos que seja explicitamente sobrescrito em uma operação específica.

```yaml
security:
  - ApiKey: []
```

A sintaxe `- ApiKey: []` indica que o esquema de segurança `ApiKey` (definido em `components.securitySchemes`) é necessário. O array vazio `[]` é usado para esquemas `apiKey` e `http`. Para `oauth2` ou `openIdConnect`, o array conteria a lista de escopos necessários.

## Segurança em Operações Específicas

É comum ter endpoints públicos e privados em uma mesma API. A especificação OpenAPI permite que você sobrescreva a segurança global em operações individuais.

Para proteger uma operação específica, adicione o mesmo objeto `security` dentro da definição da operação:

```yaml
paths:
  /reminders/{reminderId}:
    delete:
      tags:
        - Reminders
      summary: Delete an reminder by id
      security:
        - ApiKey: []
      responses:
        '200':
          description: Reminder deleted with success
```

Para tornar uma operação pública quando a segurança global está definida, você pode usar um objeto `security` vazio:

```yaml
paths:
  /status:
    get:
      summary: Check API status
      security: [] # This endpoint is public
      responses:
        '200':
          description: API is running
```

Um `security` com um array vazio (`[]`) remove qualquer requisito de segurança global para aquela operação específica, tornando-a efetivamente pública.
