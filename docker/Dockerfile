# docker/Dockerfile
# syntax=docker/dockerfile:1
FROM amazoncorretto:21-alpine
LABEL authors="tartur"

ENV APP_HOME=/app
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
ENV SERVER_PORT=8080
WORKDIR ${APP_HOME}

COPY build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]