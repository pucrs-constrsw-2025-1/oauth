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

## 📦 Como rodar o projeto

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/keycloak-user-api.git
cd keycloak-user-api

# Configure as variáveis de ambiente em application.yml
# ou use .env e Spring Boot Devtools

# Execute a aplicação
./mvnw spring-boot:run
