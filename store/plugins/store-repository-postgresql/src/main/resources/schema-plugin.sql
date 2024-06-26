do
$$
begin
create table if not exists store_plugin
(
    "id"               bigserial   primary key               not null,
    "created_time"     timestamp   default current_timestamp not null,
    "updated_time"     timestamp   default current_timestamp not null,
    "creator"          varchar(10) default 'system'          not null,
    "modifier"         varchar(10) default 'system'          not null,
    "is_published"     boolean     default false             not null,
    "owner"            varchar(32) default 'system'          not null,
    "like_count"       bigint      default 0                 not null,
    "download_count"   bigint      default 0                 not null,
    "tool_name"        varchar(64)                           not null,
    "tool_unique_name" char(36)                              not null,
    unique("tool_unique_name")
    );
create index if not exists fast_query_plugin on store_plugin ("is_published");
comment on column store_plugin.id is '插件的自增主键';
comment on column store_plugin.created_time is '插件的创建时间';
comment on column store_plugin.updated_time is '插件的更新时间';
comment on column store_plugin.creator is '插件的创建者';
comment on column store_plugin.modifier is '插件的修改者';
comment on column store_plugin.is_published is '插件是否发布';
comment on column store_plugin.owner is '插件的上传者';
comment on column store_plugin.like_count is '插件的点赞数量';
comment on column store_plugin.download_count is '插件的下载数量';
comment on column store_plugin.tool_name is '工具的名称';
comment on column store_plugin.tool_unique_name is '应用的工具唯一标识';
end
$$;