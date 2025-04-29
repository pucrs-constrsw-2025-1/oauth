# OAuth API

API REST para autenticação e gerenciamento de usuários usando Keycloak.

## Visão Geral

Este serviço implementa uma API REST que consome a API REST do Keycloak para fornecer funcionalidades de autenticação e gerenciamento de usuários e roles. A API é desenvolvida usando Java com Spring Boot e é implantada em um contêiner Docker.

## Arquitetura

A arquitetura do sistema consiste em:

- **OAuth API**: Serviço Spring Boot que fornece endpoints REST para autenticação e gerenciamento de usuários/roles
- **Keycloak**: Servidor de gerenciamento de identidade e acesso
- **PostgreSQL**: Banco de dados para o Keycloak
- **Docker**: Plataforma para conteinerização

```
┌──────────────────────────────┐
│         Usuário/Cliente      │
│ (Frontend, Postman, etc)     │
└─────────────┬────────────────┘
              │ HTTP (REST)
              ▼
┌──────────────────────────────┐
│      backend/oauth (API)     │
│  - Spring Boot (Java 21)     │
│  - Container Docker          │
│  - Usa Keycloak Admin Client │
│  - Expõe porta 8080 (app)    │
│  - Expõe porta 5005 (debug)  │
└─────────────┬────────────────┘
              │ HTTP (REST, Admin API)
              ▼
┌──────────────────────────────┐
│         Keycloak (IdP)       │
│  - Gerenciamento de usuários │
│  - Autenticação, tokens      │
│  - Container Docker          │
│  - (Configurado via env vars │
│    no docker-compose.yml)    │
└──────────────────────────────┘
```

## Pré-requisitos

- Docker
- Docker Compose
- Postman (opcional, para testes)

## Executando o Projeto

### Preparando o ambiente

1. Clone o repositório:
   ```bash
   git clone <repository-url>
   cd constrsw-2025-1
   ```

2. Crie os volumes externos necessários:
   ```bash
   docker volume create constrsw-keycloak-data
   docker volume create constrsw-postgresql-data
   docker volume create constrsw-mongodb-data
   ```

3. Configure o ambiente:
   ```bash
   # Caso necessário, ajuste as variáveis de ambiente no arquivo .env
   ```

### Iniciando os serviços

Execute o seguinte comando para iniciar todos os serviços:

```bash
docker compose up -d
```

Para visualizar os logs:

```bash
docker compose logs -f
```

### Acessando a API

- OAuth API: http://localhost:8080
- Keycloak: http://localhost:8090
- Swagger UI: http://localhost:8080/swagger-ui.html

## Documentação da API (Swagger)

A documentação completa da API está disponível através do Swagger UI, que pode ser acessado em:

```
http://localhost:8080/swagger-ui.html
```

O Swagger fornece:
- Descrição detalhada de todos os endpoints
- Exemplos de requisições e respostas
- Modelos de dados (schemas)
- Interface para teste interativo dos endpoints

### Visualização do Swagger UI

![Swagger UI Preview](./swagger-ui-preview.png)

### Endpoints Principais

#### Autenticação
- **POST /login**: Autenticação de usuário (form-data)
  - Parâmetros: `username`, `password`
  - Resposta: Token JWT e informações relacionadas

#### Usuários
- **POST /users**: Criação de usuário
- **GET /users**: Listagem de usuários (com filtro opcional por status)
- **GET /users/{id}**: Busca de usuário por ID
- **PUT /users/{id}**: Atualização de usuário
- **PATCH /users/{id}**: Atualização de senha
- **DELETE /users/{id}**: Desativação de usuário

#### Roles
- **POST /roles**: Criação de role
- **GET /roles**: Listagem de roles
- **GET /roles/{id}**: Busca de role por ID
- **PUT /roles/{id}**: Atualização de role
- **PATCH /roles/{id}**: Atualização parcial de role
- **DELETE /roles/{id}**: Exclusão de role

#### Associação de Roles a Usuários
- **GET /users/{userId}/roles**: Listagem de roles de um usuário
- **POST /users/{userId}/roles**: Atribuição de roles a um usuário
- **DELETE /users/{userId}/roles**: Remoção de roles de um usuário

### Modelos de Dados

Os principais modelos de dados documentados são:
- **AuthResponse**: Resposta de autenticação com tokens
- **UserRequest**: Dados para criação/atualização de usuário
- **UserResponse**: Dados de usuário retornados pela API
- **RoleRequest**: Dados para criação/atualização de role
- **RoleResponse**: Dados de role retornados pela API
- **RoleAssignmentRequest**: Lista de IDs de roles para atribuição/remoção
- **ErrorResponse**: Estrutura padronizada para mensagens de erro

## Tratamento de Erros

A API padroniza o tratamento de erros retornando a seguinte estrutura:

```json
{
  "error_code": "OA-000",
  "error_description": "...",
  "error_source": "...",
  "error_stack": [{
    ...
  }]
}
```

## Desenvolvimento

### Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── constrsw/
│   │           └── oauth/
│   │               ├── config/
│   │               │   └── KeycloakConfig.java
│   │               │   └── SwaggerConfig.java
│   │               ├── controller/
│   │               │   └── AuthController.java
│   │               │   └── UserController.java
│   │               │   └── RoleController.java
│   │               │   └── UserRoleController.java
│   │               │   └── HealthController.java
│   │               ├── dto/
│   │               │   └── AuthRequest.java
│   │               │   └── AuthResponse.java
│   │               │   └── UserRequest.java
│   │               │   └── UserResponse.java
│   │               │   └── RoleRequest.java
│   │               │   └── RoleResponse.java
│   │               │   └── RoleAssignmentRequest.java
│   │               ├── exception/
│   │               │   └── CustomExceptionHandler.java
│   │               │   └── ErrorResponse.java
│   │               │   └── GlobalException.java
│   │               ├── service/
│   │               │   └── AuthService.java
│   │               │   └── UserService.java
│   │               │   └── RoleService.java
│   │               └── OAuthApplication.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/
        └── com/
            └── constrsw/
                └── oauth/
                    └── ... (testes)
```

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.2.4
- Keycloak 24.0.1
- Spring Doc OpenAPI UI 2.3.0
- Docker
- Maven

## Configuração

A aplicação é configurada através do arquivo `application.yml`, que contém as seguintes seções principais:

### Configuração do Servidor
```yaml
server:
  port: ${OAUTH_INTERNAL_PORT:8080}
  shutdown: graceful
  servlet:
    context-path: /
```

### Configuração do Keycloak
```yaml
keycloak:
  server:
    url: ${OAUTH_INTERNAL_PROTOCOL:http}://${KEYCLOAK_INTERNAL_HOST:keycloak}:${KEYCLOAK_INTERNAL_API_PORT:8080}
  realm: ${KEYCLOAK_REALM:constrsw}
  client:
    id: ${KEYCLOAK_CLIENT_ID:oauth}
    secret: ${KEYCLOAK_CLIENT_SECRET:wsNXUxaupU9X6jCncsn3rOEy6PDt7oJO}
  admin:
    pool:
      max-size: 10
      min-size: 1
      max-wait: 3000
```

### Configuração do SpringDoc (Swagger)
```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
  api-docs:
    path: /v3/api-docs
```

### Monitoramento
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /manage
```

## Licença

Este projeto é licenciado sob a Licença MIT - consulte o arquivo LICENSE para obter detalhes.