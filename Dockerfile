
# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user and group
RUN addgroup -S oauthgroup && adduser -S oauthuser -G oauthgroup

# Create log directory and set permissions
RUN mkdir -p /var/log/oauth && \
    chown oauthuser:oauthgroup /var/log/oauth && \
    chmod 755 /var/log/oauth

# Copy built JAR
COPY --from=build --chown=oauthuser:oauthgroup /app/target/*.jar app.jar

# Environment variables with defaults
ENV OAUTH_INTERNAL_PORT=8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/oauth/heapdump.hprof"
ENV DEBUG_OPTS=""

# Only enable debug if DEBUG_PORT is set
CMD if [ -n "$OAUTH_INTERNAL_DEBUG_PORT" ]; then \
      DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${OAUTH_INTERNAL_DEBUG_PORT}"; \
    fi; \
    exec java ${JAVA_OPTS} ${DEBUG_OPTS} -jar app.jar

# Expose ports
EXPOSE ${OAUTH_INTERNAL_PORT}
EXPOSE ${OAUTH_INTERNAL_DEBUG_PORT}

# Health check with increased timeout for startup
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s \
  CMD wget --quiet --tries=1 --spider http://localhost:${OAUTH_INTERNAL_PORT}/manage/health || exit 1

# Run as non-root user
USER oauthuser