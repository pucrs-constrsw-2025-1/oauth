# OAuth API - Grupo 4

API REST para autenticação, gerenciamento de usuários e gerenciamento/atribuição de roles, integrando com Keycloak.

---

## Arquitetura de Software

- **Spring Boot**: Framework principal da aplicação.
- **Controllers**: Expõem os endpoints REST.
- **Services**: Lógica de negócio e integração com Keycloak via REST.
- **Models**: Representação dos dados trafegados (requests/responses).
- **Tratamento de Erros**: Centralizado, seguindo padrão definido em `rules.md`.
- **Swagger/OpenAPI**: Documentação interativa automática.

---

## Documentação Interativa

Acesse a documentação Swagger UI após subir o backend:

```
http://localhost:8080/swagger-ui.html
```

Nela você pode testar todos os endpoints, ver exemplos de requisição e resposta, e explorar os contratos da API.

---

## Como Executar

1. Instale as dependências:
   ```bash
   ./gradlew build
   ```
2. Rode a aplicação:
   ```bash
   ./gradlew bootRun
   ```
3. Acesse o Swagger UI em [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Instruções Complementares

- Certifique-se de que o Keycloak está rodando e configurado (realm, client, usuários, roles).
- As variáveis de ambiente do Keycloak podem ser ajustadas no arquivo `application.yml`.
- O token de acesso é obrigatório para endpoints protegidos (usuários e roles).
- O padrão de variáveis de path é sempre `userId` e `roleId`.
- Exemplo de login:
  ```http
  POST /login
  Content-Type: application/x-www-form-urlencoded

  client_id=oauth&username=admin@pucrs.br&password=novaSenha123&grant_type=password
  ```

---

## Rotas da API

### Autenticação
- `POST /login` — Autenticação (token)
- `POST /refresh` — Renovação de token
- `GET /health` — Health check

### Usuários
- `POST /users` — Criar usuário
- `GET /users` — Listar todos os usuários
- `GET /users/{userId}` — Buscar usuário por ID
- `PUT /users/{userId}` — Atualizar usuário por ID
- `DELETE /users/{userId}` — Excluir usuário por ID
- `PATCH /users/{userId}/password` — Atualizar senha do usuário

### Roles
- `POST /roles` — Criar role
- `GET /roles` — Listar todos os roles
- `GET /roles/{roleId}` — Buscar role por ID
- `PUT /roles/{roleId}` — Atualizar role por ID (total)
- `PATCH /roles/{roleId}` — Atualizar role por ID (parcial)
- `DELETE /roles/{roleId}` — Exclusão lógica de role

### Atribuição de Roles
- `POST /roles/assign/{userId}/{roleId}` — Atribuir role a usuário
- `DELETE /roles/assign/{userId}/{roleId}` — Remover role de usuário

---

## Tratamento de Erros

A API segue o seguinte padrão de resposta para erros:

```json
{
  "error_code": "OA-XXX",
  "error_description": "Descrição do erro",
  "error_source": "OAuthAPI",
  "error_stack": [
    {
      "errorCode": "OA-XXX",
      "errorDescription": "Descrição do erro",
      "errorSource": "OAuthAPI"
    }
  ]
}
```
- `error_code`: Código do erro (da aplicação ou do Keycloak)
- `error_description`: Descrição legível do erro
- `error_source`: Origem do erro (OAuthAPI, Keycloak, etc)
- `error_stack`: Pilha de erros até o erro final

---

Se precisar de mais detalhes sobre a arquitetura, exemplos de uso ou integração, consulte a documentação Swagger ou entre em contato com o time do Grupo 4.
