--Вот готовый SQL-скрипт, который создаёт структуру базы cars и наполняет её по 5 тестовых записей в каждую таблицу:

-- Создание базы
CREATE DATABASE cars;

-- Переходим в неё (PostgreSQL)
\c cars;

-- ===============================
-- Схема: mark
-- ===============================
CREATE SCHEMA mark;

CREATE TABLE mark.ford (
    model VARCHAR(255),
    year_of_release INTEGER,
    engine_power FLOAT
);

CREATE TABLE mark.honda (
    model VARCHAR(255),
    year_of_release INTEGER,
    engine_power FLOAT
);

CREATE TABLE mark.bmw (
    model VARCHAR(255),
    year_of_release INTEGER,
    engine_power FLOAT,
    is_there_a_turn_signal BOOLEAN
);

-- ===============================
-- Схема: Designer
-- ===============================
CREATE SCHEMA Designer;

CREATE TABLE Designer.ford (
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    year_of_birth INTEGER
);

CREATE TABLE Designer.bmw (
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    year_of_birth INTEGER
);

-- ===============================
-- Наполнение данными
-- ===============================

-- mark.ford
INSERT INTO mark.ford VALUES
('Focus', 2018, 120.5),
('Mustang', 2020, 450.0),
('Fiesta', 2017, 95.0),
('Explorer', 2019, 300.0),
('Fusion', 2016, 175.0);

-- mark.honda
INSERT INTO mark.honda VALUES
('Civic', 2018, 158.0),
('Accord', 2019, 192.0),
('CR-V', 2020, 190.0),
('Fit', 2017, 130.0),
('Pilot', 2021, 280.0);

-- mark.bmw
INSERT INTO mark.bmw VALUES
('X5', 2020, 335.0, TRUE),
('3 Series', 2019, 255.0, TRUE),
('i8', 2018, 369.0, TRUE),
('M3', 2021, 473.0, TRUE),
('X1', 2017, 228.0, FALSE);

-- Designer.ford
INSERT INTO Designer.ford VALUES
('Henry', 'Ford', 1863),
('Edsel', 'Ford', 1893),
('Carol', 'Smith', 1975),
('John', 'Doe', 1980),
('Emily', 'Johnson', 1990);

-- Designer.bmw
INSERT INTO Designer.bmw VALUES
('Karl', 'Rapp', 1882),
('Franz', 'Popp', 1886),
('Hans', 'Zimmer', 1965),
('Stefan', 'Müller', 1978),
('Anna', 'Schmidt', 1992);
