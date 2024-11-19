FROM maven:3.9.9 AS build
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY ./pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY ./src ./src
RUN mvn package -DskipTests

FROM openjdk:17-jdk-slim

# Install Tesseract and Ghostscript
RUN apt-get update && apt-get install -y tesseract-ocr libtesseract-dev ghostscript

# Copy application JAR
COPY --from=build /app/target/*.jar /app/application.jar

# Set the entry point
ENTRYPOINT ["java", "-jar", "/app/application.jar"]