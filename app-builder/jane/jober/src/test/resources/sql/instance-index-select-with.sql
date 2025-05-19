SELECT "ins"."id", "ins"."task_id", "ins"."task_type_id", "ins"."source_id",
  "ins"."text_1" AS "info_id",
  "ins"."text_2" AS "info_name",
  "ins"."text_3" AS "info_category",
  "ins"."integer_1" AS "info_priority"
FROM "task_instance_wide" AS "ins"
INNER JOIN "index_text" AS "info_id_index" ON "info_id_index"."instance_id" = "ins"."id" AND "info_id_index"."property_id" = ?
INNER JOIN "index_integer" AS "info_priority_index" ON "info_priority_index"."instance_id" = "ins"."id" AND "info_priority_index"."property_id" = ?
WHERE "ins"."task_id" = ?
AND "info_id_index"."value" = ?
AND "info_priority_index"."value" = ?
AND "ins"."text_2" LIKE ? ESCAPE '\'
AND ("ins"."text_3" LIKE ? ESCAPE '\' OR "ins"."text_3" LIKE ? ESCAPE '\')