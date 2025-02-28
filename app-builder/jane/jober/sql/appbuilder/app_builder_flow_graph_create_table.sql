create table if not exists app_builder_flow_graph
(
    id         varchar(64) not null primary key COMMENT '主键id',
    name       varchar(255) not null COMMENT '名称',
    create_by  varchar(64)  not null COMMENT '创建人',
    create_at  timestamp    not null default current_timestamp COMMENT '创建时间',
    update_by  varchar(64)  not null COMMENT '更新人',
    update_at  timestamp    not null default current_timestamp COMMENT '更新时间',
    appearance TEXT COMMENT '描述'
) COMMENT 'app流程图表';