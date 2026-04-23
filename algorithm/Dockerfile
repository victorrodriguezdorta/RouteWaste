# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package assembly:single

# Runtime stage
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /build/target/algorithm-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

RUN apk add --no-cache libgudev

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
