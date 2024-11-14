# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the WAR file to the container
COPY build/libs/devel-0.0.1-SNAPSHOT.war /app/devel-0.0.1-SNAPSHOT.war

# Expose the port the application runs on
EXPOSE 8244

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app/devel-0.0.1-SNAPSHOT.war"]
