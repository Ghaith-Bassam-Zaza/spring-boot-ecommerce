-- password are in the format of : Password@<UserLetter>123. unless specified otherwise.
-- password encrypted using https://www.javainuse.com/onlineBcrypt
INSERT INTO local_user (email, first_name, last_name, password, username, email_verified)
    VALUES('UserA@Junit.com','UserA-FirstName','UserA-LastName','$2a$10$IsNm2KZ/lzivEUgYaBlCB.Y1/nIPa4ojHeKNrxhMy0as0s9Be9ncu','UserA',true)
        , ('UserB@Junit.com','UserB-FirstName','UserB-LastName','$2a$10$IfczO/cgxxu5OFYmFH9EpeswBlpfORuU3cWcFJHLx1bRR9t5VxNQm','UserB',false)
