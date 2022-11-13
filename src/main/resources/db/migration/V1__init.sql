CREATE EXTENSION IF NOT EXISTS citext;

create table bot_users
(
    id                  uuid        PRIMARY KEY,
    user_telegram_id    bigint      NOT NULL,
    chat_telegram_id    bigint      NOT NULL,
    bot_settings        citext,
    last_update         timestamp   NOT NULL
);


CREATE UNIQUE INDEX telegram_id ON bot_users (user_telegram_id);