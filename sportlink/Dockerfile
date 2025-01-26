# Use the official OpenJDK 21 image as the base image
FROM eclipse-temurin:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle Wrapper files
COPY gradlew .
COPY gradle/wrapper gradle/wrapper

# Copy the build configuration files
COPY build.gradle .
COPY settings.gradle .

# Copy the source code
COPY src src

# Run the Gradle Wrapper to build the application
RUN ./gradlew build -x test

# Expose the application port
EXPOSE 8080

# Command to run the application
CMD ["./gradlew", "bootRun"]