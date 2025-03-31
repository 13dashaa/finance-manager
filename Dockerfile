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
ENV PORT=10000
EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "app.jar"]
#ENTRYPOINT ["sh", "-c", "echo '--- Environment Variables ---' && printenv && echo '--- Directory /app Listing ---' && ls -l /app && echo '--- Java Version ---' && java -version && echo '--- Sleeping forever (kill manually or redeploy) ---' && sleep infinity"]