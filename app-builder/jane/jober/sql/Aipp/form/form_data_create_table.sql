create table if not exists form_data(
    form_id varchar(64) not null COMMENT '表单id',
    form_version varchar(32) not null COMMENT '表单版本',
    form_name varchar(256) not null COMMENT '表单名称',
    tenant_id varchar(64) COMMENT '租户id',
    update_time timestamp COMMENT '更新时间',
    update_user varchar(64) COMMENT '更新人',
    create_time timestamp not null COMMENT '创建时间',
    create_user varchar(64) COMMENT '创建人',
    constraint form_pkey primary key (form_id, form_version) COMMENT '主键'
) COMMENT '表单数据表';


create or replace function update_time() returns trigger as $$
begin
    NEW.update_time = now();
    return NEW;
end;
$$ language plpgsql;

create trigger form_data_updater before UPDATE on form_data for each row execute function update_time();
