CREATE TABLE IF NOT EXISTS time_scheduler
(
    scheduler_id         VARCHAR(32) PRIMARY KEY,
    task_definition_id   VARCHAR(32) NOT NULL,
    task_source_id       VARCHAR(32) NOT NULL,
    scheduler_data_type  VARCHAR(63) NOT NULL,
    source_app           VARCHAR(63) NOT NULL,
    create_time          BIGINT      NOT NULL,
    end_time             BIGINT      NOT NULL,
    scheduler_interval   BIGINT      NOT NULL,
    latest_executor_time BIGINT      NOT NULL,
    filter               JSONB,
    properties           JSONB,
    modify_time          BIGINT      NOT NULL,
    owner_address        VARCHAR(32),
    task_type_id         VARCHAR(32)
);
CREATE UNIQUE INDEX IF NOT EXISTS UK_TASK_SOURCE_ID ON time_scheduler (task_source_id);