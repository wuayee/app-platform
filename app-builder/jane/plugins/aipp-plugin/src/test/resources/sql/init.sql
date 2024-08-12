create table if not exists aipp_instance_file
(
    file_id    bigserial    not null primary key,
    aipp_id    varchar(255) not null,
    filename   varchar(255) not null,
    create_at  timestamp    not null default current_timestamp,
    create_by  varchar(64)  not null,
    is_deleted int2 DEFAULT 0
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
    path                   text,
    is_deleted int2 DEFAULT 0
    );

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
    collection_usr_cnt bigint NOT NULL DEFAULT 0,
    is_deleted int2 DEFAULT 0
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
    update_at  timestamp    not null default current_timestamp,
    is_deleted int2 DEFAULT 0
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
    update_at  timestamp    not null default current_timestamp,
    is_deleted int2 DEFAULT 0
    );

create table if not exists app_builder_config_property
(
    id         varchar(64) not null primary key,
    node_id    varchar(255),
    form_property_id  varchar(255) not null,
    config_id  varchar(64)  not null,
    is_deleted int2 DEFAULT 0
    );

create table if not exists app_builder_flow_graph
(
    id         varchar(64) not null primary key,
    name       varchar(255) not null,
    create_by  varchar(64)  not null,
    create_at  timestamp    not null default current_timestamp,
    update_by  varchar(64)  not null,
    update_at  timestamp    not null default current_timestamp,
    appearance TEXT,
    is_deleted int2 DEFAULT 0
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
    update_at  timestamp    not null default current_timestamp,
    is_deleted int2 DEFAULT 0
    );

create table if not exists app_builder_form_property
(
    id         varchar(64) not null primary key,
    form_id    varchar(255) not null,
    name       varchar(255) not null,
    data_type  varchar(255) not null,
    default_value  text,
    is_deleted int2 DEFAULT 0
    );

