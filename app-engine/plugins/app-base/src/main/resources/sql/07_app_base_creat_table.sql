CREATE TABLE IF NOT EXISTS usr_feedback_info
(
    id bigserial NOT NULL PRIMARY KEY,
    log_id bigint NOT NULL,
    usr_feedback_text text,
    usr_feedback integer,
    instance_id varchar(255) NOT NULL
);

create index if not exists idx_log_id  ON usr_feedback_info(log_id);
create index if not exists idx_instance_id  ON usr_feedback_info(instance_id);