create table if not exists app_builder_app
(
    id         varchar(64) not null primary key COMMENT '主键id',
    name       varchar(255) not null COMMENT 'app名称',
    create_by  varchar(64)  not null COMMENT '创建人',
    create_at  timestamp    not null default current_timestamp COMMENT '创建时间',
    update_by  varchar(64)  not null COMMENT '更新人',
    update_at  timestamp    not null default current_timestamp COMMENT '更新时间',
    config_id  varchar(255) not null COMMENT '关联配置表id',
    flow_graph_id varchar(255) not null COMMENT '关联流程图表id',
    tenant_id  varchar(255) not null COMMENT '租户id',
    type       varchar(255) not null COMMENT 'app类型',
    attributes JSON not null DEFAULT '{}' COMMENT '属性',
    state varchar(255) not null COMMENT '状态'
) COMMENT 'app主表';