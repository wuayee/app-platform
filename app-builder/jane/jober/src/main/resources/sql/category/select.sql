SELECT "c"."id", "c"."name" as "category", "cg"."name" as "group"
FROM "category" AS "c" INNER JOIN "category_group" AS "cg" ON "cg"."id" = "c"."category_group_id"
