FROM ubuntu

ARG JARFILE=/build/libs/api-server-0.0.1-SNAPSHOT.jar
COPY ${JARFILE} /api-server.jar

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    rm -rf /var/lib/apt/lists/* \


#ENTRYPOINT ["java", "-jar", "/api-server.jar", "--spring.profiles.active=prod"]