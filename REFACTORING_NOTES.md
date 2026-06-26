# Заметки по рефакторингу Spring Shop

## Выполненные задачи

### 1. Миграция с Spring Boot на обычный Spring
- **Обновлен pom.xml**:
  - Удален родительский POM `spring-boot-starter-parent`
  - Заменены все `spring-boot-starter-*` зависимости на обычные Spring зависимости
  - Изменен packaging на `war`
  - Добавлены явные версии для Spring Framework, Spring Security и других зависимостей

- **Создана инициализация приложения**:
  - `WebAppInitializer.java` - инициализация веб-приложения вместо Spring Boot
  - `AppConfig.java` - основной конфигурационный класс
  - `SecurityInitializer.java` - инициализация Spring Security

### 2. Замена JpaRepository на JDBC DAO
- **Созданы DAO классы** для всех сущностей:
  - `UserDao`
  - `ProductDao`
  - `CategoryDao`
  - `ReviewDao`
  - `CartItemDao`
  - `OrderDao`
  - `CharacteristicDao`
  - `CharacteristicValueDao`
  - `OrderedProductDao`

- **Обновлены все сервисы** для использования DAO вместо JpaRepository:
  - `UserService`
  - `ProductService`
  - `CategoryService`
  - `CartItemService`
  - `OrderService`
  - `ReviewService`
  - `RegistrationService`
  - `CharacteristicService`
  - `OrderedProductService`
  - `UserDetailService`
  - `AdminService`

### 3. Добавлено SLF4J логирование
- **Заменены все System.out.println** на SLF4J логирование
- **Добавлены логгеры** во все DAO и Service классы
- **Создан logback.xml** с настройками:
  - Логирование в консоль
  - Ротация файлов логов по дням
  - Отдельный файл для ошибок (error.log)
  - Уровень DEBUG для пакета приложения
  - Уровень INFO для Spring Framework

### 4. Добавлен централизованный Exception Handler
- **Созданы кастомные исключения**:
  - `ResourceNotFoundException`
  - `ValidationException`

- **Создан GlobalExceptionHandler** с обработкой:
  - `ResourceNotFoundException` → 404 страница
  - `ValidationException` → страница валидации
  - `MethodArgumentNotValidException` → JSON с ошибками
  - `NoSuchElementException` → 404 страница
  - `UsernameNotFoundException` → 404 страница
  - `AccessDeniedException` → 403 страница
  - `Exception` → 500 страница

### 5. Добавлена локализация (i18n)
- **Созданы файлы сообщений**:
  - `messages.properties` - русский язык (по умолчанию)
  - `messages_en.properties` - английский язык
  - `messages_kk.properties` - казахский язык

- **Настроена локализация в AppConfig**:
  - `MessageSource` - источник сообщений
  - `LocaleResolver` - резолвер локали (по умолчанию: русский)
  - `LocaleChangeInterceptor` - перехватчик для смены языка через параметр `?lang=en`

### 6. Написаны JUnit тесты
Созданы unit-тесты для 6 сервисов (более 50%):
- `UserServiceTest` - 2 теста
- `ProductServiceTest` - 5 тестов
- `CategoryServiceTest` - 3 теста
- `OrderServiceTest` - 4 теста
- `ReviewServiceTest` - 7 тестов
- `CartItemServiceTest` - 6 тестов

Всего: **27 unit-тестов** с использованием JUnit 5 и Mockito

## Структура проекта после рефакторинга

```
src/main/java/kz/askar/shop/
├── config/
│   ├── AppConfig.java
│   ├── MvcConfig.java
│   ├── SecurityConfig.java
│   ├── SecurityInitializer.java
│   └── WebAppInitializer.java
├── dao/
│   ├── CartItemDao.java
│   ├── CategoryDao.java
│   ├── CharacteristicDao.java
│   ├── CharacteristicValueDao.java
│   ├── OrderDao.java
│   ├── OrderedProductDao.java
│   ├── ProductDao.java
│   ├── ReviewDao.java
│   └── UserDao.java
├── entity/
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── ValidationException.java
├── service/
└── controller/

src/main/resources/
├── application.properties
├── logback.xml
├── messages.properties
├── messages_en.properties
└── messages_kk.properties
```

## Как использовать

### Запуск приложения
Приложение теперь запускается как обычное WAR-приложение на servlet-контейнере (Tomcat, Jetty и т.д.)

### Смена языка
Добавьте параметр `lang` в URL:
- Русский (по умолчанию): `http://localhost:8080/products`
- Английский: `http://localhost:8080/products?lang=en`
- Казахский: `http://localhost:8080/products?lang=kk`

### Логи
Логи сохраняются в директории `logs/`:
- `application.log` - все логи приложения
- `error.log` - только ошибки

### Запуск тестов
```bash
mvn test
```

## Примечания

### Важные исправления
- **Удалены все Repository интерфейсы** - больше не нужны, используются только DAO
- **Удалены JPA/Hibernate аннотации из entity классов** - упрощены до обычных POJO
- **Исправлены циклические зависимости в DAO** - RowMapper'ы создают только ID для связанных объектов
- **Упрощены equals/hashCode** - теперь используют только ID
- **Добавлен AuthenticationManager** в SecurityConfig для работы с UserDetailService

### DTO не создавались
DTO не создавались, так как в требованиях было указано "опционально"

### Рекомендации для дальнейшего развития
1. Добавить транзакционность для DAO операций (@Transactional)
2. Создать DTO для отделения слоя представления от слоя данных
3. Добавить интеграционные тесты
4. Добавить кэширование для часто используемых запросов
5. Настроить connection pool для JDBC (HikariCP)
6. Добавить методы для полной загрузки объектов со связями при необходимости
