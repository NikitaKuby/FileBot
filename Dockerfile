FROM openjdk:17-jdk-slim-buster
WORKDIR /app
LABEL authors="nikit"
COPY /target/demobot-0.0.1-SNAPSHOT.jar /app/demoBot.jar
ENTRYPOINT ["java","-jar","demoBot.jar"]