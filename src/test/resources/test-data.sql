TRUNCATE users, lineup, likes;

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
    (21, 'HARBOR', 'FRACTURE', 'titleFour', 'bodyFour', 3);

   -- some values with a non-default createdAt and updatedAt
   INSERT INTO lineup(id, agent, map, title, body, user_id, created_at, updated_at) VALUES
       (22, 'GEKKO', 'BIND', 'titleFour', 'bodyFour', 3, '2003-11-28 19:05:46+00', '2077-11-28 19:05:46+00'),
       (23, 'DEADLOCK', 'FRACTURE', 'titleFour', 'bodyFour', 3, '2004-07-04 18:09:55+00', '2077-07-04 18:09:55+00'),
       (24, 'ISO', 'BIND', 'titleFour', 'bodyFour', 3, '2005-06-14 22:07:28+00', '2077-06-14 22:07:28+00'),
       (25, 'CLOVE', 'BIND', 'titleFour', 'bodyFour', 3, '2006-04-10 14:01:42+00', '2077-04-10 14:01:42+00'),
       (26, 'JETT', 'FRACTURE', 'titleFour', 'bodyFour', 3, '2007-03-04 04:02:44+00', '2077-03-04 04:02:44+00');

 INSERT INTO likes(user_id, lineup_id, created_at) VALUES
    (1, 2, '2023-01-15 14:23:00+01'),
    (1, 3, '2023-02-10 08:45:30+01'),
    (1, 11, '2023-03-05 18:12:15+01'),
    (1, 22, '2023-04-20 11:07:45+01'),
    (1, 18, '2023-05-18 21:30:20+01'),
    (1, 16, '2023-06-12 03:55:10+01'),
    (2, 2, '2023-07-07 17:25:00+01'),
    (2, 1, '2023-08-22 09:10:05+01'),
    (2, 22, '2023-09-16 14:42:00+01'),
    (2, 23, '2023-10-03 06:35:40+01'),
    (2, 12, '2023-11-12 15:15:30+01'),
    (2, 14, '2023-12-08 23:50:10+01'),
    (3, 4, '2024-01-22 08:05:20+01'),
    (3, 22, '2024-02-13 13:25:00+01'),
    (3, 15, '2024-03-10 04:45:30+01'),
    (3, 9, '2024-04-05 16:10:10+01'),
    (3, 20, '2024-05-02 19:30:50+01'),
    (3, 1, '2024-06-15 10:55:25+01'),

    (2, 20, '2024-06-15 10:55:25+01'),
    (4, 9, '2024-06-15 10:55:25+01'),
    (4, 22 ,'2024-06-15 10:55:25+01');

ALTER SEQUENCE users_id_seq RESTART WITH 101;
ALTER SEQUENCE lineup_id_seq RESTART WITH 101;
