FROM openjdk:8-jdk-alpine
LABEL maintainer="anilersan93@gmail.com"
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/conference-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} conference.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/conference.jar"]