create table if not exists app_engine_eval_data
(
    "id"              bigserial primary key                   not null,
    "content"         text                                    not null,
    "created_version" bigint                                  not null,
    "expired_version" bigint      default 9223372036854775807 not null,
    "created_at"      timestamp   default current_timestamp   not null,
    "updated_at"      timestamp   default current_timestamp   not null,
    "created_by"      varchar(10) default 'system'            not null,
    "updated_by"      varchar(10) default 'system'            not null,
    "ds_id"           bigint                                  not null
);

create index if not exists idx_ds_id_created_version_expired_version on app_engine_eval_data ("ds_id", "created_version", "expired_version");

comment on table app_engine_eval_data is '评估数据表';
comment on column app_engine_eval_data.id is '主键';
comment on column app_engine_eval_data.content is '评估内容';
comment on column app_engine_eval_data.created_version is '创建版本';
comment on column app_engine_eval_data.expired_version is '过期版本';
comment on column app_engine_eval_data.created_at is '创建时间';
comment on column app_engine_eval_data.updated_at is '更新时间';
comment on column app_engine_eval_data.created_by is '创建者';
comment on column app_engine_eval_data.updated_by is '更新者';
comment on column app_engine_eval_data.ds_id is '外键，关联评估数据集';
