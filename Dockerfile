# Stage 1: Build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper & pom.xml first for dependency caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source and build
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime (smaller)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the fat jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose app port (Render uses 10000)
EXPOSE 8090

# Run the app with prod profile and container-aware JVM options
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-Dspring.profiles.active=prod","-jar","app.jar"]
CMD ["java", "-jar", "app.jar"]