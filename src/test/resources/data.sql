MERGE INTO genres (id, name)
    VALUES ( 1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

MERGE INTO MPA (id, name, description)
    VALUES (1, 'G', 'у фильма нет возрастных ограничений'),
           (2, 'PG', 'детям рекомендуется смотреть фильм с родителями'),
           (3, 'PG-13', 'детям до 13 лет просмотр не желателен'),
           (4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
           (5, 'NC-17', 'лицам до 18 лет просмотр запрещён');

INSERT INTO users (name, login, email, birthday)
VALUES ( 'testName1', 'testLogin1', 'test1@test.com', '2000-5-14' );