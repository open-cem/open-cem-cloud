FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Aaron Müller, Nils Stutz"

COPY ./build/libs/cem-cloud-backend.jar cem-cloud-backend.jar

ENTRYPOINT ["java", "-jar", "/cem-cloud-backend.jar"]
