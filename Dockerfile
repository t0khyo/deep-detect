# Use an image with Maven and OpenJDK
FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml /app/pom.xml
COPY src /app/src

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /

# Copy the JAR from the build container to the final container
COPY --from=build /app/target/deepdetect-1.0.0.jar /deep-detect.jar

# Expose the port the application will run on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/deep-detect.jar"]
