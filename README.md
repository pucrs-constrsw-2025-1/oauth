# Auth-Keycloak
## Descrição do Projeto
Esta aplicação é um serviço de autenticação e gerenciamento de usuários e perfis de acesso baseado em Keycloak. O sistema oferece uma API RESTful que permite gerenciar usuários e seus perfis de acesso, além de autenticação.
A aplicação foi desenvolvida com:
- Spring Boot
- Spring Security
- Keycloak
- Swagger para documentação da API
- Docker para contêinerização

## Funcionalidades
### Gerenciamento de Perfis (Roles)
- Criação, atualização e exclusão de perfis de acesso
- Listagem de todos os perfis
- Busca de perfil por ID
- Atualização parcial de perfis (PATCH)
- Atribuição de perfis a usuários
- Remoção de perfis de usuários

### Gerenciamento de Usuários
- Criação, atualização e exclusão de usuários
- Autenticação de usuários (login)

## API Endpoints
### Swagger UI
A documentação completa da API está disponível em:
``` 
http://localhost:8080/swagger-ui.html
```
### Endpoints Principais
#### Perfis de Acesso
- `POST /api/admin/roles`: Criar novo perfil
- `GET /api/admin/roles`: Listar todos os perfis
- `GET /api/admin/roles/{id}`: Buscar perfil por ID
- `PUT /api/admin/roles/{id}`: Atualizar perfil
- `PATCH /api/admin/roles/{id}`: Atualização parcial de perfil
- `DELETE /api/admin/roles/{id}`: Excluir perfil
- `POST /api/admin/users/{userId}/roles/{roleId}`: Atribuir perfil a usuário
- `DELETE /api/admin/users/{userId}/roles/{roleId}`: Remover perfil de usuário

#### Usuários
- Endpoints para gerenciamento de usuários (criar, atualizar, excluir)

#### Autenticação
- Endpoint para login e autenticação de usuários

## Execução do Projeto
Para executar o projeto, você precisa ter o Docker e o Docker Compose instalados. Siga as instruções abaixo:
``` bash
# Clone o repositório
git clone [URL_DO_REPOSITÓRIO]
cd auth-keycloak

# Execute com Docker Compose
docker compose up -d
```
Este comando irá iniciar:
1. A aplicação Spring Boot
2. Uma instância do Keycloak configurada
3. Quaisquer outros serviços necessários

## Tecnologias Utilizadas
- Java 17
- Spring Boot
- Spring Security
- Keycloak
- Lombok
- Jakarta EE
- Swagger/OpenAPI para documentação
