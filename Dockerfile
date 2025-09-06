FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV URL_PROCESADOR=https://tp-dds-2025-procesadorpdi.onrender.com
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
EXPOSE 8080