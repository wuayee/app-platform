do
$$
begin
create table if not exists store_tool
(
    "id"             bigserial primary key                 not null,
    "created_time"   timestamp   default current_timestamp not null,
    "updated_time"   timestamp   default current_timestamp not null,
    "creator"        varchar(10)    default 'system'          not null,
    "modifier"       varchar(10)    default 'system'          not null,
    "name"           varchar(64)                           not null,
    "description"    text        default 'no desc'         not null,
    "schema"         json        default '{}'::json        not null,
    "runnables"      json        default '{}'::json        not null,
    "source"         varchar(16) default 'Builtin'         not null,
    "icon"           text,
    "unique_name"    char(36)                              not null,
    unique("unique_name")
    );
comment on column store_tool.id is '工具的自增主键';
        comment on column store_tool.created_time is '工具的创建时间';
        comment on column store_tool.updated_time is '工具的更新时间';
        comment on column store_tool.creator is '工具的创建者';
        comment on column store_tool.modifier is '工具的修改者';
        comment on column store_tool.name is '工具的名字';
        comment on column store_tool.description is '工具的描述';
        comment on column store_tool.schema is '工具的格式';
        comment on column store_tool.runnables is '工具的运行描述';
        comment on column store_tool.source is '工具的来源';
        comment on column store_tool.icon is '工具的图标';
        comment on column store_tool.unique_name is '工具的唯一标识';
create table if not exists store_tag
(
    "id"           bigserial primary key               not null,
    "created_time" timestamp default current_timestamp not null,
    "updated_time" timestamp default current_timestamp not null,
    "creator"      char(10)  default 'system'          not null,
    "modifier"     char(10)  default 'system'          not null,
    "tool_unique_name"      char(36)                   not null,
    "name"         varchar(64)                         not null,
    unique ("tool_unique_name", "name")
    );
comment on column store_tag.id is '标签的自增主键';
        comment on column store_tag.created_time is '标签的创建时间';
        comment on column store_tag.updated_time is '标签的更新时间';
        comment on column store_tag.creator is '标签的创建者';
        comment on column store_tag.modifier is '标签的修改者';
        comment on column store_tag.name is '标签的名称';
        comment on column store_tag.tool_unique_name is '工具的唯一标识';
end
$$;