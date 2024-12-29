FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/deepdetect-0.0.1-SNAPSHOT.jar deep-detect-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
