FROM maven:3.9.4-amazoncorretto-21 AS build

WORKDIR /oauth

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn package -DskipTests

FROM amazoncorretto:21-alpine-jdk

RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*

WORKDIR /oauth

COPY --from=build /oauth/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
