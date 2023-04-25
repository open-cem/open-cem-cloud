FROM eclipse-temurin:17-jdk-alpine
LABEL authors="Aaron Müller, Nils Stutz"

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]