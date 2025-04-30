Serviço wrapper para autenticação e autorização com o Keycloak utilizando o Spring Boot.

## Instruções de uso

Sempre rode os comandos de compose abaixo no diretório raiz do projeto.

### Rodar localmente com volumes externos

Para rodar o projeto localmente, crie os volumes externos com os seguintes comandos:

```bash
docker volume create constrsw-keycloak-data
docker volume create constrsw-postgresql-data
docker volume create constrsw-mongodb-data
```

Em seguida, execute o comando abaixo:

```bash
docker-compose up
```

### Rodar localmente sem volumes externos

Para rodar o projeto localmente, sem precisar criar os volumes externos, basta rodar o comando abaixo:

```bash
docker-compose -f docker-compose.yml -f docker-compose.local.yml up
```
### Rodar localmente com suporte a debug remoto

Para adicionar suporte a debug para o serviço OAuth, primeiro, rode o comando abaixo:

```bash
docker-compose -f docker-compose.yml -f docker-compose.local.yml -f docker-compose.debug.yml up
```

Depois, utilize o fluxo de debug remoto da sua IDE favorita para conectar ao serviço OAuth no port de debug 9230. Estes são os argumentos para a configuração Remote JVM Debug do IntelliJ IDEA:

```code
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9230
```

## URLs padrões

URL para o Swagger: http://localhost:8080/swagger-ui/index.html
URL para o keycloak admin console: http://localhost:8090/admin/master/console

## Postman

A collection do Postman está localizada no arquivo `oauth.postman_collection.json`.

## Autenticação do administrador

Usuário root: admin@pucrs.br

Senha root: a12345678

## Arquitetura

A arquitetura adotada é uma versão simplificada do padrão MVC prescrito pelo Spring Boot. As responsabilidades são divididas de forma vertical entre cada controller. Serviços agnósticos de tecnologia como o serviço de autorização estão separados dos adapters específicos de tecnologia (controllers e Feign client para as APIs do Keycloak)

## Clients

Foram criados os seguintes clientes (localizados em `backend/clients/oauth`) gerados automaticamente a partir da especificação OpenAPI do serviço OAuth:
- oauth-client-spring: cliente Spring Boot que utiliza clients Feign para acessar as APIs do serviço
- oauth-client-ts: cliente TypeScript Node que utiliza axios para acessar as APIs do serviço

Ambos os clientes possuem exemplos de uso em seus respectivos diretórios.

Para utilizar ambos os clients, não esqueça de configurar as seguintes variáveis de ambiente:
- `OAUTH_HOST`
- `OAUTH_PORT`

Ambos os clients são completamente type-safe.

## Tarefas

- [X] Use client roles instead of realm roles
- [X] Double-check if roles are correctly configured (Santiago)
- [X] Add read and write scopes support and update permissions (Santiago)
- [X] Login endpoint (Santiago)
  - [X] Ensure that the login endpoint returns 400 on a bad request
  - [X] Ensure that the login endpoint returns 401 on an invalid username or password
- [X] Create user endpoint (Lucas)
  - [X] Return the user id using the LOCATION header from the create user keycloak endpoint
  - [X] Ensure that the response body has the correct format
  - [X] Ensure that it returns 400 on a bad request or a bad email
  - [X] Ensure that it returns 401 if the access token is invalid
  - [X] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
  - [X] Ensure that it returns 409 if the user already exists
- [X] Get users endpoint (Lucas)
    - [X] Ensure that it returns a list of JSON objects with the id, username, firstName, lastName, and enabled fields
    - [X] Ensure that it returns 400 on a bad request
    - [X] Ensure that it returns 401 if the access token is invalid
    - [X] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
    - [X] Add query parameters to filter the results
        - [X] username
        - [X] firstName
        - [X] lastName
        - [X] email
        - [X] enabled
- Get user endpoint (Henrique)
    - [X] Ensure that it returns a JSON object with the id, username, firstName, lastName, and enabled fields
    - [X] Ensure that it returns 400 on a bad request
    - [X] Ensure that it returns 401 if the access token is invalid
    - [X] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
    - [X] Ensure that it returns 404 if the user doesn't exist
- Update user endpoint (Henrique)
    - [X] Ensure that it receives a JSON object with the id, username, firstName, lastName, and enabled fields
    - [X] Ensure that it returns 400 on a bad request
    - [X] Ensure that it returns 401 if the access token is invalid
    - [X] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
    - [X] Ensure that it returns 404 if the user doesn't exist
    - [X] Ensure that it updates the user's fields
- Update user password endpoint (Leonardo)
    - [X] Ensure that it receives a JSON object with the newPassword field
    - [X] Ensure that it returns 400 on a bad request
    - [X] Ensure that it returns 401 if the access token is invalid
    - [X] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
    - [X] Ensure that it returns 404 if the user doesn't exist
    - [X] Ensure that it updates the user's password
- Delete user endpoint (Leonardo)
    - [X] Ensure that it returns 400 on a bad request
    - [X] Ensure that it returns 401 if the access token is invalid
    - [X] Ensure that it returns 403 if the access token doesn't have the necessary scopes (not admin)
    - [X] Ensure that it returns 404 if the user doesn't exist
    - [X] Ensure that it deletes the user
- General error handling (Santiago)
    - [ ] Document errors on Swagger UI
    - [ ] Ensure that all endpoints have the following response body on errors:
        - errorCode: não havendo uma instrução em sentido contrário, repassar o response code do Keycloak
        - errorDescription: descrição do erro provida pelo desenvolvedor (grupo)
        - errorSource: origem do erro final (exemplos: OAuthAPI, CoursesAPI, BuildingsAPI etc.)
        - errorStack: pilha de todos os erros até o erro final 
- [X] Autocreate realm if not present
- [X] Token introspection for admin accounts
- [X] Endpoint authorization through Keycloak's OAuth2 endpoint (given a token and a resource, check if the user is authorized to access it) (Santiago)
    - We can probably use the token introspection endpoint to get all authorized resources for a user
    - [X] Return 200 if the user is authorized to access the resource
    - [X] Return 403 if the user is not authorized to access the resource
- [X] Recreate and store our client's secret (client is already created in the realm, we just need to autocreate the secret now)
