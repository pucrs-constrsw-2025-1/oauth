# 🔐 Keycloak User Management API

Esta aplicação é uma API REST em Spring Boot que atua como uma camada de abstração para o Keycloak. Ela fornece endpoints personalizados para **login**, **cadastro**, **listagem**, **edição**, **desativação** de usuários e **gerenciamento de roles**.

## 🚀 Tecnologias Utilizadas

- Java 17+
- Spring Boot
- Spring Security
- Spring Web
- Keycloak Admin REST API
- Lombok
- Postman (para testes)

---

## ⚙️ Pré-requisitos

- Java 17+
- Maven
- Docker (opcional, para subir o Keycloak localmente)
- Keycloak 24+ configurado com:
  - Realm: `master` ou personalizado
  - Cliente: `backend-client` (confidencial)
  - Role padrão: `user` (ou outras que deseje gerenciar)

---

🔐 Configurações do Keycloak
Configure o Keycloak com:

Client ID: backend-client

Client Secret: gerado pelo Keycloak

Access Type: Confidential

Admin User: admin@pucrs.br / 12345678

As chamadas à API são autenticadas via token JWT obtido pelo endpoint de login.

📡 Endpoints disponíveis
Método	Endpoint	Descrição
POST	/api/auth/login	Autentica usuário e retorna token
POST	/api/users	Cria novo usuário
GET	/api/users	Lista todos os usuários
GET	/api/users/{id}	Busca usuário por ID
PUT	/api/users/{id}	Atualiza dados do usuário
PATCH	/api/users/{id}	Atualiza senha do usuário
DELETE	/api/users/{id}	Desativa (desabilita) usuário
PATCH	/api/users/{id}/roles/add	Adiciona role a um usuário
PATCH	/api/users/{id}/roles/remove	Remove role de um usuário

⚠️ Todos os endpoints, exceto login, requerem token JWT no header Authorization: Bearer {token}.

🧪 Testando com Postman
✅ Como importar a coleção
Abra o Postman.

Vá em File > Import > Raw Text.

Cole o conteúdo do arquivo postman_collection.json (ou peça aqui que geramos o download).

Defina as variáveis:

base_url: ex: http://localhost:8080

access_token: preencha após o login

👤 Exemplo de login
Request

http
Copiar
Editar
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin@pucrs.br",
  "password": "12345678"
}
Response

json
Copiar
Editar
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR..."
}
🧰 Estrutura do Projeto
bash
Copiar
Editar
src/
├── config/                # Beans de configuração e segurança
├── controller/            # Endpoints REST
├── dto/                   # DTOs para comunicação
├── service/               # Lógica de negócio e integração com Keycloak
├── util/                  # Utils (ex: extrair ID de token)
└── KeycloakUserApiApp.java # Main class


```bash
# Clone o repositório
git clone https://github.com/seu-usuario/keycloak-user-api.git
cd keycloak-user-api

# Configure as variáveis de ambiente em application.yml
# ou use .env e Spring Boot Devtools

# Execute a aplicação
./mvnw spring-boot:run

````
## 📦 Collection em json
```bash
{
  "info": {
    "name": "Keycloak User Management API",
    "_postman_id": "e6bd3d04-5823-4e9c-abe5-78d8a6b2eac3",
    "description": "Coleção de testes para autenticação e gerenciamento de usuários via Keycloak",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080"
    },
    {
      "key": "access_token",
      "value": ""
    }
  ],
  "item": [
    {
      "name": "Login",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"admin@pucrs.br\",\n  \"password\": \"12345678\"\n}"
        },
        "url": {
          "raw": "{{base_url}}/api/auth/login",
          "host": ["{{base_url}}"],
          "path": ["api", "auth", "login"]
        }
      },
      "response": []
    },
    {
      "name": "User Management",
      "item": [
        {
          "name": "Create User",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"johndoe\",\n  \"email\": \"john@example.com\",\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\",\n  \"password\": \"senha123\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/users",
              "host": ["{{base_url}}"],
              "path": ["api", "users"]
            }
          }
        },
        {
          "name": "Get All Users",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              }
            ],
            "url": {
              "raw": "{{base_url}}/api/users",
              "host": ["{{base_url}}"],
              "path": ["api", "users"]
            }
          }
        },
        {
          "name": "Get User by ID",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              }
            ],
            "url": {
              "raw": "{{base_url}}/api/users/{{user_id}}",
              "host": ["{{base_url}}"],
              "path": ["api", "users", "{{user_id}}"]
            }
          }
        },
        {
          "name": "Update User",
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"firstName\": \"Updated\",\n  \"lastName\": \"User\",\n  \"email\": \"updated@example.com\",\n  \"enabled\": true\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/users/{{user_id}}",
              "host": ["{{base_url}}"],
              "path": ["api", "users", "{{user_id}}"]
            }
          }
        },
        {
          "name": "Update Password",
          "request": {
            "method": "PATCH",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"password\": \"novaSenha123\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/users/{{user_id}}",
              "host": ["{{base_url}}"],
              "path": ["api", "users", "{{user_id}}"]
            }
          }
        },
        {
          "name": "Disable User",
          "request": {
            "method": "DELETE",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              }
            ],
            "url": {
              "raw": "{{base_url}}/api/users/{{user_id}}",
              "host": ["{{base_url}}"],
              "path": ["api", "users", "{{user_id}}"]
            }
          }
        },
        {
          "name": "Add Role to User",
          "request": {
            "method": "PATCH",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"roleName\": \"user\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/users/{{user_id}}/roles/add",
              "host": ["{{base_url}}"],
              "path": ["api", "users", "{{user_id}}", "roles", "add"]
            }
          }
        },
        {
          "name": "Remove Role from User",
          "request": {
            "method": "PATCH",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"roleName\": \"user\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/users/{{user_id}}/roles/remove",
              "host": ["{{base_url}}"],
              "path": ["api", "users", "{{user_id}}", "roles", "remove"]
            }
          }
        }
      ]
    },
    {
      "name": "Role Management",
      "item": [
        {
          "name": "Create Role",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"admin\",\n  \"description\": \"Administrator role\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/roles",
              "host": ["{{base_url}}"],
              "path": ["api", "roles"]
            }
          }
        },
        {
          "name": "Get All Roles",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              }
            ],
            "url": {
              "raw": "{{base_url}}/api/roles",
              "host": ["{{base_url}}"],
              "path": ["api", "roles"]
            }
          }
        },
        {
          "name": "Get Role by Name",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              }
            ],
            "url": {
              "raw": "{{base_url}}/api/roles/{{role_name}}",
              "host": ["{{base_url}}"],
              "path": ["api", "roles", "{{role_name}}"]
            }
          }
        },
        {
          "name": "Update Role",
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"admin\",\n  \"description\": \"Updated description\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/roles/{{role_name}}",
              "host": ["{{base_url}}"],
              "path": ["api", "roles", "{{role_name}}"]
            }
          }
        },
        {
          "name": "Patch Role",
          "request": {
            "method": "PATCH",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"description\": \"Partially updated description\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/api/roles/{{role_name}}",
              "host": ["{{base_url}}"],
              "path": ["api", "roles", "{{role_name}}"]
            }
          }
        },
        {
          "name": "Disable Role",
          "request": {
            "method": "DELETE",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{access_token}}"
              }
            ],
            "url": {
              "raw": "{{base_url}}/api/roles/{{role_name}}",
              "host": ["{{base_url}}"],
              "path": ["api", "roles", "{{role_name}}"]
            }
          }
        }
      ]
    }
  ]
}

````
