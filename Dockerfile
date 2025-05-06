FROM openjdk:17-jdk-slim

WORKDIR /

COPY ./target/deepdetect-1.0.0.jar /deep-detect.jar

EXPOSE 8080

CMD ["java", "-jar", "/deep-detect.jar"]
