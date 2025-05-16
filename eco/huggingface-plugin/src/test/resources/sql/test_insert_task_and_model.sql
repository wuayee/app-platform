DELETE
FROM t_eco_huggingface_task;

INSERT INTO t_eco_huggingface_task(task_name_code, task_description_code, total_model_num)
VALUES ('name', 'desc', 2);

INSERT INTO t_eco_huggingface_model(model_name, model_schema, task_id, created_at, created_by)
VALUES ('name1', 'schema1', 1, '2024-09-10T14:30:00', 'test'),
       ('name2', 'schema2', 1, '2024-09-10T14:30:00', 'test');