# Stage 1: Build the application
FROM maven:3.9.9 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY ./pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY ./src ./src
RUN mvn package -DskipTests

# Stage 2: Create runtime image with OpenJDK and Tesseract
FROM openjdk:17-jdk-slim

# Install Tesseract, language files, and Ghostscript
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-eng \
    libtesseract-dev \
    ghostscript \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Set TESSDATA_PREFIX to the default Tesseract path
ENV TESSDATA_PREFIX="/usr/share/tesseract-ocr/4.00/tessdata/"

# Copy the built application JAR
COPY --from=build /app/target/*.jar /app/application.jar

# Set the entry point
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
