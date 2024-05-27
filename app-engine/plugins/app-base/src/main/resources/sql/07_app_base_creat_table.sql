CREATE TABLE IF NOT EXISTS usr_feedback_info
(
    id bigserial NOT NULL PRIMARY KEY,
    log_id bigint NOT NULL,
    usr_feedback_text text COLLATE pg_catalog."default",
    usr_feedback integer,
    aipp_id varchar(255)
);

create index if not exists idx_log_id  ON usr_feedback_info(log_id);