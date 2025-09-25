
```java
@RestController
@Slf4j
@AllArgsConstructor
public class OrderController {
    private final OrderService service;

    @GetMapping("/orders/find")
    public OrderResponse getProduct(@RequestParam("id") String id) {
        return service.findById(id);
    }

    @GetMapping("/orders/create")
    public CreatedOrderResponse createOrder(@RequestParam("product") String productEan,
                                            @RequestParam("price") Double price) {
        return service.createOrder(productEan, price);
    }

    @PutMapping("/orders/delete")
    public CreatedOrderResponse deleteOrder(@RequestParam("id") String id) {
        return service.delete(id);
    }

    @PutMapping("/orders/order-item/create")
    public CreatedOrderItemResponse createOrderItems(@RequestParam("id") String itemId,
                                                     @RequestParam("product") String productEan,
                                                     @RequestParam("price") Double price) {
        return service.createOrderItem(itemId, productEan, price);
    }
}
```

# Итог (одним предложением)

Контроллер нарушает HTTP/REST семантику, неправильно использует аннотации и типы, не валидирует/необрабатывает ошибки, возвращает неверные статусы/DTO — нужно рефакторить на RESTful-эндпойнты, DTO + валидация, `ResponseEntity`/статусы, `@ControllerAdvice`, и безопасную работу с деньгами/идентификаторами. 🚀

---

# Подробно по пунктам — проблема → почему → как именно исправить (с примерами)

---

## 1) Неправильные HTTP-методы

**Проблема:** `GET /orders/create`, `PUT /orders/delete`, `PUT /orders/order-item/create`.  
**Почему плохо:** нарушает семантику HTTP:

- `GET` — только чтение (без побочных эффектов).

- `POST` — создание (не идемпотентно).

- `PUT` — замена/идемпотентное обновление.

- `DELETE` — удаление.  
  **Как исправить:** заменить на правильные методы и пути:

- `GET /orders/{id}` — получение.

- `POST /orders` — создание заказа.

- `DELETE /orders/{id}` — удаление.

- `POST /orders/{orderId}/items` — добавление позиции.


**Пример:**
```java
@GetMapping("/orders/{id}")
public OrderResponse getOrder(@PathVariable String id) { ... }

@PostMapping("/orders")
public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest req) { ... }

@DeleteMapping("/orders/{id}")
public ResponseEntity<Void> deleteOrder(@PathVariable String id) { ... }

@PostMapping("/orders/{orderId}/items")
public ResponseEntity<OrderItemResponse> createOrderItem(@PathVariable String orderId,
                                                         @RequestBody @Valid CreateOrderItemRequest req) { ... }
```

---

## 2) Нейминг эндпоинтов и ресурсо-ориентированность

**Проблема:** пути содержат действия (`/find`, `/create`).  
**Почему:** REST использует существительные — действия задаются методом.  
**Как исправить:** использовать ресурсы (см. п.1). Версионность: `/api/v1/orders`.

---

## 3) `@RequestParam` вместо `@RequestBody` для создания/обновления

**Проблема:** `createOrder(@RequestParam("product") String productEan, ...)`  
**Почему:** query-параметры — не подходят для сложных/чувствительных/обязательных данных; ухудшает читаемость и расширяемость.  
**Как исправить:** создать DTO `CreateOrderRequest` и принимать `@RequestBody @Valid`.

**DTO пример:**
```java
public class CreateOrderRequest {
  @NotBlank private String productEan;
  @NotNull @DecimalMin("0.0") private BigDecimal price;
  // + валидир. аннотации
}
```

---

## 4) Работа с деньгами — `Double` нельзя использовать

**Проблема:** `Double price` — погрешности, округления.  
**Почему:** float/double не точны для валют.  
**Как исправить:** использовать `BigDecimal` + валюты (или `Money` из библиотеки). В DTO — `BigDecimal`, в БД — DECIMAL(precision, scale).

---

## 5) Неверные возвращаемые типы и HTTP-статусы

**Проблема:** `deleteOrder` возвращает `CreatedOrderResponse`.  
**Почему:** удаление должно быть 204/200, не возвращать создание.  
**Как исправить:** для `POST /orders` — вернуть `201 Created` + `Location` header; для `DELETE` — `204 No Content` или `200` с body.

**Пример:**
```java
@PostMapping("/orders")
public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest req) {
    OrderResponse created = service.createOrder(req);
    URI location = URI.create("/api/v1/orders/" + created.getId());
    return ResponseEntity.created(location).body(created);
}

@DeleteMapping("/orders/{id}")
public ResponseEntity<Void> delete(@PathVariable String id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
}
```

---

## 6) Прямо возвращаем сервисные объекты — утечка доменной модели

**Проблема:** контроллер возвращает `service.findById(id)` напрямую.  
**Почему:** сервисные/сущностные объекты (entity) могут содержать лишние поля (пароли, внутренние поля).  
**Как исправить:** возвращать DTO, маппить (MapStruct/ручная маппинг).

---

## 7) Нет валидации входных данных

**Проблема:** отсутствуют `@Valid`, `@NotNull`, `@Positive` и т.п.  
**Почему:** некорректные данные могут привести к ошибкам в сервисе или НУЛЛ-пойнтам.  
**Как исправить:** добавить DTO с аннотациями, в контроллер — `@Validated`/`@Valid`.

**Пример:**
```java
@RestController
@Validated
public class OrderController {
  @PostMapping("/orders")
  public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest req) { ... }
}
```

---

## 8) Нет глобальной обработки ошибок (`@ControllerAdvice`)

**Проблема:** ошибки попадут клиенту в сыром виде/500.  
**Почему:** надо единообразно маппить исключения (404, 400, 409, 500).  
**Как исправить:** создать `@RestControllerAdvice` с обработкой `MethodArgumentNotValidException`, `EntityNotFoundException`, `BusinessException` и т.д.

**Пример:**
```java
@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiError> onNotFound(...) { ... } // 404
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> onValidation(...) { ... } // 400
}
```

---

## 9) Использование Lombok: `@AllArgsConstructor` vs `@RequiredArgsConstructor`

**Проблема:** `@AllArgsConstructor` генерирует конструктор для всех полей (включая не-final), может мешать.  
**Почему:** `@RequiredArgsConstructor` лучше для final-полей (безопаснее).  
**Как исправить:** заменить на `@RequiredArgsConstructor` и делать сервис `final`. Либо явно использовать конструктор.

---

## 10) Логгер `@Slf4j` — использовать или убрать

**Проблема:** аннотация есть, но логирования нет (либо логик неинформативен).  
**Почему:** логирование полезно для трассировки, но не должно логать чувствительные данные.  
**Как исправить:** логировать входящие запросы/ошибки/ид-операций на INFO/ERROR, не логировать PII/карты/пароли.

---

## 11) Нет контрактной/интеграционной проверки (проверки статусов)

**Проблема:** контроллер неявно зависит от сервиса.  
**Как исправить:** писать unit-тесты (mock service) и интеграционные тесты (MockMvc / @SpringBootTest). Тестировать статусы, заголовки (Location), валидацию.

---

## 12) Idempotency и уникальность операций

**Проблема:** создание заказов не учитывает повторы; удаление/создание позиций — idempotентность.  
**Как исправить:** для создающих операций определить idempotency-key механизмы (опционально), проверять дубликаты и возвращать корректный ответ (409/200).

---

## 13) Безопасность / авторизация

**Проблема:** контроллер не показывает авторизацию/аутентификацию.  
**Как исправить:** ограничить доступ к эндпоинтам (Spring Security), проверять права пользователя на удаление/создание и пр.

---

## 14) Типы идентификаторов — PathVariable vs RequestParam

**Проблема:** `@RequestParam("id")` неудобен для REST.  
**Как исправить:** использовать `@PathVariable` в URL (`/orders/{id}`) — понятнее и RESTful.

---

## 15) Консистентность имен DTO/методов

**Проблема:** перемешаны `getProduct`, `createOrder` — метод `getProduct` возвращает `OrderResponse`.  
**Как исправить:** называть методы по ресурсам: `getOrder`, `createOrder`, `deleteOrder`, `addOrderItem`.

---

# Полный пример исправленного контроллера (с DTO и обработкой)
```java
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {
    private final OrderService service;

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id) {
        OrderResponse resp = service.findById(id);
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest req) {
        OrderResponse created = service.createOrder(req);
        URI location = URI.create("/api/v1/orders/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItemResponse> createOrderItem(@PathVariable String orderId,
                                                             @RequestBody @Valid CreateOrderItemRequest req) {
        OrderItemResponse item = service.createOrderItem(orderId, req);
        URI location = URI.create(String.format("/api/v1/orders/%s/items/%s", orderId, item.getId()));
        return ResponseEntity.created(location).body(item);
    }
}
```

---
# Короткий чек-лист действий (приоритеты)

1. Перевести эндпоинты на RESTful (методы + пути).
2. Заменить `@RequestParam` на `@RequestBody` + DTO (валидация).
3. Заменить `Double` на `BigDecimal` для денег.
4. Вернуть корректные HTTP-статусы (201/204/200/404/400).
5. Добавить `@ControllerAdvice` для ошибок.
6. Маппинг entity → DTO.
7. Тесты: unit (Mock service) + интеграция (MockMvc).
8. Добавить безопасность и логирование.

---
