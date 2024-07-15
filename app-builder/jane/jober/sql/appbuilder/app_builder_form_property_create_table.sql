create table if not exists app_builder_form_property
(
    id         varchar(64) not null primary key COMMENT '主键id',
    form_id    varchar(255) not null COMMENT '表单id',
    name       varchar(255) not null COMMENT '名称',
    data_type  varchar(255) not null COMMENT '数据类型',
    default_value  text COMMENT '默认值'
) COMMENT 'app表单项表';