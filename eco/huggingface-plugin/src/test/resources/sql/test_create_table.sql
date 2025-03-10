drop table if exists t_eco_huggingface_model;
drop table if exists t_eco_huggingface_task;

create table if not exists t_eco_huggingface_model
(
    "id"           bigserial primary key                 not null,
    "model_name"   varchar(32)                           not null,
    "model_schema" text                                  not null,
    "task_id"      bigint                                not null,
    "created_at"   timestamp   default current_timestamp not null,
    "created_by"   varchar(32) default 'system'          not null
);
create table if not exists t_eco_huggingface_task
(
    "id"                    bigserial primary key not null,
    "task_name_code"        varchar(32)           not null,
    "task_description_code" varchar(32)           not null,
    "total_model_num"       integer               not null
);