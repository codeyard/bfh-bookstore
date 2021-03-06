CREATE TABLE BOOK
(
    ISBN             VARCHAR       NOT NULL,
    TITLE            VARCHAR       NOT NULL,
    SUBTITLE         VARCHAR,
    AUTHORS          VARCHAR       NOT NULL,
    PUBLISHER        VARCHAR       NOT NULL,
    PUBLICATION_YEAR INTEGER,
    NUMBER_OF_PAGES  INTEGER,
    DESCRIPTION      VARCHAR,
    IMAGE_URL        VARCHAR,
    PRICE            NUMERIC(5, 2) NOT NULL,
    PRIMARY KEY (ISBN)
);
