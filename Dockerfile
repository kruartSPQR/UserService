FROM gradle:jdk23-alpine AS builder
ARG GITHUB_ACTOR
ARG GIT_TOKEN
WORKDIR /opt/app
COPY . .
RUN GITHUB_ACTOR=${GITHUB_ACTOR} GIT_TOKEN=${GIT_TOKEN} gradle clean bootJar --no-daemon

FROM eclipse-temurin:23-jre-alpine
WORKDIR /app
COPY --from=builder /opt/app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]