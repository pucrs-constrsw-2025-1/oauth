# Actuator para OAuth Service

Este serviço implementa um actuator similar ao Spring Boot Actuator usando Node.js/Express + Prometheus + Custom Health Checks.

## Endpoints Disponíveis

### Health Checks
- `GET /actuator/health` - Health check completo com status de componentes
- `GET /actuator/health/liveness` - Liveness probe (aplicação está viva)
- `GET /actuator/health/readiness` - Readiness probe (aplicação está pronta)

### Métricas
- `GET /actuator/metrics` - Métricas do Prometheus
- `GET /actuator/prometheus` - Endpoint alternativo para métricas

### Informações
- `GET /actuator/info` - Informações sobre a aplicação
- `GET /actuator/env` - Informações sobre o ambiente
- `GET /actuator/mappings` - Mapeamento dos endpoints disponíveis

## Componentes Verificados

### Health Check Completo (`/actuator/health`)
Verifica:
- **Keycloak**: Conectividade com o servidor Keycloak
- **System**: Recursos do sistema (CPU, memória, disco)

### Liveness Probe (`/actuator/health/liveness`)
- Verifica se a aplicação está viva
- Sempre retorna `{"status": "UP"}` se a aplicação estiver rodando

### Readiness Probe (`/actuator/health/readiness`)
- Verifica se a aplicação está pronta para receber tráfego
- Inclui verificações de dependências (Keycloak, recursos)

## Métricas do Prometheus

O serviço expõe automaticamente métricas do Prometheus incluindo:
- Requisições HTTP (contadores, duração, status codes)
- Métricas de sistema (CPU, memória, disco)
- Métricas customizadas da aplicação

## Configuração

As configurações do actuator estão integradas ao sistema de configuração existente:
- Health checks automáticos do Keycloak
- Métricas de sistema via `systeminformation`
- Métricas HTTP via `prom-client`

## Docker Health Check

O health check do Docker está configurado para usar `/actuator/health`:
```yaml
healthcheck:
  test: ["CMD-SHELL", "curl -f http://localhost:3000/actuator/health"]
  interval: 10s
  timeout: 5s
  retries: 5
```

## Dependências

- `prom-client`: Cliente Prometheus para Node.js
- `systeminformation`: Métricas de sistema
- `axios`: Cliente HTTP para verificação do Keycloak

## Estrutura de Arquivos

- `src/health/healthChecker.ts`: Lógica de health checks
- `src/routes/actuator.routes.ts`: Rotas do actuator
- `src/app.ts`: Integração do actuator na aplicação

## Verificação do Keycloak

O health check verifica a conectividade com o Keycloak através do endpoint `/health` do próprio Keycloak, garantindo que o serviço de autenticação esteja disponível. 