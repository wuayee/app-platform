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