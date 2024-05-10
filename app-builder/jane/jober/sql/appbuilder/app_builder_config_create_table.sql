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