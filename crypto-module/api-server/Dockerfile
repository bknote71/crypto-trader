FROM openjdk:17-alpine

ARG JARFILE=/build/libs/api-server-0.0.1-SNAPSHOT.jar
COPY ${JARFILE} /api-server.jar

ENTRYPOINT ["java", "-jar", "/api-server.jar", "--spring.profiles.active=prod"]
