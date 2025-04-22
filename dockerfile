# Etapa de build com Maven + JDK 17
FROM maven:3.9.6-eclipse-temurin-17 as builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa de execução: apenas o JAR
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar /app/oauth-service.jar

EXPOSE ${OAUTH_INTERNAL_PORT}

ENV KEYCLOAK_INTERNAL_HOST=${KEYCLOAK_INTERNAL_HOST}
ENV KEYCLOAK_INTERNAL_PORT=${KEYCLOAK_INTERNAL_PORT}
ENV KEYCLOAK_REALM=${KEYCLOAK_REALM}
ENV KEYCLOAK_CLIENT_ID=${KEYCLOAK_CLIENT_ID}
ENV KEYCLOAK_CLIENT_SECRET=${KEYCLOAK_CLIENT_SECRET}
ENV KEYCLOAK_GRANT_TYPE=${KEYCLOAK_GRANT_TYPE}

ENTRYPOINT ["java", "-jar", "/app/oauth-service.jar"]
