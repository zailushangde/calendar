CREATE TABLE event(
    id            SERIAL        PRIMARY KEY,
    title         TEXT          NOT NULL,
    description   TEXT,
    event_start   TIMESTAMP     NOT NULL,
    event_end     TIMESTAMP     NOT NULL
);