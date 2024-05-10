create table if not exists app_builder_config_property
(
    id         varchar(64) not null primary key,
    node_id    varchar(255),
    form_property_id  varchar(255) not null,
    config_id  varchar(64)  not null
);