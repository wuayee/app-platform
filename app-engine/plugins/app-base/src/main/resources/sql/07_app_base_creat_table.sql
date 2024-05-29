CREATE TABLE IF NOT EXISTS usr_feedback_info
(
    id bigserial NOT NULL PRIMARY KEY,
    log_id bigint NOT NULL,
    usr_feedback_text text,
    usr_feedback integer,
    instance_id varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS usr_collection_info
(
    id bigserial NOT NULL PRIMARY KEY,
    aipp_id varchar(255),
    usr_info character varying(64)  NOT NULL,
    is_default boolean NOT NULL
);

create index if not exists idx_log_id  ON usr_feedback_info(log_id);
create index if not exists idx_instance_id  ON usr_feedback_info(instance_id);
create index if not exists idx_usr_info ON usr_collection_info(usr_info);