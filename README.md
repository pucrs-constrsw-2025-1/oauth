# OAuth API com Keycloak

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Keycloak](https://img.shields.io/badge/Keycloak-5F6BED?style=for-the-badge&logo=Keycloak&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

API de autenticação OAuth2 utilizando Spring Boot e Keycloak como provedor de identidade, containerizada com Docker.

## 📋 Pré-requisitos

- Docker 20.10+
- Docker Compose 2.0+
- Java 21 (para desenvolvimento)
- Maven (para desenvolvimento)

## 🚀 Instalação

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/constrsw-2025-1.git
cd constrsw-2025-1

docker volume create constrsw-keycloak-data
docker volume create constrsw-postgresql-data
docker volume create constrsw-mongodb-data

docker-compose up -d