WITH "update_values"("property_id", "new_value") AS (VALUES(?, ?), (?, ?))
UPDATE "index_text" SET "value" = "update_values"."new_value"
FROM "update_values"
WHERE "index_text"."instance_id" = ? AND "index_text"."property_id" = "update_values"."property_id"