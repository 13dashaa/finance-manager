FROM gradle:8.7-jdk21 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradlew .
COPY gradle/ gradle/

RUN chmod +x ./gradlew
RUN ./gradlew dependencies

COPY src ./src

RUN ./gradlew bootJar -x test


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/finance-manager.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]