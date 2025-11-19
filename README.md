# SkillBench

SkillBench — веб-приложение на Java 17, Tomcat, Servlets, Hibernate ORM, PostgreSQL, Liquibase и Thymeleaf.

---

## 🚀 Запуск через Docker

В корне проекта выполните:

```bash
docker compose build
docker compose up -d
```

После запуска приложение будет доступно по адресу:

```
http://localhost:8080
```

Контейнеры:

- **skillbench-db** — PostgreSQL
- **skillbench-app** — Tomcat + приложение (`ROOT.war`)

Остановка контейнеров:

```bash
docker compose down
```

---

## 🗄️ Миграции и тестовые данные

Liquibase автоматически применяет миграции при старте приложения  
и загружает тестовые данные (тесты, вопросы, ответы).

---

## 👤 DevRunnerServlet (тестовый запуск)

При старте сервлета создаётся администратор по умолчанию:

- **Email:** `admin@admin.ru`
- **Password:** `adminADMIN1`
- **Name:** `admin`

---

## 📝 Регистрация пользователей

Регистрация осуществляется вручную через HTML-шаблоны (Thymeleaf).

---

## ⚙️ Локальный запуск без Docker (IntelliJ IDEA)

В проекте есть файл:

```
src/main/resources/hibernate.properties
```

Он содержит два варианта конфигурации БД:

### ✔ Вариант 1 — Docker (активный по умолчанию)

Использует:

```properties
hibernate.hikari.jdbcUrl=jdbc:postgresql://db:5432/skillbench
hibernate.hikari.username=postgres
hibernate.hikari.password=postgres
```

### ✔ Вариант 2 — Локальный PostgreSQL (закомментирован)

Чтобы запустить локально:

1. Закомментировать Docker-параметры.
2. Раскомментировать блок вида:

```properties
hibernate.hikari.jdbcUrl=jdbc:postgresql://localhost:5432/skillbench
hibernate.hikari.username=postgres
hibernate.hikari.password=postgres
```

---

## 📦 Технологии

- Java 17
- Tomcat 10/11
- Jakarta Servlets
- Hibernate ORM + HikariCP
- Liquibase (YAML)
- PostgreSQL
- Thymeleaf

---
