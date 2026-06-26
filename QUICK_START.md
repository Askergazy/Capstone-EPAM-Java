# Быстрый старт

## Перед запуском

1. **Убедитесь что PostgreSQL запущен**
2. **Создайте базу данных** (если еще не создана)
3. **Проверьте настройки подключения** в `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## Способ 1: Быстрый запуск через скрипт (РЕКОМЕНДУЕТСЯ)

### Windows:
```bash
run.bat
```

### Linux/Mac:
```bash
chmod +x run.sh
./run.sh
```

Приложение будет доступно по адресу: **http://localhost:8080**

---

## Способ 2: Запуск через Maven команды

### 1. Соберите проект
```bash
mvn clean package -DskipTests
```

### 2. Запустите встроенный Tomcat
```bash
mvn tomcat7:run
```

Приложение запустится на **http://localhost:8080**

---

## Способ 3: Деплой на внешний Tomcat

### 1. Соберите WAR файл
```bash
mvn clean package
```

### 2. Скопируйте WAR в Tomcat
```bash
copy target\shop-0.0.1-SNAPSHOT.war C:\path\to\tomcat\webapps\
```

### 3. Запустите Tomcat
```bash
C:\path\to\tomcat\bin\startup.bat
```

Приложение будет доступно: **http://localhost:8080/shop-0.0.1-SNAPSHOT/**

---

## Способ 4: Запуск из IntelliJ IDEA

1. Откройте проект в IDEA
2. Найдите класс `WebAppInitializer`
3. Нажмите правой кнопкой → "Run on Tomcat"
4. Или создайте конфигурацию Tomcat:
   - Run → Edit Configurations
   - Add New → Tomcat Server → Local
   - Deployment → Add → Artifact → shop:war
   - Apply → OK → Run

---

## Проверка работы

После запуска откройте браузер:
- **http://localhost:8080** - главная страница
- **http://localhost:8080/products** - каталог товаров

---

## Смена языка

Добавьте параметр `lang` к URL:
- Русский: http://localhost:8080?lang=ru
- English: http://localhost:8080?lang=en
- Қазақша: http://localhost:8080?lang=kk

---

## Остановка приложения

### Maven Tomcat:
Нажмите **Ctrl + C** в консоли

### Внешний Tomcat:
```bash
C:\path\to\tomcat\bin\shutdown.bat
```

---

## Проблемы и решения

### Ошибка: "Port 8080 already in use"
```bash
# Измените порт в pom.xml (тег <port>) или остановите процесс на порту 8080
netstat -ano | findstr :8080
taskkill /PID <номер_процесса> /F
```

### Ошибка подключения к базе данных
- Проверьте что PostgreSQL запущен
- Убедитесь что база данных создана
- Проверьте логин/пароль в application.properties

### Просмотр логов
Логи сохраняются в директории `logs/`:
- `logs/application.log` - все логи
- `logs/error.log` - только ошибки

---

## Запуск тестов

```bash
mvn test
```

Должно пройти 26 тестов без ошибок.
