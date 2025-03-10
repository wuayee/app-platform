create table if not exists app_builder_config
(
    id         varchar(64) not null primary key COMMENT '主键id',
    form_id    varchar(255) not null COMMENT '表单表id',
    app_id varchar(255) not null COMMENT 'app应用id',
    tenant_id  varchar(255) not null COMMENT '租户id',
    create_by  varchar(64)  not null COMMENT '创建人',
    create_at  timestamp    not null default current_timestamp COMMENT '创建时间',
    update_by  varchar(64)  not null COMMENT '更新人',
    update_at  timestamp    not null default current_timestamp COMMENT '更新时间'
) COMMENT 'app配置表';