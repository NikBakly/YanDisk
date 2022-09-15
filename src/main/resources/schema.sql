CREATE TABLE IF not exists items
(
    id        VARCHAR UNIQUE              NOT NULL,
    type      VARCHAR(6)                  NOT NULL,
    size      INT,
    url       VARCHAR,
    parent_id VARCHAR,
    date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT FK_ITEM_ON_PARENT FOREIGN KEY (parent_id) REFERENCES items (id),
    CHECK ( type = 'FOLDER' OR type = 'FILE')
);

CREATE TABLE IF not exists items_history
(
    id_history BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    id         VARCHAR                                 NOT NULL,
    type       VARCHAR(6)                              NOT NULL,
    size       INT,
    url        VARCHAR,
    parent_id  VARCHAR,
    date       TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_item_history PRIMARY KEY (id_history),
    CHECK ( type = 'FOLDER' OR type = 'FILE')
);