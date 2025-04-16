# oauth
# OAuth Service with Keycloak Integration

This project provides authentication and user management services using Spring Boot and Keycloak.

## Architecture

The project follows a clean architecture approach with the following layers:

- **Presentation Layer**: Controllers that handle HTTP requests and responses
- **Application Layer**: Services that implement business logic
- **Infrastructure Layer**: Configuration and integration with external systems (Keycloak, databases)

Key technologies used:
- Spring Boot 3.1.0
- Keycloak 21.1.1
- PostgreSQL 13
- MongoDB 5.0
- Docker Compose

## Prerequisites

- Docker 20.10+
- Docker Compose 1.29+
- JDK 17+

## Getting Started

1. Clone the repository
2. Create `.env` file based on `.env.example`
3. Run the application:

```bash
docker-compose up --build