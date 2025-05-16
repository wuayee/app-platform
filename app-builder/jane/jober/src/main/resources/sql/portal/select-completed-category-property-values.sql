SELECT "cm"."property_id", "cm"."value"
FROM "category_matcher" AS "cm"
INNER JOIN "category" AS "c" ON "c"."id" = "cm"."category_id"
INNER JOIN "category_group" AS "cg" ON "c"."category_group_id" = "cg"."id"
WHERE "cg"."name" = 'status' AND "c"."name" = '已完成'