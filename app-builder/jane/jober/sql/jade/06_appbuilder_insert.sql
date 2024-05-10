-- 任务模板
INSERT INTO task_template ("id", "name", "description") VALUES ('4f91b69973d1453aadf384d508aed894', '普通任务', '最基础的默认模板');
INSERT INTO task_template ("id", "name", "description") VALUES ('00000000000000000000000000000000', 'empty', 'empty');

INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('08f8fc32484d408aacbcff35bf8be687', '4f91b69973d1453aadf384d508aed894', 'id', 'TEXT', 1);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('abb064f84f36496bb769321b1f3443c9', '4f91b69973d1453aadf384d508aed894', 'decomposed_from', 'TEXT', 2);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('be686a0325694aadb688539f7523f88a', '4f91b69973d1453aadf384d508aed894', 'title', 'TEXT', 3);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('59e0d16c3fe541899778beb76e56b0e3', '4f91b69973d1453aadf384d508aed894', 'owner', 'TEXT', 4);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('e2af84d4a8714ce8a87be79d19a5eb88', '4f91b69973d1453aadf384d508aed894', 'created_by', 'TEXT', 5);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('4aebbf316cf443d791e0280f56f435d6', '4f91b69973d1453aadf384d508aed894', 'created_date', 'DATETIME', 1);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('387c16ae70ac49f68f670f54e4456fc8', '4f91b69973d1453aadf384d508aed894', 'modified_by', 'TEXT', 6);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('354944b0e9234429aabfbafb08059c2e', '4f91b69973d1453aadf384d508aed894', 'modified_date', 'DATETIME', 2);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('c41b4da4013e484ead6fc8b03192dee2', '4f91b69973d1453aadf384d508aed894', 'status', 'TEXT', 7);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('34a33a519dd64dc5aa4b3c98c70a5989', '4f91b69973d1453aadf384d508aed894', 'target_url', 'TEXT', 8);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('456bc8b7b9754708828c68221af977c9', '4f91b69973d1453aadf384d508aed894', 'progress_feedback', 'TEXT', 9);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('d8e5fad3feed41fe8400bd2f35048abe', '4f91b69973d1453aadf384d508aed894', 'risk', 'TEXT', 10);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('e8cf80fdb51a4d4e9c9a9c660714efc7', '4f91b69973d1453aadf384d508aed894', 'priority', 'TEXT', 11);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('819adccd6214437fadec3936b297007b', '4f91b69973d1453aadf384d508aed894', 'finish_time', 'DATETIME', 3);
INSERT INTO task_template_property ("id", "task_template_id", "name", "data_type", "sequence") VALUES ('aac65de27f014c3895228fdb5d32573e', '4f91b69973d1453aadf384d508aed894', 'tag', 'TEXT', 12);

-- app模板（模板数据需要修改）
