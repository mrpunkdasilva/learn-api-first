# O Básico do OpenAPI

O arquivo `openapi.yaml` que você vê no diretório `swagger` é o ponto de partida para definir sua API usando a Especificação OpenAPI. Vamos analisar o que cada parte significa.

## A Estrutura Principal

O arquivo `openapi.yaml` começa com três campos principais:

- `openapi`: Especifica a versão da Especificação OpenAPI que está sendo usada. No nosso caso, é a `3.0.3`.
- `info`: Contém metadados sobre a API, como título, versão e descrição.
- `paths`: É aqui que os *endpoints* da sua API são definidos.

## Analisando nosso `openapi.yaml`

Vamos dar uma olhada no conteúdo do nosso arquivo `swagger/openapi.yaml`:

```yaml
openapi: 3.0.3

info:
  title: Eliza
  version: 1.0.0
  description: REST API to your assistance

paths:
```

### A Versão do OpenAPI

```yaml
openapi: 3.0.3
```

Esta linha simplesmente declara que estamos usando a versão 3.0.3 da Especificação OpenAPI.

### A Seção `info`

```yaml
info:
  title: Eliza
  version: 1.0.0
  description: REST API to your assistance
```

Esta seção fornece informações essenciais sobre a nossa API:
- **title:** O nome da nossa API é "Eliza".
- **version:** A versão atual é a `1.0.0`.
- **description:** Uma breve descrição do que a API faz.

### A Seção `paths`

```yaml
paths:
```

Esta seção está atualmente vazia, mas é o coração da sua definição de API. É aqui que você definirá todos os seus *endpoints*, como `/users` ou `/products/{id}`, e os métodos HTTP que eles suportam (`GET`, `POST`, `PUT`, `DELETE`, etc.).

Nos próximos tópicos, vamos preencher esta seção para construir nossa API.
