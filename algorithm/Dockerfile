# Base image
FROM eclipse-temurin:21-jdk-alpine

# Volume
VOLUME /data

# Copy JAR
COPY target/algorithm-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Install libgudev-dev
RUN apk add libgudev-dev

# Execution
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]