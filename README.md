# OAuth Service

Este serviço implementa uma API REST para autenticação OAuth e gerenciamento de usuários/roles usando Keycloak.

## Arquitetura

A aplicação segue os princípios de Arquitetura Limpa e SOLID:

1. **Domain Layer**: Contém entidades de domínio, interfaces de repositório e serviços do domínio.
2. **Application Layer**: Contém os serviços de aplicação, DTOs e casos de uso.
3. **Infrastructure Layer**: Contém implementações concretas de repositórios, configurações e adaptadores.
4. **Interfaces Layer**: Contém controladores REST, mapeadores e manipulação de requisições.

### Diagrama da Arquitetura
```
┌────────────────────────────────────────────────────────┐
│                     Interfaces Layer                   │
│ ┌─────────────────┐ ┌─────────────────┐ ┌────────────┐ │
│ │ AuthController  │ │ UserController  │ │ RoleContr. │ │
│ └────────┬────────┘ └────────┬────────┘ └─────┬──────┘ │
└──────────┼─────────────────┬─┼────────────────┼────────┘
           │                 │ │                │
┌──────────▼─────────────────▼─▼────────────────▼────────┐
│                     Application Layer                  │
│ ┌─────────────────┐ ┌─────────────────┐ ┌────────────┐ │
│ │  AuthService    │ │  UserService    │ │ RoleService│ │
│ └────────┬────────┘ └────────┬────────┘ └─────┬──────┘ │
└──────────┼─────────────────┬─┼────────────────┼────────┘
           │                 │ │                │
┌──────────▼─────────────────▼─▼────────────────▼────────┐
│                       Domain Layer                     │
│ ┌─────────────────┐ ┌─────────────────┐ ┌────────────┐ │
│ │AuthenticationSvc│ │UserManagementSvc│ │RoleManag.Sv│ │
│ └────────┬────────┘ └────────┬────────┘ └─────┬──────┘ │
└──────────┼─────────────────┬─┼────────────────┼────────┘
           │                 │ │                │
┌──────────▼─────────────────▼─▼────────────────▼────────┐
│                   Infrastructure Layer                 │
│ ┌─────────────────┐ ┌─────────────────┐ ┌────────────┐ │
│ │KeycloakAuthAdapt│ │KeycloakUserAdapt│ │KeycloakRole│ │
│ └────────┬────────┘ └────────┬────────┘ └─────┬──────┘ │
└──────────┼─────────────────┬─┼────────────────┼────────┘
           │                 │ │                │
┌──────────▼─────────────────▼─▼────────────────▼────────┐
│                       Keycloak API                     │
└────────────────────────────────────────────────────────┘
```

## Tecnologias Utilizadas

- **Java 21**: Linguagem de programação
- **Spring Boot 3.1.5**: Framework para desenvolvimento da API
- **Spring Security**: Para implementação dos mecanismos de segurança
- **Spring OAuth2 Resource Server**: Para validação e processamento de tokens JWT
- **Spring OAuth2 Client**: Para comunicação com o Keycloak
- **Keycloak 24.0.5**: Servidor de identidade e controle de acesso
- **Docker/Docker Compose**: Para containerização e orquestração
- **Maven**: Gerenciamento de dependências e build
- **Lombok**: Para redução de código boilerplate
- **SpringDoc OpenAPI**: Para documentação da API (Swagger)

## Configuração e Execução

### Pré-requisitos

- Docker e Docker Compose instalados
- Java 21 (para desenvolvimento local)
- Maven (para desenvolvimento local)

### Criação dos Volumes Docker

Execute os seguintes comandos para criar os volumes persistentes:

```bash
docker volume create constrsw-keycloak-data
docker volume create constrsw-postgresql-data
docker volume create constrsw-mongodb-data
```

### Variáveis de Ambiente

As configurações são definidas através de variáveis de ambiente no arquivo `.env`:

## Endpoints da API

### Autenticação

- `POST /login`: Autenticação de usuário (acesso público)

### Usuários

- `POST /users`: Criar um novo usuário
- `GET /users`: Listar todos os usuários (com filtro opcional de `enabled=[true|false]`)
- `GET /users/{id}`: Obter um usuário específico
- `PUT /users/{id}`: Atualizar um usuário
- `PATCH /users/{id}`: Atualizar a senha de um usuário
- `DELETE /users/{id}`: Desabilitar um usuário (exclusão lógica)
- `POST /users/{userId}/roles/{roleId}`: Adicionar um role a um usuário
- `DELETE /users/{userId}/roles/{roleId}`: Remover um role de um usuário

### Roles

- `POST /roles`: Criar um novo role
- `GET /roles`: Listar todos os roles
- `GET /roles/{id}`: Obter um role específico
- `PUT /roles/{id}`: Atualizar um role
- `PATCH /roles/{id}`: Atualizar parcialmente um role
- `DELETE /roles/{id}`: Excluir um role

### Saúde/Monitoramento

- `GET /health`: Status de saúde da API

## Documentação da API

- Swagger UI: `http://localhost:8088/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8088/v3/api-docs`

## Pré-requisitos

- Java 21
- Docker e Docker Compose

## Como executar

1. Certifique-se de que os volumes necessários estejam criados:

```bash
docker volume create constrsw-keycloak-data
docker volume create constrsw-postgresql-data
docker volume create constrsw-mongodb-data

### Iniciar o Sistema

```bash
docker-compose up -d
```

### Verificar Status

```bash
docker-compose ps
```

### Acessar Interfaces

- **Keycloak Admin Console**: http://localhost:8090/admin
- **OAuth API**: http://localhost:8091
- **Swagger UI**: http://localhost:8091/swagger-ui.html

## Endpoints da API

### Autenticação

- **POST /auth/login**: Autenticar usuário e obter token
  ```bash
  curl -X 'POST' 'http://localhost:8091/auth/login?username=admin@pucrs.br&password=a12345678' -H 'accept: */*'
  ```

- **GET /auth/config**: Verificar configuração do Keycloak
  ```bash
  curl -X 'GET' 'http://localhost:8091/auth/config' -H 'accept: */*'
  ```

### Debugging

- **POST /auth/debug/token**: Endpoint para debug de autenticação
  ```bash
  curl -X 'POST' 'http://localhost:8091/auth/debug/token?username=admin@pucrs.br&password=a12345678' -H 'accept: */*'
  ```

### Health Check

- **GET /health**: Verificar a saúde da aplicação
  ```bash
  curl -X 'GET' 'http://localhost:8091/health' -H 'accept: */*'
  ```

## Estrutura do Projeto

```
Estrutura de Pastas
oauth/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── constrsw/
│   │   │           └── oauth/
│   │   │               ├── OAuthApplication.java
│   │   │               │
│   │   │               ├── domain/
│   │   │               │   ├── entity/
│   │   │               │   │   ├── User.java
│   │   │               │   │   └── Role.java
│   │   │               │   ├── exception/
│   │   │               │   │   └── DomainException.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── UserRepository.java
│   │   │               │   │   └── RoleRepository.java
│   │   │               │   └── service/
│   │   │               │       ├── AuthenticationService.java
│   │   │               │       ├── UserManagementService.java
│   │   │               │       └── RoleManagementService.java
│   │   │               │
│   │   │               ├── application/
│   │   │               │   ├── service/
│   │   │               │   │   ├── AuthService.java
│   │   │               │   │   ├── UserService.java
│   │   │               │   │   └── RoleService.java
│   │   │               │   └── dto/
│   │   │               │       ├── auth/
│   │   │               │       │   ├── AuthRequest.java
│   │   │               │       │   └── AuthResponse.java
│   │   │               │       ├── user/
│   │   │               │       │   ├── UserRequest.java
│   │   │               │       │   └── UserResponse.java
│   │   │               │       └── role/
│   │   │               │           ├── RoleRequest.java
│   │   │               │           └── RoleResponse.java
│   │   │               │
│   │   │               ├── infrastructure/
│   │   │               │   ├── config/
│   │   │               │   │   ├── KeycloakConfig.java
│   │   │               │   │   └── SecurityConfig.java
│   │   │               │   ├── exception/
│   │   │               │   │   ├── GlobalExceptionHandler.java
│   │   │               │   │   └── GlobalException.java
│   │   │               │   └── adapter/
│   │   │               │       ├── keycloak/
│   │   │               │       │   ├── KeycloakAuthAdapter.java
│   │   │               │       │   ├── KeycloakUserAdapter.java
│   │   │               │       │   └── KeycloakRoleAdapter.java
│   │   │               │       └── rest/
│   │   │               │           └── HealthCheckAdapter.java
│   │   │               │
│   │   │               └── interfaces/
│   │   │                   ├── rest/
│   │   │                   │   ├── AuthController.java
│   │   │                   │   ├── UserController.java
│   │   │                   │   ├── RoleController.java
│   │   │                   │   └── HealthController.java
│   │   │                   └── mapper/
│   │   │                       ├── UserMapper.java
│   │   │                       └── RoleMapper.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback.xml
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── constrsw/
│                   └── oauth/
│                       ├── domain/
│                       ├── application/
│                       ├── infrastructure/
│                       └── interfaces/
│
├── Dockerfile
├── pom.xml
└── README.md                         
```

## Integração com Keycloak

O serviço OAuth se integra com o Keycloak para:

1. **Autenticação**: Autenticar usuários contra o Keycloak
2. **Autorização**: Verificar permissões e funções (roles)
3. **Gerenciamento de Usuários**: API para gerenciar usuários no Keycloak
4. **Gerenciamento de Roles**: API para gerenciar papéis e permissões

## Fluxo de Autenticação

1. O cliente envia credenciais para o endpoint `/auth/login`
2. O serviço OAuth encaminha as credenciais ao Keycloak
3. Keycloak valida as credenciais e retorna um token JWT
4. O serviço OAuth retorna o token para o cliente
5. O cliente usa o token nas requisições subsequentes

## Desenvolvimento

### Compilar o Projeto

```bash
mvn clean package
```

### Executar Localmente

```bash
mvn spring-boot:run
```

### Construir Imagem Docker

```bash
docker build -t constrsw/oauth .
```

## Troubleshooting

### Logs do OAuth API

```bash
docker logs oauth
```

### Logs do Keycloak

```bash
docker logs keycloak
```

### Reiniciar Serviços

```bash
docker-compose restart oauth
docker-compose restart keycloak
```

## Segurança

- As senhas dos usuários são gerenciadas pelo Keycloak
- Comunicação entre serviços é feita usando client_credentials
- Autenticação de usuários finais usa fluxo password
- Todas as conexões externas podem ser configuradas para usar HTTPS