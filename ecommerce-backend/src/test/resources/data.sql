-- password are in the format of : password@<UserLetter>123. unless specified otherwise.
-- password encrypted using https://www.javainuse.com/onlineBcrypt
INSERT INTO "LOCAL_USER" ("EMAIL", "FIRST_NAME", "SECOND_NAME", "PASSWORD", "USERNAME", "EMAIL_VERIFIED")
VALUES ('UserA@Junit.com', 'UserA-FirstName', 'UserA-LastName', '$2a$10$IsNm2KZ/lzivEUgYaBlCB.Y1/nIPa4ojHeKNrxhMy0as0s9Be9ncu', 'UserA', true),
       ('UserB@Junit.com', 'UserB-FirstName', 'UserB-LastName', '$2a$10$IfczO/cgxxu5OFYmFH9EpeswBlpfORuU3cWcFJHLx1bRR9t5VxNQm', 'UserB', false),
       ('UserC@junit.com', 'UserC-FirstName', 'UserC-LastName', '$2a$10$t62Py0b0SAM2/yAdG.bR0uko0XjxcB6kkNIfykVq.O7clQQxifO96', 'UserC', false);

INSERT INTO "ADDRESS"("ADDRESS_LINE_1", "CITY", "COUNTRY", "USER_ID")
VALUES ('123 Tester Hill', 'Testerton', 'England', 1)
     , ('312 Spring Boot', 'Hibernate', 'England', 3);

INSERT INTO "PRODUCT" ("NAME", "SHORT_DESCRIPTION", "LONG_DESCRIPTION", "PRICE")
VALUES ('Product #1', 'Product one short description.', 'This is a very long description of product #1.', 5.50)
     , ('Product #2', 'Product two short description.', 'This is a very long description of product #2.', 10.56)
     , ('Product #3', 'Product three short description.', 'This is a very long description of product #3.', 2.74)
     , ('Product #4', 'Product four short description.', 'This is a very long description of product #4.', 15.69)
     , ('Product #5', 'Product five short description.', 'This is a very long description of product #5.', 42.59);

INSERT INTO "INVENTORY" ("PRODUCT_ID", "QUANTITY")
VALUES (1, 5)
     , (2, 8)
     , (3, 12)
     , (4, 73)
     , (5, 2);

INSERT INTO "WEB_ORDER" ("ADDRESS_ID", "LOCAL_USER_ID")
VALUES (1, 1)
     , (1, 1)
     , (1, 1)
     , (2, 3)
     , (2, 3);

INSERT INTO "WEB_ORDER_QUANTITIES" ("ORDER_ID", "PRODUCT_ID", "QUANTITY")
VALUES (1, 1, 5)
     , (1, 2, 5)
     , (2, 3, 5)
     , (2, 2, 5)
     , (2, 5, 5)
     , (3, 3, 5)
     , (4, 4, 5)
     , (4, 2, 5)
     , (5, 3, 5)
     , (5, 1, 5);