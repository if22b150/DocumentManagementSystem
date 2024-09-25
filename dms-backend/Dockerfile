FROM maven:3.9.9 AS build
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY ./pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY ./src ./src
RUN mvn package -DskipTests

FROM openjdk:17-jdk-slim
# the JAR file path
WORKDIR /app

# Copy the JAR file from the build stage to the final image
COPY --from=build /app/target/*.jar /app/application.jar

# Set the default command to run the Java application
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/app/application.jar"]