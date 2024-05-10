CREATE TABLE IF NOT EXISTS task_tree
(
    tree_id    VARCHAR(32) PRIMARY KEY,
    tree_name  VARCHAR(255) NOT NULL,
    tenant     VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(127)  NOT NULL,
    updated_by VARCHAR(127),
    deleted_by VARCHAR(32),
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone,
    deleted_at timestamp without time zone
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_task_tree_name_tenant_deleted_at ON task_tree (tree_name, tenant, deleted_at);

CREATE TABLE IF NOT EXISTS task_tree_node
(
    node_id    VARCHAR(32) PRIMARY KEY,
    node_name  VARCHAR(255) NOT NULL,
    tree_id    VARCHAR(32)  NOT NULL,
    parent_id  VARCHAR(32),
    node_level INT          NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(127)  NOT NULL,
    updated_by VARCHAR(127),
    deleted_by VARCHAR(32),
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone,
    deleted_at timestamp without time zone
);

CREATE TABLE IF NOT EXISTS task_definition
(
    definition_id VARCHAR(32) PRIMARY KEY,
    reference_id  VARCHAR(32)  NOT NULL,
    task_detail   TEXT,
    task_type     VARCHAR(127) NOT NULL,
    version       INT          NOT NULL,
    tenant        VARCHAR(255) NOT NULL,
    is_deleted    BOOLEAN DEFAULT FALSE,
    created_by    VARCHAR(127)  NOT NULL,
    updated_by    VARCHAR(127),
    deleted_by    VARCHAR(32),
    created_at    timestamp without time zone NOT NULL,
    updated_at    timestamp without time zone,
    deleted_at    timestamp without time zone
);

CREATE TABLE IF NOT EXISTS task_definition_props
(
    props_id          VARCHAR(32) PRIMARY KEY,
    definition_id     VARCHAR(32)  NOT NULL,
    props_name        VARCHAR(63)  NOT NULL,
    props_key         VARCHAR(63)  NOT NULL,
    props_icon        VARCHAR(63),
    props_description VARCHAR(255),
    props_value_type  VARCHAR(63)  NOT NULL,
    props_value       JSONB,
    props_type        VARCHAR(127) NOT NULL,
    props_order       INT          NOT NULL,
    is_required       BOOLEAN DEFAULT TRUE,
    is_deleted        BOOLEAN DEFAULT FALSE,
    display_props     JSONB
);

CREATE TABLE IF NOT EXISTS task_definition_props_handler
(
    handler_id           VARCHAR(32) PRIMARY KEY,
    props_id             VARCHAR(32) NOT NULL,
    definition_source_id VARCHAR(32) NOT NULL,
    props_updated_event  VARCHAR(127),
    props_updated_api    VARCHAR(255),
    is_deleted           BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS task_definition_source
(
    source_id      VARCHAR(32) PRIMARY KEY,
    definition_id  VARCHAR(32) NOT NULL,
    source_app     VARCHAR(63) NOT NULL,
    source_api     VARCHAR(255),
    source_handler VARCHAR(63) NOT NULL,
    properties     TEXT,
    is_deleted     BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS task_instance
(
    instance_id          VARCHAR(32) PRIMARY KEY,
    definition_id        VARCHAR(32) NOT NULL,
    source_definition_id VARCHAR(32) NOT NULL,
    field_value_1        TEXT,
    field_value_2        TEXT,
    field_value_3        TEXT,
    field_value_4        TEXT,
    field_value_5        TEXT,
    field_value_6        TEXT,
    field_value_7        TEXT,
    field_value_8        TEXT,
    field_value_9        TEXT,
    field_value_10       TEXT,
    field_value_11       TEXT,
    field_value_12       TEXT,
    field_value_13       TEXT,
    field_value_14       TEXT,
    field_value_15       TEXT,
    field_value_16       TEXT,
    field_value_17       TEXT,
    field_value_18       TEXT,
    field_value_19       TEXT,
    field_value_20       TEXT,
    field_value_21       TEXT,
    field_value_22       TEXT,
    field_value_23       TEXT,
    field_value_24       TEXT,
    field_value_25       TEXT,
    field_value_26  TEXT,
    field_value_27  TEXT,
    field_value_28  TEXT,
    field_value_29  TEXT,
    field_value_30  TEXT,
    field_value_31  TEXT,
    field_value_32  TEXT,
    field_value_33  TEXT,
    field_value_34  TEXT,
    field_value_35  TEXT,
    field_value_36  TEXT,
    field_value_37  TEXT,
    field_value_38  TEXT,
    field_value_39  TEXT,
    field_value_40  TEXT,
    field_value_41  TEXT,
    field_value_42  TEXT,
    field_value_43  TEXT,
    field_value_44  TEXT,
    field_value_45  TEXT,
    field_value_46  TEXT,
    field_value_47  TEXT,
    field_value_48  TEXT,
    field_value_49  TEXT,
    field_value_50  TEXT,
    field_value_51  TEXT,
    field_value_52  TEXT,
    field_value_53  TEXT,
    field_value_54  TEXT,
    field_value_55  TEXT,
    field_value_56  TEXT,
    field_value_57  TEXT,
    field_value_58  TEXT,
    field_value_59  TEXT,
    field_value_60  TEXT,
    field_value_61  TEXT,
    field_value_62  TEXT,
    field_value_63  TEXT,
    field_value_64  TEXT,
    field_value_65  TEXT,
    field_value_66  TEXT,
    field_value_67  TEXT,
    field_value_68  TEXT,
    field_value_69  TEXT,
    field_value_70  TEXT,
    field_value_71  TEXT,
    field_value_72  TEXT,
    field_value_73  TEXT,
    field_value_74  TEXT,
    field_value_75  TEXT,
    field_value_76  TEXT,
    field_value_77  TEXT,
    field_value_78  TEXT,
    field_value_79  TEXT,
    field_value_80  TEXT,
    field_value_81  TEXT,
    field_value_82  TEXT,
    field_value_83  TEXT,
    field_value_84  TEXT,
    field_value_85  TEXT,
    field_value_86  TEXT,
    field_value_87  TEXT,
    field_value_88  TEXT,
    field_value_89  TEXT,
    field_value_90  TEXT,
    field_value_91  TEXT,
    field_value_92  TEXT,
    field_value_93  TEXT,
    field_value_94  TEXT,
    field_value_95  TEXT,
    field_value_96  TEXT,
    field_value_97  TEXT,
    field_value_98  TEXT,
    field_value_99  TEXT,
    field_value_100 TEXT,
    is_deleted      BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS task_tag
(
    tag_id VARCHAR(32) PRIMARY KEY,
    name   VARCHAR(64)  NOT NULL,
    tenant VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS UK_TASK_NAME_TENANT ON task_tag (name, tenant);

INSERT INTO task_tag(tag_id, name, tenant)
VALUES ('41d00a31a9b34aa39b606d80fc426c2e', '未开始', 'public');
INSERT INTO task_tag(tag_id, name, tenant)
VALUES ('8d038441b7514f5b99ea18992126cfa8', '进行中', 'public');
INSERT INTO task_tag(tag_id, name, tenant)
VALUES ('4c4ddb220bfe4fe98374aff710afb3eb', '已完成', 'public');
INSERT INTO task_tag(tag_id, name, tenant)
VALUES ('ad31edc774e24bfbb15e87084744e259', '有风险', 'public');
INSERT INTO task_tag(tag_id, name, tenant)
VALUES ('e72a94b24e704e5abcffc920394f4a19', '无风险', 'public');


CREATE TABLE IF NOT EXISTS task_source_schedule
(
    id         CHAR(32) PRIMARY KEY,
    fitable_id CHAR(32)     NOT NULL,
    "interval" INTEGER      NOT NULL,
    filter     VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS task_source
(
    id      CHAR(32) PRIMARY KEY,
    task_id CHAR(32)    NOT NULL,
    name    VARCHAR(64) NOT NULL,
    app     VARCHAR(64) NOT NULL,
    type    VARCHAR(32) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TASK_SOURCE ON task_source (task_id, name, app);
CREATE INDEX IF NOT EXISTS IDX_TASK_SOURCE_TASK ON task_source (task_id);

CREATE TABLE IF NOT EXISTS task_property_trigger
(
    id               CHAR(32) PRIMARY KEY,
    task_source_id   CHAR(32) NOT NULL,
    task_property_id CHAR(32) NOT NULL,
    fitable_id       CHAR(32) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TASK_PROPERTY_TRIGGER ON task_property_trigger (task_source_id, task_property_id, fitable_id);
CREATE INDEX IF NOT EXISTS IDX_TASK_PROPERTY_TRIGGER_SOURCE ON task_property_trigger (task_source_id);
CREATE INDEX IF NOT EXISTS IDX_TASK_PROPERTY_TRIGGER_PROPERTY ON task_property_trigger (task_property_id);
CREATE INDEX IF NOT EXISTS IDX_TASK_PROPERTY_TRIGGER_FITABLE ON task_property_trigger (fitable_id);

CREATE TABLE IF NOT EXISTS task_property
(
    id           CHAR(32) PRIMARY KEY,
    task_id      CHAR(32)     NOT NULL,
    name         VARCHAR(64)  NOT NULL,
    required     BOOLEAN      NOT NULL,
    identifiable BOOLEAN      NOT NULL,
    description  VARCHAR(512) NOT NULL,
    scope        VARCHAR(16)  NOT NULL,
    data_type    VARCHAR(16)  NOT NULL,
    sequence     INTEGER      NOT NULL,
    appearance   JSON         NOT NULL,
    template_id CHAR(32) NOT NULL DEFAULT '00000000000000000000000000000000'
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TASK_PROPERTY ON task_property (task_id, name);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TASK_DATA_SEQUENCE ON task_property (task_id, data_type, sequence);
CREATE INDEX IF NOT EXISTS IDX_TASK_PROPERTY_TASK ON task_property (task_id);

CREATE TABLE IF NOT EXISTS task
(
    id         CHAR(32) PRIMARY KEY,
    name       VARCHAR(64) NOT NULL,
    tenant_id  CHAR(32)    NOT NULL,
    attributes JSON        NOT NULL DEFAULT '{}',
    created_by VARCHAR(127) NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    updated_by VARCHAR(127) NOT NULL,
    updated_at TIMESTAMP   NOT NULL,
    template_id CHAR(32) NOT NULL DEFAULT '00000000000000000000000000000000',
    category VARCHAR(32) NOT NULL DEFAULT 'TASK'
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_task_id_name ON task (id, name);

CREATE TABLE IF NOT EXISTS task_tree_task
(
    id      CHAR(32) PRIMARY KEY,
    tree_id CHAR(32) NOT NULL,
    task_id CHAR(32) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TASK_TREE_TASK_TREE_TASK ON task_tree_task (tree_id, task_id);
CREATE INDEX IF NOT EXISTS IDX_TASK_TREE_TASK_TREE ON task_tree_task (tree_id);
CREATE INDEX IF NOT EXISTS IDX_TASK_TREE_TASK_TASK ON task_tree_task (task_id);

CREATE TABLE IF NOT EXISTS task_tree_v2
(
    id         char(32) PRIMARY KEY,
    name       varchar(64)  NOT NULL,
    created_by varchar(127)  NOT NULL,
    created_at timestamp(6) NOT NULL,
    updated_by varchar(127)  NOT NULL,
    updated_at timestamp(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS task_instance_wide
(
    id           CHAR(32) PRIMARY KEY,
    task_id      CHAR(32) NOT NULL,
    task_type_id CHAR(32) NOT NULL,
    source_id    CHAR(32) NOT NULL,
    text_1       TEXT,
    text_2       TEXT,
    text_3       TEXT,
    text_4       TEXT,
    text_5       TEXT,
    text_6       TEXT,
    text_7       TEXT,
    text_8       TEXT,
    text_9       TEXT,
    text_10      TEXT,
    text_11      TEXT,
    text_12      TEXT,
    text_13      TEXT,
    text_14      TEXT,
    text_15      TEXT,
    text_16      TEXT,
    text_17      TEXT,
    text_18      TEXT,
    text_19      TEXT,
    text_20      TEXT,
    text_21      TEXT,
    text_22      TEXT,
    text_23      TEXT,
    text_24      TEXT,
    text_25      TEXT,
    text_26      TEXT,
    text_27      TEXT,
    text_28      TEXT,
    text_29      TEXT,
    text_30      TEXT,
    text_31      TEXT,
    text_32      TEXT,
    text_33      TEXT,
    text_34      TEXT,
    text_35      TEXT,
    text_36      TEXT,
    text_37      TEXT,
    text_38      TEXT,
    text_39      TEXT,
    text_40      TEXT,
    text_41      TEXT,
    text_42      TEXT,
    text_43      TEXT,
    text_44      TEXT,
    text_45      TEXT,
    text_46      TEXT,
    text_47      TEXT,
    text_48      TEXT,
    text_49      TEXT,
    text_50      TEXT,
    integer_1    INTEGER,
    integer_2    INTEGER,
    integer_3    INTEGER,
    integer_4    INTEGER,
    integer_5    INTEGER,
    integer_6    INTEGER,
    integer_7    INTEGER,
    integer_8    INTEGER,
    integer_9    INTEGER,
    integer_10   INTEGER,
    integer_11   INTEGER,
    integer_12   INTEGER,
    integer_13   INTEGER,
    integer_14   INTEGER,
    integer_15   INTEGER,
    integer_16   INTEGER,
    integer_17   INTEGER,
    integer_18   INTEGER,
    integer_19   INTEGER,
    integer_20   INTEGER,
    datetime_1   TIMESTAMP,
    datetime_2   TIMESTAMP,
    datetime_3   TIMESTAMP,
    datetime_4   TIMESTAMP,
    datetime_5   TIMESTAMP,
    datetime_6   TIMESTAMP,
    datetime_7   TIMESTAMP,
    datetime_8   TIMESTAMP,
    datetime_9   TIMESTAMP,
    datetime_10  TIMESTAMP,
    datetime_11  TIMESTAMP,
    datetime_12  TIMESTAMP,
    datetime_13  TIMESTAMP,
    datetime_14  TIMESTAMP,
    datetime_15  TIMESTAMP,
    datetime_16  TIMESTAMP,
    datetime_17  TIMESTAMP,
    datetime_18  TIMESTAMP,
    datetime_19  TIMESTAMP,
    datetime_20  TIMESTAMP,
    boolean_1    BOOLEAN,
    boolean_2    BOOLEAN,
    boolean_3    BOOLEAN,
    boolean_4    BOOLEAN,
    boolean_5    BOOLEAN,
    boolean_6    BOOLEAN,
    boolean_7    BOOLEAN,
    boolean_8    BOOLEAN,
    boolean_9    BOOLEAN,
    boolean_10   BOOLEAN,
    boolean_11   BOOLEAN,
    boolean_12   BOOLEAN,
    boolean_13   BOOLEAN,
    boolean_14   BOOLEAN,
    boolean_15   BOOLEAN,
    boolean_16   BOOLEAN,
    boolean_17   BOOLEAN,
    boolean_18   BOOLEAN,
    boolean_19   BOOLEAN,
    boolean_20   BOOLEAN
);

CREATE INDEX IF NOT EXISTS IDX_TASK_INSTANCE_TASK ON task_instance_wide (task_id);
CREATE INDEX IF NOT EXISTS IDX_TASK_INSTANCE_SOURCE ON task_instance_wide (source_id);

CREATE TABLE IF NOT EXISTS task_type
(
    id         CHAR(32) PRIMARY KEY,
    tree_id    CHAR(32)    NOT NULL,
    parent_id  CHAR(32)    NOT NULL,
    name       VARCHAR(64) NOT NULL,
    created_by VARCHAR(127) NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    updated_by VARCHAR(127) NOT NULL,
    updated_at TIMESTAMP   NOT NULL,
    task_id    CHAR(32)
);
CREATE INDEX IF NOT EXISTS IDX_TASK_TYPE_TREE ON task_type (tree_id);
CREATE INDEX IF NOT EXISTS IDX_TASK_TYPE_PARENT ON task_type (parent_id);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TASK_TYPE_NAME ON task_type (task_id, name);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TASK_TYPE_TREE_NAME ON task_type (tree_id, name);

CREATE TABLE IF NOT EXISTS task_node_source
(
    id         CHAR(32) PRIMARY KEY,
    node_id    CHAR(32)    NOT NULL,
    source_id  CHAR(32)    NOT NULL,
    created_by VARCHAR(127) NOT NULL,
    created_at TIMESTAMP   NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TASK_NODE_SOURCE ON task_node_source (node_id, source_id);
CREATE INDEX IF NOT EXISTS IDX_TASK_NODE_SOURCE_NODE ON task_node_source (node_id);
CREATE INDEX IF NOT EXISTS IDX_TASK_NODE_SOURCE_SOURCE ON task_node_source (source_id);

CREATE TABLE IF NOT EXISTS tag
(
    id          CHAR(32) PRIMARY KEY,
    name        VARCHAR(64)  NOT NULL,
    description VARCHAR(512) NOT NULL,
    created_by  VARCHAR(127)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_by  VARCHAR(127)  NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TAG ON tag (name);

CREATE TABLE IF NOT EXISTS tag_usage
(
    id          CHAR(32) PRIMARY KEY,
    tag_id      CHAR(32)    NOT NULL,
    object_type VARCHAR(16) NOT NULL,
    object_id   CHAR(32)    NOT NULL,
    created_by  VARCHAR(127) NOT NULL,
    created_at  TIMESTAMP   NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TAG_USAGE ON tag_usage (tag_id, object_id, object_type);
CREATE INDEX IF NOT EXISTS IDX_TAG_OBJECT_ID ON tag_usage (object_id);

CREATE TABLE IF NOT EXISTS task_category_trigger
(
    "id"          CHAR(32) PRIMARY KEY,
    "task_id"     CHAR(32) NOT NULL,
    "category_id" CHAR(32) NOT NULL,
    "fitable_id"  CHAR(32) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS "UK_TASK_CATEGORY_TRIGGER" ON task_category_trigger ("task_id", "category_id", "fitable_id");
CREATE INDEX IF NOT EXISTS "IDX_TASK_CATEGORY_TRIGGER_TASK" ON task_category_trigger ("task_id");

CREATE TABLE IF NOT EXISTS category_group
(
    id          CHAR(32) PRIMARY KEY,
    name        VARCHAR(64)  NOT NULL,
    description VARCHAR(512) NOT NULL,
    created_by  VARCHAR(127)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_by  VARCHAR(127)  NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_CATEGORY_GROUP_NAME ON category_group (name);

CREATE TABLE IF NOT EXISTS category
(
    id                CHAR(32) PRIMARY KEY,
    category_group_id CHAR(32)     NOT NULL,
    name              VARCHAR(64)  NOT NULL,
    description       VARCHAR(512) NOT NULL,
    created_by        VARCHAR(127)  NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    updated_by        VARCHAR(127)  NOT NULL,
    updated_at        TIMESTAMP    NOT NULL
);
comment on table category is '类目';
comment on column category.id is '表示类目的唯一标识。由32位16进制字符组成。';
comment on column category.category_group_id is '表示类目组的唯一标识。';
comment on column category.name is '表示类目的名称类型。';
comment on column category.description is '表示类目的描述信息。';
comment on column category.created_by is '表示创建人。';
comment on column category.created_at is '表示创建时间。';
comment on column category.updated_by is '表示最近的修改人。未被修改时与创建人相同。';
comment on column category.updated_at is '表示最近的修改时间。未被修改时与创建时间相同。';

CREATE INDEX IF NOT EXISTS IDX_CATEGORY_GROUP ON category (category_group_id);
CREATE UNIQUE INDEX IF NOT EXISTS UK_CATEGORY_NAME ON category (name);

CREATE TABLE IF NOT EXISTS category_matcher
(
    id          CHAR(32) PRIMARY KEY,
    category_id CHAR(32) NOT NULL,
    property_id CHAR(32) NOT NULL,
    "value"     TEXT     NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_CATEGORY_MATCHER ON category_matcher (category_id, property_id, "value");
CREATE INDEX IF NOT EXISTS IDX_CATEGORY_MATCHER_CATEGORY ON category_matcher (category_id);
CREATE INDEX IF NOT EXISTS IDX_CATEGORY_MATCHER_PROPERTY ON category_matcher (property_id);

CREATE TABLE IF NOT EXISTS task_instance_event (
    id CHAR(32) PRIMARY KEY,
    source_id CHAR(32) NOT NULL,
    event_type VARCHAR(16) NOT NULL,
    fitable_id CHAR(32) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TASK_INSTANCE_EVENT ON task_instance_event(source_id, event_type, fitable_id);
CREATE INDEX IF NOT EXISTS IDX_TASK_INSTANCE_EVENT_SOURCE ON task_instance_event(source_id);

CREATE TABLE IF NOT EXISTS tenant
(
    id          CHAR(32) PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    avatar_id   CHAR(32),
    created_by  VARCHAR(127) NOT NULL,
    updated_by  VARCHAR(127),
    created_at  timestamp without time zone NOT NULL,
    updated_at  timestamp without time zone,
    access_level VARCHAR(32) DEFAULT 'PRIVATE'
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TENANT ON tenant (name);

CREATE TABLE IF NOT EXISTS tenant_member
(
    id         CHAR(32) PRIMARY KEY,
    tenant_id  CHAR(32)     NOT NULL,
    user_id    VARCHAR(127) NOT NULL,
    created_by VARCHAR(127) NOT NULL,
    created_at timestamp without time zone NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TENANT_ID_USER_ID ON tenant_member (tenant_id, user_id);
CREATE INDEX IF NOT EXISTS IDX_TENANT_ID_TENANT_MEMBER ON tenant_member (tenant_id);

CREATE TABLE IF NOT EXISTS file
(
    id         CHAR(32) PRIMARY KEY,
    name       VARCHAR(127) NOT NULL,
    content    BYTEA,
    created_by VARCHAR(127) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    type VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS task_source_refresh_in_time (
    id CHAR(32) PRIMARY KEY,
    metadata JSONB NOT NULL,
    create_fitable_id CHAR(32) NOT NULL,
    patch_fitable_id CHAR(32) NOT NULL,
    delete_fitable_id CHAR(32) NOT NULL,
    retrieve_fitable_id CHAR(32) NOT NULL,
    list_fitable_id CHAR(32) NOT NULL
);

CREATE TABLE "authorization" (
    "id" CHAR(32) PRIMARY KEY,
    "system" VARCHAR(64) NOT NULL,
    "user" VARCHAR(127) NOT NULL,
    "token" TEXT NOT NULL,
    "expiration" INT8 NOT NULL,
    "created_by" VARCHAR(127) NOT NULL,
    "created_at" TIMESTAMP NOT NULL,
    "updated_by" VARCHAR(127) NOT NULL,
    "updated_at" TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS task_instance_deleted
(
    id           CHAR(32) PRIMARY KEY,
    task_id      CHAR(32) NOT NULL,
    task_type_id CHAR(32) NOT NULL,
    source_id    CHAR(32) NOT NULL,
    text_1       TEXT,
    text_2       TEXT,
    text_3       TEXT,
    text_4       TEXT,
    text_5       TEXT,
    text_6       TEXT,
    text_7       TEXT,
    text_8       TEXT,
    text_9       TEXT,
    text_10      TEXT,
    text_11      TEXT,
    text_12      TEXT,
    text_13      TEXT,
    text_14      TEXT,
    text_15      TEXT,
    text_16      TEXT,
    text_17      TEXT,
    text_18      TEXT,
    text_19      TEXT,
    text_20      TEXT,
    text_21      TEXT,
    text_22      TEXT,
    text_23      TEXT,
    text_24      TEXT,
    text_25      TEXT,
    text_26      TEXT,
    text_27      TEXT,
    text_28      TEXT,
    text_29      TEXT,
    text_30      TEXT,
    text_31      TEXT,
    text_32      TEXT,
    text_33      TEXT,
    text_34      TEXT,
    text_35      TEXT,
    text_36      TEXT,
    text_37      TEXT,
    text_38      TEXT,
    text_39      TEXT,
    text_40      TEXT,
    text_41      TEXT,
    text_42      TEXT,
    text_43      TEXT,
    text_44      TEXT,
    text_45      TEXT,
    text_46      TEXT,
    text_47      TEXT,
    text_48      TEXT,
    text_49      TEXT,
    text_50      TEXT,
    integer_1    INTEGER,
    integer_2    INTEGER,
    integer_3    INTEGER,
    integer_4    INTEGER,
    integer_5    INTEGER,
    integer_6    INTEGER,
    integer_7    INTEGER,
    integer_8    INTEGER,
    integer_9    INTEGER,
    integer_10   INTEGER,
    integer_11   INTEGER,
    integer_12   INTEGER,
    integer_13   INTEGER,
    integer_14   INTEGER,
    integer_15   INTEGER,
    integer_16   INTEGER,
    integer_17   INTEGER,
    integer_18   INTEGER,
    integer_19   INTEGER,
    integer_20   INTEGER,
    datetime_1   TIMESTAMP,
    datetime_2   TIMESTAMP,
    datetime_3   TIMESTAMP,
    datetime_4   TIMESTAMP,
    datetime_5   TIMESTAMP,
    datetime_6   TIMESTAMP,
    datetime_7   TIMESTAMP,
    datetime_8   TIMESTAMP,
    datetime_9   TIMESTAMP,
    datetime_10  TIMESTAMP,
    datetime_11  TIMESTAMP,
    datetime_12  TIMESTAMP,
    datetime_13  TIMESTAMP,
    datetime_14  TIMESTAMP,
    datetime_15  TIMESTAMP,
    datetime_16  TIMESTAMP,
    datetime_17  TIMESTAMP,
    datetime_18  TIMESTAMP,
    datetime_19  TIMESTAMP,
    datetime_20  TIMESTAMP,
    boolean_1    BOOLEAN,
    boolean_2    BOOLEAN,
    boolean_3    BOOLEAN,
    boolean_4    BOOLEAN,
    boolean_5    BOOLEAN,
    boolean_6    BOOLEAN,
    boolean_7    BOOLEAN,
    boolean_8    BOOLEAN,
    boolean_9    BOOLEAN,
    boolean_10   BOOLEAN,
    boolean_11   BOOLEAN,
    boolean_12   BOOLEAN,
    boolean_13   BOOLEAN,
    boolean_14   BOOLEAN,
    boolean_15   BOOLEAN,
    boolean_16   BOOLEAN,
    boolean_17   BOOLEAN,
    boolean_18   BOOLEAN,
    boolean_19   BOOLEAN,
    boolean_20   BOOLEAN
    );

CREATE TABLE IF NOT EXISTS operation_record
(
    id            char(32) PRIMARY KEY,
    object_type   varchar(25)  NOT NULL,
    object_id     char(32)     NOT NULL,
    operator      varchar(127) NOT NULL,
    operated_time timestamp without time zone,
    message       TEXT         NOT NULL,
    operate       char(7)      NOT NULL
);

CREATE INDEX IDX_OPERATION_RECORD_OBJECT_TYPE_OBJECT_ID_OPERATED_TIME ON operation_record (object_type,object_id,operated_time);

CREATE TABLE IF NOT EXISTS task_source_refresh_in_time (
    id CHAR(32) PRIMARY KEY,
    metadata JSONB NOT NULL,
    create_fitable_id CHAR(32) NOT NULL,
    patch_fitable_id CHAR(32) NOT NULL,
    delete_fitable_id CHAR(32) NOT NULL,
    retrieve_fitable_id CHAR(32) NOT NULL,
    list_fitable_id CHAR(32) NOT NULL
    );

CREATE TABLE IF NOT EXISTS "category_usage" (
    "id" CHAR(32) PRIMARY KEY,
    "object_type" VARCHAR(16) NOT NULL,
    "object_id" CHAR(32) NOT NULL,
    "category_group_id" CHAR(32) NOT NULL,
    "category_id" CHAR(32) NOT NULL,
    "created_by" VARCHAR(127) NOT NULL,
    "created_at" TIMESTAMP NOT NULL,
    "updated_by" VARCHAR(127) NOT NULL,
    "updated_at" TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX "UK_CATEGORY_USAGE" ON "category_usage"("object_id", "category_group_id", "object_type");
CREATE INDEX "IDX_CATEGORY_USAGE_OBJECT_ID" ON "category_usage"("object_id");

/* 创建任务模板表 */
CREATE TABLE IF NOT EXISTS task_template
(
    id          CHAR(32) PRIMARY KEY,
    name        VARCHAR(64)  NOT NULL,
    description VARCHAR(512) NOT NULL
);
CREATE UNIQUE INDEX UK_TASK_TEMPLATE_NAME ON task_template (name);
COMMENT on column task_template.id is '主键Id';
COMMENT on column task_template.name is '任务模板名称';
COMMENT on column task_template.description is '任务模板描述';

/* 创建任务模板属性表 */
CREATE TABLE IF NOT EXISTS task_template_property
(
    id               CHAR(32) PRIMARY KEY,
    task_template_id CHAR(32)    NOT NULL,
    name             VARCHAR(64) NOT NULL,
    data_type        VARCHAR(16) NOT NULL,
    sequence         INTEGER     NOT NULL
);
CREATE UNIQUE INDEX UK_TASK_TEMPLATE_PROPERTY_NAME ON task_template_property (task_template_id, name);
CREATE UNIQUE INDEX UK_TASK_TEMPLATE_PROPERTY_COLUMN ON task_template_property (task_template_id, data_type, sequence);

COMMENT on column task_template_property.id is '主键Id';
COMMENT on column task_template_property.task_template_id is '任务模板Id';
COMMENT on column task_template_property.name is '任务模板属性名称';
COMMENT on column task_template_property.data_type is '任务模板属性数据类型';
COMMENT on column task_template_property.sequence is '任务模板属性在当前数据类型中的序号';

CREATE TABLE "index" (
    "id" CHAR(32) PRIMARY KEY,
    "task_id" CHAR(32) NOT NULL,
    "name" VARCHAR(128) NOT NULL,
    "created_by" VARCHAR(127) NOT NULL,
    "created_at" TIMESTAMP NOT NULL,
    "updated_by" VARCHAR(127) NOT NULL,
    "updated_at" TIMESTAMP NOT NULL
);

CREATE TABLE "index_property" (
    "id" CHAR(32) PRIMARY KEY,
    "index_id" CHAR(32) NOT NULL,
    "property_id" CHAR(32) NOT NULL
);
CREATE INDEX "idx_index_property_index" ON "index_property"("index_id");

CREATE TABLE "index_text" (
    "id" CHAR(32) PRIMARY KEY,
    "instance_id" CHAR(32) NOT NULL,
    "property_id" CHAR(32) NOT NULL,
    "value" TEXT
);
CREATE INDEX "idx_index_text_instance" ON "index_text"("instance_id");
CREATE INDEX "idx_index_text_property" ON "index_text"("property_id");
CREATE INDEX "idx_index_text_value" ON "index_text"("value");

CREATE TABLE "index_integer" (
    "id" CHAR(32) PRIMARY KEY,
    "instance_id" CHAR(32) NOT NULL,
    "property_id" CHAR(32) NOT NULL,
    "value" BIGINT
);
CREATE INDEX "idx_index_integer_instance" ON "index_integer"("instance_id");
CREATE INDEX "idx_index_integer_property" ON "index_integer"("property_id");
CREATE INDEX "idx_index_integer_value" ON "index_integer"("value");

CREATE TABLE "index_datetime" (
    "id" CHAR(32) PRIMARY KEY,
    "instance_id" CHAR(32) NOT NULL,
    "property_id" CHAR(32) NOT NULL,
    "value" TIMESTAMP
);
CREATE INDEX "idx_index_datetime_instance" ON "index_datetime"("instance_id");
CREATE INDEX "idx_index_datetime_property" ON "index_datetime"("property_id");
CREATE INDEX "idx_index_datetime_value" ON "index_datetime"("value");

CREATE TABLE "list_text" (
    "id" CHAR(32) PRIMARY KEY,
    "instance_id" CHAR(32) NOT NULL,
    "property_id" CHAR(32) NOT NULL,
    "index" BIGINT NOT NULL,
    "value" TEXT
);
CREATE INDEX "IDX_LIST_TEXT_INSTANCE" ON "list_text"("instance_id");
CREATE INDEX "IDX_LIST_TEXT_PROPERTY" ON "list_text"("property_id");