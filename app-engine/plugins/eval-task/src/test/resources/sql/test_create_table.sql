drop table if exists t_app_engine_eval_task;

create table if not exists t_app_engine_eval_task
(
    "id"               bigserial primary key                 not null,
    "name"             varchar(64)                           not null,
    "description"      varchar(512)                          not null,
    "status"           varchar(32)                           not null,
    "created_at"       timestamp   default current_timestamp not null,
    "updated_at"       timestamp   default current_timestamp not null,
    "created_by"       varchar(64) default 'system'          not null,
    "updated_by"       varchar(64) default 'system'          not null,
    "app_id"           varchar(255)                          not null,
    "workflow_id"      varchar(32)                           not null
);
