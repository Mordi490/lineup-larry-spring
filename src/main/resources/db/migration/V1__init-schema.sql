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
        -- TODO: add create_at & last_updated_at
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
        -- TODO: add create_at & last_updated_at
        -- TODO: add patch, patches will eventually include map changes
    );

 CREATE TABLE IF NOT EXISTS
    likes (
        user_id bigint REFERENCES users (id) ON DELETE CASCADE,
        lineup_id bigint REFERENCES lineup (id) ON DELETE CASCADE,
        liked_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
        PRIMARY KEY (user_id, lineup_id)
    );
