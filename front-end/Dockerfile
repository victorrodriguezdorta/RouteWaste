# Develop stage
FROM node:alpine as develop-stage
LABEL maintainer="ull@ull.es"
ARG GITHUB_TOKEN
ENV GITHUB_TOKEN $GITHUB_TOKEN
WORKDIR /app
RUN echo @ull-tfg:registry=https://npm.pkg.github.com/ >> ./.npmrc
RUN echo //npm.pkg.github.com/:_authToken=$GITHUB_TOKEN >> ./.npmrc
COPY package*.json ./
RUN yarn install
COPY . .
# Build stage
FROM develop-stage as build-stage
RUN yarn build
# Production stage
FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]