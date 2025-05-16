SELECT "tr"."id",
"tr"."name",
"trt"."task_id",
"tr"."created_by",
"tr"."created_at",
"tr"."updated_by",
"tr"."updated_at"
FROM "task_tree_v2" AS "tr"
LEFT JOIN "task_tree_task" AS "trt" ON "trt"."tree_id" = "tr"."id"
LEFT JOIN "task" AS "t" ON "t"."id" = "trt"."task_id"
