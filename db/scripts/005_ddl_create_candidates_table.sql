create table candidates(
    id serial primary key,
    name varchar not null,
    description varchar not null,
    creation_date timestamp
);