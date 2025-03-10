SELECT COUNT(1)
FROM "task_tree_v2" AS "tr"
LEFT JOIN "task_tree_task" AS "trt" ON "trt"."tree_id" = "tr"."id"
LEFT JOIN "task" AS "t" ON "t"."id" = "trt"."task_id"