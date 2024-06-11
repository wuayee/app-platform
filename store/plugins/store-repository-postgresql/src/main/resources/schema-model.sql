do
$$
begin
create table if not exists store_model
(
    "id"               bigserial   primary key               not null,
    "created_time"     timestamp   default current_timestamp not null,
    "updated_time"     timestamp   default current_timestamp not null,
    "creator"          varchar(10) default 'system'          not null,
    "modifier"         varchar(10) default 'system'          not null,
    "task_id"          varchar(64)                           not null,
    "name"             varchar(64)                           not null,
    "url"              text                                  not null,
    "context"          json        default '{}'::json        not null
);
create index if not exists fast_query_model on store_model (task_id);
comment on column store_model.id is '模型的自增主键';
comment on column store_model.created_time is '模型的创建时间';
comment on column store_model.updated_time is '模型的更新时间';
comment on column store_model.creator is '模型的创建者';
comment on column store_model.modifier is '模型的修改者';
comment on column store_model.task_id is '任务的唯一标识';
comment on column store_model.name is '模型的结构';
comment on column store_model.url is '模型的跳转链接';
comment on column store_model.context is '模型的上下文';
end
$$;