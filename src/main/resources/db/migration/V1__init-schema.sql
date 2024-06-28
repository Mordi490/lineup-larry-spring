CREATE TABLE IF NOT EXISTS
    users (
        id bigserial not null,
        username text not null,
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS
    lineup (
        id bigserial not null,
        title text,
        body text,
        user_id bigserial REFERENCES users (id) not null,
        primary key (id)
    );

-- start the sequence for dummy data
-- ALTER SEQUENCE user_preferences_id_seq RESTART WITH 101;
