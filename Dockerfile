ARG GITHUB_ACTOR
ARG GIT_TOKEN

FROM gradle:jdk23-alpine AS builder


WORKDIR /opt/app

COPY . .

RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

COPY --from=builder /opt/app/build/libs/*.jar userService.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "userService.jar"]

RUN GITHUB_ACTOR=${GITHUB_ACTOR} GIT_TOKEN=${GIT_TOKEN} gradle clean bootJar --no-daemon