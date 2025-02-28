SELECT "task_id", "id", "data_type", "sequence"
FROM "task_property"
WHERE "id" IN (${propertyIds})