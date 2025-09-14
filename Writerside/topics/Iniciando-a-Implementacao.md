# Iniciando a Implementação da API

Com nosso contrato OpenAPI definido, o próximo passo é começar a construir a aplicação que irá implementar essa API. Este é o cerne da abordagem "API-first": o contrato orienta o desenvolvimento do código, e não o contrário.

Neste projeto, usaremos o Spring Boot para criar nossa aplicação backend.

## O Ponto de Partida: O Contrato

Tudo começa com o arquivo `openapi.yaml`. Ele é a nossa "fonte da verdade" para tudo relacionado à API: os endpoints, os formatos de dados, as respostas de erro, etc.

```yaml
# swagger/openapi.yaml
openapi: 3.0.3
info:
  title: ElizaCalendar API
  description: |-
    API para gerenciar lembretes no calendário da Eliza.
    Este projeto é parte de um estudo sobre API-first.
  contact:
    email: mrpunkdasilva@gmail.com
  license:
    name: Apache 2.0
    url: http://www.apache.org/licenses/LICENSE-2.0.html
  version: 1.0.0
# ... resto do arquivo
```

## O Projeto Spring Boot: `elizacalendar`

O diretório `elizacalendar` contém um projeto Spring Boot básico, criado a partir do [Spring Initializr](https://start.spring.io/). Ele já possui as dependências essenciais para começarmos, como o `Spring Web`.

A estrutura do projeto segue o padrão Maven:
- `src/main/java`: Onde nosso código-fonte Java ficará.
- `src/main/resources`: Para arquivos de configuração, como o `application.properties`.
- `pom.xml`: O arquivo de configuração do projeto Maven, que define as dependências e como construir o projeto.

## Próximos Passos: Gerando o Código da API

O passo mais importante na abordagem API-first é usar o contrato para gerar o "esqueleto" do nosso código. Faremos isso usando uma ferramenta chamada **OpenAPI Generator**.

Este gerador lerá nosso `openapi.yaml` e criará automaticamente:
- Interfaces Java que representam nossos endpoints da API.
- DTOs (Data Transfer Objects), que são as classes Java correspondentes aos `schemas` que definimos.

Por exemplo, para um endpoint `GET /reminders`, o gerador criaria uma interface parecida com esta:

```c#
// Exemplo de código que será gerado
@Generated(value="org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Tag(name = "reminders", description = "the reminders API")
public interface RemindersApi {

    @Operation(
        operationId = "getReminders",
        summary = "Get all reminders",
        tags = { "reminders" },
        responses = {
            @ApiResponse(responseCode = "200", description = "A list of reminders.", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Reminder.class)))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/reminders",
        produces = { "application/json" }
    )
    ResponseEntity<List<Reminder>> getReminders();
}
```

No próximo tópico, vamos configurar o `pom.xml` para executar esse gerador e criar nossa API.
