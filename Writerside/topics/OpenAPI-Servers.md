# Configurando Servidores (Servers)

Sua API pode ser implantada em diferentes ambientes: um para desenvolvimento local, um para testes (*staging*) e outro para produção. A seção `servers` na especificação OpenAPI permite que você defina as URLs base para cada um desses ambientes, tornando sua documentação e ferramentas mais flexíveis e cientes do contexto.

Isso é crucial para que ferramentas interativas, como o Swagger UI, saibam para onde enviar as requisições de teste.

## Definindo um Único Servidor

No seu projeto, você já tem uma definição de servidor simples e eficaz:

```yaml
servers:
  - description: 'Env Dev'
    url: 'https://eliza.vercel.app'
```

Vamos analisar:

*   `servers`: É uma lista de objetos, onde cada objeto representa um servidor.
*   `description`: Um nome amigável para o ambiente. É útil para que os desenvolvedores possam escolher o servidor correto em ferramentas de UI.
*   `url`: A URL base para todos os endpoints da API naquele ambiente. Os caminhos definidos em `paths` serão anexados a esta URL.

## Múltiplos Ambientes

A verdadeira força da seção `servers` aparece quando você precisa gerenciar múltiplos ambientes. Você pode listar todos os servidores disponíveis, e as ferramentas de documentação geralmente fornecerão um menu para alternar entre eles.

```yaml
# Exemplo com múltiplos servidores
servers:
  - url: https://development.gigantic-server.com/v1
    description: Development server
  - url: https://staging.gigantic-server.com/v1
    description: Staging server
  - url: https://api.gigantic-server.com/v1
    description: Production server
```

Com essa configuração, um desenvolvedor pode facilmente testar a API nos ambientes de desenvolvimento, staging ou produção diretamente da documentação.

## URLs Dinâmicas com Variáveis

E se a sua URL tiver partes que mudam, como um nome de usuário ou uma região? A OpenAPI permite que você defina variáveis diretamente na URL, usando a sintaxe `{nomeDaVariavel}`.

Este é um recurso poderoso para APIs *multi-tenant* ou com implantações regionais.

```yaml
servers:
  - url: 'https://{username}.gigantic-server.com:{port}/{basePath}'
    description: The production API server
    variables:
      username:
        default: demo
        description: 'O subdomínio específico do usuário. Use `demo` para um ambiente de sandbox gratuito.'
      port:
        enum:
          - '8443'
          - '443'
        default: '8443'
      basePath:
        default: v2
```

Analisando as `variables`:

*   Cada chave (`username`, `port`, `basePath`) corresponde a uma variável na `url`.
*   `default`: O valor padrão que será usado.
*   `description`: Explica o propósito da variável.
*   `enum`: (Opcional) Uma lista de valores permitidos. Se fornecido, as ferramentas de UI geralmente renderizam um campo de seleção em vez de um campo de texto livre.

## Conclusão

A seção `servers` é uma parte essencial de uma especificação OpenAPI bem definida. Ela remove a ambiguidade sobre onde a API está hospedada, facilita os testes em diferentes ambientes e permite a criação de URLs dinâmicas e flexíveis com o uso de variáveis. Manter esta seção atualizada garante que sua documentação seja sempre útil e prática para os consumidores da sua API.
