# build stage
FROM maven:3.9.11-eclipse-temurin-21 AS build

# Setting the working directory inside the container
WORKDIR /app

# Copying the entire project into the container
COPY . .

# Building the Spring Boot project using Maven
RUN mvn clean package -DskipTests

# Using a smaller OpenJDK runtime image to run the application
FROM eclipse-temurin:21-jdk-alpine

# Setting the working directory for the final image
WORKDIR /app

# Copying the JAR file from the build stage into the runtime image
COPY --from=build /app/target/b2b-0.0.1-SNAPSHOT.jar  /app/app.jar

# Exposing the port my application runs on
EXPOSE 8080

# Command to run my Spring Boot application
CMD ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
