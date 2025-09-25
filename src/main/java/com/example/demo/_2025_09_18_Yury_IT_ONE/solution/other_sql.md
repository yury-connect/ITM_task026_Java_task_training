## Вот набор SQL-запросов для метасервиса — они позволяют получать всю нужную информацию о БД, схемах, таблицах и колонках.

1. Получить все схемы в базе
```sql
SELECT schema_name
FROM information_schema.schemata
WHERE schema_name NOT IN ('pg_catalog', 'information_schema');
```

2. Получить все таблицы в определённой схеме
```sql
SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_schema = 'mark';
```

3. Получить все колонки таблицы
```sql
SELECT table_schema, table_name, column_name, data_type
FROM information_schema.columns
WHERE table_schema = 'mark'
  AND table_name = 'bmw';
```

4. Получить все таблицы и колонки (универсальный поиск по метаданным)
```sql
SELECT 
    c.table_schema,
    c.table_name,
    c.column_name,
    c.data_type
FROM information_schema.columns c
JOIN information_schema.tables t 
    ON c.table_name = t.table_name 
   AND c.table_schema = t.table_schema
WHERE c.table_schema NOT IN ('pg_catalog', 'information_schema')
ORDER BY c.table_schema, c.table_name;
```

5. Поиск по имени колонки/таблицы (универсальный endpoint)
Например, найти все, где встречается слово year:
```sql
SELECT 
    c.table_schema,
    c.table_name,
    c.column_name,
    c.data_type
FROM information_schema.columns c
WHERE c.column_name ILIKE '%year%'
   OR c.table_name ILIKE '%year%';
```

---
Схема хранения метаданных (`БД` → `схема` → `таблица` → `колонка`) с поддержкой историчности.

## 📂 Структура таблиц для метаданных
```sql
-- Схема для метаданных
CREATE SCHEMA meta;

-- =============================
-- Таблица: базы данных
-- =============================
CREATE TABLE meta.databases (
    id SERIAL PRIMARY KEY,
    db_name VARCHAR(255) NOT NULL,
    description TEXT,
    collected_at TIMESTAMP DEFAULT now()
);

-- =============================
-- Таблица: схемы
-- =============================
CREATE TABLE meta.schemas (
    id SERIAL PRIMARY KEY,
    database_id INT REFERENCES meta.databases(id) ON DELETE CASCADE,
    schema_name VARCHAR(255) NOT NULL,
    description TEXT,
    collected_at TIMESTAMP DEFAULT now()
);

-- =============================
-- Таблица: таблицы
-- =============================
CREATE TABLE meta.tables (
    id SERIAL PRIMARY KEY,
    schema_id INT REFERENCES meta.schemas(id) ON DELETE CASCADE,
    table_name VARCHAR(255) NOT NULL,
    description TEXT,
    collected_at TIMESTAMP DEFAULT now()
);

-- =============================
-- Таблица: колонки
-- =============================
CREATE TABLE meta.columns (
    id SERIAL PRIMARY KEY,
    table_id INT REFERENCES meta.tables(id) ON DELETE CASCADE,
    column_name VARCHAR(255) NOT NULL,
    data_type VARCHAR(255) NOT NULL,
    description TEXT,
    collected_at TIMESTAMP DEFAULT now()
);

```

### 📌 Логика
- `meta.databases` — хранит список баз (`cars`, `books` и т.д.);
- `meta.schemas` — хранит схемы внутри БД (`mark`, `Designer` и др.);
- `meta.tables` — хранит таблицы внутри схем (`ford`, `bmw` и т.д.);
- `meta.columns` — хранит колонки каждой таблицы с типами.

👉 `collected_at` нужен для **историчности** — можно хранить слепки в разные моменты времени и анализировать изменения.

---
## 📊 Пример наполнения метаданных (для твоей БД cars)
```sql
-- 1. Добавляем базу
INSERT INTO meta.databases (db_name, description) 
VALUES ('cars', 'База данных автомобилей и их дизайнеров');

-- 2. Добавляем схемы
INSERT INTO meta.schemas (database_id, schema_name, description)
VALUES 
(1, 'mark', 'Марки автомобилей'),
(1, 'Designer', 'Дизайнеры автомобилей');

-- 3. Добавляем таблицы
INSERT INTO meta.tables (schema_id, table_name, description)
VALUES
(1, 'ford', 'Автомобили марки Ford'),
(1, 'honda', 'Автомобили марки Honda'),
(1, 'bmw', 'Автомобили марки BMW'),
(2, 'ford', 'Дизайнеры Ford'),
(2, 'bmw', 'Дизайнеры BMW');

-- 4. Добавляем колонки (пример для mark.bmw)
INSERT INTO meta.columns (table_id, column_name, data_type, description)
VALUES
(3, 'model', 'varchar(255)', 'Модель автомобиля'),
(3, 'year_of_release', 'integer', 'Год выпуска'),
(3, 'engine_power', 'float', 'Мощность двигателя'),
(3, 'is_there_a_turn_signal', 'boolean', 'Наличие поворотника');
```
⚡ Теперь у тебя есть универсальная модель хранения метаданных, которая:
- Поддерживает разные БД;
- Легко расширяется;
- Сохраняет историю изменений.

---
## 📌 SQL-запрос: БД → схема → таблица → колонка
```sql
SELECT 
    d.db_name      AS database_name,
    s.schema_name  AS schema_name,
    t.table_name   AS table_name,
    c.column_name  AS column_name,
    c.data_type    AS column_type,
    COALESCE(c.description, '') AS column_description,
    c.collected_at AS collected_at
FROM meta.databases d
JOIN meta.schemas s ON s.database_id = d.id
JOIN meta.tables t  ON t.schema_id   = s.id
JOIN meta.columns c ON c.table_id    = t.id
ORDER BY d.db_name, s.schema_name, t.table_name, c.column_name;
```

## 🔎 Что вернёт
Пример результата для твоей базы cars:

| database_name | schema_name | table_name | column_name            | column_type  | column_description  | collected_at        |
|:--------------|:------------|:-----------|:-----------------------|:-------------|:--------------------|:--------------------|
| cars          | mark        | bmw        | model                  | varchar(255) | Модель автомобиля   | 2025-09-25 11:00:00 |
| cars          | mark        | bmw        | year_of_release        | integer      | Год выпуска         | 2025-09-25 11:00:00 |
| cars          | mark        | bmw        | engine_power           | float        | Мощность двигателя  | 2025-09-25 11:00:00 |
| cars          | mark        | bmw        | is_there_a_turn_signal | boolean      | Наличие поворотника | 2025-09-25 11:00:00 |
| cars          | Designer    | ford       | first_name             | varchar(255) | Имя дизайнера       | 2025-09-25 11:00:00 |

---
