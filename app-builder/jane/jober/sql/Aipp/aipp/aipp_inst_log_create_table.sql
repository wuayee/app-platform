create table if not exists aipp_instance_log
(
    log_id                 bigserial not null primary key,
    aipp_id                varchar(255) not null,
    version                varchar(255),
    aipp_type              varchar(64),
    instance_id            varchar(255) not null,
    log_data               json not null,
    log_type               varchar(64) not null,
    create_at              timestamp default current_timestamp,
    create_by              varchar(64) not null,
    reserve                json
);