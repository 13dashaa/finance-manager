# Finance Manager

## Описание проекта

Finance Manager — это приложение на Java, предназначенное для управления личными финансами. Оно позволяет пользователям отслеживать свои бюджеты, категории расходов и транзакции, а также управлять пользователями.

## Основные функции

- Создание и управление бюджетами
- Добавление и получение категорий расходов
- Ведение транзакций
- Управление пользователями

## Технологии

- **Язык программирования**: Java
- **Фреймворк**: Spring Boot
- **База данных**: (предполагается использование) H2, MySQL или PostgreSQL
- **Библиотеки**: Lombok

## Структура проекта

### Модели

- **Budget**: Представляет бюджет с полями `id`, `userId`, `categoryId`, `limit`, `period` и `createdAt`.
- **Category**: Представляет категорию расхода с полями `id` и `name`.
- **Transaction**: Представляет транзакцию с полями `id`, `description`, `amount`, `userId`, `categoryId`, `date` и `createAt`.
- **User**: Представляет пользователя с полями `id`, `username`, `email`, `password` и `createdAt`.

### Контроллеры

- **BudgetController**: Обрабатывает запросы на создание и получение бюджетов.
- **CategoryController**: Обрабатывает запросы на создание и получение категорий.
- **TransactionController**: Обрабатывает запросы на создание и получение транзакций.
- **UserController**: Обрабатывает запросы на создание и получение пользователей.

## Установка и запуск

### Клонирование репозитория
```bash
git clone https://github.com/yourusername/finance-manager.git
cd finance-manager 
```

### Настройка среды выполнения

1. Убедитесь, что у вас установлен JDK 11 или выше.
2. Установите необходимые зависимости, используя Maven или Gradle.

### Запуск приложения

Для запуска приложения используйте следующую команду:
```bash
mvn spring-boot:run
или
./gradlew bootRun
```

## Использование
Используйте REST API для взаимодействия с приложением.

