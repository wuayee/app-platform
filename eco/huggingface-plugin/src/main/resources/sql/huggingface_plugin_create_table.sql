create table if not exists t_eco_huggingface_model
(
    "id"           bigserial primary key                 not null,
    "model_name"   varchar(64)                           not null,
    "model_schema" text                                  not null,
    "task_id"      bigint                                not null,
    "created_at"   timestamp   default current_timestamp not null,
    "created_by"   varchar(32) default 'system'          not null
    );

comment on table t_eco_huggingface_model is 'Huggingface 模型表';
comment on column t_eco_huggingface_model.id is '主键';
comment on column t_eco_huggingface_model.model_name is '模型名字';
comment on column t_eco_huggingface_model.model_schema is '模型格式规范';
comment on column t_eco_huggingface_model.task_id is '外键，关联 Huggingface 任务唯一标识';
comment on column t_eco_huggingface_model.created_at is '模型创建时间';
comment on column t_eco_huggingface_model.created_by is '模型创建者';

create table if not exists t_eco_huggingface_task
(
    "id"                    bigserial primary key not null,
    "task_name_code"        varchar(64)           not null,
    "task_description_code" varchar(64)           not null,
    "total_model_num"       integer               not null
);

comment on table t_eco_huggingface_task is 'Huggingface 任务表';
comment on column t_eco_huggingface_task.id is '主键';
comment on column t_eco_huggingface_task.task_name_code is '任务类型代码';
comment on column t_eco_huggingface_task.task_description_code is '任务描述代码';
comment on column t_eco_huggingface_task.total_model_num is 'Huggingface 任务关联模型数量';

INSERT INTO t_eco_huggingface_task(task_name_code, task_description_code, total_model_num)
VALUES ('automatic-speech-recognition', 'automatic-speech-recognition.description', 0),
       ('text-to-image', 'text-to-image.description', 0),
       ('text-to-speech', 'text-to-speech.description', 0),
       ('image-to-image', 'image-to-image.description', 0),
       ('translation', 'translation.description', 0),
       ('summarization', 'summarization.description', 0);