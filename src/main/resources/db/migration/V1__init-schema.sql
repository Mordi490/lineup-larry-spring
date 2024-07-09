CREATE TABLE IF NOT EXISTS
    users (
        id bigserial not null,
        username text not null,
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS
    lineup (
        id bigserial not null,
        title text not null,
        body text not null,
        user_id bigserial REFERENCES users (id) not null,
        primary key (id)
    );
