# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/matchalah-0.0.1-SNAPSHOT.jar matchalah.jar
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "matchalah.jar"]