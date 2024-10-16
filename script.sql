DROP TABLE IF EXISTS users;

create table USERS
(
    ID         UUID                    not null,
    NAME       CHARACTER VARYING(200)  not null,
    EMAIL      CHARACTER VARYING(200)  not null
        constraint UQ_USERS_EMAIL
            unique,
    PASSWORD   CHARACTER VARYING(200)  not null,
    CREATED_AT TIMESTAMP               not null,
    UPDATED_AT TIMESTAMP               not null,
    LAST_LOGIN TIMESTAMP,
    TOKEN      CHARACTER VARYING(1000) not null,
    ACTIVE     BOOLEAN                 not null,
    constraint PK_USERS
        primary key (ID)
);

DROP TABLE IF EXISTS phones;

create table PHONES
(
    ID           INTEGER               not null,
    USERS_ID     UUID                  not null,
    NUMBER       CHARACTER VARYING(20) not null,
    CITY_CODE    CHARACTER VARYING(5)  not null,
    COUNTRY_CODE CHARACTER VARYING(5),
    CREATED_AT   TIMESTAMP             not null,
    UPDATED_AT   TIMESTAMP,
    constraint PK_PHONES
        primary key (ID),
    constraint FK_PHONES_USERS_ID
        foreign key (USERS_ID) references USERS
);
