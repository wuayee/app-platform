create table if not exists app_builder_component
(
    id         varchar(64) not null primary key COMMENT '主键id',
    name       varchar(255) not null COMMENT '名称',
    type       varchar(255) not null COMMENT '类型',
    description varchar(255) not null COMMENT '描述',
    form_id    varchar(255) not null COMMENT '表单id',
    service_id varchar(255) not null COMMENT '服务id',
    tenant_id  varchar(255) not null COMMENT '租户id',
    create_by  varchar(64)  not null COMMENT '创建人',
    create_at  timestamp    not null default current_timestamp COMMENT '创建时间',
    update_by  varchar(64)  not null COMMENT '更新人',
    update_at  timestamp    not null default current_timestamp COMMENT '更新时间'
) COMMENT 'app组件表';