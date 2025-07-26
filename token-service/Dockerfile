# Runtime stage
FROM openjdk:21-jdk-slim

# Add labels for better container management
LABEL maintainer="auth-service-team"
LABEL version="1.0.0"
LABEL description="Token Service for Authentication System"

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Install nslookup (dnsutils)
RUN apt-get update && apt-get install -y dnsutils && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy the jar file
COPY target/token-service-0.0.1-SNAPSHOT.jar token-app.jar

# Command to run the application
CMD ["java", "-jar", "token-app.jar"]
