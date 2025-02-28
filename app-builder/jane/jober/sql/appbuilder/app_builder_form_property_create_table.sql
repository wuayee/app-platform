create table if not exists app_builder_form_property
(
    id             varchar(64) not null primary key comment '主键id',
    form_id        varchar(255) not null comment '表单id',
    name           varchar(255) not null comment '名称',
    data_type      varchar(255) not null comment '数据类型',
    default_value  text comment '默认值',
    data_from      varchar(8) not null comment '数据来源',
    in_group       varchar(8) not null comment '应用所属的组',
    description    varchar(64) not null comment '应用描述',
    is_deleted     smallint default 0 comment '应用是否删除'
) comment 'app表单项表';