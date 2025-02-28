create table if not exists app_builder_app
(
    id             varchar(64) not null primary key comment '主键id',
    name           varchar(255) not null comment 'app名称',
    create_by      varchar(64)  not null comment '创建人',
    create_at      timestamp    not null default current_timestamp comment '创建时间',
    update_by      varchar(64)  not null comment '更新人',
    update_at      timestamp    not null default current_timestamp comment '更新时间',
    config_id      varchar(255) not null comment '关联配置表id',
    flow_graph_id  varchar(255) not null comment '关联流程图表id',
    tenant_id      varchar(255) not null comment '租户id',
    type           varchar(255) not null comment 'app类型',
    attributes     json not null default '{}' comment '属性',
    state          varchar(255) not null comment '状态',
    app_built_type varchar(8) not null default 'basic' comment '应用构建类型'
) comment 'app主表';