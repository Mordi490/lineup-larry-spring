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
    (6, 'KILLJOY', 'ICEBOX', 'same name', 'bodyFour', 3);

ALTER SEQUENCE users_id_seq RESTART WITH 101;
ALTER SEQUENCE lineup_id_seq RESTART WITH 101;
