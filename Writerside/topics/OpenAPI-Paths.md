# Endpoints, Parâmetros e Respostas com paths

A seção `paths` é o mapa de todas as funcionalidades que sua API oferece. Ela define os endpoints (as URLs), os métodos HTTP que cada um aceita e, crucialmente, como enviar e receber dados de cada um deles. Este guia detalha, de forma técnica e acessível, como construir esta seção usando nosso `openapi.yaml` como exemplo prático.

---

## 1. A Estrutura Fundamental: `Path Item Object`

O objeto `paths` contém uma lista de caminhos individuais. Cada caminho, como `/reminders` ou `/reminders/{reminderId}`, é um **`Path Item Object`**. Ele agrupa todas as operações (verbos HTTP) para um único caminho.

### Path Templating (Rotas Dinâmicas)

O uso de chaves, como em `{reminderId}`, é chamado de *Path Templating*. Isso cria um **parâmetro de path**, permitindo que uma única definição de rota lide com um recurso específico (ex: `/reminders/1`, `/reminders/42`, etc.).

### Parâmetros Compartilhados: O Princípio D.R.Y.

Uma prática recomendada, que vemos em nosso `openapi.yaml`, é definir parâmetros que são comuns a todas as operações de um `Path Item` em um nível superior. Isso evita repetição (Don't Repeat Yourself).

No nosso caso, `reminderId` é necessário para `GET`, `PUT` e `DELETE` de um lembrete específico. Por isso, ele é definido uma única vez, logo abaixo do caminho:

```yaml
/reminders/{reminderId}:
  parameters: # <-- Parâmetro compartilhado por todas as operações abaixo
    - name: reminderId
      in: path
      required: true
      schema:
        type: integer
        format: int64
  get:
    # ...
  put:
    # ...
  delete:
    # ...
```

---

## 2. A Operação: O Coração do Endpoint

Dentro de um `Path Item`, cada verbo HTTP (`get`, `post`, `put`, `delete`) é um **`Operation Object`**. Ele descreve uma única ação da API.

Os campos essenciais de uma operação são:

- `summary`: Um resumo curto e legível para humanos. Ex: `Get a reminder by ID`.
- `operationId`: Um identificador único para a operação, usado por geradores de código para nomear funções/métodos. Ex: `getReminderById`.
- `parameters`: Uma lista de parâmetros que se aplicam *apenas* a esta operação.
- `responses`: Um mapa das possíveis respostas que esta operação pode retornar.
- `requestBody`: (Para `POST`, `PUT`, `PATCH`) A descrição do corpo da requisição.

## 3. Parâmetros: A Interface de Entrada

Parâmetros são a forma como um cliente fornece informações para uma operação. Cada parâmetro é definido por seu `name`, sua localização (`in`) e seu `schema` (o tipo de dado).

### Parâmetros de Path (`in: path`)

Eles fazem parte da URL e identificam um recurso específico. São sempre obrigatórios (`required: true`).

- **Nosso Exemplo**: `reminderId` em `/reminders/{reminderId}`.
- **Definição**: 
  ```yaml
  - name: reminderId
    in: path
    required: true
    schema:
      type: integer
      format: int64 # Especifica que é um número de 64 bits
  ```

### Parâmetros de Query (`in: query`)

São anexados à URL após um `?` e são usados para filtrar, ordenar ou paginar resultados. Geralmente são opcionais.

- **Nosso Exemplo**: `title` em `GET /reminders`.
- **Definição**:
  ```yaml
  - name: title
    in: query
    description: 'Title' # Uma boa descrição é fundamental
    required: false # O cliente não é obrigado a fornecer
    schema:
      type: string
      example: 'Weekly Meeting' # Um exemplo ajuda a entender o uso
  ```
- **URL Resultante**: `https://api.example.com/reminders?title=Reunião`

---

## 4. Corpo da Requisição (`requestBody`)

Quando precisamos enviar dados mais complexos que não cabem na URL (como um JSON para criar um novo recurso), usamos o `requestBody`. Nosso `POST /reminders` ainda não tem um, mas se tivesse, seria assim:

```yaml
# Exemplo hipotético para POST /reminders
post:
  summary: Create a single reminder
  operationId: createReminders
  requestBody:
    description: O objeto JSON do lembrete a ser criado.
    required: true
    content:
      application/json:
        schema:
          type: object
          properties:
            title:
              type: string
              example: "Comprar pão"
            due_date:
              type: string
              format: date-time
              example: "2025-09-10T10:00:00Z"
          required:
            - title
  responses:
    '201':
      description: Reminder created with success
```

- `content`: Mapeia o *Media Type* (ex: `application/json`) para o seu `schema`.
- `schema`: Descreve a estrutura dos dados. Acima, definimos um objeto com `title` (obrigatório) e `due_date`.

---

## 5. Respostas (`responses`): O Feedback da API

Uma API robusta informa ao cliente o que aconteceu. O objeto `responses` mapeia códigos de status HTTP para os resultados esperados.

- **`200 OK`**: Sucesso. A requisição foi processada e, geralmente, retorna dados. Usado em todos os nossos `GET` e no `DELETE`.
- **`201 Created`**: Sucesso na criação. Um novo recurso foi criado. Usado no nosso `POST`.
- **`204 No Content`**: Sucesso, mas sem corpo de resposta. O cliente não precisa esperar um JSON de volta. Perfeito para o nosso `PUT`, que atualiza um recurso.
- **`404 Not Found`**: Erro do cliente. O recurso solicitado não existe. Usado em todas as operações que acessam por `reminderId`.

Para tornar as respostas mais ricas, adicionamos um objeto `content` a elas, assim como no `requestBody`, para descrever os dados retornados.

```yaml
# Exemplo de resposta rica para GET /reminders/{reminderId}
responses:
  '200':
    description: 'OK'
    content:
      application/json:
        schema:
          # Aqui descreveríamos o objeto Reminder retornado
          $ref: '#/components/schemas/Reminder' # Idealmente, referenciando um schema reutilizável
  '404':
    description: 'Reminder not found'
```

---

## Juntando Tudo: Nosso `paths` Comentado

```yaml
paths:
  # Caminho para a coleção de lembretes
  /reminders:
    # Operação para buscar todos os lembretes
    get:
      operationId: getReminders
      summary: Get All reminders
      parameters:
        # Parâmetro opcional para filtrar por título
        - name: title
          in: query
          description: 'Title'
          required: false
          schema:
            type: string
            example: 'Weekly Meeting'
      responses:
        # Resposta de sucesso
        '200':
          description: 'OK'
    # Operação para criar um novo lembrete
    post:
      operationId: createReminders
      summary: Create a single reminder
      responses:
        # Resposta de sucesso na criação
        '201':
          description: Reminder created with success

  # Caminho para um lembrete específico, usando um parâmetro de path
  /reminders/{reminderId}:
    # Parâmetro compartilhado por todas as operações neste caminho
    parameters:
      - name: reminderId
        in: path
        required: true
        schema:
          type: integer
          format: int64
    # Operação para buscar um lembrete pelo seu ID
    get:
      operationId: getReminderById
      summary: Get a reminder by ID
      responses:
        '200':
          description: 'OK'
        # Resposta de erro se o ID não for encontrado
        '404':
          description: 'Reminder not found'
    # Operação para atualizar um lembrete existente
    put:
      operationId: updateReminderByID
      summary: Update a reminder by ID
      responses:
        # Sucesso, mas sem conteúdo no corpo da resposta
        '204':
          description: Reminder updated
        '404':
          description: Reminder not found
    # Operação para deletar um lembrete
    delete:
      operationId: deleteReminderByID
      summary: Delete an reminder by id
      responses:
        '200':
          description: Reminder deleted with success
        '404':
          description: Reminder not found
```