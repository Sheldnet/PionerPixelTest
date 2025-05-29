INSERT INTO "user" (id, name, date_of_birth, password)
VALUES (100, 'Alice', '1990-01-01', '$2a$10$3yBt5Lkm7ZmFxA7cVQmkeu9Cj/WakzOHuF7zk9zglT7CO51u6S7uS'); -- пароль: 123

INSERT INTO "user" (id, name, date_of_birth, password)
VALUES (101, 'Bob', '1985-06-15', '$2a$10$3yBt5Lkm7ZmFxA7cVQmkeu9Cj/WakzOHuF7zk9zglT7CO51u6S7uS'); -- пароль: 123

INSERT INTO email_data (user_id, email) VALUES (100, 'alice@example.com');
INSERT INTO email_data (user_id, email) VALUES (101, 'bob@example.com');

INSERT INTO phone_data (user_id, phone) VALUES (100, '+10000000000');
INSERT INTO phone_data (user_id, phone) VALUES (101, '+20000000000');

INSERT INTO account (user_id, balance) VALUES (100, 1000);
INSERT INTO account (user_id, balance) VALUES (101, 2000);
