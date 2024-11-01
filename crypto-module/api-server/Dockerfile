#FROM openjdk:17
#
#ARG JARFILE=/build/libs/api-server-0.0.1-SNAPSHOT.jar
#COPY ${JARFILE} /api-server.jar
#
## for debugging:
## "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
## "--server.port=5005"
## , "--server.tomcat.accept-count=5"
#ENTRYPOINT ["java", "-jar", "/api-server.jar", "--spring.profiles.active=prod"]

## Ubuntu 기반 이미지 사용
FROM ubuntu:20.04

# 필요한 패키지 설치 및 JDK 17 설치
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    net-tools \
    iproute2 \
    && rm -rf /var/lib/apt/lists/*

RUN apt update
RUN apt install net-tools

# OpenJDK 17 설치
RUN apt-get update && apt-get install -y openjdk-17-jdk

# 이거 적용 안됨
#RUN sysctl -w net.ipv4.tcp_max_syn_backlog=4096

# ARG 선언
ARG JARFILE=/build/libs/api-server-0.0.1-SNAPSHOT.jar

# JAR 파일 복사
COPY ${JARFILE} /api-server.jar

# 디버깅용 설정 주석 처리
# "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
# "--server.port=5005"
# "--server.tomcat.accept-count=5"

# 엔트리포인트 설정
ENTRYPOINT ["java", "-jar", "/api-server.jar", "--spring.profiles.active=prod"]
