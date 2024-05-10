create table if not exists app_builder_form_property
(
    id         varchar(64) not null primary key,
    form_id    varchar(255) not null,
    name       varchar(255) not null,
    data_type  varchar(255) not null,
    default_value  text
);