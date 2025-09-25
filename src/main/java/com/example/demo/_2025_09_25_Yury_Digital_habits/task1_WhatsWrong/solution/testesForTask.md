Вариант **unit-тесты (JUnit + Mockito / MockRestServiceServer)**.  
Давай детально разберём, как правильно протестировать исправленный класс `FileEventHandler`.

---
# 1. Что мы тестируем

Класс `FileEventHandler`:
- Читает файл по пути из `event.getSource()`.
- Формирует `HttpEntity` и вызывает `RestTemplate.postForEntity(...)`.
- Возвращает `true/false` в зависимости от результата.
- Логирует и обрабатывает исключения.

---
# 2. Инструменты
- **JUnit 5** (`@Test`, `@BeforeEach`).
- **MockRestServiceServer** (Spring) — позволяет замокать HTTP-ответы для `RestTemplate`.
- **Mockito** — для мока `Event`.
- **TempFile** (Java NIO) — для тестовых файлов.

---
# 3. Пример теста (успешный сценарий)
```java
@ExtendWith(SpringExtension.class)
class FileEventHandlerTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private FileEventHandler handler;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        handler = new FileEventHandler(
                "http://localhost", 8080, "/upload", restTemplate);
    }

    @Test
    void handle_ShouldReturnTrue_WhenServerResponds200() throws Exception {
        // given
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "hello", StandardCharsets.UTF_8);

        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getSource()).thenReturn(tempFile.toString());

        URI expectedUri = UriComponentsBuilder
                .fromHttpUrl("http://localhost")
                .port(8080)
                .path("/upload")
                .build()
                .toUri();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("ok", MediaType.TEXT_PLAIN));

        // when
        boolean result = handler.handle(event);

        // then
        Assertions.assertTrue(result);
        mockServer.verify();
    }
}
```

---
# 4. Негативные сценарии

### a) Ошибка чтения файла
```java
@Test
void handle_ShouldReturnFalse_WhenFileDoesNotExist() {
    Event event = Mockito.mock(Event.class);
    Mockito.when(event.getSource()).thenReturn("/path/does/not/exist.txt");

    boolean result = handler.handle(event);

    Assertions.assertFalse(result);
}
```

---
### b) HTTP 500 от сервера
```java
@Test
void handle_ShouldReturnFalse_WhenServerError() throws Exception {
    Path tempFile = Files.createTempFile("test", ".txt");
    Files.writeString(tempFile, "oops");

    Event event = Mockito.mock(Event.class);
    Mockito.when(event.getSource()).thenReturn(tempFile.toString());

    URI expectedUri = UriComponentsBuilder
            .fromHttpUrl("http://localhost")
            .port(8080)
            .path("/upload")
            .build()
            .toUri();

    mockServer.expect(ExpectedCount.once(),
                    requestTo(expectedUri))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withServerError());

    boolean result = handler.handle(event);

    Assertions.assertFalse(result);
    mockServer.verify();
}
```

---
# 5. Ключевые приёмы

✅ Используем `MockRestServiceServer`, чтобы **не делать реальных HTTP-запросов**.  
✅ Генерируем временные файлы (`Files.createTempFile`) для проверки чтения.  
✅ Мокаем `Event` (через Mockito).  
✅ Тестируем как **позитивные**, так и **негативные** сценарии.  
✅ Проверяем `boolean`-результат и что сервер вызван (`mockServer.verify()`).

---
## Вот полный класс юнит-тестов для твоего FileEventHandler, покрывающий разные сценарии.

```java
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class FileEventHandlerTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private FileEventHandler handler;
    private URI expectedUri;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        handler = new FileEventHandler("http://localhost", 8080, "/upload", restTemplate);

        expectedUri = UriComponentsBuilder
                .fromHttpUrl("http://localhost")
                .port(8080)
                .path("/upload")
                .build()
                .toUri();
    }

    @Test
    void handle_ShouldReturnTrue_WhenServerResponds200() throws Exception {
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "hello", StandardCharsets.UTF_8);

        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getSource()).thenReturn(tempFile.toString());

        mockServer.expect(once(), requestTo(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("ok", MediaType.TEXT_PLAIN));

        boolean result = handler.handle(event);

        Assertions.assertTrue(result);
        mockServer.verify();
    }

    @Test
    void handle_ShouldReturnFalse_WhenFileDoesNotExist() {
        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getSource()).thenReturn("/path/not/exist.txt");

        boolean result = handler.handle(event);

        Assertions.assertFalse(result);
    }

    @Test
    void handle_ShouldReturnFalse_WhenServerError() throws Exception {
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "oops");

        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getSource()).thenReturn(tempFile.toString());

        mockServer.expect(once(), requestTo(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        boolean result = handler.handle(event);

        Assertions.assertFalse(result);
        mockServer.verify();
    }

    @Test
    void handle_ShouldReturnFalse_WhenFileIsEmpty() throws Exception {
        Path tempFile = Files.createTempFile("empty", ".txt");

        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getSource()).thenReturn(tempFile.toString());

        mockServer.expect(once(), requestTo(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());

        boolean result = handler.handle(event);

        Assertions.assertFalse(result);
        mockServer.verify();
    }

    @Test
    void handle_ShouldReturnFalse_WhenEventIsNull() {
        boolean result = handler.handle(null);

        Assertions.assertFalse(result);
    }
}
```

---
## 📌 Что здесь проверяется:
1. **Успех** — файл есть, сервер возвращает `200 OK`.
2. **Файл не найден** — `IOException`, метод возвращает `false`.
3. **HTTP 500** от сервера — `false`.
4. **Пустой файл** — сервер возвращает `400 Bad Request`, метод `false`.
5. **Null-event** — сразу `false`.

---
