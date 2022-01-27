-- execute as schema owner in database todoapp

CREATE TABLE aufgaben (
    id              INTEGER     NOT NULL PRIMARY KEY,
    bezeichnung     VARCHAR     NOT NULL,
    erstellzeit     TIMESTAMP   NOT NULL,
    erledigt        BOOLEAN     NOT NULL
);

ALTER TABLE aufgaben ADD COLUMN besitzer VARCHAR;
ALTER TABLE aufgaben ADD COLUMN deadline DATE;