WITH "update_values"("property_id", "new_value") AS (VALUES(?, ?))
UPDATE "index_integer" SET "value" = "update_values"."new_value"
FROM "update_values"
WHERE "index_integer"."instance_id" = ? AND "index_integer"."property_id" = "update_values"."property_id"