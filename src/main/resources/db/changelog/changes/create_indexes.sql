--liquibase formatted sql
--includeAll path:db/changelog/changes
--changeset admin:3
CREATE INDEX user_birth_date_index ON users(birth_date);
