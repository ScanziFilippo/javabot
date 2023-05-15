#FROM openjdk:8-jdk-alpine
FROM openjdk:16-alpine3.13
COPY target/app-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8888