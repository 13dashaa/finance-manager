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

# 2. Даем права на выполнение gradlew и загружаем зависимости
# Это позволит Docker кэшировать этот слой, если меняется только исходный код
# Используем --no-daemon, что рекомендуется для CI/CD и Docker сборок
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# 3. Копируем остальной исходный код проекта
COPY src ./src

# 4. Собираем исполняемый JAR-файл с помощью задачи bootJar
# Пропускаем тесты (-x test), так как их лучше запускать отдельно в CI
# clean не обязателен в чистом Docker-окружении, но не повредит
RUN ./gradlew bootJar -x test --no-daemon

# --- Этап Запуска (Runtime) ---
# Используем легковесный образ только с JRE нужной версии
# Можно использовать alpine для еще меньшего размера: eclipse-temurin:21-jre-alpine
FROM eclipse-temurin:21-jre

# Устанавливаем рабочую директорию
WORKDIR /app

# 5. Копируем ТОЛЬКО собранный JAR из этапа сборки
# ВАЖНО: Используем wildcard (*.jar), чтобы не зависеть от точной версии в имени файла.
# Если вы жестко задали имя 'finance-manager.jar' в build.gradle,
# то можно написать /app/build/libs/finance-manager.jar, но wildcard надежнее.
COPY --from=builder /app/build/libs/*.jar app.jar

# (Опционально, но рекомендуется для безопасности) Создаем пользователя без root-прав
# RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
# USER appuser

# 6. Указываем порт, который слушает приложение
EXPOSE 8080

# 7. Указываем команду для запуска приложения (стандартный способ для bootJar)
ENTRYPOINT ["java", "-jar", "app.jar"]