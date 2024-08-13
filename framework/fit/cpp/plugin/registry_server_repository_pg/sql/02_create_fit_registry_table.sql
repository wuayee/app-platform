--
-- Table structure for registry_address
--
-- DROP TABLE IF EXISTS "registry_address";
CREATE TABLE IF NOT EXISTS "registry_address"
(
    "id"          bigserial                                   not null primary key,
    "create_time" timestamp(6)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time" timestamp(6)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "host"        varchar(128) COLLATE "pg_catalog"."default"  NOT NULL,
    "port"        int4                                        NOT NULL,
    "protocol"    int2                                        NOT NULL,
    "worker_id"   varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
    constraint registry_address_index unique (host, port, worker_id)
);

--
-- Table structure for registry_application
--
-- DROP TABLE IF EXISTS "registry_application";
CREATE TABLE IF NOT EXISTS "registry_application"
(
    "id"                  bigserial                                  not null primary key,
    "create_time"         timestamp(6)                               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time"         timestamp(6)                               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "application_name"    varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "application_version" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "extensions"          varchar(4096) COLLATE "pg_catalog"."default",
    constraint registry_application_index unique (application_name, application_version)
);

--
-- Table structure for registry_fitable
--
-- DROP TABLE IF EXISTS "registry_fitable";
CREATE TABLE IF NOT EXISTS "registry_fitable"
(
    "id"                  bigserial                                   not null primary key,
    "create_time"         timestamp(6)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time"         timestamp(6)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "genericable_id"      varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
    "genericable_version" varchar(12) COLLATE "pg_catalog"."default"  NOT NULL,
    "fitable_id"          varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
    "fitable_version"     varchar(12) COLLATE "pg_catalog"."default"  NOT NULL,
    "formats"             varchar(32) COLLATE "pg_catalog"."default"  NOT NULL,
    "application_name"    varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "application_version" varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "aliases"             varchar(4096) COLLATE "pg_catalog"."default",
    "tags"                varchar(4096) COLLATE "pg_catalog"."default",
    "extensions"          varchar(4096) COLLATE "pg_catalog"."default",
    "environment"         varchar(32) COLLATE "pg_catalog"."default"           DEFAULT NULL::character varying,
    constraint registry_fitable_index unique (genericable_id, genericable_version, fitable_id, fitable_version, application_name,
                                     application_version)
);

--
-- Table structure for registry_worker
--
-- DROP TABLE IF EXISTS "registry_worker";
CREATE TABLE IF NOT EXISTS "registry_worker"
(
    "id"                  bigserial                                   not null primary key,
    "create_time"         timestamp(6)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time"         timestamp(6)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "worker_id"           varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
    "application_name"    varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "application_version" varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "expire"              int4                                        NOT NULL,
    "environment"         varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "worker_version"      varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "extensions"          varchar(4096) COLLATE "pg_catalog"."default",
    constraint registry_worker_index unique (worker_id, application_name, application_version)
);

--
-- Table structure for registry_fitable_subscribe
--
-- DROP TABLE IF EXISTS "registry_fitable_subscribe";
CREATE TABLE IF NOT EXISTS "registry_fitable_subscribe"
(
    "subscribed_generic_id"      varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
    "subscribed_generic_version" varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "subscribed_fitid"           varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
    "subscriber_callback_fitid"  varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
    "subscriber_host"            varchar(128) COLLATE "pg_catalog"."default"  NOT NULL,
    "subscriber_port"            int4                                        NOT NULL,
    "subscriber_protocol"        int2                                        NOT NULL,
    "subscriber_id"              varchar(128) COLLATE "pg_catalog"."default",
    "ctime"                      timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    "mtime"                      timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    constraint registry_fitable_subscribe_index unique (subscribed_generic_id, subscribed_generic_version, subscribed_fitid,
                                               subscriber_callback_fitid,
                                               subscriber_host, subscriber_port)
);

--
-- Table structure for registry_heartbeat
--
-- DROP TABLE IF EXISTS "registry_heartbeat";
CREATE TABLE IF NOT EXISTS "registry_heartbeat"
(
    "scene_type"          varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "id"                  varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "alive_time"          int4,
    "interval"            int4,
    "init_delay"          int4,
    "callback_fitid"      varchar(128) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
    "start_time"          int8,
    "last_heartbeat_time" int8,
    "expired_time"        int8,
    "status"              varchar(64) COLLATE "pg_catalog"."default",
    "ctime"               timestamp(6)                                DEFAULT CURRENT_TIMESTAMP,
    "mtime"               timestamp(6)                                DEFAULT CURRENT_TIMESTAMP,
    "host"                varchar(128) COLLATE "pg_catalog"."default",
    "port"                int4,
    "protocol"            int2,
    "formats"             varchar(32) COLLATE "pg_catalog"."default"  DEFAULT NULL::character varying,
    "environment"         varchar(64) COLLATE "pg_catalog"."default"  DEFAULT NULL::character varying,
    constraint registry_heartbeat_index unique (scene_type, id)
);
COMMENT ON COLUMN "registry_heartbeat"."alive_time" IS 'ms';

--
-- Table structure for registry_scene_subscribe
--
-- DROP TABLE IF EXISTS "registry_scene_subscribe";
CREATE TABLE IF NOT EXISTS "registry_scene_subscribe"
(
    "scene_type"                varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "subscriber_id"             varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
    "subscriber_callback_fitid" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
    "ctime"                     timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    constraint registry_scene_subscribe_index unique (scene_type, subscriber_id, subscriber_callback_fitid)
);

--
-- Table structure for registry_token_role
--
-- DROP TABLE IF EXISTS "registry_token_role";
CREATE TABLE IF NOT EXISTS "registry_token_role"
(
    "token" varchar(96) COLLATE "pg_catalog"."default" NOT NULL,
    "type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "role" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "timeout" int8,
    "end_time" int8,
    "ctime" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    constraint registry_token_role_index unique (token, type, role)
);

--
-- Install extension for generate uuid
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";