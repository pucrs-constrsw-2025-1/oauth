# Use official Eclipse Temurin (OpenJDK) image for Java 21
FROM eclipse-temurin:21-jdk-jammy as builder

# Set working directory
WORKDIR /app

# Copy Gradle build files first to leverage Docker cache
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src

# Run Gradle build
RUN ./gradlew build --no-daemon

# Second stage to create a smaller runtime image
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8080

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]