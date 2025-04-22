FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
ARG JAR_FILE=target/*.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s \
  CMD curl --fail http://localhost:8080/actuator/health || exit 1
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
