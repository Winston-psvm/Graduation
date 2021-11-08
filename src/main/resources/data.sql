INSERT INTO RESTAURANT (title, address, telephone )
VALUES ('Kebab', 'Lithuania', '+375882558899');

INSERT INTO USERS (name, email, password, restaurant_id)
VALUES ('Admin', 'admin@gmail.com', '{noop}admin', 1),
       ('User', 'user@yandex.ru', '{noop}password', null );

INSERT INTO USER_ROLES (role, user_id)
VALUES ('ADMIN', 1),
       ('USER', 1),
       ('USER', 2);

-- INSERT INTO VOICE (user_id, restaurant_id, voice_date_time)
-- VALUES ('2', '1', '2021-01-01 13:22');

INSERT INTO MENU (date, restaurant_id)
VALUES ('2021-10-10', 1),
       ('2021-10-11', 1),
       ('2021-10-12', 1),
       ('2021-10-13', 1),
       ('2021-10-15', 1),
       ('2021-10-14', 1);






