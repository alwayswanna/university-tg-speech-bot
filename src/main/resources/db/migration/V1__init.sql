CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE bot_users
(
    id                  uuid        PRIMARY KEY,
    user_telegram_id    bigint      NOT NULL,
    chat_telegram_id    bigint      NOT NULL,
    bot_settings        citext,
    last_update         timestamp   NOT NULL,
    is_ready       boolean     NOT NULL
);

CREATE UNIQUE INDEX telegram_id ON bot_users (user_telegram_id);

CREATE TABLE iam_token
(
    id              uuid        PRIMARY KEY,
    token           citext      NOT NULL,
    create_date     timestamp   NOT NULL,
    expired_date    timestamp
);
