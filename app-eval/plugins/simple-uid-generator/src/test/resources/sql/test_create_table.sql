DROP SEQUENCE if exists seq_app_engine_eval_id_generator;

CREATE SEQUENCE IF NOT EXISTS seq_app_engine_eval_id_generator
    INCREMENT BY 1
    START WITH 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;