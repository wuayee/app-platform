create table if not exists app_builder_form
(
    id         varchar(64) not null primary key COMMENT '主键id',
    name       varchar(255) not null COMMENT '名称',
    tenant_id  varchar(255) not null COMMENT '租户id',
    appearance TEXT COMMENT '描述',
    type       varchar(64)  not null COMMENT '类型',
    create_by  varchar(64)  not null COMMENT '创建人',
    create_at  timestamp    not null default current_timestamp COMMENT '创建时间',
    update_by  varchar(64)  not null COMMENT '更新人',
    update_at  timestamp    not null default current_timestamp COMMENT '更新时间'
) COMMENT 'app表单表';