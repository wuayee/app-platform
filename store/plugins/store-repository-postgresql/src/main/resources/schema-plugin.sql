do
$$
begin
create table if not exists store_plugin
(
    "id"           bigserial   primary key               not null,
    "created_time" timestamp   default current_timestamp not null,
    "updated_time" timestamp   default current_timestamp not null,
    "creator"      varchar(30) default 'system'          not null,
    "modifier"     varchar(30) default 'system'          not null,
    "plugin_id"    char(64)                              not null,
    "plugin_name"  varchar(256)                           not null,
    "extension"    json        default '{}'::json        not null,
    unique ("plugin_id")
);
comment on column store_plugin.id is '插件的自增主键';
comment on column store_plugin.created_time is '插件的创建时间';
comment on column store_plugin.updated_time is '插件的更新时间';
comment on column store_plugin.creator is '插件的创建者';
comment on column store_plugin.modifier is '插件的修改者';
comment on column store_plugin.plugin_id is '插件的唯一标识';
comment on column store_plugin.plugin_name is '插件的名字';
comment on column store_plugin.extension is '插件的扩展字段';
end
$$;