DELETE
FROM t_app_engine_eval_instance;
INSERT INTO t_app_engine_eval_instance("status", "pass_rate", "created_by", "task_id")
VALUES ('RUNNING', 76.0, 'sky', 1),
       ('SUCCESS', 95.0, 'fang', 1),
       ('FAILED', 23.0, 'sky fang', 2);