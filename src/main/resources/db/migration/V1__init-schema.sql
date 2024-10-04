CREATE TYPE agent AS ENUM (
    'ASTRA',
    'BREACH',
    'BRIMSTONE',
    'CHAMBER',
    'CYPHER',
    'DEADLOCK',
    'FADE',
    'GEKKO',
    'HARBOR',
    'JETT',
    'KAYO',
    'KILLJOY',
    'NEON',
    'OMEN',
    'PHOENIX',
    'RAZE',
    'REYNA',
    'SAGE',
    'SKYE',
    'SOVA',
    'VIPER',
    'YORU',
    'ISO',
    'CLOVE',
    'VYSE'
);

CREATE TYPE map AS ENUM (
    'ASCENT',
    'BIND',
    'BREEZE',
    'FRACTURE',
    'HAVEN',
    'ICEBOX',
    'LOTUS',
    'PEARL',
    'SPLIT',
    'SUNSET',
    'ABYSS'
);

CREATE TABLE IF NOT EXISTS
    users (
        id bigserial not null,
        username text not null,
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS
    lineup (
        id bigserial not null,
        agent agent,
        map map,
        title text not null,
        body text not null,
        user_id bigint REFERENCES users (id) ON DELETE CASCADE not null,
        primary key (id)
    );