create table if not exists app_builder_config_property
(
    id         varchar(64) not null primary key COMMENT '主键id',
    node_id    varchar(255) COMMENT 'node_id',
    form_property_id  varchar(255) not null COMMENT '配置表单项id',
    config_id  varchar(64)  not null COMMENT '关联配置表id'
) COMMENT 'app配置项表';