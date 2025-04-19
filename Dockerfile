# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create log directory
RUN mkdir -p /var/log/oauth

# Copy built JAR
COPY --from=build /app/target/*.jar app.jar

# Environment variables with defaults
ENV OAUTH_INTERNAL_PORT=8080
ENV JAVA_OPTS=""
ENV DEBUG_OPTS=""

# Only enable debug if DEBUG_PORT is set
CMD if [ -n "$OAUTH_INTERNAL_DEBUG_PORT" ]; then \
      DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${OAUTH_INTERNAL_DEBUG_PORT}"; \
    fi; \
    exec java ${JAVA_OPTS} ${DEBUG_OPTS} -jar app.jar

# Expose ports
EXPOSE ${OAUTH_INTERNAL_PORT}
EXPOSE ${OAUTH_INTERNAL_DEBUG_PORT}

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --quiet --tries=1 --spider http://localhost:${OAUTH_INTERNAL_PORT}/manage/health || exit 1