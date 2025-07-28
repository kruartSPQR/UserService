FROM gradle:jdk23-corretto-al2023 AS builder

WORKDIR /opt/app

COPY . .

RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

COPY --from=builder /opt/app/build/libs/*.jar userService.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "userService.jar"]
