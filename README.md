# Serviço de Autenticação e Autorização OAuth

Este projeto implementa um serviço de autenticação e autorização baseado em OAuth 2.0, integrado com Keycloak para gerenciamento de identidades e acesso.

## Arquitetura

O projeto é composto por dois contêineres Docker principais:

1. **Keycloak** - Servidor de identidade e acesso
2. **OAuth API** - Serviço de autenticação personalizado

### Diagrama de Arquitetura

```
+------------------+     +------------------+
|     Cliente      |     |  Aplicação Web   |
|    (Externo)     |     |     (Externo)    |
+--------+---------+     +---------+--------+
         |                         |
         v                         v
+--------------------------------------------+
|                Rede Externa                |
+---------------------+----------------------+
                      |
          +-----------+-----------+
          |     Docker Network    |
          |       (constrsw)      |
          |                       |
+---------+---------+  +----------+----------+
|                   |  |                     |
|  OAuth API (8081) |  |  Keycloak (8080)    |
|  Porta Ext: 8091  |  |  Porta Ext: 8090    |
|                   |  |                     |
+-------------------+  +---------------------+
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

```
# Docker Compose
COMPOSE_PROJECT_NAME=constrsw-2025-1

# Keycloak
KC_HEALTH_ENABLED=true
KEYCLOAK_REALM=constrsw
KEYCLOAK_INTERNAL_HOST=keycloak
KEYCLOAK_EXTERNAL_HOST=localhost
KEYCLOAK_INTERNAL_API_PORT=8080
KEYCLOAK_EXTERNAL_API_PORT=8081
KEYCLOAK_INTERNAL_CONSOLE_PORT=8080
KEYCLOAK_EXTERNAL_CONSOLE_PORT=8090
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=a12345678
KEYCLOAK_CLIENT_ID=oauth
KEYCLOAK_CLIENT_SECRET=wsNXUxaupU9X6jCncsn3rOEy6PDt7oJO
KEYCLOAK_GRANT_TYPE=password,client_credentials

# OAuth API
OAUTH_INTERNAL_PROTOCOL=http
OAUTH_INTERNAL_HOST=oauth
OAUTH_INTERNAL_API_PORT=8080
OAUTH_EXTERNAL_API_PORT=8091
OAUTH_INTERNAL_DEBUG_PORT=9230
```

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
/
├── backend/
│   ├── oauth/                 # Serviço OAuth
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/constrsw/oauth/
│   │   │   │   │   ├── config/          # Configurações Spring
│   │   │   │   │   ├── controller/      # Controladores REST
│   │   │   │   │   ├── dto/             # Objetos de transferência de dados
│   │   │   │   │   ├── exception/       # Manipulação de exceções
│   │   │   │   │   ├── service/         # Lógica de negócios
│   │   │   │   │   └── OAuthApplication.java
│   │   │   │   └── resources/
│   │   │   │       └── application.yml  # Configuração da aplicação
│   │   │   └── test/                    # Testes automatizados
│   │   ├── Dockerfile                   # Definição do container
│   │   └── pom.xml                      # Dependências Maven
│   └── utils/
│       └── keycloak/                    # Configuração do Keycloak
│           ├── realm-export.json        # Configuração do Realm
│           └── Dockerfile               # Definição do container
├── docker-compose.yml                   # Orquestração dos serviços
└── .env                                 # Variáveis de ambiente
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