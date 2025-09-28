## Дан код. <br>Нужно определить что именно с ним _"не так"_.

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class InterviewService {

    private final ScoreRepository scoreRepository;
    private final TransactionTemplate transactionTemplate;
    private final InterviewScoreMLService interviewScoreMLService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InterviewService(ScoreRepository scoreRepository,
                            TransactionTemplate transactionTemplate,
                            InterviewScoreMLService interviewScoreMLService) {
        this.scoreRepository = scoreRepository;
        this.transactionTemplate = transactionTemplate;
        this.interviewScoreMLService = interviewScoreMLService;
    }

    /**
     * Метод считает сколько очков заработал кандидат,
     * сохраняет результат в базу и кидает callback об этом во внешний сервис
     */
    public void process(Candidate c) {
        transactionTemplate.executeWithoutResult(status -> {
            Score s = interviewScoreMLService.compute(c);
            String body = objectMapper.writeValueAsString(Map.of(c.getName(), s));

            Mono<ResponseEntity<Void>> request = WebClient.create()
                    .post()
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .toBodilessEntity();

            scoreRepository.saveScore(s);
        });
    }
}

class Candidate {
    private final String name;
    private final List<Integer> tasksSolvedId;

    public Candidate(String name, List<Integer> tasksSolvedId) {
        this.name = name;
        this.tasksSolvedId = tasksSolvedId;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getTasksSolvedId() {
        return tasksSolvedId;
    }
}

class Score {
    private final String name;
    private final int score;

    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
```

---
## Быстрый вывод — что не так (_короче_)

1. Код **не скомпилируется** из-за `objectMapper.writeValueAsString(...)` (checked-исключение). 🚫

2. HTTP-запрос **никогда не выполняется** — вы только строите `Mono`, но не `subscribe()`/`block()`. ⚠️

3. Отправка внешнего callback **внутри транзакции** — плохая идея (долгие операции, рассинхронизация при rollback). ❌

4. Создание `WebClient` и `ObjectMapper` прямо в методе/классе — плохая инжекция/повторное создание, нет конфигов/таймаутов. 🧩

5. Нет обработки ошибок (сохранение, сериализация, HTTP) и логирования — риск Silent-fail. 🕳️

6. Используется `name` как идентификатор в `Score`/callback — риск коллизий; лучше `id`. 🆔

---
## Подробный разбор по пунктам (_что именно и почему_)

---
## 1) `objectMapper.writeValueAsString(...)` — компиляция и обработка ошибок

- `writeValueAsString` бросает `JsonProcessingException` (checked). Ваш `process(...)` 
**не** объявляет `throws` и не ловит исключение → **компилятор не пропустит**.

- **Решение:** либо обёрнуть в `try/catch` и корректно логировать/реагировать, либо инжектировать `ObjectMapper` 
и/или позволить сериализацию внутри WebClient (он сам использует Jackson).

Пример:
```java
String body;
try {
  body = objectMapper.writeValueAsString(Map.of(c.getName(), s));
} catch (JsonProcessingException ex) {
  log.error("Cannot serialize score", ex);
  throw new IllegalStateException(ex);
}
```

---
## 2) `Mono<ResponseEntity<Void>> request = ...` — запрос не выполняется
- Вы создаёте `Mono`, но **не подписываетесь** и не блокируете — следовательно HTTP-вызов не отправится.

- Кроме того, переменная `request` не используется → предупреждение/мусор.

- **Решение:** либо `request.subscribe(...)` (асинхронно) либо `request.block()` (блокир.) — но **никакой** 
из этих вариантов не годится, если запрос должен быть гарантированно отправлен _только после_ успешного коммита БД.


---

## 3) Внешний HTTP в транзакции — архитектурная ошибка
- Выполнение долгого/ненадёжного внешнего вызова **в рамках транзакции** опасно: 
если внешняя система получит callback, а затем транзакция откатится — данные рассинхронизируются.

- Также это увеличивает время удержания БД-локов и снижает пропускную способность.

- **Лучше:** отправить callback **после** успешного коммита (AFTER_COMMIT) или использовать _outbox pattern_.


Варианты исправления:

- Использовать `ApplicationEventPublisher` + `@TransactionalEventListener(phase = AFTER_COMMIT)` для отправки callback.

- Или регистрировать `TransactionSynchronization` и отправлять в `afterCommit()`.

- Или сохранять сообщение в таблицу `outbox` в той же транзакции, а внешний воркер затем прочитает и отправит.


Короткий пример с `@TransactionalEventListener` (рекомендуется):
```java
// в сервисе (в транзакции) — сохраняем и публикуем событие
scoreRepository.saveScore(s);
applicationEventPublisher.publishEvent(new ScoreSavedEvent(s));

// listener — срабатывает только после коммита
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onScoreSaved(ScoreSavedEvent ev) {
    webClient.post()...
        .bodyValue(Map.of(ev.getScore().getName(), ev.getScore()))
        .retrieve()
        .toBodilessEntity()
        .subscribe(...);
}
```

---
## 4) WebClient и ObjectMapper создаются прямо в коде

- `WebClient.create()` каждый вызов создаёт клиент без конфигурации (таймауты, baseUrl, соединения). 
Лучше **инжектировать** один настроенный бин `WebClient`/`WebClient.Builder`.

- `new ObjectMapper()` тоже лучше инжектировать — 
чтобы использовать общую конфигурацию (модули, настройки дат и т. д.).

---
## 5) Нет обработки ошибок и ретраев для внешнего вызова

- Что делать, если внешний сервис недоступен? В текущем коде — ничего.

- Добавьте обработку ошибок, лог, retry/circuit-breaker (resilience4j), alerting.

---
## 6) Логика и модель: использование `name` как id

- Использовать `name` как ключ — плохо (неуникально). 
Лучше `candidateId` и `scoreId`.

- Формат тела callback `Map.of(c.getName(), s)` — странная структура: 
ключ = имя, значение = объект с тем же именем. Лучше односторонний DTO `{ name: "...", score: 123 }`.

---
## 7) Thread-safety и прочее

- `ObjectMapper` потокобезопасен после конфигурирования — вполне можно иметь один экземпляр. 
Но лучше инжектировать.

- `scoreRepository.saveScore(s)` — предполагается блокирующая операция; 
если вы используете реактивную СУБД, то подход должен быть реактивным (не `TransactionTemplate`).

---

# Практические исправления — рабочие примеры

## A. Правильный вариант (_рекомендую_): `publish event` → `send after commit` (_асинхронно_)
```java
@Component
public class InterviewService {
  private final ScoreRepository scoreRepository;
  private final TransactionTemplate tx;
  private final ApplicationEventPublisher publisher;

  public void process(Candidate c) {
    tx.execute(status -> {
      Score s = interviewScoreMLService.compute(c);
      scoreRepository.saveScore(s);
      publisher.publishEvent(new ScoreSavedEvent(s));
    });
  }
}

@Component
public class ScoreNotificationListener {
  private final WebClient webClient;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onScoreSaved(ScoreSavedEvent ev) {
    webClient.post()
      .uri("/callback")
      .bodyValue(Map.of("name", ev.getScore().getName(), "score", ev.getScore().getScore()))
      .retrieve()
      .toBodilessEntity()
      .subscribe(
         resp -> log.info("Callback sent"),
         err -> log.error("Callback failed", err)
      );
  }
}
```

Плюсы: callback отправляется **только после** коммита, 
транзакция не блокируется длинными сетевыми операциями, есть четкие границы.

---
## B. Быстрый патч (_если нужно срочно_): блокировать вызов, но **после** сохранения и до `commit` — НЕ РЕКОМЕНДУЮ
```java
tx.execute(status -> {
  Score s = interviewScoreMLService.compute(c);
  scoreRepository.saveScore(s);
  // Блокировать — но опасно: если транзакция потом откатится, внешний уже получил callback
  webClient.post().bodyValue(...).retrieve().toBodilessEntity().block();
});
```

Использовать только если вы понимаете риски (и внешний сервис — транзакционно согласован).

---
## C. Outbox pattern (_надежно для распределённых систем_)

1. В одной транзакции сохраняете `score` и вставляете запись в `outbox` (payload JSON + topic).

2. Вне транзакции отдельный процесс читает `outbox`, шлёт callback и помечает запись как отправленную.  
   Плюс: надёжность, идемпотентность, отслеживаемость.

---
# Дополнительные рекомендации и чек-лист ✅

- Инжектируйте `WebClient` и `ObjectMapper` через Spring.

- Не выполняйте внешние HTTP-вызовы внутри транзакции — используйте AFTER_COMMIT / outbox.

- Обрабатывайте `JsonProcessingException`.

- Добавьте логирование и метрики (success/failure).

- Задайте таймауты, retry, circuit-breaker для внешних вызовов.

- Используйте уникальные идентификаторы (id), а не `name`.

- Покройте флоу тестами: unit-тест + integration test + контрактный тест внешнего callback.

---
# Фишки для запоминания (психологический мемо-трюк) 🧠✨

Запомни правило-мантру: **Compute → Save → Notify (после Commit)**.  
Представь три колонны: 
1) вычисления (compute), 
2) запись в БД (save), 
3) уведомление (notify AFTER commit).

Если колонна 3 упала — можно переотправить;  
если колонна 2 откатилась — уведомления не должны были уйти.

---
