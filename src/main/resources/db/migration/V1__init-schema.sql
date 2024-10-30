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
        -- TODO: add created_at
    );

CREATE TABLE IF NOT EXISTS
    lineup (
        id bigserial not null,
        agent agent,
        map map,
        title text not null,
        body text not null,
        user_id bigint REFERENCES users (id) ON DELETE CASCADE not null,
        created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
        updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
        primary key (id)
    );

    -- trigger to update the "updated_at" field
    CREATE OR REPLACE FUNCTION update_last_updated_at()
    RETURNS TRIGGER AS $$
    BEGIN
        NEW.updated_at = NOW();
        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;

    CREATE TRIGGER set_updated_at
    BEFORE UPDATE ON lineup
    FOR EACH ROW
    EXECUTE FUNCTION update_last_updated_at();

 CREATE TABLE IF NOT EXISTS
    likes (
        user_id bigint REFERENCES users (id) ON DELETE CASCADE,
        lineup_id bigint REFERENCES lineup (id) ON DELETE CASCADE,
        created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
        PRIMARY KEY (user_id, lineup_id)
    );


    -- TODO: groups

    -- TODO: comments
