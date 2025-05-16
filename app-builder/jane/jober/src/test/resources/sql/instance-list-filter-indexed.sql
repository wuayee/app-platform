SELECT "ins"."id", "ins"."task_id", "ins"."task_type_id", "ins"."source_id", "ins"."text_1" AS "info_id"
FROM "task_instance_wide" AS "ins"
WHERE "ins"."task_id" = ?
AND "ins"."id" IN (SELECT DISTINCT "instance_id" FROM "index_text" WHERE "property_id" = ? AND "value" = ?)
AND "ins"."text_1" = ?