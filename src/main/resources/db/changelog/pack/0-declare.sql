
CREATE TABLE data_sources.numbers (
        id bigserial NOT NULL,
        "number" varchar(255) NULL,
        CONSTRAINT numbers_pkey PRIMARY KEY (id)
);


CREATE TABLE data_sources.users (
                           id int8 NOT NULL,
                           auth boolean NULL,
                           command varchar(255) NULL,
                           "name" varchar(255) NULL,
                           "number" varchar(255) NULL,
                           state varchar(255) NULL,
                           CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE data_sources."document"
(
    id        varchar(255) NOT NULL,
    "name"    varchar(255) NULL,
    "size"    varchar(255) NULL,
    unique_id varchar(255) NULL,
    user_id   int8          NULL,
    CONSTRAINT document_pkey PRIMARY KEY (id),
    CONSTRAINT whatisit FOREIGN KEY (user_id) REFERENCES data_sources.users (id)
);