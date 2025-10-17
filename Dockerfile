# https://whichjdk.com/
# https://adoptium.net/zh-CN/temurin/releases?version=8&os=any&arch=any  Temurin 8.0.462+8
# https://adoptium.net/zh-CN/temurin/releases?version=17&os=any&arch=any Temurin 17.0.16+8
# https://adoptium.net/zh-CN/temurin/releases?version=21&os=any&arch=any Temurin 21.0.8+9
# https://releases.ubuntu.com/ noble Ubuntu 24.04.2 LTS
# https://hub.docker.com/layers/library/eclipse-temurin/8u462-b08-jdk-noble/images/sha256-8a5c518422bbf3017338bd7ee3da6ae8a7f7643f266f2367d41a737f6fa4c51a
# https://hub.docker.com/layers/library/eclipse-temurin/17.0.16_8-jdk-noble/images/sha256-bdc4fab96f2ebc23fb7014023e22d522f1e765253876cc60ec896156a10cf01c
# https://hub.docker.com/layers/library/eclipse-temurin/21.0.8_9-jdk-noble/images/sha256-99a8aa663868a7aeb6bef00c4ca28a80a04903203b0b05d92a996ae6dde06530
FROM eclipse-temurin:21.0.8_9-jdk-noble
LABEL authors="d"
ENV PARAMS=""

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS app.jar $PARAMS"]
