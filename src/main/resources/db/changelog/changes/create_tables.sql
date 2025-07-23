--liquibase formatted sql

--changeset admin:1
CREATE TABLE users (
        user_id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        surname VARCHAR(255) NOT NULL,
        email VARCHAR(255) UNIQUE NOT NULL,
        birth_date DATE NOT NULL CHECK (birth_date <= CURRENT_DATE)

);
--rollback DROP TABLE users;

--changeset admin:2
CREATE TABLE card_info (
        card_id SERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
        number VARCHAR(19) NOT NULL UNIQUE,
        holder VARCHAR(255) NOT NULL,
        expiration_date DATE NOT NULL CHECK (expiration_date >= CURRENT_DATE)
);
--rollback DROP TABLE card_info;