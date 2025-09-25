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
