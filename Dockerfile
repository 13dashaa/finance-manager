# --- Этап Сборки (Builder) ---
# Используем образ с нужной версией JDK и Gradle
FROM gradle:8.7-jdk21 AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# 1. Копируем только файлы сборки для кэширования зависимостей Docker
# (build.gradle, settings.gradle, gradlew скрипт и директорию gradle)
COPY build.gradle settings.gradle ./
COPY gradlew .
COPY gradle gradle/

RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]