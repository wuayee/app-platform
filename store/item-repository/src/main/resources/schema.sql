do $$
    begin
    create table if not exists store_enum_category (
        "id" serial primary key not null,
        "category" varchar(32) unique
    );
    insert into store_enum_category ("category") values ('APP'), ('TOOL');
    create table if not exists store_item(
        "id" bigserial primary key not null,
        "created_time" timestamp default current_timestamp not null,
        "updated_time" timestamp default current_timestamp not null,
        "creator" char(10) default 'system' not null,
        "modifier" char(10) default 'system' not null,
        "unique_name" char(36) not null,
        "category" integer not null,
        "group" varchar(256) not null,
        "name" varchar(64) not null,
        "schema" json not null,
        "source" varchar(16) default 'Builtin' not null
    );
    if not exists (select * from pg_indexes where indexname = 'category_group_name_index') then
        create unique index category_group_name_index on store_item ("category", "group", "name");
    end if;
    if not exists (select * from pg_indexes where indexname = 'category_group_index') then
        create index category_group_index on store_item using btree("category", "group");
    end if;
    comment on column store_item.id is '商品的自增主键';
    comment on column store_item.created_time is '商品的创建时间';
    comment on column store_item.updated_time is '商品的更新时间';
    comment on column store_item.creator is '商品的创建者';
    comment on column store_item.modifier is '商品的修改者';
    comment on column store_item.unique_name is '商品的唯一标识';
    comment on column store_item.category is '商品的分类';
    comment on column store_item.group is '商品的分组';
    comment on column store_item.name is '商品的名字';
    comment on column store_item.schema is '商品的格式';
    comment on column store_item.source is '商品的来源';
    create table if not exists store_tags(
        "id" bigserial primary key not null,
        "created_time" timestamp default current_timestamp not null,
        "updated_time" timestamp default current_timestamp not null,
        "creator" char(10) default 'system' not null,
        "modifier" char(10) default 'system' not null,
        "item_id" bigint not null,
        "tag" varchar(64) not null
    );
    comment on column store_tags.id is '标签的自增主键';
    comment on column store_tags.created_time is '标签的创建时间';
    comment on column store_tags.updated_time is '标签的更新时间';
    comment on column store_tags.creator is '标签的创建者';
    comment on column store_tags.modifier is '标签的修改者';
    comment on column store_tags.tag is '标签的标签';
    comment on column store_tags.item_id is '标签的自增主键';
end $$;
