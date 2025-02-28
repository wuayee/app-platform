create table if not exists aipp_instance_file
(
    file_id    bigserial    not null primary key,
    aipp_id    varchar(255) not null,
    filename   varchar(255) not null,
    create_at  timestamp    not null default current_timestamp,
    create_by  varchar(64)  not null
);