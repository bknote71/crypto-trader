FROM openjdk:17

ARG JARFILE=/build/libs/scheduler-0.0.1-SNAPSHOT.jar

COPY ${JARFILE} /scheduler.jar

ENTRYPOINT ["java", "-jar", "/scheduler.jar", "--spring.profiles.active=prod"]