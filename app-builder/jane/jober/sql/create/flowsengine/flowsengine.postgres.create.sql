CREATE TABLE IF NOT EXISTS flow_definition
(
    definition_id VARCHAR(32) PRIMARY KEY,
    meta_id       VARCHAR(32)  NOT NULL,
    name          VARCHAR(256) NOT NULL,
    tenant        VARCHAR(32)  NOT NULL,
    version       VARCHAR(16)  NOT NULL,
    status        VARCHAR(32)  NOT NULL,
    graph         JSONB        NOT NULL,
    created_by    VARCHAR(127)  NOT NULL,
    created_at    timestamp without time zone NOT NULL
);

comment on table flow_definition is '流程定义';
comment on column flow_definition.definition_id is '表示流程定义的唯一标识。由32位16进制字符组成。';
comment on column flow_definition.meta_id is '表示流程定义的元数据标识。由32位16进制字符组成。';
comment on column flow_definition.name is '表示流程定义的元数据标识。';
comment on column flow_definition.tenant is '表示流程定义的租户标识。';
comment on column flow_definition.version is '表示流程定义的版本标识。';
comment on column flow_definition.status is '表示流程定义的状态标识。';
comment on column flow_definition.graph is '表示流程定义的元数据信息。';
comment on column flow_definition.created_by is '表示创建人。';
comment on column flow_definition.created_at is '表示创建时间。';

CREATE UNIQUE INDEX IF NOT EXISTS UK_FLOW_NAME_VERSION ON flow_definition (name, version);
CREATE UNIQUE INDEX IF NOT EXISTS UK_FLOW_META_ID_VERSION ON flow_definition (meta_id, version);
CREATE INDEX IF NOT EXISTS INDEX_FLOW_DEFINITION_ID ON flow_definition (definition_id);

CREATE TABLE IF NOT EXISTS flow_context
(
    context_id    VARCHAR(32) NOT NULL PRIMARY KEY,
    trace_id      TEXT NOT NULL,
    trans_id      VARCHAR(32) NOT NULL,
    root_id       VARCHAR(32) NOT NULL,
    stream_id     VARCHAR(64) NOT NULL,
    flow_data     JSONB       NOT NULL,
    position_id   VARCHAR(32) NOT NULL,
    joined        BOOLEAN,
    status        VARCHAR(10) NOT NULL,
    parallel      VARCHAR(32),
    parallel_mode VARCHAR(10),
    previous      VARCHAR(32),
    batch_id      VARCHAR(32),
    to_batch      VARCHAR(32),
    sent          BOOLEAN DEFAULT FALSE,
    create_at     timestamp without time zone NOT NULL,
    update_at     timestamp without time zone,
    archived_at   timestamp without time zone
);

comment on table flow_context is '流程实例上下文';
comment on column flow_context.context_id is '表示流程实例上下文的唯一标识';
comment on column flow_context.trace_id is '表示流程实例轨迹唯一标识。';
comment on column flow_context.trans_id is '表示流程实例运行唯一标识。';
comment on column flow_context.root_id is '表示流程实例根节点唯一标识。';
comment on column flow_context.stream_id is '表示流程元数据标识和版本唯一标识。';
comment on column flow_context.flow_data is '表示流程实例运行上下文数据。';
comment on column flow_context.position_id is '表示流程实例上下文所处节点位置。';
comment on column flow_context.joined is '表示流程实例上下文是否由平行节点合并。';
comment on column flow_context.status is '表示流程实例上下文状态。';
comment on column flow_context.parallel is '表示流程实例上下文处于哪个平行节点。';
comment on column flow_context.parallel_mode is '表示流程实例上下文所处平行节点状态。';
comment on column flow_context.previous is '表示流程实例上下文来源哪个上下文。';
comment on column flow_context.batch_id is '表示流程实例上下文所处批次标识。';
comment on column flow_context.to_batch is '表示流程实例上下文指向批次标识。';
comment on column flow_context.sent is '表示流程实例上下文是否已被事件发送。';
comment on column flow_context.create_at is '表示流程实例上下文创建时间。';
comment on column flow_context.update_at is '表示流程实例上下文更新时间。';
comment on column flow_context.archived_at is '表示流程实例上下文完成时间。';

CREATE INDEX IF NOT EXISTS INDEX_FLOW_BATCH_ID ON flow_context (batch_id, to_batch);
CREATE INDEX IF NOT EXISTS INDEX_FLOW_STREAM_ID ON flow_context (stream_id, position_id, batch_id);
CREATE INDEX IF NOT EXISTS INDEX_FLOW_CONTEXT_ID ON flow_context (context_id);
CREATE INDEX IF NOT EXISTS INDEX_FLOW_TO_BATCH ON flow_context (to_batch);

CREATE TABLE IF NOT EXISTS flow_trace
(
    trace_id         VARCHAR(32) NOT NULL PRIMARY KEY,
    stream_id        VARCHAR(64) NOT NULL,
    operator         VARCHAR(127) NOT NULL,
    application_name VARCHAR(32) NOT NULL,
    start_node       VARCHAR(32) NOT NULL,
    cur_nodes        TEXT        NOT NULL DEFAULT 'default_node',
    start_time       timestamp without time zone NOT NULL,
    end_time         timestamp without time zone,
    status           VARCHAR(32) NOT NULL DEFAULT 'ARCHIVED',
    context_pool     TEXT
);

comment on table flow_trace is '流程轨迹';
comment on column flow_trace.trace_id is '表示流程轨迹的唯一标识';
comment on column flow_trace.stream_id is '表示流程轨迹所处的流程标识';
comment on column flow_trace.operator is '表示流程的发起人';
comment on column flow_trace.application_name is '表示流程启动的应用';
comment on column flow_trace.start_node is '表示流程启动的开始节点';
comment on column flow_trace.cur_nodes is '表示流程实例当前所处节点';
comment on column flow_trace.start_time is '表示流程实例启动时间';
comment on column flow_trace.end_time is '表示流程实例结束时间';

CREATE INDEX IF NOT EXISTS INDEX_FLOW_TRACE_STREAM_ID ON flow_trace (stream_id);
CREATE INDEX IF NOT EXISTS INDEX_FLOW_TRACE_ID ON flow_trace (trace_id);

CREATE TABLE IF NOT EXISTS flow_graph
(
    id               VARCHAR(32) NOT NULL,
    version          VARCHAR(16) NOT NULL,
    tenant           VARCHAR(32) NOT NULL,
    status           VARCHAR(32) NOT NULL,
    name             VARCHAR(256),
    data             TEXT,
    created_by       VARCHAR(127) NOT NULL,
    created_at       timestamp without time zone NOT NULL,
    updated_by       VARCHAR(127) NOT NULL,
    updated_at       timestamp without time zone NOT NULL,
    previous         VARCHAR(50),
    is_deleted       BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id, version)
);

CREATE TABLE fitable_usage
(
    fitable_id      VARCHAR(128) NOT NULL,
    definition_id   VARCHAR(32) NOT NULL,
    PRIMARY KEY (fitable_id, definition_id)
);

CREATE TABLE IF NOT EXISTS flow_retry
(
    entity_id       VARCHAR(32) NOT NULL,
    entity_type     VARCHAR(32) NOT NULL,
    next_retry_time timestamp without time zone NOT NULL,
    last_retry_time timestamp without time zone,
    retry_count     INTEGER NOT NULL,
    version         INTEGER NOT NULL,
    PRIMARY KEY (entity_id)
    );

comment on table flow_retry is '流程节点执行异常重试记录';
comment on column flow_retry.entity_id is '表示任务重试的上下文实体ID';
comment on column flow_retry.entity_type is '表示任务重试的上下文实体类型';
comment on column flow_retry.next_retry_time is '表示任务重试的下次时间';
comment on column flow_retry.last_retry_time is '表示上次执行的任务重试时间';
comment on column flow_retry.retry_count is '表示到目前为止的任务重试次数';
comment on column flow_retry.version is '表示当前重试的版本号';