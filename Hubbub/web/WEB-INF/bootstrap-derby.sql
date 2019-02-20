-- WIPE OLD DATA AND START FRESH
-- HARMLESS ERROR MESSAGE IF THE TABLES DON'T ALREADY EXIST
-- DERBY DOESN'T HAVE THE 'IF EXISTS' CLAUSE
DROP TABLE following;
DROP TABLE profiles;
DROP TABLE posts;
DROP TABLE users;

CREATE TABLE users (
    username   VARCHAR(12) NOT NULL,
    password   VARCHAR(100) NOT NULL,
    joined     DATE        NOT NULL DEFAULT CURRENT DATE,
    CONSTRAINT pk_users PRIMARY KEY (username)
);

CREATE TABLE posts (
    author  VARCHAR(12) NOT NULL,
    content VARCHAR(255) NOT NULL,
    posted  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id      INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
    CONSTRAINT pk_posts PRIMARY KEY (id),
    CONSTRAINT fk_post_author FOREIGN KEY (author) REFERENCES users(username)
);

CREATE TABLE profiles (
    owner VARCHAR(12) NOT NULL,
    firstname VARCHAR(20),
    lastname VARCHAR(30),
    email VARCHAR(100),
    biography VARCHAR(255),
    avatar BLOB(200K),
    mime VARCHAR(30),
    id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
    CONSTRAINT pk_profiles PRIMARY KEY (id),
    CONSTRAINT fk_profile_owner FOREIGN KEY (owner) REFERENCES users(username)
);

CREATE TABLE following (
    follower VARCHAR(12) NOT NULL,
    followee VARCHAR(12) NOT NULL,
    CONSTRAINT pk_following PRIMARY KEY (follower,followee),
    CONSTRAINT fk_follower_user FOREIGN KEY (follower) REFERENCES users(username),
    CONSTRAINT fk_followee_user FOREIGN KEY (followee) REFERENCES users(username)
);

-- POPULATE THE TABLES WITH SOME INITIAL DATA

INSERT INTO USERS VALUES
	--('johndoe',  'P@ssw0rd', '2017-05-09'),
	--('jilljack', 'P@ssw0rd', '2017-06-03'),
	--('janedoe',  'P@ssw0rd', '2018-01-13');
        ('johndoe', '$5$3tnEGA07$6QF98Qtf6BMGRh/09sIEOk1Xrgt8X2.GwH1xgl4ftB0', '2017-05-09'),
        ('jilljack', '$5$3tnEGA07$6QF98Qtf6BMGRh/09sIEOk1Xrgt8X2.GwH1xgl4ftB0', '2017-06-03'),
        ('janedoe', '$5$3tnEGA07$6QF98Qtf6BMGRh/09sIEOk1Xrgt8X2.GwH1xgl4ftB0', '2018-01-13');
	
INSERT INTO POSTS (author,content,posted) VALUES
	('johndoe',  'My first Hubbub post! #JavaRules #J2EERocks', '2017-05-09 08:23:47.110'),
	('johndoe',  'I''ve invited Jill to join.', '2017-06-02 19:00:05.965'),
	('jilljack', 'Glad to join the team, johndoe.', '2017-06-03 13:44:34.376'),
	('johndoe',  'Let''s recruit more friends, Jill. #DownWithPython', '2017-11-29 02:51:18.656'),
	('jilljack', 'I''ll reach out to Jane. #DownWithPython', '2017-11-29 07:03:05.123'),
	('janedoe',  'Alright guys, I''ve signed up. Now what?', '2018-01-13 6:30:45.888');

INSERT INTO PROFILES (owner,firstname,lastname,email,biography) VALUES 
        ('johndoe', 'John', 'Doe', 'johndoe@morgue.info', NULL),
        ('janedoe', 'Jane', 'Doe', NULL, 'I sometimes hang with John Doe'),
        ('jilljack', NULL, NULL, 'jilljack@pailowater.net', NULL);

INSERT INTO FOLLOWING VALUES
        ('johndoe','janedoe'),
        ('johndoe','jilljack'),
        ('janedoe','johndoe'),
        ('jilljack','janedoe');
