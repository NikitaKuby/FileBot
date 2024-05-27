
CREATE TABLE data_sources.numbers (
        id bigserial NOT NULL,
        "number" varchar(255) NULL,
        CONSTRAINT numbers_pkey PRIMARY KEY (id)
);


CREATE TABLE data_sources.users (
                           id int8 NOT NULL,
                           auth bool NULL,
                           command varchar(255) NULL,
                           "name" varchar(255) NULL,
                           "number" varchar(255) NULL,
                           state varchar(255) NULL,
                           CONSTRAINT users_pkey PRIMARY KEY (id)
);