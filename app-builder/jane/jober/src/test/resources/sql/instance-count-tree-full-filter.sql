WITH "w_ins" AS (SELECT "ins"."id", "ins"."task_id", "ins"."task_type_id", "ins"."source_id", "ins"."text_1" AS "info_id", "ins"."text_2" AS "info_status", "ins"."text_3" AS "info_decomposed_from"
    FROM "task_instance_wide" AS "ins"
    INNER JOIN "category_usage" AS "cu1" ON "cu1"."object_id" = "ins"."id" AND "cu1"."object_type" = ?
    INNER JOIN "category_usage" AS "cu2" ON "cu2"."object_id" = "ins"."id" AND "cu2"."object_type" = ?
    WHERE "ins"."task_id" = ? AND "ins"."id" IN (?, ?) AND "ins"."task_type_id" = ? AND "ins"."source_id" IN (?, ?)
    AND "cu1"."category_id" IN (?, ?) AND "cu2"."category_id" = ?
    AND "ins"."id" IN (SELECT "object_id" FROM "tag_usage" WHERE "tag_id" IN (?, ?))
    AND "ins"."text_1" LIKE ? ESCAPE '\' AND ("ins"."text_2" LIKE ? ESCAPE '\' OR "ins"."text_2" LIKE ? ESCAPE '\'))
SELECT COUNT(1) FROM "w_ins" WHERE "w_ins"."info_decomposed_from" IS NULL
OR "w_ins"."info_decomposed_from" NOT IN (SELECT "info_id" FROM "w_ins" WHERE "info_id" IS NOT NULL)