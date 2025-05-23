# ---------- build stage -------------------------------------------------
    FROM maven:3.8.5-openjdk-17 AS build
    WORKDIR /app
    COPY . .
    RUN mvn -q clean package -DskipTests
    
    # ---------- runtime stage ----------------------------------------------
    FROM openjdk:17-jdk-slim
    
    # instala curl só para o health-check
    RUN apt-get update && \
        apt-get install -y --no-install-recommends curl && \
        rm -rf /var/lib/apt/lists/*
    
    WORKDIR /app
    COPY --from=build /app/target/*.jar app.jar
    
    EXPOSE 8000
    
    # health-check executado **dentro** do container
    HEALTHCHECK --interval=20s --timeout=10s --start-period=90s --retries=5 \
        CMD curl -fs http://localhost:8000/health || exit 1
    
    ENTRYPOINT ["java","-jar","app.jar"]