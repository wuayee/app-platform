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

comment on table t_app_engine_eval_task is '评估任务表';
comment on column t_app_engine_eval_task.id is '主键';
comment on column t_app_engine_eval_task.name is '评估任务名字';
comment on column t_app_engine_eval_task.description is '评估任务描述';
comment on column t_app_engine_eval_task.status is '评估任务状态';
comment on column t_app_engine_eval_task.created_at is '评估任务创建时间';
comment on column t_app_engine_eval_task.updated_at is '评估任务修改时间';
comment on column t_app_engine_eval_task.created_by is '评估任务创建者';
comment on column t_app_engine_eval_task.updated_by is '评估任务最近更新者';
comment on column t_app_engine_eval_task.app_id is '应用 ID';
comment on column t_app_engine_eval_task.workflow_id is '工作流 ID';