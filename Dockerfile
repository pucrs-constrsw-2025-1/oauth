# ===============================
# Stage 1: Build
# ===============================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY build.gradle settings.gradle* ./
COPY src ./src
COPY . .

RUN ./gradlew build -x test --no-daemon

# ===============================
# Stage 2: Run
# ===============================
FROM eclipse-temurin:17-jre-alpine

# Instala o curl para healthcheck e prepara para debug
RUN apk update && apk --no-cache add curl

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

# Expõe a porta de execução da app e a porta de debug
EXPOSE ${OAUTH_INTERNAL_PORT}
EXPOSE ${OAUTH_INTERNAL_DEBUG_PORT}

ENV OAUTH_INTERNAL_API_PORT=${OAUTH_INTERNAL_PORT}
ENV OAUTH_INTERNAL_DEBUG_PORT=${OAUTH_INTERNAL_DEBUG_PORT:-9230}
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${OAUTH_INTERNAL_DEBUG_PORT}"

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
