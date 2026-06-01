# syntax=docker/dockerfile:1
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build

WORKDIR /build

RUN --mount=type=secret,id=github_token \
    GITHUB_TOKEN="$(cat /run/secrets/github_token)" && \
    test -n "$GITHUB_TOKEN" || (echo "ERROR: set GITHUB_TOKEN in .env (GitHub PAT with read:packages)." >&2 && exit 1) && \
    mkdir -p /root/.m2 && cat > /root/.m2/settings.xml <<EOF
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>github</id>
      <username>token</username>
      <password>${GITHUB_TOKEN}</password>
    </server>
  </servers>
</settings>
EOF

COPY pom.xml .
COPY src ./src

RUN mvn -B -q -DskipTests -Pgithub package

FROM docker:27-cli AS docker-cli

FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache libstdc++

COPY --from=docker-cli /usr/local/bin/docker /usr/local/bin/docker
COPY --from=docker-cli /usr/local/libexec/docker/cli-plugins/docker-compose /usr/local/libexec/docker/cli-plugins/docker-compose
RUN ln -sf /usr/local/bin/docker /usr/bin/docker

EXPOSE 8080

COPY --from=build /build/target/back-end-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
