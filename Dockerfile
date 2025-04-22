FROM amazoncorretto:21-alpine

WORKDIR /app

RUN apk add --no-cache maven

COPY . .

RUN mvn clean package spring-boot:repackage

FROM registry.access.redhat.com/ubi8

RUN dnf install -y java-21-openjdk-headless && dnf clean all

WORKDIR /app

COPY --from=0 /app/target/oauth-0.0.1-SNAPSHOT.jar target/oauth-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","target/oauth-0.0.1-SNAPSHOT.jar"]
