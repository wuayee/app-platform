drop table if exists t_test;

create table t_test
(
    "id"         bigserial primary key not null,
    "created_at" timestamp             not null,
    "updated_at" timestamp             not null,
    "created_by" varchar(10)           not null,
    "updated_by" varchar(10)           not null
);