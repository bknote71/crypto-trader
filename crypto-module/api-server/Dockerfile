FROM openjdk:17

ARG JARFILE=/build/libs/api-server-0.0.1-SNAPSHOT.jar
COPY ${JARFILE} /api-server.jar

# for debugging:
# "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
# "--server.port=5005"
ENTRYPOINT ["java", "-jar", "/api-server.jar", "--spring.profiles.active=prod", "--server.tomcat.accept-count=5"]
