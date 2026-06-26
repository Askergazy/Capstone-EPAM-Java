# Spring Shop

Интернет-магазин на Spring Framework (без Spring Boot) с использованием JDBC для работы с базой данных.

## Технологии

- **Spring Framework 6.0.10** (без Spring Boot)
- **Spring Security 6.1.1**
- **JDBC Template** для работы с БД
- **PostgreSQL** в качестве СУБД
- **Thymeleaf** для шаблонов
- **SLF4J + Logback** для логирования
- **i18n** поддержка (русский, английский, казахский)

## Требования

- Java 17+
- PostgreSQL 12+
- Maven 3.6+
- Servlet Container (Tomcat 10+, Jetty, и т.д.)

## Настройка базы данных

1. Создайте базу данных PostgreSQL
2. Обновите настройки подключения в `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
```

3. Создайте необходимые таблицы (схему БД нужно применить отдельно)

## Сборка проекта

```bash
mvn clean package
```

Это создаст WAR файл в директории `target/`

## Запуск

### Вариант 1: На Tomcat
1. Скопируйте `target/shop-0.0.1-SNAPSHOT.war` в директорию `webapps/` вашего Tomcat
2. Запустите Tomcat
3. Приложение будет доступно по адресу: `http://localhost:8080/shop-0.0.1-SNAPSHOT/`

### Вариант 2: Maven Tomcat Plugin
Добавьте в `pom.xml`:
```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <path>/</path>
        <port>8080</port>
    </configuration>
</plugin>
```

Затем запустите:
```bash
mvn tomcat7:run
```

## Функционал

- Аутентификация и регистрация пользователей
- Просмотр каталога товаров
- Поиск товаров по категориям и названию
- Корзина покупок
- Оформление заказов
- Отзывы на товары
- Панель администратора

## Локализация

Приложение поддерживает 3 языка:
- Русский (по умолчанию)
- Английский (`?lang=en`)
- Казахский (`?lang=kk`)

Для смены языка добавьте параметр `lang` к URL:
```
http://localhost:8080/products?lang=en
```

## Логи

Логи сохраняются в директории `logs/`:
- `application.log` - все логи приложения
- `error.log` - только ошибки

Уровни логирования:
- DEBUG - для пакета приложения `kz.askar.shop`
- INFO - для Spring Framework
- DEBUG - для Spring JDBC

## Структура проекта

```
src/main/java/kz/askar/shop/
├── config/         # Конфигурация Spring
├── controller/     # REST и MVC контроллеры
├── dao/           # Data Access Objects (JDBC)
├── entity/        # Модели данных
├── exception/     # Обработчики исключений
├── security/      # Безопасность
├── service/       # Бизнес-логика
└── util/          # Утилиты

src/main/resources/
├── messages*.properties  # Локализация
├── logback.xml          # Конфигурация логирования
└── application.properties # Настройки приложения

src/test/java/
└── kz/askar/shop/service/  # Unit-тесты
```

## Запуск тестов

```bash
mvn test
```

**Результат**: Все 26 тестов проходят успешно ✅

## Что исправлено и оптимизировано

✅ **Удалены старые Repository интерфейсы** - больше не используются
✅ **Убраны JPA/Hibernate аннотации** из всех entity классов
✅ **Исправлены циклические зависимости** в DAO классах
✅ **Упрощены equals/hashCode** - используют только ID
✅ **Добавлен CharacteristicValueService** для работы с характеристиками товаров
✅ **Обновлены контроллеры** для использования новых DAO/Service
✅ **Исправлены все ошибки компиляции**
✅ **Проект успешно собирается** в WAR файл
✅ **Все unit-тесты проходят** успешно

## Важные изменения

Проект был полностью рефакторен:
- Удален Spring Boot, используется обычный Spring Framework
- JPA заменен на JDBC Template с DAO паттерном
- Добавлено централизованное логирование (SLF4J + Logback)
- Добавлен глобальный обработчик исключений
- Реализована поддержка локализации (i18n)
- Написаны unit-тесты для основных сервисов

Подробнее см. [REFACTORING_NOTES.md](REFACTORING_NOTES.md)

## Авторы

Проект рефакторен с использованием лучших практик Spring Framework.
