DELETE
FROM app_engine_eval_data;

INSERT INTO app_engine_eval_data (content,
                                  created_version,
                                  expired_version,
                                  ds_id)
VALUES ('{C1: C1}', 3, 4, 1),
       ('{C2: C2}', 3, 4, 1),
       ('{C3: C3}', 3, 4, 1),
       ('{C4: C4}', 3, 4, 1),
       ('{C5: C5}', 1, 2, 1),
       ('{C6: C6}', 5, 6, 1),
       ('{C7: C7}', 2, 6, 1),
       ('{C8: C8}', 1, 2, 2);