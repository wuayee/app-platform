create table if not exists conversation_record
(
    id          bigint not null
        primary key,
    app_id      varchar(255),
    question    text,
    answer      text,
    create_user varchar(255),
    create_time timestamp,
    finish_time timestamp,
    instance_id varchar(255)
);
