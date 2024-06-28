DELETE FROM users;
DELETE FROM lineup;

INSERT INTO users(id, username) VALUES
    (1, 'userOne'),
    (2, 'userTwo'),
    (3, 'userThree'),
    (4, 'userFour'),
    (5, 'userFive');

INSERT INTO lineup(id, title, body, user_id) VALUES
    (1, 'lineupOne', 'bodyOne', 1),
    (2, 'lineupTwo', 'bodyTwo', 2),
    (3, 'lineupThree', 'bodyThree', 2),
    (4, 'lineupFour', 'bodyFour', 3);

ALTER SEQUENCE users_id_seq RESTART WITH 101;
ALTER SEQUENCE lineup_id_seq RESTART WITH 101;
