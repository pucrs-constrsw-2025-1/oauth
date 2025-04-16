# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim as builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml file to the container (Maven build configuration)
COPY ./pom.xml ./ 

# Download dependencies to cache them
RUN mvn dependency:go-offline

# Copy the source code into the container
COPY ./src ./src

# Package the application (This will create a fat JAR file)
RUN mvn clean package -DskipTests

# Create the final stage using a smaller base image for the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the fat JAR file from the builder image into the final image
COPY --from=builder /app/target/*.jar /app/oauth-service.jar

# Expose the port that the app will run on
EXPOSE 8080

# Set environment variables required for OAuth integration with Keycloak
ENV KEYCLOAK_INTERNAL_HOST=${KEYCLOAK_INTERNAL_HOST}
ENV KEYCLOAK_INTERNAL_PORT=${KEYCLOAK_INTERNAL_PORT}
ENV KEYCLOAK_REALM=${KEYCLOAK_REALM}
ENV KEYCLOAK_CLIENT_ID=${KEYCLOAK_CLIENT_ID}
ENV KEYCLOAK_CLIENT_SECRET=${KEYCLOAK_CLIENT_SECRET}
ENV KEYCLOAK_GRANT_TYPE=${KEYCLOAK_GRANT_TYPE}

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/oauth-service.jar"]
