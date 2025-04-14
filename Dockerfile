FROM amazoncorretto:21-alpine

WORKDIR /app

RUN apk add --no-cache maven

COPY . .

RUN mvn clean package spring-boot:repackage

FROM amazoncorretto:21-alpine

WORKDIR /app

COPY --from=0 /app/target/keycloak-wrapper-0.0.1-SNAPSHOT.jar target/keycloak-wrapper-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","target/keycloak-wrapper-0.0.1-SNAPSHOT.jar"]
