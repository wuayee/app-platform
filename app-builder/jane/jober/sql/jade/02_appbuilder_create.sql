create table if not exists aipp_instance_file
(
    file_id    bigserial    not null primary key,
    aipp_id    varchar(255) not null,
    filename   varchar(255) not null,
    create_at  timestamp    not null default current_timestamp,
    create_by  varchar(64)  not null
    );

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
    path                   text
    );

create table if not exists form_data(
    form_id varchar(64) not null,
    form_version varchar(32) not null,
    form_name varchar(256) not null,
    tenant_id varchar(64),
    update_time timestamp,
    update_user varchar(64),
    create_time timestamp not null,
    create_user varchar(64),
    constraint form_pkey primary key (form_id, form_version)
    );


create or replace function update_time() returns trigger as $$
begin
    NEW.update_time = now();
return NEW;
end;
$$ language plpgsql;

create or replace trigger form_data_updater before UPDATE on form_data for each row execute function update_time();

create table if not exists app_builder_app
(
    id         varchar(64) not null primary key,
    name       varchar(255) not null,
    create_by  varchar(64)  not null,
    create_at  timestamp    not null default current_timestamp,
    update_by  varchar(64)  not null,
    update_at  timestamp    not null default current_timestamp,
    config_id  varchar(255) not null,
    flow_graph_id varchar(255) not null,
    tenant_id  varchar(255) not null,
    type       varchar(255) not null,
    version    varchar(255),
    attributes JSON not null DEFAULT '{}',
    state varchar(255) not null,
    collection_usr_cnt bigint NOT NULL DEFAULT 0
    );

create table if not exists app_builder_component
(
    id         varchar(64) not null primary key,
    name       varchar(255) not null,
    type       varchar(255) not null,
    description varchar(255) not null,
    form_id    varchar(255) not null,
    service_id varchar(255) not null,
    tenant_id  varchar(255) not null,
    create_by  varchar(64)  not null,
    create_at  timestamp    not null default current_timestamp,
    update_by  varchar(64)  not null,
    update_at  timestamp    not null default current_timestamp
    );

create table if not exists app_builder_config
(
    id         varchar(64) not null primary key,
    form_id    varchar(255) not null,
    app_id varchar(255) not null,
    tenant_id  varchar(255) not null,
    create_by  varchar(64)  not null,
    create_at  timestamp    not null default current_timestamp,
    update_by  varchar(64)  not null,
    update_at  timestamp    not null default current_timestamp
    );

create table if not exists app_builder_config_property
(
    id         varchar(64) not null primary key,
    node_id    varchar(255),
    form_property_id  varchar(255) not null,
    config_id  varchar(64)  not null
    );

create table if not exists app_builder_flow_graph
(
    id         varchar(64) not null primary key,
    name       varchar(255) not null,
    create_by  varchar(64)  not null,
    create_at  timestamp    not null default current_timestamp,
    update_by  varchar(64)  not null,
    update_at  timestamp    not null default current_timestamp,
    appearance TEXT
    );

create table if not exists app_builder_form
(
    id         varchar(64) not null primary key,
    name       varchar(255) not null,
    tenant_id  varchar(255) not null,
    appearance TEXT,
    type       varchar(64)  not null,
    create_by  varchar(64)  not null,
    create_at  timestamp    not null default current_timestamp,
    update_by  varchar(64)  not null,
    update_at  timestamp    not null default current_timestamp
    );

create table if not exists app_builder_form_property
(
    id         varchar(64) not null primary key,
    form_id    varchar(255) not null,
    name       varchar(255) not null,
    data_type  varchar(255) not null,
    default_value  text
    );

create table if not exists app_builder_runtime_info
(
    id                 BIGSERIAL primary key,
    trace_id           varchar(64) not null,
    flow_definition_id varchar(64) not null,
    instance_id        varchar(64) not null,
    node_id            varchar(64) not null,
    node_type          varchar(32) not null,
    start_time         bigint not null,
    end_time           bigint not null,
    status             varchar(32),
    published          smallint not null,
    error_msg          text,
    parameters         json not null DEFAULT '[]',
    create_by          varchar(64),
    create_at          timestamp    not null default current_timestamp,
    update_by          varchar(64),
    update_at          timestamp    not null default current_timestamp
);
create index idx_trace_id on app_builder_runtime_info(trace_id);

CREATE TABLE IF NOT EXISTS "t_chat_session_task_instance_wide_relationship" (
    "msg_id"                VARCHAR(32)  NOT NULL DEFAULT NULL,
    "chat_id"               VARCHAR(32)  NULL     DEFAULT NULL,
    "task_instance_wide_id" VARCHAR(32)  NULL     DEFAULT NULL,
    "create_at"             TIMESTAMP(6) NULL     DEFAULT NULL,
    "create_by"             VARCHAR      NULL     DEFAULT NULL,
    "update_at"             TIMESTAMP(6) NULL     DEFAULT NULL,
    "update_by"             VARCHAR      NULL     DEFAULT NULL,
    CONSTRAINT "pk_t_chat_session_task_instance_wide_relationship_msg_id" PRIMARY KEY("msg_id")
    );
COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."msg_id" IS '消息ID';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."chat_id" IS '会话ID';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."task_instance_wide_id" IS '实例ID';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."create_at" IS '创建时间';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."create_by" IS '创建人';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."update_at" IS '更新时间';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."update_by" IS '更新人';

CREATE TABLE IF NOT EXISTS "t_chat_session" (
    "chat_id"     VARCHAR(32)   NOT NULL DEFAULT NULL,
    "app_id"      VARCHAR(32)   NULL     DEFAULT NULL,
    "app_version" VARCHAR(32)   NULL     DEFAULT NULL,
    "name"        VARCHAR(2000) NULL     DEFAULT NULL,
    "attributes"  JSON          NULL     DEFAULT NULL,
    "create_at"   TIMESTAMP(6)  NULL     DEFAULT NULL,
    "create_by"   VARCHAR(32)   NULL     DEFAULT NULL,
    "update_at"   TIMESTAMP(6)  NULL     DEFAULT NULL,
    "update_by"   VARCHAR(32)   NULL     DEFAULT NULL,
    "status"      INT4          NULL     DEFAULT NULL,
    CONSTRAINT "pk_t_chat_session" PRIMARY KEY("chat_id")
    );
COMMENT ON COLUMN "t_chat_session"."chat_id" IS '会话ID';

COMMENT ON COLUMN "t_chat_session"."app_id" IS '应用ID';

COMMENT ON COLUMN "t_chat_session"."app_version" IS '应用版本';

COMMENT ON COLUMN "t_chat_session"."name" IS '会话纪要';

COMMENT ON COLUMN "t_chat_session"."attributes" IS '属性扩展字段';

COMMENT ON COLUMN "t_chat_session"."create_at" IS '创建时间';

COMMENT ON COLUMN "t_chat_session"."create_by" IS '创建人';

COMMENT ON COLUMN "t_chat_session"."update_at" IS '更新时间';

COMMENT ON COLUMN "t_chat_session"."update_by" IS '更新人';

COMMENT ON COLUMN "t_chat_session"."status" IS '状态 0-正常 1-已删除';