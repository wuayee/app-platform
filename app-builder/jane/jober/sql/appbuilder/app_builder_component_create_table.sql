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