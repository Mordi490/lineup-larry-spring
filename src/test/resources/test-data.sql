TRUNCATE users, lineup;

INSERT INTO users(id, username) VALUES
    (1, 'userOne'),
    (2, 'userTwo'),
    (3, 'userThree'),
    (4, 'userFour'),
    (5, 'userFive');

INSERT INTO lineup(id, agent, map, title, body, user_id) VALUES
    (1, 'SOVA', 'ASCENT', 'lineupOne', 'bodyOne', 1),
    (2, 'SOVA', 'ASCENT', 'lineupTwo', 'bodyTwo', 2),
    (3, 'BRIMSTONE', 'BIND', 'lineupThree', 'bodyThree', 2),
    (4, 'CYPHER', 'SUNSET', 'lineupFour', 'bodyFour', 3),
    (5, 'KILLJOY', 'ICEBOX', 'same name', 'bodyFour', 3),
    (6, 'KILLJOY', 'ICEBOX', 'same name', 'bodyFour', 3),
    (7, 'CHAMBER', 'SPLIT', 'awp crutch', 'filler text here', 3),
    (8, 'BREACH', 'FRACTURE', 'some flash', 'even more filler text here', 1),
    (9, 'YORU', 'HAVEN', 'teleport thingy', 'good for post plant', 2),
    (10, 'PHOENIX', 'LOTUS', 'cheeky flash', 'then click heads', 1),
    (11, 'SKYE', 'SPLIT', 'sick pop flash', 'then dog', 3),
    (12, 'VYSE', 'BREEZE', 'click heads', 'just click the head', 3),
    (13, 'OMEN', 'SUNSET', 'titleFour', 'bodyFour', 3),
    (14, 'VIPER', 'SPLIT', 'titleFour', 'bodyFour', 3),
    (15, 'SAGE', 'ICEBOX', 'titleFour', 'bodyFour', 3),
    (16, 'RAZE', 'BREEZE', 'titleFour', 'bodyFour', 3),
    (17, 'ASTRA', 'ICEBOX', 'titleFour', 'bodyFour', 3),
    (18, 'KAYO', 'ASCENT', 'titleFour', 'bodyFour', 3),
    (19, 'NEON', 'FRACTURE', 'titleFour', 'bodyFour', 3),
    (20, 'FADE', 'LOTUS', 'titleFour', 'bodyFour', 3),
    (21, 'HARBOR', 'FRACTURE', 'titleFour', 'bodyFour', 3),
    (22, 'GEKKO', 'BIND', 'titleFour', 'bodyFour', 3),
    (23, 'DEADLOCK', 'FRACTURE', 'titleFour', 'bodyFour', 3),
    (24, 'ISO', 'BIND', 'titleFour', 'bodyFour', 3),
    (25, 'CLOVE', 'BIND', 'titleFour', 'bodyFour', 3);

ALTER SEQUENCE users_id_seq RESTART WITH 101;
ALTER SEQUENCE lineup_id_seq RESTART WITH 101;
