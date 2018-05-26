CREATE TABLE event(
    id            SERIAL        PRIMARY KEY,
    title         TEXT          NOT NULL,
    description   TEXT,
    event_start   TIMESTAMP     NOT NULL,
    event_end     TIMESTAMP     NOT NULL
);

INSERT INTO event (title, description, event_start, event_end) VALUES
    ('Bank Holiday', 'Enjoy Sunshine!', '2018-05-01 00:00:00', '2018-05-01 23:59:59'),
    ('Last Day of May', 'Happy End!', '2018-05-31 00:00:00', '2018-05-31 23:59:59');