#!/bin/bash

# Build the Spring Boot application
./gradlew build

# Copy the JAR file to the root directory
cp build/libs/multi-blog-service-0.0.1-SNAPSHOT.jar .

# Deploy to Fly.io
fly deploy

# Remove the copied JAR file
rm multi-blog-service-0.0.1-SNAPSHOT.jar

echo "Deployment to Fly.io completed."
echo "To run the application locally, use: java -jar -Dspring.profiles.active=local build/libs/multi-blog-service-0.0.1-SNAPSHOT.jar"
