FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-alpine
RUN apk add --no-cache \
    tesseract-ocr \
    tesseract-ocr-data-nor

WORKDIR /app
COPY --from=build /build/target/image-scanner-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]