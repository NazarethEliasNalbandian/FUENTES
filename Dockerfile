# Importing JDK and copying required files
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY src src

<<<<<<< HEAD
#FROM openjdk:17-jdk
FROM maven:3.9.6-eclipse-temurin-17
WORKDIR /app
=======
# Copy Maven wrapper
COPY mvnw .
COPY .mvn .mvn

# Set execution permission for the Maven wrapper
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final Docker image using OpenJDK 19
FROM eclipse-temurin:21-jdk
VOLUME /tmp

# Copy the JAR from the build stage
>>>>>>> 190833db250422182de65a79175049e85ce00277
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
