# Финтех: Java-разработка. Лабораторные работы

В данном репозитории представлены выполненные лабораторные работы курса по Java-разработке для финтеха. В ходе курса были изучены и использованы технологии Spring Boot, Docker, Grafana, Kafka, 
а также применены навыки работы с системами сборки, структурами данных, многопоточностью, базами данных и паттернами проектирования. 

---

## Лаб 1. Git
1. Пройти курс по git, создать репозиторий на GitHub.
2. В ветке `fj_2024_lesson_1`:
    - Добавить README.md (опыт, цели, проект).
    - Настроить `.gitignore`.
    - Сделать PR в `main`.
3. **Критерии оценки:** 4 балла (1 балл за ветку/PR, 1 за опыт/цели, 1 за markdown, 1 за `.gitignore`).


## Лаб 2. Системы сборки
1. Добавить Gradle, подключить `Lombok` и `jackson-databind`.
2. Обработать JSON-файлы, преобразовать в XML.
3. Использовать `logger` (info, error, debug, warn).

## Лаб 3. Структуры данных (Java)
1. Реализовать `CustomLinkedList` с методами (`add`, `get`, `size`, `contains`, `addAll`).
2. Использовать терминальную операцию `reduce` для преобразования стрима.

## Лаб 4. Структуры данных (Kotlin)
1. Подготовить DTO для работы с API KudaGo.
2. Написать функции:
    - Получение новостей, сортировка по дате.
    - Фильтрация новостей по рейтингу.
    - Сохранение новостей в CSV.
3. **Доп. задание:** создать DSL для красивого вывода новостей.

## Лаб 5. Spring Boot
1. Реализовать 2 CRUD REST контроллера (`categories`, `locations`).
2. Хранить данные в хранилище (например, `ConcurrentHashMap`).
3. Инициализировать данные из API KudaGo при старте, логировать процесс.
4. Реализовать аннотацию для логирования времени выполнения методов.

---

### Лаб 6. *Встреча по курсовой работе.*

---

## Лаб 7. Тестирование и подходы к нему.

### Эндпоинты

1. **GET /currencies/rates/{code}**
    - Формат ответа: `{"currency": "USD", "rate": 123.4}`

2. **POST /currencies/convert**
    - Формат запроса: `{"fromCurrency": "USD", "toCurrency": "RUB", "amount": 100.5}`
    - Формат ответа: `{"fromCurrency": "USD", "toCurrency": "RUB", "convertedAmount": 9000.5}`

### Источник данных
- Получать курсы валют из ЦБ РФ:  
  `http://www.cbr.ru/scripts/XML_daily.asp?date_req=02/03/2002`  
  (XML формат ответа).
- Использовать `RestTemplate`, `FeignClient` или `WebClient`.
- URL сервиса вынести в конфигурацию.

### Дополнительные функции
1. **Circuit Breaker** для обработки недоступности сервиса ЦБ.
2. **Кэширование** курсов валют на 1 час.

### Валидация запросов
1. Если параметр запроса отсутствует, вернуть:  
   **BAD_REQUEST** с описанием ошибки.
2. Если `amount <= 0`, вернуть:  
   **BAD_REQUEST** с описанием ошибки.
3. Если указана несуществующая валюта, вернуть:  
   **BAD_REQUEST** с описанием ошибки.
4. Если указанная валюта отсутствует в ответе от ЦБ, вернуть:  
   **NOT_FOUND** с описанием ошибки.
5. Если сервис ЦБ недоступен, вернуть:  
   **SERVICE_UNAVAILABLE** с заголовком `Retry-After: {через час}`.

**Формат ошибки:**  
`{"code": 404, "message": "Unsupported currency code"}`  
(Использовать `@ExceptionHandler`).

### Документация
- Добавить эндпоинты в Swagger с описанием и кодами ошибок.

### Тестирование
1. Проверка валидации для обоих эндпоинтов.
2. Проверка корректности конвертации для `/currencies/convert`.
3. Проверка обработки ошибки при недоступности сервиса ЦБ.

## Лаб 8. Взаимодействие с другими сервисами. Сетевые протоколы.

### Эндпоинты

1. **GET /currencies/rates/{code}**
    - Формат ответа: `{"currency": "USD", "rate": 123.4}`

2. **POST /currencies/convert**
    - Формат запроса: `{"fromCurrency": "USD", "toCurrency": "RUB", "amount": 100.5}`
    - Формат ответа: `{"fromCurrency": "USD", "toCurrency": "RUB", "convertedAmount": 9000.5}`

### Источник данных
- Получать курсы валют из ЦБ РФ:  
  `http://www.cbr.ru/scripts/XML_daily.asp?date_req=02/03/2002`  
  (XML формат ответа).
- Использовать `RestTemplate`, `FeignClient` или `WebClient`.
- URL сервиса вынести в конфигурацию.


### Дополнительные функции
1. **Circuit Breaker** для обработки недоступности сервиса ЦБ.
2. **Кэширование** курсов валют на 1 час.


### Валидация запросов
1. Если параметр запроса отсутствует, вернуть:  
   **BAD_REQUEST** с описанием ошибки.
2. Если `amount <= 0`, вернуть:  
   **BAD_REQUEST** с описанием ошибки.
3. Если указана несуществующая валюта, вернуть:  
   **BAD_REQUEST** с описанием ошибки.
4. Если указанная валюта отсутствует в ответе от ЦБ, вернуть:  
   **NOT_FOUND** с описанием ошибки.
5. Если сервис ЦБ недоступен, вернуть:  
   **SERVICE_UNAVAILABLE** с заголовком `Retry-After: {через час}`.

**Формат ошибки:**  
`{"code": 404, "message": "Unsupported currency code"}`  
(Использовать `@ExceptionHandler`).

### Документация
- Добавить эндпоинты в Swagger с описанием и кодами ошибок.

### Тестирование
1. Проверка валидации для обоих эндпоинтов.
2. Проверка корректности конвертации для `/currencies/convert`.
3. Проверка обработки ошибки при недоступности сервиса ЦБ.

## Лаб 9. Многопоточность и конкурентный код.

### 1. Использование ExecutorService и Future
- Создать два бина ExecutorService:
    1. `FixedThreadPool` с фиксированным количеством потоков (из конфигурации).
    2. `ScheduledThreadPool` для запуска задач по расписанию.
- Параллелизовать процесс инициализации данных:
    - Использовать `FixedThreadPool` для разделения задач по типам данных.
    - Запуск задач по расписанию через `ScheduledThreadPool`.
- Измерить время работы до и после параллелизации (с различным количеством потоков), результаты представить в таблице.

### 2. Использование CompletableFuture
- Реализовать метод `/events` в `EventController` для получения мероприятий:
    - Асинхронно запросить список событий и конвертировать бюджет в рубли.
    - Использовать `CompletableFuture.thenAcceptBoth` для фильтрации подходящих мероприятий.
    - Вернуть `CompletableFuture` со списком событий.

### 3. Реализация на Project Reactor
- Переписать реализацию из пункта 2 с использованием Project Reactor:
    - Применить операторы `concatMap`, `flatMap`, `Mono.zip`, `Flux.concat` и другие.

### 4. Kotlin Coroutines
- Параллелизация сохранения новостей:
    1. **Worker'ы**: Выполняют операции получения данных (`getNews`) и записи в `Channel` на пуле потоков (`newFixedThreadPoolContext`).
    2. **Processor**: Читает из `Channel` и записывает в файл, завершая работу после окончания данных.
- Замерить время до и после, оценить влияние количества потоков.

### 5. Доработка клиентов для API
- Реализовать рейт-лимитинг для API KudaGo:
    - Ограничить количество одновременных запросов с использованием синхронизаторов (`Semaphore`, `Barrier`).
    - Количество потоков вынести в конфигурацию.
- Покрыть функциональность тестами.

### Общие требования
- Обеспечить тестирование, логгирование и обработку ошибок.

## Лаб 10. Работа с Базами данных и СУБД

1. **Описать структуру таблиц `places` и `events` в чейнджлогах `liquibase`.**
    - Добавить поля в таблице `places` аналогичные полям в АПИ для городов, реализованным в лаб. 5.
    - Обязательные поля для таблицы `events`: **name**, **date**, **place_id**. Можно добавить дополнительные поля по усмотрению.
    - Таблица `events` должна ссылаться на таблицу `places` через поле `id`.

2. **Добавить `dataSource` БД.**
    - Конфигурационные файлы для настроек базы данных должны быть в соответствующих конфигурационных файлах проекта.

3. **Добавить Spring Data репозитории и Entity классы для `events` и `places`.**
    - Поле `place.events` должно загружаться лениво по умолчанию.
    - Для метода поиска `place` по `id` загружать события жадно с использованием `@Query` и **JOIN FETCH**.
    - Добавить метод поиска событий по `name`, `place`, `fromDate` и `toDate`, используя `Specification`, чтобы избежать проблемы N+1.
    - При удалении `place`, события, связанные с этим местом, должны автоматически удаляться каскадно.

4. **Добавить CRUD API для `events`.**
    - Существующие API для городов и новое API для событий должны работать с базой данных, а не с API kudago.

5. **Реализовать обработку ошибок.**
    - Валидация входящих параметров.
    - Возвращать **NOT_FOUND** если сущность не найдена при поиске, обновлении или удалении по `id`.
    - Возвращать **400** если связанная сущность не найдена (например, при создании события с несуществующим местом).
    - Для метода поиска по фильтру, если события не найдены, возвращать пустой список.

6. **Покрыть функционал сквозными тестами, используя `@TestContainers`.**
    - Проверить, что при вызове метода создания/обновления сущности она сохраняется в базе данных.
    - При наличии сущности в базе данных проверять, что она возвращается в API через метод получения этой сущности.

## Лаб 11. Паттерны проектирования в Java

### Реализовать следующие паттерны проектирования:

1. **Итератор**  
   Дополнить интерфейс `CustomLinkedList<E>` из лаб. 3 методом `iterator()`. Реализовать интерфейс `CustomIterator<E>` с методами:
    - `boolean hasNext()`
    - `E next()`
    - `forEachRemaining(Consumer<? super E> action)`

2. **Наблюдатель**  
   Реализовать механизм сохранения данных в хранилище записей из лаб. 5 через паттерн наблюдатель.

3. **Снимок**  
   В лаб. 5 были реализованы CRUD методы для сущностей `categories` и `locations`. Необходимо реализовать хранение истории изменений сущностей с использованием паттерна **Снимок**.

4. **Команда**  
   Реализовать инициализацию информации о категориях и локациях из лаб. 5 с использованием паттерна **Команда**.

---

### Лаб 12. Встреча по курсовой работе

---

## Лаб 13. Безопасность backend-приложений

### Требования:

1. **Регистрация пользователей:**
    - Реализовать эндпоинт для регистрации новых пользователей.
    - Все зарегистрированные пользователи должны получать роль `USER` и сохраняться в базе данных.
    - Использовать надежные механизмы хеширования (bcrypt) для хранения паролей в базе данных.

2. **Логин и логаут:**
    - Подключить зависимость `spring-boot-starter-security`.
    - Реализовать эндпоинты для логина и логаута пользователя.
    - После успешного логина пользователь должен получать токен, который будет использоваться для последующих запросов. Токен должен быть выдан на срок 10 минут. Если пользователь нажал "запомнить меня", токен должен запоминаться на 30 дней.
    - Реализовать механизм для логаута, который будет инвалидировать (удалять) текущий токен.

3. **Сброс пароля:**
    - Реализовать функционал сброса пароля через эндпоинт, с возможностью использования двухфакторной аутентификации (заглушка с кодом "0000").

4. **Авторизация и доступ:**
    - Все эндпоинты должны проверять роль пользователя для доступа.
    - Например, к эндпоинтам с ролью "admin" доступ должен быть только у пользователей с ролью `ADMIN`.

## Лаб 14. Контейнеризация и CI/CD

### Создать workflow:

1. Собирать проект при каждом `push` в Merge Request.
2. Автоматизировать прогон тестов.
3. Рассчитывать покрытие тестами.
4. Проверить код с использованием статического анализатора.
5. Залить Docker-образ в GitHub Packages.

## Лаб 15. Очереди сообщений и асинхронное взаимодействие

### Конфигурации для тестирования:

1. **Необходимо протестировать следующие конфигурации продюсеров и консюмеров для каждой системы:**
    - 1 продюсер и 1 консюмер (Simple)
    - 3 продюсера и 1 консюмер (Load balancing)
    - 1 продюсер и 3 консюмера (Multiple consumers)
    - 3 продюсера и 3 консюмера (Load balancing + multiple consumers)
    - 10 продюсеров и 10 консюмеров (Stress test)

2. **Для каждой конфигурации нужно измерить:**
    - Время отклика (latency) на уровне продюсера и консюмеров.
    - Пропускную способность (throughput) — количество сообщений, которые система может обработать в секунду.
    - Задержки доставки сообщений и время обработки на консюмере.
