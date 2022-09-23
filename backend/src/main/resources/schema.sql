CREATE TABLE activities
(
    strava_id   BIGINT UNIQUE,
    id          BIGINT IDENTITY NOT NULL PRIMARY KEY,
    date        DATE         NOT NULL,
    sport       VARCHAR(32)  NOT NULL,
    description VARCHAR(128) NOT NULL,
    time        INTEGER      NOT NULL,
    rege_time   INTEGER,
    hr          INTEGER,
    hr_max      INTEGER,
    cadence     INTEGER,
    power       INTEGER,
    ef          DOUBLE,
    tss         DOUBLE,
    effort      INTEGER,
    elevation   INTEGER,
    speed       DOUBLE,
    distance    DOUBLE,
    notes       VARCHAR(1024),
);

CREATE TABLE people  (
                         person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
                         first_name VARCHAR(20),
                         last_name VARCHAR(20)
);