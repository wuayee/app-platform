drop table if exists t_app_engine_eval_data;

create table t_app_engine_eval_data
(
    "id"              bigserial primary key                   not null,
    "content"         text                                    not null,
    "created_version" bigint                                  not null,
    "expired_version" bigint      default 9223372036854775807 not null,
    "created_at"      timestamp   default current_timestamp   not null,
    "updated_at"      timestamp   default current_timestamp   not null,
    "created_by"      varchar(10) default 'system'            not null,
    "updated_by"      varchar(10) default 'system'            not null,
    "dataset_id"      bigint                                  not null
);