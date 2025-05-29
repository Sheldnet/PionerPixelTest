CREATE TABLE IF NOT EXISTS "user"
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(500) NOT NULL,
    date_of_birth DATE         NOT NULL,
    password      VARCHAR(500) NOT NULL
    );

CREATE TABLE IF NOT EXISTS account
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT      NOT NULL UNIQUE,
    balance NUMERIC(19, 2) DEFAULT 0 CHECK (balance >= 0),
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES "user"(id)
    );

CREATE TABLE IF NOT EXISTS email_data
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT      NOT NULL,
    email   VARCHAR(200) NOT NULL UNIQUE,
    CONSTRAINT fk_email_user FOREIGN KEY (user_id) REFERENCES "user"(id)
    );

CREATE TABLE IF NOT EXISTS phone_data
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT      NOT NULL,
    phone   VARCHAR(13)  NOT NULL UNIQUE,
    CONSTRAINT fk_phone_user FOREIGN KEY (user_id) REFERENCES "user"(id)
    );