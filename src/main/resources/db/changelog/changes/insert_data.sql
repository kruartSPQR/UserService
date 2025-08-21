--liquibase formatted sql

--changeset admin:4
INSERT INTO users (name, surname, email, birth_date) VALUES
                                                         ('Ivan', 'Ivanov', 'ivan.ivanov@example.com', '1990-01-01'),
                                                         ('Petr', 'Petrov', 'petr.petrov@example.com', '1985-05-10'),
                                                         ('Sidor', 'Sidorov', 'sidor.sidorov@example.com', '2000-07-15');

--changeset admin:5
INSERT INTO card_info (user_id, number, holder, expiration_date) VALUES
                                                                     ((SELECT user_id FROM users WHERE email='ivan.ivanov@example.com'), '1111-2222-3333-4444', 'Ivan Ivanov', '2027-12-31'),

                                                                     ((SELECT user_id FROM users WHERE email='petr.petrov@example.com'), '5555-6666-7777-8888', 'Petr Petrov', '2026-11-30'),
                                                                     ((SELECT user_id FROM users WHERE email='petr.petrov@example.com'), '9999-0000-1111-2222', 'Petr Petrov', '2028-06-30'),

                                                                     ((SELECT user_id FROM users WHERE email='sidor.sidorov@example.com'), '3333-4444-5555-6666', 'Sidor Sidorov', '2027-05-31'),
                                                                     ((SELECT user_id FROM users WHERE email='sidor.sidorov@example.com'), '7777-8888-9999-0000', 'Sidor Sidorov', '2029-03-31'),
                                                                     ((SELECT user_id FROM users WHERE email='sidor.sidorov@example.com'), '1234-5678-9012-3456', 'Sidor Sidorov', '2030-08-31');

--rollback DELETE FROM card_info WHERE number IN ('1111-2222-3333-4444', '5555-6666-7777-8888', '9999-0000-1111-2222', '3333-4444-5555-6666', '7777-8888-9999-0000', '1234-5678-9012-3456');
--rollback DELETE FROM users WHERE email IN ('ivan.ivanov@example.com', 'petr.petrov@example.com', 'sidor.sidorov@example.com');
