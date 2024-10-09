FROM maven:3.8.8-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and any necessary files for dependency resolution
COPY pom.xml .

# Download Maven dependencies (this step is cached unless pom.xml changes)
RUN mvn dependency:go-offline

# Copy the entire source code to the working directory
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Step 2: Use an official JDK runtime image for the app to run
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port that Spring Boot uses (usually 8080)
EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
