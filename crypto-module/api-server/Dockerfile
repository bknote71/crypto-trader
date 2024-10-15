FROM openjdk:17

ARG JARFILE=/build/libs/api-server-0.0.1-SNAPSHOT.jar
COPY ${JARFILE} /api-server.jar

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/api-server.jar", "--spring.profiles.active=prod", "--server.tomcat.accept-count=5", "--server.port=5005"]
