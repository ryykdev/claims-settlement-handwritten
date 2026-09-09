# Replace this Dockerfile with one that builds and runs YOUR service.
#
# Your service should listen on the container port you map in docker-compose.yml
# (8000 is preconfigured as an example) and reach PostgreSQL via the DATABASE_URL
# environment variable. The XML exports are mounted read-only at /data.
#
# Typical starting points:
#
#   Python:       FROM python:3.12-slim
#   Kotlin/Java:  multi-stage build, e.g.
#                 FROM gradle:8-jdk21 AS build / FROM eclipse-temurin:21-jre
#
# The placeholder below only exists so `docker compose up` works out of the box.

# stage 1 - build kotlin app
FROM eclipse-temurin:26-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

# stage 2 - run app
FROM eclipse-temurin:26-jre-alpine
WORKDIR /app
COPY --from=builder app/build/libs/*.jar claims.jar
EXPOSE 8000
CMD ["java", "-jar", "claims.jar"]
