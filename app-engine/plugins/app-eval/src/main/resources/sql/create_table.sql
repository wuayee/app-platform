create table if not exists eval_dataset
(
    id           bigserial
        primary key,
    dataset_name varchar(256) not null,
    description  varchar(256),
    author       varchar(64)  not null,
    app_id       varchar(256) not null,
    create_time  timestamp    not null,
    modify_time  timestamp    not null
);

create table if not exists eval_data
(
    id          bigserial
        primary key,
    dataset_id  bigserial
        constraint eval_data_eval_dataset_id_fk
            references eval_dataset
            on update cascade on delete cascade,
    create_time timestamp not null,
    modify_time timestamp not null,
    input       text      not null,
    output      text      not null
);

create table if not exists eval_task
(
    id            bigserial
        constraint eval_task_pk
            primary key,
    app_id        varchar(256)     not null,
    author        varchar(64)      not null,
    algorithm_id  varchar(64)      not null,
    pass_score    double precision not null,
    create_time   timestamp        not null,
    status        integer          not null,
    start_node_id varchar(64)      not null,
    end_node_id   varchar(64)      not null,
    version       varchar(64)      not null,
    pass_rate     double precision not null,
    start_time    timestamp,
    finish_time   timestamp
);

create table if not exists eval_report
(
    id              bigserial
        constraint eval_report_pk
            primary key,
    instance_id     varchar(256)
                    constraint eval_report_pk2
                    unique,
    score           double precision,
    meta            varchar(256),
    eval_task_id    bigint not null
        constraint eval_report_eval_task_id_fk
            references eval_task
            on update cascade on delete cascade,
    input           text   not null,
    expected_output text   not null,
    output          text,
    start_time      timestamp,
    end_time        timestamp
);

create table if not exists eval_report_trace
(
    id        bigserial
        constraint eval_trace_pk
            primary key,
    instance_id varchar(256) not null,
    node_id   varchar(64)    not null,
    input     text           not null,
    output    text           not null,
    time      timestamp      not null,
    latency   bigint         not null
);

create table if not exists eval_task_dataset
(
    eval_task_id    bigint not null,
    eval_dataset_id bigint not null,
    constraint eval_task_dataset_pk
        primary key (eval_dataset_id, eval_task_id)
);


