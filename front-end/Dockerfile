# syntax=docker/dockerfile:1
# Develop stage
FROM node:20-alpine AS develop-stage
LABEL maintainer="ull@ull.es"
WORKDIR /app
RUN --mount=type=secret,id=github_token \
    GITHUB_TOKEN="$(cat /run/secrets/github_token)" && \
    test -n "$GITHUB_TOKEN" || (echo "ERROR: set GITHUB_TOKEN in .env (GitHub PAT with read:packages)." >&2 && exit 1) && \
    printf '%s\n' \
      '@ull-tfg:registry=https://npm.pkg.github.com/' \
      "//npm.pkg.github.com/:_authToken=${GITHUB_TOKEN}" \
      > .npmrc
COPY package*.json ./
RUN npm cache clean --force \
 && npm install --prefer-online --no-audit --no-fund
COPY . .
# Build stage
FROM develop-stage AS build-stage
RUN npm run build
# Production stage
FROM nginx:stable-alpine AS production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
